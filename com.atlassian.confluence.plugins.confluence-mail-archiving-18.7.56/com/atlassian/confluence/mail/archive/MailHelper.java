/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.mail.address.ConfluenceMailAddress
 *  com.atlassian.confluence.themes.GlobalHelper
 */
package com.atlassian.confluence.mail.archive;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.mail.address.ConfluenceMailAddress;
import com.atlassian.confluence.mail.archive.Mail;
import com.atlassian.confluence.mail.archive.actions.AbstractMailAction;
import com.atlassian.confluence.themes.GlobalHelper;

public class MailHelper
extends GlobalHelper {
    public MailHelper() {
    }

    public MailHelper(AbstractMailAction action) {
        super((ConfluenceActionSupport)action);
    }

    public Mail getMail() {
        if (this.getAction() instanceof AbstractMailAction) {
            AbstractMailAction abstractMailAction = (AbstractMailAction)this.getAction();
            return abstractMailAction.getMail();
        }
        return null;
    }

    public String getSender(Mail mail) {
        ConfluenceMailAddress address = mail.getFrom();
        return this.getSender(address);
    }

    public String getSender(ConfluenceMailAddress address) {
        if (address == null) {
            return this.getText("anonymous.name");
        }
        return address.getSender();
    }
}

