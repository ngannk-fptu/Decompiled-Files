/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.spring.container.ContainerManager
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.themes;

import com.atlassian.confluence.core.CustomPageSettings;
import com.atlassian.confluence.core.CustomPageSettingsManager;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.actions.AbstractPageAction;
import com.atlassian.confluence.pages.actions.TinyUrlAware;
import com.atlassian.confluence.pages.actions.ViewPageAction;
import com.atlassian.confluence.themes.GlobalHelper;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.spring.container.ContainerManager;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class PageHelper
extends GlobalHelper
implements TinyUrlAware {
    private static final int DEFAULT_COMMENT_UPDATE_DISPLAY_THRESHOLD = 600;
    private AttachmentManager attachmentManager;
    private LabelManager labelManager;
    private CustomPageSettingsManager customPageSettingsManager;
    private List displayLabels;
    private Integer numberOfAttachments;

    public PageHelper() {
    }

    public PageHelper(AbstractPageAction action) {
        super(action);
    }

    public boolean isHistoricalVersion() {
        return this.getPage() != null && !this.getPage().isLatestVersion();
    }

    public boolean isChildrenShowing() {
        if (this.getAction() instanceof ViewPageAction) {
            ViewPageAction viewPageAction = (ViewPageAction)this.getAction();
            return viewPageAction.getChildrenShowing();
        }
        return false;
    }

    public int getNumberOfAttachments() {
        if (this.numberOfAttachments != null) {
            return this.numberOfAttachments;
        }
        if (this.attachmentManager == null) {
            this.attachmentManager = (AttachmentManager)ContainerManager.getInstance().getContainerContext().getComponent((Object)"attachmentManager");
        }
        this.numberOfAttachments = this.getPage() == null ? 0 : this.attachmentManager.countLatestVersionsOfAttachments(this.getPage());
        return this.numberOfAttachments;
    }

    @HtmlSafe
    public String getCustomHeader(String key) {
        CustomPageSettingsManager customPageSettingsManager = this.getCustomPageSettingsManager();
        CustomPageSettings settings = customPageSettingsManager.retrieveSettings(key);
        if (StringUtils.isBlank((CharSequence)settings.getHeader())) {
            settings = customPageSettingsManager.retrieveSettings();
        }
        return this.renderConfluenceMacro(settings.getHeader());
    }

    @HtmlSafe
    public String getCustomFooter(String key) {
        CustomPageSettingsManager customPageSettingsManager = this.getCustomPageSettingsManager();
        CustomPageSettings settings = customPageSettingsManager.retrieveSettings(key);
        if (StringUtils.isBlank((CharSequence)settings.getFooter())) {
            settings = customPageSettingsManager.retrieveSettings();
        }
        return this.renderConfluenceMacro(settings.getFooter());
    }

    public String getNumberOfAttachmentsAsString() {
        int numAttachments = this.getNumberOfAttachments();
        String property = numAttachments > 1 ? "editor.attachments.plural" : (numAttachments == 0 ? "editor.attachments.zero" : "editor.attachments.singular");
        return this.getText(property, new Object[]{numAttachments});
    }

    public int getNumberOfLabels() {
        return this.getViewableLabels().size();
    }

    public String getNumberOfLabelsAsString() {
        int numLabels = this.getNumberOfLabels();
        String property = numLabels > 1 ? "editor.labels.plural" : (numLabels == 0 ? "editor.labels.zero" : "editor.labels.singular");
        return this.getText(property, new Object[]{numLabels});
    }

    @Override
    public String getTinyUrl() {
        if (this.getAction() instanceof AbstractPageAction) {
            AbstractPageAction abstractPageAction = (AbstractPageAction)this.getAction();
            return abstractPageAction.getTinyUrl();
        }
        return null;
    }

    public LabelManager getLabelManager() {
        if (this.labelManager != null) {
            return this.labelManager;
        }
        this.labelManager = (LabelManager)ContainerManager.getInstance().getContainerContext().getComponent((Object)"labelManager");
        return this.labelManager;
    }

    public CustomPageSettingsManager getCustomPageSettingsManager() {
        if (this.customPageSettingsManager != null) {
            return this.customPageSettingsManager;
        }
        this.customPageSettingsManager = (CustomPageSettingsManager)ContainerManager.getInstance().getContainerContext().getComponent((Object)"customPageSettingsManager");
        return this.customPageSettingsManager;
    }

    public List getViewableLabels() {
        if (this.getPage() == null) {
            return Collections.EMPTY_LIST;
        }
        if (this.displayLabels != null) {
            return this.displayLabels;
        }
        this.displayLabels = this.getPage().getLabelsForDisplay(this.getAction().getAuthenticatedUser());
        return this.displayLabels;
    }

    public boolean shouldRenderCommentAsUpdated(Comment comment, int thresholdSeconds) {
        if (comment.getLastModifierName() == null) {
            return false;
        }
        return !comment.getLastModifierName().equals(comment.getCreatorName()) || comment.getLastModificationDate().getTime() - comment.getCreationDate().getTime() > (long)(thresholdSeconds * 1000);
    }

    public boolean shouldRenderCommentAsUpdated(Comment comment) {
        return this.shouldRenderCommentAsUpdated(comment, 600);
    }
}

