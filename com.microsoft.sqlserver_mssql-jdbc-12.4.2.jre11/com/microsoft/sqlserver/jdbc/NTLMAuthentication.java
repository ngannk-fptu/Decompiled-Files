/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SSPIAuthentication;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import mssql.security.provider.MD4;

final class NTLMAuthentication
extends SSPIAuthentication {
    private final Logger logger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.NTLMAuthentication");
    private static final byte[] NTLM_HEADER_SIGNATURE = new byte[]{78, 84, 76, 77, 83, 83, 80, 0};
    private static final int NTLM_MESSAGE_TYPE_NEGOTIATE = 1;
    private static final int NTLM_MESSAGE_TYPE_CHALLENGE = 2;
    private static final int NTLM_MESSAGE_TYPE_AUTHENTICATE = 3;
    private static final byte[] NTLM_CLIENT_CHALLENGE_RESPONSE_TYPE = new byte[]{1, 1};
    private static final byte[] NTLM_CLIENT_CHALLENGE_RESERVED1 = new byte[]{0, 0};
    private static final byte[] NTLM_CLIENT_CHALLENGE_RESERVED2 = new byte[]{0, 0, 0, 0};
    private static final byte[] NTLM_CLIENT_CHALLENGE_RESERVED3 = new byte[]{0, 0, 0, 0};
    private static final byte[] NTLM_LMCHALLENAGERESPONSE = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final byte[] NTLMSSP_VERSION = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
    private static final long NTLMSSP_NEGOTIATE_UNICODE = 1L;
    private static final long NTLMSSP_REQUEST_TARGET = 4L;
    private static final long NTLMSSP_NEGOTIATE_OEM_DOMAIN_SUPPLIED = 4096L;
    private static final long NTLMSSP_NEGOTIATE_OEM_WORKSTATION_SUPPLIED = 8192L;
    private static final long NTLMSSP_NEGOTIATE_TARGET_INFO = 0x800000L;
    private static final long NTLMSSP_NEGOTIATE_ALWAYS_SIGN = 32768L;
    private static final long NTLMSSP_NEGOTIATE_EXTENDED_SESSIONSECURITY = 524288L;
    private static final short NTLM_AVID_MSVAVEOL = 0;
    private static final short NTLM_AVID_MSVAVNBCOMPUTERNAME = 1;
    private static final short NTLM_AVID_MSVAVNBDOMAINNAME = 2;
    private static final short NTLM_AVID_MSVAVDNSCOMPUTERNAME = 3;
    private static final short NTLM_AVID_MSVAVDNSDOMAINNAME = 4;
    private static final short NTLM_AVID_MSVAVDNSTREENAME = 5;
    private static final short NTLM_AVID_MSVAVFLAGS = 6;
    private static final short NTLM_AVID_MSVAVTIMESTAMP = 7;
    private static final short NTLM_AVID_MSVAVSINGLEHOST = 8;
    private static final short NTLM_AVID_MSVAVTARGETNAME = 9;
    private static final int NTLM_AVID_LENGTH = 2;
    private static final int NTLM_AVLEN_LENGTH = 2;
    private static final int NTLM_AVFLAG_VALUE_MIC = 2;
    private static final int NTLM_MIC_LENGTH = 16;
    private static final int NTLM_AVID_MSVAVFLAGS_LEN = 4;
    private static final int NTLM_NEGOTIATE_PAYLOAD_OFFSET = 32;
    private static final int NTLM_AUTHENTICATE_PAYLOAD_OFFSET = 88;
    private static final int NTLM_CLIENT_NONCE_LENGTH = 8;
    private static final int NTLM_SERVER_CHALLENGE_LENGTH = 8;
    private static final int NTLM_TIMESTAMP_LENGTH = 8;
    private static final long WINDOWS_EPOCH_DIFF = 11644473600L;
    private NTLMContext context = null;

    NTLMAuthentication(SQLServerConnection con, String domainName, String userName, byte[] passwordHash, String workstation) throws SQLServerException {
        if (null == this.context) {
            this.context = new NTLMContext(con, domainName, userName, passwordHash, workstation);
        }
    }

    @Override
    byte[] generateClientContext(byte[] inToken, boolean[] done) throws SQLServerException {
        return this.initializeSecurityContext(inToken, done);
    }

    @Override
    void releaseClientContext() {
        this.context = null;
    }

    private void parseNtlmChallenge(byte[] inToken) throws SQLServerException {
        ByteBuffer token = ByteBuffer.wrap(inToken).order(ByteOrder.LITTLE_ENDIAN);
        byte[] signature = new byte[NTLM_HEADER_SIGNATURE.length];
        token.get(signature);
        if (!Arrays.equals(signature, NTLM_HEADER_SIGNATURE)) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ntlmSignatureError"));
            Object[] msgArgs = new Object[]{signature};
            throw new SQLServerException(form.format(msgArgs), null);
        }
        int messageType = token.getInt();
        if (messageType != 2) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ntlmMessageTypeError"));
            Object[] msgArgs = new Object[]{messageType};
            throw new SQLServerException(form.format(msgArgs), null);
        }
        short targetNameLen = token.getShort();
        token.getShort();
        token.getInt();
        token.getInt();
        token.get(this.context.serverChallenge);
        token.getLong();
        short targetInfoLen = token.getShort();
        token.getShort();
        token.getInt();
        token.getLong();
        byte[] targetName = new byte[targetNameLen];
        token.get(targetName);
        this.context.targetInfo = new byte[targetInfoLen];
        token.get(this.context.targetInfo);
        if (0 == this.context.targetInfo.length) {
            throw new SQLServerException(SQLServerException.getErrString("R_ntlmNoTargetInfo"), null);
        }
        ByteBuffer targetInfoBuf = ByteBuffer.wrap(this.context.targetInfo).order(ByteOrder.LITTLE_ENDIAN);
        boolean done = false;
        int i = 0;
        while (i < this.context.targetInfo.length && !done) {
            short id = targetInfoBuf.getShort();
            byte[] value = new byte[targetInfoBuf.getShort()];
            targetInfoBuf.get(value);
            switch (id) {
                case 7: {
                    if (value.length <= 0) break;
                    this.context.timestamp = new byte[8];
                    System.arraycopy(value, 0, this.context.timestamp, 0, 8);
                    break;
                }
                case 0: {
                    done = true;
                    break;
                }
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 5: 
                case 6: 
                case 8: 
                case 9: {
                    break;
                }
                default: {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ntlmUnknownValue"));
                    Object[] msgArgs = new Object[]{value};
                    throw new SQLServerException(form.format(msgArgs), null);
                }
            }
            if (!this.logger.isLoggable(Level.FINEST)) continue;
            this.logger.finest(this.toString() + " NTLM Challenge Message target info: AvId " + id);
        }
        if (null == this.context.timestamp || 0 >= this.context.timestamp.length) {
            if (this.logger.isLoggable(Level.WARNING)) {
                this.logger.warning(this.toString() + " NTLM Challenge Message target info error: Missing timestamp.");
            }
        } else {
            this.context.challengeMsg = new byte[inToken.length];
            System.arraycopy(inToken, 0, this.context.challengeMsg, 0, inToken.length);
        }
    }

    private byte[] initializeSecurityContext(byte[] inToken, boolean[] done) throws SQLServerException {
        if (null == inToken || 0 == inToken.length) {
            return this.generateNtlmNegotiate();
        }
        this.parseNtlmChallenge(inToken);
        done[0] = true;
        return this.generateNtlmAuthenticate();
    }

    private byte[] generateClientChallengeBlob(byte[] clientNonce) {
        ByteBuffer time = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        time.putLong(TimeUnit.SECONDS.toNanos(Instant.now().getEpochSecond() + 11644473600L) / 100L);
        byte[] currentTime = time.array();
        ByteBuffer token = ByteBuffer.allocate(NTLM_CLIENT_CHALLENGE_RESPONSE_TYPE.length + NTLM_CLIENT_CHALLENGE_RESERVED1.length + NTLM_CLIENT_CHALLENGE_RESERVED2.length + currentTime.length + 8 + NTLM_CLIENT_CHALLENGE_RESERVED3.length + this.context.targetInfo.length + 2 + 2 + 4 + 2 + 2 + this.context.spnUbytes.length).order(ByteOrder.LITTLE_ENDIAN);
        token.put(NTLM_CLIENT_CHALLENGE_RESPONSE_TYPE);
        token.put(NTLM_CLIENT_CHALLENGE_RESERVED1);
        token.put(NTLM_CLIENT_CHALLENGE_RESERVED2);
        token.put(currentTime, 0, 8);
        token.put(clientNonce, 0, 8);
        token.put(NTLM_CLIENT_CHALLENGE_RESERVED3);
        if (null == this.context.timestamp || 0 >= this.context.timestamp.length) {
            token.put(this.context.targetInfo, 0, this.context.targetInfo.length);
            if (this.logger.isLoggable(Level.WARNING)) {
                this.logger.warning(this.toString() + " MsvAvTimestamp not recieved from SQL Server in Challenge Message. MIC field will not be set.");
            }
        } else {
            token.put(this.context.targetInfo, 0, this.context.targetInfo.length - 2 - 2);
            token.putShort((short)6);
            token.putShort((short)4);
            token.putInt(2);
        }
        token.putShort((short)9);
        token.putShort((short)this.context.spnUbytes.length);
        token.put(this.context.spnUbytes, 0, this.context.spnUbytes.length);
        token.putShort((short)0);
        token.putShort((short)0);
        return token.array();
    }

    private byte[] hmacMD5(byte[] key, byte[] data) throws InvalidKeyException {
        SecretKeySpec keySpec = new SecretKeySpec(key, "HmacMD5");
        this.context.mac.init(keySpec);
        return this.context.mac.doFinal(data);
    }

    private static byte[] md4(byte[] str) {
        MD4 md = new MD4();
        md.reset();
        md.update(str, 0, str.length);
        byte[] hash = new byte[md.getDigestSize()];
        md.doFinal(hash, 0);
        return hash;
    }

    private static byte[] unicode(String str) {
        return null != str ? str.getBytes(StandardCharsets.UTF_16LE) : null;
    }

    private byte[] concat(byte[] arr1, byte[] arr2) {
        if (null == arr1 || null == arr2) {
            return null;
        }
        byte[] temp = new byte[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, temp, 0, arr1.length);
        System.arraycopy(arr2, 0, temp, arr1.length, arr2.length);
        return temp;
    }

    private int getByteArrayLength(byte[] arr) {
        return null == arr ? 0 : arr.length;
    }

    private byte[] ntowfv2() throws InvalidKeyException {
        return this.hmacMD5(this.context.passwordHash, null != this.context.upperUserName ? NTLMAuthentication.unicode(this.context.upperUserName + this.context.domainName) : NTLMAuthentication.unicode(this.context.domainName));
    }

    private byte[] computeResponse(byte[] responseKeyNT) throws InvalidKeyException {
        byte[] clientNonce = new byte[8];
        ThreadLocalRandom.current().nextBytes(clientNonce);
        byte[] temp = this.generateClientChallengeBlob(clientNonce);
        byte[] ntProofStr = this.hmacMD5(responseKeyNT, this.concat(this.context.serverChallenge, temp));
        this.context.sessionBaseKey = this.hmacMD5(responseKeyNT, ntProofStr);
        return this.concat(ntProofStr, temp);
    }

    private byte[] getNtChallengeResp() throws InvalidKeyException {
        byte[] responseKeyNT = this.ntowfv2();
        return this.computeResponse(responseKeyNT);
    }

    private byte[] generateNtlmAuthenticate() throws SQLServerException {
        int domainNameLen = this.getByteArrayLength(this.context.domainUbytes);
        int userNameLen = this.getByteArrayLength(this.context.userNameUbytes);
        byte[] workstationBytes = NTLMAuthentication.unicode(this.context.workstation);
        int workstationLen = this.getByteArrayLength(workstationBytes);
        byte[] msg = null;
        try {
            byte[] ntChallengeResp = this.getNtChallengeResp();
            int ntChallengeLen = this.getByteArrayLength(ntChallengeResp);
            ByteBuffer token = ByteBuffer.allocate(88 + NTLM_LMCHALLENAGERESPONSE.length + ntChallengeLen + domainNameLen + userNameLen + workstationLen).order(ByteOrder.LITTLE_ENDIAN);
            token.put(NTLM_HEADER_SIGNATURE, 0, NTLM_HEADER_SIGNATURE.length);
            token.putInt(3);
            int offset = 88;
            token.putShort((short)0);
            token.putShort((short)0);
            token.putInt(offset);
            token.putShort((short)ntChallengeLen);
            token.putShort((short)ntChallengeLen);
            token.putInt(offset += NTLM_LMCHALLENAGERESPONSE.length);
            token.putShort((short)domainNameLen);
            token.putShort((short)domainNameLen);
            token.putInt(offset += ntChallengeLen);
            token.putShort((short)userNameLen);
            token.putShort((short)userNameLen);
            token.putInt(offset += domainNameLen);
            token.putShort((short)workstationLen);
            token.putShort((short)workstationLen);
            token.putInt(offset += userNameLen);
            token.putShort((short)0);
            token.putShort((short)0);
            token.putInt(offset += workstationLen);
            token.putInt((int)this.context.negotiateFlags);
            token.put(NTLMSSP_VERSION, 0, NTLMSSP_VERSION.length);
            byte[] mic = new byte[16];
            int micPosition = token.position();
            token.put(mic, 0, 16);
            token.put(NTLM_LMCHALLENAGERESPONSE, 0, NTLM_LMCHALLENAGERESPONSE.length);
            token.put(ntChallengeResp, 0, ntChallengeLen);
            token.put(this.context.domainUbytes, 0, domainNameLen);
            token.put(this.context.userNameUbytes, 0, userNameLen);
            token.put(workstationBytes, 0, workstationLen);
            msg = token.array();
            if (null != this.context.timestamp && 0 < this.context.timestamp.length) {
                SecretKeySpec keySpec = new SecretKeySpec(this.context.sessionBaseKey, "HmacMD5");
                this.context.mac.init(keySpec);
                this.context.mac.update(this.context.negotiateMsg);
                this.context.mac.update(this.context.challengeMsg);
                mic = this.context.mac.doFinal(msg);
                System.arraycopy(mic, 0, msg, micPosition, 16);
            }
        }
        catch (InvalidKeyException e) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ntlmAuthenticateError"));
            Object[] msgArgs = new Object[]{e.getMessage()};
            throw new SQLServerException(form.format(msgArgs), e);
        }
        return msg;
    }

    private byte[] generateNtlmNegotiate() {
        int domainNameLen = this.getByteArrayLength(this.context.domainUbytes);
        int workstationLen = this.getByteArrayLength(this.context.workstation.getBytes());
        ByteBuffer token = null;
        token = ByteBuffer.allocate(32 + domainNameLen + workstationLen).order(ByteOrder.LITTLE_ENDIAN);
        token.put(NTLM_HEADER_SIGNATURE, 0, NTLM_HEADER_SIGNATURE.length);
        token.putInt(1);
        this.context.negotiateFlags = 8957957L;
        token.putInt((int)this.context.negotiateFlags);
        int offset = 32;
        token.putShort((short)domainNameLen);
        token.putShort((short)domainNameLen);
        token.putInt(offset);
        token.putShort((short)workstationLen);
        token.putShort((short)workstationLen);
        token.putInt(offset += domainNameLen);
        token.put(this.context.domainUbytes, 0, domainNameLen);
        token.put(this.context.workstation.getBytes(), 0, workstationLen);
        byte[] msg = token.array();
        this.context.negotiateMsg = new byte[msg.length];
        System.arraycopy(msg, 0, this.context.negotiateMsg, 0, msg.length);
        return msg;
    }

    public static byte[] getNtlmPasswordHash(String password) throws SQLServerException {
        if (null == password) {
            throw new SQLServerException(SQLServerException.getErrString("R_NtlmNoUserPasswordDomain"), null);
        }
        return NTLMAuthentication.md4(NTLMAuthentication.unicode(password));
    }

    private class NTLMContext {
        private final String domainName;
        private final byte[] domainUbytes;
        private final String upperUserName;
        private final byte[] userNameUbytes;
        private final byte[] passwordHash;
        private String workstation;
        private final byte[] spnUbytes;
        private Mac mac = null;
        private long negotiateFlags = 0L;
        private byte[] sessionBaseKey = null;
        private byte[] timestamp = null;
        private byte[] targetInfo = null;
        private byte[] serverChallenge = new byte[8];
        private byte[] negotiateMsg = null;
        private byte[] challengeMsg = null;

        NTLMContext(SQLServerConnection con, String domainName, String userName, byte[] passwordHash, String workstation) throws SQLServerException {
            this.domainName = domainName.toUpperCase();
            this.domainUbytes = NTLMAuthentication.unicode(this.domainName);
            this.userNameUbytes = null != userName ? NTLMAuthentication.unicode(userName) : null;
            this.upperUserName = null != userName ? userName.toUpperCase() : null;
            this.passwordHash = passwordHash;
            this.workstation = workstation;
            String spn = null != con ? NTLMAuthentication.this.getSpn(con) : null;
            byte[] byArray = this.spnUbytes = null != spn ? NTLMAuthentication.unicode(spn) : null;
            if (NTLMAuthentication.this.logger.isLoggable(Level.FINEST)) {
                NTLMAuthentication.this.logger.finest(this.toString() + " SPN detected: " + spn);
            }
            try {
                this.mac = Mac.getInstance("HmacMD5");
            }
            catch (NoSuchAlgorithmException e) {
                MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ntlmHmacMD5Error"));
                Object[] msgArgs = new Object[]{domainName, e.getMessage()};
                throw new SQLServerException(form.format(msgArgs), e);
            }
        }
    }
}

