/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSortedSet
 */
package com.google.template.soy.parseinfo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.template.soy.parseinfo.SoyTemplateInfo;

public class SoyFileInfo {
    private final String fileName;
    private final String namespace;
    private final ImmutableSortedSet<String> paramsFromAllTemplates;
    private final ImmutableList<SoyTemplateInfo> templates;
    private final ImmutableMap<String, CssTagsPrefixPresence> cssNameMap;

    public SoyFileInfo(String fileName, String namespace, ImmutableSortedSet<String> paramsFromAllTemplates, ImmutableList<SoyTemplateInfo> templates, ImmutableMap<String, CssTagsPrefixPresence> cssNameMap) {
        this.fileName = fileName;
        this.namespace = namespace;
        this.paramsFromAllTemplates = paramsFromAllTemplates;
        this.templates = templates;
        this.cssNameMap = cssNameMap;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public ImmutableSortedSet<String> getParamsFromAllTemplates() {
        return this.paramsFromAllTemplates;
    }

    public ImmutableList<SoyTemplateInfo> getTemplates() {
        return this.templates;
    }

    public ImmutableMap<String, CssTagsPrefixPresence> getCssNames() {
        return this.cssNameMap;
    }

    public ImmutableList<Object> getProtoTypes() {
        return ImmutableList.of();
    }

    public static enum CssTagsPrefixPresence {
        ALWAYS,
        NEVER,
        SOMETIMES;

    }
}

