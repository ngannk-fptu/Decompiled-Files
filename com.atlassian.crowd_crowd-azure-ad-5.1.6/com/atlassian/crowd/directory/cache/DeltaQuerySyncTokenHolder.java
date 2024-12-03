/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.mapper.SimpleObjectMapper
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.directory.cache;

import com.atlassian.crowd.mapper.SimpleObjectMapper;
import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeltaQuerySyncTokenHolder {
    private static final Logger log = LoggerFactory.getLogger(DeltaQuerySyncTokenHolder.class);
    private static final SimpleObjectMapper<DeltaQuerySyncTokenHolder> OBJECT_MAPPER = new SimpleObjectMapper(DeltaQuerySyncTokenHolder.class);
    @Nullable
    private final String usersDeltaQuerySyncToken;
    @Nullable
    private final String groupsDeltaQuerySyncToken;

    private DeltaQuerySyncTokenHolder() {
        this(null, null);
    }

    public DeltaQuerySyncTokenHolder(String usersDeltaQuerySyncToken, String groupsDeltaQuerySyncToken) {
        this.usersDeltaQuerySyncToken = usersDeltaQuerySyncToken;
        this.groupsDeltaQuerySyncToken = groupsDeltaQuerySyncToken;
    }

    public String getUsersDeltaQuerySyncToken() {
        return this.usersDeltaQuerySyncToken;
    }

    public String getGroupsDeltaQuerySyncToken() {
        return this.groupsDeltaQuerySyncToken;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DeltaQuerySyncTokenHolder that = (DeltaQuerySyncTokenHolder)o;
        return Objects.equals(this.usersDeltaQuerySyncToken, that.usersDeltaQuerySyncToken) && Objects.equals(this.groupsDeltaQuerySyncToken, that.groupsDeltaQuerySyncToken);
    }

    public int hashCode() {
        return Objects.hash(this.usersDeltaQuerySyncToken, this.groupsDeltaQuerySyncToken);
    }

    public String serialize() {
        try {
            return OBJECT_MAPPER.serialize((Object)this);
        }
        catch (IOException e) {
            log.warn("Cannot serialize synchronisation token obtained from Azure AD. Users sync token: '{}', groups sync token: '{}'", new Object[]{this.usersDeltaQuerySyncToken, this.groupsDeltaQuerySyncToken, e});
            return null;
        }
    }

    public static DeltaQuerySyncTokenHolder deserialize(@Nullable String syncToken) {
        try {
            return (DeltaQuerySyncTokenHolder)OBJECT_MAPPER.deserialize(syncToken);
        }
        catch (IOException e) {
            log.warn("Cannot perform incremental synchronisation for directory [{}] due to a malformed synchronisation token", (Object)syncToken);
            throw new RuntimeException(e);
        }
    }
}

