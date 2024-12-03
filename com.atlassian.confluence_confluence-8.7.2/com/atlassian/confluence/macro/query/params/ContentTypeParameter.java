/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.query.params;

import com.atlassian.confluence.macro.MacroExecutionContext;
import com.atlassian.confluence.macro.query.SearchQueryInterpreter;
import com.atlassian.confluence.macro.query.params.BooleanQueryFactoryParameter;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.CustomContentTypeQuery;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContentTypeParameter
extends BooleanQueryFactoryParameter {
    private static final String[] DEFAULT_PARAM_NAMES = new String[]{"type"};

    public ContentTypeParameter() {
        this(null);
    }

    public ContentTypeParameter(String defaultValue) {
        super(DEFAULT_PARAM_NAMES, defaultValue);
    }

    public ContentTypeParameter(List<String> names, String defaultValue) {
        super(names, defaultValue);
    }

    @Override
    protected SearchQueryInterpreter createSearchQueryInterpreter(MacroExecutionContext ctx) {
        return new Interpreter();
    }

    static class Interpreter
    implements SearchQueryInterpreter {
        private static final Set<String> VALUE_NEWS = new HashSet<String>(Arrays.asList("news", "blog", "blogs", "blog-post", "blogposts", "blog-posts"));
        private static final Set<String> VALUE_PAGE = new HashSet<String>(Arrays.asList("page", "pages"));
        private static final Set<String> VALUE_ATTACHMENT = new HashSet<String>(Arrays.asList("attachment", "attachments"));
        private static final Set<String> VALUE_COMMENT = new HashSet<String>(Arrays.asList("comment", "comments"));

        Interpreter() {
        }

        @Override
        public SearchQuery createSearchQuery(String value) {
            ContentTypeEnum contentType = VALUE_NEWS.contains(value = value.toLowerCase()) ? ContentTypeEnum.BLOG : (VALUE_PAGE.contains(value) ? ContentTypeEnum.PAGE : (VALUE_ATTACHMENT.contains(value) ? ContentTypeEnum.ATTACHMENT : (VALUE_COMMENT.contains(value) ? ContentTypeEnum.COMMENT : ContentTypeEnum.getByRepresentation(value))));
            if (contentType != null) {
                return new ContentTypeQuery(contentType);
            }
            return new CustomContentTypeQuery(value);
        }
    }
}

