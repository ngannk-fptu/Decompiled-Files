/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONArray
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.jose.jwk;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.PasswordLookup;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.IOUtils;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jose.util.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

@Immutable
public class JWKSet
implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String MIME_TYPE = "application/jwk-set+json; charset=UTF-8";
    private final List<JWK> keys;
    private final Map<String, Object> customMembers;

    public JWKSet() {
        this(Collections.emptyList());
    }

    public JWKSet(JWK key) {
        this(Collections.singletonList(key));
        if (key == null) {
            throw new IllegalArgumentException("The JWK must not be null");
        }
    }

    public JWKSet(List<JWK> keys) {
        this(keys, Collections.emptyMap());
    }

    public JWKSet(List<JWK> keys, Map<String, Object> customMembers) {
        if (keys == null) {
            throw new IllegalArgumentException("The JWK list must not be null");
        }
        this.keys = Collections.unmodifiableList(keys);
        this.customMembers = Collections.unmodifiableMap(customMembers);
    }

    public List<JWK> getKeys() {
        return this.keys;
    }

    public JWK getKeyByKeyId(String kid) {
        for (JWK key : this.getKeys()) {
            if (key.getKeyID() == null || !key.getKeyID().equals(kid)) continue;
            return key;
        }
        return null;
    }

    public boolean containsJWK(JWK jwk) throws JOSEException {
        Base64URL thumbprint = jwk.computeThumbprint();
        for (JWK k : this.getKeys()) {
            if (!thumbprint.equals(k.computeThumbprint())) continue;
            return true;
        }
        return false;
    }

    public Map<String, Object> getAdditionalMembers() {
        return this.customMembers;
    }

    public JWKSet toPublicJWKSet() {
        LinkedList<JWK> publicKeyList = new LinkedList<JWK>();
        for (JWK key : this.keys) {
            JWK publicKey = key.toPublicJWK();
            if (publicKey == null) continue;
            publicKeyList.add(publicKey);
        }
        return new JWKSet(publicKeyList, this.customMembers);
    }

    public JSONObject toJSONObject() {
        return this.toJSONObject(true);
    }

    public JSONObject toJSONObject(boolean publicKeysOnly) {
        JSONObject o = new JSONObject(this.customMembers);
        JSONArray a = new JSONArray();
        for (JWK key : this.keys) {
            if (publicKeysOnly) {
                JWK publicKey = key.toPublicJWK();
                if (publicKey == null) continue;
                a.add((Object)publicKey.toJSONObject());
                continue;
            }
            a.add((Object)key.toJSONObject());
        }
        o.put((Object)"keys", (Object)a);
        return o;
    }

    public String toString() {
        return this.toJSONObject().toString();
    }

    public static JWKSet parse(String s) throws ParseException {
        return JWKSet.parse(JSONObjectUtils.parse(s));
    }

    public static JWKSet parse(JSONObject json) throws ParseException {
        JSONArray keyArray = JSONObjectUtils.getJSONArray(json, "keys");
        if (keyArray == null) {
            throw new ParseException("Missing required \"keys\" member", 0);
        }
        LinkedList<JWK> keys = new LinkedList<JWK>();
        for (int i = 0; i < keyArray.size(); ++i) {
            if (!(keyArray.get(i) instanceof JSONObject)) {
                throw new ParseException("The \"keys\" JSON array must contain JSON objects only", 0);
            }
            JSONObject keyJSON = (JSONObject)keyArray.get(i);
            try {
                keys.add(JWK.parse(keyJSON));
                continue;
            }
            catch (ParseException e) {
                if (e.getMessage() != null && e.getMessage().startsWith("Unsupported key type")) continue;
                throw new ParseException("Invalid JWK at position " + i + ": " + e.getMessage(), 0);
            }
        }
        HashMap<String, Object> additionalMembers = new HashMap<String, Object>();
        for (Map.Entry entry : json.entrySet()) {
            if (entry.getKey() == null || ((String)entry.getKey()).equals("keys")) continue;
            additionalMembers.put((String)entry.getKey(), entry.getValue());
        }
        return new JWKSet(keys, additionalMembers);
    }

    public static JWKSet load(InputStream inputStream) throws IOException, ParseException {
        return JWKSet.parse(IOUtils.readInputStreamToString(inputStream, Charset.forName("UTF-8")));
    }

    public static JWKSet load(File file) throws IOException, ParseException {
        return JWKSet.parse(IOUtils.readFileToString(file, Charset.forName("UTF-8")));
    }

    public static JWKSet load(URL url, int connectTimeout, int readTimeout, int sizeLimit) throws IOException, ParseException {
        return JWKSet.load(url, connectTimeout, readTimeout, sizeLimit, null);
    }

    public static JWKSet load(URL url, int connectTimeout, int readTimeout, int sizeLimit, Proxy proxy) throws IOException, ParseException {
        DefaultResourceRetriever resourceRetriever = new DefaultResourceRetriever(connectTimeout, readTimeout, sizeLimit);
        resourceRetriever.setProxy(proxy);
        Resource resource = resourceRetriever.retrieveResource(url);
        return JWKSet.parse(resource.getContent());
    }

    public static JWKSet load(URL url) throws IOException, ParseException {
        return JWKSet.load(url, 0, 0, 0);
    }

    public static JWKSet load(KeyStore keyStore, PasswordLookup pwLookup) throws KeyStoreException {
        char[] keyPassword;
        String keyAlias;
        LinkedList<JWK> jwks = new LinkedList<JWK>();
        Enumeration<String> keyAliases = keyStore.aliases();
        while (keyAliases.hasMoreElements()) {
            ECKey ecJWK;
            keyAlias = keyAliases.nextElement();
            char[] cArray = keyPassword = pwLookup == null ? "".toCharArray() : pwLookup.lookupPassword(keyAlias);
            Certificate cert = keyStore.getCertificate(keyAlias);
            if (cert == null) continue;
            if (cert.getPublicKey() instanceof RSAPublicKey) {
                RSAKey rsaJWK;
                try {
                    rsaJWK = RSAKey.load(keyStore, keyAlias, keyPassword);
                }
                catch (JOSEException e) {
                    continue;
                }
                if (rsaJWK == null) continue;
                jwks.add(rsaJWK);
                continue;
            }
            if (!(cert.getPublicKey() instanceof ECPublicKey)) continue;
            try {
                ecJWK = ECKey.load(keyStore, keyAlias, keyPassword);
            }
            catch (JOSEException e) {
                continue;
            }
            if (ecJWK == null) continue;
            jwks.add(ecJWK);
        }
        keyAliases = keyStore.aliases();
        while (keyAliases.hasMoreElements()) {
            OctetSequenceKey octJWK;
            keyAlias = keyAliases.nextElement();
            keyPassword = pwLookup == null ? "".toCharArray() : pwLookup.lookupPassword(keyAlias);
            try {
                octJWK = OctetSequenceKey.load(keyStore, keyAlias, keyPassword);
            }
            catch (JOSEException e) {
                continue;
            }
            if (octJWK == null) continue;
            jwks.add(octJWK);
        }
        return new JWKSet(jwks);
    }
}

