/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.mail.archive;

import com.atlassian.confluence.mail.archive.MailAccount;
import java.io.Serializable;

public abstract class AbstractMailAccount
implements MailAccount,
Serializable {
    protected int id;
    protected String name;
    protected String description;
    protected String hostname;
    protected String username = null;
    protected String password = null;
    protected int port;
    protected boolean secure = false;
    protected boolean status;
    protected boolean enabled = true;
    protected String authentication;
    protected String token;

    protected AbstractMailAccount() {
    }

    protected AbstractMailAccount(int id, String name, String description, String hostname, String username, String password, int port, boolean secure, String authentication) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.port = port;
        this.secure = secure;
        this.authentication = authentication;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getHostname() {
        return this.hostname;
    }

    @Override
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean isSecure() {
        return this.secure;
    }

    @Override
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    @Override
    public boolean getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public void enable() {
        this.setEnabled(true);
    }

    @Override
    public void disable() {
        this.setEnabled(false);
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(boolean b) {
        this.enabled = b;
    }

    @Override
    public boolean isDisabled() {
        return !this.isEnabled();
    }

    public String toString() {
        return this.getProtocol().toUpperCase() + " account [" + this.getUsername() + "@" + this.getHostname() + ":" + this.getPort() + "]";
    }

    @Override
    public String lockName() {
        return this.username + "@" + this.hostname + ":" + this.port;
    }

    @Override
    public String getAuthentication() {
        return this.authentication;
    }

    @Override
    public void setAuthentication(String authentication) {
        this.authentication = authentication;
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

