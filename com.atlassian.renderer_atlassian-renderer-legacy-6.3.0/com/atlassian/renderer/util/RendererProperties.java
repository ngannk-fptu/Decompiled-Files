/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.ClassLoaderUtils
 *  org.apache.commons.beanutils.ConvertUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.renderer.util;

import com.atlassian.plugin.util.ClassLoaderUtils;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;
import org.apache.commons.beanutils.ConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RendererProperties {
    public static final String EMOTICONS_PATH = "icons/emoticons/";
    public static final String ICONS_PATH = "icons/";
    public static final String URL_LINK_TITLE = "renderer.external.link.title";
    public static final String SITE_RELATIVE_LINK_TITLE = "renderer.site.relative.link.title";
    public static final String RELATIVE_LINK_TITLE = "renderer.relative.link.title";
    public static final String SEND_MAIL_TO = "renderer.send.mail.to";
    public static final String TITLE_WITH_ANCHOR = "renderer.title.with.anchor";
    public static final String ATTACHED_TO = "renderer.attached.to";
    public static final String NEWS_ITEMS_FOR = "renderer.news.items.for";
    public static final String NEWS_ITEMS_FOR_SPACEKEY = "renderer.news.items.for.spacekey";
    public static final String CREATE_PAGE = "renderer.create.page";
    public static final String CREATE_PAGE_IN_SPACE = "renderer.create.page.in.space";
    public static final String EXTERNAL_SHORTCUT_LINK = "renderer.external.shortcut.link";
    public static final String VIEW_SPACE = "renderer.view.space";
    public static final String VIEW_PROFILE = "renderer.view.profile";
    private static final String propertiesFileName = "atlassian-renderer.properties";
    private static final Logger log;

    static {
        Field[] fields;
        log = LoggerFactory.getLogger(RendererProperties.class);
        Properties props = new Properties();
        try {
            InputStream propsStream = ClassLoaderUtils.getResourceAsStream((String)propertiesFileName, RendererProperties.class);
            props.load(propsStream);
        }
        catch (Throwable t) {
            log.info("The atlassian-renderer was unable to find the atlassian-renderer.properties on the classpath, using default property values.");
        }
        for (Field field : fields = RendererProperties.class.getFields()) {
            String name = field.getName();
            String value = props.getProperty(name);
            if (value == null) continue;
            value = value.trim();
            Object oVal = ConvertUtils.convert((String)value, field.getType());
            try {
                field.set(null, oVal);
            }
            catch (IllegalAccessException e) {
                log.warn("The properties object of atlassian-renderer was unable to set the field: " + field.getName());
            }
        }
    }
}

