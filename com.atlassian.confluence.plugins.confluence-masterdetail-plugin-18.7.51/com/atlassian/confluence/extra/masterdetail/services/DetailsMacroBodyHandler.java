/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.confluence.xhtml.api.MacroDefinitionHandler
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.confluence.extra.masterdetail.services;

import com.atlassian.confluence.plugins.pageproperties.api.model.PageProperty;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;

public interface DetailsMacroBodyHandler
extends MacroDefinitionHandler {
    public void handle(MacroDefinition var1);

    public List<? extends Map<String, PageProperty>> getDetails(String var1);

    public ImmutableMap<String, ImmutableList<ImmutableMap<String, PageProperty>>> getDetails();
}

