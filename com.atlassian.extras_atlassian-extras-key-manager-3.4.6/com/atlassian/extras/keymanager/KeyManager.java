/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.common.log.Logger
 *  com.atlassian.extras.common.log.Logger$Log
 *  org.apache.commons.codec.binary.Base64
 */
package com.atlassian.extras.keymanager;

import com.atlassian.extras.common.log.Logger;
import com.atlassian.extras.keymanager.Key;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.codec.binary.Base64;

public class KeyManager {
    private static final Logger.Log log = Logger.getInstance(KeyManager.class);
    private static final String KEY_ALGORITHM = "DSA";
    private static final String SIGNATURE_ALGORITHM = "SHA1withDSA";
    public static final String ENV_VAR_BASE = "ATLAS_LICENSE_";
    public static final String ENV_VAR_PRIVATE_KEY_BASE = "ATLAS_LICENSE_PRIVATE_KEY_";
    public static final String ENV_VAR_PUBLIC_KEY_BASE = "ATLAS_LICENSE_PUBLIC_KEY_";
    private final Map<String, PrivateKey> privateKeys = new ConcurrentHashMap<String, PrivateKey>();
    private final Map<String, PublicKey> publicKeys = new ConcurrentHashMap<String, PublicKey>();
    private Map<String, String> env = System.getenv();
    private static KeyManager INSTANCE = new KeyManager();

    public static KeyManager getInstance() {
        return INSTANCE;
    }

    protected KeyManager() {
        this.reset();
    }

    public void loadKey(Key key) {
        try {
            if (key.getType() == Key.Type.PRIVATE) {
                this.privateKeys.put(key.getVersion(), KeyManager.generatePrivateKey(key.getKey()));
            } else if (key.getType() == Key.Type.PUBLIC) {
                this.publicKeys.put(key.getVersion(), KeyManager.generatePublicKey(key.getKey()));
            } else {
                log.warn((Object)("Ignoring key version " + key.getVersion() + " with unknown type"));
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to load key", e);
        }
    }

    public PrivateKey getPrivateKey(String version) {
        return this.privateKeys.get(version);
    }

    public PublicKey getPublicKey(String version) {
        return this.publicKeys.get(version);
    }

    public Collection<PrivateKey> getPrivateKeys() {
        return this.privateKeys.values();
    }

    public Collection<PublicKey> getPublicKeys() {
        return this.publicKeys.values();
    }

    public void reset() {
        this.privateKeys.clear();
        this.publicKeys.clear();
        ArrayList<Key> keys = new ArrayList<Key>();
        for (Map.Entry<String, String> envVar : this.env.entrySet()) {
            String envVarKey = envVar.getKey();
            if (envVarKey.startsWith(ENV_VAR_PRIVATE_KEY_BASE)) {
                keys.add(new Key(envVar.getValue(), KeyManager.extractVersion(envVarKey), Key.Type.PRIVATE));
            }
            if (!envVarKey.startsWith(ENV_VAR_PUBLIC_KEY_BASE)) continue;
            keys.add(new Key(envVar.getValue(), KeyManager.extractVersion(envVarKey), Key.Type.PUBLIC));
        }
        for (Key key : keys) {
            this.loadKey(key);
        }
        this.loadKey(new Key("MIIBuDCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYUAAoGBAIvfweZvmGo5otwawI3no7Udanxal3hX2haw962KL/nHQrnC4FG2PvUFf34OecSK1KtHDPQoSQ+DHrfdf6vKUJphw0Kn3gXm4LS8VK/LrY7on/wh2iUobS2XlhuIqEc5mLAUu9Hd+1qxsQkQ50d0lzKrnDqPsM0WA9htkdJJw2nS", "LICENSE_STRING_KEY_V2", Key.Type.PUBLIC));
        this.loadKey(new Key("MIIBtzCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYQAAoGALZHuJwQzgGnYm/X9BkMcewYQnWjMIGWHd9Yom5Qw7cVIdiZkqpiSzSKurO/WAHHLN31obg7NgGkitWUysECRE3zuJVbKGhx9xjVMnP6z5SwI89vB7Gn7UWxoCvT0JZgcMyQobXeVBtM9J3EgzkdDx/+Dck7uz/l1y+HDNdRzW00=", "1600708331", Key.Type.PUBLIC));
    }

    public String sign(String payload, String keyVersion) {
        if (payload == null) {
            throw new IllegalArgumentException("Payload cannot be null");
        }
        PrivateKey key = this.getPrivateKey(keyVersion);
        if (key == null) {
            throw new IllegalStateException("Private key version " + keyVersion + " not found");
        }
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(key);
            signature.update(Base64.decodeBase64((String)payload));
            return new String(Base64.encodeBase64((byte[])signature.sign()), StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to sign", e);
        }
    }

    public boolean verify(String payload, String hash, String keyVersion) {
        PublicKey key = this.getPublicKey(keyVersion);
        if (key == null) {
            throw new IllegalStateException("Public key version " + keyVersion + " not found");
        }
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(key);
            signature.update(Base64.decodeBase64((String)payload));
            return signature.verify(Base64.decodeBase64((String)hash));
        }
        catch (Exception e) {
            throw new RuntimeException("Signature verification failed", e);
        }
    }

    private static String extractVersion(String envVarKey) {
        return envVarKey.substring(envVarKey.lastIndexOf("_") + 1);
    }

    private static PublicKey generatePublicKey(String encodedPublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePublic(new X509EncodedKeySpec(Base64.decodeBase64((byte[])encodedPublicKey.getBytes(StandardCharsets.UTF_8))));
    }

    private static PrivateKey generatePrivateKey(String encodedPrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decodeBase64((byte[])encodedPrivateKey.getBytes(StandardCharsets.UTF_8))));
    }
}

