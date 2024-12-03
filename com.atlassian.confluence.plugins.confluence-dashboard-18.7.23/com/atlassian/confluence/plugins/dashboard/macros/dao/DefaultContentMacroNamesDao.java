/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 */
package com.atlassian.confluence.plugins.dashboard.macros.dao;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.plugins.dashboard.macros.dao.ContentMacroNames;
import com.atlassian.confluence.plugins.dashboard.macros.dao.ContentMacroNamesDao;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultContentMacroNamesDao
implements ContentMacroNamesDao {
    private final ContentMacroNamesDao primary;
    private final ContentMacroNamesDao fallback;

    public DefaultContentMacroNamesDao(ContentMacroNamesDao dbDao, ContentMacroNamesDao luceneDao) {
        this.primary = dbDao;
        this.fallback = luceneDao;
    }

    @Override
    public List<ContentMacroNames> getContentMacroNames(Iterable<Content> contents, List<ContentMacroNames> knownMacroNamesAndComments, boolean includeComments) {
        List<ContentMacroNames> list = this.primary.getContentMacroNames(contents, knownMacroNamesAndComments, includeComments);
        Map<Long, ContentMacroNames> map = list.stream().collect(Collectors.toMap(ContentMacroNames::getContentId, c -> c));
        Iterable missing = Iterables.filter(contents, c -> !DefaultContentMacroNamesDao.hasCompleteMacroNameList(map, c, includeComments));
        if (!Iterables.isEmpty((Iterable)missing)) {
            for (ContentMacroNames macroNames : this.fallback.getContentMacroNames(missing, list, includeComments)) {
                map.put(macroNames.getContentId(), macroNames);
            }
        }
        return ImmutableList.copyOf(map.values());
    }

    private static boolean hasCompleteMacroNameList(Map<Long, ContentMacroNames> map, Content content, boolean includeComments) {
        ContentMacroNames contentMacroNames = map.get(content.getId().asLong());
        Collection<String> macroNames = includeComments ? contentMacroNames.getMacroNamesIncludingComments() : contentMacroNames.getMacroNames();
        return macroNames != null;
    }
}

