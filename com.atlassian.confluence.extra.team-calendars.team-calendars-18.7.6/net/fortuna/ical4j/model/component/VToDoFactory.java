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
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VToDo;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class VToDoFactory
extends AbstractComponentFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public VToDoFactory() {
        CallSite[] callSiteArray = VToDoFactory.$getCallSiteArray();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = VToDoFactory.$getCallSiteArray();
        VToDo toDo = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, VToDo.class))) {
            VToDo vToDo;
            toDo = vToDo = (VToDo)ScriptBytecodeAdapter.castToType(value, VToDo.class);
        } else {
            Object object = ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractComponentFactory.class, this, "newInstance", new Object[]{builder, name, value, attributes});
            toDo = (VToDo)ScriptBytecodeAdapter.castToType(object, VToDo.class);
        }
        return toDo;
    }

    @Override
    protected Object newInstance(PropertyList properties) {
        CallSite[] callSiteArray = VToDoFactory.$getCallSiteArray();
        return callSiteArray[1].callConstructor(VToDo.class, properties);
    }

    @Override
    public void setChild(FactoryBuilderSupport build, Object parent, Object child) {
        CallSite[] callSiteArray = VToDoFactory.$getCallSiteArray();
        if (child instanceof VAlarm) {
            callSiteArray[2].call(callSiteArray[3].callGetProperty(parent), child);
        } else {
            ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractComponentFactory.class, this, "setChild", new Object[]{build, parent, child});
        }
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != VToDoFactory.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ void super$3$setChild(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2) {
        super.setChild(factoryBuilderSupport, object, object2);
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
        stringArray[2] = "add";
        stringArray[3] = "alarms";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[4];
        VToDoFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(VToDoFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = VToDoFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

