/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.digester;

import org.apache.tomcat.util.digester.ArrayStack;
import org.apache.tomcat.util.digester.ObjectCreationFactory;
import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

public class FactoryCreateRule
extends Rule {
    private boolean ignoreCreateExceptions;
    private ArrayStack<Boolean> exceptionIgnoredStack;
    protected ObjectCreationFactory creationFactory = null;

    public FactoryCreateRule(ObjectCreationFactory creationFactory, boolean ignoreCreateExceptions) {
        this.creationFactory = creationFactory;
        this.ignoreCreateExceptions = ignoreCreateExceptions;
    }

    @Override
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        if (this.ignoreCreateExceptions) {
            if (this.exceptionIgnoredStack == null) {
                this.exceptionIgnoredStack = new ArrayStack();
            }
            try {
                Object instance = this.creationFactory.createObject(attributes);
                if (this.digester.log.isDebugEnabled()) {
                    this.digester.log.debug((Object)("[FactoryCreateRule]{" + this.digester.match + "} New " + instance.getClass().getName()));
                }
                this.digester.push(instance);
                this.exceptionIgnoredStack.push(Boolean.FALSE);
            }
            catch (Exception e) {
                if (this.digester.log.isInfoEnabled()) {
                    this.digester.log.info((Object)sm.getString("rule.createError", new Object[]{e.getMessage() == null ? e.getClass().getName() : e.getMessage()}));
                    if (this.digester.log.isDebugEnabled()) {
                        this.digester.log.debug((Object)"[FactoryCreateRule] Ignored exception:", (Throwable)e);
                    }
                }
                this.exceptionIgnoredStack.push(Boolean.TRUE);
            }
        } else {
            Object instance = this.creationFactory.createObject(attributes);
            if (this.digester.log.isDebugEnabled()) {
                this.digester.log.debug((Object)("[FactoryCreateRule]{" + this.digester.match + "} New " + instance.getClass().getName()));
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
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("FactoryCreateRule[");
        if (this.creationFactory != null) {
            sb.append("creationFactory=");
            sb.append(this.creationFactory);
        }
        sb.append(']');
        return sb.toString();
    }
}

