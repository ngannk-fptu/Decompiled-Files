/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.util.UrlUtil
 *  org.apache.commons.lang3.RandomStringUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.servlet.download.AttachmentUrlParser;
import com.atlassian.renderer.util.UrlUtil;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportPathUtils {
    private static final Logger log = LoggerFactory.getLogger(ExportPathUtils.class);

    static String constructRelativeExportPath(String baseUrl, String imageSource, AttachmentUrlParser attachmentUrlParser) {
        try {
            String pathFromAttachment;
            URL base = new URL(baseUrl);
            String contextPath = base.getPath();
            URL url = new URL(base, imageSource);
            URI uri = url.toURI();
            Object relativePath = uri.normalize().getPath();
            if (StringUtils.contains((CharSequence)relativePath, (CharSequence)"/../")) {
                throw new MalformedURLException("The imageSource provided does not map to a valid url");
            }
            relativePath = ExportPathUtils.stripStaticResourcePrefix((String)relativePath);
            if (StringUtils.isNotEmpty((CharSequence)contextPath) && ((String)relativePath).startsWith(contextPath)) {
                relativePath = ((String)relativePath).substring(contextPath.length());
            }
            if (((String)relativePath).startsWith("/")) {
                relativePath = ((String)relativePath).substring(1);
            }
            if ((pathFromAttachment = ExportPathUtils.getExportPathFromAttachment((String)relativePath, UrlUtil.getQueryParameters((String)imageSource), attachmentUrlParser)) != null) {
                return pathFromAttachment;
            }
            if (ExportPathUtils.hasValidFilename((String)relativePath)) {
                return relativePath;
            }
            relativePath = ((String)relativePath).substring(0, ((String)relativePath).lastIndexOf(47) + 1) + RandomStringUtils.randomNumeric((int)20);
            log.debug("Unsafe chars in filename replacing with: " + (String)relativePath);
            return relativePath;
        }
        catch (MalformedURLException e) {
            log.error("invalid path " + imageSource, (Throwable)e);
            return "";
        }
        catch (URISyntaxException e) {
            log.error("invalid path " + imageSource, (Throwable)e);
            return "";
        }
    }

    public static String stripStaticResourcePrefix(String path) {
        Pattern pattern = Pattern.compile("(.*)/s/.*/_(/.*)");
        Matcher match = pattern.matcher(path);
        if (match.matches()) {
            return match.group(1) + match.group(2);
        }
        return path;
    }

    private static String getExportPathFromAttachment(String relativePath, Map queryParams, AttachmentUrlParser attachmentUrlParser) {
        Attachment attachment;
        if (attachmentUrlParser == null) {
            return null;
        }
        if (relativePath.contains("/attachments") && (attachment = attachmentUrlParser.getAttachment(relativePath, "attachments", queryParams)) != null) {
            return attachment.getExportPath();
        }
        if (relativePath.contains("/thumbnails") && (attachment = attachmentUrlParser.getAttachment(relativePath, "thumbnails", queryParams)) != null) {
            return attachment.getExportPathForThumbnail();
        }
        return null;
    }

    static boolean hasValidFilename(String path) {
        String filename = path.substring(path.lastIndexOf(47) + 1);
        if (filename.lastIndexOf(46) != -1) {
            filename = filename.substring(0, filename.lastIndexOf(46));
        }
        return filename.matches("[a-zA-Z0-9_-]+");
    }

    public static String constructRelativePath(String baseUrl, String resourcePath) {
        String newBaseUrl = baseUrl + (baseUrl.endsWith("/") ? "" : "/");
        try {
            URL base = new URL(newBaseUrl);
            String contextPath = base.getPath();
            URL url = new URL(base, resourcePath);
            Object relativePath = url.getPath();
            if (StringUtils.isNotEmpty((CharSequence)contextPath) && !((String)relativePath).startsWith(contextPath)) {
                if (contextPath.endsWith("/")) {
                    contextPath = contextPath.substring(0, contextPath.length() - 1);
                }
                relativePath = contextPath + (String)relativePath;
            }
            return relativePath;
        }
        catch (MalformedURLException e) {
            log.warn("Invalid path " + resourcePath);
            return resourcePath;
        }
    }
}

