/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentSelector
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.service.content.ContentPropertyService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.BooleanUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.service.content.ContentPropertyService;
import com.atlassian.confluence.content.service.page.CreateContextProvider;
import com.atlassian.confluence.content.service.page.PageProvider;
import com.atlassian.confluence.content.service.space.SpaceProvider;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.event.events.analytics.PageCopiedAnalyticsEvent;
import com.atlassian.confluence.impl.content.duplicatetags.DuplicateNestedTagsRemover;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.links.RelatedContentRefactorer;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.actions.CreatePageAction;
import com.atlassian.confluence.plugin.descriptor.web.DefaultWebInterfaceContext;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.io.IOException;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CopyPageAction
extends CreatePageAction {
    private static Logger log = LoggerFactory.getLogger(CopyPageAction.class);
    private long idOfPageToCopyTo;
    private Page pageToCopyTo;
    private long idOfPageToCopy;
    private Page pageToCopy;
    private RelatedContentRefactorer refactorer;
    private SpaceProvider spaceProvider;
    private DefaultWebInterfaceContext webInterfaceContext;
    private Boolean copyAttachments;
    private EventPublisher eventPublisher;
    private ContentPropertyService contentPropertyService;
    private DuplicateNestedTagsRemover duplicateNestedTagsRemover;

    @Override
    protected ServiceCommand createCommand() {
        ConfluenceUser remoteUser = this.getAuthenticatedUser();
        boolean notifySelf = false;
        if (remoteUser != null) {
            notifySelf = this.userAccessor.getConfluenceUserPreferences(remoteUser).isWatchingOwnContent();
        }
        this.pageProvider = new CreatePageAction.SimplePageProvider();
        this.spaceProvider = new SimpleSpaceProvider();
        return this.pageService.newCreatePageCommandFromExisting((PageProvider)this.pageProvider, null, (CreateContextProvider)this.contextProvider, this.getDraftAsCEO(), (User)remoteUser, notifySelf, this.spaceProvider);
    }

    @Override
    @PermittedMethods(value={HttpMethod.POST})
    public String doDefault() throws Exception {
        String result;
        if (!this.getPageToCopy().isPresent()) {
            return "error";
        }
        if (!this.getPageToCopyTo().isPresent()) {
            if (this.getPageToCopy().get().getParent() != null) {
                this.setFromPageId(this.getPageToCopy().get().getParent().getId());
            }
        } else {
            Page destinationPage = this.getPageToCopyTo().get();
            this.setFromPageId(destinationPage.getId());
            this.setNewSpaceKey(destinationPage.getSpaceKey());
        }
        if (!"input".equals(result = super.doDefault())) {
            return result;
        }
        String titleOfCopy = this.getPageToCopy().get().getTitle().startsWith(this.getText("copy.of")) ? this.getPageToCopy().get().getTitle() : this.getText("copy.of") + " " + this.getPageToCopy().get().getTitle();
        this.setTitle(titleOfCopy);
        Page pageToCopy = this.getPageToCopy().get();
        String content = this.refactorer.refactorReferencesToBeRelative(pageToCopy);
        this.setWysiwygContent(this.getEditorFormattedContent(content));
        this.copyToDraft(titleOfCopy, content);
        boolean includeAttachments = BooleanUtils.toBooleanDefaultIfNull((Boolean)this.copyAttachments, (boolean)true);
        if (includeAttachments) {
            this.copyAttachmentsToDraft();
        }
        this.copyLabelsToDraft();
        this.copyJsonContentPropertiesToDraft();
        this.eventPublisher.publish((Object)new PageCopiedAnalyticsEvent(includeAttachments));
        return super.doDefault();
    }

    private void copyToDraft(String titleOfCopy, String content) {
        ContentEntityObject draft = this.getDraftAsCEO();
        if (draft != null) {
            draft.setTitle(titleOfCopy);
            draft.setBodyAsString(content);
        }
    }

    private void copyLabelsToDraft() {
        if (this.getDraftAsCEO() != null && this.getPageToCopy().isPresent()) {
            for (Label label : this.getPageToCopy().get().getVisibleLabels(this.getAuthenticatedUser())) {
                this.getLabelManager().addLabel(this.getDraftAsCEO(), label);
            }
        }
        this.getPageToCopy().ifPresent(page -> this.setLabelsString(page.getVisibleLabels(this.getAuthenticatedUser())));
        this.webInterfaceContext = this.getWebInterfaceContext();
        this.webInterfaceContext.setParameter("numLabelsString", this.getNumberOfLabelsAsString());
        this.webInterfaceContext.setParameter("labels", this.getLabels());
    }

    private void copyJsonContentPropertiesToDraft() {
        ContentEntityObject draftAsCEO = this.getDraftAsCEO();
        if (draftAsCEO != null && this.getPageToCopy().isPresent()) {
            ContentSelector draftSelector = draftAsCEO.getSelector();
            if (draftSelector.getId().asLong() == 0L) {
                draftSelector = ContentSelector.builder().id(ContentId.of((long)draftAsCEO.getId())).status(draftAsCEO.getContentStatusObject()).version(draftAsCEO.getVersion()).build();
            }
            this.contentPropertyService.copyAllJsonContentProperties(this.getPageToCopy().get().getSelector(), draftSelector);
        }
    }

    private void copyAttachmentsToDraft() {
        try {
            this.attachmentManager.copyAttachments(this.getPageToCopy().get(), this.getDraftAsCEO());
        }
        catch (IOException ex) {
            log.warn("Failed to copy the attachments from the source page {} to its copy.", (Object)this.getPageToCopy().get().getTitle());
        }
    }

    @Override
    public boolean isShowDraftMessage() {
        return false;
    }

    @Override
    public boolean isPermitted() {
        this.getCommandActionHelper();
        this.getPageToCopy().ifPresent(page -> this.pageProvider.setPage((Page)page));
        return this.getCommandActionHelper().isAuthorized();
    }

    public Optional<Page> getPageToCopyTo() {
        if (this.pageToCopyTo == null && this.idOfPageToCopyTo > 0L) {
            this.pageToCopyTo = this.pageManager.getPage(this.idOfPageToCopyTo);
        }
        return Optional.ofNullable(this.pageToCopyTo);
    }

    public Optional<Page> getPageToCopy() {
        if (this.pageToCopy == null) {
            this.pageToCopy = this.pageManager.getPage(this.idOfPageToCopy);
        }
        return Optional.ofNullable(this.pageToCopy);
    }

    @Override
    public DefaultWebInterfaceContext getWebInterfaceContext() {
        if (this.webInterfaceContext == null) {
            this.webInterfaceContext = DefaultWebInterfaceContext.copyOf(super.getWebInterfaceContext());
        }
        return this.webInterfaceContext;
    }

    @Override
    public boolean isPageRequired() {
        return false;
    }

    public long getIdOfPageToCopyTo() {
        return this.idOfPageToCopyTo;
    }

    public void setIdOfPageToCopyTo(long idOfPageToCopyTo) {
        this.idOfPageToCopyTo = idOfPageToCopyTo;
    }

    public long getIdOfPageToCopy() {
        return this.idOfPageToCopy;
    }

    public void setIdOfPageToCopy(long idOfPageToCopy) {
        this.idOfPageToCopy = idOfPageToCopy;
    }

    public void setRelatedContentRefactorer(RelatedContentRefactorer refactorer) {
        this.refactorer = refactorer;
    }

    @Override
    public Space getNewSpace() {
        return this.spaceManager.getSpace(this.getNewSpaceKey());
    }

    public Boolean isCopyAttachments() {
        return this.copyAttachments;
    }

    public void setCopyAttachments(Boolean copyAttachments) {
        this.copyAttachments = copyAttachments;
    }

    @Override
    protected String getNumberOfLabelsAsString() {
        if (!this.getPageToCopy().isPresent()) {
            return "editor.labels.zero";
        }
        int numLabels = this.getPageToCopy().get().getVisibleLabels(this.getAuthenticatedUser()).size();
        String key = numLabels > 1 ? "editor.labels.plural" : (numLabels == 0 ? "editor.labels.zero" : "editor.labels.singular");
        return this.getText(key, new Object[]{numLabels});
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void setContentPropertyService(ContentPropertyService contentPropertyService) {
        this.contentPropertyService = contentPropertyService;
    }

    public void setDuplicateNestedTagsRemover(DuplicateNestedTagsRemover duplicateNestedTagsRemover) {
        this.duplicateNestedTagsRemover = duplicateNestedTagsRemover;
    }

    @Override
    public String getEditorFormattedContent(String storageFormat) {
        String editorFormattedContent = super.getEditorFormattedContent(storageFormat);
        return this.removeDuplicateTags(editorFormattedContent);
    }

    private String removeDuplicateTags(String content) {
        return this.duplicateNestedTagsRemover != null ? this.duplicateNestedTagsRemover.cleanQuietly(content) : content;
    }

    public class SimpleSpaceProvider
    implements SpaceProvider {
        @Override
        public Space getSpace() {
            return CopyPageAction.this.getNewSpace();
        }
    }
}

