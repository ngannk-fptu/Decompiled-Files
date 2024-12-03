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
import org.codehaus.groovy.tools.shell.ComplexCommandSupport;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.IO;
import org.codehaus.groovy.tools.shell.util.Preferences;

public class ShadowCommand
extends ComplexCommandSupport {
    public static final String COMMAND_NAME = ":shadow";
    private Object do_debug;
    private Object do_verbose;
    private Object do_info;
    private Object do_this;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ShadowCommand(Groovysh shell) {
        CallSite[] callSiteArray = ShadowCommand.$getCallSiteArray();
        super(shell, COMMAND_NAME, ":&", ScriptBytecodeAdapter.createList(new Object[]{"debug", "verbose", "info", "this"}));
        _closure1 _closure110 = new _closure1(this, this);
        this.do_debug = _closure110;
        _closure2 _closure210 = new _closure2(this, this);
        this.do_verbose = _closure210;
        _closure3 _closure310 = new _closure3(this, this);
        this.do_info = _closure310;
        _closure4 _closure44 = new _closure4(this, this);
        this.do_this = _closure44;
        boolean bl = true;
        ScriptBytecodeAdapter.setGroovyObjectProperty(bl, ShadowCommand.class, this, "hidden");
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ShadowCommand.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public Object getDo_debug() {
        return this.do_debug;
    }

    public void setDo_debug(Object object) {
        this.do_debug = object;
    }

    public Object getDo_verbose() {
        return this.do_verbose;
    }

    public void setDo_verbose(Object object) {
        this.do_verbose = object;
    }

    public Object getDo_info() {
        return this.do_info;
    }

    public void setDo_info(Object object) {
        this.do_info = object;
    }

    public Object getDo_this() {
        return this.do_this;
    }

    public void setDo_this(Object object) {
        this.do_this = object;
    }

    public /* synthetic */ MetaClass super$3$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[]{};
        return new CallSiteArray(ShadowCommand.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ShadowCommand.$createCallSiteArray();
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
            Object object = callSiteArray[0].callGetProperty(IO.Verbosity.class);
            ScriptBytecodeAdapter.setProperty(object, null, Preferences.class, "verbosity");
            return object;
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

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[1];
            stringArray[0] = "DEBUG";
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
            Object object = callSiteArray[0].callGetProperty(IO.Verbosity.class);
            ScriptBytecodeAdapter.setProperty(object, null, Preferences.class, "verbosity");
            return object;
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
            stringArray[0] = "VERBOSE";
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
            Object object = callSiteArray[0].callGetProperty(IO.Verbosity.class);
            ScriptBytecodeAdapter.setProperty(object, null, Preferences.class, "verbosity");
            return object;
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

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[1];
            stringArray[0] = "INFO";
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
            return this.getThisObject();
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
            String[] stringArray = new String[]{};
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

