package eu.europa.esig.dss.ws.signature.dto.parameters;

import java.io.Serializable;

import javax.xml.crypto.dsig.CanonicalizationMethod;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;

@SuppressWarnings("serial")
public class RemoteTimestampParameters implements Serializable {

	/**
	 * The digest algorithm to provide to the timestamping authority
	 */
	private DigestAlgorithm digestAlgorithm = DigestAlgorithm.SHA256;

	/**
	 * This is the default canonicalization method for XMLDSIG used for timestamps. Another complication arises because
	 * of the way that the default canonicalization algorithm
	 * handles namespace declarations; frequently a signed XML document needs to be embedded in another document; in
	 * this case the original canonicalization algorithm will not
	 * yield the same result as if the document is treated alone. For this reason, the so-called Exclusive
	 * Canonicalization, which serializes XML namespace declarations
	 * independently of the surrounding XML, was created.
	 */
	private String canonicalizationMethod = CanonicalizationMethod.EXCLUSIVE;

	public RemoteTimestampParameters() {
	}

	public RemoteTimestampParameters(DigestAlgorithm digestAlgorithm) {
		this.digestAlgorithm = digestAlgorithm;
		this.canonicalizationMethod = null;
	}

	public RemoteTimestampParameters(DigestAlgorithm digestAlgorithm, String canonicalizationMethod) {
		this.digestAlgorithm = digestAlgorithm;
		this.canonicalizationMethod = canonicalizationMethod;
	}

	public DigestAlgorithm getDigestAlgorithm() {
		return digestAlgorithm;
	}

	public void setDigestAlgorithm(final DigestAlgorithm digestAlgorithm) {
		if (digestAlgorithm == null) {
			throw new NullPointerException();
		}
		this.digestAlgorithm = digestAlgorithm;
	}

	public String getCanonicalizationMethod() {
		return canonicalizationMethod;
	}

	public void setCanonicalizationMethod(final String canonicalizationMethod) {
		this.canonicalizationMethod = canonicalizationMethod;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((canonicalizationMethod == null) ? 0 : canonicalizationMethod.hashCode());
		result = (prime * result) + ((digestAlgorithm == null) ? 0 : digestAlgorithm.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RemoteTimestampParameters other = (RemoteTimestampParameters) obj;
		if (canonicalizationMethod == null) {
			if (other.canonicalizationMethod != null) {
				return false;
			}
		} else if (!canonicalizationMethod.equals(other.canonicalizationMethod)) {
			return false;
		}
		if (digestAlgorithm != other.digestAlgorithm) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "RemoteTimestampParameters{" + ", digestAlgorithm=" + digestAlgorithm.getName() + ", canonicalizationMethod=" + canonicalizationMethod + "}";
	}
	
}
