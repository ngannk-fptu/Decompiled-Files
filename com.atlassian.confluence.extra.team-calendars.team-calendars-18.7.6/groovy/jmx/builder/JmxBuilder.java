/*
 * Decompiled with CFR 0.152.
 */
package groovy.jmx.builder;

import groovy.jmx.builder.JmxBeanExportFactory;
import groovy.jmx.builder.JmxBeanFactory;
import groovy.jmx.builder.JmxBeansFactory;
import groovy.jmx.builder.JmxBuilderTools;
import groovy.jmx.builder.JmxClientConnectorFactory;
import groovy.jmx.builder.JmxEmitterFactory;
import groovy.jmx.builder.JmxListenerFactory;
import groovy.jmx.builder.JmxServerConnectorFactory;
import groovy.jmx.builder.JmxTimerFactory;
import groovy.lang.Closure;
import groovy.lang.MetaClass;
import groovy.util.Factory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Map;
import javax.management.MBeanServerConnection;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class JmxBuilder
extends FactoryBuilderSupport {
    private MBeanServerConnection server;
    private String defaultNameDomain;
    private String defaultNameType;
    private String mode;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public JmxBuilder() {
        String string;
        CallSite[] callSiteArray = JmxBuilder.$getCallSiteArray();
        Object object = callSiteArray[0].callGetProperty(JmxBuilderTools.class);
        this.defaultNameDomain = ShortTypeHandling.castToString(object);
        Object object2 = callSiteArray[1].callGetProperty(JmxBuilderTools.class);
        this.defaultNameType = ShortTypeHandling.castToString(object2);
        this.mode = string = "markup";
        if (BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[2].callCurrent(this);
        } else {
            this.registerFactories();
        }
    }

    public JmxBuilder(MBeanServerConnection svrConnection) {
        MBeanServerConnection mBeanServerConnection;
        CallSite[] callSiteArray = JmxBuilder.$getCallSiteArray();
        this();
        this.server = mBeanServerConnection = svrConnection;
    }

    protected void registerFactories() {
        CallSite[] callSiteArray = JmxBuilder.$getCallSiteArray();
        callSiteArray[3].callCurrent(this, "export", callSiteArray[4].callConstructor(JmxBeanExportFactory.class));
        callSiteArray[5].callCurrent(this, "bean", callSiteArray[6].callConstructor(JmxBeanFactory.class));
        callSiteArray[7].callCurrent(this, "beans", callSiteArray[8].callConstructor(JmxBeansFactory.class));
        callSiteArray[9].callCurrent(this, "timer", callSiteArray[10].callConstructor(JmxTimerFactory.class));
        callSiteArray[11].callCurrent(this, "listener", callSiteArray[12].callConstructor(JmxListenerFactory.class));
        callSiteArray[13].callCurrent(this, "emitter", callSiteArray[14].callConstructor(JmxEmitterFactory.class));
        JmxServerConnectorFactory svrFactory = (JmxServerConnectorFactory)ScriptBytecodeAdapter.castToType(callSiteArray[15].callConstructor(JmxServerConnectorFactory.class), JmxServerConnectorFactory.class);
        callSiteArray[16].callCurrent(this, "server", svrFactory);
        callSiteArray[17].callCurrent(this, "connectorServer", svrFactory);
        callSiteArray[18].callCurrent(this, "serverConnector", svrFactory);
        public class _registerFactories_closure1
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _registerFactories_closure1(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _registerFactories_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _registerFactories_closure1.$getCallSiteArray();
                return callSiteArray[0].callConstructor(JmxClientConnectorFactory.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _registerFactories_closure1.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _registerFactories_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[1];
                stringArray[0] = "<$constructor$>";
                return new CallSiteArray(_registerFactories_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _registerFactories_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        _registerFactories_closure1 newClientFactory = new _registerFactories_closure1(this, this);
        callSiteArray[19].callCurrent(this, "client", callSiteArray[20].call(newClientFactory));
        callSiteArray[21].callCurrent(this, "connector", callSiteArray[22].call(newClientFactory));
        callSiteArray[23].callCurrent(this, "clientConnector", callSiteArray[24].call(newClientFactory));
        callSiteArray[25].callCurrent(this, "connectorClient", callSiteArray[26].call(newClientFactory));
    }

    public MBeanServerConnection getMBeanServer() {
        CallSite[] callSiteArray = JmxBuilder.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(this.server)) {
            Object object = callSiteArray[27].call(JmxBuilderTools.class);
            this.server = (MBeanServerConnection)ScriptBytecodeAdapter.castToType(object, MBeanServerConnection.class);
        }
        return this.server;
    }

    public void setDefaultJmxNameDomain(String domain) {
        CallSite[] callSiteArray = JmxBuilder.$getCallSiteArray();
        String string = domain;
        this.defaultNameDomain = ShortTypeHandling.castToString(string);
    }

    public String getDefaultJmxNameDomain() {
        CallSite[] callSiteArray = JmxBuilder.$getCallSiteArray();
        return this.defaultNameDomain;
    }

    public void setDefaultJmxNameType(String type) {
        CallSite[] callSiteArray = JmxBuilder.$getCallSiteArray();
        String string = type;
        this.defaultNameType = ShortTypeHandling.castToString(string);
    }

    public String getDefaultJmxNameType() {
        CallSite[] callSiteArray = JmxBuilder.$getCallSiteArray();
        return this.defaultNameType;
    }

    public void setMBeanServer(MBeanServerConnection svr) {
        MBeanServerConnection mBeanServerConnection;
        CallSite[] callSiteArray = JmxBuilder.$getCallSiteArray();
        this.server = mBeanServerConnection = svr;
    }

    public void setMode(String mode) {
        CallSite[] callSiteArray = JmxBuilder.$getCallSiteArray();
        String string = mode;
        this.mode = ShortTypeHandling.castToString(string);
    }

    public String getMode() {
        CallSite[] callSiteArray = JmxBuilder.$getCallSiteArray();
        return this.mode;
    }

    @Override
    protected Factory resolveFactory(Object name, Map attributes, Object value) {
        CallSite[] callSiteArray = JmxBuilder.$getCallSiteArray();
        Factory factory = (Factory)ScriptBytecodeAdapter.castToType(ScriptBytecodeAdapter.invokeMethodOnSuperN(FactoryBuilderSupport.class, this, "resolveFactory", new Object[]{name, attributes, value}), Factory.class);
        if (!DefaultTypeTransformation.booleanUnbox(factory)) {
            Object object = callSiteArray[28].callGetPropertySafe(callSiteArray[29].callCurrent(this));
            factory = (Factory)ScriptBytecodeAdapter.castToType(object, Factory.class);
        }
        return factory;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != JmxBuilder.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ Factory super$4$resolveFactory(Object object, Map map, Object object2) {
        return super.resolveFactory(object, map, object2);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "DEFAULT_DOMAIN";
        stringArray[1] = "DEFAULT_NAME_TYPE";
        stringArray[2] = "registerFactories";
        stringArray[3] = "registerFactory";
        stringArray[4] = "<$constructor$>";
        stringArray[5] = "registerFactory";
        stringArray[6] = "<$constructor$>";
        stringArray[7] = "registerFactory";
        stringArray[8] = "<$constructor$>";
        stringArray[9] = "registerFactory";
        stringArray[10] = "<$constructor$>";
        stringArray[11] = "registerFactory";
        stringArray[12] = "<$constructor$>";
        stringArray[13] = "registerFactory";
        stringArray[14] = "<$constructor$>";
        stringArray[15] = "<$constructor$>";
        stringArray[16] = "registerFactory";
        stringArray[17] = "registerFactory";
        stringArray[18] = "registerFactory";
        stringArray[19] = "registerFactory";
        stringArray[20] = "call";
        stringArray[21] = "registerFactory";
        stringArray[22] = "call";
        stringArray[23] = "registerFactory";
        stringArray[24] = "call";
        stringArray[25] = "registerFactory";
        stringArray[26] = "call";
        stringArray[27] = "getMBeanServer";
        stringArray[28] = "childFactory";
        stringArray[29] = "getParentFactory";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[30];
        JmxBuilder.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(JmxBuilder.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = JmxBuilder.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

