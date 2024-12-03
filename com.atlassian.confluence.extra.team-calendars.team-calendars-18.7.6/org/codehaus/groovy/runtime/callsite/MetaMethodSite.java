/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.MetaClassSite;

public abstract class MetaMethodSite
extends MetaClassSite {
    final MetaMethod metaMethod;
    protected final Class[] params;

    public MetaMethodSite(CallSite site, MetaClass metaClass, MetaMethod metaMethod, Class[] params) {
        super(site, metaClass);
        this.metaMethod = metaMethod;
        this.params = params;
    }
}

