/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base64
 */
package org.apache.commons.httpclient.auth;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.auth.AuthenticationException;
import org.apache.commons.httpclient.util.EncodingUtil;

final class NTLM {
    public static final String DEFAULT_CHARSET = "ASCII";
    private byte[] currentResponse;
    private int currentPosition = 0;
    private String credentialCharset = "ASCII";

    NTLM() {
    }

    public final String getResponseFor(String message, String username, String password, String host, String domain) throws AuthenticationException {
        String response = message == null || message.trim().equals("") ? this.getType1Message(host, domain) : this.getType3Message(username, password, host, domain, this.parseType2Message(message));
        return response;
    }

    private Cipher getCipher(byte[] key) throws AuthenticationException {
        try {
            Cipher ecipher = Cipher.getInstance("DES/ECB/NoPadding");
            key = this.setupKey(key);
            ecipher.init(1, new SecretKeySpec(key, "DES"));
            return ecipher;
        }
        catch (NoSuchAlgorithmException e) {
            throw new AuthenticationException("DES encryption is not available.", e);
        }
        catch (InvalidKeyException e) {
            throw new AuthenticationException("Invalid key for DES encryption.", e);
        }
        catch (NoSuchPaddingException e) {
            throw new AuthenticationException("NoPadding option for DES is not available.", e);
        }
    }

    private byte[] setupKey(byte[] key56) {
        byte[] key = new byte[]{(byte)(key56[0] >> 1 & 0xFF), (byte)(((key56[0] & 1) << 6 | (key56[1] & 0xFF) >> 2 & 0xFF) & 0xFF), (byte)(((key56[1] & 3) << 5 | (key56[2] & 0xFF) >> 3 & 0xFF) & 0xFF), (byte)(((key56[2] & 7) << 4 | (key56[3] & 0xFF) >> 4 & 0xFF) & 0xFF), (byte)(((key56[3] & 0xF) << 3 | (key56[4] & 0xFF) >> 5 & 0xFF) & 0xFF), (byte)(((key56[4] & 0x1F) << 2 | (key56[5] & 0xFF) >> 6 & 0xFF) & 0xFF), (byte)(((key56[5] & 0x3F) << 1 | (key56[6] & 0xFF) >> 7 & 0xFF) & 0xFF), (byte)(key56[6] & 0x7F)};
        for (int i = 0; i < key.length; ++i) {
            key[i] = (byte)(key[i] << 1);
        }
        return key;
    }

    private byte[] encrypt(byte[] key, byte[] bytes) throws AuthenticationException {
        Cipher ecipher = this.getCipher(key);
        try {
            byte[] enc = ecipher.doFinal(bytes);
            return enc;
        }
        catch (IllegalBlockSizeException e) {
            throw new AuthenticationException("Invalid block size for DES encryption.", e);
        }
        catch (BadPaddingException e) {
            throw new AuthenticationException("Data not padded correctly for DES encryption.", e);
        }
    }

    private void prepareResponse(int length) {
        this.currentResponse = new byte[length];
        this.currentPosition = 0;
    }

    private void addByte(byte b) {
        this.currentResponse[this.currentPosition] = b;
        ++this.currentPosition;
    }

    private void addBytes(byte[] bytes) {
        for (int i = 0; i < bytes.length; ++i) {
            this.currentResponse[this.currentPosition] = bytes[i];
            ++this.currentPosition;
        }
    }

    private String getResponse() {
        byte[] resp;
        if (this.currentResponse.length > this.currentPosition) {
            byte[] tmp = new byte[this.currentPosition];
            for (int i = 0; i < this.currentPosition; ++i) {
                tmp[i] = this.currentResponse[i];
            }
            resp = tmp;
        } else {
            resp = this.currentResponse;
        }
        return EncodingUtil.getAsciiString(Base64.encodeBase64((byte[])resp));
    }

