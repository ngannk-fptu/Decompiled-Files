/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.service.permissions;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.people.Subject;
import com.atlassian.confluence.api.model.permissions.ContentRestriction;
import com.atlassian.confluence.api.model.permissions.ContentRestrictionsPageResponse;
import com.atlassian.confluence.api.model.permissions.OperationKey;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import java.util.Collection;
import java.util.Map;

@ExperimentalApi
public interface ContentRestrictionService {
    public static final String DEFAULT_BY_OPERATION_EXPANSIONS = "update.restrictions.user,read.restrictions.group,read.restrictions.user,update.restrictions.group";
    public static final String DEFAULT_FOR_OPERATION_EXPANSIONS = "restrictions.user,restrictions.group";
    public static final String DEFAULT_FOR_OPERATION_AND_CONTENT_EXPANSIONS = "restrictions.user,restrictions.group,content";

    public Validator validator();

    public Map<OperationKey, ContentRestriction> getRestrictionsGroupByOperation(ContentId var1, Expansion ... var2) throws ServiceException;

    public ContentRestriction getRestrictionsForOperation(ContentId var1, OperationKey var2, PageRequest var3, Expansion ... var4) throws ServiceException;

    public ContentRestrictionsPageResponse getRestrictions(ContentId var1, PageRequest var2, Expansion ... var3) throws NotFoundException;

    public ContentRestrictionsPageResponse updateRestrictions(ContentId var1, Collection<? extends ContentRestriction> var2, Expansion ... var3) throws ServiceException;

    public ContentRestrictionsPageResponse addRestrictions(ContentId var1, Collection<? extends ContentRestriction> var2, Expansion ... var3) throws ServiceException;

    public ContentRestrictionsPageResponse deleteAllDirectRestrictions(ContentId var1, Expansion ... var2) throws ServiceException;

    public boolean hasDirectRestrictionForSubject(ContentId var1, OperationKey var2, Subject var3) throws ServiceException;

    public void deleteDirectRestrictionForSubject(ContentId var1, OperationKey var2, Subject var3) throws ServiceException;

    public void addDirectRestrictionForSubject(ContentId var1, OperationKey var2, Subject var3) throws ServiceException;

    public static interface Validator {
        public ValidationResult validateGetRestrictions(ContentId var1);

        public ValidationResult validateGetRestrictionsForOperation(ContentId var1, OperationKey var2);

        public ValidationResult validateUpdateRestrictions(ContentId var1, Collection<? extends ContentRestriction> var2);

        public ValidationResult validateAddRestrictions(ContentId var1, Collection<? extends ContentRestriction> var2);

        public ValidationResult validateDeleteAllDirectRestrictions(ContentId var1);

        public ValidationResult validateHasDirectRestrictionsForSubject(ContentId var1, OperationKey var2, Subject var3);

        public ValidationResult validateDeleteDirectRestrictionForSubject(ContentId var1, OperationKey var2, Subject var3);

        public ValidationResult validateAddDirectRestrictionForSubject(ContentId var1, OperationKey var2, Subject var3);
    }
}

