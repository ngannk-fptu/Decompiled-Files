/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.io.IOException;
import java.net.ContentHandler;
import java.net.ContentHandlerFactory;
import java.net.URLConnection;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.URLHandlers;
import org.apache.felix.framework.util.SecureAction;
import org.apache.felix.framework.util.Util;

class URLHandlersContentHandlerProxy
extends ContentHandler {
    private static final Class[] STRING_TYPES = new Class[]{String.class};
    private static final String CONTENT_HANDLER_PACKAGE_PROP = "java.content.handler.pkgs";
    private static final String DEFAULT_CONTENT_HANDLER_PACKAGE = "sun.net.www.content|sun.awt.www.content|com.ibm.oti.net.www.content|gnu.java.net.content|org.apache.harmony.luni.internal.net.www.content|COM.newmonics.www.content";
    private static final ConcurrentHashMap<String, ContentHandler> m_builtIn = new ConcurrentHashMap();
    private static final String m_pkgs;
    private final ContentHandlerFactory m_factory;
    private final String m_mimeType;
    private final SecureAction m_action;

    public URLHandlersContentHandlerProxy(String mimeType, SecureAction action, ContentHandlerFactory factory) {
        this.m_mimeType = mimeType;
        this.m_action = action;
        this.m_factory = factory;
    }

    @Override
    public Object getContent(URLConnection urlc) throws IOException {
        ContentHandler svc = this.getContentHandlerService();
        if (svc == null) {
            return urlc.getInputStream();
        }
        return svc.getContent(urlc);
    }

    private ContentHandler getContentHandlerService() {
        Object framework = URLHandlers.getFrameworkFromContext();
        if (framework == null) {
            return this.getBuiltIn();
        }
        try {
            ContentHandler service = framework instanceof Felix ? (ContentHandler)((Felix)framework).getContentHandlerService(this.m_mimeType) : (ContentHandler)this.m_action.invoke(this.m_action.getDeclaredMethod(framework.getClass(), "getContentHandlerService", STRING_TYPES), framework, new Object[]{this.m_mimeType});
            return service == null ? this.getBuiltIn() : service;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private ContentHandler getBuiltIn() {
        ContentHandler result = m_builtIn.get(this.m_mimeType);
        if (result != null) {
            return result;
        }
        if (this.m_factory != null && (result = this.m_factory.createContentHandler(this.m_mimeType)) != null) {
            return Util.putIfAbsentAndReturn(m_builtIn, this.m_mimeType, result);
        }
        String fixedType = this.m_mimeType.replace('.', '_').replace('/', '.').replace('-', '_');
        StringTokenizer pkgTok = new StringTokenizer(m_pkgs, "| ");
        while (pkgTok.hasMoreTokens()) {
            String pkg = pkgTok.nextToken().trim();
            String className = pkg + "." + fixedType;
            try {
                Class handler = this.m_action.forName(className, null);
                if (handler == null) continue;
                return Util.putIfAbsentAndReturn(m_builtIn, this.m_mimeType, (ContentHandler)handler.newInstance());
            }
            catch (Exception exception) {
            }
        }
        return null;
    }

    static {
        String pkgs = new SecureAction().getSystemProperty(CONTENT_HANDLER_PACKAGE_PROP, "");
        m_pkgs = pkgs.equals("") ? DEFAULT_CONTENT_HANDLER_PACKAGE : pkgs + "|" + DEFAULT_CONTENT_HANDLER_PACKAGE;
    }
}

