/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.ui.context.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.context.HierarchicalThemeSource;
import org.springframework.ui.context.ThemeSource;
import org.springframework.ui.context.support.DelegatingThemeSource;
import org.springframework.ui.context.support.ResourceBundleThemeSource;

public abstract class UiApplicationContextUtils {
    public static final String THEME_SOURCE_BEAN_NAME = "themeSource";
    private static final Log logger = LogFactory.getLog(UiApplicationContextUtils.class);

    public static ThemeSource initThemeSource(ApplicationContext context) {
        if (context.containsLocalBean(THEME_SOURCE_BEAN_NAME)) {
            HierarchicalThemeSource hts;
            ThemeSource themeSource = context.getBean(THEME_SOURCE_BEAN_NAME, ThemeSource.class);
            if (context.getParent() instanceof ThemeSource && themeSource instanceof HierarchicalThemeSource && (hts = (HierarchicalThemeSource)themeSource).getParentThemeSource() == null) {
                hts.setParentThemeSource((ThemeSource)((Object)context.getParent()));
            }
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Using ThemeSource [" + themeSource + "]"));
            }
            return themeSource;
        }
        HierarchicalThemeSource themeSource = null;
        if (context.getParent() instanceof ThemeSource) {
            themeSource = new DelegatingThemeSource();
            themeSource.setParentThemeSource((ThemeSource)((Object)context.getParent()));
        } else {
            themeSource = new ResourceBundleThemeSource();
        }
        if (logger.isDebugEnabled()) {
            logger.debug((Object)("Unable to locate ThemeSource with name 'themeSource': using default [" + themeSource + "]"));
        }
        return themeSource;
    }
}

