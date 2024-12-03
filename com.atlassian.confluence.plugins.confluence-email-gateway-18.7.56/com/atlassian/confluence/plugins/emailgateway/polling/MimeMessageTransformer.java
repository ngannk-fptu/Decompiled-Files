/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mail.server.MailServer
 *  javax.mail.internet.MimeMessage
 */
package com.atlassian.confluence.plugins.emailgateway.polling;

import com.atlassian.confluence.plugins.emailgateway.api.ReceivedEmail;
import com.atlassian.mail.server.MailServer;
import javax.mail.internet.MimeMessage;

interface MimeMessageTransformer {
    public ReceivedEmail transformMimeMessage(MimeMessage var1, MailServer var2) throws Exception;
}

