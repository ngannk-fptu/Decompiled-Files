/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator
 *  org.apache.commons.lang.StringUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.cmpt.validation.IdentityAcceptedEmailValidator;
import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.agent.dto.UserDomainRuleDto;
import com.atlassian.migration.agent.entity.DomainRuleBehaviour;
import com.atlassian.migration.agent.service.check.domain.TrustedDomainChecker;
import com.atlassian.migration.agent.service.email.DomainId;
import com.atlassian.migration.agent.service.email.InvalidEmailValidator;
import com.atlassian.migration.agent.service.extract.UserGroupExtractFacade;
import com.atlassian.migration.agent.service.impl.MigrationUser;
import com.atlassian.migration.agent.service.impl.UserDomainService;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;

public class BlockedDomainService {
    private final UserGroupExtractFacade userGroupExtractFacade;
    private final InvalidEmailValidator invalidEmailValidator;
    private final UserDomainService userDomainService;
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;

    public BlockedDomainService(UserGroupExtractFacade userGroupExtractFacade, InvalidEmailValidator invalidEmailValidator, UserDomainService userDomainService, MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        this.userGroupExtractFacade = userGroupExtractFacade;
        this.invalidEmailValidator = invalidEmailValidator;
        this.userDomainService = userDomainService;
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
    }

    public List<String> getBlockedDomains(String cloudId) {
        if (this.migrationDarkFeaturesManager.isUmsCheckForTrustedDomainsDisabled()) {
            List<String> emptyList = Collections.emptyList();
            this.deleteStaleRuleReferences(emptyList);
            return emptyList;
        }
        String scanId = UUID.randomUUID().toString();
        List<MigrationUser> fakeUsersWithAllDistinctDomains = this.userGroupExtractFacade.getAllUsers().stream().map(MigrationUser::getEmail).map(BlockedDomainService::getDomainFromEmail).filter(org.apache.commons.lang3.StringUtils::isNotBlank).map(DomainId::new).distinct().map(domain -> MigrationUser.fromEmailAsActive(domain.generateRandomEmail())).filter(migrationUser -> IdentityAcceptedEmailValidator.isValidEmailAddress((String)migrationUser.getEmail())).collect(Collectors.toList());
        List<String> blockedDomainsFromUms = this.invalidEmailValidator.getInvalidEmails(scanId, cloudId, fakeUsersWithAllDistinctDomains).stream().filter(emailData -> StringUtils.isNotBlank((String)emailData.email)).map(emailData -> TrustedDomainChecker.toDomain(emailData.email)).distinct().collect(Collectors.toList());
        this.deleteStaleRuleReferences(blockedDomainsFromUms);
        blockedDomainsFromUms.forEach(domain -> this.userDomainService.upsertDomainRule(new UserDomainRuleDto((String)domain, DomainRuleBehaviour.BLOCKED)));
        return blockedDomainsFromUms;
    }

    private static String getDomainFromEmail(String email) {
        return org.apache.commons.lang3.StringUtils.substringAfter((String)email, (String)"@");
    }

    private void deleteStaleRuleReferences(List<String> blockedDomainsFromUms) {
        List<String> domainsToDelete = this.userDomainService.getBlockedDomainsFromStore().stream().filter(storedDomain -> !blockedDomainsFromUms.contains(storedDomain)).collect(Collectors.toList());
        this.userDomainService.deleteDomainRules(domainsToDelete);
    }
}

