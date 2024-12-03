/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.group.interceptors;

import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelInterceptor;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.group.ChannelInterceptorBase;
import org.apache.catalina.tribes.group.InterceptorPayload;
import org.apache.catalina.tribes.group.interceptors.EncryptInterceptorMBean;
import org.apache.catalina.tribes.group.interceptors.TcpFailureDetector;
import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class EncryptInterceptor
extends ChannelInterceptorBase
implements EncryptInterceptorMBean {
    private static final Log log = LogFactory.getLog(EncryptInterceptor.class);
    protected static final StringManager sm = StringManager.getManager(EncryptInterceptor.class);
    private static final String DEFAULT_ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding";
    private String providerName;
    private String encryptionAlgorithm = "AES/CBC/PKCS5Padding";
    private byte[] encryptionKeyBytes;
    private String encryptionKeyString;
    private BaseEncryptionManager encryptionManager;
    private static final int[] DEC = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15};

    @Override
    public void start(int svc) throws ChannelException {
        this.validateChannelChain();
        if (2 == (svc & 2)) {
            try {
                this.encryptionManager = EncryptInterceptor.createEncryptionManager(this.getEncryptionAlgorithm(), this.getEncryptionKeyInternal(), this.getProviderName());
            }
            catch (GeneralSecurityException gse) {
                throw new ChannelException(sm.getString("encryptInterceptor.init.failed"), gse);
            }
        }
        super.start(svc);
    }

    private void validateChannelChain() throws ChannelException {
        for (ChannelInterceptor interceptor = this.getPrevious(); null != interceptor; interceptor = interceptor.getPrevious()) {
            if (!(interceptor instanceof TcpFailureDetector)) continue;
            throw new ChannelConfigException(sm.getString("encryptInterceptor.tcpFailureDetector.ordering"));
        }
    }

    @Override
    public void stop(int svc) throws ChannelException {
        if (2 == (svc & 2)) {
            this.encryptionManager.shutdown();
        }
        super.stop(svc);
    }

    @Override
    public void sendMessage(Member[] destination, ChannelMessage msg, InterceptorPayload payload) throws ChannelException {
        try {
            byte[] data = msg.getMessage().getBytes();
            byte[][] bytes = this.encryptionManager.encrypt(data);
            XByteBuffer xbb = msg.getMessage();
            xbb.clear();
            xbb.append(bytes[0], 0, bytes[0].length);
            xbb.append(bytes[1], 0, bytes[1].length);
            super.sendMessage(destination, msg, payload);
        }
        catch (GeneralSecurityException gse) {
            log.error((Object)sm.getString("encryptInterceptor.encrypt.failed"));
            throw new ChannelException(gse);
        }
    }

    @Override
    public void messageReceived(ChannelMessage msg) {
        try {
            byte[] data = msg.getMessage().getBytes();
            data = this.encryptionManager.decrypt(data);
            XByteBuffer xbb = msg.getMessage();
            xbb.clear();
            xbb.append(data, 0, data.length);
            super.messageReceived(msg);
        }
        catch (GeneralSecurityException gse) {
            log.error((Object)sm.getString("encryptInterceptor.decrypt.failed"), (Throwable)gse);
        }
    }

    @Override
    public void setEncryptionAlgorithm(String algorithm) {
        if (null == this.getEncryptionAlgorithm()) {
            throw new IllegalStateException(sm.getString("encryptInterceptor.algorithm.required"));
        }
        int pos = algorithm.indexOf(47);
        if (pos < 0) {
            throw new IllegalArgumentException(sm.getString("encryptInterceptor.algorithm.required"));
        }
        if ((pos = algorithm.indexOf(47, pos + 1)) < 0) {
            throw new IllegalArgumentException(sm.getString("encryptInterceptor.algorithm.required"));
        }
        this.encryptionAlgorithm = algorithm;
    }

    @Override
    public String getEncryptionAlgorithm() {
        return this.encryptionAlgorithm;
    }

    @Override
    public void setEncryptionKey(byte[] key) {
        this.encryptionKeyBytes = (byte[])(null == key ? null : (byte[])key.clone());
    }

    public void setEncryptionKey(String keyBytes) {
        this.encryptionKeyString = keyBytes;
        if (null == keyBytes) {
            this.setEncryptionKey((byte[])null);
        } else {
            this.setEncryptionKey(EncryptInterceptor.fromHexString(keyBytes.trim()));
        }
    }

    @Override
    public byte[] getEncryptionKey() {
        byte[] key = this.getEncryptionKeyInternal();
        if (null != key) {
            key = (byte[])key.clone();
        }
        return key;
    }

    private byte[] getEncryptionKeyInternal() {
        return this.encryptionKeyBytes;
    }

    public String getEncryptionKeyString() {
        return this.encryptionKeyString;
    }

    public void setEncryptionKeyString(String encryptionKeyString) {
        this.setEncryptionKey(encryptionKeyString);
    }

    @Override
    public void setProviderName(String provider) {
        this.providerName = provider;
    }

    @Override
    public String getProviderName() {
        return this.providerName;
    }

    private static int getDec(int index) {
        try {
            return DEC[index - 48];
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            return -1;
        }
    }

    private static byte[] fromHexString(String input) {
        if (input == null) {
            return null;
        }
        if ((input.length() & 1) == 1) {
            throw new IllegalArgumentException(sm.getString("hexUtils.fromHex.oddDigits"));
        }
        char[] inputChars = input.toCharArray();
        byte[] result = new byte[input.length() >> 1];
        for (int i = 0; i < result.length; ++i) {
            int upperNibble = EncryptInterceptor.getDec(inputChars[2 * i]);
            int lowerNibble = EncryptInterceptor.getDec(inputChars[2 * i + 1]);
            if (upperNibble < 0 || lowerNibble < 0) {
                throw new IllegalArgumentException(sm.getString("hexUtils.fromHex.nonHex"));
            }
            result[i] = (byte)((upperNibble << 4) + lowerNibble);
        }
        return result;
    }

    private static BaseEncryptionManager createEncryptionManager(String algorithm, byte[] encryptionKey, String providerName) throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
        String algorithmMode;
        String algorithmName;
        if (null == encryptionKey) {
            throw new IllegalStateException(sm.getString("encryptInterceptor.key.required"));
        }
        int pos = algorithm.indexOf(47);
        if (pos >= 0) {
            algorithmName = algorithm.substring(0, pos);
            int pos2 = algorithm.indexOf(47, pos + 1);
            algorithmMode = pos2 >= 0 ? algorithm.substring(pos + 1, pos2) : "CBC";
        } else {
            algorithmName = algorithm;
            algorithmMode = "CBC";
        }
        if ("GCM".equalsIgnoreCase(algorithmMode)) {
            return new GCMEncryptionManager(algorithm, new SecretKeySpec(encryptionKey, algorithmName), providerName);
        }
        if ("CBC".equalsIgnoreCase(algorithmMode) || "OFB".equalsIgnoreCase(algorithmMode) || "CFB".equalsIgnoreCase(algorithmMode)) {
            return new BaseEncryptionManager(algorithm, new SecretKeySpec(encryptionKey, algorithmName), providerName);
        }
        throw new IllegalArgumentException(sm.getString("encryptInterceptor.algorithm.unsupported-mode", algorithmMode));
    }

    private static class BaseEncryptionManager {
        private final String algorithm;
        private final int blockSize;
        private final String providerName;
        private final SecretKeySpec secretKey;
        private final ConcurrentLinkedQueue<Cipher> cipherPool;
        private final ConcurrentLinkedQueue<SecureRandom> randomPool;

        BaseEncryptionManager(String algorithm, SecretKeySpec secretKey, String providerName) throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
            this.algorithm = algorithm;
            this.providerName = providerName;
            this.secretKey = secretKey;
            this.cipherPool = new ConcurrentLinkedQueue();
            Cipher cipher = this.createCipher();
            this.blockSize = cipher.getBlockSize();
            this.cipherPool.offer(cipher);
            this.randomPool = new ConcurrentLinkedQueue();
        }

        public void shutdown() {
            this.cipherPool.clear();
            this.randomPool.clear();
        }

        private String getAlgorithm() {
            return this.algorithm;
        }

        private SecretKeySpec getSecretKey() {
            return this.secretKey;
        }

        protected int getIVSize() {
            return this.blockSize;
        }

        private String getProviderName() {
            return this.providerName;
        }

        private Cipher createCipher() throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
            String providerName = this.getProviderName();
            if (null == providerName) {
                return Cipher.getInstance(this.getAlgorithm());
            }
            return Cipher.getInstance(this.getAlgorithm(), providerName);
        }

        private Cipher getCipher() throws GeneralSecurityException {
            Cipher cipher = this.cipherPool.poll();
            if (null == cipher) {
                cipher = this.createCipher();
            }
            return cipher;
        }

        private void returnCipher(Cipher cipher) {
            this.cipherPool.offer(cipher);
        }

        private SecureRandom getRandom() {
            SecureRandom random = this.randomPool.poll();
            if (null == random) {
                random = new SecureRandom();
            }
            return random;
        }

        private void returnRandom(SecureRandom random) {
            this.randomPool.offer(random);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private byte[][] encrypt(byte[] bytes) throws GeneralSecurityException {
            Cipher cipher = null;
            byte[] iv = this.generateIVBytes();
            try {
                cipher = this.getCipher();
                cipher.init(1, (Key)this.getSecretKey(), this.generateIV(iv, 0, this.getIVSize()));
                byte[][] data = new byte[][]{iv, cipher.doFinal(bytes)};
                byte[][] byArrayArray = data;
                return byArrayArray;
            }
            finally {
                if (null != cipher) {
                    this.returnCipher(cipher);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private byte[] decrypt(byte[] bytes) throws GeneralSecurityException {
            Cipher cipher = null;
            int ivSize = this.getIVSize();
            AlgorithmParameterSpec IV = this.generateIV(bytes, 0, ivSize);
            try {
                cipher = this.getCipher();
                cipher.init(2, (Key)this.getSecretKey(), IV);
                byte[] byArray = cipher.doFinal(bytes, ivSize, bytes.length - ivSize);
                return byArray;
            }
            finally {
                if (null != cipher) {
                    this.returnCipher(cipher);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected byte[] generateIVBytes() {
            byte[] ivBytes = new byte[this.getIVSize()];
            SecureRandom random = null;
            try {
                random = this.getRandom();
                random.nextBytes(ivBytes);
                byte[] byArray = ivBytes;
                return byArray;
            }
            finally {
                if (null != random) {
                    this.returnRandom(random);
                }
            }
        }

        protected AlgorithmParameterSpec generateIV(byte[] ivBytes, int offset, int length) {
            return new IvParameterSpec(ivBytes, offset, length);
        }
    }

    static class ChannelConfigException
    extends ChannelException {
        private static final long serialVersionUID = 1L;

        ChannelConfigException(String message) {
            super(message);
        }
    }

    private static class GCMEncryptionManager
    extends BaseEncryptionManager {
        GCMEncryptionManager(String algorithm, SecretKeySpec secretKey, String providerName) throws NoSuchAlgorithmException, NoSuchPaddingException, NoSuchProviderException {
            super(algorithm, secretKey, providerName);
        }

        @Override
        protected int getIVSize() {
            return 12;
        }

        @Override
        protected AlgorithmParameterSpec generateIV(byte[] bytes, int offset, int length) {
            return new GCMParameterSpec(128, bytes, offset, length);
        }
    }
}

