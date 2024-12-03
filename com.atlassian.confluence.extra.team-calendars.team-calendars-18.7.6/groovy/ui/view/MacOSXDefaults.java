/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui.view;

import groovy.lang.Binding;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Script;
import groovy.ui.Console;
import groovy.ui.text.GroovyFilter;
import groovy.ui.view.Defaults;
import groovy.ui.view.MacOSXMenuBar;
import java.awt.Color;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.prefs.Preferences;
import javax.swing.text.StyleConstants;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class MacOSXDefaults
extends Script {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public MacOSXDefaults() {
        CallSite[] callSiteArray = MacOSXDefaults.$getCallSiteArray();
    }

    public MacOSXDefaults(Binding context) {
        CallSite[] callSiteArray = MacOSXDefaults.$getCallSiteArray();
        super(context);
    }

    public static void main(String ... args) {
        CallSite[] callSiteArray = MacOSXDefaults.$getCallSiteArray();
        callSiteArray[0].call(InvokerHelper.class, MacOSXDefaults.class, args);
    }

    @Override
    public Object run() {
        CallSite[] callSiteArray = MacOSXDefaults.$getCallSiteArray();
        callSiteArray[1].callCurrent((GroovyObject)this, Defaults.class);
        callSiteArray[2].call(System.class, "apple.laf.useScreenMenuBar", "true");
        callSiteArray[3].call(System.class, "com.apple.mrj.application.apple.menu.about.name", "GroovyConsole");
        Object prefs = callSiteArray[4].call(Preferences.class, Console.class);
        Object fontFamily = callSiteArray[5].call(prefs, "fontName", "Monaco");
        Map map = ScriptBytecodeAdapter.createMap(new Object[]{"regular", ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[6].callGetProperty(StyleConstants.class), fontFamily}), "prompt", ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[7].callGetProperty(StyleConstants.class), callSiteArray[8].callGetProperty(Color.class)}), "command", ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[9].callGetProperty(StyleConstants.class), callSiteArray[10].callGetProperty(Color.class)}), "stacktrace", ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[11].callGetProperty(StyleConstants.class), callSiteArray[12].call(callSiteArray[13].callGetProperty(Color.class))}), "hyperlink", ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[14].callGetProperty(StyleConstants.class), callSiteArray[15].callGetProperty(Color.class), callSiteArray[16].callGetProperty(StyleConstants.class), true}), "output", ScriptBytecodeAdapter.createMap(new Object[0]), "result", ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[17].callGetProperty(StyleConstants.class), callSiteArray[18].callGetProperty(Color.class), callSiteArray[19].callGetProperty(StyleConstants.class), callSiteArray[20].callGetProperty(Color.class)}), callSiteArray[21].callGetProperty(GroovyFilter.class), ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[22].callGetProperty(StyleConstants.class), callSiteArray[23].call(callSiteArray[24].call(callSiteArray[25].callGetProperty(Color.class))), callSiteArray[26].callGetProperty(StyleConstants.class), true}), callSiteArray[27].callGetProperty(GroovyFilter.class), ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[28].callGetProperty(StyleConstants.class), callSiteArray[29].call(callSiteArray[30].call(callSiteArray[31].callGetProperty(Color.class)))}), callSiteArray[32].callGetProperty(GroovyFilter.class), ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[33].callGetProperty(StyleConstants.class), callSiteArray[34].call(callSiteArray[35].call(callSiteArray[36].callGetProperty(Color.class)))}), callSiteArray[37].callGetProperty(GroovyFilter.class), ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[38].callGetProperty(StyleConstants.class), callSiteArray[39].call(callSiteArray[40].callGetProperty(Color.class))}), callSiteArray[41].callGetProperty(GroovyFilter.class), ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[42].callGetProperty(StyleConstants.class), callSiteArray[43].call(callSiteArray[44].callGetProperty(Color.class))}), callSiteArray[45].callGetProperty(GroovyFilter.class), ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[46].callGetProperty(StyleConstants.class), true}), callSiteArray[47].callGetProperty(GroovyFilter.class), ScriptBytecodeAdapter.createMap(new Object[0]), callSiteArray[48].callGetProperty(GroovyFilter.class), ScriptBytecodeAdapter.createMap(new Object[]{callSiteArray[49].callGetProperty(StyleConstants.class), true, callSiteArray[50].callGetProperty(StyleConstants.class), callSiteArray[51].call(callSiteArray[52].call(callSiteArray[53].callGetProperty(Color.class)))})});
        ScriptBytecodeAdapter.setGroovyObjectProperty(map, MacOSXDefaults.class, this, "styles");
        Class<MacOSXMenuBar> clazz = MacOSXMenuBar.class;
        ScriptBytecodeAdapter.setGroovyObjectProperty(clazz, MacOSXDefaults.class, this, "menuBarClass");
        return clazz;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != MacOSXDefaults.class) {
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
        stringArray[1] = "build";
        stringArray[2] = "setProperty";
        stringArray[3] = "setProperty";
        stringArray[4] = "userNodeForPackage";
        stringArray[5] = "get";
        stringArray[6] = "FontFamily";
        stringArray[7] = "Foreground";
        stringArray[8] = "LIGHT_GRAY";
        stringArray[9] = "Foreground";
        stringArray[10] = "GRAY";
        stringArray[11] = "Foreground";
        stringArray[12] = "darker";
        stringArray[13] = "RED";
        stringArray[14] = "Foreground";
        stringArray[15] = "BLUE";
        stringArray[16] = "Underline";
        stringArray[17] = "Foreground";
        stringArray[18] = "WHITE";
        stringArray[19] = "Background";
        stringArray[20] = "BLACK";
        stringArray[21] = "COMMENT";
        stringArray[22] = "Foreground";
        stringArray[23] = "darker";
        stringArray[24] = "darker";
        stringArray[25] = "LIGHT_GRAY";
        stringArray[26] = "Italic";
        stringArray[27] = "QUOTES";
        stringArray[28] = "Foreground";
        stringArray[29] = "darker";
        stringArray[30] = "darker";
        stringArray[31] = "MAGENTA";
        stringArray[32] = "SINGLE_QUOTES";
        stringArray[33] = "Foreground";
        stringArray[34] = "darker";
        stringArray[35] = "darker";
        stringArray[36] = "GREEN";
        stringArray[37] = "SLASHY_QUOTES";
        stringArray[38] = "Foreground";
        stringArray[39] = "darker";
        stringArray[40] = "ORANGE";
        stringArray[41] = "DIGIT";
        stringArray[42] = "Foreground";
        stringArray[43] = "darker";
        stringArray[44] = "RED";
        stringArray[45] = "OPERATION";
        stringArray[46] = "Bold";
        stringArray[47] = "IDENT";
        stringArray[48] = "RESERVED_WORD";
        stringArray[49] = "Bold";
        stringArray[50] = "Foreground";
        stringArray[51] = "darker";
        stringArray[52] = "darker";
        stringArray[53] = "BLUE";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[54];
        MacOSXDefaults.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(MacOSXDefaults.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = MacOSXDefaults.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

