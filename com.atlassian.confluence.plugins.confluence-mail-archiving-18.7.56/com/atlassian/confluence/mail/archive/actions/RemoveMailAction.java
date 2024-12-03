/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.mail.address.ConfluenceMailAddress
 *  com.atlassian.confluence.search.ConfluenceIndexer
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.mail.archive.actions;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.mail.address.ConfluenceMailAddress;
import com.atlassian.confluence.mail.archive.MailHelper;
import com.atlassian.confluence.mail.archive.actions.AbstractMailAction;
import com.atlassian.confluence.search.ConfluenceIndexer;
import com.atlassian.confluence.security.Permission;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveMailAction
extends AbstractMailAction {
    static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(RemoveMailAction.class);
    private transient ConfluenceIndexer indexer;
    private transient MailHelper mailHelper = new MailHelper();

    public String doRemove() {
        log.debug("starting mail removal");
        this.getMail().getEntity().trash();
        log.debug("trashed mail.  Unindexing mail...");
        this.unIndex((EntityObject)this.getMail().getEntity());
        log.debug("Mail unindexed.");
        return "success";
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    private void unIndex(EntityObject eo) {
        if (this.indexer != null && eo instanceof Searchable) {
            for (Object o : ((Searchable)eo).getSearchableDependants()) {
                this.unIndex((EntityObject)o);
            }
            this.indexer.unIndex((Searchable)eo);
        }
    }

    public ConfluenceIndexer getIndexer() {
        return this.indexer;
    }

    public void setIndexer(ConfluenceIndexer indexer) {
        this.indexer = indexer;
    }

    public String getSender(ConfluenceMailAddress address) {
        return this.mailHelper.getSender(address);
    }

    public boolean isPermitted() {
        return super.isPermitted() && this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.REMOVE, (Object)this.getMail());
    }
}

