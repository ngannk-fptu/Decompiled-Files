/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package com.atlassian.secrets.tomcat.utils;

import com.atlassian.secrets.api.SecretStoreException;
import com.atlassian.secrets.tomcat.cipher.ProductCipher;
import com.atlassian.secrets.tomcat.utils.PasswordDataBean;
import java.io.File;
import java.util.Optional;
import java.util.Set;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class DecryptionUtils {
    private static final Log log = LogFactory.getLog(DecryptionUtils.class);

    public static void initPasswords(Set<PasswordDataBean> passwordsToSet, Optional<String> encryptionKey) {
        if (passwordsToSet.isEmpty()) {
            log.debug((Object)"No passwords to set");
            return;
        }
        ProductCipher cipher = new ProductCipher();
        String encryptionKeyFile = encryptionKey.orElseThrow(() -> new SecretStoreException("Cannot decrypt passwords since the encryption key is missing"));
        for (PasswordDataBean passwordDataBean : passwordsToSet) {
            log.debug((Object)("Setting password: " + passwordDataBean.passwordName));
            if (DecryptionUtils.isFile(passwordDataBean.encryptedPasswordFile)) {
                String decryptedPassword = cipher.decrypt(passwordDataBean.encryptedPasswordFile, encryptionKeyFile);
                passwordDataBean.superSetter.accept(decryptedPassword);
                log.debug((Object)("Successfully decrypted and set password: " + passwordDataBean.passwordName));
                continue;
            }
            log.warn((Object)String.format("Password for %s is not a file path or the file is missing. Attempting to use password as it is", passwordDataBean.passwordName));
            passwordDataBean.superSetter.accept(passwordDataBean.encryptedPasswordFile);
        }
        log.debug((Object)"All passwords have been set");
    }

    private static boolean isFile(String encryptedPasswordFile) {
        File f = new File(encryptedPasswordFile);
        return f.isFile();
    }
}

