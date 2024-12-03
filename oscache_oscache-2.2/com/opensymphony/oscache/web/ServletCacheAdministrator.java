/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.oscache.web;

import com.opensymphony.oscache.base.AbstractCacheAdministrator;
import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.EntryRefreshPolicy;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.base.events.CacheEventListener;
import com.opensymphony.oscache.base.events.ScopeEvent;
import com.opensymphony.oscache.base.events.ScopeEventListener;
import com.opensymphony.oscache.base.events.ScopeEventType;
import com.opensymphony.oscache.web.ServletCache;
import java.io.Serializable;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ServletCacheAdministrator
extends AbstractCacheAdministrator
implements Serializable {
    private static final transient Log log = LogFactory.getLog((Class)(class$com$opensymphony$oscache$web$ServletCacheAdministrator == null ? (class$com$opensymphony$oscache$web$ServletCacheAdministrator = ServletCacheAdministrator.class$("com.opensymphony.oscache.web.ServletCacheAdministrator")) : class$com$opensymphony$oscache$web$ServletCacheAdministrator));
    private static final String CACHE_USE_HOST_DOMAIN_KEY = "cache.use.host.domain.in.key";
    private static final String CACHE_KEY_KEY = "cache.key";
    private static final String DEFAULT_CACHE_KEY = "__oscache_cache";
    public static final String SESSION_SCOPE_NAME = "session";
    public static final String APPLICATION_SCOPE_NAME = "application";
    private static final String CACHE_ADMINISTRATOR_KEY = "__oscache_admin";
    public static final String HASH_KEY_SCOPE = "scope";
    public static final String HASH_KEY_SESSION_ID = "sessionId";
    public static final String HASH_KEY_CONTEXT_TMPDIR = "context.tempdir";
    private static final String FILE_SEPARATOR = "/";
    private static final char FILE_SEPARATOR_CHAR = "/".charAt(0);
    private static final short AVERAGE_KEY_LENGTH = 30;
    private static final String m_strBase64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private Map flushTimes;
    private transient ServletContext context;
    private String cacheKey;
    private boolean useHostDomainInKey = false;
    static /* synthetic */ Class class$com$opensymphony$oscache$web$ServletCacheAdministrator;
    static /* synthetic */ Class class$com$opensymphony$oscache$base$events$ScopeEventListener;

    private ServletCacheAdministrator(ServletContext context, Properties p) {
        super(p);
        this.config.set(HASH_KEY_CONTEXT_TMPDIR, context.getAttribute("javax.servlet.context.tempdir"));
        this.flushTimes = new HashMap();
        this.initHostDomainInKey();
        this.context = context;
    }

    public static ServletCacheAdministrator getInstance(ServletContext context) {
        return ServletCacheAdministrator.getInstance(context, null);
    }

    public static synchronized ServletCacheAdministrator getInstance(ServletContext context, Properties p) {
        ServletCacheAdministrator admin = (ServletCacheAdministrator)context.getAttribute(CACHE_ADMINISTRATOR_KEY);
        if (admin == null) {
            admin = new ServletCacheAdministrator(context, p);
            context.setAttribute(CACHE_ADMINISTRATOR_KEY, (Object)admin);
            if (log.isInfoEnabled()) {
                log.info((Object)"Created new instance of ServletCacheAdministrator");
            }
            admin.getAppScopeCache(context);
        }
        if (admin.context == null) {
            admin.context = context;
        }
        return admin;
    }

    public static void destroyInstance(ServletContext context) {
        ServletCacheAdministrator admin = (ServletCacheAdministrator)context.getAttribute(CACHE_ADMINISTRATOR_KEY);
        if (admin != null) {
            Cache cache = (Cache)context.getAttribute(admin.getCacheKey());
            if (cache != null) {
                admin.finalizeListeners(cache);
                context.removeAttribute(admin.getCacheKey());
                context.removeAttribute(CACHE_ADMINISTRATOR_KEY);
                cache = null;
                if (log.isInfoEnabled()) {
                    log.info((Object)"Shut down the ServletCacheAdministrator");
                }
            }
            admin = null;
        }
    }

    public Cache getCache(HttpServletRequest request, int scope) {
        if (scope == 4) {
            return this.getAppScopeCache(this.context);
        }
        if (scope == 3) {
            return this.getSessionScopeCache(request.getSession(true));
        }
        throw new RuntimeException("The supplied scope value of " + scope + " is invalid. Acceptable values are PageContext.APPLICATION_SCOPE and PageContext.SESSION_SCOPE");
    }

    public Cache getAppScopeCache(ServletContext context) {
        Cache cache;
        Object obj = context.getAttribute(this.getCacheKey());
        if (obj == null || !(obj instanceof Cache)) {
            if (log.isInfoEnabled()) {
                log.info((Object)("Created new application-scoped cache at key: " + this.getCacheKey()));
            }
            cache = this.createCache(4, null);
            context.setAttribute(this.getCacheKey(), (Object)cache);
        } else {
            cache = (Cache)obj;
        }
        return cache;
    }

    public Cache getSessionScopeCache(HttpSession session) {
        Cache cache;
        Object obj = session.getAttribute(this.getCacheKey());
        if (obj == null || !(obj instanceof Cache)) {
            if (log.isInfoEnabled()) {
                log.info((Object)("Created new session-scoped cache in session " + session.getId() + " at key: " + this.getCacheKey()));
            }
            cache = this.createCache(3, session.getId());
            session.setAttribute(this.getCacheKey(), (Object)cache);
        } else {
            cache = (Cache)obj;
        }
        return cache;
    }

    public String getCacheKey() {
        if (this.cacheKey == null) {
            this.cacheKey = this.getProperty(CACHE_KEY_KEY);
            if (this.cacheKey == null) {
                this.cacheKey = DEFAULT_CACHE_KEY;
            }
        }
        return this.cacheKey;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setFlushTime(Date date, int scope) {
        if (log.isInfoEnabled()) {
            log.info((Object)("Flushing scope " + scope + " at " + date));
        }
        Map map = this.flushTimes;
        synchronized (map) {
            if (date == null) {
                this.logError("setFlushTime called with a null date.");
                throw new IllegalArgumentException("setFlushTime called with a null date.");
            }
            this.dispatchScopeEvent(ScopeEventType.SCOPE_FLUSHED, scope, date, null);
            this.flushTimes.put(new Integer(scope), date);
        }
    }

    public void setFlushTime(int scope) {
        this.setFlushTime(new Date(), scope);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Date getFlushTime(int scope) {
        Map map = this.flushTimes;
        synchronized (map) {
            return (Date)this.flushTimes.get(new Integer(scope));
        }
    }

    public Object getFromCache(int scope, HttpServletRequest request, String key, int refreshPeriod) throws NeedsRefreshException {
        Cache cache = this.getCache(request, scope);
        key = this.generateEntryKey(key, request, scope);
        return cache.getFromCache(key, refreshPeriod);
    }

    public boolean isScopeFlushed(CacheEntry cacheEntry, int scope) {
        Date flushDateTime = this.getFlushTime(scope);
        if (flushDateTime != null) {
            long lastUpdate = cacheEntry.getLastUpdate();
            return flushDateTime.getTime() >= lastUpdate;
        }
        return false;
    }

    public void addScopeEventListener(ScopeEventListener listener) {
        this.listenerList.add(class$com$opensymphony$oscache$base$events$ScopeEventListener == null ? (class$com$opensymphony$oscache$base$events$ScopeEventListener = ServletCacheAdministrator.class$("com.opensymphony.oscache.base.events.ScopeEventListener")) : class$com$opensymphony$oscache$base$events$ScopeEventListener, listener);
    }

    public void cancelUpdate(int scope, HttpServletRequest request, String key) {
        Cache cache = this.getCache(request, scope);
        key = this.generateEntryKey(key, request, scope);
        cache.cancelUpdate(key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void flushAll(Date date) {
        Map map = this.flushTimes;
        synchronized (map) {
            this.setFlushTime(date, 4);
            this.setFlushTime(date, 3);
            this.setFlushTime(date, 2);
            this.setFlushTime(date, 1);
        }
        this.dispatchScopeEvent(ScopeEventType.ALL_SCOPES_FLUSHED, -1, date, null);
    }

    public void flushAll() {
        this.flushAll(new Date());
    }

    public String generateEntryKey(String key, HttpServletRequest request, int scope) {
        return this.generateEntryKey(key, request, scope, null, null);
    }

    public String generateEntryKey(String key, HttpServletRequest request, int scope, String language) {
        return this.generateEntryKey(key, request, scope, language, null);
    }

    public String generateEntryKey(String key, HttpServletRequest request, int scope, String language, String suffix) {
        StringBuffer cBuffer = new StringBuffer(30);
        if (language != null) {
            cBuffer.append(FILE_SEPARATOR).append(language);
        }
        if (this.useHostDomainInKey) {
            cBuffer.append(FILE_SEPARATOR).append(request.getServerName());
        }
        if (key != null) {
            cBuffer.append(FILE_SEPARATOR).append(key);
        } else {
            String generatedKey = request.getRequestURI();
            if (generatedKey.charAt(0) != FILE_SEPARATOR_CHAR) {
                cBuffer.append(FILE_SEPARATOR_CHAR);
            }
            cBuffer.append(generatedKey);
            cBuffer.append("_").append(request.getMethod()).append("_");
            generatedKey = this.getSortedQueryString(request);
            if (generatedKey != null) {
                try {
                    MessageDigest digest = MessageDigest.getInstance("MD5");
                    byte[] b = digest.digest(generatedKey.getBytes());
                    cBuffer.append("_");
                    cBuffer.append(ServletCacheAdministrator.toBase64(b).replace('/', '_'));
                }
                catch (Exception e) {
                    // empty catch block
                }
            }
        }
        if (suffix != null && suffix.length() > 0) {
            cBuffer.append(suffix);
        }
        return cBuffer.toString();
    }

    protected String getSortedQueryString(HttpServletRequest request) {
        Map paramMap = request.getParameterMap();
        if (paramMap.isEmpty()) {
            return null;
        }
        Set paramSet = new TreeMap(paramMap).entrySet();
        StringBuffer buf = new StringBuffer();
        boolean first = true;
        Iterator it = paramSet.iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            String[] values = (String[])entry.getValue();
            for (int i = 0; i < values.length; ++i) {
                String key = (String)entry.getKey();
                if (key.length() == 10 && "jsessionid".equals(key)) continue;
                if (first) {
                    first = false;
                } else {
                    buf.append('&');
                }
                buf.append(key).append('=').append(values[i]);
            }
        }
        if (buf.length() == 0) {
            return null;
        }
        return buf.toString();
    }

    public void logError(String message) {
        log.error((Object)("[oscache]: " + message));
    }

    public void putInCache(int scope, HttpServletRequest request, String key, Object content) {
        this.putInCache(scope, request, key, content, null);
    }

    public void putInCache(int scope, HttpServletRequest request, String key, Object content, EntryRefreshPolicy policy) {
        Cache cache = this.getCache(request, scope);
        key = this.generateEntryKey(key, request, scope);
        cache.putInCache(key, content, policy);
    }

    public void setCacheCapacity(int scope, HttpServletRequest request, int capacity) {
        this.setCacheCapacity(capacity);
        this.getCache(request, scope).setCapacity(capacity);
    }

    public void removeScopeEventListener(ScopeEventListener listener) {
        this.listenerList.remove(class$com$opensymphony$oscache$base$events$ScopeEventListener == null ? (class$com$opensymphony$oscache$base$events$ScopeEventListener = ServletCacheAdministrator.class$("com.opensymphony.oscache.base.events.ScopeEventListener")) : class$com$opensymphony$oscache$base$events$ScopeEventListener, listener);
    }

    protected void finalizeListeners(Cache cache) {
        super.finalizeListeners(cache);
    }

    private static String toBase64(byte[] aValue) {
        int iByteLen = aValue.length;
        StringBuffer tt = new StringBuffer();
        for (int i = 0; i < iByteLen; i += 3) {
            boolean bByte2 = i + 1 < iByteLen;
            boolean bByte3 = i + 2 < iByteLen;
            int byte1 = aValue[i] & 0xFF;
            int byte2 = bByte2 ? aValue[i + 1] & 0xFF : 0;
            int byte3 = bByte3 ? aValue[i + 2] & 0xFF : 0;
            tt.append(m_strBase64Chars.charAt(byte1 / 4));
            tt.append(m_strBase64Chars.charAt(byte2 / 16 + (byte1 & 3) * 16));
            tt.append(bByte2 ? m_strBase64Chars.charAt(byte3 / 64 + (byte2 & 0xF) * 4) : (char)'=');
            tt.append(bByte3 ? m_strBase64Chars.charAt(byte3 & 0x3F) : (char)'=');
        }
        return tt.toString();
    }

    private ServletCache createCache(int scope, String sessionId) {
        ServletCache newCache = new ServletCache(this, this.algorithmClass, this.cacheCapacity, scope);
        this.config.set(HASH_KEY_SCOPE, "" + scope);
        this.config.set(HASH_KEY_SESSION_ID, sessionId);
        newCache = (ServletCache)this.configureStandardListeners(newCache);
        if (this.config.getProperty("cache.event.listeners") != null) {
            CacheEventListener[] listeners = this.getCacheEventListeners();
            for (int i = 0; i < listeners.length; ++i) {
                if (!(listeners[i] instanceof ScopeEventListener)) continue;
                newCache.addCacheEventListener(listeners[i], class$com$opensymphony$oscache$base$events$ScopeEventListener == null ? ServletCacheAdministrator.class$("com.opensymphony.oscache.base.events.ScopeEventListener") : class$com$opensymphony$oscache$base$events$ScopeEventListener);
            }
        }
        return newCache;
    }

    private void dispatchScopeEvent(ScopeEventType eventType, int scope, Date date, String origin) {
        ScopeEvent event = new ScopeEvent(eventType, scope, date, origin);
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] != (class$com$opensymphony$oscache$base$events$ScopeEventListener == null ? ServletCacheAdministrator.class$("com.opensymphony.oscache.base.events.ScopeEventListener") : class$com$opensymphony$oscache$base$events$ScopeEventListener)) continue;
            ((ScopeEventListener)listeners[i + 1]).scopeFlushed(event);
        }
    }

    private void initHostDomainInKey() {
        String propStr = this.getProperty(CACHE_USE_HOST_DOMAIN_KEY);
        this.useHostDomainInKey = "true".equalsIgnoreCase(propStr);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

