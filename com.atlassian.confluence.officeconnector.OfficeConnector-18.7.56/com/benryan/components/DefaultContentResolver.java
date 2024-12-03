/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.SpaceContentEntityObject
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.Draft
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.benryan.components;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.benryan.components.ContentResolver;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="contentResolver")
public class DefaultContentResolver
implements ContentResolver {
    private final PageManager pageManager;

    @Autowired
    public DefaultContentResolver(@ComponentImport PageManager pageManager) {
        this.pageManager = pageManager;
    }

    @Override
    public ContentEntityObject getContent(String page, String spaceKey, String date, ContentEntityObject context) throws ParseException {
        if (StringUtils.isBlank((CharSequence)page)) {
            return context;
        }
        if (StringUtils.isBlank((CharSequence)spaceKey)) {
            spaceKey = this.getSpaceKey(context);
        }
        if (StringUtils.isBlank((CharSequence)spaceKey)) {
            throw new IllegalArgumentException("No spaceKey parameter was supplied and it could not be deduced from the context parameter.");
        }
        Page content = null;
        if (StringUtils.isNotBlank((CharSequence)date)) {
            DateFormat dateFormat = DateFormat.getDateInstance(3, Locale.US);
            Date parsedDate = dateFormat.parse(date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(parsedDate);
            content = this.pageManager.getBlogPost(spaceKey, page, cal);
        } else {
            content = this.pageManager.getPage(spaceKey, page);
        }
        return content;
    }

    private String getSpaceKey(ContentEntityObject contentObject) {
        if (contentObject == null) {
            return null;
        }
        String spaceKey = null;
        if (contentObject instanceof Comment) {
            contentObject = ((Comment)contentObject).getContainer();
        }
        if (contentObject instanceof SpaceContentEntityObject) {
            spaceKey = ((SpaceContentEntityObject)contentObject).getSpaceKey();
        } else if (contentObject instanceof Draft) {
            spaceKey = ((Draft)contentObject).getDraftSpaceKey();
        }
        return spaceKey;
    }
}

