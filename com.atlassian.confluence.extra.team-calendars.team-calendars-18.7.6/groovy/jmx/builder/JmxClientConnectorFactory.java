/*
 * Decompiled with CFR 0.152.
 */
package groovy.jmx.builder;

import groovy.jmx.builder.JmxBuilderException;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class JmxClientConnectorFactory
extends AbstractFactory
implements GroovyObject {
    private static final List SUPPORTED_PROTOCOLS;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public JmxClientConnectorFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = JmxClientConnectorFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object nodeName, Object nodeArgs, Map nodeAttribs) {
        Object object;
        Object object2;
        Object object3;
        CallSite[] callSiteArray = JmxClientConnectorFactory.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(nodeArgs)) {
            throw (Throwable)callSiteArray[0].callConstructor(JmxBuilderException.class, new GStringImpl(new Object[]{nodeName}, new String[]{"Node '", "' only supports named attributes."}));
        }
        Object object4 = callSiteArray[1].callSafe((Object)nodeAttribs, "protocol");
        Object protocol = DefaultTypeTransformation.booleanUnbox(object4) ? object4 : (DefaultTypeTransformation.booleanUnbox(object3 = callSiteArray[2].callSafe((Object)nodeAttribs, "transport")) ? object3 : "rmi");
        Object port = callSiteArray[3].callSafe((Object)nodeAttribs, "port");
        Object object5 = callSiteArray[4].callSafe((Object)nodeAttribs, "host");
        Object host = DefaultTypeTransformation.booleanUnbox(object5) ? object5 : (DefaultTypeTransformation.booleanUnbox(object2 = callSiteArray[5].callSafe((Object)nodeAttribs, "address")) ? object2 : "localhost");
        Object url = callSiteArray[6].callSafe((Object)nodeAttribs, "url");
        Object object6 = callSiteArray[7].callSafe((Object)nodeAttribs, "properties");
        Object props = DefaultTypeTransformation.booleanUnbox(object6) ? object6 : (DefaultTypeTransformation.booleanUnbox(object = callSiteArray[8].callSafe((Object)nodeAttribs, "props")) ? object : callSiteArray[9].callSafe((Object)nodeAttribs, "env"));
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (!DefaultTypeTransformation.booleanUnbox(port) && !DefaultTypeTransformation.booleanUnbox(url)) {
                throw (Throwable)callSiteArray[10].callConstructor(JmxBuilderException.class, new GStringImpl(new Object[]{nodeName}, new String[]{"Node '", " requires attribute 'port' to specify server's port number."}));
            }
        } else if (!DefaultTypeTransformation.booleanUnbox(port) && !DefaultTypeTransformation.booleanUnbox(url)) {
            throw (Throwable)callSiteArray[11].callConstructor(JmxBuilderException.class, new GStringImpl(new Object[]{nodeName}, new String[]{"Node '", " requires attribute 'port' to specify server's port number."}));
        }
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[12].call((Object)SUPPORTED_PROTOCOLS, protocol))) {
            throw (Throwable)callSiteArray[13].callConstructor(JmxBuilderException.class, callSiteArray[14].call((Object)new GStringImpl(new Object[]{protocol}, new String[]{"Connector protocol '", " is not supported at this time. "}), new GStringImpl(new Object[]{SUPPORTED_PROTOCOLS}, new String[]{"Supported protocols are ", "."})));
        }
        JMXServiceURL serviceUrl = (JMXServiceURL)ScriptBytecodeAdapter.castToType(DefaultTypeTransformation.booleanUnbox(url) ? callSiteArray[15].callConstructor(JMXServiceURL.class, url) : callSiteArray[16].callCurrent(this, protocol, host, port), JMXServiceURL.class);
        JMXConnector connector = (JMXConnector)ScriptBytecodeAdapter.castToType(callSiteArray[17].call(JMXConnectorFactory.class, serviceUrl, props), JMXConnector.class);
        return connector;
    }

    private JMXServiceURL generateServiceUrl(Object protocol, Object host, Object port) {
        CallSite[] callSiteArray = JmxClientConnectorFactory.$getCallSiteArray();
        String url = ShortTypeHandling.castToString(new GStringImpl(new Object[]{protocol, protocol, host, port}, new String[]{"service:jmx:", ":///jndi/", "://", ":", "/jmxrmi"}));
        return (JMXServiceURL)ScriptBytecodeAdapter.castToType(callSiteArray[18].callConstructor(JMXServiceURL.class, url), JMXServiceURL.class);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != JmxClientConnectorFactory.class) {
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
        stringArray[10] = "<$constructor$>";
        stringArray[11] = "<$constructor$>";
        stringArray[12] = "contains";
        stringArray[13] = "<$constructor$>";
        stringArray[14] = "plus";
        stringArray[15] = "<$constructor$>";
        stringArray[16] = "generateServiceUrl";
        stringArray[17] = "newJMXConnector";
        stringArray[18] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[19];
        JmxClientConnectorFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(JmxClientConnectorFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = JmxClientConnectorFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

