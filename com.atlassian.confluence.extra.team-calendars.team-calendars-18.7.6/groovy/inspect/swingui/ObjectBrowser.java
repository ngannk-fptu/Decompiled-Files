/*
 * Decompiled with CFR 0.152.
 */
package groovy.inspect.swingui;

import groovy.inspect.Inspector;
import groovy.inspect.swingui.TableSorter;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.swing.SwingBuilder;
import groovy.ui.Console;
import java.awt.FlowLayout;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.EventObject;
import java.util.Map;
import javax.swing.WindowConstants;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class ObjectBrowser
implements GroovyObject {
    private Object inspector;
    private Object swing;
    private Object frame;
    private Object fieldTable;
    private Object methodTable;
    private Object itemTable;
    private Object mapTable;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ObjectBrowser() {
        MetaClass metaClass;
        CallSite[] callSiteArray = ObjectBrowser.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public static void main(String ... args) {
        CallSite[] callSiteArray = ObjectBrowser.$getCallSiteArray();
        callSiteArray[0].callStatic(ObjectBrowser.class, "some String");
    }

    public static void inspect(Object objectUnderInspection) {
        CallSite[] callSiteArray = ObjectBrowser.$getCallSiteArray();
        Object browser = callSiteArray[1].callConstructor(ObjectBrowser.class);
        Object object = callSiteArray[2].callConstructor(Inspector.class, objectUnderInspection);
        ScriptBytecodeAdapter.setProperty(object, null, browser, "inspector");
        callSiteArray[3].call(browser);
    }

    public void run() {
        Object object;
        Object object2;
        CallSite[] callSiteArray = ObjectBrowser.$getCallSiteArray();
        this.swing = object2 = callSiteArray[4].callConstructor(SwingBuilder.class);
        public class _run_closure1
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _run_closure1(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _run_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _run_closure1.$getCallSiteArray();
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

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure2.$getCallSiteArray();
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

                            public Object doCall(Object it) {
                                CallSite[] callSiteArray = _closure4.$getCallSiteArray();
                                public class _closure5
                                extends Closure
                                implements GeneratedClosure {
                                    private static /* synthetic */ ClassInfo $staticClassInfo;
                                    public static transient /* synthetic */ boolean __$stMC;
                                    private static /* synthetic */ SoftReference $callSiteArray;

                                    public _closure5(Object _outerInstance, Object _thisObject) {
                                        CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                                        super(_outerInstance, _thisObject);
                                    }

                                    public Object doCall(Object it) {
                                        CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                                        return callSiteArray[0].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "About", "closure", ScriptBytecodeAdapter.getMethodPointer(this.getThisObject(), "showAbout")}));
                                    }

                                    public Object doCall() {
                                        CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                                        return this.doCall(null);
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

                                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                        String[] stringArray = new String[1];
                                        stringArray[0] = "action";
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
                                return callSiteArray[0].callCurrent((GroovyObject)this, new _closure5(this, this.getThisObject()));
                            }

                            public Object doCall() {
                                CallSite[] callSiteArray = _closure4.$getCallSiteArray();
                                return this.doCall(null);
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
                                stringArray[0] = "menuItem";
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
                        return callSiteArray[0].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"text", "Help"}), new _closure4(this, this.getThisObject()));
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure2.$getCallSiteArray();
                        return this.doCall(null);
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

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[1];
                        stringArray[0] = "menu";
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
                callSiteArray[0].callCurrent((GroovyObject)this, new _closure2(this, this.getThisObject()));
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

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure3.$getCallSiteArray();
                        callSiteArray[0].callCurrent(this);
                        public class _closure6
                        extends Closure
                        implements GeneratedClosure {
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure6(Object _outerInstance, Object _thisObject) {
                                CallSite[] callSiteArray = _closure6.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                            }

                            public Object doCall(Object it) {
                                CallSite[] callSiteArray = _closure6.$getCallSiteArray();
                                callSiteArray[0].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"alignment", callSiteArray[1].callGetProperty(FlowLayout.class)}));
                                Object props = callSiteArray[2].callGetProperty(callSiteArray[3].callGroovyObjectGetProperty(this));
                                Object classLabel = callSiteArray[4].call((Object)"<html>", callSiteArray[5].call(props, "<br>"));
                                return callSiteArray[6].callCurrent((GroovyObject)this, classLabel);
                            }

                            public Object doCall() {
                                CallSite[] callSiteArray = _closure6.$getCallSiteArray();
                                return this.doCall(null);
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
                                stringArray[0] = "flowLayout";
                                stringArray[1] = "LEFT";
                                stringArray[2] = "classProps";
                                stringArray[3] = "inspector";
                                stringArray[4] = "plus";
                                stringArray[5] = "join";
                                stringArray[6] = "label";
                            }

                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                String[] stringArray = new String[7];
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
                        callSiteArray[1].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Class Info", "border", callSiteArray[2].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createList(new Object[]{5, 10, 5, 10})), "constraints", callSiteArray[3].callGroovyObjectGetProperty(this)}), new _closure6(this, this.getThisObject()));
                        public class _closure7
                        extends Closure
                        implements GeneratedClosure {
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure7(Object _outerInstance, Object _thisObject) {
                                CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                            }

                            public Object doCall(Object it) {
                                CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                                if (callSiteArray[0].callGetProperty(callSiteArray[1].callGroovyObjectGetProperty(this)) instanceof Collection) {
                                    public class _closure8
                                    extends Closure
                                    implements GeneratedClosure {
                                        private static /* synthetic */ ClassInfo $staticClassInfo;
                                        public static transient /* synthetic */ boolean __$stMC;
                                        private static /* synthetic */ SoftReference $callSiteArray;

                                        public _closure8(Object _outerInstance, Object _thisObject) {
                                            CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                                            super(_outerInstance, _thisObject);
                                        }

                                        public Object doCall(Object it) {
                                            CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                                            public class _closure12
                                            extends Closure
                                            implements GeneratedClosure {
                                                private static /* synthetic */ ClassInfo $staticClassInfo;
                                                public static transient /* synthetic */ boolean __$stMC;
                                                private static /* synthetic */ SoftReference $callSiteArray;

                                                public _closure12(Object _outerInstance, Object _thisObject) {
                                                    CallSite[] callSiteArray = _closure12.$getCallSiteArray();
                                                    super(_outerInstance, _thisObject);
                                                }

                                                public Object doCall(Object it) {
                                                    CallSite[] callSiteArray = _closure12.$getCallSiteArray();
                                                    Reference<Integer> i = new Reference<Integer>(0);
                                                    public class _closure13
                                                    extends Closure
                                                    implements GeneratedClosure {
                                                        private /* synthetic */ Reference i;
                                                        private static /* synthetic */ ClassInfo $staticClassInfo;
                                                        public static transient /* synthetic */ boolean __$stMC;
                                                        private static /* synthetic */ SoftReference $callSiteArray;

                                                        public _closure13(Object _outerInstance, Object _thisObject, Reference i) {
                                                            Reference reference;
                                                            CallSite[] callSiteArray = _closure13.$getCallSiteArray();
                                                            super(_outerInstance, _thisObject);
                                                            this.i = reference = i;
                                                        }

                                                        public Object doCall(Object val) {
                                                            CallSite[] callSiteArray = _closure13.$getCallSiteArray();
                                                            Object[] objectArray = new Object[2];
                                                            Object t = this.i.get();
                                                            this.i.set((Integer)ScriptBytecodeAdapter.castToType(callSiteArray[0].call(t), Integer.class));
                                                            objectArray[0] = t;
                                                            objectArray[1] = val;
                                                            return ScriptBytecodeAdapter.createList(objectArray);
                                                        }

                                                        public Integer getI() {
                                                            CallSite[] callSiteArray = _closure13.$getCallSiteArray();
                                                            return (Integer)ScriptBytecodeAdapter.castToType(this.i.get(), Integer.class);
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

                                                        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                                            String[] stringArray = new String[1];
                                                            stringArray[0] = "next";
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
                                                    Object data = callSiteArray[0].call(callSiteArray[1].callGetProperty(callSiteArray[2].callGroovyObjectGetProperty(this)), new _closure13(this, this.getThisObject(), i));
                                                    public class _closure14
                                                    extends Closure
                                                    implements GeneratedClosure {
                                                        private static /* synthetic */ ClassInfo $staticClassInfo;
                                                        public static transient /* synthetic */ boolean __$stMC;
                                                        private static /* synthetic */ SoftReference $callSiteArray;

                                                        public _closure14(Object _outerInstance, Object _thisObject) {
                                                            CallSite[] callSiteArray = _closure14.$getCallSiteArray();
                                                            super(_outerInstance, _thisObject);
                                                        }

                                                        public Object doCall(Object it) {
                                                            CallSite[] callSiteArray = _closure14.$getCallSiteArray();
                                                            public class _closure15
                                                            extends Closure
                                                            implements GeneratedClosure {
                                                                private static /* synthetic */ ClassInfo $staticClassInfo;
                                                                public static transient /* synthetic */ boolean __$stMC;
                                                                private static /* synthetic */ SoftReference $callSiteArray;

                                                                public _closure15(Object _outerInstance, Object _thisObject) {
                                                                    CallSite[] callSiteArray = _closure15.$getCallSiteArray();
                                                                    super(_outerInstance, _thisObject);
                                                                }

                                                                public Object doCall(Object it) {
                                                                    CallSite[] callSiteArray = _closure15.$getCallSiteArray();
                                                                    return callSiteArray[0].call(it, 0);
                                                                }

                                                                public Object doCall() {
                                                                    CallSite[] callSiteArray = _closure15.$getCallSiteArray();
                                                                    return this.doCall(null);
                                                                }

                                                                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                                    if (this.getClass() != _closure15.class) {
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
                                                                    stringArray[0] = "getAt";
                                                                    return new CallSiteArray(_closure15.class, stringArray);
                                                                }

                                                                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                                    CallSiteArray callSiteArray;
                                                                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                                        callSiteArray = _closure15.$createCallSiteArray();
                                                                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                                    }
                                                                    return callSiteArray.array;
                                                                }
                                                            }
                                                            callSiteArray[0].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"header", "Index", "read", new _closure15(this, this.getThisObject())}));
                                                            public class _closure16
                                                            extends Closure
                                                            implements GeneratedClosure {
                                                                private static /* synthetic */ ClassInfo $staticClassInfo;
                                                                public static transient /* synthetic */ boolean __$stMC;
                                                                private static /* synthetic */ SoftReference $callSiteArray;

                                                                public _closure16(Object _outerInstance, Object _thisObject) {
                                                                    CallSite[] callSiteArray = _closure16.$getCallSiteArray();
                                                                    super(_outerInstance, _thisObject);
                                                                }

                                                                public Object doCall(Object it) {
                                                                    CallSite[] callSiteArray = _closure16.$getCallSiteArray();
                                                                    return callSiteArray[0].call(it, 1);
                                                                }

                                                                public Object doCall() {
                                                                    CallSite[] callSiteArray = _closure16.$getCallSiteArray();
                                                                    return this.doCall(null);
                                                                }

                                                                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                                    if (this.getClass() != _closure16.class) {
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
                                                                    stringArray[0] = "getAt";
                                                                    return new CallSiteArray(_closure16.class, stringArray);
                                                                }

                                                                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                                    CallSiteArray callSiteArray;
                                                                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                                        callSiteArray = _closure16.$createCallSiteArray();
                                                                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                                    }
                                                                    return callSiteArray.array;
                                                                }
                                                            }
                                                            return callSiteArray[1].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"header", "Value", "read", new _closure16(this, this.getThisObject())}));
                                                        }

                                                        public Object doCall() {
                                                            CallSite[] callSiteArray = _closure14.$getCallSiteArray();
                                                            return this.doCall(null);
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
                                                            stringArray[0] = "closureColumn";
                                                            stringArray[1] = "closureColumn";
                                                        }

                                                        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                                            String[] stringArray = new String[2];
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
                                                    return callSiteArray[3].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"list", data}), new _closure14(this, this.getThisObject()));
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
                                                    stringArray[0] = "collect";
                                                    stringArray[1] = "object";
                                                    stringArray[2] = "inspector";
                                                    stringArray[3] = "tableModel";
                                                }

                                                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                                    String[] stringArray = new String[4];
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
                                            Object object = callSiteArray[0].callCurrent((GroovyObject)this, new _closure12(this, this.getThisObject()));
                                            ScriptBytecodeAdapter.setGroovyObjectProperty(object, _closure8.class, this, "itemTable");
                                            return object;
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

                                        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                            String[] stringArray = new String[1];
                                            stringArray[0] = "table";
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
                                    callSiteArray[2].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"name", " Collection data "}), new _closure8(this, this.getThisObject()));
                                }
                                if (callSiteArray[3].callGetProperty(callSiteArray[4].callGroovyObjectGetProperty(this)) instanceof Map) {
                                    public class _closure9
                                    extends Closure
                                    implements GeneratedClosure {
                                        private static /* synthetic */ ClassInfo $staticClassInfo;
                                        public static transient /* synthetic */ boolean __$stMC;
                                        private static /* synthetic */ SoftReference $callSiteArray;

                                        public _closure9(Object _outerInstance, Object _thisObject) {
                                            CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                                            super(_outerInstance, _thisObject);
                                        }

                                        public Object doCall(Object it) {
                                            CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                                            public class _closure17
                                            extends Closure
                                            implements GeneratedClosure {
                                                private static /* synthetic */ ClassInfo $staticClassInfo;
                                                public static transient /* synthetic */ boolean __$stMC;
                                                private static /* synthetic */ SoftReference $callSiteArray;

                                                public _closure17(Object _outerInstance, Object _thisObject) {
                                                    CallSite[] callSiteArray = _closure17.$getCallSiteArray();
                                                    super(_outerInstance, _thisObject);
                                                }

                                                public Object doCall(Object it) {
                                                    CallSite[] callSiteArray = _closure17.$getCallSiteArray();
                                                    Reference<Integer> i = new Reference<Integer>(0);
                                                    public class _closure18
                                                    extends Closure
                                                    implements GeneratedClosure {
                                                        private /* synthetic */ Reference i;
                                                        private static /* synthetic */ ClassInfo $staticClassInfo;
                                                        public static transient /* synthetic */ boolean __$stMC;
                                                        private static /* synthetic */ SoftReference $callSiteArray;

                                                        public _closure18(Object _outerInstance, Object _thisObject, Reference i) {
                                                            Reference reference;
                                                            CallSite[] callSiteArray = _closure18.$getCallSiteArray();
                                                            super(_outerInstance, _thisObject);
                                                            this.i = reference = i;
                                                        }

                                                        public Object doCall(Object key, Object val) {
                                                            CallSite[] callSiteArray = _closure18.$getCallSiteArray();
                                                            Object[] objectArray = new Object[3];
                                                            Object t = this.i.get();
                                                            this.i.set((Integer)ScriptBytecodeAdapter.castToType(callSiteArray[0].call(t), Integer.class));
                                                            objectArray[0] = t;
                                                            objectArray[1] = key;
                                                            objectArray[2] = val;
                                                            return ScriptBytecodeAdapter.createList(objectArray);
                                                        }

                                                        public Object call(Object key, Object val) {
                                                            CallSite[] callSiteArray = _closure18.$getCallSiteArray();
                                                            return callSiteArray[1].callCurrent(this, key, val);
                                                        }

                                                        public Integer getI() {
                                                            CallSite[] callSiteArray = _closure18.$getCallSiteArray();
                                                            return (Integer)ScriptBytecodeAdapter.castToType(this.i.get(), Integer.class);
                                                        }

                                                        protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                            if (this.getClass() != _closure18.class) {
                                                                return ScriptBytecodeAdapter.initMetaClass(this);
                                                            }
                                                            ClassInfo classInfo = $staticClassInfo;
                                                            if (classInfo == null) {
                                                                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                                            }
                                                            return classInfo.getMetaClass();
                                                        }

                                                        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                                            stringArray[0] = "next";
                                                            stringArray[1] = "doCall";
                                                        }

                                                        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                                            String[] stringArray = new String[2];
                                                            _closure18.$createCallSiteArray_1(stringArray);
                                                            return new CallSiteArray(_closure18.class, stringArray);
                                                        }

                                                        private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                            CallSiteArray callSiteArray;
                                                            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                                callSiteArray = _closure18.$createCallSiteArray();
                                                                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                            }
                                                            return callSiteArray.array;
                                                        }
                                                    }
                                                    Object data = callSiteArray[0].call(callSiteArray[1].callGetProperty(callSiteArray[2].callGroovyObjectGetProperty(this)), new _closure18(this, this.getThisObject(), i));
                                                    public class _closure19
                                                    extends Closure
                                                    implements GeneratedClosure {
                                                        private static /* synthetic */ ClassInfo $staticClassInfo;
                                                        public static transient /* synthetic */ boolean __$stMC;
                                                        private static /* synthetic */ SoftReference $callSiteArray;

                                                        public _closure19(Object _outerInstance, Object _thisObject) {
                                                            CallSite[] callSiteArray = _closure19.$getCallSiteArray();
                                                            super(_outerInstance, _thisObject);
                                                        }

                                                        public Object doCall(Object it) {
                                                            CallSite[] callSiteArray = _closure19.$getCallSiteArray();
                                                            public class _closure20
                                                            extends Closure
                                                            implements GeneratedClosure {
                                                                private static /* synthetic */ ClassInfo $staticClassInfo;
                                                                public static transient /* synthetic */ boolean __$stMC;
                                                                private static /* synthetic */ SoftReference $callSiteArray;

                                                                public _closure20(Object _outerInstance, Object _thisObject) {
                                                                    CallSite[] callSiteArray = _closure20.$getCallSiteArray();
                                                                    super(_outerInstance, _thisObject);
                                                                }

                                                                public Object doCall(Object it) {
                                                                    CallSite[] callSiteArray = _closure20.$getCallSiteArray();
                                                                    return callSiteArray[0].call(it, 0);
                                                                }

                                                                public Object doCall() {
                                                                    CallSite[] callSiteArray = _closure20.$getCallSiteArray();
                                                                    return this.doCall(null);
                                                                }

                                                                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                                    if (this.getClass() != _closure20.class) {
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
                                                                    stringArray[0] = "getAt";
                                                                    return new CallSiteArray(_closure20.class, stringArray);
                                                                }

                                                                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                                    CallSiteArray callSiteArray;
                                                                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                                        callSiteArray = _closure20.$createCallSiteArray();
                                                                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                                    }
                                                                    return callSiteArray.array;
                                                                }
                                                            }
                                                            callSiteArray[0].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"header", "Index", "read", new _closure20(this, this.getThisObject())}));
                                                            public class _closure21
                                                            extends Closure
                                                            implements GeneratedClosure {
                                                                private static /* synthetic */ ClassInfo $staticClassInfo;
                                                                public static transient /* synthetic */ boolean __$stMC;
                                                                private static /* synthetic */ SoftReference $callSiteArray;

                                                                public _closure21(Object _outerInstance, Object _thisObject) {
                                                                    CallSite[] callSiteArray = _closure21.$getCallSiteArray();
                                                                    super(_outerInstance, _thisObject);
                                                                }

                                                                public Object doCall(Object it) {
                                                                    CallSite[] callSiteArray = _closure21.$getCallSiteArray();
                                                                    return callSiteArray[0].call(it, 1);
                                                                }

                                                                public Object doCall() {
                                                                    CallSite[] callSiteArray = _closure21.$getCallSiteArray();
                                                                    return this.doCall(null);
                                                                }

                                                                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                                    if (this.getClass() != _closure21.class) {
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
                                                                    stringArray[0] = "getAt";
                                                                    return new CallSiteArray(_closure21.class, stringArray);
                                                                }

                                                                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                                    CallSiteArray callSiteArray;
                                                                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                                        callSiteArray = _closure21.$createCallSiteArray();
                                                                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                                    }
                                                                    return callSiteArray.array;
                                                                }
                                                            }
                                                            callSiteArray[1].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"header", "Key", "read", new _closure21(this, this.getThisObject())}));
                                                            public class _closure22
                                                            extends Closure
                                                            implements GeneratedClosure {
                                                                private static /* synthetic */ ClassInfo $staticClassInfo;
                                                                public static transient /* synthetic */ boolean __$stMC;
                                                                private static /* synthetic */ SoftReference $callSiteArray;

                                                                public _closure22(Object _outerInstance, Object _thisObject) {
                                                                    CallSite[] callSiteArray = _closure22.$getCallSiteArray();
                                                                    super(_outerInstance, _thisObject);
                                                                }

                                                                public Object doCall(Object it) {
                                                                    CallSite[] callSiteArray = _closure22.$getCallSiteArray();
                                                                    return callSiteArray[0].call(it, 2);
                                                                }

                                                                public Object doCall() {
                                                                    CallSite[] callSiteArray = _closure22.$getCallSiteArray();
                                                                    return this.doCall(null);
                                                                }

                                                                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                                    if (this.getClass() != _closure22.class) {
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
                                                                    stringArray[0] = "getAt";
                                                                    return new CallSiteArray(_closure22.class, stringArray);
                                                                }

                                                                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                                    CallSiteArray callSiteArray;
                                                                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                                        callSiteArray = _closure22.$createCallSiteArray();
                                                                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                                    }
                                                                    return callSiteArray.array;
                                                                }
                                                            }
                                                            return callSiteArray[2].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"header", "Value", "read", new _closure22(this, this.getThisObject())}));
                                                        }

                                                        public Object doCall() {
                                                            CallSite[] callSiteArray = _closure19.$getCallSiteArray();
                                                            return this.doCall(null);
                                                        }

                                                        protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                            if (this.getClass() != _closure19.class) {
                                                                return ScriptBytecodeAdapter.initMetaClass(this);
                                                            }
                                                            ClassInfo classInfo = $staticClassInfo;
                                                            if (classInfo == null) {
                                                                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                                            }
                                                            return classInfo.getMetaClass();
                                                        }

                                                        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                                            stringArray[0] = "closureColumn";
                                                            stringArray[1] = "closureColumn";
                                                            stringArray[2] = "closureColumn";
                                                        }

                                                        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                                            String[] stringArray = new String[3];
                                                            _closure19.$createCallSiteArray_1(stringArray);
                                                            return new CallSiteArray(_closure19.class, stringArray);
                                                        }

                                                        private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                            CallSiteArray callSiteArray;
                                                            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                                callSiteArray = _closure19.$createCallSiteArray();
                                                                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                            }
                                                            return callSiteArray.array;
                                                        }
                                                    }
                                                    return callSiteArray[3].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"list", data}), new _closure19(this, this.getThisObject()));
                                                }

                                                public Object doCall() {
                                                    CallSite[] callSiteArray = _closure17.$getCallSiteArray();
                                                    return this.doCall(null);
                                                }

                                                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                    if (this.getClass() != _closure17.class) {
                                                        return ScriptBytecodeAdapter.initMetaClass(this);
                                                    }
                                                    ClassInfo classInfo = $staticClassInfo;
                                                    if (classInfo == null) {
                                                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                                    }
                                                    return classInfo.getMetaClass();
                                                }

                                                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                                    stringArray[0] = "collect";
                                                    stringArray[1] = "object";
                                                    stringArray[2] = "inspector";
                                                    stringArray[3] = "tableModel";
                                                }

                                                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                                    String[] stringArray = new String[4];
                                                    _closure17.$createCallSiteArray_1(stringArray);
                                                    return new CallSiteArray(_closure17.class, stringArray);
                                                }

                                                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                    CallSiteArray callSiteArray;
                                                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                        callSiteArray = _closure17.$createCallSiteArray();
                                                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                    }
                                                    return callSiteArray.array;
                                                }
                                            }
                                            Object object = callSiteArray[0].callCurrent((GroovyObject)this, new _closure17(this, this.getThisObject()));
                                            ScriptBytecodeAdapter.setGroovyObjectProperty(object, _closure9.class, this, "itemTable");
                                            return object;
                                        }

                                        public Object doCall() {
                                            CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                                            return this.doCall(null);
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

                                        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                            String[] stringArray = new String[1];
                                            stringArray[0] = "table";
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
                                    callSiteArray[5].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"name", " Map data "}), new _closure9(this, this.getThisObject()));
                                }
                                public class _closure10
                                extends Closure
                                implements GeneratedClosure {
                                    private static /* synthetic */ ClassInfo $staticClassInfo;
                                    public static transient /* synthetic */ boolean __$stMC;
                                    private static /* synthetic */ SoftReference $callSiteArray;

                                    public _closure10(Object _outerInstance, Object _thisObject) {
                                        CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                                        super(_outerInstance, _thisObject);
                                    }

                                    public Object doCall(Object it) {
                                        CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                                        public class _closure23
                                        extends Closure
                                        implements GeneratedClosure {
                                            private static /* synthetic */ ClassInfo $staticClassInfo;
                                            public static transient /* synthetic */ boolean __$stMC;
                                            private static /* synthetic */ SoftReference $callSiteArray;

                                            public _closure23(Object _outerInstance, Object _thisObject) {
                                                CallSite[] callSiteArray = _closure23.$getCallSiteArray();
                                                super(_outerInstance, _thisObject);
                                            }

                                            public Object doCall(Object it) {
                                                CallSite[] callSiteArray = _closure23.$getCallSiteArray();
                                                Object data = callSiteArray[0].call(Inspector.class, callSiteArray[1].call(callSiteArray[2].callGetProperty(callSiteArray[3].callGroovyObjectGetProperty(this))));
                                                callSiteArray[4].call(data, callSiteArray[5].call(Inspector.class, callSiteArray[6].call(callSiteArray[7].callGetProperty(callSiteArray[8].callGroovyObjectGetProperty(this)))));
                                                public class _closure24
                                                extends Closure
                                                implements GeneratedClosure {
                                                    private static /* synthetic */ ClassInfo $staticClassInfo;
                                                    public static transient /* synthetic */ boolean __$stMC;
                                                    private static /* synthetic */ SoftReference $callSiteArray;

                                                    public _closure24(Object _outerInstance, Object _thisObject) {
                                                        CallSite[] callSiteArray = _closure24.$getCallSiteArray();
                                                        super(_outerInstance, _thisObject);
                                                    }

                                                    public Object doCall(Object it) {
                                                        CallSite[] callSiteArray = _closure24.$getCallSiteArray();
                                                        public class _closure25
                                                        extends Closure
                                                        implements GeneratedClosure {
                                                            private static /* synthetic */ ClassInfo $staticClassInfo;
                                                            public static transient /* synthetic */ boolean __$stMC;
                                                            private static /* synthetic */ SoftReference $callSiteArray;

                                                            public _closure25(Object _outerInstance, Object _thisObject) {
                                                                CallSite[] callSiteArray = _closure25.$getCallSiteArray();
                                                                super(_outerInstance, _thisObject);
                                                            }

                                                            public Object doCall(Object it) {
                                                                CallSite[] callSiteArray = _closure25.$getCallSiteArray();
                                                                return callSiteArray[0].call(it, callSiteArray[1].callGetProperty(Inspector.class));
                                                            }

                                                            public Object doCall() {
                                                                CallSite[] callSiteArray = _closure25.$getCallSiteArray();
                                                                return this.doCall(null);
                                                            }

                                                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                                if (this.getClass() != _closure25.class) {
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
                                                                stringArray[1] = "MEMBER_NAME_IDX";
                                                            }

                                                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                                                String[] stringArray = new String[2];
                                                                _closure25.$createCallSiteArray_1(stringArray);
                                                                return new CallSiteArray(_closure25.class, stringArray);
                                                            }

                                                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                                CallSiteArray callSiteArray;
                                                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                                    callSiteArray = _closure25.$createCallSiteArray();
                                                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                                }
                                                                return callSiteArray.array;
                                                            }
                                                        }
                                                        callSiteArray[0].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"header", "Name", "read", new _closure25(this, this.getThisObject())}));
                                                        public class _closure26
                                                        extends Closure
                                                        implements GeneratedClosure {
                                                            private static /* synthetic */ ClassInfo $staticClassInfo;
                                                            public static transient /* synthetic */ boolean __$stMC;
                                                            private static /* synthetic */ SoftReference $callSiteArray;

                                                            public _closure26(Object _outerInstance, Object _thisObject) {
                                                                CallSite[] callSiteArray = _closure26.$getCallSiteArray();
                                                                super(_outerInstance, _thisObject);
                                                            }

                                                            public Object doCall(Object it) {
                                                                CallSite[] callSiteArray = _closure26.$getCallSiteArray();
                                                                return callSiteArray[0].call(it, callSiteArray[1].callGetProperty(Inspector.class));
                                                            }

                                                            public Object doCall() {
                                                                CallSite[] callSiteArray = _closure26.$getCallSiteArray();
                                                                return this.doCall(null);
                                                            }

                                                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                                if (this.getClass() != _closure26.class) {
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
                                                                stringArray[1] = "MEMBER_VALUE_IDX";
                                                            }

                                                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                                                String[] stringArray = new String[2];
                                                                _closure26.$createCallSiteArray_1(stringArray);
                                                                return new CallSiteArray(_closure26.class, stringArray);
                                                            }

                                                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                                CallSiteArray callSiteArray;
                                                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                                    callSiteArray = _closure26.$createCallSiteArray();
                                                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                                }
                                                                return callSiteArray.array;
                                                            }
                                                        }
                                                        callSiteArray[1].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"header", "Value", "read", new _closure26(this, this.getThisObject())}));
                                                        public class _closure27
                                                        extends Closure
                                                        implements GeneratedClosure {
                                                            private static /* synthetic */ ClassInfo $staticClassInfo;
                                                            public static transient /* synthetic */ boolean __$stMC;
                                                            private static /* synthetic */ SoftReference $callSiteArray;

                                                            public _closure27(Object _outerInstance, Object _thisObject) {
                                                                CallSite[] callSiteArray = _closure27.$getCallSiteArray();
                                                                super(_outerInstance, _thisObject);
                                                            }

                                                            public Object doCall(Object it) {
                                                                CallSite[] callSiteArray = _closure27.$getCallSiteArray();
                                                                return callSiteArray[0].call(it, callSiteArray[1].callGetProperty(Inspector.class));
                                                            }

                                                            public Object doCall() {
                                                                CallSite[] callSiteArray = _closure27.$getCallSiteArray();
                                                                return this.doCall(null);
                                                            }

                                                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                                if (this.getClass() != _closure27.class) {
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
                                                                stringArray[1] = "MEMBER_TYPE_IDX";
                                                            }

                                                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                                                String[] stringArray = new String[2];
                                                                _closure27.$createCallSiteArray_1(stringArray);
                                                                return new CallSiteArray(_closure27.class, stringArray);
                                                            }

                                                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                                CallSiteArray callSiteArray;
                                                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                                    callSiteArray = _closure27.$createCallSiteArray();
                                                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                                }
                                                                return callSiteArray.array;
                                                            }
                                                        }
                                                        callSiteArray[2].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"header", "Type", "read", new _closure27(this, this.getThisObject())}));
                                                        public class _closure28
                                                        extends Closure
                                                        implements GeneratedClosure {
                                                            private static /* synthetic */ ClassInfo $staticClassInfo;
                                                            public static transient /* synthetic */ boolean __$stMC;
                                                            private static /* synthetic */ SoftReference $callSiteArray;

                                                            public _closure28(Object _outerInstance, Object _thisObject) {
                                                                CallSite[] callSiteArray = _closure28.$getCallSiteArray();
                                                                super(_outerInstance, _thisObject);
                                                            }

                                                            public Object doCall(Object it) {
                                                                CallSite[] callSiteArray = _closure28.$getCallSiteArray();
                                                                return callSiteArray[0].call(it, callSiteArray[1].callGetProperty(Inspector.class));
                                                            }

                                                            public Object doCall() {
                                                                CallSite[] callSiteArray = _closure28.$getCallSiteArray();
                                                                return this.doCall(null);
                                                            }

                                                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                                if (this.getClass() != _closure28.class) {
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
                                                                stringArray[1] = "MEMBER_ORIGIN_IDX";
                                                            }

                                                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                                                String[] stringArray = new String[2];
                                                                _closure28.$createCallSiteArray_1(stringArray);
                                                                return new CallSiteArray(_closure28.class, stringArray);
                                                            }

                                                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                                CallSiteArray callSiteArray;
                                                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                                    callSiteArray = _closure28.$createCallSiteArray();
                                                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                                }
                                                                return callSiteArray.array;
                                                            }
                                                        }
                                                        callSiteArray[3].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"header", "Origin", "read", new _closure28(this, this.getThisObject())}));
                                                        public class _closure29
                                                        extends Closure
                                                        implements GeneratedClosure {
                                                            private static /* synthetic */ ClassInfo $staticClassInfo;
                                                            public static transient /* synthetic */ boolean __$stMC;
                                                            private static /* synthetic */ SoftReference $callSiteArray;

                                                            public _closure29(Object _outerInstance, Object _thisObject) {
                                                                CallSite[] callSiteArray = _closure29.$getCallSiteArray();
                                                                super(_outerInstance, _thisObject);
                                                            }

                                                            public Object doCall(Object it) {
                                                                CallSite[] callSiteArray = _closure29.$getCallSiteArray();
                                                                return callSiteArray[0].call(it, callSiteArray[1].callGetProperty(Inspector.class));
                                                            }

                                                            public Object doCall() {
                                                                CallSite[] callSiteArray = _closure29.$getCallSiteArray();
                                                                return this.doCall(null);
                                                            }

                                                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                                if (this.getClass() != _closure29.class) {
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
                                                                stringArray[1] = "MEMBER_MODIFIER_IDX";
                                                            }

                                                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                                                String[] stringArray = new String[2];
                                                                _closure29.$createCallSiteArray_1(stringArray);
                                                                return new CallSiteArray(_closure29.class, stringArray);
                                                            }

                                                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                                CallSiteArray callSiteArray;
                                                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                                    callSiteArray = _closure29.$createCallSiteArray();
                                                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                                }
                                                                return callSiteArray.array;
                                                            }
                                                        }
                                                        callSiteArray[4].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"header", "Modifier", "read", new _closure29(this, this.getThisObject())}));
                                                        public class _closure30
                                                        extends Closure
                                                        implements GeneratedClosure {
                                                            private static /* synthetic */ ClassInfo $staticClassInfo;
                                                            public static transient /* synthetic */ boolean __$stMC;
                                                            private static /* synthetic */ SoftReference $callSiteArray;

                                                            public _closure30(Object _outerInstance, Object _thisObject) {
                                                                CallSite[] callSiteArray = _closure30.$getCallSiteArray();
                                                                super(_outerInstance, _thisObject);
                                                            }

                                                            public Object doCall(Object it) {
                                                                CallSite[] callSiteArray = _closure30.$getCallSiteArray();
                                                                return callSiteArray[0].call(it, callSiteArray[1].callGetProperty(Inspector.class));
                                                            }

                                                            public Object doCall() {
                                                                CallSite[] callSiteArray = _closure30.$getCallSiteArray();
                                                                return this.doCall(null);
                                                            }

                                                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                                if (this.getClass() != _closure30.class) {
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
                                                                stringArray[1] = "MEMBER_DECLARER_IDX";
                                                            }

                                                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                                                String[] stringArray = new String[2];
                                                                _closure30.$createCallSiteArray_1(stringArray);
                                                                return new CallSiteArray(_closure30.class, stringArray);
                                                            }

                                                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                                CallSiteArray callSiteArray;
                                                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                                    callSiteArray = _closure30.$createCallSiteArray();
                                                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                                }
                                                                return callSiteArray.array;
                                                            }
                                                        }
                                                        return callSiteArray[5].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"header", "Declarer", "read", new _closure30(this, this.getThisObject())}));
                                                    }

                                                    public Object doCall() {
                                                        CallSite[] callSiteArray = _closure24.$getCallSiteArray();
                                                        return this.doCall(null);
                                                    }

                                                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                        if (this.getClass() != _closure24.class) {
                                                            return ScriptBytecodeAdapter.initMetaClass(this);
                                                        }
                                                        ClassInfo classInfo = $staticClassInfo;
                                                        if (classInfo == null) {
                                                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                                        }
                                                        return classInfo.getMetaClass();
                                                    }

                                                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                                        stringArray[0] = "closureColumn";
                                                        stringArray[1] = "closureColumn";
                                                        stringArray[2] = "closureColumn";
                                                        stringArray[3] = "closureColumn";
                                                        stringArray[4] = "closureColumn";
                                                        stringArray[5] = "closureColumn";
                                                    }

                                                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                                        String[] stringArray = new String[6];
                                                        _closure24.$createCallSiteArray_1(stringArray);
                                                        return new CallSiteArray(_closure24.class, stringArray);
                                                    }

                                                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                        CallSiteArray callSiteArray;
                                                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                            callSiteArray = _closure24.$createCallSiteArray();
                                                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                        }
                                                        return callSiteArray.array;
                                                    }
                                                }
                                                return callSiteArray[9].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"list", data}), new _closure24(this, this.getThisObject()));
                                            }

                                            public Object doCall() {
                                                CallSite[] callSiteArray = _closure23.$getCallSiteArray();
                                                return this.doCall(null);
                                            }

                                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                if (this.getClass() != _closure23.class) {
                                                    return ScriptBytecodeAdapter.initMetaClass(this);
                                                }
                                                ClassInfo classInfo = $staticClassInfo;
                                                if (classInfo == null) {
                                                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                                }
                                                return classInfo.getMetaClass();
                                            }

                                            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                                stringArray[0] = "sort";
                                                stringArray[1] = "toList";
                                                stringArray[2] = "publicFields";
                                                stringArray[3] = "inspector";
                                                stringArray[4] = "addAll";
                                                stringArray[5] = "sort";
                                                stringArray[6] = "toList";
                                                stringArray[7] = "propertyInfo";
                                                stringArray[8] = "inspector";
                                                stringArray[9] = "tableModel";
                                            }

                                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                                String[] stringArray = new String[10];
                                                _closure23.$createCallSiteArray_1(stringArray);
                                                return new CallSiteArray(_closure23.class, stringArray);
                                            }

                                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                CallSiteArray callSiteArray;
                                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                    callSiteArray = _closure23.$createCallSiteArray();
                                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                }
                                                return callSiteArray.array;
                                            }
                                        }
                                        Object object = callSiteArray[0].callCurrent((GroovyObject)this, new _closure23(this, this.getThisObject()));
                                        ScriptBytecodeAdapter.setGroovyObjectProperty(object, _closure10.class, this, "fieldTable");
                                        return object;
                                    }

                                    public Object doCall() {
                                        CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                                        return this.doCall(null);
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

                                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                        String[] stringArray = new String[1];
                                        stringArray[0] = "table";
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
                                callSiteArray[6].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"name", " Public Fields and Properties "}), new _closure10(this, this.getThisObject()));
                                public class _closure11
                                extends Closure
                                implements GeneratedClosure {
                                    private static /* synthetic */ ClassInfo $staticClassInfo;
                                    public static transient /* synthetic */ boolean __$stMC;
                                    private static /* synthetic */ SoftReference $callSiteArray;

                                    public _closure11(Object _outerInstance, Object _thisObject) {
                                        CallSite[] callSiteArray = _closure11.$getCallSiteArray();
                                        super(_outerInstance, _thisObject);
                                    }

                                    public Object doCall(Object it) {
                                        CallSite[] callSiteArray = _closure11.$getCallSiteArray();
                                        public class _closure31
                                        extends Closure
                                        implements GeneratedClosure {
                                            private static /* synthetic */ ClassInfo $staticClassInfo;
                                            public static transient /* synthetic */ boolean __$stMC;
                                            private static /* synthetic */ SoftReference $callSiteArray;

                                            public _closure31(Object _outerInstance, Object _thisObject) {
                                                CallSite[] callSiteArray = _closure31.$getCallSiteArray();
                                                super(_outerInstance, _thisObject);
                                            }

                                            public Object doCall(Object it) {
                                                CallSite[] callSiteArray = _closure31.$getCallSiteArray();
                                                Object data = callSiteArray[0].call(Inspector.class, callSiteArray[1].call(callSiteArray[2].callGetProperty(callSiteArray[3].callGroovyObjectGetProperty(this))));
                                                callSiteArray[4].call(data, callSiteArray[5].call(Inspector.class, callSiteArray[6].call(callSiteArray[7].callGetProperty(callSiteArray[8].callGroovyObjectGetProperty(this)))));
                                                public class _closure32
                                                extends Closure
                                                implements GeneratedClosure {
                                                    private static /* synthetic */ ClassInfo $staticClassInfo;
                                                    public static transient /* synthetic */ boolean __$stMC;
                                                    private static /* synthetic */ SoftReference $callSiteArray;

                                                    public _closure32(Object _outerInstance, Object _thisObject) {
                                                        CallSite[] callSiteArray = _closure32.$getCallSiteArray();
                                                        super(_outerInstance, _thisObject);
                                                    }

                                                    public Object doCall(Object it) {
                                                        CallSite[] callSiteArray = _closure32.$getCallSiteArray();
                                                        public class _closure33
                                                        extends Closure
                                                        implements GeneratedClosure {
                                                            private static /* synthetic */ ClassInfo $staticClassInfo;
                                                            public static transient /* synthetic */ boolean __$stMC;
                                                            private static /* synthetic */ SoftReference $callSiteArray;

                                                            public _closure33(Object _outerInstance, Object _thisObject) {
                                                                CallSite[] callSiteArray = _closure33.$getCallSiteArray();
                                                                super(_outerInstance, _thisObject);
                                                            }

                                                            public Object doCall(Object it) {
                                                                CallSite[] callSiteArray = _closure33.$getCallSiteArray();
                                                                return callSiteArray[0].call(it, callSiteArray[1].callGetProperty(Inspector.class));
                                                            }

                                                            public Object doCall() {
                                                                CallSite[] callSiteArray = _closure33.$getCallSiteArray();
                                                                return this.doCall(null);
                                                            }

                                                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                                if (this.getClass() != _closure33.class) {
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
                                                                stringArray[1] = "MEMBER_NAME_IDX";
                                                            }

                                                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                                                String[] stringArray = new String[2];
                                                                _closure33.$createCallSiteArray_1(stringArray);
                                                                return new CallSiteArray(_closure33.class, stringArray);
                                                            }

                                                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                                CallSiteArray callSiteArray;
                                                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                                    callSiteArray = _closure33.$createCallSiteArray();
                                                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                                }
                                                                return callSiteArray.array;
                                                            }
                                                        }
                                                        callSiteArray[0].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"header", "Name", "read", new _closure33(this, this.getThisObject())}));
                                                        public class _closure34
                                                        extends Closure
                                                        implements GeneratedClosure {
                                                            private static /* synthetic */ ClassInfo $staticClassInfo;
                                                            public static transient /* synthetic */ boolean __$stMC;
                                                            private static /* synthetic */ SoftReference $callSiteArray;

                                                            public _closure34(Object _outerInstance, Object _thisObject) {
                                                                CallSite[] callSiteArray = _closure34.$getCallSiteArray();
                                                                super(_outerInstance, _thisObject);
                                                            }

                                                            public Object doCall(Object it) {
                                                                CallSite[] callSiteArray = _closure34.$getCallSiteArray();
                                                                return callSiteArray[0].call(it, callSiteArray[1].callGetProperty(Inspector.class));
                                                            }

                                                            public Object doCall() {
                                                                CallSite[] callSiteArray = _closure34.$getCallSiteArray();
                                                                return this.doCall(null);
                                                            }

                                                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                                if (this.getClass() != _closure34.class) {
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
                                                                stringArray[1] = "MEMBER_PARAMS_IDX";
                                                            }

                                                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                                                String[] stringArray = new String[2];
                                                                _closure34.$createCallSiteArray_1(stringArray);
                                                                return new CallSiteArray(_closure34.class, stringArray);
                                                            }

                                                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                                CallSiteArray callSiteArray;
                                                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                                    callSiteArray = _closure34.$createCallSiteArray();
                                                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                                }
                                                                return callSiteArray.array;
                                                            }
                                                        }
                                                        callSiteArray[1].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"header", "Params", "read", new _closure34(this, this.getThisObject())}));
                                                        public class _closure35
                                                        extends Closure
                                                        implements GeneratedClosure {
                                                            private static /* synthetic */ ClassInfo $staticClassInfo;
                                                            public static transient /* synthetic */ boolean __$stMC;
                                                            private static /* synthetic */ SoftReference $callSiteArray;

                                                            public _closure35(Object _outerInstance, Object _thisObject) {
                                                                CallSite[] callSiteArray = _closure35.$getCallSiteArray();
                                                                super(_outerInstance, _thisObject);
                                                            }

                                                            public Object doCall(Object it) {
                                                                CallSite[] callSiteArray = _closure35.$getCallSiteArray();
                                                                return callSiteArray[0].call(it, callSiteArray[1].callGetProperty(Inspector.class));
                                                            }

                                                            public Object doCall() {
                                                                CallSite[] callSiteArray = _closure35.$getCallSiteArray();
                                                                return this.doCall(null);
                                                            }

                                                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                                if (this.getClass() != _closure35.class) {
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
                                                                stringArray[1] = "MEMBER_TYPE_IDX";
                                                            }

                                                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                                                String[] stringArray = new String[2];
                                                                _closure35.$createCallSiteArray_1(stringArray);
                                                                return new CallSiteArray(_closure35.class, stringArray);
                                                            }

                                                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                                CallSiteArray callSiteArray;
                                                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                                    callSiteArray = _closure35.$createCallSiteArray();
                                                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                                }
                                                                return callSiteArray.array;
                                                            }
                                                        }
                                                        callSiteArray[2].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"header", "Type", "read", new _closure35(this, this.getThisObject())}));
                                                        public class _closure36
                                                        extends Closure
                                                        implements GeneratedClosure {
                                                            private static /* synthetic */ ClassInfo $staticClassInfo;
                                                            public static transient /* synthetic */ boolean __$stMC;
                                                            private static /* synthetic */ SoftReference $callSiteArray;

                                                            public _closure36(Object _outerInstance, Object _thisObject) {
                                                                CallSite[] callSiteArray = _closure36.$getCallSiteArray();
                                                                super(_outerInstance, _thisObject);
                                                            }

                                                            public Object doCall(Object it) {
                                                                CallSite[] callSiteArray = _closure36.$getCallSiteArray();
                                                                return callSiteArray[0].call(it, callSiteArray[1].callGetProperty(Inspector.class));
                                                            }

                                                            public Object doCall() {
                                                                CallSite[] callSiteArray = _closure36.$getCallSiteArray();
                                                                return this.doCall(null);
                                                            }

                                                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                                if (this.getClass() != _closure36.class) {
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
                                                                stringArray[1] = "MEMBER_ORIGIN_IDX";
                                                            }

                                                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                                                String[] stringArray = new String[2];
                                                                _closure36.$createCallSiteArray_1(stringArray);
                                                                return new CallSiteArray(_closure36.class, stringArray);
                                                            }

                                                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                                CallSiteArray callSiteArray;
                                                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                                    callSiteArray = _closure36.$createCallSiteArray();
                                                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                                }
                                                                return callSiteArray.array;
                                                            }
                                                        }
                                                        callSiteArray[3].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"header", "Origin", "read", new _closure36(this, this.getThisObject())}));
                                                        public class _closure37
                                                        extends Closure
                                                        implements GeneratedClosure {
                                                            private static /* synthetic */ ClassInfo $staticClassInfo;
                                                            public static transient /* synthetic */ boolean __$stMC;
                                                            private static /* synthetic */ SoftReference $callSiteArray;

                                                            public _closure37(Object _outerInstance, Object _thisObject) {
                                                                CallSite[] callSiteArray = _closure37.$getCallSiteArray();
                                                                super(_outerInstance, _thisObject);
                                                            }

                                                            public Object doCall(Object it) {
                                                                CallSite[] callSiteArray = _closure37.$getCallSiteArray();
                                                                return callSiteArray[0].call(it, callSiteArray[1].callGetProperty(Inspector.class));
                                                            }

                                                            public Object doCall() {
                                                                CallSite[] callSiteArray = _closure37.$getCallSiteArray();
                                                                return this.doCall(null);
                                                            }

                                                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                                if (this.getClass() != _closure37.class) {
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
                                                                stringArray[1] = "MEMBER_MODIFIER_IDX";
                                                            }

                                                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                                                String[] stringArray = new String[2];
                                                                _closure37.$createCallSiteArray_1(stringArray);
                                                                return new CallSiteArray(_closure37.class, stringArray);
                                                            }

                                                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                                CallSiteArray callSiteArray;
                                                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                                    callSiteArray = _closure37.$createCallSiteArray();
                                                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                                }
                                                                return callSiteArray.array;
                                                            }
                                                        }
                                                        callSiteArray[4].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"header", "Modifier", "read", new _closure37(this, this.getThisObject())}));
                                                        public class _closure38
                                                        extends Closure
                                                        implements GeneratedClosure {
                                                            private static /* synthetic */ ClassInfo $staticClassInfo;
                                                            public static transient /* synthetic */ boolean __$stMC;
                                                            private static /* synthetic */ SoftReference $callSiteArray;

                                                            public _closure38(Object _outerInstance, Object _thisObject) {
                                                                CallSite[] callSiteArray = _closure38.$getCallSiteArray();
                                                                super(_outerInstance, _thisObject);
                                                            }

                                                            public Object doCall(Object it) {
                                                                CallSite[] callSiteArray = _closure38.$getCallSiteArray();
                                                                return callSiteArray[0].call(it, callSiteArray[1].callGetProperty(Inspector.class));
                                                            }

                                                            public Object doCall() {
                                                                CallSite[] callSiteArray = _closure38.$getCallSiteArray();
                                                                return this.doCall(null);
                                                            }

                                                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                                if (this.getClass() != _closure38.class) {
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
                                                                stringArray[1] = "MEMBER_DECLARER_IDX";
                                                            }

                                                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                                                String[] stringArray = new String[2];
                                                                _closure38.$createCallSiteArray_1(stringArray);
                                                                return new CallSiteArray(_closure38.class, stringArray);
                                                            }

                                                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                                CallSiteArray callSiteArray;
                                                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                                    callSiteArray = _closure38.$createCallSiteArray();
                                                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                                }
                                                                return callSiteArray.array;
                                                            }
                                                        }
                                                        callSiteArray[5].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"header", "Declarer", "read", new _closure38(this, this.getThisObject())}));
                                                        public class _closure39
                                                        extends Closure
                                                        implements GeneratedClosure {
                                                            private static /* synthetic */ ClassInfo $staticClassInfo;
                                                            public static transient /* synthetic */ boolean __$stMC;
                                                            private static /* synthetic */ SoftReference $callSiteArray;

                                                            public _closure39(Object _outerInstance, Object _thisObject) {
                                                                CallSite[] callSiteArray = _closure39.$getCallSiteArray();
                                                                super(_outerInstance, _thisObject);
                                                            }

                                                            public Object doCall(Object it) {
                                                                CallSite[] callSiteArray = _closure39.$getCallSiteArray();
                                                                return callSiteArray[0].call(it, callSiteArray[1].callGetProperty(Inspector.class));
                                                            }

                                                            public Object doCall() {
                                                                CallSite[] callSiteArray = _closure39.$getCallSiteArray();
                                                                return this.doCall(null);
                                                            }

                                                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                                if (this.getClass() != _closure39.class) {
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
                                                                stringArray[1] = "MEMBER_EXCEPTIONS_IDX";
                                                            }

                                                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                                                String[] stringArray = new String[2];
                                                                _closure39.$createCallSiteArray_1(stringArray);
                                                                return new CallSiteArray(_closure39.class, stringArray);
                                                            }

                                                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                                CallSiteArray callSiteArray;
                                                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                                    callSiteArray = _closure39.$createCallSiteArray();
                                                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                                }
                                                                return callSiteArray.array;
                                                            }
                                                        }
                                                        return callSiteArray[6].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"header", "Exceptions", "read", new _closure39(this, this.getThisObject())}));
                                                    }

                                                    public Object doCall() {
                                                        CallSite[] callSiteArray = _closure32.$getCallSiteArray();
                                                        return this.doCall(null);
                                                    }

                                                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                        if (this.getClass() != _closure32.class) {
                                                            return ScriptBytecodeAdapter.initMetaClass(this);
                                                        }
                                                        ClassInfo classInfo = $staticClassInfo;
                                                        if (classInfo == null) {
                                                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                                        }
                                                        return classInfo.getMetaClass();
                                                    }

                                                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                                        stringArray[0] = "closureColumn";
                                                        stringArray[1] = "closureColumn";
                                                        stringArray[2] = "closureColumn";
                                                        stringArray[3] = "closureColumn";
                                                        stringArray[4] = "closureColumn";
                                                        stringArray[5] = "closureColumn";
                                                        stringArray[6] = "closureColumn";
                                                    }

                                                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                                        String[] stringArray = new String[7];
                                                        _closure32.$createCallSiteArray_1(stringArray);
                                                        return new CallSiteArray(_closure32.class, stringArray);
                                                    }

                                                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                        CallSiteArray callSiteArray;
                                                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                            callSiteArray = _closure32.$createCallSiteArray();
                                                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                        }
                                                        return callSiteArray.array;
                                                    }
                                                }
                                                return callSiteArray[9].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"list", data}), new _closure32(this, this.getThisObject()));
                                            }

                                            public Object doCall() {
                                                CallSite[] callSiteArray = _closure31.$getCallSiteArray();
                                                return this.doCall(null);
                                            }

                                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                if (this.getClass() != _closure31.class) {
                                                    return ScriptBytecodeAdapter.initMetaClass(this);
                                                }
                                                ClassInfo classInfo = $staticClassInfo;
                                                if (classInfo == null) {
                                                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                                }
                                                return classInfo.getMetaClass();
                                            }

                                            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                                stringArray[0] = "sort";
                                                stringArray[1] = "toList";
                                                stringArray[2] = "methods";
                                                stringArray[3] = "inspector";
                                                stringArray[4] = "addAll";
                                                stringArray[5] = "sort";
                                                stringArray[6] = "toList";
                                                stringArray[7] = "metaMethods";
                                                stringArray[8] = "inspector";
                                                stringArray[9] = "tableModel";
                                            }

                                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                                String[] stringArray = new String[10];
                                                _closure31.$createCallSiteArray_1(stringArray);
                                                return new CallSiteArray(_closure31.class, stringArray);
                                            }

                                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                CallSiteArray callSiteArray;
                                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                    callSiteArray = _closure31.$createCallSiteArray();
                                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                }
                                                return callSiteArray.array;
                                            }
                                        }
                                        Object object = callSiteArray[0].callCurrent((GroovyObject)this, new _closure31(this, this.getThisObject()));
                                        ScriptBytecodeAdapter.setGroovyObjectProperty(object, _closure11.class, this, "methodTable");
                                        return object;
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

                                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                        String[] stringArray = new String[1];
                                        stringArray[0] = "table";
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
                                return callSiteArray[7].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"name", " (Meta) Methods "}), new _closure11(this, this.getThisObject()));
                            }

                            public Object doCall() {
                                CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                                return this.doCall(null);
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
                                stringArray[0] = "object";
                                stringArray[1] = "inspector";
                                stringArray[2] = "scrollPane";
                                stringArray[3] = "object";
                                stringArray[4] = "inspector";
                                stringArray[5] = "scrollPane";
                                stringArray[6] = "scrollPane";
                                stringArray[7] = "scrollPane";
                            }

                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                String[] stringArray = new String[8];
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
                        return callSiteArray[4].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"constraints", callSiteArray[5].callGroovyObjectGetProperty(this)}), new _closure7(this, this.getThisObject()));
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure3.$getCallSiteArray();
                        return this.doCall(null);
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
                        stringArray[0] = "borderLayout";
                        stringArray[1] = "panel";
                        stringArray[2] = "emptyBorder";
                        stringArray[3] = "NORTH";
                        stringArray[4] = "tabbedPane";
                        stringArray[5] = "CENTER";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[6];
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
                return callSiteArray[1].callCurrent((GroovyObject)this, new _closure3(this, this.getThisObject()));
            }

            public Object doCall() {
                CallSite[] callSiteArray = _run_closure1.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _run_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "menuBar";
                stringArray[1] = "panel";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _run_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_run_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _run_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        this.frame = object = callSiteArray[5].call(this.swing, ScriptBytecodeAdapter.createMap(new Object[]{"title", "Groovy Object Browser", "location", ScriptBytecodeAdapter.createList(new Object[]{200, 200}), "size", ScriptBytecodeAdapter.createList(new Object[]{800, 600}), "pack", true, "show", true, "iconImage", callSiteArray[6].callGetProperty(callSiteArray[7].call(this.swing, callSiteArray[8].callGetProperty(Console.class))), "defaultCloseOperation", callSiteArray[9].callGetProperty(WindowConstants.class)}), new _run_closure1(this, this));
        callSiteArray[10].callCurrent((GroovyObject)this, this.itemTable);
        callSiteArray[11].callCurrent((GroovyObject)this, this.mapTable);
        callSiteArray[12].callCurrent((GroovyObject)this, this.fieldTable);
        callSiteArray[13].callCurrent((GroovyObject)this, this.methodTable);
        callSiteArray[14].call(this.frame);
    }

    public void addSorter(Object table) {
        CallSite[] callSiteArray = ObjectBrowser.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareNotEqual(table, null)) {
            Object sorter = callSiteArray[15].callConstructor(TableSorter.class, callSiteArray[16].callGetProperty(table));
            Object object = sorter;
            ScriptBytecodeAdapter.setProperty(object, null, table, "model");
            callSiteArray[17].call(sorter, table);
        }
    }

    public void showAbout(EventObject evt) {
        CallSite[] callSiteArray = ObjectBrowser.$getCallSiteArray();
        Object pane = callSiteArray[18].call(this.swing);
        callSiteArray[19].call(pane, "An interactive GUI to explore object capabilities.");
        Object dialog = callSiteArray[20].call(pane, this.frame, "About Groovy Object Browser");
        callSiteArray[21].call(dialog);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ObjectBrowser.class) {
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

    public Object getInspector() {
        return this.inspector;
    }

    public void setInspector(Object object) {
        this.inspector = object;
    }

    public Object getSwing() {
        return this.swing;
    }

    public void setSwing(Object object) {
        this.swing = object;
    }

    public Object getFrame() {
        return this.frame;
    }

    public void setFrame(Object object) {
        this.frame = object;
    }

    public Object getFieldTable() {
        return this.fieldTable;
    }

    public void setFieldTable(Object object) {
        this.fieldTable = object;
    }

    public Object getMethodTable() {
        return this.methodTable;
    }

    public void setMethodTable(Object object) {
        this.methodTable = object;
    }

    public Object getItemTable() {
        return this.itemTable;
    }

    public void setItemTable(Object object) {
        this.itemTable = object;
    }

    public Object getMapTable() {
        return this.mapTable;
    }

    public void setMapTable(Object object) {
        this.mapTable = object;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "inspect";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "run";
        stringArray[4] = "<$constructor$>";
        stringArray[5] = "frame";
        stringArray[6] = "image";
        stringArray[7] = "imageIcon";
        stringArray[8] = "ICON_PATH";
        stringArray[9] = "DISPOSE_ON_CLOSE";
        stringArray[10] = "addSorter";
        stringArray[11] = "addSorter";
        stringArray[12] = "addSorter";
        stringArray[13] = "addSorter";
        stringArray[14] = "toFront";
        stringArray[15] = "<$constructor$>";
        stringArray[16] = "model";
        stringArray[17] = "addMouseListenerToHeaderInTable";
        stringArray[18] = "optionPane";
        stringArray[19] = "setMessage";
        stringArray[20] = "createDialog";
        stringArray[21] = "show";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[22];
        ObjectBrowser.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ObjectBrowser.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ObjectBrowser.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

