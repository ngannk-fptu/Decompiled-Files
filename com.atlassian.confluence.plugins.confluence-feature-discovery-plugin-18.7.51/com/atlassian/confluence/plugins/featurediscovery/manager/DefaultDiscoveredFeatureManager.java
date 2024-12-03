/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.google.common.collect.ImmutableList
 *  net.java.ao.DBParam
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 */
package com.atlassian.confluence.plugins.featurediscovery.manager;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.plugins.featurediscovery.entity.DiscoveredFeatureAo;
import com.atlassian.confluence.plugins.featurediscovery.manager.DiscoveredFeatureManager;
import com.atlassian.confluence.plugins.featurediscovery.model.DiscoveredFeature;
import com.google.common.collect.ImmutableList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.java.ao.DBParam;
import net.java.ao.Query;
import net.java.ao.RawEntity;

public class DefaultDiscoveredFeatureManager
implements DiscoveredFeatureManager {
    private static final Function<DiscoveredFeatureAo, DiscoveredFeature> ENTITY_MAPPER = input -> new DiscoveredFeature(Objects.requireNonNull(input).getPluginKey(), input.getKey(), input.getUserKey(), input.getDate());
    private final ActiveObjects ao;

    public DefaultDiscoveredFeatureManager(ActiveObjects ao) {
        this.ao = ao;
    }

    @Override
    public DiscoveredFeature find(String pluginKey, String featureKey, String userKey) {
        DiscoveredFeatureAo entity = this.findEntity(pluginKey, featureKey, userKey);
        return entity != null ? ENTITY_MAPPER.apply(entity) : null;
    }

    @Override
    public DiscoveredFeature create(String pluginKey, String featureKey, String userKey, Date date) {
        DiscoveredFeatureAo newEntity = (DiscoveredFeatureAo)this.ao.create(DiscoveredFeatureAo.class, new DBParam[0]);
        newEntity.setPluginKey(pluginKey);
        newEntity.setKey(featureKey);
        newEntity.setUserKey(userKey);
        newEntity.setDate(date);
        newEntity.save();
        return ENTITY_MAPPER.apply(newEntity);
    }

    @Override
    public void delete(String pluginKey, String featureKey, String userKey) {
        DiscoveredFeatureAo entity = this.findEntity(pluginKey, featureKey, userKey);
        if (entity != null) {
            this.ao.delete(new RawEntity[]{entity});
        }
    }

    @Override
    public List<DiscoveredFeature> listForUser(String userKey) {
        Query query = Query.select().where("USER_KEY = ?", new Object[]{userKey});
        Object[] features = (DiscoveredFeatureAo[])this.ao.find(DiscoveredFeatureAo.class, query);
        return ImmutableList.copyOf((Object[])features).stream().map(ENTITY_MAPPER).collect(Collectors.toList());
    }

    private DiscoveredFeatureAo findEntity(String pluginKey, String featureKey, String userKey) {
        DiscoveredFeatureAo[] result = (DiscoveredFeatureAo[])this.ao.find(DiscoveredFeatureAo.class, Query.select().where("PLUGIN_KEY = ? AND KEY = ? AND USER_KEY = ?", new Object[]{pluginKey, featureKey, userKey}));
        return result.length > 0 ? result[0] : null;
    }
}

