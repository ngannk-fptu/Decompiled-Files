/*
 * Decompiled with CFR 0.152.
 */
package net.sf.cglib.proxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.sf.cglib.core.AbstractClassGenerator;
import net.sf.cglib.core.ClassEmitter;
import net.sf.cglib.core.ReflectUtils;
import net.sf.cglib.core.Signature;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;

public class InterfaceMaker
extends AbstractClassGenerator {
    private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source((class$net$sf$cglib$proxy$InterfaceMaker == null ? (class$net$sf$cglib$proxy$InterfaceMaker = InterfaceMaker.class$("net.sf.cglib.proxy.InterfaceMaker")) : class$net$sf$cglib$proxy$InterfaceMaker).getName());
    private Map signatures = new HashMap();
    static /* synthetic */ Class class$net$sf$cglib$proxy$InterfaceMaker;

    public InterfaceMaker() {
        super(SOURCE);
    }

    public void add(Signature sig, Type[] exceptions) {
        this.signatures.put(sig, exceptions);
    }

    public void add(Method method) {
        this.add(ReflectUtils.getSignature(method), ReflectUtils.getExceptionTypes(method));
    }

    public void add(Class clazz) {
        Method[] methods = clazz.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            Method m = methods[i];
            if (m.getDeclaringClass().getName().equals("java.lang.Object")) continue;
            this.add(m);
        }
    }

    public Class create() {
        this.setUseCache(false);
        return (Class)super.create(this);
    }

    protected ClassLoader getDefaultClassLoader() {
        return null;
    }

    protected Object firstInstance(Class type) {
        return type;
    }

    protected Object nextInstance(Object instance) {
        throw new IllegalStateException("InterfaceMaker does not cache");
    }

    public void generateClass(ClassVisitor v) throws Exception {
        ClassEmitter ce = new ClassEmitter(v);
        ce.begin_class(46, 513, this.getClassName(), null, null, "<generated>");
        Iterator it = this.signatures.keySet().iterator();
        while (it.hasNext()) {
            Signature sig = (Signature)it.next();
            Type[] exceptions = (Type[])this.signatures.get(sig);
            ce.begin_method(1025, sig, exceptions).end_method();
        }
        ce.end_class();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

