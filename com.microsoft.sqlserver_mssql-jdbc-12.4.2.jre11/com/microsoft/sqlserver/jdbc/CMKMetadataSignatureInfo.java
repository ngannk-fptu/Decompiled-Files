/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.Util;

class CMKMetadataSignatureInfo {
    String masterKeyPath;
    boolean allowEnclaveComputations;
    String signatureHexString;

    public CMKMetadataSignatureInfo(String masterKeyPath, boolean allowEnclaveComputations, byte[] signature) {
        this.masterKeyPath = masterKeyPath;
        this.allowEnclaveComputations = allowEnclaveComputations;
        this.signatureHexString = Util.byteToHexDisplayString(signature);
    }

    public String getMasterKeyPath() {
        return this.masterKeyPath;
    }

    public boolean isAllowEnclaveComputations() {
        return this.allowEnclaveComputations;
    }

    public String getSignatureHexString() {
        return this.signatureHexString;
    }

    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (null != this.masterKeyPath ? this.masterKeyPath.hashCode() : 0);
        hash = 31 * hash + (this.allowEnclaveComputations ? 1 : 0);
        hash = 31 * hash + (null != this.signatureHexString ? this.signatureHexString.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (null != object && CMKMetadataSignatureInfo.class == object.getClass()) {
            CMKMetadataSignatureInfo other = (CMKMetadataSignatureInfo)object;
            if (this.hashCode() == other.hashCode()) {
                return (null == this.masterKeyPath ? null == other.masterKeyPath : this.masterKeyPath.equals(other.masterKeyPath)) && this.allowEnclaveComputations == other.allowEnclaveComputations && (null == this.signatureHexString ? null == other.signatureHexString : this.signatureHexString.equals(other.signatureHexString));
            }
        }
        return false;
    }
}

