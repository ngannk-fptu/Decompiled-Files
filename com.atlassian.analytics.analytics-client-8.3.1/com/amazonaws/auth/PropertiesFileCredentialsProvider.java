/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;
import java.io.File;
import java.io.IOException;

public class PropertiesFileCredentialsProvider
implements AWSCredentialsProvider {
    private final String credentialsFilePath;

    public PropertiesFileCredentialsProvider(String credentialsFilePath) {
        if (credentialsFilePath == null) {
            throw new IllegalArgumentException("Credentials file path cannot be null");
        }
        this.credentialsFilePath = credentialsFilePath;
    }

    @Override
    public AWSCredentials getCredentials() {
        try {
            return new PropertiesCredentials(new File(this.credentialsFilePath));
        }
        catch (IOException e) {
            throw new SdkClientException("Unable to load AWS credentials from the " + this.credentialsFilePath + " file", e);
        }
    }

    @Override
    public void refresh() {
    }

    public String toString() {
        return this.getClass().getSimpleName() + "(" + this.credentialsFilePath + ")";
    }
}

