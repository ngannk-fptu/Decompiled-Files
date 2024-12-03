/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 */
package org.springframework.web.util;

import javax.servlet.ServletContext;
import org.springframework.lang.Nullable;
import org.springframework.util.PropertyPlaceholderHelper;

public abstract class ServletContextPropertyUtils {
    private static final PropertyPlaceholderHelper strictHelper = new PropertyPlaceholderHelper("${", "}", ":", false);
    private static final PropertyPlaceholderHelper nonStrictHelper = new PropertyPlaceholderHelper("${", "}", ":", true);

    public static String resolvePlaceholders(String text, ServletContext servletContext) {
        return ServletContextPropertyUtils.resolvePlaceholders(text, servletContext, false);
    }

    public static String resolvePlaceholders(String text, ServletContext servletContext, boolean ignoreUnresolvablePlaceholders) {
        if (text.isEmpty()) {
            return text;
        }
        PropertyPlaceholderHelper helper = ignoreUnresolvablePlaceholders ? nonStrictHelper : strictHelper;
        return helper.replacePlaceholders(text, new ServletContextPlaceholderResolver(text, servletContext));
    }

    private static class ServletContextPlaceholderResolver
    implements PropertyPlaceholderHelper.PlaceholderResolver {
        private final String text;
        private final ServletContext servletContext;

        public ServletContextPlaceholderResolver(String text, ServletContext servletContext) {
            this.text = text;
            this.servletContext = servletContext;
        }

        @Override
        @Nullable
        public String resolvePlaceholder(String placeholderName) {
            try {
                String propVal = this.servletContext.getInitParameter(placeholderName);
                if (propVal == null && (propVal = System.getProperty(placeholderName)) == null) {
                    propVal = System.getenv(placeholderName);
                }
                return propVal;
            }
            catch (Throwable ex) {
                System.err.println("Could not resolve placeholder '" + placeholderName + "' in [" + this.text + "] as ServletContext init-parameter or system property: " + ex);
                return null;
            }
        }
    }
}

