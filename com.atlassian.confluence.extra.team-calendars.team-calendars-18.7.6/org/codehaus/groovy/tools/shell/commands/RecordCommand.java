/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.commands;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.io.File;
import java.io.PrintWriter;
import java.lang.ref.SoftReference;
import java.util.Date;
import java.util.List;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.tools.shell.ComplexCommandSupport;
import org.codehaus.groovy.tools.shell.Groovysh;

public class RecordCommand
extends ComplexCommandSupport {
    public static final String COMMAND_NAME = ":record";
    private File file;
    private PrintWriter writer;
    private Object do_start;
    private Object do_stop;
    private Object do_status;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public RecordCommand(Groovysh shell) {
        CallSite[] callSiteArray = RecordCommand.$getCallSiteArray();
        super(shell, COMMAND_NAME, ":r", ScriptBytecodeAdapter.createList(new Object[]{"start", "stop", "status"}), "status");
        _closure1 _closure110 = new _closure1(this, this);
        this.do_start = _closure110;
        _closure2 _closure210 = new _closure2(this, this);
        this.do_stop = _closure210;
        _closure3 _closure310 = new _closure3(this, this);
        this.do_status = _closure310;
        callSiteArray[0].callCurrent((GroovyObject)this, new _closure4(this, this));
    }

    public boolean isRecording() {
        CallSite[] callSiteArray = RecordCommand.$getCallSiteArray();
        return ScriptBytecodeAdapter.compareNotEqual(this.file, null);
    }

    public Object recordInput(String line) {
        CallSite[] callSiteArray = RecordCommand.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            ValueRecorder valueRecorder = new ValueRecorder();
            try {
                String string = line;
                valueRecorder.record(string, 8);
                boolean bl = ScriptBytecodeAdapter.compareNotEqual(string, null);
                valueRecorder.record(bl, 13);
                if (bl) {
                    valueRecorder.clear();
                } else {
                    ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert line != null", valueRecorder), null);
                }
            }
            catch (Throwable throwable) {
                valueRecorder.clear();
                throw throwable;
            }
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[1].callCurrent(this))) {
                callSiteArray[2].call((Object)this.writer, line);
                return callSiteArray[3].call(this.writer);
            }
            return null;
        }
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            String string = line;
            valueRecorder.record(string, 8);
            boolean bl = ScriptBytecodeAdapter.compareNotEqual(string, null);
            valueRecorder.record(bl, 13);
            if (bl) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert line != null", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        if (this.isRecording()) {
            callSiteArray[4].call((Object)this.writer, line);
            return callSiteArray[5].call(this.writer);
        }
        return null;
    }

    public Object recordResult(Object result) {
        CallSite[] callSiteArray = RecordCommand.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[6].callCurrent(this))) {
                callSiteArray[7].call((Object)this.writer, new GStringImpl(new Object[]{callSiteArray[8].call(InvokerHelper.class, result)}, new String[]{"// RESULT: ", ""}));
                return callSiteArray[9].call(this.writer);
            }
            return null;
        }
        if (this.isRecording()) {
            callSiteArray[10].call((Object)this.writer, new GStringImpl(new Object[]{callSiteArray[11].call(InvokerHelper.class, result)}, new String[]{"// RESULT: ", ""}));
            return callSiteArray[12].call(this.writer);
        }
        return null;
    }

    public Object recordError(Throwable cause) {
        public class _recordError_closure5
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _recordError_closure5(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _recordError_closure5.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _recordError_closure5.$getCallSiteArray();
                return callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{it}, new String[]{"//    ", ""}));
            }

            public Object doCall() {
                CallSite[] callSiteArray = _recordError_closure5.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _recordError_closure5.class) {
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
                stringArray[1] = "writer";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _recordError_closure5.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_recordError_closure5.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _recordError_closure5.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        CallSite[] callSiteArray = RecordCommand.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            ValueRecorder valueRecorder = new ValueRecorder();
            try {
                Throwable throwable = cause;
                valueRecorder.record(throwable, 8);
                boolean bl = ScriptBytecodeAdapter.compareNotEqual(throwable, null);
                valueRecorder.record(bl, 14);
                if (bl) {
                    valueRecorder.clear();
                } else {
                    ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert cause != null", valueRecorder), null);
                }
            }
            catch (Throwable throwable) {
                valueRecorder.clear();
                throw throwable;
            }
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[13].callCurrent(this))) {
                callSiteArray[14].call((Object)this.writer, new GStringImpl(new Object[]{cause}, new String[]{"// ERROR: ", ""}));
                callSiteArray[15].call(callSiteArray[16].callGetProperty(cause), new _recordError_closure5(this, this));
                return callSiteArray[17].call(this.writer);
            }
            return null;
        }
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            Throwable throwable = cause;
            valueRecorder.record(throwable, 8);
            boolean bl = ScriptBytecodeAdapter.compareNotEqual(throwable, null);
            valueRecorder.record(bl, 14);
            if (bl) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert cause != null", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        if (this.isRecording()) {
            callSiteArray[18].call((Object)this.writer, new GStringImpl(new Object[]{cause}, new String[]{"// ERROR: ", ""}));
            callSiteArray[19].call(callSiteArray[20].callGetProperty(cause), new _recordError_closure5(this, this));
            return callSiteArray[21].call(this.writer);
        }
        return null;
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != RecordCommand.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public Object getDo_start() {
        return this.do_start;
    }

    public void setDo_start(Object object) {
        this.do_start = object;
    }

    public Object getDo_stop() {
        return this.do_stop;
    }

    public void setDo_stop(Object object) {
        this.do_stop = object;
    }

    public Object getDo_status() {
        return this.do_status;
    }

    public void setDo_status(Object object) {
        this.do_status = object;
    }

    public /* synthetic */ MetaClass super$3$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "addShutdownHook";
        stringArray[1] = "isRecording";
        stringArray[2] = "println";
        stringArray[3] = "flush";
        stringArray[4] = "println";
        stringArray[5] = "flush";
        stringArray[6] = "isRecording";
        stringArray[7] = "println";
        stringArray[8] = "toString";
        stringArray[9] = "flush";
        stringArray[10] = "println";
        stringArray[11] = "toString";
        stringArray[12] = "flush";
        stringArray[13] = "isRecording";
        stringArray[14] = "println";
        stringArray[15] = "each";
        stringArray[16] = "stackTrace";
        stringArray[17] = "flush";
        stringArray[18] = "println";
        stringArray[19] = "each";
        stringArray[20] = "stackTrace";
        stringArray[21] = "flush";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[22];
        RecordCommand.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(RecordCommand.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = RecordCommand.$createCallSiteArray();
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

        public Object doCall(List<String> args) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].callCurrent(this))) {
                callSiteArray[1].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{callSiteArray[2].callGroovyObjectGetProperty(this)}, new String[]{"Already recording to: \"", "\""}));
            }
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[3].call(args), 0)) {
                Object object = callSiteArray[4].call(File.class, "groovysh-", ".txt");
                ScriptBytecodeAdapter.setGroovyObjectProperty((File)ScriptBytecodeAdapter.castToType(object, File.class), _closure1.class, this, "file");
            } else if (ScriptBytecodeAdapter.compareEqual(callSiteArray[5].call(args), 1)) {
                Object object = callSiteArray[6].callConstructor(File.class, ScriptBytecodeAdapter.createPojoWrapper((String)ScriptBytecodeAdapter.asType(callSiteArray[7].call(args, 0), String.class), String.class));
                ScriptBytecodeAdapter.setGroovyObjectProperty((File)ScriptBytecodeAdapter.castToType(object, File.class), _closure1.class, this, "file");
            } else {
                callSiteArray[8].callCurrent((GroovyObject)this, "Too many arguments. Usage: record start [filename]");
            }
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[9].callGetProperty(callSiteArray[10].callGroovyObjectGetProperty(this)))) {
                callSiteArray[11].call(callSiteArray[12].callGetProperty(callSiteArray[13].callGroovyObjectGetProperty(this)));
            }
            Object object = callSiteArray[14].call(callSiteArray[15].callGroovyObjectGetProperty(this));
            ScriptBytecodeAdapter.setGroovyObjectProperty((PrintWriter)ScriptBytecodeAdapter.castToType(object, PrintWriter.class), _closure1.class, this, "writer");
            callSiteArray[16].call(callSiteArray[17].callGroovyObjectGetProperty(this), callSiteArray[18].call((Object)"// OPENED: ", callSiteArray[19].callConstructor(Date.class)));
            callSiteArray[20].call(callSiteArray[21].callGroovyObjectGetProperty(this));
            callSiteArray[22].call(callSiteArray[23].callGetProperty(callSiteArray[24].callGroovyObjectGetProperty(this)), new GStringImpl(new Object[]{callSiteArray[25].callGroovyObjectGetProperty(this)}, new String[]{"Recording session to: \"", "\""}));
            return callSiteArray[26].callGroovyObjectGetProperty(this);
        }

        public Object call(List<String> args) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            return callSiteArray[27].callCurrent((GroovyObject)this, args);
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
            stringArray[0] = "isRecording";
            stringArray[1] = "fail";
            stringArray[2] = "file";
            stringArray[3] = "size";
            stringArray[4] = "createTempFile";
            stringArray[5] = "size";
            stringArray[6] = "<$constructor$>";
            stringArray[7] = "getAt";
            stringArray[8] = "fail";
            stringArray[9] = "parentFile";
            stringArray[10] = "file";
            stringArray[11] = "mkdirs";
            stringArray[12] = "parentFile";
            stringArray[13] = "file";
            stringArray[14] = "newPrintWriter";
            stringArray[15] = "file";
            stringArray[16] = "println";
            stringArray[17] = "writer";
            stringArray[18] = "plus";
            stringArray[19] = "<$constructor$>";
            stringArray[20] = "flush";
            stringArray[21] = "writer";
            stringArray[22] = "println";
            stringArray[23] = "out";
            stringArray[24] = "io";
            stringArray[25] = "file";
            stringArray[26] = "file";
            stringArray[27] = "doCall";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[28];
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
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[0].callCurrent(this))) {
                callSiteArray[1].callCurrent((GroovyObject)this, "Not recording");
            }
            callSiteArray[2].call(callSiteArray[3].callGroovyObjectGetProperty(this), callSiteArray[4].call((Object)"// CLOSED: ", callSiteArray[5].callConstructor(Date.class)));
            callSiteArray[6].call(callSiteArray[7].callGroovyObjectGetProperty(this));
            callSiteArray[8].call(callSiteArray[9].callGroovyObjectGetProperty(this));
            Object var3_3 = null;
            ScriptBytecodeAdapter.setGroovyObjectProperty((PrintWriter)ScriptBytecodeAdapter.castToType(var3_3, PrintWriter.class), _closure2.class, this, "writer");
            callSiteArray[10].call(callSiteArray[11].callGetProperty(callSiteArray[12].callGroovyObjectGetProperty(this)), new GStringImpl(new Object[]{callSiteArray[13].callGroovyObjectGetProperty(this), callSiteArray[14].call(callSiteArray[15].callGroovyObjectGetProperty(this))}, new String[]{"Recording stopped; session saved as: \"", "\" (", " bytes)"}));
            Object tmp = callSiteArray[16].callGroovyObjectGetProperty(this);
            Object var5_5 = null;
            ScriptBytecodeAdapter.setGroovyObjectProperty((File)ScriptBytecodeAdapter.castToType(var5_5, File.class), _closure2.class, this, "file");
            return tmp;
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
            stringArray[0] = "isRecording";
            stringArray[1] = "fail";
            stringArray[2] = "println";
            stringArray[3] = "writer";
            stringArray[4] = "plus";
            stringArray[5] = "<$constructor$>";
            stringArray[6] = "flush";
            stringArray[7] = "writer";
            stringArray[8] = "close";
            stringArray[9] = "writer";
            stringArray[10] = "println";
            stringArray[11] = "out";
            stringArray[12] = "io";
            stringArray[13] = "file";
            stringArray[14] = "length";
            stringArray[15] = "file";
            stringArray[16] = "file";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[17];
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
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[0].callCurrent(this))) {
                callSiteArray[1].call(callSiteArray[2].callGetProperty(callSiteArray[3].callGroovyObjectGetProperty(this)), "Not recording");
                return null;
            }
            callSiteArray[4].call(callSiteArray[5].callGetProperty(callSiteArray[6].callGroovyObjectGetProperty(this)), new GStringImpl(new Object[]{callSiteArray[7].callGroovyObjectGetProperty(this), callSiteArray[8].call(callSiteArray[9].callGroovyObjectGetProperty(this))}, new String[]{"Recording to file: \"", "\" (", " bytes)"}));
            return callSiteArray[10].callGroovyObjectGetProperty(this);
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
            stringArray[0] = "isRecording";
            stringArray[1] = "println";
            stringArray[2] = "out";
            stringArray[3] = "io";
            stringArray[4] = "println";
            stringArray[5] = "out";
            stringArray[6] = "io";
            stringArray[7] = "file";
            stringArray[8] = "length";
            stringArray[9] = "file";
            stringArray[10] = "file";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[11];
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
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].callCurrent(this))) {
                return callSiteArray[1].callCurrent((GroovyObject)this.getThisObject());
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
            stringArray[0] = "isRecording";
            stringArray[1] = "do_stop";
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
}

