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
import net.fortuna.ical4j.model.property.BusyType;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class BusyTypeFactory
extends AbstractPropertyFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public BusyTypeFactory() {
        CallSite[] callSiteArray = BusyTypeFactory.$getCallSiteArray();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = BusyTypeFactory.$getCallSiteArray();
        BusyType busyType = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, BusyType.class))) {
            BusyType busyType2;
            busyType = busyType2 = (BusyType)ScriptBytecodeAdapter.castToType(value, BusyType.class);
        } else {
            String busyTypeValue = ShortTypeHandling.castToString(callSiteArray[1].call((Object)attributes, "value"));
            if (ScriptBytecodeAdapter.compareNotEqual(busyTypeValue, null)) {
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call(callSiteArray[3].call(callSiteArray[4].callGetProperty(BusyType.class)), busyTypeValue))) {
                    Object object = callSiteArray[5].callGetProperty(BusyType.class);
                    busyType = (BusyType)ScriptBytecodeAdapter.castToType(object, BusyType.class);
                } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[6].call(callSiteArray[7].call(callSiteArray[8].callGetProperty(BusyType.class)), busyTypeValue))) {
                    Object object = callSiteArray[9].callGetProperty(BusyType.class);
                    busyType = (BusyType)ScriptBytecodeAdapter.castToType(object, BusyType.class);
                } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[10].call(callSiteArray[11].call(callSiteArray[12].callGetProperty(BusyType.class)), busyTypeValue))) {
                    Object object = callSiteArray[13].callGetProperty(BusyType.class);
                    busyType = (BusyType)ScriptBytecodeAdapter.castToType(object, BusyType.class);
                } else {
                    callSiteArray[14].call(attributes, "value", busyTypeValue);
                    Object object = ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractPropertyFactory.class, this, "newInstance", new Object[]{builder, name, value, attributes});
                    busyType = (BusyType)ScriptBytecodeAdapter.castToType(object, BusyType.class);
                }
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[15].call(callSiteArray[16].call(callSiteArray[17].callGetProperty(BusyType.class)), value))) {
                Object object = callSiteArray[18].callGetProperty(BusyType.class);
                busyType = (BusyType)ScriptBytecodeAdapter.castToType(object, BusyType.class);
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[19].call(callSiteArray[20].call(callSiteArray[21].callGetProperty(BusyType.class)), value))) {
                Object object = callSiteArray[22].callGetProperty(BusyType.class);
                busyType = (BusyType)ScriptBytecodeAdapter.castToType(object, BusyType.class);
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[23].call(callSiteArray[24].call(callSiteArray[25].callGetProperty(BusyType.class)), value))) {
                Object object = callSiteArray[26].callGetProperty(BusyType.class);
                busyType = (BusyType)ScriptBytecodeAdapter.castToType(object, BusyType.class);
            } else {
                Object object = ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractPropertyFactory.class, this, "newInstance", new Object[]{builder, name, value, attributes});
                busyType = (BusyType)ScriptBytecodeAdapter.castToType(object, BusyType.class);
            }
        }
        return busyType;
    }

    @Override
    protected Object newInstance(ParameterList parameters, String value) {
        CallSite[] callSiteArray = BusyTypeFactory.$getCallSiteArray();
        return callSiteArray[27].callConstructor(BusyType.class, parameters, value);
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != BusyTypeFactory.class) {
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
        stringArray[4] = "BUSY";
        stringArray[5] = "BUSY";
        stringArray[6] = "equals";
        stringArray[7] = "getValue";
        stringArray[8] = "BUSY_TENTATIVE";
        stringArray[9] = "BUSY_TENTATIVE";
        stringArray[10] = "equals";
        stringArray[11] = "getValue";
        stringArray[12] = "BUSY_UNAVAILABLE";
        stringArray[13] = "BUSY_UNAVAILABLE";
        stringArray[14] = "put";
        stringArray[15] = "equals";
        stringArray[16] = "getValue";
        stringArray[17] = "BUSY";
        stringArray[18] = "BUSY";
        stringArray[19] = "equals";
        stringArray[20] = "getValue";
        stringArray[21] = "BUSY_TENTATIVE";
        stringArray[22] = "BUSY_TENTATIVE";
        stringArray[23] = "equals";
        stringArray[24] = "getValue";
        stringArray[25] = "BUSY_UNAVAILABLE";
        stringArray[26] = "BUSY_UNAVAILABLE";
        stringArray[27] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[28];
        BusyTypeFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(BusyTypeFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = BusyTypeFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

