/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.FormatConverter
 *  com.atlassian.confluence.core.AttachmentResource
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.InputStreamAttachmentResource
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.pages.DraftsTransitionHelper
 *  com.atlassian.confluence.pages.FileUploadManager
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.plugins.createcontent.ContentBlueprintManager
 *  com.atlassian.confluence.plugins.createcontent.actions.BlueprintContentGenerator
 *  com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint
 *  com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef
 *  com.atlassian.confluence.plugins.createcontent.rest.BlueprintWebItemService
 *  com.atlassian.confluence.plugins.createcontent.rest.entities.CreateBlueprintPageRestEntity
 *  com.atlassian.confluence.plugins.createcontent.rest.entities.CreateDialogWebItemEntity
 *  com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintPageEntity
 *  com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintPageRequest
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.i18n.DocumentationBean
 *  com.atlassian.confluence.util.i18n.DocumentationBeanFactory
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.soy.renderer.SoyException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.efi.rest;

import com.atlassian.confluence.content.render.xhtml.FormatConverter;
import com.atlassian.confluence.core.AttachmentResource;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.InputStreamAttachmentResource;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.pages.FileUploadManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.createcontent.ContentBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.actions.BlueprintContentGenerator;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.confluence.plugins.createcontent.rest.BlueprintWebItemService;
import com.atlassian.confluence.plugins.createcontent.rest.entities.CreateBlueprintPageRestEntity;
import com.atlassian.confluence.plugins.createcontent.rest.entities.CreateDialogWebItemEntity;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintPageEntity;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintPageRequest;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.i18n.DocumentationBean;
import com.atlassian.confluence.util.i18n.DocumentationBeanFactory;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="draft")
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
public class DraftResource {
    private static final Logger logger = LoggerFactory.getLogger(DraftResource.class);
    private static final Map<String, String> TEMPLATES_SOY_MAP = Collections.unmodifiableMap(new HashMap<String, String>(){
        {
            this.put("meeting-notes-item", "Confluence.Templates.OB.Blueprint.meetingNotes");
            this.put("requirements-item", "Confluence.Templates.OB.Blueprint.requirements");
            this.put("decisions-item", "Confluence.Templates.OB.Blueprint.decisions");
        }
    });
    private static final Map<String, String> TEMPLATES_BLUEPRINTS_KEY_MAP = Collections.unmodifiableMap(new HashMap<String, String>(){
        {
            this.put("com.atlassian.confluence.plugins.confluence-business-blueprints:meeting-notes-blueprint", "meeting-notes-item");
            this.put("com.atlassian.confluence.plugins.confluence-software-blueprints:requirements-blueprint", "requirements-item");
            this.put("com.atlassian.confluence.plugins.confluence-business-blueprints:decisions-blueprint", "decisions-item");
        }
    });
    private static final Map<String, String> MIMETYPE_MAP = Collections.unmodifiableMap(new HashMap<String, String>(){
        {
            this.put(".png", "image/png");
            this.put(".jpg", "image/jpeg");
        }
    });
    private static final String[] IMAGES_OF_REQUIREMENTS = new String[]{"mobile_activity_screen.png", "mobile_login_screen.png"};
    private static final String[] IMAGES_OF_MEETING_NOTES = new String[]{"design_feedback.jpg"};
    private static final String[] IMAGES_OF_DECISIONS = new String[]{"cake.jpg", "pie.png"};
    private static final String REQUIREMENTS_DIR = "requirements/";
    private static final String MEETING_NOTES_DIR = "meeting-notes/";
    private static final String DECISIONS_DIR = "decisions/";
    private static final String BLUEPRINT_TEMPLATES_DIR = "/blueprint-templates/";
    private static final String BLUEPRINT_IMAGES_DIR = "/blueprint-templates/images/";
    private static final String BLUEPRINT_TEMPLATES_MODULE_KEY = "com.atlassian.confluence.plugins.confluence-onboarding:onboarding-blueprint-templates-resources";
    public static final String DARK_FEATURE_NEW_TEMPLATE = "confluence.efi.onboarding.new.templates";
    private final FormatConverter formatConverter;
    private final FileUploadManager fileUploadManager;
    private final SoyTemplateRenderer soyTemplateRenderer;
    private final DarkFeaturesManager darkFeaturesManager;
    private final BlueprintWebItemService blueprintWebItemService;
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;
    private final SpaceManager spaceManager;
    private final PageManager pageManager;
    private final DocumentationBeanFactory documentationBeanFactory;
    private final ContentBlueprintManager contentBlueprintManager;
    private final BlueprintContentGenerator blueprintContentGenerator;
    private final DraftsTransitionHelper draftsTransitionHelper;

