/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.settings;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class PasswordCryptor {
    static final byte[] salt = new byte[]{33, 72, 63, 101, 0, 8, -18, -1};
    static final int count = 50;
    static final PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 50);
    final SecretKeyFactory keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");

    public OutputStream encrypt(char[] password, OutputStream out) throws Exception {
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
        SecretKey pbeKey = this.keyFac.generateSecret(pbeKeySpec);
        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        cipher.init(1, (Key)pbeKey, pbeParamSpec);
        return new CipherOutputStream(out, cipher);
    }

    public InputStream decrypt(char[] password, InputStream out) throws Exception {
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password);
        SecretKey pbeKey = this.keyFac.generateSecret(pbeKeySpec);
        Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
        cipher.init(2, (Key)pbeKey, pbeParamSpec);
        return new CipherInputStream(out, cipher);
    }
}

