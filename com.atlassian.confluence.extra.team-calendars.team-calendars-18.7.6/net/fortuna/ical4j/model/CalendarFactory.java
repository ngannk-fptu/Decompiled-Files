/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovy.transform.Generated
 *  groovy.transform.Internal
 */
package net.fortuna.ical4j.model;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.transform.Generated;
import groovy.transform.Internal;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class CalendarFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public CalendarFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = CalendarFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = CalendarFactory.$getCallSiteArray();
        Calendar calendar = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, Calendar.class))) {
            Object object = value;
            calendar = (Calendar)ScriptBytecodeAdapter.castToType(object, Calendar.class);
        } else {
            List list = ScriptBytecodeAdapter.createList(new Object[0]);
            calendar = (Calendar)ScriptBytecodeAdapter.castToType(list, Calendar.class);
        }
        return calendar;
    }

    @Override
    public void setChild(FactoryBuilderSupport build, Object parent, Object child) {
        CallSite[] callSiteArray = CalendarFactory.$getCallSiteArray();
        if (child instanceof Property) {
            callSiteArray[1].call(callSiteArray[2].callGetProperty(parent), child);
        } else if (child instanceof Component) {
            callSiteArray[3].call(callSiteArray[4].callGetProperty(parent), child);
        }
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != CalendarFactory.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    @Override
    @Generated
    @Internal
    public /* synthetic */ MetaClass getMetaClass() {
        MetaClass metaClass = this.metaClass;
        if (metaClass != null) {
            return metaClass;
        }
        this.metaClass = this.$getStaticMetaClass();
        return this.metaClass;
    }

    @Override
    @Generated
    @Internal
    public /* synthetic */ void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    @Generated
    @Internal
    public /* synthetic */ Object invokeMethod(String string, Object object) {
        return this.getMetaClass().invokeMethod((Object)this, string, object);
    }

    @Override
    @Generated
    @Internal
    public /* synthetic */ Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    @Generated
    @Internal
    public /* synthetic */ void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }

    public /* synthetic */ void super$2$setChild(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2) {
        super.setChild(factoryBuilderSupport, object, object2);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "checkValueIsType";
        stringArray[1] = "add";
        stringArray[2] = "properties";
        stringArray[3] = "add";
        stringArray[4] = "components";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[5];
        CalendarFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(CalendarFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = CalendarFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

