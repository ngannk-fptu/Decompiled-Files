/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui.view;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.lang.Script;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.lang.ref.SoftReference;
import java.math.BigDecimal;
import javax.swing.SwingConstants;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class BasicStatusBar
extends Script {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public BasicStatusBar() {
        CallSite[] callSiteArray = BasicStatusBar.$getCallSiteArray();
    }

    public BasicStatusBar(Binding context) {
        CallSite[] callSiteArray = BasicStatusBar.$getCallSiteArray();
        super(context);
    }

    public static void main(String ... args) {
        CallSite[] callSiteArray = BasicStatusBar.$getCallSiteArray();
        callSiteArray[0].call(InvokerHelper.class, BasicStatusBar.class, args);
    }

    @Override
    public Object run() {
        CallSite[] callSiteArray = BasicStatusBar.$getCallSiteArray();
        public class _run_closure1
        extends Closure
        implements GeneratedClosure {
            private static final /* synthetic */ BigDecimal $const$0;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _run_closure1(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _run_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _run_closure1.$getCallSiteArray();
                callSiteArray[0].callCurrent(this);
                callSiteArray[1].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"gridwidth", callSiteArray[2].callGetProperty(GridBagConstraints.class), "fill", callSiteArray[3].callGetProperty(GridBagConstraints.class)}));
                Object object = callSiteArray[4].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"weightx", $const$0, "anchor", callSiteArray[5].callGetProperty(GridBagConstraints.class), "fill", callSiteArray[6].callGetProperty(GridBagConstraints.class), "insets", ScriptBytecodeAdapter.createList(new Object[]{1, 3, 1, 3})}), new GStringImpl(new Object[]{callSiteArray[7].callGetProperty(GroovySystem.class)}, new String[]{"Welcome to Groovy ", "."}));
                ScriptBytecodeAdapter.setGroovyObjectProperty(object, _run_closure1.class, this, "status");
                callSiteArray[8].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"orientation", callSiteArray[9].callGetProperty(SwingConstants.class), "fill", callSiteArray[10].callGetProperty(GridBagConstraints.class)}));
                Object object2 = callSiteArray[11].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"insets", ScriptBytecodeAdapter.createList(new Object[]{1, 3, 1, 3})}), "1:1");
                ScriptBytecodeAdapter.setGroovyObjectProperty(object2, _run_closure1.class, this, "rowNumAndColNum");
                return object2;
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

            public static /* synthetic */ void __$swapInit() {
                BigDecimal bigDecimal;
                CallSite[] callSiteArray = _run_closure1.$getCallSiteArray();
                $callSiteArray = null;
                $const$0 = bigDecimal = new BigDecimal("1.0");
            }

            static {
                _run_closure1.__$swapInit();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "gridBagLayout";
                stringArray[1] = "separator";
                stringArray[2] = "REMAINDER";
                stringArray[3] = "HORIZONTAL";
                stringArray[4] = "label";
                stringArray[5] = "WEST";
                stringArray[6] = "HORIZONTAL";
                stringArray[7] = "version";
                stringArray[8] = "separator";
                stringArray[9] = "VERTICAL";
                stringArray[10] = "VERTICAL";
                stringArray[11] = "label";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[12];
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
        Object object = callSiteArray[1].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"constraints", callSiteArray[2].callGetProperty(BorderLayout.class)}), new _run_closure1(this, this));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object, BasicStatusBar.class, this, "statusPanel");
        return object;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != BasicStatusBar.class) {
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
        stringArray[1] = "panel";
        stringArray[2] = "SOUTH";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[3];
        BasicStatusBar.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(BasicStatusBar.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = BasicStatusBar.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

