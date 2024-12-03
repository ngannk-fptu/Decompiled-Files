/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jcp.xml.dsig.internal.dom;

import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;

public class RSAPSSParameterSpec
implements SignatureMethodParameterSpec {
    private int trailerField;
    private int saltLength;
    private String digestName;

    public int getTrailerField() {
        return this.trailerField;
    }

    public void setTrailerField(int trailerField) {
        this.trailerField = trailerField;
    }

    public int getSaltLength() {
        return this.saltLength;
    }

    public void setSaltLength(int saltLength) {
        this.saltLength = saltLength;
    }

    public String getDigestName() {
        return this.digestName;
    }

    public void setDigestName(String digestName) {
        this.digestName = digestName;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.digestName == null ? 0 : this.digestName.hashCode());
        result = 31 * result + this.saltLength;
        result = 31 * result + this.trailerField;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        RSAPSSParameterSpec other = (RSAPSSParameterSpec)obj;
        if (this.digestName == null ? other.digestName != null : !this.digestName.equals(other.digestName)) {
            return false;
        }
        if (this.saltLength != other.saltLength) {
            return false;
        }
        return this.trailerField == other.trailerField;
    }
}

