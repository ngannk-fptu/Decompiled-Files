/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.AbstractCallSite;
import org.codehaus.groovy.runtime.callsite.CallSite;

public class PogoMetaClassGetPropertySite
extends AbstractCallSite {
    private final MetaClass metaClass;

    public PogoMetaClassGetPropertySite(CallSite parent, MetaClass metaClass) {
        super(parent);
        this.metaClass = metaClass;
    }

    @Override
    public final CallSite acceptGetProperty(Object receiver) {
        if (!(receiver instanceof GroovyObject) || ((GroovyObject)receiver).getMetaClass() != this.metaClass) {
            return this.createGetPropertySite(receiver);
        }
        return this;
    }

    @Override
    public final CallSite acceptGroovyObjectGetProperty(Object receiver) {
        if (!(receiver instanceof GroovyObject) || ((GroovyObject)receiver).getMetaClass() != this.metaClass) {
            return this.createGroovyObjectGetPropertySite(receiver);
        }
        return this;
    }

    @Override
    public final Object getProperty(Object receiver) throws Throwable {
        try {
            return this.metaClass.getProperty(receiver, this.name);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }
}

