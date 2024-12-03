/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.confluence.xhtml.api.MacroDefinitionHandler
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.renderer.RenderContext
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira.services;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.jira.api.services.JiraMacroFinderService;
import com.atlassian.confluence.extra.jira.util.JiraIssuePredicates;
import com.atlassian.confluence.extra.jira.util.JiraUtil;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.renderer.RenderContext;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultJiraMacroFinderService
implements JiraMacroFinderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultJiraMacroFinderService.class);
    private final XhtmlContent xhtmlContent;

    public DefaultJiraMacroFinderService(XhtmlContent xhtmlContent) {
        this.xhtmlContent = xhtmlContent;
    }

    @Override
    public Set<MacroDefinition> findJiraIssueMacros(AbstractPage page, Predicate<MacroDefinition> filter) {
        Predicate jiraPredicate = JiraIssuePredicates.isJiraIssueMacro;
        if (filter != null) {
            jiraPredicate = Predicates.and(jiraPredicate, filter);
        }
        return new HashSet<MacroDefinition>(this.findJiraMacros((ContentEntityObject)page, (Predicate<MacroDefinition>)jiraPredicate));
    }

    @Override
    public List<MacroDefinition> findJiraMacros(ContentEntityObject contentEntityObject, Predicate<MacroDefinition> filter) {
        Predicate jiraPredicate = JiraIssuePredicates.isJiraIssueMacro;
        if (filter != null) {
            jiraPredicate = Predicates.and(filter, jiraPredicate);
        }
        Predicate jiraMacroPredicate = jiraPredicate;
        ArrayList definitions = Lists.newArrayList();
        MacroDefinitionHandler handler = macroDefinition -> {
            if (jiraMacroPredicate.apply((Object)macroDefinition)) {
                definitions.add(macroDefinition);
            }
        };
        try {
            this.xhtmlContent.handleMacroDefinitions(contentEntityObject.getBodyAsString(), (ConversionContext)new DefaultConversionContext((RenderContext)contentEntityObject.toPageContext()), handler);
        }
        catch (XhtmlException e) {
            LOGGER.warn("Could not get macro definitions.", (Throwable)e);
        }
        return definitions;
    }

    @Override
    public List<MacroDefinition> findSingleJiraIssueMacros(String body, ConversionContext conversionContext) throws XhtmlException {
        SingleJiraIssuePredicate singleJiraIssuePredicate = new SingleJiraIssuePredicate();
        ArrayList definitions = Lists.newArrayList();
        MacroDefinitionHandler handler = macroDefinition -> {
            if (singleJiraIssuePredicate.apply(macroDefinition)) {
                macroDefinition.setParameter("key", singleJiraIssuePredicate.getIssueKey());
                definitions.add(macroDefinition);
            }
        };
        this.xhtmlContent.handleMacroDefinitions(body, conversionContext, handler);
        return definitions;
    }

    private class SingleJiraIssuePredicate
    implements Predicate<MacroDefinition> {
        private String issueKey;

        private SingleJiraIssuePredicate() {
        }

        public boolean apply(MacroDefinition definition) {
            boolean isJiraIssue;
            boolean bl = isJiraIssue = definition.getName().equals("jira") || definition.getName().equals("jiraissues");
            if (!isJiraIssue) {
                return false;
            }
            this.issueKey = JiraUtil.getSingleIssueKey(definition.getParameters());
            return this.issueKey != null;
        }

        String getIssueKey() {
            return this.issueKey;
        }
    }
}

