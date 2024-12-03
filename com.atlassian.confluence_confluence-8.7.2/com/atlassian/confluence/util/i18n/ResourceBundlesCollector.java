/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.comparators.ReverseComparator
 */
package com.atlassian.confluence.util.i18n;

import com.atlassian.confluence.util.i18n.CombinedResourceBundleFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
import org.apache.commons.collections.comparators.ReverseComparator;

public class ResourceBundlesCollector {
    private TreeMap<String, List<ResourceBundle>> bundleListsByLocale = new TreeMap(new ReverseComparator());

    public void addBundle(ResourceBundle bundle) {
        List<ResourceBundle> bundlesList = this.bundleListsByLocale.get("<NOLOCALE>");
        if (bundlesList == null) {
            bundlesList = new ArrayList<ResourceBundle>();
            this.bundleListsByLocale.put("<NOLOCALE>", bundlesList);
        }
        bundlesList.add(bundle);
    }

    public void addBundles(Map<String, ResourceBundle> bundlesByLocale) {
        bundlesByLocale.forEach((locale, bundle) -> this.bundleListsByLocale.computeIfAbsent((String)locale, x -> new ArrayList()).add(bundle));
    }

    public List<ResourceBundle> getCombinedResourceBundles() {
        ArrayList<ResourceBundle> result = new ArrayList<ResourceBundle>();
        for (List<ResourceBundle> bundlesList : this.bundleListsByLocale.values()) {
            result.add(CombinedResourceBundleFactory.createCombinedResourceBundle(bundlesList));
        }
        return result;
    }
}

