/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.type.TypeReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.model.directory;

import com.google.common.base.Strings;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum SynchronisationStatusKey {
    STARTED("directory.caching.sync.started"),
    INCREMENTAL("directory.caching.sync.incremental"),
    FULL("directory.caching.sync.full"),
    ADDING_USERS("directory.caching.sync.adding.users"),
    UPDATING_USERS("directory.caching.sync.updating.users"),
    DELETING_USERS("directory.caching.sync.deleting.users"),
    USER_MEMBERSHIPS("directory.caching.sync.user.memberships"),
    ADDING_GROUPS("directory.caching.sync.adding.groups"),
    UPDATING_GROUPS("directory.caching.sync.updating.groups"),
    DELETING_GROUPS("directory.caching.sync.deleting.groups"),
    GROUP_MEMBERSHIPS("directory.caching.sync.group.memberships"),
    SUCCESS_INCREMENTAL("directory.caching.sync.completed.INCREMENTAL"),
    SUCCESS_FULL("directory.caching.sync.completed.FULL"),
    FAILURE("directory.caching.sync.completed.error"),
    ABORTED("directory.caching.sync.completed.aborted");

    private static final Logger logger;
    private String i18Key;

    private SynchronisationStatusKey(String i18Key) {
        this.i18Key = i18Key;
    }

    public static Optional<SynchronisationStatusKey> fromKey(String key) {
        return Stream.of(SynchronisationStatusKey.values()).filter(k -> k.getI18Key().equals(key)).findFirst();
    }

    public String getI18Key() {
        return this.i18Key;
    }

    public List<Serializable> unmarshallParams(String marshalledParams) {
        if (Strings.nullToEmpty((String)marshalledParams).isEmpty()) {
            return new ArrayList<Serializable>();
        }
        try {
            return (List)new ObjectMapper().readValue(marshalledParams, (TypeReference)new TypeReference<List<String>>(){});
        }
        catch (IOException ignored) {
            logger.info("Could not unmarshall synchronisation parameters for status {}, parameters: {}", (Object)this, (Object)marshalledParams);
            return Collections.emptyList();
        }
    }

    public String marshallParams(List<Serializable> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        try {
            return new ObjectMapper().writeValueAsString(params);
        }
        catch (IOException ignored) {
            logger.info("Could not marshall synchronisation parameters for status {}, parameters: {}", (Object)this, params);
            return "";
        }
    }

    public boolean isFinal() {
        return SUCCESS_INCREMENTAL.equals((Object)this) || SUCCESS_FULL.equals((Object)this) || FAILURE.equals((Object)this) || ABORTED.equals((Object)this);
    }

    static {
        logger = LoggerFactory.getLogger(SynchronisationStatusKey.class);
    }
}

