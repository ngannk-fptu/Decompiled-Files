/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ELContext
 *  javax.el.ELResolver
 *  javax.el.PropertyNotWritableException
 *  javax.servlet.http.Cookie
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 */
package javax.servlet.jsp.el;

import java.beans.FeatureDescriptor;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.PageContext;

public class ImplicitObjectELResolver
extends ELResolver {
    public Object getValue(ELContext context, Object base, Object property) {
        Scope scope;
        Objects.requireNonNull(context);
        if (base == null && property != null && (scope = (Scope)((Object)Scope.lookupMap.get(property.toString()))) != null) {
            return scope.getScopeValue(context, base, property);
        }
        return null;
    }

    public Class getType(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base == null && property != null && Scope.lookupMap.containsKey(property.toString())) {
            context.setPropertyResolved(base, property);
        }
        return null;
    }

    public void setValue(ELContext context, Object base, Object property, Object value) {
        Objects.requireNonNull(context);
        if (base == null && property != null && Scope.lookupMap.containsKey(property.toString())) {
            context.setPropertyResolved(base, property);
            throw new PropertyNotWritableException();
        }
    }

    public boolean isReadOnly(ELContext context, Object base, Object property) {
        Objects.requireNonNull(context);
        if (base == null && property != null && Scope.lookupMap.containsKey(property.toString())) {
            context.setPropertyResolved(base, property);
            return true;
        }
        return false;
    }

    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        ArrayList<FeatureDescriptor> feats = new ArrayList<FeatureDescriptor>(Scope.values().length);
        for (Scope scope : Scope.values()) {
            FeatureDescriptor feat = new FeatureDescriptor();
            feat.setDisplayName(scope.implicitName);
            feat.setExpert(false);
            feat.setHidden(false);
            feat.setName(scope.implicitName);
            feat.setPreferred(true);
            feat.setValue("resolvableAtDesignTime", Boolean.TRUE);
            feat.setValue("type", String.class);
            feats.add(feat);
        }
        return feats.iterator();
    }

    public Class<String> getCommonPropertyType(ELContext context, Object base) {
        if (base == null) {
            return String.class;
        }
        return null;
    }

    private static enum Scope {
        APPLICATION_SCOPE("applicationScope"){

            @Override
            Object getScopeValue(ELContext context, Object base, Object property) {
                return Scope.getScopeManager(context, base, property).getApplicationScope();
            }
        }
        ,
        COOKIE("cookie"){

            @Override
            Object getScopeValue(ELContext context, Object base, Object property) {
                return Scope.getScopeManager(context, base, property).getCookie();
            }
        }
        ,
        HEADER("header"){

            @Override
            Object getScopeValue(ELContext context, Object base, Object property) {
                return Scope.getScopeManager(context, base, property).getHeader();
            }
        }
        ,
        HEADER_VALUES("headerValues"){

            @Override
            Object getScopeValue(ELContext context, Object base, Object property) {
                return Scope.getScopeManager(context, base, property).getHeaderValues();
            }
        }
        ,
        INIT_PARAM("initParam"){

            @Override
            Object getScopeValue(ELContext context, Object base, Object property) {
                return Scope.getScopeManager(context, base, property).getInitParam();
            }
        }
        ,
        PAGE_CONTEXT("pageContext"){

            @Override
            Object getScopeValue(ELContext context, Object base, Object property) {
                return Scope.getScopeManager(context, base, property).getPageContext();
            }
        }
        ,
        PAGE_SCOPE("pageScope"){

            @Override
            Object getScopeValue(ELContext context, Object base, Object property) {
                return Scope.getScopeManager(context, base, property).getPageScope();
            }
        }
        ,
        PARAM("param"){

            @Override
            Object getScopeValue(ELContext context, Object base, Object property) {
                return Scope.getScopeManager(context, base, property).getParam();
            }
        }
        ,
        PARAM_VALUES("paramValues"){

            @Override
            Object getScopeValue(ELContext context, Object base, Object property) {
                return Scope.getScopeManager(context, base, property).getParamValues();
            }
        }
        ,
        REQUEST_SCOPE("requestScope"){

            @Override
            Object getScopeValue(ELContext context, Object base, Object property) {
                return Scope.getScopeManager(context, base, property).getRequestScope();
            }
        }
        ,
        SESSION_SCOPE("sessionScope"){

            @Override
            Object getScopeValue(ELContext context, Object base, Object property) {
                return Scope.getScopeManager(context, base, property).getSessionScope();
            }
        };

        private static final Map<String, Scope> lookupMap;
        private final String implicitName;

        private static ScopeManager getScopeManager(ELContext context, Object base, Object property) {
            PageContext page = (PageContext)context.getContext(JspContext.class);
            context.setPropertyResolved(base, property);
            return ScopeManager.get(page);
        }

        private Scope(String implicitName) {
            this.implicitName = implicitName;
        }

        abstract Object getScopeValue(ELContext var1, Object var2, Object var3);

        static {
            lookupMap = new HashMap<String, Scope>();
            for (Scope scope : Scope.values()) {
                lookupMap.put(scope.implicitName, scope);
            }
        }
    }

    private static abstract class ScopeMap<V>
    extends AbstractMap<String, V> {
        private ScopeMap() {
        }

        protected abstract Enumeration<String> getAttributeNames();

        protected abstract V getAttribute(String var1);

        protected void removeAttribute(String name) {
            throw new UnsupportedOperationException();
        }

        protected void setAttribute(String name, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final Set<Map.Entry<String, V>> entrySet() {
            Enumeration<String> e = this.getAttributeNames();
            HashSet<Map.Entry<String, V>> set = new HashSet<Map.Entry<String, V>>();
            if (e != null) {
                while (e.hasMoreElements()) {
                    set.add(new ScopeEntry(e.nextElement()));
                }
            }
            return set;
        }

        @Override
        public final int size() {
            int size = 0;
            Enumeration<String> e = this.getAttributeNames();
            if (e != null) {
                while (e.hasMoreElements()) {
                    e.nextElement();
                    ++size;
                }
            }
            return size;
        }

        @Override
        public final boolean containsKey(Object key) {
            if (key == null) {
                return false;
            }
            Enumeration<String> e = this.getAttributeNames();
            if (e != null) {
                while (e.hasMoreElements()) {
                    if (!key.equals(e.nextElement())) continue;
                    return true;
                }
            }
            return false;
        }

        @Override
        public final V get(Object key) {
            if (key != null) {
                return this.getAttribute((String)key);
            }
            return null;
        }

        @Override
        public final V put(String key, V value) {
            Objects.requireNonNull(key);
            if (value == null) {
                this.removeAttribute(key);
            } else {
                this.setAttribute(key, value);
            }
            return null;
        }

        @Override
        public final V remove(Object key) {
            Objects.requireNonNull(key);
            this.removeAttribute((String)key);
            return null;
        }

        private class ScopeEntry
        implements Map.Entry<String, V> {
            private final String key;

            ScopeEntry(String key) {
                this.key = key;
            }

            @Override
            public String getKey() {
                return this.key;
            }

            @Override
            public V getValue() {
                return ScopeMap.this.getAttribute(this.key);
            }

            @Override
            public V setValue(Object value) {
                if (value == null) {
                    ScopeMap.this.removeAttribute(this.key);
                } else {
                    ScopeMap.this.setAttribute(this.key, value);
                }
                return null;
            }

            @Override
            public boolean equals(Object obj) {
                return obj != null && this.hashCode() == obj.hashCode();
            }

            @Override
            public int hashCode() {
                return this.key.hashCode();
            }
        }
    }

    private static class ScopeManager {
        private static final String MNGR_KEY = ScopeManager.class.getName();
        private final PageContext page;
        private Map<String, Object> applicationScope;
        private Map<String, Cookie> cookie;
        private Map<String, String> header;
        private Map<String, String[]> headerValues;
        private Map<String, String> initParam;
        private Map<String, Object> pageScope;
        private Map<String, String> param;
        private Map<String, String[]> paramValues;
        private Map<String, Object> requestScope;
        private Map<String, Object> sessionScope;

        ScopeManager(PageContext page) {
            this.page = page;
        }

        public static ScopeManager get(PageContext page) {
            ScopeManager mngr = (ScopeManager)page.getAttribute(MNGR_KEY);
            if (mngr == null) {
                mngr = new ScopeManager(page);
                page.setAttribute(MNGR_KEY, mngr);
            }
            return mngr;
        }

        public Map<String, Object> getApplicationScope() {
            if (this.applicationScope == null) {
                this.applicationScope = new ScopeMap<Object>(){

                    @Override
                    protected void setAttribute(String name, Object value) {
                        page.getServletContext().setAttribute(name, value);
                    }

                    @Override
                    protected void removeAttribute(String name) {
                        page.getServletContext().removeAttribute(name);
                    }

                    @Override
                    protected Enumeration<String> getAttributeNames() {
                        return page.getServletContext().getAttributeNames();
                    }

                    @Override
                    protected Object getAttribute(String name) {
                        return page.getServletContext().getAttribute(name);
                    }
                };
            }
            return this.applicationScope;
        }

        public Map<String, Cookie> getCookie() {
            if (this.cookie == null) {
                this.cookie = new ScopeMap<Cookie>(){

                    @Override
                    protected Enumeration<String> getAttributeNames() {
                        Cookie[] cookies = ((HttpServletRequest)page.getRequest()).getCookies();
                        if (cookies != null) {
                            ArrayList<String> list = new ArrayList<String>(cookies.length);
                            for (Cookie cookie : cookies) {
                                list.add(cookie.getName());
                            }
                            return Collections.enumeration(list);
                        }
                        return null;
                    }

                    @Override
                    protected Cookie getAttribute(String name) {
                        Cookie[] cookies = ((HttpServletRequest)page.getRequest()).getCookies();
                        if (cookies != null) {
                            for (Cookie cookie : cookies) {
                                if (!name.equals(cookie.getName())) continue;
                                return cookie;
                            }
                        }
                        return null;
                    }
                };
            }
            return this.cookie;
        }

        public Map<String, String> getHeader() {
            if (this.header == null) {
                this.header = new ScopeMap<String>(){

                    @Override
                    protected Enumeration<String> getAttributeNames() {
                        return ((HttpServletRequest)page.getRequest()).getHeaderNames();
                    }

                    @Override
                    protected String getAttribute(String name) {
                        return ((HttpServletRequest)page.getRequest()).getHeader(name);
                    }
                };
            }
            return this.header;
        }

        public Map<String, String[]> getHeaderValues() {
            if (this.headerValues == null) {
                this.headerValues = new ScopeMap<String[]>(){

                    @Override
                    protected Enumeration<String> getAttributeNames() {
                        return ((HttpServletRequest)page.getRequest()).getHeaderNames();
                    }

                    @Override
                    protected String[] getAttribute(String name) {
                        Enumeration e = ((HttpServletRequest)page.getRequest()).getHeaders(name);
                        if (e != null) {
                            ArrayList<String> list = new ArrayList<String>();
                            while (e.hasMoreElements()) {
                                list.add((String)e.nextElement());
                            }
                            return list.toArray(new String[0]);
                        }
                        return null;
                    }
                };
            }
            return this.headerValues;
        }

        public Map<String, String> getInitParam() {
            if (this.initParam == null) {
                this.initParam = new ScopeMap<String>(){

                    @Override
                    protected Enumeration<String> getAttributeNames() {
                        return page.getServletContext().getInitParameterNames();
                    }

                    @Override
                    protected String getAttribute(String name) {
                        return page.getServletContext().getInitParameter(name);
                    }
                };
            }
            return this.initParam;
        }

        public PageContext getPageContext() {
            return this.page;
        }

        public Map<String, Object> getPageScope() {
            if (this.pageScope == null) {
                this.pageScope = new ScopeMap<Object>(){

                    @Override
                    protected void setAttribute(String name, Object value) {
                        page.setAttribute(name, value);
                    }

                    @Override
                    protected void removeAttribute(String name) {
                        page.removeAttribute(name);
                    }

                    @Override
                    protected Enumeration<String> getAttributeNames() {
                        return page.getAttributeNamesInScope(1);
                    }

                    @Override
                    protected Object getAttribute(String name) {
                        return page.getAttribute(name);
                    }
                };
            }
            return this.pageScope;
        }

        public Map<String, String> getParam() {
            if (this.param == null) {
                this.param = new ScopeMap<String>(){

                    @Override
                    protected Enumeration<String> getAttributeNames() {
                        return page.getRequest().getParameterNames();
                    }

                    @Override
                    protected String getAttribute(String name) {
                        return page.getRequest().getParameter(name);
                    }
                };
            }
            return this.param;
        }

        public Map<String, String[]> getParamValues() {
            if (this.paramValues == null) {
                this.paramValues = new ScopeMap<String[]>(){

                    @Override
                    protected String[] getAttribute(String name) {
                        return page.getRequest().getParameterValues(name);
                    }

                    @Override
                    protected Enumeration<String> getAttributeNames() {
                        return page.getRequest().getParameterNames();
                    }
                };
            }
            return this.paramValues;
        }

        public Map<String, Object> getRequestScope() {
            if (this.requestScope == null) {
                this.requestScope = new ScopeMap<Object>(){

                    @Override
                    protected void setAttribute(String name, Object value) {
                        page.getRequest().setAttribute(name, value);
                    }

                    @Override
                    protected void removeAttribute(String name) {
                        page.getRequest().removeAttribute(name);
                    }

                    @Override
                    protected Enumeration<String> getAttributeNames() {
                        return page.getRequest().getAttributeNames();
                    }

                    @Override
                    protected Object getAttribute(String name) {
                        return page.getRequest().getAttribute(name);
                    }
                };
            }
            return this.requestScope;
        }

        public Map<String, Object> getSessionScope() {
            if (this.sessionScope == null) {
                this.sessionScope = new ScopeMap<Object>(){

                    @Override
                    protected void setAttribute(String name, Object value) {
                        ((HttpServletRequest)page.getRequest()).getSession().setAttribute(name, value);
                    }

                    @Override
                    protected void removeAttribute(String name) {
                        HttpSession session = page.getSession();
                        if (session != null) {
                            session.removeAttribute(name);
                        }
                    }

                    @Override
                    protected Enumeration<String> getAttributeNames() {
                        HttpSession session = page.getSession();
                        if (session != null) {
                            return session.getAttributeNames();
                        }
                        return null;
                    }

                    @Override
                    protected Object getAttribute(String name) {
                        HttpSession session = page.getSession();
                        if (session != null) {
                            return session.getAttribute(name);
                        }
                        return null;
                    }
                };
            }
            return this.sessionScope;
        }
    }
}

