/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui.view;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Script;
import java.awt.BorderLayout;
import java.lang.ref.SoftReference;
import javax.swing.SwingConstants;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class BasicToolBar
extends Script {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public BasicToolBar() {
        CallSite[] callSiteArray = BasicToolBar.$getCallSiteArray();
    }

    public BasicToolBar(Binding context) {
        CallSite[] callSiteArray = BasicToolBar.$getCallSiteArray();
        super(context);
    }

    public static void main(String ... args) {
        CallSite[] callSiteArray = BasicToolBar.$getCallSiteArray();
        callSiteArray[0].call(InvokerHelper.class, BasicToolBar.class, args);
    }

    @Override
    public Object run() {
        CallSite[] callSiteArray = BasicToolBar.$getCallSiteArray();
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
                callSiteArray[0].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"text", null}), callSiteArray[1].callGroovyObjectGetProperty(this));
                callSiteArray[2].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"text", null}), callSiteArray[3].callGroovyObjectGetProperty(this));
                callSiteArray[4].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"text", null}), callSiteArray[5].callGroovyObjectGetProperty(this));
                callSiteArray[6].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"orientation", callSiteArray[7].callGetProperty(SwingConstants.class)}));
                callSiteArray[8].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"text", null}), callSiteArray[9].callGroovyObjectGetProperty(this));
                callSiteArray[10].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"text", null}), callSiteArray[11].callGroovyObjectGetProperty(this));
                callSiteArray[12].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"orientation", callSiteArray[13].callGetProperty(SwingConstants.class)}));
                callSiteArray[14].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"text", null}), callSiteArray[15].callGroovyObjectGetProperty(this));
                callSiteArray[16].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"text", null}), callSiteArray[17].callGroovyObjectGetProperty(this));
                callSiteArray[18].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"text", null}), callSiteArray[19].callGroovyObjectGetProperty(this));
                callSiteArray[20].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"orientation", callSiteArray[21].callGetProperty(SwingConstants.class)}));
                callSiteArray[22].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"text", null}), callSiteArray[23].callGroovyObjectGetProperty(this));
                callSiteArray[24].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"text", null}), callSiteArray[25].callGroovyObjectGetProperty(this));
                callSiteArray[26].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"orientation", callSiteArray[27].callGetProperty(SwingConstants.class)}));
                callSiteArray[28].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"text", null}), callSiteArray[29].callGroovyObjectGetProperty(this));
                callSiteArray[30].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"text", null}), callSiteArray[31].callGroovyObjectGetProperty(this));
                callSiteArray[32].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"orientation", callSiteArray[33].callGetProperty(SwingConstants.class)}));
                callSiteArray[34].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"text", null}), callSiteArray[35].callGroovyObjectGetProperty(this));
                return callSiteArray[36].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"text", null}), callSiteArray[37].callGroovyObjectGetProperty(this));
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
                stringArray[0] = "button";
                stringArray[1] = "newFileAction";
                stringArray[2] = "button";
                stringArray[3] = "openAction";
                stringArray[4] = "button";
                stringArray[5] = "saveAction";
                stringArray[6] = "separator";
                stringArray[7] = "VERTICAL";
                stringArray[8] = "button";
                stringArray[9] = "undoAction";
                stringArray[10] = "button";
                stringArray[11] = "redoAction";
                stringArray[12] = "separator";
                stringArray[13] = "VERTICAL";
                stringArray[14] = "button";
                stringArray[15] = "cutAction";
                stringArray[16] = "button";
                stringArray[17] = "copyAction";
                stringArray[18] = "button";
                stringArray[19] = "pasteAction";
                stringArray[20] = "separator";
                stringArray[21] = "VERTICAL";
                stringArray[22] = "button";
                stringArray[23] = "findAction";
                stringArray[24] = "button";
                stringArray[25] = "replaceAction";
                stringArray[26] = "separator";
                stringArray[27] = "VERTICAL";
                stringArray[28] = "button";
                stringArray[29] = "historyPrevAction";
                stringArray[30] = "button";
                stringArray[31] = "historyNextAction";
                stringArray[32] = "separator";
                stringArray[33] = "VERTICAL";
                stringArray[34] = "button";
                stringArray[35] = "runAction";
                stringArray[36] = "button";
                stringArray[37] = "interruptAction";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[38];
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
        Object object = callSiteArray[1].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"rollover", true, "visible", callSiteArray[2].callGetProperty(callSiteArray[3].callGroovyObjectGetProperty(this)), "constraints", callSiteArray[4].callGetProperty(BorderLayout.class)}), new _run_closure1(this, this));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object, BasicToolBar.class, this, "toolbar");
        return object;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != BasicToolBar.class) {
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
        stringArray[1] = "toolBar";
        stringArray[2] = "showToolbar";
        stringArray[3] = "controller";
        stringArray[4] = "NORTH";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[5];
        BasicToolBar.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(BasicToolBar.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = BasicToolBar.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

