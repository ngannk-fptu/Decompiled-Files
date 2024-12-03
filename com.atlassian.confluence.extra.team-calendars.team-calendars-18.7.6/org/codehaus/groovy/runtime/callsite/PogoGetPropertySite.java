/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.AbstractCallSite;
import org.codehaus.groovy.runtime.callsite.CallSite;

public class PogoGetPropertySite
extends AbstractCallSite {
    private final Class aClass;

    public PogoGetPropertySite(CallSite parent, Class aClass) {
        super(parent);
        this.aClass = aClass;
    }

    @Override
    public CallSite acceptGetProperty(Object receiver) {
        if (receiver == null || receiver.getClass() != this.aClass) {
            return this.createGetPropertySite(receiver);
        }
        return this;
    }

    @Override
    public CallSite acceptGroovyObjectGetProperty(Object receiver) {
        if (receiver == null || receiver.getClass() != this.aClass) {
            return this.createGroovyObjectGetPropertySite(receiver);
        }
        return this;
    }

    @Override
    public Object getProperty(Object receiver) throws Throwable {
        try {
            return ((GroovyObject)receiver).getProperty(this.name);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }
}

