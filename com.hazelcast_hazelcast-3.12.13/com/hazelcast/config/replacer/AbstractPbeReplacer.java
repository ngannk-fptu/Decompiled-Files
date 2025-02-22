/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.replacer;

import com.hazelcast.config.replacer.spi.ConfigReplacer;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.util.Base64;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.StringUtil;
import java.security.SecureRandom;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public abstract class AbstractPbeReplacer
implements ConfigReplacer {
    public static final String PROPERTY_CIPHER_ALGORITHM = "cipherAlgorithm";
    public static final String PROPERTY_SECRET_KEY_FACTORY_ALGORITHM = "secretKeyFactoryAlgorithm";
    public static final String PROPERTY_SECRET_KEY_ALGORITHM = "secretKeyAlgorithm";
    public static final String PROPERTY_KEY_LENGTH_BITS = "keyLengthBits";
    public static final String PROPERTY_SALT_LENGTH_BYTES = "saltLengthBytes";
    public static final String PROPERTY_SECURITY_PROVIDER = "securityProvider";
    public static final String DEFAULT_CIPHER_ALGORITHM = "AES";
    public static final String DEFAULT_SECRET_KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private final ILogger logger = Logger.getLogger(AbstractPbeReplacer.class);
    private String cipherAlgorithm;
    private String secretKeyFactoryAlgorithm;
    private String secretKeyAlgorithm;
    private String securityProvider;
    private int keyLengthBits;
    private int saltLengthBytes;

    @Override
    public void init(Properties properties) {
        this.securityProvider = properties.getProperty(PROPERTY_SECURITY_PROVIDER);
        this.cipherAlgorithm = properties.getProperty(PROPERTY_CIPHER_ALGORITHM, DEFAULT_CIPHER_ALGORITHM);
        this.secretKeyFactoryAlgorithm = properties.getProperty(PROPERTY_SECRET_KEY_FACTORY_ALGORITHM, DEFAULT_SECRET_KEY_FACTORY_ALGORITHM);
        this.secretKeyAlgorithm = properties.getProperty(PROPERTY_SECRET_KEY_ALGORITHM, DEFAULT_CIPHER_ALGORITHM);
        this.keyLengthBits = Integer.parseInt(properties.getProperty(PROPERTY_KEY_LENGTH_BITS, "128"));
        this.saltLengthBytes = Integer.parseInt(properties.getProperty(PROPERTY_SALT_LENGTH_BYTES, "8"));
        Preconditions.checkPositive(this.keyLengthBits, "Key length has to be positive number");
        Preconditions.checkPositive(this.saltLengthBytes, "Salt length has to be positive number");
    }

    protected abstract char[] getPassword();

    @Override
    public String getReplacement(String variable) {
        try {
            return this.decrypt(variable);
        }
        catch (Exception e) {
            this.logger.warning("Unable to decrypt variable " + variable, e);
            return null;
        }
    }

    protected String encrypt(String secretStr, int iterations) throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[this.saltLengthBytes];
        secureRandom.nextBytes(salt);
        byte[] encryptedVal = this.transform(1, secretStr.getBytes(StringUtil.UTF8_CHARSET), salt, iterations);
        return new String(Base64.encode(salt), StringUtil.UTF8_CHARSET) + ":" + iterations + ":" + new String(Base64.encode(encryptedVal), StringUtil.UTF8_CHARSET);
    }

    protected String decrypt(String encryptedStr) throws Exception {
        String[] split = encryptedStr.split(":");
        Preconditions.checkTrue(split.length == 3, "Wrong format of the encrypted variable (" + encryptedStr + ")");
        byte[] salt = Base64.decode(split[0].getBytes(StringUtil.UTF8_CHARSET));
        Preconditions.checkTrue(salt.length == this.saltLengthBytes, "Salt length doesn't match.");
        int iterations = Integer.parseInt(split[1]);
        byte[] encryptedVal = Base64.decode(split[2].getBytes(StringUtil.UTF8_CHARSET));
        return new String(this.transform(2, encryptedVal, salt, iterations), StringUtil.UTF8_CHARSET);
    }

    private byte[] transform(int cryptMode, byte[] value, byte[] salt, int iterations) throws Exception {
        Preconditions.checkPositive(iterations, "Count of iterations has to be positive number.");
        SecretKeyFactory factory = SecretKeyFactory.getInstance(this.secretKeyFactoryAlgorithm);
        char[] password = this.getPassword();
        Preconditions.checkTrue(password != null && password.length > 0, "Empty password is not supported");
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password, salt, iterations, this.keyLengthBits);
        byte[] tmpKey = factory.generateSecret(pbeKeySpec).getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(tmpKey, this.secretKeyAlgorithm);
        Cipher cipher = this.securityProvider == null ? Cipher.getInstance(this.cipherAlgorithm) : Cipher.getInstance(this.cipherAlgorithm, this.securityProvider);
        cipher.init(cryptMode, secretKeySpec);
        return cipher.doFinal(value);
    }
}

