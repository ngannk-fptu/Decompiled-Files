/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.oauth2.provider.api.client.dao.RedirectUriDao
 *  javax.annotation.Nonnull
 *  net.java.ao.DBParam
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.core.client.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.oauth2.provider.api.client.dao.RedirectUriDao;
import com.atlassian.oauth2.provider.core.client.dao.entity.AORedirect;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.java.ao.DBParam;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRedirectUriDao
implements RedirectUriDao {
    private static final Logger logger = LoggerFactory.getLogger(DefaultRedirectUriDao.class);
    private static final String WHERE_CLIENT_ID_EQUAL_TO = "CLIENT_ID = ?";
    private final ActiveObjects activeObjects;

    public DefaultRedirectUriDao(ActiveObjects activeObjects) {
        this.activeObjects = activeObjects;
    }

    public void create(@Nonnull String clientId, List<String> redirectUris) {
        redirectUris.stream().distinct().forEach(redirect -> this.createRedirect(clientId, (String)redirect));
    }

    private AORedirect createRedirect(String clientId, String redirectUri) {
        logger.debug("Creating redirect [{}] for client ID [{}]", (Object)redirectUri, (Object)clientId);
        return (AORedirect)this.activeObjects.create(AORedirect.class, new DBParam[]{new DBParam("URI", (Object)redirectUri), new DBParam("CLIENT_ID", (Object)clientId)});
    }

    public List<String> findByClientId(@Nonnull String clientId) {
        return Arrays.stream(this.activeObjects.find(AORedirect.class, Query.select().where(WHERE_CLIENT_ID_EQUAL_TO, new Object[]{clientId}).order("URI ASC"))).map(AORedirect::getUri).collect(Collectors.toList());
    }

    public void updateRedirectUris(@Nonnull String clientId, @Nonnull List<String> newRedirectUris) {
        Map oldRedirectUris = Arrays.stream(this.activeObjects.find(AORedirect.class, Query.select().where(WHERE_CLIENT_ID_EQUAL_TO, new Object[]{clientId}))).collect(Collectors.toMap(AORedirect::getUri, Function.identity()));
        for (String redirectUri : newRedirectUris) {
            if (oldRedirectUris.containsKey(redirectUri)) {
                oldRedirectUris.remove(redirectUri);
                continue;
            }
            logger.debug("Updating redirect [{}] associated with client ID [{}]", (Object)redirectUri, (Object)clientId);
            this.createRedirect(clientId, redirectUri);
        }
        if (!oldRedirectUris.isEmpty()) {
            logger.debug("Removing redirects [{}] associated with client ID [{}]", oldRedirectUris.values(), (Object)clientId);
            this.activeObjects.delete((RawEntity[])oldRedirectUris.values().toArray(new AORedirect[0]));
        }
    }

    public void removeByClientId(@NotNull String clientId) {
        logger.debug("Removing client by client ID [{}]", (Object)clientId);
        this.activeObjects.deleteWithSQL(AORedirect.class, WHERE_CLIENT_ID_EQUAL_TO, new Object[]{clientId});
    }
}

