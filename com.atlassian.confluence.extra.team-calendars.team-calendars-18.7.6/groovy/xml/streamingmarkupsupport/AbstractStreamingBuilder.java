/*
 * Decompiled with CFR 0.152.
 */
package groovy.xml.streamingmarkupsupport;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import java.lang.ref.SoftReference;
import java.util.Map;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class AbstractStreamingBuilder
implements GroovyObject {
    private Object badTagClosure;
    private Object namespaceSetupClosure;
    private Object aliasSetupClosure;
    private Object getNamespaceClosure;
    private Object specialTags;
    private Object builder;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public AbstractStreamingBuilder() {
        MetaClass metaClass;
        CallSite[] callSiteArray = AbstractStreamingBuilder.$getCallSiteArray();
        _closure1 _closure110 = new _closure1(this, this);
        this.badTagClosure = _closure110;
        _closure2 _closure210 = new _closure2(this, this);
        this.namespaceSetupClosure = _closure210;
        _closure3 _closure310 = new _closure3(this, this);
        this.aliasSetupClosure = _closure310;
        _closure4 _closure44 = new _closure4(this, this);
        this.getNamespaceClosure = _closure44;
        Map map = ScriptBytecodeAdapter.createMap(new Object[]{"declareNamespace", this.namespaceSetupClosure, "declareAlias", this.aliasSetupClosure, "getNamespaces", this.getNamespaceClosure});
        this.specialTags = map;
        Object var7_7 = null;
        this.builder = var7_7;
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != AbstractStreamingBuilder.class) {
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

    public Object getBadTagClosure() {
        return this.badTagClosure;
    }

    public void setBadTagClosure(Object object) {
        this.badTagClosure = object;
    }

    public Object getNamespaceSetupClosure() {
        return this.namespaceSetupClosure;
    }

    public void setNamespaceSetupClosure(Object object) {
        this.namespaceSetupClosure = object;
    }

    public Object getAliasSetupClosure() {
        return this.aliasSetupClosure;
    }

    public void setAliasSetupClosure(Object object) {
        this.aliasSetupClosure = object;
    }

    public Object getGetNamespaceClosure() {
        return this.getNamespaceClosure;
    }

    public void setGetNamespaceClosure(Object object) {
        this.getNamespaceClosure = object;
    }

    public Object getSpecialTags() {
        return this.specialTags;
    }

    public void setSpecialTags(Object object) {
        this.specialTags = object;
    }

    public Object getBuilder() {
        return this.builder;
    }

    public void setBuilder(Object object) {
        this.builder = object;
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[]{};
        return new CallSiteArray(AbstractStreamingBuilder.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = AbstractStreamingBuilder.$createCallSiteArray();
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

        public Object doCall(Object tag, Object doc, Object pendingNamespaces, Object namespaces, Object namespaceSpecificTags, Object prefix, Object ... rest) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            Object uri = callSiteArray[0].call(pendingNamespaces, prefix);
            if (ScriptBytecodeAdapter.compareEqual(uri, null)) {
                Object object;
                uri = object = callSiteArray[1].call(namespaces, prefix);
            }
            throw (Throwable)callSiteArray[2].callConstructor(GroovyRuntimeException.class, new GStringImpl(new Object[]{tag, uri}, new String[]{"Tag ", " is not allowed in namespace ", ""}));
        }

        public Object call(Object tag, Object doc, Object pendingNamespaces, Object namespaces, Object namespaceSpecificTags, Object prefix, Object ... rest) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            return callSiteArray[3].callCurrent((GroovyObject)this, ArrayUtil.createArray(tag, doc, pendingNamespaces, namespaces, namespaceSpecificTags, prefix, rest));
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
            stringArray[0] = "getAt";
            stringArray[1] = "getAt";
            stringArray[2] = "<$constructor$>";
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

        /*
         * WARNING - void declaration
         */
        public Object doCall(Object doc, Object pendingNamespaces, Object namespaces, Object namespaceSpecificTags, Object prefix, Object attrs, Object ... rest) {
            void var3_3;
            Reference<Object> pendingNamespaces2 = new Reference<Object>(pendingNamespaces);
            Reference<void> namespaces2 = new Reference<void>(var3_3);
            Reference<Object> namespaceSpecificTags2 = new Reference<Object>(namespaceSpecificTags);
            CallSite[] callSiteArray = _closure2.$getCallSiteArray();
            public class _closure5
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference namespaces;
                private /* synthetic */ Reference pendingNamespaces;
                private /* synthetic */ Reference namespaceSpecificTags;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _closure5(Object _outerInstance, Object _thisObject, Reference namespaces, Reference pendingNamespaces, Reference namespaceSpecificTags) {
                    Reference reference;
                    Reference reference2;
                    Reference reference3;
                    CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.namespaces = reference3 = namespaces;
                    this.pendingNamespaces = reference2 = pendingNamespaces;
                    this.namespaceSpecificTags = reference = namespaceSpecificTags;
                }

                public Object doCall(Object key, Object value) {
                    Object object;
                    CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                    if (ScriptBytecodeAdapter.compareEqual(key, "")) {
                        String string = ":";
                        key = string;
                    }
                    value = object = callSiteArray[0].call(value);
                    if (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[1].call(this.namespaces.get(), key), value)) {
                        Object object2 = value;
                        callSiteArray[2].call(this.pendingNamespaces.get(), key, object2);
                    }
                    if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[3].call(this.namespaceSpecificTags.get(), value))) {
                        Object baseEntry = callSiteArray[4].call(this.namespaceSpecificTags.get(), ":");
                        Object object3 = callSiteArray[5].call(ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[6].call(baseEntry, 0), callSiteArray[7].call(baseEntry, 1), ScriptBytecodeAdapter.createMap(new Object[0])}));
                        callSiteArray[8].call(this.namespaceSpecificTags.get(), value, object3);
                        return object3;
                    }
                    return null;
                }

                public Object call(Object key, Object value) {
                    CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                    return callSiteArray[9].callCurrent(this, key, value);
                }

                public Object getNamespaces() {
                    CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                    return this.namespaces.get();
                }

                public Object getPendingNamespaces() {
                    CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                    return this.pendingNamespaces.get();
                }

                public Object getNamespaceSpecificTags() {
                    CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                    return this.namespaceSpecificTags.get();
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _closure5.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "toString";
                    stringArray[1] = "getAt";
                    stringArray[2] = "putAt";
                    stringArray[3] = "containsKey";
                    stringArray[4] = "getAt";
                    stringArray[5] = "toArray";
                    stringArray[6] = "getAt";
                    stringArray[7] = "getAt";
                    stringArray[8] = "putAt";
                    stringArray[9] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[10];
                    _closure5.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_closure5.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _closure5.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            return callSiteArray[0].call(attrs, new _closure5(this, this.getThisObject(), namespaces2, pendingNamespaces2, namespaceSpecificTags2));
        }

        /*
         * WARNING - void declaration
         */
        public Object call(Object doc, Object pendingNamespaces, Object namespaces, Object namespaceSpecificTags, Object prefix, Object attrs, Object ... rest) {
            void var3_3;
            Reference<Object> pendingNamespaces2 = new Reference<Object>(pendingNamespaces);
            Reference<void> namespaces2 = new Reference<void>(var3_3);
            Reference<Object> namespaceSpecificTags2 = new Reference<Object>(namespaceSpecificTags);
            CallSite[] callSiteArray = _closure2.$getCallSiteArray();
            return callSiteArray[1].callCurrent((GroovyObject)this, ArrayUtil.createArray(doc, pendingNamespaces2.get(), namespaces2.get(), namespaceSpecificTags2.get(), prefix, attrs, rest));
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

        /*
         * WARNING - void declaration
         */
        public Object doCall(Object doc, Object pendingNamespaces, Object namespaces, Object namespaceSpecificTags, Object prefix, Object attrs, Object ... rest) {
            void var3_3;
            Reference<Object> pendingNamespaces2 = new Reference<Object>(pendingNamespaces);
            Reference<void> namespaces2 = new Reference<void>(var3_3);
            Reference<Object> namespaceSpecificTags2 = new Reference<Object>(namespaceSpecificTags);
            CallSite[] callSiteArray = _closure3.$getCallSiteArray();
            public class _closure6
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference namespaces;
                private /* synthetic */ Reference namespaceSpecificTags;
                private /* synthetic */ Reference pendingNamespaces;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _closure6(Object _outerInstance, Object _thisObject, Reference namespaces, Reference namespaceSpecificTags, Reference pendingNamespaces) {
                    Reference reference;
                    Reference reference2;
                    Reference reference3;
                    CallSite[] callSiteArray = _closure6.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.namespaces = reference3 = namespaces;
                    this.namespaceSpecificTags = reference2 = namespaceSpecificTags;
                    this.pendingNamespaces = reference = pendingNamespaces;
                }

                public Object doCall(Object key, Object value) {
                    CallSite[] callSiteArray = _closure6.$getCallSiteArray();
                    if (value instanceof Map) {
                        Reference<Object> info = new Reference<Object>(null);
                        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(this.namespaces.get(), key))) {
                            Object object = callSiteArray[1].call(this.namespaceSpecificTags.get(), callSiteArray[2].call(this.namespaces.get(), key));
                            info.set(object);
                        } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[3].call(this.pendingNamespaces.get(), key))) {
                            Object object = callSiteArray[4].call(this.namespaceSpecificTags.get(), callSiteArray[5].call(this.pendingNamespaces.get(), key));
                            info.set(object);
                        } else {
                            throw (Throwable)callSiteArray[6].callConstructor(GroovyRuntimeException.class, new GStringImpl(new Object[]{key}, new String[]{"namespace prefix ", " has not been declared"}));
                        }
                        public class _closure7
                        extends Closure
                        implements GeneratedClosure {
                            private /* synthetic */ Reference info;
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure7(Object _outerInstance, Object _thisObject, Reference info) {
                                Reference reference;
                                CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                                this.info = reference = info;
                            }

                            public Object doCall(Object to, Object from) {
                                CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                                Object object = callSiteArray[0].call(callSiteArray[1].call(this.info.get(), 1), from);
                                callSiteArray[2].call(callSiteArray[3].call(this.info.get(), 2), to, object);
                                return object;
                            }

                            public Object call(Object to, Object from) {
                                CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                                return callSiteArray[4].callCurrent(this, to, from);
                            }

                            public Object getInfo() {
                                CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                                return this.info.get();
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
                                stringArray[0] = "curry";
                                stringArray[1] = "getAt";
                                stringArray[2] = "putAt";
                                stringArray[3] = "getAt";
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
                        return callSiteArray[7].call(value, new _closure7(this, this.getThisObject(), info));
                    }
                    Object info = callSiteArray[8].call(this.namespaceSpecificTags.get(), ":");
                    Object object = callSiteArray[9].call(callSiteArray[10].call(info, 1), value);
                    callSiteArray[11].call(callSiteArray[12].call(info, 2), key, object);
                    return object;
                }

                public Object call(Object key, Object value) {
                    CallSite[] callSiteArray = _closure6.$getCallSiteArray();
                    return callSiteArray[13].callCurrent(this, key, value);
                }

                public Object getNamespaces() {
                    CallSite[] callSiteArray = _closure6.$getCallSiteArray();
                    return this.namespaces.get();
                }

                public Object getNamespaceSpecificTags() {
                    CallSite[] callSiteArray = _closure6.$getCallSiteArray();
                    return this.namespaceSpecificTags.get();
                }

                public Object getPendingNamespaces() {
                    CallSite[] callSiteArray = _closure6.$getCallSiteArray();
                    return this.pendingNamespaces.get();
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
                    stringArray[0] = "containsKey";
                    stringArray[1] = "getAt";
                    stringArray[2] = "getAt";
                    stringArray[3] = "containsKey";
                    stringArray[4] = "getAt";
                    stringArray[5] = "getAt";
                    stringArray[6] = "<$constructor$>";
                    stringArray[7] = "each";
                    stringArray[8] = "getAt";
                    stringArray[9] = "curry";
                    stringArray[10] = "getAt";
                    stringArray[11] = "putAt";
                    stringArray[12] = "getAt";
                    stringArray[13] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[14];
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
            return callSiteArray[0].call(attrs, new _closure6(this, this.getThisObject(), namespaces2, namespaceSpecificTags2, pendingNamespaces2));
        }

        /*
         * WARNING - void declaration
         */
        public Object call(Object doc, Object pendingNamespaces, Object namespaces, Object namespaceSpecificTags, Object prefix, Object attrs, Object ... rest) {
            void var3_3;
            Reference<Object> pendingNamespaces2 = new Reference<Object>(pendingNamespaces);
            Reference<void> namespaces2 = new Reference<void>(var3_3);
            Reference<Object> namespaceSpecificTags2 = new Reference<Object>(namespaceSpecificTags);
            CallSite[] callSiteArray = _closure3.$getCallSiteArray();
            return callSiteArray[1].callCurrent((GroovyObject)this, ArrayUtil.createArray(doc, pendingNamespaces2.get(), namespaces2.get(), namespaceSpecificTags2.get(), prefix, attrs, rest));
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
            stringArray[0] = "each";
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

        public Object doCall(Object doc, Object pendingNamespaces, Object namespaces, Object ... rest) {
            CallSite[] callSiteArray = _closure4.$getCallSiteArray();
            return ScriptBytecodeAdapter.createList(new Object[]{namespaces, pendingNamespaces});
        }

        public Object call(Object doc, Object pendingNamespaces, Object namespaces, Object ... rest) {
            CallSite[] callSiteArray = _closure4.$getCallSiteArray();
            return callSiteArray[0].callCurrent(this, doc, pendingNamespaces, namespaces, rest);
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

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[1];
            stringArray[0] = "doCall";
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

