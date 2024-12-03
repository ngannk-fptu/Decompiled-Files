/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.servlet.download;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.persistence.ContentEntityObjectDao;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.BlogPostReference;
import com.atlassian.confluence.util.GeneralUtil;
import java.text.ParseException;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachmentUrlParser {
    private static final Logger log = LoggerFactory.getLogger(AttachmentUrlParser.class);
    public static final String VERSION_PARAMETER = "version";
    public static final String MODIFICATION_DATE_PARAMETER = "modificationDate";
    private static final String PAGE_EMBEDDED = "embedded-page";
    private static final String BLOGPOST_EMBEDDED = "embedded-blogpost";
    private ContentEntityObjectDao contentEntityObjectDao;
    private AttachmentManager attachmentManager;
    private PageManager pageManager;

    public Attachment getAttachment(String urlPath, String urlPrefix, Map parameters) {
        ContentEntityObject entity = this.getEntity(urlPath, urlPrefix);
        if (entity == null) {
            return null;
        }
        String fileName = this.getAttachmentFileName(urlPath);
        int version = this.getIntParameter(VERSION_PARAMETER, parameters);
        return this.attachmentManager.getAttachment(entity, fileName, version);
    }

    public ContentEntityObject getEntity(String urlPath, String prefix) {
        ContentEntityObject entity = this.tryParseEntityId(urlPath, prefix);
        if (entity == null) {
            return this.tryParseEntityName(urlPath, prefix);
        }
        return entity;
    }

    private ContentEntityObject tryParseEntityName(String urlPath, String prefix) {
        String[] pathSegments = urlPath.split("/");
        for (int i = 0; i < pathSegments.length; ++i) {
            if (!prefix.equals(pathSegments[i])) continue;
            try {
                if (PAGE_EMBEDDED.equals(pathSegments[i + 1]) && pathSegments.length - i + 4 > 0) {
                    String spaceKey = pathSegments[i + 2];
                    String pageTitle = pathSegments[i + 3];
                    return this.makePageContentEntity(spaceKey, pageTitle);
                }
                if (!BLOGPOST_EMBEDDED.equals(pathSegments[i + 1]) || pathSegments.length - i + 7 <= 0) continue;
                String spaceKey = pathSegments[i + 2];
                String blogpostPath = StringUtils.join((Object[])new String[]{pathSegments[i + 3], pathSegments[i + 4], pathSegments[i + 5], pathSegments[i + 6]}, (String)"/");
                return this.makeBlogPostContentEntity(spaceKey, blogpostPath);
            }
            catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("unable to parse urlPath [%s]", urlPath), (Throwable)e);
                }
                return null;
            }
        }
        return null;
    }

    private ContentEntityObject makePageContentEntity(String spaceKey, String pageTitle) {
        return this.pageManager.getPage(spaceKey, pageTitle);
    }

    private ContentEntityObject makeBlogPostContentEntity(String spaceKey, String blogpostPath) {
        try {
            BlogPostReference blogPostReference = new BlogPostReference(blogpostPath, spaceKey, this.pageManager);
            return blogPostReference.getBlogPost();
        }
        catch (ParseException ignored) {
            return null;
        }
    }

    private ContentEntityObject tryParseEntityId(String urlPath, String prefix) {
        long entityId = this.getEntityId(urlPath, prefix);
        if (entityId == -1L) {
            if (log.isDebugEnabled()) {
                log.debug("Cannot parse entity id from the url: " + urlPath + " with prefix: " + prefix);
            }
            return null;
        }
        return this.contentEntityObjectDao.getById(entityId);
    }

    public long getEntityId(String urlPath, String prefix) {
        String[] parts = urlPath.split("/");
        for (int i = 0; i < parts.length - 1; ++i) {
            String part = parts[i];
            if (!part.equals(prefix)) continue;
            try {
                return Long.parseLong(parts[i + 1]);
            }
            catch (NumberFormatException ex) {
                return -1L;
            }
        }
        return -1L;
    }

    public String getAttachmentFileName(String urlPath) {
        return urlPath.substring(urlPath.lastIndexOf(47) + 1);
    }

    public void setContentEntityObjectDao(ContentEntityObjectDao contentEntityObjectDao) {
        this.contentEntityObjectDao = contentEntityObjectDao;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    private int getIntParameter(String parameterName, Map parameters) {
        Object obj = parameters.get(parameterName);
        if (obj != null) {
            Integer integerValue;
            String stringValue = obj.toString();
            if (obj instanceof String[]) {
                String[] values;
                for (String value : values = (String[])obj) {
                    if (!StringUtils.isNotEmpty((CharSequence)value)) continue;
                    stringValue = value;
                    break;
                }
            }
            if ((integerValue = GeneralUtil.convertToInteger(stringValue)) == null) {
                throw new RuntimeException(parameterName + " '" + obj + "' is not a valid number");
            }
            return integerValue;
        }
        return 0;
    }
}

