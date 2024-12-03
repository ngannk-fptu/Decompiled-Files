/*
 * Decompiled with CFR 0.152.
 */
package net.sf.cglib.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import net.sf.cglib.core.CollectionUtils;
import net.sf.cglib.core.ReflectUtils;
import net.sf.cglib.core.RejectModifierPredicate;
import net.sf.cglib.proxy.MixinEmitter;
import org.objectweb.asm.ClassVisitor;

class MixinEverythingEmitter
extends MixinEmitter {
    public MixinEverythingEmitter(ClassVisitor v, String className, Class[] classes) {
        super(v, className, classes, null);
    }

    protected Class[] getInterfaces(Class[] classes) {
        ArrayList list = new ArrayList();
        for (int i = 0; i < classes.length; ++i) {
            ReflectUtils.addAllInterfaces(classes[i], list);
        }
        return list.toArray(new Class[list.size()]);
    }

    protected Method[] getMethods(Class type) {
        ArrayList<Method> methods = new ArrayList<Method>(Arrays.asList(type.getMethods()));
        CollectionUtils.filter(methods, new RejectModifierPredicate(24));
        return methods.toArray(new Method[methods.size()]);
    }
}

