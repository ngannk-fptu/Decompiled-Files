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
import net.fortuna.ical4j.model.property.Priority;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class PriorityFactory
extends AbstractPropertyFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public PriorityFactory() {
        CallSite[] callSiteArray = PriorityFactory.$getCallSiteArray();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = PriorityFactory.$getCallSiteArray();
        Priority instance = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, Priority.class))) {
            Priority priority;
            instance = priority = (Priority)ScriptBytecodeAdapter.castToType(value, Priority.class);
        } else {
            String instanceValue = ShortTypeHandling.castToString(callSiteArray[1].call((Object)attributes, "value"));
            if (ScriptBytecodeAdapter.compareNotEqual(instanceValue, null)) {
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call(callSiteArray[3].call(callSiteArray[4].callGetProperty(Priority.class)), instanceValue))) {
                    Object object = callSiteArray[5].callGetProperty(Priority.class);
                    instance = (Priority)ScriptBytecodeAdapter.castToType(object, Priority.class);
                } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[6].call(callSiteArray[7].call(callSiteArray[8].callGetProperty(Priority.class)), instanceValue))) {
                    Object object = callSiteArray[9].callGetProperty(Priority.class);
                    instance = (Priority)ScriptBytecodeAdapter.castToType(object, Priority.class);
                } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[10].call(callSiteArray[11].call(callSiteArray[12].callGetProperty(Priority.class)), instanceValue))) {
                    Object object = callSiteArray[13].callGetProperty(Priority.class);
                    instance = (Priority)ScriptBytecodeAdapter.castToType(object, Priority.class);
                } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[14].call(callSiteArray[15].call(callSiteArray[16].callGetProperty(Priority.class)), instanceValue))) {
                    Object object = callSiteArray[17].callGetProperty(Priority.class);
                    instance = (Priority)ScriptBytecodeAdapter.castToType(object, Priority.class);
                } else {
                    callSiteArray[18].call(attributes, "value", instanceValue);
                    Object object = ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractPropertyFactory.class, this, "newInstance", new Object[]{builder, name, value, attributes});
                    instance = (Priority)ScriptBytecodeAdapter.castToType(object, Priority.class);
                }
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[19].call(callSiteArray[20].call(callSiteArray[21].callGetProperty(Priority.class)), value))) {
                Object object = callSiteArray[22].callGetProperty(Priority.class);
                instance = (Priority)ScriptBytecodeAdapter.castToType(object, Priority.class);
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[23].call(callSiteArray[24].call(callSiteArray[25].callGetProperty(Priority.class)), value))) {
                Object object = callSiteArray[26].callGetProperty(Priority.class);
                instance = (Priority)ScriptBytecodeAdapter.castToType(object, Priority.class);
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[27].call(callSiteArray[28].call(callSiteArray[29].callGetProperty(Priority.class)), value))) {
                Object object = callSiteArray[30].callGetProperty(Priority.class);
                instance = (Priority)ScriptBytecodeAdapter.castToType(object, Priority.class);
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[31].call(callSiteArray[32].call(callSiteArray[33].callGetProperty(Priority.class)), value))) {
                Object object = callSiteArray[34].callGetProperty(Priority.class);
                instance = (Priority)ScriptBytecodeAdapter.castToType(object, Priority.class);
            } else {
                Object object = ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractPropertyFactory.class, this, "newInstance", new Object[]{builder, name, value, attributes});
                instance = (Priority)ScriptBytecodeAdapter.castToType(object, Priority.class);
            }
        }
        return instance;
    }

    @Override
    protected Object newInstance(ParameterList parameters, String value) {
        CallSite[] callSiteArray = PriorityFactory.$getCallSiteArray();
        return callSiteArray[35].callConstructor(Priority.class, parameters, value);
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != PriorityFactory.class) {
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
        stringArray[4] = "HIGH";
        stringArray[5] = "HIGH";
        stringArray[6] = "equals";
        stringArray[7] = "getValue";
        stringArray[8] = "MEDIUM";
        stringArray[9] = "MEDIUM";
        stringArray[10] = "equals";
        stringArray[11] = "getValue";
        stringArray[12] = "LOW";
        stringArray[13] = "LOW";
        stringArray[14] = "equals";
        stringArray[15] = "getValue";
        stringArray[16] = "UNDEFINED";
        stringArray[17] = "UNDEFINED";
        stringArray[18] = "put";
        stringArray[19] = "equals";
        stringArray[20] = "getValue";
        stringArray[21] = "HIGH";
        stringArray[22] = "HIGH";
        stringArray[23] = "equals";
        stringArray[24] = "getValue";
        stringArray[25] = "MEDIUM";
        stringArray[26] = "MEDIUM";
        stringArray[27] = "equals";
        stringArray[28] = "getValue";
        stringArray[29] = "LOW";
        stringArray[30] = "LOW";
        stringArray[31] = "equals";
        stringArray[32] = "getValue";
        stringArray[33] = "UNDEFINED";
        stringArray[34] = "UNDEFINED";
        stringArray[35] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[36];
        PriorityFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(PriorityFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = PriorityFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

