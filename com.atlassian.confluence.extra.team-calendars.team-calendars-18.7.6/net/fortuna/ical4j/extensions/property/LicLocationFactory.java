/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.extensions.property;

import groovy.lang.MetaClass;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Map;
import net.fortuna.ical4j.extensions.property.LicLocation;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.property.AbstractPropertyFactory;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class LicLocationFactory
extends AbstractPropertyFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public LicLocationFactory() {
        CallSite[] callSiteArray = LicLocationFactory.$getCallSiteArray();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = LicLocationFactory.$getCallSiteArray();
        LicLocation instance = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, LicLocation.class))) {
            LicLocation licLocation;
            instance = licLocation = (LicLocation)ScriptBytecodeAdapter.castToType(value, LicLocation.class);
        } else {
            String instanceValue = ShortTypeHandling.castToString(callSiteArray[1].call((Object)attributes, "value"));
            if (ScriptBytecodeAdapter.compareNotEqual(instanceValue, null)) {
                callSiteArray[2].call(attributes, "value", instanceValue);
                Object object = ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractPropertyFactory.class, this, "newInstance", new Object[]{builder, name, value, attributes});
                instance = (LicLocation)ScriptBytecodeAdapter.castToType(object, LicLocation.class);
            } else {
                Object object = ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractPropertyFactory.class, this, "newInstance", new Object[]{builder, name, value, attributes});
                instance = (LicLocation)ScriptBytecodeAdapter.castToType(object, LicLocation.class);
            }
        }
        return instance;
    }

    @Override
    protected Object newInstance(ParameterList parameters, String value) {
        CallSite[] callSiteArray = LicLocationFactory.$getCallSiteArray();
        return callSiteArray[3].callConstructor(LicLocation.class, parameters, value);
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != LicLocationFactory.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ Object super$3$newInstance(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2, Map map) {
        return super.newInstance(factoryBuilderSupport, object, object2, map);
    }

    public /* synthetic */ MetaClass super$3$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "checkValueIsTypeNotString";
        stringArray[1] = "remove";
        stringArray[2] = "put";
        stringArray[3] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[4];
        LicLocationFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(LicLocationFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = LicLocationFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

