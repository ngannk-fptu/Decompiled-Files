/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.callsite.MetaClassSite;
import org.codehaus.groovy.runtime.metaclass.MissingMethodExecutionFailed;

public class PogoMetaClassSite
extends MetaClassSite {
    public PogoMetaClassSite(CallSite site, MetaClass metaClass) {
        super(site, metaClass);
    }

    @Override
    public final Object call(Object receiver, Object[] args) throws Throwable {
        if (this.checkCall(receiver)) {
            try {
                try {
                    return this.metaClass.invokeMethod(receiver, this.name, args);
                }
                catch (MissingMethodException e) {
                    if (e instanceof MissingMethodExecutionFailed) {
                        throw (MissingMethodException)e.getCause();
                    }
                    if (receiver.getClass() == e.getType() && e.getMethod().equals(this.name)) {
                        return ((GroovyObject)receiver).invokeMethod(this.name, args);
                    }
                    throw e;
                }
            }
            catch (GroovyRuntimeException gre) {
                throw ScriptBytecodeAdapter.unwrap(gre);
            }
        }
        return CallSiteArray.defaultCall(this, receiver, args);
    }

    protected final boolean checkCall(Object receiver) {
        return receiver instanceof GroovyObject && ((GroovyObject)receiver).getMetaClass() == this.metaClass;
    }

    @Override
    public final Object callCurrent(GroovyObject receiver, Object[] args) throws Throwable {
        if (this.checkCall(receiver)) {
            try {
                try {
                    return this.metaClass.invokeMethod((Object)receiver, this.name, args);
                }
                catch (MissingMethodException e) {
                    if (e instanceof MissingMethodExecutionFailed) {
                        throw (MissingMethodException)e.getCause();
                    }
                    if (receiver.getClass() == e.getType() && e.getMethod().equals(this.name)) {
                        return receiver.invokeMethod(this.name, args);
                    }
                    throw e;
                }
            }
            catch (GroovyRuntimeException gre) {
                throw ScriptBytecodeAdapter.unwrap(gre);
            }
        }
        return CallSiteArray.defaultCallCurrent(this, receiver, args);
    }
}

