/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.springframework.core.io.ClassPathResource
 */
package com.atlassian.migration.agent.service.util;

import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.service.util.MaskingRule;
import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Generated;
import org.springframework.core.io.ClassPathResource;

public final class MaskingUtility {
    private static final String MASKING_RULE_RESOURCE = "masking-rules.json";
    private static final MaskingRule[] MASKING_RULES;

    public static String mask(String text) {
        if (Objects.nonNull(text)) {
            for (MaskingRule maskingRule : MASKING_RULES) {
                Pattern pattern = maskingRule.getPattern();
                String replacement = maskingRule.getReplacement();
                Matcher matcher = pattern.matcher(text);
                text = matcher.replaceAll(replacement);
            }
        }
        return text;
    }

    @Generated
    private MaskingUtility() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static {
        try {
            MASKING_RULES = Jsons.readValue(new ClassPathResource(MASKING_RULE_RESOURCE, MethodHandles.lookup().lookupClass().getClassLoader()).getInputStream(), MaskingRule[].class);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

