/*
 * Decompiled with CFR 0.152.
 */
package freemarker.cache;

import freemarker.cache.CacheStorage;
import freemarker.cache.ConcurrentCacheStorage;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.StatefulTemplateLoader;
import freemarker.cache.TemplateConfigurationFactory;
import freemarker.cache.TemplateConfigurationFactoryException;
import freemarker.cache.TemplateLoader;
import freemarker.cache.TemplateLookupContext;
import freemarker.cache.TemplateLookupResult;
import freemarker.cache.TemplateLookupStrategy;
import freemarker.cache.TemplateNameFormat;
import freemarker.cache.URLTemplateSource;
import freemarker.core.BugException;
import freemarker.core.Environment;
import freemarker.core.TemplateConfiguration;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template._TemplateAPI;
import freemarker.template._VersionInts;
import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.StringUtil;
import freemarker.template.utility.UndeclaredThrowableException;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class TemplateCache {
    public static final long DEFAULT_TEMPLATE_UPDATE_DELAY_MILLIS = 5000L;
    private static final String ASTERISKSTR = "*";
    private static final char ASTERISK = '*';
    private static final char SLASH = '/';
    private static final String LOCALE_PART_SEPARATOR = "_";
    private static final Logger LOG = Logger.getLogger("freemarker.cache");
    private final TemplateLoader templateLoader;
    private final CacheStorage storage;
    private final TemplateLookupStrategy templateLookupStrategy;
    private final TemplateNameFormat templateNameFormat;
    private final TemplateConfigurationFactory templateConfigurations;
    private final boolean isStorageConcurrent;
    private long updateDelay = 5000L;
    private boolean localizedLookup = true;
    private Configuration config;
    private static final Method INIT_CAUSE = TemplateCache.getInitCauseMethod();

    @Deprecated
    public TemplateCache() {
        this(_TemplateAPI.createDefaultTemplateLoader(Configuration.VERSION_2_3_0));
    }

    @Deprecated
    public TemplateCache(TemplateLoader templateLoader) {
        this(templateLoader, (Configuration)null);
    }

    @Deprecated
    public TemplateCache(TemplateLoader templateLoader, CacheStorage cacheStorage) {
        this(templateLoader, cacheStorage, null);
    }

    public TemplateCache(TemplateLoader templateLoader, Configuration config) {
        this(templateLoader, _TemplateAPI.createDefaultCacheStorage(Configuration.VERSION_2_3_0), config);
    }

    public TemplateCache(TemplateLoader templateLoader, CacheStorage cacheStorage, Configuration config) {
        this(templateLoader, cacheStorage, _TemplateAPI.getDefaultTemplateLookupStrategy(Configuration.VERSION_2_3_0), _TemplateAPI.getDefaultTemplateNameFormat(Configuration.VERSION_2_3_0), config);
    }

    public TemplateCache(TemplateLoader templateLoader, CacheStorage cacheStorage, TemplateLookupStrategy templateLookupStrategy, TemplateNameFormat templateNameFormat, Configuration config) {
        this(templateLoader, cacheStorage, templateLookupStrategy, templateNameFormat, null, config);
    }

    public TemplateCache(TemplateLoader templateLoader, CacheStorage cacheStorage, TemplateLookupStrategy templateLookupStrategy, TemplateNameFormat templateNameFormat, TemplateConfigurationFactory templateConfigurations, Configuration config) {
        this.templateLoader = templateLoader;
        NullArgumentException.check("cacheStorage", cacheStorage);
        this.storage = cacheStorage;
        this.isStorageConcurrent = cacheStorage instanceof ConcurrentCacheStorage && ((ConcurrentCacheStorage)cacheStorage).isConcurrent();
        NullArgumentException.check("templateLookupStrategy", templateLookupStrategy);
        this.templateLookupStrategy = templateLookupStrategy;
        NullArgumentException.check("templateNameFormat", templateNameFormat);
        this.templateNameFormat = templateNameFormat;
        this.templateConfigurations = templateConfigurations;
        this.config = config;
    }

    @Deprecated
    public void setConfiguration(Configuration config) {
        this.config = config;
        this.clear();
    }

    public TemplateLoader getTemplateLoader() {
        return this.templateLoader;
    }

    public CacheStorage getCacheStorage() {
        return this.storage;
    }

    public TemplateLookupStrategy getTemplateLookupStrategy() {
        return this.templateLookupStrategy;
    }

    public TemplateNameFormat getTemplateNameFormat() {
        return this.templateNameFormat;
    }

    public TemplateConfigurationFactory getTemplateConfigurations() {
        return this.templateConfigurations;
    }

    public MaybeMissingTemplate getTemplate(String name, Locale locale, Object customLookupCondition, String encoding, boolean parseAsFTL) throws IOException {
        NullArgumentException.check("name", name);
        NullArgumentException.check("locale", locale);
        NullArgumentException.check("encoding", encoding);
        try {
            name = this.templateNameFormat.normalizeRootBasedName(name);
        }
        catch (MalformedTemplateNameException e) {
            if (this.templateNameFormat != TemplateNameFormat.DEFAULT_2_3_0 || this.config.getIncompatibleImprovements().intValue() >= _VersionInts.V_2_4_0) {
                throw e;
            }
            return new MaybeMissingTemplate(null, e);
        }
        if (this.templateLoader == null) {
            return new MaybeMissingTemplate(name, "The TemplateLoader was null.");
        }
        Template template = this.getTemplateInternal(name, locale, customLookupCondition, encoding, parseAsFTL);
        return template != null ? new MaybeMissingTemplate(template) : new MaybeMissingTemplate(name, (String)null);
    }

    @Deprecated
    public Template getTemplate(String name, Locale locale, String encoding, boolean parseAsFTL) throws IOException {
        return this.getTemplate(name, locale, null, encoding, parseAsFTL).getTemplate();
    }

    @Deprecated
    protected static TemplateLoader createLegacyDefaultTemplateLoader() {
        return _TemplateAPI.createDefaultTemplateLoader(Configuration.VERSION_2_3_0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Template getTemplateInternal(String name, Locale locale, Object customLookupCondition, String encoding, boolean parseAsFTL) throws IOException {
        CachedTemplate cachedTemplate;
        boolean debug = LOG.isDebugEnabled();
        String debugName = debug ? this.buildDebugName(name, locale, customLookupCondition, encoding, parseAsFTL) : null;
        TemplateKey tk = new TemplateKey(name, locale, customLookupCondition, encoding, parseAsFTL);
        if (this.isStorageConcurrent) {
            cachedTemplate = (CachedTemplate)this.storage.get(tk);
        } else {
            CacheStorage cacheStorage = this.storage;
            synchronized (cacheStorage) {
                cachedTemplate = (CachedTemplate)this.storage.get(tk);
            }
        }
        long now = System.currentTimeMillis();
        long lastModified = -1L;
        boolean rethrown = false;
        TemplateLookupResult newLookupResult = null;
        try {
            Object source;
            if (cachedTemplate != null) {
                if (now - cachedTemplate.lastChecked < this.updateDelay) {
                    Object t;
                    if (debug) {
                        LOG.debug(debugName + " cached copy not yet stale; using cached.");
                    }
                    if ((t = cachedTemplate.templateOrException) instanceof Template || t == null) {
                        Template template = (Template)t;
                        return template;
                    }
                    if (t instanceof RuntimeException) {
                        this.throwLoadFailedException((RuntimeException)t);
                    } else if (t instanceof IOException) {
                        rethrown = true;
                        this.throwLoadFailedException((IOException)t);
                    }
                    throw new BugException("t is " + t.getClass().getName());
                }
                cachedTemplate = cachedTemplate.cloneCachedTemplate();
                cachedTemplate.lastChecked = now;
                newLookupResult = this.lookupTemplate(name, locale, customLookupCondition);
                if (!newLookupResult.isPositive()) {
                    if (debug) {
                        LOG.debug(debugName + " no source found.");
                    }
                    this.storeNegativeLookup(tk, cachedTemplate, null);
                    Template t = null;
                    return t;
                }
                Object newLookupResultSource = newLookupResult.getTemplateSource();
                lastModified = this.templateLoader.getLastModified(newLookupResultSource);
                boolean lastModifiedNotChanged = lastModified == cachedTemplate.lastModified;
                boolean sourceEquals = newLookupResultSource.equals(cachedTemplate.source);
                if (lastModifiedNotChanged && sourceEquals) {
                    if (debug) {
                        LOG.debug(debugName + ": using cached since " + newLookupResultSource + " hasn't changed.");
                    }
                    this.storeCached(tk, cachedTemplate);
                    Template template = (Template)cachedTemplate.templateOrException;
                    return template;
                }
                if (debug) {
                    if (!sourceEquals) {
                        LOG.debug("Updating source because: sourceEquals=" + sourceEquals + ", newlyFoundSource=" + StringUtil.jQuoteNoXSS(newLookupResultSource) + ", cached.source=" + StringUtil.jQuoteNoXSS(cachedTemplate.source));
                    } else if (!lastModifiedNotChanged) {
                        LOG.debug("Updating source because: lastModifiedNotChanged=" + lastModifiedNotChanged + ", cached.lastModified=" + cachedTemplate.lastModified + " != source.lastModified=" + lastModified);
                    }
                }
            } else {
                if (debug) {
                    LOG.debug("Couldn't find template in cache for " + debugName + "; will try to load it.");
                }
                cachedTemplate = new CachedTemplate();
                cachedTemplate.lastChecked = now;
                newLookupResult = this.lookupTemplate(name, locale, customLookupCondition);
                if (!newLookupResult.isPositive()) {
                    this.storeNegativeLookup(tk, cachedTemplate, null);
                    Template newLookupResultSource = null;
                    return newLookupResultSource;
                }
                lastModified = Long.MIN_VALUE;
                cachedTemplate.lastModified = Long.MIN_VALUE;
            }
            cachedTemplate.source = source = newLookupResult.getTemplateSource();
            if (debug) {
                LOG.debug("Loading template for " + debugName + " from " + StringUtil.jQuoteNoXSS(source));
            }
            lastModified = lastModified == Long.MIN_VALUE ? this.templateLoader.getLastModified(source) : lastModified;
            Template template = this.loadTemplate(this.templateLoader, source, name, newLookupResult.getTemplateSourceName(), locale, customLookupCondition, encoding, parseAsFTL);
            cachedTemplate.templateOrException = template;
            cachedTemplate.lastModified = lastModified;
            this.storeCached(tk, cachedTemplate);
            Template template2 = template;
            return template2;
        }
        catch (RuntimeException e) {
            if (cachedTemplate != null) {
                this.storeNegativeLookup(tk, cachedTemplate, e);
            }
            throw e;
        }
        catch (IOException e) {
            if (!rethrown) {
                this.storeNegativeLookup(tk, cachedTemplate, e);
            }
            throw e;
        }
        finally {
            if (newLookupResult != null && newLookupResult.isPositive()) {
                this.templateLoader.closeTemplateSource(newLookupResult.getTemplateSource());
            }
        }
    }

    private static final Method getInitCauseMethod() {
        try {
            return Throwable.class.getMethod("initCause", Throwable.class);
        }
        catch (NoSuchMethodException e) {
            return null;
        }
    }

    private IOException newIOException(String message, Throwable cause) {
        IOException ioe;
        if (cause == null) {
            return new IOException(message);
        }
        if (INIT_CAUSE != null) {
            ioe = new IOException(message);
            try {
                INIT_CAUSE.invoke((Object)ioe, cause);
            }
            catch (RuntimeException ex) {
                throw ex;
            }
            catch (Exception ex) {
                throw new UndeclaredThrowableException(ex);
            }
        } else {
            ioe = new IOException(message + "\nCaused by: " + cause.getClass().getName() + ": " + cause.getMessage());
        }
        return ioe;
    }

    private void throwLoadFailedException(Throwable e) throws IOException {
        throw this.newIOException("There was an error loading the template on an earlier attempt; see cause exception.", e);
    }

    private void storeNegativeLookup(TemplateKey tk, CachedTemplate cachedTemplate, Exception e) {
        cachedTemplate.templateOrException = e;
        cachedTemplate.source = null;
        cachedTemplate.lastModified = 0L;
        this.storeCached(tk, cachedTemplate);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void storeCached(TemplateKey tk, CachedTemplate cachedTemplate) {
        if (this.isStorageConcurrent) {
            this.storage.put(tk, cachedTemplate);
        } else {
            CacheStorage cacheStorage = this.storage;
            synchronized (cacheStorage) {
                this.storage.put(tk, cachedTemplate);
            }
        }
    }

    private Template loadTemplate(TemplateLoader templateLoader, Object source, String name, String sourceName, Locale locale, Object customLookupCondition, String initialEncoding, boolean parseAsFTL) throws IOException {
        Template template;
        TemplateConfiguration tc;
        block48: {
            try {
                tc = this.templateConfigurations != null ? this.templateConfigurations.get(sourceName, source) : null;
            }
            catch (TemplateConfigurationFactoryException e) {
                throw this.newIOException("Error while getting TemplateConfiguration; see cause exception.", e);
            }
            if (tc != null) {
                if (tc.isEncodingSet()) {
                    initialEncoding = tc.getEncoding();
                }
                if (tc.isLocaleSet()) {
                    locale = tc.getLocale();
                }
            }
            if (parseAsFTL) {
                try (Reader reader = templateLoader.getReader(source, initialEncoding);){
                    template = new Template(name, sourceName, reader, this.config, tc, initialEncoding);
                    break block48;
                }
                catch (Template.WrongEncodingException wee) {
                    String actualEncoding = wee.getTemplateSpecifiedEncoding();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Initial encoding \"" + initialEncoding + "\" was incorrect, re-reading with \"" + actualEncoding + "\". Template: " + sourceName);
                    }
                    try (Reader reader2 = templateLoader.getReader(source, actualEncoding);){
                        template = new Template(name, sourceName, reader2, this.config, tc, actualEncoding);
                        break block48;
                    }
                }
            }
            StringWriter sw = new StringWriter();
            char[] buf = new char[4096];
            try (Reader reader = templateLoader.getReader(source, initialEncoding);){
                while (true) {
                    int charsRead;
                    if ((charsRead = reader.read(buf)) > 0) {
                        sw.write(buf, 0, charsRead);
                        continue;
                    }
                    if (charsRead < 0) break;
                }
            }
            template = Template.getPlainTextTemplate(name, sourceName, sw.toString(), this.config);
            template.setEncoding(initialEncoding);
        }
        if (tc != null) {
            tc.apply(template);
        }
        template.setLocale(locale);
        template.setCustomLookupCondition(customLookupCondition);
        return template;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long getDelay() {
        TemplateCache templateCache = this;
        synchronized (templateCache) {
            return this.updateDelay;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setDelay(long delay) {
        TemplateCache templateCache = this;
        synchronized (templateCache) {
            this.updateDelay = delay;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean getLocalizedLookup() {
        TemplateCache templateCache = this;
        synchronized (templateCache) {
            return this.localizedLookup;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setLocalizedLookup(boolean localizedLookup) {
        TemplateCache templateCache = this;
        synchronized (templateCache) {
            if (this.localizedLookup != localizedLookup) {
                this.localizedLookup = localizedLookup;
                this.clear();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clear() {
        CacheStorage cacheStorage = this.storage;
        synchronized (cacheStorage) {
            this.storage.clear();
            if (this.templateLoader instanceof StatefulTemplateLoader) {
                ((StatefulTemplateLoader)this.templateLoader).resetState();
            }
        }
    }

    public void removeTemplate(String name, Locale locale, String encoding, boolean parse) throws IOException {
        this.removeTemplate(name, locale, null, encoding, parse);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeTemplate(String name, Locale locale, Object customLookupCondition, String encoding, boolean parse) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException("Argument \"name\" can't be null");
        }
        if (locale == null) {
            throw new IllegalArgumentException("Argument \"locale\" can't be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument \"encoding\" can't be null");
        }
        if ((name = this.templateNameFormat.normalizeRootBasedName(name)) != null && this.templateLoader != null) {
            boolean debug = LOG.isDebugEnabled();
            String debugName = debug ? this.buildDebugName(name, locale, customLookupCondition, encoding, parse) : null;
            TemplateKey tk = new TemplateKey(name, locale, customLookupCondition, encoding, parse);
            if (this.isStorageConcurrent) {
                this.storage.remove(tk);
            } else {
                CacheStorage cacheStorage = this.storage;
                synchronized (cacheStorage) {
                    this.storage.remove(tk);
                }
            }
            if (debug) {
                LOG.debug(debugName + " was removed from the cache, if it was there");
            }
        }
    }

    private String buildDebugName(String name, Locale locale, Object customLookupCondition, String encoding, boolean parse) {
        return StringUtil.jQuoteNoXSS(name) + "(" + StringUtil.jQuoteNoXSS(locale) + (customLookupCondition != null ? ", cond=" + StringUtil.jQuoteNoXSS(customLookupCondition) : "") + ", " + encoding + (parse ? ", parsed)" : ", unparsed]");
    }

    @Deprecated
    public static String getFullTemplatePath(Environment env, String baseName, String targetName) {
        try {
            return env.toFullTemplateName(baseName, targetName);
        }
        catch (MalformedTemplateNameException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private TemplateLookupResult lookupTemplate(String name, Locale locale, Object customLookupCondition) throws IOException {
        TemplateLookupResult lookupResult = this.templateLookupStrategy.lookup(new TemplateCacheTemplateLookupContext(name, locale, customLookupCondition));
        if (lookupResult == null) {
            throw new NullPointerException("Lookup result shouldn't be null");
        }
        return lookupResult;
    }

    private TemplateLookupResult lookupTemplateWithAcquisitionStrategy(String path) throws IOException {
        int asterisk = path.indexOf(42);
        if (asterisk == -1) {
            return TemplateLookupResult.from(path, this.findTemplateSource(path));
        }
        StringTokenizer tok = new StringTokenizer(path, "/");
        int lastAsterisk = -1;
        ArrayList<String> tokpath = new ArrayList<String>();
        while (tok.hasMoreTokens()) {
            String pathToken = tok.nextToken();
            if (pathToken.equals(ASTERISKSTR)) {
                if (lastAsterisk != -1) {
                    tokpath.remove(lastAsterisk);
                }
                lastAsterisk = tokpath.size();
            }
            tokpath.add(pathToken);
        }
        if (lastAsterisk == -1) {
            return TemplateLookupResult.from(path, this.findTemplateSource(path));
        }
        String basePath = this.concatPath(tokpath, 0, lastAsterisk);
        String resourcePath = this.concatPath(tokpath, lastAsterisk + 1, tokpath.size());
        if (resourcePath.endsWith("/")) {
            resourcePath = resourcePath.substring(0, resourcePath.length() - 1);
        }
        StringBuilder buf = new StringBuilder(path.length()).append(basePath);
        int l = basePath.length();
        String fullPath;
        Object templateSource;
        while ((templateSource = this.findTemplateSource(fullPath = buf.append(resourcePath).toString())) == null) {
            if (l == 0) {
                return TemplateLookupResult.createNegativeResult();
            }
            l = basePath.lastIndexOf(47, l - 2) + 1;
            buf.setLength(l);
        }
        return TemplateLookupResult.from(fullPath, templateSource);
    }

    private Object findTemplateSource(String path) throws IOException {
        Object result = this.templateLoader.findTemplateSource(path);
        if (LOG.isDebugEnabled()) {
            LOG.debug("TemplateLoader.findTemplateSource(" + StringUtil.jQuote(path) + "): " + (result == null ? "Not found" : "Found"));
        }
        return this.modifyForConfIcI(result);
    }

    private Object modifyForConfIcI(Object templateSource) {
        if (templateSource == null) {
            return null;
        }
        if (this.config.getIncompatibleImprovements().intValue() < _VersionInts.V_2_3_21) {
            return templateSource;
        }
        if (templateSource instanceof URLTemplateSource) {
            URLTemplateSource urlTemplateSource = (URLTemplateSource)templateSource;
            if (urlTemplateSource.getUseCaches() == null) {
                urlTemplateSource.setUseCaches(false);
            }
        } else if (templateSource instanceof MultiTemplateLoader.MultiSource) {
            this.modifyForConfIcI(((MultiTemplateLoader.MultiSource)templateSource).getWrappedSource());
        }
        return templateSource;
    }

    private String concatPath(List path, int from, int to) {
        StringBuilder buf = new StringBuilder((to - from) * 16);
        for (int i = from; i < to; ++i) {
            buf.append(path.get(i)).append('/');
        }
        return buf.toString();
    }

    public static final class MaybeMissingTemplate {
        private final Template template;
        private final String missingTemplateNormalizedName;
        private final String missingTemplateReason;
        private final MalformedTemplateNameException missingTemplateCauseException;

        private MaybeMissingTemplate(Template template) {
            this.template = template;
            this.missingTemplateNormalizedName = null;
            this.missingTemplateReason = null;
            this.missingTemplateCauseException = null;
        }

        private MaybeMissingTemplate(String normalizedName, MalformedTemplateNameException missingTemplateCauseException) {
            this.template = null;
            this.missingTemplateNormalizedName = normalizedName;
            this.missingTemplateReason = null;
            this.missingTemplateCauseException = missingTemplateCauseException;
        }

        private MaybeMissingTemplate(String normalizedName, String missingTemplateReason) {
            this.template = null;
            this.missingTemplateNormalizedName = normalizedName;
            this.missingTemplateReason = missingTemplateReason;
            this.missingTemplateCauseException = null;
        }

        public Template getTemplate() {
            return this.template;
        }

        public String getMissingTemplateReason() {
            return this.missingTemplateReason != null ? this.missingTemplateReason : (this.missingTemplateCauseException != null ? this.missingTemplateCauseException.getMalformednessDescription() : null);
        }

        public String getMissingTemplateNormalizedName() {
            return this.missingTemplateNormalizedName;
        }
    }

    private class TemplateCacheTemplateLookupContext
    extends TemplateLookupContext {
        TemplateCacheTemplateLookupContext(String templateName, Locale templateLocale, Object customLookupCondition) {
            super(templateName, TemplateCache.this.localizedLookup ? templateLocale : null, customLookupCondition);
        }

        @Override
        public TemplateLookupResult lookupWithAcquisitionStrategy(String name) throws IOException {
            if (name.startsWith("/")) {
                throw new IllegalArgumentException("Non-normalized name, starts with \"/\": " + name);
            }
            return TemplateCache.this.lookupTemplateWithAcquisitionStrategy(name);
        }

        @Override
        public TemplateLookupResult lookupWithLocalizedThenAcquisitionStrategy(String templateName, Locale templateLocale) throws IOException {
            if (templateLocale == null) {
                return this.lookupWithAcquisitionStrategy(templateName);
            }
            int lastDot = templateName.lastIndexOf(46);
            String prefix = lastDot == -1 ? templateName : templateName.substring(0, lastDot);
            String suffix = lastDot == -1 ? "" : templateName.substring(lastDot);
            String localeName = TemplateCache.LOCALE_PART_SEPARATOR + templateLocale.toString();
            StringBuilder buf = new StringBuilder(templateName.length() + localeName.length());
            buf.append(prefix);
            while (true) {
                buf.setLength(prefix.length());
                String path = buf.append(localeName).append(suffix).toString();
                TemplateLookupResult lookupResult = this.lookupWithAcquisitionStrategy(path);
                if (lookupResult.isPositive()) {
                    return lookupResult;
                }
                int lastUnderscore = localeName.lastIndexOf(95);
                if (lastUnderscore == -1) break;
                localeName = localeName.substring(0, lastUnderscore);
            }
            return this.createNegativeLookupResult();
        }
    }

    private static final class CachedTemplate
    implements Cloneable,
    Serializable {
        private static final long serialVersionUID = 1L;
        Object templateOrException;
        Object source;
        long lastChecked;
        long lastModified;

        private CachedTemplate() {
        }

        public CachedTemplate cloneCachedTemplate() {
            try {
                return (CachedTemplate)super.clone();
            }
            catch (CloneNotSupportedException e) {
                throw new UndeclaredThrowableException(e);
            }
        }
    }

    private static final class TemplateKey {
        private final String name;
        private final Locale locale;
        private final Object customLookupCondition;
        private final String encoding;
        private final boolean parse;

        TemplateKey(String name, Locale locale, Object customLookupCondition, String encoding, boolean parse) {
            this.name = name;
            this.locale = locale;
            this.customLookupCondition = customLookupCondition;
            this.encoding = encoding;
            this.parse = parse;
        }

        public boolean equals(Object o) {
            if (o instanceof TemplateKey) {
                TemplateKey tk = (TemplateKey)o;
                return this.parse == tk.parse && this.name.equals(tk.name) && this.locale.equals(tk.locale) && this.nullSafeEquals(this.customLookupCondition, tk.customLookupCondition) && this.encoding.equals(tk.encoding);
            }
            return false;
        }

        private boolean nullSafeEquals(Object o1, Object o2) {
            return o1 != null ? (o2 != null ? o1.equals(o2) : false) : o2 == null;
        }

        public int hashCode() {
            return this.name.hashCode() ^ this.locale.hashCode() ^ this.encoding.hashCode() ^ (this.customLookupCondition != null ? this.customLookupCondition.hashCode() : 0) ^ Boolean.valueOf(!this.parse).hashCode();
        }
    }
}

