/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui.view;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.lang.Script;
import groovy.ui.Console;
import groovy.ui.ConsoleTextEditor;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;
import java.math.BigDecimal;
import java.util.prefs.Preferences;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class BasicContentPane
extends Script {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static final /* synthetic */ BigDecimal $const$0;
    private static final /* synthetic */ BigDecimal $const$1;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public BasicContentPane() {
        CallSite[] callSiteArray = BasicContentPane.$getCallSiteArray();
    }

    public BasicContentPane(Binding context) {
        CallSite[] callSiteArray = BasicContentPane.$getCallSiteArray();
        super(context);
    }

    public static void main(String ... args) {
        CallSite[] callSiteArray = BasicContentPane.$getCallSiteArray();
        callSiteArray[0].call(InvokerHelper.class, BasicContentPane.class, args);
    }

    @Override
    public Object run() {
        CallSite[] callSiteArray = BasicContentPane.$getCallSiteArray();
        Reference<Object> prefs = new Reference<Object>(callSiteArray[1].call(Preferences.class, Console.class));
        Object detachedOutputFlag = callSiteArray[2].call(prefs.get(), "detachedOutput", false);
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
                Object object = callSiteArray[0].callCurrent(this);
                ScriptBytecodeAdapter.setGroovyObjectProperty(object, _run_closure1.class, this, "blank");
                Dimension dimension = (Dimension)ScriptBytecodeAdapter.asType(ScriptBytecodeAdapter.createList(new Object[]{0, 0}), Dimension.class);
                ScriptBytecodeAdapter.setProperty(dimension, null, callSiteArray[1].callGroovyObjectGetProperty(this), "preferredSize");
                return dimension;
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
                stringArray[0] = "glue";
                stringArray[1] = "blank";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
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
        Object object = callSiteArray[3].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"visible", false, "defaultCloseOperation", callSiteArray[4].callGetProperty(WindowConstants.class)}), new _run_closure1(this, this));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object, BasicContentPane.class, this, "outputWindow");
        public class _run_closure2
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference prefs;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _run_closure2(Object _outerInstance, Object _thisObject, Reference prefs) {
                Reference reference;
                CallSite[] callSiteArray = _run_closure2.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.prefs = reference = prefs;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _run_closure2.$getCallSiteArray();
                Object object = callSiteArray[0].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"border", callSiteArray[1].callCurrent((GroovyObject)this, 0)}), callSiteArray[2].callConstructor(ConsoleTextEditor.class));
                ScriptBytecodeAdapter.setGroovyObjectProperty(object, _run_closure2.class, this, "inputEditor");
                return callSiteArray[3].callCurrent((GroovyObject)this, this.prefs.get());
            }

            public Object getPrefs() {
                CallSite[] callSiteArray = _run_closure2.$getCallSiteArray();
                return this.prefs.get();
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
                stringArray[0] = "widget";
                stringArray[1] = "emptyBorder";
                stringArray[2] = "<$constructor$>";
                stringArray[3] = "buildOutputArea";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[4];
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
        Object object2 = callSiteArray[5].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"resizeWeight", $const$0, "orientation", callSiteArray[6].callGetProperty(JSplitPane.class)}), new _run_closure2(this, this, prefs));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object2, BasicContentPane.class, this, "splitPane");
        Object object3 = callSiteArray[7].callGetProperty(callSiteArray[8].callGroovyObjectGetProperty(this));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object3, BasicContentPane.class, this, "inputArea");
        public class _run_closure3
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference prefs;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _run_closure3(Object _outerInstance, Object _thisObject, Reference prefs) {
                Reference reference;
                CallSite[] callSiteArray = _run_closure3.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.prefs = reference = prefs;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _run_closure3.$getCallSiteArray();
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
                        callSiteArray[0].callCurrent((GroovyObject)this, callSiteArray[1].callGroovyObjectGetProperty(this));
                        callSiteArray[2].callCurrent((GroovyObject)this, callSiteArray[3].callGroovyObjectGetProperty(this));
                        return callSiteArray[4].callCurrent((GroovyObject)this, callSiteArray[5].callGroovyObjectGetProperty(this));
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
                        stringArray[0] = "action";
                        stringArray[1] = "runAction";
                        stringArray[2] = "action";
                        stringArray[3] = "runSelectionAction";
                        stringArray[4] = "action";
                        stringArray[5] = "showOutputWindowAction";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[6];
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
                callSiteArray[0].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "inputArea", "font", callSiteArray[1].callConstructor(Font.class, "Monospaced", callSiteArray[2].callGetProperty(Font.class), callSiteArray[3].call(this.prefs.get(), "fontSize", 12)), "border", callSiteArray[4].callCurrent((GroovyObject)this, 4)}), callSiteArray[5].callGroovyObjectGetProperty(this), new _closure7(this, this.getThisObject()));
                public class _closure8
                extends Closure
                implements GeneratedClosure {
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure8(Object _outerInstance, Object _thisObject) {
                        CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                        callSiteArray[0].callCurrent((GroovyObject)this, callSiteArray[1].callGroovyObjectGetProperty(this));
                        callSiteArray[2].callCurrent((GroovyObject)this, callSiteArray[3].callGroovyObjectGetProperty(this));
                        callSiteArray[4].callCurrent((GroovyObject)this, callSiteArray[5].callGroovyObjectGetProperty(this));
                        return callSiteArray[6].callCurrent((GroovyObject)this, callSiteArray[7].callGroovyObjectGetProperty(this));
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure8.class) {
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
                        stringArray[1] = "hideOutputWindowAction1";
                        stringArray[2] = "action";
                        stringArray[3] = "hideOutputWindowAction2";
                        stringArray[4] = "action";
                        stringArray[5] = "hideOutputWindowAction3";
                        stringArray[6] = "action";
                        stringArray[7] = "hideOutputWindowAction4";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[8];
                        _closure8.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_closure8.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure8.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                return callSiteArray[6].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "outputArea"}), callSiteArray[7].callGroovyObjectGetProperty(this), new _closure8(this, this.getThisObject()));
            }

            public Object getPrefs() {
                CallSite[] callSiteArray = _run_closure3.$getCallSiteArray();
                return this.prefs.get();
            }

            public Object doCall() {
                CallSite[] callSiteArray = _run_closure3.$getCallSiteArray();
                return this.doCall(null);
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
                stringArray[0] = "container";
                stringArray[1] = "<$constructor$>";
                stringArray[2] = "PLAIN";
                stringArray[3] = "getInt";
                stringArray[4] = "emptyBorder";
                stringArray[5] = "inputArea";
                stringArray[6] = "container";
                stringArray[7] = "outputArea";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[8];
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
        callSiteArray[9].callCurrent((GroovyObject)this, new _run_closure3(this, this, prefs));
        Object object4 = callSiteArray[10].callConstructor(Font.class, "Monospaced", callSiteArray[11].callGetProperty(callSiteArray[12].callGetProperty(callSiteArray[13].callGroovyObjectGetProperty(this))), callSiteArray[14].callGetProperty(callSiteArray[15].callGetProperty(callSiteArray[16].callGroovyObjectGetProperty(this))));
        ScriptBytecodeAdapter.setProperty(object4, null, callSiteArray[17].callGroovyObjectGetProperty(this), "font");
        StyledDocument doc = (StyledDocument)ScriptBytecodeAdapter.castToType(callSiteArray[18].callGetProperty(callSiteArray[19].callGroovyObjectGetProperty(this)), StyledDocument.class);
        Style defStyle = (Style)ScriptBytecodeAdapter.castToType(callSiteArray[20].call(callSiteArray[21].callGetProperty(StyleContext.class), callSiteArray[22].callGetProperty(StyleContext.class)), Style.class);
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

            public Object doCall(Style style, Object values) {
                Reference<Style> style2 = new Reference<Style>(style);
                CallSite[] callSiteArray = _run_closure4.$getCallSiteArray();
                public class _closure9
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference style;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure9(Object _outerInstance, Object _thisObject, Reference style) {
                        Reference reference;
                        CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.style = reference = style;
                    }

                    public Object doCall(Object k, Object v) {
                        CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                        return callSiteArray[0].call(this.style.get(), k, v);
                    }

                    public Object call(Object k, Object v) {
                        CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                        return callSiteArray[1].callCurrent(this, k, v);
                    }

                    public Style getStyle() {
                        CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                        return (Style)ScriptBytecodeAdapter.castToType(this.style.get(), Style.class);
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
                        stringArray[0] = "addAttribute";
                        stringArray[1] = "doCall";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[2];
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
                return callSiteArray[0].call(values, new _closure9(this, this.getThisObject(), style2));
            }

            public Object call(Style style, Object values) {
                Reference<Style> style2 = new Reference<Style>(style);
                CallSite[] callSiteArray = _run_closure4.$getCallSiteArray();
                return callSiteArray[1].callCurrent(this, style2.get(), values);
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
                stringArray[0] = "each";
                stringArray[1] = "doCall";
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
        Reference<_run_closure4> applyStyle = new Reference<_run_closure4>(new _run_closure4(this, this));
        Style regular = (Style)ScriptBytecodeAdapter.castToType(callSiteArray[23].call(doc, "regular", defStyle), Style.class);
        callSiteArray[24].call(applyStyle.get(), regular, callSiteArray[25].callGetProperty(callSiteArray[26].callGroovyObjectGetProperty(this)));
        Object object5 = callSiteArray[27].call(doc, "prompt", regular);
        ScriptBytecodeAdapter.setGroovyObjectProperty(object5, BasicContentPane.class, this, "promptStyle");
        callSiteArray[28].call(applyStyle.get(), callSiteArray[29].callGroovyObjectGetProperty(this), callSiteArray[30].callGetProperty(callSiteArray[31].callGroovyObjectGetProperty(this)));
        Object object6 = callSiteArray[32].call(doc, "command", regular);
        ScriptBytecodeAdapter.setGroovyObjectProperty(object6, BasicContentPane.class, this, "commandStyle");
        callSiteArray[33].call(applyStyle.get(), callSiteArray[34].callGroovyObjectGetProperty(this), callSiteArray[35].callGetProperty(callSiteArray[36].callGroovyObjectGetProperty(this)));
        Object object7 = callSiteArray[37].call(doc, "output", regular);
        ScriptBytecodeAdapter.setGroovyObjectProperty(object7, BasicContentPane.class, this, "outputStyle");
        callSiteArray[38].call(applyStyle.get(), callSiteArray[39].callGroovyObjectGetProperty(this), callSiteArray[40].callGetProperty(callSiteArray[41].callGroovyObjectGetProperty(this)));
        Object object8 = callSiteArray[42].call(doc, "result", regular);
        ScriptBytecodeAdapter.setGroovyObjectProperty(object8, BasicContentPane.class, this, "resultStyle");
        callSiteArray[43].call(applyStyle.get(), callSiteArray[44].callGroovyObjectGetProperty(this), callSiteArray[45].callGetProperty(callSiteArray[46].callGroovyObjectGetProperty(this)));
        Object object9 = callSiteArray[47].call(doc, "stacktrace", regular);
        ScriptBytecodeAdapter.setGroovyObjectProperty(object9, BasicContentPane.class, this, "stacktraceStyle");
        callSiteArray[48].call(applyStyle.get(), callSiteArray[49].callGroovyObjectGetProperty(this), callSiteArray[50].callGetProperty(callSiteArray[51].callGroovyObjectGetProperty(this)));
        Object object10 = callSiteArray[52].call(doc, "hyperlink", regular);
        ScriptBytecodeAdapter.setGroovyObjectProperty(object10, BasicContentPane.class, this, "hyperlinkStyle");
        callSiteArray[53].call(applyStyle.get(), callSiteArray[54].callGroovyObjectGetProperty(this), callSiteArray[55].callGetProperty(callSiteArray[56].callGroovyObjectGetProperty(this)));
        Object object11 = callSiteArray[57].callGetProperty(callSiteArray[58].callGroovyObjectGetProperty(this));
        doc = (StyledDocument)ScriptBytecodeAdapter.castToType(object11, StyledDocument.class);
        Reference<StyleContext> styleContext = new Reference<StyleContext>((StyleContext)ScriptBytecodeAdapter.castToType(callSiteArray[59].callGetProperty(StyleContext.class), StyleContext.class));
        public class _run_closure5
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference styleContext;
            private /* synthetic */ Reference applyStyle;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _run_closure5(Object _outerInstance, Object _thisObject, Reference styleContext, Reference applyStyle) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _run_closure5.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.styleContext = reference2 = styleContext;
                this.applyStyle = reference = applyStyle;
            }

            public Object doCall(Object styleName, Object defs) {
                CallSite[] callSiteArray = _run_closure5.$getCallSiteArray();
                Style style = (Style)ScriptBytecodeAdapter.castToType(callSiteArray[0].call(this.styleContext.get(), styleName), Style.class);
                if (DefaultTypeTransformation.booleanUnbox(style)) {
                    callSiteArray[1].call(this.applyStyle.get(), style, defs);
                    String family = ShortTypeHandling.castToString(callSiteArray[2].call(defs, callSiteArray[3].callGetProperty(StyleConstants.class)));
                    if (ScriptBytecodeAdapter.compareEqual(callSiteArray[4].callGetProperty(style), "default") && DefaultTypeTransformation.booleanUnbox(family)) {
                        String string = family;
                        ScriptBytecodeAdapter.setProperty(string, null, callSiteArray[5].callGroovyObjectGetProperty(this), "defaultFamily");
                        Object object = callSiteArray[6].callConstructor(Font.class, family, callSiteArray[7].callGetProperty(Font.class), callSiteArray[8].callGetProperty(callSiteArray[9].callGetProperty(callSiteArray[10].callGroovyObjectGetProperty(this))));
                        ScriptBytecodeAdapter.setProperty(object, null, callSiteArray[11].callGroovyObjectGetProperty(this), "font");
                        return object;
                    }
                    return null;
                }
                return null;
            }

            public Object call(Object styleName, Object defs) {
                CallSite[] callSiteArray = _run_closure5.$getCallSiteArray();
                return callSiteArray[12].callCurrent(this, styleName, defs);
            }

            public StyleContext getStyleContext() {
                CallSite[] callSiteArray = _run_closure5.$getCallSiteArray();
                return (StyleContext)ScriptBytecodeAdapter.castToType(this.styleContext.get(), StyleContext.class);
            }

            public Object getApplyStyle() {
                CallSite[] callSiteArray = _run_closure5.$getCallSiteArray();
                return this.applyStyle.get();
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
                stringArray[0] = "getStyle";
                stringArray[1] = "call";
                stringArray[2] = "getAt";
                stringArray[3] = "FontFamily";
                stringArray[4] = "name";
                stringArray[5] = "inputEditor";
                stringArray[6] = "<$constructor$>";
                stringArray[7] = "PLAIN";
                stringArray[8] = "size";
                stringArray[9] = "font";
                stringArray[10] = "inputArea";
                stringArray[11] = "inputArea";
                stringArray[12] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[13];
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
        callSiteArray[60].call(callSiteArray[61].callGroovyObjectGetProperty(this), new _run_closure5(this, this, styleContext, applyStyle));
        Graphics g = (Graphics)ScriptBytecodeAdapter.castToType(callSiteArray[62].call(callSiteArray[63].callGetProperty(GraphicsEnvironment.class), callSiteArray[64].callConstructor(BufferedImage.class, 100, 100, callSiteArray[65].callGetProperty(BufferedImage.class))), Graphics.class);
        FontMetrics fm = (FontMetrics)ScriptBytecodeAdapter.castToType(callSiteArray[66].call((Object)g, callSiteArray[67].callGetProperty(callSiteArray[68].callGroovyObjectGetProperty(this))), FontMetrics.class);
        Dimension dimension = (Dimension)ScriptBytecodeAdapter.asType(ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[69].call(prefs.get(), "outputAreaWidth", callSiteArray[70].call(callSiteArray[71].call((Object)fm, 119), 81)), callSiteArray[72].call(prefs.get(), "outputAreaHeight", callSiteArray[73].call(callSiteArray[74].call(callSiteArray[75].call(fm), callSiteArray[76].callGetProperty(fm)), 12))}), Dimension.class);
        ScriptBytecodeAdapter.setProperty(dimension, null, callSiteArray[77].callGroovyObjectGetProperty(this), "preferredSize");
        Dimension dimension2 = (Dimension)ScriptBytecodeAdapter.asType(ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[78].call(prefs.get(), "inputAreaWidth", callSiteArray[79].call(callSiteArray[80].call((Object)fm, 119), 81)), callSiteArray[81].call(prefs.get(), "inputAreaHeight", callSiteArray[82].call(callSiteArray[83].call(callSiteArray[84].call(fm), callSiteArray[85].callGetProperty(fm)), 12))}), Dimension.class);
        ScriptBytecodeAdapter.setProperty(dimension2, null, callSiteArray[86].callGroovyObjectGetProperty(this), "preferredSize");
        Integer n = -1;
        ScriptBytecodeAdapter.setGroovyObjectProperty(n, BasicContentPane.class, this, "origDividerSize");
        if (DefaultTypeTransformation.booleanUnbox(detachedOutputFlag)) {
            callSiteArray[87].call(callSiteArray[88].callGroovyObjectGetProperty(this), callSiteArray[89].callGroovyObjectGetProperty(this), callSiteArray[90].callGetProperty(JSplitPane.class));
            Object object12 = callSiteArray[91].callGetProperty(callSiteArray[92].callGroovyObjectGetProperty(this));
            ScriptBytecodeAdapter.setGroovyObjectProperty(object12, BasicContentPane.class, this, "origDividerSize");
            int n2 = 0;
            ScriptBytecodeAdapter.setProperty(n2, null, callSiteArray[93].callGroovyObjectGetProperty(this), "dividerSize");
            BigDecimal bigDecimal = $const$1;
            ScriptBytecodeAdapter.setProperty(bigDecimal, null, callSiteArray[94].callGroovyObjectGetProperty(this), "resizeWeight");
            return callSiteArray[95].call(callSiteArray[96].callGroovyObjectGetProperty(this), callSiteArray[97].callGroovyObjectGetProperty(this), callSiteArray[98].callGetProperty(BorderLayout.class));
        }
        return null;
    }

    private Object buildOutputArea(Object prefs) {
        Reference<Object> prefs2 = new Reference<Object>(prefs);
        CallSite[] callSiteArray = BasicContentPane.$getCallSiteArray();
        public class _buildOutputArea_closure6
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference prefs;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _buildOutputArea_closure6(Object _outerInstance, Object _thisObject, Reference prefs) {
                Reference reference;
                CallSite[] callSiteArray = _buildOutputArea_closure6.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.prefs = reference = prefs;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _buildOutputArea_closure6.$getCallSiteArray();
                Object object = callSiteArray[0].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"editable", false, "name", "outputArea", "contentType", "text/html", "background", callSiteArray[1].callConstructor(Color.class, 255, 255, 218), "font", callSiteArray[2].callConstructor(Font.class, "Monospaced", callSiteArray[3].callGetProperty(Font.class), callSiteArray[4].call(this.prefs.get(), "fontSize", 12)), "border", callSiteArray[5].callCurrent((GroovyObject)this, 4)}));
                ScriptBytecodeAdapter.setGroovyObjectProperty(object, _buildOutputArea_closure6.class, this, "outputArea");
                return object;
            }

            public Object getPrefs() {
                CallSite[] callSiteArray = _buildOutputArea_closure6.$getCallSiteArray();
                return this.prefs.get();
            }

            public Object doCall() {
                CallSite[] callSiteArray = _buildOutputArea_closure6.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _buildOutputArea_closure6.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "textPane";
                stringArray[1] = "<$constructor$>";
                stringArray[2] = "<$constructor$>";
                stringArray[3] = "PLAIN";
                stringArray[4] = "getInt";
                stringArray[5] = "emptyBorder";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[6];
                _buildOutputArea_closure6.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_buildOutputArea_closure6.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _buildOutputArea_closure6.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        Object object = callSiteArray[99].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"border", callSiteArray[100].callCurrent((GroovyObject)this, 0)}), new _buildOutputArea_closure6(this, this, prefs2));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object, BasicContentPane.class, this, "scrollArea");
        return object;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != BasicContentPane.class) {
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
        BigDecimal bigDecimal2;
        CallSite[] callSiteArray = BasicContentPane.$getCallSiteArray();
        $callSiteArray = null;
        $const$0 = bigDecimal2 = new BigDecimal("0.5");
        $const$1 = bigDecimal = new BigDecimal("1.0");
    }

    static {
        BasicContentPane.__$swapInit();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "runScript";
        stringArray[1] = "userNodeForPackage";
        stringArray[2] = "getBoolean";
        stringArray[3] = "frame";
        stringArray[4] = "HIDE_ON_CLOSE";
        stringArray[5] = "splitPane";
        stringArray[6] = "VERTICAL_SPLIT";
        stringArray[7] = "textEditor";
        stringArray[8] = "inputEditor";
        stringArray[9] = "actions";
        stringArray[10] = "<$constructor$>";
        stringArray[11] = "style";
        stringArray[12] = "font";
        stringArray[13] = "outputArea";
        stringArray[14] = "size";
        stringArray[15] = "font";
        stringArray[16] = "outputArea";
        stringArray[17] = "outputArea";
        stringArray[18] = "styledDocument";
        stringArray[19] = "outputArea";
        stringArray[20] = "getStyle";
        stringArray[21] = "defaultStyleContext";
        stringArray[22] = "DEFAULT_STYLE";
        stringArray[23] = "addStyle";
        stringArray[24] = "call";
        stringArray[25] = "regular";
        stringArray[26] = "styles";
        stringArray[27] = "addStyle";
        stringArray[28] = "call";
        stringArray[29] = "promptStyle";
        stringArray[30] = "prompt";
        stringArray[31] = "styles";
        stringArray[32] = "addStyle";
        stringArray[33] = "call";
        stringArray[34] = "commandStyle";
        stringArray[35] = "command";
        stringArray[36] = "styles";
        stringArray[37] = "addStyle";
        stringArray[38] = "call";
        stringArray[39] = "outputStyle";
        stringArray[40] = "output";
        stringArray[41] = "styles";
        stringArray[42] = "addStyle";
        stringArray[43] = "call";
        stringArray[44] = "resultStyle";
        stringArray[45] = "result";
        stringArray[46] = "styles";
        stringArray[47] = "addStyle";
        stringArray[48] = "call";
        stringArray[49] = "stacktraceStyle";
        stringArray[50] = "stacktrace";
        stringArray[51] = "styles";
        stringArray[52] = "addStyle";
        stringArray[53] = "call";
        stringArray[54] = "hyperlinkStyle";
        stringArray[55] = "hyperlink";
        stringArray[56] = "styles";
        stringArray[57] = "styledDocument";
        stringArray[58] = "inputArea";
        stringArray[59] = "defaultStyleContext";
        stringArray[60] = "each";
        stringArray[61] = "styles";
        stringArray[62] = "createGraphics";
        stringArray[63] = "localGraphicsEnvironment";
        stringArray[64] = "<$constructor$>";
        stringArray[65] = "TYPE_INT_RGB";
        stringArray[66] = "getFontMetrics";
        stringArray[67] = "font";
        stringArray[68] = "outputArea";
        stringArray[69] = "getInt";
        stringArray[70] = "multiply";
        stringArray[71] = "charWidth";
        stringArray[72] = "getInt";
        stringArray[73] = "multiply";
        stringArray[74] = "plus";
        stringArray[75] = "getHeight";
        stringArray[76] = "leading";
        stringArray[77] = "outputArea";
        stringArray[78] = "getInt";
        stringArray[79] = "multiply";
        stringArray[80] = "charWidth";
        stringArray[81] = "getInt";
        stringArray[82] = "multiply";
        stringArray[83] = "plus";
        stringArray[84] = "getHeight";
        stringArray[85] = "leading";
        stringArray[86] = "inputEditor";
        stringArray[87] = "add";
        stringArray[88] = "splitPane";
        stringArray[89] = "blank";
        stringArray[90] = "BOTTOM";
        stringArray[91] = "dividerSize";
        stringArray[92] = "splitPane";
        stringArray[93] = "splitPane";
        stringArray[94] = "splitPane";
        stringArray[95] = "add";
        stringArray[96] = "outputWindow";
        stringArray[97] = "scrollArea";
        stringArray[98] = "CENTER";
        stringArray[99] = "scrollPane";
        stringArray[100] = "emptyBorder";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[101];
        BasicContentPane.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(BasicContentPane.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = BasicContentPane.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

