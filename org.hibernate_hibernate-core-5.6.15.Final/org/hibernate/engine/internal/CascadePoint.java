/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.internal;

public enum CascadePoint {
    AFTER_INSERT_BEFORE_DELETE,
    BEFORE_INSERT_AFTER_DELETE,
    AFTER_INSERT_BEFORE_DELETE_VIA_COLLECTION,
    AFTER_UPDATE,
    BEFORE_FLUSH,
    AFTER_EVICT,
    BEFORE_REFRESH,
    AFTER_LOCK,
    BEFORE_MERGE;

}

