/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.dispatcher;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.DefaultStaticContentLoader;
import org.apache.struts2.dispatcher.HostConfig;

public interface StaticContentLoader {
    public static final String DEFAULT_STATIC_CONTENT_PATH = "/static";

    public boolean canHandle(String var1);

    public void setHostConfig(HostConfig var1);

    public void findStaticResource(String var1, HttpServletRequest var2, HttpServletResponse var3) throws IOException;

    public static class Validator {
        private static final Logger LOG = LogManager.getLogger(DefaultStaticContentLoader.class);

        public static String validateStaticContentPath(String uiStaticContentPath) {
            if (StringUtils.isBlank((CharSequence)uiStaticContentPath)) {
                LOG.warn("\"{}\" has been set to \"{}\", falling back into default value \"{}\"", (Object)"struts.ui.staticContentPath", (Object)uiStaticContentPath, (Object)StaticContentLoader.DEFAULT_STATIC_CONTENT_PATH);
                return StaticContentLoader.DEFAULT_STATIC_CONTENT_PATH;
            }
            if ("/".equals(uiStaticContentPath)) {
                LOG.warn("\"{}\" cannot be set to \"{}\", falling back into default value \"{}\"", (Object)"struts.ui.staticContentPath", (Object)uiStaticContentPath, (Object)StaticContentLoader.DEFAULT_STATIC_CONTENT_PATH);
                return StaticContentLoader.DEFAULT_STATIC_CONTENT_PATH;
            }
            if (!uiStaticContentPath.startsWith("/")) {
                LOG.warn("\"{}\" must start with \"/\", but has been set to \"{}\", prepending the missing \"/\"!", (Object)"struts.ui.staticContentPath", (Object)uiStaticContentPath);
                return "/" + uiStaticContentPath;
            }
            if (uiStaticContentPath.endsWith("/")) {
                LOG.warn("\"{}\" must not end with \"/\", but has been set to \"{}\", removing all trailing \"/\"!", (Object)"struts.ui.staticContentPath", (Object)uiStaticContentPath);
                return StringUtils.stripEnd((String)uiStaticContentPath, (String)"/");
            }
            LOG.debug("\"{}\" has been set to \"{}\"", (Object)"struts.ui.staticContentPath", (Object)uiStaticContentPath);
            return uiStaticContentPath;
        }
    }
}

