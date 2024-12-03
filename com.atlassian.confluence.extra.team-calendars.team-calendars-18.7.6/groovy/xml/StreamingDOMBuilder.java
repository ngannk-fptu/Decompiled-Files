/*
 * Decompiled with CFR 0.152.
 */
package groovy.xml;

import groovy.lang.Buildable;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.xml.streamingmarkupsupport.AbstractStreamingBuilder;
import groovy.xml.streamingmarkupsupport.BaseMarkupBuilder;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.w3c.dom.Node;

public class StreamingDOMBuilder
extends AbstractStreamingBuilder {
    private Object pendingStack;
    private Object defaultNamespaceStack;
    private Object commentClosure;
    private Object piClosure;
    private Object noopClosure;
    private Object tagClosure;
    private Object builder;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public StreamingDOMBuilder() {
        Object object;
        CallSite[] callSiteArray = StreamingDOMBuilder.$getCallSiteArray();
        List list = ScriptBytecodeAdapter.createList(new Object[0]);
        this.pendingStack = list;
        List list2 = ScriptBytecodeAdapter.createList(new Object[]{""});
        this.defaultNamespaceStack = list2;
        _closure1 _closure110 = new _closure1(this, this);
        this.commentClosure = _closure110;
        _closure2 _closure210 = new _closure2(this, this);
        this.piClosure = _closure210;
        _closure3 _closure310 = new _closure3(this, this);
        this.noopClosure = _closure310;
        _closure4 _closure44 = new _closure4(this, this);
        this.tagClosure = _closure44;
        Object var8_8 = null;
        this.builder = var8_8;
        callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this), ScriptBytecodeAdapter.createMap(new Object[]{"yield", this.noopClosure, "yieldUnescaped", this.noopClosure, "comment", this.commentClosure, "pi", this.piClosure}));
        Map nsSpecificTags = ScriptBytecodeAdapter.createMap(new Object[]{":", ScriptBytecodeAdapter.createList(new Object[]{this.tagClosure, this.tagClosure, ScriptBytecodeAdapter.createMap(new Object[0])}), "http://www.w3.org/2000/xmlns/", ScriptBytecodeAdapter.createList(new Object[]{this.tagClosure, this.tagClosure, ScriptBytecodeAdapter.createMap(new Object[0])}), "http://www.codehaus.org/Groovy/markup/keywords", ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[2].callGroovyObjectGetProperty(this), this.tagClosure, callSiteArray[3].callGroovyObjectGetProperty(this)})});
        this.builder = object = callSiteArray[4].callConstructor(BaseMarkupBuilder.class, nsSpecificTags);
    }

    public Object bind(Object closure) {
        CallSite[] callSiteArray = StreamingDOMBuilder.$getCallSiteArray();
        Reference<Object> boundClosure = new Reference<Object>(callSiteArray[5].call(this.builder, closure));
        public class _bind_closure5
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference boundClosure;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _bind_closure5(Object _outerInstance, Object _thisObject, Reference boundClosure) {
                Reference reference;
                CallSite[] callSiteArray = _bind_closure5.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.boundClosure = reference = boundClosure;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _bind_closure5.$getCallSiteArray();
                if (it instanceof Node) {
                    Object document = callSiteArray[0].call(it);
                    Map map = ScriptBytecodeAdapter.createMap(new Object[]{"document", document, "element", it});
                    ScriptBytecodeAdapter.setProperty(map, null, this.boundClosure.get(), "trigger");
                    return document;
                }
                Object dBuilder = callSiteArray[1].call(DocumentBuilderFactory.class);
                boolean bl = true;
                ScriptBytecodeAdapter.setProperty(bl, null, dBuilder, "namespaceAware");
                Object newDocument = callSiteArray[2].call(callSiteArray[3].call(dBuilder));
                Map map = ScriptBytecodeAdapter.createMap(new Object[]{"document", newDocument, "element", newDocument});
                ScriptBytecodeAdapter.setProperty(map, null, this.boundClosure.get(), "trigger");
                return newDocument;
            }

            public Object getBoundClosure() {
                CallSite[] callSiteArray = _bind_closure5.$getCallSiteArray();
                return this.boundClosure.get();
            }

            public Object doCall() {
                CallSite[] callSiteArray = _bind_closure5.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _bind_closure5.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "getOwnerDocument";
                stringArray[1] = "newInstance";
                stringArray[2] = "newDocument";
                stringArray[3] = "newDocumentBuilder";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[4];
                _bind_closure5.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_bind_closure5.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _bind_closure5.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return new _bind_closure5(this, this, boundClosure);
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != StreamingDOMBuilder.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public Object getPendingStack() {
        return this.pendingStack;
    }

    public void setPendingStack(Object object) {
        this.pendingStack = object;
    }

    public Object getDefaultNamespaceStack() {
        return this.defaultNamespaceStack;
    }

    public void setDefaultNamespaceStack(Object object) {
        this.defaultNamespaceStack = object;
    }

    public Object getCommentClosure() {
        return this.commentClosure;
    }

    public void setCommentClosure(Object object) {
        this.commentClosure = object;
    }

    public Object getPiClosure() {
        return this.piClosure;
    }

    public void setPiClosure(Object object) {
        this.piClosure = object;
    }

    public Object getNoopClosure() {
        return this.noopClosure;
    }

    public void setNoopClosure(Object object) {
        this.noopClosure = object;
    }

    public Object getTagClosure() {
        return this.tagClosure;
    }

    public void setTagClosure(Object object) {
        this.tagClosure = object;
    }

    @Override
    public Object getBuilder() {
        return this.builder;
    }

    @Override
    public void setBuilder(Object object) {
        this.builder = object;
    }

    public /* synthetic */ MetaClass super$2$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    public /* synthetic */ Object super$2$getBuilder() {
        return super.getBuilder();
    }

    public /* synthetic */ void super$2$setBuilder(Object object) {
        super.setBuilder(object);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "putAll";
        stringArray[1] = "specialTags";
        stringArray[2] = "badTagClosure";
        stringArray[3] = "specialTags";
        stringArray[4] = "<$constructor$>";
        stringArray[5] = "bind";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[6];
        StreamingDOMBuilder.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(StreamingDOMBuilder.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = StreamingDOMBuilder.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }

    public class _closure1
    extends Closure
    implements GeneratedClosure {
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private static /* synthetic */ SoftReference $callSiteArray;

        public _closure1(Object _outerInstance, Object _thisObject) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            super(_outerInstance, _thisObject);
        }

        public Object doCall(Object doc, Object pendingNamespaces, Object namespaces, Object namespaceSpecificTags, Object prefix, Object attrs, Object body, Object dom) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            Object comment = callSiteArray[0].call(callSiteArray[1].callGetProperty(dom), body);
            if (ScriptBytecodeAdapter.compareNotEqual(comment, null)) {
                return callSiteArray[2].call(callSiteArray[3].callGetProperty(dom), comment);
            }
            return null;
        }

        public Object call(Object doc, Object pendingNamespaces, Object namespaces, Object namespaceSpecificTags, Object prefix, Object attrs, Object body, Object dom) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            return callSiteArray[4].callCurrent((GroovyObject)this, ArrayUtil.createArray(doc, pendingNamespaces, namespaces, namespaceSpecificTags, prefix, attrs, body, dom));
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != _closure1.class) {
                return ScriptBytecodeAdapter.initMetaClass(this);
            }
            ClassInfo classInfo = $staticClassInfo;
            if (classInfo == null) {
                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
            }
            return classInfo.getMetaClass();
        }

        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
            stringArray[0] = "createComment";
            stringArray[1] = "document";
            stringArray[2] = "appendChild";
            stringArray[3] = "element";
            stringArray[4] = "doCall";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[5];
            _closure1.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(_closure1.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = _closure1.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }

    public class _closure2
    extends Closure
    implements GeneratedClosure {
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private static /* synthetic */ SoftReference $callSiteArray;

        public _closure2(Object _outerInstance, Object _thisObject) {
            CallSite[] callSiteArray = _closure2.$getCallSiteArray();
            super(_outerInstance, _thisObject);
        }

        public Object doCall(Object doc, Object pendingNamespaces, Object namespaces, Object namespaceSpecificTags, Object prefix, Object attrs, Object body, Object dom) {
            Reference<Object> dom2 = new Reference<Object>(dom);
            CallSite[] callSiteArray = _closure2.$getCallSiteArray();
            public class _closure6
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference dom;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _closure6(Object _outerInstance, Object _thisObject, Reference dom) {
                    Reference reference;
                    CallSite[] callSiteArray = _closure6.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.dom = reference = dom;
                }

                public Object doCall(Object target, Object instruction) {
                    CallSite[] callSiteArray = _closure6.$getCallSiteArray();
                    Object pi = null;
                    if (instruction instanceof Map) {
                        Object object;
                        Reference<Object> buf = new Reference<Object>(callSiteArray[0].callConstructor(StringBuffer.class));
                        public class _closure7
                        extends Closure
                        implements GeneratedClosure {
                            private /* synthetic */ Reference buf;
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure7(Object _outerInstance, Object _thisObject, Reference buf) {
                                Reference reference;
                                CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                                this.buf = reference = buf;
                            }

                            public Object doCall(Object name, Object value) {
                                CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(callSiteArray[1].call(value), "\""))) {
                                    return callSiteArray[2].call(this.buf.get(), new GStringImpl(new Object[]{name, value}, new String[]{" ", "='", "'"}));
                                }
                                return callSiteArray[3].call(this.buf.get(), new GStringImpl(new Object[]{name, value}, new String[]{" ", "=\"", "\""}));
                            }

                            public Object call(Object name, Object value) {
                                CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                                return callSiteArray[4].callCurrent(this, name, value);
                            }

                            public Object getBuf() {
                                CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                                return this.buf.get();
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _closure7.class) {
                                    return ScriptBytecodeAdapter.initMetaClass(this);
                                }
                                ClassInfo classInfo = $staticClassInfo;
                                if (classInfo == null) {
                                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                }
                                return classInfo.getMetaClass();
                            }

                            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                stringArray[0] = "contains";
                                stringArray[1] = "toString";
                                stringArray[2] = "append";
                                stringArray[3] = "append";
                                stringArray[4] = "doCall";
                            }

                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                String[] stringArray = new String[5];
                                _closure7.$createCallSiteArray_1(stringArray);
                                return new CallSiteArray(_closure7.class, stringArray);
                            }

                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                CallSiteArray callSiteArray;
                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                    callSiteArray = _closure7.$createCallSiteArray();
                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                }
                                return callSiteArray.array;
                            }
                        }
                        callSiteArray[1].call(instruction, new _closure7(this, this.getThisObject(), buf));
                        pi = object = callSiteArray[2].call(callSiteArray[3].callGetProperty(this.dom.get()), target, callSiteArray[4].call(buf.get()));
                    } else {
                        Object object;
                        pi = object = callSiteArray[5].call(callSiteArray[6].callGetProperty(this.dom.get()), target, instruction);
                    }
                    if (ScriptBytecodeAdapter.compareNotEqual(pi, null)) {
                        return callSiteArray[7].call(callSiteArray[8].callGetProperty(this.dom.get()), pi);
                    }
                    return null;
                }

                public Object call(Object target, Object instruction) {
                    CallSite[] callSiteArray = _closure6.$getCallSiteArray();
                    return callSiteArray[9].callCurrent(this, target, instruction);
                }

                public Object getDom() {
                    CallSite[] callSiteArray = _closure6.$getCallSiteArray();
                    return this.dom.get();
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _closure6.class) {
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
                    stringArray[1] = "each";
                    stringArray[2] = "createProcessingInstruction";
                    stringArray[3] = "document";
                    stringArray[4] = "toString";
                    stringArray[5] = "createProcessingInstruction";
                    stringArray[6] = "document";
                    stringArray[7] = "appendChild";
                    stringArray[8] = "element";
                    stringArray[9] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[10];
                    _closure6.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_closure6.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _closure6.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            return callSiteArray[0].call(attrs, new _closure6(this, this.getThisObject(), dom2));
        }

        public Object call(Object doc, Object pendingNamespaces, Object namespaces, Object namespaceSpecificTags, Object prefix, Object attrs, Object body, Object dom) {
            Reference<Object> dom2 = new Reference<Object>(dom);
            CallSite[] callSiteArray = _closure2.$getCallSiteArray();
            return callSiteArray[1].callCurrent((GroovyObject)this, ArrayUtil.createArray(doc, pendingNamespaces, namespaces, namespaceSpecificTags, prefix, attrs, body, dom2.get()));
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != _closure2.class) {
                return ScriptBytecodeAdapter.initMetaClass(this);
            }
            ClassInfo classInfo = $staticClassInfo;
            if (classInfo == null) {
                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
            }
            return classInfo.getMetaClass();
        }

        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
            stringArray[0] = "each";
            stringArray[1] = "doCall";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[2];
            _closure2.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(_closure2.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = _closure2.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }

    public class _closure3
    extends Closure
    implements GeneratedClosure {
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private static /* synthetic */ SoftReference $callSiteArray;

        public _closure3(Object _outerInstance, Object _thisObject) {
            CallSite[] callSiteArray = _closure3.$getCallSiteArray();
            super(_outerInstance, _thisObject);
        }

        public Object doCall(Object doc, Object pendingNamespaces, Object namespaces, Object namespaceSpecificTags, Object prefix, Object attrs, Object body, Object dom) {
            Reference<Object> doc2 = new Reference<Object>(doc);
            Reference<Object> dom2 = new Reference<Object>(dom);
            CallSite[] callSiteArray = _closure3.$getCallSiteArray();
            if (body instanceof Closure) {
                Object body1 = callSiteArray[0].call(body);
                Object object = doc2.get();
                ScriptBytecodeAdapter.setProperty(object, null, body1, "delegate");
                return callSiteArray[1].call(body1, doc2.get());
            }
            if (body instanceof Buildable) {
                return callSiteArray[2].call(body, doc2.get());
            }
            if (ScriptBytecodeAdapter.compareNotEqual(body, null)) {
                public class _closure8
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference doc;
                    private /* synthetic */ Reference dom;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure8(Object _outerInstance, Object _thisObject, Reference doc, Reference dom) {
                        Reference reference;
                        Reference reference2;
                        CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.doc = reference2 = doc;
                        this.dom = reference = dom;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                        if (it instanceof Closure) {
                            Object body1 = callSiteArray[0].call(it);
                            Object t = this.doc.get();
                            ScriptBytecodeAdapter.setProperty(t, null, body1, "delegate");
                            return callSiteArray[1].call(body1, this.doc.get());
                        }
                        if (it instanceof Buildable) {
                            return callSiteArray[2].call(it, this.doc.get());
                        }
                        return callSiteArray[3].call(callSiteArray[4].callGetProperty(this.dom.get()), callSiteArray[5].call(callSiteArray[6].callGetProperty(this.dom.get()), it));
                    }

                    public Object getDoc() {
                        CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                        return this.doc.get();
                    }

                    public Object getDom() {
                        CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                        return this.dom.get();
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure8.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "clone";
                        stringArray[1] = "call";
                        stringArray[2] = "build";
                        stringArray[3] = "appendChild";
                        stringArray[4] = "element";
                        stringArray[5] = "createTextNode";
                        stringArray[6] = "document";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[7];
                        _closure8.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_closure8.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure8.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                return callSiteArray[3].call(body, new _closure8(this, this.getThisObject(), doc2, dom2));
            }
            return null;
        }

        public Object call(Object doc, Object pendingNamespaces, Object namespaces, Object namespaceSpecificTags, Object prefix, Object attrs, Object body, Object dom) {
            Reference<Object> doc2 = new Reference<Object>(doc);
            Reference<Object> dom2 = new Reference<Object>(dom);
            CallSite[] callSiteArray = _closure3.$getCallSiteArray();
            return callSiteArray[4].callCurrent((GroovyObject)this, ArrayUtil.createArray(doc2.get(), pendingNamespaces, namespaces, namespaceSpecificTags, prefix, attrs, body, dom2.get()));
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != _closure3.class) {
                return ScriptBytecodeAdapter.initMetaClass(this);
            }
            ClassInfo classInfo = $staticClassInfo;
            if (classInfo == null) {
                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
            }
            return classInfo.getMetaClass();
        }

        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
            stringArray[0] = "clone";
            stringArray[1] = "call";
            stringArray[2] = "build";
            stringArray[3] = "each";
            stringArray[4] = "doCall";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[5];
            _closure3.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(_closure3.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = _closure3.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }

    public class _closure4
    extends Closure
    implements GeneratedClosure {
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private static /* synthetic */ SoftReference $callSiteArray;

        public _closure4(Object _outerInstance, Object _thisObject) {
            CallSite[] callSiteArray = _closure4.$getCallSiteArray();
            super(_outerInstance, _thisObject);
        }

        public Object doCall(Object tag, Object doc, Object pendingNamespaces, Object namespaces, Object namespaceSpecificTags, Object prefix, Object attrs, Object body, Object dom) {
            Reference<Object> doc2 = new Reference<Object>(doc);
            Reference<Object> namespaces2 = new Reference<Object>(namespaces);
            Reference<Object> dom2 = new Reference<Object>(dom);
            CallSite[] callSiteArray = _closure4.$getCallSiteArray();
            Reference<List> attributes = new Reference<List>(ScriptBytecodeAdapter.createList(new Object[0]));
            Reference<List> nsAttributes = new Reference<List>(ScriptBytecodeAdapter.createList(new Object[0]));
            Reference<Object> defaultNamespace = new Reference<Object>(callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this)));
            public class _closure9
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference namespaces;
                private /* synthetic */ Reference nsAttributes;
                private /* synthetic */ Reference attributes;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _closure9(Object _outerInstance, Object _thisObject, Reference namespaces, Reference nsAttributes, Reference attributes) {
                    Reference reference;
                    Reference reference2;
                    Reference reference3;
                    CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.namespaces = reference3 = namespaces;
                    this.nsAttributes = reference2 = nsAttributes;
                    this.attributes = reference = attributes;
                }

                public Object doCall(Object key, Object value) {
                    CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(key, "$"))) {
                        Object parts = callSiteArray[1].call(key, "$");
                        Object namespaceUri = null;
                        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call(this.namespaces.get(), callSiteArray[3].call(parts, 0)))) {
                            Object object;
                            namespaceUri = object = callSiteArray[4].call(this.namespaces.get(), callSiteArray[5].call(parts, 0));
                            return callSiteArray[6].call(this.nsAttributes.get(), ScriptBytecodeAdapter.createList(new Object[]{namespaceUri, new GStringImpl(new Object[]{callSiteArray[7].call(parts, 0), callSiteArray[8].call(parts, 1)}, new String[]{"", ":", ""}), new GStringImpl(new Object[]{value}, new String[]{"", ""})}));
                        }
                        throw (Throwable)callSiteArray[9].callConstructor(GroovyRuntimeException.class, new GStringImpl(new Object[]{key}, new String[]{"bad attribute namespace tag in ", ""}));
                    }
                    return callSiteArray[10].call(this.attributes.get(), ScriptBytecodeAdapter.createList(new Object[]{key, value}));
                }

                public Object call(Object key, Object value) {
                    CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                    return callSiteArray[11].callCurrent(this, key, value);
                }

                public Object getNamespaces() {
                    CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                    return this.namespaces.get();
                }

                public Object getNsAttributes() {
                    CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                    return this.nsAttributes.get();
                }

                public Object getAttributes() {
                    CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                    return this.attributes.get();
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _closure9.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "contains";
                    stringArray[1] = "tokenize";
                    stringArray[2] = "containsKey";
                    stringArray[3] = "getAt";
                    stringArray[4] = "getAt";
                    stringArray[5] = "getAt";
                    stringArray[6] = "add";
                    stringArray[7] = "getAt";
                    stringArray[8] = "getAt";
                    stringArray[9] = "<$constructor$>";
                    stringArray[10] = "add";
                    stringArray[11] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[12];
                    _closure9.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_closure9.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _closure9.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[2].call(attrs, new _closure9(this, this.getThisObject(), namespaces2, nsAttributes, attributes));
            Reference<Map> hiddenNamespaces = new Reference<Map>(ScriptBytecodeAdapter.createMap(new Object[0]));
            public class _closure10
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference defaultNamespace;
                private /* synthetic */ Reference nsAttributes;
                private /* synthetic */ Reference hiddenNamespaces;
                private /* synthetic */ Reference namespaces;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _closure10(Object _outerInstance, Object _thisObject, Reference defaultNamespace, Reference nsAttributes, Reference hiddenNamespaces, Reference namespaces) {
                    Reference reference;
                    Reference reference2;
                    Reference reference3;
                    Reference reference4;
                    CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.defaultNamespace = reference4 = defaultNamespace;
                    this.nsAttributes = reference3 = nsAttributes;
                    this.hiddenNamespaces = reference2 = hiddenNamespaces;
                    this.namespaces = reference = namespaces;
                }

                public Object doCall(Object key, Object value) {
                    CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                    if (ScriptBytecodeAdapter.compareEqual(key, ":")) {
                        GStringImpl gStringImpl = new GStringImpl(new Object[]{value}, new String[]{"", ""});
                        this.defaultNamespace.set(gStringImpl);
                        return callSiteArray[0].call(this.nsAttributes.get(), ScriptBytecodeAdapter.createList(new Object[]{"http://www.w3.org/2000/xmlns/", "xmlns", this.defaultNamespace.get()}));
                    }
                    Object object = callSiteArray[1].call(this.namespaces.get(), key);
                    callSiteArray[2].call(this.hiddenNamespaces.get(), key, object);
                    Object object2 = value;
                    callSiteArray[3].call(this.namespaces.get(), key, object2);
                    return callSiteArray[4].call(this.nsAttributes.get(), ScriptBytecodeAdapter.createList(new Object[]{"http://www.w3.org/2000/xmlns/", new GStringImpl(new Object[]{key}, new String[]{"xmlns:", ""}), new GStringImpl(new Object[]{value}, new String[]{"", ""})}));
                }

                public Object call(Object key, Object value) {
                    CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                    return callSiteArray[5].callCurrent(this, key, value);
                }

                public Object getDefaultNamespace() {
                    CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                    return this.defaultNamespace.get();
                }

                public Object getNsAttributes() {
                    CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                    return this.nsAttributes.get();
                }

                public Object getHiddenNamespaces() {
                    CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                    return this.hiddenNamespaces.get();
                }

                public Object getNamespaces() {
                    CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                    return this.namespaces.get();
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _closure10.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "add";
                    stringArray[1] = "getAt";
                    stringArray[2] = "putAt";
                    stringArray[3] = "putAt";
                    stringArray[4] = "add";
                    stringArray[5] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[6];
                    _closure10.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_closure10.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _closure10.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[3].call(pendingNamespaces, new _closure10(this, this.getThisObject(), defaultNamespace, nsAttributes, hiddenNamespaces, namespaces2));
            Object uri = defaultNamespace.get();
            Object qualifiedName = tag;
            if (ScriptBytecodeAdapter.compareNotEqual(prefix, "")) {
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[4].call(namespaces2.get(), prefix))) {
                    Object object;
                    uri = object = callSiteArray[5].call(namespaces2.get(), prefix);
                } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[6].call(pendingNamespaces, prefix))) {
                    Object object;
                    uri = object = callSiteArray[7].call(pendingNamespaces, prefix);
                } else {
                    throw (Throwable)callSiteArray[8].callConstructor(GroovyRuntimeException.class, new GStringImpl(new Object[]{prefix}, new String[]{"Namespace prefix: ", " is not bound to a URI"}));
                }
                if (ScriptBytecodeAdapter.compareNotEqual(prefix, ":")) {
                    Object object;
                    qualifiedName = object = callSiteArray[9].call(callSiteArray[10].call(prefix, ":"), tag);
                }
            }
            Reference<Object> element = new Reference<Object>(callSiteArray[11].call(callSiteArray[12].callGetProperty(dom2.get()), uri, qualifiedName));
            public class _closure11
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference element;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _closure11(Object _outerInstance, Object _thisObject, Reference element) {
                    Reference reference;
                    CallSite[] callSiteArray = _closure11.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.element = reference = element;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _closure11.$getCallSiteArray();
                    return callSiteArray[0].call(this.element.get(), callSiteArray[1].call(it, 0), callSiteArray[2].call(it, 1), callSiteArray[3].call(it, 2));
                }

                public Object getElement() {
                    CallSite[] callSiteArray = _closure11.$getCallSiteArray();
                    return this.element.get();
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _closure11.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _closure11.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "setAttributeNS";
                    stringArray[1] = "getAt";
                    stringArray[2] = "getAt";
                    stringArray[3] = "getAt";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[4];
                    _closure11.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_closure11.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _closure11.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[13].call((Object)nsAttributes.get(), new _closure11(this, this.getThisObject(), element));
            public class _closure12
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference element;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _closure12(Object _outerInstance, Object _thisObject, Reference element) {
                    Reference reference;
                    CallSite[] callSiteArray = _closure12.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.element = reference = element;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _closure12.$getCallSiteArray();
                    return callSiteArray[0].call(this.element.get(), callSiteArray[1].call(it, 0), callSiteArray[2].call(it, 1));
                }

                public Object getElement() {
                    CallSite[] callSiteArray = _closure12.$getCallSiteArray();
                    return this.element.get();
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _closure12.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _closure12.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "setAttribute";
                    stringArray[1] = "getAt";
                    stringArray[2] = "getAt";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[3];
                    _closure12.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_closure12.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _closure12.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[14].call((Object)attributes.get(), new _closure12(this, this.getThisObject(), element));
            callSiteArray[15].call(callSiteArray[16].callGetProperty(dom2.get()), element.get());
            Object object = element.get();
            ScriptBytecodeAdapter.setProperty(object, null, dom2.get(), "element");
            if (ScriptBytecodeAdapter.compareNotEqual(body, null)) {
                callSiteArray[17].call(callSiteArray[18].callGroovyObjectGetProperty(this), defaultNamespace.get());
                callSiteArray[19].call(callSiteArray[20].callGroovyObjectGetProperty(this), callSiteArray[21].call(pendingNamespaces));
                callSiteArray[22].call(pendingNamespaces);
                if (body instanceof Closure) {
                    Object body1 = callSiteArray[23].call(body);
                    Object object2 = doc2.get();
                    ScriptBytecodeAdapter.setProperty(object2, null, body1, "delegate");
                    callSiteArray[24].call(body1, doc2.get());
                } else if (body instanceof Buildable) {
                    callSiteArray[25].call(body, doc2.get());
                } else {
                    public class _closure13
                    extends Closure
                    implements GeneratedClosure {
                        private /* synthetic */ Reference doc;
                        private /* synthetic */ Reference dom;
                        private static /* synthetic */ ClassInfo $staticClassInfo;
                        public static transient /* synthetic */ boolean __$stMC;
                        private static /* synthetic */ SoftReference $callSiteArray;

                        public _closure13(Object _outerInstance, Object _thisObject, Reference doc, Reference dom) {
                            Reference reference;
                            Reference reference2;
                            CallSite[] callSiteArray = _closure13.$getCallSiteArray();
                            super(_outerInstance, _thisObject);
                            this.doc = reference2 = doc;
                            this.dom = reference = dom;
                        }

                        public Object doCall(Object it) {
                            CallSite[] callSiteArray = _closure13.$getCallSiteArray();
                            if (it instanceof Closure) {
                                Object body1 = callSiteArray[0].call(it);
                                Object t = this.doc.get();
                                ScriptBytecodeAdapter.setProperty(t, null, body1, "delegate");
                                return callSiteArray[1].call(body1, this.doc.get());
                            }
                            if (it instanceof Buildable) {
                                return callSiteArray[2].call(it, this.doc.get());
                            }
                            return callSiteArray[3].call(callSiteArray[4].callGetProperty(this.dom.get()), callSiteArray[5].call(callSiteArray[6].callGetProperty(this.dom.get()), it));
                        }

                        public Object getDoc() {
                            CallSite[] callSiteArray = _closure13.$getCallSiteArray();
                            return this.doc.get();
                        }

                        public Object getDom() {
                            CallSite[] callSiteArray = _closure13.$getCallSiteArray();
                            return this.dom.get();
                        }

                        public Object doCall() {
                            CallSite[] callSiteArray = _closure13.$getCallSiteArray();
                            return this.doCall(null);
                        }

                        protected /* synthetic */ MetaClass $getStaticMetaClass() {
                            if (this.getClass() != _closure13.class) {
                                return ScriptBytecodeAdapter.initMetaClass(this);
                            }
                            ClassInfo classInfo = $staticClassInfo;
                            if (classInfo == null) {
                                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                            }
                            return classInfo.getMetaClass();
                        }

                        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                            stringArray[0] = "clone";
                            stringArray[1] = "call";
                            stringArray[2] = "build";
                            stringArray[3] = "appendChild";
                            stringArray[4] = "element";
                            stringArray[5] = "createTextNode";
                            stringArray[6] = "document";
                        }

                        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                            String[] stringArray = new String[7];
                            _closure13.$createCallSiteArray_1(stringArray);
                            return new CallSiteArray(_closure13.class, stringArray);
                        }

                        private static /* synthetic */ CallSite[] $getCallSiteArray() {
                            CallSiteArray callSiteArray;
                            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                callSiteArray = _closure13.$createCallSiteArray();
                                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                            }
                            return callSiteArray.array;
                        }
                    }
                    callSiteArray[26].call(body, new _closure13(this, this.getThisObject(), doc2, dom2));
                }
                callSiteArray[27].call(pendingNamespaces);
                callSiteArray[28].call(pendingNamespaces, callSiteArray[29].call(callSiteArray[30].callGroovyObjectGetProperty(this)));
                callSiteArray[31].call(callSiteArray[32].callGroovyObjectGetProperty(this));
            }
            Object object3 = callSiteArray[33].call(callSiteArray[34].callGetProperty(dom2.get()));
            ScriptBytecodeAdapter.setProperty(object3, null, dom2.get(), "element");
            public class _closure14
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference namespaces;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _closure14(Object _outerInstance, Object _thisObject, Reference namespaces) {
                    Reference reference;
                    CallSite[] callSiteArray = _closure14.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.namespaces = reference = namespaces;
                }

                public Object doCall(Object key, Object value) {
                    CallSite[] callSiteArray = _closure14.$getCallSiteArray();
                    if (ScriptBytecodeAdapter.compareEqual(value, null)) {
                        return callSiteArray[0].call(this.namespaces.get(), key);
                    }
                    Object object = value;
                    callSiteArray[1].call(this.namespaces.get(), key, object);
                    return object;
                }

                public Object call(Object key, Object value) {
                    CallSite[] callSiteArray = _closure14.$getCallSiteArray();
                    return callSiteArray[2].callCurrent(this, key, value);
                }

                public Object getNamespaces() {
                    CallSite[] callSiteArray = _closure14.$getCallSiteArray();
                    return this.namespaces.get();
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _closure14.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "remove";
                    stringArray[1] = "putAt";
                    stringArray[2] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[3];
                    _closure14.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_closure14.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _closure14.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            return callSiteArray[35].call((Object)hiddenNamespaces.get(), new _closure14(this, this.getThisObject(), namespaces2));
        }

        public Object call(Object tag, Object doc, Object pendingNamespaces, Object namespaces, Object namespaceSpecificTags, Object prefix, Object attrs, Object body, Object dom) {
            Reference<Object> doc2 = new Reference<Object>(doc);
            Reference<Object> namespaces2 = new Reference<Object>(namespaces);
            Reference<Object> dom2 = new Reference<Object>(dom);
            CallSite[] callSiteArray = _closure4.$getCallSiteArray();
            return callSiteArray[36].callCurrent((GroovyObject)this, ArrayUtil.createArray(tag, doc2.get(), pendingNamespaces, namespaces2.get(), namespaceSpecificTags, prefix, attrs, body, dom2.get()));
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != _closure4.class) {
                return ScriptBytecodeAdapter.initMetaClass(this);
            }
            ClassInfo classInfo = $staticClassInfo;
            if (classInfo == null) {
                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
            }
            return classInfo.getMetaClass();
        }

        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
            stringArray[0] = "last";
            stringArray[1] = "defaultNamespaceStack";
            stringArray[2] = "each";
            stringArray[3] = "each";
            stringArray[4] = "containsKey";
            stringArray[5] = "getAt";
            stringArray[6] = "containsKey";
            stringArray[7] = "getAt";
            stringArray[8] = "<$constructor$>";
            stringArray[9] = "plus";
            stringArray[10] = "plus";
            stringArray[11] = "createElementNS";
            stringArray[12] = "document";
            stringArray[13] = "each";
            stringArray[14] = "each";
            stringArray[15] = "appendChild";
            stringArray[16] = "element";
            stringArray[17] = "push";
            stringArray[18] = "defaultNamespaceStack";
            stringArray[19] = "add";
            stringArray[20] = "pendingStack";
            stringArray[21] = "clone";
            stringArray[22] = "clear";
            stringArray[23] = "clone";
            stringArray[24] = "call";
            stringArray[25] = "build";
            stringArray[26] = "each";
            stringArray[27] = "clear";
            stringArray[28] = "putAll";
            stringArray[29] = "pop";
            stringArray[30] = "pendingStack";
            stringArray[31] = "pop";
            stringArray[32] = "defaultNamespaceStack";
            stringArray[33] = "getParentNode";
            stringArray[34] = "element";
            stringArray[35] = "each";
            stringArray[36] = "doCall";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[37];
            _closure4.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(_closure4.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = _closure4.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }
}

