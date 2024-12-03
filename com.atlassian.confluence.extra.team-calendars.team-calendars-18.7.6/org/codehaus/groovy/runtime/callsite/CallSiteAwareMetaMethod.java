/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.callsite;

import groovy.lang.MetaClassImpl;
import groovy.lang.MetaMethod;
import org.codehaus.groovy.runtime.callsite.CallSite;

public abstract class CallSiteAwareMetaMethod
extends MetaMethod {
    public abstract CallSite createPojoCallSite(CallSite var1, MetaClassImpl var2, MetaMethod var3, Class[] var4, Object var5, Object[] var6);
}

