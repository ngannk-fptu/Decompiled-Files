/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.auth;

import com.amazonaws.AmazonClientException;
import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.InstanceMetadataServiceCredentialsFetcher;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class InstanceProfileCredentialsProvider
implements AWSCredentialsProvider,
Closeable {
    private static final Log LOG = LogFactory.getLog(InstanceProfileCredentialsProvider.class);
    private static final int ASYNC_REFRESH_INTERVAL_TIME_MINUTES = 1;
    private static final InstanceProfileCredentialsProvider INSTANCE = new InstanceProfileCredentialsProvider();
    private final InstanceMetadataServiceCredentialsFetcher credentialsFetcher = new InstanceMetadataServiceCredentialsFetcher();
    private volatile ScheduledExecutorService executor;
    private volatile boolean shouldRefresh = false;

    @Deprecated
    public InstanceProfileCredentialsProvider() {
        this(false);
    }

    public InstanceProfileCredentialsProvider(boolean refreshCredentialsAsync) {
        this(refreshCredentialsAsync, true);
    }

    public static InstanceProfileCredentialsProvider createAsyncRefreshingProvider(boolean eagerlyRefreshCredentialsAsync) {
        return new InstanceProfileCredentialsProvider(true, eagerlyRefreshCredentialsAsync);
    }

    private InstanceProfileCredentialsProvider(boolean refreshCredentialsAsync, boolean eagerlyRefreshCredentialsAsync) {
        if (!SDKGlobalConfiguration.isEc2MetadataDisabled() && refreshCredentialsAsync) {
            this.executor = Executors.newScheduledThreadPool(1, new ThreadFactory(){

                @Override
                public Thread newThread(Runnable r) {
                    Thread t = Executors.defaultThreadFactory().newThread(r);
                    t.setName("instance-profile-credentials-refresh");
                    t.setDaemon(true);
                    return t;
                }
            });
            this.executor.scheduleWithFixedDelay(new Runnable(){

                @Override
                public void run() {
                    try {
                        if (InstanceProfileCredentialsProvider.this.shouldRefresh) {
                            InstanceProfileCredentialsProvider.this.credentialsFetcher.getCredentials();
                        }
                    }
                    catch (AmazonClientException ace) {
                        this.handleAsyncRefreshError(ace);
                    }
                    catch (RuntimeException re) {
                        this.handleAsyncRefreshError(re);
                    }
                }

                private void handleAsyncRefreshError(Exception e) {
                    LOG.warn((Object)"Failed when refreshing credentials asynchronously.", (Throwable)e);
                }
            }, 0L, 1L, TimeUnit.MINUTES);
        }
    }

    public static InstanceProfileCredentialsProvider getInstance() {
        return INSTANCE;
    }

    protected void finalize() throws Throwable {
        if (this.executor != null) {
            this.executor.shutdownNow();
        }
    }

    @Override
    public AWSCredentials getCredentials() {
        if (SDKGlobalConfiguration.isEc2MetadataDisabled()) {
            throw new AmazonClientException("AWS_EC2_METADATA_DISABLED is set to true, not loading credentials from EC2 Instance Metadata service");
        }
        AWSCredentials creds = this.credentialsFetcher.getCredentials();
        this.shouldRefresh = true;
        return creds;
    }

    @Override
    public void refresh() {
        if (this.credentialsFetcher != null) {
            this.credentialsFetcher.refresh();
        }
    }

    @Override
    public void close() throws IOException {
        if (this.executor != null) {
            this.executor.shutdownNow();
            this.executor = null;
        }
    }
}

