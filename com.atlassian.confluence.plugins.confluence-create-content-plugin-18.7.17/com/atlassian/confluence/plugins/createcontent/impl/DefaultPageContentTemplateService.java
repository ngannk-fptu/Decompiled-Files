/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentBody
 *  com.atlassian.confluence.api.model.content.ContentBody$ContentBodyBuilder
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.content.template.ContentBlueprintInstance
 *  com.atlassian.confluence.api.model.content.template.ContentTemplate
 *  com.atlassian.confluence.api.model.content.template.ContentTemplateId
 *  com.atlassian.confluence.api.model.content.template.ContentTemplateId$ContentTemplateIdWithId
 *  com.atlassian.confluence.api.model.content.template.ContentTemplateType
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.atlassian.confluence.api.model.messages.SimpleMessage
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.content.ContentBodyConversionService
 *  com.atlassian.confluence.api.service.content.template.ContentTemplateService$TemplateFinder
 *  com.atlassian.confluence.api.service.content.template.ContentTemplateService$Validator
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.ConflictException
 *  com.atlassian.confluence.api.service.exceptions.InternalServerException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.core.BodyType
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.ContentPermissionManager
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.core.persistence.confluence.StaleObjectStateException
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.labels.Labelable
 *  com.atlassian.confluence.pages.Draft
 *  com.atlassian.confluence.pages.DraftManager
 *  com.atlassian.confluence.pages.DraftsTransitionHelper
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.pages.actions.PagePermissionsActionHelper
 *  com.atlassian.confluence.pages.templates.ContentBlueprintInstanceFactory
 *  com.atlassian.confluence.pages.templates.ContentTemplateFactory
 *  com.atlassian.confluence.pages.templates.PageTemplate
 *  com.atlassian.confluence.pages.templates.PageTemplateManager
 *  com.atlassian.confluence.pages.templates.variables.StringVariable
 *  com.atlassian.confluence.pages.templates.variables.Variable
 *  com.atlassian.confluence.renderer.PageTemplateContext
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.LabelUtil
 *  com.atlassian.confluence.xhtml.api.EditorFormatService
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent.impl;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentBody;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.content.template.ContentBlueprintInstance;
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
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.content.ContentBodyConversionService;
import com.atlassian.confluence.api.service.content.template.ContentTemplateService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ConflictException;
import com.atlassian.confluence.api.service.exceptions.InternalServerException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.core.persistence.confluence.StaleObjectStateException;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftManager;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.actions.PagePermissionsActionHelper;
import com.atlassian.confluence.pages.templates.ContentBlueprintInstanceFactory;
import com.atlassian.confluence.pages.templates.ContentTemplateFactory;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.pages.templates.variables.StringVariable;
import com.atlassian.confluence.pages.templates.variables.Variable;
import com.atlassian.confluence.plugins.createcontent.api.services.PageContentTemplateService;
import com.atlassian.confluence.plugins.createcontent.factory.FinderFactory;
import com.atlassian.confluence.renderer.PageTemplateContext;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.confluence.xhtml.api.EditorFormatService;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultPageContentTemplateService
implements PageContentTemplateService {
    private static final int MAX_PAGE_LIMIT = 200;
    private final PageTemplateManager pageTemplateManager;
    private final ContentTemplateFactory contentTemplateFactory;
    private final SpaceManager spaceManager;
    private final ContentBodyConversionService contentBodyConversionService;
    private final LabelManager labelManager;
    private final PermissionManager permissionManager;
    private final ContentPermissionManager contentPermissionManager;
    private final FinderFactory templateFinderFactory;
    private final DraftsTransitionHelper draftsTransitionHelper;
    private final DraftManager draftManager;
    private final PageManager pageManager;
    private final UserAccessor userAccessor;
    private final ContentBlueprintInstanceFactory contentBlueprintInstanceFactory;
    private final EditorFormatService editorFormatService;

    @Autowired
    public DefaultPageContentTemplateService(@ComponentImport PageTemplateManager pageTemplateManager, @ComponentImport ContentTemplateFactory contentTemplateFactory, @ComponentImport SpaceManager spaceManager, @ComponentImport ContentBodyConversionService contentBodyConversionService, @ComponentImport LabelManager labelManager, @ComponentImport PermissionManager permissionManager, FinderFactory templateFinderFactory, @ComponentImport DraftsTransitionHelper draftTransactionHelper, @ComponentImport DraftManager draftManager, @ComponentImport PageManager pageManager, @ComponentImport UserAccessor userAccessor, @ComponentImport ContentPermissionManager contentPermissionManager, @ComponentImport ContentBlueprintInstanceFactory contentBlueprintInstanceFactory, @ComponentImport EditorFormatService editorFormatService) {
        this.pageTemplateManager = pageTemplateManager;
        this.contentTemplateFactory = contentTemplateFactory;
        this.spaceManager = spaceManager;
        this.contentBodyConversionService = contentBodyConversionService;
        this.labelManager = labelManager;
        this.permissionManager = permissionManager;
        this.templateFinderFactory = templateFinderFactory;
        this.draftsTransitionHelper = draftTransactionHelper;
        this.draftManager = draftManager;
        this.pageManager = pageManager;
        this.userAccessor = userAccessor;
        this.contentPermissionManager = contentPermissionManager;
        this.contentBlueprintInstanceFactory = contentBlueprintInstanceFactory;
        this.editorFormatService = editorFormatService;
    }

    public PageResponse<ContentTemplate> getTemplates(ContentTemplateType contentTemplateType, Option<com.atlassian.confluence.api.model.content.Space> space, PageRequest pageRequest, Expansion ... expansions) {
        return this.getTemplates(contentTemplateType, Optional.ofNullable((com.atlassian.confluence.api.model.content.Space)space.getOrNull()), pageRequest, expansions);
    }

    public PageResponse<ContentTemplate> getTemplates(ContentTemplateType contentTemplateType, Optional<com.atlassian.confluence.api.model.content.Space> space, PageRequest pageRequest, Expansion ... expansions) {
        this.validator().validateGet(space).throwIfInvalid("Cannot get templates");
        List<PageTemplate> pageTemplates = this.getPageTemplates(space);
        Function<PageTemplate, ContentTemplate> mapPageTemplateToContentTemplate = pageTemplate -> this.contentTemplateFactory.buildFrom(pageTemplate, new Expansions(expansions));
        List contentTemplates = pageTemplates.stream().map(mapPageTemplateToContentTemplate).collect(Collectors.toList());
        if (pageRequest == null) {
            throw new BadRequestException("PageRequest cannot be null");
        }
        LimitedRequest limitedRequest = LimitedRequestImpl.create((PageRequest)pageRequest, (int)200);
        return PageResponseImpl.filteredPageResponse((LimitedRequest)limitedRequest, contentTemplates, null);
    }

    public PageResponse<ContentTemplate> getTemplates(Optional<com.atlassian.confluence.api.model.content.Space> space, PageRequest pageRequest, Expansion ... expansions) {
        return this.getTemplates(ContentTemplateType.PAGE, space, pageRequest, expansions);
    }

    public ContentTemplate getTemplate(ContentTemplateId contentTemplateId, Expansion ... expansions) {
        this.validator().validateGet(contentTemplateId).throwIfInvalid("Cannot get template with id: " + contentTemplateId);
        return this.contentTemplateFactory.buildFrom(this.getPageTemplate(contentTemplateId), new Expansions(expansions));
    }

    private List<PageTemplate> getPageTemplates(Optional<com.atlassian.confluence.api.model.content.Space> space) {
        if (!space.isPresent()) {
            return this.pageTemplateManager.getGlobalPageTemplates();
        }
        Space spaceEntity = this.spaceManager.getSpace(space.get().getKey());
        if (spaceEntity == null) {
            throw new IllegalArgumentException("Cannot find the space. spaceKey = " + space.get().getKey());
        }
        return spaceEntity.getPageTemplates();
    }

    private PageTemplate getPageTemplate(ContentTemplateId contentTemplateId) {
        if (contentTemplateId instanceof ContentTemplateId.ContentTemplateIdWithId) {
            ContentTemplateId.ContentTemplateIdWithId id = (ContentTemplateId.ContentTemplateIdWithId)contentTemplateId;
            return this.pageTemplateManager.getPageTemplate(id.getId());
        }
        throw new BadRequestException("ContentTemplateId is not supported: " + contentTemplateId);
    }

    public ContentTemplate create(ContentTemplate contentTemplate, Expansion ... expansions) {
        this.validator().validateCreate(contentTemplate).throwIfInvalid("Cannot create ContentTemplate");
        PageTemplate pageTemplate = new PageTemplate();
        pageTemplate.setName(contentTemplate.getName());
        pageTemplate.setDescription(contentTemplate.getDescription());
        Space spaceEntity = this.getSpace(contentTemplate);
        if (spaceEntity != null) {
            pageTemplate.setSpace(spaceEntity);
            spaceEntity.addPageTemplate(pageTemplate);
        }
        this.setTemplateBody(contentTemplate, pageTemplate);
        this.pageTemplateManager.savePageTemplate(pageTemplate, null);
        LabelUtil.syncState((List)contentTemplate.getLabels(), (LabelManager)this.labelManager, (User)AuthenticatedUserThreadLocal.get(), (Labelable)pageTemplate);
        PageTemplate createdTemplate = this.pageTemplateManager.getPageTemplate(contentTemplate.getName(), spaceEntity);
        return this.contentTemplateFactory.buildFrom(createdTemplate, new Expansions(expansions));
    }

    public ContentTemplate update(ContentTemplate contentTemplate, Expansion ... expansions) {
        PageTemplate originalTemplate;
        this.validator().validateUpdate(contentTemplate).throwIfInvalid("Cannot update contentTemplate");
        PageTemplate updatingTemplate = this.getPageTemplate(contentTemplate.getTemplateId());
        try {
            originalTemplate = (PageTemplate)updatingTemplate.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new ServiceException("Cannot clone PageTemplate: " + updatingTemplate);
        }
        updatingTemplate.setName(contentTemplate.getName());
        updatingTemplate.setDescription(contentTemplate.getDescription());
        this.setTemplateBody(contentTemplate, updatingTemplate);
        try {
            this.pageTemplateManager.savePageTemplate(updatingTemplate, originalTemplate);
        }
        catch (StaleObjectStateException ex) {
            throw new ConflictException("The template being updated has been modified in the database.", (Throwable)ex);
        }
        LabelUtil.syncState((List)contentTemplate.getLabels(), (LabelManager)this.labelManager, (User)AuthenticatedUserThreadLocal.get(), (Labelable)updatingTemplate);
        PageTemplate updatedTemplate = this.pageTemplateManager.getPageTemplate(contentTemplate.getName(), this.getSpace(contentTemplate));
        return this.contentTemplateFactory.buildFrom(updatedTemplate, new Expansions(expansions));
    }

    public void delete(ContentTemplateId contentTemplateId) {
        this.validator().validateDelete(contentTemplateId).throwIfInvalid("Cannot delete contentTemplateId: " + contentTemplateId);
        this.pageTemplateManager.removePageTemplate(this.getPageTemplate(contentTemplateId));
    }

    public ValidatorImpl validator() {
        return new ValidatorImpl();
    }

    public ValidatorImpl validator(ContentTemplateType contentTemplateType) {
        return new ValidatorImpl();
    }

    public ContentTemplateService.TemplateFinder find(Expansion ... expansions) {
        return this.templateFinderFactory.createFinder(this, expansions);
    }

    public ContentBlueprintInstance createInstance(ContentBlueprintInstance blueprintInstance, Expansion ... expansions) {
        ContentEntityObject createdEntity;
        this.validator().validateCreateInstance(blueprintInstance).throwIfInvalid("Cannot create instance from template");
        ContentTemplateId.ContentTemplateIdWithId contentTemplateId = (ContentTemplateId.ContentTemplateIdWithId)blueprintInstance.getContentBlueprintSpec().getContentTemplateId().get();
        PageTemplate template = this.pageTemplateManager.getPageTemplate(contentTemplateId.getId());
        String contentString = this.generateContentString(blueprintInstance, template);
        List labels = template.getLabels();
        ContentStatus status = blueprintInstance.getContent().getStatus();
        if (status.equals((Object)ContentStatus.DRAFT)) {
            createdEntity = this.createDraft(blueprintInstance);
            createdEntity.setTitle(blueprintInstance.getContent().getTitle());
            createdEntity.setBodyAsString(contentString);
            if (DraftsTransitionHelper.isLegacyDraft((ContentEntityObject)createdEntity)) {
                this.draftManager.saveDraft((Draft)createdEntity);
            } else {
                this.pageManager.saveContentEntity(createdEntity, DefaultSaveContext.DRAFT);
            }
        } else if (status.equals((Object)ContentStatus.CURRENT)) {
            Page page = new Page();
            page.setTitle(blueprintInstance.getContent().getTitle());
            page.setBodyAsString(contentString);
            Space spaceEntity = this.spaceManager.getSpace(blueprintInstance.getContent().getSpace().getKey());
            page.setSpace(spaceEntity);
            if (blueprintInstance.getContent().getOptionalParent().isPresent()) {
                Page parent = this.pageManager.getPage(((Content)blueprintInstance.getContent().getOptionalParent().get()).getId().asLong());
                page.setParentPage(parent);
            }
            this.pageManager.saveContentEntity((ContentEntityObject)page, DefaultSaveContext.DEFAULT);
            createdEntity = page;
        } else {
            throw new BadRequestException("Cannot handle content status: " + blueprintInstance.getContent().getStatus());
        }
        this.addLabels(labels, (Labelable)createdEntity, blueprintInstance.getContentBlueprintSpec().labelsString());
        this.addPermission(blueprintInstance.getContentBlueprintSpec().viewPermissionUsersString(), createdEntity);
        return this.contentBlueprintInstanceFactory.convertToInstance(createdEntity, blueprintInstance, expansions);
    }

    private ContentEntityObject createDraft(ContentBlueprintInstance blueprintInstance) {
        ContentEntityObject contentDraft = blueprintInstance.getContent().getParentId() == ContentId.UNSET ? this.draftsTransitionHelper.createDraft("page", blueprintInstance.getContent().getSpaceRef().exists() ? blueprintInstance.getContent().getSpace().getKey() : null) : this.draftsTransitionHelper.createDraft("page", blueprintInstance.getContent().getSpaceRef().exists() ? blueprintInstance.getContent().getSpace().getKey() : null, blueprintInstance.getContent().getParentId().asLong());
        return contentDraft;
    }

    private String generateContentString(ContentBlueprintInstance blueprintInstance, PageTemplate template) {
        String contentString;
        try {
            List templateVariables = this.pageTemplateManager.getTemplateVariables(template);
            if (templateVariables == null || templateVariables.size() == 0) {
                contentString = template.getContent();
            } else {
                List<Variable> variables = this.convertContextToVariables(blueprintInstance.getContentBlueprintSpec().getContext());
                contentString = this.pageTemplateManager.mergeVariables(template, variables, null);
            }
        }
        catch (XhtmlException ex) {
            throw new InternalServerException((Throwable)ex);
        }
        return contentString;
    }

    private void addLabels(List<Label> label, Labelable labelable, Optional<String> labelsString) {
        label.forEach(l -> this.labelManager.addLabel(labelable, l));
        if (labelsString.isPresent()) {
            List labelsFromString = LabelUtil.split((String)labelsString.get());
            labelsFromString.forEach(l -> LabelUtil.addLabel((String)l, (LabelManager)this.labelManager, (Labelable)labelable));
        }
    }

    private void addPermission(Optional<String> viewPermissionsUsers, ContentEntityObject contentEntityObject) {
        if (viewPermissionsUsers.isPresent()) {
            PagePermissionsActionHelper permissionHelper = new PagePermissionsActionHelper(AuthenticatedUserThreadLocal.get(), this.userAccessor);
            List viewPermissions = permissionHelper.createPermissions("View", null, viewPermissionsUsers.get());
            this.contentPermissionManager.setContentPermissions((Collection)viewPermissions, contentEntityObject, "View");
        }
    }

    private List<Variable> convertContextToVariables(Map<String, Object> context) {
        ArrayList<Variable> variables = new ArrayList<Variable>();
        context.forEach((key, value) -> {
            StringVariable variable = new StringVariable(key, value.toString());
            variables.add((Variable)variable);
        });
        return variables;
    }

    private void setTemplateBody(ContentTemplate contentTemplate, PageTemplate pageTemplate) throws BadRequestException {
        ContentBody body;
        if (contentTemplate.getBody().containsKey(ContentRepresentation.STORAGE)) {
            body = (ContentBody)contentTemplate.getBody().get(ContentRepresentation.STORAGE);
        } else {
            ContentBody bodyToCreate;
            Optional<ContentRepresentation> firstConvertiblePresentation = contentTemplate.getBody().keySet().stream().filter(ContentRepresentation::convertsToStorage).findFirst();
            if (!firstConvertiblePresentation.isPresent()) {
                throw new BadRequestException("Cannot find valid content for template from contentTemplate");
            }
            if (ContentRepresentation.WIKI.equals((Object)firstConvertiblePresentation.get())) {
                try {
                    ContentBody unconvertedBody = (ContentBody)contentTemplate.getBody().get(ContentRepresentation.WIKI);
                    DefaultConversionContext context = new DefaultConversionContext((RenderContext)new PageTemplateContext(pageTemplate));
                    String editConvertedToStorageValue = this.editorFormatService.convertEditToStorage(this.editorFormatService.convertWikiToEdit(unconvertedBody.getValue(), (ConversionContext)context), (ConversionContext)context);
                    bodyToCreate = ((ContentBody.ContentBodyBuilder)((ContentBody.ContentBodyBuilder)ContentBody.contentBodyBuilder().content(unconvertedBody.getContentRef()).representation(ContentRepresentation.STORAGE)).value(editConvertedToStorageValue)).build();
                }
                catch (XhtmlException | XMLStreamException e) {
                    throw new BadRequestException("Exception thrown when converting PageTemplate body", e);
                }
            } else {
                bodyToCreate = (ContentBody)contentTemplate.getBody().get(firstConvertiblePresentation.get());
            }
            body = this.contentBodyConversionService.convert(bodyToCreate, ContentRepresentation.STORAGE);
        }
        pageTemplate.setBodyType(BodyType.XHTML);
        pageTemplate.setContent(body.getValue());
    }

    private Space getSpace(ContentTemplate contentTemplate) {
        Space spaceEntity = null;
        if (contentTemplate.getSpace().isDefined()) {
            spaceEntity = this.spaceManager.getSpace(((com.atlassian.confluence.api.model.content.Space)contentTemplate.getSpace().get()).getKey());
        }
        return spaceEntity;
    }

    class ValidatorImpl
    implements ContentTemplateService.Validator {
        private ValidatorImpl() {
        }

        public ValidationResult validateDelete(ContentTemplateId contentTemplateId) {
            SimpleValidationResult.Builder simpleValidationResultBuilder = new SimpleValidationResult.Builder();
            simpleValidationResultBuilder.authorized(true);
            if (contentTemplateId == null) {
                return simpleValidationResultBuilder.addMessage((Message)SimpleMessage.withTranslation((String)"Missing contentTemplateId")).build();
            }
            PageTemplate pageTemplate = DefaultPageContentTemplateService.this.getPageTemplate(contentTemplateId);
            if (pageTemplate == null) {
                return simpleValidationResultBuilder.addMessage((Message)SimpleMessage.withTranslation((String)"Template doesn't exist")).build();
            }
            this.checkAdminPermission(simpleValidationResultBuilder, pageTemplate.getSpace());
            return simpleValidationResultBuilder.build();
        }

        public ValidationResult validateCreate(ContentTemplate newContentTemplate) throws ServiceException {
            SimpleValidationResult.Builder simpleValidationResultBuilder = new SimpleValidationResult.Builder();
            simpleValidationResultBuilder.authorized(true);
            if (newContentTemplate == null) {
                return simpleValidationResultBuilder.addMessage((Message)SimpleMessage.withTranslation((String)"Missing contentTemplate")).build();
            }
            String errorMessage = this.validateTemplate(newContentTemplate);
            if (errorMessage != null) {
                return simpleValidationResultBuilder.addMessage((Message)SimpleMessage.withTranslation((String)errorMessage)).build();
            }
            this.checkAdminPermission(simpleValidationResultBuilder, DefaultPageContentTemplateService.this.getSpace(newContentTemplate));
            return simpleValidationResultBuilder.build();
        }

        public ValidationResult validateUpdate(ContentTemplate contentTemplate) {
            ValidationResult result = this.validateGet(contentTemplate.getTemplateId());
            if (!result.isValid()) {
                return result;
            }
            return this.validateCreate(contentTemplate);
        }

        public ValidationResult validateGet(Optional<com.atlassian.confluence.api.model.content.Space> space) {
            Space entitySpace = null;
            if (space.isPresent()) {
                entitySpace = DefaultPageContentTemplateService.this.spaceManager.getSpace(space.get().getKey());
            }
            SimpleValidationResult.Builder simpleValidationResultBuilder = new SimpleValidationResult.Builder();
            this.checkViewPermission(simpleValidationResultBuilder, entitySpace);
            return simpleValidationResultBuilder.build();
        }

        public ValidationResult validateGet(ContentTemplateId contentTemplateId) {
            SimpleValidationResult.Builder simpleValidationResultBuilder = new SimpleValidationResult.Builder();
            try {
                PageTemplate pageTemplate = DefaultPageContentTemplateService.this.getPageTemplate(contentTemplateId);
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

        public ValidationResult validateCreateInstance(ContentBlueprintInstance instance) {
            SimpleValidationResult.Builder simpleValidationResultBuilder = new SimpleValidationResult.Builder();
            simpleValidationResultBuilder.authorized(true);
            if (instance == null || instance.getContentBlueprintSpec().getContentTemplateId().isEmpty() || !(instance.getContentBlueprintSpec().getContentTemplateId().get() instanceof ContentTemplateId.ContentTemplateIdWithId)) {
                return simpleValidationResultBuilder.addMessage((Message)SimpleMessage.withTranslation((String)"Instance doesn't have a Valid ContentTemplateId provided")).build();
            }
            ContentStatus status = instance.getContent().getStatus();
            if (!ImmutableList.of((Object)ContentStatus.DRAFT, (Object)ContentStatus.CURRENT).contains((Object)status)) {
                return simpleValidationResultBuilder.addMessage((Message)SimpleMessage.withTranslation((String)("Status of content must be DRAFT or CURRENT, supplied value: " + status))).build();
            }
            if (instance.getContent().getSpace() == null) {
                return simpleValidationResultBuilder.addMessage((Message)SimpleMessage.withTranslation((String)"Content must have a space")).build();
            }
            Space entitySpace = DefaultPageContentTemplateService.this.spaceManager.getSpace(instance.getContent().getSpace().getKey());
            this.checkEditPermission(simpleValidationResultBuilder, entitySpace);
            return simpleValidationResultBuilder.build();
        }

        private void checkAdminPermission(SimpleValidationResult.Builder simpleValidationResultBuilder, Space space) {
            this.checkPermission(simpleValidationResultBuilder, space, Permission.ADMINISTER);
        }

        private void checkViewPermission(SimpleValidationResult.Builder simpleValidationResultBuilder, Space space) {
            this.checkPermission(simpleValidationResultBuilder, space, Permission.VIEW);
        }

        private void checkEditPermission(SimpleValidationResult.Builder simpleValidationResultBuilder, Space space) {
            this.checkPermission(simpleValidationResultBuilder, space, Permission.EDIT);
        }

        private void checkPermission(SimpleValidationResult.Builder simpleValidationResultBuilder, Space space, Permission permission) {
            Object target = space != null ? space : PermissionManager.TARGET_APPLICATION;
            boolean authorized = DefaultPageContentTemplateService.this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), permission, target);
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
            return (otherTemplate = DefaultPageContentTemplateService.this.pageTemplateManager.getPageTemplate(contentTemplate.getName(), DefaultPageContentTemplateService.this.getSpace(contentTemplate))) == null || this.getContentTemplateType(otherTemplate) != contentTemplate.getTemplateType() || templateId == otherTemplate.getId();
        }

        private ContentTemplateType getContentTemplateType(PageTemplate otherTemplate) {
            if (StringUtils.isNotBlank((CharSequence)otherTemplate.getModuleKey())) {
                return ContentTemplateType.BLUEPRINT;
            }
            return ContentTemplateType.PAGE;
        }
    }
}

