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

public class EditMailAccountAction
extends AbstractMailAccountAction {
    private int id;
    private String flowId;
    private String token;

    @Override
    public String doDefault() throws Exception {
        MailAccount mailAccount = this.mailAccountManager.getMailAccount(this.getSpace(), this.getId());
        this.setName(mailAccount.getName());
        this.setDescription(mailAccount.getDescription());
        this.setHostname(mailAccount.getHostname());
        this.setProtocol(mailAccount.getProtocol());
        this.setUsername(mailAccount.getUsername());
        this.setPassword(mailAccount.getPassword());
        this.setPort(mailAccount.getPort());
        this.setSecure(mailAccount.isSecure());
        this.setAuthentication(mailAccount.getAuthentication());
        this.setToken(mailAccount.getToken());
        return super.doDefault();
    }

    @Override
    protected String buildRedirect(String flowId) {
        return String.format("%s/mail/archive/editmailaccount.action?space=%s&key=%s&id=%s&flowId=%s", this.applicationProperties.getBaseUrl(UrlMode.RELATIVE), this.getSpaceKey(), this.getSpaceKey(), this.getId(), flowId);
    }

    @Override
    public String executeInternal() throws Exception {
        MailAccount mailAccount = this.mailAccountManager.getMailAccount(this.getSpace(), this.getId());
        mailAccount.setName(this.getName());
        mailAccount.setDescription(this.getDescription());
        mailAccount.setHostname(this.getHostname());
        mailAccount.setUsername(this.getUsername());
        mailAccount.setPassword(this.getPassword());
        mailAccount.setPort(this.getPort());
        mailAccount.setSecure(this.isSecure());
        if (this.getProtocol().endsWith("s")) {
            mailAccount.setSecure(true);
        }
        mailAccount.setAuthentication(this.getAuthentication());
        if (!"BasicAuthentication".equals(this.getAuthentication()) && this.getToken() == null) {
            throw new Exception("A valid OAuth token is required.");
        }
        if (!"BasicAuthentication".equals(this.getAuthentication())) {
            mailAccount.setToken(this.getToken());
        }
        this.mailAccountManager.updateAccountStatus(mailAccount);
        this.mailAccountManager.updateAccount(this.getSpace(), mailAccount);
        return "success";
    }

    public List<HTMLPairType> getProtocolList() {
        ArrayList<HTMLPairType> result = new ArrayList<HTMLPairType>();
        if (this.getProtocol().contains("pop3")) {
            result.add(new HTMLPairType("pop3", "POP"));
            result.add(new HTMLPairType("pop3s", "POPS"));
        } else if (this.getProtocol().contains("imap")) {
            result.add(new HTMLPairType("imap", "IMAP"));
            result.add(new HTMLPairType("imaps", "IMAPS"));
        } else {
            throw new UnsupportedOperationException(this.getProtocol() + " no supported.");
        }
        return result;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean isEditAction() {
        return true;
    }

    @Override
    public String getFlowId() {
        return this.flowId;
    }

    @Override
    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    @Override
    public String getToken() {
        return this.token;
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }
}

