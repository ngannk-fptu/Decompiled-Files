/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui;

import groovy.inspect.swingui.AstBrowser;
import groovy.inspect.swingui.ObjectBrowser;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.swing.SwingBuilder;
import groovy.transform.ThreadInterrupt;
import groovy.ui.ConsoleActions;
import groovy.ui.ConsoleTextEditor;
import groovy.ui.ConsoleView;
import groovy.ui.GroovyFileFilter;
import groovy.ui.HistoryRecord;
import groovy.ui.OutputTransforms;
import groovy.ui.SystemOutputInterceptor;
import groovy.ui.text.FindReplaceUtility;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.SoftReference;
import java.math.BigDecimal;
import java.net.URL;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.regex.Matcher;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JApplet;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.codehaus.groovy.control.customizers.ASTTransformationCustomizer;
import org.codehaus.groovy.control.messages.ExceptionMessage;
import org.codehaus.groovy.control.messages.SimpleMessage;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.StackTraceUtils;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.syntax.SyntaxException;

public class Console
implements CaretListener,
HyperlinkListener,
ComponentListener,
FocusListener,
GroovyObject {
    private static final String DEFAULT_SCRIPT_NAME_START = "ConsoleScript";
    private static Object prefs;
    private static boolean captureStdOut;
    private static boolean captureStdErr;
    private static Object consoleControllers;
    private boolean fullStackTraces;
    private Action fullStackTracesAction;
    private boolean showScriptInOutput;
    private Action showScriptInOutputAction;
    private boolean visualizeScriptResults;
    private Action visualizeScriptResultsAction;
    private boolean showToolbar;
    private Component toolbar;
    private Action showToolbarAction;
    private boolean detachedOutput;
    private Action detachedOutputAction;
    private Action showOutputWindowAction;
    private Action hideOutputWindowAction1;
    private Action hideOutputWindowAction2;
    private Action hideOutputWindowAction3;
    private Action hideOutputWindowAction4;
    private int origDividerSize;
    private Component outputWindow;
    private Component copyFromComponent;
    private Component blank;
    private Component scrollArea;
    private boolean autoClearOutput;
    private Action autoClearOutputAction;
    private boolean threadInterrupt;
    private Action threadInterruptAction;
    private boolean saveOnRun;
    private Action saveOnRunAction;
    private boolean useScriptClassLoaderForScriptExecution;
    private int maxHistory;
    private int maxOutputChars;
    private SwingBuilder swing;
    private RootPaneContainer frame;
    private ConsoleTextEditor inputEditor;
    private JSplitPane splitPane;
    private JTextPane inputArea;
    private JTextPane outputArea;
    private JLabel statusLabel;
    private JLabel rowNumAndColNum;
    private Element rootElement;
    private int cursorPos;
    private int rowNum;
    private int colNum;
    private Style promptStyle;
    private Style commandStyle;
    private Style outputStyle;
    private Style stacktraceStyle;
    private Style hyperlinkStyle;
    private Style resultStyle;
    private List history;
    private int historyIndex;
    private HistoryRecord pendingRecord;
    private Action prevHistoryAction;
    private Action nextHistoryAction;
    private boolean dirty;
    private Action saveAction;
    private int textSelectionStart;
    private int textSelectionEnd;
    private Object scriptFile;
    private File currentFileChooserDir;
    private File currentClasspathJarDir;
    private File currentClasspathDir;
    private CompilerConfiguration config;
    private GroovyShell shell;
    private int scriptNameCounter;
    private SystemOutputInterceptor systemOutInterceptor;
    private SystemOutputInterceptor systemErrorInterceptor;
    private Thread runThread;
    private Closure beforeExecution;
    private Closure afterExecution;
    public static URL ICON_PATH;
    public static URL NODE_ICON_PATH;
    private static Object groovyFileFilter;
    private boolean scriptRunning;
    private boolean stackOverFlowError;
    private Action interruptAction;
    private static Object frameConsoleDelegates;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static final /* synthetic */ BigDecimal $const$0;
    private static final /* synthetic */ BigDecimal $const$1;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public Console() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object[] objectArray = new Object[]{callSiteArray[0].callConstructor(Binding.class)};
        Console console = this;
        switch (ScriptBytecodeAdapter.selectConstructorAndTransformArguments(objectArray, -1, Console.class)) {
            case -2044833963: {
                Object[] objectArray2 = objectArray;
                console((ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[0], ClassLoader.class));
                break;
            }
            case -1304685753: {
                Object[] objectArray2 = objectArray;
                console((ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[0], ClassLoader.class), (Binding)ScriptBytecodeAdapter.castToType(objectArray[1], Binding.class));
                break;
            }
            case 39797: {
                Object[] objectArray2 = objectArray;
                console();
                break;
            }
            case 498637607: {
                Object[] objectArray2 = objectArray;
                console((Binding)ScriptBytecodeAdapter.castToType(objectArray[0], Binding.class));
                break;
            }
            default: {
                throw new IllegalArgumentException("This class has been compiled with a super class which is binary incompatible with the current super class found on classpath. You should recompile this class with the new version.");
            }
        }
    }

    public Console(Binding binding) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this(null, binding);
    }

    public Console(ClassLoader parent) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this(parent, (Binding)ScriptBytecodeAdapter.castToType(callSiteArray[1].callConstructor(Binding.class), Binding.class));
    }

    public Console(ClassLoader parent, Binding binding) {
        MetaClass metaClass;
        boolean bl;
        boolean bl2;
        int n;
        int n2;
        List list;
        int n3;
        int n4;
        boolean bl3;
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object object = callSiteArray[2].call(prefs, "fullStackTraces", callSiteArray[3].call(Boolean.class, callSiteArray[4].call(System.class, "groovy.full.stacktrace", "false")));
        this.fullStackTraces = DefaultTypeTransformation.booleanUnbox(object);
        Object object2 = callSiteArray[5].call(prefs, "showScriptInOutput", true);
        this.showScriptInOutput = DefaultTypeTransformation.booleanUnbox(object2);
        Object object3 = callSiteArray[6].call(prefs, "visualizeScriptResults", false);
        this.visualizeScriptResults = DefaultTypeTransformation.booleanUnbox(object3);
        Object object4 = callSiteArray[7].call(prefs, "showToolbar", true);
        this.showToolbar = DefaultTypeTransformation.booleanUnbox(object4);
        Object object5 = callSiteArray[8].call(prefs, "detachedOutput", false);
        this.detachedOutput = DefaultTypeTransformation.booleanUnbox(object5);
        Object object6 = callSiteArray[9].call(prefs, "autoClearOutput", false);
        this.autoClearOutput = DefaultTypeTransformation.booleanUnbox(object6);
        Object object7 = callSiteArray[10].call(prefs, "threadInterrupt", false);
        this.threadInterrupt = DefaultTypeTransformation.booleanUnbox(object7);
        Object object8 = callSiteArray[11].call(prefs, "saveOnRun", false);
        this.saveOnRun = DefaultTypeTransformation.booleanUnbox(object8);
        this.useScriptClassLoaderForScriptExecution = bl3 = false;
        this.maxHistory = n4 = 10;
        this.maxOutputChars = n3 = DefaultTypeTransformation.intUnbox(ScriptBytecodeAdapter.asType(callSiteArray[12].call(System.class, "groovy.console.output.limit", "20000"), Integer.TYPE));
        this.history = list = ScriptBytecodeAdapter.createList(new Object[0]);
        this.historyIndex = n2 = 1;
        Object object9 = callSiteArray[13].callConstructor(HistoryRecord.class, ScriptBytecodeAdapter.createMap(new Object[]{"allText", "", "selectionStart", 0, "selectionEnd", 0}));
        this.pendingRecord = (HistoryRecord)ScriptBytecodeAdapter.castToType(object9, HistoryRecord.class);
        Object object10 = callSiteArray[14].callConstructor(File.class, callSiteArray[15].call(callSiteArray[16].call(Preferences.class, Console.class), "currentFileChooserDir", "."));
        this.currentFileChooserDir = (File)ScriptBytecodeAdapter.castToType(object10, File.class);
        Object object11 = callSiteArray[17].callConstructor(File.class, callSiteArray[18].call(callSiteArray[19].call(Preferences.class, Console.class), "currentClasspathJarDir", "."));
        this.currentClasspathJarDir = (File)ScriptBytecodeAdapter.castToType(object11, File.class);
        Object object12 = callSiteArray[20].callConstructor(File.class, callSiteArray[21].call(callSiteArray[22].call(Preferences.class, Console.class), "currentClasspathDir", "."));
        this.currentClasspathDir = (File)ScriptBytecodeAdapter.castToType(object12, File.class);
        this.scriptNameCounter = n = 0;
        Object var22_22 = null;
        this.runThread = (Thread)ScriptBytecodeAdapter.castToType(var22_22, Thread.class);
        this.scriptRunning = bl2 = false;
        this.stackOverFlowError = bl = false;
        this.metaClass = metaClass = this.$getStaticMetaClass();
        callSiteArray[23].callCurrent(this, parent, binding);
        try {
            callSiteArray[24].call(System.class, "groovy.full.stacktrace", callSiteArray[25].call(System.class, "groovy.full.stacktrace", callSiteArray[26].call(Boolean.class, callSiteArray[27].call(prefs, "fullStackTraces", false))));
        }
        catch (SecurityException se) {
            boolean bl4 = false;
            ScriptBytecodeAdapter.setProperty(bl4, null, this.fullStackTracesAction, "enabled");
        }
        consoleControllers = callSiteArray[28].call(consoleControllers, this);
        try {
            if (DefaultTypeTransformation.booleanUnbox(Class.forName("org.apache.ivy.core.event.IvyListener"))) {
                Class<?> ivyPluginClass = Class.forName("groovy.ui.ConsoleIvyPlugin");
                callSiteArray[29].call(callSiteArray[30].call(ivyPluginClass), this);
            }
        }
        catch (ClassNotFoundException ignore) {
        }
        Object object13 = callSiteArray[31].call(OutputTransforms.class);
        ScriptBytecodeAdapter.setProperty(object13, null, callSiteArray[32].callGroovyObjectGetProperty(binding), "_outputTransforms");
    }

    public static void main(String ... args) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[33].callGetProperty(args), 1) && ScriptBytecodeAdapter.compareEqual(callSiteArray[34].call((Object)args, 0), "--help")) {
                callSiteArray[35].callStatic(Console.class, "usage: groovyConsole [options] [filename]\noptions:\n  --help                               This Help message\n  -cp,-classpath,--classpath <path>    Specify classpath");
                return;
            }
        } else if (ScriptBytecodeAdapter.compareEqual(callSiteArray[36].callGetProperty(args), 1) && ScriptBytecodeAdapter.compareEqual(callSiteArray[37].call((Object)args, 0), "--help")) {
            callSiteArray[38].callStatic(Console.class, "usage: groovyConsole [options] [filename]\noptions:\n  --help                               This Help message\n  -cp,-classpath,--classpath <path>    Specify classpath");
            return;
        }
        boolean bl = false;
        ScriptBytecodeAdapter.setProperty(bl, null, callSiteArray[39].call(Logger.class, callSiteArray[40].callGetProperty(StackTraceUtils.class)), "useParentHandlers");
        callSiteArray[41].call(UIManager.class, callSiteArray[42].call(UIManager.class));
        Object console = callSiteArray[43].callConstructor(Console.class, callSiteArray[44].callSafe(callSiteArray[45].callGetProperty(Console.class)));
        boolean bl2 = true;
        ScriptBytecodeAdapter.setProperty(bl2, null, console, "useScriptClassLoaderForScriptExecution");
        callSiteArray[46].call(console);
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[47].callGetProperty(args), 1)) {
            callSiteArray[48].call(console, ScriptBytecodeAdapter.createPojoWrapper((File)ScriptBytecodeAdapter.asType(callSiteArray[49].call((Object)args, 0), File.class), File.class));
        }
    }

    public void newScript(ClassLoader parent, Binding binding) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object object = callSiteArray[50].callConstructor(CompilerConfiguration.class);
        this.config = (CompilerConfiguration)ScriptBytecodeAdapter.castToType(object, CompilerConfiguration.class);
        if (this.threadInterrupt) {
            callSiteArray[51].call((Object)this.config, callSiteArray[52].callConstructor(ASTTransformationCustomizer.class, ThreadInterrupt.class));
        }
        Object object2 = callSiteArray[53].callConstructor(GroovyShell.class, parent, binding, this.config);
        this.shell = (GroovyShell)ScriptBytecodeAdapter.castToType(object2, GroovyShell.class);
    }

    public void run() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        callSiteArray[54].callCurrent((GroovyObject)this, frameConsoleDelegates);
    }

    public void run(JApplet applet) {
        Reference<JApplet> applet2 = new Reference<JApplet>(applet);
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        public class _run_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference applet;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _run_closure1(Object _outerInstance, Object _thisObject, Reference applet) {
                Reference reference;
                CallSite[] callSiteArray = _run_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.applet = reference = applet;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _run_closure1.$getCallSiteArray();
                ScriptBytecodeAdapter.setGroovyObjectProperty(callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this), callSiteArray[2].call(SwingUtilities.class, callSiteArray[3].call(this.applet.get()))), _run_closure1.class, this, "containingWindows");
                return this.applet.get();
            }

            public JApplet getApplet() {
                CallSite[] callSiteArray = _run_closure1.$getCallSiteArray();
                return (JApplet)ScriptBytecodeAdapter.castToType(this.applet.get(), JApplet.class);
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
                stringArray[0] = "plus";
                stringArray[1] = "containingWindows";
                stringArray[2] = "getRoot";
                stringArray[3] = "getParent";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[4];
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

            public Object doCall(Object arg) {
                CallSite[] callSiteArray = _run_closure2.$getCallSiteArray();
                Object object = callSiteArray[0].callCurrent((GroovyObject)this, arg);
                ScriptBytecodeAdapter.setProperty(object, null, callSiteArray[1].callGroovyObjectGetProperty(this), "JMenuBar");
                return object;
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
                stringArray[0] = "build";
                stringArray[1] = "current";
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
        callSiteArray[55].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"rootContainerDelegate", new _run_closure1(this, this, applet2), "menuBarDelegate", new _run_closure2(this, this)}));
    }

    public void run(Map defaults) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object object = callSiteArray[56].callConstructor(SwingBuilder.class);
        this.swing = (SwingBuilder)ScriptBytecodeAdapter.castToType(object, SwingBuilder.class);
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

            public Object doCall(Object k, Object v) {
                CallSite[] callSiteArray = _run_closure3.$getCallSiteArray();
                Object object = v;
                callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this), k, object);
                return object;
            }

            public Object call(Object k, Object v) {
                CallSite[] callSiteArray = _run_closure3.$getCallSiteArray();
                return callSiteArray[2].callCurrent(this, k, v);
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
                stringArray[0] = "putAt";
                stringArray[1] = "swing";
                stringArray[2] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[3];
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
        callSiteArray[57].call((Object)defaults, new _run_closure3(this, this));
        callSiteArray[58].call(System.class, "groovy.sanitized.stacktraces", "org.codehaus.groovy.runtime.\n                org.codehaus.groovy.\n                groovy.lang.\n                gjdk.groovy.lang.\n                sun.\n                java.lang.reflect.\n                java.lang.Thread\n                groovy.ui.Console");
        Console console = this;
        ScriptBytecodeAdapter.setGroovyObjectProperty(console, Console.class, this.swing, "controller");
        callSiteArray[59].call((Object)this.swing, ConsoleActions.class);
        callSiteArray[60].call((Object)this.swing, ConsoleView.class);
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[61].callCurrent(this);
        } else {
            this.bindResults();
        }
        callSiteArray[62].call((Object)this.swing, ScriptBytecodeAdapter.createMap(new Object[]{"source", callSiteArray[63].callGetProperty(callSiteArray[64].callGroovyObjectGetProperty(this.swing)), "sourceProperty", "enabled", "target", callSiteArray[65].callGroovyObjectGetProperty(this.swing), "targetProperty", "enabled"}));
        callSiteArray[66].call((Object)this.swing, ScriptBytecodeAdapter.createMap(new Object[]{"source", callSiteArray[67].callGetProperty(callSiteArray[68].callGroovyObjectGetProperty(this.swing)), "sourceProperty", "enabled", "target", callSiteArray[69].callGroovyObjectGetProperty(this.swing), "targetProperty", "enabled"}));
        if (callSiteArray[70].callGroovyObjectGetProperty(this.swing) instanceof Window) {
            callSiteArray[71].callCurrent((GroovyObject)this, callSiteArray[72].callGroovyObjectGetProperty(this.swing));
            callSiteArray[73].call(callSiteArray[74].callGroovyObjectGetProperty(this.swing));
            callSiteArray[75].call(callSiteArray[76].callGroovyObjectGetProperty(this.swing));
        }
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[77].callCurrent(this);
        } else {
            this.installInterceptor();
        }
        callSiteArray[78].call((Object)this.swing, ScriptBytecodeAdapter.getMethodPointer(this.inputArea, "requestFocus"));
    }

    private void nativeFullScreenForMac(Window frame) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[79].call(callSiteArray[80].call(System.class, "os.name"), "Mac OS X"))) {
            callSiteArray[81].call(callSiteArray[82].callConstructor(GroovyShell.class, callSiteArray[83].callConstructor(Binding.class, ScriptBytecodeAdapter.createMap(new Object[]{"frame", frame}))), "\n                    try {\n                        com.apple.eawt.FullScreenUtilities.setWindowCanFullScreen(frame, true)\n                    } catch (Throwable t) {\n                        // simply ignore as full screen capability is not available\n                    }\n                ");
        }
    }

    public void installInterceptor() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object object = callSiteArray[84].callConstructor(SystemOutputInterceptor.class, ScriptBytecodeAdapter.getMethodPointer(this, "notifySystemOut"), true);
        this.systemOutInterceptor = (SystemOutputInterceptor)ScriptBytecodeAdapter.castToType(object, SystemOutputInterceptor.class);
        callSiteArray[85].call(this.systemOutInterceptor);
        Object object2 = callSiteArray[86].callConstructor(SystemOutputInterceptor.class, ScriptBytecodeAdapter.getMethodPointer(this, "notifySystemErr"), false);
        this.systemErrorInterceptor = (SystemOutputInterceptor)ScriptBytecodeAdapter.castToType(object2, SystemOutputInterceptor.class);
        callSiteArray[87].call(this.systemErrorInterceptor);
    }

    public void addToHistory(Object record) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        callSiteArray[88].call((Object)this.history, record);
        if (ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[89].call(this.history), this.maxHistory)) {
            callSiteArray[90].call((Object)this.history, 0);
        }
        Object object = callSiteArray[91].call(this.history);
        this.historyIndex = DefaultTypeTransformation.intUnbox(object);
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[92].callCurrent(this);
        } else {
            this.updateHistoryActions();
        }
    }

    private Object ensureNoDocLengthOverflow(Object doc) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        int offset = this.stackOverFlowError ? this.maxOutputChars : 0;
        if (ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[93].callGetProperty(doc), this.maxOutputChars)) {
            return callSiteArray[94].call(doc, offset, callSiteArray[95].call(callSiteArray[96].callGetProperty(doc), this.maxOutputChars));
        }
        return null;
    }

    public void appendOutput(String text, AttributeSet style) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object doc = callSiteArray[97].callGetProperty(this.outputArea);
        callSiteArray[98].call(doc, callSiteArray[99].callGetProperty(doc), text, style);
        callSiteArray[100].callCurrent((GroovyObject)this, doc);
    }

    public void appendOutput(Window window, AttributeSet style) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        callSiteArray[101].callCurrent(this, callSiteArray[102].call(window), style);
    }

    public void appendOutput(Object object, AttributeSet style) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        callSiteArray[103].callCurrent(this, callSiteArray[104].call(object), style);
    }

    public void appendOutput(Component component, AttributeSet style) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        SimpleAttributeSet sas = (SimpleAttributeSet)ScriptBytecodeAdapter.castToType(callSiteArray[105].callConstructor(SimpleAttributeSet.class), SimpleAttributeSet.class);
        callSiteArray[106].call(sas, callSiteArray[107].callGetProperty(StyleConstants.class), "component");
        callSiteArray[108].call(StyleConstants.class, sas, component);
        callSiteArray[109].callCurrent(this, callSiteArray[110].call(component), sas);
    }

    public void appendOutput(Icon icon, AttributeSet style) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        SimpleAttributeSet sas = (SimpleAttributeSet)ScriptBytecodeAdapter.castToType(callSiteArray[111].callConstructor(SimpleAttributeSet.class), SimpleAttributeSet.class);
        callSiteArray[112].call(sas, callSiteArray[113].callGetProperty(StyleConstants.class), "icon");
        callSiteArray[114].call(StyleConstants.class, sas, icon);
        callSiteArray[115].callCurrent(this, callSiteArray[116].call(icon), sas);
    }

    public void appendStacktrace(Object text) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Reference<Object> doc = new Reference<Object>(callSiteArray[117].callGetProperty(this.outputArea));
        Object lines = callSiteArray[118].call(text, "(\\n|\\r|\\r\\n|\u0085|\u2028|\u2029)");
        String ji = "([\\p{Alnum}_\\$][\\p{Alnum}_\\$]*)";
        Reference<GStringImpl> stacktracePattern = new Reference<GStringImpl>(new GStringImpl(new Object[]{ji, ji, ji}, new String[]{"\\tat ", "(\\.", ")+\\(((", "(\\.(java|groovy))?):(\\d+))\\)"}));
        public class _appendStacktrace_closure4
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference doc;
            private /* synthetic */ Reference stacktracePattern;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _appendStacktrace_closure4(Object _outerInstance, Object _thisObject, Reference doc, Reference stacktracePattern) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _appendStacktrace_closure4.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.doc = reference2 = doc;
                this.stacktracePattern = reference = stacktracePattern;
            }

            public Object doCall(Object line) {
                CallSite[] callSiteArray = _appendStacktrace_closure4.$getCallSiteArray();
                int initialLength = DefaultTypeTransformation.intUnbox(callSiteArray[0].callGetProperty(this.doc.get()));
                Matcher matcher = ScriptBytecodeAdapter.findRegex(line, this.stacktracePattern.get());
                String fileName = DefaultTypeTransformation.booleanUnbox(callSiteArray[1].call(matcher)) ? callSiteArray[2].call(callSiteArray[3].call((Object)matcher, 0), -5) : "";
                if (ScriptBytecodeAdapter.compareEqual(fileName, callSiteArray[4].callGetPropertySafe(callSiteArray[5].callGroovyObjectGetProperty(this))) || DefaultTypeTransformation.booleanUnbox(callSiteArray[6].call((Object)fileName, callSiteArray[7].callGetProperty(Console.class)))) {
                    Object fileNameAndLineNumber = callSiteArray[8].call(callSiteArray[9].call((Object)matcher, 0), -6);
                    Object length = callSiteArray[10].call(fileNameAndLineNumber);
                    Object index = callSiteArray[11].call(line, fileNameAndLineNumber);
                    Object style = callSiteArray[12].callGroovyObjectGetProperty(this);
                    Object hrefAttr = callSiteArray[13].callConstructor(SimpleAttributeSet.class);
                    callSiteArray[14].call(hrefAttr, callSiteArray[15].callGetProperty(HTML.Attribute.class), callSiteArray[16].call((Object)"file://", fileNameAndLineNumber));
                    callSiteArray[17].call(style, callSiteArray[18].callGetProperty(HTML.Tag.class), hrefAttr);
                    callSiteArray[19].call(this.doc.get(), initialLength, callSiteArray[20].call(line, ScriptBytecodeAdapter.createRange(0, index, false)), callSiteArray[21].callGroovyObjectGetProperty(this));
                    callSiteArray[22].call(this.doc.get(), callSiteArray[23].call((Object)initialLength, index), callSiteArray[24].call(line, ScriptBytecodeAdapter.createRange(index, callSiteArray[25].call(index, length), false)), style);
                    return callSiteArray[26].call(this.doc.get(), callSiteArray[27].call(callSiteArray[28].call((Object)initialLength, index), length), callSiteArray[29].call(callSiteArray[30].call(line, ScriptBytecodeAdapter.createRange(callSiteArray[31].call(index, length), -1, true)), "\n"), callSiteArray[32].callGroovyObjectGetProperty(this));
                }
                return callSiteArray[33].call(this.doc.get(), initialLength, callSiteArray[34].call(line, "\n"), callSiteArray[35].callGroovyObjectGetProperty(this));
            }

            public Object getDoc() {
                CallSite[] callSiteArray = _appendStacktrace_closure4.$getCallSiteArray();
                return this.doc.get();
            }

            public Object getStacktracePattern() {
                CallSite[] callSiteArray = _appendStacktrace_closure4.$getCallSiteArray();
                return this.stacktracePattern.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _appendStacktrace_closure4.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "length";
                stringArray[1] = "matches";
                stringArray[2] = "getAt";
                stringArray[3] = "getAt";
                stringArray[4] = "name";
                stringArray[5] = "scriptFile";
                stringArray[6] = "startsWith";
                stringArray[7] = "DEFAULT_SCRIPT_NAME_START";
                stringArray[8] = "getAt";
                stringArray[9] = "getAt";
                stringArray[10] = "length";
                stringArray[11] = "indexOf";
                stringArray[12] = "hyperlinkStyle";
                stringArray[13] = "<$constructor$>";
                stringArray[14] = "addAttribute";
                stringArray[15] = "HREF";
                stringArray[16] = "plus";
                stringArray[17] = "addAttribute";
                stringArray[18] = "A";
                stringArray[19] = "insertString";
                stringArray[20] = "getAt";
                stringArray[21] = "stacktraceStyle";
                stringArray[22] = "insertString";
                stringArray[23] = "plus";
                stringArray[24] = "getAt";
                stringArray[25] = "plus";
                stringArray[26] = "insertString";
                stringArray[27] = "plus";
                stringArray[28] = "plus";
                stringArray[29] = "plus";
                stringArray[30] = "getAt";
                stringArray[31] = "plus";
                stringArray[32] = "stacktraceStyle";
                stringArray[33] = "insertString";
                stringArray[34] = "plus";
                stringArray[35] = "stacktraceStyle";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[36];
                _appendStacktrace_closure4.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_appendStacktrace_closure4.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _appendStacktrace_closure4.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[119].call(lines, new _appendStacktrace_closure4(this, this, doc, stacktracePattern));
        callSiteArray[120].callCurrent((GroovyObject)this, doc.get());
    }

    public void appendOutputNl(Object text, Object style) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object doc = callSiteArray[121].callGetProperty(this.outputArea);
        Object len = callSiteArray[122].callGetProperty(doc);
        Boolean alreadyNewLine = null;
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            boolean bl = ScriptBytecodeAdapter.compareEqual(len, 0) || ScriptBytecodeAdapter.compareEqual(callSiteArray[123].call(doc, callSiteArray[124].call(len, 1), 1), "\n");
            alreadyNewLine = bl;
        } else {
            boolean bl = ScriptBytecodeAdapter.compareEqual(len, 0) || ScriptBytecodeAdapter.compareEqual(callSiteArray[125].call(doc, callSiteArray[126].call(len, 1), 1), "\n");
            alreadyNewLine = bl;
        }
        callSiteArray[127].call(doc, callSiteArray[128].callGetProperty(doc), " \n", style);
        if (DefaultTypeTransformation.booleanUnbox(alreadyNewLine)) {
            callSiteArray[129].call(doc, len, 2);
        }
        callSiteArray[130].callCurrent(this, text, style);
    }

    public void appendOutputLines(Object text, Object style) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        callSiteArray[131].callCurrent(this, text, style);
        Object doc = callSiteArray[132].callGetProperty(this.outputArea);
        Object len = callSiteArray[133].callGetProperty(doc);
        callSiteArray[134].call(doc, len, " \n", style);
        callSiteArray[135].call(doc, len, 2);
    }

    public boolean askToSaveFile() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (!this.dirty) {
            return true;
        }
        Object object = callSiteArray[136].call(JOptionPane.class, this.frame, callSiteArray[137].call(callSiteArray[138].call((Object)"Save changes", ScriptBytecodeAdapter.compareNotEqual(this.scriptFile, null) ? new GStringImpl(new Object[]{callSiteArray[139].callGetProperty(this.scriptFile)}, new String[]{" to ", ""}) : ""), "?"), "GroovyConsole", callSiteArray[140].callGetProperty(JOptionPane.class));
        if (ScriptBytecodeAdapter.isCase(object, callSiteArray[141].callGetProperty(JOptionPane.class))) {
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                return DefaultTypeTransformation.booleanUnbox(callSiteArray[142].callCurrent(this));
            }
            return this.fileSave();
        }
        return ScriptBytecodeAdapter.isCase(object, callSiteArray[143].callGetProperty(JOptionPane.class));
    }

    public void beep() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        callSiteArray[144].call(callSiteArray[145].callGetProperty(Toolkit.class));
    }

    public void bindResults() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[146].call(this.shell, "_", callSiteArray[147].callCurrent(this));
        } else {
            callSiteArray[148].call(this.shell, "_", this.getLastResult());
        }
        public class _bindResults_closure5
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _bindResults_closure5(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _bindResults_closure5.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _bindResults_closure5.$getCallSiteArray();
                return callSiteArray[0].callGetProperty(it);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _bindResults_closure5.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _bindResults_closure5.class) {
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
                stringArray[0] = "result";
                return new CallSiteArray(_bindResults_closure5.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _bindResults_closure5.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[149].call(this.shell, "__", callSiteArray[150].call((Object)this.history, new _bindResults_closure5(this, this)));
    }

    public static void captureStdOut(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object object = callSiteArray[151].callGetProperty(callSiteArray[152].callGetProperty(evt));
        captureStdOut = DefaultTypeTransformation.booleanUnbox(object);
        callSiteArray[153].call(prefs, "captureStdOut", captureStdOut);
    }

    public static void captureStdErr(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object object = callSiteArray[154].callGetProperty(callSiteArray[155].callGetProperty(evt));
        captureStdErr = DefaultTypeTransformation.booleanUnbox(object);
        callSiteArray[156].call(prefs, "captureStdErr", captureStdErr);
    }

    public void fullStackTraces(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object object = callSiteArray[157].callGetProperty(callSiteArray[158].callGetProperty(evt));
        this.fullStackTraces = DefaultTypeTransformation.booleanUnbox(object);
        callSiteArray[159].call(System.class, "groovy.full.stacktrace", callSiteArray[160].call(Boolean.class, this.fullStackTraces));
        callSiteArray[161].call(prefs, "fullStackTraces", this.fullStackTraces);
    }

    public void showScriptInOutput(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object object = callSiteArray[162].callGetProperty(callSiteArray[163].callGetProperty(evt));
        this.showScriptInOutput = DefaultTypeTransformation.booleanUnbox(object);
        callSiteArray[164].call(prefs, "showScriptInOutput", this.showScriptInOutput);
    }

    public void visualizeScriptResults(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object object = callSiteArray[165].callGetProperty(callSiteArray[166].callGetProperty(evt));
        this.visualizeScriptResults = DefaultTypeTransformation.booleanUnbox(object);
        callSiteArray[167].call(prefs, "visualizeScriptResults", this.visualizeScriptResults);
    }

    public void showToolbar(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object object = callSiteArray[168].callGetProperty(callSiteArray[169].callGetProperty(evt));
        this.showToolbar = DefaultTypeTransformation.booleanUnbox(object);
        callSiteArray[170].call(prefs, "showToolbar", this.showToolbar);
        boolean bl = this.showToolbar;
        ScriptBytecodeAdapter.setProperty(bl, null, this.toolbar, "visible");
    }

    public void detachedOutput(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Boolean oldDetachedOutput = this.detachedOutput;
        Object object = callSiteArray[171].callGetProperty(callSiteArray[172].callGetProperty(evt));
        this.detachedOutput = DefaultTypeTransformation.booleanUnbox(object);
        callSiteArray[173].call(prefs, "detachedOutput", this.detachedOutput);
        if (ScriptBytecodeAdapter.compareNotEqual(oldDetachedOutput, this.detachedOutput)) {
            if (this.detachedOutput) {
                callSiteArray[174].call(this.splitPane, this.blank, callSiteArray[175].callGetProperty(JSplitPane.class));
                Object object2 = callSiteArray[176].callGetProperty(this.splitPane);
                this.origDividerSize = DefaultTypeTransformation.intUnbox(object2);
                int n = 0;
                ScriptBytecodeAdapter.setProperty(n, null, this.splitPane, "dividerSize");
                BigDecimal bigDecimal = $const$0;
                ScriptBytecodeAdapter.setProperty(bigDecimal, null, this.splitPane, "resizeWeight");
                callSiteArray[177].call(this.outputWindow, this.scrollArea, callSiteArray[178].callGetProperty(BorderLayout.class));
                if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    callSiteArray[179].callCurrent(this);
                } else {
                    this.prepareOutputWindow();
                }
            } else {
                callSiteArray[180].call(this.splitPane, this.scrollArea, callSiteArray[181].callGetProperty(JSplitPane.class));
                int n = this.origDividerSize;
                ScriptBytecodeAdapter.setProperty(n, null, this.splitPane, "dividerSize");
                callSiteArray[182].call(this.outputWindow, this.blank, callSiteArray[183].callGetProperty(BorderLayout.class));
                boolean bl = false;
                ScriptBytecodeAdapter.setProperty(bl, null, this.outputWindow, "visible");
                BigDecimal bigDecimal = $const$1;
                ScriptBytecodeAdapter.setProperty(bigDecimal, null, this.splitPane, "resizeWeight");
            }
        }
    }

    public void autoClearOutput(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object object = callSiteArray[184].callGetProperty(callSiteArray[185].callGetProperty(evt));
        this.autoClearOutput = DefaultTypeTransformation.booleanUnbox(object);
        callSiteArray[186].call(prefs, "autoClearOutput", this.autoClearOutput);
    }

    public void threadInterruption(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object object = callSiteArray[187].callGetProperty(callSiteArray[188].callGetProperty(evt));
        this.threadInterrupt = DefaultTypeTransformation.booleanUnbox(object);
        callSiteArray[189].call(prefs, "threadInterrupt", this.threadInterrupt);
        Object customizers = callSiteArray[190].callGetProperty(this.config);
        callSiteArray[191].call(customizers);
        if (this.threadInterrupt) {
            callSiteArray[192].call((Object)this.config, callSiteArray[193].callConstructor(ASTTransformationCustomizer.class, ThreadInterrupt.class));
        }
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object object = callSiteArray[194].call(Math.class, callSiteArray[195].callGetProperty(e), callSiteArray[196].callGetProperty(e));
        this.textSelectionStart = DefaultTypeTransformation.intUnbox(object);
        Object object2 = callSiteArray[197].call(Math.class, callSiteArray[198].callGetProperty(e), callSiteArray[199].callGetProperty(e));
        this.textSelectionEnd = DefaultTypeTransformation.intUnbox(object2);
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[200].callCurrent(this);
        } else {
            this.setRowNumAndColNum();
        }
    }

    public void clearOutput(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        String string = "";
        ScriptBytecodeAdapter.setProperty(string, null, this.outputArea, "text");
    }

    public Object askToInterruptScript() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (!this.scriptRunning) {
            return true;
        }
        Object rc = callSiteArray[201].call(JOptionPane.class, this.frame, "Script executing. Press 'OK' to attempt to interrupt it before exiting.", "GroovyConsole", callSiteArray[202].callGetProperty(JOptionPane.class));
        if (ScriptBytecodeAdapter.compareEqual(rc, callSiteArray[203].callGetProperty(JOptionPane.class))) {
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                callSiteArray[204].callCurrent(this);
            } else {
                this.doInterrupt();
            }
            return true;
        }
        return false;
    }

    public void doInterrupt(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        callSiteArray[205].callSafe(this.runThread);
    }

    public void exit(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[206].callCurrent(this)) && DefaultTypeTransformation.booleanUnbox(callSiteArray[207].callCurrent(this))) {
                if (this.frame instanceof Window) {
                    callSiteArray[208].call(this.frame);
                    callSiteArray[209].call(this.frame);
                    callSiteArray[210].callSafe(this.outputWindow);
                }
                callSiteArray[211].call(FindReplaceUtility.class);
                callSiteArray[212].call(consoleControllers, this);
                if (!DefaultTypeTransformation.booleanUnbox(consoleControllers)) {
                    callSiteArray[213].call(this.systemOutInterceptor);
                    callSiteArray[214].call(this.systemErrorInterceptor);
                }
            }
        } else if (DefaultTypeTransformation.booleanUnbox(this.askToInterruptScript()) && this.askToSaveFile()) {
            if (this.frame instanceof Window) {
                callSiteArray[215].call(this.frame);
                callSiteArray[216].call(this.frame);
                callSiteArray[217].callSafe(this.outputWindow);
            }
            callSiteArray[218].call(FindReplaceUtility.class);
            callSiteArray[219].call(consoleControllers, this);
            if (!DefaultTypeTransformation.booleanUnbox(consoleControllers)) {
                callSiteArray[220].call(this.systemOutInterceptor);
                callSiteArray[221].call(this.systemErrorInterceptor);
            }
        }
    }

    public void fileNewFile(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[222].callCurrent(this))) {
                Object var3_3 = null;
                this.scriptFile = var3_3;
                callSiteArray[223].callCurrent((GroovyObject)this, false);
                String string = "";
                ScriptBytecodeAdapter.setProperty(string, null, this.inputArea, "text");
            }
        } else if (this.askToSaveFile()) {
            Object var5_5 = null;
            this.scriptFile = var5_5;
            this.setDirty(false);
            String string = "";
            ScriptBytecodeAdapter.setProperty(string, null, this.inputArea, "text");
        }
    }

    public void fileNewWindow(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Console consoleController = (Console)ScriptBytecodeAdapter.castToType(callSiteArray[224].callConstructor(Console.class, callSiteArray[225].callConstructor(Binding.class, callSiteArray[226].callConstructor(HashMap.class, callSiteArray[227].callGetProperty(callSiteArray[228].call(this.shell))))), Console.class);
        SystemOutputInterceptor systemOutputInterceptor = this.systemOutInterceptor;
        ScriptBytecodeAdapter.setGroovyObjectProperty(systemOutputInterceptor, Console.class, consoleController, "systemOutInterceptor");
        SystemOutputInterceptor systemOutputInterceptor2 = this.systemErrorInterceptor;
        ScriptBytecodeAdapter.setGroovyObjectProperty(systemOutputInterceptor2, Console.class, consoleController, "systemErrorInterceptor");
        Reference<SwingBuilder> swing = new Reference<SwingBuilder>((SwingBuilder)ScriptBytecodeAdapter.castToType(callSiteArray[229].callConstructor(SwingBuilder.class), SwingBuilder.class));
        SwingBuilder swingBuilder = swing.get();
        ScriptBytecodeAdapter.setGroovyObjectProperty(swingBuilder, Console.class, consoleController, "swing");
        public class _fileNewWindow_closure6
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference swing;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _fileNewWindow_closure6(Object _outerInstance, Object _thisObject, Reference swing) {
                Reference reference;
                CallSite[] callSiteArray = _fileNewWindow_closure6.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.swing = reference = swing;
            }

            public Object doCall(Object k, Object v) {
                CallSite[] callSiteArray = _fileNewWindow_closure6.$getCallSiteArray();
                Object object = v;
                callSiteArray[0].call(this.swing.get(), k, object);
                return object;
            }

            public Object call(Object k, Object v) {
                CallSite[] callSiteArray = _fileNewWindow_closure6.$getCallSiteArray();
                return callSiteArray[1].callCurrent(this, k, v);
            }

            public SwingBuilder getSwing() {
                CallSite[] callSiteArray = _fileNewWindow_closure6.$getCallSiteArray();
                return (SwingBuilder)ScriptBytecodeAdapter.castToType(this.swing.get(), SwingBuilder.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _fileNewWindow_closure6.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "putAt";
                stringArray[1] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _fileNewWindow_closure6.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_fileNewWindow_closure6.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _fileNewWindow_closure6.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[230].call(frameConsoleDelegates, new _fileNewWindow_closure6(this, this, swing));
        Console console = consoleController;
        ScriptBytecodeAdapter.setGroovyObjectProperty(console, Console.class, swing.get(), "controller");
        callSiteArray[231].call((Object)swing.get(), ConsoleActions.class);
        callSiteArray[232].call((Object)swing.get(), ConsoleView.class);
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[233].callCurrent(this);
        } else {
            this.installInterceptor();
        }
        callSiteArray[234].callCurrent((GroovyObject)this, callSiteArray[235].callGroovyObjectGetProperty(swing.get()));
        callSiteArray[236].call(callSiteArray[237].callGroovyObjectGetProperty(swing.get()));
        callSiteArray[238].call(callSiteArray[239].callGroovyObjectGetProperty(swing.get()));
        callSiteArray[240].call((Object)swing.get(), ScriptBytecodeAdapter.getMethodPointer(callSiteArray[241].callGroovyObjectGetProperty(swing.get()), "requestFocus"));
    }

    public void fileOpen(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[242].callCurrent(this))) {
                Object scriptName = callSiteArray[243].callCurrent(this);
                if (ScriptBytecodeAdapter.compareNotEqual(scriptName, null)) {
                    callSiteArray[244].callCurrent((GroovyObject)this, scriptName);
                }
            }
        } else if (this.askToSaveFile()) {
            Object scriptName = this.selectFilename();
            if (ScriptBytecodeAdapter.compareNotEqual(scriptName, null)) {
                callSiteArray[245].callCurrent((GroovyObject)this, scriptName);
            }
        }
    }

    public void loadScriptFile(File file) {
        Reference<File> file2 = new Reference<File>(file);
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        public class _loadScriptFile_closure7
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _loadScriptFile_closure7(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _loadScriptFile_closure7.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _loadScriptFile_closure7.$getCallSiteArray();
                boolean bl = false;
                ScriptBytecodeAdapter.setProperty(bl, null, callSiteArray[0].callGroovyObjectGetProperty(this), "editable");
                return bl;
            }

            public Object doCall() {
                CallSite[] callSiteArray = _loadScriptFile_closure7.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _loadScriptFile_closure7.class) {
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
                stringArray[0] = "inputArea";
                return new CallSiteArray(_loadScriptFile_closure7.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _loadScriptFile_closure7.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[246].call((Object)this.swing, new _loadScriptFile_closure7(this, this));
        public class _loadScriptFile_closure8
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference file;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _loadScriptFile_closure8(Object _outerInstance, Object _thisObject, Reference file) {
                Reference reference;
                CallSite[] callSiteArray = _loadScriptFile_closure8.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.file = reference = file;
            }

            public Object doCall(Object it) {
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
                        boolean bl = true;
                        ScriptBytecodeAdapter.setProperty(bl, null, callSiteArray[0].callGroovyObjectGetProperty(this), "editable");
                        return bl;
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
                        String[] stringArray = new String[1];
                        stringArray[0] = "inputArea";
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
                Object object;
                CallSite[] callSiteArray = _loadScriptFile_closure8.$getCallSiteArray();
                try {
                    Object object2 = callSiteArray[0].call(callSiteArray[1].call(this.file.get()), "\n");
                    ScriptBytecodeAdapter.setGroovyObjectProperty(object2, _loadScriptFile_closure8.class, this, "consoleText");
                    Object t = this.file.get();
                    ScriptBytecodeAdapter.setGroovyObjectProperty(t, _loadScriptFile_closure8.class, this, "scriptFile");
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
                            Object listeners = callSiteArray[0].call(callSiteArray[1].callGetProperty(callSiteArray[2].callGroovyObjectGetProperty(this)), DocumentListener.class);
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
                                    return callSiteArray[0].call(callSiteArray[1].callGetProperty(callSiteArray[2].callGroovyObjectGetProperty(this)), it);
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
                                    stringArray[0] = "removeDocumentListener";
                                    stringArray[1] = "document";
                                    stringArray[2] = "inputArea";
                                }

                                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                    String[] stringArray = new String[3];
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
                            callSiteArray[3].call(listeners, new _closure28(this, this.getThisObject()));
                            callSiteArray[4].callCurrent(this);
                            callSiteArray[5].call(callSiteArray[6].callGetProperty(callSiteArray[7].callGroovyObjectGetProperty(this)), 0, callSiteArray[8].callGetProperty(callSiteArray[9].callGetProperty(callSiteArray[10].callGroovyObjectGetProperty(this))));
                            callSiteArray[11].call(callSiteArray[12].callGetProperty(callSiteArray[13].callGroovyObjectGetProperty(this)), 0, callSiteArray[14].callGroovyObjectGetProperty(this), null);
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
                                    return callSiteArray[0].call(callSiteArray[1].callGetProperty(callSiteArray[2].callGroovyObjectGetProperty(this)), it);
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

                                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                    stringArray[0] = "addDocumentListener";
                                    stringArray[1] = "document";
                                    stringArray[2] = "inputArea";
                                }

                                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                    String[] stringArray = new String[3];
                                    _closure29.$createCallSiteArray_1(stringArray);
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
                            callSiteArray[15].call(listeners, new _closure29(this, this.getThisObject()));
                            callSiteArray[16].callCurrent((GroovyObject)this, false);
                            int n = 0;
                            ScriptBytecodeAdapter.setProperty(n, null, callSiteArray[17].callGroovyObjectGetProperty(this), "caretPosition");
                            return n;
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

                        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                            stringArray[0] = "getListeners";
                            stringArray[1] = "document";
                            stringArray[2] = "inputArea";
                            stringArray[3] = "each";
                            stringArray[4] = "updateTitle";
                            stringArray[5] = "remove";
                            stringArray[6] = "document";
                            stringArray[7] = "inputArea";
                            stringArray[8] = "length";
                            stringArray[9] = "document";
                            stringArray[10] = "inputArea";
                            stringArray[11] = "insertString";
                            stringArray[12] = "document";
                            stringArray[13] = "inputArea";
                            stringArray[14] = "consoleText";
                            stringArray[15] = "each";
                            stringArray[16] = "setDirty";
                            stringArray[17] = "inputArea";
                        }

                        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                            String[] stringArray = new String[18];
                            _closure26.$createCallSiteArray_1(stringArray);
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
                    object = callSiteArray[2].call(callSiteArray[3].callGroovyObjectGetProperty(this), new _closure26(this, this.getThisObject()));
                }
                catch (Throwable throwable) {
                    callSiteArray[20].call(callSiteArray[21].callGroovyObjectGetProperty(this), new _closure27(this, this.getThisObject()));
                    callSiteArray[22].call(callSiteArray[23].callGroovyObjectGetProperty(this), ScriptBytecodeAdapter.getMethodPointer(callSiteArray[24].callGroovyObjectGetProperty(this), "requestFocusInWindow"));
                    callSiteArray[25].call(callSiteArray[26].callGroovyObjectGetProperty(this), ScriptBytecodeAdapter.getMethodPointer(callSiteArray[27].callGroovyObjectGetProperty(this), "requestFocusInWindow"));
                    throw throwable;
                }
                callSiteArray[4].call(callSiteArray[5].callGroovyObjectGetProperty(this), new _closure27(this, this.getThisObject()));
                callSiteArray[6].call(callSiteArray[7].callGroovyObjectGetProperty(this), ScriptBytecodeAdapter.getMethodPointer(callSiteArray[8].callGroovyObjectGetProperty(this), "requestFocusInWindow"));
                callSiteArray[9].call(callSiteArray[10].callGroovyObjectGetProperty(this), ScriptBytecodeAdapter.getMethodPointer(callSiteArray[11].callGroovyObjectGetProperty(this), "requestFocusInWindow"));
                return object;
            }

            public File getFile() {
                CallSite[] callSiteArray = _loadScriptFile_closure8.$getCallSiteArray();
                return (File)ScriptBytecodeAdapter.castToType(this.file.get(), File.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _loadScriptFile_closure8.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _loadScriptFile_closure8.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "join";
                stringArray[1] = "readLines";
                stringArray[2] = "edt";
                stringArray[3] = "swing";
                stringArray[4] = "edt";
                stringArray[5] = "swing";
                stringArray[6] = "doLater";
                stringArray[7] = "swing";
                stringArray[8] = "outputArea";
                stringArray[9] = "doLater";
                stringArray[10] = "swing";
                stringArray[11] = "inputArea";
                stringArray[12] = "edt";
                stringArray[13] = "swing";
                stringArray[14] = "doLater";
                stringArray[15] = "swing";
                stringArray[16] = "outputArea";
                stringArray[17] = "doLater";
                stringArray[18] = "swing";
                stringArray[19] = "inputArea";
                stringArray[20] = "edt";
                stringArray[21] = "swing";
                stringArray[22] = "doLater";
                stringArray[23] = "swing";
                stringArray[24] = "outputArea";
                stringArray[25] = "doLater";
                stringArray[26] = "swing";
                stringArray[27] = "inputArea";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[28];
                _loadScriptFile_closure8.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_loadScriptFile_closure8.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _loadScriptFile_closure8.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[247].call((Object)this.swing, new _loadScriptFile_closure8(this, this, file2));
    }

    public boolean fileSave(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareEqual(this.scriptFile, null)) {
            return DefaultTypeTransformation.booleanUnbox(callSiteArray[248].callCurrent((GroovyObject)this, evt));
        }
        callSiteArray[249].call(this.scriptFile, callSiteArray[250].callGetProperty(this.inputArea));
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[251].callCurrent((GroovyObject)this, false);
        } else {
            this.setDirty(false);
        }
        return true;
    }

    public boolean fileSaveAs(EventObject evt) {
        Object object;
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.scriptFile = object = callSiteArray[252].callCurrent((GroovyObject)this, "Save");
        if (ScriptBytecodeAdapter.compareNotEqual(this.scriptFile, null)) {
            callSiteArray[253].call(this.scriptFile, callSiteArray[254].callGetProperty(this.inputArea));
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                callSiteArray[255].callCurrent((GroovyObject)this, false);
            } else {
                this.setDirty(false);
            }
            return true;
        }
        return false;
    }

    public Object finishException(Throwable t, boolean executing) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (executing) {
            String string = "Execution terminated with exception.";
            ScriptBytecodeAdapter.setProperty(string, null, this.statusLabel, "text");
            Throwable throwable = t;
            ScriptBytecodeAdapter.setProperty(throwable, null, callSiteArray[256].call((Object)this.history, -1), "exception");
        } else {
            String string = "Compilation failed.";
            ScriptBytecodeAdapter.setProperty(string, null, this.statusLabel, "text");
        }
        if (t instanceof MultipleCompilationErrorsException) {
            MultipleCompilationErrorsException mcee = (MultipleCompilationErrorsException)ScriptBytecodeAdapter.castToType(t, MultipleCompilationErrorsException.class);
            ErrorCollector collector = (ErrorCollector)ScriptBytecodeAdapter.castToType(callSiteArray[257].callGetProperty(mcee), ErrorCollector.class);
            int count = DefaultTypeTransformation.intUnbox(callSiteArray[258].callGetProperty(collector));
            if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                callSiteArray[259].callCurrent(this, new GStringImpl(new Object[]{count, count > 1 ? "s" : ""}, new String[]{"", " compilation error", ":\n\n"}), this.commandStyle);
            } else {
                callSiteArray[260].callCurrent(this, new GStringImpl(new Object[]{count, count > 1 ? "s" : ""}, new String[]{"", " compilation error", ":\n\n"}), this.commandStyle);
            }
            public class _finishException_closure9
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _finishException_closure9(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _finishException_closure9.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(Object error) {
                    CallSite[] callSiteArray = _finishException_closure9.$getCallSiteArray();
                    if (error instanceof SyntaxErrorMessage) {
                        SyntaxException se = (SyntaxException)ScriptBytecodeAdapter.castToType(callSiteArray[0].callGetProperty(error), SyntaxException.class);
                        int errorLine = DefaultTypeTransformation.intUnbox(callSiteArray[1].callGetProperty(se));
                        String message = ShortTypeHandling.castToString(callSiteArray[2].callGetProperty(se));
                        Object object = callSiteArray[3].callGetPropertySafe(callSiteArray[4].callGroovyObjectGetProperty(this));
                        String scriptFileName = ShortTypeHandling.castToString(DefaultTypeTransformation.booleanUnbox(object) ? object : callSiteArray[5].callGetProperty(Console.class));
                        Object doc = callSiteArray[6].callGetProperty(callSiteArray[7].callGroovyObjectGetProperty(this));
                        Object style = callSiteArray[8].callGroovyObjectGetProperty(this);
                        Object hrefAttr = callSiteArray[9].callConstructor(SimpleAttributeSet.class);
                        callSiteArray[10].call(hrefAttr, callSiteArray[11].callGetProperty(HTML.Attribute.class), callSiteArray[12].call(callSiteArray[13].call(callSiteArray[14].call((Object)"file://", scriptFileName), ":"), errorLine));
                        callSiteArray[15].call(style, callSiteArray[16].callGetProperty(HTML.Tag.class), hrefAttr);
                        callSiteArray[17].call(doc, callSiteArray[18].callGetProperty(doc), callSiteArray[19].call((Object)message, " at "), callSiteArray[20].callGroovyObjectGetProperty(this));
                        return callSiteArray[21].call(doc, callSiteArray[22].callGetProperty(doc), new GStringImpl(new Object[]{callSiteArray[23].callGetProperty(se), callSiteArray[24].callGetProperty(se)}, new String[]{"line: ", ", column: ", "\n\n"}), style);
                    }
                    if (error instanceof Throwable) {
                        return callSiteArray[25].callCurrent((GroovyObject)this, error);
                    }
                    if (error instanceof ExceptionMessage) {
                        return callSiteArray[26].callCurrent((GroovyObject)this, callSiteArray[27].callGetProperty(error));
                    }
                    if (error instanceof SimpleMessage) {
                        Object doc = callSiteArray[28].callGetProperty(callSiteArray[29].callGroovyObjectGetProperty(this));
                        return callSiteArray[30].call(doc, callSiteArray[31].callGetProperty(doc), new GStringImpl(new Object[]{callSiteArray[32].callGetProperty(error)}, new String[]{"", "\n"}), callSiteArray[33].callConstructor(SimpleAttributeSet.class));
                    }
                    return null;
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _finishException_closure9.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "cause";
                    stringArray[1] = "line";
                    stringArray[2] = "originalMessage";
                    stringArray[3] = "name";
                    stringArray[4] = "scriptFile";
                    stringArray[5] = "DEFAULT_SCRIPT_NAME_START";
                    stringArray[6] = "styledDocument";
                    stringArray[7] = "outputArea";
                    stringArray[8] = "hyperlinkStyle";
                    stringArray[9] = "<$constructor$>";
                    stringArray[10] = "addAttribute";
                    stringArray[11] = "HREF";
                    stringArray[12] = "plus";
                    stringArray[13] = "plus";
                    stringArray[14] = "plus";
                    stringArray[15] = "addAttribute";
                    stringArray[16] = "A";
                    stringArray[17] = "insertString";
                    stringArray[18] = "length";
                    stringArray[19] = "plus";
                    stringArray[20] = "stacktraceStyle";
                    stringArray[21] = "insertString";
                    stringArray[22] = "length";
                    stringArray[23] = "line";
                    stringArray[24] = "startColumn";
                    stringArray[25] = "reportException";
                    stringArray[26] = "reportException";
                    stringArray[27] = "cause";
                    stringArray[28] = "styledDocument";
                    stringArray[29] = "outputArea";
                    stringArray[30] = "insertString";
                    stringArray[31] = "length";
                    stringArray[32] = "message";
                    stringArray[33] = "<$constructor$>";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[34];
                    _finishException_closure9.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_finishException_closure9.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _finishException_closure9.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[261].call(callSiteArray[262].callGetProperty(collector), new _finishException_closure9(this, this));
        } else {
            callSiteArray[263].callCurrent((GroovyObject)this, t);
        }
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (!executing) {
                callSiteArray[264].callCurrent(this);
            }
        } else if (!executing) {
            this.bindResults();
        }
        int n = 0;
        ScriptBytecodeAdapter.setProperty(n, null, this.outputArea, "caretPosition");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (this.detachedOutput) {
                callSiteArray[265].callCurrent(this);
                return callSiteArray[266].callCurrent(this);
            }
            return null;
        }
        if (this.detachedOutput) {
            this.prepareOutputWindow();
            this.showOutputWindow();
            return null;
        }
        return null;
    }

    private Object calcPreferredSize(Object a, Object b, Object c) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        return callSiteArray[267].call(ScriptBytecodeAdapter.createList(new Object[]{c, callSiteArray[268].call(ScriptBytecodeAdapter.createList(new Object[]{a, b}))}));
    }

    private Object reportException(Throwable t) {
        Reference<Throwable> t2 = new Reference<Throwable>(t);
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        callSiteArray[269].callCurrent(this, "Exception thrown\n", this.commandStyle);
        StringWriter sw = (StringWriter)ScriptBytecodeAdapter.castToType(callSiteArray[270].callConstructor(StringWriter.class), StringWriter.class);
        public class _reportException_closure10
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference t;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _reportException_closure10(Object _outerInstance, Object _thisObject, Reference t) {
                Reference reference;
                CallSite[] callSiteArray = _reportException_closure10.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.t = reference = t;
            }

            public Object doCall(Object pw) {
                CallSite[] callSiteArray = _reportException_closure10.$getCallSiteArray();
                return callSiteArray[0].call(callSiteArray[1].call(StackTraceUtils.class, this.t.get()), pw);
            }

            public Throwable getT() {
                CallSite[] callSiteArray = _reportException_closure10.$getCallSiteArray();
                return (Throwable)ScriptBytecodeAdapter.castToType(this.t.get(), Throwable.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _reportException_closure10.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "printStackTrace";
                stringArray[1] = "deepSanitize";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _reportException_closure10.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_reportException_closure10.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _reportException_closure10.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[271].call(callSiteArray[272].callConstructor(PrintWriter.class, sw), new _reportException_closure10(this, this, t2));
        return callSiteArray[273].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{callSiteArray[274].callGetProperty(sw)}, new String[]{"\n", "\n"}));
    }

    public Object finishNormal(Object result) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object object = result;
        ScriptBytecodeAdapter.setProperty(object, null, callSiteArray[275].call((Object)this.history, -1), "result");
        if (ScriptBytecodeAdapter.compareNotEqual(result, null)) {
            String string = "Execution complete.";
            ScriptBytecodeAdapter.setProperty(string, null, this.statusLabel, "text");
            callSiteArray[276].callCurrent(this, "Result: ", this.promptStyle);
            Object obj = this.visualizeScriptResults ? callSiteArray[277].call(OutputTransforms.class, result, callSiteArray[278].callGetProperty(callSiteArray[279].call(this.shell))) : callSiteArray[280].call(result);
            callSiteArray[281].callCurrent(this, obj, this.resultStyle);
        } else {
            String string = "Execution complete. Result was null.";
            ScriptBytecodeAdapter.setProperty(string, null, this.statusLabel, "text");
        }
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[282].callCurrent(this);
        } else {
            this.bindResults();
        }
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (this.detachedOutput) {
                callSiteArray[283].callCurrent(this);
                return callSiteArray[284].callCurrent(this);
            }
            return null;
        }
        if (this.detachedOutput) {
            this.prepareOutputWindow();
            this.showOutputWindow();
            return null;
        }
        return null;
    }

    public Object compileFinishNormal() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        String string = "Compilation complete.";
        ScriptBytecodeAdapter.setProperty(string, null, this.statusLabel, "text");
        return string;
    }

    private Object prepareOutputWindow() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        callSiteArray[285].call((Object)this.outputArea, (Object)null);
        callSiteArray[286].call(this.outputWindow);
        callSiteArray[287].call((Object)this.outputArea, ScriptBytecodeAdapter.createPojoWrapper((Dimension)ScriptBytecodeAdapter.asType(ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[288].callCurrent(this, callSiteArray[289].call(this.outputWindow), callSiteArray[290].call(this.inputEditor), 120), callSiteArray[291].callCurrent(this, callSiteArray[292].call(this.outputWindow), callSiteArray[293].call(this.inputEditor), 60)}), Dimension.class), Dimension.class));
        return callSiteArray[294].call(this.outputWindow);
    }

    public Object getLastResult() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(this.history)) {
            return null;
        }
        Object i = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[295].call(ScriptBytecodeAdapter.createRange(callSiteArray[296].call(callSiteArray[297].call(this.history), 1), 0, true)), Iterator.class);
        while (iterator.hasNext()) {
            i = iterator.next();
            if (!ScriptBytecodeAdapter.compareNotEqual(callSiteArray[298].callGetProperty(callSiteArray[299].call((Object)this.history, (Object)i)), null)) continue;
            return callSiteArray[300].callGetProperty(callSiteArray[301].call((Object)this.history, (Object)i));
        }
        return null;
    }

    public void historyNext(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareLessThan(this.historyIndex, callSiteArray[302].call(this.history))) {
                callSiteArray[303].callCurrent((GroovyObject)this, callSiteArray[304].call((Object)this.historyIndex, 1));
            } else {
                String string = "Can't go past end of history (time travel not allowed)";
                ScriptBytecodeAdapter.setProperty(string, null, this.statusLabel, "text");
                callSiteArray[305].callCurrent(this);
            }
        } else if (ScriptBytecodeAdapter.compareLessThan(this.historyIndex, callSiteArray[306].call(this.history))) {
            callSiteArray[307].callCurrent((GroovyObject)this, this.historyIndex + 1);
        } else {
            String string = "Can't go past end of history (time travel not allowed)";
            ScriptBytecodeAdapter.setProperty(string, null, this.statusLabel, "text");
            this.beep();
        }
    }

    public void historyPrev(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (this.historyIndex > 0) {
                callSiteArray[308].callCurrent((GroovyObject)this, callSiteArray[309].call((Object)this.historyIndex, 1));
            } else {
                String string = "Can't go past start of history";
                ScriptBytecodeAdapter.setProperty(string, null, this.statusLabel, "text");
                callSiteArray[310].callCurrent(this);
            }
        } else if (this.historyIndex > 0) {
            callSiteArray[311].callCurrent((GroovyObject)this, this.historyIndex - 1);
        } else {
            String string = "Can't go past start of history";
            ScriptBytecodeAdapter.setProperty(string, null, this.statusLabel, "text");
            this.beep();
        }
    }

    public void inspectLast(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareEqual(null, callSiteArray[312].callGroovyObjectGetProperty(this))) {
            callSiteArray[313].call(JOptionPane.class, this.frame, "The last result is null.", "Cannot Inspect", callSiteArray[314].callGetProperty(JOptionPane.class));
            return;
        }
        callSiteArray[315].call(ObjectBrowser.class, callSiteArray[316].callGroovyObjectGetProperty(this));
    }

    public void inspectVariables(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        callSiteArray[317].call(ObjectBrowser.class, callSiteArray[318].callGetProperty(callSiteArray[319].call(this.shell)));
    }

    public void inspectAst(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        public class _inspectAst_closure11
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _inspectAst_closure11(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _inspectAst_closure11.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _inspectAst_closure11.$getCallSiteArray();
                return callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this));
            }

            public Object doCall() {
                CallSite[] callSiteArray = _inspectAst_closure11.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _inspectAst_closure11.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "getText";
                stringArray[1] = "inputArea";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _inspectAst_closure11.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_inspectAst_closure11.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _inspectAst_closure11.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[320].call(callSiteArray[321].callConstructor(AstBrowser.class, this.inputArea, this.rootElement, callSiteArray[322].call(this.shell)), new _inspectAst_closure11(this, this));
    }

    public void largerFont(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        callSiteArray[323].callCurrent((GroovyObject)this, callSiteArray[324].call(callSiteArray[325].callGetProperty(callSiteArray[326].callGetProperty(this.inputArea)), 2));
    }

    /*
     * WARNING - void declaration
     */
    public static boolean notifySystemOut(int consoleId, String str) {
        void var1_1;
        Reference<Integer> consoleId2 = new Reference<Integer>(consoleId);
        Reference<void> str2 = new Reference<void>(var1_1);
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (!captureStdOut) {
            return true;
        }
        public class _notifySystemOut_closure12
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference consoleId;
            private /* synthetic */ Reference str;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _notifySystemOut_closure12(Object _outerInstance, Object _thisObject, Reference consoleId, Reference str) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _notifySystemOut_closure12.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.consoleId = reference2 = consoleId;
                this.str = reference = str;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _notifySystemOut_closure12.$getCallSiteArray();
                Console console = (Console)ScriptBytecodeAdapter.castToType(callSiteArray[0].callCurrent((GroovyObject)this, this.consoleId.get()), Console.class);
                if (DefaultTypeTransformation.booleanUnbox(console)) {
                    return callSiteArray[1].call(console, this.str.get(), callSiteArray[2].callGroovyObjectGetProperty(console));
                }
                public class _closure30
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference str;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure30(Object _outerInstance, Object _thisObject, Reference str) {
                        Reference reference;
                        CallSite[] callSiteArray = _closure30.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.str = reference = str;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure30.$getCallSiteArray();
                        return callSiteArray[0].call(it, this.str.get(), callSiteArray[1].callGetProperty(it));
                    }

                    public String getStr() {
                        CallSite[] callSiteArray = _closure30.$getCallSiteArray();
                        return ShortTypeHandling.castToString(this.str.get());
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

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "appendOutputLines";
                        stringArray[1] = "outputStyle";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[2];
                        _closure30.$createCallSiteArray_1(stringArray);
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
                return callSiteArray[3].call(callSiteArray[4].callGroovyObjectGetProperty(this), new _closure30(this, this.getThisObject(), this.str));
            }

            public int getConsoleId() {
                CallSite[] callSiteArray = _notifySystemOut_closure12.$getCallSiteArray();
                return DefaultTypeTransformation.intUnbox(this.consoleId.get());
            }

            public String getStr() {
                CallSite[] callSiteArray = _notifySystemOut_closure12.$getCallSiteArray();
                return ShortTypeHandling.castToString(this.str.get());
            }

            public Object doCall() {
                CallSite[] callSiteArray = _notifySystemOut_closure12.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _notifySystemOut_closure12.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "findConsoleById";
                stringArray[1] = "appendOutputLines";
                stringArray[2] = "outputStyle";
                stringArray[3] = "each";
                stringArray[4] = "consoleControllers";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[5];
                _notifySystemOut_closure12.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_notifySystemOut_closure12.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _notifySystemOut_closure12.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        _notifySystemOut_closure12 doAppend = new _notifySystemOut_closure12(Console.class, Console.class, consoleId2, str2);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[327].call(EventQueue.class))) {
            callSiteArray[328].call(doAppend);
        } else {
            callSiteArray[329].call(SwingUtilities.class, doAppend);
        }
        return false;
    }

    /*
     * WARNING - void declaration
     */
    public static boolean notifySystemErr(int consoleId, String str) {
        void var1_1;
        Reference<Integer> consoleId2 = new Reference<Integer>(consoleId);
        Reference<void> str2 = new Reference<void>(var1_1);
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (!captureStdErr) {
            return true;
        }
        public class _notifySystemErr_closure13
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference consoleId;
            private /* synthetic */ Reference str;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _notifySystemErr_closure13(Object _outerInstance, Object _thisObject, Reference consoleId, Reference str) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _notifySystemErr_closure13.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.consoleId = reference2 = consoleId;
                this.str = reference = str;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _notifySystemErr_closure13.$getCallSiteArray();
                Console console = (Console)ScriptBytecodeAdapter.castToType(callSiteArray[0].callCurrent((GroovyObject)this, this.consoleId.get()), Console.class);
                if (DefaultTypeTransformation.booleanUnbox(console)) {
                    return callSiteArray[1].call((Object)console, this.str.get());
                }
                public class _closure31
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference str;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure31(Object _outerInstance, Object _thisObject, Reference str) {
                        Reference reference;
                        CallSite[] callSiteArray = _closure31.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.str = reference = str;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure31.$getCallSiteArray();
                        return callSiteArray[0].call(it, this.str.get());
                    }

                    public String getStr() {
                        CallSite[] callSiteArray = _closure31.$getCallSiteArray();
                        return ShortTypeHandling.castToString(this.str.get());
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
                        stringArray[0] = "appendStacktrace";
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
                return callSiteArray[2].call(callSiteArray[3].callGroovyObjectGetProperty(this), new _closure31(this, this.getThisObject(), this.str));
            }

            public int getConsoleId() {
                CallSite[] callSiteArray = _notifySystemErr_closure13.$getCallSiteArray();
                return DefaultTypeTransformation.intUnbox(this.consoleId.get());
            }

            public String getStr() {
                CallSite[] callSiteArray = _notifySystemErr_closure13.$getCallSiteArray();
                return ShortTypeHandling.castToString(this.str.get());
            }

            public Object doCall() {
                CallSite[] callSiteArray = _notifySystemErr_closure13.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _notifySystemErr_closure13.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "findConsoleById";
                stringArray[1] = "appendStacktrace";
                stringArray[2] = "each";
                stringArray[3] = "consoleControllers";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[4];
                _notifySystemErr_closure13.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_notifySystemErr_closure13.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _notifySystemErr_closure13.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        _notifySystemErr_closure13 doAppend = new _notifySystemErr_closure13(Console.class, Console.class, consoleId2, str2);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[330].call(EventQueue.class))) {
            callSiteArray[331].call(doAppend);
        } else {
            callSiteArray[332].call(SwingUtilities.class, doAppend);
        }
        return false;
    }

    public int getConsoleId() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        return DefaultTypeTransformation.intUnbox(callSiteArray[333].call(System.class, this));
    }

    private static Console findConsoleById(int consoleId) {
        Reference<Integer> consoleId2 = new Reference<Integer>(consoleId);
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        public class _findConsoleById_closure14
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference consoleId;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _findConsoleById_closure14(Object _outerInstance, Object _thisObject, Reference consoleId) {
                Reference reference;
                CallSite[] callSiteArray = _findConsoleById_closure14.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.consoleId = reference = consoleId;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _findConsoleById_closure14.$getCallSiteArray();
                return ScriptBytecodeAdapter.compareEqual(callSiteArray[0].callGetProperty(it), this.consoleId.get());
            }

            public int getConsoleId() {
                CallSite[] callSiteArray = _findConsoleById_closure14.$getCallSiteArray();
                return DefaultTypeTransformation.intUnbox(this.consoleId.get());
            }

            public Object doCall() {
                CallSite[] callSiteArray = _findConsoleById_closure14.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _findConsoleById_closure14.class) {
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
                stringArray[0] = "consoleId";
                return new CallSiteArray(_findConsoleById_closure14.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _findConsoleById_closure14.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return (Console)ScriptBytecodeAdapter.castToType(callSiteArray[334].call(consoleControllers, new _findConsoleById_closure14(Console.class, Console.class, consoleId2)), Console.class);
    }

    public void runScript(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (this.saveOnRun && ScriptBytecodeAdapter.compareNotEqual(this.scriptFile, null)) {
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[335].callCurrent((GroovyObject)this, evt))) {
                    callSiteArray[336].callCurrent((GroovyObject)this, false);
                }
            } else {
                callSiteArray[337].callCurrent((GroovyObject)this, false);
            }
        } else if (this.saveOnRun && ScriptBytecodeAdapter.compareNotEqual(this.scriptFile, null)) {
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[338].callCurrent((GroovyObject)this, evt))) {
                this.runScriptImpl(false);
            }
        } else {
            this.runScriptImpl(false);
        }
    }

    public void saveOnRun(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object object = callSiteArray[339].callGetProperty(callSiteArray[340].callGetProperty(evt));
        this.saveOnRun = DefaultTypeTransformation.booleanUnbox(object);
        callSiteArray[341].call(prefs, "saveOnRun", this.saveOnRun);
    }

    public void runSelectedScript(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[342].callCurrent((GroovyObject)this, true);
        } else {
            this.runScriptImpl(true);
        }
    }

    public void addClasspathJar(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object fc = callSiteArray[343].callConstructor(JFileChooser.class, this.currentClasspathJarDir);
        Object object = callSiteArray[344].callGetProperty(JFileChooser.class);
        ScriptBytecodeAdapter.setProperty(object, null, fc, "fileSelectionMode");
        boolean bl = true;
        ScriptBytecodeAdapter.setProperty(bl, null, fc, "multiSelectionEnabled");
        boolean bl2 = true;
        ScriptBytecodeAdapter.setProperty(bl2, null, fc, "acceptAllFileFilterUsed");
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[345].call(fc, this.frame, "Add"), callSiteArray[346].callGetProperty(JFileChooser.class))) {
            Object object2 = callSiteArray[347].callGetProperty(fc);
            this.currentClasspathJarDir = (File)ScriptBytecodeAdapter.castToType(object2, File.class);
            callSiteArray[348].call(callSiteArray[349].call(Preferences.class, Console.class), "currentClasspathJarDir", callSiteArray[350].callGetProperty(this.currentClasspathJarDir));
            public class _addClasspathJar_closure15
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _addClasspathJar_closure15(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _addClasspathJar_closure15.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(Object file) {
                    CallSite[] callSiteArray = _addClasspathJar_closure15.$getCallSiteArray();
                    return callSiteArray[0].call(callSiteArray[1].call(callSiteArray[2].callGroovyObjectGetProperty(this)), callSiteArray[3].call(file));
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _addClasspathJar_closure15.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "addURL";
                    stringArray[1] = "getClassLoader";
                    stringArray[2] = "shell";
                    stringArray[3] = "toURL";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[4];
                    _addClasspathJar_closure15.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_addClasspathJar_closure15.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _addClasspathJar_closure15.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[351].callSafe(callSiteArray[352].callGetProperty(fc), new _addClasspathJar_closure15(this, this));
        }
    }

    public void addClasspathDir(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object fc = callSiteArray[353].callConstructor(JFileChooser.class, this.currentClasspathDir);
        Object object = callSiteArray[354].callGetProperty(JFileChooser.class);
        ScriptBytecodeAdapter.setProperty(object, null, fc, "fileSelectionMode");
        boolean bl = true;
        ScriptBytecodeAdapter.setProperty(bl, null, fc, "acceptAllFileFilterUsed");
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[355].call(fc, this.frame, "Add"), callSiteArray[356].callGetProperty(JFileChooser.class))) {
            Object object2 = callSiteArray[357].callGetProperty(fc);
            this.currentClasspathDir = (File)ScriptBytecodeAdapter.castToType(object2, File.class);
            callSiteArray[358].call(callSiteArray[359].call(Preferences.class, Console.class), "currentClasspathDir", callSiteArray[360].callGetProperty(this.currentClasspathDir));
            callSiteArray[361].call(callSiteArray[362].call(this.shell), callSiteArray[363].call(callSiteArray[364].callGetProperty(fc)));
        }
    }

    public void clearContext(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object binding = callSiteArray[365].callConstructor(Binding.class);
        callSiteArray[366].callCurrent(this, null, binding);
        Object object = callSiteArray[367].call(OutputTransforms.class);
        ScriptBytecodeAdapter.setProperty(object, null, callSiteArray[368].callGetProperty(binding), "_outputTransforms");
    }

    private void runScriptImpl(boolean selected) {
        boolean bl;
        boolean bl2;
        Reference<Boolean> selected2 = new Reference<Boolean>(selected);
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (this.scriptRunning) {
            String string = "Cannot run script now as a script is already running. Please wait or use \"Interrupt Script\" option.";
            ScriptBytecodeAdapter.setProperty(string, null, this.statusLabel, "text");
            return;
        }
        this.scriptRunning = bl2 = true;
        boolean bl3 = true;
        ScriptBytecodeAdapter.setProperty(bl3, null, this.interruptAction, "enabled");
        this.stackOverFlowError = bl = false;
        Object endLine = callSiteArray[369].call(System.class, "line.separator");
        Reference<Object> record = new Reference<Object>(callSiteArray[370].callConstructor(HistoryRecord.class, ScriptBytecodeAdapter.createMap(new Object[]{"allText", callSiteArray[371].call(callSiteArray[372].call(this.inputArea), endLine, "\n"), "selectionStart", this.textSelectionStart, "selectionEnd", this.textSelectionEnd})));
        callSiteArray[373].callCurrent((GroovyObject)this, record.get());
        Object object = callSiteArray[374].callConstructor(HistoryRecord.class, ScriptBytecodeAdapter.createMap(new Object[]{"allText", "", "selectionStart", 0, "selectionEnd", 0}));
        this.pendingRecord = (HistoryRecord)ScriptBytecodeAdapter.castToType(object, HistoryRecord.class);
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[375].call(prefs, "autoClearOutput", false))) {
                callSiteArray[376].callCurrent(this);
            }
        } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[377].call(prefs, "autoClearOutput", false))) {
            this.clearOutput();
        }
        if (this.showScriptInOutput) {
            Object line = null;
            Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[378].call(callSiteArray[379].call(callSiteArray[380].call(record.get(), DefaultTypeTransformation.booleanUnbox(selected2.get())), "\n")), Iterator.class);
            while (iterator.hasNext()) {
                line = iterator.next();
                callSiteArray[381].callCurrent(this, "groovy> ", this.promptStyle);
                callSiteArray[382].callCurrent(this, line, this.commandStyle);
            }
            callSiteArray[383].callCurrent(this, " \n", this.promptStyle);
        }
        public class _runScriptImpl_closure16
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference record;
            private /* synthetic */ Reference selected;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _runScriptImpl_closure16(Object _outerInstance, Object _thisObject, Reference record, Reference selected) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _runScriptImpl_closure16.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.record = reference2 = record;
                this.selected = reference = selected;
            }

            public Object doCall(Object it) {
                Reference<Object> result;
                CallSite[] callSiteArray;
                block18: {
                    String name;
                    block17: {
                        ClassLoader classLoader;
                        callSiteArray = _runScriptImpl_closure16.$getCallSiteArray();
                        callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this), callSiteArray[2].callCurrent((GroovyObject)this.getThisObject()));
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
                                return callSiteArray[0].callCurrent(this);
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

                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                String[] stringArray = new String[1];
                                stringArray[0] = "showExecutingMessage";
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
                        callSiteArray[3].call(SwingUtilities.class, new _closure32(this, this.getThisObject()));
                        name = null;
                        if (!BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                            Object object;
                            Object object2 = callSiteArray[4].callGetPropertySafe(callSiteArray[5].callGroovyObjectGetProperty(this));
                            if (DefaultTypeTransformation.booleanUnbox(object2)) {
                                object = object2;
                            } else {
                                CallSite callSite = callSiteArray[6];
                                Object object3 = callSiteArray[7].callGetProperty(Console.class);
                                Object object4 = callSiteArray[8].callGroovyObjectGetProperty(this);
                                ScriptBytecodeAdapter.setGroovyObjectProperty(callSiteArray[9].call(object4), _runScriptImpl_closure16.class, this, "scriptNameCounter");
                                object = callSite.call(object3, object4);
                            }
                            Object object5 = object;
                            name = ShortTypeHandling.castToString(object5);
                        } else {
                            Object object;
                            Object object6 = callSiteArray[10].callGetPropertySafe(callSiteArray[11].callGroovyObjectGetProperty(this));
                            if (DefaultTypeTransformation.booleanUnbox(object6)) {
                                object = object6;
                            } else {
                                CallSite callSite = callSiteArray[12];
                                Object object7 = callSiteArray[13].callGetProperty(Console.class);
                                Object object8 = callSiteArray[14].callGroovyObjectGetProperty(this);
                                ScriptBytecodeAdapter.setGroovyObjectProperty(DefaultTypeTransformation.intUnbox(object8) + 1, _runScriptImpl_closure16.class, this, "scriptNameCounter");
                                object = callSite.call(object7, object8);
                            }
                            Object object9 = object;
                            name = ShortTypeHandling.castToString(object9);
                        }
                        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[15].callGroovyObjectGetProperty(this))) {
                            callSiteArray[16].callCurrent(this);
                        }
                        result = new Reference<Object>(null);
                        result.get();
                        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[17].callGroovyObjectGetProperty(this))) break block17;
                        ClassLoader savedThreadContextClassLoader = (ClassLoader)ScriptBytecodeAdapter.castToType(callSiteArray[18].callGetProperty(callSiteArray[19].call(Thread.class)), ClassLoader.class);
                        try {
                            Object object = callSiteArray[20].callGroovyObjectGetProperty(callSiteArray[21].callGroovyObjectGetProperty(this));
                            ScriptBytecodeAdapter.setProperty(object, null, callSiteArray[22].call(Thread.class), "contextClassLoader");
                            Object object10 = callSiteArray[23].call(callSiteArray[24].callGroovyObjectGetProperty(this), callSiteArray[25].call(this.record.get(), this.selected.get()), name, ScriptBytecodeAdapter.createList(new Object[0]));
                            result.set(object10);
                            classLoader = savedThreadContextClassLoader;
                        }
                        catch (Throwable throwable) {
                            ClassLoader classLoader2 = savedThreadContextClassLoader;
                            ScriptBytecodeAdapter.setProperty(classLoader2, null, callSiteArray[27].call(Thread.class), "contextClassLoader");
                            throw throwable;
                        }
                        ScriptBytecodeAdapter.setProperty(classLoader, null, callSiteArray[26].call(Thread.class), "contextClassLoader");
                        break block18;
                    }
                    Object object = callSiteArray[28].call(callSiteArray[29].callGroovyObjectGetProperty(this), callSiteArray[30].call(this.record.get(), this.selected.get()), name, ScriptBytecodeAdapter.createList(new Object[0]));
                    result.set(object);
                }
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[31].callGroovyObjectGetProperty(this))) {
                    callSiteArray[32].callCurrent(this);
                }
                public class _closure33
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference result;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure33(Object _outerInstance, Object _thisObject, Reference result) {
                        Reference reference;
                        CallSite[] callSiteArray = _closure33.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.result = reference = result;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure33.$getCallSiteArray();
                        return callSiteArray[0].callCurrent((GroovyObject)this, this.result.get());
                    }

                    public Object getResult() {
                        CallSite[] callSiteArray = _closure33.$getCallSiteArray();
                        return this.result.get();
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

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[1];
                        stringArray[0] = "finishNormal";
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
                Object object = callSiteArray[33].call(SwingUtilities.class, new _closure33(this, this.getThisObject(), result));
                Object var19_19 = null;
                ScriptBytecodeAdapter.setGroovyObjectProperty((Thread)ScriptBytecodeAdapter.castToType(var19_19, Thread.class), _runScriptImpl_closure16.class, this, "runThread");
                boolean bl = false;
                ScriptBytecodeAdapter.setGroovyObjectProperty(bl, _runScriptImpl_closure16.class, this, "scriptRunning");
                boolean bl2 = false;
                ScriptBytecodeAdapter.setProperty(bl2, null, callSiteArray[34].callGroovyObjectGetProperty(this), "enabled");
                callSiteArray[35].call(callSiteArray[36].callGroovyObjectGetProperty(this));
                try {
                    return object;
                }
                catch (Throwable throwable) {
                    Reference<Throwable> t = new Reference<Throwable>(throwable);
                    if (t.get() instanceof StackOverflowError) {
                        boolean bl3 = true;
                        ScriptBytecodeAdapter.setGroovyObjectProperty(bl3, _runScriptImpl_closure16.class, this, "stackOverFlowError");
                        callSiteArray[37].callCurrent(this);
                    }
                    public class _closure34
                    extends Closure
                    implements GeneratedClosure {
                        private /* synthetic */ Reference t;
                        private static /* synthetic */ ClassInfo $staticClassInfo;
                        public static transient /* synthetic */ boolean __$stMC;
                        private static /* synthetic */ SoftReference $callSiteArray;

                        public _closure34(Object _outerInstance, Object _thisObject, Reference t) {
                            Reference reference;
                            CallSite[] callSiteArray = _closure34.$getCallSiteArray();
                            super(_outerInstance, _thisObject);
                            this.t = reference = t;
                        }

                        public Object doCall(Object it) {
                            CallSite[] callSiteArray = _closure34.$getCallSiteArray();
                            return callSiteArray[0].callCurrent(this, this.t.get(), true);
                        }

                        public Throwable getT() {
                            CallSite[] callSiteArray = _closure34.$getCallSiteArray();
                            return (Throwable)ScriptBytecodeAdapter.castToType(this.t.get(), Throwable.class);
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
                            stringArray[0] = "finishException";
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
                    Object object11 = callSiteArray[38].call(SwingUtilities.class, new _closure34(this, this.getThisObject(), t));
                    Object var25_25 = null;
                    ScriptBytecodeAdapter.setGroovyObjectProperty((Thread)ScriptBytecodeAdapter.castToType(var25_25, Thread.class), _runScriptImpl_closure16.class, this, "runThread");
                    boolean bl4 = false;
                    ScriptBytecodeAdapter.setGroovyObjectProperty(bl4, _runScriptImpl_closure16.class, this, "scriptRunning");
                    boolean bl5 = false;
                    ScriptBytecodeAdapter.setProperty(bl5, null, callSiteArray[39].callGroovyObjectGetProperty(this), "enabled");
                    callSiteArray[40].call(callSiteArray[41].callGroovyObjectGetProperty(this));
                    try {
                        return object11;
                    }
                    catch (Throwable throwable2) {
                        Object var32_29 = null;
                        ScriptBytecodeAdapter.setGroovyObjectProperty((Thread)ScriptBytecodeAdapter.castToType(var32_29, Thread.class), _runScriptImpl_closure16.class, this, "runThread");
                        boolean bl6 = false;
                        ScriptBytecodeAdapter.setGroovyObjectProperty(bl6, _runScriptImpl_closure16.class, this, "scriptRunning");
                        boolean bl7 = false;
                        ScriptBytecodeAdapter.setProperty(bl7, null, callSiteArray[45].callGroovyObjectGetProperty(this), "enabled");
                        callSiteArray[46].call(callSiteArray[47].callGroovyObjectGetProperty(this));
                        throw throwable2;
                    }
                }
            }

            public Object getRecord() {
                CallSite[] callSiteArray = _runScriptImpl_closure16.$getCallSiteArray();
                return this.record.get();
            }

            public boolean getSelected() {
                CallSite[] callSiteArray = _runScriptImpl_closure16.$getCallSiteArray();
                return DefaultTypeTransformation.booleanUnbox(this.selected.get());
            }

            public Object doCall() {
                CallSite[] callSiteArray = _runScriptImpl_closure16.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _runScriptImpl_closure16.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "setConsoleId";
                stringArray[1] = "systemOutInterceptor";
                stringArray[2] = "getConsoleId";
                stringArray[3] = "invokeLater";
                stringArray[4] = "name";
                stringArray[5] = "scriptFile";
                stringArray[6] = "plus";
                stringArray[7] = "DEFAULT_SCRIPT_NAME_START";
                stringArray[8] = "scriptNameCounter";
                stringArray[9] = "next";
                stringArray[10] = "name";
                stringArray[11] = "scriptFile";
                stringArray[12] = "plus";
                stringArray[13] = "DEFAULT_SCRIPT_NAME_START";
                stringArray[14] = "scriptNameCounter";
                stringArray[15] = "beforeExecution";
                stringArray[16] = "beforeExecution";
                stringArray[17] = "useScriptClassLoaderForScriptExecution";
                stringArray[18] = "contextClassLoader";
                stringArray[19] = "currentThread";
                stringArray[20] = "classLoader";
                stringArray[21] = "shell";
                stringArray[22] = "currentThread";
                stringArray[23] = "run";
                stringArray[24] = "shell";
                stringArray[25] = "getTextToRun";
                stringArray[26] = "currentThread";
                stringArray[27] = "currentThread";
                stringArray[28] = "run";
                stringArray[29] = "shell";
                stringArray[30] = "getTextToRun";
                stringArray[31] = "afterExecution";
                stringArray[32] = "afterExecution";
                stringArray[33] = "invokeLater";
                stringArray[34] = "interruptAction";
                stringArray[35] = "removeConsoleId";
                stringArray[36] = "systemOutInterceptor";
                stringArray[37] = "clearOutput";
                stringArray[38] = "invokeLater";
                stringArray[39] = "interruptAction";
                stringArray[40] = "removeConsoleId";
                stringArray[41] = "systemOutInterceptor";
                stringArray[42] = "interruptAction";
                stringArray[43] = "removeConsoleId";
                stringArray[44] = "systemOutInterceptor";
                stringArray[45] = "interruptAction";
                stringArray[46] = "removeConsoleId";
                stringArray[47] = "systemOutInterceptor";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[48];
                _runScriptImpl_closure16.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_runScriptImpl_closure16.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _runScriptImpl_closure16.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        Object object2 = callSiteArray[384].call(Thread.class, new _runScriptImpl_closure16(this, this, record, selected2));
        this.runThread = (Thread)ScriptBytecodeAdapter.castToType(object2, Thread.class);
    }

    public void compileScript(EventObject evt) {
        boolean bl;
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (this.scriptRunning) {
            String string = "Cannot compile script now as a script is already running. Please wait or use \"Interrupt Script\" option.";
            ScriptBytecodeAdapter.setProperty(string, null, this.statusLabel, "text");
            return;
        }
        this.stackOverFlowError = bl = false;
        Object endLine = callSiteArray[385].call(System.class, "line.separator");
        Reference<Object> record = new Reference<Object>(callSiteArray[386].callConstructor(HistoryRecord.class, ScriptBytecodeAdapter.createMap(new Object[]{"allText", callSiteArray[387].call(callSiteArray[388].call(this.inputArea), endLine, "\n"), "selectionStart", this.textSelectionStart, "selectionEnd", this.textSelectionEnd})));
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[389].call(prefs, "autoClearOutput", false))) {
                callSiteArray[390].callCurrent(this);
            }
        } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[391].call(prefs, "autoClearOutput", false))) {
            this.clearOutput();
        }
        if (this.showScriptInOutput) {
            Object line = null;
            Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[392].call(callSiteArray[393].call(callSiteArray[394].callGetProperty(record.get()), "\n")), Iterator.class);
            while (iterator.hasNext()) {
                line = iterator.next();
                callSiteArray[395].callCurrent(this, "groovy> ", this.promptStyle);
                callSiteArray[396].callCurrent(this, line, this.commandStyle);
            }
            callSiteArray[397].callCurrent(this, " \n", this.promptStyle);
        }
        public class _compileScript_closure17
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference record;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _compileScript_closure17(Object _outerInstance, Object _thisObject, Reference record) {
                Reference reference;
                CallSite[] callSiteArray = _compileScript_closure17.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.record = reference = record;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _compileScript_closure17.$getCallSiteArray();
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

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure35.$getCallSiteArray();
                        return callSiteArray[0].callCurrent(this);
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure35.$getCallSiteArray();
                        return this.doCall(null);
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

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[1];
                        stringArray[0] = "showCompilingMessage";
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
                callSiteArray[0].call(SwingUtilities.class, new _closure35(this, this.getThisObject()));
                callSiteArray[1].call(callSiteArray[2].call(callSiteArray[3].callGroovyObjectGetProperty(this)), callSiteArray[4].callGetProperty(this.record.get()));
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

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure36.$getCallSiteArray();
                        return callSiteArray[0].callCurrent(this);
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure36.$getCallSiteArray();
                        return this.doCall(null);
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
                        stringArray[0] = "compileFinishNormal";
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
                Object object = callSiteArray[5].call(SwingUtilities.class, new _closure36(this, this.getThisObject()));
                Object var4_4 = null;
                ScriptBytecodeAdapter.setGroovyObjectProperty((Thread)ScriptBytecodeAdapter.castToType(var4_4, Thread.class), _compileScript_closure17.class, this, "runThread");
                try {
                    try {
                        return object;
                    }
                    catch (Throwable throwable) {
                        Reference<Throwable> t = new Reference<Throwable>(throwable);
                        public class _closure37
                        extends Closure
                        implements GeneratedClosure {
                            private /* synthetic */ Reference t;
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure37(Object _outerInstance, Object _thisObject, Reference t) {
                                Reference reference;
                                CallSite[] callSiteArray = _closure37.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                                this.t = reference = t;
                            }

                            public Object doCall(Object it) {
                                CallSite[] callSiteArray = _closure37.$getCallSiteArray();
                                return callSiteArray[0].callCurrent(this, this.t.get(), false);
                            }

                            public Throwable getT() {
                                CallSite[] callSiteArray = _closure37.$getCallSiteArray();
                                return (Throwable)ScriptBytecodeAdapter.castToType(this.t.get(), Throwable.class);
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

                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                String[] stringArray = new String[1];
                                stringArray[0] = "finishException";
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
                        Object object2 = callSiteArray[6].call(SwingUtilities.class, new _closure37(this, this.getThisObject(), t));
                        return object2;
                    }
                }
                finally {
                    Object var7_7 = null;
                    ScriptBytecodeAdapter.setGroovyObjectProperty((Thread)ScriptBytecodeAdapter.castToType(var7_7, Thread.class), _compileScript_closure17.class, this, "runThread");
                }
            }

            public Object getRecord() {
                CallSite[] callSiteArray = _compileScript_closure17.$getCallSiteArray();
                return this.record.get();
            }

            public Object doCall() {
                CallSite[] callSiteArray = _compileScript_closure17.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _compileScript_closure17.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "invokeLater";
                stringArray[1] = "parseClass";
                stringArray[2] = "getClassLoader";
                stringArray[3] = "shell";
                stringArray[4] = "allText";
                stringArray[5] = "invokeLater";
                stringArray[6] = "invokeLater";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[7];
                _compileScript_closure17.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_compileScript_closure17.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _compileScript_closure17.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        Object object = callSiteArray[398].call(Thread.class, new _compileScript_closure17(this, this, record));
        this.runThread = (Thread)ScriptBytecodeAdapter.castToType(object, Thread.class);
    }

    public Object selectFilename(Object name) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object fc = callSiteArray[399].callConstructor(JFileChooser.class, this.currentFileChooserDir);
        Object object = callSiteArray[400].callGetProperty(JFileChooser.class);
        ScriptBytecodeAdapter.setProperty(object, null, fc, "fileSelectionMode");
        boolean bl = true;
        ScriptBytecodeAdapter.setProperty(bl, null, fc, "acceptAllFileFilterUsed");
        Object object2 = groovyFileFilter;
        ScriptBytecodeAdapter.setProperty(object2, null, fc, "fileFilter");
        if (ScriptBytecodeAdapter.compareEqual(name, "Save")) {
            Object object3 = callSiteArray[401].callConstructor(File.class, "*.groovy");
            ScriptBytecodeAdapter.setProperty(object3, null, fc, "selectedFile");
        }
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[402].call(fc, this.frame, name), callSiteArray[403].callGetProperty(JFileChooser.class))) {
            Object object4 = callSiteArray[404].callGetProperty(fc);
            this.currentFileChooserDir = (File)ScriptBytecodeAdapter.castToType(object4, File.class);
            callSiteArray[405].call(callSiteArray[406].call(Preferences.class, Console.class), "currentFileChooserDir", callSiteArray[407].callGetProperty(this.currentFileChooserDir));
            return callSiteArray[408].callGetProperty(fc);
        }
        return null;
    }

    public void setDirty(boolean newDirty) {
        boolean bl;
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.dirty = bl = newDirty;
        boolean bl2 = newDirty;
        ScriptBytecodeAdapter.setProperty(bl2, null, this.saveAction, "enabled");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[409].callCurrent(this);
        } else {
            this.updateTitle();
        }
    }

    private void setInputTextFromHistory(Object newIndex) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object endLine = callSiteArray[410].call(System.class, "line.separator");
        if (ScriptBytecodeAdapter.compareGreaterThanEqual(this.historyIndex, callSiteArray[411].call(this.history))) {
            Object object = callSiteArray[412].callConstructor(HistoryRecord.class, ScriptBytecodeAdapter.createMap(new Object[]{"allText", callSiteArray[413].call(callSiteArray[414].call(this.inputArea), endLine, "\n"), "selectionStart", this.textSelectionStart, "selectionEnd", this.textSelectionEnd}));
            this.pendingRecord = (HistoryRecord)ScriptBytecodeAdapter.castToType(object, HistoryRecord.class);
        }
        Object object = newIndex;
        this.historyIndex = DefaultTypeTransformation.intUnbox(object);
        Object record = null;
        if (ScriptBytecodeAdapter.compareLessThan(this.historyIndex, callSiteArray[415].call(this.history))) {
            Object object2;
            record = object2 = callSiteArray[416].call((Object)this.history, this.historyIndex);
            GStringImpl gStringImpl = new GStringImpl(new Object[]{callSiteArray[417].call(callSiteArray[418].call(this.history), this.historyIndex)}, new String[]{"command history ", ""});
            ScriptBytecodeAdapter.setProperty(gStringImpl, null, this.statusLabel, "text");
        } else {
            HistoryRecord historyRecord = this.pendingRecord;
            record = historyRecord;
            String string = "at end of history";
            ScriptBytecodeAdapter.setProperty(string, null, this.statusLabel, "text");
        }
        Object object3 = callSiteArray[419].callGetProperty(record);
        ScriptBytecodeAdapter.setProperty(object3, null, this.inputArea, "text");
        Object object4 = callSiteArray[420].callGetProperty(record);
        ScriptBytecodeAdapter.setProperty(object4, null, this.inputArea, "selectionStart");
        Object object5 = callSiteArray[421].callGetProperty(record);
        ScriptBytecodeAdapter.setProperty(object5, null, this.inputArea, "selectionEnd");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[422].callCurrent((GroovyObject)this, true);
        } else {
            this.setDirty(true);
        }
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[423].callCurrent(this);
        } else {
            this.updateHistoryActions();
        }
    }

    private void updateHistoryActions() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        boolean bl = ScriptBytecodeAdapter.compareLessThan(this.historyIndex, callSiteArray[424].call(this.history));
        ScriptBytecodeAdapter.setProperty(bl, null, this.nextHistoryAction, "enabled");
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            boolean bl2 = this.historyIndex > 0;
            ScriptBytecodeAdapter.setProperty(bl2, null, this.prevHistoryAction, "enabled");
        } else {
            boolean bl3 = this.historyIndex > 0;
            ScriptBytecodeAdapter.setProperty(bl3, null, this.prevHistoryAction, "enabled");
        }
    }

    public void setVariable(String name, Object value) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        callSiteArray[425].call(callSiteArray[426].call(this.shell), name, value);
    }

    public void showAbout(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object version = callSiteArray[427].call(GroovySystem.class);
        Object pane = callSiteArray[428].call(this.swing);
        callSiteArray[429].call(pane, callSiteArray[430].call((Object)"Welcome to the Groovy Console for evaluating Groovy scripts\nVersion ", version));
        Object dialog = callSiteArray[431].call(pane, this.frame, "About GroovyConsole");
        callSiteArray[432].call(dialog);
    }

    public void find(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        callSiteArray[433].call(FindReplaceUtility.class);
    }

    public void findNext(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        callSiteArray[434].call(callSiteArray[435].callGetProperty(FindReplaceUtility.class), evt);
    }

    public void findPrevious(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object reverseEvt = callSiteArray[436].callConstructor((Object)ActionEvent.class, ArrayUtil.createArray(callSiteArray[437].call(evt), callSiteArray[438].call(evt), callSiteArray[439].call(evt), callSiteArray[440].call(evt), callSiteArray[441].callGetProperty(ActionEvent.class)));
        callSiteArray[442].call(callSiteArray[443].callGetProperty(FindReplaceUtility.class), reverseEvt);
    }

    public void replace(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        callSiteArray[444].call(FindReplaceUtility.class, true);
    }

    public void comment(EventObject evt) {
        int n;
        int n2;
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Reference<Object> rootElement = new Reference<Object>(callSiteArray[445].callGetProperty(callSiteArray[446].callGetProperty(this.inputArea)));
        Object cursorPos = callSiteArray[447].call(this.inputArea);
        int startRow = DefaultTypeTransformation.intUnbox(callSiteArray[448].call(rootElement.get(), cursorPos));
        int endRow = 0;
        endRow = !BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass() ? (n2 = startRow) : (n = startRow);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[449].call(this.inputArea))) {
            Object selectionStart = callSiteArray[450].call(this.inputArea);
            Object object = callSiteArray[451].call(rootElement.get(), selectionStart);
            startRow = DefaultTypeTransformation.intUnbox(object);
            Object selectionEnd = callSiteArray[452].call(this.inputArea);
            Object object2 = callSiteArray[453].call(rootElement.get(), selectionEnd);
            endRow = DefaultTypeTransformation.intUnbox(object2);
        }
        Reference<Boolean> allCommented = new Reference<Boolean>(true);
        public class _comment_closure18
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference rootElement;
            private /* synthetic */ Reference allCommented;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _comment_closure18(Object _outerInstance, Object _thisObject, Reference rootElement, Reference allCommented) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _comment_closure18.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.rootElement = reference2 = rootElement;
                this.allCommented = reference = allCommented;
            }

            public Object doCall(Object rowIndex) {
                CallSite[] callSiteArray = _comment_closure18.$getCallSiteArray();
                Object rowElement = callSiteArray[0].call(this.rootElement.get(), rowIndex);
                int startOffset = DefaultTypeTransformation.intUnbox(callSiteArray[1].call(rowElement));
                int endOffset = DefaultTypeTransformation.intUnbox(callSiteArray[2].call(rowElement));
                String rowText = null;
                if (!BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    Object object = callSiteArray[3].call(callSiteArray[4].callGetProperty(callSiteArray[5].callGroovyObjectGetProperty(this)), startOffset, callSiteArray[6].call((Object)endOffset, startOffset));
                    rowText = ShortTypeHandling.castToString(object);
                } else {
                    Object object = callSiteArray[7].call(callSiteArray[8].callGetProperty(callSiteArray[9].callGroovyObjectGetProperty(this)), startOffset, endOffset - startOffset);
                    rowText = ShortTypeHandling.castToString(object);
                }
                if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    if (ScriptBytecodeAdapter.compareLessThan(callSiteArray[10].call(callSiteArray[11].call(rowText)), 2) || !DefaultTypeTransformation.booleanUnbox(callSiteArray[12].call(callSiteArray[13].call(callSiteArray[14].call(rowText), 0, 2), "//"))) {
                        boolean bl = false;
                        this.allCommented.set(bl);
                        return bl;
                    }
                    return null;
                }
                if (ScriptBytecodeAdapter.compareLessThan(callSiteArray[15].call(callSiteArray[16].call(rowText)), 2) || !DefaultTypeTransformation.booleanUnbox(callSiteArray[17].call(callSiteArray[18].call(callSiteArray[19].call(rowText), 0, 2), "//"))) {
                    boolean bl = false;
                    this.allCommented.set(bl);
                    return bl;
                }
                return null;
            }

            public Object getRootElement() {
                CallSite[] callSiteArray = _comment_closure18.$getCallSiteArray();
                return this.rootElement.get();
            }

            public Object getAllCommented() {
                CallSite[] callSiteArray = _comment_closure18.$getCallSiteArray();
                return this.allCommented.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _comment_closure18.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "getElement";
                stringArray[1] = "getStartOffset";
                stringArray[2] = "getEndOffset";
                stringArray[3] = "getText";
                stringArray[4] = "document";
                stringArray[5] = "inputArea";
                stringArray[6] = "minus";
                stringArray[7] = "getText";
                stringArray[8] = "document";
                stringArray[9] = "inputArea";
                stringArray[10] = "length";
                stringArray[11] = "trim";
                stringArray[12] = "equals";
                stringArray[13] = "substring";
                stringArray[14] = "trim";
                stringArray[15] = "length";
                stringArray[16] = "trim";
                stringArray[17] = "equals";
                stringArray[18] = "substring";
                stringArray[19] = "trim";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[20];
                _comment_closure18.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_comment_closure18.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _comment_closure18.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[454].call(startRow, endRow, new _comment_closure18(this, this, rootElement, allCommented));
        public class _comment_closure19
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference rootElement;
            private /* synthetic */ Reference allCommented;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _comment_closure19(Object _outerInstance, Object _thisObject, Reference rootElement, Reference allCommented) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _comment_closure19.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.rootElement = reference2 = rootElement;
                this.allCommented = reference = allCommented;
            }

            public Object doCall(Object rowIndex) {
                CallSite[] callSiteArray = _comment_closure19.$getCallSiteArray();
                Object rowElement = callSiteArray[0].call(this.rootElement.get(), rowIndex);
                int startOffset = DefaultTypeTransformation.intUnbox(callSiteArray[1].call(rowElement));
                int endOffset = DefaultTypeTransformation.intUnbox(callSiteArray[2].call(rowElement));
                String rowText = null;
                if (!BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    Object object = callSiteArray[3].call(callSiteArray[4].callGetProperty(callSiteArray[5].callGroovyObjectGetProperty(this)), startOffset, callSiteArray[6].call((Object)endOffset, startOffset));
                    rowText = ShortTypeHandling.castToString(object);
                } else {
                    Object object = callSiteArray[7].call(callSiteArray[8].callGetProperty(callSiteArray[9].callGroovyObjectGetProperty(this)), startOffset, endOffset - startOffset);
                    rowText = ShortTypeHandling.castToString(object);
                }
                if (DefaultTypeTransformation.booleanUnbox(this.allCommented.get())) {
                    int slashOffset = DefaultTypeTransformation.intUnbox(callSiteArray[10].call((Object)rowText, "//"));
                    if (!BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                        return callSiteArray[11].call(callSiteArray[12].callGetProperty(callSiteArray[13].callGroovyObjectGetProperty(this)), callSiteArray[14].call((Object)slashOffset, startOffset), 2);
                    }
                    return callSiteArray[15].call(callSiteArray[16].callGetProperty(callSiteArray[17].callGroovyObjectGetProperty(this)), slashOffset + startOffset, 2);
                }
                return callSiteArray[18].call(callSiteArray[19].callGetProperty(callSiteArray[20].callGroovyObjectGetProperty(this)), startOffset, "//", callSiteArray[21].callConstructor(SimpleAttributeSet.class));
            }

            public Object getRootElement() {
                CallSite[] callSiteArray = _comment_closure19.$getCallSiteArray();
                return this.rootElement.get();
            }

            public Object getAllCommented() {
                CallSite[] callSiteArray = _comment_closure19.$getCallSiteArray();
                return this.allCommented.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _comment_closure19.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "getElement";
                stringArray[1] = "getStartOffset";
                stringArray[2] = "getEndOffset";
                stringArray[3] = "getText";
                stringArray[4] = "document";
                stringArray[5] = "inputArea";
                stringArray[6] = "minus";
                stringArray[7] = "getText";
                stringArray[8] = "document";
                stringArray[9] = "inputArea";
                stringArray[10] = "indexOf";
                stringArray[11] = "remove";
                stringArray[12] = "document";
                stringArray[13] = "inputArea";
                stringArray[14] = "plus";
                stringArray[15] = "remove";
                stringArray[16] = "document";
                stringArray[17] = "inputArea";
                stringArray[18] = "insertString";
                stringArray[19] = "document";
                stringArray[20] = "inputArea";
                stringArray[21] = "<$constructor$>";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[22];
                _comment_closure19.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_comment_closure19.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _comment_closure19.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[455].call(startRow, endRow, new _comment_closure19(this, this, rootElement, allCommented));
    }

    public void showMessage(String message) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        String string = message;
        ScriptBytecodeAdapter.setProperty(string, null, this.statusLabel, "text");
    }

    public void showExecutingMessage() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        String string = "Script executing now. Please wait or use \"Interrupt Script\" option.";
        ScriptBytecodeAdapter.setProperty(string, null, this.statusLabel, "text");
    }

    public void showCompilingMessage() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        String string = "Script compiling now. Please wait.";
        ScriptBytecodeAdapter.setProperty(string, null, this.statusLabel, "text");
    }

    public void showOutputWindow(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (this.detachedOutput) {
            callSiteArray[456].call((Object)this.outputWindow, this.frame);
            callSiteArray[457].call(this.outputWindow);
        }
    }

    public void hideOutputWindow(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (this.detachedOutput) {
            boolean bl = false;
            ScriptBytecodeAdapter.setProperty(bl, null, this.outputWindow, "visible");
        }
    }

    public void hideAndClearOutputWindow(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[458].callCurrent(this);
            callSiteArray[459].callCurrent(this);
        } else {
            this.clearOutput();
            this.hideOutputWindow();
        }
    }

    public void smallerFont(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        callSiteArray[460].callCurrent((GroovyObject)this, callSiteArray[461].call(callSiteArray[462].callGetProperty(callSiteArray[463].callGetProperty(this.inputArea)), 2));
    }

    public void updateTitle() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[464].call(callSiteArray[465].callGetProperty(this.frame), "title"))) {
            if (ScriptBytecodeAdapter.compareNotEqual(this.scriptFile, null)) {
                Object object = callSiteArray[466].call(callSiteArray[467].call(callSiteArray[468].callGetProperty(this.scriptFile), this.dirty ? " * " : ""), " - GroovyConsole");
                ScriptBytecodeAdapter.setProperty(object, null, this.frame, "title");
            } else {
                String string = "GroovyConsole";
                ScriptBytecodeAdapter.setProperty(string, null, this.frame, "title");
            }
        }
    }

    private Object updateFontSize(Object newFontSize) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareGreaterThan(newFontSize, 40)) {
            int n = 40;
            newFontSize = n;
        } else if (ScriptBytecodeAdapter.compareLessThan(newFontSize, 4)) {
            int n = 4;
            newFontSize = n;
        }
        callSiteArray[469].call(prefs, "fontSize", newFontSize);
        Object newFont = callSiteArray[470].callConstructor(Font.class, callSiteArray[471].callGetProperty(this.inputEditor), callSiteArray[472].callGetProperty(Font.class), newFontSize);
        Object object = newFont;
        ScriptBytecodeAdapter.setProperty(object, null, this.inputArea, "font");
        Object object2 = newFont;
        ScriptBytecodeAdapter.setProperty(object2, null, this.outputArea, "font");
        return object2;
    }

    public void invokeTextAction(Object evt, Object closure, Object area) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object source = callSiteArray[473].call(evt);
        if (ScriptBytecodeAdapter.compareNotEqual(source, null)) {
            callSiteArray[474].call(closure, area);
        }
    }

    public void cut(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        public class _cut_closure20
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _cut_closure20(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _cut_closure20.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object source) {
                CallSite[] callSiteArray = _cut_closure20.$getCallSiteArray();
                return callSiteArray[0].call(source);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _cut_closure20.class) {
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
                stringArray[0] = "cut";
                return new CallSiteArray(_cut_closure20.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _cut_closure20.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[475].callCurrent(this, evt, new _cut_closure20(this, this));
    }

    public void copy(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Component component = this.copyFromComponent;
        public class _copy_closure21
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _copy_closure21(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _copy_closure21.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object source) {
                CallSite[] callSiteArray = _copy_closure21.$getCallSiteArray();
                return callSiteArray[0].call(source);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _copy_closure21.class) {
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
                stringArray[0] = "copy";
                return new CallSiteArray(_copy_closure21.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _copy_closure21.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[476].callCurrent(this, evt, new _copy_closure21(this, this), DefaultTypeTransformation.booleanUnbox(component) ? component : this.inputArea);
    }

    public void paste(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        public class _paste_closure22
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _paste_closure22(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _paste_closure22.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object source) {
                CallSite[] callSiteArray = _paste_closure22.$getCallSiteArray();
                return callSiteArray[0].call(source);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _paste_closure22.class) {
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
                stringArray[0] = "paste";
                return new CallSiteArray(_paste_closure22.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _paste_closure22.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[477].callCurrent(this, evt, new _paste_closure22(this, this));
    }

    public void selectAll(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        public class _selectAll_closure23
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _selectAll_closure23(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _selectAll_closure23.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object source) {
                CallSite[] callSiteArray = _selectAll_closure23.$getCallSiteArray();
                return callSiteArray[0].call(source);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _selectAll_closure23.class) {
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
                stringArray[0] = "selectAll";
                return new CallSiteArray(_selectAll_closure23.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _selectAll_closure23.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[478].callCurrent(this, evt, new _selectAll_closure23(this, this));
    }

    public void setRowNumAndColNum() {
        Object object;
        Object object2;
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object object3 = callSiteArray[479].call(this.inputArea);
        this.cursorPos = DefaultTypeTransformation.intUnbox(object3);
        Object object4 = callSiteArray[480].call(callSiteArray[481].call((Object)this.rootElement, this.cursorPos), 1);
        this.rowNum = DefaultTypeTransformation.intUnbox(object4);
        Object rowElement = null;
        rowElement = !BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass() ? (object2 = callSiteArray[482].call((Object)this.rootElement, callSiteArray[483].call((Object)this.rowNum, 1))) : (object = callSiteArray[484].call((Object)this.rootElement, this.rowNum - 1));
        Object object5 = callSiteArray[485].call(callSiteArray[486].call((Object)this.cursorPos, callSiteArray[487].call(rowElement)), 1);
        this.colNum = DefaultTypeTransformation.intUnbox(object5);
        callSiteArray[488].call((Object)this.rowNumAndColNum, new GStringImpl(new Object[]{this.rowNum, this.colNum}, new String[]{"", ":", ""}));
    }

    public void print(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        callSiteArray[489].call(callSiteArray[490].callGetProperty(this.inputEditor), evt);
    }

    public void undo(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        callSiteArray[491].call(callSiteArray[492].callGetProperty(this.inputEditor), evt);
    }

    public void redo(EventObject evt) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        callSiteArray[493].call(callSiteArray[494].callGetProperty(this.inputEditor), evt);
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent e) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[495].callGetProperty(e), callSiteArray[496].callGetProperty(HyperlinkEvent.EventType.class))) {
            String url = ShortTypeHandling.castToString(callSiteArray[497].call(e));
            int lineNumber = DefaultTypeTransformation.intUnbox(callSiteArray[498].call(callSiteArray[499].call((Object)url, ScriptBytecodeAdapter.createRange(callSiteArray[500].call(callSiteArray[501].call((Object)url, ":"), 1), -1, true))));
            Object editor = callSiteArray[502].callGetProperty(this.inputEditor);
            Object text = callSiteArray[503].callGetProperty(editor);
            int newlineBefore = 0;
            int newlineAfter = 0;
            int currentLineNumber = 1;
            int i = 0;
            if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                Object ch = null;
                Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[504].call(text), Iterator.class);
                while (iterator.hasNext()) {
                    ch = iterator.next();
                    if (ScriptBytecodeAdapter.compareEqual(ch, "\n")) {
                        int n = currentLineNumber;
                        currentLineNumber = DefaultTypeTransformation.intUnbox(callSiteArray[505].call(n));
                    }
                    if (currentLineNumber == lineNumber) {
                        int n;
                        newlineBefore = n = i;
                        Object nextNewline = callSiteArray[506].call(text, "\n", callSiteArray[507].call((Object)i, 1));
                        Object object = ScriptBytecodeAdapter.compareGreaterThan(nextNewline, -1) ? nextNewline : callSiteArray[508].call(text);
                        newlineAfter = DefaultTypeTransformation.intUnbox(object);
                        break;
                    }
                    int n = i;
                    i = DefaultTypeTransformation.intUnbox(callSiteArray[509].call(n));
                }
            } else {
                Object ch = null;
                Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[510].call(text), Iterator.class);
                while (iterator.hasNext()) {
                    ch = iterator.next();
                    if (ScriptBytecodeAdapter.compareEqual(ch, "\n")) {
                        int n = currentLineNumber;
                        int cfr_ignored_0 = n + 1;
                    }
                    if (currentLineNumber == lineNumber) {
                        int n;
                        newlineBefore = n = i;
                        Object nextNewline = callSiteArray[511].call(text, "\n", i + 1);
                        Object object = ScriptBytecodeAdapter.compareGreaterThan(nextNewline, -1) ? nextNewline : callSiteArray[512].call(text);
                        newlineAfter = DefaultTypeTransformation.intUnbox(object);
                        break;
                    }
                    int n = i;
                    int cfr_ignored_1 = n + 1;
                }
            }
            callSiteArray[513].call(editor, newlineBefore);
            callSiteArray[514].call(editor, newlineAfter);
        }
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        Object component = callSiteArray[515].call(e);
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual(component, this.outputArea) || ScriptBytecodeAdapter.compareEqual(component, this.inputArea)) {
                Object rect = callSiteArray[516].call(component);
                callSiteArray[517].call(prefs, new GStringImpl(new Object[]{callSiteArray[518].callGetProperty(component)}, new String[]{"", "Width"}), callSiteArray[519].call(callSiteArray[520].call(rect)));
                callSiteArray[521].call(prefs, new GStringImpl(new Object[]{callSiteArray[522].callGetProperty(component)}, new String[]{"", "Height"}), callSiteArray[523].call(callSiteArray[524].call(rect)));
            } else {
                callSiteArray[525].call(prefs, new GStringImpl(new Object[]{callSiteArray[526].callGetProperty(component)}, new String[]{"", "Width"}), callSiteArray[527].callGetProperty(component));
                callSiteArray[528].call(prefs, new GStringImpl(new Object[]{callSiteArray[529].callGetProperty(component)}, new String[]{"", "Height"}), callSiteArray[530].callGetProperty(component));
            }
        } else if (ScriptBytecodeAdapter.compareEqual(component, this.outputArea) || ScriptBytecodeAdapter.compareEqual(component, this.inputArea)) {
            Object rect = callSiteArray[531].call(component);
            callSiteArray[532].call(prefs, new GStringImpl(new Object[]{callSiteArray[533].callGetProperty(component)}, new String[]{"", "Width"}), callSiteArray[534].call(callSiteArray[535].call(rect)));
            callSiteArray[536].call(prefs, new GStringImpl(new Object[]{callSiteArray[537].callGetProperty(component)}, new String[]{"", "Height"}), callSiteArray[538].call(callSiteArray[539].call(rect)));
        } else {
            callSiteArray[540].call(prefs, new GStringImpl(new Object[]{callSiteArray[541].callGetProperty(component)}, new String[]{"", "Width"}), callSiteArray[542].callGetProperty(component));
            callSiteArray[543].call(prefs, new GStringImpl(new Object[]{callSiteArray[544].callGetProperty(component)}, new String[]{"", "Height"}), callSiteArray[545].callGetProperty(component));
        }
    }

    @Override
    public void componentShown(ComponentEvent e) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
    }

    @Override
    public void focusGained(FocusEvent e) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[546].callGetProperty(e), this.outputArea) || ScriptBytecodeAdapter.compareEqual(callSiteArray[547].callGetProperty(e), this.inputArea)) {
                Object object = callSiteArray[548].callGetProperty(e);
                this.copyFromComponent = (Component)ScriptBytecodeAdapter.castToType(object, Component.class);
            }
        } else if (ScriptBytecodeAdapter.compareEqual(callSiteArray[549].callGetProperty(e), this.outputArea) || ScriptBytecodeAdapter.compareEqual(callSiteArray[550].callGetProperty(e), this.inputArea)) {
            Object object = callSiteArray[551].callGetProperty(e);
            this.copyFromComponent = (Component)ScriptBytecodeAdapter.castToType(object, Component.class);
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != Console.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public void clearOutput() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.clearOutput(null);
    }

    public void doInterrupt() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.doInterrupt(null);
    }

    public void exit() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.exit(null);
    }

    public void fileNewFile() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.fileNewFile(null);
    }

    public void fileNewWindow() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.fileNewWindow(null);
    }

    public void fileOpen() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.fileOpen(null);
    }

    public boolean fileSave() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        return this.fileSave(null);
    }

    public boolean fileSaveAs() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        return this.fileSaveAs(null);
    }

    public void historyNext() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.historyNext(null);
    }

    public void historyPrev() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.historyPrev(null);
    }

    public void inspectLast() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.inspectLast(null);
    }

    public void inspectVariables() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.inspectVariables(null);
    }

    public void inspectAst() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.inspectAst(null);
    }

    public void largerFont() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.largerFont(null);
    }

    public void runScript() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.runScript(null);
    }

    public void saveOnRun() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.saveOnRun(null);
    }

    public void runSelectedScript() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.runSelectedScript(null);
    }

    public void addClasspathJar() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.addClasspathJar(null);
    }

    public void addClasspathDir() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.addClasspathDir(null);
    }

    public void clearContext() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.clearContext(null);
    }

    public void compileScript() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.compileScript(null);
    }

    public Object selectFilename() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        return this.selectFilename("Open");
    }

    public void showAbout() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.showAbout(null);
    }

    public void find() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.find(null);
    }

    public void findNext() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.findNext(null);
    }

    public void findPrevious() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.findPrevious(null);
    }

    public void replace() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.replace(null);
    }

    public void comment() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.comment(null);
    }

    public void showOutputWindow() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.showOutputWindow(null);
    }

    public void hideOutputWindow() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.hideOutputWindow(null);
    }

    public void hideAndClearOutputWindow() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.hideAndClearOutputWindow(null);
    }

    public void smallerFont() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.smallerFont(null);
    }

    public void invokeTextAction(Object evt, Object closure) {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.invokeTextAction(evt, closure, this.inputArea);
    }

    public void cut() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.cut(null);
    }

    public void copy() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.copy(null);
    }

    public void paste() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.paste(null);
    }

    public void selectAll() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.selectAll(null);
    }

    public void print() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.print(null);
    }

    public void undo() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.undo(null);
    }

    public void redo() {
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        this.redo(null);
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

    public static /* synthetic */ void __$swapInit() {
        BigDecimal bigDecimal;
        BigDecimal bigDecimal2;
        CallSite[] callSiteArray = Console.$getCallSiteArray();
        $callSiteArray = null;
        $const$0 = bigDecimal2 = new BigDecimal("1.0");
        $const$1 = bigDecimal = new BigDecimal("0.5");
    }

    static {
        Object object;
        Object object2;
        Console.__$swapInit();
        prefs = object2 = Console.$getCallSiteArray()[552].call(Preferences.class, Console.class);
        Object object3 = Console.$getCallSiteArray()[553].call(prefs, "captureStdOut", true);
        captureStdOut = DefaultTypeTransformation.booleanUnbox(object3);
        Object object4 = Console.$getCallSiteArray()[554].call(prefs, "captureStdErr", true);
        captureStdErr = DefaultTypeTransformation.booleanUnbox(object4);
        List list = ScriptBytecodeAdapter.createList(new Object[0]);
        consoleControllers = list;
        Object object5 = Console.$getCallSiteArray()[555].call(Console.$getCallSiteArray()[556].callGetProperty(Console.class), "groovy/ui/ConsoleIcon.png");
        ICON_PATH = (URL)ScriptBytecodeAdapter.castToType(object5, URL.class);
        Object object6 = Console.$getCallSiteArray()[557].call(Console.$getCallSiteArray()[558].callGetProperty(Console.class), "groovy/ui/icons/bullet_green.png");
        NODE_ICON_PATH = (URL)ScriptBytecodeAdapter.castToType(object6, URL.class);
        groovyFileFilter = object = Console.$getCallSiteArray()[559].callConstructor(GroovyFileFilter.class);
        public class __clinit__closure24
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public __clinit__closure24(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = __clinit__closure24.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = __clinit__closure24.$getCallSiteArray();
                public class _closure38
                extends Closure
                implements GeneratedClosure {
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure38(Object _outerInstance, Object _thisObject) {
                        CallSite[] callSiteArray = _closure38.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure38.$getCallSiteArray();
                        try {
                            boolean bl = true;
                            ScriptBytecodeAdapter.setProperty(bl, null, callSiteArray[0].callGroovyObjectGetProperty(this), "locationByPlatform");
                        }
                        catch (Exception e) {
                            List list = ScriptBytecodeAdapter.createList(new Object[]{100, 100});
                            ScriptBytecodeAdapter.setProperty(list, null, callSiteArray[1].callGroovyObjectGetProperty(this), "location");
                        }
                        Object object = callSiteArray[2].call(callSiteArray[3].callGroovyObjectGetProperty(this), callSiteArray[4].callGroovyObjectGetProperty(this));
                        ScriptBytecodeAdapter.setGroovyObjectProperty(object, _closure38.class, this, "containingWindows");
                        return object;
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
                        stringArray[0] = "current";
                        stringArray[1] = "current";
                        stringArray[2] = "plus";
                        stringArray[3] = "containingWindows";
                        stringArray[4] = "current";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[5];
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
                return callSiteArray[0].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"title", "GroovyConsole", "iconImage", callSiteArray[1].callGetProperty(callSiteArray[2].callCurrent((GroovyObject)this, "/groovy/ui/ConsoleIcon.png")), "defaultCloseOperation", callSiteArray[3].callGetProperty(JFrame.class)}), new _closure38(this, this.getThisObject()));
            }

            public Object doCall() {
                CallSite[] callSiteArray = __clinit__closure24.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != __clinit__closure24.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "frame";
                stringArray[1] = "image";
                stringArray[2] = "imageIcon";
                stringArray[3] = "DO_NOTHING_ON_CLOSE";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[4];
                __clinit__closure24.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(__clinit__closure24.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = __clinit__closure24.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        public class __clinit__closure25
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public __clinit__closure25(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = __clinit__closure25.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object arg) {
                CallSite[] callSiteArray = __clinit__closure25.$getCallSiteArray();
                Object object = callSiteArray[0].callCurrent((GroovyObject)this, arg);
                ScriptBytecodeAdapter.setProperty(object, null, callSiteArray[1].callGroovyObjectGetProperty(this), "JMenuBar");
                return object;
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != __clinit__closure25.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "build";
                stringArray[1] = "current";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                __clinit__closure25.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(__clinit__closure25.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = __clinit__closure25.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        Map map = ScriptBytecodeAdapter.createMap(new Object[]{"rootContainerDelegate", new __clinit__closure24(Console.class, Console.class), "menuBarDelegate", new __clinit__closure25(Console.class, Console.class)});
        frameConsoleDelegates = map;
    }

    public static String getDEFAULT_SCRIPT_NAME_START() {
        return DEFAULT_SCRIPT_NAME_START;
    }

    public static boolean getCaptureStdOut() {
        return captureStdOut;
    }

    public static boolean isCaptureStdOut() {
        return captureStdOut;
    }

    public static void setCaptureStdOut(boolean bl) {
        captureStdOut = bl;
    }

    public static boolean getCaptureStdErr() {
        return captureStdErr;
    }

    public static boolean isCaptureStdErr() {
        return captureStdErr;
    }

    public static void setCaptureStdErr(boolean bl) {
        captureStdErr = bl;
    }

    public static Object getConsoleControllers() {
        return consoleControllers;
    }

    public static void setConsoleControllers(Object object) {
        consoleControllers = object;
    }

    public boolean getFullStackTraces() {
        return this.fullStackTraces;
    }

    public boolean isFullStackTraces() {
        return this.fullStackTraces;
    }

    public void setFullStackTraces(boolean bl) {
        this.fullStackTraces = bl;
    }

    public Action getFullStackTracesAction() {
        return this.fullStackTracesAction;
    }

    public void setFullStackTracesAction(Action action) {
        this.fullStackTracesAction = action;
    }

    public boolean getShowScriptInOutput() {
        return this.showScriptInOutput;
    }

    public boolean isShowScriptInOutput() {
        return this.showScriptInOutput;
    }

    public void setShowScriptInOutput(boolean bl) {
        this.showScriptInOutput = bl;
    }

    public Action getShowScriptInOutputAction() {
        return this.showScriptInOutputAction;
    }

    public void setShowScriptInOutputAction(Action action) {
        this.showScriptInOutputAction = action;
    }

    public boolean getVisualizeScriptResults() {
        return this.visualizeScriptResults;
    }

    public boolean isVisualizeScriptResults() {
        return this.visualizeScriptResults;
    }

    public void setVisualizeScriptResults(boolean bl) {
        this.visualizeScriptResults = bl;
    }

    public Action getVisualizeScriptResultsAction() {
        return this.visualizeScriptResultsAction;
    }

    public void setVisualizeScriptResultsAction(Action action) {
        this.visualizeScriptResultsAction = action;
    }

    public boolean getShowToolbar() {
        return this.showToolbar;
    }

    public boolean isShowToolbar() {
        return this.showToolbar;
    }

    public void setShowToolbar(boolean bl) {
        this.showToolbar = bl;
    }

    public Component getToolbar() {
        return this.toolbar;
    }

    public void setToolbar(Component component) {
        this.toolbar = component;
    }

    public Action getShowToolbarAction() {
        return this.showToolbarAction;
    }

    public void setShowToolbarAction(Action action) {
        this.showToolbarAction = action;
    }

    public boolean getDetachedOutput() {
        return this.detachedOutput;
    }

    public boolean isDetachedOutput() {
        return this.detachedOutput;
    }

    public void setDetachedOutput(boolean bl) {
        this.detachedOutput = bl;
    }

    public Action getDetachedOutputAction() {
        return this.detachedOutputAction;
    }

    public void setDetachedOutputAction(Action action) {
        this.detachedOutputAction = action;
    }

    public Action getShowOutputWindowAction() {
        return this.showOutputWindowAction;
    }

    public void setShowOutputWindowAction(Action action) {
        this.showOutputWindowAction = action;
    }

    public Action getHideOutputWindowAction1() {
        return this.hideOutputWindowAction1;
    }

    public void setHideOutputWindowAction1(Action action) {
        this.hideOutputWindowAction1 = action;
    }

    public Action getHideOutputWindowAction2() {
        return this.hideOutputWindowAction2;
    }

    public void setHideOutputWindowAction2(Action action) {
        this.hideOutputWindowAction2 = action;
    }

    public Action getHideOutputWindowAction3() {
        return this.hideOutputWindowAction3;
    }

    public void setHideOutputWindowAction3(Action action) {
        this.hideOutputWindowAction3 = action;
    }

    public Action getHideOutputWindowAction4() {
        return this.hideOutputWindowAction4;
    }

    public void setHideOutputWindowAction4(Action action) {
        this.hideOutputWindowAction4 = action;
    }

    public int getOrigDividerSize() {
        return this.origDividerSize;
    }

    public void setOrigDividerSize(int n) {
        this.origDividerSize = n;
    }

    public Component getOutputWindow() {
        return this.outputWindow;
    }

    public void setOutputWindow(Component component) {
        this.outputWindow = component;
    }

    public Component getCopyFromComponent() {
        return this.copyFromComponent;
    }

    public void setCopyFromComponent(Component component) {
        this.copyFromComponent = component;
    }

    public Component getBlank() {
        return this.blank;
    }

    public void setBlank(Component component) {
        this.blank = component;
    }

    public Component getScrollArea() {
        return this.scrollArea;
    }

    public void setScrollArea(Component component) {
        this.scrollArea = component;
    }

    public boolean getAutoClearOutput() {
        return this.autoClearOutput;
    }

    public boolean isAutoClearOutput() {
        return this.autoClearOutput;
    }

    public void setAutoClearOutput(boolean bl) {
        this.autoClearOutput = bl;
    }

    public Action getAutoClearOutputAction() {
        return this.autoClearOutputAction;
    }

    public void setAutoClearOutputAction(Action action) {
        this.autoClearOutputAction = action;
    }

    public boolean getThreadInterrupt() {
        return this.threadInterrupt;
    }

    public boolean isThreadInterrupt() {
        return this.threadInterrupt;
    }

    public void setThreadInterrupt(boolean bl) {
        this.threadInterrupt = bl;
    }

    public Action getThreadInterruptAction() {
        return this.threadInterruptAction;
    }

    public void setThreadInterruptAction(Action action) {
        this.threadInterruptAction = action;
    }

    public boolean getSaveOnRun() {
        return this.saveOnRun;
    }

    public boolean isSaveOnRun() {
        return this.saveOnRun;
    }

    public void setSaveOnRun(boolean bl) {
        this.saveOnRun = bl;
    }

    public Action getSaveOnRunAction() {
        return this.saveOnRunAction;
    }

    public void setSaveOnRunAction(Action action) {
        this.saveOnRunAction = action;
    }

    public boolean getUseScriptClassLoaderForScriptExecution() {
        return this.useScriptClassLoaderForScriptExecution;
    }

    public boolean isUseScriptClassLoaderForScriptExecution() {
        return this.useScriptClassLoaderForScriptExecution;
    }

    public void setUseScriptClassLoaderForScriptExecution(boolean bl) {
        this.useScriptClassLoaderForScriptExecution = bl;
    }

    public int getMaxHistory() {
        return this.maxHistory;
    }

    public void setMaxHistory(int n) {
        this.maxHistory = n;
    }

    public int getMaxOutputChars() {
        return this.maxOutputChars;
    }

    public void setMaxOutputChars(int n) {
        this.maxOutputChars = n;
    }

    public SwingBuilder getSwing() {
        return this.swing;
    }

    public void setSwing(SwingBuilder swingBuilder) {
        this.swing = swingBuilder;
    }

    public RootPaneContainer getFrame() {
        return this.frame;
    }

    public void setFrame(RootPaneContainer rootPaneContainer) {
        this.frame = rootPaneContainer;
    }

    public ConsoleTextEditor getInputEditor() {
        return this.inputEditor;
    }

    public void setInputEditor(ConsoleTextEditor consoleTextEditor) {
        this.inputEditor = consoleTextEditor;
    }

    public JSplitPane getSplitPane() {
        return this.splitPane;
    }

    public void setSplitPane(JSplitPane jSplitPane) {
        this.splitPane = jSplitPane;
    }

    public JTextPane getInputArea() {
        return this.inputArea;
    }

    public void setInputArea(JTextPane jTextPane) {
        this.inputArea = jTextPane;
    }

    public JTextPane getOutputArea() {
        return this.outputArea;
    }

    public void setOutputArea(JTextPane jTextPane) {
        this.outputArea = jTextPane;
    }

    public JLabel getStatusLabel() {
        return this.statusLabel;
    }

    public void setStatusLabel(JLabel jLabel) {
        this.statusLabel = jLabel;
    }

    public JLabel getRowNumAndColNum() {
        return this.rowNumAndColNum;
    }

    public void setRowNumAndColNum(JLabel jLabel) {
        this.rowNumAndColNum = jLabel;
    }

    public Element getRootElement() {
        return this.rootElement;
    }

    public void setRootElement(Element element) {
        this.rootElement = element;
    }

    public int getCursorPos() {
        return this.cursorPos;
    }

    public void setCursorPos(int n) {
        this.cursorPos = n;
    }

    public int getRowNum() {
        return this.rowNum;
    }

    public void setRowNum(int n) {
        this.rowNum = n;
    }

    public int getColNum() {
        return this.colNum;
    }

    public void setColNum(int n) {
        this.colNum = n;
    }

    public Style getPromptStyle() {
        return this.promptStyle;
    }

    public void setPromptStyle(Style style) {
        this.promptStyle = style;
    }

    public Style getCommandStyle() {
        return this.commandStyle;
    }

    public void setCommandStyle(Style style) {
        this.commandStyle = style;
    }

    public Style getOutputStyle() {
        return this.outputStyle;
    }

    public void setOutputStyle(Style style) {
        this.outputStyle = style;
    }

    public Style getStacktraceStyle() {
        return this.stacktraceStyle;
    }

    public void setStacktraceStyle(Style style) {
        this.stacktraceStyle = style;
    }

    public Style getHyperlinkStyle() {
        return this.hyperlinkStyle;
    }

    public void setHyperlinkStyle(Style style) {
        this.hyperlinkStyle = style;
    }

    public Style getResultStyle() {
        return this.resultStyle;
    }

    public void setResultStyle(Style style) {
        this.resultStyle = style;
    }

    public List getHistory() {
        return this.history;
    }

    public void setHistory(List list) {
        this.history = list;
    }

    public int getHistoryIndex() {
        return this.historyIndex;
    }

    public void setHistoryIndex(int n) {
        this.historyIndex = n;
    }

    public HistoryRecord getPendingRecord() {
        return this.pendingRecord;
    }

    public void setPendingRecord(HistoryRecord historyRecord) {
        this.pendingRecord = historyRecord;
    }

    public Action getPrevHistoryAction() {
        return this.prevHistoryAction;
    }

    public void setPrevHistoryAction(Action action) {
        this.prevHistoryAction = action;
    }

    public Action getNextHistoryAction() {
        return this.nextHistoryAction;
    }

    public void setNextHistoryAction(Action action) {
        this.nextHistoryAction = action;
    }

    public boolean getDirty() {
        return this.dirty;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public Action getSaveAction() {
        return this.saveAction;
    }

    public void setSaveAction(Action action) {
        this.saveAction = action;
    }

    public int getTextSelectionStart() {
        return this.textSelectionStart;
    }

    public void setTextSelectionStart(int n) {
        this.textSelectionStart = n;
    }

    public int getTextSelectionEnd() {
        return this.textSelectionEnd;
    }

    public void setTextSelectionEnd(int n) {
        this.textSelectionEnd = n;
    }

    public Object getScriptFile() {
        return this.scriptFile;
    }

    public void setScriptFile(Object object) {
        this.scriptFile = object;
    }

    public File getCurrentFileChooserDir() {
        return this.currentFileChooserDir;
    }

    public void setCurrentFileChooserDir(File file) {
        this.currentFileChooserDir = file;
    }

    public File getCurrentClasspathJarDir() {
        return this.currentClasspathJarDir;
    }

    public void setCurrentClasspathJarDir(File file) {
        this.currentClasspathJarDir = file;
    }

    public File getCurrentClasspathDir() {
        return this.currentClasspathDir;
    }

    public void setCurrentClasspathDir(File file) {
        this.currentClasspathDir = file;
    }

    public CompilerConfiguration getConfig() {
        return this.config;
    }

    public void setConfig(CompilerConfiguration compilerConfiguration) {
        this.config = compilerConfiguration;
    }

    public GroovyShell getShell() {
        return this.shell;
    }

    public void setShell(GroovyShell groovyShell) {
        this.shell = groovyShell;
    }

    public int getScriptNameCounter() {
        return this.scriptNameCounter;
    }

    public void setScriptNameCounter(int n) {
        this.scriptNameCounter = n;
    }

    public SystemOutputInterceptor getSystemOutInterceptor() {
        return this.systemOutInterceptor;
    }

    public void setSystemOutInterceptor(SystemOutputInterceptor systemOutputInterceptor) {
        this.systemOutInterceptor = systemOutputInterceptor;
    }

    public SystemOutputInterceptor getSystemErrorInterceptor() {
        return this.systemErrorInterceptor;
    }

    public void setSystemErrorInterceptor(SystemOutputInterceptor systemOutputInterceptor) {
        this.systemErrorInterceptor = systemOutputInterceptor;
    }

    public Thread getRunThread() {
        return this.runThread;
    }

    public void setRunThread(Thread thread) {
        this.runThread = thread;
    }

    public Closure getBeforeExecution() {
        return this.beforeExecution;
    }

    public void setBeforeExecution(Closure closure) {
        this.beforeExecution = closure;
    }

    public Closure getAfterExecution() {
        return this.afterExecution;
    }

    public void setAfterExecution(Closure closure) {
        this.afterExecution = closure;
    }

    public static Object getGroovyFileFilter() {
        return groovyFileFilter;
    }

    public static void setGroovyFileFilter(Object object) {
        groovyFileFilter = object;
    }

    public boolean getScriptRunning() {
        return this.scriptRunning;
    }

    public boolean isScriptRunning() {
        return this.scriptRunning;
    }

    public void setScriptRunning(boolean bl) {
        this.scriptRunning = bl;
    }

    public boolean getStackOverFlowError() {
        return this.stackOverFlowError;
    }

    public boolean isStackOverFlowError() {
        return this.stackOverFlowError;
    }

    public void setStackOverFlowError(boolean bl) {
        this.stackOverFlowError = bl;
    }

    public Action getInterruptAction() {
        return this.interruptAction;
    }

    public void setInterruptAction(Action action) {
        this.interruptAction = action;
    }

    public static Object getFrameConsoleDelegates() {
        return frameConsoleDelegates;
    }

    public static void setFrameConsoleDelegates(Object object) {
        frameConsoleDelegates = object;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "getBoolean";
        stringArray[3] = "valueOf";
        stringArray[4] = "getProperty";
        stringArray[5] = "getBoolean";
        stringArray[6] = "getBoolean";
        stringArray[7] = "getBoolean";
        stringArray[8] = "getBoolean";
        stringArray[9] = "getBoolean";
        stringArray[10] = "getBoolean";
        stringArray[11] = "getBoolean";
        stringArray[12] = "getProperty";
        stringArray[13] = "<$constructor$>";
        stringArray[14] = "<$constructor$>";
        stringArray[15] = "get";
        stringArray[16] = "userNodeForPackage";
        stringArray[17] = "<$constructor$>";
        stringArray[18] = "get";
        stringArray[19] = "userNodeForPackage";
        stringArray[20] = "<$constructor$>";
        stringArray[21] = "get";
        stringArray[22] = "userNodeForPackage";
        stringArray[23] = "newScript";
        stringArray[24] = "setProperty";
        stringArray[25] = "getProperty";
        stringArray[26] = "toString";
        stringArray[27] = "getBoolean";
        stringArray[28] = "plus";
        stringArray[29] = "addListener";
        stringArray[30] = "newInstance";
        stringArray[31] = "loadOutputTransforms";
        stringArray[32] = "variables";
        stringArray[33] = "length";
        stringArray[34] = "getAt";
        stringArray[35] = "println";
        stringArray[36] = "length";
        stringArray[37] = "getAt";
        stringArray[38] = "println";
        stringArray[39] = "getLogger";
        stringArray[40] = "STACK_LOG_NAME";
        stringArray[41] = "setLookAndFeel";
        stringArray[42] = "getSystemLookAndFeelClassName";
        stringArray[43] = "<$constructor$>";
        stringArray[44] = "getRootLoader";
        stringArray[45] = "classLoader";
        stringArray[46] = "run";
        stringArray[47] = "length";
        stringArray[48] = "loadScriptFile";
        stringArray[49] = "getAt";
        stringArray[50] = "<$constructor$>";
        stringArray[51] = "addCompilationCustomizers";
        stringArray[52] = "<$constructor$>";
        stringArray[53] = "<$constructor$>";
        stringArray[54] = "run";
        stringArray[55] = "run";
        stringArray[56] = "<$constructor$>";
        stringArray[57] = "each";
        stringArray[58] = "setProperty";
        stringArray[59] = "build";
        stringArray[60] = "build";
        stringArray[61] = "bindResults";
        stringArray[62] = "bind";
        stringArray[63] = "undoAction";
        stringArray[64] = "inputEditor";
        stringArray[65] = "undoAction";
        stringArray[66] = "bind";
        stringArray[67] = "redoAction";
        stringArray[68] = "inputEditor";
        stringArray[69] = "redoAction";
        stringArray[70] = "consoleFrame";
        stringArray[71] = "nativeFullScreenForMac";
        stringArray[72] = "consoleFrame";
        stringArray[73] = "pack";
        stringArray[74] = "consoleFrame";
        stringArray[75] = "show";
        stringArray[76] = "consoleFrame";
        stringArray[77] = "installInterceptor";
        stringArray[78] = "doLater";
        stringArray[79] = "contains";
        stringArray[80] = "getProperty";
        stringArray[81] = "evaluate";
        stringArray[82] = "<$constructor$>";
        stringArray[83] = "<$constructor$>";
        stringArray[84] = "<$constructor$>";
        stringArray[85] = "start";
        stringArray[86] = "<$constructor$>";
        stringArray[87] = "start";
        stringArray[88] = "add";
        stringArray[89] = "size";
        stringArray[90] = "remove";
        stringArray[91] = "size";
        stringArray[92] = "updateHistoryActions";
        stringArray[93] = "length";
        stringArray[94] = "remove";
        stringArray[95] = "minus";
        stringArray[96] = "length";
        stringArray[97] = "styledDocument";
        stringArray[98] = "insertString";
        stringArray[99] = "length";
        stringArray[100] = "ensureNoDocLengthOverflow";
        stringArray[101] = "appendOutput";
        stringArray[102] = "toString";
        stringArray[103] = "appendOutput";
        stringArray[104] = "toString";
        stringArray[105] = "<$constructor$>";
        stringArray[106] = "addAttribute";
        stringArray[107] = "NameAttribute";
        stringArray[108] = "setComponent";
        stringArray[109] = "appendOutput";
        stringArray[110] = "toString";
        stringArray[111] = "<$constructor$>";
        stringArray[112] = "addAttribute";
        stringArray[113] = "NameAttribute";
        stringArray[114] = "setIcon";
        stringArray[115] = "appendOutput";
        stringArray[116] = "toString";
        stringArray[117] = "styledDocument";
        stringArray[118] = "split";
        stringArray[119] = "each";
        stringArray[120] = "ensureNoDocLengthOverflow";
        stringArray[121] = "styledDocument";
        stringArray[122] = "length";
        stringArray[123] = "getText";
        stringArray[124] = "minus";
        stringArray[125] = "getText";
        stringArray[126] = "minus";
        stringArray[127] = "insertString";
        stringArray[128] = "length";
        stringArray[129] = "remove";
        stringArray[130] = "appendOutput";
        stringArray[131] = "appendOutput";
        stringArray[132] = "styledDocument";
        stringArray[133] = "length";
        stringArray[134] = "insertString";
        stringArray[135] = "remove";
        stringArray[136] = "showConfirmDialog";
        stringArray[137] = "plus";
        stringArray[138] = "plus";
        stringArray[139] = "name";
        stringArray[140] = "YES_NO_CANCEL_OPTION";
        stringArray[141] = "YES_OPTION";
        stringArray[142] = "fileSave";
        stringArray[143] = "NO_OPTION";
        stringArray[144] = "beep";
        stringArray[145] = "defaultToolkit";
        stringArray[146] = "setVariable";
        stringArray[147] = "getLastResult";
        stringArray[148] = "setVariable";
        stringArray[149] = "setVariable";
        stringArray[150] = "collect";
        stringArray[151] = "selected";
        stringArray[152] = "source";
        stringArray[153] = "putBoolean";
        stringArray[154] = "selected";
        stringArray[155] = "source";
        stringArray[156] = "putBoolean";
        stringArray[157] = "selected";
        stringArray[158] = "source";
        stringArray[159] = "setProperty";
        stringArray[160] = "toString";
        stringArray[161] = "putBoolean";
        stringArray[162] = "selected";
        stringArray[163] = "source";
        stringArray[164] = "putBoolean";
        stringArray[165] = "selected";
        stringArray[166] = "source";
        stringArray[167] = "putBoolean";
        stringArray[168] = "selected";
        stringArray[169] = "source";
        stringArray[170] = "putBoolean";
        stringArray[171] = "selected";
        stringArray[172] = "source";
        stringArray[173] = "putBoolean";
        stringArray[174] = "add";
        stringArray[175] = "BOTTOM";
        stringArray[176] = "dividerSize";
        stringArray[177] = "add";
        stringArray[178] = "CENTER";
        stringArray[179] = "prepareOutputWindow";
        stringArray[180] = "add";
        stringArray[181] = "BOTTOM";
        stringArray[182] = "add";
        stringArray[183] = "CENTER";
        stringArray[184] = "selected";
        stringArray[185] = "source";
        stringArray[186] = "putBoolean";
        stringArray[187] = "selected";
        stringArray[188] = "source";
        stringArray[189] = "putBoolean";
        stringArray[190] = "compilationCustomizers";
        stringArray[191] = "clear";
        stringArray[192] = "addCompilationCustomizers";
        stringArray[193] = "<$constructor$>";
        stringArray[194] = "min";
        stringArray[195] = "dot";
        stringArray[196] = "mark";
        stringArray[197] = "max";
        stringArray[198] = "dot";
        stringArray[199] = "mark";
        stringArray[200] = "setRowNumAndColNum";
        stringArray[201] = "showConfirmDialog";
        stringArray[202] = "OK_CANCEL_OPTION";
        stringArray[203] = "OK_OPTION";
        stringArray[204] = "doInterrupt";
        stringArray[205] = "interrupt";
        stringArray[206] = "askToInterruptScript";
        stringArray[207] = "askToSaveFile";
        stringArray[208] = "hide";
        stringArray[209] = "dispose";
        stringArray[210] = "dispose";
        stringArray[211] = "dispose";
        stringArray[212] = "remove";
        stringArray[213] = "stop";
        stringArray[214] = "stop";
        stringArray[215] = "hide";
        stringArray[216] = "dispose";
        stringArray[217] = "dispose";
        stringArray[218] = "dispose";
        stringArray[219] = "remove";
        stringArray[220] = "stop";
        stringArray[221] = "stop";
        stringArray[222] = "askToSaveFile";
        stringArray[223] = "setDirty";
        stringArray[224] = "<$constructor$>";
        stringArray[225] = "<$constructor$>";
        stringArray[226] = "<$constructor$>";
        stringArray[227] = "variables";
        stringArray[228] = "getContext";
        stringArray[229] = "<$constructor$>";
        stringArray[230] = "each";
        stringArray[231] = "build";
        stringArray[232] = "build";
        stringArray[233] = "installInterceptor";
        stringArray[234] = "nativeFullScreenForMac";
        stringArray[235] = "consoleFrame";
        stringArray[236] = "pack";
        stringArray[237] = "consoleFrame";
        stringArray[238] = "show";
        stringArray[239] = "consoleFrame";
        stringArray[240] = "doLater";
        stringArray[241] = "inputArea";
        stringArray[242] = "askToSaveFile";
        stringArray[243] = "selectFilename";
        stringArray[244] = "loadScriptFile";
        stringArray[245] = "loadScriptFile";
        stringArray[246] = "edt";
        stringArray[247] = "doOutside";
        stringArray[248] = "fileSaveAs";
        stringArray[249] = "write";
        stringArray[250] = "text";
        stringArray[251] = "setDirty";
        stringArray[252] = "selectFilename";
        stringArray[253] = "write";
        stringArray[254] = "text";
        stringArray[255] = "setDirty";
        stringArray[256] = "getAt";
        stringArray[257] = "errorCollector";
        stringArray[258] = "errorCount";
        stringArray[259] = "appendOutputNl";
        stringArray[260] = "appendOutputNl";
        stringArray[261] = "each";
        stringArray[262] = "errors";
        stringArray[263] = "reportException";
        stringArray[264] = "bindResults";
        stringArray[265] = "prepareOutputWindow";
        stringArray[266] = "showOutputWindow";
        stringArray[267] = "max";
        stringArray[268] = "min";
        stringArray[269] = "appendOutputNl";
        stringArray[270] = "<$constructor$>";
        stringArray[271] = "withWriter";
        stringArray[272] = "<$constructor$>";
        stringArray[273] = "appendStacktrace";
        stringArray[274] = "buffer";
        stringArray[275] = "getAt";
        stringArray[276] = "appendOutputNl";
        stringArray[277] = "transformResult";
        stringArray[278] = "_outputTransforms";
        stringArray[279] = "getContext";
        stringArray[280] = "toString";
        stringArray[281] = "appendOutput";
        stringArray[282] = "bindResults";
        stringArray[283] = "prepareOutputWindow";
        stringArray[284] = "showOutputWindow";
        stringArray[285] = "setPreferredSize";
        stringArray[286] = "pack";
        stringArray[287] = "setPreferredSize";
        stringArray[288] = "calcPreferredSize";
        stringArray[289] = "getWidth";
        stringArray[290] = "getWidth";
        stringArray[291] = "calcPreferredSize";
        stringArray[292] = "getHeight";
        stringArray[293] = "getHeight";
        stringArray[294] = "pack";
        stringArray[295] = "iterator";
        stringArray[296] = "minus";
        stringArray[297] = "size";
        stringArray[298] = "result";
        stringArray[299] = "getAt";
        stringArray[300] = "result";
        stringArray[301] = "getAt";
        stringArray[302] = "size";
        stringArray[303] = "setInputTextFromHistory";
        stringArray[304] = "plus";
        stringArray[305] = "beep";
        stringArray[306] = "size";
        stringArray[307] = "setInputTextFromHistory";
        stringArray[308] = "setInputTextFromHistory";
        stringArray[309] = "minus";
        stringArray[310] = "beep";
        stringArray[311] = "setInputTextFromHistory";
        stringArray[312] = "lastResult";
        stringArray[313] = "showMessageDialog";
        stringArray[314] = "INFORMATION_MESSAGE";
        stringArray[315] = "inspect";
        stringArray[316] = "lastResult";
        stringArray[317] = "inspect";
        stringArray[318] = "variables";
        stringArray[319] = "getContext";
        stringArray[320] = "run";
        stringArray[321] = "<$constructor$>";
        stringArray[322] = "getClassLoader";
        stringArray[323] = "updateFontSize";
        stringArray[324] = "plus";
        stringArray[325] = "size";
        stringArray[326] = "font";
        stringArray[327] = "isDispatchThread";
        stringArray[328] = "call";
        stringArray[329] = "invokeLater";
        stringArray[330] = "isDispatchThread";
        stringArray[331] = "call";
        stringArray[332] = "invokeLater";
        stringArray[333] = "identityHashCode";
        stringArray[334] = "find";
        stringArray[335] = "fileSave";
        stringArray[336] = "runScriptImpl";
        stringArray[337] = "runScriptImpl";
        stringArray[338] = "fileSave";
        stringArray[339] = "selected";
        stringArray[340] = "source";
        stringArray[341] = "putBoolean";
        stringArray[342] = "runScriptImpl";
        stringArray[343] = "<$constructor$>";
        stringArray[344] = "FILES_ONLY";
        stringArray[345] = "showDialog";
        stringArray[346] = "APPROVE_OPTION";
        stringArray[347] = "currentDirectory";
        stringArray[348] = "put";
        stringArray[349] = "userNodeForPackage";
        stringArray[350] = "path";
        stringArray[351] = "each";
        stringArray[352] = "selectedFiles";
        stringArray[353] = "<$constructor$>";
        stringArray[354] = "DIRECTORIES_ONLY";
        stringArray[355] = "showDialog";
        stringArray[356] = "APPROVE_OPTION";
        stringArray[357] = "currentDirectory";
        stringArray[358] = "put";
        stringArray[359] = "userNodeForPackage";
        stringArray[360] = "path";
        stringArray[361] = "addURL";
        stringArray[362] = "getClassLoader";
        stringArray[363] = "toURL";
        stringArray[364] = "selectedFile";
        stringArray[365] = "<$constructor$>";
        stringArray[366] = "newScript";
        stringArray[367] = "loadOutputTransforms";
        stringArray[368] = "variables";
        stringArray[369] = "getProperty";
        stringArray[370] = "<$constructor$>";
        stringArray[371] = "replaceAll";
        stringArray[372] = "getText";
        stringArray[373] = "addToHistory";
        stringArray[374] = "<$constructor$>";
        stringArray[375] = "getBoolean";
        stringArray[376] = "clearOutput";
        stringArray[377] = "getBoolean";
        stringArray[378] = "iterator";
        stringArray[379] = "tokenize";
        stringArray[380] = "getTextToRun";
        stringArray[381] = "appendOutputNl";
        stringArray[382] = "appendOutput";
        stringArray[383] = "appendOutputNl";
        stringArray[384] = "start";
        stringArray[385] = "getProperty";
        stringArray[386] = "<$constructor$>";
        stringArray[387] = "replaceAll";
        stringArray[388] = "getText";
        stringArray[389] = "getBoolean";
        stringArray[390] = "clearOutput";
        stringArray[391] = "getBoolean";
        stringArray[392] = "iterator";
        stringArray[393] = "tokenize";
        stringArray[394] = "allText";
        stringArray[395] = "appendOutputNl";
        stringArray[396] = "appendOutput";
        stringArray[397] = "appendOutputNl";
        stringArray[398] = "start";
        stringArray[399] = "<$constructor$>";
        stringArray[400] = "FILES_ONLY";
        stringArray[401] = "<$constructor$>";
        stringArray[402] = "showDialog";
        stringArray[403] = "APPROVE_OPTION";
        stringArray[404] = "currentDirectory";
        stringArray[405] = "put";
        stringArray[406] = "userNodeForPackage";
        stringArray[407] = "path";
        stringArray[408] = "selectedFile";
        stringArray[409] = "updateTitle";
        stringArray[410] = "getProperty";
        stringArray[411] = "size";
        stringArray[412] = "<$constructor$>";
        stringArray[413] = "replaceAll";
        stringArray[414] = "getText";
        stringArray[415] = "size";
        stringArray[416] = "getAt";
        stringArray[417] = "minus";
        stringArray[418] = "size";
        stringArray[419] = "allText";
        stringArray[420] = "selectionStart";
        stringArray[421] = "selectionEnd";
        stringArray[422] = "setDirty";
        stringArray[423] = "updateHistoryActions";
        stringArray[424] = "size";
        stringArray[425] = "setVariable";
        stringArray[426] = "getContext";
        stringArray[427] = "getVersion";
        stringArray[428] = "optionPane";
        stringArray[429] = "setMessage";
        stringArray[430] = "plus";
        stringArray[431] = "createDialog";
        stringArray[432] = "show";
        stringArray[433] = "showDialog";
        stringArray[434] = "actionPerformed";
        stringArray[435] = "FIND_ACTION";
        stringArray[436] = "<$constructor$>";
        stringArray[437] = "getSource";
        stringArray[438] = "getID";
        stringArray[439] = "getActionCommand";
        stringArray[440] = "getWhen";
        stringArray[441] = "SHIFT_MASK";
        stringArray[442] = "actionPerformed";
        stringArray[443] = "FIND_ACTION";
        stringArray[444] = "showDialog";
        stringArray[445] = "defaultRootElement";
        stringArray[446] = "document";
        stringArray[447] = "getCaretPosition";
        stringArray[448] = "getElementIndex";
        stringArray[449] = "getSelectedText";
        stringArray[450] = "getSelectionStart";
        stringArray[451] = "getElementIndex";
        stringArray[452] = "getSelectionEnd";
        stringArray[453] = "getElementIndex";
        stringArray[454] = "upto";
        stringArray[455] = "upto";
        stringArray[456] = "setLocationRelativeTo";
        stringArray[457] = "show";
        stringArray[458] = "clearOutput";
        stringArray[459] = "hideOutputWindow";
        stringArray[460] = "updateFontSize";
        stringArray[461] = "minus";
        stringArray[462] = "size";
        stringArray[463] = "font";
        stringArray[464] = "containsKey";
        stringArray[465] = "properties";
        stringArray[466] = "plus";
        stringArray[467] = "plus";
        stringArray[468] = "name";
        stringArray[469] = "putInt";
        stringArray[470] = "<$constructor$>";
        stringArray[471] = "defaultFamily";
        stringArray[472] = "PLAIN";
        stringArray[473] = "getSource";
        stringArray[474] = "call";
        stringArray[475] = "invokeTextAction";
        stringArray[476] = "invokeTextAction";
        stringArray[477] = "invokeTextAction";
        stringArray[478] = "invokeTextAction";
        stringArray[479] = "getCaretPosition";
        stringArray[480] = "plus";
        stringArray[481] = "getElementIndex";
        stringArray[482] = "getElement";
        stringArray[483] = "minus";
        stringArray[484] = "getElement";
        stringArray[485] = "plus";
        stringArray[486] = "minus";
        stringArray[487] = "getStartOffset";
        stringArray[488] = "setText";
        stringArray[489] = "actionPerformed";
        stringArray[490] = "printAction";
        stringArray[491] = "actionPerformed";
        stringArray[492] = "undoAction";
        stringArray[493] = "actionPerformed";
        stringArray[494] = "redoAction";
        stringArray[495] = "eventType";
        stringArray[496] = "ACTIVATED";
        stringArray[497] = "getURL";
        stringArray[498] = "toInteger";
        stringArray[499] = "getAt";
        stringArray[500] = "plus";
        stringArray[501] = "lastIndexOf";
        stringArray[502] = "textEditor";
        stringArray[503] = "text";
        stringArray[504] = "iterator";
        stringArray[505] = "next";
        stringArray[506] = "indexOf";
        stringArray[507] = "plus";
        stringArray[508] = "length";
        stringArray[509] = "next";
        stringArray[510] = "iterator";
        stringArray[511] = "indexOf";
        stringArray[512] = "length";
        stringArray[513] = "setCaretPosition";
        stringArray[514] = "moveCaretPosition";
        stringArray[515] = "getComponent";
        stringArray[516] = "getVisibleRect";
        stringArray[517] = "putInt";
        stringArray[518] = "name";
        stringArray[519] = "intValue";
        stringArray[520] = "getWidth";
        stringArray[521] = "putInt";
        stringArray[522] = "name";
        stringArray[523] = "intValue";
        stringArray[524] = "getHeight";
        stringArray[525] = "putInt";
        stringArray[526] = "name";
        stringArray[527] = "width";
        stringArray[528] = "putInt";
        stringArray[529] = "name";
        stringArray[530] = "height";
        stringArray[531] = "getVisibleRect";
        stringArray[532] = "putInt";
        stringArray[533] = "name";
        stringArray[534] = "intValue";
        stringArray[535] = "getWidth";
        stringArray[536] = "putInt";
        stringArray[537] = "name";
        stringArray[538] = "intValue";
        stringArray[539] = "getHeight";
        stringArray[540] = "putInt";
        stringArray[541] = "name";
        stringArray[542] = "width";
        stringArray[543] = "putInt";
        stringArray[544] = "name";
        stringArray[545] = "height";
        stringArray[546] = "component";
        stringArray[547] = "component";
        stringArray[548] = "component";
        stringArray[549] = "component";
        stringArray[550] = "component";
        stringArray[551] = "component";
        stringArray[552] = "userNodeForPackage";
        stringArray[553] = "getBoolean";
        stringArray[554] = "getBoolean";
        stringArray[555] = "getResource";
        stringArray[556] = "classLoader";
        stringArray[557] = "getResource";
        stringArray[558] = "classLoader";
        stringArray[559] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[560];
        Console.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(Console.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = Console.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

