/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.Labelable
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.pages.actions.CreatePageAction
 *  com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.xhtml.api.EditorFormatService
 *  com.atlassian.event.Event
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.reflect.TypeToken
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.createcontent.actions;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.actions.CreatePageAction;
import com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceManager;
import com.atlassian.confluence.plugins.createcontent.ContentBlueprintManager;
import com.atlassian.confluence.plugins.createcontent.actions.BlueprintContentGenerator;
import com.atlassian.confluence.plugins.createcontent.actions.BlueprintManager;
import com.atlassian.confluence.plugins.createcontent.api.events.BlueprintPageCreateEvent;
import com.atlassian.confluence.plugins.createcontent.impl.ContentBlueprint;
import com.atlassian.confluence.plugins.createcontent.impl.ContentTemplateRef;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.xhtml.api.EditorFormatService;
import com.atlassian.event.Event;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractCreateBlueprintPageAction
extends CreatePageAction {
    @ComponentImport
    private EditorFormatService editorFormatService;
    @ComponentImport
    private ConfluenceWebResourceManager confluenceWebResourceManager;
    @ComponentImport
    private ContextPathHolder contextPathHolder;
    private BlueprintManager blueprintManager;
    private BlueprintContentGenerator contentGenerator;
    protected ContentBlueprint contentBlueprint;
    protected Map<String, Object> context = Maps.newHashMap();
    private boolean goToIndexPage;
    private ContentTemplateRef contentTemplateRef;
    protected ContentBlueprintManager contentBlueprintManager;

    protected void validatePageTitleAgainstIndexPageTitle() {
        String indexPageTitle = this.blueprintManager.getIndexPageTitle(this.contentBlueprint);
        if (indexPageTitle.equalsIgnoreCase(this.getTitle())) {
            this.addActionError("create.content.plugin.index.page.title.clash", new Object[]{this.getTitle()});
        }
    }

    private void initialiseWysiwygContent(String contentBody) throws XhtmlException {
        DefaultConversionContext conversionContext = new DefaultConversionContext((RenderContext)new PageContext(this.getSpace().getKey()));
        try {
            String wysiwygContent = this.editorFormatService.convertStorageToEdit(contentBody, (ConversionContext)conversionContext);
            this.setWysiwygContent(wysiwygContent);
        }
        catch (XMLStreamException e) {
            throw new XhtmlException((Throwable)e);
        }
    }

    protected Page getOrCreateIndexPage() {
        return this.blueprintManager.createAndPinIndexPage(this.contentBlueprint, this.getSpace());
    }

    private Label getBlueprintIndexLabel() {
        return new Label(this.contentBlueprint.getIndexKey());
    }

    protected Page populateBlueprintPage() throws XhtmlException {
        ContentTemplateRef contentTemplateRef = this.getContentTemplateRef();
        Page blueprintPage = this.contentGenerator.generateBlueprintPageObject(contentTemplateRef, this.getSpace(), this.context);
        String blueprintPageDefaultTitle = blueprintPage.getTitle();
        String blueprintPageCustomTitle = this.getTitle();
        if (StringUtils.isNotBlank((CharSequence)blueprintPageCustomTitle)) {
            blueprintPage.setTitle(blueprintPageCustomTitle);
        } else {
            this.setTitle(blueprintPageDefaultTitle);
        }
        Label blueprintIndexLabel = this.getBlueprintIndexLabel();
        this.getLabelManager().addLabel((Labelable)blueprintPage, blueprintIndexLabel);
        this.initialiseWysiwygContent(blueprintPage.getBodyAsString());
        this.setLabelsString(blueprintIndexLabel.getName());
        return blueprintPage;
    }

    public void storeBlueprintKeyInEditorContext() {
        if (this.contentBlueprint != null) {
            UUID aoId = this.contentBlueprint.getId();
            this.confluenceWebResourceManager.putMetadata("content-blueprint-id", String.valueOf(aoId));
        }
        this.confluenceWebResourceManager.requireResourcesForContext("editor-blueprint");
    }

    public void storeBlueprintPageIndicatorInEditorContext() {
        this.confluenceWebResourceManager.putMetadata("is-blueprint-page", "true");
    }

    protected void sendBlueprintPageCreateEvent(Page page) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        this.getEventManager().publishEvent((Event)new BlueprintPageCreateEvent((Object)this, page, this.contentBlueprint, user, this.context));
    }

    public void setContextJson(String contextJson) {
        if (StringUtils.isNotBlank((CharSequence)contextJson)) {
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            Map newContext = (Map)new Gson().fromJson(contextJson, type);
            this.setContext(newContext);
        }
    }

    protected void setContext(Map<String, Object> context) {
        this.context = context;
    }

    public void setContentBlueprintId(String id) {
        this.contentBlueprint = (ContentBlueprint)this.contentBlueprintManager.getById(UUID.fromString(id));
    }

    public void setSpaceKey(String spaceKey) {
        super.setSpaceKey(spaceKey);
        this.setNewSpaceKey(spaceKey);
    }

    private ContentTemplateRef getContentTemplateRef() {
        if (this.contentTemplateRef == null) {
            this.contentTemplateRef = this.contentBlueprint.getFirstContentTemplateRef();
            assert (this.contentTemplateRef != null);
        }
        return this.contentTemplateRef;
    }

    public void setContentTemplateRefId(String contentTemplateRefId) {
        if (contentTemplateRefId != null) {
            this.contentTemplateRef = this.findContentTemplateRefInBlueprint(UUID.fromString(contentTemplateRefId));
        }
    }

    @Deprecated
    public void setContentTemplateKey(String contentTemplateKey) {
        if (StringUtils.isBlank((CharSequence)contentTemplateKey)) {
            return;
        }
        ModuleCompleteKey contentBlueprintKey = new ModuleCompleteKey(this.contentBlueprint.getModuleCompleteKey());
        ModuleCompleteKey contentTemplateModuleKey = new ModuleCompleteKey(contentBlueprintKey.getPluginKey(), contentTemplateKey);
        this.contentTemplateRef = this.findContentTemplateRefInBlueprint(contentTemplateModuleKey.getCompleteKey());
    }

    private ContentTemplateRef findContentTemplateRefInBlueprint(UUID refId) {
        for (ContentTemplateRef ref : this.contentBlueprint.getContentTemplateRefs()) {
            if (!ref.getId().equals(refId)) continue;
            return ref;
        }
        throw new IllegalStateException("Content blueprint has no ContentTemplateRef with id: " + refId);
    }

    private ContentTemplateRef findContentTemplateRefInBlueprint(String moduleCompleteKey) {
        for (ContentTemplateRef ref : this.contentBlueprint.getContentTemplateRefs()) {
            if (!moduleCompleteKey.equals(ref.getModuleCompleteKey())) continue;
            return ref;
        }
        throw new IllegalStateException("Content blueprint has no ContentTemplateRef with moduleCompleteKey: " + moduleCompleteKey);
    }

    public String getFormaction() {
        return this.contextPathHolder.getContextPath() + "/plugins/createcontent/docreatepage.action";
    }

    public void setContextPathHolder(ContextPathHolder contextPathHolder) {
        this.contextPathHolder = contextPathHolder;
    }

    public void setBlueprintManager(BlueprintManager blueprintManager) {
        this.blueprintManager = blueprintManager;
    }

    public void setContentGenerator(BlueprintContentGenerator contentGenerator) {
        this.contentGenerator = contentGenerator;
    }

    public void setEditorFormatService(EditorFormatService editorFormatService) {
        this.editorFormatService = editorFormatService;
    }

    public void setConfluenceWebResourceManager(ConfluenceWebResourceManager confluenceWebResourceManager) {
        this.confluenceWebResourceManager = confluenceWebResourceManager;
    }

    public void setContentBlueprintManager(ContentBlueprintManager contentBlueprintManager) {
        this.contentBlueprintManager = contentBlueprintManager;
    }

    protected PageManager getPageManager() {
        return this.pageManager;
    }

    public boolean getGoToIndexPage() {
        return this.goToIndexPage;
    }

    public void setGoToIndexPage(boolean goToIndexPage) {
        this.goToIndexPage = goToIndexPage;
    }
}

