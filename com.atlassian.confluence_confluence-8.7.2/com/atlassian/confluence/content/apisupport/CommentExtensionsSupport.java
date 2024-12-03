/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.content.apisupport;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.content.apisupport.NullCommentExtensionsSupport;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.util.FugueConversionUtil;
import com.atlassian.fugue.Option;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Internal
public interface CommentExtensionsSupport {
    public static final CommentExtensionsSupport NULL_OBJECT = new NullCommentExtensionsSupport();

    public Iterable<ContentType> getCommentContainerType();

    public Map<ContentId, Map<String, Object>> getExtensions(Iterable<Comment> var1, Expansions var2);

    @Deprecated
    default public Map<String, Option<String>> getExpansions() {
        return this.expansions().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> FugueConversionUtil.toComOption((Optional)e.getValue())));
    }

    public Map<String, Optional<String>> expansions();

    public ValidationResult validateExtensionsForCreate(Map<String, Object> var1, SimpleValidationResult.Builder var2);

    public ValidationResult validateExtensionsForUpdate(Comment var1, Map<String, Object> var2, SimpleValidationResult.Builder var3);

    public void updateExtensionsOnEntity(Comment var1, Map<String, Object> var2);
}

