/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb
 *  com.atlassian.confluence.util.breadcrumbs.AbstractSpaceBreadcrumb
 *  com.atlassian.confluence.util.breadcrumbs.Breadcrumb
 *  com.atlassian.confluence.util.breadcrumbs.SimpleBreadcrumb
 */
package com.atlassian.confluence.mail.archive.actions;

import com.atlassian.confluence.mail.archive.Mail;
import com.atlassian.confluence.mail.archive.actions.ViewMailAction;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.breadcrumbs.AbstractBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.AbstractSpaceBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.SimpleBreadcrumb;
import java.util.ArrayList;
import java.util.List;

public class MailActionBreadcrumb
extends AbstractSpaceBreadcrumb {
    private Mail mail;
    private Object action;
    private Breadcrumb mailArchiveParent;

    public MailActionBreadcrumb(Object action, Space space, Mail mail, Breadcrumb mailArchiveParent) {
        super(space);
        this.mail = mail;
        this.action = action;
        this.mailArchiveParent = mailArchiveParent;
    }

    protected List<Breadcrumb> getMyCrumbs() {
        ArrayList<Breadcrumb> crumbs = new ArrayList<Breadcrumb>();
        if (this.mail != null) {
            ViewMailAction viewMailAction;
            if (this.action instanceof ViewMailAction && (viewMailAction = (ViewMailAction)((Object)this.action)).isInThread()) {
                crumbs.add((Breadcrumb)new SimpleBreadcrumb("mail.thread", "/mail/archive/viewthread.action?key=" + this.space.getKey() + "&id=" + this.mail.getEntity().getId()));
            }
            crumbs.add((Breadcrumb)new SimpleBreadcrumb(this.mail.getEntity().getTitle(), this.mail.getEntity().getUrlPath()));
        }
        return crumbs;
    }

    protected Breadcrumb getParent() {
        return new MailArchiveBreadcrumb(this.mailArchiveParent);
    }

    private class MailArchiveBreadcrumb
    extends AbstractBreadcrumb {
        private Breadcrumb parent;

        public MailArchiveBreadcrumb(Breadcrumb parent) {
            super("mail.archive", "/mail/archive/viewmailarchive.action?key=" + MailActionBreadcrumb.this.space.getKey());
            this.parent = parent;
        }

        protected Breadcrumb getParent() {
            return this.parent;
        }
    }
}

