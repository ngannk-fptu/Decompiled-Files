/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.index.api.mapping.LongFieldMapping
 *  com.atlassian.confluence.plugins.index.api.mapping.StringFieldMapping
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.plugins.index.api.mapping.LongFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.StringFieldMapping;

public final class EdgeIndexFieldMappings {
    public static final StringFieldMapping EDGE_TARGET_ID = StringFieldMapping.builder((String)"edge.targetId").store(true).build();
    public static final StringFieldMapping EDGE_USERKEY = StringFieldMapping.builder((String)"edge.userKey").store(true).build();
    public static final StringFieldMapping EDGE_TYPE = StringFieldMapping.builder((String)"edge.type").store(true).build();
    public static final StringFieldMapping EDGE_ID = StringFieldMapping.builder((String)"edge.id").store(true).build();
    public static final StringFieldMapping EDGE_TARGET_TYPE = StringFieldMapping.builder((String)"edge.targetType").store(true).build();
    public static final LongFieldMapping EDGE_DATE_FIELD = LongFieldMapping.builder((String)"edge.date").store(true).build();
    public static final StringFieldMapping EDGE_TARGET_AUTHOR = StringFieldMapping.builder((String)"edge.targetAuthor").store(true).build();
}

