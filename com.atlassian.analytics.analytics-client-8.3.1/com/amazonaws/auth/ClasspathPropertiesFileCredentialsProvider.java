/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;
import java.io.IOException;
import java.io.InputStream;

public class ClasspathPropertiesFileCredentialsProvider
implements AWSCredentialsProvider {
    private static String DEFAULT_PROPERTIES_FILE = "AwsCredentials.properties";
    private final String credentialsFilePath;

    public ClasspathPropertiesFileCredentialsProvider() {
        this(DEFAULT_PROPERTIES_FILE);
    }

    public ClasspathPropertiesFileCredentialsProvider(String credentialsFilePath) {
        if (credentialsFilePath == null) {
            throw new IllegalArgumentException("Credentials file path cannot be null");
        }
        this.credentialsFilePath = !credentialsFilePath.startsWith("/") ? "/" + credentialsFilePath : credentialsFilePath;
    }

    @Override
    public AWSCredentials getCredentials() {
        InputStream inputStream = this.getClass().getResourceAsStream(this.credentialsFilePath);
        if (inputStream == null) {
            throw new SdkClientException("Unable to load AWS credentials from the " + this.credentialsFilePath + " file on the classpath");
        }
        try {
            return new PropertiesCredentials(inputStream);
        }
        catch (IOException e) {
            throw new SdkClientException("Unable to load AWS credentials from the " + this.credentialsFilePath + " file on the classpath", e);
        }
    }

    @Override
    public void refresh() {
    }

    public String toString() {
        return this.getClass().getSimpleName() + "(" + this.credentialsFilePath + ")";
    }
}

