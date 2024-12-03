/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.admin.support;

import com.atlassian.crowd.embedded.admin.util.ConfigurationWithPassword;

public class TroubleshootingCommand
implements ConfigurationWithPassword {
    private static final String TROUBLE_SHOOTING_PASSWORD_KEY = "password";
    private long directoryId = -1L;
    private String edit;
    private String username;
    private String password;

    @Override
    public long getDirectoryId() {
        return this.directoryId;
    }

    public void setDirectoryId(long directoryId) {
        this.directoryId = directoryId;
    }

    public String getEdit() {
        return this.edit;
    }

    public boolean redirectToEdit() {
        return this.edit != null;
    }

    public void setEdit(String edit) {
        this.edit = edit;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getPasswordAttributeKey() {
        return TROUBLE_SHOOTING_PASSWORD_KEY;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }
}

