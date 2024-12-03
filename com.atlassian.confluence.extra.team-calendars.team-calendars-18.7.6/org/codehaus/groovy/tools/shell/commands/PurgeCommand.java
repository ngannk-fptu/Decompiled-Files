/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.commands;

import groovy.lang.Closure;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.tools.shell.ComplexCommandSupport;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.util.Preferences;

public class PurgeCommand
extends ComplexCommandSupport {
    public static final String COMMAND_NAME = ":purge";
    private Object do_variables;
    private Object do_classes;
    private Object do_imports;
    private Object do_preferences;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public PurgeCommand(Groovysh shell) {
        CallSite[] callSiteArray = PurgeCommand.$getCallSiteArray();
        super(shell, COMMAND_NAME, ":p", ScriptBytecodeAdapter.createList(new Object[]{"variables", "classes", "imports", "preferences", "all"}));
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
        if (this.getClass() != PurgeCommand.class) {
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
        return new CallSiteArray(PurgeCommand.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = PurgeCommand.$createCallSiteArray();
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
            callSiteArray[5].call(callSiteArray[6].callGroovyObjectGetProperty(this));
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[7].callGetProperty(callSiteArray[8].callGroovyObjectGetProperty(this)))) {
                return callSiteArray[9].call(callSiteArray[10].callGetProperty(callSiteArray[11].callGroovyObjectGetProperty(this)), "Custom variables purged");
            }
            return null;
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
            stringArray[5] = "clear";
            stringArray[6] = "variables";
            stringArray[7] = "verbose";
            stringArray[8] = "io";
            stringArray[9] = "println";
            stringArray[10] = "out";
            stringArray[11] = "io";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[12];
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
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[0].call(callSiteArray[1].callGetProperty(callSiteArray[2].callGroovyObjectGetProperty(this))), 0)) {
                return callSiteArray[3].call(callSiteArray[4].callGetProperty(callSiteArray[5].callGroovyObjectGetProperty(this)), "No classes have been loaded");
            }
            callSiteArray[6].call(callSiteArray[7].callGroovyObjectGetProperty(this));
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[8].callGetProperty(callSiteArray[9].callGroovyObjectGetProperty(this)))) {
                return callSiteArray[10].call(callSiteArray[11].callGetProperty(callSiteArray[12].callGroovyObjectGetProperty(this)), "Loaded classes purged");
            }
            return null;
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
            stringArray[0] = "size";
            stringArray[1] = "loadedClasses";
            stringArray[2] = "classLoader";
            stringArray[3] = "println";
            stringArray[4] = "out";
            stringArray[5] = "io";
            stringArray[6] = "clearCache";
            stringArray[7] = "classLoader";
            stringArray[8] = "verbose";
            stringArray[9] = "io";
            stringArray[10] = "println";
            stringArray[11] = "out";
            stringArray[12] = "io";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[13];
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
            callSiteArray[5].call(callSiteArray[6].callGroovyObjectGetProperty(this));
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[7].callGetProperty(callSiteArray[8].callGroovyObjectGetProperty(this)))) {
                return callSiteArray[9].call(callSiteArray[10].callGetProperty(callSiteArray[11].callGroovyObjectGetProperty(this)), "Custom imports purged");
            }
            return null;
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
            stringArray[5] = "clear";
            stringArray[6] = "imports";
            stringArray[7] = "verbose";
            stringArray[8] = "io";
            stringArray[9] = "println";
            stringArray[10] = "out";
            stringArray[11] = "io";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[12];
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
            callSiteArray[0].call(Preferences.class);
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[1].callGetProperty(callSiteArray[2].callGroovyObjectGetProperty(this)))) {
                return callSiteArray[3].call(callSiteArray[4].callGetProperty(callSiteArray[5].callGroovyObjectGetProperty(this)), "Preferences purged");
            }
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
            stringArray[0] = "clear";
            stringArray[1] = "verbose";
            stringArray[2] = "io";
            stringArray[3] = "println";
            stringArray[4] = "out";
            stringArray[5] = "io";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[6];
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

