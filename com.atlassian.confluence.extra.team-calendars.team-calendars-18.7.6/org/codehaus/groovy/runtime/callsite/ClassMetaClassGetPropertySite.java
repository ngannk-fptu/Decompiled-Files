/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaClass;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.AbstractCallSite;
import org.codehaus.groovy.runtime.callsite.CallSite;

class ClassMetaClassGetPropertySite
extends AbstractCallSite {
    final MetaClass metaClass;
    private final Class aClass;
    private final ClassInfo classInfo;
    private final int version;

    public ClassMetaClassGetPropertySite(CallSite parent, Class aClass) {
        super(parent);
        this.aClass = aClass;
        this.classInfo = ClassInfo.getClassInfo(aClass);
        this.version = this.classInfo.getVersion();
        this.metaClass = this.classInfo.getMetaClass();
    }

    @Override
    public final CallSite acceptGetProperty(Object receiver) {
        if (receiver != this.aClass || this.version != this.classInfo.getVersion()) {
            return this.createGetPropertySite(receiver);
        }
        return this;
    }

    @Override
    public final Object getProperty(Object receiver) throws Throwable {
        try {
            return this.metaClass.getProperty(this.aClass, this.name);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }
}

