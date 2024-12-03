/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.Numbers;
import clojure.lang.RT;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class crypto_utils$encrypt_aes256_gcm_96_iv_prefix
extends AFunction {
    public static final Object const__1 = 96L;

    public static Object invokeStatic(Object plaintext, Object priv_key) {
        Object object = priv_key;
        priv_key = null;
        SecretKeySpec key2 = new SecretKeySpec((byte[])object, "AES");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecureRandom secure_rand = new SecureRandom();
        byte[] iv = Numbers.byte_array(const__1);
        SecureRandom secureRandom = secure_rand;
        secure_rand = null;
        secureRandom.nextBytes(iv);
        GCMParameterSpec param_spec = new GCMParameterSpec(RT.intCast(128L), iv);
        SecretKeySpec secretKeySpec = key2;
        key2 = null;
        GCMParameterSpec gCMParameterSpec = param_spec;
        param_spec = null;
        cipher.init(Cipher.ENCRYPT_MODE, (Key)secretKeySpec, gCMParameterSpec);
        cipher.updateAAD(iv);
        ByteArrayOutputStream G__18505 = new ByteArrayOutputStream();
        byte[] byArray = iv;
        iv = null;
        ((OutputStream)G__18505).write(byArray);
        Cipher cipher2 = cipher;
        cipher = null;
        Object object2 = plaintext;
        plaintext = null;
        ((OutputStream)G__18505).write(cipher2.doFinal((byte[])object2));
        ByteArrayOutputStream byteArrayOutputStream = G__18505;
        G__18505 = null;
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return crypto_utils$encrypt_aes256_gcm_96_iv_prefix.invokeStatic(object3, object4);
    }
}

