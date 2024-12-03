/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovy.lang.Closure
 *  groovy.lang.GroovyObject
 *  groovy.lang.GroovyObjectSupport
 *  groovy.lang.MetaClass
 *  groovy.lang.Reference
 *  groovy.transform.Generated
 *  groovy.xml.StreamingMarkupBuilder
 *  org.codehaus.groovy.reflection.ClassInfo
 *  org.codehaus.groovy.runtime.BytecodeInterface8
 *  org.codehaus.groovy.runtime.GStringImpl
 *  org.codehaus.groovy.runtime.GeneratedClosure
 *  org.codehaus.groovy.runtime.ScriptBytecodeAdapter
 *  org.codehaus.groovy.runtime.callsite.CallSite
 *  org.codehaus.groovy.runtime.callsite.CallSiteArray
 *  org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation
 *  org.codehaus.groovy.runtime.typehandling.ShortTypeHandling
 */
package org.springframework.beans.factory.groovy;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.transform.Generated;
import groovy.xml.StreamingMarkupBuilder;
import java.io.StringWriter;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.Map;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.groovy.GroovyBeanDefinitionWrapper;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.w3c.dom.Element;

class GroovyDynamicElementReader
extends GroovyObjectSupport {
    private final String rootNamespace;
    private final Map<String, String> xmlNamespaces;
    private final BeanDefinitionParserDelegate delegate;
    private final GroovyBeanDefinitionWrapper beanDefinition;
    protected final boolean decorating;
    private boolean callAfterInvocation;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public GroovyDynamicElementReader(String namespace, Map<String, String> namespaceMap, BeanDefinitionParserDelegate delegate, GroovyBeanDefinitionWrapper beanDefinition, boolean decorating) {
        boolean bl;
        GroovyBeanDefinitionWrapper groovyBeanDefinitionWrapper;
        BeanDefinitionParserDelegate beanDefinitionParserDelegate;
        String string;
        boolean bl2;
        CallSite[] callSiteArray = GroovyDynamicElementReader.$getCallSiteArray();
        this.callAfterInvocation = bl2 = true;
        this.rootNamespace = string = namespace;
        Map<String, String> map = namespaceMap;
        this.xmlNamespaces = map;
        this.delegate = beanDefinitionParserDelegate = delegate;
        this.beanDefinition = groovyBeanDefinitionWrapper = beanDefinition;
        this.decorating = bl = decorating;
    }

    /*
     * WARNING - void declaration
     */
    public Object invokeMethod(String name, Object args) {
        void var2_2;
        Reference name2 = new Reference((Object)name);
        Reference args2 = new Reference((Object)var2_2);
        CallSite[] callSiteArray = GroovyDynamicElementReader.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox((Object)callSiteArray[0].call((Object)((String)name2.get()), (Object)"doCall"))) {
            Object callable = callSiteArray[1].call(args2.get(), (Object)0);
            Object object = callSiteArray[2].callGetProperty(Closure.class);
            ScriptBytecodeAdapter.setProperty((Object)object, null, (Object)callable, (String)"resolveStrategy");
            GroovyDynamicElementReader groovyDynamicElementReader = this;
            ScriptBytecodeAdapter.setProperty((Object)((Object)groovyDynamicElementReader), null, (Object)callable, (String)"delegate");
            Object result = callSiteArray[3].call(callable);
            if (this.callAfterInvocation) {
                boolean bl;
                if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    callSiteArray[4].callCurrent((GroovyObject)this);
                } else {
                    this.afterInvocation();
                }
                this.callAfterInvocation = bl = false;
            }
            return result;
        }
        Reference builder = new Reference((Object)((StreamingMarkupBuilder)ScriptBytecodeAdapter.castToType((Object)callSiteArray[5].callConstructor(StreamingMarkupBuilder.class), StreamingMarkupBuilder.class)));
        Reference myNamespace = new Reference((Object)this.rootNamespace);
        Reference myNamespaces = new Reference(this.xmlNamespaces);
        public final class _invokeMethod_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference myNamespaces;
            private /* synthetic */ Reference args;
            private /* synthetic */ Reference builder;
            private /* synthetic */ Reference myNamespace;
            private /* synthetic */ Reference name;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _invokeMethod_closure1(Object _outerInstance, Object _thisObject, Reference myNamespaces, Reference args, Reference builder, Reference myNamespace, Reference name) {
                Reference reference;
                Reference reference2;
                Reference reference3;
                Reference reference4;
                Reference reference5;
                CallSite[] callSiteArray = _invokeMethod_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.myNamespaces = reference5 = myNamespaces;
                this.args = reference4 = args;
                this.builder = reference3 = builder;
                this.myNamespace = reference2 = myNamespace;
                this.name = reference = name;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _invokeMethod_closure1.$getCallSiteArray();
                Object namespace = null;
                Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType((Object)callSiteArray[0].call(this.myNamespaces.get()), Iterator.class);
                while (iterator.hasNext()) {
                    namespace = iterator.next();
                    callSiteArray[1].call(callSiteArray[2].callGroovyObjectGetProperty((Object)this), (Object)ScriptBytecodeAdapter.createMap((Object[])new Object[]{callSiteArray[3].callGetProperty(namespace), callSiteArray[4].callGetProperty(namespace)}));
                }
                if (DefaultTypeTransformation.booleanUnbox((Object)this.args.get()) && callSiteArray[5].call(this.args.get(), (Object)-1) instanceof Closure) {
                    Object object = callSiteArray[6].callGetProperty(Closure.class);
                    ScriptBytecodeAdapter.setProperty((Object)object, null, (Object)callSiteArray[7].call(this.args.get(), (Object)-1), (String)"resolveStrategy");
                    Object object2 = this.builder.get();
                    ScriptBytecodeAdapter.setProperty((Object)object2, null, (Object)callSiteArray[8].call(this.args.get(), (Object)-1), (String)"delegate");
                }
                return ScriptBytecodeAdapter.invokeMethodN(_invokeMethod_closure1.class, (Object)ScriptBytecodeAdapter.getProperty(_invokeMethod_closure1.class, (Object)callSiteArray[9].callGroovyObjectGetProperty((Object)this), (String)ShortTypeHandling.castToString((Object)new GStringImpl(new Object[]{this.myNamespace.get()}, new String[]{"", ""}))), (String)ShortTypeHandling.castToString((Object)new GStringImpl(new Object[]{this.name.get()}, new String[]{"", ""})), (Object[])ScriptBytecodeAdapter.despreadList((Object[])new Object[0], (Object[])new Object[]{this.args.get()}, (int[])new int[]{0}));
            }

            @Generated
            public Object getMyNamespaces() {
                CallSite[] callSiteArray = _invokeMethod_closure1.$getCallSiteArray();
                return this.myNamespaces.get();
            }

            @Generated
            public Object getArgs() {
                CallSite[] callSiteArray = _invokeMethod_closure1.$getCallSiteArray();
                return this.args.get();
            }

            @Generated
            public StreamingMarkupBuilder getBuilder() {
                CallSite[] callSiteArray = _invokeMethod_closure1.$getCallSiteArray();
                return (StreamingMarkupBuilder)ScriptBytecodeAdapter.castToType((Object)this.builder.get(), StreamingMarkupBuilder.class);
            }

            @Generated
            public Object getMyNamespace() {
                CallSite[] callSiteArray = _invokeMethod_closure1.$getCallSiteArray();
                return this.myNamespace.get();
            }

            @Generated
            public String getName() {
                CallSite[] callSiteArray = _invokeMethod_closure1.$getCallSiteArray();
                return ShortTypeHandling.castToString((Object)this.name.get());
            }

            @Generated
            public Object doCall() {
                CallSite[] callSiteArray = _invokeMethod_closure1.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (((Object)((Object)this)).getClass() != _invokeMethod_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass((Object)((Object)this));
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(((Object)((Object)this)).getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "iterator";
                stringArray[1] = "declareNamespace";
                stringArray[2] = "mkp";
                stringArray[3] = "key";
                stringArray[4] = "value";
                stringArray[5] = "getAt";
                stringArray[6] = "DELEGATE_FIRST";
                stringArray[7] = "getAt";
                stringArray[8] = "getAt";
                stringArray[9] = "delegate";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[10];
                _invokeMethod_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_invokeMethod_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _invokeMethod_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        _invokeMethod_closure1 callable = new _invokeMethod_closure1((Object)this, (Object)this, myNamespaces, args2, builder, myNamespace, name2);
        Object object = callSiteArray[6].callGetProperty(Closure.class);
        ScriptBytecodeAdapter.setProperty((Object)object, null, (Object)((Object)callable), (String)"resolveStrategy");
        StreamingMarkupBuilder streamingMarkupBuilder = (StreamingMarkupBuilder)builder.get();
        ScriptBytecodeAdapter.setProperty((Object)streamingMarkupBuilder, null, (Object)((Object)callable), (String)"delegate");
        Object writable = callSiteArray[7].call((Object)((StreamingMarkupBuilder)builder.get()), (Object)callable);
        Object sw = callSiteArray[8].callConstructor(StringWriter.class);
        callSiteArray[9].call(writable, sw);
        Element element = (Element)ScriptBytecodeAdapter.castToType((Object)callSiteArray[10].callGetProperty(callSiteArray[11].call(callSiteArray[12].callGetProperty((Object)this.delegate), callSiteArray[13].call(sw))), Element.class);
        callSiteArray[14].call((Object)this.delegate, (Object)element);
        if (this.decorating) {
            BeanDefinitionHolder holder = (BeanDefinitionHolder)ScriptBytecodeAdapter.castToType((Object)callSiteArray[15].callGetProperty((Object)this.beanDefinition), BeanDefinitionHolder.class);
            Object object2 = callSiteArray[16].call((Object)this.delegate, (Object)element, (Object)holder, null);
            holder = (BeanDefinitionHolder)ScriptBytecodeAdapter.castToType((Object)object2, BeanDefinitionHolder.class);
            callSiteArray[17].call((Object)this.beanDefinition, (Object)holder);
        } else {
            Object beanDefinition = callSiteArray[18].call((Object)this.delegate, (Object)element);
            if (DefaultTypeTransformation.booleanUnbox((Object)beanDefinition)) {
                callSiteArray[19].call((Object)this.beanDefinition, beanDefinition);
            }
        }
        if (this.callAfterInvocation) {
            boolean bl;
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                callSiteArray[20].callCurrent((GroovyObject)this);
            } else {
                this.afterInvocation();
            }
            this.callAfterInvocation = bl = false;
        }
        return element;
    }

    protected void afterInvocation() {
        CallSite[] callSiteArray = GroovyDynamicElementReader.$getCallSiteArray();
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (((Object)((Object)this)).getClass() != GroovyDynamicElementReader.class) {
            return ScriptBytecodeAdapter.initMetaClass((Object)((Object)this));
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(((Object)((Object)this)).getClass());
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ Object super$2$invokeMethod(String string, Object object) {
        return super.invokeMethod(string, object);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "equals";
        stringArray[1] = "getAt";
        stringArray[2] = "DELEGATE_FIRST";
        stringArray[3] = "call";
        stringArray[4] = "afterInvocation";
        stringArray[5] = "<$constructor$>";
        stringArray[6] = "DELEGATE_FIRST";
        stringArray[7] = "bind";
        stringArray[8] = "<$constructor$>";
        stringArray[9] = "writeTo";
        stringArray[10] = "documentElement";
        stringArray[11] = "readDocumentFromString";
        stringArray[12] = "readerContext";
        stringArray[13] = "toString";
        stringArray[14] = "initDefaults";
        stringArray[15] = "beanDefinitionHolder";
        stringArray[16] = "decorateIfRequired";
        stringArray[17] = "setBeanDefinitionHolder";
        stringArray[18] = "parseCustomElement";
        stringArray[19] = "setBeanDefinition";
        stringArray[20] = "afterInvocation";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[21];
        GroovyDynamicElementReader.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(GroovyDynamicElementReader.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = GroovyDynamicElementReader.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

