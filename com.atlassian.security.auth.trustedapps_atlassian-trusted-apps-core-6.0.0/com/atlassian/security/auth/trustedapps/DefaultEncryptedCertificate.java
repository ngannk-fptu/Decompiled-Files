/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.EncryptedCertificate;
import com.atlassian.security.auth.trustedapps.Null;
import com.atlassian.security.auth.trustedapps.TrustedApplicationUtils;

public class DefaultEncryptedCertificate
implements EncryptedCertificate {
    private final String id;
    private final String key;
    private final String certificate;
    private final Integer protocolVersion;
    private final String magic;
    private final String signature;

    public DefaultEncryptedCertificate(String id, String key, String certificate) {
        this(id, key, certificate, null, null, null);
    }

    public DefaultEncryptedCertificate(String id, String key, String certificate, Integer protocolVersion, String magic) {
        this(id, key, certificate, protocolVersion, magic, null);
    }

    public DefaultEncryptedCertificate(String id, String key, String certificate, Integer protocolVersion, String magic, String signature) {
        Null.not("id", id);
        if (!TrustedApplicationUtils.Constant.VERSION_THREE.equals(protocolVersion)) {
            Null.not("key", key);
        }
        Null.not("certificate", certificate);
        this.id = id;
        this.key = key;
        this.certificate = certificate;
        this.protocolVersion = protocolVersion;
        this.magic = magic;
        this.signature = signature;
    }

    @Override
    public String getCertificate() {
        return this.certificate;
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public String getSecretKey() {
        return this.key;
    }

    @Override
    public Integer getProtocolVersion() {
        return this.protocolVersion;
    }

    @Override
    public String getMagicNumber() {
        return this.magic;
    }

    @Override
    public String getSignature() {
        return this.signature;
    }
}

