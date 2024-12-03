/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaClass;
import java.lang.reflect.Field;
import org.codehaus.groovy.reflection.CachedField;
import org.codehaus.groovy.runtime.GroovyCategorySupport;
import org.codehaus.groovy.runtime.callsite.AbstractCallSite;
import org.codehaus.groovy.runtime.callsite.CallSite;

public class GetEffectivePogoFieldSite
extends AbstractCallSite {
    private final MetaClass metaClass;
    private final Field effective;

    public GetEffectivePogoFieldSite(CallSite site, MetaClass metaClass, CachedField effective) {
        super(site);
        this.metaClass = metaClass;
        this.effective = effective.field;
    }

    @Override
    public final Object callGetProperty(Object receiver) throws Throwable {
        if (GroovyCategorySupport.hasCategoryInCurrentThread() || !(receiver instanceof GroovyObject) || ((GroovyObject)receiver).getMetaClass() != this.metaClass) {
            return this.createGetPropertySite(receiver).getProperty(receiver);
        }
        return this.getProperty(receiver);
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
        if (GroovyCategorySupport.hasCategoryInCurrentThread() || ((GroovyObject)receiver).getMetaClass() != this.metaClass) {
            return this.createGroovyObjectGetPropertySite(receiver).getProperty(receiver);
        }
        return this.getProperty(receiver);
    }

    @Override
    public final CallSite acceptGroovyObjectGetProperty(Object receiver) {
        if (GroovyCategorySupport.hasCategoryInCurrentThread() || !(receiver instanceof GroovyObject) || ((GroovyObject)receiver).getMetaClass() != this.metaClass) {
            return this.createGroovyObjectGetPropertySite(receiver);
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

