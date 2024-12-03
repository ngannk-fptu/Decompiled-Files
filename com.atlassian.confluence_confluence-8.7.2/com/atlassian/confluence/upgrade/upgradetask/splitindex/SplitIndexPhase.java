/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade.upgradetask.splitindex;

public enum SplitIndexPhase {
    CREATE_CHANGE_FOLDER("creating folder /changes in index root"),
    COPY_FILES("copying index files to /changes"),
    PURGE_CONTENT_FROM_CHANGES("purging content from changes index"),
    PURGE_CHANGES_FROM_CONTENT("purging changes from context index"),
    DONE("completed successfully"),
    ABORTED("aborted due to errors"),
    REMOVE_OLD_INDEX("remove older format index");

    private final String description;

    private SplitIndexPhase(String description) {
        this.description = description;
    }

    public String getId() {
        return this.name();
    }

    public String getDescription() {
        return this.description;
    }

    public static SplitIndexPhase fromId(String id) {
        for (SplitIndexPhase phase : SplitIndexPhase.values()) {
            if (!phase.name().equalsIgnoreCase(id)) continue;
            return phase;
        }
        return null;
    }

    public String toString() {
        return this.name();
    }
}

