/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.business.insights.api.dataset.DataChannel
 *  com.atlassian.business.insights.api.dataset.Dataset
 *  com.atlassian.business.insights.api.extract.LogRecordStreamer
 *  com.atlassian.business.insights.api.schema.Schema
 *  com.atlassian.business.insights.attribute.AttributeDefinition
 *  com.atlassian.business.insights.core.dataset.DefaultDataChannel
 *  com.atlassian.business.insights.core.schema.ColumnFileSchema
 *  com.atlassian.business.insights.core.schema.DefaultSchema
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.confluence.dataset;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.business.insights.api.dataset.DataChannel;
import com.atlassian.business.insights.api.dataset.Dataset;
import com.atlassian.business.insights.api.extract.LogRecordStreamer;
import com.atlassian.business.insights.api.schema.Schema;
import com.atlassian.business.insights.attribute.AttributeDefinition;
import com.atlassian.business.insights.confluence.attribute.CommentAttributes;
import com.atlassian.business.insights.confluence.attribute.PageAttributes;
import com.atlassian.business.insights.confluence.attribute.SpaceAttributes;
import com.atlassian.business.insights.confluence.attribute.UserAttributes;
import com.atlassian.business.insights.confluence.extract.CommentLogRecordStreamer;
import com.atlassian.business.insights.confluence.extract.PageLogRecordStreamer;
import com.atlassian.business.insights.confluence.extract.SpaceLogRecordStreamer;
import com.atlassian.business.insights.confluence.extract.UserLogRecordStreamer;
import com.atlassian.business.insights.core.dataset.DefaultDataChannel;
import com.atlassian.business.insights.core.schema.ColumnFileSchema;
import com.atlassian.business.insights.core.schema.DefaultSchema;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;

public class ConfluenceDatasetV1
implements Dataset {
    @VisibleForTesting
    public static final String SPACES_FILE_NAME = "spaces";
    @VisibleForTesting
    public static final String PAGES_FILE_NAME = "pages";
    @VisibleForTesting
    public static final String COMMENTS_FILE_NAME = "comments";
    @VisibleForTesting
    public static final String USERS_FILE_NAME = "users";
    private static final String DATASET_NAME = "Confluence Dataset V1 - spaces,pages,comments,users";
    private final int version = 1;
    private final Schema spaceSchema = new DefaultSchema("spaces", Collections.singletonList(new ColumnFileSchema("spaces", AttributeDefinition.toLinkedHashMap(SpaceAttributes.getAttributes()))), 1);
    private final Schema pageSchema = new DefaultSchema("pages", Collections.singletonList(new ColumnFileSchema("pages", AttributeDefinition.toLinkedHashMap(PageAttributes.getAttributes()))), 1);
    private final Schema commentSchema = new DefaultSchema("comments", Collections.singletonList(new ColumnFileSchema("comments", AttributeDefinition.toLinkedHashMap(CommentAttributes.getAttributes()))), 1);
    private final Schema userSchema = new DefaultSchema("users", Collections.singletonList(new ColumnFileSchema("users", AttributeDefinition.toLinkedHashMap(UserAttributes.getAttributes()))), 1);
    private final List<DataChannel> dataChannels;

    public ConfluenceDatasetV1(SpaceLogRecordStreamer spaceLogRecordStreamer, PageLogRecordStreamer pageLogRecordStreamer, CommentLogRecordStreamer commentLogRecordStreamer, UserLogRecordStreamer userLogRecordStreamer) {
        this.dataChannels = Arrays.asList(new DefaultDataChannel(this.spaceSchema, (LogRecordStreamer)spaceLogRecordStreamer), new DefaultDataChannel(this.pageSchema, (LogRecordStreamer)pageLogRecordStreamer), new DefaultDataChannel(this.commentSchema, (LogRecordStreamer)commentLogRecordStreamer), new DefaultDataChannel(this.userSchema, (LogRecordStreamer)userLogRecordStreamer));
    }

    @Nonnull
    public String getDescription() {
        return DATASET_NAME;
    }

    public int getVersion() {
        return 1;
    }

    @Nonnull
    public List<DataChannel> getChannels() {
        return this.dataChannels;
    }
}

