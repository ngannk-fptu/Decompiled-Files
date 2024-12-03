/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.component;

import groovy.lang.MetaClass;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Map;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.AbstractComponentFactory;
import net.fortuna.ical4j.model.component.Daylight;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class DaylightFactory
extends AbstractComponentFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public DaylightFactory() {
        CallSite[] callSiteArray = DaylightFactory.$getCallSiteArray();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = DaylightFactory.$getCallSiteArray();
        Daylight daylight = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, Daylight.class))) {
            Daylight daylight2;
            daylight = daylight2 = (Daylight)ScriptBytecodeAdapter.castToType(value, Daylight.class);
        } else {
            Object object = ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractComponentFactory.class, this, "newInstance", new Object[]{builder, name, value, attributes});
            daylight = (Daylight)ScriptBytecodeAdapter.castToType(object, Daylight.class);
        }
        return daylight;
    }

    @Override
    protected Object newInstance(PropertyList properties) {
        CallSite[] callSiteArray = DaylightFactory.$getCallSiteArray();
        return callSiteArray[1].callConstructor(Daylight.class, properties);
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != DaylightFactory.class) {
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
        stringArray[0] = "checkValueIsType";
        stringArray[1] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[2];
        DaylightFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(DaylightFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = DaylightFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