    public String getType1Message(String host, String domain) {
        host = host.toUpperCase(Locale.ENGLISH);
        domain = domain.toUpperCase(Locale.ENGLISH);
        byte[] hostBytes = EncodingUtil.getBytes(host, DEFAULT_CHARSET);
        byte[] domainBytes = EncodingUtil.getBytes(domain, DEFAULT_CHARSET);
        int finalLength = 32 + hostBytes.length + domainBytes.length;
        this.prepareResponse(finalLength);
        byte[] protocol = EncodingUtil.getBytes("NTLMSSP", DEFAULT_CHARSET);
        this.addBytes(protocol);
        this.addByte((byte)0);
        this.addByte((byte)1);
        this.addByte((byte)0);
        this.addByte((byte)0);
        this.addByte((byte)0);
        this.addByte((byte)6);
        this.addByte((byte)82);
        this.addByte((byte)0);
        this.addByte((byte)0);
        int iDomLen = domainBytes.length;
        byte[] domLen = this.convertShort(iDomLen);
        this.addByte(domLen[0]);
        this.addByte(domLen[1]);
        this.addByte(domLen[0]);
        this.addByte(domLen[1]);
        byte[] domOff = this.convertShort(hostBytes.length + 32);
        this.addByte(domOff[0]);
        this.addByte(domOff[1]);
        this.addByte((byte)0);
        this.addByte((byte)0);
        byte[] hostLen = this.convertShort(hostBytes.length);
        this.addByte(hostLen[0]);
        this.addByte(hostLen[1]);
        this.addByte(hostLen[0]);
        this.addByte(hostLen[1]);
        byte[] hostOff = this.convertShort(32);
        this.addByte(hostOff[0]);
        this.addByte(hostOff[1]);
        this.addByte((byte)0);
        this.addByte((byte)0);
        this.addBytes(hostBytes);
        this.addBytes(domainBytes);
        return this.getResponse();
    }

    public byte[] parseType2Message(String message) {
        byte[] msg = Base64.decodeBase64((byte[])EncodingUtil.getBytes(message, DEFAULT_CHARSET));
        byte[] nonce = new byte[8];
        for (int i = 0; i < 8; ++i) {
            nonce[i] = msg[i + 24];
        }
        return nonce;
    }

    public String getType3Message(String user, String password, String host, String domain, byte[] nonce) throws AuthenticationException {
        int ntRespLen = 0;
        int lmRespLen = 24;
        domain = domain.toUpperCase(Locale.ENGLISH);
        host = host.toUpperCase(Locale.ENGLISH);
        user = user.toUpperCase(Locale.ENGLISH);
        byte[] domainBytes = EncodingUtil.getBytes(domain, DEFAULT_CHARSET);
        byte[] hostBytes = EncodingUtil.getBytes(host, DEFAULT_CHARSET);
        byte[] userBytes = EncodingUtil.getBytes(user, this.credentialCharset);
        int domainLen = domainBytes.length;
        int hostLen = hostBytes.length;
        int userLen = userBytes.length;
        int finalLength = 64 + ntRespLen + lmRespLen + domainLen + userLen + hostLen;
        this.prepareResponse(finalLength);
        byte[] ntlmssp = EncodingUtil.getBytes("NTLMSSP", DEFAULT_CHARSET);
        this.addBytes(ntlmssp);
        this.addByte((byte)0);
        this.addByte((byte)3);
        this.addByte((byte)0);
        this.addByte((byte)0);
        this.addByte((byte)0);
        this.addBytes(this.convertShort(24));
        this.addBytes(this.convertShort(24));
        this.addBytes(this.convertShort(finalLength - 24));
        this.addByte((byte)0);
        this.addByte((byte)0);
        this.addBytes(this.convertShort(0));
        this.addBytes(this.convertShort(0));
        this.addBytes(this.convertShort(finalLength));
        this.addByte((byte)0);
        this.addByte((byte)0);
        this.addBytes(this.convertShort(domainLen));
        this.addBytes(this.convertShort(domainLen));
        this.addBytes(this.convertShort(64));
        this.addByte((byte)0);
        this.addByte((byte)0);
        this.addBytes(this.convertShort(userLen));
        this.addBytes(this.convertShort(userLen));
        this.addBytes(this.convertShort(64 + domainLen));
        this.addByte((byte)0);
        this.addByte((byte)0);
        this.addBytes(this.convertShort(hostLen));
        this.addBytes(this.convertShort(hostLen));
        this.addBytes(this.convertShort(64 + domainLen + userLen));
        for (int i = 0; i < 6; ++i) {
            this.addByte((byte)0);
        }
        this.addBytes(this.convertShort(finalLength));
        this.addByte((byte)0);
        this.addByte((byte)0);
        this.addByte((byte)6);
        this.addByte((byte)82);
        this.addByte((byte)0);
        this.addByte((byte)0);
        this.addBytes(domainBytes);
        this.addBytes(userBytes);
        this.addBytes(hostBytes);
        this.addBytes(this.hashPassword(password, nonce));
        return this.getResponse();
    }

