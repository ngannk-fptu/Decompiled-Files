/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.edec;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.util.Fingerprint;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

class Utils {
    Utils() {
    }

    static boolean isValidPrefix(byte[] prefix, byte[] encoding) {
        if (encoding.length < prefix.length) {
            return !Utils.isValidPrefix(prefix, prefix);
        }
        int nonEqual = 0;
        for (int i = 0; i != prefix.length; ++i) {
            nonEqual |= prefix[i] ^ encoding[i];
        }
        return nonEqual == 0;
    }

    static String keyToString(String label, String algorithm, AsymmetricKeyParameter pubKey) {
        StringBuffer buf = new StringBuffer();
        String nl = Strings.lineSeparator();
        byte[] keyBytes = pubKey instanceof X448PublicKeyParameters ? ((X448PublicKeyParameters)pubKey).getEncoded() : (pubKey instanceof Ed448PublicKeyParameters ? ((Ed448PublicKeyParameters)pubKey).getEncoded() : (pubKey instanceof X25519PublicKeyParameters ? ((X25519PublicKeyParameters)pubKey).getEncoded() : ((Ed25519PublicKeyParameters)pubKey).getEncoded()));
        buf.append(algorithm).append(" ").append(label).append(" [").append(Utils.generateKeyFingerprint(keyBytes)).append("]").append(nl).append("    public data: ").append(Hex.toHexString(keyBytes)).append(nl);
        return buf.toString();
    }

    private static String generateKeyFingerprint(byte[] keyBytes) {
        return new Fingerprint(keyBytes).toString();
    }
}

