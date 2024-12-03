/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.swing.factory.LayoutFactory;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.awt.Container;
import java.lang.ref.SoftReference;
import java.util.Map;
import javax.swing.BoxLayout;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class BoxLayoutFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public BoxLayoutFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = BoxLayoutFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = BoxLayoutFactory.$getCallSiteArray();
        callSiteArray[0].call(FactoryBuilderSupport.class, value, name);
        Object parent = callSiteArray[1].call(builder);
        if (parent instanceof Container) {
            Object axisObject = callSiteArray[2].call((Object)attributes, "axis");
            int axis = DefaultTypeTransformation.intUnbox(callSiteArray[3].callGetProperty(BoxLayout.class));
            if (ScriptBytecodeAdapter.compareNotEqual(axisObject, null)) {
                Integer i = (Integer)ScriptBytecodeAdapter.castToType(axisObject, Integer.class);
                Object object = callSiteArray[4].call(i);
                axis = DefaultTypeTransformation.intUnbox(object);
            }
            Container target = (Container)ScriptBytecodeAdapter.castToType(callSiteArray[5].call(LayoutFactory.class, parent), Container.class);
            BoxLayout answer = (BoxLayout)ScriptBytecodeAdapter.castToType(callSiteArray[6].callConstructor(BoxLayout.class, target, axis), BoxLayout.class);
            callSiteArray[7].call(InvokerHelper.class, target, "layout", answer);
            return answer;
        }
        throw (Throwable)callSiteArray[8].callConstructor(RuntimeException.class, "Must be nested inside a Container");
    }

    @Override
    public void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
        CallSite[] callSiteArray = BoxLayoutFactory.$getCallSiteArray();
        if (parent instanceof Container) {
            Container target = (Container)ScriptBytecodeAdapter.castToType(callSiteArray[9].call(LayoutFactory.class, parent), Container.class);
            callSiteArray[10].call(InvokerHelper.class, target, "layout", child);
        }
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != BoxLayoutFactory.class) {
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

    public /* synthetic */ void super$2$setParent(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2) {
        super.setParent(factoryBuilderSupport, object, object2);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "checkValueIsNull";
        stringArray[1] = "getCurrent";
        stringArray[2] = "remove";
        stringArray[3] = "X_AXIS";
        stringArray[4] = "intValue";
        stringArray[5] = "getLayoutTarget";
        stringArray[6] = "<$constructor$>";
        stringArray[7] = "setProperty";
        stringArray[8] = "<$constructor$>";
        stringArray[9] = "getLayoutTarget";
        stringArray[10] = "setProperty";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[11];
        BoxLayoutFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(BoxLayoutFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = BoxLayoutFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

