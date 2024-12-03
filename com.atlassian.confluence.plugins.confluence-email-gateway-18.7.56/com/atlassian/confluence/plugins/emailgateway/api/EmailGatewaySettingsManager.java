/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.mail.InboundMailServer
 *  com.atlassian.mail.server.MailServer
 */
package com.atlassian.confluence.plugins.emailgateway.api;

import com.atlassian.confluence.mail.InboundMailServer;
import com.atlassian.mail.server.MailServer;

public interface EmailGatewaySettingsManager {
    public static final String ALLOW_TO_CREATE_PAGE_BY_EMAIL_KEY = "com.atlassian.confluence.plugins.emailgateway.allow.create.page";
    public static final String ALLOW_TO_CREATE_COMMENT_BY_EMAIL_KEY = "com.atlassian.confluence.plugins.emailgateway.allow.create.comment";
    public static final String DEFAULT_MAIL_SERVER_KEY = "com.atlassian.confluence.plugins.emailgateway.default.pop.server";

    public boolean isAllowToCreatePageByEmail();

    public boolean isAllowToCreateCommentByEmail();

    public InboundMailServer getDefaultInboundMailServer();

    public void setAllowToCreatePageByEmail(boolean var1);

    public void setAllowToCreateCommentByEmail(boolean var1);

    public void setDefaultMailServer(MailServer var1);
}

