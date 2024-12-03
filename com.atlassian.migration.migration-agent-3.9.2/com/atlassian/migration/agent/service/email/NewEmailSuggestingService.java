/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.digest.DigestUtils
 */
package com.atlassian.migration.agent.service.email;

import com.atlassian.migration.agent.service.email.EmailCheckType;
import com.atlassian.migration.agent.service.email.MostFrequentDomainService;
import java.util.Optional;
import java.util.regex.Pattern;
import org.apache.commons.codec.digest.DigestUtils;

public class NewEmailSuggestingService {
    private static final Pattern spaceRegex = Pattern.compile("\\s");
    private static final Pattern alphabetRegex = Pattern.compile("^[a-zA-Z]*$");
    private static final int ID_HASH_LENGTH = 8;
    private final MostFrequentDomainService mostFrequentDomainService;

    public NewEmailSuggestingService(MostFrequentDomainService mostFrequentDomainService) {
        this.mostFrequentDomainService = mostFrequentDomainService;
    }

    public String suggest(String userKey, String username, String displayName, EmailCheckType type, String cloudId) {
        String prefix = this.tryNewPrefix(username).orElse(this.tryNewPrefix(userKey).orElse(this.tryNewPrefix(displayName).orElse(type.name())));
        String id = DigestUtils.sha512Hex((String)userKey).substring(0, 8);
        String domain = this.mostFrequentDomainService.getMostFrequentDomainName(cloudId);
        return prefix + "_" + id + "@" + domain;
    }

    private Optional<String> tryNewPrefix(String prefix) {
        if ((prefix = (prefix.length() > 10 ? prefix.substring(0, 10) : prefix).replaceAll(spaceRegex.pattern(), "")).isEmpty() || !prefix.matches(alphabetRegex.pattern())) {
            return Optional.empty();
        }
        return Optional.of(prefix);
    }
}

