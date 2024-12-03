/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.mail.archive;

import com.atlassian.confluence.mail.archive.AbstractMailAccount;

public class ImapMailAccount
extends AbstractMailAccount {
    static final long serialVersionUID = 7185061735839874291L;

    public ImapMailAccount() {
    }

    public ImapMailAccount(int id, String name, String description, String hostname, String username, String password, int port, boolean secure, String authentication) {
        super(id, name, description, hostname, username, password, port, secure, authentication);
    }

    @Override
    public String getProtocol() {
        return "imap" + (this.isSecure() ? "s" : "");
    }

    @Override
    public String getFolderName() {
        return "INBOX";
    }
}

