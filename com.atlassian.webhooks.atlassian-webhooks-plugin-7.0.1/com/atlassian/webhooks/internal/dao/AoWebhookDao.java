/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.webhooks.AbstractWebhookRequest
 *  com.atlassian.webhooks.WebhookCreateRequest
 *  com.atlassian.webhooks.WebhookEvent
 *  com.atlassian.webhooks.WebhookScope
 *  com.atlassian.webhooks.WebhookSearchRequest
 *  com.atlassian.webhooks.WebhookUpdateRequest
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  net.java.ao.Entity
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 */
package com.atlassian.webhooks.internal.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.webhooks.AbstractWebhookRequest;
import com.atlassian.webhooks.WebhookCreateRequest;
import com.atlassian.webhooks.WebhookEvent;
import com.atlassian.webhooks.WebhookScope;
import com.atlassian.webhooks.WebhookSearchRequest;
import com.atlassian.webhooks.WebhookUpdateRequest;
import com.atlassian.webhooks.internal.dao.WebhookDao;
import com.atlassian.webhooks.internal.dao.ao.AoWebhook;
import com.atlassian.webhooks.internal.dao.ao.AoWebhookConfigurationEntry;
import com.atlassian.webhooks.internal.dao.ao.AoWebhookEvent;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.java.ao.Entity;
import net.java.ao.Query;
import net.java.ao.RawEntity;

