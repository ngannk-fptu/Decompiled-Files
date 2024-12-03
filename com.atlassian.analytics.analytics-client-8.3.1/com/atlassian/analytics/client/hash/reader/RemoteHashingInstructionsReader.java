/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.analytics.client.hash.reader;

import com.atlassian.analytics.client.hash.reader.HashingInstructionsReader;
import com.atlassian.analytics.client.s3.AnalyticsS3Client;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteHashingInstructionsReader
implements HashingInstructionsReader {
    private static final Logger LOG = LoggerFactory.getLogger(RemoteHashingInstructionsReader.class);
    private static final String ANALYTICS_CONFIG_S3_BUCKET_NAME = "btf-analytics";
    private static final String ANALYTICS_CONFIG_S3_KEY_PREFIX = "config/";
    private final AnalyticsS3Client analyticsS3Client;

    public RemoteHashingInstructionsReader(AnalyticsS3Client analyticsS3Client) {
        this.analyticsS3Client = Objects.requireNonNull(analyticsS3Client);
    }

    @Override
    public Optional<String> readInstructions(String instructionsKey) {
        block3: {
            try {
                com.google.common.base.Optional<InputStream> remoteListObject = this.getRemoteListObject(instructionsKey);
                if (remoteListObject.isPresent()) {
                    return Optional.ofNullable(StringUtils.trim((String)IOUtils.toString((InputStream)((InputStream)remoteListObject.get()))));
                }
                LOG.warn("Unable to read remote instructions with key '{}'.", (Object)instructionsKey);
            }
            catch (IOException e) {
                LOG.warn("Unable to read remote instructions with key '{}'. Enable debug logging for more info.", (Object)instructionsKey);
                if (!LOG.isDebugEnabled()) break block3;
                LOG.debug("Unable to read remote instructions with key '{}'", (Object)instructionsKey, (Object)e);
            }
        }
        return Optional.empty();
    }

    private com.google.common.base.Optional<InputStream> getRemoteListObject(String objectName) {
        return com.google.common.base.Optional.fromNullable((Object)this.analyticsS3Client.getS3ObjectInputStream(ANALYTICS_CONFIG_S3_BUCKET_NAME, ANALYTICS_CONFIG_S3_KEY_PREFIX + objectName));
    }
}

