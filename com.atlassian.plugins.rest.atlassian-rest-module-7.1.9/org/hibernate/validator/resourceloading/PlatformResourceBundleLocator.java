/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.resourceloading;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.hibernate.validator.internal.util.Contracts;
import org.hibernate.validator.internal.util.logging.Messages;
import org.hibernate.validator.internal.util.privilegedactions.GetClassLoader;
import org.hibernate.validator.internal.util.privilegedactions.GetMethod;
import org.hibernate.validator.internal.util.privilegedactions.GetResources;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;
import org.jboss.logging.Logger;

public class PlatformResourceBundleLocator
implements ResourceBundleLocator {
    private static final Logger log = Logger.getLogger(PlatformResourceBundleLocator.class.getName());
    private static final boolean RESOURCE_BUNDLE_CONTROL_INSTANTIABLE = PlatformResourceBundleLocator.determineAvailabilityOfResourceBundleControl();
    private final String bundleName;
    private final ClassLoader classLoader;
    private final boolean aggregate;

    public PlatformResourceBundleLocator(String bundleName) {
        this(bundleName, null);
    }

    public PlatformResourceBundleLocator(String bundleName, ClassLoader classLoader) {
        this(bundleName, classLoader, false);
    }

    public PlatformResourceBundleLocator(String bundleName, ClassLoader classLoader, boolean aggregate) {
        Contracts.assertNotNull(bundleName, "bundleName");
        this.bundleName = bundleName;
        this.classLoader = classLoader;
        this.aggregate = aggregate && RESOURCE_BUNDLE_CONTROL_INSTANTIABLE;
    }

    @Override
    public ResourceBundle getResourceBundle(Locale locale) {
        ClassLoader classLoader;
        ResourceBundle rb = null;
        if (this.classLoader != null) {
            rb = this.loadBundle(this.classLoader, locale, this.bundleName + " not found by user-provided classloader");
        }
        if (rb == null && (classLoader = PlatformResourceBundleLocator.run(GetClassLoader.fromContext())) != null) {
            rb = this.loadBundle(classLoader, locale, this.bundleName + " not found by thread context classloader");
        }
        if (rb == null) {
            classLoader = PlatformResourceBundleLocator.run(GetClassLoader.fromClass(PlatformResourceBundleLocator.class));
            rb = this.loadBundle(classLoader, locale, this.bundleName + " not found by validator classloader");
        }
        if (rb != null) {
            log.debugf("%s found.", (Object)this.bundleName);
        } else {
            log.debugf("%s not found.", (Object)this.bundleName);
        }
        return rb;
    }

    private ResourceBundle loadBundle(ClassLoader classLoader, Locale locale, String message) {
        ResourceBundle rb = null;
        try {
            rb = this.aggregate ? ResourceBundle.getBundle(this.bundleName, locale, classLoader, AggregateResourceBundle.CONTROL) : ResourceBundle.getBundle(this.bundleName, locale, classLoader);
        }
        catch (MissingResourceException e) {
            log.trace(message);
        }
        return rb;
    }

    private static <T> T run(PrivilegedAction<T> action) {
        return System.getSecurityManager() != null ? AccessController.doPrivileged(action) : action.run();
    }

    private static boolean determineAvailabilityOfResourceBundleControl() {
        try {
            ResourceBundle.Control dummyControl = AggregateResourceBundle.CONTROL;
            if (dummyControl == null) {
                return false;
            }
            Method getModule = PlatformResourceBundleLocator.run(GetMethod.action(Class.class, "getModule"));
            if (getModule == null) {
                return true;
            }
            Object module = getModule.invoke(PlatformResourceBundleLocator.class, new Object[0]);
            Method isNamedMethod = PlatformResourceBundleLocator.run(GetMethod.action(module.getClass(), "isNamed"));
            boolean isNamed = (Boolean)isNamedMethod.invoke(module, new Object[0]);
            return !isNamed;
        }
        catch (Throwable e) {
            log.info(Messages.MESSAGES.unableToUseResourceBundleAggregation());
            return false;
        }
    }

    private static class AggregateResourceBundleControl
    extends ResourceBundle.Control {
        private AggregateResourceBundleControl() {
        }

        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
            if (!"java.properties".equals(format)) {
                return super.newBundle(baseName, locale, format, loader, reload);
            }
            String resourceName = this.toBundleName(baseName, locale) + ".properties";
            Properties properties = this.load(resourceName, loader);
            return properties.size() == 0 ? null : new AggregateResourceBundle(properties);
        }

        private Properties load(String resourceName, ClassLoader loader) throws IOException {
            Properties aggregatedProperties = new Properties();
            Enumeration urls = (Enumeration)PlatformResourceBundleLocator.run(GetResources.action(loader, resourceName));
            while (urls.hasMoreElements()) {
                URL url = (URL)urls.nextElement();
                Properties properties = new Properties();
                properties.load(url.openStream());
                aggregatedProperties.putAll((Map<?, ?>)properties);
            }
            return aggregatedProperties;
        }
    }

    private static class AggregateResourceBundle
    extends ResourceBundle {
        protected static final ResourceBundle.Control CONTROL = new AggregateResourceBundleControl();
        private final Properties properties;

        protected AggregateResourceBundle(Properties properties) {
            this.properties = properties;
        }

        @Override
        protected Object handleGetObject(String key) {
            return this.properties.get(key);
        }

        @Override
        public Enumeration<String> getKeys() {
            HashSet keySet = CollectionHelper.newHashSet();
            keySet.addAll(this.properties.stringPropertyNames());
            if (this.parent != null) {
                keySet.addAll(Collections.list(this.parent.getKeys()));
            }
            return Collections.enumeration(keySet);
        }
    }
}

