/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 */
package com.atlassian.crowd.embedded.validator.rule;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.validator.rule.RuleBuilder;
import java.util.function.Function;

public class DirectoryRuleBuilder
extends RuleBuilder<Directory> {
    private DirectoryRuleBuilder(String fieldName) {
        super(fieldName);
    }

    public static DirectoryRuleBuilder ruleFor(String fieldName) {
        return new DirectoryRuleBuilder(fieldName);
    }

    public static Function<Directory, String> valueOf(String attributeKey) {
        return directory -> directory.getValue(attributeKey);
    }
}

