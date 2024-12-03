/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaClassImpl;
import groovy.lang.MetaProperty;
import org.codehaus.groovy.runtime.GroovyCategorySupport;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.AbstractCallSite;
import org.codehaus.groovy.runtime.callsite.CallSite;

public class GetEffectivePojoPropertySite
extends AbstractCallSite {
    private final MetaClassImpl metaClass;
    private final MetaProperty effective;
    private final int version;

    public GetEffectivePojoPropertySite(CallSite site, MetaClassImpl metaClass, MetaProperty effective) {
        super(site);
        this.metaClass = metaClass;
        this.effective = effective;
        this.version = metaClass.getVersion();
    }

    @Override
    public final CallSite acceptGetProperty(Object receiver) {
        if (GroovyCategorySupport.hasCategoryInCurrentThread() || receiver == null || receiver.getClass() != this.metaClass.getTheClass() || this.version != this.metaClass.getVersion()) {
            return this.createGetPropertySite(receiver);
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

