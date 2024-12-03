/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.encryption;

import java.security.AccessController;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class XMLCipherUtil {
    private static final Logger LOG = LoggerFactory.getLogger(XMLCipherUtil.class);
    private static final boolean gcmUseIvParameterSpec = AccessController.doPrivileged(() -> Boolean.getBoolean("org.apache.xml.security.cipher.gcm.useIvParameterSpec"));

    public static AlgorithmParameterSpec constructBlockCipherParameters(String algorithm, byte[] iv) {
        if ("http://www.w3.org/2009/xmlenc11#aes128-gcm".equals(algorithm) || "http://www.w3.org/2009/xmlenc11#aes192-gcm".equals(algorithm) || "http://www.w3.org/2009/xmlenc11#aes256-gcm".equals(algorithm)) {
            return XMLCipherUtil.constructBlockCipherParametersForGCMAlgorithm(algorithm, iv);
        }
        LOG.debug("Saw non-AES-GCM mode block cipher, returning IvParameterSpec: {}", (Object)algorithm);
        return new IvParameterSpec(iv);
    }

    public static AlgorithmParameterSpec constructBlockCipherParameters(boolean gcmAlgorithm, byte[] iv) {
        if (gcmAlgorithm) {
            return XMLCipherUtil.constructBlockCipherParametersForGCMAlgorithm("AES/GCM/NoPadding", iv);
        }
        LOG.debug("Saw non-AES-GCM mode block cipher, returning IvParameterSpec");
        return new IvParameterSpec(iv);
    }

    private static AlgorithmParameterSpec constructBlockCipherParametersForGCMAlgorithm(String algorithm, byte[] iv) {
        if (gcmUseIvParameterSpec) {
            LOG.debug("Saw AES-GCM block cipher, using IvParameterSpec due to system property override: {}", (Object)algorithm);
            return new IvParameterSpec(iv);
        }
        LOG.debug("Saw AES-GCM block cipher, attempting to create GCMParameterSpec: {}", (Object)algorithm);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
        LOG.debug("Successfully created GCMParameterSpec");
        return gcmSpec;
    }
}

