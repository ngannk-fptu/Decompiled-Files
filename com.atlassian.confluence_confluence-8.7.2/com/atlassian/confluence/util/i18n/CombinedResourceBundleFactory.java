/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Pair
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.i18n;

import com.atlassian.confluence.util.i18n.SimpleMapResourceBundle;
import com.atlassian.fugue.Pair;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CombinedResourceBundleFactory {
    private static final Logger log = LoggerFactory.getLogger(CombinedResourceBundleFactory.class);

    public static ResourceBundle createCombinedResourceBundle(Iterable<? extends ResourceBundle> componentResourceBundles) {
        return new SimpleMapResourceBundle(CombinedResourceBundleFactory.combineBundles(componentResourceBundles));
    }

    static Object[][] extractCombinedResourceBundleContents(Iterable<? extends ResourceBundle> componentResourceBundles) {
        return CombinedResourceBundleFactory.internAndUnpack(CombinedResourceBundleFactory.deduplicateKeys(componentResourceBundles));
    }

    static Map<String, Object> combineBundles(Iterable<? extends ResourceBundle> componentResourceBundles) {
        return CombinedResourceBundleFactory.asMap(CombinedResourceBundleFactory.extractCombinedResourceBundleContents(componentResourceBundles));
    }

    private static Map<String, Object> asMap(Object[][] contents) {
        HashMap discarded = Maps.newHashMap();
        HashMap temp = Maps.newHashMap();
        for (int i = 0; i < contents.length; ++i) {
            String key = (String)contents[i][0];
            Object value = contents[i][1];
            if (key == null || value == null) {
                throw new NullPointerException();
            }
            Object oldValue = temp.put(key, value);
            if (oldValue == null) continue;
            discarded.put(key, oldValue);
        }
        if (!discarded.isEmpty()) {
            log.debug("Discarded {} entries out of {}", (Object)discarded.size(), (Object)contents.length);
        }
        return Collections.unmodifiableMap(temp);
    }

    private static Object[][] internAndUnpack(Collection<Pair<String, String>> bundleItems) {
        int counter = 0;
        Object[][] contents = new Object[bundleItems.size()][2];
        for (Pair<String, String> bundleItem : bundleItems) {
            contents[counter++] = new Object[]{((String)bundleItem.left()).intern(), ((String)bundleItem.right()).intern()};
        }
        return contents;
    }

    private static Set<Pair<String, String>> deduplicateKeys(Iterable<? extends ResourceBundle> componentResourceBundles) {
        LinkedHashSet bundleItems = Sets.newLinkedHashSet();
        for (ResourceBundle resourceBundle : componentResourceBundles) {
            Enumeration<String> keys = resourceBundle.getKeys();
            while (keys.hasMoreElements()) {
                String i18nValue;
                String i18nKey = keys.nextElement();
                Pair bundleItem = Pair.pair((Object)i18nKey, (Object)(i18nValue = resourceBundle.getString(i18nKey)));
                if (bundleItems.contains(bundleItem)) {
                    bundleItems.remove(bundleItem);
                }
                bundleItems.add(bundleItem);
            }
        }
        return bundleItems;
    }
}

