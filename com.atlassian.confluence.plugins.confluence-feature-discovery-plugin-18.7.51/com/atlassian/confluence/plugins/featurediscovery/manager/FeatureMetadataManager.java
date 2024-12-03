/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.google.common.collect.Maps
 *  net.java.ao.DBParam
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.featurediscovery.manager;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.confluence.plugins.featurediscovery.FeatureCompleteKey;
import com.atlassian.confluence.plugins.featurediscovery.FeatureMetadata;
import com.atlassian.confluence.plugins.featurediscovery.entity.FeatureMetadataAo;
import com.atlassian.plugin.ModuleCompleteKey;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import net.java.ao.DBParam;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeatureMetadataManager {
    private static final Logger log = LoggerFactory.getLogger(FeatureMetadataManager.class);
    private final ActiveObjects ao;
    static final int BATCH_SIZE = 50;

    public FeatureMetadataManager(ActiveObjects ao) {
        this.ao = ao;
    }

    public boolean hasData() {
        return (Boolean)this.ao.executeInTransaction(() -> {
            FeatureMetadataAo[] featureMetadatas = (FeatureMetadataAo[])this.ao.find(FeatureMetadataAo.class, Query.select().limit(1));
            return featureMetadatas.length > 0;
        });
    }

    public FeatureMetadata save(FeatureCompleteKey featureCompleteKey, Date installationDate) {
        return new FeatureMetadata((FeatureMetadataAo)this.ao.executeInTransaction(() -> {
            FeatureMetadataAo existingFeatureMetadata = this.find(featureCompleteKey);
            if (existingFeatureMetadata != null) {
                return existingFeatureMetadata;
            }
            FeatureMetadataAo featureMetadataAo = (FeatureMetadataAo)this.ao.create(FeatureMetadataAo.class, new DBParam[0]);
            featureMetadataAo.setContext(featureCompleteKey.getContext());
            featureMetadataAo.setKey(featureCompleteKey.getKey());
            featureMetadataAo.setInstallationDate(installationDate);
            featureMetadataAo.save();
            return featureMetadataAo;
        }));
    }

    public Date getInstallationDate(FeatureCompleteKey featureCompleteKey) {
        String key;
        String context = featureCompleteKey.getContext();
        FeatureMetadataAo featureMetadataEntity = (FeatureMetadataAo)this.ao.executeInTransaction(() -> this.lambda$getInstallationDate$2(context, key = featureCompleteKey.getKey()));
        if (featureMetadataEntity == null) {
            return null;
        }
        return featureMetadataEntity.getInstallationDate();
    }

    public boolean delete(FeatureCompleteKey featureCompleteKey) {
        return (Boolean)this.ao.executeInTransaction(() -> {
            FeatureMetadataAo featureMetadata = this.find(featureCompleteKey);
            if (featureMetadata == null) {
                return false;
            }
            this.ao.delete(new RawEntity[]{featureMetadata});
            return true;
        });
    }

    public List<FeatureMetadata> getFeatures(List<FeatureCompleteKey> featureCompleteKeys) {
        FeatureMetadataAo[] pluginModuleEntities = (FeatureMetadataAo[])this.ao.executeInTransaction(() -> this.getFeatureMetadataAosFromFeaturesInBatches(featureCompleteKeys));
        return Arrays.stream(pluginModuleEntities).map(FeatureMetadata::new).collect(Collectors.toList());
    }

    public List<FeatureMetadata> getModules(List<ModuleCompleteKey> moduleCompleteKeys) {
        FeatureMetadataAo[] pluginModuleEntities = (FeatureMetadataAo[])this.ao.executeInTransaction(() -> this.getFeatureMetadataAosFromModules(moduleCompleteKeys));
        return Arrays.stream(pluginModuleEntities).map(FeatureMetadata::new).collect(Collectors.toList());
    }

    public void save(List<FeatureMetadata> featureMetadatas) {
        HashMap featureMetadatasToSave = Maps.newHashMap();
        for (FeatureMetadata featureMetadata : featureMetadatas) {
            featureMetadatasToSave.put(featureMetadata.getFeatureCompleteKey(), featureMetadata);
        }
        this.ao.executeInTransaction(() -> {
            List<ModuleCompleteKey> moduleCompleteKeys = featureMetadatas.stream().map(featureMetadata -> new ModuleCompleteKey(featureMetadata.getContext(), featureMetadata.getKey())).collect(Collectors.toList());
            FeatureMetadataAo[] existingModuleMetadatas = this.getFeatureMetadataAosFromModules(moduleCompleteKeys);
            for (FeatureMetadataAo existingModuleMetadata : existingModuleMetadatas) {
                featureMetadatasToSave.remove(new FeatureCompleteKey(existingModuleMetadata.getContext(), existingModuleMetadata.getKey()));
            }
            for (FeatureMetadata featureMetadata2 : featureMetadatasToSave.values()) {
                log.debug("Found new plugin module: {}", (Object)featureMetadata2.getContext());
                FeatureMetadataAo pluginModuleEntity = (FeatureMetadataAo)this.ao.create(FeatureMetadataAo.class, new DBParam[0]);
                pluginModuleEntity.setContext(featureMetadata2.getContext());
                pluginModuleEntity.setKey(featureMetadata2.getKey());
                pluginModuleEntity.setInstallationDate(featureMetadata2.getInstallationDate());
                pluginModuleEntity.save();
            }
            return null;
        });
    }

    private FeatureMetadataAo find(FeatureCompleteKey featureCompleteKey) {
        String context = featureCompleteKey.getContext();
        String key = featureCompleteKey.getKey();
        Query query = Query.select().where("CONTEXT = ? AND KEY = ?", new Object[]{context, key}).order("INSTALLATION_DATE ASC");
        FeatureMetadataAo[] featureMetadatas = (FeatureMetadataAo[])this.ao.find(FeatureMetadataAo.class, query);
        if (featureMetadatas.length > 1) {
            log.warn("Found more than 1 installation date entry for feature: {}:{}", (Object)context, (Object)key);
        }
        if (featureMetadatas.length > 0) {
            return featureMetadatas[0];
        }
        return null;
    }

    private FeatureMetadataAo[] getFeatureMetadataAosFromModules(List<ModuleCompleteKey> moduleCompleteKeys) {
        return this.getFeatureMetadataAosFromFeaturesInBatches(moduleCompleteKeys.stream().map(FeatureCompleteKey::new).collect(Collectors.toList()));
    }

    private FeatureMetadataAo[] getFeatureMetadataAosFromFeaturesInBatches(List<FeatureCompleteKey> featureCompleteKeys) {
        ArrayList result = new ArrayList(featureCompleteKeys.size());
        int totalSize = featureCompleteKeys.size();
        for (int i = 0; i < totalSize; i += 50) {
            int toIndex = Math.min(i + 50, totalSize);
            Collections.addAll(result, this.getFeatureMetadataAosFromFeatures(featureCompleteKeys.subList(i, toIndex)));
        }
        return result.toArray(new FeatureMetadataAo[0]);
    }

    private FeatureMetadataAo[] getFeatureMetadataAosFromFeatures(List<FeatureCompleteKey> featureCompleteKeys) {
        int size = featureCompleteKeys.size();
        if (size == 0) {
            return new FeatureMetadataAo[0];
        }
        Query query = Query.select();
        StringBuilder whereClause = new StringBuilder();
        Object[] whereParams = new String[size * 2];
        int numFilledWhereParams = 0;
        Iterator<FeatureCompleteKey> it = featureCompleteKeys.iterator();
        while (it.hasNext()) {
            FeatureCompleteKey featureCompleteKey = it.next();
            whereClause.append("(CONTEXT = ? AND KEY = ?)");
            if (it.hasNext()) {
                whereClause.append(" OR ");
            }
            whereParams[numFilledWhereParams++] = featureCompleteKey.getContext();
            whereParams[numFilledWhereParams++] = featureCompleteKey.getKey();
        }
        query.setWhereClause(whereClause.toString());
        query.setWhereParams(whereParams);
        return (FeatureMetadataAo[])this.ao.find(FeatureMetadataAo.class, query);
    }

    private /* synthetic */ FeatureMetadataAo lambda$getInstallationDate$2(String context, String key) {
        FeatureMetadataAo[] featureMetadatas = (FeatureMetadataAo[])this.ao.find(FeatureMetadataAo.class, Query.select((String)"ID, INSTALLATION_DATE").where("CONTEXT = ? AND KEY = ?", new Object[]{context, key}).order("INSTALLATION_DATE ASC"));
        if (featureMetadatas.length == 0) {
            return null;
        }
        if (featureMetadatas.length != 1) {
            log.warn("Found more than 1 installation date entry for feature: {}:{}", (Object)context, (Object)key);
        }
        return featureMetadatas[0];
    }
}

