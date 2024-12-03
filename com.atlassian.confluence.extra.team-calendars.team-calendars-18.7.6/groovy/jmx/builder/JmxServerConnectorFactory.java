/*
 * Decompiled with CFR 0.152.
 */
package groovy.jmx.builder;

import groovy.jmx.builder.JmxBuilder;
import groovy.jmx.builder.JmxBuilderException;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnectorServer;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class JmxServerConnectorFactory
extends AbstractFactory
implements GroovyObject {
    private static final List SUPPORTED_PROTOCOLS;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public JmxServerConnectorFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = JmxServerConnectorFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object nodeName, Object nodeArgs, Map nodeAttribs) {
        Object object;
        Object object2;
        Object object3;
        CallSite[] callSiteArray = JmxServerConnectorFactory.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(nodeArgs)) {
            throw (Throwable)callSiteArray[0].callConstructor(JmxBuilderException.class, new GStringImpl(new Object[]{nodeName}, new String[]{"Node '", "' only supports named attributes."}));
        }
        JmxBuilder fsb = (JmxBuilder)ScriptBytecodeAdapter.castToType(builder, JmxBuilder.class);
        Object object4 = callSiteArray[1].callSafe((Object)nodeAttribs, "protocol");
        Object protocol = DefaultTypeTransformation.booleanUnbox(object4) ? object4 : (DefaultTypeTransformation.booleanUnbox(object3 = callSiteArray[2].callSafe((Object)nodeAttribs, "transport")) ? object3 : "rmi");
        Object port = callSiteArray[3].callSafe((Object)nodeAttribs, "port");
        Object object5 = callSiteArray[4].callSafe((Object)nodeAttribs, "host");
        Object host = DefaultTypeTransformation.booleanUnbox(object5) ? object5 : (DefaultTypeTransformation.booleanUnbox(object2 = callSiteArray[5].callSafe((Object)nodeAttribs, "address")) ? object2 : "localhost");
        Object url = callSiteArray[6].callSafe((Object)nodeAttribs, "url");
        Object object6 = callSiteArray[7].callSafe((Object)nodeAttribs, "properties");
        Object props = DefaultTypeTransformation.booleanUnbox(object6) ? object6 : (DefaultTypeTransformation.booleanUnbox(object = callSiteArray[8].callSafe((Object)nodeAttribs, "props")) ? object : callSiteArray[9].callSafe((Object)nodeAttribs, "env"));
        Object env = callSiteArray[10].callCurrent(this, protocol, port, props);
        callSiteArray[11].call(nodeAttribs);
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (!DefaultTypeTransformation.booleanUnbox(port) && !DefaultTypeTransformation.booleanUnbox(url)) {
                throw (Throwable)callSiteArray[12].callConstructor(JmxBuilderException.class, new GStringImpl(new Object[]{nodeName}, new String[]{"Node '", " requires attribute 'port' to specify server's port number."}));
            }
        } else if (!DefaultTypeTransformation.booleanUnbox(port) && !DefaultTypeTransformation.booleanUnbox(url)) {
            throw (Throwable)callSiteArray[13].callConstructor(JmxBuilderException.class, new GStringImpl(new Object[]{nodeName}, new String[]{"Node '", " requires attribute 'port' to specify server's port number."}));
        }
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[14].call((Object)SUPPORTED_PROTOCOLS, protocol))) {
            throw (Throwable)callSiteArray[15].callConstructor(JmxBuilderException.class, callSiteArray[16].call((Object)new GStringImpl(new Object[]{protocol}, new String[]{"Connector protocol '", " is not supported at this time. "}), new GStringImpl(new Object[]{SUPPORTED_PROTOCOLS}, new String[]{"Supported protocols are ", "."})));
        }
        MBeanServer server = (MBeanServer)ScriptBytecodeAdapter.castToType(callSiteArray[17].call(fsb), MBeanServer.class);
        JMXServiceURL serviceUrl = (JMXServiceURL)ScriptBytecodeAdapter.castToType(DefaultTypeTransformation.booleanUnbox(url) ? callSiteArray[18].callConstructor(JMXServiceURL.class, url) : callSiteArray[19].callCurrent(this, protocol, host, port), JMXServiceURL.class);
        JMXConnectorServer connector = (JMXConnectorServer)ScriptBytecodeAdapter.castToType(callSiteArray[20].call(JMXConnectorServerFactory.class, serviceUrl, env, server), JMXConnectorServer.class);
        return connector;
    }

    @Override
    public boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map nodeAttribs) {
        CallSite[] callSiteArray = JmxServerConnectorFactory.$getCallSiteArray();
        return true;
    }

    @Override
    public boolean isLeaf() {
        CallSite[] callSiteArray = JmxServerConnectorFactory.$getCallSiteArray();
        return false;
    }

    @Override
    public void onNodeCompleted(FactoryBuilderSupport builder, Object parentNode, Object thisNode) {
        CallSite[] callSiteArray = JmxServerConnectorFactory.$getCallSiteArray();
    }

    private Map confiConnectorProperties(String protocol, int port, Map props) {
        CallSite[] callSiteArray = JmxServerConnectorFactory.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(props)) {
            return (Map)ScriptBytecodeAdapter.castToType(null, Map.class);
        }
        Reference<HashMap> env = new Reference<HashMap>((HashMap)ScriptBytecodeAdapter.castToType(callSiteArray[21].callConstructor(HashMap.class), HashMap.class));
        Object object = callSiteArray[22].call((Object)props, "com.sun.management.jmxremote.authenticate");
        Object auth = DefaultTypeTransformation.booleanUnbox(object) ? object : callSiteArray[23].call((Object)props, "authenticate");
        callSiteArray[24].call(env.get(), "com.sun.management.jmxremote.authenticate", auth);
        Object object2 = callSiteArray[25].call((Object)props, "com.sun.management.jmxremote.password.file");
        Object pFile = DefaultTypeTransformation.booleanUnbox(object2) ? object2 : callSiteArray[26].call((Object)props, "passwordFile");
        callSiteArray[27].call(env.get(), "com.sun.management.jmxremote.password.file", pFile);
        Object object3 = callSiteArray[28].call((Object)props, "com.sun.management.jmxremote.access.file");
        Object aFile = DefaultTypeTransformation.booleanUnbox(object3) ? object3 : callSiteArray[29].call((Object)props, "accessFile");
        callSiteArray[30].call(env.get(), "com.sun.management.jmxremote.access.file", aFile);
        Object object4 = callSiteArray[31].call((Object)props, "com.sun.management.jmxremote. ssl");
        Object ssl = DefaultTypeTransformation.booleanUnbox(object4) ? object4 : callSiteArray[32].call((Object)props, "sslEnabled");
        callSiteArray[33].call(env.get(), "com.sun.management.jmxremote.ssl", ssl);
        if (ScriptBytecodeAdapter.compareEqual(protocol, "rmi") && DefaultTypeTransformation.booleanUnbox(ssl)) {
            Object object5 = callSiteArray[34].call((Object)props, callSiteArray[35].callGetProperty(RMIConnectorServer.class));
            Object csf = DefaultTypeTransformation.booleanUnbox(object5) ? object5 : callSiteArray[36].callConstructor(SslRMIClientSocketFactory.class);
            Object object6 = callSiteArray[37].call((Object)props, callSiteArray[38].callGetProperty(RMIConnectorServer.class));
            Object ssf = DefaultTypeTransformation.booleanUnbox(object6) ? object6 : callSiteArray[39].callConstructor(SslRMIServerSocketFactory.class);
            callSiteArray[40].call(env.get(), callSiteArray[41].callGetProperty(RMIConnectorServer.class), csf);
            callSiteArray[42].call(env.get(), callSiteArray[43].callGetProperty(RMIConnectorServer.class), ssf);
        }
        public class _confiConnectorProperties_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference env;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _confiConnectorProperties_closure1(Object _outerInstance, Object _thisObject, Reference env) {
                Reference reference;
                CallSite[] callSiteArray = _confiConnectorProperties_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.env = reference = env;
            }

            public Object doCall(Object key, Object value) {
                CallSite[] callSiteArray = _confiConnectorProperties_closure1.$getCallSiteArray();
                return callSiteArray[0].call(this.env.get(), key, value);
            }

            public Object call(Object key, Object value) {
                CallSite[] callSiteArray = _confiConnectorProperties_closure1.$getCallSiteArray();
                return callSiteArray[1].callCurrent(this, key, value);
            }

            public HashMap getEnv() {
                CallSite[] callSiteArray = _confiConnectorProperties_closure1.$getCallSiteArray();
                return (HashMap)ScriptBytecodeAdapter.castToType(this.env.get(), HashMap.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _confiConnectorProperties_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "put";
                stringArray[1] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _confiConnectorProperties_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_confiConnectorProperties_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _confiConnectorProperties_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[44].call((Object)props, new _confiConnectorProperties_closure1(this, this, env));
        return (Map)ScriptBytecodeAdapter.castToType(callSiteArray[45].call(props), Map.class);
    }

    private JMXServiceURL generateServiceUrl(Object protocol, Object host, Object port) {
        CallSite[] callSiteArray = JmxServerConnectorFactory.$getCallSiteArray();
        String url = ShortTypeHandling.castToString(new GStringImpl(new Object[]{protocol, protocol, host, port}, new String[]{"service:jmx:", ":///jndi/", "://", ":", "/jmxrmi"}));
        return (JMXServiceURL)ScriptBytecodeAdapter.castToType(callSiteArray[46].callConstructor(JMXServiceURL.class, url), JMXServiceURL.class);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != JmxServerConnectorFactory.class) {
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

    static {
        List list;
        SUPPORTED_PROTOCOLS = list = ScriptBytecodeAdapter.createList(new Object[]{"rmi", "jrmp", "iiop", "jmxmp"});
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
        stringArray[1] = "remove";
        stringArray[2] = "remove";
        stringArray[3] = "remove";
        stringArray[4] = "remove";
        stringArray[5] = "remove";
        stringArray[6] = "remove";
        stringArray[7] = "remove";
        stringArray[8] = "remove";
        stringArray[9] = "remove";
        stringArray[10] = "confiConnectorProperties";
        stringArray[11] = "clear";
        stringArray[12] = "<$constructor$>";
        stringArray[13] = "<$constructor$>";
        stringArray[14] = "contains";
        stringArray[15] = "<$constructor$>";
        stringArray[16] = "plus";
        stringArray[17] = "getMBeanServer";
        stringArray[18] = "<$constructor$>";
        stringArray[19] = "generateServiceUrl";
        stringArray[20] = "newJMXConnectorServer";
        stringArray[21] = "<$constructor$>";
        stringArray[22] = "remove";
        stringArray[23] = "remove";
        stringArray[24] = "put";
        stringArray[25] = "remove";
        stringArray[26] = "remove";
        stringArray[27] = "put";
        stringArray[28] = "remove";
        stringArray[29] = "remove";
        stringArray[30] = "put";
        stringArray[31] = "remove";
        stringArray[32] = "remove";
        stringArray[33] = "put";
        stringArray[34] = "remove";
        stringArray[35] = "RMI_CLIENT_SOCKET_FACTORY_ATTRIBUTE";
        stringArray[36] = "<$constructor$>";
        stringArray[37] = "remove";
        stringArray[38] = "RMI_SERVER_SOCKET_FACTORY_ATTRIBUTE";
        stringArray[39] = "<$constructor$>";
        stringArray[40] = "put";
        stringArray[41] = "RMI_CLIENT_SOCKET_FACTORY_ATTRIBUTE";
        stringArray[42] = "put";
        stringArray[43] = "RMI_SERVER_SOCKET_FACTORY_ATTRIBUTE";
        stringArray[44] = "each";
        stringArray[45] = "clear";
        stringArray[46] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[47];
        JmxServerConnectorFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(JmxServerConnectorFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = JmxServerConnectorFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

