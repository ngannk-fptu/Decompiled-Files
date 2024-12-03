/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.embedded.api.Query
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.google.common.collect.ImmutableSet
 *  io.atlassian.util.concurrent.ThreadFactories
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.interceptor.DefaultTransactionAttribute
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.user;

import com.atlassian.confluence.security.denormalisedpermissions.impl.manage.DenormalisedLockService;
import com.atlassian.confluence.security.denormalisedpermissions.impl.user.dao.DenormalisedSidDao;
import com.atlassian.confluence.security.denormalisedpermissions.impl.user.domain.DenormalisedSid;
import com.atlassian.confluence.security.denormalisedpermissions.impl.user.domain.DenormalisedSidType;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.embedded.api.Query;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.google.common.collect.ImmutableSet;
import io.atlassian.util.concurrent.ThreadFactories;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.TransactionTemplate;

public class DenormalisedSidManager {
    private static final Logger log = LoggerFactory.getLogger(DenormalisedSidManager.class);
    private static final Set<Long> ANONYMOUS_SID_SET = Collections.singleton(-1L);
    private static final Set<Long> SUPER_ADMIN_SID_SET = Collections.singleton(-3L);
    private final PlatformTransactionManager transactionManager;
    private final DenormalisedSidDao denormalisedSidDao;
    private final DenormalisedLockService denormalisedLockService;
    private final CrowdService crowdService;
    private final ExecutorService executor = Executors.newCachedThreadPool(ThreadFactories.namedThreadFactory((String)this.getClass().getSimpleName()));

    public DenormalisedSidManager(PlatformTransactionManager transactionManager, DenormalisedSidDao denormalisedSidDao, DenormalisedLockService denormalisedLockService, CrowdService crowdService) {
        this.transactionManager = transactionManager;
        this.denormalisedSidDao = denormalisedSidDao;
        this.denormalisedLockService = denormalisedLockService;
        this.crowdService = crowdService;
    }

    public Map<String, Long> getAllUserSidsAndCreateThemIfRequired(Set<String> userKeys) {
        try {
            return this.getAllSidsAndCreateThemIfRequired(userKeys, DenormalisedSidType.USER);
        }
        catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    public Map<String, Long> getAllGroupSidsAndCreateThemIfRequired(Set<String> groupNames) {
        try {
            return this.getAllSidsAndCreateThemIfRequired(groupNames, DenormalisedSidType.GROUP);
        }
        catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    private Map<String, Long> getAllSidsAndCreateThemIfRequired(Set<String> names, DenormalisedSidType sidType) throws ExecutionException, InterruptedException {
        List<DenormalisedSid> existingSids = this.denormalisedSidDao.getExistingSids(names, sidType);
        HashSet<String> notFoundNames = new HashSet<String>(names);
        HashMap<String, Long> nameToSids = new HashMap<String, Long>();
        existingSids.forEach(sid -> {
            String userOrGroupName = sid.getName();
            if (!notFoundNames.contains(userOrGroupName)) {
                throw new IllegalStateException("We got user or group name '" + userOrGroupName + "' from database even though we haven't requested it. It seems it's a bug");
            }
            nameToSids.put(userOrGroupName, sid.getId());
            notFoundNames.remove(userOrGroupName);
        });
        if (notFoundNames.size() == 0) {
            return nameToSids;
        }
        Map<String, Long> createdNameToSidsMap = this.createSidsInSeparateTransaction((Set<String>)ImmutableSet.copyOf(notFoundNames), sidType);
        nameToSids.putAll(createdNameToSidsMap);
        return nameToSids;
    }

    private Map<String, Long> createSidsInSeparateTransaction(Set<String> sidNames, DenormalisedSidType sidType) throws ExecutionException, InterruptedException {
        Future<Map> future = this.executor.submit(() -> {
            DefaultTransactionAttribute transactionDefinition = new DefaultTransactionAttribute(3);
            return (Map)new TransactionTemplate(this.transactionManager, (TransactionDefinition)transactionDefinition).execute(status -> {
                HashMap<String, Long> nameToSids = new HashMap<String, Long>();
                this.denormalisedLockService.acquireLockForTransaction(DenormalisedLockService.LockName.UPDATE_SIDS);
                List<DenormalisedSid> existingSids = this.denormalisedSidDao.getExistingSids(sidNames, sidType);
                HashSet sidsToAdd = new HashSet(sidNames);
                existingSids.forEach(sid -> {
                    String userOrGroupName = sid.getName();
                    if (!sidsToAdd.contains(userOrGroupName)) {
                        if (log.isDebugEnabled()) {
                            log.debug("We got user or group name '{}' from database even though we haven't requested it. It seems it's a bug. Sid names: {}, existing sids: {}", new Object[]{userOrGroupName, String.join((CharSequence)", ", sidNames), existingSids.stream().map(DenormalisedSid::getName).collect(Collectors.joining(", "))});
                        }
                        throw new IllegalStateException("We got user or group name '" + userOrGroupName + "' from database even though we haven't requested it. It seems it's a bug");
                    }
                    nameToSids.put(userOrGroupName, sid.getId());
                    sidsToAdd.remove(userOrGroupName);
                });
                for (String userOrGroupName : sidsToAdd) {
                    long sidId = this.denormalisedSidDao.addNewSid(userOrGroupName, sidType);
                    nameToSids.put(userOrGroupName, sidId);
                }
                return nameToSids;
            });
        });
        return future.get();
    }

    public Set<Long> getAllUserSids(ConfluenceUser confluenceUser) {
        if (confluenceUser == null) {
            return ANONYMOUS_SID_SET;
        }
        String userName = confluenceUser.getLowerName();
        Set<String> userGroups = this.findAllUserGroups(userName);
        log.debug("Found {} groups for user {}", (Object)userGroups.size(), (Object)userName);
        if (userGroups.contains("confluence-administrators")) {
            return SUPER_ADMIN_SID_SET;
        }
        HashSet<Long> denormalisedSidSet = new HashSet<Long>();
        denormalisedSidSet.addAll(this.denormalisedSidDao.getExistingSidIdList(userGroups, DenormalisedSidType.GROUP));
        denormalisedSidSet.addAll(this.denormalisedSidDao.getExistingSidIdList(new HashSet<String>(Collections.singletonList(confluenceUser.getKey().getStringValue())), DenormalisedSidType.USER));
        denormalisedSidSet.add(-1L);
        denormalisedSidSet.add(-2L);
        return denormalisedSidSet;
    }

    private Set<String> findAllUserGroups(String lowerName) {
        Iterable groups = this.crowdService.search((Query)QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.group()).parentsOf(EntityDescriptor.user()).withName(lowerName).startingAt(0).returningAtMost(Integer.MAX_VALUE));
        return StreamSupport.stream(groups.spliterator(), false).collect(Collectors.toSet());
    }
}

