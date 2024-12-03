/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.guardrails.logs;

import com.atlassian.migration.agent.service.guardrails.logs.PageType;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.Generated;

public class PageTypeProvider {
    private static final Set<String> WRITE_METHODS = ImmutableSet.of((Object)"POST", (Object)"PUT");
    private static final Set<String> READ_METHODS = ImmutableSet.of((Object)"GET");
    private final Map<String, List<PageTypeMatcher>> matchersByMethod = new HashMap<String, List<PageTypeMatcher>>();

    public PageTypeProvider() {
        this.addReadMatchers();
        this.addWriteMatchers();
    }

    private void addReadMatchers() {
        this.addReadMatcher(".*/pages/viewpage.action.*", PageType.VIEW_PAGE);
        this.addReadMatcher(".*/display/[^.]+/\\d{4}/\\d{2}/\\d{2}/.*", PageType.VIEW_BLOG);
        this.addReadMatcher(".*/display/[^.]+/.*", PageType.VIEW_PAGE);
        this.addReadMatcher(".*/", PageType.VIEW_HOME_PAGE);
        this.addReadMatcher(".*/pages/editpage.action.*", PageType.EDIT_PAGE);
        this.addReadMatcher(".*/rest/quicknav/1/search\\?query=.*", PageType.QUICK_SEARCH);
        this.addReadMatcher(".*/dosearchsite.action.*", PageType.ADVANCED_SEARCH);
        this.addReadMatcher(".*/pages/viewpreviousversions.action?.*", PageType.PAGE_HISTORY_VIEW);
        this.addReadMatcher(".*/content/search\\?cql=content.*", PageType.CQL_RANDOM_PAGEID_SEARCH);
        this.addReadMatcher(".*/rest/searchv3/\\d+(\\.\\d+)?/cqlSearch.*cql=.*contributor.*", PageType.CQL_SEARCH_BY_USERNAME);
        this.addReadMatcher(".*/rest/searchv3/\\d+(\\.\\d+)?/cqlSearch.*cql=.*title.*", PageType.CQL_SEARCH_BY_TITLE);
        this.addReadMatcher(".*/rest/searchv3/\\d+(\\.\\d+)?/cqlSearch.*cql=.*content.*", PageType.CQL_RANDOM_PAGEID_SEARCH);
    }

    private void addWriteMatchers() {
        this.addWriteMatcher(".*/rest/tinymce/(.*)/content/(.*)/comment\\?.*", PageType.ADD_COMMENT);
        this.addWriteMatcher(".*/rest/ui/(.*)/content/(.*)/labels", PageType.ADD_LABELS);
        this.addWriteMatcher(".*/rest/api/content/(\\d+).*", PageType.PUBLISH_PAGE);
        this.addWriteMatcher(".*/rest/tinymce/(.*)/drafts.*", PageType.CREATE_DRAFT);
        this.addWriteMatcher(".*/rest/likes/(.*)/content/(.*)/likes.*", PageType.LIKE_PAGE_OR_COMMENT);
        this.addWriteMatcher(".*/rest/inlinecomments/(.*)/comments.*", PageType.ADD_OR_RESOLVE_INLINE_COMMENT);
        this.addWriteMatcher(".*/doeditspacepermissions.action.*", PageType.CHANGE_SPACE_PERMISSIONS_FOR_GROUP);
    }

    private void addWriteMatcher(String pattern, PageType pageType) {
        this.addMatcher(WRITE_METHODS, pattern, pageType);
    }

    private void addReadMatcher(String pattern, PageType pageType) {
        this.addMatcher(READ_METHODS, pattern, pageType);
    }

    private void addMatcher(Set<String> methods, String pattern, PageType pageType) {
        PageTypeMatcher matcher = new PageTypeMatcher(pattern, pageType);
        for (String method : methods) {
            this.matchersByMethod.computeIfAbsent(method, v -> new ArrayList()).add(matcher);
        }
    }

    public PageType pageType(String method, String path) {
        for (PageTypeMatcher matcher : this.matchersByMethod.getOrDefault(method, Collections.emptyList())) {
            if (!matcher.matches(path)) continue;
            return matcher.getPageType();
        }
        return PageType.UNKNOWN;
    }

    private static final class PageTypeMatcher {
        private final Pattern pathPattern;
        private final PageType pageType;

        public PageTypeMatcher(String pathPattern, PageType pageType) {
            this.pathPattern = Pattern.compile(pathPattern);
            this.pageType = pageType;
        }

        boolean matches(String path) {
            return this.pathPattern.matcher(path).matches();
        }

        @Generated
        public Pattern getPathPattern() {
            return this.pathPattern;
        }

        @Generated
        public PageType getPageType() {
            return this.pageType;
        }

        @Generated
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof PageTypeMatcher)) {
                return false;
            }
            PageTypeMatcher other = (PageTypeMatcher)o;
            Pattern this$pathPattern = this.getPathPattern();
            Pattern other$pathPattern = other.getPathPattern();
            if (this$pathPattern == null ? other$pathPattern != null : !this$pathPattern.equals(other$pathPattern)) {
                return false;
            }
            PageType this$pageType = this.getPageType();
            PageType other$pageType = other.getPageType();
            return !(this$pageType == null ? other$pageType != null : !((Object)((Object)this$pageType)).equals((Object)other$pageType));
        }

        @Generated
        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            Pattern $pathPattern = this.getPathPattern();
            result = result * 59 + ($pathPattern == null ? 43 : $pathPattern.hashCode());
            PageType $pageType = this.getPageType();
            result = result * 59 + ($pageType == null ? 43 : ((Object)((Object)$pageType)).hashCode());
            return result;
        }

        @Generated
        public String toString() {
            return "PageTypeProvider.PageTypeMatcher(pathPattern=" + this.getPathPattern() + ", pageType=" + (Object)((Object)this.getPageType()) + ")";
        }
    }
}

