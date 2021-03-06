package eu.europa.esig.dss.spi.x509.revocation.ocsp;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ocsp.ResponderID;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.RespID;

import eu.europa.esig.dss.enumerations.CertificateSourceType;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.DSSASN1Utils;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.x509.CertificatePool;

public class OCSPTokenUtils {
	
	private OCSPTokenUtils() {
	}
	
	public static void checkTokenValidity(OCSPToken ocspToken, CertificateToken certificateToken, CertificateToken issuerCertificateToken) {
		CertificatePool validationCertPool = new CertificatePool();
		validationCertPool.getInstance(certificateToken, CertificateSourceType.OCSP_RESPONSE);
		validationCertPool.getInstance(issuerCertificateToken, CertificateSourceType.OCSP_RESPONSE);
		checkTokenValidity(ocspToken, validationCertPool);
	}
	
	public static void checkTokenValidity(OCSPToken ocspToken, CertificatePool validationCertPool) {
		final boolean found = extractSigningCertificateFromResponse(ocspToken, validationCertPool);
		if (!found) {
			extractSigningCertificateFormResponderId(ocspToken, validationCertPool);
		}
	}
	
	private static boolean extractSigningCertificateFromResponse(OCSPToken ocspToken, CertificatePool validationCertPool) {
		BasicOCSPResp basicOCSPResp = ocspToken.getBasicOCSPResp();
		if (basicOCSPResp != null) {
			for (final X509CertificateHolder x509CertificateHolder : basicOCSPResp.getCerts()) {
				CertificateToken certificateToken = DSSASN1Utils.getCertificate(x509CertificateHolder);
				CertificateToken certToken = validationCertPool.getInstance(certificateToken, CertificateSourceType.OCSP_RESPONSE);
				if (ocspToken.isSignedBy(certToken)) {
					ocspToken.setIssuerX500Principal(certToken.getSubjectX500Principal());
					return true;
				}
			}
		}
		return false;
	}

	private static void extractSigningCertificateFormResponderId(OCSPToken ocspToken, CertificatePool validationCertPool) {
		BasicOCSPResp basicOCSPResp = ocspToken.getBasicOCSPResp();
		if (basicOCSPResp != null) {
			final RespID responderId = basicOCSPResp.getResponderId();
			final ResponderID responderIdAsASN1Object = responderId.toASN1Primitive();
			final DERTaggedObject derTaggedObject = (DERTaggedObject) responderIdAsASN1Object.toASN1Primitive();
			if (1 == derTaggedObject.getTagNo()) {
				final ASN1Primitive derObject = derTaggedObject.getObject();
				final byte[] derEncoded = DSSASN1Utils.getDEREncoded(derObject);
				final X500Principal x500Principal = DSSUtils.getNormalizedX500Principal(new X500Principal(derEncoded));
				final Set<CertificateToken> candidates = validationCertPool.get(x500Principal);
				setIssuerToOcspToken(ocspToken, candidates);
			} else if (2 == derTaggedObject.getTagNo()) {
				final ASN1OctetString hashOctetString = (ASN1OctetString) derTaggedObject.getObject();
				final byte[] expectedHash = hashOctetString.getOctets();
				final List<CertificateToken> candidates = validationCertPool.getBySki(expectedHash);
				setIssuerToOcspToken(ocspToken, candidates);
			} else {
				throw new DSSException("Unsupported tag No " + derTaggedObject.getTagNo());
			}
		}
	}
	
	private static void setIssuerToOcspToken(OCSPToken ocspToken, Collection<CertificateToken> candidates) {
		for (CertificateToken issuerCertificateToken : candidates) {
			if (ocspToken.isSignedBy(issuerCertificateToken)) {
				ocspToken.setIssuerX500Principal(issuerCertificateToken.getSubjectX500Principal());
				return;
			}
		}
	}

}
