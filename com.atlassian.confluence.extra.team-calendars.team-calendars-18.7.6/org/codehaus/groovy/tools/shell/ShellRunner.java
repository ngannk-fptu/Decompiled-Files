/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.tools.shell.ExitNotification;
import org.codehaus.groovy.tools.shell.Shell;
import org.codehaus.groovy.tools.shell.util.Logger;

public abstract class ShellRunner
implements Runnable,
GroovyObject {
    protected final Logger log;
    private final Shell shell;
    private boolean running;
    private boolean breakOnNull;
    private Closure errorHandler;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    protected ShellRunner(Shell shell) {
        MetaClass metaClass;
        boolean bl;
        boolean bl2;
        CallSite[] callSiteArray = ShellRunner.$getCallSiteArray();
        Object object = callSiteArray[0].call(Logger.class, callSiteArray[1].callGroovyObjectGetProperty(this));
        this.log = (Logger)ScriptBytecodeAdapter.castToType(object, Logger.class);
        this.running = bl2 = false;
        this.breakOnNull = bl = true;
        _closure1 _closure110 = new _closure1(this, this);
        this.errorHandler = _closure110;
        this.metaClass = metaClass = this.$getStaticMetaClass();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            Shell shell2 = shell;
            valueRecorder.record(shell2, 8);
            boolean bl3 = ScriptBytecodeAdapter.compareNotEqual(shell2, null);
            valueRecorder.record(bl3, 14);
            if (bl3) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert(shell != null)", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        Shell shell3 = shell;
        this.shell = (Shell)ScriptBytecodeAdapter.castToType(shell3, Shell.class);
    }

    @Override
    public void run() {
        boolean bl;
        CallSite[] callSiteArray = ShellRunner.$getCallSiteArray();
        callSiteArray[2].call((Object)this.log, "Running");
        this.running = bl = true;
        while (this.running) {
            try {
                Object object = callSiteArray[3].callCurrent(this);
                this.running = DefaultTypeTransformation.booleanUnbox(object);
            }
            catch (ExitNotification n) {
                throw (Throwable)n;
            }
            catch (Throwable t) {
                callSiteArray[4].call(this.log, new GStringImpl(new Object[]{t}, new String[]{"Work failed: ", ""}), t);
                if (!DefaultTypeTransformation.booleanUnbox(this.errorHandler)) continue;
                try {
                    callSiteArray[5].call((Object)this.errorHandler, t);
                }
                catch (Throwable t2) {
                    ScriptBytecodeAdapter.invokeClosure(this.errorHandler, new Object[]{callSiteArray[6].callConstructor(IllegalArgumentException.class, new GStringImpl(new Object[]{callSiteArray[7].callGetProperty(t)}, new String[]{"Error when handling error: ", ""}))});
                    callSiteArray[8].call((Object)this.errorHandler, t2);
                }
            }
        }
        callSiteArray[9].call((Object)this.log, "Finished");
    }

    protected boolean work() {
        CallSite[] callSiteArray = ShellRunner.$getCallSiteArray();
        Object line = null;
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object;
            line = object = callSiteArray[10].callCurrent(this);
        } else {
            String string = this.readLine();
            line = string;
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[11].callGetProperty(this.log))) {
            callSiteArray[12].call((Object)this.log, new GStringImpl(new Object[]{line}, new String[]{"Read line: ", ""}));
        }
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass() ? ScriptBytecodeAdapter.compareEqual(line, null) && this.breakOnNull : ScriptBytecodeAdapter.compareEqual(line, null) && this.breakOnNull) {
            return false;
        }
        if (ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[13].call(callSiteArray[14].call(line)), 0)) {
            callSiteArray[15].call((Object)this.shell, line);
        }
        return true;
    }

    protected abstract String readLine();

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ShellRunner.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    @Override
    public MetaClass getMetaClass() {
        MetaClass metaClass = this.metaClass;
        if (metaClass != null) {
            return metaClass;
        }
        this.metaClass = this.$getStaticMetaClass();
        return this.metaClass;
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    public Object invokeMethod(String string, Object object) {
        return this.getMetaClass().invokeMethod((Object)this, string, object);
    }

    @Override
    public Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    public void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }

    public final Shell getShell() {
        return this.shell;
    }

    public boolean getRunning() {
        return this.running;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void setRunning(boolean bl) {
        this.running = bl;
    }

    public boolean getBreakOnNull() {
        return this.breakOnNull;
    }

    public boolean isBreakOnNull() {
        return this.breakOnNull;
    }

    public void setBreakOnNull(boolean bl) {
        this.breakOnNull = bl;
    }

    public Closure getErrorHandler() {
        return this.errorHandler;
    }

    public void setErrorHandler(Closure closure) {
        this.errorHandler = closure;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "create";
        stringArray[1] = "class";
        stringArray[2] = "debug";
        stringArray[3] = "work";
        stringArray[4] = "debug";
        stringArray[5] = "call";
        stringArray[6] = "<$constructor$>";
        stringArray[7] = "message";
        stringArray[8] = "call";
        stringArray[9] = "debug";
        stringArray[10] = "readLine";
        stringArray[11] = "debugEnabled";
        stringArray[12] = "debug";
        stringArray[13] = "size";
        stringArray[14] = "trim";
        stringArray[15] = "leftShift";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[16];
        ShellRunner.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ShellRunner.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ShellRunner.$createCallSiteArray();
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

        public Object doCall(Object e) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this), e);
            boolean bl = false;
            ScriptBytecodeAdapter.setGroovyObjectProperty(bl, _closure1.class, this, "running");
            return bl;
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
            stringArray[0] = "debug";
            stringArray[1] = "log";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[2];
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
}

