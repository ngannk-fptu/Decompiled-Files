/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  javax.inject.Inject
 *  javax.inject.Named
 *  lombok.Generated
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.check.domain;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.migration.agent.service.check.domain.TrustedDomainContext;
import com.atlassian.migration.agent.service.extract.UserGroupExtractFacade;
import com.atlassian.migration.agent.service.impl.MigrationUser;
import com.atlassian.migration.agent.service.impl.UserDomainService;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Generated;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class TrustedDomainChecker
implements Checker<TrustedDomainContext> {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(TrustedDomainChecker.class);
    private final UserGroupExtractFacade userGroupExtractFacade;
    private final UserDomainService userDomainService;

    @Inject
    public TrustedDomainChecker(UserGroupExtractFacade userGroupExtractFacade, UserDomainService userDomainService) {
        this.userGroupExtractFacade = userGroupExtractFacade;
        this.userDomainService = userDomainService;
    }

    public CheckResult check(TrustedDomainContext ctx) {
        log.info("Trusted domain check started.");
        HashSet<String> blockedDomains = new HashSet<String>(this.userDomainService.getBlockedDomainsFromStore());
        List<MigrationUser> users = this.userGroupExtractFacade.getAllUsers().stream().filter(user -> {
            String userEmailDomain = TrustedDomainChecker.toDomain(user.getEmail());
            return !blockedDomains.contains(userEmailDomain);
        }).collect(Collectors.toList());
        List<MigrationUser> disallowedUsers = this.userDomainService.getUntrustedUsers(users, true);
        log.info("Trusted domain check finished.");
        return new CheckResult(disallowedUsers.isEmpty(), Collections.emptyMap());
    }

    public static String toDomain(String email) {
        return StringUtils.substringAfter((String)email.toLowerCase(), (String)"@");
    }
}

