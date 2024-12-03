/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.CustomContentEntityObject
 *  com.atlassian.confluence.content.render.xhtml.Renderer
 *  com.atlassian.confluence.mail.address.ConfluenceMailAddress
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.rss.AbstractContentEntityRenderSupport
 *  com.atlassian.confluence.rss.RssRenderItem
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.misc.ConcurrentConversionUtil
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.renderer.WikiStyleRenderer
 *  com.atlassian.util.concurrent.Timeout
 *  io.atlassian.util.concurrent.Timeout
 */
package com.atlassian.confluence.mail.archive.content;

import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.mail.address.ConfluenceMailAddress;
import com.atlassian.confluence.mail.archive.Mail;
import com.atlassian.confluence.mail.archive.content.ContentBackedMail;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.rss.AbstractContentEntityRenderSupport;
import com.atlassian.confluence.rss.RssRenderItem;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.misc.ConcurrentConversionUtil;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.util.concurrent.Timeout;
import java.util.Map;

public class MailFeedSupport
extends AbstractContentEntityRenderSupport<CustomContentEntityObject> {
    @ComponentImport
    private SettingsManager importSettingsManager;
    @ComponentImport
    private UserAccessor importUserAccessor;
    @ComponentImport
    private WebResourceManager importWebResourceManager;
    @ComponentImport
    private WikiStyleRenderer importWikiStyleRenderer;
    @ComponentImport
    private Renderer importRenderer;
    @ComponentImport
    private VelocityHelperService velocityHelperService;

    private Mail getMail(RssRenderItem item) {
        return ContentBackedMail.newInstance((CustomContentEntityObject)item.getEntity());
    }

    public String getTitle(RssRenderItem<? extends CustomContentEntityObject> item) {
        Mail mail = this.getMail(item);
        return mail.getSubject() + " " + this.getText("rss.template.from") + ": " + this.getSender(mail);
    }

    public String getRenderedContent(RssRenderItem<? extends CustomContentEntityObject> item, Timeout timeout) {
        return this.renderedContext(item, ConcurrentConversionUtil.toIoTimeout((Timeout)timeout));
    }

    public String renderedContext(RssRenderItem<? extends CustomContentEntityObject> item, io.atlassian.util.concurrent.Timeout timeout) {
        Mail mail = this.getMail(item);
        Map contextMap = this.contextMap(item, timeout);
        contextMap.put("entity", mail.getEntity());
        contextMap.put("mail", mail);
        contextMap.put("content", GeneralUtil.plain2html((String)mail.getMessageBody()));
        return this.velocityHelperService.getRenderedTemplate("templates/mail-archive/rss/mail-rss-content.vm", contextMap);
    }

    public String getSender(Mail mail) {
        ConfluenceMailAddress address = mail.getFrom();
        return this.getSender(address);
    }

    public String getSender(ConfluenceMailAddress address) {
        if (address == null) {
            return this.getText("anonymous.name");
        }
        try {
            return address.getSender();
        }
        catch (Exception e) {
            return this.getText("invalid.email.address");
        }
    }

    public void setVelocityHelperService(VelocityHelperService velocityHelperService) {
        this.velocityHelperService = velocityHelperService;
    }
}

