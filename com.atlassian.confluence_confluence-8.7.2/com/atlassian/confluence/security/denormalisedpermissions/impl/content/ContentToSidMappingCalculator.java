/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.time.StopWatch
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.security.denormalisedpermissions.impl.content;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.security.denormalisedpermissions.impl.content.dao.RealContentAndPermissionsDao;
import com.atlassian.confluence.security.denormalisedpermissions.impl.user.DenormalisedSidManager;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Internal
public class ContentToSidMappingCalculator {
    private static final Logger log = LoggerFactory.getLogger(ContentToSidMappingCalculator.class);
    final RealContentAndPermissionsDao realContentAndPermissionsDao;
    final DenormalisedSidManager denormalisedSidManager;

    public ContentToSidMappingCalculator(RealContentAndPermissionsDao realContentAndPermissionsDao, DenormalisedSidManager denormalisedSidManager) {
        this.realContentAndPermissionsDao = realContentAndPermissionsDao;
        this.denormalisedSidManager = denormalisedSidManager;
    }

    Map<Long, Set<Long>> getRequiredSidsForPages(Collection<Long> pageIdSet) {
        StopWatch globalWatch = StopWatch.createStarted();
        Map<Long, Long> permissionSetToPageIdMap = this.realContentAndPermissionsDao.getContentPermissionSets(pageIdSet, "View");
        Map<Long, List<RealContentAndPermissionsDao.SimpleContentPermission>> permissionSetToPermissionsMap = this.realContentAndPermissionsDao.getSimpleContentPermissions(permissionSetToPageIdMap.keySet());
        HashMap usersHavingAccessToContent = new HashMap();
        HashMap groupHavingAccessToContent = new HashMap();
        HashSet<String> allUserKeys = new HashSet<String>();
        HashSet<String> allGroupNames = new HashSet<String>();
        for (Map.Entry<Long, List<RealContentAndPermissionsDao.SimpleContentPermission>> entry : permissionSetToPermissionsMap.entrySet()) {
            List<RealContentAndPermissionsDao.SimpleContentPermission> permissions = entry.getValue();
            Long contentPermissionSetId = entry.getKey();
            Long pageId = permissionSetToPageIdMap.get(contentPermissionSetId);
            usersHavingAccessToContent.put(pageId, permissions.stream().filter(r -> ObjectUtils.isNotEmpty((Object)r.getUserName())).map(r -> r.getUserName()).collect(Collectors.toSet()));
            groupHavingAccessToContent.put(pageId, permissions.stream().filter(r -> ObjectUtils.isNotEmpty((Object)r.getGroupName())).map(r -> r.getGroupName()).collect(Collectors.toSet()));
            allUserKeys.addAll((Collection)usersHavingAccessToContent.get(pageId));
            allGroupNames.addAll((Collection)groupHavingAccessToContent.get(pageId));
        }
        StopWatch stopWatch = StopWatch.createStarted();
        Map<String, Long> userKeyToSidIdMap = this.denormalisedSidManager.getAllUserSidsAndCreateThemIfRequired(allUserKeys);
        Map<String, Long> groupToSidIdMap = this.denormalisedSidManager.getAllGroupSidsAndCreateThemIfRequired(allGroupNames);
        log.trace("Users ({}) and groups ({}) were retrieved/created in {} ms", new Object[]{allUserKeys.size(), allGroupNames.size(), stopWatch.getTime()});
        HashMap<Long, Set<Long>> contentToSidsMap = new HashMap<Long, Set<Long>>();
        for (Map.Entry<Long, Long> entry : permissionSetToPageIdMap.entrySet()) {
            Long contentId = entry.getValue();
            Set userList = usersHavingAccessToContent.getOrDefault(contentId, Collections.emptySet());
            Set groupList = groupHavingAccessToContent.getOrDefault(contentId, Collections.emptySet());
            HashSet sids = new HashSet();
            sids.addAll(userList.stream().map(userKeyToSidIdMap::get).collect(Collectors.toSet()));
            sids.addAll(groupList.stream().map(groupToSidIdMap::get).collect(Collectors.toSet()));
            contentToSidsMap.put(contentId, sids);
        }
        log.trace("Got all required sids for {} pages in {} ms", (Object)pageIdSet.size(), (Object)globalWatch.getTime());
        return contentToSidsMap;
    }
}

