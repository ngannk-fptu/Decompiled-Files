/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.resourceloading;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.resourceloading.DelegatingResourceBundleLocator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

public class AggregateResourceBundleLocator
extends DelegatingResourceBundleLocator {
    private final List<String> bundleNames;
    private final ClassLoader classLoader;

    public AggregateResourceBundleLocator(List<String> bundleNames) {
        this(bundleNames, null);
    }

    public AggregateResourceBundleLocator(List<String> bundleNames, ResourceBundleLocator delegate) {
        this(bundleNames, delegate, null);
    }

    public AggregateResourceBundleLocator(List<String> bundleNames, ResourceBundleLocator delegate, ClassLoader classLoader) {
        super(delegate);
        Contracts.assertValueNotNull(bundleNames, "bundleNames");
        this.bundleNames = CollectionHelper.toImmutableList(bundleNames);
        this.classLoader = classLoader;
    }

    @Override
    public ResourceBundle getResourceBundle(Locale locale) {
        ArrayList<ResourceBundle> sourceBundles = new ArrayList<ResourceBundle>();
        for (String bundleName : this.bundleNames) {
            PlatformResourceBundleLocator resourceBundleLocator = new PlatformResourceBundleLocator(bundleName, this.classLoader);
            ResourceBundle resourceBundle = resourceBundleLocator.getResourceBundle(locale);
            if (resourceBundle == null) continue;
            sourceBundles.add(resourceBundle);
        }
        ResourceBundle bundleFromDelegate = super.getResourceBundle(locale);
        if (bundleFromDelegate != null) {
            sourceBundles.add(bundleFromDelegate);
        }
        return sourceBundles.isEmpty() ? null : new AggregateBundle(sourceBundles);
    }

    private static class IteratorEnumeration<T>
    implements Enumeration<T> {
        private final Iterator<T> source;

        public IteratorEnumeration(Iterator<T> source) {
            if (source == null) {
                throw new IllegalArgumentException("Source must not be null");
            }
            this.source = source;
        }

        @Override
        public boolean hasMoreElements() {
            return this.source.hasNext();
        }

        @Override
        public T nextElement() {
            return this.source.next();
        }
    }

    public static class AggregateBundle
    extends ResourceBundle {
        private final Map<String, Object> contents = new HashMap<String, Object>();

        public AggregateBundle(List<ResourceBundle> bundles) {
            if (bundles != null) {
                for (ResourceBundle bundle2 : bundles) {
                    Enumeration<String> keys = bundle2.getKeys();
                    while (keys.hasMoreElements()) {
                        String oneKey = keys.nextElement();
                        if (this.contents.containsKey(oneKey)) continue;
                        this.contents.put(oneKey, bundle2.getObject(oneKey));
                    }
                }
            }
        }

        @Override
        public Enumeration<String> getKeys() {
            return new IteratorEnumeration<String>(this.contents.keySet().iterator());
        }

        @Override
        protected Object handleGetObject(String key) {
            return this.contents.get(key);
        }
    }
}

