/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.HTMLPairType
 *  com.atlassian.sal.api.UrlMode
 */
package com.atlassian.confluence.mail.archive.actions;

import com.atlassian.confluence.mail.archive.MailAccount;
import com.atlassian.confluence.mail.archive.actions.AbstractMailAccountAction;
import com.atlassian.confluence.util.HTMLPairType;
import com.atlassian.sal.api.UrlMode;
import java.util.ArrayList;
import java.util.List;

public class AddMailAccountAction
extends AbstractMailAccountAction {
    @Override
    public String doDefault() throws Exception {
        this.setProtocol("pop3");
        this.setAuthentication("BasicAuthentication");
        this.setPort(110);
        return super.doDefault();
    }

    @Override
    protected String buildRedirect(String flowId) {
        return String.format("%s/mail/archive/addmailaccount.action?space=%s&key=%s&flowId=%s", this.applicationProperties.getBaseUrl(UrlMode.RELATIVE), this.getSpaceKey(), this.getSpaceKey(), flowId);
    }

    @Override
    public String executeInternal() throws Exception {
        MailAccount mailAccount = this.createMailAccountFromFormData();
        String tokenId = this.getToken();
        if (!"BasicAuthentication".equals(this.getAuthentication()) && tokenId == null) {
            throw new Exception("A valid OAuth token is required.");
        }
        if (!"BasicAuthentication".equals(this.getAuthentication())) {
            mailAccount.setToken(tokenId);
        }
        this.mailAccountManager.updateAccountStatus(mailAccount);
        this.mailAccountManager.addMailAccount(this.getSpace(), mailAccount);
        return "success";
    }

    public List<HTMLPairType> getProtocolList() {
        ArrayList<HTMLPairType> result = new ArrayList<HTMLPairType>();
        result.add(new HTMLPairType("pop3", "POP"));
        result.add(new HTMLPairType("imap", "IMAP"));
        result.add(new HTMLPairType("pop3s", "POPS"));
        result.add(new HTMLPairType("imaps", "IMAPS"));
        return result;
    }

    @Override
    public boolean isEditAction() {
        return false;
    }
}