public class AoWebhookDao
implements WebhookDao {
    private static final Map<Class, String> AO_ALIAS_NAMES = ImmutableMap.of(AoWebhookEvent.class, (Object)"evt", AoWebhookConfigurationEntry.class, (Object)"config", AoWebhook.class, (Object)"webhook");
    private static final AoWebhook[] NO_RESULTS = new AoWebhook[0];
    private final ActiveObjects ao;

    public AoWebhookDao(ActiveObjects ao) {
        this.ao = ao;
    }

    @Override
    @Nonnull
    public AoWebhook create(@Nonnull WebhookCreateRequest request) {
        Objects.requireNonNull(request, "request");
        Date now = new Date();
        WebhookScope scope = request.getScope();
        ImmutableMap.Builder builder = ImmutableMap.builder().put((Object)"CREATED", (Object)now).put((Object)"UPDATED", (Object)now).put((Object)"URL", (Object)request.getUrl()).put((Object)"NAME", (Object)request.getName()).put((Object)"ACTIVE", (Object)request.isActive()).put((Object)"SCOPE_TYPE", (Object)scope.getType());
        scope.getId().ifPresent(id -> builder.put((Object)"SCOPE_ID", id));
        return (AoWebhook)this.ao.executeInTransaction(() -> {
            AoWebhook aoWebhook = (AoWebhook)this.ao.create(AoWebhook.class, (Map)builder.build());
            this.saveForeignObjects((AbstractWebhookRequest)request, aoWebhook.getID());
            return aoWebhook;
        });
    }

    @Override
    public boolean delete(int id) {
        return (Boolean)this.ao.executeInTransaction(() -> {
            AoWebhook aoWebhook = (AoWebhook)this.ao.get(AoWebhook.class, (Object)id);
            if (aoWebhook == null) {
                return false;
            }
            this.ao.delete((RawEntity[])aoWebhook.getEvents());
            this.ao.delete((RawEntity[])aoWebhook.getConfiguration());
            this.ao.delete(new RawEntity[]{aoWebhook});
            return true;
        });
    }

    @Override
    public void delete(AoWebhook[] webhooks) {
        Objects.requireNonNull(webhooks, "webhooks");
        if (webhooks.length == 0) {
            return;
        }
        this.ao.executeInTransaction(() -> {
            List ids = Arrays.stream(webhooks).map(Entity::getID).collect(Collectors.toList());
            String idParams = Collections.nCopies(webhooks.length, "?").stream().collect(Collectors.joining(","));
            this.ao.deleteWithSQL(AoWebhookEvent.class, "WEBHOOKID IN (" + idParams + ")", ids.toArray());
            this.ao.deleteWithSQL(AoWebhookConfigurationEntry.class, "WEBHOOKID IN (" + idParams + ")", ids.toArray());
            this.ao.delete((RawEntity[])webhooks);
            return null;
        });
    }

    @Override
    @Nullable
    public AoWebhook getById(int id) {
        return (AoWebhook)this.ao.get(AoWebhook.class, (Object)id);
    }

    @Override
    @Nonnull
    public AoWebhook[] search(@Nonnull WebhookSearchRequest search) {
        return (AoWebhook[])this.ao.executeInTransaction(() -> {
            Objects.requireNonNull(search, "search");
            Query query = Query.select((String)"ID");
            ArrayList<String> whereJoinedByAnd = new ArrayList<String>();
            ArrayList<Object> params = new ArrayList<Object>();
            AO_ALIAS_NAMES.forEach((arg_0, arg_1) -> ((Query)query).alias(arg_0, arg_1));
            if (search.getId() != null) {
                whereJoinedByAnd.add("ID = ?");
                params.add(search.getId());
            }
            if (!search.getEvents().isEmpty()) {
                this.addEventBasedQuery(search, query, whereJoinedByAnd, params);
            }
            if (!search.getScopes().isEmpty() || !search.getScopeTypes().isEmpty()) {
                this.addScopeBasedQuery(search, whereJoinedByAnd, params);
            }
            if (search.getName() != null) {
                whereJoinedByAnd.add(this.getTableName(AoWebhook.class) + '.' + "NAME" + " = ?");
                params.add(search.getName());
            }
            if (search.getActive() != null) {
                whereJoinedByAnd.add(this.getTableName(AoWebhook.class) + '.' + "ACTIVE" + " = ?");
                params.add(search.getActive());
            }
            if (!whereJoinedByAnd.isEmpty()) {
                query.where(String.join((CharSequence)" AND ", whereJoinedByAnd), params.toArray());
            }
            query.limit(search.getLimit());
            query.offset(search.getOffset());
            query.distinct();
            query.order("ID ASC");
            ArrayList ids = new ArrayList();
            this.ao.stream(AoWebhook.class, query, aoWebhook -> ids.add(aoWebhook.getID()));
            if (!ids.isEmpty()) {
                String idParams = Collections.nCopies(ids.size(), "?").stream().collect(Collectors.joining(","));
                Query inQuery = Query.select();
                AO_ALIAS_NAMES.forEach((arg_0, arg_1) -> ((Query)inQuery).alias(arg_0, arg_1));
                return (AoWebhook[])this.ao.find(AoWebhook.class, inQuery.where(this.getTableName(AoWebhook.class) + ".ID IN (" + idParams + ")", ids.toArray()));
            }
            return NO_RESULTS;
        });
    }

    @Override
    public AoWebhook update(int id, @Nonnull WebhookUpdateRequest request) {
        Objects.requireNonNull(request, "request");
        return (AoWebhook)this.ao.executeInTransaction(() -> {
            AoWebhook aoWebhook = (AoWebhook)this.ao.get(AoWebhook.class, (Object)id);
            if (aoWebhook == null) {
                return null;
            }
            aoWebhook.setName(request.getName());
            aoWebhook.setUrl(request.getUrl());
            aoWebhook.setActive(request.isActive());
            WebhookScope scope = request.getScope();
            aoWebhook.setScopeId(scope.getId().orElse(null));
            aoWebhook.setScopeType(scope.getType());
            aoWebhook.setUpdatedDate(new Date());
            this.ao.delete((RawEntity[])aoWebhook.getConfiguration());
            this.ao.delete((RawEntity[])aoWebhook.getEvents());
            this.saveForeignObjects((AbstractWebhookRequest)request, aoWebhook.getID());
            aoWebhook.save();
            return (AoWebhook)this.ao.get(AoWebhook.class, (Object)id);
        });
    }

    private void addEventBasedQuery(WebhookSearchRequest search, Query query, List<String> where, List<Object> params) {
        query.join(AoWebhookEvent.class, this.getTableName(AoWebhookEvent.class) + '.' + "WEBHOOKID" + " = " + this.getTableName(AoWebhook.class) + ".ID");
        String whereQuery = this.group(search.getEvents().stream().map(event -> this.getTableName(AoWebhookEvent.class) + '.' + "EVENT_ID" + " = ?").collect(Collectors.joining(" OR ")));
        where.add(whereQuery);
        search.getEvents().stream().map(WebhookEvent::getId).forEach(params::add);
    }

    private void addScopeBasedQuery(WebhookSearchRequest search, List<String> where, List<Object> params) {
        if (!search.getScopes().isEmpty()) {
            this.addScopeQuery(search, where, params);
        } else {
            this.addScopeTypeQuery(search, where, params);
        }
    }

    private void addScopeTypeQuery(WebhookSearchRequest search, List<String> where, List<Object> params) {
        where.add(this.group(search.getScopeTypes().stream().map(type -> "SCOPE_TYPE = ?").collect(Collectors.joining(" OR "))));
        params.addAll(search.getScopeTypes());
    }

    private void addScopeQuery(WebhookSearchRequest search, List<String> where, List<Object> params) {
        where.add(this.group(search.getScopes().stream().map(scope -> {
            if (scope.getId().isPresent()) {
                String whereQuery = this.group("SCOPE_TYPE = ? AND SCOPE_ID = ? ");
                params.add(scope.getType());
                params.add(scope.getId().get());
                return whereQuery;
            }
            String whereQuery = this.group("SCOPE_TYPE = ? AND SCOPE_ID IS NULL ");
            params.add(scope.getType());
            return whereQuery;
        }).collect(Collectors.joining(" OR "))));
    }

    private String getTableName(Class clazz) {
        return AO_ALIAS_NAMES.get(clazz);
    }

    private String group(String query) {
        return "(" + query + ")";
    }

    private void saveForeignObjects(AbstractWebhookRequest request, int webhookId) {
        request.getConfiguration().forEach((key, value) -> {
            AoWebhookConfigurationEntry cfr_ignored_0 = (AoWebhookConfigurationEntry)this.ao.create(AoWebhookConfigurationEntry.class, (Map)ImmutableMap.of((Object)"KEY", (Object)key, (Object)"WEBHOOKID", (Object)webhookId, (Object)"VALUE", (Object)value));
        });
        request.getEvents().forEach(event -> {
            AoWebhookEvent cfr_ignored_0 = (AoWebhookEvent)this.ao.create(AoWebhookEvent.class, (Map)ImmutableMap.of((Object)"EVENT_ID", (Object)event.getId(), (Object)"WEBHOOKID", (Object)webhookId));
        });
    }
}

