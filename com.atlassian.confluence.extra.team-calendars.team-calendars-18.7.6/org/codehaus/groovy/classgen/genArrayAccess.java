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
import java.util.Map;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class genArrayAccess
extends Script {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public genArrayAccess() {
        CallSite[] callSiteArray = genArrayAccess.$getCallSiteArray();
    }

    public genArrayAccess(Binding context) {
        CallSite[] callSiteArray = genArrayAccess.$getCallSiteArray();
        super(context);
    }

    public static void main(String ... args) {
        CallSite[] callSiteArray = genArrayAccess.$getCallSiteArray();
        callSiteArray[0].call(InvokerHelper.class, genArrayAccess.class, args);
    }

    @Override
    public Object run() {
        CallSite[] callSiteArray = genArrayAccess.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return callSiteArray[1].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{callSiteArray[2].callCurrent(this)}, new String[]{"\npackage org.codehaus.groovy.runtime.dgmimpl;\n\nimport groovy.lang.MetaClassImpl;\nimport groovy.lang.MetaMethod;\nimport org.codehaus.groovy.runtime.callsite.CallSite;\nimport org.codehaus.groovy.runtime.callsite.PojoMetaMethodSite;\nimport org.codehaus.groovy.reflection.CachedClass;\nimport org.codehaus.groovy.reflection.ReflectionCache;\n\npublic class ArrayOperations {\n  ", "\n}\n"}));
        }
        return callSiteArray[3].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{this.genInners()}, new String[]{"\npackage org.codehaus.groovy.runtime.dgmimpl;\n\nimport groovy.lang.MetaClassImpl;\nimport groovy.lang.MetaMethod;\nimport org.codehaus.groovy.runtime.callsite.CallSite;\nimport org.codehaus.groovy.runtime.callsite.PojoMetaMethodSite;\nimport org.codehaus.groovy.reflection.CachedClass;\nimport org.codehaus.groovy.reflection.ReflectionCache;\n\npublic class ArrayOperations {\n  ", "\n}\n"}));
    }

    public Object genInners() {
        CallSite[] callSiteArray = genArrayAccess.$getCallSiteArray();
        Reference<String> res = new Reference<String>("");
        Map primitives = ScriptBytecodeAdapter.createMap(new Object[]{"boolean", "Boolean", "byte", "Byte", "char", "Character", "short", "Short", "int", "Integer", "long", "Long", "float", "Float", "double", "Double"});
        public class _genInners_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference res;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _genInners_closure1(Object _outerInstance, Object _thisObject, Reference res) {
                Reference reference;
                CallSite[] callSiteArray = _genInners_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.res = reference = res;
            }

            public Object doCall(Object primName, Object clsName) {
                CallSite[] callSiteArray = _genInners_closure1.$getCallSiteArray();
                Object object = callSiteArray[0].call(this.res.get(), new GStringImpl(new Object[]{clsName, primName, clsName, primName, primName, primName, primName, primName, primName, primName, primName, primName, clsName, primName, clsName, primName, primName, clsName, primName, clsName, primName, clsName, primName, clsName, primName, primName, clsName, primName}, new String[]{"\n         public static class ", "ArrayGetAtMetaMethod extends ArrayGetAtMetaMethod {\n            private static final CachedClass ARR_CLASS = ReflectionCache.getCachedClass(", "[].class);\n\n            public Class getReturnType() {\n                return ", ".class;\n            }\n\n            public final CachedClass getDeclaringClass() {\n                return ARR_CLASS;\n            }\n\n            public Object invoke(Object object, Object[] args) {\n                final ", "[] objects = (", "[]) object;\n                return objects[normaliseIndex(((Integer) args[0]).intValue(), objects.length)];\n            }\n\n            public CallSite createPojoCallSite(CallSite site, MetaClassImpl metaClass, MetaMethod metaMethod, Class[] params, Object receiver, Object[] args) {\n                if (!(args [0] instanceof Integer))\n                  return PojoMetaMethodSite.createNonAwareCallSite(site, metaClass, metaMethod, params, args);\n                else\n                    return new PojoMetaMethodSite(site, metaClass, metaMethod, params) {\n                        public Object invoke(Object receiver, Object[] args) {\n                            final ", "[] objects = (", "[]) receiver;\n                            return objects[normaliseIndex(((Integer) args[0]).intValue(), objects.length)];\n                        }\n\n                        public Object callBinop(Object receiver, Object arg) {\n                            if ((receiver instanceof ", "[] && arg instanceof Integer)\n                                    && checkMetaClass()) {\n                                final ", "[] objects = (", "[]) receiver;\n                                return objects[normaliseIndex(((Integer) arg).intValue(), objects.length)];\n                            }\n                            else\n                              return super.callBinop(receiver,arg);\n                        }\n\n                        public Object invokeBinop(Object receiver, Object arg) {\n                            final ", "[] objects = (", "[]) receiver;\n                            return objects[normaliseIndex(((Integer) arg).intValue(), objects.length)];\n                        }\n                    };\n            }\n         }\n\n\n        public static class ", "ArrayPutAtMetaMethod extends ArrayPutAtMetaMethod {\n            private static final CachedClass OBJECT_CLASS = ReflectionCache.OBJECT_CLASS;\n            private static final CachedClass ARR_CLASS = ReflectionCache.getCachedClass(", "[].class);\n            private static final CachedClass [] PARAM_CLASS_ARR = new CachedClass[] {INTEGER_CLASS, OBJECT_CLASS};\n\n            public ", "ArrayPutAtMetaMethod() {\n                parameterTypes = PARAM_CLASS_ARR;\n            }\n\n            public final CachedClass getDeclaringClass() {\n                return ARR_CLASS;\n            }\n\n            public Object invoke(Object object, Object[] args) {\n                final ", "[] objects = (", "[]) object;\n                final int index = normaliseIndex(((Integer) args[0]).intValue(), objects.length);\n                Object newValue = args[1];\n                if (!(newValue instanceof ", ")) {\n                    Number n = (Number) newValue;\n                    objects[index] = ((Number)newValue).", "Value();\n                }\n                else\n                  objects[index] = ((", ")args[1]).", "Value();\n                return null;\n            }\n\n            public CallSite createPojoCallSite(CallSite site, MetaClassImpl metaClass, MetaMethod metaMethod, Class[] params, Object receiver, Object[] args) {\n                if (!(args [0] instanceof Integer) || !(args [1] instanceof ", "))\n                  return PojoMetaMethodSite.createNonAwareCallSite(site, metaClass, metaMethod, params, args);\n                else\n                    return new PojoMetaMethodSite(site, metaClass, metaMethod, params) {\n                        public Object call(Object receiver, Object[] args) {\n                            if ((receiver instanceof ", "[] && args[0] instanceof Integer && args[1] instanceof ", " )\n                                    && checkMetaClass()) {\n                                final ", "[] objects = (", "[]) receiver;\n                                objects[normaliseIndex(((Integer) args[0]).intValue(), objects.length)] = ((", ")args[1]).", "Value();\n                                return null;\n                            }\n                            else\n                              return super.call(receiver,args);\n                        }\n                    };\n            }\n        }\n\n       "}));
                this.res.set(object);
                return object;
            }

            public Object call(Object primName, Object clsName) {
                CallSite[] callSiteArray = _genInners_closure1.$getCallSiteArray();
                return callSiteArray[1].callCurrent(this, primName, clsName);
            }

            public Object getRes() {
                CallSite[] callSiteArray = _genInners_closure1.$getCallSiteArray();
                return this.res.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _genInners_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "plus";
                stringArray[1] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _genInners_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_genInners_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _genInners_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[4].call((Object)primitives, new _genInners_closure1(this, this, res));
        return res.get();
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != genArrayAccess.class) {
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
        stringArray[2] = "genInners";
        stringArray[3] = "println";
        stringArray[4] = "each";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[5];
        genArrayAccess.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(genArrayAccess.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = genArrayAccess.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

