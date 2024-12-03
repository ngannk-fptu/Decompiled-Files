/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.oauth2.client.api.ClientTokenMetadata$ClientTokenStatus
 *  com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity
 *  com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity$Builder
 *  com.atlassian.oauth2.client.api.storage.token.exception.TokenNotFoundException
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 */
package com.atlassian.oauth2.client.storage.token.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.oauth2.client.api.ClientTokenMetadata;
import com.atlassian.oauth2.client.api.storage.token.ClientTokenEntity;
import com.atlassian.oauth2.client.api.storage.token.exception.TokenNotFoundException;
import com.atlassian.oauth2.client.storage.AbstractStore;
import com.atlassian.oauth2.client.storage.token.dao.ClientTokenStore;
import com.atlassian.oauth2.client.storage.token.dao.entity.AOClientToken;
import com.atlassian.oauth2.common.IdGenerator;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.java.ao.Query;
import net.java.ao.RawEntity;

public class ClientTokenStoreImpl
extends AbstractStore
implements ClientTokenStore {
    private final IdGenerator idGenerator;

    public ClientTokenStoreImpl(ActiveObjects activeObjects, IdGenerator idGenerator) {
        super(activeObjects);
        this.idGenerator = idGenerator;
    }

    @Override
    @Nonnull
    public ClientTokenEntity create(ClientTokenEntity clientTokenEntity) {
        String newId = this.idGenerator.generate();
        HashMap<String, Object> tokenProperties = new HashMap<String, Object>();
        tokenProperties.put("ID", newId);
        tokenProperties.put("CONFIG_ID", clientTokenEntity.getConfigId());
        tokenProperties.put("ACCESS_TOKEN", clientTokenEntity.getAccessToken());
        tokenProperties.put("ACCESS_TOKEN_EXPIRATION", clientTokenEntity.getAccessTokenExpiration().toEpochMilli());
        tokenProperties.put("REFRESH_TOKEN", clientTokenEntity.getRefreshToken());
        tokenProperties.put("REFRESH_TOKEN_EXPIRATION", this.toEpochMilli(clientTokenEntity.getRefreshTokenExpiration()));
        tokenProperties.put("STATUS", clientTokenEntity.getStatus().name());
        tokenProperties.put("REFRESH_COUNT", clientTokenEntity.getRefreshCount());
        tokenProperties.put("LAST_REFRESHED", this.toEpochMilli(clientTokenEntity.getLastRefreshed()));
        tokenProperties.put("LAST_STATUS_UPDATED", this.toEpochMilli(clientTokenEntity.getLastStatusUpdated()));
        this.activeObjects.executeInTransaction(() -> (AOClientToken)this.activeObjects.create(AOClientToken.class, tokenProperties));
        return this.truncatedToMillis(clientTokenEntity).id(newId).build();
    }

    @Override
    @Nonnull
    public ClientTokenEntity update(@Nonnull ClientTokenEntity clientTokenEntity) throws TokenNotFoundException {
        return this.executeInTransaction(() -> {
            AOClientToken aoClientToken = this.findByIdOrFail(clientTokenEntity.getId());
            this.updateClientToken(aoClientToken, clientTokenEntity);
            return this.truncatedToMillis(clientTokenEntity).build();
        }, TokenNotFoundException.class);
    }

    @Override
    public void delete(@Nonnull String id) throws TokenNotFoundException {
        this.executeInTransaction(() -> {
            AOClientToken token = this.findByIdOrFail(id);
            this.activeObjects.delete(new RawEntity[]{token});
            return null;
        }, TokenNotFoundException.class);
    }

    @Override
    public List<String> deleteWithConfigId(@Nonnull String configId) {
        return (List)this.activeObjects.executeInTransaction(() -> {
            Query where = this.selectQuery().where("CONFIG_ID = ?", new Object[]{configId});
            return this.deleteTokens(where);
        });
    }

    @Override
    public List<String> deleteTokensExpiringBefore(@Nonnull Instant timestamp) {
        return (List)this.activeObjects.executeInTransaction(() -> this.deleteTokens(this.selectQuery().where("REFRESH_TOKEN_EXPIRATION < ? AND ACCESS_TOKEN_EXPIRATION < ?", new Object[]{timestamp.toEpochMilli(), timestamp.toEpochMilli()})));
    }

    @Override
    public List<String> deleteTokensUnrecoverableSince(@Nonnull Instant timestamp) {
        return (List)this.activeObjects.executeInTransaction(() -> this.deleteTokens(this.selectQuery().where("STATUS = ? AND LAST_STATUS_UPDATED < ?", new Object[]{ClientTokenMetadata.ClientTokenStatus.UNRECOVERABLE, timestamp.toEpochMilli()})));
    }

    private List<String> deleteTokens(Query whereClause) {
        RawEntity[] aoClientTokens = (AOClientToken[])this.activeObjects.find(AOClientToken.class, whereClause);
        this.activeObjects.delete(aoClientTokens);
        return Arrays.stream(aoClientTokens).map(AOClientToken::getId).collect(Collectors.toList());
    }

    @Override
    @Nullable
    public ClientTokenEntity getById(@Nonnull String id) {
        return ((Optional)this.activeObjects.executeInTransaction(() -> this.findById(id))).map(this::toEntity).orElse(null);
    }

    @Override
    @Nonnull
    public ClientTokenEntity getByIdOrFail(@Nonnull String id) throws TokenNotFoundException {
        return this.executeInTransaction(() -> this.toEntity(this.findByIdOrFail(id)), TokenNotFoundException.class);
    }

    private AOClientToken findByIdOrFail(@Nonnull String id) throws TokenNotFoundException {
        return this.findById(id).orElseThrow(() -> new TokenNotFoundException("Token {" + id + "} does not exist"));
    }

    private Optional<AOClientToken> findById(@Nonnull String id) {
        return Optional.ofNullable(this.activeObjects.get(AOClientToken.class, (Object)id));
    }

    private void updateClientToken(AOClientToken aoClientToken, ClientTokenEntity clientTokenEntity) {
        aoClientToken.setConfigId(clientTokenEntity.getConfigId());
        aoClientToken.setAccessToken(clientTokenEntity.getAccessToken());
        aoClientToken.setAccessTokenExpiration(clientTokenEntity.getAccessTokenExpiration().toEpochMilli());
        aoClientToken.setRefreshToken(clientTokenEntity.getRefreshToken());
        aoClientToken.setRefreshTokenExpiration(this.toEpochMilli(clientTokenEntity.getRefreshTokenExpiration()));
        aoClientToken.setStatus(clientTokenEntity.getStatus().name());
        aoClientToken.setLastRefreshed(this.toEpochMilli(clientTokenEntity.getLastRefreshed()));
        aoClientToken.setRefreshCount(clientTokenEntity.getRefreshCount());
        aoClientToken.setLastStatusUpdated(this.toEpochMilli(clientTokenEntity.getLastStatusUpdated()));
        aoClientToken.save();
    }

    @Override
    @Nonnull
    public List<ClientTokenEntity> getAccessTokensExpiringBefore(@Nonnull Instant timestamp) {
        return (List)this.activeObjects.executeInTransaction(() -> this.fetchResults(this.selectQuery().where("ACCESS_TOKEN_EXPIRATION < ?", new Object[]{timestamp.toEpochMilli()})));
    }

    @Override
    @Nonnull
    public List<ClientTokenEntity> getRefreshTokensExpiringBefore(@Nonnull Instant timestamp) {
        return (List)this.activeObjects.executeInTransaction(() -> this.fetchResults(this.selectQuery().where("REFRESH_TOKEN_EXPIRATION < ?", new Object[]{timestamp.toEpochMilli()})));
    }

    private List<ClientTokenEntity> fetchResults(Query whereClause) {
        return Arrays.stream(this.activeObjects.find(AOClientToken.class, whereClause)).map(this::toEntity).collect(Collectors.toList());
    }

    @Override
    @Nonnull
    public List<ClientTokenEntity> list() {
        return (List)this.activeObjects.executeInTransaction(() -> Arrays.stream(this.activeObjects.find(AOClientToken.class, Query.select().order("CONFIG_ID, ID ASC"))).map(this::toEntity).collect(Collectors.toList()));
    }

    private Query selectQuery() {
        return Query.select((String)String.join((CharSequence)", ", "ID", "CONFIG_ID", "ACCESS_TOKEN", "ACCESS_TOKEN_EXPIRATION", "REFRESH_TOKEN", "STATUS", "LAST_REFRESHED", "REFRESH_COUNT", "LAST_STATUS_UPDATED"));
    }

    private ClientTokenEntity toEntity(AOClientToken aoClientToken) {
        return ClientTokenEntity.builder().id(aoClientToken.getId()).configId(aoClientToken.getConfigId()).accessToken(aoClientToken.getAccessToken()).accessTokenExpiration(this.toInstant(aoClientToken.getAccessTokenExpiration())).refreshToken(aoClientToken.getRefreshToken()).refreshTokenExpiration(this.fromEpochMilli(aoClientToken.getRefreshTokenExpiration())).status(ClientTokenMetadata.ClientTokenStatus.valueOf((String)aoClientToken.getStatus())).lastRefreshed(this.fromEpochMilli(aoClientToken.getLastRefreshed())).lastStatusUpdated(this.fromEpochMilli(aoClientToken.getLastStatusUpdated())).refreshCount(aoClientToken.getRefreshCount()).build();
    }

    private Instant toInstant(long timestamp) {
        return Instant.ofEpochMilli(timestamp);
    }

    private ClientTokenEntity.Builder truncatedToMillis(ClientTokenEntity clientTokenEntity) {
        return ClientTokenEntity.builder((ClientTokenEntity)clientTokenEntity).accessTokenExpiration(clientTokenEntity.getAccessTokenExpiration().truncatedTo(ChronoUnit.MILLIS)).refreshTokenExpiration((Instant)Optional.ofNullable(clientTokenEntity.getRefreshTokenExpiration()).map(date -> date.truncatedTo(ChronoUnit.MILLIS)).orElse(null));
    }

    private Long toEpochMilli(@Nullable Instant instant) {
        return instant == null ? null : Long.valueOf(instant.toEpochMilli());
    }

    private Instant fromEpochMilli(@Nullable Long epochMillis) {
        return epochMillis == null ? null : Instant.ofEpochMilli(epochMillis);
    }
}

