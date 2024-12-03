/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.analytics.client.eventfilter.reader;

import com.atlassian.analytics.client.eventfilter.reader.FilterListReader;
import com.atlassian.analytics.client.s3.AnalyticsS3Client;
import java.io.InputStream;
import java.util.Objects;
import javax.annotation.Nullable;

public class RemoteListReader
implements FilterListReader {
    private static final String ANALYTICS_CONFIG_S3_BUCKET_NAME = "btf-analytics";
    private static final String ANALYTICS_CONFIG_S3_KEY_PREFIX = "config/";
    private final AnalyticsS3Client analyticsS3Client;

    public RemoteListReader(AnalyticsS3Client analyticsS3Client) {
        this.analyticsS3Client = Objects.requireNonNull(analyticsS3Client);
    }

    @Override
    @Nullable
    public InputStream readFilterList(String listName) {
        return this.getRemoteListObject(listName);
    }

    private InputStream getRemoteListObject(String listName) {
        return this.analyticsS3Client.getS3ObjectInputStream(ANALYTICS_CONFIG_S3_BUCKET_NAME, ANALYTICS_CONFIG_S3_KEY_PREFIX + listName);
    }
}

