/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.message.Message
 *  org.apache.logging.log4j.message.ParameterizedMessage
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.LocalizedTextProvider;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;

abstract class AbstractLocalizedTextProvider
implements LocalizedTextProvider {
    private static final Logger LOG = LogManager.getLogger(AbstractLocalizedTextProvider.class);
    public static final String XWORK_MESSAGES_BUNDLE = "com/opensymphony/xwork2/xwork-messages";
    public static final String STRUTS_MESSAGES_BUNDLE = "org/apache/struts2/struts-messages";
    private static final String TOMCAT_RESOURCE_ENTRIES_FIELD = "resourceEntries";
    private static final String TOMCAT_PARALLEL_WEBAPP_CLASSLOADER = "org.apache.catalina.loader.ParallelWebappClassLoader";
    private static final String TOMCAT_WEBAPP_CLASSLOADER = "org.apache.catalina.loader.WebappClassLoader";
    private static final String TOMCAT_WEBAPP_CLASSLOADER_BASE = "org.apache.catalina.loader.WebappClassLoaderBase";
    private static final String RELOADED = "com.opensymphony.xwork2.util.LocalizedTextProvider.reloaded";
    protected final ConcurrentMap<String, ResourceBundle> bundlesMap = new ConcurrentHashMap<String, ResourceBundle>();
    protected boolean devMode = false;
    protected boolean reloadBundles = false;
    protected boolean searchDefaultBundlesFirst = false;
    private final ConcurrentMap<MessageFormatKey, MessageFormat> messageFormats = new ConcurrentHashMap<MessageFormatKey, MessageFormat>();
    private final ConcurrentMap<Integer, List<String>> classLoaderMap = new ConcurrentHashMap<Integer, List<String>>();
    private final Set<String> missingBundles = ConcurrentHashMap.newKeySet();
    private final ConcurrentMap<Integer, ClassLoader> delegatedClassLoaderMap = new ConcurrentHashMap<Integer, ClassLoader>();

    AbstractLocalizedTextProvider() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addDefaultResourceBundle(String resourceBundleName) {
        ClassLoader ccl = this.getCurrentThreadContextClassLoader();
        String string = XWORK_MESSAGES_BUNDLE;
        synchronized (XWORK_MESSAGES_BUNDLE) {
            CopyOnWriteArrayList<String> bundles = (CopyOnWriteArrayList<String>)this.classLoaderMap.get(ccl.hashCode());
            if (bundles == null) {
                bundles = new CopyOnWriteArrayList<String>();
                this.classLoaderMap.put(ccl.hashCode(), bundles);
            }
            bundles.remove(resourceBundleName);
            bundles.add(0, resourceBundleName);
            // ** MonitorExit[var3_3] (shouldn't be in output)
            if (LOG.isDebugEnabled()) {
                LOG.debug("Added default resource bundle '{}' to default resource bundles for the following classloader '{}'", (Object)resourceBundleName, (Object)ccl.toString());
            }
            return;
        }
    }

    protected List<String> getCurrentBundleNames() {
        return (List)this.classLoaderMap.get(this.getCurrentThreadContextClassLoader().hashCode());
    }

    protected ClassLoader getCurrentThreadContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    @Inject(value="struts.custom.i18n.resources", required=false)
    public void setCustomI18NResources(String bundles) {
        if (bundles != null && bundles.length() > 0) {
            StringTokenizer customBundles = new StringTokenizer(bundles, ", ");
            while (customBundles.hasMoreTokens()) {
                String name = customBundles.nextToken();
                try {
                    LOG.trace("Loading global messages from [{}]", (Object)name);
                    this.addDefaultResourceBundle(name);
                }
                catch (Exception e) {
                    LOG.error((Message)new ParameterizedMessage("Could not find messages file {}.properties. Skipping", (Object)name), (Throwable)e);
                }
            }
        }
    }

    @Override
    public String findDefaultText(String aTextName, Locale locale) {
        List<String> localList = this.getCurrentBundleNames();
        for (String bundleName : localList) {
            ResourceBundle bundle = this.findResourceBundle(bundleName, locale);
            if (bundle == null) continue;
            this.reloadBundles();
            try {
                return bundle.getString(aTextName);
            }
            catch (MissingResourceException missingResourceException) {
            }
        }
        if (this.devMode) {
            LOG.warn("Missing key [{}] in bundles [{}]!", (Object)aTextName, localList);
        } else {
            LOG.debug("Missing key [{}] in bundles [{}]!", (Object)aTextName, localList);
        }
        return null;
    }

    @Override
    public String findDefaultText(String aTextName, Locale locale, Object[] params) {
        String defaultText = this.findDefaultText(aTextName, locale);
        if (defaultText != null) {
            MessageFormat mf = this.buildMessageFormat(defaultText, locale);
            return this.formatWithNullDetection(mf, params);
        }
        return null;
    }

    @Override
    public String findText(ResourceBundle bundle, String aTextName, Locale locale, String defaultMessage, Object[] args, ValueStack valueStack) {
        try {
            this.reloadBundles(valueStack.getContext());
            String message = TextParseUtil.translateVariables(bundle.getString(aTextName), valueStack);
            MessageFormat mf = this.buildMessageFormat(message, locale);
            return this.formatWithNullDetection(mf, args);
        }
        catch (MissingResourceException ex) {
            if (this.devMode) {
                LOG.warn("Missing key [{}] in bundle [{}]!", (Object)aTextName, (Object)bundle);
            } else {
                LOG.debug("Missing key [{}] in bundle [{}]!", (Object)aTextName, (Object)bundle);
            }
            GetDefaultMessageReturnArg result = this.getDefaultMessage(aTextName, locale, valueStack, args, defaultMessage);
            if (this.unableToFindTextForKey(result)) {
                LOG.warn("Unable to find text for key '{}' in ResourceBundles for locale '{}'", (Object)aTextName, (Object)locale);
            }
            return result != null ? result.message : null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setDelegatedClassLoader(ClassLoader classLoader) {
        ConcurrentMap<String, ResourceBundle> concurrentMap = this.bundlesMap;
        synchronized (concurrentMap) {
            this.delegatedClassLoaderMap.put(this.getCurrentThreadContextClassLoader().hashCode(), classLoader);
        }
    }

    public void clearBundle(String bundleName) {
        LOG.debug("No-op.  Did NOT clear resource bundle [{}], result: false.", (Object)bundleName);
    }

    protected void clearBundle(String bundleName, Locale locale) {
        String key = this.createMissesKey(String.valueOf(this.getCurrentThreadContextClassLoader().hashCode()), bundleName, locale);
        ResourceBundle removedBundle = (ResourceBundle)this.bundlesMap.remove(key);
        LOG.debug("Clearing resource bundle [{}], locale [{}], result: [{}].", (Object)bundleName, (Object)locale, (Object)(removedBundle != null ? 1 : 0));
    }

    protected void clearMissingBundlesCache() {
        this.missingBundles.clear();
        LOG.debug("Cleared the missing bundles cache.");
    }

    protected void reloadBundles() {
        this.reloadBundles(ActionContext.getContext() != null ? ActionContext.getContext().getContextMap() : null);
    }

    protected void reloadBundles(Map<String, Object> context) {
        if (this.reloadBundles) {
            try {
                Boolean reloaded = context != null ? (Boolean)ObjectUtils.defaultIfNull((Object)context.get(RELOADED), (Object)Boolean.FALSE) : Boolean.FALSE;
                if (!reloaded.booleanValue()) {
                    this.bundlesMap.clear();
                    this.clearResourceBundleClassloaderCaches();
                    this.clearTomcatCache();
                    if (context != null) {
                        context.put(RELOADED, true);
                    }
                    LOG.debug("Resource bundles reloaded");
                }
            }
            catch (Exception e) {
                LOG.error("Could not reload resource bundles", (Throwable)e);
            }
        }
    }

    private void clearResourceBundleClassloaderCaches() {
        ClassLoader ccl = this.getCurrentThreadContextClassLoader();
        ResourceBundle.clearCache();
        ResourceBundle.clearCache(ccl);
        this.delegatedClassLoaderMap.forEach((key, value) -> {
            if (value != null) {
                ResourceBundle.clearCache(value);
            }
        });
    }

    private void clearTomcatCache() {
        ClassLoader loader = this.getCurrentThreadContextClassLoader();
        Class<?> cl = loader.getClass();
        Class<?> superCl = cl.getSuperclass();
        try {
            if ((TOMCAT_WEBAPP_CLASSLOADER.equals(cl.getName()) || TOMCAT_PARALLEL_WEBAPP_CLASSLOADER.equals(cl.getName())) && superCl != null && TOMCAT_WEBAPP_CLASSLOADER_BASE.equals(superCl.getName())) {
                this.clearMap(superCl, loader, TOMCAT_RESOURCE_ENTRIES_FIELD);
                LOG.debug("Cleared tomcat cache via classloader's parent class.");
            } else {
                LOG.debug("Class loader {} is not tomcat loader.", (Object)cl.getName());
            }
        }
        catch (NoSuchFieldException nsfe) {
            LOG.debug("Parent class {} doesn't contain '{}' field, trying with base!", (Object)superCl.getName(), (Object)TOMCAT_RESOURCE_ENTRIES_FIELD, (Object)nsfe);
            try {
                this.clearMap(cl, loader, TOMCAT_RESOURCE_ENTRIES_FIELD);
                LOG.debug("Cleared tomcat cache via classloader's class.");
            }
            catch (Exception e) {
                LOG.warn("Couldn't clear tomcat cache using {}", (Object)cl.getName(), (Object)e);
            }
        }
        catch (Exception e) {
            LOG.warn("Couldn't clear tomcat cache using {}", (Object)(superCl != null ? superCl.getName() : null), (Object)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void clearMap(Class cl, Object obj, String name) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Object cache;
        Field field = cl.getDeclaredField(name);
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        Object object = cache = field.get(obj);
        synchronized (object) {
            Class<?> ccl = cache.getClass();
            Method clearMethod = ccl.getMethod("clear", new Class[0]);
            clearMethod.invoke(cache, new Object[0]);
        }
    }

    protected MessageFormat buildMessageFormat(String pattern, Locale locale) {
        MessageFormatKey key = new MessageFormatKey(pattern, locale);
        MessageFormat format = (MessageFormat)this.messageFormats.get(key);
        if (format == null) {
            format = new MessageFormat(pattern);
            format.setLocale(locale);
            format.applyPattern(pattern);
            this.messageFormats.put(key, format);
        }
        return format;
    }

    protected String formatWithNullDetection(MessageFormat mf, Object[] args) {
        String message = mf.format(args);
        if ("null".equals(message)) {
            return null;
        }
        return message;
    }

    @Inject(value="struts.i18n.reload", required=false)
    public void setReloadBundles(String reloadBundles) {
        this.reloadBundles = Boolean.parseBoolean(reloadBundles);
    }

    @Inject(value="struts.devMode", required=false)
    public void setDevMode(String devMode) {
        this.devMode = Boolean.parseBoolean(devMode);
    }

    @Inject(value="struts.i18n.search.defaultbundles.first", required=false)
    public void setSearchDefaultBundlesFirst(String searchDefaultBundlesFirst) {
        this.searchDefaultBundlesFirst = Boolean.parseBoolean(searchDefaultBundlesFirst);
    }

    @Override
    public ResourceBundle findResourceBundle(String aBundleName, Locale locale) {
        ClassLoader classLoader = this.getCurrentThreadContextClassLoader();
        String key = this.createMissesKey(String.valueOf(classLoader.hashCode()), aBundleName, locale);
        if (this.missingBundles.contains(key)) {
            return null;
        }
        ResourceBundle bundle = null;
        try {
            if (this.bundlesMap.containsKey(key)) {
                bundle = (ResourceBundle)this.bundlesMap.get(key);
            } else {
                bundle = ResourceBundle.getBundle(aBundleName, locale, classLoader);
                this.bundlesMap.putIfAbsent(key, bundle);
            }
        }
        catch (MissingResourceException ex) {
            if (this.delegatedClassLoaderMap.containsKey(classLoader.hashCode())) {
                try {
                    if (this.bundlesMap.containsKey(key)) {
                        bundle = (ResourceBundle)this.bundlesMap.get(key);
                    }
                    bundle = ResourceBundle.getBundle(aBundleName, locale, (ClassLoader)this.delegatedClassLoaderMap.get(classLoader.hashCode()));
                    this.bundlesMap.putIfAbsent(key, bundle);
                }
                catch (MissingResourceException e) {
                    LOG.debug("Missing resource bundle [{}]!", (Object)aBundleName, (Object)e);
                    this.missingBundles.add(key);
                }
            }
            LOG.debug("Missing resource bundle [{}]!", (Object)aBundleName);
            this.missingBundles.add(key);
        }
        return bundle;
    }

    @Deprecated
    public void reset() {
    }

    protected boolean unableToFindTextForKey(GetDefaultMessageReturnArg result) {
        if (result == null || result.message == null) {
            return true;
        }
        return !result.foundInBundle;
    }

    private String createMissesKey(String prefix, String aBundleName, Locale locale) {
        return prefix + aBundleName + "_" + locale.toString();
    }

    protected GetDefaultMessageReturnArg getDefaultMessage(String key, Locale locale, ValueStack valueStack, Object[] args, String defaultMessage) {
        GetDefaultMessageReturnArg result = null;
        boolean found = true;
        if (key != null) {
            String message = this.findDefaultText(key, locale);
            if (message == null) {
                message = defaultMessage;
                found = false;
            }
            if (message != null) {
                MessageFormat mf = this.buildMessageFormat(TextParseUtil.translateVariables(message, valueStack), locale);
                String msg = this.formatWithNullDetection(mf, args);
                result = new GetDefaultMessageReturnArg(msg, found);
            }
        }
        return result;
    }

    protected GetDefaultMessageReturnArg getDefaultMessageWithAlternateKey(String key, String alternateKey, Locale locale, ValueStack valueStack, Object[] args, String defaultMessage) {
        GetDefaultMessageReturnArg result;
        if (alternateKey == null || alternateKey.isEmpty()) {
            result = this.getDefaultMessage(key, locale, valueStack, args, defaultMessage);
        } else {
            result = this.getDefaultMessage(key, locale, valueStack, args, null);
            if (result == null || result.message == null) {
                result = this.getDefaultMessage(alternateKey, locale, valueStack, args, defaultMessage);
            }
        }
        return result;
    }

    protected String getMessage(String bundleName, Locale locale, String key, ValueStack valueStack, Object[] args) {
        ResourceBundle bundle = this.findResourceBundle(bundleName, locale);
        if (bundle == null) {
            return null;
        }
        if (valueStack != null) {
            this.reloadBundles(valueStack.getContext());
        }
        try {
            String message = bundle.getString(key);
            if (valueStack != null) {
                message = TextParseUtil.translateVariables(bundle.getString(key), valueStack);
            }
            MessageFormat mf = this.buildMessageFormat(message, locale);
            return this.formatWithNullDetection(mf, args);
        }
        catch (MissingResourceException e) {
            LOG.debug("Missing key [{}] in bundle [{}]!", (Object)key, (Object)bundleName);
            return null;
        }
    }

    protected String findMessage(Class clazz, String key, String indexedKey, Locale locale, Object[] args, Set<String> checked, ValueStack valueStack) {
        Class<?>[] interfaces;
        if (checked == null) {
            checked = new TreeSet<String>();
        } else if (checked.contains(clazz.getName())) {
            return null;
        }
        String msg = this.getMessage(clazz.getName(), locale, key, valueStack, args);
        if (msg != null) {
            return msg;
        }
        if (indexedKey != null && (msg = this.getMessage(clazz.getName(), locale, indexedKey, valueStack, args)) != null) {
            return msg;
        }
        for (Class<?> anInterface : interfaces = clazz.getInterfaces()) {
            msg = this.getMessage(anInterface.getName(), locale, key, valueStack, args);
            if (msg != null) {
                return msg;
            }
            if (indexedKey == null || (msg = this.getMessage(anInterface.getName(), locale, indexedKey, valueStack, args)) == null) continue;
            return msg;
        }
        if (clazz.isInterface()) {
            for (Class<?> anInterface : interfaces = clazz.getInterfaces()) {
                msg = this.findMessage(anInterface, key, indexedKey, locale, args, checked, valueStack);
                if (msg == null) continue;
                return msg;
            }
        } else if (!clazz.equals(Object.class) && !clazz.isPrimitive()) {
            return this.findMessage(clazz.getSuperclass(), key, indexedKey, locale, args, checked, valueStack);
        }
        return null;
    }

    static class GetDefaultMessageReturnArg {
        String message;
        boolean foundInBundle;

        public GetDefaultMessageReturnArg(String message, boolean foundInBundle) {
            this.message = message;
            this.foundInBundle = foundInBundle;
        }
    }

    static class MessageFormatKey {
        String pattern;
        Locale locale;

        MessageFormatKey(String pattern, Locale locale) {
            this.pattern = pattern;
            this.locale = locale;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            MessageFormatKey that = (MessageFormatKey)o;
            if (this.pattern != null ? !this.pattern.equals(that.pattern) : that.pattern != null) {
                return false;
            }
            return this.locale != null ? this.locale.equals(that.locale) : that.locale == null;
        }

        public int hashCode() {
            int result = this.pattern != null ? this.pattern.hashCode() : 0;
            result = 31 * result + (this.locale != null ? this.locale.hashCode() : 0);
            return result;
        }
    }
}

