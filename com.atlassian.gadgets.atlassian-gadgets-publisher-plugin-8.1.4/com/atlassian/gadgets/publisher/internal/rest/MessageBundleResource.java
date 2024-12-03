/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.LocaleUtils
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.text.translate.CharSequenceTranslator
 *  org.apache.commons.lang3.text.translate.NumericEntityEscaper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.gadgets.publisher.internal.rest;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.message.I18nResolver;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.NumericEntityEscaper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/g/messagebundle")
@Produces(value={"application/xml"})
@AnonymousAllowed
public class MessageBundleResource {
    private static final Logger log = LoggerFactory.getLogger(MessageBundleResource.class);
    private static final String UNDETERMINED_LOCALE_KEY = "und";
    private final I18nResolver i18nResolver;

    public MessageBundleResource(@ComponentImport I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    @GET
    @Path(value="{locale}/{i18nPrefixes}")
    public Response getLocale(@PathParam(value="locale") String localeString, @PathParam(value="i18nPrefixes") String i18nPrefixes) {
        Locale locale;
        long start = System.currentTimeMillis();
        Set prefixes = Arrays.stream(StringUtils.split((String)i18nPrefixes, (String)",")).collect(Collectors.toSet());
        Locale locale2 = locale = UNDETERMINED_LOCALE_KEY.equals(localeString) ? new Locale("") : LocaleUtils.toLocale((String)localeString);
        if (prefixes.isEmpty()) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        StringBuilder supportedLocalesString = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        supportedLocalesString.append("<messagebundle>\n");
        for (String prefix : prefixes) {
            Map messages = this.i18nResolver.getAllTranslationsForPrefix(prefix, locale);
            for (Map.Entry message : messages.entrySet()) {
                supportedLocalesString.append("\t<msg name=\"").append(StringEscapeUtils.escapeXml10((String)((String)message.getKey()))).append("\">").append(this.escapeAllUnicode((String)message.getValue())).append("</msg>\n");
            }
        }
        supportedLocalesString.append("</messagebundle>");
        if (log.isDebugEnabled()) {
            long duration = System.currentTimeMillis() - start;
            log.debug(String.format("Produced messagebundle with locale '%s' and prefixes '%s' in %s ms.", locale.toString(), i18nPrefixes, Long.toString(duration)));
        }
        return Response.ok((Object)supportedLocalesString.toString()).build();
    }

    private String escapeAllUnicode(String value) {
        return StringEscapeUtils.ESCAPE_XML.with(new CharSequenceTranslator[]{NumericEntityEscaper.between((int)127, (int)Integer.MAX_VALUE)}).translate((CharSequence)value);
    }
}

