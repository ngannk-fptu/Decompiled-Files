/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.MetaClass;
import groovy.swing.factory.RootPaneContainerFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.JWindow;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class WindowFactory
extends RootPaneContainerFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public WindowFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = WindowFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = WindowFactory.$getCallSiteArray();
        JWindow window = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, JWindow.class))) {
            Object object = value;
            window = (JWindow)ScriptBytecodeAdapter.castToType(object, JWindow.class);
        } else {
            LinkedList containingWindows = (LinkedList)ScriptBytecodeAdapter.castToType(callSiteArray[1].callGroovyObjectGetProperty(builder), LinkedList.class);
            Object owner = callSiteArray[2].call((Object)attributes, "owner");
            if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (ScriptBytecodeAdapter.compareEqual(owner, null) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[3].callGetProperty(containingWindows))) {
                    Object object;
                    owner = object = callSiteArray[4].callGetProperty(containingWindows);
                }
            } else if (ScriptBytecodeAdapter.compareEqual(owner, null) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[5].callGetProperty(containingWindows))) {
                Object object;
                owner = object = callSiteArray[6].callGetProperty(containingWindows);
            }
            if (DefaultTypeTransformation.booleanUnbox(owner)) {
                Object object = callSiteArray[7].callConstructor(JWindow.class, owner);
                window = (JWindow)ScriptBytecodeAdapter.castToType(object, JWindow.class);
            } else {
                Object object = callSiteArray[8].callConstructor(JWindow.class);
                window = (JWindow)ScriptBytecodeAdapter.castToType(object, JWindow.class);
            }
        }
        callSiteArray[9].callCurrent(this, builder, window, attributes);
        return window;
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != WindowFactory.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ MetaClass super$3$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "checkValueIsType";
        stringArray[1] = "containingWindows";
        stringArray[2] = "remove";
        stringArray[3] = "empty";
        stringArray[4] = "last";
        stringArray[5] = "empty";
        stringArray[6] = "last";
        stringArray[7] = "<$constructor$>";
        stringArray[8] = "<$constructor$>";
        stringArray[9] = "handleRootPaneTasks";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[10];
        WindowFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(WindowFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = WindowFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

