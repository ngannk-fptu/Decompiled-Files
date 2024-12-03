/*
 * Decompiled with CFR 0.152.
 */
package groovy.jmx.builder;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Map;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class JmxBeanExportFactory
extends AbstractFactory
implements GroovyObject {
    private Object registrationPolicy;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public JmxBeanExportFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = JmxBeanExportFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object nodeName, Object nodeArgs, Map nodeAttribs) {
        Object object;
        Object object2;
        CallSite[] callSiteArray = JmxBeanExportFactory.$getCallSiteArray();
        Object object3 = callSiteArray[0].callSafe((Object)nodeAttribs, "policy");
        this.registrationPolicy = object2 = DefaultTypeTransformation.booleanUnbox(object3) ? object3 : (DefaultTypeTransformation.booleanUnbox(object = callSiteArray[1].callSafe((Object)nodeAttribs, "regPolicy")) ? object : "replace");
        return ScriptBytecodeAdapter.createList(new Object[0]);
    }

    @Override
    public boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map nodeAttribs) {
        CallSite[] callSiteArray = JmxBeanExportFactory.$getCallSiteArray();
        return true;
    }

    @Override
    public boolean isLeaf() {
        CallSite[] callSiteArray = JmxBeanExportFactory.$getCallSiteArray();
        return false;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != JmxBeanExportFactory.class) {
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

    public Object getRegistrationPolicy() {
        return this.registrationPolicy;
    }

    public void setRegistrationPolicy(Object object) {
        this.registrationPolicy = object;
    }

    public /* synthetic */ boolean super$2$isLeaf() {
        return super.isLeaf();
    }

    public /* synthetic */ boolean super$2$onHandleNodeAttributes(FactoryBuilderSupport factoryBuilderSupport, Object object, Map map) {
        return super.onHandleNodeAttributes(factoryBuilderSupport, object, map);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "remove";
        stringArray[1] = "remove";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[2];
        JmxBeanExportFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(JmxBeanExportFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = JmxBeanExportFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

