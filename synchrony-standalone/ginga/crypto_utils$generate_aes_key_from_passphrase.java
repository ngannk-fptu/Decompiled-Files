/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.RT;
import java.security.Key;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class crypto_utils$generate_aes_key_from_passphrase
extends AFunction {
    public static Object invokeStatic(Object passphrase, Object salt, Object iterations, Object key_size) {
        SecretKey aes_key;
        SecretKeyFactory secret_key_factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        Object object = passphrase;
        passphrase = null;
        Object object2 = salt;
        salt = null;
        Object object3 = iterations;
        iterations = null;
        Object object4 = key_size;
        key_size = null;
        PBEKeySpec key_spec = new PBEKeySpec((char[])object, (byte[])object2, RT.intCast((Number)object3), RT.intCast((Number)object4));
        SecretKeyFactory secretKeyFactory = secret_key_factory;
        secret_key_factory = null;
        PBEKeySpec pBEKeySpec = key_spec;
        key_spec = null;
        SecretKey secretKey = aes_key = secretKeyFactory.generateSecret(pBEKeySpec);
        aes_key = null;
        return ((Key)secretKey).getEncoded();
    }

    @Override
    public Object invoke(Object object, Object object2, Object object3, Object object4) {
        Object object5 = object;
        object = null;
        Object object6 = object2;
        object2 = null;
        Object object7 = object3;
        object3 = null;
        Object object8 = object4;
        object4 = null;
        return crypto_utils$generate_aes_key_from_passphrase.invokeStatic(object5, object6, object7, object8);
    }
}

