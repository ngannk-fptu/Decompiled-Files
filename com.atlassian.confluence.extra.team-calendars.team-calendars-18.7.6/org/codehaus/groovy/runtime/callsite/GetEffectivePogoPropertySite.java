/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaClass;
import groovy.lang.MetaProperty;
import org.codehaus.groovy.runtime.GroovyCategorySupport;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.AbstractCallSite;
import org.codehaus.groovy.runtime.callsite.CallSite;

class GetEffectivePogoPropertySite
extends AbstractCallSite {
    private final MetaClass metaClass;
    private final MetaProperty effective;

    public GetEffectivePogoPropertySite(CallSite site, MetaClass metaClass, MetaProperty effective) {
        super(site);
        this.metaClass = metaClass;
        this.effective = effective;
    }

    @Override
    public final Object callGetProperty(Object receiver) throws Throwable {
        if (GroovyCategorySupport.hasCategoryInCurrentThread() || !(receiver instanceof GroovyObject) || ((GroovyObject)receiver).getMetaClass() != this.metaClass) {
            return this.createGetPropertySite(receiver).getProperty(receiver);
        }
        try {
            return this.effective.getProperty(receiver);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }

    @Override
    public final CallSite acceptGetProperty(Object receiver) {
        if (GroovyCategorySupport.hasCategoryInCurrentThread() || !(receiver instanceof GroovyObject) || ((GroovyObject)receiver).getMetaClass() != this.metaClass) {
            return this.createGetPropertySite(receiver);
        }
        return this;
    }

    @Override
    public final Object callGroovyObjectGetProperty(Object receiver) throws Throwable {
        if (GroovyCategorySupport.hasCategoryInCurrentThread() || !(receiver instanceof GroovyObject) || ((GroovyObject)receiver).getMetaClass() != this.metaClass) {
            return this.createGetPropertySite(receiver).getProperty(receiver);
        }
        try {
            return this.effective.getProperty(receiver);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }

    @Override
    public final CallSite acceptGroovyObjectGetProperty(Object receiver) {
        if (GroovyCategorySupport.hasCategoryInCurrentThread() || !(receiver instanceof GroovyObject) || ((GroovyObject)receiver).getMetaClass() != this.metaClass) {
            return this.createGroovyObjectGetPropertySite(receiver);
        }
        return this;
    }

    @Override
    public final Object getProperty(Object receiver) throws Throwable {
        try {
            return this.effective.getProperty(receiver);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }
}

