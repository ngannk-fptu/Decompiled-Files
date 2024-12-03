/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.statistics;

public enum SkippedObjectsReason {
    INVALID_FIELDS,
    PERSISTER_NOT_FOUND,
    PARENT_WAS_NOT_PERSISTED,
    NOT_SATISFIED_DEPENDENCIES,
    SPACE_IS_NOT_ALLOWED,
    PARENT_NO_LONGER_EXISTS;

}

