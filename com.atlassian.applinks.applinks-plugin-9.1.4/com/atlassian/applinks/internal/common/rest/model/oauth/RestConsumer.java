/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.Consumer$SignatureMethod
 *  com.atlassian.oauth.util.RSAKeys
 *  com.google.common.base.Function
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 */
package com.atlassian.applinks.internal.common.rest.model.oauth;

import com.atlassian.applinks.internal.rest.model.BaseRestEntity;
import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.util.RSAKeys;
import com.google.common.base.Function;
import java.net.URI;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize
public class RestConsumer
extends BaseRestEntity {
    public static final Function<Object, RestConsumer> REST_TRANSFORM = new Function<Object, RestConsumer>(){

        @Nullable
        public RestConsumer apply(@Nullable Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof RestConsumer) {
                return (RestConsumer)object;
            }
            if (object instanceof Map) {
                return new RestConsumer((Map)object);
            }
            throw new IllegalArgumentException("Cannot instantiate RestConsumer from " + object);
        }
    };
    public static final String KEY = "key";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String SIGNATURE_METHOD = "signatureMethod";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String CALLBACK = "callback";
    public static final String TWO_LO_ALLOWED = "twoLOAllowed";
    public static final String EXECUTING_TWO_LO_USER = "executingTwoLOUser";
    public static final String TWO_LO_IMPERSONATION_ALLOWED = "twoLOImpersonationAllowed";
    public static final String OUTGOING = "outgoing";
    public static final String SHARED_SECRET = "sharedSecret";

    public RestConsumer() {
    }

    public RestConsumer(Map<String, Object> original) {
        super(original);
    }

    public RestConsumer(@Nonnull Consumer consumer, boolean outgoing, @Nullable String sharedSecret) {
        this(consumer);
        this.put(OUTGOING, (Object)outgoing);
        this.putIfNotNull(SHARED_SECRET, sharedSecret);
    }

    public RestConsumer(@Nonnull Consumer consumer) {
        Objects.requireNonNull(consumer, "consumer");
        this.put(KEY, (Object)consumer.getKey());
        this.put(NAME, (Object)consumer.getName());
        this.putIfNotNull(DESCRIPTION, consumer.getDescription());
        this.put(SIGNATURE_METHOD, (Object)consumer.getSignatureMethod().name());
        if (consumer.getPublicKey() != null) {
            this.put(PUBLIC_KEY, (Object)RSAKeys.toPemEncoding((Key)consumer.getPublicKey()));
        }
        this.putAsString(CALLBACK, consumer.getCallback());
        this.put(TWO_LO_ALLOWED, (Object)consumer.getTwoLOAllowed());
        this.putIfNotNull(EXECUTING_TWO_LO_USER, consumer.getExecutingTwoLOUser());
        this.put(TWO_LO_IMPERSONATION_ALLOWED, (Object)consumer.getTwoLOImpersonationAllowed());
    }

    @Nonnull
    public String getKey() {
        return RestConsumer.requiredValue(KEY, this.getString(KEY));
    }

    @Nonnull
    public String getName() {
        return RestConsumer.requiredValue(NAME, this.getString(NAME));
    }

    @Nonnull
    public Consumer.SignatureMethod getSignatureMethod() {
        return RestConsumer.requiredValue(SIGNATURE_METHOD, this.getEnum(SIGNATURE_METHOD, Consumer.SignatureMethod.class));
    }

    @Nullable
    public URI getCallback() {
        if (this.containsKey(CALLBACK)) {
            return URI.create(this.getString(CALLBACK));
        }
        return null;
    }

    @Nullable
    public PublicKey getPublicKey() {
        if (!this.containsKey(PUBLIC_KEY)) {
            return null;
        }
        try {
            return RSAKeys.fromPemEncodingToPublicKey((String)this.getString(PUBLIC_KEY));
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Invalid key", e);
        }
    }

    public boolean isTwoLoAllowed() {
        return this.getBooleanValue(TWO_LO_ALLOWED);
    }

    public boolean isTwoLoImpersonationAllowed() {
        return this.getBooleanValue(TWO_LO_IMPERSONATION_ALLOWED);
    }
}

