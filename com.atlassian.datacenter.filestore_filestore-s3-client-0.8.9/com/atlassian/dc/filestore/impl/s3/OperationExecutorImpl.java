/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.conn.ConnectionPoolTimeoutException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  software.amazon.awssdk.core.exception.SdkException
 *  software.amazon.awssdk.services.s3.S3Client
 */
package com.atlassian.dc.filestore.impl.s3;

import com.atlassian.dc.filestore.impl.s3.ClientFactory;
import com.atlassian.dc.filestore.impl.s3.ClientOperation;
import com.atlassian.dc.filestore.impl.s3.OperationExecutor;
import java.io.IOException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;

public class OperationExecutorImpl
implements OperationExecutor {
    private static final Logger log = LoggerFactory.getLogger(OperationExecutorImpl.class);
    private final ClientFactory clientFactory;
    private S3Client cachedClient;

    public OperationExecutorImpl(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public <T> T performOperation(ClientOperation<T> operation) throws IOException {
        S3Client firstClient = this.getCachedClient();
        try {
            return operation.perform(firstClient);
        }
        catch (SdkException exception) {
            S3Client secondClient = this.onClientFailure((Exception)((Object)exception), firstClient);
            try {
                return operation.perform(secondClient);
            }
            catch (SdkException fatalException) {
                throw new IOException(fatalException.getMessage(), fatalException);
            }
        }
    }

    private synchronized S3Client getCachedClient() {
        if (this.cachedClient == null) {
            this.cachedClient = this.clientFactory.getClient();
        }
        return this.cachedClient;
    }

    private synchronized S3Client onClientFailure(Exception error, S3Client failedClient) {
        if (this.cachedClient == failedClient) {
            log.warn("Error during S3 Operation: {}", (Object)error.getMessage());
            if (error.getCause() instanceof ConnectionPoolTimeoutException) {
                log.warn("Consider increasing max connections or connection acquisition timeout.");
            }
            this.cachedClient = this.clientFactory.getClient();
        }
        return this.cachedClient;
    }
}

