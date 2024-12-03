/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui;

import groovy.lang.Binding;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Script;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.ref.SoftReference;
import javax.swing.KeyStroke;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class ConsoleActions
extends Script {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ConsoleActions() {
        CallSite[] callSiteArray = ConsoleActions.$getCallSiteArray();
    }

    public ConsoleActions(Binding context) {
        CallSite[] callSiteArray = ConsoleActions.$getCallSiteArray();
        super(context);
    }

    public static void main(String ... args) {
        CallSite[] callSiteArray = ConsoleActions.$getCallSiteArray();
        callSiteArray[0].call(InvokerHelper.class, ConsoleActions.class, args);
    }

    @Override
    public Object run() {
        CallSite[] callSiteArray = ConsoleActions.$getCallSiteArray();
        Object object = callSiteArray[1].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "New File", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[2].callGroovyObjectGetProperty(this), "fileNewFile"), "mnemonic", "N", "accelerator", callSiteArray[3].callCurrent((GroovyObject)this, "N"), "smallIcon", callSiteArray[4].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"resource", "icons/page.png", "class", this})), "shortDescription", "New Groovy Script"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object, ConsoleActions.class, this, "newFileAction");
        Object object2 = callSiteArray[5].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "New Window", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[6].callGroovyObjectGetProperty(this), "fileNewWindow"), "mnemonic", "W", "accelerator", callSiteArray[7].callCurrent((GroovyObject)this, "shift N")}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object2, ConsoleActions.class, this, "newWindowAction");
        Object object3 = callSiteArray[8].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Open", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[9].callGroovyObjectGetProperty(this), "fileOpen"), "mnemonic", "O", "accelerator", callSiteArray[10].callCurrent((GroovyObject)this, "O"), "smallIcon", callSiteArray[11].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"resource", "icons/folder_page.png", "class", this})), "shortDescription", "Open Groovy Script"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object3, ConsoleActions.class, this, "openAction");
        Object object4 = callSiteArray[12].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Save", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[13].callGroovyObjectGetProperty(this), "fileSave"), "mnemonic", "S", "accelerator", callSiteArray[14].callCurrent((GroovyObject)this, "S"), "smallIcon", callSiteArray[15].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"resource", "icons/disk.png", "class", this})), "shortDescription", "Save Groovy Script", "enabled", false}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object4, ConsoleActions.class, this, "saveAction");
        Object object5 = callSiteArray[16].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Save As...", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[17].callGroovyObjectGetProperty(this), "fileSaveAs"), "mnemonic", "A"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object5, ConsoleActions.class, this, "saveAsAction");
        Object object6 = callSiteArray[18].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Print...", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[19].callGroovyObjectGetProperty(this), "print"), "mnemonic", "P", "accelerator", callSiteArray[20].callCurrent((GroovyObject)this, "P")}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object6, ConsoleActions.class, this, "printAction");
        Object object7 = callSiteArray[21].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Exit", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[22].callGroovyObjectGetProperty(this), "exit"), "mnemonic", "X"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object7, ConsoleActions.class, this, "exitAction");
        Object object8 = callSiteArray[23].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Undo", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[24].callGroovyObjectGetProperty(this), "undo"), "mnemonic", "U", "accelerator", callSiteArray[25].callCurrent((GroovyObject)this, "Z"), "smallIcon", callSiteArray[26].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"resource", "icons/arrow_undo.png", "class", this})), "shortDescription", "Undo"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object8, ConsoleActions.class, this, "undoAction");
        Object object9 = callSiteArray[27].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Redo", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[28].callGroovyObjectGetProperty(this), "redo"), "mnemonic", "R", "accelerator", callSiteArray[29].callCurrent((GroovyObject)this, "shift Z"), "smallIcon", callSiteArray[30].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"resource", "icons/arrow_redo.png", "class", this})), "shortDescription", "Redo"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object9, ConsoleActions.class, this, "redoAction");
        Object object10 = callSiteArray[31].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Find...", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[32].callGroovyObjectGetProperty(this), "find"), "mnemonic", "F", "accelerator", callSiteArray[33].callCurrent((GroovyObject)this, "F"), "smallIcon", callSiteArray[34].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"resource", "icons/find.png", "class", this})), "shortDescription", "Find"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object10, ConsoleActions.class, this, "findAction");
        Object object11 = callSiteArray[35].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Find Next", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[36].callGroovyObjectGetProperty(this), "findNext"), "mnemonic", "N", "accelerator", callSiteArray[37].call(KeyStroke.class, callSiteArray[38].callGetProperty(KeyEvent.class), 0)}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object11, ConsoleActions.class, this, "findNextAction");
        Object object12 = callSiteArray[39].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Find Previous", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[40].callGroovyObjectGetProperty(this), "findPrevious"), "mnemonic", "V", "accelerator", callSiteArray[41].call(KeyStroke.class, callSiteArray[42].callGetProperty(KeyEvent.class), callSiteArray[43].callGetProperty(InputEvent.class))}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object12, ConsoleActions.class, this, "findPreviousAction");
        Object object13 = callSiteArray[44].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Replace...", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[45].callGroovyObjectGetProperty(this), "replace"), "mnemonic", "E", "accelerator", callSiteArray[46].callCurrent((GroovyObject)this, "H"), "smallIcon", callSiteArray[47].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"resource", "icons/text_replace.png", "class", this})), "shortDescription", "Replace"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object13, ConsoleActions.class, this, "replaceAction");
        Object object14 = callSiteArray[48].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Cut", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[49].callGroovyObjectGetProperty(this), "cut"), "mnemonic", "T", "accelerator", callSiteArray[50].callCurrent((GroovyObject)this, "X"), "smallIcon", callSiteArray[51].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"resource", "icons/cut.png", "class", this})), "shortDescription", "Cut"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object14, ConsoleActions.class, this, "cutAction");
        Object object15 = callSiteArray[52].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Copy", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[53].callGroovyObjectGetProperty(this), "copy"), "mnemonic", "C", "accelerator", callSiteArray[54].callCurrent((GroovyObject)this, "C"), "smallIcon", callSiteArray[55].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"resource", "icons/page_copy.png", "class", this})), "shortDescription", "Copy"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object15, ConsoleActions.class, this, "copyAction");
        Object object16 = callSiteArray[56].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Paste", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[57].callGroovyObjectGetProperty(this), "paste"), "mnemonic", "P", "accelerator", callSiteArray[58].callCurrent((GroovyObject)this, "V"), "smallIcon", callSiteArray[59].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"resource", "icons/page_paste.png", "class", this})), "shortDescription", "Paste"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object16, ConsoleActions.class, this, "pasteAction");
        Object object17 = callSiteArray[60].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Select All", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[61].callGroovyObjectGetProperty(this), "selectAll"), "mnemonic", "A", "accelerator", callSiteArray[62].callCurrent((GroovyObject)this, "A")}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object17, ConsoleActions.class, this, "selectAllAction");
        Object object18 = callSiteArray[63].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Previous", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[64].callGroovyObjectGetProperty(this), "historyPrev"), "mnemonic", "P", "accelerator", callSiteArray[65].callCurrent((GroovyObject)this, callSiteArray[66].callGetProperty(KeyEvent.class)), "smallIcon", callSiteArray[67].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"resource", "icons/book_previous.png", "class", this})), "shortDescription", "Previous Groovy Script", "enabled", false}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object18, ConsoleActions.class, this, "historyPrevAction");
        Object object19 = callSiteArray[68].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Next", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[69].callGroovyObjectGetProperty(this), "historyNext"), "mnemonic", "N", "accelerator", callSiteArray[70].callCurrent((GroovyObject)this, callSiteArray[71].callGetProperty(KeyEvent.class)), "smallIcon", callSiteArray[72].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"resource", "icons/book_next.png", "class", this})), "shortDescription", "Next Groovy Script", "enabled", false}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object19, ConsoleActions.class, this, "historyNextAction");
        Object object20 = callSiteArray[73].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Clear Output", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[74].callGroovyObjectGetProperty(this), "clearOutput"), "mnemonic", "C", "accelerator", callSiteArray[75].callCurrent((GroovyObject)this, "W")}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object20, ConsoleActions.class, this, "clearOutputAction");
        Object object21 = callSiteArray[76].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Run", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[77].callGroovyObjectGetProperty(this), "runScript"), "mnemonic", "R", "keyStroke", callSiteArray[78].callCurrent((GroovyObject)this, "ENTER"), "accelerator", callSiteArray[79].callCurrent((GroovyObject)this, "R"), "smallIcon", callSiteArray[80].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"resource", "icons/script_go.png", "class", this})), "shortDescription", "Execute Groovy Script"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object21, ConsoleActions.class, this, "runAction");
        Object object22 = callSiteArray[81].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Run Selection", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[82].callGroovyObjectGetProperty(this), "runSelectedScript"), "mnemonic", "E", "keyStroke", callSiteArray[83].callCurrent((GroovyObject)this, "shift ENTER"), "accelerator", callSiteArray[84].callCurrent((GroovyObject)this, "shift R")}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object22, ConsoleActions.class, this, "runSelectionAction");
        Object object23 = callSiteArray[85].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Add Jar(s) to ClassPath", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[86].callGroovyObjectGetProperty(this), "addClasspathJar"), "mnemonic", "J"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object23, ConsoleActions.class, this, "addClasspathJar");
        Object object24 = callSiteArray[87].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Add Directory to ClassPath", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[88].callGroovyObjectGetProperty(this), "addClasspathDir"), "mnemonic", "D"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object24, ConsoleActions.class, this, "addClasspathDir");
        Object object25 = callSiteArray[89].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Clear Script Context", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[90].callGroovyObjectGetProperty(this), "clearContext"), "mnemonic", "C"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object25, ConsoleActions.class, this, "clearClassloader");
        Object object26 = callSiteArray[91].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Inspect Last", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[92].callGroovyObjectGetProperty(this), "inspectLast"), "mnemonic", "I", "accelerator", callSiteArray[93].callCurrent((GroovyObject)this, "I")}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object26, ConsoleActions.class, this, "inspectLastAction");
        Object object27 = callSiteArray[94].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Inspect Variables", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[95].callGroovyObjectGetProperty(this), "inspectVariables"), "mnemonic", "V", "accelerator", callSiteArray[96].callCurrent((GroovyObject)this, "J")}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object27, ConsoleActions.class, this, "inspectVariablesAction");
        Object object28 = callSiteArray[97].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Inspect Ast", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[98].callGroovyObjectGetProperty(this), "inspectAst"), "mnemonic", "A", "accelerator", callSiteArray[99].callCurrent((GroovyObject)this, "T")}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object28, ConsoleActions.class, this, "inspectAstAction");
        Object object29 = callSiteArray[100].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Capture Standard Output", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[101].callGroovyObjectGetProperty(this), "captureStdOut"), "mnemonic", "O"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object29, ConsoleActions.class, this, "captureStdOutAction");
        Object object30 = callSiteArray[102].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Capture Standard Error Output", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[103].callGroovyObjectGetProperty(this), "captureStdErr"), "mnemonic", "E"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object30, ConsoleActions.class, this, "captureStdErrAction");
        Object object31 = callSiteArray[104].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Show Full Stack Traces", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[105].callGroovyObjectGetProperty(this), "fullStackTraces"), "mnemonic", "F"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object31, ConsoleActions.class, this, "fullStackTracesAction");
        Object object32 = callSiteArray[106].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Show Script in Output", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[107].callGroovyObjectGetProperty(this), "showScriptInOutput"), "mnemonic", "R"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object32, ConsoleActions.class, this, "showScriptInOutputAction");
        Object object33 = callSiteArray[108].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Visualize Script Results", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[109].callGroovyObjectGetProperty(this), "visualizeScriptResults"), "mnemonic", "V"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object33, ConsoleActions.class, this, "visualizeScriptResultsAction");
        Object object34 = callSiteArray[110].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Show Toolbar", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[111].callGroovyObjectGetProperty(this), "showToolbar"), "mnemonic", "T"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object34, ConsoleActions.class, this, "showToolbarAction");
        Object object35 = callSiteArray[112].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Detached Output", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[113].callGroovyObjectGetProperty(this), "detachedOutput"), "mnemonic", "D"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object35, ConsoleActions.class, this, "detachedOutputAction");
        Object object36 = callSiteArray[114].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[115].callGroovyObjectGetProperty(this), "showOutputWindow"), "keyStroke", callSiteArray[116].callCurrent((GroovyObject)this, "shift O")}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object36, ConsoleActions.class, this, "showOutputWindowAction");
        Object object37 = callSiteArray[117].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[118].callGroovyObjectGetProperty(this), "hideOutputWindow"), "keyStroke", "SPACE"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object37, ConsoleActions.class, this, "hideOutputWindowAction1");
        Object object38 = callSiteArray[119].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[120].callGroovyObjectGetProperty(this), "hideOutputWindow"), "keyStroke", "ENTER"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object38, ConsoleActions.class, this, "hideOutputWindowAction2");
        Object object39 = callSiteArray[121].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[122].callGroovyObjectGetProperty(this), "hideOutputWindow"), "keyStroke", "ESCAPE"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object39, ConsoleActions.class, this, "hideOutputWindowAction3");
        Object object40 = callSiteArray[123].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[124].callGroovyObjectGetProperty(this), "hideAndClearOutputWindow"), "keyStroke", callSiteArray[125].callCurrent((GroovyObject)this, "W")}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object40, ConsoleActions.class, this, "hideOutputWindowAction4");
        Object object41 = callSiteArray[126].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Auto Clear Output On Run", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[127].callGroovyObjectGetProperty(this), "autoClearOutput"), "mnemonic", "A"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object41, ConsoleActions.class, this, "autoClearOutputAction");
        Object object42 = callSiteArray[128].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Auto Save on Runs", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[129].callGroovyObjectGetProperty(this), "saveOnRun"), "mnemonic", "A"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object42, ConsoleActions.class, this, "saveOnRunAction");
        Object object43 = callSiteArray[130].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Larger Font", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[131].callGroovyObjectGetProperty(this), "largerFont"), "mnemonic", "L", "accelerator", callSiteArray[132].callCurrent((GroovyObject)this, "shift L")}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object43, ConsoleActions.class, this, "largerFontAction");
        Object object44 = callSiteArray[133].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Smaller Font", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[134].callGroovyObjectGetProperty(this), "smallerFont"), "mnemonic", "S", "accelerator", callSiteArray[135].callCurrent((GroovyObject)this, "shift S")}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object44, ConsoleActions.class, this, "smallerFontAction");
        Object object45 = callSiteArray[136].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "About", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[137].callGroovyObjectGetProperty(this), "showAbout"), "mnemonic", "A"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object45, ConsoleActions.class, this, "aboutAction");
        Object object46 = callSiteArray[138].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Allow Interruption", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[139].callGroovyObjectGetProperty(this), "threadInterruption"), "mnemonic", "O"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object46, ConsoleActions.class, this, "threadInterruptAction");
        Object object47 = callSiteArray[140].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Interrupt", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[141].callGroovyObjectGetProperty(this), "doInterrupt"), "mnemonic", "T", "smallIcon", callSiteArray[142].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"resource", "icons/cross.png", "class", this})), "shortDescription", "Interrupt Running Script", "enabled", false}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object47, ConsoleActions.class, this, "interruptAction");
        Object object48 = callSiteArray[143].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Compile", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[144].callGroovyObjectGetProperty(this), "compileScript"), "mnemonic", "L", "accelerator", callSiteArray[145].callCurrent((GroovyObject)this, "L"), "shortDescription", "Compile Groovy Script"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object48, ConsoleActions.class, this, "compileAction");
        Object object49 = callSiteArray[146].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Comment", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[147].callGroovyObjectGetProperty(this), "comment"), "mnemonic", "C", "accelerator", callSiteArray[148].call(KeyStroke.class, callSiteArray[149].callGetProperty(KeyEvent.class), callSiteArray[150].call(callSiteArray[151].call(Toolkit.class))), "shortDescription", "Comment/Uncomment Selected Script"}));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object49, ConsoleActions.class, this, "commentAction");
        return object49;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ConsoleActions.class) {
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
        stringArray[1] = "action";
        stringArray[2] = "controller";
        stringArray[3] = "shortcut";
        stringArray[4] = "imageIcon";
        stringArray[5] = "action";
        stringArray[6] = "controller";
        stringArray[7] = "shortcut";
        stringArray[8] = "action";
        stringArray[9] = "controller";
        stringArray[10] = "shortcut";
        stringArray[11] = "imageIcon";
        stringArray[12] = "action";
        stringArray[13] = "controller";
        stringArray[14] = "shortcut";
        stringArray[15] = "imageIcon";
        stringArray[16] = "action";
        stringArray[17] = "controller";
        stringArray[18] = "action";
        stringArray[19] = "controller";
        stringArray[20] = "shortcut";
        stringArray[21] = "action";
        stringArray[22] = "controller";
        stringArray[23] = "action";
        stringArray[24] = "controller";
        stringArray[25] = "shortcut";
        stringArray[26] = "imageIcon";
        stringArray[27] = "action";
        stringArray[28] = "controller";
        stringArray[29] = "shortcut";
        stringArray[30] = "imageIcon";
        stringArray[31] = "action";
        stringArray[32] = "controller";
        stringArray[33] = "shortcut";
        stringArray[34] = "imageIcon";
        stringArray[35] = "action";
        stringArray[36] = "controller";
        stringArray[37] = "getKeyStroke";
        stringArray[38] = "VK_F3";
        stringArray[39] = "action";
        stringArray[40] = "controller";
        stringArray[41] = "getKeyStroke";
        stringArray[42] = "VK_F3";
        stringArray[43] = "SHIFT_DOWN_MASK";
        stringArray[44] = "action";
        stringArray[45] = "controller";
        stringArray[46] = "shortcut";
        stringArray[47] = "imageIcon";
        stringArray[48] = "action";
        stringArray[49] = "controller";
        stringArray[50] = "shortcut";
        stringArray[51] = "imageIcon";
        stringArray[52] = "action";
        stringArray[53] = "controller";
        stringArray[54] = "shortcut";
        stringArray[55] = "imageIcon";
        stringArray[56] = "action";
        stringArray[57] = "controller";
        stringArray[58] = "shortcut";
        stringArray[59] = "imageIcon";
        stringArray[60] = "action";
        stringArray[61] = "controller";
        stringArray[62] = "shortcut";
        stringArray[63] = "action";
        stringArray[64] = "controller";
        stringArray[65] = "shortcut";
        stringArray[66] = "VK_COMMA";
        stringArray[67] = "imageIcon";
        stringArray[68] = "action";
        stringArray[69] = "controller";
        stringArray[70] = "shortcut";
        stringArray[71] = "VK_PERIOD";
        stringArray[72] = "imageIcon";
        stringArray[73] = "action";
        stringArray[74] = "controller";
        stringArray[75] = "shortcut";
        stringArray[76] = "action";
        stringArray[77] = "controller";
        stringArray[78] = "shortcut";
        stringArray[79] = "shortcut";
        stringArray[80] = "imageIcon";
        stringArray[81] = "action";
        stringArray[82] = "controller";
        stringArray[83] = "shortcut";
        stringArray[84] = "shortcut";
        stringArray[85] = "action";
        stringArray[86] = "controller";
        stringArray[87] = "action";
        stringArray[88] = "controller";
        stringArray[89] = "action";
        stringArray[90] = "controller";
        stringArray[91] = "action";
        stringArray[92] = "controller";
        stringArray[93] = "shortcut";
        stringArray[94] = "action";
        stringArray[95] = "controller";
        stringArray[96] = "shortcut";
        stringArray[97] = "action";
        stringArray[98] = "controller";
        stringArray[99] = "shortcut";
        stringArray[100] = "action";
        stringArray[101] = "controller";
        stringArray[102] = "action";
        stringArray[103] = "controller";
        stringArray[104] = "action";
        stringArray[105] = "controller";
        stringArray[106] = "action";
        stringArray[107] = "controller";
        stringArray[108] = "action";
        stringArray[109] = "controller";
        stringArray[110] = "action";
        stringArray[111] = "controller";
        stringArray[112] = "action";
        stringArray[113] = "controller";
        stringArray[114] = "action";
        stringArray[115] = "controller";
        stringArray[116] = "shortcut";
        stringArray[117] = "action";
        stringArray[118] = "controller";
        stringArray[119] = "action";
        stringArray[120] = "controller";
        stringArray[121] = "action";
        stringArray[122] = "controller";
        stringArray[123] = "action";
        stringArray[124] = "controller";
        stringArray[125] = "shortcut";
        stringArray[126] = "action";
        stringArray[127] = "controller";
        stringArray[128] = "action";
        stringArray[129] = "controller";
        stringArray[130] = "action";
        stringArray[131] = "controller";
        stringArray[132] = "shortcut";
        stringArray[133] = "action";
        stringArray[134] = "controller";
        stringArray[135] = "shortcut";
        stringArray[136] = "action";
        stringArray[137] = "controller";
        stringArray[138] = "action";
        stringArray[139] = "controller";
        stringArray[140] = "action";
        stringArray[141] = "controller";
        stringArray[142] = "imageIcon";
        stringArray[143] = "action";
        stringArray[144] = "controller";
        stringArray[145] = "shortcut";
        stringArray[146] = "action";
        stringArray[147] = "controller";
        stringArray[148] = "getKeyStroke";
        stringArray[149] = "VK_SLASH";
        stringArray[150] = "getMenuShortcutKeyMask";
        stringArray[151] = "getDefaultToolkit";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[152];
        ConsoleActions.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ConsoleActions.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ConsoleActions.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

