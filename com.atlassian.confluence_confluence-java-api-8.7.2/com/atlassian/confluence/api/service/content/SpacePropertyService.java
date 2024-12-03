/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.service.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.JsonSpaceProperty;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.finder.ManyFetcher;
import com.atlassian.confluence.api.service.finder.SingleFetcher;

@ExperimentalApi
public interface SpacePropertyService {
    public JsonSpaceProperty create(JsonSpaceProperty var1) throws ServiceException;

    public SpacePropertyFinder find(Expansion ... var1);

    public JsonSpaceProperty update(JsonSpaceProperty var1);

    public void delete(JsonSpaceProperty var1);

    public Validator validator();

    public static interface Validator {
        public ValidationResult validateCreate(JsonSpaceProperty var1);

        public ValidationResult validateUpdate(JsonSpaceProperty var1);

        public ValidationResult validateDelete(JsonSpaceProperty var1);
    }

    public static interface SpacePropertyFinder
    extends ManyFetcher<JsonSpaceProperty>,
    SingleFetcher<JsonSpaceProperty> {
        public SpacePropertyFinder withSpaceKey(String var1);

        public SpacePropertyFinder withPropertyKey(String var1);
    }
}

