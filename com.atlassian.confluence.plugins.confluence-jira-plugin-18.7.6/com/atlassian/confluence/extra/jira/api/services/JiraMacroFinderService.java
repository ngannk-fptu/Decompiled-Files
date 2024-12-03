/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.google.common.base.Predicate
 */
package com.atlassian.confluence.extra.jira.api.services;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.google.common.base.Predicate;
import java.util.List;
import java.util.Set;

public interface JiraMacroFinderService {
    public Set<MacroDefinition> findJiraIssueMacros(AbstractPage var1, Predicate<MacroDefinition> var2) throws XhtmlException;

    @Deprecated
    public List<MacroDefinition> findSingleJiraIssueMacros(String var1, ConversionContext var2) throws XhtmlException;

    public List<MacroDefinition> findJiraMacros(ContentEntityObject var1, Predicate<MacroDefinition> var2) throws XhtmlException;
}

