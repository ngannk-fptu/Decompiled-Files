/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui.view;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Script;
import java.lang.ref.SoftReference;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class BasicMenuBar
extends Script {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public BasicMenuBar() {
        CallSite[] callSiteArray = BasicMenuBar.$getCallSiteArray();
    }

    public BasicMenuBar(Binding context) {
        CallSite[] callSiteArray = BasicMenuBar.$getCallSiteArray();
        super(context);
    }

    public static void main(String ... args) {
        CallSite[] callSiteArray = BasicMenuBar.$getCallSiteArray();
        callSiteArray[0].call(InvokerHelper.class, BasicMenuBar.class, args);
    }

    @Override
    public Object run() {
        CallSite[] callSiteArray = BasicMenuBar.$getCallSiteArray();
        public class _run_closure1
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _run_closure1(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _run_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _run_closure1.$getCallSiteArray();
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
                        callSiteArray[0].callCurrent((GroovyObject)this, callSiteArray[1].callGroovyObjectGetProperty(this));
                        callSiteArray[2].callCurrent((GroovyObject)this, callSiteArray[3].callGroovyObjectGetProperty(this));
                        callSiteArray[4].callCurrent((GroovyObject)this, callSiteArray[5].callGroovyObjectGetProperty(this));
                        callSiteArray[6].callCurrent(this);
                        callSiteArray[7].callCurrent((GroovyObject)this, callSiteArray[8].callGroovyObjectGetProperty(this));
                        callSiteArray[9].callCurrent((GroovyObject)this, callSiteArray[10].callGroovyObjectGetProperty(this));
                        callSiteArray[11].callCurrent(this);
                        callSiteArray[12].callCurrent((GroovyObject)this, callSiteArray[13].callGroovyObjectGetProperty(this));
                        callSiteArray[14].callCurrent(this);
                        return callSiteArray[15].callCurrent((GroovyObject)this, callSiteArray[16].callGroovyObjectGetProperty(this));
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
                        stringArray[0] = "menuItem";
                        stringArray[1] = "newFileAction";
                        stringArray[2] = "menuItem";
                        stringArray[3] = "newWindowAction";
                        stringArray[4] = "menuItem";
                        stringArray[5] = "openAction";
                        stringArray[6] = "separator";
                        stringArray[7] = "menuItem";
                        stringArray[8] = "saveAction";
                        stringArray[9] = "menuItem";
                        stringArray[10] = "saveAsAction";
                        stringArray[11] = "separator";
                        stringArray[12] = "menuItem";
                        stringArray[13] = "printAction";
                        stringArray[14] = "separator";
                        stringArray[15] = "menuItem";
                        stringArray[16] = "exitAction";
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
                callSiteArray[0].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"text", "File", "mnemonic", "F"}), new _closure2(this, this.getThisObject()));
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
                        callSiteArray[0].callCurrent((GroovyObject)this, callSiteArray[1].callGroovyObjectGetProperty(this));
                        callSiteArray[2].callCurrent((GroovyObject)this, callSiteArray[3].callGroovyObjectGetProperty(this));
                        callSiteArray[4].callCurrent(this);
                        callSiteArray[5].callCurrent((GroovyObject)this, callSiteArray[6].callGroovyObjectGetProperty(this));
                        callSiteArray[7].callCurrent((GroovyObject)this, callSiteArray[8].callGroovyObjectGetProperty(this));
                        callSiteArray[9].callCurrent((GroovyObject)this, callSiteArray[10].callGroovyObjectGetProperty(this));
                        callSiteArray[11].callCurrent(this);
                        callSiteArray[12].callCurrent((GroovyObject)this, callSiteArray[13].callGroovyObjectGetProperty(this));
                        callSiteArray[14].callCurrent((GroovyObject)this, callSiteArray[15].callGroovyObjectGetProperty(this));
                        callSiteArray[16].callCurrent((GroovyObject)this, callSiteArray[17].callGroovyObjectGetProperty(this));
                        callSiteArray[18].callCurrent((GroovyObject)this, callSiteArray[19].callGroovyObjectGetProperty(this));
                        callSiteArray[20].callCurrent(this);
                        callSiteArray[21].callCurrent((GroovyObject)this, callSiteArray[22].callGroovyObjectGetProperty(this));
                        callSiteArray[23].callCurrent(this);
                        return callSiteArray[24].callCurrent((GroovyObject)this, callSiteArray[25].callGroovyObjectGetProperty(this));
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
                        stringArray[0] = "menuItem";
                        stringArray[1] = "undoAction";
                        stringArray[2] = "menuItem";
                        stringArray[3] = "redoAction";
                        stringArray[4] = "separator";
                        stringArray[5] = "menuItem";
                        stringArray[6] = "cutAction";
                        stringArray[7] = "menuItem";
                        stringArray[8] = "copyAction";
                        stringArray[9] = "menuItem";
                        stringArray[10] = "pasteAction";
                        stringArray[11] = "separator";
                        stringArray[12] = "menuItem";
                        stringArray[13] = "findAction";
                        stringArray[14] = "menuItem";
                        stringArray[15] = "findNextAction";
                        stringArray[16] = "menuItem";
                        stringArray[17] = "findPreviousAction";
                        stringArray[18] = "menuItem";
                        stringArray[19] = "replaceAction";
                        stringArray[20] = "separator";
                        stringArray[21] = "menuItem";
                        stringArray[22] = "selectAllAction";
                        stringArray[23] = "separator";
                        stringArray[24] = "menuItem";
                        stringArray[25] = "commentAction";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[26];
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
                callSiteArray[1].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"text", "Edit", "mnemonic", "E"}), new _closure3(this, this.getThisObject()));
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
                        callSiteArray[0].callCurrent((GroovyObject)this, callSiteArray[1].callGroovyObjectGetProperty(this));
                        callSiteArray[2].callCurrent(this);
                        callSiteArray[3].callCurrent((GroovyObject)this, callSiteArray[4].callGroovyObjectGetProperty(this));
                        callSiteArray[5].callCurrent((GroovyObject)this, callSiteArray[6].callGroovyObjectGetProperty(this));
                        callSiteArray[7].callCurrent(this);
                        callSiteArray[8].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"selected", callSiteArray[9].callGetProperty(callSiteArray[10].callGroovyObjectGetProperty(this))}), callSiteArray[11].callGroovyObjectGetProperty(this));
                        callSiteArray[12].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"selected", callSiteArray[13].callGetProperty(callSiteArray[14].callGroovyObjectGetProperty(this))}), callSiteArray[15].callGroovyObjectGetProperty(this));
                        callSiteArray[16].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"selected", callSiteArray[17].callGetProperty(callSiteArray[18].callGroovyObjectGetProperty(this))}), callSiteArray[19].callGroovyObjectGetProperty(this));
                        callSiteArray[20].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"selected", callSiteArray[21].callGetProperty(callSiteArray[22].callGroovyObjectGetProperty(this))}), callSiteArray[23].callGroovyObjectGetProperty(this));
                        callSiteArray[24].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"selected", callSiteArray[25].callGetProperty(callSiteArray[26].callGroovyObjectGetProperty(this))}), callSiteArray[27].callGroovyObjectGetProperty(this));
                        callSiteArray[28].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"selected", callSiteArray[29].callGetProperty(callSiteArray[30].callGroovyObjectGetProperty(this))}), callSiteArray[31].callGroovyObjectGetProperty(this));
                        callSiteArray[32].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"selected", callSiteArray[33].callGetProperty(callSiteArray[34].callGroovyObjectGetProperty(this))}), callSiteArray[35].callGroovyObjectGetProperty(this));
                        return callSiteArray[36].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"selected", callSiteArray[37].callGetProperty(callSiteArray[38].callGroovyObjectGetProperty(this))}), callSiteArray[39].callGroovyObjectGetProperty(this));
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
                        stringArray[0] = "menuItem";
                        stringArray[1] = "clearOutputAction";
                        stringArray[2] = "separator";
                        stringArray[3] = "menuItem";
                        stringArray[4] = "largerFontAction";
                        stringArray[5] = "menuItem";
                        stringArray[6] = "smallerFontAction";
                        stringArray[7] = "separator";
                        stringArray[8] = "checkBoxMenuItem";
                        stringArray[9] = "captureStdOut";
                        stringArray[10] = "controller";
                        stringArray[11] = "captureStdOutAction";
                        stringArray[12] = "checkBoxMenuItem";
                        stringArray[13] = "captureStdErr";
                        stringArray[14] = "controller";
                        stringArray[15] = "captureStdErrAction";
                        stringArray[16] = "checkBoxMenuItem";
                        stringArray[17] = "fullStackTraces";
                        stringArray[18] = "controller";
                        stringArray[19] = "fullStackTracesAction";
                        stringArray[20] = "checkBoxMenuItem";
                        stringArray[21] = "showScriptInOutput";
                        stringArray[22] = "controller";
                        stringArray[23] = "showScriptInOutputAction";
                        stringArray[24] = "checkBoxMenuItem";
                        stringArray[25] = "visualizeScriptResults";
                        stringArray[26] = "controller";
                        stringArray[27] = "visualizeScriptResultsAction";
                        stringArray[28] = "checkBoxMenuItem";
                        stringArray[29] = "showToolbar";
                        stringArray[30] = "controller";
                        stringArray[31] = "showToolbarAction";
                        stringArray[32] = "checkBoxMenuItem";
                        stringArray[33] = "detachedOutput";
                        stringArray[34] = "controller";
                        stringArray[35] = "detachedOutputAction";
                        stringArray[36] = "checkBoxMenuItem";
                        stringArray[37] = "autoClearOutput";
                        stringArray[38] = "controller";
                        stringArray[39] = "autoClearOutputAction";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[40];
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
                callSiteArray[2].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"text", "View", "mnemonic", "V"}), new _closure4(this, this.getThisObject()));
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

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                        callSiteArray[0].callCurrent((GroovyObject)this, callSiteArray[1].callGroovyObjectGetProperty(this));
                        return callSiteArray[2].callCurrent((GroovyObject)this, callSiteArray[3].callGroovyObjectGetProperty(this));
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                        return this.doCall(null);
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
                        stringArray[0] = "menuItem";
                        stringArray[1] = "historyPrevAction";
                        stringArray[2] = "menuItem";
                        stringArray[3] = "historyNextAction";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[4];
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
                callSiteArray[3].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"text", "History", "mnemonic", "I"}), new _closure5(this, this.getThisObject()));
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

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure6.$getCallSiteArray();
                        callSiteArray[0].callCurrent((GroovyObject)this, callSiteArray[1].callGroovyObjectGetProperty(this));
                        callSiteArray[2].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"selected", callSiteArray[3].callGetProperty(callSiteArray[4].callGroovyObjectGetProperty(this))}), callSiteArray[5].callGroovyObjectGetProperty(this));
                        callSiteArray[6].callCurrent((GroovyObject)this, callSiteArray[7].callGroovyObjectGetProperty(this));
                        callSiteArray[8].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"selected", callSiteArray[9].callGetProperty(callSiteArray[10].callGroovyObjectGetProperty(this))}), callSiteArray[11].callGroovyObjectGetProperty(this));
                        callSiteArray[12].callCurrent((GroovyObject)this, callSiteArray[13].callGroovyObjectGetProperty(this));
                        callSiteArray[14].callCurrent((GroovyObject)this, callSiteArray[15].callGroovyObjectGetProperty(this));
                        callSiteArray[16].callCurrent(this);
                        callSiteArray[17].callCurrent((GroovyObject)this, callSiteArray[18].callGroovyObjectGetProperty(this));
                        callSiteArray[19].callCurrent((GroovyObject)this, callSiteArray[20].callGroovyObjectGetProperty(this));
                        callSiteArray[21].callCurrent((GroovyObject)this, callSiteArray[22].callGroovyObjectGetProperty(this));
                        callSiteArray[23].callCurrent(this);
                        callSiteArray[24].callCurrent((GroovyObject)this, callSiteArray[25].callGroovyObjectGetProperty(this));
                        callSiteArray[26].callCurrent((GroovyObject)this, callSiteArray[27].callGroovyObjectGetProperty(this));
                        return callSiteArray[28].callCurrent((GroovyObject)this, callSiteArray[29].callGroovyObjectGetProperty(this));
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure6.$getCallSiteArray();
                        return this.doCall(null);
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
                        stringArray[0] = "menuItem";
                        stringArray[1] = "runAction";
                        stringArray[2] = "checkBoxMenuItem";
                        stringArray[3] = "saveOnRun";
                        stringArray[4] = "controller";
                        stringArray[5] = "saveOnRunAction";
                        stringArray[6] = "menuItem";
                        stringArray[7] = "runSelectionAction";
                        stringArray[8] = "checkBoxMenuItem";
                        stringArray[9] = "threadInterrupt";
                        stringArray[10] = "controller";
                        stringArray[11] = "threadInterruptAction";
                        stringArray[12] = "menuItem";
                        stringArray[13] = "interruptAction";
                        stringArray[14] = "menuItem";
                        stringArray[15] = "compileAction";
                        stringArray[16] = "separator";
                        stringArray[17] = "menuItem";
                        stringArray[18] = "addClasspathJar";
                        stringArray[19] = "menuItem";
                        stringArray[20] = "addClasspathDir";
                        stringArray[21] = "menuItem";
                        stringArray[22] = "clearClassloader";
                        stringArray[23] = "separator";
                        stringArray[24] = "menuItem";
                        stringArray[25] = "inspectLastAction";
                        stringArray[26] = "menuItem";
                        stringArray[27] = "inspectVariablesAction";
                        stringArray[28] = "menuItem";
                        stringArray[29] = "inspectAstAction";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[30];
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
                callSiteArray[4].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"text", "Script", "mnemonic", "S"}), new _closure6(this, this.getThisObject()));
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

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                        return callSiteArray[0].callCurrent((GroovyObject)this, callSiteArray[1].callGroovyObjectGetProperty(this));
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                        return this.doCall(null);
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
                        stringArray[0] = "menuItem";
                        stringArray[1] = "aboutAction";
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
                return callSiteArray[5].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"text", "Help", "mnemonic", "H"}), new _closure7(this, this.getThisObject()));
            }

            public Object doCall() {
                CallSite[] callSiteArray = _run_closure1.$getCallSiteArray();
                return this.doCall(null);
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
                stringArray[0] = "menu";
                stringArray[1] = "menu";
                stringArray[2] = "menu";
                stringArray[3] = "menu";
                stringArray[4] = "menu";
                stringArray[5] = "menu";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[6];
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
        return callSiteArray[1].callCurrent((GroovyObject)this, new _run_closure1(this, this));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != BasicMenuBar.class) {
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
        stringArray[1] = "menuBar";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[2];
        BasicMenuBar.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(BasicMenuBar.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = BasicMenuBar.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

