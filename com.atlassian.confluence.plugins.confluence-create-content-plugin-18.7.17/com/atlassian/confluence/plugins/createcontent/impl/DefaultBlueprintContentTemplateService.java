/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.ContentBody
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.template.ContentTemplate
 *  com.atlassian.confluence.api.model.content.template.ContentTemplateId
 *  com.atlassian.confluence.api.model.content.template.ContentTemplateId$ContentTemplateIdWithId
 *  com.atlassian.confluence.api.model.content.template.ContentTemplateId$ContentTemplateIdWithKeys
 *  com.atlassian.confluence.api.model.content.template.ContentTemplateId$ContentTemplateIdWithUUID
 *  com.atlassian.confluence.api.model.content.template.ContentTemplateType
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.atlassian.confluence.api.model.messages.SimpleMessage
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.content.ContentBodyConversionService
 *  com.atlassian.confluence.api.service.content.template.ContentTemplateService$TemplateFinder
 *  com.atlassian.confluence.api.service.content.template.ContentTemplateService$Validator
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.confluence.pages.templates.PageTemplateManager
 *  com.atlassian.confluence.pages.templates.PluginTemplateReference
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.google.common.base.Strings
 *  com.google.common.collect.Collections2
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.ContentBody;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.template.ContentTemplate;
import com.atlassian.confluence.api.model.content.template.ContentTemplateId;
import com.atlassian.confluence.api.model.content.template.ContentTemplateType;
import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.messages.SimpleMessage;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.content.ContentBodyConversionService;
import com.atlassian.confluence.api.service.content.template.ContentTemplateService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.pages.templates.PluginTemplateReference;
import com.atlassian.confluence.plugins.createcontent.BlueprintStateController;
import com.atlassian.confluence.plugins.createcontent.ContentBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.ContentTemplateRefManager;
import com.atlassian.confluence.plugins.createcontent.api.events.BlueprintTemplateUpdateEvent;
import com.atlassian.confluence.plugins.createcontent.api.services.BlueprintContentTemplateService;
import com.atlassian.confluence.plugins.createcontent.factory.ContentTemplateFactory;
import com.atlassian.confluence.plugins.createcontent.factory.FinderFactory;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.confluence.plugins.createcontent.impl.PluginBackedBlueprint;
import com.atlassian.confluence.plugins.createcontent.model.BlueprintState;
import com.atlassian.confluence.plugins.createcontent.template.PageTemplateGrouper;
import com.atlassian.confluence.plugins.createcontent.template.PluginPageTemplateHelper;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultBlueprintContentTemplateService
implements BlueprintContentTemplateService {
    public static final int MAX_LIMIT = 200;
    private final PageTemplateGrouper pageTemplateGrouper;
    private final BlueprintStateController blueprintStateController;
    private final SpaceManager spaceManager;
    private final PluginPageTemplateHelper pluginPageTemplateHelper;
    private final ContentTemplateRefManager contentTemplateRefManager;
    private final PageTemplateManager pageTemplateManager;
    private final ContentBodyConversionService contentBodyConversionService;
    private final EventPublisher eventPublisher;
    private final ContentBlueprintManager contentBlueprintManager;
    private final PermissionManager permissionManager;
    private final FinderFactory templateFinderFactory;

    @Autowired
    public DefaultBlueprintContentTemplateService(PageTemplateGrouper pageTemplateGrouper, BlueprintStateController blueprintStateController, @ComponentImport SpaceManager spaceManager, PluginPageTemplateHelper pluginPageTemplateHelper, ContentTemplateRefManager contentTemplateRefManager, @ComponentImport PageTemplateManager pageTemplateManager, @ComponentImport ContentBodyConversionService contentBodyConversionService, @ComponentImport EventPublisher eventPublisher, ContentBlueprintManager contentBlueprintManager, @ComponentImport PermissionManager permissionManager, FinderFactory templateFinderFactory) {
        this.pageTemplateGrouper = pageTemplateGrouper;
        this.blueprintStateController = blueprintStateController;
        this.spaceManager = spaceManager;
        this.pluginPageTemplateHelper = pluginPageTemplateHelper;
        this.contentTemplateRefManager = contentTemplateRefManager;
        this.pageTemplateManager = pageTemplateManager;
        this.contentBodyConversionService = contentBodyConversionService;
        this.eventPublisher = eventPublisher;
        this.contentBlueprintManager = contentBlueprintManager;
        this.permissionManager = permissionManager;
        this.templateFinderFactory = templateFinderFactory;
    }

    public PageResponse<ContentTemplate> getTemplates(ContentTemplateType contentTemplateType, Option<com.atlassian.confluence.api.model.content.Space> space, PageRequest pageRequest, Expansion ... expansions) {
        return this.getTemplates(contentTemplateType, Optional.ofNullable((com.atlassian.confluence.api.model.content.Space)space.getOrNull()), pageRequest, expansions);
    }

    public PageResponse<ContentTemplate> getTemplates(ContentTemplateType contentTemplateType, Optional<com.atlassian.confluence.api.model.content.Space> space, PageRequest pageRequest, Expansion ... expansions) {
        this.validator().validateGet(space).throwIfInvalid("Cannot get templates");
        Space mySpace = null;
        if (space.isPresent()) {
            mySpace = this.spaceManager.getSpace(space.get().getKey());
        }
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        Map<UUID, BlueprintState> blueprintStateMap = this.blueprintStateController.getAllContentBlueprintState("system.create.dialog/content", currentUser, mySpace);
        Collection<ContentBlueprint> contentBlueprints = this.pageTemplateGrouper.getSpaceContentBlueprints(mySpace);
        Collection<ContentBlueprint> displayedBlueprints = this.getDisplayableBlueprints(contentBlueprints, blueprintStateMap, space.isPresent());
        List<ContentTemplate> contentTemplates = this.convertToContentTemplates(space, displayedBlueprints, expansions);
        if (pageRequest == null) {
            pageRequest = new SimplePageRequest(0, 200);
        }
        LimitedRequest limitedRequest = LimitedRequestImpl.create((PageRequest)pageRequest, (int)200);
        return PageResponseImpl.filteredPageResponse((LimitedRequest)limitedRequest, contentTemplates, contentTemplate -> true);
    }

    public PageResponse<ContentTemplate> getTemplates(Optional<com.atlassian.confluence.api.model.content.Space> space, PageRequest pageRequest, Expansion ... expansions) {
        return this.getTemplates(ContentTemplateType.BLUEPRINT, space, pageRequest, expansions);
    }

    private List<ContentTemplate> convertToContentTemplates(Optional<com.atlassian.confluence.api.model.content.Space> space, Collection<ContentBlueprint> displayedBlueprints, Expansion ... expansions) {
        ArrayList<ContentTemplate> contentTemplates = new ArrayList<ContentTemplate>();
        for (ContentBlueprint blueprint : displayedBlueprints) {
            blueprint.getContentTemplateRefs().forEach(templateRef -> contentTemplates.add(this.getContentTemplate(space, (ContentTemplateRef)templateRef, expansions)));
            if (blueprint.getIndexPageTemplateRef() == null) continue;
            contentTemplates.add(this.getContentTemplate(space, blueprint.getIndexPageTemplateRef(), expansions));
        }
        return contentTemplates;
    }

    private ContentTemplate getContentTemplate(Optional<com.atlassian.confluence.api.model.content.Space> space, ContentTemplateRef templateRef, Expansion ... expansions) {
        PageTemplate pageTemplate = this.pluginPageTemplateHelper.getPageTemplate(templateRef);
        if (space.isPresent()) {
            pageTemplate = this.resolvePageTemplate(templateRef);
        }
        return ContentTemplateFactory.buildFrom(space, templateRef, pageTemplate, expansions);
    }

    public ContentTemplate getTemplate(ContentTemplateId contentTemplateId, Expansion ... expansions) {
        ContentTemplateRef templateRef;
        this.validator().validateGet(contentTemplateId).throwIfInvalid("Cannot get template");
        if (contentTemplateId instanceof ContentTemplateId.ContentTemplateIdWithUUID) {
            templateRef = this.getContentTemplateRefWithUuid(contentTemplateId);
        } else if (contentTemplateId instanceof ContentTemplateId.ContentTemplateIdWithKeys) {
            templateRef = this.getContentTemplateRefWithKeys(contentTemplateId);
        } else {
            throw new NotImplementedServiceException("contentTemplateId is not supported: " + contentTemplateId);
        }
        ContentBlueprint blueprint = templateRef.getParent();
        PageTemplate pageTemplate = this.resolvePageTemplate(templateRef);
        Optional<com.atlassian.confluence.api.model.content.Space> space = Strings.isNullOrEmpty((String)blueprint.getSpaceKey()) ? Optional.empty() : Optional.of(com.atlassian.confluence.api.model.content.Space.builder().key(blueprint.getSpaceKey()).build());
        return ContentTemplateFactory.buildFrom(space, templateRef, pageTemplate, expansions);
    }

    private PageTemplate resolvePageTemplate(ContentTemplateRef templateRef) {
        ContentBlueprint blueprint = templateRef.getParent();
        PluginTemplateReference pluginTemplateReference = PluginTemplateReference.spaceTemplateReference((ModuleCompleteKey)new ModuleCompleteKey(templateRef.getModuleCompleteKey()), (ModuleCompleteKey)new ModuleCompleteKey(blueprint.getModuleCompleteKey()), Strings.isNullOrEmpty((String)blueprint.getSpaceKey()) ? null : this.spaceManager.getSpace(blueprint.getSpaceKey()));
        return this.pluginPageTemplateHelper.getPageTemplate(pluginTemplateReference);
    }

    public ContentTemplate create(ContentTemplate contentTemplate, Expansion ... expand) {
        PageTemplate firstVersionPageTemplate;
        this.validator().validateCreate(contentTemplate).throwIfInvalid("Cannot create contentTemplate.");
        PageTemplate editingPageTemplate = this.getPageTemplate(contentTemplate.getTemplateId());
        if (editingPageTemplate == null) {
            throw new IllegalStateException("Cannot find the contentTemplateId: " + contentTemplate.getTemplateId());
        }
        editingPageTemplate = this.createFirstVersionForPageTemplate(contentTemplate, editingPageTemplate);
        try {
            firstVersionPageTemplate = (PageTemplate)editingPageTemplate.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new ServiceException("Should not happened error happened. ", (Throwable)ex);
        }
        editingPageTemplate.setName(contentTemplate.getName());
        editingPageTemplate.setDescription(contentTemplate.getDescription());
        this.setTemplateBody(contentTemplate, editingPageTemplate);
        ContentTemplateRef templateRef = contentTemplate.getTemplateId() instanceof ContentTemplateId.ContentTemplateIdWithUUID ? this.getContentTemplateRefWithUuid(contentTemplate.getTemplateId()) : this.getContentTemplateRefWithKeys(contentTemplate.getTemplateId());
        ModuleCompleteKey templateModuleKey = new ModuleCompleteKey(templateRef.getModuleCompleteKey());
        editingPageTemplate.setModuleCompleteKey(templateModuleKey);
        editingPageTemplate.setReferencingModuleCompleteKey(new ModuleCompleteKey(templateRef.getParent().getModuleCompleteKey()));
        this.pageTemplateManager.savePageTemplate(editingPageTemplate, firstVersionPageTemplate);
        this.publishUpdatedEvent(templateModuleKey, contentTemplate.space());
        return ContentTemplateFactory.buildFromNewPageTemplate(contentTemplate.space(), templateRef, editingPageTemplate, expand);
    }

    public ContentTemplate update(ContentTemplate contentTemplate, Expansion ... expansions) {
        throw new NotImplementedServiceException("This is not needed, as editing is handled by ContentTemplateService. Please use ContentTemplateIdWithId to identify template (long) for editing.");
    }

    public ContentTemplateService.TemplateFinder find(Expansion ... expansions) {
        return this.templateFinderFactory.createFinder(this, expansions);
    }

    public ValidatorImpl validator() {
        return new ValidatorImpl();
    }

    public ValidatorImpl validator(ContentTemplateType contentTemplateType) {
        return new ValidatorImpl();
    }

    public void delete(ContentTemplateId contentTemplateId) {
        throw new NotImplementedServiceException("This is not needed, as revoking is handled by ContentTemplateService");
    }

    private void publishUpdatedEvent(ModuleCompleteKey templateModuleKey, Optional<com.atlassian.confluence.api.model.content.Space> space) {
        Space templateSpace = space.map(value -> this.spaceManager.getSpace(value.getKey())).orElse(null);
        String pluginKey = templateModuleKey.getPluginKey();
        String moduleKey = templateModuleKey.getModuleKey();
        BlueprintTemplateUpdateEvent templateUpdatedEvent = new BlueprintTemplateUpdateEvent(this, pluginKey, moduleKey, templateSpace);
        this.eventPublisher.publish((Object)templateUpdatedEvent);
    }

    private void setTemplateBody(ContentTemplate contentTemplate, PageTemplate pageTemplate) {
        ContentBody body;
        if (contentTemplate.getBody().containsKey(ContentRepresentation.STORAGE)) {
            body = (ContentBody)contentTemplate.getBody().get(ContentRepresentation.STORAGE);
        } else {
            Optional<ContentRepresentation> firstConvertiblePresentation = contentTemplate.getBody().keySet().stream().filter(ContentRepresentation::convertsToStorage).findFirst();
            if (!firstConvertiblePresentation.isPresent()) {
                throw new BadRequestException("Cannot find valid content for template from contentTemplate");
            }
            ContentBody bodyToCreate = (ContentBody)contentTemplate.getBody().get(firstConvertiblePresentation.get());
            body = this.contentBodyConversionService.convert(bodyToCreate, ContentRepresentation.STORAGE);
        }
        pageTemplate.setContent(body.getValue());
    }

    private PageTemplate createFirstVersionForPageTemplate(ContentTemplate contentTemplate, PageTemplate editingPageTemplate) {
        PageTemplate firstVersion = editingPageTemplate;
        if (contentTemplate.getSpace().isDefined()) {
            if (this.isEditingTemplateAModifiedGlobalVersion(editingPageTemplate)) {
                firstVersion = new PageTemplate(editingPageTemplate);
            }
            this.spaceManager.getSpace(((com.atlassian.confluence.api.model.content.Space)contentTemplate.getSpace().get()).getKey()).addPageTemplate(firstVersion);
        }
        if (firstVersion.isNew()) {
            this.pageTemplateManager.savePageTemplate(firstVersion, null);
        }
        return firstVersion;
    }

    private boolean isEditingTemplateAModifiedGlobalVersion(PageTemplate originalPageTemplate) {
        return originalPageTemplate.getSpace() == null && !originalPageTemplate.isNew();
    }

    private PageTemplate getPageTemplate(ContentTemplateId contentTemplateId) {
        if (contentTemplateId instanceof ContentTemplateId.ContentTemplateIdWithUUID) {
            return this.pluginPageTemplateHelper.getPageTemplate(this.getContentTemplateRefWithUuid(contentTemplateId));
        }
        if (contentTemplateId instanceof ContentTemplateId.ContentTemplateIdWithKeys) {
            ContentTemplateRef templateRef = this.getContentTemplateRefWithKeys(contentTemplateId);
            return this.resolvePageTemplate(templateRef);
        }
        return null;
    }

    private ContentTemplateRef getContentTemplateRefWithUuid(ContentTemplateId contentTemplateId) {
        ContentTemplateId.ContentTemplateIdWithUUID uuidId = (ContentTemplateId.ContentTemplateIdWithUUID)contentTemplateId;
        ContentTemplateRef templateRef = (ContentTemplateRef)this.contentTemplateRefManager.getById(uuidId.getUuid());
        if (templateRef == null) {
            throw new NotFoundException("No content template ref found with UUID: " + uuidId);
        }
        return templateRef;
    }

    private ContentTemplateRef getContentTemplateRefWithKeys(ContentTemplateId contentTemplateId) {
        ContentTemplateId.ContentTemplateIdWithKeys keysId = (ContentTemplateId.ContentTemplateIdWithKeys)contentTemplateId;
        ModuleCompleteKey moduleCompleteKey = new ModuleCompleteKey(keysId.getModuleCompleteKey());
        ContentTemplateRef clonedTemplateRef = (ContentTemplateRef)this.contentTemplateRefManager.getCloneByModuleCompleteKey(moduleCompleteKey);
        if (clonedTemplateRef == null) {
            throw new NotFoundException("Cannot locate contentTemplateId: " + contentTemplateId);
        }
        ContentBlueprint clonedBlueprint = clonedTemplateRef.getParent();
        ContentBlueprint blueprintOfSpaceOrGlobal = this.contentBlueprintManager.getPluginBackedContentBlueprint(new ModuleCompleteKey(clonedBlueprint.getModuleCompleteKey()), (String)keysId.getSpaceKey().getOrNull());
        if (blueprintOfSpaceOrGlobal.getIndexPageTemplateRef() != null && blueprintOfSpaceOrGlobal.getIndexPageTemplateRef().getModuleCompleteKey().equals(keysId.getModuleCompleteKey())) {
            return blueprintOfSpaceOrGlobal.getIndexPageTemplateRef();
        }
        Optional<ContentTemplateRef> foundRef = blueprintOfSpaceOrGlobal.getContentTemplateRefs().stream().filter(ref -> ref.getModuleCompleteKey().equals(keysId.getModuleCompleteKey())).findFirst();
        if (foundRef.isPresent()) {
            return foundRef.get();
        }
        throw new NotFoundException("Cannot locate contentTemplateId: " + contentTemplateId);
    }

    private Space getSpace(ContentTemplate contentTemplate) {
        Space spaceEntity = null;
        if (contentTemplate.getSpace().isDefined()) {
            spaceEntity = this.spaceManager.getSpace(((com.atlassian.confluence.api.model.content.Space)contentTemplate.getSpace().get()).getKey());
        }
        return spaceEntity;
    }

    private <T extends PluginBackedBlueprint> Collection<T> getDisplayableBlueprints(Collection<T> blueprints, Map<UUID, BlueprintState> blueprintStateMap, boolean isViewingSpaceTemplateAdmin) {
        return Collections2.filter(blueprints, input -> {
            UUID blueprintId = input.getId();
            BlueprintState blueprintState = (BlueprintState)blueprintStateMap.get(blueprintId);
            if (blueprintState == null) {
                return false;
            }
            if (blueprintState.isDisabledInPluginSystem()) {
                return false;
            }
            if (blueprintState.isDisabledByWebInterfaceManager()) {
                return false;
            }
            return !isViewingSpaceTemplateAdmin || !blueprintState.isDisabledGlobally();
        });
    }

    class ValidatorImpl
    implements ContentTemplateService.Validator {
        private ValidatorImpl() {
        }

        public ValidationResult validateDelete(ContentTemplateId contentTemplateId) {
            return SimpleValidationResult.FORBIDDEN;
        }

        public ValidationResult validateCreate(ContentTemplate newContentTemplate) throws ServiceException {
            SimpleValidationResult.Builder simpleValidationResultBuilder = new SimpleValidationResult.Builder();
            if (newContentTemplate == null) {
                return simpleValidationResultBuilder.addMessage((Message)SimpleMessage.withTranslation((String)"Missing contentTemplate")).build();
            }
            if (!newContentTemplate.getTemplateType().equals((Object)ContentTemplateType.BLUEPRINT)) {
                return simpleValidationResultBuilder.addMessage((Message)SimpleMessage.withTranslation((String)("Unsupported ContentTemplateType: " + newContentTemplate.getTemplateType()))).build();
            }
            this.checkAdminPermission(simpleValidationResultBuilder, DefaultBlueprintContentTemplateService.this.getSpace(newContentTemplate));
            String errorMessage = this.validateTemplate(newContentTemplate);
            if (errorMessage != null) {
                return simpleValidationResultBuilder.addMessage((Message)SimpleMessage.withTranslation((String)errorMessage)).build();
            }
            return simpleValidationResultBuilder.build();
        }

        public ValidationResult validateUpdate(ContentTemplate contentTemplate) {
            return SimpleValidationResult.FORBIDDEN;
        }

        public ValidationResult validateGet(Optional<com.atlassian.confluence.api.model.content.Space> space) {
            Space entitySpace = null;
            if (space.isPresent()) {
                entitySpace = DefaultBlueprintContentTemplateService.this.spaceManager.getSpace(space.get().getKey());
            }
            SimpleValidationResult.Builder simpleValidationResultBuilder = new SimpleValidationResult.Builder();
            this.checkViewPermission(simpleValidationResultBuilder, entitySpace);
            return simpleValidationResultBuilder.build();
        }

        public ValidationResult validateGet(ContentTemplateId contentTemplateId) {
            SimpleValidationResult.Builder simpleValidationResultBuilder = new SimpleValidationResult.Builder();
            try {
                PageTemplate pageTemplate = DefaultBlueprintContentTemplateService.this.getPageTemplate(contentTemplateId);
                if (pageTemplate != null) {
                    Space space = pageTemplate.getSpace();
                    Optional<com.atlassian.confluence.api.model.content.Space> templateSpace = Optional.empty();
                    if (space != null) {
                        templateSpace = Optional.of(com.atlassian.confluence.api.model.content.Space.builder().name(space.getName()).build());
                    }
                    return this.validateGet(templateSpace);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            simpleValidationResultBuilder.addMessage((Message)SimpleMessage.withTranslation((String)("Cannot find ContentTemplateId: " + contentTemplateId))).build();
            return simpleValidationResultBuilder.build();
        }

        private void checkAdminPermission(SimpleValidationResult.Builder simpleValidationResultBuilder, Space space) {
            this.checkPermission(simpleValidationResultBuilder, space, Permission.ADMINISTER);
        }

        private void checkViewPermission(SimpleValidationResult.Builder simpleValidationResultBuilder, Space space) {
            this.checkPermission(simpleValidationResultBuilder, space, Permission.VIEW);
        }

        private void checkPermission(SimpleValidationResult.Builder simpleValidationResultBuilder, Space space, Permission permission) {
            Object target = space != null ? space : PermissionManager.TARGET_APPLICATION;
            boolean authorized = DefaultBlueprintContentTemplateService.this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), permission, target);
            simpleValidationResultBuilder.authorized(authorized);
        }

        private String validateTemplate(ContentTemplate contentTemplate) {
            String title = contentTemplate.getName();
            if (!StringUtils.isNotEmpty((CharSequence)title)) {
                return "Template title cannot be null or empty string.";
            }
            if (title.length() > 255) {
                return "Template title must be less than 255 characters.";
            }
            if (!this.isTemplateTitleUnique(contentTemplate)) {
                return "Template title already used, please use a different name.";
            }
            return null;
        }

        private boolean isTemplateTitleUnique(ContentTemplate contentTemplate) {
            PageTemplate otherTemplate;
            long templateId = 0L;
            if (contentTemplate.getTemplateId() instanceof ContentTemplateId.ContentTemplateIdWithId) {
                templateId = ((ContentTemplateId.ContentTemplateIdWithId)contentTemplate.getTemplateId()).getId();
            }
            return (otherTemplate = DefaultBlueprintContentTemplateService.this.pageTemplateManager.getPageTemplate(contentTemplate.getName(), DefaultBlueprintContentTemplateService.this.getSpace(contentTemplate))) == null || this.getContentTemplateType(otherTemplate) != contentTemplate.getTemplateType() || templateId == otherTemplate.getId();
        }

        private ContentTemplateType getContentTemplateType(PageTemplate otherTemplate) {
            if (StringUtils.isNotBlank((CharSequence)otherTemplate.getModuleKey())) {
                return ContentTemplateType.BLUEPRINT;
            }
            return ContentTemplateType.PAGE;
        }
    }
}

