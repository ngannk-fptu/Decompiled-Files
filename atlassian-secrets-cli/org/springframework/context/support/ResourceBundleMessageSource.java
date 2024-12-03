/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.support.AbstractResourceBasedMessageSource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class ResourceBundleMessageSource
extends AbstractResourceBasedMessageSource
implements BeanClassLoaderAware {
    @Nullable
    private ClassLoader bundleClassLoader;
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    private final Map<String, Map<Locale, ResourceBundle>> cachedResourceBundles = new ConcurrentHashMap<String, Map<Locale, ResourceBundle>>();
    private final Map<ResourceBundle, Map<String, Map<Locale, MessageFormat>>> cachedBundleMessageFormats = new ConcurrentHashMap<ResourceBundle, Map<String, Map<Locale, MessageFormat>>>();
    @Nullable
    private volatile MessageSourceControl control = new MessageSourceControl();

    public void setBundleClassLoader(ClassLoader classLoader) {
        this.bundleClassLoader = classLoader;
    }

    @Nullable
    protected ClassLoader getBundleClassLoader() {
        return this.bundleClassLoader != null ? this.bundleClassLoader : this.beanClassLoader;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        Set<String> basenames = this.getBasenameSet();
        for (String basename : basenames) {
            String result;
            ResourceBundle bundle = this.getResourceBundle(basename, locale);
            if (bundle == null || (result = this.getStringOrNull(bundle, code)) == null) continue;
            return result;
        }
        return null;
    }

    @Override
    @Nullable
    protected MessageFormat resolveCode(String code, Locale locale) {
        Set<String> basenames = this.getBasenameSet();
        for (String basename : basenames) {
            MessageFormat messageFormat;
            ResourceBundle bundle = this.getResourceBundle(basename, locale);
            if (bundle == null || (messageFormat = this.getMessageFormat(bundle, code, locale)) == null) continue;
            return messageFormat;
        }
        return null;
    }

    @Nullable
    protected ResourceBundle getResourceBundle(String basename, Locale locale) {
        ResourceBundle bundle;
        if (this.getCacheMillis() >= 0L) {
            return this.doGetBundle(basename, locale);
        }
        Map<Locale, ResourceBundle> localeMap = this.cachedResourceBundles.get(basename);
        if (localeMap != null && (bundle = localeMap.get(locale)) != null) {
            return bundle;
        }
        try {
            Map<Locale, ResourceBundle> existing;
            bundle = this.doGetBundle(basename, locale);
            if (localeMap == null && (existing = this.cachedResourceBundles.putIfAbsent(basename, localeMap = new ConcurrentHashMap<Locale, ResourceBundle>())) != null) {
                localeMap = existing;
            }
            localeMap.put(locale, bundle);
            return bundle;
        }
        catch (MissingResourceException ex) {
            if (this.logger.isWarnEnabled()) {
                this.logger.warn("ResourceBundle [" + basename + "] not found for MessageSource: " + ex.getMessage());
            }
            return null;
        }
    }

    protected ResourceBundle doGetBundle(String basename, Locale locale) throws MissingResourceException {
        ClassLoader classLoader;
        block3: {
            classLoader = this.getBundleClassLoader();
            Assert.state(classLoader != null, "No bundle ClassLoader set");
            MessageSourceControl control = this.control;
            if (control != null) {
                try {
                    return ResourceBundle.getBundle(basename, locale, classLoader, control);
                }
                catch (UnsupportedOperationException ex) {
                    this.control = null;
                    if (!this.logger.isInfoEnabled()) break block3;
                    this.logger.info("ResourceBundle.Control not supported in current system environment: " + ex.getMessage() + " - falling back to plain ResourceBundle.getBundle retrieval.");
                }
            }
        }
        return ResourceBundle.getBundle(basename, locale, classLoader);
    }

    protected ResourceBundle loadBundle(Reader reader) throws IOException {
        return new PropertyResourceBundle(reader);
    }

    @Nullable
    protected MessageFormat getMessageFormat(ResourceBundle bundle, String code, Locale locale) throws MissingResourceException {
        MessageFormat result;
        Map<String, Map<Locale, MessageFormat>> codeMap = this.cachedBundleMessageFormats.get(bundle);
        Map<Object, Object> localeMap = null;
        if (codeMap != null && (localeMap = codeMap.get(code)) != null && (result = localeMap.get(locale)) != null) {
            return result;
        }
        String msg = this.getStringOrNull(bundle, code);
        if (msg != null) {
            Map<Object, Object> existing;
            if (codeMap == null && (existing = this.cachedBundleMessageFormats.putIfAbsent(bundle, codeMap = new ConcurrentHashMap<String, Map<Locale, MessageFormat>>())) != null) {
                codeMap = existing;
            }
            if (localeMap == null && (existing = codeMap.putIfAbsent(code, localeMap = new ConcurrentHashMap<Locale, MessageFormat>())) != null) {
                localeMap = existing;
            }
            MessageFormat result2 = this.createMessageFormat(msg, locale);
            localeMap.put(locale, result2);
            return result2;
        }
        return null;
    }

    @Nullable
    protected String getStringOrNull(ResourceBundle bundle, String key) {
        if (bundle.containsKey(key)) {
            try {
                return bundle.getString(key);
            }
            catch (MissingResourceException missingResourceException) {
                // empty catch block
            }
        }
        return null;
    }

    public String toString() {
        return this.getClass().getName() + ": basenames=" + this.getBasenameSet();
    }

    private class MessageSourceControl
    extends ResourceBundle.Control {
        private MessageSourceControl() {
        }

        @Override
        @Nullable
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
            if (format.equals("java.properties")) {
                InputStream inputStream;
                String bundleName = this.toBundleName(baseName, locale);
                String resourceName = this.toResourceName(bundleName, "properties");
                ClassLoader classLoader = loader;
                boolean reloadFlag = reload;
                try {
                    inputStream = AccessController.doPrivileged(() -> {
                        InputStream is = null;
                        if (reloadFlag) {
                            URLConnection connection;
                            URL url = classLoader.getResource(resourceName);
                            if (url != null && (connection = url.openConnection()) != null) {
                                connection.setUseCaches(false);
                                is = connection.getInputStream();
                            }
                        } else {
                            is = classLoader.getResourceAsStream(resourceName);
                        }
                        return is;
                    });
                }
                catch (PrivilegedActionException ex) {
                    throw (IOException)ex.getException();
                }
                if (inputStream != null) {
                    String encoding = ResourceBundleMessageSource.this.getDefaultEncoding();
                    if (encoding == null) {
                        encoding = "ISO-8859-1";
                    }
                    try (InputStreamReader bundleReader = new InputStreamReader(inputStream, encoding);){
                        ResourceBundle resourceBundle = ResourceBundleMessageSource.this.loadBundle(bundleReader);
                        return resourceBundle;
                    }
                }
                return null;
            }
            return super.newBundle(baseName, locale, format, loader, reload);
        }

        @Override
        @Nullable
        public Locale getFallbackLocale(String baseName, Locale locale) {
            return ResourceBundleMessageSource.this.isFallbackToSystemLocale() ? super.getFallbackLocale(baseName, locale) : null;
        }

        @Override
        public long getTimeToLive(String baseName, Locale locale) {
            long cacheMillis = ResourceBundleMessageSource.this.getCacheMillis();
            return cacheMillis >= 0L ? cacheMillis : super.getTimeToLive(baseName, locale);
        }

        @Override
        public boolean needsReload(String baseName, Locale locale, String format, ClassLoader loader, ResourceBundle bundle, long loadTime) {
            if (super.needsReload(baseName, locale, format, loader, bundle, loadTime)) {
                ResourceBundleMessageSource.this.cachedBundleMessageFormats.remove(bundle);
                return true;
            }
            return false;
        }
    }
}

