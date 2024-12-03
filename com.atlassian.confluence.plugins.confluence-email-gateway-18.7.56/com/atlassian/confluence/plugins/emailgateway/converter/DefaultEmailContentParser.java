/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.renderer.RenderContext
 *  javax.mail.internet.InternetAddress
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.emailgateway.converter;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.plugins.emailgateway.api.EmailContentParser;
import com.atlassian.confluence.plugins.emailgateway.api.EmailHtmlToStorageConverter;
import com.atlassian.confluence.plugins.emailgateway.api.ReceivedEmail;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.renderer.RenderContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.internet.InternetAddress;
import org.apache.commons.lang3.StringUtils;

public class DefaultEmailContentParser
implements EmailContentParser {
    private static final Pattern SUBJECT_PREFIX_PATTERN = Pattern.compile("^(fwd?:\\s*|re:\\s*)+", 2);
    private final EmailHtmlToStorageConverter converter;

    public DefaultEmailContentParser(EmailHtmlToStorageConverter converter) {
        this.converter = converter;
    }

    @Override
    public String parseContent(ReceivedEmail email) {
        String emailContent = email.getBodyContentAsString();
        switch (email.getBodyType()) {
            case HTML: {
                return this.convertEmailHtmlToStorageXml(emailContent, email.getContext());
            }
            case TEXT: {
                return this.convertTextToHtml(emailContent);
            }
        }
        return emailContent;
    }

    private String convertEmailHtmlToStorageXml(String emailContent, Map<String, ? extends Serializable> context) {
        PageContext pageContext = new PageContext();
        for (String key : context.keySet()) {
            pageContext.addParam((Object)key, (Object)context.get(key));
        }
        DefaultConversionContext conversionContext = new DefaultConversionContext((RenderContext)pageContext);
        return this.converter.convert(emailContent, (ConversionContext)conversionContext);
    }

    private String convertTextToHtml(String emailContent) {
        String[] lines;
        int level = 0;
        StringBuilder content = new StringBuilder();
        for (String line : lines = emailContent.split("\n")) {
            if (StringUtils.isWhitespace((CharSequence)line)) {
                content.append("<p></p>");
                continue;
            }
            if (line.startsWith(">")) {
                String[] splitString = line.split(" ", 2);
                String quotes = splitString[0];
                if (quotes.length() > level) {
                    do {
                        content.append("<blockquote>");
                    } while (++level != quotes.length());
                } else if (quotes.length() < level) {
                    do {
                        content.append("</blockquote>");
                    } while (--level != quotes.length());
                }
                line = splitString.length == 2 ? splitString[1] : "";
            } else if (level > 0) {
                do {
                    content.append("</blockquote>");
                } while (--level != 0);
            }
            content.append(line + "<br />");
        }
        return content.toString();
    }

    @Override
    public List<InternetAddress> getEmailAddressesFromContent(String content) {
        return new ArrayList<InternetAddress>();
    }

    @Override
    public String parseSubject(String subject) {
        if (StringUtils.isBlank((CharSequence)subject)) {
            return "Untitled Email";
        }
        Matcher subjectPrefixMatcher = SUBJECT_PREFIX_PATTERN.matcher(subject);
        return subjectPrefixMatcher.replaceAll("");
    }
}

