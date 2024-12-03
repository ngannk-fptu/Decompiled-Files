/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.actions.RssDescriptor
 *  com.atlassian.confluence.event.Evented
 *  com.atlassian.confluence.mail.address.ConfluenceMailAddress
 *  com.atlassian.confluence.pages.ManualTotalPaginationSupport
 *  com.atlassian.confluence.pages.actions.AbstractPaginatedListAction
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.breadcrumbs.Breadcrumb
 *  com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware
 *  com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.mail.archive.actions;

import com.atlassian.confluence.core.actions.RssDescriptor;
import com.atlassian.confluence.event.Evented;
import com.atlassian.confluence.mail.address.ConfluenceMailAddress;
import com.atlassian.confluence.mail.archive.Mail;
import com.atlassian.confluence.mail.archive.MailAccountManager;
import com.atlassian.confluence.mail.archive.MailContentManager;
import com.atlassian.confluence.mail.archive.MailHelper;
import com.atlassian.confluence.mail.archive.MailPollResult;
import com.atlassian.confluence.mail.archive.actions.MailActionBreadcrumb;
import com.atlassian.confluence.mail.archive.events.MailListViewEvent;
import com.atlassian.confluence.pages.ManualTotalPaginationSupport;
import com.atlassian.confluence.pages.actions.AbstractPaginatedListAction;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbAware;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewMailArchiveAction
extends AbstractPaginatedListAction
implements BreadcrumbAware,
Evented<MailListViewEvent> {
    static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(ViewMailArchiveAction.class);
    private static final String PLUGIN_KEY = "space-mails";
    private static final String PAGE_NOT_FOUND = "pagenotfound";
    private transient MailAccountManager mailAccountManager;
    private transient MailContentManager localMailContentManager;
    private boolean polling;
    private transient List<MailPollResult> pollResults;
    private final transient MailHelper mailHelper = new MailHelper();
    private transient BreadcrumbGenerator breadcrumbGenerator;

    public ViewMailArchiveAction() {
        this(ITEMS_PER_PAGE);
    }

    public ViewMailArchiveAction(int itemsPerPage) {
        this.paginationSupport = new ManualTotalPaginationSupport(itemsPerPage);
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        if (this.getSpace() == null) {
            log.info("Cannot view mail archive, cannot resolve space for key '{}'", (Object)this.getSpaceKey());
            return PAGE_NOT_FOUND;
        }
        if (this.getSpace().isPersonal()) {
            log.info("Mail archive not supported for personal spaces e.g. '{}'", (Object)this.getSpaceKey());
            return PAGE_NOT_FOUND;
        }
        GeneralUtil.setCookie((String)"confluence.browse.space.cookie", (String)PLUGIN_KEY);
        this.getPaginationSupport().setTotal(this.localMailContentManager.findMailTotal(this.getSpace()));
        return super.execute();
    }

    public MailListViewEvent getEventToPublish(String result) {
        return new MailListViewEvent((Object)this, this.getSpace());
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String poll() throws Exception {
        this.pollResults = this.mailAccountManager.poll(this.getSpace());
        this.polling = true;
        return this.execute();
    }

    public String getSender(ConfluenceMailAddress address) {
        return this.mailHelper.getSender(address);
    }

    public List<Mail> getItems() {
        ArrayList<Mail> list = new ArrayList<Mail>();
        for (Mail o : this.localMailContentManager.getSpaceMail(this.getSpace(), this.paginationSupport.getStartIndex(), AbstractPaginatedListAction.ITEMS_PER_PAGE)) {
            list.add(o);
        }
        return list;
    }

    public boolean hasMailAccounts() {
        return this.mailAccountManager.getMailAccounts(this.getSpace()).size() > 0;
    }

    public void setMailAccountManager(MailAccountManager mailAccountManager) {
        this.mailAccountManager = mailAccountManager;
    }

    public void setLocalMailContentManager(MailContentManager localMailContentManager) {
        this.localMailContentManager = localMailContentManager;
    }

    public RssDescriptor getRssDescriptor() {
        String title = this.getSpace().getName() + " " + this.getText("recent.mail");
        return new RssDescriptor("/spaces/createrssfeed.action?types=mail&spaces=" + GeneralUtil.urlEncode((String)this.getKey()) + "&sort=modified&title=" + GeneralUtil.urlEncode((String)title) + "&maxResults=15", title, this.getAuthenticatedUser() != null);
    }

    public void setBreadcrumbGenerator(@ComponentImport BreadcrumbGenerator breadcrumbGenerator) {
        this.breadcrumbGenerator = breadcrumbGenerator;
    }

    public boolean isPolling() {
        return this.polling;
    }

    public List<MailPollResult> getPollResults() {
        return this.pollResults;
    }

    public boolean isPollSuccessful() {
        for (MailPollResult pollResult : this.pollResults) {
            if (pollResult.isSuccess()) continue;
            return false;
        }
        return true;
    }

    public Breadcrumb getBreadcrumb() {
        return new MailActionBreadcrumb((Object)this, this.getSpace(), null, this.breadcrumbGenerator.getAdvancedBreadcrumb(this.getSpace()));
    }
}

