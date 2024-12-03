/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.mail.impl;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.upm.mail.EmailType;
import com.atlassian.upm.mail.MailRenderer;
import com.atlassian.upm.mail.MailRenderingException;
import com.atlassian.upm.mail.UpmEmail;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailRendererImpl
implements MailRenderer {
    private static final Logger logger = LoggerFactory.getLogger(MailRendererImpl.class);
    private final TemplateRenderer renderer;
    private final I18nResolver i18nResolver;

    public MailRendererImpl(TemplateRenderer renderer, I18nResolver i18nResolver) {
        this.renderer = Objects.requireNonNull(renderer, "renderer");
        this.i18nResolver = Objects.requireNonNull(i18nResolver, "i18nResolver");
    }

    @Override
    public String renderEmailBody(EmailType type, UpmEmail.Format format, Map<String, Object> context) {
        StringWriter tempWriter = new StringWriter();
        try {
            this.renderer.render(type.getBodyTemplate(format), context, (Writer)tempWriter);
            String string = tempWriter.toString();
            return string;
        }
        catch (IOException e) {
            logger.warn(String.format("IOException while trying to render email body (%s) : %s", new Object[]{type, e.getMessage()}));
            throw new MailRenderingException(String.format("Unable to render mail template: %s", new Object[]{type}), e);
        }
        finally {
            IOUtils.closeQuietly((Writer)tempWriter);
        }
    }

    @Override
    public String renderEmailSubject(EmailType type, List<String> args) {
        return this.i18nResolver.getText(type.getI18nSubject(), (Serializable[])args.toArray(new String[0]));
    }
}

