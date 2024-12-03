/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.MetaClass;
import groovy.swing.factory.RootPaneContainerFactory;
import groovy.util.FactoryBuilderSupport;
import java.awt.Dialog;
import java.awt.Frame;
import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.JDialog;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class DialogFactory
extends RootPaneContainerFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public DialogFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = DialogFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = DialogFactory.$getCallSiteArray();
        JDialog dialog = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, JDialog.class))) {
            Object object = value;
            dialog = (JDialog)ScriptBytecodeAdapter.castToType(object, JDialog.class);
        } else {
            Object owner = callSiteArray[1].call((Object)attributes, "owner");
            LinkedList containingWindows = (LinkedList)ScriptBytecodeAdapter.castToType(callSiteArray[2].callGroovyObjectGetProperty(builder), LinkedList.class);
            if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (ScriptBytecodeAdapter.compareEqual(owner, null) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[3].call(containingWindows))) {
                    Object object;
                    owner = object = callSiteArray[4].call(containingWindows);
                }
            } else if (ScriptBytecodeAdapter.compareEqual(owner, null) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[5].call(containingWindows))) {
                Object object;
                owner = object = callSiteArray[6].call(containingWindows);
            }
            if (owner instanceof Frame) {
                Object object = callSiteArray[7].callConstructor(JDialog.class, ScriptBytecodeAdapter.createPojoWrapper((Frame)ScriptBytecodeAdapter.castToType(owner, Frame.class), Frame.class));
                dialog = (JDialog)ScriptBytecodeAdapter.castToType(object, JDialog.class);
            } else if (owner instanceof Dialog) {
                Object object = callSiteArray[8].callConstructor(JDialog.class, ScriptBytecodeAdapter.createPojoWrapper((Dialog)ScriptBytecodeAdapter.castToType(owner, Dialog.class), Dialog.class));
                dialog = (JDialog)ScriptBytecodeAdapter.castToType(object, JDialog.class);
            } else {
                Object object = callSiteArray[9].callConstructor(JDialog.class);
                dialog = (JDialog)ScriptBytecodeAdapter.castToType(object, JDialog.class);
            }
        }
        callSiteArray[10].callCurrent(this, builder, dialog, attributes);
        return dialog;
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != DialogFactory.class) {
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
        stringArray[1] = "remove";
        stringArray[2] = "containingWindows";
        stringArray[3] = "isEmpty";
        stringArray[4] = "getLast";
        stringArray[5] = "isEmpty";
        stringArray[6] = "getLast";
        stringArray[7] = "<$constructor$>";
        stringArray[8] = "<$constructor$>";
        stringArray[9] = "<$constructor$>";
        stringArray[10] = "handleRootPaneTasks";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[11];
        DialogFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(DialogFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = DialogFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

