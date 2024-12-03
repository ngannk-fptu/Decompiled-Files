/*
 * Decompiled with CFR 0.152.
 */
package groovy.inspect.swingui;

import groovy.inspect.swingui.AstBrowserUiPreferences;
import groovy.inspect.swingui.AstNodeToScriptAdapter;
import groovy.inspect.swingui.CompilePhaseAdapter;
import groovy.inspect.swingui.GeneratedBytecodeAwareGroovyClassLoader;
import groovy.inspect.swingui.ScriptToTreeNodeAdapter;
import groovy.inspect.swingui.SwingTreeNodeMaker;
import groovy.inspect.swingui.TreeNodeWithProperties;
import groovy.lang.Closure;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.swing.SwingBuilder;
import groovy.ui.Console;
import groovy.ui.ConsoleTextEditor;
import groovyjarjarasm.asm.ClassReader;
import groovyjarjarasm.asm.util.TraceClassVisitor;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.SoftReference;
import java.math.BigDecimal;
import java.util.EventObject;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class AstBrowser
implements GroovyObject {
    private Object inputArea;
    private Object rootElement;
    private Object decompiledSource;
    private Object jTree;
    private Object propertyTable;
    private Object splitterPane;
    private Object mainSplitter;
    private Object bytecodeView;
    private boolean showScriptFreeForm;
    private boolean showScriptClass;
    private boolean showClosureClasses;
    private boolean showTreeView;
    private GeneratedBytecodeAwareGroovyClassLoader classLoader;
    private Object prefs;
    private SwingBuilder swing;
    private Object frame;
    private final Object updateFontSize;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public AstBrowser(Object inputArea, Object rootElement, Object classLoader) {
        Object object;
        Object object2;
        MetaClass metaClass;
        Object object3;
        CallSite[] callSiteArray = AstBrowser.$getCallSiteArray();
        this.prefs = object3 = callSiteArray[0].callConstructor(AstBrowserUiPreferences.class);
        _closure1 _closure110 = new _closure1(this, this);
        this.updateFontSize = _closure110;
        this.metaClass = metaClass = this.$getStaticMetaClass();
        this.inputArea = object2 = inputArea;
        this.rootElement = object = rootElement;
        Object object4 = callSiteArray[1].callConstructor(GeneratedBytecodeAwareGroovyClassLoader.class, classLoader);
        this.classLoader = (GeneratedBytecodeAwareGroovyClassLoader)ScriptBytecodeAdapter.castToType(object4, GeneratedBytecodeAwareGroovyClassLoader.class);
    }

    public static void main(String ... args) {
        CallSite[] callSiteArray = AstBrowser.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(args)) {
            callSiteArray[2].callStatic(AstBrowser.class, "Usage: java groovy.inspect.swingui.AstBrowser [filename]\nwhere [filename] is a Groovy script");
        } else {
            Reference<Object> file = new Reference<Object>(callSiteArray[3].callConstructor(File.class, ScriptBytecodeAdapter.createPojoWrapper(ShortTypeHandling.castToString(callSiteArray[4].call((Object)args, 0)), String.class)));
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[5].call(file.get()))) {
                callSiteArray[6].callStatic(AstBrowser.class, new GStringImpl(new Object[]{args}, new String[]{"File ", "[0] cannot be found."}));
            } else {
                callSiteArray[7].call(UIManager.class, callSiteArray[8].call(UIManager.class));
                public class _main_closure2
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference file;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _main_closure2(Object _outerInstance, Object _thisObject, Reference file) {
                        Reference reference;
                        CallSite[] callSiteArray = _main_closure2.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.file = reference = file;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _main_closure2.$getCallSiteArray();
                        return callSiteArray[0].callGetProperty(this.file.get());
                    }

                    public Object getFile() {
                        CallSite[] callSiteArray = _main_closure2.$getCallSiteArray();
                        return this.file.get();
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _main_closure2.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _main_closure2.class) {
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
                        stringArray[0] = "text";
                        return new CallSiteArray(_main_closure2.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _main_closure2.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                callSiteArray[9].call(callSiteArray[10].callConstructor(AstBrowser.class, null, null, callSiteArray[11].callConstructor(GroovyClassLoader.class)), new _main_closure2(AstBrowser.class, AstBrowser.class, file), callSiteArray[12].callGetProperty(file.get()));
            }
        }
    }

    public void run(Closure script) {
        CallSite[] callSiteArray = AstBrowser.$getCallSiteArray();
        callSiteArray[13].callCurrent(this, script, null);
    }

    public void run(Closure script, String name) {
        Object object;
        Reference<Closure> script2 = new Reference<Closure>(script);
        CallSite[] callSiteArray = AstBrowser.$getCallSiteArray();
        Object object2 = callSiteArray[14].callConstructor(SwingBuilder.class);
        this.swing = (SwingBuilder)ScriptBytecodeAdapter.castToType(object2, SwingBuilder.class);
        Reference<Object> phasePicker = new Reference<Object>(null);
        phasePicker.get();
        Object object3 = callSiteArray[15].callGetProperty(this.prefs);
        this.showScriptFreeForm = DefaultTypeTransformation.booleanUnbox(object3);
        Object object4 = callSiteArray[16].callGetProperty(this.prefs);
        this.showScriptClass = DefaultTypeTransformation.booleanUnbox(object4);
        Object object5 = callSiteArray[17].callGetProperty(this.prefs);
        this.showClosureClasses = DefaultTypeTransformation.booleanUnbox(object5);
        Object object6 = callSiteArray[18].callGetProperty(this.prefs);
        this.showTreeView = DefaultTypeTransformation.booleanUnbox(object6);
        public class _run_closure3
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference phasePicker;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _run_closure3(Object _outerInstance, Object _thisObject, Reference phasePicker) {
                Reference reference;
                CallSite[] callSiteArray = _run_closure3.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.phasePicker = reference = phasePicker;
            }

            public Object doCall(Object event) {
                CallSite[] callSiteArray = _run_closure3.$getCallSiteArray();
                return callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this), ArrayUtil.createArray(callSiteArray[2].callGroovyObjectGetProperty(this), callSiteArray[3].callGroovyObjectGetProperty(this), callSiteArray[4].callGroovyObjectGetProperty(this), callSiteArray[5].callGroovyObjectGetProperty(this), callSiteArray[6].callGroovyObjectGetProperty(this), callSiteArray[7].callGroovyObjectGetProperty(this), callSiteArray[8].callGetProperty(this.phasePicker.get()), callSiteArray[9].callGroovyObjectGetProperty(this)));
            }

            public Object getPhasePicker() {
                CallSite[] callSiteArray = _run_closure3.$getCallSiteArray();
                return this.phasePicker.get();
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
                stringArray[0] = "save";
                stringArray[1] = "prefs";
                stringArray[2] = "frame";
                stringArray[3] = "splitterPane";
                stringArray[4] = "mainSplitter";
                stringArray[5] = "showScriptFreeForm";
                stringArray[6] = "showScriptClass";
                stringArray[7] = "showClosureClasses";
                stringArray[8] = "selectedItem";
                stringArray[9] = "showTreeView";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[10];
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
            private /* synthetic */ Reference phasePicker;
            private /* synthetic */ Reference script;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _run_closure4(Object _outerInstance, Object _thisObject, Reference phasePicker, Reference script) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _run_closure4.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.phasePicker = reference2 = phasePicker;
                this.script = reference = script;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _run_closure4.$getCallSiteArray();
                public class _closure9
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference phasePicker;
                    private /* synthetic */ Reference script;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure9(Object _outerInstance, Object _thisObject, Reference phasePicker, Reference script) {
                        Reference reference;
                        Reference reference2;
                        CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.phasePicker = reference2 = phasePicker;
                        this.script = reference = script;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                        public class _closure11
                        extends Closure
                        implements GeneratedClosure {
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure11(Object _outerInstance, Object _thisObject) {
                                CallSite[] callSiteArray = _closure11.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                            }

                            public Object doCall(Object it) {
                                CallSite[] callSiteArray = _closure11.$getCallSiteArray();
                                public class _closure14
                                extends Closure
                                implements GeneratedClosure {
                                    private static /* synthetic */ ClassInfo $staticClassInfo;
                                    public static transient /* synthetic */ boolean __$stMC;
                                    private static /* synthetic */ SoftReference $callSiteArray;

                                    public _closure14(Object _outerInstance, Object _thisObject) {
                                        CallSite[] callSiteArray = _closure14.$getCallSiteArray();
                                        super(_outerInstance, _thisObject);
                                    }

                                    public Object doCall(Object it) {
                                        CallSite[] callSiteArray = _closure14.$getCallSiteArray();
                                        return callSiteArray[0].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Free Form", "closure", ScriptBytecodeAdapter.getMethodPointer(this.getThisObject(), "showScriptFreeForm"), "mnemonic", "F"}));
                                    }

                                    public Object doCall() {
                                        CallSite[] callSiteArray = _closure14.$getCallSiteArray();
                                        return this.doCall(null);
                                    }

                                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                        if (this.getClass() != _closure14.class) {
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
                                        stringArray[0] = "action";
                                        return new CallSiteArray(_closure14.class, stringArray);
                                    }

                                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                        CallSiteArray callSiteArray;
                                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                            callSiteArray = _closure14.$createCallSiteArray();
                                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                        }
                                        return callSiteArray.array;
                                    }
                                }
                                callSiteArray[0].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"selected", callSiteArray[1].callGroovyObjectGetProperty(this)}), new _closure14(this, this.getThisObject()));
                                public class _closure15
                                extends Closure
                                implements GeneratedClosure {
                                    private static /* synthetic */ ClassInfo $staticClassInfo;
                                    public static transient /* synthetic */ boolean __$stMC;
                                    private static /* synthetic */ SoftReference $callSiteArray;

                                    public _closure15(Object _outerInstance, Object _thisObject) {
                                        CallSite[] callSiteArray = _closure15.$getCallSiteArray();
                                        super(_outerInstance, _thisObject);
                                    }

                                    public Object doCall(Object it) {
                                        CallSite[] callSiteArray = _closure15.$getCallSiteArray();
                                        return callSiteArray[0].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Class Form", "closure", ScriptBytecodeAdapter.getMethodPointer(this.getThisObject(), "showScriptClass"), "mnemonic", "C"}));
                                    }

                                    public Object doCall() {
                                        CallSite[] callSiteArray = _closure15.$getCallSiteArray();
                                        return this.doCall(null);
                                    }

                                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                        if (this.getClass() != _closure15.class) {
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
                                        stringArray[0] = "action";
                                        return new CallSiteArray(_closure15.class, stringArray);
                                    }

                                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                        CallSiteArray callSiteArray;
                                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                            callSiteArray = _closure15.$createCallSiteArray();
                                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                        }
                                        return callSiteArray.array;
                                    }
                                }
                                callSiteArray[2].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"selected", callSiteArray[3].callGroovyObjectGetProperty(this)}), new _closure15(this, this.getThisObject()));
                                public class _closure16
                                extends Closure
                                implements GeneratedClosure {
                                    private static /* synthetic */ ClassInfo $staticClassInfo;
                                    public static transient /* synthetic */ boolean __$stMC;
                                    private static /* synthetic */ SoftReference $callSiteArray;

                                    public _closure16(Object _outerInstance, Object _thisObject) {
                                        CallSite[] callSiteArray = _closure16.$getCallSiteArray();
                                        super(_outerInstance, _thisObject);
                                    }

                                    public Object doCall(Object it) {
                                        CallSite[] callSiteArray = _closure16.$getCallSiteArray();
                                        return callSiteArray[0].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Generated Closure Classes", "closure", ScriptBytecodeAdapter.getMethodPointer(this.getThisObject(), "showClosureClasses"), "mnemonic", "G"}));
                                    }

                                    public Object doCall() {
                                        CallSite[] callSiteArray = _closure16.$getCallSiteArray();
                                        return this.doCall(null);
                                    }

                                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                        if (this.getClass() != _closure16.class) {
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
                                        stringArray[0] = "action";
                                        return new CallSiteArray(_closure16.class, stringArray);
                                    }

                                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                        CallSiteArray callSiteArray;
                                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                            callSiteArray = _closure16.$createCallSiteArray();
                                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                        }
                                        return callSiteArray.array;
                                    }
                                }
                                callSiteArray[4].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"selected", callSiteArray[5].callGroovyObjectGetProperty(this)}), new _closure16(this, this.getThisObject()));
                                public class _closure17
                                extends Closure
                                implements GeneratedClosure {
                                    private static /* synthetic */ ClassInfo $staticClassInfo;
                                    public static transient /* synthetic */ boolean __$stMC;
                                    private static /* synthetic */ SoftReference $callSiteArray;

                                    public _closure17(Object _outerInstance, Object _thisObject) {
                                        CallSite[] callSiteArray = _closure17.$getCallSiteArray();
                                        super(_outerInstance, _thisObject);
                                    }

                                    public Object doCall(Object it) {
                                        CallSite[] callSiteArray = _closure17.$getCallSiteArray();
                                        return callSiteArray[0].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Tree View", "closure", ScriptBytecodeAdapter.getMethodPointer(this.getThisObject(), "showTreeView"), "mnemonic", "T"}));
                                    }

                                    public Object doCall() {
                                        CallSite[] callSiteArray = _closure17.$getCallSiteArray();
                                        return this.doCall(null);
                                    }

                                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                        if (this.getClass() != _closure17.class) {
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
                                        stringArray[0] = "action";
                                        return new CallSiteArray(_closure17.class, stringArray);
                                    }

                                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                        CallSiteArray callSiteArray;
                                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                            callSiteArray = _closure17.$createCallSiteArray();
                                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                        }
                                        return callSiteArray.array;
                                    }
                                }
                                return callSiteArray[6].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"selected", callSiteArray[7].callGroovyObjectGetProperty(this)}), new _closure17(this, this.getThisObject()));
                            }

                            public Object doCall() {
                                CallSite[] callSiteArray = _closure11.$getCallSiteArray();
                                return this.doCall(null);
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _closure11.class) {
                                    return ScriptBytecodeAdapter.initMetaClass(this);
                                }
                                ClassInfo classInfo = $staticClassInfo;
                                if (classInfo == null) {
                                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                }
                                return classInfo.getMetaClass();
                            }

                            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                stringArray[0] = "checkBoxMenuItem";
                                stringArray[1] = "showScriptFreeForm";
                                stringArray[2] = "checkBoxMenuItem";
                                stringArray[3] = "showScriptClass";
                                stringArray[4] = "checkBoxMenuItem";
                                stringArray[5] = "showClosureClasses";
                                stringArray[6] = "checkBoxMenuItem";
                                stringArray[7] = "showTreeView";
                            }

                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                String[] stringArray = new String[8];
                                _closure11.$createCallSiteArray_1(stringArray);
                                return new CallSiteArray(_closure11.class, stringArray);
                            }

                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                CallSiteArray callSiteArray;
                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                    callSiteArray = _closure11.$createCallSiteArray();
                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                }
                                return callSiteArray.array;
                            }
                        }
                        callSiteArray[0].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"text", "Show Script", "mnemonic", "S"}), new _closure11(this, this.getThisObject()));
                        public class _closure12
                        extends Closure
                        implements GeneratedClosure {
                            private /* synthetic */ Reference phasePicker;
                            private /* synthetic */ Reference script;
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure12(Object _outerInstance, Object _thisObject, Reference phasePicker, Reference script) {
                                Reference reference;
                                Reference reference2;
                                CallSite[] callSiteArray = _closure12.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                                this.phasePicker = reference2 = phasePicker;
                                this.script = reference = script;
                            }

                            public Object doCall(Object it) {
                                CallSite[] callSiteArray = _closure12.$getCallSiteArray();
                                public class _closure18
                                extends Closure
                                implements GeneratedClosure {
                                    private static /* synthetic */ ClassInfo $staticClassInfo;
                                    public static transient /* synthetic */ boolean __$stMC;
                                    private static /* synthetic */ SoftReference $callSiteArray;

                                    public _closure18(Object _outerInstance, Object _thisObject) {
                                        CallSite[] callSiteArray = _closure18.$getCallSiteArray();
                                        super(_outerInstance, _thisObject);
                                    }

                                    public Object doCall(Object it) {
                                        CallSite[] callSiteArray = _closure18.$getCallSiteArray();
                                        return callSiteArray[0].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Larger Font", "closure", ScriptBytecodeAdapter.getMethodPointer(this.getThisObject(), "largerFont"), "mnemonic", "L", "accelerator", callSiteArray[1].callCurrent((GroovyObject)this, "shift L")}));
                                    }

                                    public Object doCall() {
                                        CallSite[] callSiteArray = _closure18.$getCallSiteArray();
                                        return this.doCall(null);
                                    }

                                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                        if (this.getClass() != _closure18.class) {
                                            return ScriptBytecodeAdapter.initMetaClass(this);
                                        }
                                        ClassInfo classInfo = $staticClassInfo;
                                        if (classInfo == null) {
                                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                        }
                                        return classInfo.getMetaClass();
                                    }

                                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                        stringArray[0] = "action";
                                        stringArray[1] = "shortcut";
                                    }

                                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                        String[] stringArray = new String[2];
                                        _closure18.$createCallSiteArray_1(stringArray);
                                        return new CallSiteArray(_closure18.class, stringArray);
                                    }

                                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                        CallSiteArray callSiteArray;
                                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                            callSiteArray = _closure18.$createCallSiteArray();
                                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                        }
                                        return callSiteArray.array;
                                    }
                                }
                                callSiteArray[0].callCurrent((GroovyObject)this, new _closure18(this, this.getThisObject()));
                                public class _closure19
                                extends Closure
                                implements GeneratedClosure {
                                    private static /* synthetic */ ClassInfo $staticClassInfo;
                                    public static transient /* synthetic */ boolean __$stMC;
                                    private static /* synthetic */ SoftReference $callSiteArray;

                                    public _closure19(Object _outerInstance, Object _thisObject) {
                                        CallSite[] callSiteArray = _closure19.$getCallSiteArray();
                                        super(_outerInstance, _thisObject);
                                    }

                                    public Object doCall(Object it) {
                                        CallSite[] callSiteArray = _closure19.$getCallSiteArray();
                                        return callSiteArray[0].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Smaller Font", "closure", ScriptBytecodeAdapter.getMethodPointer(this.getThisObject(), "smallerFont"), "mnemonic", "S", "accelerator", callSiteArray[1].callCurrent((GroovyObject)this, "shift S")}));
                                    }

                                    public Object doCall() {
                                        CallSite[] callSiteArray = _closure19.$getCallSiteArray();
                                        return this.doCall(null);
                                    }

                                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                        if (this.getClass() != _closure19.class) {
                                            return ScriptBytecodeAdapter.initMetaClass(this);
                                        }
                                        ClassInfo classInfo = $staticClassInfo;
                                        if (classInfo == null) {
                                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                        }
                                        return classInfo.getMetaClass();
                                    }

                                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                        stringArray[0] = "action";
                                        stringArray[1] = "shortcut";
                                    }

                                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                        String[] stringArray = new String[2];
                                        _closure19.$createCallSiteArray_1(stringArray);
                                        return new CallSiteArray(_closure19.class, stringArray);
                                    }

                                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                        CallSiteArray callSiteArray;
                                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                            callSiteArray = _closure19.$createCallSiteArray();
                                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                        }
                                        return callSiteArray.array;
                                    }
                                }
                                callSiteArray[1].callCurrent((GroovyObject)this, new _closure19(this, this.getThisObject()));
                                public class _closure20
                                extends Closure
                                implements GeneratedClosure {
                                    private /* synthetic */ Reference phasePicker;
                                    private /* synthetic */ Reference script;
                                    private static /* synthetic */ ClassInfo $staticClassInfo;
                                    public static transient /* synthetic */ boolean __$stMC;
                                    private static /* synthetic */ SoftReference $callSiteArray;

                                    public _closure20(Object _outerInstance, Object _thisObject, Reference phasePicker, Reference script) {
                                        Reference reference;
                                        Reference reference2;
                                        CallSite[] callSiteArray = _closure20.$getCallSiteArray();
                                        super(_outerInstance, _thisObject);
                                        this.phasePicker = reference2 = phasePicker;
                                        this.script = reference = script;
                                    }

                                    public Object doCall(Object it) {
                                        CallSite[] callSiteArray = _closure20.$getCallSiteArray();
                                        public class _closure21
                                        extends Closure
                                        implements GeneratedClosure {
                                            private /* synthetic */ Reference phasePicker;
                                            private /* synthetic */ Reference script;
                                            private static /* synthetic */ ClassInfo $staticClassInfo;
                                            public static transient /* synthetic */ boolean __$stMC;
                                            private static /* synthetic */ SoftReference $callSiteArray;

                                            public _closure21(Object _outerInstance, Object _thisObject, Reference phasePicker, Reference script) {
                                                Reference reference;
                                                Reference reference2;
                                                CallSite[] callSiteArray = _closure21.$getCallSiteArray();
                                                super(_outerInstance, _thisObject);
                                                this.phasePicker = reference2 = phasePicker;
                                                this.script = reference = script;
                                            }

                                            public Object doCall(Object it) {
                                                CallSite[] callSiteArray = _closure21.$getCallSiteArray();
                                                callSiteArray[0].callCurrent(this, callSiteArray[1].callGetProperty(callSiteArray[2].callGetProperty(this.phasePicker.get())), callSiteArray[3].call(this.script.get()));
                                                return callSiteArray[4].callCurrent(this, callSiteArray[5].callGroovyObjectGetProperty(this), callSiteArray[6].call(this.script.get()), callSiteArray[7].callGetProperty(callSiteArray[8].callGetProperty(this.phasePicker.get())));
                                            }

                                            public Object getPhasePicker() {
                                                CallSite[] callSiteArray = _closure21.$getCallSiteArray();
                                                return this.phasePicker.get();
                                            }

                                            public Closure getScript() {
                                                CallSite[] callSiteArray = _closure21.$getCallSiteArray();
                                                return (Closure)ScriptBytecodeAdapter.castToType(this.script.get(), Closure.class);
                                            }

                                            public Object doCall() {
                                                CallSite[] callSiteArray = _closure21.$getCallSiteArray();
                                                return this.doCall(null);
                                            }

                                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                if (this.getClass() != _closure21.class) {
                                                    return ScriptBytecodeAdapter.initMetaClass(this);
                                                }
                                                ClassInfo classInfo = $staticClassInfo;
                                                if (classInfo == null) {
                                                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                                }
                                                return classInfo.getMetaClass();
                                            }

                                            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                                stringArray[0] = "decompile";
                                                stringArray[1] = "phaseId";
                                                stringArray[2] = "selectedItem";
                                                stringArray[3] = "call";
                                                stringArray[4] = "compile";
                                                stringArray[5] = "jTree";
                                                stringArray[6] = "call";
                                                stringArray[7] = "phaseId";
                                                stringArray[8] = "selectedItem";
                                            }

                                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                                String[] stringArray = new String[9];
                                                _closure21.$createCallSiteArray_1(stringArray);
                                                return new CallSiteArray(_closure21.class, stringArray);
                                            }

                                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                CallSiteArray callSiteArray;
                                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                    callSiteArray = _closure21.$createCallSiteArray();
                                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                }
                                                return callSiteArray.array;
                                            }
                                        }
                                        return callSiteArray[0].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Refresh", "closure", new _closure21(this, this.getThisObject(), this.phasePicker, this.script), "mnemonic", "R", "accelerator", callSiteArray[1].call(KeyStroke.class, callSiteArray[2].callGetProperty(KeyEvent.class), 0)}));
                                    }

                                    public Object getPhasePicker() {
                                        CallSite[] callSiteArray = _closure20.$getCallSiteArray();
                                        return this.phasePicker.get();
                                    }

                                    public Closure getScript() {
                                        CallSite[] callSiteArray = _closure20.$getCallSiteArray();
                                        return (Closure)ScriptBytecodeAdapter.castToType(this.script.get(), Closure.class);
                                    }

                                    public Object doCall() {
                                        CallSite[] callSiteArray = _closure20.$getCallSiteArray();
                                        return this.doCall(null);
                                    }

                                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                        if (this.getClass() != _closure20.class) {
                                            return ScriptBytecodeAdapter.initMetaClass(this);
                                        }
                                        ClassInfo classInfo = $staticClassInfo;
                                        if (classInfo == null) {
                                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                        }
                                        return classInfo.getMetaClass();
                                    }

                                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                        stringArray[0] = "action";
                                        stringArray[1] = "getKeyStroke";
                                        stringArray[2] = "VK_F5";
                                    }

                                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                        String[] stringArray = new String[3];
                                        _closure20.$createCallSiteArray_1(stringArray);
                                        return new CallSiteArray(_closure20.class, stringArray);
                                    }

                                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                        CallSiteArray callSiteArray;
                                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                            callSiteArray = _closure20.$createCallSiteArray();
                                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                        }
                                        return callSiteArray.array;
                                    }
                                }
                                return callSiteArray[2].callCurrent((GroovyObject)this, new _closure20(this, this.getThisObject(), this.phasePicker, this.script));
                            }

                            public Object getPhasePicker() {
                                CallSite[] callSiteArray = _closure12.$getCallSiteArray();
                                return this.phasePicker.get();
                            }

                            public Closure getScript() {
                                CallSite[] callSiteArray = _closure12.$getCallSiteArray();
                                return (Closure)ScriptBytecodeAdapter.castToType(this.script.get(), Closure.class);
                            }

                            public Object doCall() {
                                CallSite[] callSiteArray = _closure12.$getCallSiteArray();
                                return this.doCall(null);
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _closure12.class) {
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
                                stringArray[1] = "menuItem";
                                stringArray[2] = "menuItem";
                            }

                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                String[] stringArray = new String[3];
                                _closure12.$createCallSiteArray_1(stringArray);
                                return new CallSiteArray(_closure12.class, stringArray);
                            }

                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                CallSiteArray callSiteArray;
                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                    callSiteArray = _closure12.$createCallSiteArray();
                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                }
                                return callSiteArray.array;
                            }
                        }
                        callSiteArray[1].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"text", "View", "mnemonic", "V"}), new _closure12(this, this.getThisObject(), this.phasePicker, this.script));
                        public class _closure13
                        extends Closure
                        implements GeneratedClosure {
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure13(Object _outerInstance, Object _thisObject) {
                                CallSite[] callSiteArray = _closure13.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                            }

                            public Object doCall(Object it) {
                                CallSite[] callSiteArray = _closure13.$getCallSiteArray();
                                public class _closure22
                                extends Closure
                                implements GeneratedClosure {
                                    private static /* synthetic */ ClassInfo $staticClassInfo;
                                    public static transient /* synthetic */ boolean __$stMC;
                                    private static /* synthetic */ SoftReference $callSiteArray;

                                    public _closure22(Object _outerInstance, Object _thisObject) {
                                        CallSite[] callSiteArray = _closure22.$getCallSiteArray();
                                        super(_outerInstance, _thisObject);
                                    }

                                    public Object doCall(Object it) {
                                        CallSite[] callSiteArray = _closure22.$getCallSiteArray();
                                        return callSiteArray[0].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "About", "closure", ScriptBytecodeAdapter.getMethodPointer(this.getThisObject(), "showAbout"), "mnemonic", "A"}));
                                    }

                                    public Object doCall() {
                                        CallSite[] callSiteArray = _closure22.$getCallSiteArray();
                                        return this.doCall(null);
                                    }

                                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                        if (this.getClass() != _closure22.class) {
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
                                        stringArray[0] = "action";
                                        return new CallSiteArray(_closure22.class, stringArray);
                                    }

                                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                        CallSiteArray callSiteArray;
                                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                            callSiteArray = _closure22.$createCallSiteArray();
                                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                        }
                                        return callSiteArray.array;
                                    }
                                }
                                return callSiteArray[0].callCurrent((GroovyObject)this, new _closure22(this, this.getThisObject()));
                            }

                            public Object doCall() {
                                CallSite[] callSiteArray = _closure13.$getCallSiteArray();
                                return this.doCall(null);
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _closure13.class) {
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
                                stringArray[0] = "menuItem";
                                return new CallSiteArray(_closure13.class, stringArray);
                            }

                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                CallSiteArray callSiteArray;
                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                    callSiteArray = _closure13.$createCallSiteArray();
                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                }
                                return callSiteArray.array;
                            }
                        }
                        return callSiteArray[2].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"text", "Help", "mnemonic", "H"}), new _closure13(this, this.getThisObject()));
                    }

                    public Object getPhasePicker() {
                        CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                        return this.phasePicker.get();
                    }

                    public Closure getScript() {
                        CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                        return (Closure)ScriptBytecodeAdapter.castToType(this.script.get(), Closure.class);
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure9.class) {
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
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[3];
                        _closure9.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_closure9.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure9.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                callSiteArray[0].callCurrent((GroovyObject)this, new _closure9(this, this.getThisObject(), this.phasePicker, this.script));
                public class _closure10
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference phasePicker;
                    private /* synthetic */ Reference script;
                    private static final /* synthetic */ BigDecimal $const$0;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure10(Object _outerInstance, Object _thisObject, Reference phasePicker, Reference script) {
                        Reference reference;
                        Reference reference2;
                        CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.phasePicker = reference2 = phasePicker;
                        this.script = reference = script;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                        callSiteArray[0].callCurrent(this);
                        callSiteArray[1].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"text", "At end of Phase: ", "constraints", callSiteArray[2].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"gridx", 0, "gridy", 0, "gridwidth", 1, "gridheight", 1, "weightx", 0, "weighty", 0, "anchor", callSiteArray[3].callGetProperty(GridBagConstraints.class), "fill", callSiteArray[4].callGetProperty(GridBagConstraints.class), "insets", ScriptBytecodeAdapter.createList(new Object[]{2, 2, 2, 2})}))}));
                        public class _closure23
                        extends Closure
                        implements GeneratedClosure {
                            private /* synthetic */ Reference phasePicker;
                            private /* synthetic */ Reference script;
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure23(Object _outerInstance, Object _thisObject, Reference phasePicker, Reference script) {
                                Reference reference;
                                Reference reference2;
                                CallSite[] callSiteArray = _closure23.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                                this.phasePicker = reference2 = phasePicker;
                                this.script = reference = script;
                            }

                            public Object doCall(Object it) {
                                CallSite[] callSiteArray = _closure23.$getCallSiteArray();
                                String string = "// Please select a class node in the tree view.";
                                ScriptBytecodeAdapter.setProperty(string, null, callSiteArray[0].callGetProperty(callSiteArray[1].callGroovyObjectGetProperty(this)), "text");
                                callSiteArray[2].callCurrent(this, callSiteArray[3].callGetProperty(callSiteArray[4].callGetProperty(this.phasePicker.get())), callSiteArray[5].call(this.script.get()));
                                return callSiteArray[6].callCurrent(this, callSiteArray[7].callGroovyObjectGetProperty(this), callSiteArray[8].call(this.script.get()), callSiteArray[9].callGetProperty(callSiteArray[10].callGetProperty(this.phasePicker.get())));
                            }

                            public Object getPhasePicker() {
                                CallSite[] callSiteArray = _closure23.$getCallSiteArray();
                                return this.phasePicker.get();
                            }

                            public Closure getScript() {
                                CallSite[] callSiteArray = _closure23.$getCallSiteArray();
                                return (Closure)ScriptBytecodeAdapter.castToType(this.script.get(), Closure.class);
                            }

                            public Object doCall() {
                                CallSite[] callSiteArray = _closure23.$getCallSiteArray();
                                return this.doCall(null);
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _closure23.class) {
                                    return ScriptBytecodeAdapter.initMetaClass(this);
                                }
                                ClassInfo classInfo = $staticClassInfo;
                                if (classInfo == null) {
                                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                }
                                return classInfo.getMetaClass();
                            }

                            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                stringArray[0] = "textEditor";
                                stringArray[1] = "bytecodeView";
                                stringArray[2] = "decompile";
                                stringArray[3] = "phaseId";
                                stringArray[4] = "selectedItem";
                                stringArray[5] = "call";
                                stringArray[6] = "compile";
                                stringArray[7] = "jTree";
                                stringArray[8] = "call";
                                stringArray[9] = "phaseId";
                                stringArray[10] = "selectedItem";
                            }

                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                String[] stringArray = new String[11];
                                _closure23.$createCallSiteArray_1(stringArray);
                                return new CallSiteArray(_closure23.class, stringArray);
                            }

                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                CallSiteArray callSiteArray;
                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                    callSiteArray = _closure23.$createCallSiteArray();
                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                }
                                return callSiteArray.array;
                            }
                        }
                        Object object = callSiteArray[5].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"items", callSiteArray[6].call(CompilePhaseAdapter.class), "selectedItem", callSiteArray[7].callGetProperty(callSiteArray[8].callGroovyObjectGetProperty(this)), "actionPerformed", new _closure23(this, this.getThisObject(), this.phasePicker, this.script), "constraints", callSiteArray[9].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"gridx", 1, "gridy", 0, "gridwidth", 1, "gridheight", 1, "weightx", $const$0, "weighty", 0, "anchor", callSiteArray[10].callGetProperty(GridBagConstraints.class), "fill", callSiteArray[11].callGetProperty(GridBagConstraints.class), "insets", ScriptBytecodeAdapter.createList(new Object[]{2, 2, 2, 2})}))}));
                        this.phasePicker.set(object);
                        public class _closure24
                        extends Closure
                        implements GeneratedClosure {
                            private /* synthetic */ Reference phasePicker;
                            private /* synthetic */ Reference script;
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure24(Object _outerInstance, Object _thisObject, Reference phasePicker, Reference script) {
                                Reference reference;
                                Reference reference2;
                                CallSite[] callSiteArray = _closure24.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                                this.phasePicker = reference2 = phasePicker;
                                this.script = reference = script;
                            }

                            public Object doCall(Object it) {
                                CallSite[] callSiteArray = _closure24.$getCallSiteArray();
                                callSiteArray[0].callCurrent(this, callSiteArray[1].callGetProperty(callSiteArray[2].callGetProperty(this.phasePicker.get())), callSiteArray[3].call(this.script.get()));
                                return callSiteArray[4].callCurrent(this, callSiteArray[5].callGroovyObjectGetProperty(this), callSiteArray[6].call(this.script.get()), callSiteArray[7].callGetProperty(callSiteArray[8].callGetProperty(this.phasePicker.get())));
                            }

                            public Object getPhasePicker() {
                                CallSite[] callSiteArray = _closure24.$getCallSiteArray();
                                return this.phasePicker.get();
                            }

                            public Closure getScript() {
                                CallSite[] callSiteArray = _closure24.$getCallSiteArray();
                                return (Closure)ScriptBytecodeAdapter.castToType(this.script.get(), Closure.class);
                            }

                            public Object doCall() {
                                CallSite[] callSiteArray = _closure24.$getCallSiteArray();
                                return this.doCall(null);
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _closure24.class) {
                                    return ScriptBytecodeAdapter.initMetaClass(this);
                                }
                                ClassInfo classInfo = $staticClassInfo;
                                if (classInfo == null) {
                                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                }
                                return classInfo.getMetaClass();
                            }

                            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                stringArray[0] = "decompile";
                                stringArray[1] = "phaseId";
                                stringArray[2] = "selectedItem";
                                stringArray[3] = "call";
                                stringArray[4] = "compile";
                                stringArray[5] = "jTree";
                                stringArray[6] = "call";
                                stringArray[7] = "phaseId";
                                stringArray[8] = "selectedItem";
                            }

                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                String[] stringArray = new String[9];
                                _closure24.$createCallSiteArray_1(stringArray);
                                return new CallSiteArray(_closure24.class, stringArray);
                            }

                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                CallSiteArray callSiteArray;
                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                    callSiteArray = _closure24.$createCallSiteArray();
                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                }
                                return callSiteArray.array;
                            }
                        }
                        callSiteArray[12].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"text", "Refresh", "actionPerformed", new _closure24(this, this.getThisObject(), this.phasePicker, this.script), "constraints", callSiteArray[13].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"gridx", 2, "gridy", 0, "gridwidth", 1, "gridheight", 1, "weightx", 0, "weighty", 0, "anchor", callSiteArray[14].callGetProperty(GridBagConstraints.class), "fill", callSiteArray[15].callGetProperty(GridBagConstraints.class), "insets", ScriptBytecodeAdapter.createList(new Object[]{2, 2, 2, 3})}))}));
                        public class _closure25
                        extends Closure
                        implements GeneratedClosure {
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure25(Object _outerInstance, Object _thisObject) {
                                CallSite[] callSiteArray = _closure25.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                            }

                            public Object doCall(Object it) {
                                CallSite[] callSiteArray = _closure25.$getCallSiteArray();
                                public class _closure30
                                extends Closure
                                implements GeneratedClosure {
                                    private static /* synthetic */ ClassInfo $staticClassInfo;
                                    public static transient /* synthetic */ boolean __$stMC;
                                    private static /* synthetic */ SoftReference $callSiteArray;

                                    public _closure30(Object _outerInstance, Object _thisObject) {
                                        CallSite[] callSiteArray = _closure30.$getCallSiteArray();
                                        super(_outerInstance, _thisObject);
                                    }

                                    public Object doCall(Object it) {
                                        CallSite[] callSiteArray = _closure30.$getCallSiteArray();
                                        return null;
                                    }

                                    public Object doCall() {
                                        CallSite[] callSiteArray = _closure30.$getCallSiteArray();
                                        return this.doCall(null);
                                    }

                                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                        if (this.getClass() != _closure30.class) {
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
                                        return new CallSiteArray(_closure30.class, stringArray);
                                    }

                                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                        CallSiteArray callSiteArray;
                                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                            callSiteArray = _closure30.$createCallSiteArray();
                                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                        }
                                        return callSiteArray.array;
                                    }
                                }
                                Object object = callSiteArray[0].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "AstTreeView", "rowHeight", 0, "model", callSiteArray[1].callConstructor(DefaultTreeModel.class, callSiteArray[2].callConstructor(DefaultMutableTreeNode.class, "Loading..."))}), new _closure30(this, this.getThisObject()));
                                ScriptBytecodeAdapter.setGroovyObjectProperty(object, _closure25.class, this, "jTree");
                                return object;
                            }

                            public Object doCall() {
                                CallSite[] callSiteArray = _closure25.$getCallSiteArray();
                                return this.doCall(null);
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _closure25.class) {
                                    return ScriptBytecodeAdapter.initMetaClass(this);
                                }
                                ClassInfo classInfo = $staticClassInfo;
                                if (classInfo == null) {
                                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                }
                                return classInfo.getMetaClass();
                            }

                            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                stringArray[0] = "tree";
                                stringArray[1] = "<$constructor$>";
                                stringArray[2] = "<$constructor$>";
                            }

                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                String[] stringArray = new String[3];
                                _closure25.$createCallSiteArray_1(stringArray);
                                return new CallSiteArray(_closure25.class, stringArray);
                            }

                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                CallSiteArray callSiteArray;
                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                    callSiteArray = _closure25.$createCallSiteArray();
                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                }
                                return callSiteArray.array;
                            }
                        }
                        public class _closure26
                        extends Closure
                        implements GeneratedClosure {
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure26(Object _outerInstance, Object _thisObject) {
                                CallSite[] callSiteArray = _closure26.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                            }

                            public Object doCall(Object it) {
                                CallSite[] callSiteArray = _closure26.$getCallSiteArray();
                                public class _closure31
                                extends Closure
                                implements GeneratedClosure {
                                    private static /* synthetic */ ClassInfo $staticClassInfo;
                                    public static transient /* synthetic */ boolean __$stMC;
                                    private static /* synthetic */ SoftReference $callSiteArray;

                                    public _closure31(Object _outerInstance, Object _thisObject) {
                                        CallSite[] callSiteArray = _closure31.$getCallSiteArray();
                                        super(_outerInstance, _thisObject);
                                    }

                                    public Object doCall(Object it) {
                                        CallSite[] callSiteArray = _closure31.$getCallSiteArray();
                                        public class _closure32
                                        extends Closure
                                        implements GeneratedClosure {
                                            private static /* synthetic */ ClassInfo $staticClassInfo;
                                            public static transient /* synthetic */ boolean __$stMC;
                                            private static /* synthetic */ SoftReference $callSiteArray;

                                            public _closure32(Object _outerInstance, Object _thisObject) {
                                                CallSite[] callSiteArray = _closure32.$getCallSiteArray();
                                                super(_outerInstance, _thisObject);
                                            }

                                            public Object doCall(Object it) {
                                                CallSite[] callSiteArray = _closure32.$getCallSiteArray();
                                                callSiteArray[0].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"header", "Name", "propertyName", "name"}));
                                                callSiteArray[1].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"header", "Value", "propertyName", "value"}));
                                                return callSiteArray[2].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"header", "Type", "propertyName", "type"}));
                                            }

                                            public Object doCall() {
                                                CallSite[] callSiteArray = _closure32.$getCallSiteArray();
                                                return this.doCall(null);
                                            }

                                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                                if (this.getClass() != _closure32.class) {
                                                    return ScriptBytecodeAdapter.initMetaClass(this);
                                                }
                                                ClassInfo classInfo = $staticClassInfo;
                                                if (classInfo == null) {
                                                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                                }
                                                return classInfo.getMetaClass();
                                            }

                                            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                                stringArray[0] = "propertyColumn";
                                                stringArray[1] = "propertyColumn";
                                                stringArray[2] = "propertyColumn";
                                            }

                                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                                String[] stringArray = new String[3];
                                                _closure32.$createCallSiteArray_1(stringArray);
                                                return new CallSiteArray(_closure32.class, stringArray);
                                            }

                                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                                CallSiteArray callSiteArray;
                                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                    callSiteArray = _closure32.$createCallSiteArray();
                                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                                }
                                                return callSiteArray.array;
                                            }
                                        }
                                        return callSiteArray[0].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"list", ScriptBytecodeAdapter.createList(new Object[]{ScriptBytecodeAdapter.createMap(new Object[0])})}), new _closure32(this, this.getThisObject()));
                                    }

                                    public Object doCall() {
                                        CallSite[] callSiteArray = _closure31.$getCallSiteArray();
                                        return this.doCall(null);
                                    }

                                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                        if (this.getClass() != _closure31.class) {
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
                                        stringArray[0] = "tableModel";
                                        return new CallSiteArray(_closure31.class, stringArray);
                                    }

                                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                        CallSiteArray callSiteArray;
                                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                            callSiteArray = _closure31.$createCallSiteArray();
                                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                        }
                                        return callSiteArray.array;
                                    }
                                }
                                Object object = callSiteArray[0].callCurrent((GroovyObject)this, new _closure31(this, this.getThisObject()));
                                ScriptBytecodeAdapter.setGroovyObjectProperty(object, _closure26.class, this, "propertyTable");
                                return object;
                            }

                            public Object doCall() {
                                CallSite[] callSiteArray = _closure26.$getCallSiteArray();
                                return this.doCall(null);
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _closure26.class) {
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
                                stringArray[0] = "table";
                                return new CallSiteArray(_closure26.class, stringArray);
                            }

                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                CallSiteArray callSiteArray;
                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                    callSiteArray = _closure26.$createCallSiteArray();
                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                }
                                return callSiteArray.array;
                            }
                        }
                        public class _closure27
                        extends Closure
                        implements GeneratedClosure {
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure27(Object _outerInstance, Object _thisObject) {
                                CallSite[] callSiteArray = _closure27.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                            }

                            public Object doCall(Object it) {
                                CallSite[] callSiteArray = _closure27.$getCallSiteArray();
                                return null;
                            }

                            public Object doCall() {
                                CallSite[] callSiteArray = _closure27.$getCallSiteArray();
                                return this.doCall(null);
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _closure27.class) {
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
                                return new CallSiteArray(_closure27.class, stringArray);
                            }

                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                CallSiteArray callSiteArray;
                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                    callSiteArray = _closure27.$createCallSiteArray();
                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                }
                                return callSiteArray.array;
                            }
                        }
                        Object object2 = callSiteArray[16].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"visible", callSiteArray[17].callGroovyObjectGetProperty(this), "leftComponent", callSiteArray[18].callCurrent((GroovyObject)this, new _closure25(this, this.getThisObject())), "rightComponent", callSiteArray[19].callCurrent((GroovyObject)this, new _closure26(this, this.getThisObject()))}), new _closure27(this, this.getThisObject()));
                        ScriptBytecodeAdapter.setGroovyObjectProperty(object2, _closure10.class, this, "splitterPane");
                        public class _closure28
                        extends Closure
                        implements GeneratedClosure {
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure28(Object _outerInstance, Object _thisObject) {
                                CallSite[] callSiteArray = _closure28.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                            }

                            public Object doCall(Object it) {
                                CallSite[] callSiteArray = _closure28.$getCallSiteArray();
                                CallSite callSite = callSiteArray[0];
                                Map map = ScriptBytecodeAdapter.createMap(new Object[]{"title", "Source"});
                                Object object = callSiteArray[1].callConstructor(ConsoleTextEditor.class, ScriptBytecodeAdapter.createMap(new Object[]{"editable", false, "showLineNumbers", false}));
                                ScriptBytecodeAdapter.setGroovyObjectProperty(object, _closure28.class, this, "decompiledSource");
                                callSite.callCurrent(this, map, object);
                                CallSite callSite2 = callSiteArray[2];
                                Map map2 = ScriptBytecodeAdapter.createMap(new Object[]{"title", "Bytecode"});
                                Object object2 = callSiteArray[3].callConstructor(ConsoleTextEditor.class, ScriptBytecodeAdapter.createMap(new Object[]{"editable", false, "showLineNumbers", false}));
                                ScriptBytecodeAdapter.setGroovyObjectProperty(object2, _closure28.class, this, "bytecodeView");
                                return callSite2.callCurrent(this, map2, object2);
                            }

                            public Object doCall() {
                                CallSite[] callSiteArray = _closure28.$getCallSiteArray();
                                return this.doCall(null);
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _closure28.class) {
                                    return ScriptBytecodeAdapter.initMetaClass(this);
                                }
                                ClassInfo classInfo = $staticClassInfo;
                                if (classInfo == null) {
                                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                }
                                return classInfo.getMetaClass();
                            }

                            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                stringArray[0] = "widget";
                                stringArray[1] = "<$constructor$>";
                                stringArray[2] = "widget";
                                stringArray[3] = "<$constructor$>";
                            }

                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                String[] stringArray = new String[4];
                                _closure28.$createCallSiteArray_1(stringArray);
                                return new CallSiteArray(_closure28.class, stringArray);
                            }

                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                CallSiteArray callSiteArray;
                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                    callSiteArray = _closure28.$createCallSiteArray();
                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                }
                                return callSiteArray.array;
                            }
                        }
                        public class _closure29
                        extends Closure
                        implements GeneratedClosure {
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure29(Object _outerInstance, Object _thisObject) {
                                CallSite[] callSiteArray = _closure29.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                            }

                            public Object doCall(Object it) {
                                CallSite[] callSiteArray = _closure29.$getCallSiteArray();
                                return null;
                            }

                            public Object doCall() {
                                CallSite[] callSiteArray = _closure29.$getCallSiteArray();
                                return this.doCall(null);
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _closure29.class) {
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
                                return new CallSiteArray(_closure29.class, stringArray);
                            }

                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                CallSiteArray callSiteArray;
                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                    callSiteArray = _closure29.$createCallSiteArray();
                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                }
                                return callSiteArray.array;
                            }
                        }
                        Object object3 = callSiteArray[20].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"orientation", callSiteArray[21].callGetProperty(JSplitPane.class), "topComponent", callSiteArray[22].callGroovyObjectGetProperty(this), "bottomComponent", callSiteArray[23].callCurrent((GroovyObject)this, new _closure28(this, this.getThisObject())), "constraints", callSiteArray[24].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"gridx", 0, "gridy", 2, "gridwidth", 3, "gridheight", 1, "weightx", $const$0, "weighty", $const$0, "anchor", callSiteArray[25].callGetProperty(GridBagConstraints.class), "fill", callSiteArray[26].callGetProperty(GridBagConstraints.class), "insets", ScriptBytecodeAdapter.createList(new Object[]{2, 2, 2, 2})}))}), new _closure29(this, this.getThisObject()));
                        ScriptBytecodeAdapter.setGroovyObjectProperty(object3, _closure10.class, this, "mainSplitter");
                        return object3;
                    }

                    public Object getPhasePicker() {
                        CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                        return this.phasePicker.get();
                    }

                    public Closure getScript() {
                        CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                        return (Closure)ScriptBytecodeAdapter.castToType(this.script.get(), Closure.class);
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure10.class) {
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
                        CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                        $callSiteArray = null;
                        $const$0 = bigDecimal = new BigDecimal("1.0");
                    }

                    static {
                        _closure10.__$swapInit();
                    }

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "gridBagLayout";
                        stringArray[1] = "label";
                        stringArray[2] = "gbc";
                        stringArray[3] = "WEST";
                        stringArray[4] = "HORIZONTAL";
                        stringArray[5] = "comboBox";
                        stringArray[6] = "values";
                        stringArray[7] = "selectedPhase";
                        stringArray[8] = "prefs";
                        stringArray[9] = "gbc";
                        stringArray[10] = "NORTHWEST";
                        stringArray[11] = "NONE";
                        stringArray[12] = "button";
                        stringArray[13] = "gbc";
                        stringArray[14] = "NORTHEAST";
                        stringArray[15] = "NONE";
                        stringArray[16] = "splitPane";
                        stringArray[17] = "showTreeView";
                        stringArray[18] = "scrollPane";
                        stringArray[19] = "scrollPane";
                        stringArray[20] = "splitPane";
                        stringArray[21] = "VERTICAL_SPLIT";
                        stringArray[22] = "splitterPane";
                        stringArray[23] = "tabbedPane";
                        stringArray[24] = "gbc";
                        stringArray[25] = "NORTHWEST";
                        stringArray[26] = "BOTH";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[27];
                        _closure10.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_closure10.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure10.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                return callSiteArray[1].callCurrent((GroovyObject)this, new _closure10(this, this.getThisObject(), this.phasePicker, this.script));
            }

            public Object getPhasePicker() {
                CallSite[] callSiteArray = _run_closure4.$getCallSiteArray();
                return this.phasePicker.get();
            }

            public Closure getScript() {
                CallSite[] callSiteArray = _run_closure4.$getCallSiteArray();
                return (Closure)ScriptBytecodeAdapter.castToType(this.script.get(), Closure.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _run_closure4.$getCallSiteArray();
                return this.doCall(null);
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

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "menuBar";
                stringArray[1] = "panel";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _run_closure4.$createCallSiteArray_1(stringArray);
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
        this.frame = object = callSiteArray[19].call(this.swing, ScriptBytecodeAdapter.createMap(new Object[]{"title", callSiteArray[20].call((Object)"Groovy AST Browser", DefaultTypeTransformation.booleanUnbox(name) ? new GStringImpl(new Object[]{name}, new String[]{" - ", ""}) : ""), "location", callSiteArray[21].callGetProperty(this.prefs), "size", callSiteArray[22].callGetProperty(this.prefs), "iconImage", callSiteArray[23].callGetProperty(callSiteArray[24].call((Object)this.swing, callSiteArray[25].callGetProperty(Console.class))), "defaultCloseOperation", callSiteArray[26].callGetProperty(WindowConstants.class), "windowClosing", new _run_closure3(this, this, phasePicker)}), new _run_closure4(this, this, phasePicker, script2));
        String string = "// Please select a class node in the tree view.";
        ScriptBytecodeAdapter.setProperty(string, null, callSiteArray[27].callGetProperty(this.bytecodeView), "text");
        callSiteArray[28].call(callSiteArray[29].callGetProperty(callSiteArray[30].callGetProperty(this.propertyTable)));
        callSiteArray[31].call(callSiteArray[32].callGetProperty(this.jTree), callSiteArray[33].call((Object)this.swing, callSiteArray[34].callGetProperty(Console.class)));
        Object object7 = callSiteArray[35].callGetProperty(TreeSelectionModel.class);
        ScriptBytecodeAdapter.setProperty(object7, null, callSiteArray[36].callGetProperty(this.jTree), "selectionMode");
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

            public Object doCall(TreeSelectionEvent e) {
                CallSite[] callSiteArray = _run_closure5.$getCallSiteArray();
                callSiteArray[0].call(callSiteArray[1].callGetProperty(callSiteArray[2].callGetProperty(callSiteArray[3].callGroovyObjectGetProperty(this))));
                Reference<TreeNode> node = new Reference<TreeNode>((TreeNode)ScriptBytecodeAdapter.castToType(callSiteArray[4].callGetProperty(callSiteArray[5].callGroovyObjectGetProperty(this)), TreeNode.class));
                if (node.get() instanceof TreeNodeWithProperties) {
                    public class _closure33
                    extends Closure
                    implements GeneratedClosure {
                        private static /* synthetic */ ClassInfo $staticClassInfo;
                        public static transient /* synthetic */ boolean __$stMC;
                        private static /* synthetic */ SoftReference $callSiteArray;

                        public _closure33(Object _outerInstance, Object _thisObject) {
                            CallSite[] callSiteArray = _closure33.$getCallSiteArray();
                            super(_outerInstance, _thisObject);
                        }

                        public Object doCall(Object it) {
                            CallSite[] callSiteArray = _closure33.$getCallSiteArray();
                            return callSiteArray[0].call(callSiteArray[1].callGetProperty(callSiteArray[2].callGetProperty(callSiteArray[3].callGroovyObjectGetProperty(this))), ScriptBytecodeAdapter.createMap(new Object[]{"name", callSiteArray[4].call(it, 0), "value", callSiteArray[5].call(it, 1), "type", callSiteArray[6].call(it, 2)}));
                        }

                        public Object doCall() {
                            CallSite[] callSiteArray = _closure33.$getCallSiteArray();
                            return this.doCall(null);
                        }

                        protected /* synthetic */ MetaClass $getStaticMetaClass() {
                            if (this.getClass() != _closure33.class) {
                                return ScriptBytecodeAdapter.initMetaClass(this);
                            }
                            ClassInfo classInfo = $staticClassInfo;
                            if (classInfo == null) {
                                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                            }
                            return classInfo.getMetaClass();
                        }

                        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                            stringArray[0] = "leftShift";
                            stringArray[1] = "rows";
                            stringArray[2] = "model";
                            stringArray[3] = "propertyTable";
                            stringArray[4] = "getAt";
                            stringArray[5] = "getAt";
                            stringArray[6] = "getAt";
                        }

                        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                            String[] stringArray = new String[7];
                            _closure33.$createCallSiteArray_1(stringArray);
                            return new CallSiteArray(_closure33.class, stringArray);
                        }

                        private static /* synthetic */ CallSite[] $getCallSiteArray() {
                            CallSiteArray callSiteArray;
                            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                callSiteArray = _closure33.$createCallSiteArray();
                                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                            }
                            return callSiteArray.array;
                        }
                    }
                    callSiteArray[6].call(callSiteArray[7].callGetProperty(node.get()), new _closure33(this, this.getThisObject()));
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[8].callGroovyObjectGetProperty(this)) && DefaultTypeTransformation.booleanUnbox(callSiteArray[9].callGroovyObjectGetProperty(this))) {
                        public class _closure34
                        extends Closure
                        implements GeneratedClosure {
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure34(Object _outerInstance, Object _thisObject) {
                                CallSite[] callSiteArray = _closure34.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                            }

                            public Object doCall(Object it) {
                                CallSite[] callSiteArray = _closure34.$getCallSiteArray();
                                return ScriptBytecodeAdapter.isCase(callSiteArray[0].call(it, 0), ScriptBytecodeAdapter.createList(new Object[]{"lineNumber", "columnNumber", "lastLineNumber", "lastColumnNumber"}));
                            }

                            public Object doCall() {
                                CallSite[] callSiteArray = _closure34.$getCallSiteArray();
                                return this.doCall(null);
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _closure34.class) {
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
                                stringArray[0] = "getAt";
                                return new CallSiteArray(_closure34.class, stringArray);
                            }

                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                CallSiteArray callSiteArray;
                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                    callSiteArray = _closure34.$createCallSiteArray();
                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                }
                                return callSiteArray.array;
                            }
                        }
                        Object lineInfo = callSiteArray[10].call(callSiteArray[11].callGetProperty(node.get()), new _closure34(this, this.getThisObject()));
                        public class _closure35
                        extends Closure
                        implements GeneratedClosure {
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure35(Object _outerInstance, Object _thisObject) {
                                CallSite[] callSiteArray = _closure35.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                            }

                            public Object doCall(Object map, Object info) {
                                CallSite[] callSiteArray = _closure35.$getCallSiteArray();
                                Object object = callSiteArray[0].call(Integer.class, callSiteArray[1].call(info, 1));
                                callSiteArray[2].call(map, callSiteArray[3].call(info, 0), object);
                                return map;
                            }

                            public Object call(Object map, Object info) {
                                CallSite[] callSiteArray = _closure35.$getCallSiteArray();
                                return callSiteArray[4].callCurrent(this, map, info);
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _closure35.class) {
                                    return ScriptBytecodeAdapter.initMetaClass(this);
                                }
                                ClassInfo classInfo = $staticClassInfo;
                                if (classInfo == null) {
                                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                }
                                return classInfo.getMetaClass();
                            }

                            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                stringArray[0] = "valueOf";
                                stringArray[1] = "getAt";
                                stringArray[2] = "putAt";
                                stringArray[3] = "getAt";
                                stringArray[4] = "doCall";
                            }

                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                String[] stringArray = new String[5];
                                _closure35.$createCallSiteArray_1(stringArray);
                                return new CallSiteArray(_closure35.class, stringArray);
                            }

                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                CallSiteArray callSiteArray;
                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                    callSiteArray = _closure35.$createCallSiteArray();
                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                }
                                return callSiteArray.array;
                            }
                        }
                        Object lineInfoMap = callSiteArray[12].call(lineInfo, ScriptBytecodeAdapter.createMap(new Object[0]), new _closure35(this, this.getThisObject()));
                        public class _closure36
                        extends Closure
                        implements GeneratedClosure {
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure36(Object _outerInstance, Object _thisObject) {
                                CallSite[] callSiteArray = _closure36.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                            }

                            public Object doCall(Object k, Object v) {
                                CallSite[] callSiteArray = _closure36.$getCallSiteArray();
                                return ScriptBytecodeAdapter.compareEqual(v, -1);
                            }

                            public Object call(Object k, Object v) {
                                CallSite[] callSiteArray = _closure36.$getCallSiteArray();
                                return callSiteArray[0].callCurrent(this, k, v);
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _closure36.class) {
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
                                return new CallSiteArray(_closure36.class, stringArray);
                            }

                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                CallSiteArray callSiteArray;
                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                    callSiteArray = _closure36.$createCallSiteArray();
                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                }
                                return callSiteArray.array;
                            }
                        }
                        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[13].call(lineInfoMap, new _closure36(this, this.getThisObject())))) {
                            Object startOffset = callSiteArray[14].callGetProperty(callSiteArray[15].call(callSiteArray[16].callGroovyObjectGetProperty(this), callSiteArray[17].call(callSiteArray[18].callGetProperty(lineInfoMap), 1)));
                            callSiteArray[19].call(callSiteArray[20].callGroovyObjectGetProperty(this), callSiteArray[21].call(callSiteArray[22].call(startOffset, callSiteArray[23].callGetProperty(lineInfoMap)), 1));
                            Object endOffset = callSiteArray[24].callGetProperty(callSiteArray[25].call(callSiteArray[26].callGroovyObjectGetProperty(this), callSiteArray[27].call(callSiteArray[28].callGetProperty(lineInfoMap), 1)));
                            callSiteArray[29].call(callSiteArray[30].callGroovyObjectGetProperty(this), callSiteArray[31].call(callSiteArray[32].call(endOffset, callSiteArray[33].callGetProperty(lineInfoMap)), 1));
                        } else {
                            callSiteArray[34].call(callSiteArray[35].callGroovyObjectGetProperty(this), callSiteArray[36].call(callSiteArray[37].callGroovyObjectGetProperty(this)));
                        }
                    }
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[38].callGetProperty(node.get())) || DefaultTypeTransformation.booleanUnbox(callSiteArray[39].callGetProperty(node.get()))) {
                        String string = "// Loading bytecode ...";
                        ScriptBytecodeAdapter.setProperty(string, null, callSiteArray[40].callGetProperty(callSiteArray[41].callGroovyObjectGetProperty(this)), "text");
                        Reference<Boolean> showOnlyMethodCode = new Reference<Boolean>((Boolean)ScriptBytecodeAdapter.castToType(callSiteArray[42].callGetProperty(node.get()), Boolean.class));
                        public class _closure37
                        extends Closure
                        implements GeneratedClosure {
                            private /* synthetic */ Reference showOnlyMethodCode;
                            private /* synthetic */ Reference node;
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure37(Object _outerInstance, Object _thisObject, Reference showOnlyMethodCode, Reference node) {
                                Reference reference;
                                Reference reference2;
                                CallSite[] callSiteArray = _closure37.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                                this.showOnlyMethodCode = reference2 = showOnlyMethodCode;
                                this.node = reference = node;
                            }

                            public Object doCall(Object it) {
                                CallSite[] callSiteArray = _closure37.$getCallSiteArray();
                                Object className = DefaultTypeTransformation.booleanUnbox(this.showOnlyMethodCode.get()) ? callSiteArray[0].call(this.node.get(), "declaringClass") : callSiteArray[1].call(this.node.get(), "name");
                                Object bytecode = callSiteArray[2].call(callSiteArray[3].callGroovyObjectGetProperty(this), className);
                                if (DefaultTypeTransformation.booleanUnbox(bytecode)) {
                                    Object writer = callSiteArray[4].callConstructor(StringWriter.class);
                                    Object visitor = callSiteArray[5].callConstructor(TraceClassVisitor.class, callSiteArray[6].callConstructor(PrintWriter.class, writer));
                                    Object reader = callSiteArray[7].callConstructor(ClassReader.class, bytecode);
                                    callSiteArray[8].call(reader, visitor, 0);
                                    Reference<Object> source = new Reference<Object>(callSiteArray[9].call(writer));
                                    public class _closure38
                                    extends Closure
                                    implements GeneratedClosure {
                                        private /* synthetic */ Reference source;
                                        private /* synthetic */ Reference showOnlyMethodCode;
                                        private /* synthetic */ Reference node;
                                        private static /* synthetic */ ClassInfo $staticClassInfo;
                                        public static transient /* synthetic */ boolean __$stMC;
                                        private static /* synthetic */ SoftReference $callSiteArray;

                                        public _closure38(Object _outerInstance, Object _thisObject, Reference source, Reference showOnlyMethodCode, Reference node) {
                                            Reference reference;
                                            Reference reference2;
                                            Reference reference3;
                                            CallSite[] callSiteArray = _closure38.$getCallSiteArray();
                                            super(_outerInstance, _thisObject);
                                            this.source = reference3 = source;
                                            this.showOnlyMethodCode = reference2 = showOnlyMethodCode;
                                            this.node = reference = node;
                                        }

                                        public Object doCall(Object it) {
                                            CallSite[] callSiteArray = _closure38.$getCallSiteArray();
                                            Object t = this.source.get();
                                            ScriptBytecodeAdapter.setProperty(t, null, callSiteArray[0].callGetProperty(callSiteArray[1].callGroovyObjectGetProperty(this)), "text");
                                            if (DefaultTypeTransformation.booleanUnbox(this.showOnlyMethodCode.get())) {
                                                Object methodName = callSiteArray[2].call(this.node.get(), "name");
                                                Object methodDescriptor = callSiteArray[3].call(this.node.get(), "descriptor");
                                                if (DefaultTypeTransformation.booleanUnbox(methodName) && DefaultTypeTransformation.booleanUnbox(methodDescriptor)) {
                                                    Object pattern = callSiteArray[4].call(Pattern.class, new GStringImpl(new Object[]{callSiteArray[5].call(Pattern.class, callSiteArray[6].call(methodName, methodDescriptor))}, new String[]{"^.*\\n.*", "[\\s\\S]*?\\n[}|\\n]"}), callSiteArray[7].callGetProperty(Pattern.class));
                                                    Object matcher = callSiteArray[8].call(pattern, this.source.get());
                                                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[9].call(matcher))) {
                                                        Object object = callSiteArray[10].call(this.source.get(), callSiteArray[11].call(matcher, 0), callSiteArray[12].call(matcher, 0));
                                                        ScriptBytecodeAdapter.setProperty(object, null, callSiteArray[13].callGetProperty(callSiteArray[14].callGroovyObjectGetProperty(this)), "text");
                                                    }
                                                }
                                            }
                                            int n = 0;
                                            ScriptBytecodeAdapter.setProperty(n, null, callSiteArray[15].callGetProperty(callSiteArray[16].callGroovyObjectGetProperty(this)), "caretPosition");
                                            return n;
                                        }

                                        public Object getSource() {
                                            CallSite[] callSiteArray = _closure38.$getCallSiteArray();
                                            return this.source.get();
                                        }

                                        public Boolean getShowOnlyMethodCode() {
                                            CallSite[] callSiteArray = _closure38.$getCallSiteArray();
                                            return (Boolean)ScriptBytecodeAdapter.castToType(this.showOnlyMethodCode.get(), Boolean.class);
                                        }

                                        public TreeNode getNode() {
                                            CallSite[] callSiteArray = _closure38.$getCallSiteArray();
                                            return (TreeNode)ScriptBytecodeAdapter.castToType(this.node.get(), TreeNode.class);
                                        }

                                        public Object doCall() {
                                            CallSite[] callSiteArray = _closure38.$getCallSiteArray();
                                            return this.doCall(null);
                                        }

                                        protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                            if (this.getClass() != _closure38.class) {
                                                return ScriptBytecodeAdapter.initMetaClass(this);
                                            }
                                            ClassInfo classInfo = $staticClassInfo;
                                            if (classInfo == null) {
                                                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                            }
                                            return classInfo.getMetaClass();
                                        }

                                        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                            stringArray[0] = "textEditor";
                                            stringArray[1] = "bytecodeView";
                                            stringArray[2] = "getPropertyValue";
                                            stringArray[3] = "getPropertyValue";
                                            stringArray[4] = "compile";
                                            stringArray[5] = "quote";
                                            stringArray[6] = "plus";
                                            stringArray[7] = "MULTILINE";
                                            stringArray[8] = "matcher";
                                            stringArray[9] = "find";
                                            stringArray[10] = "substring";
                                            stringArray[11] = "start";
                                            stringArray[12] = "end";
                                            stringArray[13] = "textEditor";
                                            stringArray[14] = "bytecodeView";
                                            stringArray[15] = "textEditor";
                                            stringArray[16] = "bytecodeView";
                                        }

                                        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                            String[] stringArray = new String[17];
                                            _closure38.$createCallSiteArray_1(stringArray);
                                            return new CallSiteArray(_closure38.class, stringArray);
                                        }

                                        private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                            CallSiteArray callSiteArray;
                                            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                                callSiteArray = _closure38.$createCallSiteArray();
                                                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                            }
                                            return callSiteArray.array;
                                        }
                                    }
                                    return callSiteArray[10].call(callSiteArray[11].callGroovyObjectGetProperty(this), new _closure38(this, this.getThisObject(), source, this.showOnlyMethodCode, this.node));
                                }
                                public class _closure39
                                extends Closure
                                implements GeneratedClosure {
                                    private static /* synthetic */ ClassInfo $staticClassInfo;
                                    public static transient /* synthetic */ boolean __$stMC;
                                    private static /* synthetic */ SoftReference $callSiteArray;

                                    public _closure39(Object _outerInstance, Object _thisObject) {
                                        CallSite[] callSiteArray = _closure39.$getCallSiteArray();
                                        super(_outerInstance, _thisObject);
                                    }

                                    public Object doCall(Object it) {
                                        CallSite[] callSiteArray = _closure39.$getCallSiteArray();
                                        String string = "// No bytecode available at this phase";
                                        ScriptBytecodeAdapter.setProperty(string, null, callSiteArray[0].callGetProperty(callSiteArray[1].callGroovyObjectGetProperty(this)), "text");
                                        return string;
                                    }

                                    public Object doCall() {
                                        CallSite[] callSiteArray = _closure39.$getCallSiteArray();
                                        return this.doCall(null);
                                    }

                                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                        if (this.getClass() != _closure39.class) {
                                            return ScriptBytecodeAdapter.initMetaClass(this);
                                        }
                                        ClassInfo classInfo = $staticClassInfo;
                                        if (classInfo == null) {
                                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                        }
                                        return classInfo.getMetaClass();
                                    }

                                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                        stringArray[0] = "textEditor";
                                        stringArray[1] = "bytecodeView";
                                    }

                                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                        String[] stringArray = new String[2];
                                        _closure39.$createCallSiteArray_1(stringArray);
                                        return new CallSiteArray(_closure39.class, stringArray);
                                    }

                                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                        CallSiteArray callSiteArray;
                                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                            callSiteArray = _closure39.$createCallSiteArray();
                                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                        }
                                        return callSiteArray.array;
                                    }
                                }
                                return callSiteArray[12].call(callSiteArray[13].callGroovyObjectGetProperty(this), new _closure39(this, this.getThisObject()));
                            }

                            public Boolean getShowOnlyMethodCode() {
                                CallSite[] callSiteArray = _closure37.$getCallSiteArray();
                                return (Boolean)ScriptBytecodeAdapter.castToType(this.showOnlyMethodCode.get(), Boolean.class);
                            }

                            public TreeNode getNode() {
                                CallSite[] callSiteArray = _closure37.$getCallSiteArray();
                                return (TreeNode)ScriptBytecodeAdapter.castToType(this.node.get(), TreeNode.class);
                            }

                            public Object doCall() {
                                CallSite[] callSiteArray = _closure37.$getCallSiteArray();
                                return this.doCall(null);
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _closure37.class) {
                                    return ScriptBytecodeAdapter.initMetaClass(this);
                                }
                                ClassInfo classInfo = $staticClassInfo;
                                if (classInfo == null) {
                                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                }
                                return classInfo.getMetaClass();
                            }

                            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                stringArray[0] = "getPropertyValue";
                                stringArray[1] = "getPropertyValue";
                                stringArray[2] = "getBytecode";
                                stringArray[3] = "classLoader";
                                stringArray[4] = "<$constructor$>";
                                stringArray[5] = "<$constructor$>";
                                stringArray[6] = "<$constructor$>";
                                stringArray[7] = "<$constructor$>";
                                stringArray[8] = "accept";
                                stringArray[9] = "toString";
                                stringArray[10] = "doLater";
                                stringArray[11] = "swing";
                                stringArray[12] = "doLater";
                                stringArray[13] = "swing";
                            }

                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                String[] stringArray = new String[14];
                                _closure37.$createCallSiteArray_1(stringArray);
                                return new CallSiteArray(_closure37.class, stringArray);
                            }

                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                CallSiteArray callSiteArray;
                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                    callSiteArray = _closure37.$createCallSiteArray();
                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                }
                                return callSiteArray.array;
                            }
                        }
                        callSiteArray[43].call(callSiteArray[44].callGroovyObjectGetProperty(this), new _closure37(this, this.getThisObject(), showOnlyMethodCode, node));
                    } else {
                        String string = "";
                        ScriptBytecodeAdapter.setProperty(string, null, callSiteArray[45].callGetProperty(callSiteArray[46].callGroovyObjectGetProperty(this)), "text");
                    }
                }
                return callSiteArray[47].call(callSiteArray[48].callGetProperty(callSiteArray[49].callGroovyObjectGetProperty(this)));
            }

            public Object call(TreeSelectionEvent e) {
                CallSite[] callSiteArray = _run_closure5.$getCallSiteArray();
                return callSiteArray[50].callCurrent((GroovyObject)this, e);
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

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "clear";
                stringArray[1] = "rows";
                stringArray[2] = "model";
                stringArray[3] = "propertyTable";
                stringArray[4] = "lastSelectedPathComponent";
                stringArray[5] = "jTree";
                stringArray[6] = "each";
                stringArray[7] = "properties";
                stringArray[8] = "inputArea";
                stringArray[9] = "rootElement";
                stringArray[10] = "findAll";
                stringArray[11] = "properties";
                stringArray[12] = "inject";
                stringArray[13] = "every";
                stringArray[14] = "startOffset";
                stringArray[15] = "getElement";
                stringArray[16] = "rootElement";
                stringArray[17] = "minus";
                stringArray[18] = "lineNumber";
                stringArray[19] = "setCaretPosition";
                stringArray[20] = "inputArea";
                stringArray[21] = "minus";
                stringArray[22] = "plus";
                stringArray[23] = "columnNumber";
                stringArray[24] = "startOffset";
                stringArray[25] = "getElement";
                stringArray[26] = "rootElement";
                stringArray[27] = "minus";
                stringArray[28] = "lastLineNumber";
                stringArray[29] = "moveCaretPosition";
                stringArray[30] = "inputArea";
                stringArray[31] = "minus";
                stringArray[32] = "plus";
                stringArray[33] = "lastColumnNumber";
                stringArray[34] = "moveCaretPosition";
                stringArray[35] = "inputArea";
                stringArray[36] = "getCaretPosition";
                stringArray[37] = "inputArea";
                stringArray[38] = "classNode";
                stringArray[39] = "methodNode";
                stringArray[40] = "textEditor";
                stringArray[41] = "bytecodeView";
                stringArray[42] = "methodNode";
                stringArray[43] = "doOutside";
                stringArray[44] = "swing";
                stringArray[45] = "textEditor";
                stringArray[46] = "bytecodeView";
                stringArray[47] = "fireTableDataChanged";
                stringArray[48] = "model";
                stringArray[49] = "propertyTable";
                stringArray[50] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[51];
                _run_closure5.$createCallSiteArray_1(stringArray);
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
        callSiteArray[37].call(this.jTree, ScriptBytecodeAdapter.createPojoWrapper((TreeSelectionListener)ScriptBytecodeAdapter.asType(new _run_closure5(this, this), TreeSelectionListener.class), TreeSelectionListener.class));
        ScriptBytecodeAdapter.invokeClosure(this.updateFontSize, new Object[]{callSiteArray[38].callGetProperty(this.prefs)});
        callSiteArray[39].call(this.frame);
        Object object8 = callSiteArray[40].callGetProperty(this.prefs);
        ScriptBytecodeAdapter.setProperty(object8, null, this.frame, "location");
        Object object9 = callSiteArray[41].callGetProperty(this.prefs);
        ScriptBytecodeAdapter.setProperty(object9, null, this.frame, "size");
        Object object10 = callSiteArray[42].callGetProperty(this.prefs);
        ScriptBytecodeAdapter.setProperty(object10, null, this.splitterPane, "dividerLocation");
        Object object11 = callSiteArray[43].callGetProperty(this.prefs);
        ScriptBytecodeAdapter.setProperty(object11, null, this.mainSplitter, "dividerLocation");
        callSiteArray[44].call(this.frame);
        String source = ShortTypeHandling.castToString(callSiteArray[45].call(script2.get()));
        callSiteArray[46].callCurrent(this, callSiteArray[47].callGetProperty(callSiteArray[48].callGetProperty(phasePicker.get())), source);
        callSiteArray[49].callCurrent(this, this.jTree, source, callSiteArray[50].callGetProperty(callSiteArray[51].callGetProperty(phasePicker.get())));
        boolean bl = false;
        ScriptBytecodeAdapter.setProperty(bl, null, this.jTree, "rootVisible");
        boolean bl2 = true;
        ScriptBytecodeAdapter.setProperty(bl2, null, this.jTree, "showsRootHandles");
    }

    public void largerFont(EventObject evt) {
        CallSite[] callSiteArray = AstBrowser.$getCallSiteArray();
        ScriptBytecodeAdapter.invokeClosure(this.updateFontSize, new Object[]{callSiteArray[52].call(callSiteArray[53].callGetProperty(callSiteArray[54].callGetProperty(callSiteArray[55].callGetProperty(this.decompiledSource))), 2)});
    }

    public void smallerFont(EventObject evt) {
        CallSite[] callSiteArray = AstBrowser.$getCallSiteArray();
        ScriptBytecodeAdapter.invokeClosure(this.updateFontSize, new Object[]{callSiteArray[56].call(callSiteArray[57].callGetProperty(callSiteArray[58].callGetProperty(callSiteArray[59].callGetProperty(this.decompiledSource))), 2)});
    }

    public void showAbout(EventObject evt) {
        CallSite[] callSiteArray = AstBrowser.$getCallSiteArray();
        Object pane = callSiteArray[60].call(this.swing);
        callSiteArray[61].call(pane, "An interactive GUI to explore AST capabilities.");
        Object dialog = callSiteArray[62].call(pane, this.frame, "About Groovy AST Browser");
        callSiteArray[63].call(dialog);
    }

    public void showScriptFreeForm(EventObject evt) {
        CallSite[] callSiteArray = AstBrowser.$getCallSiteArray();
        Object object = callSiteArray[64].callGetProperty(callSiteArray[65].callGetProperty(evt));
        this.showScriptFreeForm = DefaultTypeTransformation.booleanUnbox(object);
    }

    public void showScriptClass(EventObject evt) {
        CallSite[] callSiteArray = AstBrowser.$getCallSiteArray();
        Object object = callSiteArray[66].callGetProperty(callSiteArray[67].callGetProperty(evt));
        this.showScriptClass = DefaultTypeTransformation.booleanUnbox(object);
    }

    public void showClosureClasses(EventObject evt) {
        CallSite[] callSiteArray = AstBrowser.$getCallSiteArray();
        Object object = callSiteArray[68].callGetProperty(callSiteArray[69].callGetProperty(evt));
        this.showClosureClasses = DefaultTypeTransformation.booleanUnbox(object);
    }

    public void showTreeView(EventObject evt) {
        boolean bl;
        CallSite[] callSiteArray = AstBrowser.$getCallSiteArray();
        this.showTreeView = bl = !this.showTreeView;
        boolean bl2 = this.showTreeView;
        ScriptBytecodeAdapter.setProperty(bl2, null, this.splitterPane, "visible");
        if (this.showTreeView) {
            int n = 100;
            ScriptBytecodeAdapter.setProperty(n, null, this.mainSplitter, "dividerLocation");
        } else {
            int n = 0;
            ScriptBytecodeAdapter.setProperty(n, null, this.mainSplitter, "dividerLocation");
        }
    }

    /*
     * WARNING - void declaration
     */
    public void decompile(Object phaseId, Object source) {
        void var2_2;
        Reference<Object> phaseId2 = new Reference<Object>(phaseId);
        Reference<void> source2 = new Reference<void>(var2_2);
        CallSite[] callSiteArray = AstBrowser.$getCallSiteArray();
        callSiteArray[70].call(callSiteArray[71].callGetProperty(this.decompiledSource), callSiteArray[72].call(Cursor.class, callSiteArray[73].callGetProperty(Cursor.class)));
        String string = "Loading...";
        ScriptBytecodeAdapter.setProperty(string, null, callSiteArray[74].callGetProperty(this.decompiledSource), "text");
        public class _decompile_closure6
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference source;
            private /* synthetic */ Reference phaseId;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _decompile_closure6(Object _outerInstance, Object _thisObject, Reference source, Reference phaseId) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _decompile_closure6.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.source = reference2 = source;
                this.phaseId = reference = phaseId;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _decompile_closure6.$getCallSiteArray();
                Reference<String> result = new Reference<String>(ShortTypeHandling.castToString(callSiteArray[0].call(callSiteArray[1].callConstructor(AstNodeToScriptAdapter.class), ArrayUtil.createArray(this.source.get(), this.phaseId.get(), callSiteArray[2].callGroovyObjectGetProperty(this), callSiteArray[3].callGroovyObjectGetProperty(this), callSiteArray[4].callGroovyObjectGetProperty(this)))));
                public class _closure40
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference result;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure40(Object _outerInstance, Object _thisObject, Reference result) {
                        Reference reference;
                        CallSite[] callSiteArray = _closure40.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.result = reference = result;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure40.$getCallSiteArray();
                        Object t = this.result.get();
                        ScriptBytecodeAdapter.setProperty(t, null, callSiteArray[0].callGetProperty(callSiteArray[1].callGroovyObjectGetProperty(this)), "text");
                        callSiteArray[2].call(callSiteArray[3].callGetProperty(callSiteArray[4].callGroovyObjectGetProperty(this)), 0);
                        return callSiteArray[5].call(callSiteArray[6].callGetProperty(callSiteArray[7].callGroovyObjectGetProperty(this)), callSiteArray[8].callGetProperty(Cursor.class));
                    }

                    public String getResult() {
                        CallSite[] callSiteArray = _closure40.$getCallSiteArray();
                        return ShortTypeHandling.castToString(this.result.get());
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure40.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure40.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "textEditor";
                        stringArray[1] = "decompiledSource";
                        stringArray[2] = "setCaretPosition";
                        stringArray[3] = "textEditor";
                        stringArray[4] = "decompiledSource";
                        stringArray[5] = "setCursor";
                        stringArray[6] = "textEditor";
                        stringArray[7] = "decompiledSource";
                        stringArray[8] = "defaultCursor";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[9];
                        _closure40.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_closure40.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure40.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                Object object = callSiteArray[5].call(callSiteArray[6].callGroovyObjectGetProperty(this), new _closure40(this, this.getThisObject(), result));
                try {
                    return object;
                }
                catch (Throwable throwable) {
                    Reference<Throwable> t = new Reference<Throwable>(throwable);
                    public class _closure41
                    extends Closure
                    implements GeneratedClosure {
                        private /* synthetic */ Reference t;
                        private static /* synthetic */ ClassInfo $staticClassInfo;
                        public static transient /* synthetic */ boolean __$stMC;
                        private static /* synthetic */ SoftReference $callSiteArray;

                        public _closure41(Object _outerInstance, Object _thisObject, Reference t) {
                            Reference reference;
                            CallSite[] callSiteArray = _closure41.$getCallSiteArray();
                            super(_outerInstance, _thisObject);
                            this.t = reference = t;
                        }

                        public Object doCall(Object it) {
                            CallSite[] callSiteArray = _closure41.$getCallSiteArray();
                            Object object = callSiteArray[0].call(this.t.get());
                            ScriptBytecodeAdapter.setProperty(object, null, callSiteArray[1].callGetProperty(callSiteArray[2].callGroovyObjectGetProperty(this)), "text");
                            callSiteArray[3].call(callSiteArray[4].callGetProperty(callSiteArray[5].callGroovyObjectGetProperty(this)), 0);
                            return callSiteArray[6].call(callSiteArray[7].callGetProperty(callSiteArray[8].callGroovyObjectGetProperty(this)), callSiteArray[9].callGetProperty(Cursor.class));
                        }

                        public Throwable getT() {
                            CallSite[] callSiteArray = _closure41.$getCallSiteArray();
                            return (Throwable)ScriptBytecodeAdapter.castToType(this.t.get(), Throwable.class);
                        }

                        public Object doCall() {
                            CallSite[] callSiteArray = _closure41.$getCallSiteArray();
                            return this.doCall(null);
                        }

                        protected /* synthetic */ MetaClass $getStaticMetaClass() {
                            if (this.getClass() != _closure41.class) {
                                return ScriptBytecodeAdapter.initMetaClass(this);
                            }
                            ClassInfo classInfo = $staticClassInfo;
                            if (classInfo == null) {
                                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                            }
                            return classInfo.getMetaClass();
                        }

                        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                            stringArray[0] = "getMessage";
                            stringArray[1] = "textEditor";
                            stringArray[2] = "decompiledSource";
                            stringArray[3] = "setCaretPosition";
                            stringArray[4] = "textEditor";
                            stringArray[5] = "decompiledSource";
                            stringArray[6] = "setCursor";
                            stringArray[7] = "textEditor";
                            stringArray[8] = "decompiledSource";
                            stringArray[9] = "defaultCursor";
                        }

                        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                            String[] stringArray = new String[10];
                            _closure41.$createCallSiteArray_1(stringArray);
                            return new CallSiteArray(_closure41.class, stringArray);
                        }

                        private static /* synthetic */ CallSite[] $getCallSiteArray() {
                            CallSiteArray callSiteArray;
                            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                callSiteArray = _closure41.$createCallSiteArray();
                                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                            }
                            return callSiteArray.array;
                        }
                    }
                    callSiteArray[7].call(callSiteArray[8].callGroovyObjectGetProperty(this), new _closure41(this, this.getThisObject(), t));
                    throw t.get();
                }
            }

            public Object getSource() {
                CallSite[] callSiteArray = _decompile_closure6.$getCallSiteArray();
                return this.source.get();
            }

            public Object getPhaseId() {
                CallSite[] callSiteArray = _decompile_closure6.$getCallSiteArray();
                return this.phaseId.get();
            }

            public Object doCall() {
                CallSite[] callSiteArray = _decompile_closure6.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _decompile_closure6.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "compileToScript";
                stringArray[1] = "<$constructor$>";
                stringArray[2] = "classLoader";
                stringArray[3] = "showScriptFreeForm";
                stringArray[4] = "showScriptClass";
                stringArray[5] = "doLater";
                stringArray[6] = "swing";
                stringArray[7] = "doLater";
                stringArray[8] = "swing";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[9];
                _decompile_closure6.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_decompile_closure6.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _decompile_closure6.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[75].call((Object)this.swing, new _decompile_closure6(this, this, source2, phaseId2));
    }

    /*
     * WARNING - void declaration
     */
    public void compile(Object jTree, String script, int compilePhase) {
        void var2_2;
        Reference<Object> jTree2 = new Reference<Object>(jTree);
        Reference<void> script2 = new Reference<void>(var2_2);
        Reference<Integer> compilePhase2 = new Reference<Integer>(compilePhase);
        CallSite[] callSiteArray = AstBrowser.$getCallSiteArray();
        callSiteArray[76].call(jTree2.get(), callSiteArray[77].call(Cursor.class, callSiteArray[78].callGetProperty(Cursor.class)));
        Reference<Object> model = new Reference<Object>(callSiteArray[79].callGetProperty(jTree2.get()));
        public class _compile_closure7
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference model;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _compile_closure7(Object _outerInstance, Object _thisObject, Reference model) {
                Reference reference;
                CallSite[] callSiteArray = _compile_closure7.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.model = reference = model;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _compile_closure7.$getCallSiteArray();
                Object root = callSiteArray[0].call(this.model.get());
                callSiteArray[1].call(root);
                callSiteArray[2].call(root, callSiteArray[3].callConstructor(DefaultMutableTreeNode.class, callSiteArray[4].callConstructor(DefaultMutableTreeNode.class, "Loading...")));
                return callSiteArray[5].call(this.model.get(), root);
            }

            public Object getModel() {
                CallSite[] callSiteArray = _compile_closure7.$getCallSiteArray();
                return this.model.get();
            }

            public Object doCall() {
                CallSite[] callSiteArray = _compile_closure7.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _compile_closure7.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "getRoot";
                stringArray[1] = "removeAllChildren";
                stringArray[2] = "add";
                stringArray[3] = "<$constructor$>";
                stringArray[4] = "<$constructor$>";
                stringArray[5] = "reload";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[6];
                _compile_closure7.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_compile_closure7.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _compile_closure7.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[80].call((Object)this.swing, new _compile_closure7(this, this, model));
        public class _compile_closure8
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference script;
            private /* synthetic */ Reference compilePhase;
            private /* synthetic */ Reference model;
            private /* synthetic */ Reference jTree;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _compile_closure8(Object _outerInstance, Object _thisObject, Reference script, Reference compilePhase, Reference model, Reference jTree) {
                Reference reference;
                Reference reference2;
                Reference reference3;
                Reference reference4;
                CallSite[] callSiteArray = _compile_closure8.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.script = reference4 = script;
                this.compilePhase = reference3 = compilePhase;
                this.model = reference2 = model;
                this.jTree = reference = jTree;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _compile_closure8.$getCallSiteArray();
                Object nodeMaker = callSiteArray[0].callConstructor(SwingTreeNodeMaker.class);
                Object adapter = callSiteArray[1].callConstructor((Object)ScriptToTreeNodeAdapter.class, ArrayUtil.createArray(callSiteArray[2].callGroovyObjectGetProperty(this), callSiteArray[3].callGroovyObjectGetProperty(this), callSiteArray[4].callGroovyObjectGetProperty(this), callSiteArray[5].callGroovyObjectGetProperty(this), nodeMaker));
                callSiteArray[6].call(callSiteArray[7].callGroovyObjectGetProperty(this));
                Reference<Object> result = new Reference<Object>(callSiteArray[8].call(adapter, this.script.get(), this.compilePhase.get()));
                public class _closure42
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference model;
                    private /* synthetic */ Reference result;
                    private /* synthetic */ Reference jTree;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure42(Object _outerInstance, Object _thisObject, Reference model, Reference result, Reference jTree) {
                        Reference reference;
                        Reference reference2;
                        Reference reference3;
                        CallSite[] callSiteArray = _closure42.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.model = reference3 = model;
                        this.result = reference2 = result;
                        this.jTree = reference = jTree;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure42.$getCallSiteArray();
                        callSiteArray[0].call(this.model.get(), this.result.get());
                        callSiteArray[1].call(this.model.get());
                        return callSiteArray[2].call(this.jTree.get(), callSiteArray[3].callGetProperty(Cursor.class));
                    }

                    public Object getModel() {
                        CallSite[] callSiteArray = _closure42.$getCallSiteArray();
                        return this.model.get();
                    }

                    public Object getResult() {
                        CallSite[] callSiteArray = _closure42.$getCallSiteArray();
                        return this.result.get();
                    }

                    public Object getjTree() {
                        CallSite[] callSiteArray = _closure42.$getCallSiteArray();
                        return this.jTree.get();
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure42.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure42.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "setRoot";
                        stringArray[1] = "reload";
                        stringArray[2] = "setCursor";
                        stringArray[3] = "defaultCursor";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[4];
                        _closure42.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_closure42.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure42.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                Object object = callSiteArray[9].call(callSiteArray[10].callGroovyObjectGetProperty(this), new _closure42(this, this.getThisObject(), this.model, result, this.jTree));
                try {
                    return object;
                }
                catch (Throwable t) {
                    public class _closure43
                    extends Closure
                    implements GeneratedClosure {
                        private /* synthetic */ Reference jTree;
                        private static /* synthetic */ ClassInfo $staticClassInfo;
                        public static transient /* synthetic */ boolean __$stMC;
                        private static /* synthetic */ SoftReference $callSiteArray;

                        public _closure43(Object _outerInstance, Object _thisObject, Reference jTree) {
                            Reference reference;
                            CallSite[] callSiteArray = _closure43.$getCallSiteArray();
                            super(_outerInstance, _thisObject);
                            this.jTree = reference = jTree;
                        }

                        public Object doCall(Object it) {
                            CallSite[] callSiteArray = _closure43.$getCallSiteArray();
                            return callSiteArray[0].call(this.jTree.get(), callSiteArray[1].callGetProperty(Cursor.class));
                        }

                        public Object getjTree() {
                            CallSite[] callSiteArray = _closure43.$getCallSiteArray();
                            return this.jTree.get();
                        }

                        public Object doCall() {
                            CallSite[] callSiteArray = _closure43.$getCallSiteArray();
                            return this.doCall(null);
                        }

                        protected /* synthetic */ MetaClass $getStaticMetaClass() {
                            if (this.getClass() != _closure43.class) {
                                return ScriptBytecodeAdapter.initMetaClass(this);
                            }
                            ClassInfo classInfo = $staticClassInfo;
                            if (classInfo == null) {
                                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                            }
                            return classInfo.getMetaClass();
                        }

                        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                            stringArray[0] = "setCursor";
                            stringArray[1] = "defaultCursor";
                        }

                        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                            String[] stringArray = new String[2];
                            _closure43.$createCallSiteArray_1(stringArray);
                            return new CallSiteArray(_closure43.class, stringArray);
                        }

                        private static /* synthetic */ CallSite[] $getCallSiteArray() {
                            CallSiteArray callSiteArray;
                            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                callSiteArray = _closure43.$createCallSiteArray();
                                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                            }
                            return callSiteArray.array;
                        }
                    }
                    callSiteArray[11].call(callSiteArray[12].callGroovyObjectGetProperty(this), new _closure43(this, this.getThisObject(), this.jTree));
                    throw t;
                }
            }

            public String getScript() {
                CallSite[] callSiteArray = _compile_closure8.$getCallSiteArray();
                return ShortTypeHandling.castToString(this.script.get());
            }

            public int getCompilePhase() {
                CallSite[] callSiteArray = _compile_closure8.$getCallSiteArray();
                return DefaultTypeTransformation.intUnbox(this.compilePhase.get());
            }

            public Object getModel() {
                CallSite[] callSiteArray = _compile_closure8.$getCallSiteArray();
                return this.model.get();
            }

            public Object getjTree() {
                CallSite[] callSiteArray = _compile_closure8.$getCallSiteArray();
                return this.jTree.get();
            }

            public Object doCall() {
                CallSite[] callSiteArray = _compile_closure8.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _compile_closure8.class) {
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
                stringArray[1] = "<$constructor$>";
                stringArray[2] = "classLoader";
                stringArray[3] = "showScriptFreeForm";
                stringArray[4] = "showScriptClass";
                stringArray[5] = "showClosureClasses";
                stringArray[6] = "clearBytecodeTable";
                stringArray[7] = "classLoader";
                stringArray[8] = "compile";
                stringArray[9] = "doLater";
                stringArray[10] = "swing";
                stringArray[11] = "doLater";
                stringArray[12] = "swing";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[13];
                _compile_closure8.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_compile_closure8.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _compile_closure8.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[81].call((Object)this.swing, new _compile_closure8(this, this, script2, compilePhase2, model, jTree2));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != AstBrowser.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public void largerFont() {
        CallSite[] callSiteArray = AstBrowser.$getCallSiteArray();
        this.largerFont(null);
    }

    public void smallerFont() {
        CallSite[] callSiteArray = AstBrowser.$getCallSiteArray();
        this.smallerFont(null);
    }

    public void showTreeView() {
        CallSite[] callSiteArray = AstBrowser.$getCallSiteArray();
        this.showTreeView(null);
    }

    @Override
    public /* synthetic */ MetaClass getMetaClass() {
        MetaClass metaClass = this.metaClass;
        if (metaClass != null) {
            return metaClass;
        }
        this.metaClass = this.$getStaticMetaClass();
        return this.metaClass;
    }

    @Override
    public /* synthetic */ void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    public /* synthetic */ Object invokeMethod(String string, Object object) {
        return this.getMetaClass().invokeMethod((Object)this, string, object);
    }

    @Override
    public /* synthetic */ Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    public /* synthetic */ void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }

    public boolean getShowScriptFreeForm() {
        return this.showScriptFreeForm;
    }

    public boolean isShowScriptFreeForm() {
        return this.showScriptFreeForm;
    }

    public void setShowScriptFreeForm(boolean bl) {
        this.showScriptFreeForm = bl;
    }

    public boolean getShowScriptClass() {
        return this.showScriptClass;
    }

    public boolean isShowScriptClass() {
        return this.showScriptClass;
    }

    public void setShowScriptClass(boolean bl) {
        this.showScriptClass = bl;
    }

    public boolean getShowClosureClasses() {
        return this.showClosureClasses;
    }

    public boolean isShowClosureClasses() {
        return this.showClosureClasses;
    }

    public void setShowClosureClasses(boolean bl) {
        this.showClosureClasses = bl;
    }

    public boolean getShowTreeView() {
        return this.showTreeView;
    }

    public boolean isShowTreeView() {
        return this.showTreeView;
    }

    public void setShowTreeView(boolean bl) {
        this.showTreeView = bl;
    }

    public GeneratedBytecodeAwareGroovyClassLoader getClassLoader() {
        return this.classLoader;
    }

    public void setClassLoader(GeneratedBytecodeAwareGroovyClassLoader generatedBytecodeAwareGroovyClassLoader) {
        this.classLoader = generatedBytecodeAwareGroovyClassLoader;
    }

    public Object getPrefs() {
        return this.prefs;
    }

    public void setPrefs(Object object) {
        this.prefs = object;
    }

    public SwingBuilder getSwing() {
        return this.swing;
    }

    public void setSwing(SwingBuilder swingBuilder) {
        this.swing = swingBuilder;
    }

    public Object getFrame() {
        return this.frame;
    }

    public void setFrame(Object object) {
        this.frame = object;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "println";
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "getAt";
        stringArray[5] = "exists";
        stringArray[6] = "println";
        stringArray[7] = "setLookAndFeel";
        stringArray[8] = "getSystemLookAndFeelClassName";
        stringArray[9] = "run";
        stringArray[10] = "<$constructor$>";
        stringArray[11] = "<$constructor$>";
        stringArray[12] = "path";
        stringArray[13] = "run";
        stringArray[14] = "<$constructor$>";
        stringArray[15] = "showScriptFreeForm";
        stringArray[16] = "showScriptClass";
        stringArray[17] = "showClosureClasses";
        stringArray[18] = "showTreeView";
        stringArray[19] = "frame";
        stringArray[20] = "plus";
        stringArray[21] = "frameLocation";
        stringArray[22] = "frameSize";
        stringArray[23] = "image";
        stringArray[24] = "imageIcon";
        stringArray[25] = "ICON_PATH";
        stringArray[26] = "DISPOSE_ON_CLOSE";
        stringArray[27] = "textEditor";
        stringArray[28] = "clear";
        stringArray[29] = "rows";
        stringArray[30] = "model";
        stringArray[31] = "setLeafIcon";
        stringArray[32] = "cellRenderer";
        stringArray[33] = "imageIcon";
        stringArray[34] = "NODE_ICON_PATH";
        stringArray[35] = "SINGLE_TREE_SELECTION";
        stringArray[36] = "selectionModel";
        stringArray[37] = "addTreeSelectionListener";
        stringArray[38] = "decompiledSourceFontSize";
        stringArray[39] = "pack";
        stringArray[40] = "frameLocation";
        stringArray[41] = "frameSize";
        stringArray[42] = "verticalDividerLocation";
        stringArray[43] = "horizontalDividerLocation";
        stringArray[44] = "show";
        stringArray[45] = "call";
        stringArray[46] = "decompile";
        stringArray[47] = "phaseId";
        stringArray[48] = "selectedItem";
        stringArray[49] = "compile";
        stringArray[50] = "phaseId";
        stringArray[51] = "selectedItem";
        stringArray[52] = "plus";
        stringArray[53] = "size";
        stringArray[54] = "font";
        stringArray[55] = "textEditor";
        stringArray[56] = "minus";
        stringArray[57] = "size";
        stringArray[58] = "font";
        stringArray[59] = "textEditor";
        stringArray[60] = "optionPane";
        stringArray[61] = "setMessage";
        stringArray[62] = "createDialog";
        stringArray[63] = "show";
        stringArray[64] = "selected";
        stringArray[65] = "source";
        stringArray[66] = "selected";
        stringArray[67] = "source";
        stringArray[68] = "selected";
        stringArray[69] = "source";
        stringArray[70] = "setCursor";
        stringArray[71] = "textEditor";
        stringArray[72] = "getPredefinedCursor";
        stringArray[73] = "WAIT_CURSOR";
        stringArray[74] = "textEditor";
        stringArray[75] = "doOutside";
        stringArray[76] = "setCursor";
        stringArray[77] = "getPredefinedCursor";
        stringArray[78] = "WAIT_CURSOR";
        stringArray[79] = "model";
        stringArray[80] = "edt";
        stringArray[81] = "doOutside";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[82];
        AstBrowser.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(AstBrowser.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = AstBrowser.$createCallSiteArray();
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

        public Object doCall(Object newFontSize) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            if (ScriptBytecodeAdapter.compareGreaterThan(newFontSize, 40)) {
                int n = 40;
                newFontSize = n;
            } else if (ScriptBytecodeAdapter.compareLessThan(newFontSize, 4)) {
                int n = 4;
                newFontSize = n;
            }
            Object object = newFontSize;
            ScriptBytecodeAdapter.setProperty(object, null, callSiteArray[0].callGroovyObjectGetProperty(this), "decompiledSourceFontSize");
            Object newDecompilerFont = callSiteArray[1].callConstructor(Font.class, callSiteArray[2].callGetProperty(callSiteArray[3].callGetProperty(callSiteArray[4].callGetProperty(callSiteArray[5].callGroovyObjectGetProperty(this)))), callSiteArray[6].callGetProperty(callSiteArray[7].callGetProperty(callSiteArray[8].callGetProperty(callSiteArray[9].callGroovyObjectGetProperty(this)))), newFontSize);
            Object object2 = newDecompilerFont;
            ScriptBytecodeAdapter.setProperty(object2, null, callSiteArray[10].callGetProperty(callSiteArray[11].callGroovyObjectGetProperty(this)), "font");
            Object newFont = callSiteArray[12].callConstructor(Font.class, callSiteArray[13].callGetProperty(callSiteArray[14].callGetProperty(callSiteArray[15].callGetProperty(callSiteArray[16].callGroovyObjectGetProperty(this)))), callSiteArray[17].callGetProperty(callSiteArray[18].callGetProperty(callSiteArray[19].callGetProperty(callSiteArray[20].callGroovyObjectGetProperty(this)))), newFontSize);
            Object object3 = newFont;
            ScriptBytecodeAdapter.setProperty(object3, null, callSiteArray[21].callGetProperty(callSiteArray[22].callGroovyObjectGetProperty(this)), "font");
            callSiteArray[23].call(callSiteArray[24].callGetProperty(callSiteArray[25].callGroovyObjectGetProperty(this)), callSiteArray[26].callGetProperty(callSiteArray[27].callGetProperty(callSiteArray[28].callGroovyObjectGetProperty(this))));
            Object object4 = newFont;
            ScriptBytecodeAdapter.setProperty(object4, null, callSiteArray[29].callGetProperty(callSiteArray[30].callGroovyObjectGetProperty(this)), "font");
            Object object5 = newFont;
            ScriptBytecodeAdapter.setProperty(object5, null, callSiteArray[31].callGroovyObjectGetProperty(this), "font");
            Object object6 = callSiteArray[32].call(newFontSize, 2);
            ScriptBytecodeAdapter.setProperty(object6, null, callSiteArray[33].callGroovyObjectGetProperty(this), "rowHeight");
            return object6;
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
            stringArray[0] = "prefs";
            stringArray[1] = "<$constructor$>";
            stringArray[2] = "name";
            stringArray[3] = "font";
            stringArray[4] = "textEditor";
            stringArray[5] = "decompiledSource";
            stringArray[6] = "style";
            stringArray[7] = "font";
            stringArray[8] = "textEditor";
            stringArray[9] = "decompiledSource";
            stringArray[10] = "textEditor";
            stringArray[11] = "decompiledSource";
            stringArray[12] = "<$constructor$>";
            stringArray[13] = "name";
            stringArray[14] = "font";
            stringArray[15] = "cellRenderer";
            stringArray[16] = "jTree";
            stringArray[17] = "style";
            stringArray[18] = "font";
            stringArray[19] = "cellRenderer";
            stringArray[20] = "jTree";
            stringArray[21] = "cellRenderer";
            stringArray[22] = "jTree";
            stringArray[23] = "reload";
            stringArray[24] = "model";
            stringArray[25] = "jTree";
            stringArray[26] = "root";
            stringArray[27] = "model";
            stringArray[28] = "jTree";
            stringArray[29] = "tableHeader";
            stringArray[30] = "propertyTable";
            stringArray[31] = "propertyTable";
            stringArray[32] = "plus";
            stringArray[33] = "propertyTable";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[34];
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

