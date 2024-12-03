/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.servlet.http.HttpSession
 */
package org.springframework.web.context.request;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.AbstractRequestAttributes;
import org.springframework.web.context.request.DestructionCallbackBindingListener;
import org.springframework.web.util.WebUtils;

public class ServletRequestAttributes
extends AbstractRequestAttributes {
    public static final String DESTRUCTION_CALLBACK_NAME_PREFIX = ServletRequestAttributes.class.getName() + ".DESTRUCTION_CALLBACK.";
    protected static final Set<Class<?>> immutableValueTypes = new HashSet(16);
    private final HttpServletRequest request;
    @Nullable
    private HttpServletResponse response;
    @Nullable
    private volatile HttpSession session;
    private final Map<String, Object> sessionAttributesToUpdate = new ConcurrentHashMap<String, Object>(1);

    public ServletRequestAttributes(HttpServletRequest request) {
        Assert.notNull((Object)request, "Request must not be null");
        this.request = request;
    }

    public ServletRequestAttributes(HttpServletRequest request, @Nullable HttpServletResponse response) {
        this(request);
        this.response = response;
    }

    public final HttpServletRequest getRequest() {
        return this.request;
    }

    @Nullable
    public final HttpServletResponse getResponse() {
        return this.response;
    }

    @Nullable
    protected final HttpSession getSession(boolean allowCreate) {
        if (this.isRequestActive()) {
            HttpSession session;
            this.session = session = this.request.getSession(allowCreate);
            return session;
        }
        HttpSession session = this.session;
        if (session == null) {
            if (allowCreate) {
                throw new IllegalStateException("No session found and request already completed - cannot create new session!");
            }
            this.session = session = this.request.getSession(false);
        }
        return session;
    }

    private HttpSession obtainSession() {
        HttpSession session = this.getSession(true);
        Assert.state(session != null, "No HttpSession");
        return session;
    }

    @Override
    public Object getAttribute(String name, int scope) {
        if (scope == 0) {
            if (!this.isRequestActive()) {
                throw new IllegalStateException("Cannot ask for request attribute - request is not active anymore!");
            }
            return this.request.getAttribute(name);
        }
        HttpSession session = this.getSession(false);
        if (session != null) {
            try {
                Object value = session.getAttribute(name);
                if (value != null) {
                    this.sessionAttributesToUpdate.put(name, value);
                }
                return value;
            }
            catch (IllegalStateException illegalStateException) {
                // empty catch block
            }
        }
        return null;
    }

    @Override
    public void setAttribute(String name, Object value, int scope) {
        if (scope == 0) {
            if (!this.isRequestActive()) {
                throw new IllegalStateException("Cannot set request attribute - request is not active anymore!");
            }
            this.request.setAttribute(name, value);
        } else {
            HttpSession session = this.obtainSession();
            this.sessionAttributesToUpdate.remove(name);
            session.setAttribute(name, value);
        }
    }

    @Override
    public void removeAttribute(String name, int scope) {
        if (scope == 0) {
            if (this.isRequestActive()) {
                this.removeRequestDestructionCallback(name);
                this.request.removeAttribute(name);
            }
        } else {
            HttpSession session = this.getSession(false);
            if (session != null) {
                this.sessionAttributesToUpdate.remove(name);
                try {
                    session.removeAttribute(DESTRUCTION_CALLBACK_NAME_PREFIX + name);
                    session.removeAttribute(name);
                }
                catch (IllegalStateException illegalStateException) {
                    // empty catch block
                }
            }
        }
    }

    @Override
    public String[] getAttributeNames(int scope) {
        if (scope == 0) {
            if (!this.isRequestActive()) {
                throw new IllegalStateException("Cannot ask for request attributes - request is not active anymore!");
            }
            return StringUtils.toStringArray(this.request.getAttributeNames());
        }
        HttpSession session = this.getSession(false);
        if (session != null) {
            try {
                return StringUtils.toStringArray(session.getAttributeNames());
            }
            catch (IllegalStateException illegalStateException) {
                // empty catch block
            }
        }
        return new String[0];
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback, int scope) {
        if (scope == 0) {
            this.registerRequestDestructionCallback(name, callback);
        } else {
            this.registerSessionDestructionCallback(name, callback);
        }
    }

    @Override
    public Object resolveReference(String key) {
        if ("request".equals(key)) {
            return this.request;
        }
        if ("session".equals(key)) {
            return this.getSession(true);
        }
        return null;
    }

    @Override
    public String getSessionId() {
        return this.obtainSession().getId();
    }

    @Override
    public Object getSessionMutex() {
        return WebUtils.getSessionMutex(this.obtainSession());
    }

    @Override
    protected void updateAccessedSessionAttributes() {
        if (!this.sessionAttributesToUpdate.isEmpty()) {
            HttpSession session = this.getSession(false);
            if (session != null) {
                try {
                    for (Map.Entry<String, Object> entry : this.sessionAttributesToUpdate.entrySet()) {
                        String name = entry.getKey();
                        Object newValue = entry.getValue();
                        Object oldValue = session.getAttribute(name);
                        if (oldValue != newValue || this.isImmutableSessionAttribute(name, newValue)) continue;
                        session.setAttribute(name, newValue);
                    }
                }
                catch (IllegalStateException illegalStateException) {
                    // empty catch block
                }
            }
            this.sessionAttributesToUpdate.clear();
        }
    }

    protected boolean isImmutableSessionAttribute(String name, @Nullable Object value) {
        return value == null || immutableValueTypes.contains(value.getClass());
    }

    protected void registerSessionDestructionCallback(String name, Runnable callback) {
        HttpSession session = this.obtainSession();
        session.setAttribute(DESTRUCTION_CALLBACK_NAME_PREFIX + name, (Object)new DestructionCallbackBindingListener(callback));
    }

    public String toString() {
        return this.request.toString();
    }

    static {
        immutableValueTypes.addAll(NumberUtils.STANDARD_NUMBER_TYPES);
        immutableValueTypes.add(Boolean.class);
        immutableValueTypes.add(Character.class);
        immutableValueTypes.add(String.class);
    }
}

