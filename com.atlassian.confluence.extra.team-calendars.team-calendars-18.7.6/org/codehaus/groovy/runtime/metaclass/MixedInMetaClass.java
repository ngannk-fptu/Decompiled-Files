/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.metaclass;

import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import java.lang.ref.WeakReference;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.runtime.metaclass.OwnedMetaClass;

public class MixedInMetaClass
extends OwnedMetaClass {
    private final WeakReference owner;

    public MixedInMetaClass(Object instance, Object owner) {
        super(GroovySystem.getMetaClassRegistry().getMetaClass(instance.getClass()));
        this.owner = new WeakReference<Object>(owner);
        MetaClassHelper.doSetMetaClass(instance, this);
    }

    @Override
    protected Object getOwner() {
        return this.owner.get();
    }

    @Override
    protected MetaClass getOwnerMetaClass(Object owner) {
        return InvokerHelper.getMetaClass(owner);
    }

    @Override
    public Object invokeMethod(Class sender, Object receiver, String methodName, Object[] arguments, boolean isCallToSuper, boolean fromInsideClass) {
        if (isCallToSuper) {
            return this.delegate.invokeMethod(sender, receiver, methodName, arguments, true, fromInsideClass);
        }
        return super.invokeMethod(sender, receiver, methodName, arguments, false, fromInsideClass);
    }
}

