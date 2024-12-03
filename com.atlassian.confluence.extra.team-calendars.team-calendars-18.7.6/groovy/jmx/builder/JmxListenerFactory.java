/*
 * Decompiled with CFR 0.152.
 */
package groovy.jmx.builder;

import groovy.jmx.builder.JmxBuilder;
import groovy.jmx.builder.JmxBuilderException;
import groovy.jmx.builder.JmxEventListener;
import groovy.jmx.builder.JmxMetaMapBuilder;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Map;
import javax.management.InstanceNotFoundException;
import javax.management.NotificationFilterSupport;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class JmxListenerFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public JmxListenerFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = JmxListenerFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object nodeName, Object nodeParam, Map nodeAttribs) {
        CallSite[] callSiteArray = JmxListenerFactory.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(nodeParam)) {
            throw (Throwable)callSiteArray[0].callConstructor(JmxBuilderException.class, new GStringImpl(new Object[]{nodeName}, new String[]{"Node '", "' only supports named attributes."}));
        }
        JmxBuilder fsb = (JmxBuilder)ScriptBytecodeAdapter.castToType(builder, JmxBuilder.class);
        Object server = callSiteArray[1].call(fsb);
        Object map = callSiteArray[2].call(JmxMetaMapBuilder.class, nodeAttribs);
        Object broadcaster = callSiteArray[3].call(map, "from");
        try {
            String eventType = ShortTypeHandling.castToString(callSiteArray[4].call(map, "event"));
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[5].call(server, broadcaster))) {
                throw (Throwable)callSiteArray[6].callConstructor(JmxBuilderException.class, new GStringImpl(new Object[]{callSiteArray[7].call(broadcaster)}, new String[]{"MBean '", "' is not registered in server."}));
            }
            if (DefaultTypeTransformation.booleanUnbox(eventType)) {
                NotificationFilterSupport filter = (NotificationFilterSupport)ScriptBytecodeAdapter.castToType(callSiteArray[8].callConstructor(NotificationFilterSupport.class), NotificationFilterSupport.class);
                callSiteArray[9].call((Object)filter, eventType);
                callSiteArray[10].call(server, broadcaster, callSiteArray[11].call(JmxEventListener.class), filter, map);
            } else {
                callSiteArray[12].call(server, broadcaster, callSiteArray[13].call(JmxEventListener.class), null, map);
            }
        }
        catch (InstanceNotFoundException e) {
            throw (Throwable)callSiteArray[14].callConstructor(JmxBuilderException.class, e);
        }
        return map;
    }

    @Override
    public boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map nodeAttribs) {
        CallSite[] callSiteArray = JmxListenerFactory.$getCallSiteArray();
        return false;
    }

    @Override
    public boolean isLeaf() {
        CallSite[] callSiteArray = JmxListenerFactory.$getCallSiteArray();
        return true;
    }

    @Override
    public void onNodeCompleted(FactoryBuilderSupport builder, Object parentNode, Object thisNode) {
        CallSite[] callSiteArray = JmxListenerFactory.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareNotEqual(parentNode, null)) {
            callSiteArray[15].call(parentNode, thisNode);
        }
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != JmxListenerFactory.class) {
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
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "getMBeanServer";
        stringArray[2] = "createListenerMap";
        stringArray[3] = "get";
        stringArray[4] = "get";
        stringArray[5] = "isRegistered";
        stringArray[6] = "<$constructor$>";
        stringArray[7] = "toString";
        stringArray[8] = "<$constructor$>";
        stringArray[9] = "enableType";
        stringArray[10] = "addNotificationListener";
        stringArray[11] = "getListener";
        stringArray[12] = "addNotificationListener";
        stringArray[13] = "getListener";
        stringArray[14] = "<$constructor$>";
        stringArray[15] = "add";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[16];
        JmxListenerFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(JmxListenerFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = JmxListenerFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

