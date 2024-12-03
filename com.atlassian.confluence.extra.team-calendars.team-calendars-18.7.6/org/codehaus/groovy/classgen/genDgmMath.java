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
import java.util.Iterator;
import java.util.List;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class genDgmMath
extends Script {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public genDgmMath() {
        CallSite[] callSiteArray = genDgmMath.$getCallSiteArray();
    }

    public genDgmMath(Binding context) {
        CallSite[] callSiteArray = genDgmMath.$getCallSiteArray();
        super(context);
    }

    public static void main(String ... args) {
        CallSite[] callSiteArray = genDgmMath.$getCallSiteArray();
        callSiteArray[0].call(InvokerHelper.class, genDgmMath.class, args);
    }

    @Override
    public Object run() {
        CallSite[] callSiteArray = genDgmMath.$getCallSiteArray();
        Reference<List> types = new Reference<List>(ScriptBytecodeAdapter.createList(new Object[]{"Integer", "Long", "Float", "Double"}));
        callSiteArray[1].callCurrent((GroovyObject)this, "\npublic CallSite createPojoCallSite(CallSite site, MetaClassImpl metaClass, MetaMethod metaMethod, Class[] params, Object receiver, Object[] args) {\n    NumberMath m = NumberMath.getMath((Number)receiver, (Number)args[0]);\n");
        public class _run_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference types;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _run_closure1(Object _outerInstance, Object _thisObject, Reference types) {
                Reference reference;
                CallSite[] callSiteArray = _run_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.types = reference = types;
            }

            public Object doCall(Object a) {
                Reference<Object> a2 = new Reference<Object>(a);
                CallSite[] callSiteArray = _run_closure1.$getCallSiteArray();
                callSiteArray[0].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{a2.get()}, new String[]{"\n    if (receiver instanceof ", ") {"}));
                public class _closure2
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference a;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure2(Object _outerInstance, Object _thisObject, Reference a) {
                        Reference reference;
                        CallSite[] callSiteArray = _closure2.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.a = reference = a;
                    }

                    public Object doCall(Object b) {
                        CallSite[] callSiteArray = _closure2.$getCallSiteArray();
                        return callSiteArray[0].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{b, callSiteArray[1].callCurrent(this, this.a.get(), b), this.a.get(), b, callSiteArray[2].callCurrent(this, this.a.get(), b), this.a.get(), b}, new String[]{"\n        if (args[0] instanceof ", ")\n            return new NumberNumberCallSite (site, metaClass, metaMethod, params, (Number)receiver, (Number)args[0]){\n                public final Object invoke(Object receiver, Object[] args) {\n                    return ", ".INSTANCE.addImpl((", ")receiver,(", ")args[0]);\n                }\n\n                public final Object invokeBinop(Object receiver, Object arg) {\n                    return ", ".INSTANCE.addImpl((", ")receiver,(", ")arg);\n                }\n            };\n        "}));
                    }

                    public Object getA() {
                        CallSite[] callSiteArray = _closure2.$getCallSiteArray();
                        return this.a.get();
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
                        stringArray[0] = "print";
                        stringArray[1] = "getMath";
                        stringArray[2] = "getMath";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[3];
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
                callSiteArray[1].call(this.types.get(), new _closure2(this, this.getThisObject(), a2));
                return callSiteArray[2].callCurrent((GroovyObject)this, "}");
            }

            public Object getTypes() {
                CallSite[] callSiteArray = _run_closure1.$getCallSiteArray();
                return this.types.get();
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
                stringArray[0] = "print";
                stringArray[1] = "each";
                stringArray[2] = "println";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[3];
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
        callSiteArray[2].call((Object)types.get(), new _run_closure1(this, this, types));
        callSiteArray[3].callCurrent((GroovyObject)this, "\n    return new NumberNumberCallSite (site, metaClass, metaMethod, params, (Number)receiver, (Number)args[0]){\n        public final Object invoke(Object receiver, Object[] args) {\n            return math.addImpl((Number)receiver,(Number)args[0]);\n        }\n\n        public final Object invokeBinop(Object receiver, Object arg) {\n            return math.addImpl((Number)receiver,(Number)arg);\n        }\n}\n");
        Object i = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[4].call(ScriptBytecodeAdapter.createRange(2, 256, true)), Iterator.class);
        while (iterator.hasNext()) {
            i = iterator.next();
            callSiteArray[5].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{i}, new String[]{"public Object invoke", " (Object receiver, "}));
            Object j = null;
            Iterator iterator2 = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[6].call(ScriptBytecodeAdapter.createRange(1, callSiteArray[7].call(i, 1), true)), Iterator.class);
            while (iterator2.hasNext()) {
                j = iterator2.next();
                callSiteArray[8].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{j}, new String[]{"Object a", ", "}));
            }
            callSiteArray[9].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{i}, new String[]{"Object a", ") {"}));
            callSiteArray[10].callCurrent((GroovyObject)this, "  return invoke (receiver, new Object[] {");
            Object j2 = null;
            Iterator iterator3 = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[11].call(ScriptBytecodeAdapter.createRange(1, callSiteArray[12].call(i, 1), true)), Iterator.class);
            while (iterator3.hasNext()) {
                j2 = iterator3.next();
                callSiteArray[13].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{j2}, new String[]{"a", ", "}));
            }
            callSiteArray[14].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{i}, new String[]{"a", "} );"}));
            callSiteArray[15].callCurrent((GroovyObject)this, "}");
        }
        return null;
    }

    public Object getMath(Object a, Object b) {
        CallSite[] callSiteArray = genDgmMath.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass() ? ScriptBytecodeAdapter.compareEqual(a, "Double") || ScriptBytecodeAdapter.compareEqual(b, "Double") || ScriptBytecodeAdapter.compareEqual(a, "Float") || ScriptBytecodeAdapter.compareEqual(b, "Float") : ScriptBytecodeAdapter.compareEqual(a, "Double") || ScriptBytecodeAdapter.compareEqual(b, "Double") || ScriptBytecodeAdapter.compareEqual(a, "Float") || ScriptBytecodeAdapter.compareEqual(b, "Float")) {
            return "FloatingPointMath";
        }
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass() ? ScriptBytecodeAdapter.compareEqual(a, "Long") || ScriptBytecodeAdapter.compareEqual(b, "Long") : ScriptBytecodeAdapter.compareEqual(a, "Long") || ScriptBytecodeAdapter.compareEqual(b, "Long")) {
            return "LongMath";
        }
        return "IntegerMath";
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != genDgmMath.class) {
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
        stringArray[1] = "println";
        stringArray[2] = "each";
        stringArray[3] = "println";
        stringArray[4] = "iterator";
        stringArray[5] = "print";
        stringArray[6] = "iterator";
        stringArray[7] = "minus";
        stringArray[8] = "print";
        stringArray[9] = "println";
        stringArray[10] = "print";
        stringArray[11] = "iterator";
        stringArray[12] = "minus";
        stringArray[13] = "print";
        stringArray[14] = "println";
        stringArray[15] = "println";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[16];
        genDgmMath.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(genDgmMath.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = genDgmMath.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

