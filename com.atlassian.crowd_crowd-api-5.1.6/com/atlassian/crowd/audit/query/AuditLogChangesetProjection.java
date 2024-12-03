/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.crowd.audit.query;

import com.atlassian.annotations.ExperimentalApi;

@ExperimentalApi
public enum AuditLogChangesetProjection {
    EVENT_TYPE,
    AUTHOR,
    ENTITY_USER,
    ENTITY_GROUP,
    ENTITY_APPLICATION,
    ENTITY_DIRECTORY,
    SOURCE;

}

