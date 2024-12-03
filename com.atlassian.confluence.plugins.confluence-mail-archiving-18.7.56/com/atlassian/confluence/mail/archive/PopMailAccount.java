/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.mail.archive;

import com.atlassian.confluence.mail.archive.AbstractMailAccount;

public class PopMailAccount
extends AbstractMailAccount {
    static final long serialVersionUID = 1393090744918206329L;
    public static final int DEFAULT_POP_PORT = 110;

    public PopMailAccount() {
        this.setPort(110);
    }

    public PopMailAccount(int id, String name, String description, String hostname, String username, String password, int port, boolean secure, String authentication) {
        super(id, name, description, hostname, username, password, port, secure, authentication);
    }

    @Override
    public String getProtocol() {
        return "pop3" + (this.isSecure() ? "s" : "");
    }

    @Override
    public String getFolderName() {
        return "INBOX";
    }
}

