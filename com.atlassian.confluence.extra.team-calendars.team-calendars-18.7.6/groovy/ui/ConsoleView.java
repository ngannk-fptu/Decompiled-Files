/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.lang.Script;
import groovy.ui.view.Defaults;
import groovy.ui.view.GTKDefaults;
import groovy.ui.view.MacOSXDefaults;
import groovy.ui.view.WindowsDefaults;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.lang.ref.SoftReference;
import javax.swing.UIManager;
import javax.swing.event.DocumentListener;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class ConsoleView
extends Script {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ConsoleView() {
        CallSite[] callSiteArray = ConsoleView.$getCallSiteArray();
    }

    public ConsoleView(Binding context) {
        CallSite[] callSiteArray = ConsoleView.$getCallSiteArray();
        super(context);
    }

    public static void main(String ... args) {
        CallSite[] callSiteArray = ConsoleView.$getCallSiteArray();
        callSiteArray[0].call(InvokerHelper.class, ConsoleView.class, args);
    }

    @Override
    public Object run() {
        CallSite[] callSiteArray = ConsoleView.$getCallSiteArray();
        Object object = callSiteArray[1].call(UIManager.class);
        if (ScriptBytecodeAdapter.isCase(object, "com.sun.java.swing.plaf.windows.WindowsLookAndFeel") || ScriptBytecodeAdapter.isCase(object, "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel")) {
            callSiteArray[2].callCurrent((GroovyObject)this, WindowsDefaults.class);
        } else if (ScriptBytecodeAdapter.isCase(object, "apple.laf.AquaLookAndFeel") || ScriptBytecodeAdapter.isCase(object, "com.apple.laf.AquaLookAndFeel")) {
            callSiteArray[3].callCurrent((GroovyObject)this, MacOSXDefaults.class);
        } else if (ScriptBytecodeAdapter.isCase(object, "com.sun.java.swing.plaf.gtk.GTKLookAndFeel")) {
            callSiteArray[4].callCurrent((GroovyObject)this, GTKDefaults.class);
        } else {
            callSiteArray[5].callCurrent((GroovyObject)this, Defaults.class);
        }
        ConsoleView consoleView = this;
        ScriptBytecodeAdapter.setProperty(consoleView, null, callSiteArray[6].callGetProperty(callSiteArray[7].callGroovyObjectGetProperty(this)), "delegate");
        Object object2 = callSiteArray[8].call(callSiteArray[9].call(callSiteArray[10].callGroovyObjectGetProperty(this), "rootContainerDelegate"));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object2, ConsoleView.class, this, "consoleFrame");
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
                Object object = callSiteArray[0].callGroovyObjectGetProperty(this);
                ScriptBytecodeAdapter.setProperty(object, null, callSiteArray[1].callGetProperty(callSiteArray[2].callGroovyObjectGetProperty(this)), "delegate");
                callSiteArray[3].call(callSiteArray[4].call(callSiteArray[5].callGroovyObjectGetProperty(this), "menuBarDelegate"), callSiteArray[6].callGroovyObjectGetProperty(this));
                callSiteArray[7].callCurrent((GroovyObject)this, callSiteArray[8].callGroovyObjectGetProperty(this));
                callSiteArray[9].callCurrent((GroovyObject)this, callSiteArray[10].callGroovyObjectGetProperty(this));
                return callSiteArray[11].callCurrent((GroovyObject)this, callSiteArray[12].callGroovyObjectGetProperty(this));
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
                stringArray[0] = "delegate";
                stringArray[1] = "menuBarDelegate";
                stringArray[2] = "binding";
                stringArray[3] = "call";
                stringArray[4] = "getAt";
                stringArray[5] = "binding";
                stringArray[6] = "menuBarClass";
                stringArray[7] = "build";
                stringArray[8] = "contentPaneClass";
                stringArray[9] = "build";
                stringArray[10] = "toolBarClass";
                stringArray[11] = "build";
                stringArray[12] = "statusBarClass";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[13];
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
        callSiteArray[11].callCurrent(this, callSiteArray[12].callGroovyObjectGetProperty(this), new _run_closure1(this, this));
        Object object3 = callSiteArray[13].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object3, null, callSiteArray[14].callGroovyObjectGetProperty(this), "promptStyle");
        Object object4 = callSiteArray[15].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object4, null, callSiteArray[16].callGroovyObjectGetProperty(this), "commandStyle");
        Object object5 = callSiteArray[17].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object5, null, callSiteArray[18].callGroovyObjectGetProperty(this), "outputStyle");
        Object object6 = callSiteArray[19].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object6, null, callSiteArray[20].callGroovyObjectGetProperty(this), "stacktraceStyle");
        Object object7 = callSiteArray[21].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object7, null, callSiteArray[22].callGroovyObjectGetProperty(this), "hyperlinkStyle");
        Object object8 = callSiteArray[23].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object8, null, callSiteArray[24].callGroovyObjectGetProperty(this), "resultStyle");
        if (callSiteArray[25].callGroovyObjectGetProperty(this) instanceof Window) {
            Closure closure = ScriptBytecodeAdapter.getMethodPointer(callSiteArray[26].callGroovyObjectGetProperty(this), "exit");
            ScriptBytecodeAdapter.setProperty(closure, null, callSiteArray[27].callGroovyObjectGetProperty(this), "windowClosing");
        }
        Object object9 = callSiteArray[28].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object9, null, callSiteArray[29].callGroovyObjectGetProperty(this), "inputEditor");
        Object object10 = callSiteArray[30].callGetProperty(callSiteArray[31].callGroovyObjectGetProperty(this));
        ScriptBytecodeAdapter.setProperty(object10, null, callSiteArray[32].callGroovyObjectGetProperty(this), "inputArea");
        Object object11 = callSiteArray[33].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object11, null, callSiteArray[34].callGroovyObjectGetProperty(this), "outputArea");
        Object object12 = callSiteArray[35].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object12, null, callSiteArray[36].callGroovyObjectGetProperty(this), "outputWindow");
        Object object13 = callSiteArray[37].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object13, null, callSiteArray[38].callGroovyObjectGetProperty(this), "statusLabel");
        Object object14 = callSiteArray[39].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object14, null, callSiteArray[40].callGroovyObjectGetProperty(this), "frame");
        Object object15 = callSiteArray[41].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object15, null, callSiteArray[42].callGroovyObjectGetProperty(this), "rowNumAndColNum");
        Object object16 = callSiteArray[43].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object16, null, callSiteArray[44].callGroovyObjectGetProperty(this), "toolbar");
        Object object17 = callSiteArray[45].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object17, null, callSiteArray[46].callGroovyObjectGetProperty(this), "saveAction");
        Object object18 = callSiteArray[47].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object18, null, callSiteArray[48].callGroovyObjectGetProperty(this), "prevHistoryAction");
        Object object19 = callSiteArray[49].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object19, null, callSiteArray[50].callGroovyObjectGetProperty(this), "nextHistoryAction");
        Object object20 = callSiteArray[51].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object20, null, callSiteArray[52].callGroovyObjectGetProperty(this), "fullStackTracesAction");
        Object object21 = callSiteArray[53].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object21, null, callSiteArray[54].callGroovyObjectGetProperty(this), "showToolbarAction");
        Object object22 = callSiteArray[55].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object22, null, callSiteArray[56].callGroovyObjectGetProperty(this), "detachedOutputAction");
        Object object23 = callSiteArray[57].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object23, null, callSiteArray[58].callGroovyObjectGetProperty(this), "autoClearOutputAction");
        Object object24 = callSiteArray[59].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object24, null, callSiteArray[60].callGroovyObjectGetProperty(this), "saveOnRunAction");
        Object object25 = callSiteArray[61].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object25, null, callSiteArray[62].callGroovyObjectGetProperty(this), "threadInterruptAction");
        Object object26 = callSiteArray[63].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object26, null, callSiteArray[64].callGroovyObjectGetProperty(this), "showOutputWindowAction");
        Object object27 = callSiteArray[65].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object27, null, callSiteArray[66].callGroovyObjectGetProperty(this), "hideOutputWindowAction1");
        Object object28 = callSiteArray[67].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object28, null, callSiteArray[68].callGroovyObjectGetProperty(this), "hideOutputWindowAction2");
        Object object29 = callSiteArray[69].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object29, null, callSiteArray[70].callGroovyObjectGetProperty(this), "hideOutputWindowAction3");
        Object object30 = callSiteArray[71].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object30, null, callSiteArray[72].callGroovyObjectGetProperty(this), "hideOutputWindowAction4");
        Object object31 = callSiteArray[73].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object31, null, callSiteArray[74].callGroovyObjectGetProperty(this), "interruptAction");
        Object object32 = callSiteArray[75].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object32, null, callSiteArray[76].callGroovyObjectGetProperty(this), "origDividerSize");
        Object object33 = callSiteArray[77].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object33, null, callSiteArray[78].callGroovyObjectGetProperty(this), "splitPane");
        Object object34 = callSiteArray[79].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object34, null, callSiteArray[80].callGroovyObjectGetProperty(this), "blank");
        Object object35 = callSiteArray[81].callGroovyObjectGetProperty(this);
        ScriptBytecodeAdapter.setProperty(object35, null, callSiteArray[82].callGroovyObjectGetProperty(this), "scrollArea");
        callSiteArray[83].call(callSiteArray[84].callGetProperty(callSiteArray[85].callGroovyObjectGetProperty(this)), callSiteArray[86].callGroovyObjectGetProperty(this));
        callSiteArray[87].call(callSiteArray[88].callGetProperty(callSiteArray[89].callGroovyObjectGetProperty(this)), callSiteArray[90].callGroovyObjectGetProperty(this));
        callSiteArray[91].call(callSiteArray[92].callGetProperty(callSiteArray[93].callGroovyObjectGetProperty(this)), callSiteArray[94].callGroovyObjectGetProperty(this));
        callSiteArray[95].call(callSiteArray[96].callGetProperty(callSiteArray[97].callGroovyObjectGetProperty(this)), callSiteArray[98].callGroovyObjectGetProperty(this));
        callSiteArray[99].call(callSiteArray[100].callGetProperty(callSiteArray[101].callGroovyObjectGetProperty(this)), callSiteArray[102].callGroovyObjectGetProperty(this));
        callSiteArray[103].call(callSiteArray[104].callGetProperty(callSiteArray[105].callGroovyObjectGetProperty(this)), callSiteArray[106].callGroovyObjectGetProperty(this));
        public class _run_closure2
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _run_closure2(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _run_closure2.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _run_closure2.$getCallSiteArray();
                return callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this), true);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _run_closure2.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _run_closure2.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "setDirty";
                stringArray[1] = "controller";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _run_closure2.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_run_closure2.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _run_closure2.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[107].call(callSiteArray[108].callGetProperty(callSiteArray[109].callGetProperty(callSiteArray[110].callGroovyObjectGetProperty(this))), ScriptBytecodeAdapter.createPojoWrapper((DocumentListener)ScriptBytecodeAdapter.asType(new _run_closure2(this, this), DocumentListener.class), DocumentListener.class));
        Object object36 = callSiteArray[111].callGetProperty(callSiteArray[112].callGetProperty(callSiteArray[113].callGroovyObjectGetProperty(this)));
        ScriptBytecodeAdapter.setProperty(object36, null, callSiteArray[114].callGroovyObjectGetProperty(this), "rootElement");
        public class _run_closure3
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _run_closure3(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _run_closure3.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(DropTargetDragEvent evt) {
                CallSite[] callSiteArray = _run_closure3.$getCallSiteArray();
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(callSiteArray[1].callGetProperty(evt), callSiteArray[2].callGetProperty(DataFlavor.class)))) {
                    return callSiteArray[3].call((Object)evt, callSiteArray[4].callGetProperty(DnDConstants.class));
                }
                return callSiteArray[5].call(evt);
            }

            public Object call(DropTargetDragEvent evt) {
                CallSite[] callSiteArray = _run_closure3.$getCallSiteArray();
                return callSiteArray[6].callCurrent((GroovyObject)this, evt);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _run_closure3.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "isDataFlavorSupported";
                stringArray[1] = "dropTargetContext";
                stringArray[2] = "javaFileListFlavor";
                stringArray[3] = "acceptDrag";
                stringArray[4] = "ACTION_COPY";
                stringArray[5] = "rejectDrag";
                stringArray[6] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[7];
                _run_closure3.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_run_closure3.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _run_closure3.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        public class _run_closure4
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _run_closure4(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _run_closure4.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(DropTargetDragEvent evt) {
                CallSite[] callSiteArray = _run_closure4.$getCallSiteArray();
                return null;
            }

            public Object call(DropTargetDragEvent evt) {
                CallSite[] callSiteArray = _run_closure4.$getCallSiteArray();
                return callSiteArray[0].callCurrent((GroovyObject)this, evt);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _run_closure4.class) {
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
                stringArray[0] = "doCall";
                return new CallSiteArray(_run_closure4.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _run_closure4.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        public class _run_closure5
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _run_closure5(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _run_closure5.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(DropTargetDragEvent evt) {
                CallSite[] callSiteArray = _run_closure5.$getCallSiteArray();
                return null;
            }

            public Object call(DropTargetDragEvent evt) {
                CallSite[] callSiteArray = _run_closure5.$getCallSiteArray();
                return callSiteArray[0].callCurrent((GroovyObject)this, evt);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _run_closure5.class) {
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
                stringArray[0] = "doCall";
                return new CallSiteArray(_run_closure5.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _run_closure5.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        public class _run_closure6
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _run_closure6(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _run_closure6.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(DropTargetEvent evt) {
                CallSite[] callSiteArray = _run_closure6.$getCallSiteArray();
                return null;
            }

            public Object call(DropTargetEvent evt) {
                CallSite[] callSiteArray = _run_closure6.$getCallSiteArray();
                return callSiteArray[0].callCurrent((GroovyObject)this, evt);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _run_closure6.class) {
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
                stringArray[0] = "doCall";
                return new CallSiteArray(_run_closure6.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _run_closure6.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        public class _run_closure7
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _run_closure7(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _run_closure7.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(DropTargetDropEvent evt) {
                CallSite[] callSiteArray = _run_closure7.$getCallSiteArray();
                callSiteArray[0].call((Object)evt, callSiteArray[1].callGetProperty(DnDConstants.class));
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call(callSiteArray[3].callGroovyObjectGetProperty(this)))) {
                    return callSiteArray[4].call(callSiteArray[5].callGroovyObjectGetProperty(this), callSiteArray[6].call(callSiteArray[7].call(callSiteArray[8].callGetProperty(evt), callSiteArray[9].callGetProperty(DataFlavor.class)), 0));
                }
                return null;
            }

            public Object call(DropTargetDropEvent evt) {
                CallSite[] callSiteArray = _run_closure7.$getCallSiteArray();
                return callSiteArray[10].callCurrent((GroovyObject)this, evt);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _run_closure7.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "acceptDrop";
                stringArray[1] = "ACTION_COPY";
                stringArray[2] = "askToSaveFile";
                stringArray[3] = "controller";
                stringArray[4] = "loadScriptFile";
                stringArray[5] = "controller";
                stringArray[6] = "getAt";
                stringArray[7] = "getTransferData";
                stringArray[8] = "transferable";
                stringArray[9] = "javaFileListFlavor";
                stringArray[10] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[11];
                _run_closure7.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_run_closure7.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _run_closure7.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        Reference<DropTargetListener> dtListener = new Reference<DropTargetListener>((DropTargetListener)ScriptBytecodeAdapter.asType(ScriptBytecodeAdapter.createMap(new Object[]{"dragEnter", new _run_closure3(this, this), "dragOver", new _run_closure4(this, this), "dropActionChanged", new _run_closure5(this, this), "dragExit", new _run_closure6(this, this), "drop", new _run_closure7(this, this)}), DropTargetListener.class));
        public class _run_closure8
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference dtListener;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _run_closure8(Object _outerInstance, Object _thisObject, Reference dtListener) {
                Reference reference;
                CallSite[] callSiteArray = _run_closure8.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.dtListener = reference = dtListener;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _run_closure8.$getCallSiteArray();
                return callSiteArray[0].callConstructor(DropTarget.class, it, callSiteArray[1].callGetProperty(DnDConstants.class), this.dtListener.get());
            }

            public Object getDtListener() {
                CallSite[] callSiteArray = _run_closure8.$getCallSiteArray();
                return this.dtListener.get();
            }

            public Object doCall() {
                CallSite[] callSiteArray = _run_closure8.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _run_closure8.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "<$constructor$>";
                stringArray[1] = "ACTION_COPY";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _run_closure8.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_run_closure8.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _run_closure8.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[115].call((Object)ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[116].callGroovyObjectGetProperty(this), callSiteArray[117].callGroovyObjectGetProperty(this), callSiteArray[118].callGroovyObjectGetProperty(this)}), new _run_closure8(this, this, dtListener));
        return null;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ConsoleView.class) {
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
        stringArray[1] = "getSystemLookAndFeelClassName";
        stringArray[2] = "build";
        stringArray[3] = "build";
        stringArray[4] = "build";
        stringArray[5] = "build";
        stringArray[6] = "rootContainerDelegate";
        stringArray[7] = "binding";
        stringArray[8] = "call";
        stringArray[9] = "getAt";
        stringArray[10] = "binding";
        stringArray[11] = "container";
        stringArray[12] = "consoleFrame";
        stringArray[13] = "promptStyle";
        stringArray[14] = "controller";
        stringArray[15] = "commandStyle";
        stringArray[16] = "controller";
        stringArray[17] = "outputStyle";
        stringArray[18] = "controller";
        stringArray[19] = "stacktraceStyle";
        stringArray[20] = "controller";
        stringArray[21] = "hyperlinkStyle";
        stringArray[22] = "controller";
        stringArray[23] = "resultStyle";
        stringArray[24] = "controller";
        stringArray[25] = "consoleFrame";
        stringArray[26] = "controller";
        stringArray[27] = "consoleFrame";
        stringArray[28] = "inputEditor";
        stringArray[29] = "controller";
        stringArray[30] = "textEditor";
        stringArray[31] = "inputEditor";
        stringArray[32] = "controller";
        stringArray[33] = "outputArea";
        stringArray[34] = "controller";
        stringArray[35] = "outputWindow";
        stringArray[36] = "controller";
        stringArray[37] = "status";
        stringArray[38] = "controller";
        stringArray[39] = "consoleFrame";
        stringArray[40] = "controller";
        stringArray[41] = "rowNumAndColNum";
        stringArray[42] = "controller";
        stringArray[43] = "toolbar";
        stringArray[44] = "controller";
        stringArray[45] = "saveAction";
        stringArray[46] = "controller";
        stringArray[47] = "historyPrevAction";
        stringArray[48] = "controller";
        stringArray[49] = "historyNextAction";
        stringArray[50] = "controller";
        stringArray[51] = "fullStackTracesAction";
        stringArray[52] = "controller";
        stringArray[53] = "showToolbarAction";
        stringArray[54] = "controller";
        stringArray[55] = "detachedOutputAction";
        stringArray[56] = "controller";
        stringArray[57] = "autoClearOutputAction";
        stringArray[58] = "controller";
        stringArray[59] = "saveOnRunAction";
        stringArray[60] = "controller";
        stringArray[61] = "threadInterruptAction";
        stringArray[62] = "controller";
        stringArray[63] = "showOutputWindowAction";
        stringArray[64] = "controller";
        stringArray[65] = "hideOutputWindowAction1";
        stringArray[66] = "controller";
        stringArray[67] = "hideOutputWindowAction2";
        stringArray[68] = "controller";
        stringArray[69] = "hideOutputWindowAction3";
        stringArray[70] = "controller";
        stringArray[71] = "hideOutputWindowAction4";
        stringArray[72] = "controller";
        stringArray[73] = "interruptAction";
        stringArray[74] = "controller";
        stringArray[75] = "origDividerSize";
        stringArray[76] = "controller";
        stringArray[77] = "splitPane";
        stringArray[78] = "controller";
        stringArray[79] = "blank";
        stringArray[80] = "controller";
        stringArray[81] = "scrollArea";
        stringArray[82] = "controller";
        stringArray[83] = "addComponentListener";
        stringArray[84] = "outputArea";
        stringArray[85] = "controller";
        stringArray[86] = "controller";
        stringArray[87] = "addComponentListener";
        stringArray[88] = "inputArea";
        stringArray[89] = "controller";
        stringArray[90] = "controller";
        stringArray[91] = "addHyperlinkListener";
        stringArray[92] = "outputArea";
        stringArray[93] = "controller";
        stringArray[94] = "controller";
        stringArray[95] = "addHyperlinkListener";
        stringArray[96] = "outputArea";
        stringArray[97] = "controller";
        stringArray[98] = "controller";
        stringArray[99] = "addFocusListener";
        stringArray[100] = "outputArea";
        stringArray[101] = "controller";
        stringArray[102] = "controller";
        stringArray[103] = "addCaretListener";
        stringArray[104] = "inputArea";
        stringArray[105] = "controller";
        stringArray[106] = "controller";
        stringArray[107] = "addDocumentListener";
        stringArray[108] = "document";
        stringArray[109] = "inputArea";
        stringArray[110] = "controller";
        stringArray[111] = "defaultRootElement";
        stringArray[112] = "document";
        stringArray[113] = "inputArea";
        stringArray[114] = "controller";
        stringArray[115] = "each";
        stringArray[116] = "consoleFrame";
        stringArray[117] = "inputArea";
        stringArray[118] = "outputArea";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[119];
        ConsoleView.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ConsoleView.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ConsoleView.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

