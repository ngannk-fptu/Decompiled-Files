/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.jira.util;

import com.atlassian.confluence.extra.jira.util.JiraUtil;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class JiraIssuePredicates {
    public static final Pattern ISSUE_KEY_PATTERN = Pattern.compile("\\s*([A-Z][A-Z]+)-[0-9]+\\s*");
    public static Predicate<MacroDefinition> isJiraIssueMacro = macroDefinition -> StringUtils.equals((CharSequence)macroDefinition.getName(), (CharSequence)"jira") || StringUtils.equals((CharSequence)macroDefinition.getName(), (CharSequence)"jiraissues");
    public static Predicate<MacroDefinition> isSingleIssue = Predicates.and(isJiraIssueMacro, macroDefinition -> {
        Map parameters = macroDefinition.getParameters();
        String issueKey = JiraUtil.getSingleIssueKey(parameters);
        if (StringUtils.isNotEmpty((CharSequence)issueKey)) {
            macroDefinition.setParameter("key", issueKey);
            return true;
        }
        return false;
    });
    public static Predicate<MacroDefinition> isTableIssue = Predicates.and((Predicate)Predicates.not(isSingleIssue), macroDefinition -> StringUtils.isEmpty((CharSequence)((CharSequence)macroDefinition.getParameters().get("count"))));
    public static Predicate<MacroDefinition> isCountIssue = Predicates.and((Predicate)Predicates.not(isSingleIssue), macroDefinition -> Boolean.parseBoolean((String)macroDefinition.getParameters().get("count")));
}