    public DraftResource(@ComponentImport FormatConverter formatConverter, @ComponentImport FileUploadManager fileUploadManager, @ComponentImport SoyTemplateRenderer soyTemplateRenderer, @ComponentImport DarkFeaturesManager darkFeaturesManager, @ComponentImport BlueprintWebItemService blueprintWebItemService, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport LocaleManager localeManager, @ComponentImport SpaceManager spaceManager, @ComponentImport PageManager pageManager, @ComponentImport DocumentationBeanFactory documentationBeanFactory, @ComponentImport ContentBlueprintManager contentBlueprintManager, @ComponentImport BlueprintContentGenerator blueprintContentGenerator, @ComponentImport DraftsTransitionHelper draftsTransitionHelper) {
        this.formatConverter = formatConverter;
        this.fileUploadManager = fileUploadManager;
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.darkFeaturesManager = darkFeaturesManager;
        this.blueprintWebItemService = blueprintWebItemService;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
        this.spaceManager = spaceManager;
        this.pageManager = pageManager;
        this.documentationBeanFactory = documentationBeanFactory;
        this.contentBlueprintManager = contentBlueprintManager;
        this.blueprintContentGenerator = blueprintContentGenerator;
        this.draftsTransitionHelper = draftsTransitionHelper;
    }

