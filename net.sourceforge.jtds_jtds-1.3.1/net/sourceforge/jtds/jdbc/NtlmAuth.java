/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import net.sourceforge.jtds.util.DESEngine;
import net.sourceforge.jtds.util.MD4Digest;
import net.sourceforge.jtds.util.MD5Digest;

public class NtlmAuth {
    public static byte[] answerNtChallenge(String password, byte[] nonce) throws UnsupportedEncodingException {
        return NtlmAuth.encryptNonce(NtlmAuth.ntHash(password), nonce);
    }

    public static byte[] answerLmChallenge(String pwd, byte[] nonce) throws UnsupportedEncodingException {
        byte[] password = NtlmAuth.convertPassword(pwd);
        DESEngine d1 = new DESEngine(true, NtlmAuth.makeDESkey(password, 0));
        DESEngine d2 = new DESEngine(true, NtlmAuth.makeDESkey(password, 7));
        byte[] encrypted = new byte[21];
        Arrays.fill(encrypted, (byte)0);
        d1.processBlock(nonce, 0, encrypted, 0);
        d2.processBlock(nonce, 0, encrypted, 8);
        return NtlmAuth.encryptNonce(encrypted, nonce);
    }

    public static byte[] answerNtlmv2Challenge(String domain, String user, String password, byte[] nonce, byte[] targetInfo, byte[] clientNonce) throws UnsupportedEncodingException {
        return NtlmAuth.answerNtlmv2Challenge(domain, user, password, nonce, targetInfo, clientNonce, System.currentTimeMillis());
    }

    public static byte[] answerNtlmv2Challenge(String domain, String user, String password, byte[] nonce, byte[] targetInfo, byte[] clientNonce, byte[] timestamp) throws UnsupportedEncodingException {
        byte[] hash = NtlmAuth.ntv2Hash(domain, user, password);
        byte[] blob = NtlmAuth.createBlob(targetInfo, clientNonce, timestamp);
        return NtlmAuth.lmv2Response(hash, blob, nonce);
    }

    public static byte[] answerNtlmv2Challenge(String domain, String user, String password, byte[] nonce, byte[] targetInfo, byte[] clientNonce, long now) throws UnsupportedEncodingException {
        return NtlmAuth.answerNtlmv2Challenge(domain, user, password, nonce, targetInfo, clientNonce, NtlmAuth.createTimestamp(now));
    }

    public static byte[] answerLmv2Challenge(String domain, String user, String password, byte[] nonce, byte[] clientNonce) throws UnsupportedEncodingException {
        byte[] hash = NtlmAuth.ntv2Hash(domain, user, password);
        return NtlmAuth.lmv2Response(hash, clientNonce, nonce);
    }

    private static byte[] ntv2Hash(String domain, String user, String password) throws UnsupportedEncodingException {
        byte[] hash = NtlmAuth.ntHash(password);
        String identity = user.toUpperCase() + domain.toUpperCase();
        byte[] identityBytes = identity.getBytes("UnicodeLittleUnmarked");
        return NtlmAuth.hmacMD5(identityBytes, hash);
    }

    private static byte[] lmv2Response(byte[] hash, byte[] clientData, byte[] challenge) {
        byte[] data = new byte[challenge.length + clientData.length];
        System.arraycopy(challenge, 0, data, 0, challenge.length);
        System.arraycopy(clientData, 0, data, challenge.length, clientData.length);
        byte[] mac = NtlmAuth.hmacMD5(data, hash);
        byte[] lmv2Response = new byte[mac.length + clientData.length];
        System.arraycopy(mac, 0, lmv2Response, 0, mac.length);
        System.arraycopy(clientData, 0, lmv2Response, mac.length, clientData.length);
        return lmv2Response;
    }

    private static byte[] hmacMD5(byte[] data, byte[] key) {
        int i;
        byte[] ipad = new byte[64];
        byte[] opad = new byte[64];
        for (i = 0; i < 64; ++i) {
            ipad[i] = 54;
            opad[i] = 92;
        }
        for (i = key.length - 1; i >= 0; --i) {
            int n = i;
            ipad[n] = (byte)(ipad[n] ^ key[i]);
            int n2 = i;
            opad[n2] = (byte)(opad[n2] ^ key[i]);
        }
        byte[] content = new byte[data.length + 64];
        System.arraycopy(ipad, 0, content, 0, 64);
        System.arraycopy(data, 0, content, 64, data.length);
        data = NtlmAuth.md5(content);
        content = new byte[data.length + 64];
        System.arraycopy(opad, 0, content, 0, 64);
        System.arraycopy(data, 0, content, 64, data.length);
        return NtlmAuth.md5(content);
    }

