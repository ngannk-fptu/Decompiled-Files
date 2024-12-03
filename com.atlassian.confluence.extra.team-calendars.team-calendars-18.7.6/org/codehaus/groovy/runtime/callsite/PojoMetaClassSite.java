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

public class PojoMetaClassSite
extends MetaClassSite {
    private final ClassInfo classInfo;
    private final int version;

    public PojoMetaClassSite(CallSite site, MetaClass metaClass) {
        super(site, metaClass);
        this.classInfo = ClassInfo.getClassInfo(metaClass.getTheClass());
        this.version = this.classInfo.getVersion();
    }

    @Override
    public Object call(Object receiver, Object[] args) throws Throwable {
        if (this.checkCall(receiver)) {
            try {
                return this.metaClass.invokeMethod(receiver, this.name, args);
            }
            catch (GroovyRuntimeException gre) {
                throw ScriptBytecodeAdapter.unwrap(gre);
            }
        }
        return CallSiteArray.defaultCall(this, receiver, args);
    }

    protected final boolean checkCall(Object receiver) {
        return receiver != null && receiver.getClass() == this.metaClass.getTheClass() && this.version == this.classInfo.getVersion();
    }
}

