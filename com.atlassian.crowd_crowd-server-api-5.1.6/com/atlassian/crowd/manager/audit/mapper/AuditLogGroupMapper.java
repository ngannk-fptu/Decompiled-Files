/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.crowd.audit.AuditLogEntry
 *  com.atlassian.crowd.model.group.Group
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.manager.audit.mapper;

import com.atlassian.annotations.Internal;
import com.atlassian.crowd.audit.AuditLogEntry;
import com.atlassian.crowd.model.group.Group;
import java.util.List;
import javax.annotation.Nullable;

@Internal
public interface AuditLogGroupMapper {
    public static final String DESCRIPTION_PROPERTY_KEY = "Description";
    public static final String NAME_PROPERTY_KEY = "Name";
    public static final String ACTIVE_PROPERTY_KEY = "Active";
    public static final String EXTERNAL_ID_PROPERTY_KEY = "External Id";
    public static final String LOCAL_PROPERTY_KEY = "Local";

    public List<AuditLogEntry> calculateDifference(@Nullable Group var1, @Nullable Group var2);
}

