/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaClassImpl;
import java.lang.reflect.Field;
import org.codehaus.groovy.reflection.CachedField;
import org.codehaus.groovy.runtime.GroovyCategorySupport;
import org.codehaus.groovy.runtime.callsite.AbstractCallSite;
import org.codehaus.groovy.runtime.callsite.CallSite;

class GetEffectivePojoFieldSite
extends AbstractCallSite {
    private final MetaClassImpl metaClass;
    private final Field effective;
    private final int version;

    public GetEffectivePojoFieldSite(CallSite site, MetaClassImpl metaClass, CachedField effective) {
        super(site);
        this.metaClass = metaClass;
        this.effective = effective.field;
        this.version = metaClass.getVersion();
    }

    @Override
    public final CallSite acceptGetProperty(Object receiver) {
        if (GroovyCategorySupport.hasCategoryInCurrentThread() || receiver.getClass() != this.metaClass.getTheClass() || this.version != this.metaClass.getVersion()) {
            return this.createGetPropertySite(receiver);
        }
        return this;
    }

    @Override
    public final Object getProperty(Object receiver) {
        try {
            return this.effective.get(receiver);
        }
        catch (IllegalAccessException e) {
            throw new GroovyRuntimeException("Cannot get the property '" + this.name + "'.", e);
        }
    }
}

