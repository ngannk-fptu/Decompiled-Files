/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.longrunning.LongRunningTaskId
 *  com.atlassian.confluence.util.longrunning.LongRunningTaskManager
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.service.impl;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.plugin.copyspace.entity.CopySpaceProgressBarData;
import com.atlassian.confluence.plugin.copyspace.service.CopySpaceProgressBarCacheService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.confluence.util.longrunning.LongRunningTaskManager;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component(value="copySpaceProgressBarCacheServiceImpl")
public class CopySpaceProgressBarCacheServiceImpl
implements CopySpaceProgressBarCacheService {
    private static final String CACHE_NAME = CopySpaceProgressBarCacheService.class.getName();
    private static final int CACHE_SIZE = Integer.getInteger("confluence.plugin.space.copy.progress.bar.cache.size", 1000);
    private static final int EXPIRES_AFTER = Integer.getInteger("confluence.plugin.space.copy.progress.bar.cache.expires.after.hours", 6);
    private final Cache<UserKey, List<CopySpaceProgressBarData>> copySpaceTaskIdsPerUserPerSpace;
    private final LongRunningTaskManager longRunningTaskManager;

    public CopySpaceProgressBarCacheServiceImpl(@ComponentImport CacheManager cacheManager, LongRunningTaskManager longRunningTaskManager) {
        this.copySpaceTaskIdsPerUserPerSpace = this.createCache((CacheFactory)cacheManager);
        this.longRunningTaskManager = longRunningTaskManager;
    }

    @Override
    public CopySpaceProgressBarData getProgressBarData(String spaceKey) {
        UserKey userKey = AuthenticatedUserThreadLocal.get().getKey();
        Optional<CopySpaceProgressBarData> optionalCopySpaceProgressBarData = Objects.requireNonNull((List)this.copySpaceTaskIdsPerUserPerSpace.get((Object)userKey)).stream().filter(taskDetails -> taskDetails.getOriginalSpaceKey().equals(spaceKey)).findFirst();
        if (optionalCopySpaceProgressBarData.isPresent()) {
            String taskId = optionalCopySpaceProgressBarData.get().getLongRunningTaskId();
            LongRunningTask longRunningTask = this.longRunningTaskManager.getLongRunningTask((User)AuthenticatedUserThreadLocal.get(), LongRunningTaskId.valueOf((String)taskId));
            if (longRunningTask.isComplete() || !longRunningTask.isSuccessful()) {
                this.removeProgressBarData(spaceKey);
                return CopySpaceProgressBarData.NO_OPERATION_IN_PROGRESS;
            }
            return optionalCopySpaceProgressBarData.get();
        }
        return CopySpaceProgressBarData.NO_OPERATION_IN_PROGRESS;
    }

    @Override
    public void putProgressBarData(CopySpaceProgressBarData copySpaceProgressBarData) {
        UserKey userKey = AuthenticatedUserThreadLocal.get().getKey();
        List tasksDetails = Objects.requireNonNull((List)this.copySpaceTaskIdsPerUserPerSpace.get((Object)userKey));
        tasksDetails.add(copySpaceProgressBarData);
    }

    @Override
    public void removeProgressBarData(String spaceKey) {
        UserKey userKey = AuthenticatedUserThreadLocal.get().getKey();
        List tasksDetails = Objects.requireNonNull((List)this.copySpaceTaskIdsPerUserPerSpace.get((Object)userKey));
        tasksDetails.removeIf(taskDetails -> taskDetails.getOriginalSpaceKey().equals(spaceKey));
    }

    private Cache<UserKey, List<CopySpaceProgressBarData>> createCache(CacheFactory cacheFactory) {
        return cacheFactory.getCache(CACHE_NAME, this.createCacheLoader(), new CacheSettingsBuilder().expireAfterWrite((long)EXPIRES_AFTER, TimeUnit.HOURS).expireAfterAccess((long)EXPIRES_AFTER, TimeUnit.HOURS).local().unflushable().maxEntries(CACHE_SIZE).build());
    }

    private CacheLoader<UserKey, List<CopySpaceProgressBarData>> createCacheLoader() {
        return new CacheLoader<UserKey, List<CopySpaceProgressBarData>>(){

            @Nonnull
            public List<CopySpaceProgressBarData> load(@Nonnull UserKey userKey) {
                return new ArrayList<CopySpaceProgressBarData>();
            }
        };
    }

    @Override
    public boolean isCopySpaceInProgress(String spaceKey) {
        Collection keys = this.copySpaceTaskIdsPerUserPerSpace.getKeys();
        if (keys.isEmpty() || StringUtils.isBlank((CharSequence)spaceKey)) {
            return false;
        }
        for (UserKey userKey : keys) {
            List dataList = (List)this.copySpaceTaskIdsPerUserPerSpace.get((Object)userKey);
            if (dataList == null) continue;
            for (CopySpaceProgressBarData data : dataList) {
                if (!spaceKey.equals(data.getOriginalSpaceKey())) continue;
                return true;
            }
        }
        return false;
    }
}

