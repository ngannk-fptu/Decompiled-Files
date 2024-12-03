/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovy.transform.Generated
 *  groovy.transform.Internal
 */
package net.fortuna.ical4j.model.property;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.transform.Generated;
import groovy.transform.Internal;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Map;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public abstract class AbstractPropertyFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public AbstractPropertyFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = AbstractPropertyFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = AbstractPropertyFactory.$getCallSiteArray();
        ParameterList parameters = (ParameterList)ScriptBytecodeAdapter.castToType(callSiteArray[0].call((Object)attributes, "parameters"), ParameterList.class);
        if (ScriptBytecodeAdapter.compareEqual(parameters, null)) {
            Object object = callSiteArray[1].callConstructor(ParameterList.class);
            parameters = (ParameterList)ScriptBytecodeAdapter.castToType(object, ParameterList.class);
        }
        String propValue = ShortTypeHandling.castToString(callSiteArray[2].call((Object)attributes, "value"));
        if (ScriptBytecodeAdapter.compareNotEqual(propValue, null)) {
            return callSiteArray[3].callCurrent(this, parameters, propValue);
        }
        return callSiteArray[4].callCurrent(this, parameters, value);
    }

    protected abstract Object newInstance(ParameterList var1, String var2);

    @Override
    public void setChild(FactoryBuilderSupport build, Object parent, Object child) {
        CallSite[] callSiteArray = AbstractPropertyFactory.$getCallSiteArray();
        if (child instanceof Parameter) {
            callSiteArray[5].call(callSiteArray[6].callGetProperty(parent), child);
        }
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != AbstractPropertyFactory.class) {
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
    public MetaClass getMetaClass() {
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
    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    @Generated
    @Internal
    public Object invokeMethod(String string, Object object) {
        return this.getMetaClass().invokeMethod((Object)this, string, object);
    }

    @Override
    @Generated
    @Internal
    public Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    @Generated
    @Internal
    public void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }

    public /* synthetic */ void super$2$setChild(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2) {
        super.setChild(factoryBuilderSupport, object, object2);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "remove";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "remove";
        stringArray[3] = "newInstance";
        stringArray[4] = "newInstance";
        stringArray[5] = "add";
        stringArray[6] = "parameters";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[7];
        AbstractPropertyFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(AbstractPropertyFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = AbstractPropertyFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

