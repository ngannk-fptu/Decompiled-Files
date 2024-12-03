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
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

public class StreamingSAXBuilder
extends AbstractStreamingBuilder {
    private Object pendingStack;
    private Object commentClosure;
    private Object piClosure;
    private Object noopClosure;
    private Object tagClosure;
    private Object builder;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public StreamingSAXBuilder() {
        Object object;
        CallSite[] callSiteArray = StreamingSAXBuilder.$getCallSiteArray();
        List list = ScriptBytecodeAdapter.createList(new Object[0]);
        this.pendingStack = list;
        _closure1 _closure110 = new _closure1(this, this);
        this.commentClosure = _closure110;
        _closure2 _closure210 = new _closure2(this, this);
        this.piClosure = _closure210;
        _closure3 _closure310 = new _closure3(this, this);
        this.noopClosure = _closure310;
        _closure4 _closure44 = new _closure4(this, this);
        this.tagClosure = _closure44;
        Object var7_7 = null;
        this.builder = var7_7;
        callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this), ScriptBytecodeAdapter.createMap(new Object[]{"yield", this.noopClosure, "yieldUnescaped", this.noopClosure, "comment", this.commentClosure, "pi", this.piClosure}));
        Map nsSpecificTags = ScriptBytecodeAdapter.createMap(new Object[]{":", ScriptBytecodeAdapter.createList(new Object[]{this.tagClosure, this.tagClosure, ScriptBytecodeAdapter.createMap(new Object[0])}), "http://www.w3.org/XML/1998/namespace", ScriptBytecodeAdapter.createList(new Object[]{this.tagClosure, this.tagClosure, ScriptBytecodeAdapter.createMap(new Object[0])}), "http://www.codehaus.org/Groovy/markup/keywords", ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[2].callGroovyObjectGetProperty(this), this.tagClosure, callSiteArray[3].callGroovyObjectGetProperty(this)})});
        this.builder = object = callSiteArray[4].callConstructor(BaseMarkupBuilder.class, nsSpecificTags);
    }

    private Object addAttributes(AttributesImpl attributes, Object key, Object value, Object namespaces) {
        CallSite[] callSiteArray = StreamingSAXBuilder.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[5].call(key, "$"))) {
            Object parts = callSiteArray[6].call(key, "$");
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[7].call(namespaces, callSiteArray[8].call(parts, 0)))) {
                Object namespaceUri = callSiteArray[9].call(namespaces, callSiteArray[10].call(parts, 0));
                return callSiteArray[11].call((Object)attributes, ArrayUtil.createArray(namespaceUri, callSiteArray[12].call(parts, 1), new GStringImpl(new Object[]{callSiteArray[13].call(parts, 0), callSiteArray[14].call(parts, 1)}, new String[]{"", ":", ""}), "CDATA", new GStringImpl(new Object[]{value}, new String[]{"", ""})));
            }
            throw (Throwable)callSiteArray[15].callConstructor(GroovyRuntimeException.class, new GStringImpl(new Object[]{key}, new String[]{"bad attribute namespace tag in ", ""}));
        }
        return callSiteArray[16].call((Object)attributes, ArrayUtil.createArray("", key, key, "CDATA", new GStringImpl(new Object[]{value}, new String[]{"", ""})));
    }

    /*
     * WARNING - void declaration
     */
    private Object processBody(Object body, Object doc, Object contentHandler) {
        void var3_3;
        Reference<Object> doc2 = new Reference<Object>(doc);
        Reference<void> contentHandler2 = new Reference<void>(var3_3);
        CallSite[] callSiteArray = StreamingSAXBuilder.$getCallSiteArray();
        if (body instanceof Closure) {
            Object body1 = callSiteArray[17].call(body);
            Object object = doc2.get();
            ScriptBytecodeAdapter.setProperty(object, null, body1, "delegate");
            return callSiteArray[18].call(body1, doc2.get());
        }
        if (body instanceof Buildable) {
            return callSiteArray[19].call(body, doc2.get());
        }
        public class _processBody_closure5
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference doc;
            private /* synthetic */ Reference contentHandler;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _processBody_closure5(Object _outerInstance, Object _thisObject, Reference doc, Reference contentHandler) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _processBody_closure5.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.doc = reference2 = doc;
                this.contentHandler = reference = contentHandler;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _processBody_closure5.$getCallSiteArray();
                return callSiteArray[0].callCurrent(this, it, this.doc.get(), this.contentHandler.get());
            }

            public Object getDoc() {
                CallSite[] callSiteArray = _processBody_closure5.$getCallSiteArray();
                return this.doc.get();
            }

            public Object getContentHandler() {
                CallSite[] callSiteArray = _processBody_closure5.$getCallSiteArray();
                return this.contentHandler.get();
            }

            public Object doCall() {
                CallSite[] callSiteArray = _processBody_closure5.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _processBody_closure5.class) {
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
                stringArray[0] = "processBodyPart";
                return new CallSiteArray(_processBody_closure5.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _processBody_closure5.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return callSiteArray[20].call(body, new _processBody_closure5(this, this, doc2, contentHandler2));
    }

    private Object processBodyPart(Object part, Object doc, Object contentHandler) {
        CallSite[] callSiteArray = StreamingSAXBuilder.$getCallSiteArray();
        if (part instanceof Closure) {
            Object body1 = callSiteArray[21].call(part);
            Object object = doc;
            ScriptBytecodeAdapter.setProperty(object, null, body1, "delegate");
            return callSiteArray[22].call(body1, doc);
        }
        if (part instanceof Buildable) {
            return callSiteArray[23].call(part, doc);
        }
        Object chars = callSiteArray[24].call(part);
        return callSiteArray[25].call(contentHandler, chars, 0, callSiteArray[26].call(chars));
    }

    public Object bind(Object closure) {
        CallSite[] callSiteArray = StreamingSAXBuilder.$getCallSiteArray();
        Reference<Object> boundClosure = new Reference<Object>(callSiteArray[27].call(this.builder, closure));
        public class _bind_closure6
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference boundClosure;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _bind_closure6(Object _outerInstance, Object _thisObject, Reference boundClosure) {
                Reference reference;
                CallSite[] callSiteArray = _bind_closure6.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.boundClosure = reference = boundClosure;
            }

            public Object doCall(Object contentHandler) {
                CallSite[] callSiteArray = _bind_closure6.$getCallSiteArray();
                callSiteArray[0].call(contentHandler);
                Object object = contentHandler;
                ScriptBytecodeAdapter.setProperty(object, null, this.boundClosure.get(), "trigger");
                return callSiteArray[1].call(contentHandler);
            }

            public Object getBoundClosure() {
                CallSite[] callSiteArray = _bind_closure6.$getCallSiteArray();
                return this.boundClosure.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _bind_closure6.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "startDocument";
                stringArray[1] = "endDocument";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _bind_closure6.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_bind_closure6.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _bind_closure6.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return new _bind_closure6(this, this, boundClosure);
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != StreamingSAXBuilder.class) {
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
        stringArray[5] = "contains";
        stringArray[6] = "tokenize";
        stringArray[7] = "containsKey";
        stringArray[8] = "getAt";
        stringArray[9] = "getAt";
        stringArray[10] = "getAt";
        stringArray[11] = "addAttribute";
        stringArray[12] = "getAt";
        stringArray[13] = "getAt";
        stringArray[14] = "getAt";
        stringArray[15] = "<$constructor$>";
        stringArray[16] = "addAttribute";
        stringArray[17] = "clone";
        stringArray[18] = "call";
        stringArray[19] = "build";
        stringArray[20] = "each";
        stringArray[21] = "clone";
        stringArray[22] = "call";
        stringArray[23] = "build";
        stringArray[24] = "toCharArray";
        stringArray[25] = "characters";
        stringArray[26] = "size";
        stringArray[27] = "bind";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[28];
        StreamingSAXBuilder.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(StreamingSAXBuilder.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = StreamingSAXBuilder.$createCallSiteArray();
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

        public Object doCall(Object doc, Object pendingNamespaces, Object namespaces, Object namespaceSpecificTags, Object prefix, Object attrs, Object body, Object contentHandler) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            if (contentHandler instanceof LexicalHandler) {
                return callSiteArray[0].call(contentHandler, callSiteArray[1].call(body), 0, callSiteArray[2].call(body));
            }
            return null;
        }

        public Object call(Object doc, Object pendingNamespaces, Object namespaces, Object namespaceSpecificTags, Object prefix, Object attrs, Object body, Object contentHandler) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            return callSiteArray[3].callCurrent((GroovyObject)this, ArrayUtil.createArray(doc, pendingNamespaces, namespaces, namespaceSpecificTags, prefix, attrs, body, contentHandler));
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
            stringArray[0] = "comment";
            stringArray[1] = "toCharArray";
            stringArray[2] = "size";
            stringArray[3] = "doCall";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[4];
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

        public Object doCall(Object doc, Object pendingNamespaces, Object namespaces, Object namespaceSpecificTags, Object prefix, Object attrs, Object body, Object contentHandler) {
            Reference<Object> contentHandler2 = new Reference<Object>(contentHandler);
            CallSite[] callSiteArray = _closure2.$getCallSiteArray();
            public class _closure7
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference contentHandler;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _closure7(Object _outerInstance, Object _thisObject, Reference contentHandler) {
                    Reference reference;
                    CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.contentHandler = reference = contentHandler;
                }

                public Object doCall(Object target, Object instruction) {
                    CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                    if (instruction instanceof Map) {
                        Reference<Object> buf = new Reference<Object>(callSiteArray[0].callConstructor(StringBuffer.class));
                        public class _closure8
                        extends Closure
                        implements GeneratedClosure {
                            private /* synthetic */ Reference buf;
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure8(Object _outerInstance, Object _thisObject, Reference buf) {
                                Reference reference;
                                CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                                this.buf = reference = buf;
                            }

                            public Object doCall(Object name, Object value) {
                                CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(callSiteArray[1].call(value), "\""))) {
                                    return callSiteArray[2].call(this.buf.get(), new GStringImpl(new Object[]{name, value}, new String[]{" ", "='", "'"}));
                                }
                                return callSiteArray[3].call(this.buf.get(), new GStringImpl(new Object[]{name, value}, new String[]{" ", "=\"", "\""}));
                            }

                            public Object call(Object name, Object value) {
                                CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                                return callSiteArray[4].callCurrent(this, name, value);
                            }

                            public Object getBuf() {
                                CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                                return this.buf.get();
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
                                stringArray[0] = "contains";
                                stringArray[1] = "toString";
                                stringArray[2] = "append";
                                stringArray[3] = "append";
                                stringArray[4] = "doCall";
                            }

                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                String[] stringArray = new String[5];
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
                        callSiteArray[1].call(instruction, new _closure8(this, this.getThisObject(), buf));
                        return callSiteArray[2].call(this.contentHandler.get(), target, callSiteArray[3].call(buf.get()));
                    }
                    return callSiteArray[4].call(this.contentHandler.get(), target, instruction);
                }

                public Object call(Object target, Object instruction) {
                    CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                    return callSiteArray[5].callCurrent(this, target, instruction);
                }

                public Object getContentHandler() {
                    CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                    return this.contentHandler.get();
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
                    stringArray[0] = "<$constructor$>";
                    stringArray[1] = "each";
                    stringArray[2] = "processingInstruction";
                    stringArray[3] = "toString";
                    stringArray[4] = "processingInstruction";
                    stringArray[5] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[6];
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
            return callSiteArray[0].call(attrs, new _closure7(this, this.getThisObject(), contentHandler2));
        }

        public Object call(Object doc, Object pendingNamespaces, Object namespaces, Object namespaceSpecificTags, Object prefix, Object attrs, Object body, Object contentHandler) {
            Reference<Object> contentHandler2 = new Reference<Object>(contentHandler);
            CallSite[] callSiteArray = _closure2.$getCallSiteArray();
            return callSiteArray[1].callCurrent((GroovyObject)this, ArrayUtil.createArray(doc, pendingNamespaces, namespaces, namespaceSpecificTags, prefix, attrs, body, contentHandler2.get()));
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

        public Object doCall(Object doc, Object pendingNamespaces, Object namespaces, Object namespaceSpecificTags, Object prefix, Object attrs, Object body, Object contentHandler) {
            CallSite[] callSiteArray = _closure3.$getCallSiteArray();
            if (ScriptBytecodeAdapter.compareNotEqual(body, null)) {
                return callSiteArray[0].callCurrent(this, body, doc, contentHandler);
            }
            return null;
        }

        public Object call(Object doc, Object pendingNamespaces, Object namespaces, Object namespaceSpecificTags, Object prefix, Object attrs, Object body, Object contentHandler) {
            CallSite[] callSiteArray = _closure3.$getCallSiteArray();
            return callSiteArray[1].callCurrent((GroovyObject)this, ArrayUtil.createArray(doc, pendingNamespaces, namespaces, namespaceSpecificTags, prefix, attrs, body, contentHandler));
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
            stringArray[0] = "processBody";
            stringArray[1] = "doCall";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[2];
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

        public Object doCall(Object tag, Object doc, Object pendingNamespaces, Object namespaces, Object namespaceSpecificTags, Object prefix, Object attrs, Object body, Object contentHandler) {
            Reference<Object> namespaces2 = new Reference<Object>(namespaces);
            Reference<Object> contentHandler2 = new Reference<Object>(contentHandler);
            CallSite[] callSiteArray = _closure4.$getCallSiteArray();
            Reference<Object> attributes = new Reference<Object>(callSiteArray[0].callConstructor(AttributesImpl.class));
            public class _closure9
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference attributes;
                private /* synthetic */ Reference namespaces;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _closure9(Object _outerInstance, Object _thisObject, Reference attributes, Reference namespaces) {
                    Reference reference;
                    Reference reference2;
                    CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.attributes = reference2 = attributes;
                    this.namespaces = reference = namespaces;
                }

                public Object doCall(Object key, Object value) {
                    CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                    return callSiteArray[0].callCurrent(this, this.attributes.get(), key, value, this.namespaces.get());
                }

                public Object call(Object key, Object value) {
                    CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                    return callSiteArray[1].callCurrent(this, key, value);
                }

                public Object getAttributes() {
                    CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                    return this.attributes.get();
                }

                public Object getNamespaces() {
                    CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                    return this.namespaces.get();
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
                    stringArray[0] = "addAttributes";
                    stringArray[1] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[2];
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
            callSiteArray[1].call(attrs, new _closure9(this, this.getThisObject(), attributes, namespaces2));
            Reference<Map> hiddenNamespaces = new Reference<Map>(ScriptBytecodeAdapter.createMap(new Object[0]));
            public class _closure10
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference hiddenNamespaces;
                private /* synthetic */ Reference namespaces;
                private /* synthetic */ Reference attributes;
                private /* synthetic */ Reference contentHandler;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _closure10(Object _outerInstance, Object _thisObject, Reference hiddenNamespaces, Reference namespaces, Reference attributes, Reference contentHandler) {
                    Reference reference;
                    Reference reference2;
                    Reference reference3;
                    Reference reference4;
                    CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.hiddenNamespaces = reference4 = hiddenNamespaces;
                    this.namespaces = reference3 = namespaces;
                    this.attributes = reference2 = attributes;
                    this.contentHandler = reference = contentHandler;
                }

                public Object doCall(Object key, Object value) {
                    CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                    Object k = ScriptBytecodeAdapter.compareEqual(key, ":") ? "" : key;
                    Object object = callSiteArray[0].call(this.namespaces.get(), key);
                    callSiteArray[1].call(this.hiddenNamespaces.get(), k, object);
                    Object object2 = value;
                    callSiteArray[2].call(this.namespaces.get(), k, object2);
                    callSiteArray[3].call(this.attributes.get(), ArrayUtil.createArray("http://www.w3.org/2000/xmlns/", k, new GStringImpl(new Object[]{ScriptBytecodeAdapter.compareEqual(k, "") ? "" : new GStringImpl(new Object[]{k}, new String[]{":", ""})}, new String[]{"xmlns", ""}), "CDATA", new GStringImpl(new Object[]{value}, new String[]{"", ""})));
                    return callSiteArray[4].call(this.contentHandler.get(), k, value);
                }

                public Object call(Object key, Object value) {
                    CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                    return callSiteArray[5].callCurrent(this, key, value);
                }

                public Object getHiddenNamespaces() {
                    CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                    return this.hiddenNamespaces.get();
                }

                public Object getNamespaces() {
                    CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                    return this.namespaces.get();
                }

                public Object getAttributes() {
                    CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                    return this.attributes.get();
                }

                public Object getContentHandler() {
                    CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                    return this.contentHandler.get();
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
                    stringArray[0] = "getAt";
                    stringArray[1] = "putAt";
                    stringArray[2] = "putAt";
                    stringArray[3] = "addAttribute";
                    stringArray[4] = "startPrefixMapping";
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
            callSiteArray[2].call(pendingNamespaces, new _closure10(this, this.getThisObject(), hiddenNamespaces, namespaces2, attributes, contentHandler2));
            Object uri = "";
            Object qualifiedName = tag;
            if (ScriptBytecodeAdapter.compareNotEqual(prefix, "")) {
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[3].call(namespaces2.get(), prefix))) {
                    Object object;
                    uri = object = callSiteArray[4].call(namespaces2.get(), prefix);
                } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[5].call(pendingNamespaces, prefix))) {
                    Object object;
                    uri = object = callSiteArray[6].call(pendingNamespaces, prefix);
                } else {
                    throw (Throwable)callSiteArray[7].callConstructor(GroovyRuntimeException.class, new GStringImpl(new Object[]{prefix}, new String[]{"Namespace prefix: ", " is not bound to a URI"}));
                }
                if (ScriptBytecodeAdapter.compareNotEqual(prefix, ":")) {
                    Object object;
                    qualifiedName = object = callSiteArray[8].call(callSiteArray[9].call(prefix, ":"), tag);
                }
            }
            callSiteArray[10].call(contentHandler2.get(), uri, tag, qualifiedName, attributes.get());
            if (ScriptBytecodeAdapter.compareNotEqual(body, null)) {
                callSiteArray[11].call(callSiteArray[12].callGroovyObjectGetProperty(this), callSiteArray[13].call(pendingNamespaces));
                callSiteArray[14].call(pendingNamespaces);
                callSiteArray[15].callCurrent(this, body, doc, contentHandler2.get());
                callSiteArray[16].call(pendingNamespaces);
                callSiteArray[17].call(pendingNamespaces, callSiteArray[18].call(callSiteArray[19].callGroovyObjectGetProperty(this)));
            }
            callSiteArray[20].call(contentHandler2.get(), uri, tag, qualifiedName);
            public class _closure11
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference contentHandler;
                private /* synthetic */ Reference namespaces;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _closure11(Object _outerInstance, Object _thisObject, Reference contentHandler, Reference namespaces) {
                    Reference reference;
                    Reference reference2;
                    CallSite[] callSiteArray = _closure11.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.contentHandler = reference2 = contentHandler;
                    this.namespaces = reference = namespaces;
                }

                public Object doCall(Object key, Object value) {
                    CallSite[] callSiteArray = _closure11.$getCallSiteArray();
                    callSiteArray[0].call(this.contentHandler.get(), key);
                    if (ScriptBytecodeAdapter.compareEqual(value, null)) {
                        return callSiteArray[1].call(this.namespaces.get(), key);
                    }
                    Object object = value;
                    callSiteArray[2].call(this.namespaces.get(), key, object);
                    return object;
                }

                public Object call(Object key, Object value) {
                    CallSite[] callSiteArray = _closure11.$getCallSiteArray();
                    return callSiteArray[3].callCurrent(this, key, value);
                }

                public Object getContentHandler() {
                    CallSite[] callSiteArray = _closure11.$getCallSiteArray();
                    return this.contentHandler.get();
                }

                public Object getNamespaces() {
                    CallSite[] callSiteArray = _closure11.$getCallSiteArray();
                    return this.namespaces.get();
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
                    stringArray[0] = "endPrefixMapping";
                    stringArray[1] = "remove";
                    stringArray[2] = "putAt";
                    stringArray[3] = "doCall";
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
            return callSiteArray[21].call((Object)hiddenNamespaces.get(), new _closure11(this, this.getThisObject(), contentHandler2, namespaces2));
        }

        public Object call(Object tag, Object doc, Object pendingNamespaces, Object namespaces, Object namespaceSpecificTags, Object prefix, Object attrs, Object body, Object contentHandler) {
            Reference<Object> namespaces2 = new Reference<Object>(namespaces);
            Reference<Object> contentHandler2 = new Reference<Object>(contentHandler);
            CallSite[] callSiteArray = _closure4.$getCallSiteArray();
            return callSiteArray[22].callCurrent((GroovyObject)this, ArrayUtil.createArray(tag, doc, pendingNamespaces, namespaces2.get(), namespaceSpecificTags, prefix, attrs, body, contentHandler2.get()));
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
            stringArray[0] = "<$constructor$>";
            stringArray[1] = "each";
            stringArray[2] = "each";
            stringArray[3] = "containsKey";
            stringArray[4] = "getAt";
            stringArray[5] = "containsKey";
            stringArray[6] = "getAt";
            stringArray[7] = "<$constructor$>";
            stringArray[8] = "plus";
            stringArray[9] = "plus";
            stringArray[10] = "startElement";
            stringArray[11] = "add";
            stringArray[12] = "pendingStack";
            stringArray[13] = "clone";
            stringArray[14] = "clear";
            stringArray[15] = "processBody";
            stringArray[16] = "clear";
            stringArray[17] = "putAll";
            stringArray[18] = "pop";
            stringArray[19] = "pendingStack";
            stringArray[20] = "endElement";
            stringArray[21] = "each";
            stringArray[22] = "doCall";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[23];
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

