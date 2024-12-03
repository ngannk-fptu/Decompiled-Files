/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.extension.MetadataProperty
 *  com.atlassian.confluence.api.extension.ModelMetadataProvider
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.reference.ModelMapBuilder
 *  com.atlassian.confluence.content.render.xhtml.view.macro.MacroAsyncRenderWhitelist
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.xhtml.MacroManager
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.reflect.TypeUtils
 */
package com.atlassian.confluence.plugins.dashboard.rest;

import com.atlassian.confluence.api.extension.MetadataProperty;
import com.atlassian.confluence.api.extension.ModelMetadataProvider;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.reference.ModelMapBuilder;
import com.atlassian.confluence.content.render.xhtml.view.macro.MacroAsyncRenderWhitelist;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.plugins.dashboard.macros.dao.ContentMacroNames;
import com.atlassian.confluence.plugins.dashboard.macros.dao.ContentMacroNamesDao;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.reflect.TypeUtils;

public class SpaFriendlyMetadataProvider
implements ModelMetadataProvider {
    private static final String FRONTEND_KEY = "frontend";
    private static final String SPA_FRIENDLY_KEY = "spaFriendly";
    private static final String COMMENT_SPA_FRIENDLY_KEY = "commentsSpaFriendly";
    private static final String MACRO_NAMES_NOT_SPA_FRIENDLY_KEY = "macroNamesNotSpaFriendly";
    private static final String COMMENT_MACRO_NAMES_NOT_SPA_FRIENDLY_KEY = "commentMacroNamesNotSpaFriendly";
    private static final List<String> METADATA_PROPERTY_NAMES = Collections.singletonList("frontend");
    private final ContentMacroNamesDao macroNamesDao;
    private MacroAsyncRenderWhitelist macroAsyncRenderWhitelist;
    private MacroManager macroManager;

    public SpaFriendlyMetadataProvider(ContentMacroNamesDao macroNamesDao, MacroAsyncRenderWhitelist macroAsyncRenderWhitelist, MacroManager macroManager) {
        this.macroNamesDao = macroNamesDao;
        this.macroAsyncRenderWhitelist = macroAsyncRenderWhitelist;
        this.macroManager = macroManager;
    }

    public Map<Object, Map<String, ?>> getMetadataForAll(Iterable<Object> entities, Expansions expansions) {
        Iterable contents = Iterables.filter(entities, c -> c instanceof Content);
        if (Iterables.isEmpty((Iterable)contents)) {
            return Collections.emptyMap();
        }
        Expansions frontendExpansions = expansions.getSubExpansions(FRONTEND_KEY);
        boolean includeComments = frontendExpansions.canExpand(COMMENT_SPA_FRIENDLY_KEY) || frontendExpansions.canExpand(COMMENT_MACRO_NAMES_NOT_SPA_FRIENDLY_KEY);
        ImmutableMap contentIdsToMacroNames = Maps.uniqueIndex(this.macroNamesDao.getContentMacroNames(contents, includeComments), ContentMacroNames::getContentId);
        ModelMapBuilder result = ModelMapBuilder.newExpandedInstance();
        for (Content content : contents) {
            this.addExpanderProperties(content, frontendExpansions, (Map<Long, ContentMacroNames>)contentIdsToMacroNames, result);
        }
        return result.build();
    }

    private void addExpanderProperties(Content content, Expansions frontendExpansions, Map<Long, ContentMacroNames> contentIdsToMacroNames, ModelMapBuilder<Object, Map<String, ?>> map) {
        ModelMapBuilder builder = ModelMapBuilder.newInstance();
        this.addExpanderProperties(content, frontendExpansions, contentIdsToMacroNames, (ModelMapBuilder<String, Object>)builder, SPA_FRIENDLY_KEY, MACRO_NAMES_NOT_SPA_FRIENDLY_KEY, false);
        this.addExpanderProperties(content, frontendExpansions, contentIdsToMacroNames, (ModelMapBuilder<String, Object>)builder, COMMENT_SPA_FRIENDLY_KEY, COMMENT_MACRO_NAMES_NOT_SPA_FRIENDLY_KEY, true);
        Map frontendMap = ModelMapBuilder.newExpandedInstance().put((Object)FRONTEND_KEY, (Object)builder.build()).build();
        map.put((Object)content, (Object)frontendMap);
    }

    private void addExpanderProperties(Content content, Expansions frontendExpansions, Map<Long, ContentMacroNames> contentIdsToMacroNames, ModelMapBuilder<String, Object> builder, String spaFriendlyKey, String macroNamesNotSpaFriendlyKey, boolean useComments) {
        Iterable<String> macroNamesNotSpaFriendly;
        boolean hasMacroNamesNotSpaFriendly = frontendExpansions.canExpand(macroNamesNotSpaFriendlyKey);
        boolean hasSpaFriendly = frontendExpansions.canExpand(spaFriendlyKey);
        if ((hasMacroNamesNotSpaFriendly || hasSpaFriendly) && (macroNamesNotSpaFriendly = this.getMacroNamesNotSpaFriendly(content, contentIdsToMacroNames, useComments)) != null) {
            if (hasMacroNamesNotSpaFriendly) {
                builder.put((Object)macroNamesNotSpaFriendlyKey, (Object)Lists.newArrayList(macroNamesNotSpaFriendly));
            }
            if (hasSpaFriendly) {
                builder.put((Object)spaFriendlyKey, (Object)Iterables.isEmpty(macroNamesNotSpaFriendly));
            }
        }
    }

    @Deprecated
    public List<String> getMetadataProperties() {
        return METADATA_PROPERTY_NAMES;
    }

    public List<MetadataProperty> getProperties() {
        MetadataProperty property = new MetadataProperty(FRONTEND_KEY, (List)Lists.newArrayList((Object[])new MetadataProperty[]{new MetadataProperty(SPA_FRIENDLY_KEY, Boolean.class), new MetadataProperty(COMMENT_SPA_FRIENDLY_KEY, Boolean.class), new MetadataProperty(MACRO_NAMES_NOT_SPA_FRIENDLY_KEY, (Type)TypeUtils.parameterize(List.class, (Type[])new Type[]{String.class})), new MetadataProperty(COMMENT_MACRO_NAMES_NOT_SPA_FRIENDLY_KEY, (Type)TypeUtils.parameterize(List.class, (Type[])new Type[]{String.class}))}));
        return Collections.singletonList(property);
    }

    private Iterable<String> getMacroNamesNotSpaFriendly(Content content, Map<Long, ContentMacroNames> contentIdsToMacroNames, boolean useComments) {
        ContentMacroNames contentMacroNames = contentIdsToMacroNames.get(content.getId().asLong());
        if (contentMacroNames == null) {
            return null;
        }
        Collection<String> macroNames = useComments ? contentMacroNames.getCommentMacroNames() : contentMacroNames.getMacroNames();
        return macroNames != null ? this.getMacroNamesNotSpaFriendly(macroNames) : null;
    }

    private Iterable<String> getMacroNamesNotSpaFriendly(Collection<String> macroNames) {
        return Iterables.filter(macroNames, macroName -> {
            Macro macro = this.macroManager.getMacroByName(macroName);
            return macro != null && !this.macroAsyncRenderWhitelist.isAsyncRenderSafe(macroName, macro.getClass());
        });
    }
}

