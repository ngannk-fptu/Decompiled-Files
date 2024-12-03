/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import groovy.lang.GroovyRuntimeException;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.AbstractCallSite;
import org.codehaus.groovy.runtime.callsite.CallSite;

public class PojoMetaClassGetPropertySite
extends AbstractCallSite {
    public PojoMetaClassGetPropertySite(CallSite parent) {
        super(parent);
    }

    @Override
    public final CallSite acceptGetProperty(Object receiver) {
        return this;
    }

    @Override
    public final Object getProperty(Object receiver) throws Throwable {
        try {
            return InvokerHelper.getProperty(receiver, this.name);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }

    @Override
    public Object callGetProperty(Object receiver) throws Throwable {
        try {
            return InvokerHelper.getProperty(receiver, this.name);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }
}

