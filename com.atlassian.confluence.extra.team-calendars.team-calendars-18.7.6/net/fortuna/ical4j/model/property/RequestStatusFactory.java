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
import net.fortuna.ical4j.model.property.RequestStatus;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class RequestStatusFactory
extends AbstractPropertyFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public RequestStatusFactory() {
        CallSite[] callSiteArray = RequestStatusFactory.$getCallSiteArray();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = RequestStatusFactory.$getCallSiteArray();
        RequestStatus instance = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, RequestStatus.class))) {
            RequestStatus requestStatus;
            instance = requestStatus = (RequestStatus)ScriptBytecodeAdapter.castToType(value, RequestStatus.class);
        } else {
            String instanceValue = ShortTypeHandling.castToString(callSiteArray[1].call((Object)attributes, "value"));
            if (ScriptBytecodeAdapter.compareNotEqual(instanceValue, null)) {
                callSiteArray[2].call(attributes, "value", instanceValue);
                Object object = ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractPropertyFactory.class, this, "newInstance", new Object[]{builder, name, value, attributes});
                instance = (RequestStatus)ScriptBytecodeAdapter.castToType(object, RequestStatus.class);
            } else {
                Object object = ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractPropertyFactory.class, this, "newInstance", new Object[]{builder, name, value, attributes});
                instance = (RequestStatus)ScriptBytecodeAdapter.castToType(object, RequestStatus.class);
            }
        }
        return instance;
    }

    @Override
    protected Object newInstance(ParameterList parameters, String value) {
        CallSite[] callSiteArray = RequestStatusFactory.$getCallSiteArray();
        return callSiteArray[3].callConstructor(RequestStatus.class, parameters, value);
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != RequestStatusFactory.class) {
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
        stringArray[2] = "put";
        stringArray[3] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[4];
        RequestStatusFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(RequestStatusFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = RequestStatusFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