    private static byte[] md5(byte[] data) {
        MD5Digest md5 = new MD5Digest();
        md5.update(data, 0, data.length);
        byte[] hash = new byte[16];
        md5.doFinal(hash, 0);
        return hash;
    }

    public static byte[] createTimestamp(long time) {
        time += 11644473600000L;
        time *= 10000L;
        byte[] timestamp = new byte[8];
        for (int i = 0; i < 8; ++i) {
            timestamp[i] = (byte)time;
            time >>>= 8;
        }
        return timestamp;
    }

    private static byte[] createBlob(byte[] targetInformation, byte[] clientChallenge, byte[] timestamp) {
        byte[] blobSignature = new byte[]{1, 1, 0, 0};
        byte[] reserved = new byte[]{0, 0, 0, 0};
        byte[] unknown1 = new byte[]{0, 0, 0, 0};
        byte[] unknown2 = new byte[]{0, 0, 0, 0};
        byte[] blob = new byte[blobSignature.length + reserved.length + timestamp.length + clientChallenge.length + unknown1.length + targetInformation.length + unknown2.length];
        int offset = 0;
        System.arraycopy(blobSignature, 0, blob, offset, blobSignature.length);
        System.arraycopy(reserved, 0, blob, offset += blobSignature.length, reserved.length);
        System.arraycopy(timestamp, 0, blob, offset += reserved.length, timestamp.length);
        System.arraycopy(clientChallenge, 0, blob, offset += timestamp.length, clientChallenge.length);
        System.arraycopy(unknown1, 0, blob, offset += clientChallenge.length, unknown1.length);
        System.arraycopy(targetInformation, 0, blob, offset += unknown1.length, targetInformation.length);
        System.arraycopy(unknown2, 0, blob, offset += targetInformation.length, unknown2.length);
        return blob;
    }

    private static byte[] encryptNonce(byte[] key, byte[] nonce) {
        byte[] out = new byte[24];
        DESEngine d1 = new DESEngine(true, NtlmAuth.makeDESkey(key, 0));
        DESEngine d2 = new DESEngine(true, NtlmAuth.makeDESkey(key, 7));
        DESEngine d3 = new DESEngine(true, NtlmAuth.makeDESkey(key, 14));
        d1.processBlock(nonce, 0, out, 0);
        d2.processBlock(nonce, 0, out, 8);
        d3.processBlock(nonce, 0, out, 16);
        return out;
    }

    private static byte[] ntHash(String password) throws UnsupportedEncodingException {
        byte[] key = new byte[21];
        Arrays.fill(key, (byte)0);
        byte[] pwd = password.getBytes("UnicodeLittleUnmarked");
        MD4Digest md4 = new MD4Digest();
        md4.update(pwd, 0, pwd.length);
        md4.doFinal(key, 0);
        return key;
    }

    private static byte[] convertPassword(String password) throws UnsupportedEncodingException {
        byte[] pwd = password.toUpperCase().getBytes("UTF8");
        byte[] rtn = new byte[14];
        Arrays.fill(rtn, (byte)0);
        System.arraycopy(pwd, 0, rtn, 0, pwd.length > 14 ? 14 : pwd.length);
        return rtn;
    }

    private static byte[] makeDESkey(byte[] buf, int off) {
        byte[] ret = new byte[]{(byte)(buf[off + 0] >> 1 & 0xFF), (byte)(((buf[off + 0] & 1) << 6 | (buf[off + 1] & 0xFF) >> 2 & 0xFF) & 0xFF), (byte)(((buf[off + 1] & 3) << 5 | (buf[off + 2] & 0xFF) >> 3 & 0xFF) & 0xFF), (byte)(((buf[off + 2] & 7) << 4 | (buf[off + 3] & 0xFF) >> 4 & 0xFF) & 0xFF), (byte)(((buf[off + 3] & 0xF) << 3 | (buf[off + 4] & 0xFF) >> 5 & 0xFF) & 0xFF), (byte)(((buf[off + 4] & 0x1F) << 2 | (buf[off + 5] & 0xFF) >> 6 & 0xFF) & 0xFF), (byte)(((buf[off + 5] & 0x3F) << 1 | (buf[off + 6] & 0xFF) >> 7 & 0xFF) & 0xFF), (byte)(buf[off + 6] & 0x7F)};
        for (int i = 0; i < 8; ++i) {
            ret[i] = (byte)(ret[i] << 1);
        }
        return ret;
    }
}

