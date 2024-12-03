/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jline.console.completer.Completer
 */
package org.codehaus.groovy.tools.shell.commands;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Set;
import jline.console.completer.Completer;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.tools.shell.CommandSupport;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.util.PackageHelper;
import org.codehaus.groovy.tools.shell.util.Preferences;
import org.codehaus.groovy.tools.shell.util.SimpleCompletor;

public class SetCommand
extends CommandSupport {
    public static final String COMMAND_NAME = ":set";
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public SetCommand(Groovysh shell) {
        CallSite[] callSiteArray = SetCommand.$getCallSiteArray();
        super(shell, COMMAND_NAME, ":=");
    }

    @Override
    protected List<Completer> createCompleters() {
        CallSite[] callSiteArray = SetCommand.$getCallSiteArray();
        public class _createCompleters_closure1
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _createCompleters_closure1(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _createCompleters_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _createCompleters_closure1.$getCallSiteArray();
                Reference<Set> set = new Reference<Set>((Set)ScriptBytecodeAdapter.asType(ScriptBytecodeAdapter.createList(new Object[0]), Set.class));
                String[] keys = (String[])ScriptBytecodeAdapter.castToType(callSiteArray[0].call(Preferences.class), String[].class);
                public class _closure3
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference set;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure3(Object _outerInstance, Object _thisObject, Reference set) {
                        Reference reference;
                        CallSite[] callSiteArray = _closure3.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.set = reference = set;
                    }

                    public Object doCall(String key) {
                        CallSite[] callSiteArray = _closure3.$getCallSiteArray();
                        return callSiteArray[0].call(this.set.get(), key);
                    }

                    public Object call(String key) {
                        CallSite[] callSiteArray = _closure3.$getCallSiteArray();
                        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                            return callSiteArray[1].callCurrent((GroovyObject)this, key);
                        }
                        return this.doCall(key);
                    }

                    public Set getSet() {
                        CallSite[] callSiteArray = _closure3.$getCallSiteArray();
                        return (Set)ScriptBytecodeAdapter.castToType(this.set.get(), Set.class);
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
                        stringArray[0] = "add";
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
                callSiteArray[1].call((Object)keys, new _closure3(this, this.getThisObject(), set));
                callSiteArray[2].call((Object)set.get(), callSiteArray[3].callGetProperty(Preferences.class));
                callSiteArray[4].call((Object)set.get(), callSiteArray[5].callGetProperty(Preferences.class));
                callSiteArray[6].call((Object)set.get(), callSiteArray[7].callGetProperty(Preferences.class));
                callSiteArray[8].call((Object)set.get(), callSiteArray[9].callGetProperty(Preferences.class));
                callSiteArray[10].call((Object)set.get(), callSiteArray[11].callGetProperty(Preferences.class));
                callSiteArray[12].call((Object)set.get(), callSiteArray[13].callGetProperty(Groovysh.class));
                callSiteArray[14].call((Object)set.get(), callSiteArray[15].callGetProperty(Groovysh.class));
                callSiteArray[16].call((Object)set.get(), callSiteArray[17].callGetProperty(Groovysh.class));
                callSiteArray[18].call((Object)set.get(), callSiteArray[19].callGetProperty(Groovysh.class));
                callSiteArray[20].call((Object)set.get(), callSiteArray[21].callGetProperty(PackageHelper.class));
                return callSiteArray[22].call(set.get());
            }

            public Object doCall() {
                CallSite[] callSiteArray = _createCompleters_closure1.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _createCompleters_closure1.class) {
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
                stringArray[1] = "each";
                stringArray[2] = "leftShift";
                stringArray[3] = "VERBOSITY_KEY";
                stringArray[4] = "leftShift";
                stringArray[5] = "EDITOR_KEY";
                stringArray[6] = "leftShift";
                stringArray[7] = "PARSER_FLAVOR_KEY";
                stringArray[8] = "leftShift";
                stringArray[9] = "SANITIZE_STACK_TRACE_KEY";
                stringArray[10] = "leftShift";
                stringArray[11] = "SHOW_LAST_RESULT_KEY";
                stringArray[12] = "leftShift";
                stringArray[13] = "INTERPRETER_MODE_PREFERENCE_KEY";
                stringArray[14] = "leftShift";
                stringArray[15] = "AUTOINDENT_PREFERENCE_KEY";
                stringArray[16] = "leftShift";
                stringArray[17] = "COLORS_PREFERENCE_KEY";
                stringArray[18] = "leftShift";
                stringArray[19] = "METACLASS_COMPLETION_PREFIX_LENGTH_PREFERENCE_KEY";
                stringArray[20] = "leftShift";
                stringArray[21] = "IMPORT_COMPLETION_PREFERENCE_KEY";
                stringArray[22] = "toList";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[23];
                _createCompleters_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_createCompleters_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _createCompleters_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        _createCompleters_closure1 loader = new _createCompleters_closure1(this, this);
        return ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[0].callConstructor(SimpleCompletor.class, loader), null});
    }

    @Override
    public Object execute(List<String> args) {
        CallSite[] callSiteArray = SetCommand.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            List<String> list = args;
            valueRecorder.record(list, 8);
            boolean bl = ScriptBytecodeAdapter.compareNotEqual(list, null);
            valueRecorder.record(bl, 13);
            if (bl) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert args != null", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[1].call(args), 0)) {
            Object keys = callSiteArray[2].call(Preferences.class);
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[3].call(keys), 0)) {
                callSiteArray[4].call(callSiteArray[5].callGetProperty(callSiteArray[6].callGroovyObjectGetProperty(this)), "No preferences are set");
                return null;
            }
            callSiteArray[7].call(callSiteArray[8].callGetProperty(callSiteArray[9].callGroovyObjectGetProperty(this)), "Preferences:");
            public class _execute_closure2
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _execute_closure2(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _execute_closure2.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(String key) {
                    CallSite[] callSiteArray = _execute_closure2.$getCallSiteArray();
                    Object keyvalue = callSiteArray[0].call(Preferences.class, key, null);
                    return callSiteArray[1].call(callSiteArray[2].callGetProperty(callSiteArray[3].callGroovyObjectGetProperty(this)), new GStringImpl(new Object[]{key, keyvalue}, new String[]{"    ", "=", ""}));
                }

                public Object call(String key) {
                    CallSite[] callSiteArray = _execute_closure2.$getCallSiteArray();
                    if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                        return callSiteArray[4].callCurrent((GroovyObject)this, key);
                    }
                    return this.doCall(key);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _execute_closure2.class) {
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
                    _execute_closure2.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_execute_closure2.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _execute_closure2.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[10].call(keys, new _execute_closure2(this, this));
            return null;
        }
        if (ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[11].call(args), 2)) {
            callSiteArray[12].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{callSiteArray[13].callGroovyObjectGetProperty(this)}, new String[]{"Command '", "' requires arguments: <name> [<value>]"}));
        }
        String name = ShortTypeHandling.castToString(callSiteArray[14].call(args, 0));
        Object value = null;
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[15].call(args), 1)) {
            boolean bl = true;
            value = bl;
        } else {
            Object object;
            value = object = callSiteArray[16].call(args, 1);
        }
        callSiteArray[17].call(callSiteArray[18].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{name, value}, new String[]{"Setting preference: ", "=", ""}));
        return callSiteArray[19].call(Preferences.class, name, callSiteArray[20].call(String.class, value));
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != SetCommand.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ List super$2$createCompleters() {
        return super.createCompleters();
    }

    public /* synthetic */ MetaClass super$2$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "size";
        stringArray[2] = "keys";
        stringArray[3] = "size";
        stringArray[4] = "println";
        stringArray[5] = "out";
        stringArray[6] = "io";
        stringArray[7] = "println";
        stringArray[8] = "out";
        stringArray[9] = "io";
        stringArray[10] = "each";
        stringArray[11] = "size";
        stringArray[12] = "fail";
        stringArray[13] = "name";
        stringArray[14] = "getAt";
        stringArray[15] = "size";
        stringArray[16] = "getAt";
        stringArray[17] = "debug";
        stringArray[18] = "log";
        stringArray[19] = "put";
        stringArray[20] = "valueOf";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[21];
        SetCommand.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(SetCommand.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = SetCommand.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

