/*
 * Decompiled with CFR 0.152.
 */
package com.sun.mail.auth;

import com.sun.mail.auth.MD4;
import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.BASE64EncoderStream;
import com.sun.mail.util.MailLogger;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Level;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Ntlm {
    private byte[] type1;
    private byte[] type3;
    private SecretKeyFactory fac;
    private Cipher cipher;
    private MD4 md4;
    private String hostname;
    private String ntdomain;
    private String username;
    private String password;
    private Mac hmac;
    private MailLogger logger;
    private static final int NTLMSSP_NEGOTIATE_UNICODE = 1;
    private static final int NTLMSSP_NEGOTIATE_OEM = 2;
    private static final int NTLMSSP_REQUEST_TARGET = 4;
    private static final int NTLMSSP_NEGOTIATE_SIGN = 16;
    private static final int NTLMSSP_NEGOTIATE_SEAL = 32;
    private static final int NTLMSSP_NEGOTIATE_DATAGRAM = 64;
    private static final int NTLMSSP_NEGOTIATE_LM_KEY = 128;
    private static final int NTLMSSP_NEGOTIATE_NTLM = 512;
    private static final int NTLMSSP_NEGOTIATE_OEM_DOMAIN_SUPPLIED = 4096;
    private static final int NTLMSSP_NEGOTIATE_OEM_WORKSTATION_SUPPLIED = 8192;
    private static final int NTLMSSP_NEGOTIATE_ALWAYS_SIGN = 32768;
    private static final int NTLMSSP_TARGET_TYPE_DOMAIN = 65536;
    private static final int NTLMSSP_TARGET_TYPE_SERVER = 131072;
    private static final int NTLMSSP_NEGOTIATE_EXTENDED_SESSIONSECURITY = 524288;
    private static final int NTLMSSP_NEGOTIATE_IDENTIFY = 0x100000;
    private static final int NTLMSSP_REQUEST_NON_NT_SESSION_KEY = 0x400000;
    private static final int NTLMSSP_NEGOTIATE_TARGET_INFO = 0x800000;
    private static final int NTLMSSP_NEGOTIATE_VERSION = 0x2000000;
    private static final int NTLMSSP_NEGOTIATE_128 = 0x20000000;
    private static final int NTLMSSP_NEGOTIATE_KEY_EXCH = 0x40000000;
    private static final int NTLMSSP_NEGOTIATE_56 = Integer.MIN_VALUE;
    private static final byte RESPONSERVERSION = 1;
    private static final byte HIRESPONSERVERSION = 1;
    private static final byte[] Z6 = new byte[]{0, 0, 0, 0, 0, 0};
    private static final byte[] Z4 = new byte[]{0, 0, 0, 0};
    private static char[] hex = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private void init0() {
        block4: {
            this.type1 = new byte[256];
            this.type3 = new byte[512];
            System.arraycopy(new byte[]{78, 84, 76, 77, 83, 83, 80, 0, 1}, 0, this.type1, 0, 9);
            System.arraycopy(new byte[]{78, 84, 76, 77, 83, 83, 80, 0, 3}, 0, this.type3, 0, 9);
            try {
                this.fac = SecretKeyFactory.getInstance("DES");
                this.cipher = Cipher.getInstance("DES/ECB/NoPadding");
                this.md4 = new MD4();
            }
            catch (NoSuchPaddingException e) {
                assert (false);
            }
            catch (NoSuchAlgorithmException e) {
                if ($assertionsDisabled) break block4;
                throw new AssertionError();
            }
        }
    }

    public Ntlm(String ntdomain, String hostname, String username, String password, MailLogger logger) {
        int i = hostname.indexOf(46);
        if (i != -1) {
            hostname = hostname.substring(0, i);
        }
        if ((i = username.indexOf(92)) != -1) {
            ntdomain = username.substring(0, i).toUpperCase(Locale.ENGLISH);
            username = username.substring(i + 1);
        } else if (ntdomain == null) {
            ntdomain = "";
        }
        this.ntdomain = ntdomain;
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.logger = logger.getLogger(this.getClass(), "DEBUG NTLM");
        this.init0();
    }

    private void copybytes(byte[] dest, int destpos, String src, String enc) {
        block2: {
            try {
                byte[] x = src.getBytes(enc);
                System.arraycopy(x, 0, dest, destpos, x.length);
            }
            catch (UnsupportedEncodingException e) {
                if ($assertionsDisabled) break block2;
                throw new AssertionError();
            }
        }
    }

    public String generateType1Msg(int flags) {
        return this.generateType1Msg(flags, false);
    }

    public String generateType1Msg(int flags, boolean v2) {
        String result;
        block5: {
            int dlen = this.ntdomain.length();
            int type1flags = 0xA203 | flags;
            if (dlen != 0) {
                type1flags |= 0x1000;
            }
            if (v2) {
                type1flags |= 0x80000;
            }
            this.writeInt(this.type1, 12, type1flags);
            this.type1[28] = 32;
            this.writeShort(this.type1, 16, dlen);
            this.writeShort(this.type1, 18, dlen);
            int hlen = this.hostname.length();
            this.writeShort(this.type1, 24, hlen);
            this.writeShort(this.type1, 26, hlen);
            this.copybytes(this.type1, 32, this.hostname, "iso-8859-1");
            this.copybytes(this.type1, hlen + 32, this.ntdomain, "iso-8859-1");
            this.writeInt(this.type1, 20, hlen + 32);
            byte[] msg = new byte[32 + hlen + dlen];
            System.arraycopy(this.type1, 0, msg, 0, 32 + hlen + dlen);
            if (this.logger.isLoggable(Level.FINE)) {
                this.logger.fine("type 1 message: " + Ntlm.toHex(msg));
            }
            result = null;
            try {
                result = new String(BASE64EncoderStream.encode(msg), "iso-8859-1");
            }
            catch (UnsupportedEncodingException e) {
                if ($assertionsDisabled) break block5;
                throw new AssertionError();
            }
        }
        return result;
    }

    private byte[] makeDesKey(byte[] input, int off) {
        int[] in = new int[input.length];
        for (int i = 0; i < in.length; ++i) {
            in[i] = input[i] < 0 ? input[i] + 256 : input[i];
        }
        byte[] out = new byte[]{(byte)in[off + 0], (byte)(in[off + 0] << 7 & 0xFF | in[off + 1] >> 1), (byte)(in[off + 1] << 6 & 0xFF | in[off + 2] >> 2), (byte)(in[off + 2] << 5 & 0xFF | in[off + 3] >> 3), (byte)(in[off + 3] << 4 & 0xFF | in[off + 4] >> 4), (byte)(in[off + 4] << 3 & 0xFF | in[off + 5] >> 5), (byte)(in[off + 5] << 2 & 0xFF | in[off + 6] >> 6), (byte)(in[off + 6] << 1 & 0xFF)};
        return out;
    }

    private byte[] hmacMD5(byte[] key, byte[] text) {
        block7: {
            try {
                if (this.hmac == null) {
                    this.hmac = Mac.getInstance("HmacMD5");
                }
            }
            catch (NoSuchAlgorithmException ex) {
                throw new AssertionError();
            }
            try {
                byte[] nk = new byte[16];
                System.arraycopy(key, 0, nk, 0, key.length > 16 ? 16 : key.length);
                SecretKeySpec skey = new SecretKeySpec(nk, "HmacMD5");
                this.hmac.init(skey);
                return this.hmac.doFinal(text);
            }
            catch (InvalidKeyException ex) {
                assert (false);
            }
            catch (RuntimeException e) {
                if ($assertionsDisabled) break block7;
                throw new AssertionError();
            }
        }
        return null;
    }

    private byte[] calcLMHash() throws GeneralSecurityException {
        byte[] pwb;
        byte[] magic;
        block3: {
            magic = new byte[]{75, 71, 83, 33, 64, 35, 36, 37};
            pwb = null;
            try {
                pwb = this.password.toUpperCase(Locale.ENGLISH).getBytes("iso-8859-1");
            }
            catch (UnsupportedEncodingException ex) {
                if ($assertionsDisabled) break block3;
                throw new AssertionError();
            }
        }
        byte[] pwb1 = new byte[14];
        int len = this.password.length();
        if (len > 14) {
            len = 14;
        }
        System.arraycopy(pwb, 0, pwb1, 0, len);
        DESKeySpec dks1 = new DESKeySpec(this.makeDesKey(pwb1, 0));
        DESKeySpec dks2 = new DESKeySpec(this.makeDesKey(pwb1, 7));
        SecretKey key1 = this.fac.generateSecret(dks1);
        SecretKey key2 = this.fac.generateSecret(dks2);
        this.cipher.init(1, key1);
        byte[] out1 = this.cipher.doFinal(magic, 0, 8);
        this.cipher.init(1, key2);
        byte[] out2 = this.cipher.doFinal(magic, 0, 8);
        byte[] result = new byte[21];
        System.arraycopy(out1, 0, result, 0, 8);
        System.arraycopy(out2, 0, result, 8, 8);
        return result;
    }

    private byte[] calcNTHash() throws GeneralSecurityException {
        byte[] pw;
        block2: {
            pw = null;
            try {
                pw = this.password.getBytes("UnicodeLittleUnmarked");
            }
            catch (UnsupportedEncodingException e) {
                if ($assertionsDisabled) break block2;
                throw new AssertionError();
            }
        }
        byte[] out = this.md4.digest(pw);
        byte[] result = new byte[21];
        System.arraycopy(out, 0, result, 0, 16);
        return result;
    }

    private byte[] calcResponse(byte[] key, byte[] text) throws GeneralSecurityException {
        assert (key.length == 21);
        DESKeySpec dks1 = new DESKeySpec(this.makeDesKey(key, 0));
        DESKeySpec dks2 = new DESKeySpec(this.makeDesKey(key, 7));
        DESKeySpec dks3 = new DESKeySpec(this.makeDesKey(key, 14));
        SecretKey key1 = this.fac.generateSecret(dks1);
        SecretKey key2 = this.fac.generateSecret(dks2);
        SecretKey key3 = this.fac.generateSecret(dks3);
        this.cipher.init(1, key1);
        byte[] out1 = this.cipher.doFinal(text, 0, 8);
        this.cipher.init(1, key2);
        byte[] out2 = this.cipher.doFinal(text, 0, 8);
        this.cipher.init(1, key3);
        byte[] out3 = this.cipher.doFinal(text, 0, 8);
        byte[] result = new byte[24];
        System.arraycopy(out1, 0, result, 0, 8);
        System.arraycopy(out2, 0, result, 8, 8);
        System.arraycopy(out3, 0, result, 16, 8);
        return result;
    }

    private byte[] calcV2Response(byte[] nthash, byte[] blob, byte[] challenge) throws GeneralSecurityException {
        byte[] txt;
        block2: {
            txt = null;
            try {
                txt = (this.username.toUpperCase(Locale.ENGLISH) + this.ntdomain).getBytes("UnicodeLittleUnmarked");
            }
            catch (UnsupportedEncodingException ex) {
                if ($assertionsDisabled) break block2;
                throw new AssertionError();
            }
        }
        byte[] ntlmv2hash = this.hmacMD5(nthash, txt);
        byte[] cb = new byte[blob.length + 8];
        System.arraycopy(challenge, 0, cb, 0, 8);
        System.arraycopy(blob, 0, cb, 8, blob.length);
        byte[] result = new byte[blob.length + 16];
        System.arraycopy(this.hmacMD5(ntlmv2hash, cb), 0, result, 0, 16);
        System.arraycopy(blob, 0, result, 16, blob.length);
        return result;
    }

    public String generateType3Msg(String type2msg) {
        try {
            String result;
            block13: {
                byte[] type2;
                block12: {
                    type2 = null;
                    try {
                        type2 = BASE64DecoderStream.decode(type2msg.getBytes("us-ascii"));
                    }
                    catch (UnsupportedEncodingException ex) {
                        if ($assertionsDisabled) break block12;
                        throw new AssertionError();
                    }
                }
                if (this.logger.isLoggable(Level.FINE)) {
                    this.logger.fine("type 2 message: " + Ntlm.toHex(type2));
                }
                byte[] challenge = new byte[8];
                System.arraycopy(type2, 24, challenge, 0, 8);
                int type3flags = 33281;
                int ulen = this.username.length() * 2;
                this.writeShort(this.type3, 36, ulen);
                this.writeShort(this.type3, 38, ulen);
                int dlen = this.ntdomain.length() * 2;
                this.writeShort(this.type3, 28, dlen);
                this.writeShort(this.type3, 30, dlen);
                int hlen = this.hostname.length() * 2;
                this.writeShort(this.type3, 44, hlen);
                this.writeShort(this.type3, 46, hlen);
                int l = 64;
                this.copybytes(this.type3, l, this.ntdomain, "UnicodeLittleUnmarked");
                this.writeInt(this.type3, 32, l);
                this.copybytes(this.type3, l += dlen, this.username, "UnicodeLittleUnmarked");
                this.writeInt(this.type3, 40, l);
                this.copybytes(this.type3, l += ulen, this.hostname, "UnicodeLittleUnmarked");
                this.writeInt(this.type3, 48, l);
                l += hlen;
                byte[] msg = null;
                byte[] lmresponse = null;
                byte[] ntresponse = null;
                int flags = Ntlm.readInt(type2, 20);
                if ((flags & 0x80000) != 0) {
                    this.logger.fine("Using NTLMv2");
                    type3flags |= 0x80000;
                    byte[] nonce = new byte[8];
                    new Random().nextBytes(nonce);
                    byte[] nthash = this.calcNTHash();
                    lmresponse = this.calcV2Response(nthash, nonce, challenge);
                    byte[] targetInfo = new byte[]{};
                    if ((flags & 0x800000) != 0) {
                        int tlen = Ntlm.readShort(type2, 40);
                        int toff = Ntlm.readInt(type2, 44);
                        targetInfo = new byte[tlen];
                        System.arraycopy(type2, toff, targetInfo, 0, tlen);
                    }
                    byte[] blob = new byte[32 + targetInfo.length];
                    blob[0] = 1;
                    blob[1] = 1;
                    System.arraycopy(Z6, 0, blob, 2, 6);
                    long now = (System.currentTimeMillis() + 11644473600000L) * 10000L;
                    for (int i = 0; i < 8; ++i) {
                        blob[8 + i] = (byte)(now & 0xFFL);
                        now >>= 8;
                    }
                    System.arraycopy(nonce, 0, blob, 16, 8);
                    System.arraycopy(Z4, 0, blob, 24, 4);
                    System.arraycopy(targetInfo, 0, blob, 28, targetInfo.length);
                    System.arraycopy(Z4, 0, blob, 28 + targetInfo.length, 4);
                    ntresponse = this.calcV2Response(nthash, blob, challenge);
                } else {
                    byte[] lmhash = this.calcLMHash();
                    lmresponse = this.calcResponse(lmhash, challenge);
                    byte[] nthash = this.calcNTHash();
                    ntresponse = this.calcResponse(nthash, challenge);
                }
                System.arraycopy(lmresponse, 0, this.type3, l, lmresponse.length);
                this.writeShort(this.type3, 12, lmresponse.length);
                this.writeShort(this.type3, 14, lmresponse.length);
                this.writeInt(this.type3, 16, l);
                System.arraycopy(ntresponse, 0, this.type3, l += 24, ntresponse.length);
                this.writeShort(this.type3, 20, ntresponse.length);
                this.writeShort(this.type3, 22, ntresponse.length);
                this.writeInt(this.type3, 24, l);
                this.writeShort(this.type3, 56, l += ntresponse.length);
                msg = new byte[l];
                System.arraycopy(this.type3, 0, msg, 0, l);
                this.writeInt(this.type3, 60, type3flags);
                if (this.logger.isLoggable(Level.FINE)) {
                    this.logger.fine("type 3 message: " + Ntlm.toHex(msg));
                }
                result = null;
                try {
                    result = new String(BASE64EncoderStream.encode(msg), "iso-8859-1");
                }
                catch (UnsupportedEncodingException e) {
                    if ($assertionsDisabled) break block13;
                    throw new AssertionError();
                }
            }
            return result;
        }
        catch (GeneralSecurityException ex) {
            this.logger.log(Level.FINE, "GeneralSecurityException", ex);
            return "";
        }
    }

    private static int readShort(byte[] b, int off) {
        return b[off] & 0xFF | (b[off + 1] & 0xFF) << 8;
    }

    private void writeShort(byte[] b, int off, int data) {
        b[off] = (byte)(data & 0xFF);
        b[off + 1] = (byte)(data >> 8 & 0xFF);
    }

    private static int readInt(byte[] b, int off) {
        return b[off] & 0xFF | (b[off + 1] & 0xFF) << 8 | (b[off + 2] & 0xFF) << 16 | (b[off + 3] & 0xFF) << 24;
    }

    private void writeInt(byte[] b, int off, int data) {
        b[off] = (byte)(data & 0xFF);
        b[off + 1] = (byte)(data >> 8 & 0xFF);
        b[off + 2] = (byte)(data >> 16 & 0xFF);
        b[off + 3] = (byte)(data >> 24 & 0xFF);
    }

    private static String toHex(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 3);
        for (int i = 0; i < b.length; ++i) {
            sb.append(hex[b[i] >> 4 & 0xF]).append(hex[b[i] & 0xF]).append(' ');
        }
        return sb.toString();
    }
}

