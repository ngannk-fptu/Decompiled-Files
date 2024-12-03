/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Encryptor
 */
package com.atlassian.crowd.crypto;

import com.atlassian.crowd.crypto.Base64Encryptor;
import com.atlassian.crowd.embedded.api.Encryptor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BasicEncryptorsFactory {
    public static final String BASE_64_ENCRYPTOR = "BASE64";

    private BasicEncryptorsFactory() {
    }

    public static Map<String, Encryptor> createEncryptors() {
        HashMap<String, Base64Encryptor> encryptors = new HashMap<String, Base64Encryptor>();
        encryptors.put(BASE_64_ENCRYPTOR, new Base64Encryptor());
        return Collections.unmodifiableMap(encryptors);
    }
}

