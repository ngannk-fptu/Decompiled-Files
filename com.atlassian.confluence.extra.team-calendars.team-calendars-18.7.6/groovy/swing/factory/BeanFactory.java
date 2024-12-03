/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.GString;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MissingPropertyException;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Map;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class BeanFactory
extends AbstractFactory
implements GroovyObject {
    private final Class beanClass;
    protected final boolean leaf;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public BeanFactory(Class beanClass) {
        CallSite[] callSiteArray = BeanFactory.$getCallSiteArray();
        this(beanClass, false);
    }

    public BeanFactory(Class beanClass, boolean leaf) {
        MetaClass metaClass;
        CallSite[] callSiteArray = BeanFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        Class clazz = beanClass;
        this.beanClass = ShortTypeHandling.castToClass(clazz);
        boolean bl = leaf;
        this.leaf = DefaultTypeTransformation.booleanUnbox(bl);
    }

    @Override
    public boolean isLeaf() {
        CallSite[] callSiteArray = BeanFactory.$getCallSiteArray();
        return this.leaf;
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = BeanFactory.$getCallSiteArray();
        if (value instanceof GString) {
            String string = (String)ScriptBytecodeAdapter.asType(value, String.class);
            value = string;
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, this.beanClass))) {
            return value;
        }
        Object bean = callSiteArray[1].call(this.beanClass);
        if (value instanceof String) {
            try {
                Object object = value;
                ScriptBytecodeAdapter.setProperty(object, null, bean, "text");
            }
            catch (MissingPropertyException mpe) {
                throw (Throwable)callSiteArray[2].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name}, new String[]{"In ", " value argument of type String cannot be applied to property text:"}));
            }
        }
        return bean;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != BeanFactory.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    @Override
    public /* synthetic */ MetaClass getMetaClass() {
        MetaClass metaClass = this.metaClass;
        if (metaClass != null) {
            return metaClass;
        }
        this.metaClass = this.$getStaticMetaClass();
        return this.metaClass;
    }

    @Override
    public /* synthetic */ void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    public /* synthetic */ Object invokeMethod(String string, Object object) {
        return this.getMetaClass().invokeMethod((Object)this, string, object);
    }

    @Override
    public /* synthetic */ Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    public /* synthetic */ void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }

    public final Class getBeanClass() {
        return this.beanClass;
    }

    public /* synthetic */ boolean super$2$isLeaf() {
        return super.isLeaf();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "checkValueIsTypeNotString";
        stringArray[1] = "newInstance";
        stringArray[2] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[3];
        BeanFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(BeanFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = BeanFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

