/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import groovy.lang.MetaClass;
import org.codehaus.groovy.runtime.callsite.AbstractCallSite;
import org.codehaus.groovy.runtime.callsite.CallSite;

public abstract class MetaClassSite
extends AbstractCallSite {
    protected final MetaClass metaClass;

    public MetaClassSite(CallSite site, MetaClass metaClass) {
        super(site);
        this.metaClass = metaClass;
    }
}

