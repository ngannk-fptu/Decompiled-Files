/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jline.console.history.History$Entry
 */
package org.codehaus.groovy.tools.shell.commands;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.List;
import jline.console.history.History;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.tools.shell.ComplexCommandSupport;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.util.SimpleCompletor;

public class HistoryCommand
extends ComplexCommandSupport {
    public static final String COMMAND_NAME = ":history";
    private Object do_show;
    private Object do_clear;
    private Object do_flush;
    private Object do_recall;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public HistoryCommand(Groovysh shell) {
        CallSite[] callSiteArray = HistoryCommand.$getCallSiteArray();
        super(shell, COMMAND_NAME, ":H", ScriptBytecodeAdapter.createList(new Object[]{"show", "clear", "flush", "recall"}), "show");
        _closure1 _closure110 = new _closure1(this, this);
        this.do_show = _closure110;
        _closure2 _closure210 = new _closure2(this, this);
        this.do_clear = _closure210;
        _closure3 _closure310 = new _closure3(this, this);
        this.do_flush = _closure310;
        _closure4 _closure44 = new _closure4(this, this);
        this.do_recall = _closure44;
    }

    protected List createCompleters() {
        CallSite[] callSiteArray = HistoryCommand.$getCallSiteArray();
        public class _createCompleters_closure5
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _createCompleters_closure5(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _createCompleters_closure5.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _createCompleters_closure5.$getCallSiteArray();
                List list = ScriptBytecodeAdapter.createList(new Object[0]);
                callSiteArray[0].call((Object)list, callSiteArray[1].callGroovyObjectGetProperty(this));
                return list;
            }

            public Object doCall() {
                CallSite[] callSiteArray = _createCompleters_closure5.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _createCompleters_closure5.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "addAll";
                stringArray[1] = "functions";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _createCompleters_closure5.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_createCompleters_closure5.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _createCompleters_closure5.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        _createCompleters_closure5 loader = new _createCompleters_closure5(this, this);
        return ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[0].callConstructor(SimpleCompletor.class, loader), null});
    }

    @Override
    public Object execute(List<String> args) {
        CallSite[] callSiteArray = HistoryCommand.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[1].callGroovyObjectGetProperty(this))) {
            callSiteArray[2].callCurrent((GroovyObject)this, "Shell does not appear to be interactive; Can not query history");
        }
        ScriptBytecodeAdapter.invokeMethodOnSuperN(ComplexCommandSupport.class, this, "execute", new Object[]{args});
        return null;
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != HistoryCommand.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public Object getDo_show() {
        return this.do_show;
    }

    public void setDo_show(Object object) {
        this.do_show = object;
    }

    public Object getDo_clear() {
        return this.do_clear;
    }

    public void setDo_clear(Object object) {
        this.do_clear = object;
    }

    public Object getDo_flush() {
        return this.do_flush;
    }

    public void setDo_flush(Object object) {
        this.do_flush = object;
    }

    public Object getDo_recall() {
        return this.do_recall;
    }

    public void setDo_recall(Object object) {
        this.do_recall = object;
    }

    public /* synthetic */ Object super$3$execute(List list) {
        return super.execute(list);
    }

    public /* synthetic */ List super$3$createCompleters() {
        return super.createCompleters();
    }

    public /* synthetic */ MetaClass super$3$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "history";
        stringArray[2] = "fail";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[3];
        HistoryCommand.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(HistoryCommand.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = HistoryCommand.$createCallSiteArray();
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
            Iterator histIt = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this)), Iterator.class);
            while (DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call(histIt))) {
                History.Entry next = (History.Entry)ScriptBytecodeAdapter.castToType(callSiteArray[3].call(histIt), History.Entry.class);
                if (!DefaultTypeTransformation.booleanUnbox(next)) continue;
                callSiteArray[4].call(callSiteArray[5].callGetProperty(callSiteArray[6].callGroovyObjectGetProperty(this)), new GStringImpl(new Object[]{callSiteArray[7].call(callSiteArray[8].call(callSiteArray[9].call(next)), 3, " "), callSiteArray[10].call(next)}, new String[]{" @|bold ", "|@  ", ""}));
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
            stringArray[0] = "iterator";
            stringArray[1] = "history";
            stringArray[2] = "hasNext";
            stringArray[3] = "next";
            stringArray[4] = "println";
            stringArray[5] = "out";
            stringArray[6] = "io";
            stringArray[7] = "padLeft";
            stringArray[8] = "toString";
            stringArray[9] = "index";
            stringArray[10] = "value";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[11];
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
            callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this));
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[2].callGetProperty(callSiteArray[3].callGroovyObjectGetProperty(this)))) {
                return callSiteArray[4].call(callSiteArray[5].callGetProperty(callSiteArray[6].callGroovyObjectGetProperty(this)), "History cleared");
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
            stringArray[0] = "clear";
            stringArray[1] = "history";
            stringArray[2] = "verbose";
            stringArray[3] = "io";
            stringArray[4] = "println";
            stringArray[5] = "out";
            stringArray[6] = "io";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[7];
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
            callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this));
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[2].callGetProperty(callSiteArray[3].callGroovyObjectGetProperty(this)))) {
                return callSiteArray[4].call(callSiteArray[5].callGetProperty(callSiteArray[6].callGroovyObjectGetProperty(this)), "History flushed");
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
            stringArray[0] = "flush";
            stringArray[1] = "history";
            stringArray[2] = "verbose";
            stringArray[3] = "io";
            stringArray[4] = "println";
            stringArray[5] = "out";
            stringArray[6] = "io";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[7];
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

        public Object doCall(Object args) {
            CallSite[] callSiteArray = _closure4.$getCallSiteArray();
            String line = null;
            if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (!DefaultTypeTransformation.booleanUnbox(args) || ScriptBytecodeAdapter.compareNotEqual(callSiteArray[0].call((List)ScriptBytecodeAdapter.castToType(args, List.class)), 1)) {
                    callSiteArray[1].callCurrent((GroovyObject)this, "History recall requires a single history identifer");
                }
            } else if (!DefaultTypeTransformation.booleanUnbox(args) || ScriptBytecodeAdapter.compareNotEqual(callSiteArray[2].call((List)ScriptBytecodeAdapter.castToType(args, List.class)), 1)) {
                callSiteArray[3].callCurrent((GroovyObject)this, "History recall requires a single history identifer");
            }
            String ids = ShortTypeHandling.castToString(callSiteArray[4].call((Object)((List)ScriptBytecodeAdapter.castToType(args, List.class)), 0));
            try {
                int id = DefaultTypeTransformation.intUnbox(callSiteArray[5].call(Integer.class, ids));
                if (!BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[6].callGroovyObjectGetProperty(callSiteArray[7].callGroovyObjectGetProperty(this)))) {
                        int n = id;
                        id = DefaultTypeTransformation.intUnbox(callSiteArray[8].call(n));
                    }
                } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[9].callGroovyObjectGetProperty(callSiteArray[10].callGroovyObjectGetProperty(this)))) {
                    int n = id;
                    id = n - 1;
                }
                Iterator listEntryIt = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[11].call(callSiteArray[12].callGroovyObjectGetProperty(this)), Iterator.class);
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[13].call(listEntryIt))) {
                    History.Entry next = (History.Entry)ScriptBytecodeAdapter.castToType(callSiteArray[14].call(listEntryIt), History.Entry.class);
                    if (ScriptBytecodeAdapter.compareLessThan(id, callSiteArray[15].call(callSiteArray[16].call(next), 1))) {
                        callSiteArray[17].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{ids}, new String[]{"Unknown index: ", ""}));
                    } else if (ScriptBytecodeAdapter.compareEqual(id, callSiteArray[18].call(callSiteArray[19].call(next), 1))) {
                        Object object = callSiteArray[20].callGroovyObjectGetProperty(callSiteArray[21].callGroovyObjectGetProperty(this));
                        line = ShortTypeHandling.castToString(object);
                    } else if (ScriptBytecodeAdapter.compareEqual(callSiteArray[22].call(next), id)) {
                        Object object = callSiteArray[23].call(next);
                        line = ShortTypeHandling.castToString(object);
                    } else {
                        while (DefaultTypeTransformation.booleanUnbox(callSiteArray[24].call(listEntryIt))) {
                            Object object = callSiteArray[25].call(listEntryIt);
                            next = (History.Entry)ScriptBytecodeAdapter.castToType(object, History.Entry.class);
                            if (!ScriptBytecodeAdapter.compareEqual(callSiteArray[26].call(next), id)) continue;
                            Object object2 = callSiteArray[27].call(next);
                            line = ShortTypeHandling.castToString(object2);
                        }
                    }
                }
            }
            catch (NumberFormatException e) {
                callSiteArray[28].callCurrent(this, new GStringImpl(new Object[]{ids}, new String[]{"Invalid history identifier: ", ""}), e);
            }
            callSiteArray[29].call(callSiteArray[30].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{ids, line}, new String[]{"Recalling history item #", ": ", ""}));
            if (DefaultTypeTransformation.booleanUnbox(line)) {
                return callSiteArray[31].call(callSiteArray[32].callGroovyObjectGetProperty(this), line);
            }
            return null;
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
            stringArray[0] = "size";
            stringArray[1] = "fail";
            stringArray[2] = "size";
            stringArray[3] = "fail";
            stringArray[4] = "getAt";
            stringArray[5] = "parseInt";
            stringArray[6] = "historyFull";
            stringArray[7] = "shell";
            stringArray[8] = "previous";
            stringArray[9] = "historyFull";
            stringArray[10] = "shell";
            stringArray[11] = "iterator";
            stringArray[12] = "history";
            stringArray[13] = "hasNext";
            stringArray[14] = "next";
            stringArray[15] = "minus";
            stringArray[16] = "index";
            stringArray[17] = "fail";
            stringArray[18] = "minus";
            stringArray[19] = "index";
            stringArray[20] = "evictedLine";
            stringArray[21] = "shell";
            stringArray[22] = "index";
            stringArray[23] = "value";
            stringArray[24] = "hasNext";
            stringArray[25] = "next";
            stringArray[26] = "index";
            stringArray[27] = "value";
            stringArray[28] = "fail";
            stringArray[29] = "debug";
            stringArray[30] = "log";
            stringArray[31] = "execute";
            stringArray[32] = "shell";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[33];
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

