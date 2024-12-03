/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.sharepage;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ShareGroupEmailManager {
    private static final Logger logger = LoggerFactory.getLogger(ShareGroupEmailManager.class);
    private static final String ARG_OPTION = "share.group.email.mapping";
    private final ImmutableMap<String, String> emailMapping = ImmutableMap.copyOf(this.mapFromString(System.getProperty("share.group.email.mapping")));

    public Map<String, String> getGroupEmailMapping() {
        return this.emailMapping;
    }

    public boolean hasGroupEmail(String group) {
        return this.emailMapping.containsKey((Object)group);
    }

    public String getGroupEmail(String group) {
        return (String)this.emailMapping.get((Object)group);
    }

    public Set<String> getMappedGroupNames() {
        return this.emailMapping.keySet();
    }

    private Map<String, String> mapFromString(@Nullable String argString) {
        HashMap<String, String> emailMapping = new HashMap<String, String>();
        if (StringUtils.isBlank((CharSequence)argString)) {
            logger.debug("No arg string provided for share group email mapping");
            return emailMapping;
        }
        Splitter.on((char)',').omitEmptyStrings().split((CharSequence)argString).forEach(email -> {
            String[] mapping = email.split("[:]");
            if (mapping.length == 2) {
                emailMapping.put(mapping[0].trim(), mapping[1].trim());
            } else {
                logger.warn("Badly formatted email string pair {}. Email mapping may not have been correctly processed.", email);
            }
        });
        return emailMapping;
    }
}

