/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.MetaClass;
import groovy.swing.factory.ComponentFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class BoxFactory
extends ComponentFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public BoxFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = BoxFactory.$getCallSiteArray();
        super(null);
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = BoxFactory.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, Box.class))) {
            return value;
        }
        int axis = DefaultTypeTransformation.intUnbox(callSiteArray[1].callGetProperty(BoxLayout.class));
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call((Object)attributes, "axis"))) {
            Object o = callSiteArray[3].call((Object)attributes, "axis");
            if (o instanceof Number) {
                Object object = callSiteArray[4].call((Number)ScriptBytecodeAdapter.castToType(o, Number.class));
                axis = DefaultTypeTransformation.intUnbox(object);
            }
        }
        return callSiteArray[5].callConstructor(Box.class, axis);
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != BoxFactory.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ Object super$3$newInstance(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2, Map map) {
        return super.newInstance(factoryBuilderSupport, object, object2, map);
    }

    public /* synthetic */ MetaClass super$4$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "checkValueIsType";
        stringArray[1] = "X_AXIS";
        stringArray[2] = "containsKey";
        stringArray[3] = "remove";
        stringArray[4] = "intValue";
        stringArray[5] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[6];
        BoxFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(BoxFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = BoxFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

