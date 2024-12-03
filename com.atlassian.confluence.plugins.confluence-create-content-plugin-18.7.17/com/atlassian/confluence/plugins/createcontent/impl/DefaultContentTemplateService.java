/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.template.ContentBlueprintInstance
 *  com.atlassian.confluence.api.model.content.template.ContentTemplate
 *  com.atlassian.confluence.api.model.content.template.ContentTemplateId
 *  com.atlassian.confluence.api.model.content.template.ContentTemplateId$ContentTemplateIdWithId
 *  com.atlassian.confluence.api.model.content.template.ContentTemplateId$ContentTemplateIdWithKeys
 *  com.atlassian.confluence.api.model.content.template.ContentTemplateId$ContentTemplateIdWithUUID
 *  com.atlassian.confluence.api.model.content.template.ContentTemplateType
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.service.content.template.ContentTemplateService
 *  com.atlassian.confluence.api.service.content.template.ContentTemplateService$TemplateFinder
 *  com.atlassian.confluence.api.service.content.template.ContentTemplateService$Validator
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.content.template.ContentBlueprintInstance;
import com.atlassian.confluence.api.model.content.template.ContentTemplate;
import com.atlassian.confluence.api.model.content.template.ContentTemplateId;
import com.atlassian.confluence.api.model.content.template.ContentTemplateType;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.service.content.template.ContentTemplateService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.plugins.createcontent.api.services.BlueprintContentTemplateService;
import com.atlassian.confluence.plugins.createcontent.api.services.PageContentTemplateService;
import com.atlassian.confluence.plugins.createcontent.factory.TemplateFinderFactory;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ExportAsService(value={ContentTemplateService.class})
@Component
public class DefaultContentTemplateService
implements ContentTemplateService {
    private final PageContentTemplateService pageContentTemplateService;
    private final BlueprintContentTemplateService blueprintContentTemplateService;
    private final TemplateFinderFactory templateFinderFactory;

    @Autowired
    public DefaultContentTemplateService(PageContentTemplateService pageContentTemplateService, BlueprintContentTemplateService blueprintContentTemplateService, TemplateFinderFactory templateFinderFactory) {
        this.pageContentTemplateService = pageContentTemplateService;
        this.blueprintContentTemplateService = blueprintContentTemplateService;
        this.templateFinderFactory = templateFinderFactory;
    }

    public PageResponse<ContentTemplate> getTemplates(ContentTemplateType contentTemplateType, Option<Space> space, PageRequest pageRequest, Expansion ... expansions) {
        return this.getTemplates(contentTemplateType, Optional.ofNullable((Space)space.getOrNull()), pageRequest, expansions);
    }

    public PageResponse<ContentTemplate> getTemplates(ContentTemplateType contentTemplateType, Optional<Space> space, PageRequest pageRequest, Expansion ... expansions) {
        PageResponse result;
        if (contentTemplateType.equals((Object)ContentTemplateType.BLUEPRINT)) {
            result = this.blueprintContentTemplateService.getTemplates(contentTemplateType, space, pageRequest, expansions);
        } else if (contentTemplateType.equals((Object)ContentTemplateType.PAGE)) {
            result = this.pageContentTemplateService.getTemplates(contentTemplateType, space, pageRequest, expansions);
        } else {
            throw new NotImplementedServiceException("Unknown template type: " + contentTemplateType);
        }
        return result;
    }

    public PageResponse<ContentTemplate> getTemplates(Optional<Space> space, PageRequest pageRequest, Expansion ... expansions) {
        throw new NotImplementedServiceException("This method has been deprecated on interface, will remove.");
    }

    public ContentTemplate getTemplate(ContentTemplateId contentTemplateId, Expansion ... expansions) {
        if (contentTemplateId instanceof ContentTemplateId.ContentTemplateIdWithId) {
            return this.pageContentTemplateService.getTemplate(contentTemplateId, expansions);
        }
        if (contentTemplateId instanceof ContentTemplateId.ContentTemplateIdWithUUID || contentTemplateId instanceof ContentTemplateId.ContentTemplateIdWithKeys) {
            return this.blueprintContentTemplateService.getTemplate(contentTemplateId, expansions);
        }
        throw new BadRequestException("Expect an id (long), (UUID) or (\"ModuleCompleteKey@SpaceKey\"), but received: " + contentTemplateId);
    }

    public ContentTemplate create(ContentTemplate contentTemplate, Expansion ... expansions) {
        try {
            if (contentTemplate.getTemplateType().equals((Object)ContentTemplateType.PAGE)) {
                return this.pageContentTemplateService.create(contentTemplate, expansions);
            }
            if (contentTemplate.getTemplateType().equals((Object)ContentTemplateType.BLUEPRINT)) {
                return this.blueprintContentTemplateService.create(contentTemplate, expansions);
            }
        }
        catch (Exception e) {
            throw new BadRequestException((Throwable)e);
        }
        throw new BadRequestException("Unsupported template: " + contentTemplate);
    }

    public ContentTemplate update(ContentTemplate contentTemplate, Expansion ... expansions) {
        return this.pageContentTemplateService.update(contentTemplate, expansions);
    }

    public void delete(ContentTemplateId contentTemplateId) {
        this.pageContentTemplateService.delete(contentTemplateId);
    }

    public ContentTemplateService.Validator validator() {
        return null;
    }

    public ContentTemplateService.Validator validator(ContentTemplateType contentTemplateType) {
        if (contentTemplateType.equals((Object)ContentTemplateType.PAGE)) {
            return this.pageContentTemplateService.validator(contentTemplateType);
        }
        if (contentTemplateType.equals((Object)ContentTemplateType.BLUEPRINT)) {
            return this.blueprintContentTemplateService.validator(contentTemplateType);
        }
        throw new BadRequestException("Unsupported template type: " + contentTemplateType);
    }

    public ContentTemplateService.TemplateFinder find(Expansion ... expansions) {
        return this.templateFinderFactory.createFinder(this, expansions);
    }

    public ContentBlueprintInstance createInstance(ContentBlueprintInstance blueprintInstance, Expansion ... expansions) {
        return this.pageContentTemplateService.createInstance(blueprintInstance, expansions);
    }
}

