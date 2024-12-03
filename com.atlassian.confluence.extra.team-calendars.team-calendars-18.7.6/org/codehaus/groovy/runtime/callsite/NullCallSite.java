/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import groovy.lang.GroovyRuntimeException;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.NullObject;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.AbstractCallSite;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public final class NullCallSite
extends AbstractCallSite {
    public NullCallSite(CallSite callSite) {
        super(callSite);
    }

    @Override
    public Object call(Object receiver, Object[] args) throws Throwable {
        if (receiver == null) {
            try {
                return CallSiteArray.defaultCall(this, NullObject.getNullObject(), args);
            }
            catch (GroovyRuntimeException gre) {
                throw ScriptBytecodeAdapter.unwrap(gre);
            }
        }
        return CallSiteArray.defaultCall(this, receiver, args);
    }

    @Override
    public Object getProperty(Object receiver) throws Throwable {
        if (receiver == null) {
            try {
                return InvokerHelper.getProperty(NullObject.getNullObject(), this.name);
            }
            catch (GroovyRuntimeException gre) {
                throw ScriptBytecodeAdapter.unwrap(gre);
            }
        }
        return this.acceptGetProperty(receiver).getProperty(receiver);
    }
}

