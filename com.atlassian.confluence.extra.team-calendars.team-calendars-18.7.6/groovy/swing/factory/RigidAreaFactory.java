/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.awt.Dimension;
import java.lang.ref.SoftReference;
import java.util.Map;
import javax.swing.Box;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class RigidAreaFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public RigidAreaFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = RigidAreaFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = RigidAreaFactory.$getCallSiteArray();
        callSiteArray[0].call(FactoryBuilderSupport.class, value, name);
        Dimension dim = null;
        Object o = callSiteArray[1].call((Object)attributes, "size");
        if (o instanceof Dimension) {
            Dimension dimension;
            dim = dimension = (Dimension)ScriptBytecodeAdapter.castToType(o, Dimension.class);
        } else {
            Object object;
            Object object2;
            int w = 0;
            int h = 0;
            o = object2 = callSiteArray[2].call((Object)attributes, "width");
            Integer n = o instanceof Number ? callSiteArray[3].call((Number)ScriptBytecodeAdapter.castToType(o, Number.class)) : Integer.valueOf(6);
            w = DefaultTypeTransformation.intUnbox(n);
            o = object = callSiteArray[4].call((Object)attributes, "height");
            Integer n2 = o instanceof Number ? callSiteArray[5].call((Number)ScriptBytecodeAdapter.castToType(o, Number.class)) : Integer.valueOf(6);
            h = DefaultTypeTransformation.intUnbox(n2);
            Object object3 = callSiteArray[6].callConstructor(Dimension.class, w, h);
            dim = (Dimension)ScriptBytecodeAdapter.castToType(object3, Dimension.class);
        }
        return callSiteArray[7].call(Box.class, dim);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != RigidAreaFactory.class) {
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

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "checkValueIsNull";
        stringArray[1] = "remove";
        stringArray[2] = "remove";
        stringArray[3] = "intValue";
        stringArray[4] = "remove";
        stringArray[5] = "intValue";
        stringArray[6] = "<$constructor$>";
        stringArray[7] = "createRigidArea";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[8];
        RigidAreaFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(RigidAreaFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = RigidAreaFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

