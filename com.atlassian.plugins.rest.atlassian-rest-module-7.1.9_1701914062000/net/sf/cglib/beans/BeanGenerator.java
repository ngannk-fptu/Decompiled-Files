/*
 * Decompiled with CFR 0.152.
 */
package net.sf.cglib.beans;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.sf.cglib.core.AbstractClassGenerator;
import net.sf.cglib.core.ClassEmitter;
import net.sf.cglib.core.Constants;
import net.sf.cglib.core.EmitUtils;
import net.sf.cglib.core.KeyFactory;
import net.sf.cglib.core.ReflectUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;

public class BeanGenerator
extends AbstractClassGenerator {
    private static final AbstractClassGenerator.Source SOURCE = new AbstractClassGenerator.Source((class$net$sf$cglib$beans$BeanGenerator == null ? (class$net$sf$cglib$beans$BeanGenerator = BeanGenerator.class$("net.sf.cglib.beans.BeanGenerator")) : class$net$sf$cglib$beans$BeanGenerator).getName());
    private static final BeanGeneratorKey KEY_FACTORY = (BeanGeneratorKey)((Object)KeyFactory.create(class$net$sf$cglib$beans$BeanGenerator$BeanGeneratorKey == null ? (class$net$sf$cglib$beans$BeanGenerator$BeanGeneratorKey = BeanGenerator.class$("net.sf.cglib.beans.BeanGenerator$BeanGeneratorKey")) : class$net$sf$cglib$beans$BeanGenerator$BeanGeneratorKey));
    private Class superclass;
    private Map props = new HashMap();
    private boolean classOnly;
    static /* synthetic */ Class class$net$sf$cglib$beans$BeanGenerator;
    static /* synthetic */ Class class$net$sf$cglib$beans$BeanGenerator$BeanGeneratorKey;
    static /* synthetic */ Class class$java$lang$Object;

    public BeanGenerator() {
        super(SOURCE);
    }

    public void setSuperclass(Class superclass) {
        if (superclass != null && superclass.equals(class$java$lang$Object == null ? (class$java$lang$Object = BeanGenerator.class$("java.lang.Object")) : class$java$lang$Object)) {
            superclass = null;
        }
        this.superclass = superclass;
    }

    public void addProperty(String name, Class type) {
        if (this.props.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate property name \"" + name + "\"");
        }
        this.props.put(name, Type.getType(type));
    }

    protected ClassLoader getDefaultClassLoader() {
        if (this.superclass != null) {
            return this.superclass.getClassLoader();
        }
        return null;
    }

    public Object create() {
        this.classOnly = false;
        return this.createHelper();
    }

    public Object createClass() {
        this.classOnly = true;
        return this.createHelper();
    }

    private Object createHelper() {
        if (this.superclass != null) {
            this.setNamePrefix(this.superclass.getName());
        }
        String superName = this.superclass != null ? this.superclass.getName() : "java.lang.Object";
        Object key = KEY_FACTORY.newInstance(superName, this.props);
        return super.create(key);
    }

    public void generateClass(ClassVisitor v) throws Exception {
        int size = this.props.size();
        String[] names = this.props.keySet().toArray(new String[size]);
        Type[] types = new Type[size];
        for (int i = 0; i < size; ++i) {
            types[i] = (Type)this.props.get(names[i]);
        }
        ClassEmitter ce = new ClassEmitter(v);
        ce.begin_class(46, 1, this.getClassName(), this.superclass != null ? Type.getType(this.superclass) : Constants.TYPE_OBJECT, null, null);
        EmitUtils.null_constructor(ce);
        EmitUtils.add_properties(ce, names, types);
        ce.end_class();
    }

    protected Object firstInstance(Class type) {
        if (this.classOnly) {
            return type;
        }
        return ReflectUtils.newInstance(type);
    }

    protected Object nextInstance(Object instance) {
        Class<?> protoclass;
        Class<?> clazz = protoclass = instance instanceof Class ? (Class<?>)instance : instance.getClass();
        if (this.classOnly) {
            return protoclass;
        }
        return ReflectUtils.newInstance(protoclass);
    }

    public static void addProperties(BeanGenerator gen, Map props) {
        Iterator it = props.keySet().iterator();
        while (it.hasNext()) {
            String name = (String)it.next();
            gen.addProperty(name, (Class)props.get(name));
        }
    }

    public static void addProperties(BeanGenerator gen, Class type) {
        BeanGenerator.addProperties(gen, ReflectUtils.getBeanProperties(type));
    }

    public static void addProperties(BeanGenerator gen, PropertyDescriptor[] descriptors) {
        for (int i = 0; i < descriptors.length; ++i) {
            gen.addProperty(descriptors[i].getName(), descriptors[i].getPropertyType());
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static interface BeanGeneratorKey {
        public Object newInstance(String var1, Map var2);
    }
}

