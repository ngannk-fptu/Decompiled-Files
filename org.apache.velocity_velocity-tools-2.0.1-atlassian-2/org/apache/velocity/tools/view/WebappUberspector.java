/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 *  org.apache.velocity.runtime.log.Log
 *  org.apache.velocity.runtime.parser.node.AbstractExecutor
 *  org.apache.velocity.runtime.parser.node.SetExecutor
 *  org.apache.velocity.util.introspection.AbstractChainableUberspector
 *  org.apache.velocity.util.introspection.Info
 *  org.apache.velocity.util.introspection.Introspector
 *  org.apache.velocity.util.introspection.UberspectImpl$VelGetterImpl
 *  org.apache.velocity.util.introspection.UberspectImpl$VelSetterImpl
 *  org.apache.velocity.util.introspection.VelPropertyGet
 *  org.apache.velocity.util.introspection.VelPropertySet
 */
package org.apache.velocity.tools.view;

import java.lang.reflect.InvocationTargetException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.runtime.parser.node.AbstractExecutor;
import org.apache.velocity.runtime.parser.node.SetExecutor;
import org.apache.velocity.util.introspection.AbstractChainableUberspector;
import org.apache.velocity.util.introspection.Info;
import org.apache.velocity.util.introspection.Introspector;
import org.apache.velocity.util.introspection.UberspectImpl;
import org.apache.velocity.util.introspection.VelPropertyGet;
import org.apache.velocity.util.introspection.VelPropertySet;

public class WebappUberspector
extends AbstractChainableUberspector {
    public VelPropertyGet getPropertyGet(Object obj, String identifier, Info i) throws Exception {
        VelPropertyGet ret = super.getPropertyGet(obj, identifier, i);
        if (ret == null) {
            Class<?> claz = obj.getClass();
            if (obj instanceof HttpServletRequest || obj instanceof HttpSession || obj instanceof ServletContext) {
                GetAttributeExecutor executor = new GetAttributeExecutor(this.log, this.introspector, claz, identifier);
                ret = executor.isAlive() ? new UberspectImpl.VelGetterImpl((AbstractExecutor)executor) : null;
            }
        }
        return ret;
    }

    public void init() {
        try {
            super.init();
        }
        catch (RuntimeException re) {
            throw re;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.introspector = new Introspector(this.log);
    }

    public VelPropertySet getPropertySet(Object obj, String identifier, Object arg, Info i) throws Exception {
        VelPropertySet ret = super.getPropertySet(obj, identifier, arg, i);
        if (ret == null) {
            Class<?> claz = obj.getClass();
            if (obj instanceof HttpServletRequest || obj instanceof HttpSession || obj instanceof ServletContext) {
                SetAttributeExecutor executor = new SetAttributeExecutor(this.log, this.introspector, claz, arg, identifier);
                ret = executor.isAlive() ? new UberspectImpl.VelSetterImpl((SetExecutor)executor) : null;
            }
        }
        return ret;
    }

    public class SetAttributeExecutor
    extends SetExecutor {
        private final Introspector introspector;
        private final String property;

        public SetAttributeExecutor(Log log, Introspector introspector, Class clazz, Object arg, String property) {
            this.log = log;
            this.introspector = introspector;
            this.property = property;
            this.discover(clazz, arg);
        }

        protected void discover(Class clazz, Object arg) {
            Object[] params = new Object[]{this.property, arg};
            try {
                this.setMethod(this.introspector.getMethod(clazz, "setAttribute", params));
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (Exception e) {
                this.log.error((Object)("While looking for put('" + params[0] + "') method:"), (Throwable)e);
            }
        }

        public Object execute(Object o, Object value) throws IllegalAccessException, InvocationTargetException {
            if (this.isAlive()) {
                Object[] params = new Object[]{this.property, value};
                return this.getMethod().invoke(o, params);
            }
            return null;
        }
    }

    public class GetAttributeExecutor
    extends AbstractExecutor {
        private final Introspector introspector;
        private Object[] params;

        public GetAttributeExecutor(Log log, Introspector introspector, Class clazz, String property) {
            this.log = log;
            this.introspector = introspector;
            this.params = new Object[]{property};
            this.discover(clazz);
        }

        protected void discover(Class clazz) {
            try {
                this.setMethod(this.introspector.getMethod(clazz, "getAttribute", this.params));
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (Exception e) {
                this.log.error((Object)("While looking for getAttribute('" + this.params[0] + "') method:"), (Throwable)e);
            }
        }

        public Object execute(Object o) throws IllegalAccessException, InvocationTargetException {
            return this.isAlive() ? this.getMethod().invoke(o, this.params) : null;
        }
    }
}

