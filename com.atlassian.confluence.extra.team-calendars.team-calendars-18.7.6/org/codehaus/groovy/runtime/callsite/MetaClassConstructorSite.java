/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaClass;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.callsite.MetaClassSite;

public class MetaClassConstructorSite
extends MetaClassSite {
    private final ClassInfo classInfo;
    private final int version;

    public MetaClassConstructorSite(CallSite site, MetaClass metaClass) {
        super(site, metaClass);
        this.classInfo = ClassInfo.getClassInfo(metaClass.getTheClass());
        this.version = this.classInfo.getVersion();
    }

    @Override
    public Object callConstructor(Object receiver, Object[] args) throws Throwable {
        try {
            if (receiver == this.metaClass.getTheClass() && this.version == this.classInfo.getVersion()) {
                return this.metaClass.invokeConstructor(args);
            }
            return CallSiteArray.defaultCallConstructor(this, receiver, args);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }
}

