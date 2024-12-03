/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 */
package com.atlassian.confluence.plugins.dailysummary.content.popular;

import com.atlassian.confluence.plugins.dailysummary.content.popular.PopularContentExcerptDto;
import java.util.List;
import java.util.Map;
import javax.activation.DataSource;

public class PopularContentContext {
    private static String POPULAR_CONTENT_EXCERPTS_KEY = "summary-email-popular-content-excerpts";
    private static String COMMENT_ICON_DATASOURCE_KEY = "popular-content-comment-icon";
    private static String LIKE_ICON_DATASOURCE_KEY = "popular-content-like-icon";
    private Map<String, Object> context;

    public PopularContentContext(Map<String, Object> context) {
        this.context = context;
    }

    public List<PopularContentExcerptDto> getPopularContentExcerpts() {
        return (List)this.context.get(POPULAR_CONTENT_EXCERPTS_KEY);
    }

    public void setPopularContentExcerpts(List<PopularContentExcerptDto> popularContentExcerpts) {
        this.context.put(POPULAR_CONTENT_EXCERPTS_KEY, popularContentExcerpts);
    }

    public DataSource getLikeIconDatasource() {
        return (DataSource)this.context.get(LIKE_ICON_DATASOURCE_KEY);
    }

    public void setLikeIconDatasource(DataSource likeIconDatasource) {
        this.context.put(LIKE_ICON_DATASOURCE_KEY, likeIconDatasource);
    }

    public DataSource getCommentIconDatasource() {
        return (DataSource)this.context.get(COMMENT_ICON_DATASOURCE_KEY);
    }

    public void setCommentIconDatasource(DataSource commentIconDatasource) {
        this.context.put(COMMENT_ICON_DATASOURCE_KEY, commentIconDatasource);
    }
}

