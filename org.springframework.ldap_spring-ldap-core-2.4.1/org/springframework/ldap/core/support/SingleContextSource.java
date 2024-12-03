/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 */
package org.springframework.ldap.core.support;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextProxy;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapOperationsCallback;
import org.springframework.ldap.support.LdapUtils;

public class SingleContextSource
implements ContextSource,
DisposableBean {
    private static final Logger LOG = LoggerFactory.getLogger(SingleContextSource.class);
    private static final boolean DONT_USE_READ_ONLY = false;
    private static final boolean DONT_IGNORE_PARTIAL_RESULT = false;
    private static final boolean DONT_IGNORE_NAME_NOT_FOUND = false;
    private final DirContext ctx;

    public SingleContextSource(DirContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public DirContext getReadOnlyContext() {
        return this.getNonClosingDirContextProxy(this.ctx);
    }

    @Override
    public DirContext getReadWriteContext() {
        return this.getNonClosingDirContextProxy(this.ctx);
    }

    private DirContext getNonClosingDirContextProxy(DirContext context) {
        return (DirContext)Proxy.newProxyInstance(DirContextProxy.class.getClassLoader(), new Class[]{LdapUtils.getActualTargetClass(context), DirContextProxy.class}, (InvocationHandler)new NonClosingDirContextInvocationHandler(context));
    }

    @Override
    public DirContext getContext(String principal, String credentials) {
        throw new UnsupportedOperationException("Not a valid operation for this type of ContextSource");
    }

    public void destroy() {
        try {
            this.ctx.close();
        }
        catch (NamingException e) {
            LOG.warn("Error when closing", (Throwable)e);
        }
    }

    public static <T> T doWithSingleContext(ContextSource contextSource, LdapOperationsCallback<T> callback) {
        return SingleContextSource.doWithSingleContext(contextSource, callback, false, false, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> T doWithSingleContext(ContextSource contextSource, LdapOperationsCallback<T> callback, boolean useReadOnly, boolean ignorePartialResultException, boolean ignoreNameNotFoundException) {
        SingleContextSource singleContextSource = useReadOnly ? new SingleContextSource(contextSource.getReadOnlyContext()) : new SingleContextSource(contextSource.getReadWriteContext());
        LdapTemplate ldapTemplate = new LdapTemplate(singleContextSource);
        ldapTemplate.setIgnorePartialResultException(ignorePartialResultException);
        ldapTemplate.setIgnoreNameNotFoundException(ignoreNameNotFoundException);
        try {
            T t = callback.doWithLdapOperations(ldapTemplate);
            return t;
        }
        finally {
            singleContextSource.destroy();
        }
    }

    public static class NonClosingDirContextInvocationHandler
    implements InvocationHandler {
        private DirContext target;

        public NonClosingDirContextInvocationHandler(DirContext target) {
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            if (methodName.equals("getTargetContext")) {
                return this.target;
            }
            if (methodName.equals("equals")) {
                return proxy == args[0] ? Boolean.TRUE : Boolean.FALSE;
            }
            if (methodName.equals("hashCode")) {
                return proxy.hashCode();
            }
            if (methodName.equals("close")) {
                return null;
            }
            try {
                return method.invoke((Object)this.target, args);
            }
            catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
    }
}

