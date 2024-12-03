/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.HTMLSearchableTextUtil
 *  com.atlassian.confluence.util.i18n.UserI18NBeanFactory
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.mobile.helper;

import com.atlassian.confluence.util.HTMLSearchableTextUtil;
import com.atlassian.confluence.util.i18n.UserI18NBeanFactory;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

@Component
public class TimeHelper {
    public static final String EMPTY_CONTENT = "";
    private static final double WPM = 275.0;
    private final UserI18NBeanFactory i18NBeanFactory;

    @Autowired
    public TimeHelper(UserI18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
    }

    private String getText(String key, Object[] params) {
        return this.i18NBeanFactory.getI18NBean().getText(key, params);
    }

    public String timeToRead(@Nullable String content) {
        if (Objects.isNull(content)) {
            return EMPTY_CONTENT;
        }
        int minutes = (int)Math.ceil((double)content.split(" ").length / 275.0);
        return this.getText("confluence.mobile.time.to.read", new Object[]{minutes});
    }

    public String timeToReadWithMarkupContent(@Nonnull String content, @Nonnull String title) {
        try {
            String noMarkupContent = HTMLSearchableTextUtil.stripTags((String)title, (String)content);
            return this.timeToRead(noMarkupContent);
        }
        catch (SAXException e) {
            return EMPTY_CONTENT;
        }
    }
}

