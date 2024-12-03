/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.api.service.content.template;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.template.ContentBlueprintInstance;
import com.atlassian.confluence.api.model.content.template.ContentTemplate;
import com.atlassian.confluence.api.model.content.template.ContentTemplateId;
import com.atlassian.confluence.api.model.content.template.ContentTemplateType;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.api.service.finder.ManyFetcher;
import com.atlassian.confluence.api.service.finder.SingleFetcher;
import com.atlassian.fugue.Option;
import java.util.Optional;

@ExperimentalApi
public interface ContentTemplateService {
    @Deprecated
    default public PageResponse<ContentTemplate> getTemplates(ContentTemplateType contentTemplateType, Option<Space> space, PageRequest pageRequest, Expansion ... expansions) {
        return this.getTemplates(contentTemplateType, Optional.of((Space)space.getOrNull()), pageRequest, expansions);
    }

    public PageResponse<ContentTemplate> getTemplates(ContentTemplateType var1, Optional<Space> var2, PageRequest var3, Expansion ... var4);

    public ContentTemplate getTemplate(ContentTemplateId var1, Expansion ... var2);

    public ContentTemplate create(ContentTemplate var1, Expansion ... var2);

    public ContentTemplate update(ContentTemplate var1, Expansion ... var2);

    public void delete(ContentTemplateId var1);

    public Validator validator(ContentTemplateType var1);

    public TemplateFinder find(Expansion ... var1);

    default public ContentBlueprintInstance createInstance(ContentBlueprintInstance blueprintInstance, Expansion ... expansions) {
        throw new NotImplementedServiceException("Not implemented yet, to be updated in CRA-1266");
    }

    public static interface TemplateFinder
    extends ParameterTemplateFinder {
        public SingleFetcher<ContentTemplate> withId(ContentTemplateId var1);
    }

    public static interface ParameterTemplateFinder
    extends SingleFetcher<ContentTemplate>,
    ManyFetcher<ContentTemplate> {
        public ParameterTemplateFinder withSpace(Space var1);

        public ParameterTemplateFinder withType(ContentTemplateType var1);
    }

    public static interface Validator {
        public ValidationResult validateDelete(ContentTemplateId var1);

        public ValidationResult validateCreate(ContentTemplate var1) throws ServiceException;

        public ValidationResult validateUpdate(ContentTemplate var1);

        public ValidationResult validateGet(ContentTemplateId var1);

        default public ValidationResult validateCreateInstance(ContentBlueprintInstance instance) {
            throw new NotImplementedServiceException("Not implemented yet, to be updated in CRA-1266");
        }
    }
}

