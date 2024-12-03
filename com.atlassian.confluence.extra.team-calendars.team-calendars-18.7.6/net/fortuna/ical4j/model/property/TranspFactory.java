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
import net.fortuna.ical4j.model.property.Transp;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class TranspFactory
extends AbstractPropertyFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public TranspFactory() {
        CallSite[] callSiteArray = TranspFactory.$getCallSiteArray();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = TranspFactory.$getCallSiteArray();
        Transp instance = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, Transp.class))) {
            Transp transp;
            instance = transp = (Transp)ScriptBytecodeAdapter.castToType(value, Transp.class);
        } else {
            String instanceValue = ShortTypeHandling.castToString(callSiteArray[1].call((Object)attributes, "value"));
            if (ScriptBytecodeAdapter.compareNotEqual(instanceValue, null)) {
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call(callSiteArray[3].call(callSiteArray[4].callGetProperty(Transp.class)), instanceValue))) {
                    Object object = callSiteArray[5].callGetProperty(Transp.class);
                    instance = (Transp)ScriptBytecodeAdapter.castToType(object, Transp.class);
                } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[6].call(callSiteArray[7].call(callSiteArray[8].callGetProperty(Transp.class)), instanceValue))) {
                    Object object = callSiteArray[9].callGetProperty(Transp.class);
                    instance = (Transp)ScriptBytecodeAdapter.castToType(object, Transp.class);
                } else {
                    callSiteArray[10].call(attributes, "value", instanceValue);
                    Object object = ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractPropertyFactory.class, this, "newInstance", new Object[]{builder, name, value, attributes});
                    instance = (Transp)ScriptBytecodeAdapter.castToType(object, Transp.class);
                }
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[11].call(callSiteArray[12].call(callSiteArray[13].callGetProperty(Transp.class)), value))) {
                Object object = callSiteArray[14].callGetProperty(Transp.class);
                instance = (Transp)ScriptBytecodeAdapter.castToType(object, Transp.class);
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[15].call(callSiteArray[16].call(callSiteArray[17].callGetProperty(Transp.class)), value))) {
                Object object = callSiteArray[18].callGetProperty(Transp.class);
                instance = (Transp)ScriptBytecodeAdapter.castToType(object, Transp.class);
            } else {
                Object object = ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractPropertyFactory.class, this, "newInstance", new Object[]{builder, name, value, attributes});
                instance = (Transp)ScriptBytecodeAdapter.castToType(object, Transp.class);
            }
        }
        return instance;
    }

    @Override
    protected Object newInstance(ParameterList parameters, String value) {
        CallSite[] callSiteArray = TranspFactory.$getCallSiteArray();
        return callSiteArray[19].callConstructor(Transp.class, parameters, value);
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != TranspFactory.class) {
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
        stringArray[4] = "OPAQUE";
        stringArray[5] = "OPAQUE";
        stringArray[6] = "equals";
        stringArray[7] = "getValue";
        stringArray[8] = "TRANSPARENT";
        stringArray[9] = "TRANSPARENT";
        stringArray[10] = "put";
        stringArray[11] = "equals";
        stringArray[12] = "getValue";
        stringArray[13] = "OPAQUE";
        stringArray[14] = "OPAQUE";
        stringArray[15] = "equals";
        stringArray[16] = "getValue";
        stringArray[17] = "TRANSPARENT";
        stringArray[18] = "TRANSPARENT";
        stringArray[19] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[20];
        TranspFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(TranspFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = TranspFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

