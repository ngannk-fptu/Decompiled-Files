/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.backuprestore.JobSource
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.HashBiMap
 */
package com.atlassian.confluence.impl.backuprestore.restore.idmapping.finders;

import com.atlassian.confluence.api.model.backuprestore.JobSource;
import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.finders.AbstractUserEntityFinder;
import com.atlassian.confluence.user.ConfluenceUserImpl;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.HashBiMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConfluenceUserFinder
extends AbstractUserEntityFinder {
    public ConfluenceUserFinder(ConfluenceUserDao confluenceUserDao) {
        super(confluenceUserDao);
    }

    @Override
    public Map<ImportedObjectV2, UserKey> doSecondStageFind(List<ImportedObjectV2> importedObjectPartition) {
        return this.findOnlyExistingUserKeysByLowerName(importedObjectPartition);
    }

    private Map<ImportedObjectV2, UserKey> findOnlyExistingUserKeysByLowerName(List<ImportedObjectV2> importedObjectPartition) {
        Map objectsByUserKey = (Map)importedObjectPartition.stream().collect(Collectors.toMap(object -> (String)object.getFieldValue("lowerName"), Function.identity(), (a, b) -> b, HashBiMap::create));
        Map<String, UserKey> foundUsers = this.confluenceUserDao.findUserKeysByLowerNames(objectsByUserKey.keySet());
        HashMap<ImportedObjectV2, UserKey> results = new HashMap<ImportedObjectV2, UserKey>();
        foundUsers.forEach((k, v) -> results.put((ImportedObjectV2)objectsByUserKey.get(k), (UserKey)v));
        return results;
    }

    @Override
    public Class<?> getSupportedClass() {
        return ConfluenceUserImpl.class;
    }

    @Override
    public boolean isSupportedJobSource(JobSource jobSource) {
        return JobSource.SERVER.equals((Object)jobSource);
    }
}

