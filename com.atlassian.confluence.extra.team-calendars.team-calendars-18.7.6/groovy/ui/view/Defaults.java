/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui.view;

import groovy.lang.Binding;
import groovy.lang.MetaClass;
import groovy.lang.Script;
import groovy.ui.Console;
import groovy.ui.text.GroovyFilter;
import groovy.ui.view.BasicContentPane;
import groovy.ui.view.BasicMenuBar;
import groovy.ui.view.BasicStatusBar;
import groovy.ui.view.BasicToolBar;
import java.awt.Color;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class Defaults
extends Script {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public Defaults() {
        CallSite[] callSiteArray = Defaults.$getCallSiteArray();
    }

    public Defaults(Binding context) {
        CallSite[] callSiteArray = Defaults.$getCallSiteArray();
        super(context);
    }

    public static void main(String ... args) {
        CallSite[] callSiteArray = Defaults.$getCallSiteArray();
        callSiteArray[0].call(InvokerHelper.class, Defaults.class, args);
    }

    @Override
    public Object run() {
        CallSite[] callSiteArray = Defaults.$getCallSiteArray();
        Class<BasicMenuBar> clazz = BasicMenuBar.class;
        ScriptBytecodeAdapter.setGroovyObjectProperty(clazz, Defaults.class, this, "menuBarClass");
        Class<BasicContentPane> clazz2 = BasicContentPane.class;
        ScriptBytecodeAdapter.setGroovyObjectProperty(clazz2, Defaults.class, this, "contentPaneClass");
        Class<BasicToolBar> clazz3 = BasicToolBar.class;
        ScriptBytecodeAdapter.setGroovyObjectProperty(clazz3, Defaults.class, this, "toolBarClass");
        Class<BasicStatusBar> clazz4 = BasicStatusBar.class;
        ScriptBytecodeAdapter.setGroovyObjectProperty(clazz4, Defaults.class, this, "statusBarClass");
        Object prefs = callSiteArray[1].call(Preferences.class, Console.class);
        Object fontFamily = callSiteArray[2].call(prefs, "fontName", "Monospaced");
        Map map = ScriptBytecodeAdapter.createMap(new Object[]{"regular", ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[3].callGetProperty(StyleConstants.class), fontFamily}), "prompt", ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[4].callGetProperty(StyleConstants.class), callSiteArray[5].callConstructor(Color.class, 0, 128, 0)}), "command", ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[6].callGetProperty(StyleConstants.class), callSiteArray[7].callGetProperty(Color.class)}), "stacktrace", ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[8].callGetProperty(StyleConstants.class), callSiteArray[9].call(callSiteArray[10].callGetProperty(Color.class))}), "hyperlink", ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[11].callGetProperty(StyleConstants.class), callSiteArray[12].callGetProperty(Color.class), callSiteArray[13].callGetProperty(StyleConstants.class), true}), "output", ScriptBytecodeAdapter.createMap(new Object[0]), "result", ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[14].callGetProperty(StyleConstants.class), callSiteArray[15].callGetProperty(Color.class), callSiteArray[16].callGetProperty(StyleConstants.class), callSiteArray[17].callGetProperty(Color.class)}), callSiteArray[18].callGetProperty(StyleContext.class), ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[19].callGetProperty(StyleConstants.class), fontFamily}), callSiteArray[20].callGetProperty(GroovyFilter.class), ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[21].callGetProperty(StyleConstants.class), callSiteArray[22].call(callSiteArray[23].call(callSiteArray[24].callGetProperty(Color.class))), callSiteArray[25].callGetProperty(StyleConstants.class), true}), callSiteArray[26].callGetProperty(GroovyFilter.class), ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[27].callGetProperty(StyleConstants.class), callSiteArray[28].call(callSiteArray[29].call(callSiteArray[30].callGetProperty(Color.class)))}), callSiteArray[31].callGetProperty(GroovyFilter.class), ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[32].callGetProperty(StyleConstants.class), callSiteArray[33].call(callSiteArray[34].call(callSiteArray[35].callGetProperty(Color.class)))}), callSiteArray[36].callGetProperty(GroovyFilter.class), ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[37].callGetProperty(StyleConstants.class), callSiteArray[38].call(callSiteArray[39].callGetProperty(Color.class))}), callSiteArray[40].callGetProperty(GroovyFilter.class), ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[41].callGetProperty(StyleConstants.class), callSiteArray[42].call(callSiteArray[43].callGetProperty(Color.class))}), callSiteArray[44].callGetProperty(GroovyFilter.class), ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[45].callGetProperty(StyleConstants.class), true}), callSiteArray[46].callGetProperty(GroovyFilter.class), ScriptBytecodeAdapter.createMap(new Object[0]), callSiteArray[47].callGetProperty(GroovyFilter.class), ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[48].callGetProperty(StyleConstants.class), true, callSiteArray[49].callGetProperty(StyleConstants.class), callSiteArray[50].call(callSiteArray[51].call(callSiteArray[52].callGetProperty(Color.class)))})});
        ScriptBytecodeAdapter.setGroovyObjectProperty(map, Defaults.class, this, "styles");
        return map;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != Defaults.class) {
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
        stringArray[1] = "userNodeForPackage";
        stringArray[2] = "get";
        stringArray[3] = "FontFamily";
        stringArray[4] = "Foreground";
        stringArray[5] = "<$constructor$>";
        stringArray[6] = "Foreground";
        stringArray[7] = "BLUE";
        stringArray[8] = "Foreground";
        stringArray[9] = "darker";
        stringArray[10] = "RED";
        stringArray[11] = "Foreground";
        stringArray[12] = "BLUE";
        stringArray[13] = "Underline";
        stringArray[14] = "Foreground";
        stringArray[15] = "BLUE";
        stringArray[16] = "Background";
        stringArray[17] = "YELLOW";
        stringArray[18] = "DEFAULT_STYLE";
        stringArray[19] = "FontFamily";
        stringArray[20] = "COMMENT";
        stringArray[21] = "Foreground";
        stringArray[22] = "darker";
        stringArray[23] = "darker";
        stringArray[24] = "LIGHT_GRAY";
        stringArray[25] = "Italic";
        stringArray[26] = "QUOTES";
        stringArray[27] = "Foreground";
        stringArray[28] = "darker";
        stringArray[29] = "darker";
        stringArray[30] = "MAGENTA";
        stringArray[31] = "SINGLE_QUOTES";
        stringArray[32] = "Foreground";
        stringArray[33] = "darker";
        stringArray[34] = "darker";
        stringArray[35] = "GREEN";
        stringArray[36] = "SLASHY_QUOTES";
        stringArray[37] = "Foreground";
        stringArray[38] = "darker";
        stringArray[39] = "ORANGE";
        stringArray[40] = "DIGIT";
        stringArray[41] = "Foreground";
        stringArray[42] = "darker";
        stringArray[43] = "RED";
        stringArray[44] = "OPERATION";
        stringArray[45] = "Bold";
        stringArray[46] = "IDENT";
        stringArray[47] = "RESERVED_WORD";
        stringArray[48] = "Bold";
        stringArray[49] = "Foreground";
        stringArray[50] = "darker";
        stringArray[51] = "darker";
        stringArray[52] = "BLUE";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[53];
        Defaults.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(Defaults.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = Defaults.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