    @Path(value="generate")
    @GET
    public Response generateDraft(@QueryParam(value="spaceKey") String spaceKey) throws IOException {
        if (StringUtils.isEmpty((CharSequence)spaceKey)) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)ImmutableMap.of((Object)"error", (Object)"spaceKey must be not empty")).build();
        }
        ContentEntityObject draft = this.draftsTransitionHelper.createDraft("page", spaceKey);
        if (this.darkFeaturesManager.getDarkFeatures().isFeatureEnabled(DARK_FEATURE_NEW_TEMPLATE)) {
            this.attachImagesToDraft(draft);
        }
        logger.debug("Pre-generated draft with ID {}", (Object)draft.getId());
        return Response.ok((Object)draft.getId()).build();
    }

    @Path(value="{draftId}/body/templates")
    @GET
    public Response getDraftTemplates(@PathParam(value="draftId") long draftId) throws SoyException {
        if (draftId <= 0L) {
            return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)ImmutableMap.of((Object)"error", (Object)("DraftId '" + String.valueOf(draftId) + "' must be a valid number"))).build();
        }
        ContentEntityObject draft = this.draftsTransitionHelper.getDraft(draftId);
        if (!this.darkFeaturesManager.getDarkFeatures().isFeatureEnabled(DARK_FEATURE_NEW_TEMPLATE)) {
            HashMap maps = Maps.newHashMap();
            String spaceKey = DraftsTransitionHelper.getSpaceKey((ContentEntityObject)draft);
            Space space = this.spaceManager.getSpace(spaceKey);
            ConfluenceUser user = AuthenticatedUserThreadLocal.get();
            List<CreateDialogWebItemEntity> createDialogWebItemEntities = this.getCreateDialogWebItemEntities(space, user);
            for (CreateDialogWebItemEntity createDialogWebItemEntity : createDialogWebItemEntities) {
                String blueprintModuleCompleteKey = createDialogWebItemEntity.getBlueprintModuleCompleteKey();
                if (!TEMPLATES_BLUEPRINTS_KEY_MAP.containsKey(blueprintModuleCompleteKey)) continue;
                CreateBlueprintPageRestEntity createBlueprintPageEntity = new CreateBlueprintPageRestEntity(spaceKey, createDialogWebItemEntity.getContentBlueprintId().toString(), "", "", "", "", space.getHomePage().getId(), blueprintModuleCompleteKey, new HashMap(), space.getId());
                CreateBlueprintPageRequest createBlueprintPageRequest = this.getCreateBlueprintPageRequest(space, user, (CreateBlueprintPageEntity)createBlueprintPageEntity);
                Page page = this.blueprintContentGenerator.generateBlueprintPageObject(createBlueprintPageRequest);
                String editorFormat = this.formatConverter.convertToEditorFormat(page.getBodyAsString(), (RenderContext)draft.toPageContext());
                maps.put(TEMPLATES_BLUEPRINTS_KEY_MAP.get(blueprintModuleCompleteKey), editorFormat);
            }
            return Response.ok((Object)maps).build();
        }
        HashMap data = Maps.newHashMap();
        data.put("userkey", AuthenticatedUserThreadLocal.get().getKey().getStringValue());
        HashMap maps = Maps.newHashMap();
        for (Map.Entry<String, String> entry : TEMPLATES_SOY_MAP.entrySet()) {
            String renderedStorageFormat = this.soyTemplateRenderer.render(BLUEPRINT_TEMPLATES_MODULE_KEY, entry.getValue(), (Map)data);
            String editorFormat = this.formatConverter.convertToEditorFormat(renderedStorageFormat, (RenderContext)draft.toPageContext());
            maps.put(entry.getKey(), editorFormat);
        }
        return Response.ok((Object)maps).build();
    }

    private void attachImagesToDraft(ContentEntityObject draft) throws IOException {
        this.attachImages(draft, REQUIREMENTS_DIR, IMAGES_OF_REQUIREMENTS);
        this.attachImages(draft, MEETING_NOTES_DIR, IMAGES_OF_MEETING_NOTES);
        this.attachImages(draft, DECISIONS_DIR, IMAGES_OF_DECISIONS);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void attachImages(ContentEntityObject draft, String subFolder, String[] imagesOfRequirements) throws IOException {
        for (String filename : imagesOfRequirements) {
            String extension = filename.substring(filename.lastIndexOf("."));
            URL url = this.getClass().getResource(BLUEPRINT_IMAGES_DIR + subFolder + filename);
            InputStream is = null;
            try {
                is = url.openStream();
                this.fileUploadManager.storeResource((AttachmentResource)new InputStreamAttachmentResource(is, filename, MIMETYPE_MAP.get(extension), (long)is.available(), null, false), draft);
            }
            finally {
                IOUtils.closeQuietly((InputStream)is);
            }
        }
    }

    private List<CreateDialogWebItemEntity> getCreateDialogWebItemEntities(Space space, ConfluenceUser user) {
        I18NBean i18NBean = this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)user));
        DocumentationBean documentationBean = this.documentationBeanFactory.getDocumentationBean();
        return this.blueprintWebItemService.getCreateContentWebItems(space, i18NBean, documentationBean, user);
    }

    private CreateBlueprintPageRequest getCreateBlueprintPageRequest(Space space, ConfluenceUser user, CreateBlueprintPageEntity createBlueprintPageEntity) {
        ContentBlueprint globalBlueprint;
        ContentBlueprint blueprint = (ContentBlueprint)this.contentBlueprintManager.getById(UUID.fromString(createBlueprintPageEntity.getContentBlueprintId()));
        ContentTemplateRef templateRef = blueprint.getFirstContentTemplateRef();
        ContentBlueprint parentBlueprint = templateRef.getParent();
        if (StringUtils.isNotBlank((CharSequence)parentBlueprint.getSpaceKey())) {
            ModuleCompleteKey moduleCompleteKey = new ModuleCompleteKey(parentBlueprint.getModuleCompleteKey());
            globalBlueprint = this.contentBlueprintManager.getPluginBackedContentBlueprint(moduleCompleteKey, null);
        } else {
            globalBlueprint = parentBlueprint;
        }
        ArrayList contentTemplateRefs = Lists.newArrayList();
        contentTemplateRefs.addAll(globalBlueprint.getContentTemplateRefs());
        contentTemplateRefs.add(globalBlueprint.getIndexPageTemplateRef());
        templateRef = this.findTemplateWithKey(templateRef.getModuleCompleteKey(), contentTemplateRefs);
        Page parentPage = this.pageManager.getPage(createBlueprintPageEntity.getParentPageId());
        return new CreateBlueprintPageRequest(space, createBlueprintPageEntity.getTitle(), createBlueprintPageEntity.getViewPermissionsUsers(), parentPage, createBlueprintPageEntity.getContext(), templateRef, user, blueprint);
    }

    private ContentTemplateRef findTemplateWithKey(String moduleCompleteKey, List<ContentTemplateRef> contentTemplateRefs) {
        for (ContentTemplateRef contentTemplateRef : contentTemplateRefs) {
            if (!contentTemplateRef.getModuleCompleteKey().equals(moduleCompleteKey)) continue;
            return contentTemplateRef;
        }
        throw new IllegalStateException("No content template ref found with module key: " + moduleCompleteKey);
    }
}

