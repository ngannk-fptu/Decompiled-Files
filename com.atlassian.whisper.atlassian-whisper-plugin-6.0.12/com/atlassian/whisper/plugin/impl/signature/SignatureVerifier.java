/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.io.ByteStreams
 *  com.google.gson.JsonParser
 *  javax.inject.Named
 *  org.codehaus.jettison.json.JSONArray
 *  org.codehaus.jettison.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.whisper.plugin.impl.signature;

import com.atlassian.whisper.plugin.impl.signature.SignatureVerificationException;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;
import javax.inject.Named;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class SignatureVerifier {
    private static final Logger LOG = LoggerFactory.getLogger(SignatureVerifier.class);
    private final Optional<PublicKey> publicKey;

    public SignatureVerifier() {
        this("key.der");
    }

    protected SignatureVerifier(String resourceName) {
        this.publicKey = this.getPublicKey(resourceName);
    }

    private Optional<PublicKey> getPublicKey(String resourceName) {
        try {
            byte[] publicKeyBytes = ByteStreams.toByteArray((InputStream)this.getClass().getClassLoader().getResourceAsStream(resourceName));
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return Optional.of(kf.generatePublic(spec));
        }
        catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            LOG.error("Unable to get public key", (Throwable)e);
            return Optional.empty();
        }
    }

    public void verifyMessages(JSONArray jsonMessages, JSONObject jsonMessagesSignatures) throws SignatureVerificationException {
        try {
            for (int i = 0; i < jsonMessages.length(); ++i) {
                JSONObject jsonMessage = jsonMessages.getJSONObject(i);
                String messageId = (String)jsonMessage.get("id");
                JSONArray signatures = jsonMessagesSignatures.getJSONArray(messageId);
                boolean validSignature = false;
                for (int j = 0; j < signatures.length() && !validSignature; ++j) {
                    String signature = signatures.getString(j);
                    validSignature = validSignature || this.verifyMessage(jsonMessage, signature);
                }
                if (validSignature) continue;
                LOG.debug("Failed to verify message id={} - none of the provided signatures is valid", (Object)messageId);
                throw new SignatureVerificationException();
            }
        }
        catch (Exception ex) {
            throw new SignatureVerificationException(ex);
        }
    }

    private boolean verifyMessage(JSONObject jsonMessage, String signature) throws UnsupportedEncodingException, InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        byte[] messageCanonicalBytes = new JsonParser().parse(jsonMessage.toString()).toString().getBytes("UTF-8");
        byte[] signatureBytes = Base64.getDecoder().decode(signature);
        Signature signatureVerifier = Signature.getInstance("SHA512withRSA");
        signatureVerifier.initVerify(this.publicKey.orElseThrow(() -> new NullPointerException("Public key is missing")));
        signatureVerifier.update(messageCanonicalBytes);
        return signatureVerifier.verify(signatureBytes);
    }
}

