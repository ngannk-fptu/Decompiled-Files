/*
 * Decompiled with CFR 0.152.
 */
package groovy.jmx.builder;

import groovy.jmx.builder.JmxBuilder;
import groovy.jmx.builder.JmxBuilderException;
import groovy.jmx.builder.JmxBuilderTools;
import groovy.jmx.builder.JmxMetaMapBuilder;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.Map;
import javax.management.MBeanServer;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class JmxBeanFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public JmxBeanFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = JmxBeanFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object nodeName, Object nodeParam, Map nodeAttributes) {
        CallSite[] callSiteArray = JmxBeanFactory.$getCallSiteArray();
        JmxBuilder fsb = (JmxBuilder)ScriptBytecodeAdapter.castToType(builder, JmxBuilder.class);
        MBeanServer server = (MBeanServer)ScriptBytecodeAdapter.castToType(callSiteArray[0].call(fsb), MBeanServer.class);
        Object metaMap = null;
        Object target = null;
        if (DefaultTypeTransformation.booleanUnbox(nodeParam)) {
            Object object;
            Object object2;
            Object object3;
            target = object3 = nodeParam;
            metaMap = object2 = callSiteArray[1].callCurrent((GroovyObject)this, target);
            metaMap = object = callSiteArray[2].call(JmxMetaMapBuilder.class, target);
        } else if (DefaultTypeTransformation.booleanUnbox(nodeAttributes)) {
            Object object;
            Object object4;
            Object object5;
            target = object5 = callSiteArray[3].callGetProperty(nodeAttributes);
            metaMap = object4 = callSiteArray[4].callCurrent((GroovyObject)this, target);
            metaMap = object = callSiteArray[5].call(JmxMetaMapBuilder.class, target, nodeAttributes);
        }
        Object object = callSiteArray[6].callGetProperty(metaMap);
        Object object6 = DefaultTypeTransformation.booleanUnbox(object) ? object : server;
        ScriptBytecodeAdapter.setProperty(object6, null, metaMap, "server");
        Object object7 = callSiteArray[7].call(JmxBuilderTools.class, callSiteArray[8].call(target));
        ScriptBytecodeAdapter.setProperty(object7, null, metaMap, "isMBean");
        return metaMap;
    }

    @Override
    public boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map nodeAttribs) {
        CallSite[] callSiteArray = JmxBeanFactory.$getCallSiteArray();
        return false;
    }

    @Override
    public void onNodeCompleted(FactoryBuilderSupport builder, Object parentNode, Object thisNode) {
        CallSite[] callSiteArray = JmxBeanFactory.$getCallSiteArray();
        JmxBuilder fsb = (JmxBuilder)ScriptBytecodeAdapter.castToType(builder, JmxBuilder.class);
        MBeanServer server = (MBeanServer)ScriptBytecodeAdapter.castToType(callSiteArray[9].call(fsb), MBeanServer.class);
        Object metaMap = thisNode;
        Object object = callSiteArray[10].callGetPropertySafe(callSiteArray[11].callGroovyObjectGetProperty(fsb));
        Object regPolicy = DefaultTypeTransformation.booleanUnbox(object) ? object : "replace";
        Object registeredBean = callSiteArray[12].call(JmxBuilderTools.class, regPolicy, metaMap);
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareNotEqual(parentNode, null) && DefaultTypeTransformation.booleanUnbox(registeredBean) && ScriptBytecodeAdapter.compareEqual(regPolicy, "replace")) {
                Iterator i = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[13].call(parentNode), Iterator.class);
                while (DefaultTypeTransformation.booleanUnbox(callSiteArray[14].call(i))) {
                    Object exportedBean = callSiteArray[15].call(i);
                    if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[16].call(callSiteArray[17].call(exportedBean), callSiteArray[18].callGetProperty(metaMap)))) continue;
                    callSiteArray[19].call(i);
                }
            }
        } else if (ScriptBytecodeAdapter.compareNotEqual(parentNode, null) && DefaultTypeTransformation.booleanUnbox(registeredBean) && ScriptBytecodeAdapter.compareEqual(regPolicy, "replace")) {
            Iterator i = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[20].call(parentNode), Iterator.class);
            while (DefaultTypeTransformation.booleanUnbox(callSiteArray[21].call(i))) {
                Object exportedBean = callSiteArray[22].call(i);
                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[23].call(callSiteArray[24].call(exportedBean), callSiteArray[25].callGetProperty(metaMap)))) continue;
                callSiteArray[26].call(i);
            }
        }
        if (ScriptBytecodeAdapter.compareNotEqual(parentNode, null) && DefaultTypeTransformation.booleanUnbox(registeredBean)) {
            callSiteArray[27].call(parentNode, registeredBean);
        }
    }

    @Override
    public boolean isLeaf() {
        CallSite[] callSiteArray = JmxBeanFactory.$getCallSiteArray();
        return false;
    }

    private Object initMetaMap(Object target) {
        CallSite[] callSiteArray = JmxBeanFactory.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(target)) {
            throw (Throwable)callSiteArray[28].callConstructor(JmxBuilderException.class, callSiteArray[29].call(callSiteArray[30].call((Object)"You must specify a target object to ", " export as MBean i.e. JmxBuilder.bean(targetInstance), "), " JmxBuilder.bean([target:instance]), JmxBuilder.beans([instanceList])."));
        }
        Map metaMap = ScriptBytecodeAdapter.createMap(new Object[0]);
        Object object = target;
        ScriptBytecodeAdapter.setProperty(object, null, metaMap, "target");
        Object object2 = callSiteArray[31].callGetProperty(callSiteArray[32].callGetProperty(target));
        ScriptBytecodeAdapter.setProperty(object2, null, metaMap, "name");
        return metaMap;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != JmxBeanFactory.class) {
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

    public /* synthetic */ boolean super$2$onHandleNodeAttributes(FactoryBuilderSupport factoryBuilderSupport, Object object, Map map) {
        return super.onHandleNodeAttributes(factoryBuilderSupport, object, map);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "getMBeanServer";
        stringArray[1] = "initMetaMap";
        stringArray[2] = "buildObjectMapFrom";
        stringArray[3] = "target";
        stringArray[4] = "initMetaMap";
        stringArray[5] = "buildObjectMapFrom";
        stringArray[6] = "server";
        stringArray[7] = "isClassMBean";
        stringArray[8] = "getClass";
        stringArray[9] = "getMBeanServer";
        stringArray[10] = "registrationPolicy";
        stringArray[11] = "parentFactory";
        stringArray[12] = "registerMBeanFromMap";
        stringArray[13] = "iterator";
        stringArray[14] = "hasNext";
        stringArray[15] = "next";
        stringArray[16] = "equals";
        stringArray[17] = "name";
        stringArray[18] = "jmxName";
        stringArray[19] = "remove";
        stringArray[20] = "iterator";
        stringArray[21] = "hasNext";
        stringArray[22] = "next";
        stringArray[23] = "equals";
        stringArray[24] = "name";
        stringArray[25] = "jmxName";
        stringArray[26] = "remove";
        stringArray[27] = "add";
        stringArray[28] = "<$constructor$>";
        stringArray[29] = "plus";
        stringArray[30] = "plus";
        stringArray[31] = "canonicalName";
        stringArray[32] = "class";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[33];
        JmxBeanFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(JmxBeanFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = JmxBeanFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

