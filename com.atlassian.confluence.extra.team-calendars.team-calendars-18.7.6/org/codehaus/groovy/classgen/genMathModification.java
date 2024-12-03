/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.lang.Script;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class genMathModification
extends Script {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public genMathModification() {
        CallSite[] callSiteArray = genMathModification.$getCallSiteArray();
    }

    public genMathModification(Binding context) {
        CallSite[] callSiteArray = genMathModification.$getCallSiteArray();
        super(context);
    }

    public static void main(String ... args) {
        CallSite[] callSiteArray = genMathModification.$getCallSiteArray();
        callSiteArray[0].call(InvokerHelper.class, genMathModification.class, args);
    }

    @Override
    public Object run() {
        CallSite[] callSiteArray = genMathModification.$getCallSiteArray();
        List ops = ScriptBytecodeAdapter.createList(new Object[]{"plus", "minus", "multiply", "div", "or", "and", "xor", "intdiv", "mod", "leftShift", "rightShift", "rightShiftUnsigned"});
        Reference<Map> numbers = new Reference<Map>(ScriptBytecodeAdapter.createMap(new Object[]{"Byte", "byte", "Short", "short", "Integer", "int", "Long", "long", "Float", "float", "Double", "double"}));
        public class _run_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference numbers;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _run_closure1(Object _outerInstance, Object _thisObject, Reference numbers) {
                Reference reference;
                CallSite[] callSiteArray = _run_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.numbers = reference = numbers;
            }

            public Object doCall(Object op) {
                Reference<Object> op2 = new Reference<Object>(op);
                CallSite[] callSiteArray = _run_closure1.$getCallSiteArray();
                public class _closure4
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference op;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure4(Object _outerInstance, Object _thisObject, Reference op) {
                        Reference reference;
                        CallSite[] callSiteArray = _closure4.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.op = reference = op;
                    }

                    public Object doCall(Object wrappedType, Object type) {
                        CallSite[] callSiteArray = _closure4.$getCallSiteArray();
                        return callSiteArray[0].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{type, this.op.get()}, new String[]{"public boolean ", "_", ";"}));
                    }

                    public Object call(Object wrappedType, Object type) {
                        CallSite[] callSiteArray = _closure4.$getCallSiteArray();
                        return callSiteArray[1].callCurrent(this, wrappedType, type);
                    }

                    public Object getOp() {
                        CallSite[] callSiteArray = _closure4.$getCallSiteArray();
                        return this.op.get();
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
                        stringArray[0] = "println";
                        stringArray[1] = "doCall";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[2];
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
                return callSiteArray[0].call(this.numbers.get(), new _closure4(this, this.getThisObject(), op2));
            }

            public Object getNumbers() {
                CallSite[] callSiteArray = _run_closure1.$getCallSiteArray();
                return this.numbers.get();
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

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[1];
                stringArray[0] = "each";
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
        callSiteArray[1].call((Object)ops, new _run_closure1(this, this, numbers));
        public class _run_closure2
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference numbers;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _run_closure2(Object _outerInstance, Object _thisObject, Reference numbers) {
                Reference reference;
                CallSite[] callSiteArray = _run_closure2.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.numbers = reference = numbers;
            }

            public Object doCall(Object op) {
                Reference<Object> op2 = new Reference<Object>(op);
                CallSite[] callSiteArray = _run_closure2.$getCallSiteArray();
                callSiteArray[0].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{op2.get()}, new String[]{"if (\"", "\".equals(name)) {"}));
                public class _closure5
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference op;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure5(Object _outerInstance, Object _thisObject, Reference op) {
                        Reference reference;
                        CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.op = reference = op;
                    }

                    public Object doCall(Object wrappedType, Object type) {
                        CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                        return callSiteArray[0].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{wrappedType, type, this.op.get()}, new String[]{"if (klazz==", ".class) {\n                ", "_", " = true;\n            }"}));
                    }

                    public Object call(Object wrappedType, Object type) {
                        CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                        return callSiteArray[1].callCurrent(this, wrappedType, type);
                    }

                    public Object getOp() {
                        CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                        return this.op.get();
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
                        stringArray[0] = "println";
                        stringArray[1] = "doCall";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[2];
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
                callSiteArray[1].call(this.numbers.get(), new _closure5(this, this.getThisObject(), op2));
                callSiteArray[2].callCurrent((GroovyObject)this, "if (klazz==Object.class) {");
                public class _closure6
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference op;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure6(Object _outerInstance, Object _thisObject, Reference op) {
                        Reference reference;
                        CallSite[] callSiteArray = _closure6.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.op = reference = op;
                    }

                    public Object doCall(Object wrappedType, Object type) {
                        CallSite[] callSiteArray = _closure6.$getCallSiteArray();
                        return callSiteArray[0].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{type, this.op.get()}, new String[]{"", "_", " = true;"}));
                    }

                    public Object call(Object wrappedType, Object type) {
                        CallSite[] callSiteArray = _closure6.$getCallSiteArray();
                        return callSiteArray[1].callCurrent(this, wrappedType, type);
                    }

                    public Object getOp() {
                        CallSite[] callSiteArray = _closure6.$getCallSiteArray();
                        return this.op.get();
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
                        stringArray[0] = "println";
                        stringArray[1] = "doCall";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[2];
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
                callSiteArray[3].call(this.numbers.get(), new _closure6(this, this.getThisObject(), op2));
                callSiteArray[4].callCurrent((GroovyObject)this, "}");
                return callSiteArray[5].callCurrent((GroovyObject)this, "}");
            }

            public Object getNumbers() {
                CallSite[] callSiteArray = _run_closure2.$getCallSiteArray();
                return this.numbers.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _run_closure2.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "println";
                stringArray[1] = "each";
                stringArray[2] = "println";
                stringArray[3] = "each";
                stringArray[4] = "println";
                stringArray[5] = "println";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[6];
                _run_closure2.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_run_closure2.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _run_closure2.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[2].call((Object)ops, new _run_closure2(this, this, numbers));
        public class _run_closure3
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference numbers;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _run_closure3(Object _outerInstance, Object _thisObject, Reference numbers) {
                Reference reference;
                CallSite[] callSiteArray = _run_closure3.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.numbers = reference = numbers;
            }

            public Object doCall(Object op) {
                Reference<Object> op2 = new Reference<Object>(op);
                CallSite[] callSiteArray = _run_closure3.$getCallSiteArray();
                public class _closure7
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference numbers;
                    private /* synthetic */ Reference op;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure7(Object _outerInstance, Object _thisObject, Reference numbers, Reference op) {
                        Reference reference;
                        Reference reference2;
                        CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.numbers = reference2 = numbers;
                        this.op = reference = op;
                    }

                    /*
                     * WARNING - void declaration
                     */
                    public Object doCall(Object wrappedType1, Object type1) {
                        void var2_2;
                        Reference<Object> wrappedType12 = new Reference<Object>(wrappedType1);
                        Reference<void> type12 = new Reference<void>(var2_2);
                        CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                        public class _closure8
                        extends Closure
                        implements GeneratedClosure {
                            private /* synthetic */ Reference wrappedType1;
                            private /* synthetic */ Reference op;
                            private /* synthetic */ Reference type1;
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure8(Object _outerInstance, Object _thisObject, Reference wrappedType1, Reference op, Reference type1) {
                                Reference reference;
                                Reference reference2;
                                Reference reference3;
                                CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                                this.wrappedType1 = reference3 = wrappedType1;
                                this.op = reference2 = op;
                                this.type1 = reference = type1;
                            }

                            public Object doCall(Object wrappedType2, Object type2) {
                                CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                                Object math = callSiteArray[0].callCurrent(this, this.wrappedType1.get(), wrappedType2);
                                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[1].call(math, this.op.get()))) {
                                    callSiteArray[2].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{callSiteArray[3].callGetProperty(math), this.op.get(), this.type1.get(), type2, this.type1.get(), this.op.get(), this.op.get(), ScriptBytecodeAdapter.compareNotEqual(callSiteArray[4].callGetProperty(math), this.type1.get()) ? callSiteArray[5].call(callSiteArray[6].call((Object)"((", callSiteArray[7].callGetProperty(math)), ")op1)") : "op1", callSiteArray[8].call(math, this.op.get()), ScriptBytecodeAdapter.compareNotEqual(callSiteArray[9].callGetProperty(math), type2) ? callSiteArray[10].call(callSiteArray[11].call((Object)"((", callSiteArray[12].callGetProperty(math)), ")op2)") : "op2"}, new String[]{"public static ", " ", "(", " op1, ", " op2) {\n                   if (instance.", "_", ") {\n                      return ", "Slow(op1, op2);\n                   }\n                   else {\n                      return ", " ", " ", ";\n                   }\n                }"}));
                                    return callSiteArray[13].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{callSiteArray[14].callGetProperty(math), this.op.get(), this.type1.get(), type2, this.op.get(), callSiteArray[15].callGetProperty(math)}, new String[]{"private static ", " ", "Slow(", " op1,", " op2) {\n                      return ((Number)InvokerHelper.invokeMethod(op1, \"", "\", op2)).", "Value();\n                }"}));
                                }
                                return null;
                            }

                            public Object call(Object wrappedType2, Object type2) {
                                CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                                return callSiteArray[16].callCurrent(this, wrappedType2, type2);
                            }

                            public Object getWrappedType1() {
                                CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                                return this.wrappedType1.get();
                            }

                            public Object getOp() {
                                CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                                return this.op.get();
                            }

                            public Object getType1() {
                                CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                                return this.type1.get();
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
                                stringArray[0] = "getMath";
                                stringArray[1] = "getAt";
                                stringArray[2] = "println";
                                stringArray[3] = "resType";
                                stringArray[4] = "resType";
                                stringArray[5] = "plus";
                                stringArray[6] = "plus";
                                stringArray[7] = "resType";
                                stringArray[8] = "getAt";
                                stringArray[9] = "resType";
                                stringArray[10] = "plus";
                                stringArray[11] = "plus";
                                stringArray[12] = "resType";
                                stringArray[13] = "println";
                                stringArray[14] = "resType";
                                stringArray[15] = "resType";
                                stringArray[16] = "doCall";
                            }

                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                String[] stringArray = new String[17];
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
                        return callSiteArray[0].call(this.numbers.get(), new _closure8(this, this.getThisObject(), wrappedType12, this.op, type12));
                    }

                    /*
                     * WARNING - void declaration
                     */
                    public Object call(Object wrappedType1, Object type1) {
                        void var2_2;
                        Reference<Object> wrappedType12 = new Reference<Object>(wrappedType1);
                        Reference<void> type12 = new Reference<void>(var2_2);
                        CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                        return callSiteArray[1].callCurrent(this, wrappedType12.get(), type12.get());
                    }

                    public Object getNumbers() {
                        CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                        return this.numbers.get();
                    }

                    public Object getOp() {
                        CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                        return this.op.get();
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
                        stringArray[0] = "each";
                        stringArray[1] = "doCall";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[2];
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
                return callSiteArray[0].call(this.numbers.get(), new _closure7(this, this.getThisObject(), this.numbers, op2));
            }

            public Object getNumbers() {
                CallSite[] callSiteArray = _run_closure3.$getCallSiteArray();
                return this.numbers.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _run_closure3.class) {
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
                stringArray[0] = "each";
                return new CallSiteArray(_run_closure3.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _run_closure3.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return callSiteArray[3].call((Object)ops, new _run_closure3(this, this, numbers));
    }

    public Object isFloatingPoint(Object number) {
        CallSite[] callSiteArray = genMathModification.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return ScriptBytecodeAdapter.compareEqual(number, "Double") || ScriptBytecodeAdapter.compareEqual(number, "Float");
        }
        return ScriptBytecodeAdapter.compareEqual(number, "Double") || ScriptBytecodeAdapter.compareEqual(number, "Float");
    }

    public Object isLong(Object number) {
        CallSite[] callSiteArray = genMathModification.$getCallSiteArray();
        return ScriptBytecodeAdapter.compareEqual(number, "Long");
    }

    public Object getMath(Object left, Object right) {
        CallSite[] callSiteArray = genMathModification.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[4].callCurrent((GroovyObject)this, left)) || DefaultTypeTransformation.booleanUnbox(callSiteArray[5].callCurrent((GroovyObject)this, right))) {
            return ScriptBytecodeAdapter.createMap(new Object[]{"resType", "double", "plus", "+", "minus", "-", "multiply", "*", "div", "/"});
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[6].callCurrent((GroovyObject)this, left)) || DefaultTypeTransformation.booleanUnbox(callSiteArray[7].callCurrent((GroovyObject)this, right))) {
            return ScriptBytecodeAdapter.createMap(new Object[]{"resType", "long", "plus", "+", "minus", "-", "multiply", "*", "div", "/", "or", "|", "and", "&", "xor", "^", "intdiv", "/", "mod", "%", "leftShift", "<<", "rightShift", ">>", "rightShiftUnsigned", ">>>"});
        }
        return ScriptBytecodeAdapter.createMap(new Object[]{"resType", "int", "plus", "+", "minus", "-", "multiply", "*", "div", "/", "or", "|", "and", "&", "xor", "^", "intdiv", "/", "mod", "%", "leftShift", "<<", "rightShift", ">>", "rightShiftUnsigned", ">>>"});
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != genMathModification.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "runScript";
        stringArray[1] = "each";
        stringArray[2] = "each";
        stringArray[3] = "each";
        stringArray[4] = "isFloatingPoint";
        stringArray[5] = "isFloatingPoint";
        stringArray[6] = "isLong";
        stringArray[7] = "isLong";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[8];
        genMathModification.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(genMathModification.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = genMathModification.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

