/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.util.i18n.DefaultI18NBeanFactory;
import com.atlassian.confluence.util.i18n.I18NBean;

@Deprecated
public class I18NSupport {
    private static I18NBean i18NBean = DefaultI18NBeanFactory.getDefaultI18NBean();

    @Deprecated
    public static String getText(String key) {
        return i18NBean.getText(key);
    }

    @Deprecated
    public static String getText(String key, Object[] args) {
        return i18NBean.getText(key, args);
    }
}

