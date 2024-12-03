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
import net.fortuna.ical4j.model.parameter.Role;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class RoleFactory
extends AbstractParameterFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public RoleFactory() {
        CallSite[] callSiteArray = RoleFactory.$getCallSiteArray();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = RoleFactory.$getCallSiteArray();
        Role role = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, Role.class))) {
            Object object = value;
            role = (Role)ScriptBytecodeAdapter.castToType(object, Role.class);
        } else {
            Object object = value;
            if (ScriptBytecodeAdapter.isCase(object, callSiteArray[1].callGetProperty(callSiteArray[2].callGetProperty(Role.class)))) {
                Object object2 = callSiteArray[3].callGetProperty(Role.class);
                role = (Role)ScriptBytecodeAdapter.castToType(object2, Role.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[4].callGetProperty(callSiteArray[5].callGetProperty(Role.class)))) {
                Object object3 = callSiteArray[6].callGetProperty(Role.class);
                role = (Role)ScriptBytecodeAdapter.castToType(object3, Role.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[7].callGetProperty(callSiteArray[8].callGetProperty(Role.class)))) {
                Object object4 = callSiteArray[9].callGetProperty(Role.class);
                role = (Role)ScriptBytecodeAdapter.castToType(object4, Role.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[10].callGetProperty(callSiteArray[11].callGetProperty(Role.class)))) {
                Object object5 = callSiteArray[12].callGetProperty(Role.class);
                role = (Role)ScriptBytecodeAdapter.castToType(object5, Role.class);
            } else {
                List list = ScriptBytecodeAdapter.createList(new Object[]{value});
                role = (Role)ScriptBytecodeAdapter.castToType(list, Role.class);
            }
        }
        return role;
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != RoleFactory.class) {
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
        stringArray[2] = "CHAIR";
        stringArray[3] = "CHAIR";
        stringArray[4] = "value";
        stringArray[5] = "NON_PARTICIPANT";
        stringArray[6] = "NON_PARTICIPANT";
        stringArray[7] = "value";
        stringArray[8] = "OPT_PARTICIPANT";
        stringArray[9] = "OPT_PARTICIPANT";
        stringArray[10] = "value";
        stringArray[11] = "REQ_PARTICIPANT";
        stringArray[12] = "REQ_PARTICIPANT";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[13];
        RoleFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(RoleFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = RoleFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

