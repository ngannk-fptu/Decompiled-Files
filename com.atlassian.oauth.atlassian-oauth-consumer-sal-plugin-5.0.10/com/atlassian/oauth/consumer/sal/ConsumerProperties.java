/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.Consumer$InstanceBuilder
 *  com.atlassian.oauth.Consumer$SignatureMethod
 *  com.atlassian.oauth.consumer.ConsumerCreationException
 *  com.atlassian.oauth.consumer.core.ConsumerServiceStore$ConsumerAndSecret
 *  com.atlassian.oauth.shared.sal.AbstractSettingsProperties
 *  com.atlassian.oauth.util.RSAKeys
 */
package com.atlassian.oauth.consumer.sal;

import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.consumer.ConsumerCreationException;
import com.atlassian.oauth.consumer.core.ConsumerServiceStore;
import com.atlassian.oauth.shared.sal.AbstractSettingsProperties;
import com.atlassian.oauth.util.RSAKeys;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;

final class ConsumerProperties
extends AbstractSettingsProperties {
    static final String KEY = "key";
    static final String NAME = "name";
    static final String PUBLIC_KEY = "publicKey";
    static final String PRIVATE_KEY = "privateKey";
    static final String DESCRIPTION = "description";
    static final String CALLBACK = "callback";
    static final String SIGNATURE_METHOD = "signatureMethod";
    static final String SHARED_SECRET = "sharedSecret";

    public ConsumerProperties(Properties properties) {
        super(properties);
    }

    public ConsumerProperties(ConsumerServiceStore.ConsumerAndSecret cas) {
        this.putConsumerKey(cas.getConsumer().getKey());
        this.putSignatureMethod(cas.getConsumer().getSignatureMethod());
        if (cas.getConsumer().getSignatureMethod() == Consumer.SignatureMethod.HMAC_SHA1) {
            this.putSharedSecrect(cas.getSharedSecret());
        } else {
            this.putPublicKey(cas.getConsumer().getPublicKey());
            this.putPrivateKey(cas.getPrivateKey());
        }
        this.putConsumerName(cas.getConsumer().getName());
        if (cas.getConsumer().getCallback() != null) {
            this.putCallback(cas.getConsumer().getCallback());
        }
        this.putDescription(cas.getConsumer().getDescription());
    }

    public String getConsumerKey() {
        return this.get(KEY);
    }

    public void putConsumerKey(String key) {
        this.put(KEY, key);
    }

    public Consumer.SignatureMethod getSignatureMethod() {
        return Consumer.SignatureMethod.valueOf((String)this.get(SIGNATURE_METHOD));
    }

    public void putSignatureMethod(Consumer.SignatureMethod signatureMethod) {
        this.put(SIGNATURE_METHOD, signatureMethod.name());
    }

    public String getSharedSecret() {
        return this.get(SHARED_SECRET);
    }

    public void putSharedSecrect(String sharedSecret) {
        this.put(SHARED_SECRET, sharedSecret);
    }

    public PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        return RSAKeys.fromPemEncodingToPublicKey((String)this.get(PUBLIC_KEY));
    }

    public void putPublicKey(PublicKey publicKey) {
        this.put(PUBLIC_KEY, RSAKeys.toPemEncoding((Key)publicKey));
    }

    public PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        return RSAKeys.fromPemEncodingToPrivateKey((String)this.get(PRIVATE_KEY));
    }

    public void putPrivateKey(PrivateKey privateKey) {
        this.put(PRIVATE_KEY, RSAKeys.toPemEncoding((Key)privateKey));
    }

    public String getConsumerName() {
        return this.get(NAME);
    }

    public void putConsumerName(String name) {
        this.put(NAME, name);
    }

    public URI getCallback() throws URISyntaxException {
        String callback = this.get(CALLBACK);
        if (callback == null) {
            return null;
        }
        return new URI(callback);
    }

    public void putCallback(URI callback) {
        if (callback == null) {
            return;
        }
        this.put(CALLBACK, callback.toString());
    }

    public String getDescription() {
        return this.get(DESCRIPTION);
    }

    public void putDescription(String description) {
        if (description == null) {
            return;
        }
        this.put(DESCRIPTION, description);
    }

    public ConsumerServiceStore.ConsumerAndSecret asConsumerAndSecret(String serviceName) {
        try {
            Consumer consumer = this.newConsumer();
            if (consumer.getSignatureMethod() == Consumer.SignatureMethod.HMAC_SHA1) {
                return new ConsumerServiceStore.ConsumerAndSecret(serviceName, consumer, this.getSharedSecret());
            }
            return new ConsumerServiceStore.ConsumerAndSecret(serviceName, consumer, this.getPrivateKey());
        }
        catch (NoSuchAlgorithmException e) {
            throw new ConsumerCreationException("No encryption provider with the RSA algorithm installed", (Throwable)e);
        }
        catch (InvalidKeySpecException e) {
            throw new ConsumerCreationException("Invalid public key found in store", (Throwable)e);
        }
        catch (URISyntaxException e) {
            throw new ConsumerCreationException("Callback URI in store is not a valid URI", (Throwable)e);
        }
    }

    private Consumer newConsumer() throws URISyntaxException, NoSuchAlgorithmException, InvalidKeySpecException {
        Consumer.SignatureMethod signatureMethod = this.getSignatureMethod();
        Consumer.InstanceBuilder builder = Consumer.key((String)this.getConsumerKey()).name(this.getConsumerName()).description(this.getDescription()).callback(this.getCallback()).signatureMethod(signatureMethod);
        if (signatureMethod == Consumer.SignatureMethod.RSA_SHA1) {
            builder = builder.publicKey(this.getPublicKey());
        }
        Consumer consumer = builder.build();
        return consumer;
    }
}

