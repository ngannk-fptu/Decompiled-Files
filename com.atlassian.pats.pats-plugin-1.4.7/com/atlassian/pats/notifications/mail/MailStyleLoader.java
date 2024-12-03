/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.botocss.Botocss
 *  org.apache.commons.io.IOUtils
 *  org.springframework.core.io.ClassPathResource
 *  org.springframework.core.io.Resource
 */
package com.atlassian.pats.notifications.mail;

import com.atlassian.botocss.Botocss;
import com.atlassian.pats.notifications.mail.MailSendingException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class MailStyleLoader {
    private static final Resource STYLESHEET = new ClassPathResource("/templates/email/token/EmailToken.css");
    private final String css = this.loadCss();

    public String applyStyles(String html) {
        return Botocss.inject((String)html, (String[])new String[]{this.css});
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private String loadCss() {
        try (InputStream stream = STYLESHEET.getInputStream();){
            String string = IOUtils.toString((InputStream)stream, (Charset)StandardCharsets.UTF_8);
            return string;
        }
        catch (IOException e) {
            throw new MailSendingException("Error while loading mail styles");
        }
    }
}

