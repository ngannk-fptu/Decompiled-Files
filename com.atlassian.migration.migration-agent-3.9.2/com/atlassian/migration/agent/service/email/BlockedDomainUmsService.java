/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.email.EmailData
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.email;

import com.atlassian.cmpt.check.email.EmailData;
import com.atlassian.migration.agent.service.email.DomainId;
import com.atlassian.migration.agent.service.email.InvalidEmailValidator;
import com.atlassian.migration.agent.service.impl.MigrationUser;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Generated;

public class BlockedDomainUmsService {
    private final InvalidEmailValidator invalidEmailService;

    public Set<DomainId> getBlockedDomainsFromUms(String cloudId, Set<DomainId> domains) {
        if (domains.isEmpty()) {
            return Collections.emptySet();
        }
        String executionId = UUID.randomUUID().toString();
        List<MigrationUser> users = domains.stream().map(DomainId::generateRandomEmail).map(MigrationUser::fromEmailAsActive).collect(Collectors.toList());
        List<EmailData> checkResult = this.invalidEmailService.getInvalidEmails(executionId, cloudId, users);
        return this.processBlockedDomains(checkResult);
    }

    private Set<DomainId> processBlockedDomains(List<EmailData> checkResult) {
        return checkResult.stream().map(invalidEmail -> DomainId.fromEmail(invalidEmail.email)).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Generated
    public BlockedDomainUmsService(InvalidEmailValidator invalidEmailService) {
        this.invalidEmailService = invalidEmailService;
    }
}