    private byte[] hashPassword(String password, byte[] nonce) throws AuthenticationException {
        int i;
        int idx;
        byte[] passw = EncodingUtil.getBytes(password.toUpperCase(Locale.ENGLISH), this.credentialCharset);
        byte[] lmPw1 = new byte[7];
        byte[] lmPw2 = new byte[7];
        int len = passw.length;
        if (len > 7) {
            len = 7;
        }
        for (idx = 0; idx < len; ++idx) {
            lmPw1[idx] = passw[idx];
        }
        while (idx < 7) {
            lmPw1[idx] = 0;
            ++idx;
        }
        len = passw.length;
        if (len > 14) {
            len = 14;
        }
        for (idx = 7; idx < len; ++idx) {
            lmPw2[idx - 7] = passw[idx];
        }
        while (idx < 14) {
            lmPw2[idx - 7] = 0;
            ++idx;
        }
        byte[] magic = new byte[]{75, 71, 83, 33, 64, 35, 36, 37};
        byte[] lmHpw1 = this.encrypt(lmPw1, magic);
        byte[] lmHpw2 = this.encrypt(lmPw2, magic);
        byte[] lmHpw = new byte[21];
        for (i = 0; i < lmHpw1.length; ++i) {
            lmHpw[i] = lmHpw1[i];
        }
        for (i = 0; i < lmHpw2.length; ++i) {
            lmHpw[i + 8] = lmHpw2[i];
        }
        for (i = 0; i < 5; ++i) {
            lmHpw[i + 16] = 0;
        }
        byte[] lmResp = new byte[24];
        this.calcResp(lmHpw, nonce, lmResp);
        return lmResp;
    }

    private void calcResp(byte[] keys, byte[] plaintext, byte[] results) throws AuthenticationException {
        int i;
        int i2;
        byte[] keys1 = new byte[7];
        byte[] keys2 = new byte[7];
        byte[] keys3 = new byte[7];
        for (i2 = 0; i2 < 7; ++i2) {
            keys1[i2] = keys[i2];
        }
        for (i2 = 0; i2 < 7; ++i2) {
            keys2[i2] = keys[i2 + 7];
        }
        for (i2 = 0; i2 < 7; ++i2) {
            keys3[i2] = keys[i2 + 14];
        }
        byte[] results1 = this.encrypt(keys1, plaintext);
        byte[] results2 = this.encrypt(keys2, plaintext);
        byte[] results3 = this.encrypt(keys3, plaintext);
        for (i = 0; i < 8; ++i) {
            results[i] = results1[i];
        }
        for (i = 0; i < 8; ++i) {
            results[i + 8] = results2[i];
        }
        for (i = 0; i < 8; ++i) {
            results[i + 16] = results3[i];
        }
    }

    private byte[] convertShort(int num) {
        byte[] val = new byte[2];
        String hex = Integer.toString(num, 16);
        while (hex.length() < 4) {
            hex = "0" + hex;
        }
        String low = hex.substring(2, 4);
        String high = hex.substring(0, 2);
        val[0] = (byte)Integer.parseInt(low, 16);
        val[1] = (byte)Integer.parseInt(high, 16);
        return val;
    }

    public String getCredentialCharset() {
        return this.credentialCharset;
    }

    public void setCredentialCharset(String credentialCharset) {
        this.credentialCharset = credentialCharset;
    }
}

