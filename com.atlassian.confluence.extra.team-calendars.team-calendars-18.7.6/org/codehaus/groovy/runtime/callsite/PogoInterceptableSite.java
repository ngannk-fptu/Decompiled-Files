/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.AbstractCallSite;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class PogoInterceptableSite
extends AbstractCallSite {
    public PogoInterceptableSite(CallSite site) {
        super(site);
    }

    public final Object invoke(Object receiver, Object[] args) throws Throwable {
        try {
            return ((GroovyObject)receiver).invokeMethod(this.name, InvokerHelper.asUnwrappedArray(args));
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }

    @Override
    public final Object call(Object receiver, Object[] args) throws Throwable {
        if (receiver instanceof GroovyObject) {
            try {
                return ((GroovyObject)receiver).invokeMethod(this.name, InvokerHelper.asUnwrappedArray(args));
            }
            catch (GroovyRuntimeException gre) {
                throw ScriptBytecodeAdapter.unwrap(gre);
            }
        }
        return CallSiteArray.defaultCall(this, receiver, args);
    }

    @Override
    public Object callCurrent(GroovyObject receiver, Object[] args) throws Throwable {
        return this.call((Object)receiver, args);
    }
}

