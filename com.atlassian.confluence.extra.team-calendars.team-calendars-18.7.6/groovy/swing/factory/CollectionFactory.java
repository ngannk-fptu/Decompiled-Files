/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MissingPropertyException;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Map;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class CollectionFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public CollectionFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = CollectionFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = CollectionFactory.$getCallSiteArray();
        callSiteArray[0].call(FactoryBuilderSupport.class, value, name);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[1].call(attributes))) {
            return callSiteArray[2].callConstructor(ArrayList.class);
        }
        Object item = callSiteArray[3].call(callSiteArray[4].call(callSiteArray[5].call(attributes)));
        throw (Throwable)callSiteArray[6].callConstructor(MissingPropertyException.class, new GStringImpl(new Object[]{name}, new String[]{"The builder element '", "' is a collections element and accepts no attributes"}), ScriptBytecodeAdapter.createPojoWrapper((String)ScriptBytecodeAdapter.asType(callSiteArray[7].callGetProperty(item), String.class), String.class), ScriptBytecodeAdapter.createPojoWrapper((Class)ScriptBytecodeAdapter.asType(callSiteArray[8].callGetProperty(item), Class.class), Class.class));
    }

    @Override
    public void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        CallSite[] callSiteArray = CollectionFactory.$getCallSiteArray();
        callSiteArray[9].call(parent, child);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != CollectionFactory.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    @Override
    public /* synthetic */ MetaClass getMetaClass() {
        MetaClass metaClass = this.metaClass;
        if (metaClass != null) {
            return metaClass;
        }
        this.metaClass = this.$getStaticMetaClass();
        return this.metaClass;
    }

    @Override
    public /* synthetic */ void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    public /* synthetic */ Object invokeMethod(String string, Object object) {
        return this.getMetaClass().invokeMethod((Object)this, string, object);
    }

    @Override
    public /* synthetic */ Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    public /* synthetic */ void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }

    public /* synthetic */ void super$2$setChild(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2) {
        super.setChild(factoryBuilderSupport, object, object2);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "checkValueIsNull";
        stringArray[1] = "isEmpty";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "next";
        stringArray[4] = "iterator";
        stringArray[5] = "entrySet";
        stringArray[6] = "<$constructor$>";
        stringArray[7] = "key";
        stringArray[8] = "value";
        stringArray[9] = "add";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[10];
        CollectionFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(CollectionFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = CollectionFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

