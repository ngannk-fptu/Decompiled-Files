/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.api.service.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.content.Label;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import java.util.Collection;
import org.checkerframework.checker.nullness.qual.NonNull;

@ExperimentalApi
public interface ContentLabelService {
    public PageResponse<Label> getLabels(ContentId var1, Collection<Label.Prefix> var2, PageRequest var3) throws NotFoundException;

    public PageResponse<Label> addLabels(ContentId var1, Iterable<Label> var2) throws ServiceException;

    @Deprecated
    public void removeLabel(ContentId var1, String var2);

    public void removeLabel(@NonNull ContentId var1, @NonNull Label var2);

    public Validator validator();

    public static interface Validator {
        public ValidationResult validateAddLabels(ContentId var1, Label ... var2);
    }
}

