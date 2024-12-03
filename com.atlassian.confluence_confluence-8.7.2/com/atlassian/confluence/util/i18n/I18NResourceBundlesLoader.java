/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util.i18n;

import com.atlassian.confluence.util.i18n.I18NResource;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import org.apache.commons.lang3.StringUtils;

public class I18NResourceBundlesLoader {
    public static final String NOLOCALE = "<NOLOCALE>";

    public static Map<String, ResourceBundle> getResourceBundles(I18NResource i18NResource, Locale locale) {
        ResourceBundle bundle4;
        HashMap<String, ResourceBundle> result = new HashMap<String, ResourceBundle>();
        String variant = locale.getVariant();
        String language = locale.getLanguage();
        String country = locale.getCountry();
        if (StringUtils.isNotEmpty((CharSequence)language)) {
            ResourceBundle bundle3;
            if (StringUtils.isNotEmpty((CharSequence)country)) {
                String locale2;
                ResourceBundle bundle2;
                String locale1;
                ResourceBundle bundle1;
                if (StringUtils.isNotEmpty((CharSequence)variant) && (bundle1 = i18NResource.getBundle(locale1 = language + "_" + country + "_" + variant)) != null) {
                    result.put(locale1, bundle1);
                }
                if ((bundle2 = i18NResource.getBundle(locale2 = language + "_" + country)) != null) {
                    result.put(locale2, bundle2);
                }
            }
            if ((bundle3 = i18NResource.getBundle(language)) != null) {
                result.put(language, bundle3);
            }
        }
        if ((bundle4 = i18NResource.getBundle()) != null) {
            result.put(NOLOCALE, bundle4);
        }
        return result;
    }
}

