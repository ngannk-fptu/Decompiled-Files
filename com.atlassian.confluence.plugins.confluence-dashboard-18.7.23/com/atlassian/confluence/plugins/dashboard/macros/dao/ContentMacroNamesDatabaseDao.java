/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.persistence.EntityManagerProvider
 *  com.google.common.collect.Lists
 *  javax.persistence.Query
 */
package com.atlassian.confluence.plugins.dashboard.macros.dao;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.persistence.EntityManagerProvider;
import com.atlassian.confluence.plugins.dashboard.macros.dao.ContentMacroNames;
import com.atlassian.confluence.plugins.dashboard.macros.dao.ContentMacroNamesDao;
import com.google.common.collect.Lists;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.persistence.Query;

public class ContentMacroNamesDatabaseDao
implements ContentMacroNamesDao {
    private static final String SQL_WITH_COMMENTS = String.join((CharSequence)"\n", "select", "  contentOrComment.contentid as contentId,", "  (case when contentOrComment.contenttype = 'COMMENT'", "        then contentOrComment.pageid", "        else null", "        end) as commentParentContentId,", "  stringval as macroNames", "from content as contentOrComment", "left outer join contentproperties property", "                on contentOrComment.contentid = property.contentid", "               and property.propertyname = 'macroNames'", "               and property.stringval != ','", "where contentOrComment.contentid in (:contentIds)", "   or (contentOrComment.contenttype = 'COMMENT' and contentOrComment.pageid in (:contentIds))");
    private static final String SQL_NO_COMMENTS = String.join((CharSequence)"\n", "select", "  content.contentid as contentId,", "  stringval as macroNames", "from content as content", "left outer join contentproperties property", "                on content.contentid = property.contentid", "               and property.propertyname = 'macroNames'", "               and property.stringval != ','", "where content.contentid in (:contentIds)");
    private static final int COLUMN_INDEX_CONTENT_ID = 0;
    private static final int COLUMN_INDEX_MACRO_NAMES = 1;
    private static final int COLUMN_INDEX_INCLUDE_COMMENTS_MACRO_NAMES = 2;
    private static final int COLUMN_INDEX_COMMENT_PARENT_CONTENT_ID = 1;
    private EntityManagerProvider entityManagerProvider;

    public ContentMacroNamesDatabaseDao(EntityManagerProvider entityManagerProvider) {
        this.entityManagerProvider = entityManagerProvider;
    }

    @Override
    public List<ContentMacroNames> getContentMacroNames(Iterable<Content> contents, List<ContentMacroNames> knownMacroNamesAndComments, boolean includeComments) {
        List contentIds = StreamSupport.stream(contents.spliterator(), false).map(x -> x.getId().asLong()).collect(Collectors.toList());
        String sql = includeComments ? SQL_WITH_COMMENTS : SQL_NO_COMMENTS;
        Query query = this.entityManagerProvider.getEntityManager().createNativeQuery(sql);
        query.setParameter("contentIds", contentIds);
        List results = query.getResultList();
        return this.readMacroNames(results, knownMacroNamesAndComments, includeComments);
    }

    private List<ContentMacroNames> readMacroNames(List<Object[]> results, List<ContentMacroNames> knownMacroNamesAndComments, boolean includeComments) {
        List<ContentMacroNames> flattenedContentAndComments = this.readMacroNamesFlattened(results, includeComments);
        List<ContentMacroNames> list = includeComments ? ContentMacroNames.makeCommentHierarchy(flattenedContentAndComments) : flattenedContentAndComments;
        return ContentMacroNames.merge(list, knownMacroNamesAndComments);
    }

    private List<ContentMacroNames> readMacroNamesFlattened(List<Object[]> results, boolean includeComments) {
        ArrayList<ContentMacroNames> list = new ArrayList<ContentMacroNames>();
        for (Object[] result : results) {
            String macroNamesStr;
            long contentId = ((BigInteger)result[0]).longValueExact();
            Long commentParentContentId = null;
            if (includeComments) {
                commentParentContentId = ContentMacroNamesDatabaseDao.getLongOrNull((BigInteger)result[1]);
                macroNamesStr = (String)result[2];
            } else {
                macroNamesStr = (String)result[1];
            }
            List<String> macroNames = this.readMacroNames(macroNamesStr);
            list.add(new ContentMacroNames(contentId, commentParentContentId, macroNames, null));
        }
        return list;
    }

    private List<String> readMacroNames(String macroNamesStr) {
        if (macroNamesStr == null) {
            return null;
        }
        if (macroNamesStr.endsWith(",")) {
            return null;
        }
        if (macroNamesStr.length() == 0) {
            return Collections.emptyList();
        }
        return Lists.newArrayList((Object[])macroNamesStr.split(","));
    }

    private static Long getLongOrNull(BigInteger value) {
        return value != null && value.signum() != 0 ? Long.valueOf(value.longValueExact()) : null;
    }
}

