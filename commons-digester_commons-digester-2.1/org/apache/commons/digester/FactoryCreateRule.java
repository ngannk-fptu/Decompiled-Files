/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import java.util.Stack;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ObjectCreationFactory;
import org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FactoryCreateRule
extends Rule {
    private boolean ignoreCreateExceptions;
    private Stack<Boolean> exceptionIgnoredStack;
    protected String attributeName = null;
    protected String className = null;
    protected ObjectCreationFactory creationFactory = null;

    @Deprecated
    public FactoryCreateRule(Digester digester, String className) {
        this(className);
    }

    @Deprecated
    public FactoryCreateRule(Digester digester, Class<?> clazz) {
        this(clazz);
    }

    @Deprecated
    public FactoryCreateRule(Digester digester, String className, String attributeName) {
        this(className, attributeName);
    }

    @Deprecated
    public FactoryCreateRule(Digester digester, Class<?> clazz, String attributeName) {
        this(clazz, attributeName);
    }

    @Deprecated
    public FactoryCreateRule(Digester digester, ObjectCreationFactory creationFactory) {
        this(creationFactory);
    }

    public FactoryCreateRule(String className) {
        this(className, false);
    }

    public FactoryCreateRule(Class<?> clazz) {
        this(clazz, false);
    }

    public FactoryCreateRule(String className, String attributeName) {
        this(className, attributeName, false);
    }

    public FactoryCreateRule(Class<?> clazz, String attributeName) {
        this(clazz, attributeName, false);
    }

    public FactoryCreateRule(ObjectCreationFactory creationFactory) {
        this(creationFactory, false);
    }

    public FactoryCreateRule(String className, boolean ignoreCreateExceptions) {
        this(className, null, ignoreCreateExceptions);
    }

    public FactoryCreateRule(Class<?> clazz, boolean ignoreCreateExceptions) {
        this(clazz, null, ignoreCreateExceptions);
    }

    public FactoryCreateRule(String className, String attributeName, boolean ignoreCreateExceptions) {
        this.className = className;
        this.attributeName = attributeName;
        this.ignoreCreateExceptions = ignoreCreateExceptions;
    }

    public FactoryCreateRule(Class<?> clazz, String attributeName, boolean ignoreCreateExceptions) {
        this(clazz.getName(), attributeName, ignoreCreateExceptions);
    }

    public FactoryCreateRule(ObjectCreationFactory creationFactory, boolean ignoreCreateExceptions) {
        this.creationFactory = creationFactory;
        this.ignoreCreateExceptions = ignoreCreateExceptions;
    }

    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        if (this.ignoreCreateExceptions) {
            if (this.exceptionIgnoredStack == null) {
                this.exceptionIgnoredStack = new Stack();
            }
            try {
                Object instance = this.getFactory(attributes).createObject(attributes);
                if (this.digester.log.isDebugEnabled()) {
                    this.digester.log.debug((Object)("[FactoryCreateRule]{" + this.digester.match + "} New " + (instance == null ? "null object" : instance.getClass().getName())));
                }
                this.digester.push(instance);
                this.exceptionIgnoredStack.push(Boolean.FALSE);
            }
            catch (Exception e) {
                if (this.digester.log.isInfoEnabled()) {
                    this.digester.log.info((Object)("[FactoryCreateRule] Create exception ignored: " + (e.getMessage() == null ? e.getClass().getName() : e.getMessage())));
                    if (this.digester.log.isDebugEnabled()) {
                        this.digester.log.debug((Object)"[FactoryCreateRule] Ignored exception:", (Throwable)e);
                    }
                }
                this.exceptionIgnoredStack.push(Boolean.TRUE);
            }
        } else {
            Object instance = this.getFactory(attributes).createObject(attributes);
            if (this.digester.log.isDebugEnabled()) {
                this.digester.log.debug((Object)("[FactoryCreateRule]{" + this.digester.match + "} New " + (instance == null ? "null object" : instance.getClass().getName())));
            }
            this.digester.push(instance);
        }
    }

    @Override
    public void end(String namespace, String name) throws Exception {
        if (this.ignoreCreateExceptions && this.exceptionIgnoredStack != null && !this.exceptionIgnoredStack.empty() && this.exceptionIgnoredStack.pop().booleanValue()) {
            if (this.digester.log.isTraceEnabled()) {
                this.digester.log.trace((Object)"[FactoryCreateRule] No creation so no push so no pop");
            }
            return;
        }
        Object top = this.digester.pop();
        if (this.digester.log.isDebugEnabled()) {
            this.digester.log.debug((Object)("[FactoryCreateRule]{" + this.digester.match + "} Pop " + top.getClass().getName()));
        }
    }

    @Override
    public void finish() throws Exception {
        if (this.attributeName != null) {
            this.creationFactory = null;
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("FactoryCreateRule[");
        sb.append("className=");
        sb.append(this.className);
        sb.append(", attributeName=");
        sb.append(this.attributeName);
        if (this.creationFactory != null) {
            sb.append(", creationFactory=");
            sb.append(this.creationFactory);
        }
        sb.append("]");
        return sb.toString();
    }

    protected ObjectCreationFactory getFactory(Attributes attributes) throws Exception {
        if (this.creationFactory == null) {
            String value;
            String realClassName = this.className;
            if (this.attributeName != null && (value = attributes.getValue(this.attributeName)) != null) {
                realClassName = value;
            }
            if (this.digester.log.isDebugEnabled()) {
                this.digester.log.debug((Object)("[FactoryCreateRule]{" + this.digester.match + "} New factory " + realClassName));
            }
            Class<?> clazz = this.digester.getClassLoader().loadClass(realClassName);
            this.creationFactory = (ObjectCreationFactory)clazz.newInstance();
            this.creationFactory.setDigester(this.digester);
        }
        return this.creationFactory;
    }
}

