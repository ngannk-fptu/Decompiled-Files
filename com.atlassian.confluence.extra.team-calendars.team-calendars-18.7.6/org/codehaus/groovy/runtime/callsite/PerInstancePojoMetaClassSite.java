/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import groovy.lang.GroovyRuntimeException;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.AbstractCallSite;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class PerInstancePojoMetaClassSite
extends AbstractCallSite {
    private final ClassInfo info;

    public PerInstancePojoMetaClassSite(CallSite site, ClassInfo info) {
        super(site);
        this.info = info;
    }

    @Override
    public Object call(Object receiver, Object[] args) throws Throwable {
        if (receiver != null && this.info.hasPerInstanceMetaClasses()) {
            try {
                return InvokerHelper.getMetaClass(receiver).invokeMethod(receiver, this.name, args);
            }
            catch (GroovyRuntimeException gre) {
                throw ScriptBytecodeAdapter.unwrap(gre);
            }
        }
        return CallSiteArray.defaultCall(this, receiver, args);
    }
}

