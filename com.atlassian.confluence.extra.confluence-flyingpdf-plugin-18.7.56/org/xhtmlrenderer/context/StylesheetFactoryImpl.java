/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.context;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import org.xhtmlrenderer.css.extend.StylesheetFactory;
import org.xhtmlrenderer.css.parser.CSSErrorHandler;
import org.xhtmlrenderer.css.parser.CSSParser;
import org.xhtmlrenderer.css.sheet.Ruleset;
import org.xhtmlrenderer.css.sheet.Stylesheet;
import org.xhtmlrenderer.css.sheet.StylesheetInfo;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.resource.CSSResource;
import org.xhtmlrenderer.util.Configuration;
import org.xhtmlrenderer.util.XRLog;
import org.xml.sax.InputSource;

public class StylesheetFactoryImpl
implements StylesheetFactory {
    private UserAgentCallback _userAgentCallback;
    private int _cacheCapacity = 16;
    private LinkedHashMap _cache = new LinkedHashMap(this._cacheCapacity, 0.75f, true){
        private static final long serialVersionUID = 1L;

        protected boolean removeEldestEntry(Map.Entry eldest) {
            return this.size() > StylesheetFactoryImpl.this._cacheCapacity;
        }
    };
    private CSSParser _cssParser;

    public StylesheetFactoryImpl(UserAgentCallback userAgentCallback) {
        this._userAgentCallback = userAgentCallback;
        this._cssParser = new CSSParser(new CSSErrorHandler(){

            @Override
            public void error(String uri, String message) {
                XRLog.cssParse(Level.WARNING, "(" + uri + ") " + message);
            }
        });
    }

    @Override
    public synchronized Stylesheet parse(Reader reader, StylesheetInfo info) {
        try {
            return this._cssParser.parseStylesheet(info.getUri(), info.getOrigin(), reader);
        }
        catch (IOException e) {
            XRLog.cssParse(Level.WARNING, "Couldn't parse stylesheet at URI " + info.getUri() + ": " + e.getMessage(), e);
            return new Stylesheet(info.getUri(), info.getOrigin());
        }
    }

    private Stylesheet parse(StylesheetInfo info) {
        CSSResource cr = this._userAgentCallback.getCSSResource(info.getUri());
        if (cr == null) {
            return null;
        }
        InputSource inputSource = cr.getResourceInputSource();
        if (inputSource == null) {
            return null;
        }
        InputStream is = inputSource.getByteStream();
        if (is == null) {
            return null;
        }
        try {
            Stylesheet stylesheet = this.parse(new InputStreamReader(is, Configuration.valueFor("xr.stylesheets.charset-name", "UTF-8")), info);
            return stylesheet;
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    @Override
    public synchronized Ruleset parseStyleDeclaration(int origin, String styleDeclaration) {
        return this._cssParser.parseDeclaration(origin, styleDeclaration);
    }

    public synchronized void putStylesheet(Object key, Stylesheet sheet) {
        this._cache.put(key, sheet);
    }

    public synchronized boolean containsStylesheet(Object key) {
        return this._cache.containsKey(key);
    }

    public synchronized Stylesheet getCachedStylesheet(Object key) {
        return (Stylesheet)this._cache.get(key);
    }

    public synchronized Object removeCachedStylesheet(Object key) {
        return this._cache.remove(key);
    }

    public synchronized void flushCachedStylesheets() {
        this._cache.clear();
    }

    @Override
    public Stylesheet getStylesheet(StylesheetInfo info) {
        XRLog.load("Requesting stylesheet: " + info.getUri());
        Stylesheet s = this.getCachedStylesheet(info.getUri());
        if (s == null && !this.containsStylesheet(info.getUri())) {
            s = this.parse(info);
            this.putStylesheet(info.getUri(), s);
        }
        return s;
    }

    public void setUserAgentCallback(UserAgentCallback userAgent) {
        this._userAgentCallback = userAgent;
    }

    public void setSupportCMYKColors(boolean b) {
        this._cssParser.setSupportCMYKColors(b);
    }
}

