/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.property.PropertyManager
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  org.apache.commons.codec.binary.Base64
 */
package com.atlassian.crowd.password.encoder;

import com.atlassian.crowd.exception.PasswordEncoderException;
import com.atlassian.crowd.manager.property.PropertyManager;
import com.atlassian.crowd.password.encoder.InternalPasswordEncoder;
import com.atlassian.crowd.password.encoder.LdapPasswordEncoder;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javax.crypto.Cipher;
import org.apache.commons.codec.binary.Base64;

public class DESPasswordEncoder
implements LdapPasswordEncoder,
InternalPasswordEncoder {
    private PropertyManager propertyManager;
    public static final String PASSWORD_ENCRYPTION_ALGORITHM = "DES";

    @Override
    @SuppressFBWarnings(value={"CIPHER_INTEGRITY", "DES_USAGE", "ECB_MODE"})
    public String encodePassword(String rawPass, Object salt) {
        try {
            Cipher ecipher = Cipher.getInstance(PASSWORD_ENCRYPTION_ALGORITHM);
            ecipher.init(1, this.propertyManager.getDesEncryptionKey());
            byte[] utf8 = rawPass.getBytes("UTF-8");
            byte[] enc = ecipher.doFinal(utf8);
            return Base64.encodeBase64String((byte[])enc);
        }
        catch (Exception e) {
            throw new PasswordEncoderException("Failed to encrypt password to DES", e);
        }
    }

    @Override
    public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        boolean valid = false;
        if (encPass != null) {
            valid = encPass.equals(this.encodePassword(rawPass, salt));
        }
        return valid;
    }

    @Override
    public String getKey() {
        return "des";
    }

    public void setPropertyManager(PropertyManager propertyManager) {
        this.propertyManager = propertyManager;
    }
}

