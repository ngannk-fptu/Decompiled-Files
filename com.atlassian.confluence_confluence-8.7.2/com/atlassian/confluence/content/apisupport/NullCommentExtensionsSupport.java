/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  io.atlassian.fugue.Iterables
 */
package com.atlassian.confluence.content.apisupport;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.content.apisupport.CommentExtensionsSupport;
import com.atlassian.confluence.pages.Comment;
import io.atlassian.fugue.Iterables;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class NullCommentExtensionsSupport
implements CommentExtensionsSupport {
    @Override
    public Iterable<ContentType> getCommentContainerType() {
        return Iterables.emptyIterable();
    }

    @Override
    public Map<ContentId, Map<String, Object>> getExtensions(Iterable<Comment> comments, Expansions expansions) {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, Optional<String>> expansions() {
        return Collections.emptyMap();
    }

    @Override
    public ValidationResult validateExtensionsForCreate(Map<String, Object> extensions, SimpleValidationResult.Builder validationResultBuilder) {
        return validationResultBuilder.build();
    }

    @Override
    public ValidationResult validateExtensionsForUpdate(Comment comment, Map<String, Object> extensions, SimpleValidationResult.Builder validationResultBuilder) {
        return validationResultBuilder.build();
    }

    @Override
    public void updateExtensionsOnEntity(Comment comment, Map<String, Object> extensions) {
    }
}

