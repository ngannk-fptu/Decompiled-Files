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
import net.fortuna.ical4j.model.parameter.Value;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class ValueFactory
extends AbstractParameterFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ValueFactory() {
        CallSite[] callSiteArray = ValueFactory.$getCallSiteArray();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = ValueFactory.$getCallSiteArray();
        Value valueParam = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, Value.class))) {
            Object object = value;
            valueParam = (Value)ScriptBytecodeAdapter.castToType(object, Value.class);
        } else {
            Object object = value;
            if (ScriptBytecodeAdapter.isCase(object, callSiteArray[1].callGetProperty(callSiteArray[2].callGetProperty(Value.class)))) {
                Object object2 = callSiteArray[3].callGetProperty(Value.class);
                valueParam = (Value)ScriptBytecodeAdapter.castToType(object2, Value.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[4].callGetProperty(callSiteArray[5].callGetProperty(Value.class)))) {
                Object object3 = callSiteArray[6].callGetProperty(Value.class);
                valueParam = (Value)ScriptBytecodeAdapter.castToType(object3, Value.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[7].callGetProperty(callSiteArray[8].callGetProperty(Value.class)))) {
                Object object4 = callSiteArray[9].callGetProperty(Value.class);
                valueParam = (Value)ScriptBytecodeAdapter.castToType(object4, Value.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[10].callGetProperty(callSiteArray[11].callGetProperty(Value.class)))) {
                Object object5 = callSiteArray[12].callGetProperty(Value.class);
                valueParam = (Value)ScriptBytecodeAdapter.castToType(object5, Value.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[13].callGetProperty(callSiteArray[14].callGetProperty(Value.class)))) {
                Object object6 = callSiteArray[15].callGetProperty(Value.class);
                valueParam = (Value)ScriptBytecodeAdapter.castToType(object6, Value.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[16].callGetProperty(callSiteArray[17].callGetProperty(Value.class)))) {
                Object object7 = callSiteArray[18].callGetProperty(Value.class);
                valueParam = (Value)ScriptBytecodeAdapter.castToType(object7, Value.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[19].callGetProperty(callSiteArray[20].callGetProperty(Value.class)))) {
                Object object8 = callSiteArray[21].callGetProperty(Value.class);
                valueParam = (Value)ScriptBytecodeAdapter.castToType(object8, Value.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[22].callGetProperty(callSiteArray[23].callGetProperty(Value.class)))) {
                Object object9 = callSiteArray[24].callGetProperty(Value.class);
                valueParam = (Value)ScriptBytecodeAdapter.castToType(object9, Value.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[25].callGetProperty(callSiteArray[26].callGetProperty(Value.class)))) {
                Object object10 = callSiteArray[27].callGetProperty(Value.class);
                valueParam = (Value)ScriptBytecodeAdapter.castToType(object10, Value.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[28].callGetProperty(callSiteArray[29].callGetProperty(Value.class)))) {
                Object object11 = callSiteArray[30].callGetProperty(Value.class);
                valueParam = (Value)ScriptBytecodeAdapter.castToType(object11, Value.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[31].callGetProperty(callSiteArray[32].callGetProperty(Value.class)))) {
                Object object12 = callSiteArray[33].callGetProperty(Value.class);
                valueParam = (Value)ScriptBytecodeAdapter.castToType(object12, Value.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[34].callGetProperty(callSiteArray[35].callGetProperty(Value.class)))) {
                Object object13 = callSiteArray[36].callGetProperty(Value.class);
                valueParam = (Value)ScriptBytecodeAdapter.castToType(object13, Value.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[37].callGetProperty(callSiteArray[38].callGetProperty(Value.class)))) {
                Object object14 = callSiteArray[39].callGetProperty(Value.class);
                valueParam = (Value)ScriptBytecodeAdapter.castToType(object14, Value.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[40].callGetProperty(callSiteArray[41].callGetProperty(Value.class)))) {
                Object object15 = callSiteArray[42].callGetProperty(Value.class);
                valueParam = (Value)ScriptBytecodeAdapter.castToType(object15, Value.class);
            } else {
                List list = ScriptBytecodeAdapter.createList(new Object[]{value});
                valueParam = (Value)ScriptBytecodeAdapter.castToType(list, Value.class);
            }
        }
        return valueParam;
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ValueFactory.class) {
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
        stringArray[2] = "BINARY";
        stringArray[3] = "BINARY";
        stringArray[4] = "value";
        stringArray[5] = "BOOLEAN";
        stringArray[6] = "BOOLEAN";
        stringArray[7] = "value";
        stringArray[8] = "CAL_ADDRESS";
        stringArray[9] = "CAL_ADDRESS";
        stringArray[10] = "value";
        stringArray[11] = "DATE";
        stringArray[12] = "DATE";
        stringArray[13] = "value";
        stringArray[14] = "DATE_TIME";
        stringArray[15] = "DATE_TIME";
        stringArray[16] = "value";
        stringArray[17] = "DURATION";
        stringArray[18] = "DURATION";
        stringArray[19] = "value";
        stringArray[20] = "FLOAT";
        stringArray[21] = "FLOAT";
        stringArray[22] = "value";
        stringArray[23] = "INTEGER";
        stringArray[24] = "INTEGER";
        stringArray[25] = "value";
        stringArray[26] = "PERIOD";
        stringArray[27] = "PERIOD";
        stringArray[28] = "value";
        stringArray[29] = "RECUR";
        stringArray[30] = "RECUR";
        stringArray[31] = "value";
        stringArray[32] = "TEXT";
        stringArray[33] = "TEXT";
        stringArray[34] = "value";
        stringArray[35] = "TIME";
        stringArray[36] = "TIME";
        stringArray[37] = "value";
        stringArray[38] = "URI";
        stringArray[39] = "URI";
        stringArray[40] = "value";
        stringArray[41] = "UTC_OFFSET";
        stringArray[42] = "UTC_OFFSET";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[43];
        ValueFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ValueFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ValueFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

