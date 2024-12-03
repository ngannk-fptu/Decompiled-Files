/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.collect.ImmutableList
 *  javax.inject.Inject
 *  javax.inject.Named
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.ws.rs.core.Response$Status
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.Marshaller
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.http.MediaType
 *  org.springframework.util.MimeType
 */
package com.atlassian.plugins.authentication.impl.basicauth.filter;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.impl.basicauth.rest.model.BasicAuthMessageEntity;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;

@Named
public class DisableBasicAuthResponseWriter {
    private static final Logger log = LoggerFactory.getLogger(DisableBasicAuthResponseWriter.class);
    private static final String AUTH_DISABLED_FILTER_MESSAGE = "authentication.basic.auth.disabled.filter.message";
    private static final List<MediaType> ALLOWED_MEDIA_TYPES = ImmutableList.of((Object)MediaType.APPLICATION_JSON, (Object)MediaType.APPLICATION_XML, (Object)MediaType.TEXT_PLAIN);
    private static final MediaType DEFAULT_MEDIA_TYPE = ALLOWED_MEDIA_TYPES.get(0);
    @VisibleForTesting
    static final Comparator<MediaType> MEDIA_TYPE_COMPARATOR = Comparator.comparing(MediaType::getQualityValue).reversed().thenComparing(MimeType::isWildcardType).thenComparing(MimeType::isWildcardSubtype);
    private final I18nResolver i18nResolver;
    private final JAXBContext jaxbContext;
    private final Gson gson;

    @Inject
    public DisableBasicAuthResponseWriter(@ComponentImport I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
        this.gson = new Gson();
        try {
            this.jaxbContext = JAXBContext.newInstance((Class[])new Class[]{BasicAuthMessageEntity.class});
        }
        catch (JAXBException e) {
            throw new RuntimeException("Can't instantiate writer for disabled basic auth", e);
        }
    }

    private String getI18nMessage() {
        return this.i18nResolver.getText(AUTH_DISABLED_FILTER_MESSAGE);
    }

    private BasicAuthMessageEntity getBasicAuthMessageEntity() {
        return new BasicAuthMessageEntity(this.getI18nMessage());
    }

    private String getXmlResponse() {
        try {
            Marshaller marshaller = this.jaxbContext.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", (Object)true);
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal((Object)this.getBasicAuthMessageEntity(), (Writer)stringWriter);
            return stringWriter.toString();
        }
        catch (JAXBException e) {
            throw new RuntimeException("Unable to return response for disabled basic auth in correct media type", e);
        }
    }

    private String getJsonResponse() {
        return this.gson.toJson(this.getBasicAuthMessageEntity());
    }

    public void write(HttpServletRequest request, HttpServletResponse response) throws IOException {
        MediaType responseMediaType = this.getResponseMediaType(request);
        response.setCharacterEncoding(String.valueOf(StandardCharsets.UTF_8));
        response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
        try (PrintWriter writer = response.getWriter();){
            if (responseMediaType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
                response.setContentType("application/json");
                writer.write(this.getJsonResponse());
            } else if (responseMediaType.isCompatibleWith(MediaType.APPLICATION_XML)) {
                response.setContentType("application/xml");
                writer.write(this.getXmlResponse());
            } else {
                response.setContentType("text/plain");
                writer.write(this.getI18nMessage());
            }
        }
    }

    @VisibleForTesting
    public MediaType getResponseMediaType(HttpServletRequest request) {
        MediaType acceptableMediaType = this.getAllowedMediaTypeFromAccepted(request);
        if (acceptableMediaType != null) {
            return acceptableMediaType;
        }
        MediaType requestMediaType = this.getMediaTypeFromContentType(request);
        if (requestMediaType != null) {
            return requestMediaType;
        }
        return DEFAULT_MEDIA_TYPE;
    }

    @Nullable
    private MediaType getMediaTypeFromContentType(HttpServletRequest request) {
        String contentTypeHeader = request.getHeader("Content-Type");
        if (contentTypeHeader != null) {
            try {
                MediaType requestMediaType = MediaType.valueOf((String)contentTypeHeader);
                if (requestMediaType != null && ALLOWED_MEDIA_TYPES.contains(requestMediaType)) {
                    return requestMediaType;
                }
            }
            catch (IllegalArgumentException e) {
                log.info("Unable to parse Media Type: {}", (Object)e.getMessage());
            }
        }
        return null;
    }

    @Nullable
    private MediaType getAllowedMediaTypeFromAccepted(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        List<MediaType> acceptableMediaTypes = DisableBasicAuthResponseWriter.getAcceptableMediaTypes(accept);
        for (MediaType acceptableMediaType : acceptableMediaTypes) {
            for (MediaType allowedMediaType : ALLOWED_MEDIA_TYPES) {
                if (!acceptableMediaType.isCompatibleWith(allowedMediaType)) continue;
                return allowedMediaType;
            }
        }
        return null;
    }

    public static List<MediaType> getAcceptableMediaTypes(String header) {
        List parsedMediaTypes = MediaType.parseMediaTypes((String)header);
        parsedMediaTypes.sort(MEDIA_TYPE_COMPARATOR);
        return new ArrayList<MediaType>(parsedMediaTypes);
    }
}

