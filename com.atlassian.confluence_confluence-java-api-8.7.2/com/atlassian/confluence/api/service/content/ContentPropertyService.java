/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.service.content;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.content.JsonContentProperty;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.content.id.JsonContentPropertyId;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.ConflictException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.finder.ManyFetcher;
import com.atlassian.confluence.api.service.finder.SingleFetcher;
import java.util.Iterator;
import java.util.List;

public interface ContentPropertyService {
    public static final int MAXIMUM_KEY_LENGTH = 255;
    public static final int MAXIMUM_VALUE_LENGTH = 32768;
    public static final int MAXIMUM_PROPERTIES_PER_PAGE_REQUEST = 100;

    public JsonContentProperty create(JsonContentProperty var1) throws ServiceException;

    public ContentPropertyFinder find(Expansion ... var1);

    public JsonContentProperty update(JsonContentProperty var1) throws ServiceException;

    public void delete(JsonContentProperty var1) throws ServiceException;

    default public void copyAllJsonContentProperties(ContentSelector source, ContentSelector target) throws ServiceException {
    }

    public Validator validator();

    public static interface Validator {
        public ValidationResult validateCreate(JsonContentProperty var1);

        public ValidationResult validateUpdate(JsonContentProperty var1) throws ConflictException;

        public ValidationResult validateDelete(JsonContentProperty var1);
    }

    public static interface SingleContentPropertyFetcher
    extends SingleFetcher<JsonContentProperty> {
    }

    public static interface ContentPropertyFetcher
    extends SingleContentPropertyFetcher,
    ManyFetcher<JsonContentProperty> {
    }

    public static interface ParameterContentPropertyFinder
    extends ContentPropertyFetcher {
        public ParameterContentPropertyFinder withContentId(ContentId var1);

        public ParameterContentPropertyFinder withContentIds(List<ContentId> var1);

        @Deprecated
        public ParameterContentPropertyFinder withKey(String var1);

        public ParameterContentPropertyFinder withPropertyKey(String var1);

        public ParameterContentPropertyFinder withPropertyKeys(List<String> var1);

        public Iterator<String> fetchPropertyKeys();
    }

    public static interface ContentPropertyFinder
    extends ParameterContentPropertyFinder {
        public SingleContentPropertyFetcher withId(JsonContentPropertyId var1);
    }
}

