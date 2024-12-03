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
import net.fortuna.ical4j.model.parameter.Rsvp;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class RsvpFactory
extends AbstractParameterFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public RsvpFactory() {
        CallSite[] callSiteArray = RsvpFactory.$getCallSiteArray();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = RsvpFactory.$getCallSiteArray();
        Rsvp rsvp = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, Rsvp.class))) {
            Object object = value;
            rsvp = (Rsvp)ScriptBytecodeAdapter.castToType(object, Rsvp.class);
        } else {
            Object object = value;
            if (ScriptBytecodeAdapter.isCase(object, callSiteArray[1].callGetProperty(callSiteArray[2].callGetProperty(Rsvp.class)))) {
                Object object2 = callSiteArray[3].callGetProperty(Rsvp.class);
                rsvp = (Rsvp)ScriptBytecodeAdapter.castToType(object2, Rsvp.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[4].callGetProperty(callSiteArray[5].callGetProperty(Rsvp.class)))) {
                Object object3 = callSiteArray[6].callGetProperty(Rsvp.class);
                rsvp = (Rsvp)ScriptBytecodeAdapter.castToType(object3, Rsvp.class);
            } else {
                List list = ScriptBytecodeAdapter.createList(new Object[]{value});
                rsvp = (Rsvp)ScriptBytecodeAdapter.castToType(list, Rsvp.class);
            }
        }
        return rsvp;
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != RsvpFactory.class) {
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
        stringArray[2] = "FALSE";
        stringArray[3] = "FALSE";
        stringArray[4] = "value";
        stringArray[5] = "TRUE";
        stringArray[6] = "TRUE";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[7];
        RsvpFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(RsvpFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = RsvpFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

