/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.mail.archive.actions;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.mail.archive.Mail;
import com.atlassian.confluence.mail.archive.ThreadBuilder;
import com.atlassian.confluence.mail.archive.ThreadNode;
import com.atlassian.confluence.mail.archive.actions.AbstractMailAction;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.ArrayList;
import java.util.List;

public class ViewMailAction
extends AbstractMailAction {
    static final long serialVersionUID = 1L;
    private transient ThreadBuilder threadBuilder;
    private transient ThreadNode containingThread;
    private transient AttachmentManager attachmentManager;
    private transient Mail nextMail;
    private transient Mail previousMail;
    private List highlight = new ArrayList();

    public void validate() {
        if (this.getMail() == null) {
            this.addActionError("error.unable.to.determine.thread", new Object[]{this.id});
        }
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        if (this.getMail() != null) {
            this.addToHistory((ContentEntityObject)this.getMail().getEntity());
        }
        return super.execute();
    }

    public void setThreadBuilder(ThreadBuilder threadBuilder) {
        this.threadBuilder = threadBuilder;
    }

    public Mail getNextMail() {
        if (this.nextMail == null) {
            this.nextMail = this.localMailContentManager.getFirstMailAfter(this.getMail());
        }
        return this.nextMail;
    }

    public Mail getPreviousMail() {
        if (this.previousMail == null) {
            this.previousMail = this.localMailContentManager.getFirstMailBefore(this.getMail());
        }
        return this.previousMail;
    }

    public int getTotalThreadCount() {
        return this.getContainingThread().getDescendentsCount() + 1;
    }

    public int getVisibleThreadCount() {
        return this.getPositionInThread().getVisibleThreadCount();
    }

    public List getParents(int maxParentsToReturn) {
        ArrayList<ThreadNode> parents = new ArrayList<ThreadNode>();
        ThreadNode node = this.getPositionInThread();
        if (node != null) {
            while ((node = node.getParent()) != null) {
                parents.add(0, node);
            }
        }
        if (maxParentsToReturn <= 0 || parents.size() < maxParentsToReturn) {
            return parents;
        }
        return parents.subList(parents.size() - maxParentsToReturn, parents.size());
    }

    public boolean isInThread() {
        return this.getPositionInThread() != null && !this.getPositionInThread().isIsolated();
    }

    public ThreadNode getPositionInThread() {
        return this.getContainingThread().getNodeWithMessageId(this.getMail().getMessageId());
    }

    public ThreadNode getContainingThread() {
        if (this.containingThread == null) {
            this.containingThread = this.threadBuilder.buildThreadAround(this.getMail().getSpaceKey(), this.getMail().getMessageId());
        }
        return this.containingThread;
    }

    public Boolean getAttachmentsShowing() {
        return this.getUserInterfaceState().getAttachmentsShowing();
    }

    public void setShowAttachments(Boolean showAttachments) {
        this.getUserInterfaceState().setAttachmentsShowing(showAttachments);
    }

    public String getExcerpt(long mailId) {
        Mail mailToExcerpt = this.localMailContentManager.getById(mailId);
        if (mailToExcerpt == null) {
            return this.getText("error.mail.not.found");
        }
        return mailToExcerpt.getEntity().getExcerpt();
    }

    public List<Attachment> getLatestVersionsOfAttachments() {
        return this.attachmentManager.getLatestVersionsOfAttachments((ContentEntityObject)this.getMail().getEntity());
    }

    public List getHighlight() {
        return this.highlight;
    }

    public void setHighlight(List names) {
        this.highlight = names;
    }

    public String[] getAttachmentDetails(Attachment attachment) {
        return new String[]{GeneralUtil.escapeXml((String)attachment.getFileName()), String.valueOf(attachment.getVersion())};
    }
}

