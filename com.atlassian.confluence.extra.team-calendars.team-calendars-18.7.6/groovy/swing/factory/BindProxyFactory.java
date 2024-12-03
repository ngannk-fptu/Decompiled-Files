/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Map;
import org.codehaus.groovy.binding.BindingProxy;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class BindProxyFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public BindProxyFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = BindProxyFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public boolean isLeaf() {
        CallSite[] callSiteArray = BindProxyFactory.$getCallSiteArray();
        return true;
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = BindProxyFactory.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareEqual(value, null)) {
            throw (Throwable)callSiteArray[0].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name}, new String[]{"", " requires a value argument."}));
        }
        BindingProxy mb = (BindingProxy)ScriptBytecodeAdapter.castToType(callSiteArray[1].callConstructor(BindingProxy.class, value), BindingProxy.class);
        Object o = callSiteArray[2].call((Object)attributes, "bind");
        boolean bl = o instanceof Boolean && DefaultTypeTransformation.booleanUnbox(callSiteArray[3].call((Boolean)ScriptBytecodeAdapter.castToType(o, Boolean.class)));
        ScriptBytecodeAdapter.setProperty(bl, null, callSiteArray[4].callGroovyObjectGetProperty(builder), "bind");
        return mb;
    }

    @Override
    public void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        CallSite[] callSiteArray = BindProxyFactory.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[5].callGetProperty(callSiteArray[6].callGroovyObjectGetProperty(builder)))) {
            callSiteArray[7].call(node);
        }
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != BindProxyFactory.class) {
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

    public /* synthetic */ boolean super$2$isLeaf() {
        return super.isLeaf();
    }

    public /* synthetic */ void super$2$onNodeCompleted(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2) {
        super.onNodeCompleted(factoryBuilderSupport, object, object2);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "remove";
        stringArray[3] = "booleanValue";
        stringArray[4] = "context";
        stringArray[5] = "bind";
        stringArray[6] = "context";
        stringArray[7] = "bind";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[8];
        BindProxyFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(BindProxyFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = BindProxyFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

