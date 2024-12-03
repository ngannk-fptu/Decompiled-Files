/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.MetaClass;
import groovy.swing.factory.SwingBorderFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class MatteBorderFactory
extends SwingBorderFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public MatteBorderFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = MatteBorderFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        CallSite[] callSiteArray = MatteBorderFactory.$getCallSiteArray();
        Object object = callSiteArray[0].call((Object)attributes, "parent");
        ScriptBytecodeAdapter.setProperty(object, null, callSiteArray[1].callGroovyObjectGetProperty(builder), "applyBorderToParent");
        Object matte = null;
        Object border = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call((Object)attributes, "icon"))) {
            Object object2;
            matte = object2 = callSiteArray[3].call((Object)attributes, "icon");
        } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[4].call((Object)attributes, "color"))) {
            Object object3;
            matte = object3 = callSiteArray[5].call((Object)attributes, "color");
        } else if (ScriptBytecodeAdapter.compareNotEqual(value, null)) {
            Object object4;
            matte = object4 = value;
        } else {
            throw (Throwable)callSiteArray[6].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name}, new String[]{"", " must have a matte defined, either as a value argument or as a color: or icon: attribute"}));
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[7].call((Object)attributes, "size"))) {
            Object object5;
            border = object5 = callSiteArray[8].call((Object)attributes, "size");
            List list = ScriptBytecodeAdapter.createList(new Object[]{border, border, border, border});
            border = list;
        } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[9].call((Object)attributes, "top"))) {
            Object top = callSiteArray[10].call((Object)attributes, "top");
            Object left = callSiteArray[11].call((Object)attributes, "left");
            Object bottom = callSiteArray[12].call((Object)attributes, "bottom");
            Object right = callSiteArray[13].call((Object)attributes, "right");
            if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (ScriptBytecodeAdapter.compareEqual(top, null) || ScriptBytecodeAdapter.compareEqual(left, null) || ScriptBytecodeAdapter.compareEqual(bottom, null) || ScriptBytecodeAdapter.compareEqual(right, null)) {
                    throw (Throwable)callSiteArray[14].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name}, new String[]{"In ", " if one of top:, left:, bottom: or right: is specified all must be specified"}));
                }
            } else if (ScriptBytecodeAdapter.compareEqual(top, null) || ScriptBytecodeAdapter.compareEqual(left, null) || ScriptBytecodeAdapter.compareEqual(bottom, null) || ScriptBytecodeAdapter.compareEqual(right, null)) {
                throw (Throwable)callSiteArray[15].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name}, new String[]{"In ", " if one of top:, left:, bottom: or right: is specified all must be specified"}));
            }
            List list = ScriptBytecodeAdapter.createList(new Object[]{top, left, bottom, right});
            border = list;
        } else if (ScriptBytecodeAdapter.compareNotEqual(value, null)) {
            if (ScriptBytecodeAdapter.compareEqual(matte, value)) {
                throw (Throwable)callSiteArray[16].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name}, new String[]{"In ", " some attributes are required in addition to the value argument"}));
            }
            if (value instanceof Integer) {
                List list = ScriptBytecodeAdapter.createList(new Object[]{value, value, value, value});
                border = list;
            } else {
                Object object6;
                border = object6 = value;
            }
        }
        if (DefaultTypeTransformation.booleanUnbox(attributes)) {
            throw (Throwable)callSiteArray[17].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name}, new String[]{"", " only supports the attributes [ icon: | color:]  [ size: | ( top: left: bottom: right: ) }"}));
        }
        return callSiteArray[18].call((Object)BorderFactory.class, ScriptBytecodeAdapter.despreadList(new Object[]{matte}, new Object[]{border}, new int[]{0}));
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != MatteBorderFactory.class) {
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
        stringArray[0] = "remove";
        stringArray[1] = "context";
        stringArray[2] = "containsKey";
        stringArray[3] = "remove";
        stringArray[4] = "containsKey";
        stringArray[5] = "remove";
        stringArray[6] = "<$constructor$>";
        stringArray[7] = "containsKey";
        stringArray[8] = "remove";
        stringArray[9] = "containsKey";
        stringArray[10] = "remove";
        stringArray[11] = "remove";
        stringArray[12] = "remove";
        stringArray[13] = "remove";
        stringArray[14] = "<$constructor$>";
        stringArray[15] = "<$constructor$>";
        stringArray[16] = "<$constructor$>";
        stringArray[17] = "<$constructor$>";
        stringArray[18] = "createMatteBorder";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[19];
        MatteBorderFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(MatteBorderFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = MatteBorderFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

