/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.idmapping.finders;

import com.atlassian.confluence.impl.backuprestore.restore.domain.ImportedObjectV2;
import com.atlassian.confluence.impl.backuprestore.restore.idmapping.finders.ExistingEntityFinder;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUserEntityFinder
implements ExistingEntityFinder {
    private static final Logger log = LoggerFactory.getLogger(AbstractUserEntityFinder.class);
    private final int BULK_USERS_FINDER_BATCH_SIZE;
    public final ConfluenceUserDao confluenceUserDao;

    protected AbstractUserEntityFinder(ConfluenceUserDao confluenceUserDao) {
        this.confluenceUserDao = confluenceUserDao;
        this.BULK_USERS_FINDER_BATCH_SIZE = Integer.getInteger("confluence.restore.userfinder-batch-size", 400);
    }

    @Override
    public Map<ImportedObjectV2, Object> findExistingObjectIds(Collection<ImportedObjectV2> objects) {
        HashMap<ImportedObjectV2, Object> foundUsers = new HashMap<ImportedObjectV2, Object>();
        if (objects == null || objects.isEmpty()) {
            return foundUsers;
        }
        List partitions = Lists.partition(new ArrayList<ImportedObjectV2>(objects), (int)this.BULK_USERS_FINDER_BATCH_SIZE);
        log.trace("Finding [{}] existing user entities using {} in [{}] Batch(es)", new Object[]{objects.size(), this.getClass().getSimpleName(), partitions.size()});
        for (List partition : partitions) {
            Map<ImportedObjectV2, UserKey> userKeyMap = this.findOnlyExistingUserKeysByUserKeys(partition);
            foundUsers.putAll(userKeyMap);
            log.trace("Found [{}] user entities by keys", (Object)userKeyMap.size());
            List<ImportedObjectV2> usersToFindBySecondStageFind = partition.stream().filter(item -> !userKeyMap.containsKey(item)).collect(Collectors.toList());
            if (usersToFindBySecondStageFind.isEmpty()) continue;
            Map<ImportedObjectV2, UserKey> userSecondStageFindMap = this.doSecondStageFind(usersToFindBySecondStageFind);
            foundUsers.putAll(userSecondStageFindMap);
            log.trace("Second stage find found [{}] user entities ", (Object)userSecondStageFindMap.size());
        }
        return foundUsers;
    }

    private Map<ImportedObjectV2, UserKey> findOnlyExistingUserKeysByUserKeys(List<ImportedObjectV2> importedObjectPartition) {
        BiMap objectsByUserKey = (BiMap)importedObjectPartition.stream().collect(Collectors.toMap(object -> new UserKey((String)object.getId()), Function.identity(), (a, b) -> b, HashBiMap::create));
        List foundUsers = this.confluenceUserDao.findByKeys(objectsByUserKey.keySet()).values().stream().filter(Optional::isPresent).map(Optional::get).map(ConfluenceUser::getKey).collect(Collectors.toList());
        objectsByUserKey.keySet().retainAll(foundUsers);
        return objectsByUserKey.inverse();
    }

    public abstract Map<ImportedObjectV2, UserKey> doSecondStageFind(List<ImportedObjectV2> var1);
}

