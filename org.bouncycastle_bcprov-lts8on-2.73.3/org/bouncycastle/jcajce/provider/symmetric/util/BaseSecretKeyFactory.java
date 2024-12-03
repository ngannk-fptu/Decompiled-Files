/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.symmetric.util;

import java.lang.reflect.Constructor;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactorySpi;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.symmetric.util.PBE;

public class BaseSecretKeyFactory
extends SecretKeyFactorySpi
implements PBE {
    protected String algName;
    protected ASN1ObjectIdentifier algOid;

    protected BaseSecretKeyFactory(String algName, ASN1ObjectIdentifier algOid) {
        this.algName = algName;
        this.algOid = algOid;
    }

    @Override
    protected SecretKey engineGenerateSecret(KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof SecretKeySpec) {
            return new SecretKeySpec(((SecretKeySpec)keySpec).getEncoded(), this.algName);
        }
        throw new InvalidKeySpecException("Invalid KeySpec");
    }

    protected KeySpec engineGetKeySpec(SecretKey key, Class keySpec) throws InvalidKeySpecException {
        if (keySpec == null) {
            throw new InvalidKeySpecException("keySpec parameter is null");
        }
        if (key == null) {
            throw new InvalidKeySpecException("key parameter is null");
        }
        if (SecretKeySpec.class.isAssignableFrom(keySpec)) {
            return new SecretKeySpec(key.getEncoded(), this.algName);
        }
        try {
            Class[] parameters = new Class[]{byte[].class};
            Constructor c = keySpec.getConstructor(parameters);
            Object[] p = new Object[]{key.getEncoded()};
            return (KeySpec)c.newInstance(p);
        }
        catch (Exception e) {
            throw new InvalidKeySpecException(e.toString());
        }
    }

    @Override
    protected SecretKey engineTranslateKey(SecretKey key) throws InvalidKeyException {
        if (key == null) {
            throw new InvalidKeyException("key parameter is null");
        }
        if (!key.getAlgorithm().equalsIgnoreCase(this.algName)) {
            throw new InvalidKeyException("Key not of type " + this.algName + ".");
        }
        return new SecretKeySpec(key.getEncoded(), this.algName);
    }
}

