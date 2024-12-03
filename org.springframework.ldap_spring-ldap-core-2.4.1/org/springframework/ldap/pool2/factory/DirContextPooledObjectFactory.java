/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.pool2.BaseKeyedPooledObjectFactory
 *  org.apache.commons.pool2.PooledObject
 *  org.apache.commons.pool2.impl.DefaultPooledObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.Assert
 */
package org.springframework.ldap.pool2.factory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.naming.CommunicationException;
import javax.naming.directory.DirContext;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextProxy;
import org.springframework.ldap.pool2.DirContextType;
import org.springframework.ldap.pool2.FailureAwareContext;
import org.springframework.ldap.pool2.validation.DirContextValidator;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.util.Assert;

class DirContextPooledObjectFactory
extends BaseKeyedPooledObjectFactory<Object, Object> {
    protected final Logger logger = LoggerFactory.getLogger(((Object)((Object)this)).getClass());
    private static final Set<Class<? extends Throwable>> DEFAULT_NONTRANSIENT_EXCEPTIONS = new HashSet<Class<? extends Throwable>>();
    private ContextSource contextSource;
    private DirContextValidator dirContextValidator;
    private Set<Class<? extends Throwable>> nonTransientExceptions = DEFAULT_NONTRANSIENT_EXCEPTIONS;

    DirContextPooledObjectFactory() {
    }

    void setNonTransientExceptions(Collection<Class<? extends Throwable>> nonTransientExceptions) {
        this.nonTransientExceptions = new HashSet<Class<? extends Throwable>>(nonTransientExceptions);
    }

    public ContextSource getContextSource() {
        return this.contextSource;
    }

    public void setContextSource(ContextSource contextSource) {
        if (contextSource == null) {
            throw new IllegalArgumentException("contextSource may not be null");
        }
        this.contextSource = contextSource;
    }

    public DirContextValidator getDirContextValidator() {
        return this.dirContextValidator;
    }

    public void setDirContextValidator(DirContextValidator dirContextValidator) {
        if (dirContextValidator == null) {
            throw new IllegalArgumentException("dirContextValidator may not be null");
        }
        this.dirContextValidator = dirContextValidator;
    }

    private Object makeFailureAwareProxy(DirContext readOnlyContext) {
        return Proxy.newProxyInstance(DirContextProxy.class.getClassLoader(), new Class[]{LdapUtils.getActualTargetClass(readOnlyContext), DirContextProxy.class, FailureAwareContext.class}, (InvocationHandler)new FailureAwareContextProxy(readOnlyContext));
    }

    public boolean validateObject(Object key, PooledObject<Object> pooledObject) {
        Assert.notNull((Object)this.dirContextValidator, (String)"DirContextValidator may not be null");
        Assert.isTrue((boolean)(key instanceof DirContextType), (String)"key must be a DirContextType");
        Assert.notNull(pooledObject, (String)"The Object to validate must not be null");
        Assert.isTrue((boolean)(pooledObject.getObject() instanceof DirContext), (String)("The Object to validate must be of type '" + DirContext.class + "'"));
        try {
            DirContextType contextType = (DirContextType)key;
            DirContext dirContext = (DirContext)pooledObject.getObject();
            return this.dirContextValidator.validateDirContext(contextType, dirContext);
        }
        catch (Exception e) {
            this.logger.warn("Failed to validate '" + pooledObject.getObject() + "' due to an unexpected exception.", (Throwable)e);
            return false;
        }
    }

    public void destroyObject(Object key, PooledObject<Object> pooledObject) throws Exception {
        Assert.notNull(pooledObject, (String)"The Object to destroy must not be null");
        Assert.isTrue((boolean)(pooledObject.getObject() instanceof DirContext), (String)("The Object to destroy must be of type '" + DirContext.class + "'"));
        try {
            DirContext dirContext = (DirContext)pooledObject.getObject();
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Closing " + key + " DirContext='" + dirContext + "'");
            }
            dirContext.close();
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Closed " + key + " DirContext='" + dirContext + "'");
            }
        }
        catch (Exception e) {
            this.logger.warn("An exception occured while closing '" + pooledObject.getObject() + "'", (Throwable)e);
        }
    }

    public Object create(Object key) throws Exception {
        Assert.notNull((Object)this.contextSource, (String)"ContextSource may not be null");
        Assert.isTrue((boolean)(key instanceof DirContextType), (String)"key must be a DirContextType");
        DirContextType contextType = (DirContextType)key;
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Creating a new " + contextType + " DirContext");
        }
        if (contextType == DirContextType.READ_WRITE) {
            DirContext readWriteContext = this.contextSource.getReadWriteContext();
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Created new " + DirContextType.READ_WRITE + " DirContext='" + readWriteContext + "'");
            }
            return this.makeFailureAwareProxy(readWriteContext);
        }
        if (contextType == DirContextType.READ_ONLY) {
            DirContext readOnlyContext = this.contextSource.getReadOnlyContext();
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Created new " + DirContextType.READ_ONLY + " DirContext='" + readOnlyContext + "'");
            }
            return this.makeFailureAwareProxy(readOnlyContext);
        }
        throw new IllegalArgumentException("Unrecognized ContextType: " + contextType);
    }

    public PooledObject<Object> wrap(Object value) {
        return new DefaultPooledObject(value);
    }

    static {
        DEFAULT_NONTRANSIENT_EXCEPTIONS.add(CommunicationException.class);
    }

    private class FailureAwareContextProxy
    implements InvocationHandler {
        private DirContext target;
        private boolean hasFailed = false;

        public FailureAwareContextProxy(DirContext target) {
            Assert.notNull((Object)target, (String)"Target must not be null");
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            if (methodName.equals("getTargetContext")) {
                return this.target;
            }
            if (methodName.equals("hasFailed")) {
                return this.hasFailed;
            }
            try {
                return method.invoke((Object)this.target, args);
            }
            catch (InvocationTargetException e) {
                Throwable targetException = e.getTargetException();
                Class<?> targetExceptionClass = targetException.getClass();
                boolean nonTransientEncountered = false;
                for (Class clazz : DirContextPooledObjectFactory.this.nonTransientExceptions) {
                    if (!clazz.isAssignableFrom(targetExceptionClass)) continue;
                    if (DirContextPooledObjectFactory.this.logger.isDebugEnabled()) {
                        DirContextPooledObjectFactory.this.logger.debug(String.format("A %s - explicitly configured to be a non-transient exception - encountered; eagerly invalidating the target context.", targetExceptionClass));
                    }
                    nonTransientEncountered = true;
                    break;
                }
                if (nonTransientEncountered) {
                    this.hasFailed = true;
                } else if (DirContextPooledObjectFactory.this.logger.isDebugEnabled()) {
                    DirContextPooledObjectFactory.this.logger.debug(String.format("A %s - not explicitly configured to be a non-transient exception - encountered; ignoring.", targetExceptionClass));
                }
                throw targetException;
            }
        }
    }
}

