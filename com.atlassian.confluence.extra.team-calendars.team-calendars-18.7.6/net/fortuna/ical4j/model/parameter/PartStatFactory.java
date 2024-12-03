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
import net.fortuna.ical4j.model.parameter.PartStat;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class PartStatFactory
extends AbstractParameterFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public PartStatFactory() {
        CallSite[] callSiteArray = PartStatFactory.$getCallSiteArray();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = PartStatFactory.$getCallSiteArray();
        PartStat partStat = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, PartStat.class))) {
            Object object = value;
            partStat = (PartStat)ScriptBytecodeAdapter.castToType(object, PartStat.class);
        } else {
            Object object = value;
            if (ScriptBytecodeAdapter.isCase(object, callSiteArray[1].callGetProperty(callSiteArray[2].callGetProperty(PartStat.class)))) {
                Object object2 = callSiteArray[3].callGetProperty(PartStat.class);
                partStat = (PartStat)ScriptBytecodeAdapter.castToType(object2, PartStat.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[4].callGetProperty(callSiteArray[5].callGetProperty(PartStat.class)))) {
                Object object3 = callSiteArray[6].callGetProperty(PartStat.class);
                partStat = (PartStat)ScriptBytecodeAdapter.castToType(object3, PartStat.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[7].callGetProperty(callSiteArray[8].callGetProperty(PartStat.class)))) {
                Object object4 = callSiteArray[9].callGetProperty(PartStat.class);
                partStat = (PartStat)ScriptBytecodeAdapter.castToType(object4, PartStat.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[10].callGetProperty(callSiteArray[11].callGetProperty(PartStat.class)))) {
                Object object5 = callSiteArray[12].callGetProperty(PartStat.class);
                partStat = (PartStat)ScriptBytecodeAdapter.castToType(object5, PartStat.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[13].callGetProperty(callSiteArray[14].callGetProperty(PartStat.class)))) {
                Object object6 = callSiteArray[15].callGetProperty(PartStat.class);
                partStat = (PartStat)ScriptBytecodeAdapter.castToType(object6, PartStat.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[16].callGetProperty(callSiteArray[17].callGetProperty(PartStat.class)))) {
                Object object7 = callSiteArray[18].callGetProperty(PartStat.class);
                partStat = (PartStat)ScriptBytecodeAdapter.castToType(object7, PartStat.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[19].callGetProperty(callSiteArray[20].callGetProperty(PartStat.class)))) {
                Object object8 = callSiteArray[21].callGetProperty(PartStat.class);
                partStat = (PartStat)ScriptBytecodeAdapter.castToType(object8, PartStat.class);
            } else {
                List list = ScriptBytecodeAdapter.createList(new Object[]{value});
                partStat = (PartStat)ScriptBytecodeAdapter.castToType(list, PartStat.class);
            }
        }
        return partStat;
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != PartStatFactory.class) {
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
        stringArray[2] = "ACCEPTED";
        stringArray[3] = "ACCEPTED";
        stringArray[4] = "value";
        stringArray[5] = "COMPLETED";
        stringArray[6] = "COMPLETED";
        stringArray[7] = "value";
        stringArray[8] = "DECLINED";
        stringArray[9] = "DECLINED";
        stringArray[10] = "value";
        stringArray[11] = "DELEGATED";
        stringArray[12] = "DELEGATED";
        stringArray[13] = "value";
        stringArray[14] = "IN_PROCESS";
        stringArray[15] = "IN_PROCESS";
        stringArray[16] = "value";
        stringArray[17] = "NEEDS_ACTION";
        stringArray[18] = "NEEDS_ACTION";
        stringArray[19] = "value";
        stringArray[20] = "TENTATIVE";
        stringArray[21] = "TENTATIVE";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[22];
        PartStatFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(PartStatFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = PartStatFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

