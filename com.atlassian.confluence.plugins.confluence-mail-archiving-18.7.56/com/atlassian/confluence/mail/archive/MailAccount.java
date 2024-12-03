/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.mail.archive;

public interface MailAccount {
    public static final String POP_PROTOCOL = "pop3";
    public static final String SECURE_POP_PROTOCOL = "pop3s";
    public static final String IMAP_PROTOCOL = "imap";
    public static final String SECURE_IMAP_PROTOCOL = "imaps";
    public static final String DEFAULT_FOLDER = "INBOX";
    public static final String BASIC_AUTH_KEY = "BasicAuthentication";
    public static final String BASIC_AUTH_VALUE_KEY = "basic.authentication";

    public int getId();

    public void setId(int var1);

    public String getProtocol();

    public String getFolderName();

    public String getName();

    public void setName(String var1);

    public String getDescription();

    public void setDescription(String var1);

    public String getHostname();

    public void setHostname(String var1);

    public String getUsername();

    public void setUsername(String var1);

    public String getPassword();

    public void setPassword(String var1);

    public int getPort();

    public void setPort(int var1);

    public boolean getStatus();

    public void setStatus(boolean var1);

    public void enable();

    public void disable();

    public void setEnabled(boolean var1);

    public boolean isEnabled();

    public boolean isDisabled();

    public boolean isSecure();

    public void setSecure(boolean var1);

    public String lockName();

    public String getAuthentication();

    public void setAuthentication(String var1);

    public String getToken();

    public void setToken(String var1);
}

