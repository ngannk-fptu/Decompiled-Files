/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.callsite.MetaClassSite;

public class ConstructorMetaClassSite
extends MetaClassSite {
    public ConstructorMetaClassSite(CallSite site, MetaClass metaClass) {
        super(site, metaClass);
    }

    @Override
    public Object callConstructor(Object receiver, Object[] args) throws Throwable {
        if (receiver == this.metaClass.getTheClass()) {
            try {
                return this.metaClass.invokeConstructor(args);
            }
            catch (GroovyRuntimeException gre) {
                throw ScriptBytecodeAdapter.unwrap(gre);
            }
        }
        return CallSiteArray.defaultCallConstructor(this, (Class)receiver, args);
    }
}

