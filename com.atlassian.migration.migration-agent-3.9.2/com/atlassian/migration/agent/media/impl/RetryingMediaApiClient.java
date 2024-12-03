/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.jodah.failsafe.Failsafe
 *  net.jodah.failsafe.Policy
 *  net.jodah.failsafe.RetryPolicy
 */
package com.atlassian.migration.agent.media.impl;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.migration.agent.media.ClientId;
import com.atlassian.migration.agent.media.CreateFileOptions;
import com.atlassian.migration.agent.media.Entity;
import com.atlassian.migration.agent.media.Etag;
import com.atlassian.migration.agent.media.MediaApiClient;
import com.atlassian.migration.agent.media.Upload;
import com.atlassian.migration.agent.okhttp.RetryPolicyBuilder;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.Policy;
import net.jodah.failsafe.RetryPolicy;

public class RetryingMediaApiClient
implements MediaApiClient {
    private final MediaApiClient wrapped;
    private final RetryPolicy<Entity> entityRetryPolicy;
    private final RetryPolicy<Etag> etagRetryPolicy;
    private final RetryPolicy<Optional<Upload>> optionalRetryPolicy;
    private final RetryPolicy<ClientId> clientIdRetryPolicy;
    private final RetryPolicy<Void> voidRetryPolicy;

    RetryingMediaApiClient(MediaApiClient wrapped) {
        this(wrapped, RetryPolicyBuilder.mediaRateLimitPolicy().build(), RetryPolicyBuilder.mediaRateLimitPolicy().build(), RetryPolicyBuilder.mediaRateLimitPolicy().build(), RetryPolicyBuilder.mediaRateLimitPolicy().build(), RetryPolicyBuilder.mediaRateLimitPolicy().build());
    }

    @VisibleForTesting
    RetryingMediaApiClient(MediaApiClient wrapped, RetryPolicy<Entity> entityRetryPolicy, RetryPolicy<Etag> etagRetryPolicy, RetryPolicy<Optional<Upload>> optionalRetryPolicy, RetryPolicy<ClientId> clientIdRetryPolicy, RetryPolicy<Void> voidRetryPolicy) {
        this.wrapped = wrapped;
        this.entityRetryPolicy = entityRetryPolicy;
        this.etagRetryPolicy = etagRetryPolicy;
        this.optionalRetryPolicy = optionalRetryPolicy;
        this.clientIdRetryPolicy = clientIdRetryPolicy;
        this.voidRetryPolicy = voidRetryPolicy;
    }

    @Override
    @Nonnull
    public ClientId createClient(String title, String description) {
        return (ClientId)Failsafe.with(this.clientIdRetryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.wrapped.createClient(title, description));
    }

    @Override
    @Nonnull
    public Etag uploadChunk(ByteBuffer data, String uploadId, String partNumber) {
        return (Etag)Failsafe.with(this.etagRetryPolicy, (Policy[])new RetryPolicy[0]).get(() -> {
            data.rewind();
            return this.wrapped.uploadChunk(data, uploadId, partNumber);
        });
    }

    @Override
    @Nonnull
    public Optional<Upload> createUpload() {
        List<Object> policyList = Collections.singletonList(this.optionalRetryPolicy.handleResultIf(upload -> upload != null && !upload.isPresent()));
        return (Optional)Failsafe.with(policyList).get(this.wrapped::createUpload);
    }

    @Override
    public void updateUpload(String uploadId, int offset, List<Etag> etags) {
        Failsafe.with(this.voidRetryPolicy, (Policy[])new RetryPolicy[0]).run(() -> this.wrapped.updateUpload(uploadId, offset, etags));
    }

    @Override
    public Entity uploadFile(InputStream inputStream, String fileName) {
        return (Entity)Failsafe.with(this.entityRetryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.wrapped.uploadFile(inputStream, fileName));
    }

    @Override
    @Nonnull
    public Entity createFileFromUpload(String uploadId, @Nullable String name, @Nullable String mimeType, CreateFileOptions options) {
        return (Entity)Failsafe.with(this.entityRetryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.wrapped.createFileFromUpload(uploadId, name, mimeType, options));
    }

    @Override
    @Nonnull
    public Entity createFileFromUpload(String uploadId, CreateFileOptions options) {
        return (Entity)Failsafe.with(this.entityRetryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.wrapped.createFileFromUpload(uploadId, options));
    }

    @Override
    @Nonnull
    public Entity createFileFromChunks(List<Etag> etags, @Nullable String name, @Nullable String mimeType, CreateFileOptions options) {
        return (Entity)Failsafe.with(this.entityRetryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.wrapped.createFileFromChunks(etags, name, mimeType, options));
    }

    @Override
    @Nonnull
    public Entity createFileFromChunks(List<Etag> etags, CreateFileOptions options) {
        return (Entity)Failsafe.with(this.entityRetryPolicy, (Policy[])new RetryPolicy[0]).get(() -> this.wrapped.createFileFromChunks(etags, options));
    }
}

