/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.parameter;

import groovy.lang.MetaClass;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import net.fortuna.ical4j.model.parameter.AbstractParameterFactory;
import net.fortuna.ical4j.model.parameter.FbType;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class FbTypeFactory
extends AbstractParameterFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public FbTypeFactory() {
        CallSite[] callSiteArray = FbTypeFactory.$getCallSiteArray();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = FbTypeFactory.$getCallSiteArray();
        FbType fbType = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, FbType.class))) {
            Object object = value;
            fbType = (FbType)ScriptBytecodeAdapter.castToType(object, FbType.class);
        } else {
            Object object = value;
            if (ScriptBytecodeAdapter.isCase(object, callSiteArray[1].callGetProperty(callSiteArray[2].callGetProperty(FbType.class)))) {
                Object object2 = callSiteArray[3].callGetProperty(FbType.class);
                fbType = (FbType)ScriptBytecodeAdapter.castToType(object2, FbType.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[4].callGetProperty(callSiteArray[5].callGetProperty(FbType.class)))) {
                Object object3 = callSiteArray[6].callGetProperty(FbType.class);
                fbType = (FbType)ScriptBytecodeAdapter.castToType(object3, FbType.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[7].callGetProperty(callSiteArray[8].callGetProperty(FbType.class)))) {
                Object object4 = callSiteArray[9].callGetProperty(FbType.class);
                fbType = (FbType)ScriptBytecodeAdapter.castToType(object4, FbType.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[10].callGetProperty(callSiteArray[11].callGetProperty(FbType.class)))) {
                Object object5 = callSiteArray[12].callGetProperty(FbType.class);
                fbType = (FbType)ScriptBytecodeAdapter.castToType(object5, FbType.class);
            } else {
                List list = ScriptBytecodeAdapter.createList(new Object[]{value});
                fbType = (FbType)ScriptBytecodeAdapter.castToType(list, FbType.class);
            }
        }
        return fbType;
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != FbTypeFactory.class) {
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
        stringArray[2] = "BUSY";
        stringArray[3] = "BUSY";
        stringArray[4] = "value";
        stringArray[5] = "BUSY_TENTATIVE";
        stringArray[6] = "BUSY_TENTATIVE";
        stringArray[7] = "value";
        stringArray[8] = "BUSY_UNAVAILABLE";
        stringArray[9] = "BUSY_UNAVAILABLE";
        stringArray[10] = "value";
        stringArray[11] = "FREE";
        stringArray[12] = "FREE";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[13];
        FbTypeFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(FbTypeFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = FbTypeFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

