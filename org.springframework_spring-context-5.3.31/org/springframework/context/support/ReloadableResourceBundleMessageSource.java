/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.DefaultResourceLoader
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.core.io.support.ResourcePropertiesPersister
 *  org.springframework.lang.Nullable
 *  org.springframework.util.PropertiesPersister
 *  org.springframework.util.StringUtils
 */
package org.springframework.context.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.support.AbstractResourceBasedMessageSource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePropertiesPersister;
import org.springframework.lang.Nullable;
import org.springframework.util.PropertiesPersister;
import org.springframework.util.StringUtils;

public class ReloadableResourceBundleMessageSource
extends AbstractResourceBasedMessageSource
implements ResourceLoaderAware {
    private static final String PROPERTIES_SUFFIX = ".properties";
    private static final String XML_SUFFIX = ".xml";
    @Nullable
    private Properties fileEncodings;
    private boolean concurrentRefresh = true;
    private PropertiesPersister propertiesPersister = ResourcePropertiesPersister.INSTANCE;
    private ResourceLoader resourceLoader = new DefaultResourceLoader();
    private final ConcurrentMap<String, Map<Locale, List<String>>> cachedFilenames = new ConcurrentHashMap<String, Map<Locale, List<String>>>();
    private final ConcurrentMap<String, PropertiesHolder> cachedProperties = new ConcurrentHashMap<String, PropertiesHolder>();
    private final ConcurrentMap<Locale, PropertiesHolder> cachedMergedProperties = new ConcurrentHashMap<Locale, PropertiesHolder>();

    public void setFileEncodings(Properties fileEncodings) {
        this.fileEncodings = fileEncodings;
    }

    public void setConcurrentRefresh(boolean concurrentRefresh) {
        this.concurrentRefresh = concurrentRefresh;
    }

    public void setPropertiesPersister(@Nullable PropertiesPersister propertiesPersister) {
        this.propertiesPersister = propertiesPersister != null ? propertiesPersister : ResourcePropertiesPersister.INSTANCE;
    }

    @Override
    public void setResourceLoader(@Nullable ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader != null ? resourceLoader : new DefaultResourceLoader();
    }

    @Override
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        if (this.getCacheMillis() < 0L) {
            PropertiesHolder propHolder = this.getMergedProperties(locale);
            String result = propHolder.getProperty(code);
            if (result != null) {
                return result;
            }
        } else {
            for (String basename : this.getBasenameSet()) {
                List<String> filenames = this.calculateAllFilenames(basename, locale);
                for (String filename : filenames) {
                    PropertiesHolder propHolder = this.getProperties(filename);
                    String result = propHolder.getProperty(code);
                    if (result == null) continue;
                    return result;
                }
            }
        }
        return null;
    }

    @Override
    @Nullable
    protected MessageFormat resolveCode(String code, Locale locale) {
        if (this.getCacheMillis() < 0L) {
            PropertiesHolder propHolder = this.getMergedProperties(locale);
            MessageFormat result = propHolder.getMessageFormat(code, locale);
            if (result != null) {
                return result;
            }
        } else {
            for (String basename : this.getBasenameSet()) {
                List<String> filenames = this.calculateAllFilenames(basename, locale);
                for (String filename : filenames) {
                    PropertiesHolder propHolder = this.getProperties(filename);
                    MessageFormat result = propHolder.getMessageFormat(code, locale);
                    if (result == null) continue;
                    return result;
                }
            }
        }
        return null;
    }

    protected PropertiesHolder getMergedProperties(Locale locale) {
        PropertiesHolder mergedHolder = (PropertiesHolder)this.cachedMergedProperties.get(locale);
        if (mergedHolder != null) {
            return mergedHolder;
        }
        Properties mergedProps = this.newProperties();
        long latestTimestamp = -1L;
        String[] basenames = StringUtils.toStringArray(this.getBasenameSet());
        for (int i = basenames.length - 1; i >= 0; --i) {
            List<String> filenames = this.calculateAllFilenames(basenames[i], locale);
            for (int j = filenames.size() - 1; j >= 0; --j) {
                String filename = filenames.get(j);
                PropertiesHolder propHolder = this.getProperties(filename);
                if (propHolder.getProperties() == null) continue;
                mergedProps.putAll((Map<?, ?>)propHolder.getProperties());
                if (propHolder.getFileTimestamp() <= latestTimestamp) continue;
                latestTimestamp = propHolder.getFileTimestamp();
            }
        }
        mergedHolder = new PropertiesHolder(mergedProps, latestTimestamp);
        PropertiesHolder existing = this.cachedMergedProperties.putIfAbsent(locale, mergedHolder);
        if (existing != null) {
            mergedHolder = existing;
        }
        return mergedHolder;
    }

    protected List<String> calculateAllFilenames(String basename, Locale locale) {
        Map existing;
        ArrayList<String> filenames;
        Map<Locale, ArrayList<String>> localeMap = (ConcurrentHashMap<Locale, ArrayList<String>>)this.cachedFilenames.get(basename);
        if (localeMap != null && (filenames = (ArrayList<String>)localeMap.get(locale)) != null) {
            return filenames;
        }
        filenames = new ArrayList<String>(7);
        filenames.addAll(this.calculateFilenamesForLocale(basename, locale));
        Locale defaultLocale = this.getDefaultLocale();
        if (defaultLocale != null && !defaultLocale.equals(locale)) {
            List<String> fallbackFilenames = this.calculateFilenamesForLocale(basename, defaultLocale);
            for (String fallbackFilename : fallbackFilenames) {
                if (filenames.contains(fallbackFilename)) continue;
                filenames.add(fallbackFilename);
            }
        }
        filenames.add(basename);
        if (localeMap == null && (existing = (Map)this.cachedFilenames.putIfAbsent(basename, localeMap = new ConcurrentHashMap<Locale, ArrayList<String>>())) != null) {
            localeMap = existing;
        }
        localeMap.put(locale, filenames);
        return filenames;
    }

    protected List<String> calculateFilenamesForLocale(String basename, Locale locale) {
        ArrayList<String> result = new ArrayList<String>(3);
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        StringBuilder temp = new StringBuilder(basename);
        temp.append('_');
        if (language.length() > 0) {
            temp.append(language);
            result.add(0, temp.toString());
        }
        temp.append('_');
        if (country.length() > 0) {
            temp.append(country);
            result.add(0, temp.toString());
        }
        if (variant.length() > 0 && (language.length() > 0 || country.length() > 0)) {
            temp.append('_').append(variant);
            result.add(0, temp.toString());
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected PropertiesHolder getProperties(String filename) {
        PropertiesHolder existingHolder;
        PropertiesHolder propHolder = (PropertiesHolder)this.cachedProperties.get(filename);
        long originalTimestamp = -2L;
        if (propHolder != null) {
            originalTimestamp = propHolder.getRefreshTimestamp();
            if (originalTimestamp == -1L || originalTimestamp > System.currentTimeMillis() - this.getCacheMillis()) {
                return propHolder;
            }
        } else {
            propHolder = new PropertiesHolder();
            existingHolder = this.cachedProperties.putIfAbsent(filename, propHolder);
            if (existingHolder != null) {
                propHolder = existingHolder;
            }
        }
        if (this.concurrentRefresh && propHolder.getRefreshTimestamp() >= 0L) {
            if (!propHolder.refreshLock.tryLock()) {
                return propHolder;
            }
        } else {
            propHolder.refreshLock.lock();
        }
        try {
            existingHolder = (PropertiesHolder)this.cachedProperties.get(filename);
            if (existingHolder != null && existingHolder.getRefreshTimestamp() > originalTimestamp) {
                PropertiesHolder propertiesHolder = existingHolder;
                return propertiesHolder;
            }
            PropertiesHolder propertiesHolder = this.refreshProperties(filename, propHolder);
            return propertiesHolder;
        }
        finally {
            propHolder.refreshLock.unlock();
        }
    }

    protected PropertiesHolder refreshProperties(String filename, @Nullable PropertiesHolder propHolder) {
        long refreshTimestamp = this.getCacheMillis() < 0L ? -1L : System.currentTimeMillis();
        Resource resource = this.resourceLoader.getResource(filename + PROPERTIES_SUFFIX);
        if (!resource.exists()) {
            resource = this.resourceLoader.getResource(filename + XML_SUFFIX);
        }
        if (resource.exists()) {
            long fileTimestamp = -1L;
            if (this.getCacheMillis() >= 0L) {
                try {
                    fileTimestamp = resource.lastModified();
                    if (propHolder != null && propHolder.getFileTimestamp() == fileTimestamp) {
                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug((Object)("Re-caching properties for filename [" + filename + "] - file hasn't been modified"));
                        }
                        propHolder.setRefreshTimestamp(refreshTimestamp);
                        return propHolder;
                    }
                }
                catch (IOException ex) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug((Object)(resource + " could not be resolved in the file system - assuming that it hasn't changed"), (Throwable)ex);
                    }
                    fileTimestamp = -1L;
                }
            }
            try {
                Properties props = this.loadProperties(resource, filename);
                propHolder = new PropertiesHolder(props, fileTimestamp);
            }
            catch (IOException ex) {
                if (this.logger.isWarnEnabled()) {
                    this.logger.warn((Object)("Could not parse properties file [" + resource.getFilename() + "]"), (Throwable)ex);
                }
                propHolder = new PropertiesHolder();
            }
        } else {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("No properties file found for [" + filename + "] - neither plain properties nor XML"));
            }
            propHolder = new PropertiesHolder();
        }
        propHolder.setRefreshTimestamp(refreshTimestamp);
        this.cachedProperties.put(filename, propHolder);
        return propHolder;
    }

    protected Properties loadProperties(Resource resource, String filename) throws IOException {
        Properties props = this.newProperties();
        try (InputStream is = resource.getInputStream();){
            String resourceFilename = resource.getFilename();
            if (resourceFilename != null && resourceFilename.endsWith(XML_SUFFIX)) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Loading properties [" + resource.getFilename() + "]"));
                }
                this.propertiesPersister.loadFromXml(props, is);
            } else {
                String encoding = null;
                if (this.fileEncodings != null) {
                    encoding = this.fileEncodings.getProperty(filename);
                }
                if (encoding == null) {
                    encoding = this.getDefaultEncoding();
                }
                if (encoding != null) {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug((Object)("Loading properties [" + resource.getFilename() + "] with encoding '" + encoding + "'"));
                    }
                    this.propertiesPersister.load(props, (Reader)new InputStreamReader(is, encoding));
                } else {
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug((Object)("Loading properties [" + resource.getFilename() + "]"));
                    }
                    this.propertiesPersister.load(props, is);
                }
            }
            Properties properties = props;
            return properties;
        }
    }

    protected Properties newProperties() {
        return new Properties();
    }

    public void clearCache() {
        this.logger.debug((Object)"Clearing entire resource bundle cache");
        this.cachedProperties.clear();
        this.cachedMergedProperties.clear();
    }

    public void clearCacheIncludingAncestors() {
        this.clearCache();
        if (this.getParentMessageSource() instanceof ReloadableResourceBundleMessageSource) {
            ((ReloadableResourceBundleMessageSource)this.getParentMessageSource()).clearCacheIncludingAncestors();
        }
    }

    public String toString() {
        return this.getClass().getName() + ": basenames=" + this.getBasenameSet();
    }

    protected class PropertiesHolder {
        @Nullable
        private final Properties properties;
        private final long fileTimestamp;
        private volatile long refreshTimestamp = -2L;
        private final ReentrantLock refreshLock = new ReentrantLock();
        private final ConcurrentMap<String, Map<Locale, MessageFormat>> cachedMessageFormats = new ConcurrentHashMap<String, Map<Locale, MessageFormat>>();

        public PropertiesHolder() {
            this.properties = null;
            this.fileTimestamp = -1L;
        }

        public PropertiesHolder(Properties properties, long fileTimestamp) {
            this.properties = properties;
            this.fileTimestamp = fileTimestamp;
        }

        @Nullable
        public Properties getProperties() {
            return this.properties;
        }

        public long getFileTimestamp() {
            return this.fileTimestamp;
        }

        public void setRefreshTimestamp(long refreshTimestamp) {
            this.refreshTimestamp = refreshTimestamp;
        }

        public long getRefreshTimestamp() {
            return this.refreshTimestamp;
        }

        @Nullable
        public String getProperty(String code) {
            if (this.properties == null) {
                return null;
            }
            return this.properties.getProperty(code);
        }

        @Nullable
        public MessageFormat getMessageFormat(String code, Locale locale) {
            MessageFormat result;
            if (this.properties == null) {
                return null;
            }
            Map<Locale, MessageFormat> localeMap = (ConcurrentHashMap<Locale, MessageFormat>)this.cachedMessageFormats.get(code);
            if (localeMap != null && (result = (MessageFormat)localeMap.get(locale)) != null) {
                return result;
            }
            String msg = this.properties.getProperty(code);
            if (msg != null) {
                Map existing;
                if (localeMap == null && (existing = (Map)this.cachedMessageFormats.putIfAbsent(code, localeMap = new ConcurrentHashMap<Locale, MessageFormat>())) != null) {
                    localeMap = existing;
                }
                MessageFormat result2 = ReloadableResourceBundleMessageSource.this.createMessageFormat(msg, locale);
                localeMap.put(locale, result2);
                return result2;
            }
            return null;
        }
    }
}

