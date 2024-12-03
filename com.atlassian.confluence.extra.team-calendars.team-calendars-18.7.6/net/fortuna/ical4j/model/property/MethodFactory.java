/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import groovy.lang.MetaClass;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Map;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.property.AbstractPropertyFactory;
import net.fortuna.ical4j.model.property.Method;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class MethodFactory
extends AbstractPropertyFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public MethodFactory() {
        CallSite[] callSiteArray = MethodFactory.$getCallSiteArray();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = MethodFactory.$getCallSiteArray();
        Method instance = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, Method.class))) {
            Method method;
            instance = method = (Method)ScriptBytecodeAdapter.castToType(value, Method.class);
        } else {
            String instanceValue = ShortTypeHandling.castToString(callSiteArray[1].call((Object)attributes, "value"));
            if (ScriptBytecodeAdapter.compareNotEqual(instanceValue, null)) {
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call(callSiteArray[3].call(callSiteArray[4].callGetProperty(Method.class)), instanceValue))) {
                    Object object = callSiteArray[5].callGetProperty(Method.class);
                    instance = (Method)ScriptBytecodeAdapter.castToType(object, Method.class);
                } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[6].call(callSiteArray[7].call(callSiteArray[8].callGetProperty(Method.class)), instanceValue))) {
                    Object object = callSiteArray[9].callGetProperty(Method.class);
                    instance = (Method)ScriptBytecodeAdapter.castToType(object, Method.class);
                } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[10].call(callSiteArray[11].call(callSiteArray[12].callGetProperty(Method.class)), instanceValue))) {
                    Object object = callSiteArray[13].callGetProperty(Method.class);
                    instance = (Method)ScriptBytecodeAdapter.castToType(object, Method.class);
                } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[14].call(callSiteArray[15].call(callSiteArray[16].callGetProperty(Method.class)), instanceValue))) {
                    Object object = callSiteArray[17].callGetProperty(Method.class);
                    instance = (Method)ScriptBytecodeAdapter.castToType(object, Method.class);
                } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[18].call(callSiteArray[19].call(callSiteArray[20].callGetProperty(Method.class)), instanceValue))) {
                    Object object = callSiteArray[21].callGetProperty(Method.class);
                    instance = (Method)ScriptBytecodeAdapter.castToType(object, Method.class);
                } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[22].call(callSiteArray[23].call(callSiteArray[24].callGetProperty(Method.class)), instanceValue))) {
                    Object object = callSiteArray[25].callGetProperty(Method.class);
                    instance = (Method)ScriptBytecodeAdapter.castToType(object, Method.class);
                } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[26].call(callSiteArray[27].call(callSiteArray[28].callGetProperty(Method.class)), instanceValue))) {
                    Object object = callSiteArray[29].callGetProperty(Method.class);
                    instance = (Method)ScriptBytecodeAdapter.castToType(object, Method.class);
                } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[30].call(callSiteArray[31].call(callSiteArray[32].callGetProperty(Method.class)), instanceValue))) {
                    Object object = callSiteArray[33].callGetProperty(Method.class);
                    instance = (Method)ScriptBytecodeAdapter.castToType(object, Method.class);
                } else {
                    callSiteArray[34].call(attributes, "value", instanceValue);
                    Object object = ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractPropertyFactory.class, this, "newInstance", new Object[]{builder, name, value, attributes});
                    instance = (Method)ScriptBytecodeAdapter.castToType(object, Method.class);
                }
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[35].call(callSiteArray[36].call(callSiteArray[37].callGetProperty(Method.class)), value))) {
                Object object = callSiteArray[38].callGetProperty(Method.class);
                instance = (Method)ScriptBytecodeAdapter.castToType(object, Method.class);
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[39].call(callSiteArray[40].call(callSiteArray[41].callGetProperty(Method.class)), value))) {
                Object object = callSiteArray[42].callGetProperty(Method.class);
                instance = (Method)ScriptBytecodeAdapter.castToType(object, Method.class);
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[43].call(callSiteArray[44].call(callSiteArray[45].callGetProperty(Method.class)), value))) {
                Object object = callSiteArray[46].callGetProperty(Method.class);
                instance = (Method)ScriptBytecodeAdapter.castToType(object, Method.class);
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[47].call(callSiteArray[48].call(callSiteArray[49].callGetProperty(Method.class)), value))) {
                Object object = callSiteArray[50].callGetProperty(Method.class);
                instance = (Method)ScriptBytecodeAdapter.castToType(object, Method.class);
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[51].call(callSiteArray[52].call(callSiteArray[53].callGetProperty(Method.class)), value))) {
                Object object = callSiteArray[54].callGetProperty(Method.class);
                instance = (Method)ScriptBytecodeAdapter.castToType(object, Method.class);
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[55].call(callSiteArray[56].call(callSiteArray[57].callGetProperty(Method.class)), value))) {
                Object object = callSiteArray[58].callGetProperty(Method.class);
                instance = (Method)ScriptBytecodeAdapter.castToType(object, Method.class);
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[59].call(callSiteArray[60].call(callSiteArray[61].callGetProperty(Method.class)), value))) {
                Object object = callSiteArray[62].callGetProperty(Method.class);
                instance = (Method)ScriptBytecodeAdapter.castToType(object, Method.class);
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[63].call(callSiteArray[64].call(callSiteArray[65].callGetProperty(Method.class)), value))) {
                Object object = callSiteArray[66].callGetProperty(Method.class);
                instance = (Method)ScriptBytecodeAdapter.castToType(object, Method.class);
            } else {
                Object object = ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractPropertyFactory.class, this, "newInstance", new Object[]{builder, name, value, attributes});
                instance = (Method)ScriptBytecodeAdapter.castToType(object, Method.class);
            }
        }
        return instance;
    }

    @Override
    protected Object newInstance(ParameterList parameters, String value) {
        CallSite[] callSiteArray = MethodFactory.$getCallSiteArray();
        return callSiteArray[67].callConstructor(Method.class, parameters, value);
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != MethodFactory.class) {
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

    public /* synthetic */ Object super$3$newInstance(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2, Map map) {
        return super.newInstance(factoryBuilderSupport, object, object2, map);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "checkValueIsTypeNotString";
        stringArray[1] = "remove";
        stringArray[2] = "equals";
        stringArray[3] = "getValue";
        stringArray[4] = "ADD";
        stringArray[5] = "ADD";
        stringArray[6] = "equals";
        stringArray[7] = "getValue";
        stringArray[8] = "CANCEL";
        stringArray[9] = "CANCEL";
        stringArray[10] = "equals";
        stringArray[11] = "getValue";
        stringArray[12] = "COUNTER";
        stringArray[13] = "COUNTER";
        stringArray[14] = "equals";
        stringArray[15] = "getValue";
        stringArray[16] = "DECLINE_COUNTER";
        stringArray[17] = "DECLINE_COUNTER";
        stringArray[18] = "equals";
        stringArray[19] = "getValue";
        stringArray[20] = "PUBLISH";
        stringArray[21] = "PUBLISH";
        stringArray[22] = "equals";
        stringArray[23] = "getValue";
        stringArray[24] = "REFRESH";
        stringArray[25] = "REFRESH";
        stringArray[26] = "equals";
        stringArray[27] = "getValue";
        stringArray[28] = "REPLY";
        stringArray[29] = "REPLY";
        stringArray[30] = "equals";
        stringArray[31] = "getValue";
        stringArray[32] = "REQUEST";
        stringArray[33] = "REQUEST";
        stringArray[34] = "put";
        stringArray[35] = "equals";
        stringArray[36] = "getValue";
        stringArray[37] = "ADD";
        stringArray[38] = "ADD";
        stringArray[39] = "equals";
        stringArray[40] = "getValue";
        stringArray[41] = "CANCEL";
        stringArray[42] = "CANCEL";
        stringArray[43] = "equals";
        stringArray[44] = "getValue";
        stringArray[45] = "COUNTER";
        stringArray[46] = "COUNTER";
        stringArray[47] = "equals";
        stringArray[48] = "getValue";
        stringArray[49] = "DECLINE_COUNTER";
        stringArray[50] = "DECLINE_COUNTER";
        stringArray[51] = "equals";
        stringArray[52] = "getValue";
        stringArray[53] = "PUBLISH";
        stringArray[54] = "PUBLISH";
        stringArray[55] = "equals";
        stringArray[56] = "getValue";
        stringArray[57] = "REFRESH";
        stringArray[58] = "REFRESH";
        stringArray[59] = "equals";
        stringArray[60] = "getValue";
        stringArray[61] = "REPLY";
        stringArray[62] = "REPLY";
        stringArray[63] = "equals";
        stringArray[64] = "getValue";
        stringArray[65] = "REQUEST";
        stringArray[66] = "REQUEST";
        stringArray[67] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[68];
        MethodFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(MethodFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = MethodFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

