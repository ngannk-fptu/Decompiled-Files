/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.enhance.spi;

public final class EnhancerConstants {
    public static final String PERSISTENT_FIELD_READER_PREFIX = "$$_hibernate_read_";
    public static final String PERSISTENT_FIELD_WRITER_PREFIX = "$$_hibernate_write_";
    public static final String ENTITY_INSTANCE_GETTER_NAME = "$$_hibernate_getEntityInstance";
    public static final String ENTITY_ENTRY_FIELD_NAME = "$$_hibernate_entityEntryHolder";
    public static final String ENTITY_ENTRY_GETTER_NAME = "$$_hibernate_getEntityEntry";
    public static final String ENTITY_ENTRY_SETTER_NAME = "$$_hibernate_setEntityEntry";
    public static final String PREVIOUS_FIELD_NAME = "$$_hibernate_previousManagedEntity";
    public static final String PREVIOUS_GETTER_NAME = "$$_hibernate_getPreviousManagedEntity";
    public static final String PREVIOUS_SETTER_NAME = "$$_hibernate_setPreviousManagedEntity";
    public static final String NEXT_FIELD_NAME = "$$_hibernate_nextManagedEntity";
    public static final String NEXT_GETTER_NAME = "$$_hibernate_getNextManagedEntity";
    public static final String NEXT_SETTER_NAME = "$$_hibernate_setNextManagedEntity";
    public static final String INTERCEPTOR_FIELD_NAME = "$$_hibernate_attributeInterceptor";
    public static final String INTERCEPTOR_GETTER_NAME = "$$_hibernate_getInterceptor";
    public static final String INTERCEPTOR_SETTER_NAME = "$$_hibernate_setInterceptor";
    public static final String TRACKER_FIELD_NAME = "$$_hibernate_tracker";
    public static final String TRACKER_CHANGER_NAME = "$$_hibernate_trackChange";
    public static final String TRACKER_HAS_CHANGED_NAME = "$$_hibernate_hasDirtyAttributes";
    public static final String TRACKER_GET_NAME = "$$_hibernate_getDirtyAttributes";
    public static final String TRACKER_CLEAR_NAME = "$$_hibernate_clearDirtyAttributes";
    public static final String TRACKER_SUSPEND_NAME = "$$_hibernate_suspendDirtyTracking";
    public static final String TRACKER_COLLECTION_GET_NAME = "$$_hibernate_getCollectionTracker";
    public static final String TRACKER_COLLECTION_CHANGED_NAME = "$$_hibernate_areCollectionFieldsDirty";
    public static final String TRACKER_COLLECTION_NAME = "$$_hibernate_collectionTracker";
    public static final String TRACKER_COLLECTION_CHANGED_FIELD_NAME = "$$_hibernate_getCollectionFieldDirtyNames";
    public static final String TRACKER_COLLECTION_CLEAR_NAME = "$$_hibernate_clearDirtyCollectionNames";
    public static final String TRACKER_COMPOSITE_FIELD_NAME = "$$_hibernate_compositeOwners";
    public static final String TRACKER_COMPOSITE_SET_OWNER = "$$_hibernate_setOwner";
    public static final String TRACKER_COMPOSITE_CLEAR_OWNER = "$$_hibernate_clearOwner";

    private EnhancerConstants() {
    }
}

