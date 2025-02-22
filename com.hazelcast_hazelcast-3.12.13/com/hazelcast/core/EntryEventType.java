/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

public enum EntryEventType {
    ADDED(1),
    REMOVED(2),
    UPDATED(4),
    EVICTED(8),
    EVICT_ALL(16),
    CLEAR_ALL(32),
    MERGED(64),
    EXPIRED(128),
    INVALIDATION(256),
    LOADED(512);

    private int typeId;

    private EntryEventType(int typeId) {
        this.typeId = typeId;
    }

    public int getType() {
        return this.typeId;
    }

    public static EntryEventType getByType(int typeId) {
        switch (typeId) {
            case 1: {
                return ADDED;
            }
            case 2: {
                return REMOVED;
            }
            case 4: {
                return UPDATED;
            }
            case 8: {
                return EVICTED;
            }
            case 16: {
                return EVICT_ALL;
            }
            case 32: {
                return CLEAR_ALL;
            }
            case 64: {
                return MERGED;
            }
            case 128: {
                return EXPIRED;
            }
            case 256: {
                return INVALIDATION;
            }
            case 512: {
                return LOADED;
            }
        }
        return null;
    }

    private static class TypeId {
        private static final int ADDED = 1;
        private static final int REMOVED = 2;
        private static final int UPDATED = 4;
        private static final int EVICTED = 8;
        private static final int EVICT_ALL = 16;
        private static final int CLEAR_ALL = 32;
        private static final int MERGED = 64;
        private static final int EXPIRED = 128;
        private static final int INVALIDATION = 256;
        private static final int LOADED = 512;

        private TypeId() {
        }
    }
}

