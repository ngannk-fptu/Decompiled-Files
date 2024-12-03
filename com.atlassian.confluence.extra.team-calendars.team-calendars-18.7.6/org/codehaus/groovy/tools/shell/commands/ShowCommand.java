/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.commands;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.MethodClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.tools.shell.ComplexCommandSupport;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.util.Preferences;

public class ShowCommand
extends ComplexCommandSupport {
    public static final String COMMAND_NAME = ":show";
    private Object do_variables;
    private Object do_classes;
    private Object do_imports;
    private Object do_preferences;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ShowCommand(Groovysh shell) {
        CallSite[] callSiteArray = ShowCommand.$getCallSiteArray();
        super(shell, COMMAND_NAME, ":S", ScriptBytecodeAdapter.createList(new Object[]{"variables", "classes", "imports", "preferences", "all"}));
        _closure1 _closure110 = new _closure1(this, this);
        this.do_variables = _closure110;
        _closure2 _closure210 = new _closure2(this, this);
        this.do_classes = _closure210;
        _closure3 _closure310 = new _closure3(this, this);
        this.do_imports = _closure310;
        _closure4 _closure44 = new _closure4(this, this);
        this.do_preferences = _closure44;
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ShowCommand.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public Object getDo_variables() {
        return this.do_variables;
    }

    public void setDo_variables(Object object) {
        this.do_variables = object;
    }

    public Object getDo_classes() {
        return this.do_classes;
    }

    public void setDo_classes(Object object) {
        this.do_classes = object;
    }

    public Object getDo_imports() {
        return this.do_imports;
    }

    public void setDo_imports(Object object) {
        this.do_imports = object;
    }

    public Object getDo_preferences() {
        return this.do_preferences;
    }

    public void setDo_preferences(Object object) {
        this.do_preferences = object;
    }

    public /* synthetic */ MetaClass super$3$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[]{};
        return new CallSiteArray(ShowCommand.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ShowCommand.$createCallSiteArray();
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

        public Object doCall(Object it) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this)))) {
                return callSiteArray[2].call(callSiteArray[3].callGetProperty(callSiteArray[4].callGroovyObjectGetProperty(this)), "No variables defined");
            }
            callSiteArray[5].call(callSiteArray[6].callGetProperty(callSiteArray[7].callGroovyObjectGetProperty(this)), "Variables:");
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

                public Object doCall(Object key, Object value) {
                    CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                    if (value instanceof MethodClosure) {
                        GStringImpl gStringImpl = new GStringImpl(new Object[]{callSiteArray[0].callGetProperty(value)}, new String[]{"method ", "()"});
                        value = gStringImpl;
                    }
                    return callSiteArray[1].call(callSiteArray[2].callGetProperty(callSiteArray[3].callGroovyObjectGetProperty(this)), new GStringImpl(new Object[]{key, callSiteArray[4].call(InvokerHelper.class, value)}, new String[]{"  ", " = ", ""}));
                }

                public Object call(Object key, Object value) {
                    CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                    return callSiteArray[5].callCurrent(this, key, value);
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
                    stringArray[0] = "method";
                    stringArray[1] = "println";
                    stringArray[2] = "out";
                    stringArray[3] = "io";
                    stringArray[4] = "toString";
                    stringArray[5] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[6];
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
            return callSiteArray[8].call(callSiteArray[9].callGroovyObjectGetProperty(this), new _closure5(this, this.getThisObject()));
        }

        public Object doCall() {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            return this.doCall(null);
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
            stringArray[0] = "isEmpty";
            stringArray[1] = "variables";
            stringArray[2] = "println";
            stringArray[3] = "out";
            stringArray[4] = "io";
            stringArray[5] = "println";
            stringArray[6] = "out";
            stringArray[7] = "io";
            stringArray[8] = "each";
            stringArray[9] = "variables";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[10];
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

        public Object doCall(Object it) {
            CallSite[] callSiteArray = _closure2.$getCallSiteArray();
            Class[] classes = (Class[])ScriptBytecodeAdapter.castToType(callSiteArray[0].callGetProperty(callSiteArray[1].callGroovyObjectGetProperty(this)), Class[].class);
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[2].call(classes), 0)) {
                return callSiteArray[3].call(callSiteArray[4].callGetProperty(callSiteArray[5].callGroovyObjectGetProperty(this)), "No classes have been loaded");
            }
            callSiteArray[6].call(callSiteArray[7].callGetProperty(callSiteArray[8].callGroovyObjectGetProperty(this)), "Classes:");
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

                public Object doCall(Class classIt) {
                    CallSite[] callSiteArray = _closure6.$getCallSiteArray();
                    return callSiteArray[0].call(callSiteArray[1].callGetProperty(callSiteArray[2].callGroovyObjectGetProperty(this)), new GStringImpl(new Object[]{classIt}, new String[]{"  ", ""}));
                }

                public Object call(Class classIt) {
                    CallSite[] callSiteArray = _closure6.$getCallSiteArray();
                    if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                        return callSiteArray[3].callCurrent((GroovyObject)this, classIt);
                    }
                    return this.doCall(classIt);
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
                    stringArray[1] = "out";
                    stringArray[2] = "io";
                    stringArray[3] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[4];
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
            return callSiteArray[9].call((Object)classes, new _closure6(this, this.getThisObject()));
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

        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
            stringArray[0] = "loadedClasses";
            stringArray[1] = "classLoader";
            stringArray[2] = "size";
            stringArray[3] = "println";
            stringArray[4] = "out";
            stringArray[5] = "io";
            stringArray[6] = "println";
            stringArray[7] = "out";
            stringArray[8] = "io";
            stringArray[9] = "each";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[10];
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

        public Object doCall(Object it) {
            CallSite[] callSiteArray = _closure3.$getCallSiteArray();
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this)))) {
                return callSiteArray[2].call(callSiteArray[3].callGetProperty(callSiteArray[4].callGroovyObjectGetProperty(this)), "No custom imports have been defined");
            }
            callSiteArray[5].call(callSiteArray[6].callGetProperty(callSiteArray[7].callGroovyObjectGetProperty(this)), "Custom imports:");
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

                public Object doCall(String importIt) {
                    CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                    return callSiteArray[0].call(callSiteArray[1].callGetProperty(callSiteArray[2].callGroovyObjectGetProperty(this)), new GStringImpl(new Object[]{importIt}, new String[]{"  ", ""}));
                }

                public Object call(String importIt) {
                    CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                    if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                        return callSiteArray[3].callCurrent((GroovyObject)this, importIt);
                    }
                    return this.doCall(importIt);
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
                    stringArray[0] = "println";
                    stringArray[1] = "out";
                    stringArray[2] = "io";
                    stringArray[3] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[4];
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
            return callSiteArray[8].call(callSiteArray[9].callGroovyObjectGetProperty(this), new _closure7(this, this.getThisObject()));
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
            stringArray[0] = "isEmpty";
            stringArray[1] = "imports";
            stringArray[2] = "println";
            stringArray[3] = "out";
            stringArray[4] = "io";
            stringArray[5] = "println";
            stringArray[6] = "out";
            stringArray[7] = "io";
            stringArray[8] = "each";
            stringArray[9] = "imports";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[10];
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

        public Object doCall(Object it) {
            CallSite[] callSiteArray = _closure4.$getCallSiteArray();
            String[] keys = (String[])ScriptBytecodeAdapter.castToType(callSiteArray[0].call(Preferences.class), String[].class);
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[1].call(keys), 0)) {
                callSiteArray[2].call(callSiteArray[3].callGetProperty(callSiteArray[4].callGroovyObjectGetProperty(this)), "No preferences are set");
                return null;
            }
            callSiteArray[5].call(callSiteArray[6].callGetProperty(callSiteArray[7].callGroovyObjectGetProperty(this)), "Preferences:");
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

                public Object doCall(String key) {
                    CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                    Object value = callSiteArray[0].call(Preferences.class, key, null);
                    return callSiteArray[1].call(callSiteArray[2].callGetProperty(callSiteArray[3].callGroovyObjectGetProperty(this)), new GStringImpl(new Object[]{key, value}, new String[]{"    ", "=", ""}));
                }

                public Object call(String key) {
                    CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                    if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                        return callSiteArray[4].callCurrent((GroovyObject)this, key);
                    }
                    return this.doCall(key);
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
                    stringArray[0] = "get";
                    stringArray[1] = "println";
                    stringArray[2] = "out";
                    stringArray[3] = "io";
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
            callSiteArray[8].call((Object)keys, new _closure8(this, this.getThisObject()));
            return null;
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

        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
            stringArray[0] = "keys";
            stringArray[1] = "size";
            stringArray[2] = "println";
            stringArray[3] = "out";
            stringArray[4] = "io";
            stringArray[5] = "println";
            stringArray[6] = "out";
            stringArray[7] = "io";
            stringArray[8] = "each";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[9];
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

