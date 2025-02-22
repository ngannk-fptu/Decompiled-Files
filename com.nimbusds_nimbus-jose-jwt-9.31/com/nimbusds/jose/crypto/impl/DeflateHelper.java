/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.CompressionAlgorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.util.DeflateUtils;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class DeflateHelper {
    public static byte[] applyCompression(JWEHeader jweHeader, byte[] bytes) throws JOSEException {
        CompressionAlgorithm compressionAlg = jweHeader.getCompressionAlgorithm();
        if (compressionAlg == null) {
            return bytes;
        }
        if (compressionAlg.equals(CompressionAlgorithm.DEF)) {
            try {
                return DeflateUtils.compress(bytes);
            }
            catch (Exception e) {
                throw new JOSEException("Couldn't compress plain text: " + e.getMessage(), e);
            }
        }
        throw new JOSEException("Unsupported compression algorithm: " + compressionAlg);
    }

    public static byte[] applyDecompression(JWEHeader jweHeader, byte[] bytes) throws JOSEException {
        CompressionAlgorithm compressionAlg = jweHeader.getCompressionAlgorithm();
        if (compressionAlg == null) {
            return bytes;
        }
        if (compressionAlg.equals(CompressionAlgorithm.DEF)) {
            try {
                return DeflateUtils.decompress(bytes);
            }
            catch (Exception e) {
                throw new JOSEException("Couldn't decompress plain text: " + e.getMessage(), e);
            }
        }
        throw new JOSEException("Unsupported compression algorithm: " + compressionAlg);
    }
}

