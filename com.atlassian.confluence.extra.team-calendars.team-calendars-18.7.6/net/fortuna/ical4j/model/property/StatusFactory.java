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
import net.fortuna.ical4j.model.property.Status;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class StatusFactory
extends AbstractPropertyFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public StatusFactory() {
        CallSite[] callSiteArray = StatusFactory.$getCallSiteArray();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = StatusFactory.$getCallSiteArray();
        Status instance = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, Status.class))) {
            Status status;
            instance = status = (Status)ScriptBytecodeAdapter.castToType(value, Status.class);
        } else {
            String instanceValue = ShortTypeHandling.castToString(callSiteArray[1].call((Object)attributes, "value"));
            if (ScriptBytecodeAdapter.compareNotEqual(instanceValue, null)) {
                String string = instanceValue;
                if (ScriptBytecodeAdapter.isCase(string, callSiteArray[2].callGetProperty(callSiteArray[3].callGetProperty(Status.class)))) {
                    Object object = callSiteArray[4].callGetProperty(Status.class);
                    instance = (Status)ScriptBytecodeAdapter.castToType(object, Status.class);
                } else if (ScriptBytecodeAdapter.isCase(string, callSiteArray[5].callGetProperty(callSiteArray[6].callGetProperty(Status.class)))) {
                    Object object = callSiteArray[7].callGetProperty(Status.class);
                    instance = (Status)ScriptBytecodeAdapter.castToType(object, Status.class);
                } else if (ScriptBytecodeAdapter.isCase(string, callSiteArray[8].callGetProperty(callSiteArray[9].callGetProperty(Status.class)))) {
                    Object object = callSiteArray[10].callGetProperty(Status.class);
                    instance = (Status)ScriptBytecodeAdapter.castToType(object, Status.class);
                } else if (ScriptBytecodeAdapter.isCase(string, callSiteArray[11].callGetProperty(callSiteArray[12].callGetProperty(Status.class)))) {
                    Object object = callSiteArray[13].callGetProperty(Status.class);
                    instance = (Status)ScriptBytecodeAdapter.castToType(object, Status.class);
                } else if (ScriptBytecodeAdapter.isCase(string, callSiteArray[14].callGetProperty(callSiteArray[15].callGetProperty(Status.class)))) {
                    Object object = callSiteArray[16].callGetProperty(Status.class);
                    instance = (Status)ScriptBytecodeAdapter.castToType(object, Status.class);
                } else if (ScriptBytecodeAdapter.isCase(string, callSiteArray[17].callGetProperty(callSiteArray[18].callGetProperty(Status.class)))) {
                    Object object = callSiteArray[19].callGetProperty(Status.class);
                    instance = (Status)ScriptBytecodeAdapter.castToType(object, Status.class);
                } else if (ScriptBytecodeAdapter.isCase(string, callSiteArray[20].callGetProperty(callSiteArray[21].callGetProperty(Status.class)))) {
                    Object object = callSiteArray[22].callGetProperty(Status.class);
                    instance = (Status)ScriptBytecodeAdapter.castToType(object, Status.class);
                } else if (ScriptBytecodeAdapter.isCase(string, callSiteArray[23].callGetProperty(callSiteArray[24].callGetProperty(Status.class)))) {
                    Object object = callSiteArray[25].callGetProperty(Status.class);
                    instance = (Status)ScriptBytecodeAdapter.castToType(object, Status.class);
                } else if (ScriptBytecodeAdapter.isCase(string, callSiteArray[26].callGetProperty(callSiteArray[27].callGetProperty(Status.class)))) {
                    Object object = callSiteArray[28].callGetProperty(Status.class);
                    instance = (Status)ScriptBytecodeAdapter.castToType(object, Status.class);
                } else if (ScriptBytecodeAdapter.isCase(string, callSiteArray[29].callGetProperty(callSiteArray[30].callGetProperty(Status.class)))) {
                    Object object = callSiteArray[31].callGetProperty(Status.class);
                    instance = (Status)ScriptBytecodeAdapter.castToType(object, Status.class);
                } else {
                    callSiteArray[32].call(attributes, "value", instanceValue);
                    Object object = ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractPropertyFactory.class, this, "newInstance", new Object[]{builder, name, value, attributes});
                    instance = (Status)ScriptBytecodeAdapter.castToType(object, Status.class);
                }
            } else {
                Object object = value;
                if (ScriptBytecodeAdapter.isCase(object, callSiteArray[33].callGetProperty(callSiteArray[34].callGetProperty(Status.class)))) {
                    Object object2 = callSiteArray[35].callGetProperty(Status.class);
                    instance = (Status)ScriptBytecodeAdapter.castToType(object2, Status.class);
                } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[36].callGetProperty(callSiteArray[37].callGetProperty(Status.class)))) {
                    Object object3 = callSiteArray[38].callGetProperty(Status.class);
                    instance = (Status)ScriptBytecodeAdapter.castToType(object3, Status.class);
                } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[39].callGetProperty(callSiteArray[40].callGetProperty(Status.class)))) {
                    Object object4 = callSiteArray[41].callGetProperty(Status.class);
                    instance = (Status)ScriptBytecodeAdapter.castToType(object4, Status.class);
                } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[42].callGetProperty(callSiteArray[43].callGetProperty(Status.class)))) {
                    Object object5 = callSiteArray[44].callGetProperty(Status.class);
                    instance = (Status)ScriptBytecodeAdapter.castToType(object5, Status.class);
                } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[45].callGetProperty(callSiteArray[46].callGetProperty(Status.class)))) {
                    Object object6 = callSiteArray[47].callGetProperty(Status.class);
                    instance = (Status)ScriptBytecodeAdapter.castToType(object6, Status.class);
                } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[48].callGetProperty(callSiteArray[49].callGetProperty(Status.class)))) {
                    Object object7 = callSiteArray[50].callGetProperty(Status.class);
                    instance = (Status)ScriptBytecodeAdapter.castToType(object7, Status.class);
                } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[51].callGetProperty(callSiteArray[52].callGetProperty(Status.class)))) {
                    Object object8 = callSiteArray[53].callGetProperty(Status.class);
                    instance = (Status)ScriptBytecodeAdapter.castToType(object8, Status.class);
                } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[54].callGetProperty(callSiteArray[55].callGetProperty(Status.class)))) {
                    Object object9 = callSiteArray[56].callGetProperty(Status.class);
                    instance = (Status)ScriptBytecodeAdapter.castToType(object9, Status.class);
                } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[57].callGetProperty(callSiteArray[58].callGetProperty(Status.class)))) {
                    Object object10 = callSiteArray[59].callGetProperty(Status.class);
                    instance = (Status)ScriptBytecodeAdapter.castToType(object10, Status.class);
                } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[60].callGetProperty(callSiteArray[61].callGetProperty(Status.class)))) {
                    Object object11 = callSiteArray[62].callGetProperty(Status.class);
                    instance = (Status)ScriptBytecodeAdapter.castToType(object11, Status.class);
                } else {
                    Object object12 = ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractPropertyFactory.class, this, "newInstance", new Object[]{builder, name, value, attributes});
                    instance = (Status)ScriptBytecodeAdapter.castToType(object12, Status.class);
                }
            }
        }
        return instance;
    }

    @Override
    protected Object newInstance(ParameterList parameters, String value) {
        CallSite[] callSiteArray = StatusFactory.$getCallSiteArray();
        return callSiteArray[63].callConstructor(Status.class, parameters, value);
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != StatusFactory.class) {
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
        stringArray[2] = "value";
        stringArray[3] = "VEVENT_CANCELLED";
        stringArray[4] = "VEVENT_CANCELLED";
        stringArray[5] = "value";
        stringArray[6] = "VEVENT_CONFIRMED";
        stringArray[7] = "VEVENT_CONFIRMED";
        stringArray[8] = "value";
        stringArray[9] = "VEVENT_TENTATIVE";
        stringArray[10] = "VEVENT_TENTATIVE";
        stringArray[11] = "value";
        stringArray[12] = "VJOURNAL_CANCELLED";
        stringArray[13] = "VJOURNAL_CANCELLED";
        stringArray[14] = "value";
        stringArray[15] = "VJOURNAL_DRAFT";
        stringArray[16] = "VJOURNAL_DRAFT";
        stringArray[17] = "value";
        stringArray[18] = "VJOURNAL_FINAL";
        stringArray[19] = "VJOURNAL_FINAL";
        stringArray[20] = "value";
        stringArray[21] = "VTODO_CANCELLED";
        stringArray[22] = "VTODO_CANCELLED";
        stringArray[23] = "value";
        stringArray[24] = "VTODO_COMPLETED";
        stringArray[25] = "VTODO_COMPLETED";
        stringArray[26] = "value";
        stringArray[27] = "VTODO_IN_PROCESS";
        stringArray[28] = "VTODO_IN_PROCESS";
        stringArray[29] = "value";
        stringArray[30] = "VTODO_NEEDS_ACTION";
        stringArray[31] = "VTODO_NEEDS_ACTION";
        stringArray[32] = "put";
        stringArray[33] = "value";
        stringArray[34] = "VEVENT_CANCELLED";
        stringArray[35] = "VEVENT_CANCELLED";
        stringArray[36] = "value";
        stringArray[37] = "VEVENT_CONFIRMED";
        stringArray[38] = "VEVENT_CONFIRMED";
        stringArray[39] = "value";
        stringArray[40] = "VEVENT_TENTATIVE";
        stringArray[41] = "VEVENT_TENTATIVE";
        stringArray[42] = "value";
        stringArray[43] = "VJOURNAL_CANCELLED";
        stringArray[44] = "VJOURNAL_CANCELLED";
        stringArray[45] = "value";
        stringArray[46] = "VJOURNAL_DRAFT";
        stringArray[47] = "VJOURNAL_DRAFT";
        stringArray[48] = "value";
        stringArray[49] = "VJOURNAL_FINAL";
        stringArray[50] = "VJOURNAL_FINAL";
        stringArray[51] = "value";
        stringArray[52] = "VTODO_CANCELLED";
        stringArray[53] = "VTODO_CANCELLED";
        stringArray[54] = "value";
        stringArray[55] = "VTODO_COMPLETED";
        stringArray[56] = "VTODO_COMPLETED";
        stringArray[57] = "value";
        stringArray[58] = "VTODO_IN_PROCESS";
        stringArray[59] = "VTODO_IN_PROCESS";
        stringArray[60] = "value";
        stringArray[61] = "VTODO_NEEDS_ACTION";
        stringArray[62] = "VTODO_NEEDS_ACTION";
        stringArray[63] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[64];
        StatusFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(StatusFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = StatusFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

