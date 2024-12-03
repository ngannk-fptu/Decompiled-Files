/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.parameter;

import groovy.lang.MetaClass;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Map;
import net.fortuna.ical4j.model.parameter.AbstractParameterFactory;
import net.fortuna.ical4j.model.parameter.CuType;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class CuTypeFactory
extends AbstractParameterFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public CuTypeFactory() {
        CallSite[] callSiteArray = CuTypeFactory.$getCallSiteArray();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = CuTypeFactory.$getCallSiteArray();
        CuType cuType = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, CuType.class))) {
            Object object = value;
            cuType = (CuType)ScriptBytecodeAdapter.castToType(object, CuType.class);
        } else {
            Object object = value;
            if (ScriptBytecodeAdapter.isCase(object, callSiteArray[1].callGetProperty(callSiteArray[2].callGetProperty(CuType.class)))) {
                Object object2 = callSiteArray[3].callGetProperty(CuType.class);
                cuType = (CuType)ScriptBytecodeAdapter.castToType(object2, CuType.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[4].callGetProperty(callSiteArray[5].callGetProperty(CuType.class)))) {
                Object object3 = callSiteArray[6].callGetProperty(CuType.class);
                cuType = (CuType)ScriptBytecodeAdapter.castToType(object3, CuType.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[7].callGetProperty(callSiteArray[8].callGetProperty(CuType.class)))) {
                Object object4 = callSiteArray[9].callGetProperty(CuType.class);
                cuType = (CuType)ScriptBytecodeAdapter.castToType(object4, CuType.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[10].callGetProperty(callSiteArray[11].callGetProperty(CuType.class)))) {
                Object object5 = callSiteArray[12].callGetProperty(CuType.class);
                cuType = (CuType)ScriptBytecodeAdapter.castToType(object5, CuType.class);
            } else {
                Object object6 = callSiteArray[13].callConstructor(CuType.class, value);
                cuType = (CuType)ScriptBytecodeAdapter.castToType(object6, CuType.class);
            }
        }
        return cuType;
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != CuTypeFactory.class) {
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
        stringArray[0] = "checkValueIsTypeNotString";
        stringArray[1] = "value";
        stringArray[2] = "GROUP";
        stringArray[3] = "GROUP";
        stringArray[4] = "value";
        stringArray[5] = "INDIVIDUAL";
        stringArray[6] = "INDIVIDUAL";
        stringArray[7] = "value";
        stringArray[8] = "RESOURCE";
        stringArray[9] = "RESOURCE";
        stringArray[10] = "value";
        stringArray[11] = "ROOM";
        stringArray[12] = "ROOM";
        stringArray[13] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[14];
        CuTypeFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(CuTypeFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = CuTypeFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

