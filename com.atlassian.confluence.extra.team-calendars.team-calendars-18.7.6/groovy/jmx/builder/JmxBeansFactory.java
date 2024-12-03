/*
 * Decompiled with CFR 0.152.
 */
package groovy.jmx.builder;

import groovy.jmx.builder.JmxBuilder;
import groovy.jmx.builder.JmxBuilderException;
import groovy.jmx.builder.JmxBuilderTools;
import groovy.jmx.builder.JmxMetaMapBuilder;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.management.MBeanServer;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class JmxBeansFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public JmxBeansFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = JmxBeansFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object nodeName, Object nodeParam, Map nodeAttribs) {
        CallSite[] callSiteArray = JmxBeansFactory.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (!DefaultTypeTransformation.booleanUnbox(nodeParam) || !(nodeParam instanceof List)) {
                throw (Throwable)callSiteArray[0].callConstructor(JmxBuilderException.class, new GStringImpl(new Object[]{nodeName}, new String[]{"Node '", "' requires a list of object to be exported."}));
            }
        } else if (!DefaultTypeTransformation.booleanUnbox(nodeParam) || !(nodeParam instanceof List)) {
            throw (Throwable)callSiteArray[1].callConstructor(JmxBuilderException.class, new GStringImpl(new Object[]{nodeName}, new String[]{"Node '", "' requires a list of object to be exported."}));
        }
        JmxBuilder fsb = (JmxBuilder)ScriptBytecodeAdapter.castToType(builder, JmxBuilder.class);
        Reference<MBeanServer> server = new Reference<MBeanServer>((MBeanServer)ScriptBytecodeAdapter.castToType(callSiteArray[2].call(fsb), MBeanServer.class));
        Reference<List> metaMaps = new Reference<List>(ScriptBytecodeAdapter.createList(new Object[0]));
        Object targets = nodeParam;
        Object map = null;
        public class _newInstance_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference server;
            private /* synthetic */ Reference metaMaps;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _newInstance_closure1(Object _outerInstance, Object _thisObject, Reference server, Reference metaMaps) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _newInstance_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.server = reference2 = server;
                this.metaMaps = reference = metaMaps;
            }

            public Object doCall(Object target) {
                CallSite[] callSiteArray = _newInstance_closure1.$getCallSiteArray();
                Object metaMap = callSiteArray[0].call(JmxMetaMapBuilder.class, target);
                Object object = callSiteArray[1].callGetProperty(metaMap);
                Object object2 = DefaultTypeTransformation.booleanUnbox(object) ? object : this.server.get();
                ScriptBytecodeAdapter.setProperty(object2, null, metaMap, "server");
                Object object3 = callSiteArray[2].call(JmxBuilderTools.class, callSiteArray[3].call(target));
                ScriptBytecodeAdapter.setProperty(object3, null, metaMap, "isMBean");
                return callSiteArray[4].call(this.metaMaps.get(), metaMap);
            }

            public MBeanServer getServer() {
                CallSite[] callSiteArray = _newInstance_closure1.$getCallSiteArray();
                return (MBeanServer)ScriptBytecodeAdapter.castToType(this.server.get(), MBeanServer.class);
            }

            public Object getMetaMaps() {
                CallSite[] callSiteArray = _newInstance_closure1.$getCallSiteArray();
                return this.metaMaps.get();
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
                stringArray[0] = "buildObjectMapFrom";
                stringArray[1] = "server";
                stringArray[2] = "isClassMBean";
                stringArray[3] = "getClass";
                stringArray[4] = "add";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[5];
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
        callSiteArray[3].call(targets, new _newInstance_closure1(this, this, server, metaMaps));
        return metaMaps.get();
    }

    @Override
    public boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map nodeAttribs) {
        CallSite[] callSiteArray = JmxBeansFactory.$getCallSiteArray();
        return false;
    }

    @Override
    public boolean isLeaf() {
        CallSite[] callSiteArray = JmxBeansFactory.$getCallSiteArray();
        return true;
    }

    @Override
    public void onNodeCompleted(FactoryBuilderSupport builder, Object parentNode, Object thisNode) {
        Reference<Object> parentNode2 = new Reference<Object>(parentNode);
        CallSite[] callSiteArray = JmxBeansFactory.$getCallSiteArray();
        JmxBuilder fsb = (JmxBuilder)ScriptBytecodeAdapter.castToType(builder, JmxBuilder.class);
        MBeanServer server = (MBeanServer)ScriptBytecodeAdapter.castToType(callSiteArray[4].call(fsb), MBeanServer.class);
        Object metaMaps = thisNode;
        Object object = callSiteArray[5].callGetPropertySafe(callSiteArray[6].callGroovyObjectGetProperty(fsb));
        Reference<Object> regPolicy = new Reference<Object>(DefaultTypeTransformation.booleanUnbox(object) ? object : "replace");
        public class _onNodeCompleted_closure2
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference regPolicy;
            private /* synthetic */ Reference parentNode;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _onNodeCompleted_closure2(Object _outerInstance, Object _thisObject, Reference regPolicy, Reference parentNode) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _onNodeCompleted_closure2.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.regPolicy = reference2 = regPolicy;
                this.parentNode = reference = parentNode;
            }

            public Object doCall(Object metaMap) {
                CallSite[] callSiteArray = _onNodeCompleted_closure2.$getCallSiteArray();
                Object registeredBean = callSiteArray[0].call(JmxBuilderTools.class, this.regPolicy.get(), metaMap);
                if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    if (ScriptBytecodeAdapter.compareNotEqual(this.parentNode.get(), null) && DefaultTypeTransformation.booleanUnbox(registeredBean) && ScriptBytecodeAdapter.compareEqual(this.regPolicy.get(), "replace")) {
                        Iterator i = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[1].call(this.parentNode.get()), Iterator.class);
                        while (DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call(i))) {
                            Object exportedBean = callSiteArray[3].call(i);
                            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[4].call(callSiteArray[5].call(exportedBean), callSiteArray[6].callGetProperty(metaMap)))) continue;
                            callSiteArray[7].call(i);
                        }
                    }
                } else if (ScriptBytecodeAdapter.compareNotEqual(this.parentNode.get(), null) && DefaultTypeTransformation.booleanUnbox(registeredBean) && ScriptBytecodeAdapter.compareEqual(this.regPolicy.get(), "replace")) {
                    Iterator i = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[8].call(this.parentNode.get()), Iterator.class);
                    while (DefaultTypeTransformation.booleanUnbox(callSiteArray[9].call(i))) {
                        Object exportedBean = callSiteArray[10].call(i);
                        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[11].call(callSiteArray[12].call(exportedBean), callSiteArray[13].callGetProperty(metaMap)))) continue;
                        callSiteArray[14].call(i);
                    }
                }
                if (ScriptBytecodeAdapter.compareNotEqual(this.parentNode.get(), null) && DefaultTypeTransformation.booleanUnbox(registeredBean)) {
                    return callSiteArray[15].call(this.parentNode.get(), registeredBean);
                }
                return null;
            }

            public Object getRegPolicy() {
                CallSite[] callSiteArray = _onNodeCompleted_closure2.$getCallSiteArray();
                return this.regPolicy.get();
            }

            public Object getParentNode() {
                CallSite[] callSiteArray = _onNodeCompleted_closure2.$getCallSiteArray();
                return this.parentNode.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _onNodeCompleted_closure2.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "registerMBeanFromMap";
                stringArray[1] = "iterator";
                stringArray[2] = "hasNext";
                stringArray[3] = "next";
                stringArray[4] = "equals";
                stringArray[5] = "name";
                stringArray[6] = "jmxName";
                stringArray[7] = "remove";
                stringArray[8] = "iterator";
                stringArray[9] = "hasNext";
                stringArray[10] = "next";
                stringArray[11] = "equals";
                stringArray[12] = "name";
                stringArray[13] = "jmxName";
                stringArray[14] = "remove";
                stringArray[15] = "add";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[16];
                _onNodeCompleted_closure2.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_onNodeCompleted_closure2.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _onNodeCompleted_closure2.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[7].call(metaMaps, new _onNodeCompleted_closure2(this, this, regPolicy, parentNode2));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != JmxBeansFactory.class) {
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
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "getMBeanServer";
        stringArray[3] = "each";
        stringArray[4] = "getMBeanServer";
        stringArray[5] = "registrationPolicy";
        stringArray[6] = "parentFactory";
        stringArray[7] = "each";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[8];
        JmxBeansFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(JmxBeansFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = JmxBeansFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

