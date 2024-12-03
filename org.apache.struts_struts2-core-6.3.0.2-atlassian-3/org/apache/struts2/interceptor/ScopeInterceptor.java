/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;
import java.io.Serializable;
import java.util.IdentityHashMap;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsException;
import org.apache.struts2.dispatcher.SessionMap;

public class ScopeInterceptor
extends AbstractInterceptor
implements PreResultListener {
    private static final long serialVersionUID = 9120762699600054395L;
    private static final Logger LOG = LogManager.getLogger(ScopeInterceptor.class);
    private String[] application = null;
    private String[] session = null;
    private String key;
    private String type = null;
    private boolean autoCreateSession = true;
    private String sessionReset = "session.reset";
    private boolean reset = false;
    private static final Object NULL = new NULLClass();
    private static Map<Object, Object> locks = new IdentityHashMap<Object, Object>();

    public void setApplication(String s) {
        if (s != null) {
            this.application = s.split(" *, *");
        }
    }

    public void setSession(String s) {
        if (s != null) {
            this.session = s.split(" *, *");
        }
    }

    public void setAutoCreateSession(String value) {
        if (StringUtils.isNotBlank((CharSequence)value)) {
            this.autoCreateSession = BooleanUtils.toBoolean((String)value);
        }
    }

    private String getKey(ActionInvocation invocation) {
        ActionProxy proxy = invocation.getProxy();
        if (this.key == null || "CLASS".equals(this.key)) {
            return "struts.ScopeInterceptor:" + proxy.getAction().getClass();
        }
        if ("ACTION".equals(this.key)) {
            return "struts.ScopeInterceptor:" + proxy.getNamespace() + ":" + proxy.getActionName();
        }
        return this.key;
    }

    private static Object nullConvert(Object o) {
        if (o == null) {
            return NULL;
        }
        if (o == NULL || NULL.equals(o)) {
            return null;
        }
        return o;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void lock(Object o, ActionInvocation invocation) throws Exception {
        Object object = o;
        synchronized (object) {
            Object previous;
            int count = 3;
            while ((previous = locks.get(o)) != null) {
                if (previous == invocation) {
                    return;
                }
                if (count-- <= 0) {
                    locks.remove(o);
                    o.notify();
                    throw new StrutsException("Deadlock in session lock");
                }
                o.wait(10000L);
            }
            locks.put(o, invocation);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void unlock(Object o) {
        Object object = o;
        synchronized (object) {
            locks.remove(o);
            o.notify();
        }
    }

    protected void after(ActionInvocation invocation, String result) throws Exception {
        Map<String, Object> session = ActionContext.getContext().getSession();
        if (session != null) {
            ScopeInterceptor.unlock(session);
        }
    }

    protected void before(ActionInvocation invocation) throws Exception {
        Object attribute;
        invocation.addPreResultListener(this);
        SessionMap session = ActionContext.getContext().getSession();
        if (session == null && this.autoCreateSession) {
            session = new SessionMap(ServletActionContext.getRequest());
            ActionContext.getContext().withSession(session);
        }
        if (session != null) {
            ScopeInterceptor.lock(session, invocation);
        }
        String key = this.getKey(invocation);
        Map<String, Object> application = ActionContext.getContext().getApplication();
        ValueStack stack = ActionContext.getContext().getValueStack();
        LOG.debug("scope interceptor before");
        if (this.application != null) {
            for (String string : this.application) {
                attribute = application.get(key + string);
                if (attribute == null) continue;
                LOG.debug("Application scoped variable set {} = {}", (Object)string, (Object)String.valueOf(attribute));
                stack.setValue(string, ScopeInterceptor.nullConvert(attribute));
            }
        }
        if (ActionContext.getContext().getParameters().get(this.sessionReset).isDefined()) {
            return;
        }
        if (this.reset) {
            return;
        }
        if (session == null) {
            LOG.debug("No HttpSession created... Cannot set session scoped variables");
            return;
        }
        if (this.session != null && !"start".equals(this.type)) {
            for (String string : this.session) {
                attribute = session.get(key + string);
                if (attribute == null) continue;
                LOG.debug("Session scoped variable set {} = {}", (Object)string, (Object)String.valueOf(attribute));
                stack.setValue(string, ScopeInterceptor.nullConvert(attribute));
            }
        }
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public void beforeResult(ActionInvocation invocation, String resultCode) {
        String key = this.getKey(invocation);
        Map<String, Object> application = ActionContext.getContext().getApplication();
        ValueStack stack = ActionContext.getContext().getValueStack();
        if (this.application != null) {
            for (String string : this.application) {
                Object value = stack.findValue(string);
                LOG.debug("Application scoped variable saved {} = {}", (Object)string, (Object)String.valueOf(value));
                application.put(key + string, ScopeInterceptor.nullConvert(value));
            }
        }
        boolean ends = "end".equals(this.type);
        Map<String, Object> session = ActionContext.getContext().getSession();
        if (session != null) {
            if (this.session != null) {
                for (String string : this.session) {
                    if (ends) {
                        session.remove(key + string);
                        continue;
                    }
                    Object value = stack.findValue(string);
                    LOG.debug("Session scoped variable saved {} = {}", (Object)string, (Object)String.valueOf(value));
                    session.put(key + string, ScopeInterceptor.nullConvert(value));
                }
            }
            ScopeInterceptor.unlock(session);
        } else {
            LOG.debug("No HttpSession created... Cannot save session scoped variables.");
        }
        LOG.debug("scope interceptor after (before result)");
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        if (!"start".equals(type = type.toLowerCase()) && !"end".equals(type)) {
            throw new IllegalArgumentException("Only start or end are allowed arguments for type");
        }
        this.type = type;
    }

    public String getSessionReset() {
        return this.sessionReset;
    }

    public void setSessionReset(String sessionReset) {
        this.sessionReset = sessionReset;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        String result;
        Map<String, Object> session = ActionContext.getContext().getSession();
        this.before(invocation);
        try {
            result = invocation.invoke();
            this.after(invocation, result);
        }
        finally {
            if (session != null) {
                ScopeInterceptor.unlock(session);
            }
        }
        return result;
    }

    public boolean isReset() {
        return this.reset;
    }

    public void setReset(boolean reset) {
        this.reset = reset;
    }

    private static class NULLClass
    implements Serializable {
        private NULLClass() {
        }

        public String toString() {
            return "NULL";
        }

        public int hashCode() {
            return 1;
        }

        public boolean equals(Object obj) {
            return obj == null || obj instanceof NULLClass;
        }
    }
}

