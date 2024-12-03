/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.mail.ConfluencePopMailServer
 *  com.atlassian.fugue.Option
 *  com.atlassian.mail.server.MailServerManager
 *  com.atlassian.mail.server.PopMailServer
 *  com.atlassian.plugin.notifications.api.medium.Message
 */
package com.atlassian.confluence.plugins.email.medium;

import com.atlassian.confluence.mail.ConfluencePopMailServer;
import com.atlassian.confluence.plugins.email.medium.ReplyToFieldProvider;
import com.atlassian.fugue.Option;
import com.atlassian.mail.server.MailServerManager;
import com.atlassian.mail.server.PopMailServer;
import com.atlassian.plugin.notifications.api.medium.Message;

public class DefaultReplyToFieldProvider
implements ReplyToFieldProvider {
    private MailServerManager mailServerManager;

    public DefaultReplyToFieldProvider(MailServerManager mailServerManager) {
        this.mailServerManager = mailServerManager;
    }

    @Override
    public Option<String> getReplyToField(Message message) {
        String replyToAddress = (String)message.getMetadata().get("replyToAddress");
        if (replyToAddress != null) {
            return Option.some((Object)replyToAddress);
        }
        PopMailServer popMailServer = this.mailServerManager.getDefaultPopMailServer();
        if (popMailServer instanceof ConfluencePopMailServer) {
            return Option.some((Object)((ConfluencePopMailServer)popMailServer).getToAddress());
        }
        return Option.none();
    }
}

