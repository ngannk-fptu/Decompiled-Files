/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.link.Link
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.fugue.Option
 *  com.google.common.base.Predicate
 */
package com.atlassian.confluence.content.apisupport;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.link.Link;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.ContentConvertible;
import com.atlassian.confluence.util.FugueConversionUtil;
import com.atlassian.fugue.Option;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@ExperimentalSpi
public interface ContentTypeApiSupport<T extends ContentConvertible> {
    public ContentType getHandledType();

    @Deprecated
    default public PageResponse<Content> getChildren(ContentConvertible content, LimitedRequest limitedRequest, Expansions expansions, Depth depth, com.google.common.base.Predicate<? super ContentEntityObject> predicate) {
        return this.getFilteredChildren(content, limitedRequest, expansions, depth, arg_0 -> predicate.apply(arg_0));
    }

    public PageResponse<Content> getFilteredChildren(ContentConvertible var1, LimitedRequest var2, Expansions var3, Depth var4, Predicate<? super ContentEntityObject> var5);

    public boolean supportsChildrenOfType(ContentType var1);

    public boolean supportsChildrenForParentType(ContentType var1);

    public Map<ContentId, Map<String, Object>> getExtensions(Iterable<T> var1, Expansions var2);

    public List<Link> getLinks(T var1);

    @Deprecated
    default public Option<Object> getContainer(T contentEntity, Expansions expansions) {
        return FugueConversionUtil.toComOption(this.container(contentEntity, expansions));
    }

    public Optional<Object> container(T var1, Expansions var2);

    public Class<T> getEntityClass();

    public T create(Content var1);

    public T update(Content var1, T var2);

    public ValidationResult validateCreate(Content var1);

    public ValidationResult validateUpdate(Content var1, T var2);
}

