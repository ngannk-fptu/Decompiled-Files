/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt.core.keys;

import com.atlassian.jwt.core.keys.KeyUtils;
import com.atlassian.jwt.core.keys.PrivateKeyRetriever;
import com.atlassian.jwt.exception.JwtCannotRetrieveKeyException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.interfaces.RSAPrivateKey;
import javax.annotation.Nonnull;

public class PrivateKeyRetrieverImpl
implements PrivateKeyRetriever {
    private final PrivateKeyRetriever.keyLocationType type;
    private final String location;
    private final KeyUtils keyUtils;

    public PrivateKeyRetrieverImpl(PrivateKeyRetriever.keyLocationType type, String keyLocation) {
        this(type, keyLocation, new KeyUtils());
    }

    public PrivateKeyRetrieverImpl(PrivateKeyRetriever.keyLocationType type, String keyLocation, KeyUtils keyUtils) {
        this.type = type;
        this.location = keyLocation;
        this.keyUtils = keyUtils;
    }

    @Override
    @Nonnull
    public RSAPrivateKey getPrivateKey() throws JwtCannotRetrieveKeyException {
        if (this.type == PrivateKeyRetriever.keyLocationType.CLASSPATH_RESOURCE) {
            return this.getPrivateKeyFromClasspathResource();
        }
        if (this.type == PrivateKeyRetriever.keyLocationType.FILE) {
            return this.getPrivateKeyFromFile();
        }
        throw new JwtCannotRetrieveKeyException("Unsupported key location type " + (Object)((Object)this.type));
    }

    private RSAPrivateKey getPrivateKeyFromClasspathResource() throws JwtCannotRetrieveKeyException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(this.location);
        if (in == null) {
            throw new JwtCannotRetrieveKeyException("Could not load classpath resource " + this.location);
        }
        return this.keyUtils.readRsaPrivateKeyFromPem(new InputStreamReader(in));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private RSAPrivateKey getPrivateKeyFromFile() throws JwtCannotRetrieveKeyException {
        try (FileReader reader = new FileReader(this.location);){
            RSAPrivateKey rSAPrivateKey = this.keyUtils.readRsaPrivateKeyFromPem(reader);
            return rSAPrivateKey;
        }
        catch (IOException e) {
            throw new JwtCannotRetrieveKeyException("Unable to read key file: " + this.location, e);
        }
    }
}

