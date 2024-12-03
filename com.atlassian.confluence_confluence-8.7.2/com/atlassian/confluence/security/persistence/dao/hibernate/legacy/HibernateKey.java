/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.persistence.dao.hibernate.legacy;

public class HibernateKey {
    private long id;
    private String encodedKey;
    private String algorithm;
    private String keyType;
    private String alias;

    public long getId() {
        return this.id;
    }

    void setId(long id) {
        this.id = id;
    }

    public String getEncodedKey() {
        return this.encodedKey;
    }

    void setEncodedKey(String encodedKey) {
        this.encodedKey = encodedKey;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getKeyType() {
        return this.keyType;
    }

    void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public String getAlias() {
        return this.alias;
    }

    void setAlias(String alias) {
        this.alias = alias;
    }
}

