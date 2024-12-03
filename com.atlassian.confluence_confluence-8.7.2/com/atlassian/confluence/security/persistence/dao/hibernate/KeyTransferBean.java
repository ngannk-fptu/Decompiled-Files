/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.auth.trustedapps.EncryptionProvider
 *  com.atlassian.spring.container.ContainerManager
 *  org.apache.commons.codec.binary.Base64
 */
package com.atlassian.confluence.security.persistence.dao.hibernate;

import com.atlassian.security.auth.trustedapps.EncryptionProvider;
import com.atlassian.spring.container.ContainerManager;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import org.apache.commons.codec.binary.Base64;

public class KeyTransferBean
implements Serializable {
    public static final String TYPE_PRIVATE = "private";
    public static final String TYPE_PUBLIC = "public";
    private String keyType;
    private String algorithm;
    private String encodedKey;

    public KeyTransferBean(String asCData) {
        String[] split = asCData.split("\\s+");
        if (split.length != 5) {
            throw new IllegalArgumentException("Input provided : " + asCData + " is not a valid KeyTransferBean");
        }
        this.algorithm = split[1];
        this.keyType = split[2];
        this.encodedKey = split[3];
    }

    public KeyTransferBean(String type, String algorithm, String encodedKey) {
        this.keyType = type;
        this.algorithm = algorithm;
        this.encodedKey = encodedKey;
    }

    public KeyTransferBean(Key key) {
        this.keyType = key instanceof PrivateKey ? TYPE_PRIVATE : TYPE_PUBLIC;
        this.algorithm = key.getAlgorithm();
        this.encodedKey = this.encodeKey(key);
    }

    public String getKeyType() {
        return this.keyType;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public String getEncodedKey() {
        return this.encodedKey;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public void setEncodedKey(String encodedKey) {
        this.encodedKey = encodedKey;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public Key asKey() {
        try {
            byte[] keySpec = Base64.decodeBase64((byte[])this.encodedKey.getBytes(StandardCharsets.US_ASCII));
            if (TYPE_PRIVATE.equals(this.keyType)) {
                return this.getEncryptionProvider().toPrivateKey(keySpec);
            }
            if (TYPE_PUBLIC.equals(this.keyType)) {
                return this.getEncryptionProvider().toPublicKey(keySpec);
            }
            throw new IllegalArgumentException("Unknown key type: " + this.keyType);
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Exception while getting key factory", e);
        }
        catch (InvalidKeySpecException e) {
            throw new RuntimeException("Invalid key spec", e);
        }
        catch (NoSuchProviderException e) {
            throw new RuntimeException("No such provider", e);
        }
    }

    protected EncryptionProvider getEncryptionProvider() {
        return (EncryptionProvider)ContainerManager.getComponent((String)"encryptionProvider");
    }

    public String encodeKey(Key key) {
        return new String(Base64.encodeBase64((byte[])key.getEncoded()), StandardCharsets.US_ASCII);
    }

    public String asCDataEncodedString() {
        return KeyTransferBean.CDatafyString(this.algorithm + " " + this.keyType + " " + this.encodedKey);
    }

    public static String CDatafyString(String s) {
        return "<![CDATA[ " + s + " ]]>";
    }
}

