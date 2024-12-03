/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.search.queue;

import com.atlassian.annotations.Internal;
import java.util.Optional;
import java.util.stream.Stream;

@Internal
public enum JournalEntryType {
    UNINDEX_SPACE,
    UNINDEX_SPACE_CHANGE,
    DELETE_DOCUMENT,
    UPDATE_DOCUMENT,
    UPDATE_DOCUMENT_EXCLUDING_DEPENDENTS,
    ADD_DOCUMENT,
    DELETE_CHANGE_DOCUMENTS,
    ADD_CHANGE_DOCUMENT,
    REBUILD_CHANGE_DOCUMENTS,
    REINDEX_ALL_USERS,
    REINDEX_ALL_USERS_CHANGE,
    REINDEX_USERS_IN_GROUP,
    REINDEX_USERS_IN_GROUP_CHANGE,
    REINDEX_ALL_SPACES,
    REINDEX_ALL_SPACES_CHANGE,
    UNINDEX_CONTENT_TYPE,
    UNINDEX_CONTENT_TYPE_CHANGE,
    REINDEX_ALL_BLOGS,
    REINDEX_ALL_BLOGS_CHANGE,
    INDEX_DRAFTS;


    public static Optional<JournalEntryType> optionalFromId(String name) {
        return Stream.of(JournalEntryType.values()).filter(v -> v.name().equals(name)).findAny();
    }
}

