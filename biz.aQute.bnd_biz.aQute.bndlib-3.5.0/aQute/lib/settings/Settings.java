/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.settings;

import aQute.lib.io.IO;
import aQute.lib.json.JSONCodec;
import aQute.lib.settings.PasswordCryptor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Settings
implements Map<String, String> {
    static JSONCodec codec = new JSONCodec();
    private final File where;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private boolean loaded;
    private boolean dirty;
    Data data = new Data();
    private char[] password;

    public Settings() {
        this("~/.bnd/settings.json");
    }

    public Settings(String where) {
        assert (where != null);
        this.where = IO.getFile(IO.work, where);
    }

    public boolean load() {
        return this.load(this.password);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean load(char[] password) {
        this.password = password;
        if (this.where.isFile() && this.where.length() > 1L) {
            boolean bl;
            InputStream in = IO.stream(this.where);
            try {
                if (password != null) {
                    PasswordCryptor cryptor = new PasswordCryptor();
                    in = cryptor.decrypt(password, in);
                } else {
                    String secret = System.getenv().get("BND_SETTINGS_PASSWORD");
                    if (secret != null && secret.length() > 0) {
                        PasswordCryptor cryptor = new PasswordCryptor();
                        in = cryptor.decrypt(secret.toCharArray(), in);
                    }
                }
                this.data = codec.dec().from(in).get(Data.class);
                this.loaded = true;
                bl = true;
            }
            catch (Throwable throwable) {
                try {
                    in.close();
                    throw throwable;
                }
                catch (Exception e) {
                    throw new RuntimeException("Cannot read settings file " + this.where, e);
                }
            }
            in.close();
            return bl;
        }
        if (!this.data.map.containsKey("name")) {
            this.data.map.put("name", System.getProperty("user.name"));
        }
        return false;
    }

    private void check() {
        if (this.loaded) {
            return;
        }
        this.load();
        this.loaded = true;
    }

    public void save() {
        this.save(this.password);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void save(char[] password) {
        try {
            IO.mkdirs(this.where.getParentFile());
        }
        catch (IOException e) {
            throw new RuntimeException("Cannot create directory in " + this.where.getParent(), e);
        }
        try {
            try (OutputStream out = IO.outputStream(this.where);){
                if (password != null) {
                    PasswordCryptor cryptor = new PasswordCryptor();
                    out = cryptor.encrypt(password, out);
                } else {
                    String secret = System.getenv().get("BND-SETTINGS-PASSWORD");
                    if (secret != null) {
                        PasswordCryptor cryptor = new PasswordCryptor();
                        out = cryptor.encrypt(secret.toCharArray(), out);
                    }
                }
                codec.enc().to(out).put(this.data).flush();
            }
            assert (this.where.isFile());
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot write settings file " + this.where, e);
        }
    }

    public void generate() throws Exception {
        this.generate(this.password);
    }

    public void generate(char[] password) throws Exception {
        this.check();
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        keyGen.initialize(1024, random);
        KeyPair pair = keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
        this.data.secret = this.privateKey.getEncoded();
        this.data.id = this.publicKey.getEncoded();
        this.save(password);
    }

    public String getEmail() {
        return this.get("email");
    }

    public void setEmail(String email) {
        this.put("email", email);
    }

    public void setKeyPair(byte[] id, byte[] secret) throws Exception {
        this.data.secret = secret;
        this.data.id = id;
        this.privateKey = null;
        this.publicKey = null;
        this.initKeys();
        this.save();
    }

    public void setName(String v) {
        this.put("name", v);
    }

    public String getName() {
        String name = this.get("name");
        if (name != null) {
            return name;
        }
        return System.getProperty("user.name");
    }

    public byte[] getPublicKey() throws Exception {
        this.initKeys();
        return this.data.id;
    }

    public byte[] getPrivateKey() throws Exception {
        this.initKeys();
        return this.data.secret;
    }

    private void initKeys() throws Exception {
        this.check();
        if (this.privateKey != null) {
            return;
        }
        if (this.data.id == null || this.data.secret == null) {
            this.generate();
        } else {
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(this.data.secret);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.privateKey = keyFactory.generatePrivate(privateKeySpec);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(this.data.id);
            this.publicKey = keyFactory.generatePublic(publicKeySpec);
        }
    }

    public byte[] sign(byte[] con) throws Exception {
        this.initKeys();
        Signature hmac = Signature.getInstance("SHA1withRSA");
        hmac.initSign(this.privateKey);
        hmac.update(con);
        return hmac.sign();
    }

    public boolean verify(byte[] con) throws Exception {
        this.initKeys();
        Signature hmac = Signature.getInstance("SHA1withRSA");
        hmac.initVerify(this.publicKey);
        hmac.update(con);
        return hmac.verify(con);
    }

    @Override
    public void clear() {
        this.data = new Data();
        IO.delete(this.where);
    }

    @Override
    public boolean containsKey(Object key) {
        this.check();
        return this.data.map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        this.check();
        return this.data.map.containsValue(value);
    }

    @Override
    public Set<Map.Entry<String, String>> entrySet() {
        this.check();
        return this.data.map.entrySet();
    }

    @Override
    public String get(Object key) {
        this.check();
        return this.data.map.get(key);
    }

    @Override
    public boolean isEmpty() {
        this.check();
        return this.data.map.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        this.check();
        return this.data.map.keySet();
    }

    @Override
    public String put(String key, String value) {
        this.check();
        this.dirty = true;
        return this.data.map.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> v) {
        this.check();
        this.dirty = true;
        this.data.map.putAll(v);
    }

    @Override
    public String remove(Object key) {
        this.check();
        this.dirty = true;
        return this.data.map.remove(key);
    }

    @Override
    public int size() {
        this.check();
        return this.data.map.size();
    }

    @Override
    public Collection<String> values() {
        this.check();
        return this.data.map.values();
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public String toString() {
        return "Settings[" + this.where + "]";
    }

    public static class Data {
        public int version = 1;
        public byte[] secret;
        public byte[] id;
        public Map<String, String> map = new HashMap<String, String>();
    }
}

