/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.DraftsTransitionHelper
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.spring.container.ContainerManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.createcontent.actions;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.DraftsTransitionHelper;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugins.createcontent.actions.AbstractCreateBlueprintPageAction;
import com.atlassian.confluence.plugins.createcontent.api.exceptions.BlueprintIllegalArgumentException;
import com.atlassian.confluence.plugins.createcontent.services.RequestResolver;
import com.atlassian.confluence.plugins.createcontent.services.RequestStorage;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintPageEntity;
import com.atlassian.confluence.plugins.createcontent.services.model.CreateBlueprintPageRequest;
import com.atlassian.spring.container.ContainerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CreateAndEditAction
extends AbstractCreateBlueprintPageAction {
    private static final Logger log = LoggerFactory.getLogger(CreateAndEditAction.class);
    private RequestStorage requestStorage;
    private CreateBlueprintPageRequest createBlueprintPageRequest;
    private CreateBlueprintPageEntity blueprintPageEntity;
    private RequestResolver resolver;

    public void setDraftId(long draftId) {
        Page parentPage;
        ContentEntityObject draft;
        super.setDraftId(draftId);
        this.setUseDraft(true);
        if (this.draftsTransitionHelper == null) {
            this.setDraftsTransitionHelper((DraftsTransitionHelper)ContainerManager.getComponent((String)"draftsTransitionHelper"));
        }
        if ((draft = this.getDraftAsCEO()) == null) {
            log.warn("Requested draft with id >{}< was not found", (Object)draftId);
            return;
        }
        this.setSpaceKey(DraftsTransitionHelper.getSpaceKey((ContentEntityObject)draft));
        this.setNewSpaceKey(DraftsTransitionHelper.getSpaceKey((ContentEntityObject)draft));
        this.setLabelsString(draft.getLabels());
        Page page = parentPage = DraftsTransitionHelper.isSharedDraft((ContentEntityObject)draft) && draft != null ? ((Page)draft).getParent() : this.getFromPage();
        if (DraftsTransitionHelper.isSharedDraft((ContentEntityObject)draft) && parentPage == null) {
            this.contentBlueprint = this.getCreateBlueprintPageRequest().getContentBlueprint();
            Page indexPage = this.getOrCreateIndexPage();
            this.assignParentPage(draft, indexPage);
        }
        this.blueprintPageEntity = this.requestStorage.retrieveRequest(draft);
        if (this.blueprintPageEntity == null) {
            return;
        }
        long entityParentPageId = this.blueprintPageEntity.getParentPageId();
        this.setParentPageId(entityParentPageId);
        this.setFromPageId(entityParentPageId);
    }

    public String doDefault() throws Exception {
        if (this.getDraftAsCEO() == null) {
            return "pagenotfound";
        }
        return super.doDefault();
    }

    @Deprecated
    public String doEdit() throws Exception {
        this.populateBlueprintPage();
        return super.doDefault();
    }

    public void validate() {
        if (this.contentBlueprint == null) {
            this.contentBlueprint = this.getCreateBlueprintPageRequest().getContentBlueprint();
        }
        super.validate();
        if (!this.hasActionErrors()) {
            this.validatePageTitleAgainstIndexPageTitle();
        }
    }

    protected String beforeAdd() throws Exception {
        Page indexPage = this.getOrCreateIndexPage();
        if (this.getParentPage() == null && indexPage != null) {
            this.setParentPageId(indexPage.getId());
        }
        return super.beforeAdd();
    }

    protected String afterAdd() {
        String result = super.afterAdd();
        if (!"success".equals(result)) {
            return result;
        }
        Page page = (Page)this.getPage();
        if (this.context == null || this.context.isEmpty()) {
            this.context = this.getCreateBlueprintPageRequest().getContext();
        }
        ContentEntityObject draft = this.getDraftAsCEO();
        this.requestStorage.clear(draft);
        this.sendBlueprintPageCreateEvent(page);
        return "success";
    }

    private CreateBlueprintPageRequest getCreateBlueprintPageRequest() {
        if (this.createBlueprintPageRequest == null) {
            if (this.blueprintPageEntity == null) {
                this.blueprintPageEntity = this.requestStorage.retrieveRequest(this.getDraftAsCEO());
            }
            try {
                this.createBlueprintPageRequest = this.resolver.resolve(this.blueprintPageEntity, this.getAuthenticatedUser());
            }
            catch (BlueprintIllegalArgumentException e) {
                throw new RuntimeException(e);
            }
        }
        return this.createBlueprintPageRequest;
    }

    public void setRequestStorage(RequestStorage requestStorage) {
        this.requestStorage = requestStorage;
    }

    public void setRequestResolver(RequestResolver resolver) {
        this.resolver = resolver;
    }
}

