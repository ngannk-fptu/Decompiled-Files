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

public class StaticMetaClassSite
extends MetaClassSite {
    private final ClassInfo classInfo;
    private final int version;

    public StaticMetaClassSite(CallSite site, MetaClass metaClass) {
        super(site, metaClass);
        this.classInfo = ClassInfo.getClassInfo(metaClass.getTheClass());
        this.version = this.classInfo.getVersion();
    }

    private boolean checkCall(Object receiver) {
        return receiver == this.metaClass.getTheClass() && this.version == this.classInfo.getVersion();
    }

    @Override
    public final Object call(Object receiver, Object[] args) throws Throwable {
        if (this.checkCall(receiver)) {
            try {
                return this.metaClass.invokeStaticMethod(receiver, this.name, args);
            }
            catch (GroovyRuntimeException gre) {
                throw ScriptBytecodeAdapter.unwrap(gre);
            }
        }
        return CallSiteArray.defaultCall(this, receiver, args);
    }

    @Override
    public final Object callStatic(Class receiver, Object[] args) throws Throwable {
        if (this.checkCall(receiver)) {
            try {
                return this.metaClass.invokeStaticMethod(receiver, this.name, args);
            }
            catch (GroovyRuntimeException gre) {
                throw ScriptBytecodeAdapter.unwrap(gre);
            }
        }
        return CallSiteArray.defaultCallStatic(this, receiver, args);
    }
}

