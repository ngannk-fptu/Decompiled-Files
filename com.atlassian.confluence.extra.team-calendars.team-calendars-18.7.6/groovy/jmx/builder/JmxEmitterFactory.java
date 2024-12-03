/*
 * Decompiled with CFR 0.152.
 */
package groovy.jmx.builder;

import groovy.jmx.builder.JmxBuilder;
import groovy.jmx.builder.JmxBuilderException;
import groovy.jmx.builder.JmxEventEmitter;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import groovy.util.GroovyMBean;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.NotificationFilterSupport;
import javax.management.ObjectName;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class JmxEmitterFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public JmxEmitterFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = JmxEmitterFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object nodeName, Object nodeParam, Map nodeAttribs) {
        Object object;
        CallSite[] callSiteArray = JmxEmitterFactory.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(nodeParam)) {
            throw (Throwable)callSiteArray[0].callConstructor(JmxBuilderException.class, new GStringImpl(new Object[]{nodeName}, new String[]{"Node '", "' only supports named attributes."}));
        }
        JmxBuilder fsb = (JmxBuilder)ScriptBytecodeAdapter.castToType(builder, JmxBuilder.class);
        Reference<MBeanServer> server = new Reference<MBeanServer>((MBeanServer)ScriptBytecodeAdapter.castToType(callSiteArray[1].call(fsb), MBeanServer.class));
        Reference<Object> emitter = new Reference<Object>(callSiteArray[2].callConstructor(JmxEventEmitter.class));
        Reference<Object> name = new Reference<Object>(callSiteArray[3].callCurrent(this, fsb, emitter.get(), callSiteArray[4].call((Object)nodeAttribs, "name")));
        Object object2 = callSiteArray[5].call((Object)nodeAttribs, "event");
        Object event = DefaultTypeTransformation.booleanUnbox(object2) ? object2 : (DefaultTypeTransformation.booleanUnbox(object = callSiteArray[6].call((Object)nodeAttribs, "type")) ? object : "jmx.builder.event.emitter");
        Object object3 = callSiteArray[7].call((Object)nodeAttribs, "listeners");
        Object listeners = DefaultTypeTransformation.booleanUnbox(object3) ? object3 : callSiteArray[8].call((Object)nodeAttribs, "recipients");
        Reference<Object> filter = new Reference<Object>(null);
        if (DefaultTypeTransformation.booleanUnbox(event)) {
            Object object4 = callSiteArray[9].callConstructor(NotificationFilterSupport.class);
            filter.set(((NotificationFilterSupport)ScriptBytecodeAdapter.castToType(object4, NotificationFilterSupport.class)));
            callSiteArray[10].call((Object)filter.get(), event);
            callSiteArray[11].call(emitter.get(), event);
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[12].call((Object)server.get(), name.get()))) {
            callSiteArray[13].call((Object)server.get(), name.get());
        }
        callSiteArray[14].call(server.get(), emitter.get(), name.get());
        if (DefaultTypeTransformation.booleanUnbox(listeners) && !(listeners instanceof List)) {
            throw (Throwable)callSiteArray[15].callConstructor(JmxBuilderException.class, "Listeners must be provided as a list [listener1,...,listenerN]");
        }
        public class _newInstance_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference server;
            private /* synthetic */ Reference name;
            private /* synthetic */ Reference filter;
            private /* synthetic */ Reference emitter;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _newInstance_closure1(Object _outerInstance, Object _thisObject, Reference server, Reference name, Reference filter, Reference emitter) {
                Reference reference;
                Reference reference2;
                Reference reference3;
                Reference reference4;
                CallSite[] callSiteArray = _newInstance_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.server = reference4 = server;
                this.name = reference3 = name;
                this.filter = reference2 = filter;
                this.emitter = reference = emitter;
            }

            /*
             * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            public Object doCall(Object l) {
                CallSite[] callSiteArray = _newInstance_closure1.$getCallSiteArray();
                Object listener = l;
                try {
                    if (listener instanceof String) {
                        Object object;
                        listener = object = callSiteArray[0].callConstructor(ObjectName.class, l);
                    }
                    if (!(listener instanceof ObjectName)) return callSiteArray[2].call(this.emitter.get(), listener, this.filter.get(), null);
                    return callSiteArray[1].call(this.server.get(), this.name.get(), listener, this.filter.get(), null);
                }
                catch (Exception e) {
                    throw (Throwable)callSiteArray[3].callConstructor(JmxBuilderException.class, e);
                }
                catch (Throwable throwable) {
                    throw throwable;
                }
            }

            public Object getServer() {
                CallSite[] callSiteArray = _newInstance_closure1.$getCallSiteArray();
                return this.server.get();
            }

            public Object getName() {
                CallSite[] callSiteArray = _newInstance_closure1.$getCallSiteArray();
                return this.name.get();
            }

            public NotificationFilterSupport getFilter() {
                CallSite[] callSiteArray = _newInstance_closure1.$getCallSiteArray();
                return (NotificationFilterSupport)ScriptBytecodeAdapter.castToType(this.filter.get(), NotificationFilterSupport.class);
            }

            public Object getEmitter() {
                CallSite[] callSiteArray = _newInstance_closure1.$getCallSiteArray();
                return this.emitter.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _newInstance_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "<$constructor$>";
                stringArray[1] = "addNotificationListener";
                stringArray[2] = "addNotificationListener";
                stringArray[3] = "<$constructor$>";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[4];
                _newInstance_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_newInstance_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _newInstance_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[16].call(listeners, new _newInstance_closure1(this, this, server, name, filter, emitter));
        return callSiteArray[17].callConstructor(GroovyMBean.class, callSiteArray[18].call(fsb), name.get());
    }

    private ObjectName getObjectName(Object fsb, Object emitter, Object name) {
        CallSite[] callSiteArray = JmxEmitterFactory.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(name) && name instanceof ObjectName) {
            return (ObjectName)ScriptBytecodeAdapter.castToType(name, ObjectName.class);
        }
        if (DefaultTypeTransformation.booleanUnbox(name) && name instanceof String) {
            return (ObjectName)ScriptBytecodeAdapter.castToType(callSiteArray[19].callConstructor(ObjectName.class, name), ObjectName.class);
        }
        if (!DefaultTypeTransformation.booleanUnbox(name)) {
            return (ObjectName)ScriptBytecodeAdapter.castToType(callSiteArray[20].callConstructor(ObjectName.class, new GStringImpl(new Object[]{callSiteArray[21].call(fsb), callSiteArray[22].call(emitter)}, new String[]{"", ":type=Emitter,name=Emitter@", ""})), ObjectName.class);
        }
        return (ObjectName)ScriptBytecodeAdapter.castToType(null, ObjectName.class);
    }

    @Override
    public boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map nodeAttribs) {
        CallSite[] callSiteArray = JmxEmitterFactory.$getCallSiteArray();
        return false;
    }

    @Override
    public boolean isLeaf() {
        CallSite[] callSiteArray = JmxEmitterFactory.$getCallSiteArray();
        return true;
    }

    @Override
    public void onNodeCompleted(FactoryBuilderSupport builder, Object parentNode, Object thisNode) {
        CallSite[] callSiteArray = JmxEmitterFactory.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareNotEqual(parentNode, null)) {
            callSiteArray[23].call(parentNode, thisNode);
        }
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != JmxEmitterFactory.class) {
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
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "getObjectName";
        stringArray[4] = "remove";
        stringArray[5] = "remove";
        stringArray[6] = "remove";
        stringArray[7] = "remove";
        stringArray[8] = "remove";
        stringArray[9] = "<$constructor$>";
        stringArray[10] = "enableType";
        stringArray[11] = "setEvent";
        stringArray[12] = "isRegistered";
        stringArray[13] = "unregisterMBean";
        stringArray[14] = "registerMBean";
        stringArray[15] = "<$constructor$>";
        stringArray[16] = "each";
        stringArray[17] = "<$constructor$>";
        stringArray[18] = "getMBeanServer";
        stringArray[19] = "<$constructor$>";
        stringArray[20] = "<$constructor$>";
        stringArray[21] = "getDefaultJmxNameDomain";
        stringArray[22] = "hashCode";
        stringArray[23] = "add";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[24];
        JmxEmitterFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(JmxEmitterFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = JmxEmitterFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

