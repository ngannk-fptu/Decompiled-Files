/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.mail.InboundMailServer
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  javax.mail.internet.MimeMessage
 */
package com.atlassian.confluence.plugins.emailgateway.polling;

import com.atlassian.confluence.mail.InboundMailServer;
import com.atlassian.confluence.plugins.emailgateway.api.InboundMailServerManager;
import com.atlassian.confluence.plugins.emailgateway.polling.AutoResponseMessageFilter;
import com.atlassian.confluence.plugins.emailgateway.polling.DeletedMessageFilter;
import com.atlassian.confluence.plugins.emailgateway.polling.MessageRecipientFilter;
import com.atlassian.confluence.plugins.emailgateway.polling.MimeMessageFilterFactory;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import javax.mail.internet.MimeMessage;

public class DefaultMimeMessageFilterFactory
implements MimeMessageFilterFactory {
    private final InboundMailServerManager inboundMailServerManager;

    public DefaultMimeMessageFilterFactory(InboundMailServerManager inboundMailServerManager) {
        this.inboundMailServerManager = inboundMailServerManager;
    }

    @Override
    public Predicate<MimeMessage> getFilter() {
        InboundMailServer inboundMailServer = (InboundMailServer)this.inboundMailServerManager.getMailServer();
        return Predicates.and((Predicate[])new Predicate[]{new DeletedMessageFilter(), new MessageRecipientFilter(inboundMailServer), new AutoResponseMessageFilter()});
    }
}

