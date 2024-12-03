/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.DirectoryProperties
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.spi.UserDao
 *  com.atlassian.crowd.model.application.Application
 *  com.google.common.collect.ImmutableList
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.manager.application;

import com.atlassian.crowd.directory.DirectoryProperties;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.spi.UserDao;
import com.atlassian.crowd.manager.recovery.RecoveryModeService;
import com.atlassian.crowd.model.application.Application;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationOrderOptimizer {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationOrderOptimizer.class);
    private final UserDao userDao;
    private final RecoveryModeService recoveryModeService;

    public AuthenticationOrderOptimizer(UserDao userDao, RecoveryModeService recoveryModeService) {
        this.userDao = userDao;
        this.recoveryModeService = recoveryModeService;
    }

    List<Directory> optimizeDirectoryOrderForAuthentication(Application application, List<Directory> directories, String username) {
        if (!application.isCachedDirectoriesAuthenticationOrderOptimisationEnabled() || directories.isEmpty()) {
            return directories;
        }
        Set directoriesContainingUser = this.userDao.findDirectoryIdsContainingUserName(username);
        Predicate<Directory> canDirectoryAuthenticationBePostponed = directory -> !this.recoveryModeService.isRecoveryDirectory((Directory)directory) && DirectoryProperties.cachesAllUsers((Directory)directory) && !directoriesContainingUser.contains(directory.getId());
        Map<Boolean, List<Directory>> directoryPostponeMap = directories.stream().collect(Collectors.partitioningBy(canDirectoryAuthenticationBePostponed));
        List<Directory> directoriesToPostpone = directoryPostponeMap.get(true);
        List<Directory> directoriesToNormallyProceed = directoryPostponeMap.get(false);
        if (!directoriesToPostpone.isEmpty() && !directoriesToNormallyProceed.isEmpty()) {
            logger.debug("Optimizing authentication order for application {} by moving directories {} to the end of the directory queue.", (Object)application.getId(), directoriesToPostpone);
        }
        return ImmutableList.builder().addAll(directoriesToNormallyProceed).addAll(directoriesToPostpone).build();
    }
}

