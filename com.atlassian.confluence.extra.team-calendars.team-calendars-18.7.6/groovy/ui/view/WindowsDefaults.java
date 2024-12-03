/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui.view;

import groovy.lang.Binding;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Script;
import groovy.ui.Console;
import groovy.ui.view.Defaults;
import java.lang.ref.SoftReference;
import java.util.prefs.Preferences;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class WindowsDefaults
extends Script {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public WindowsDefaults() {
        CallSite[] callSiteArray = WindowsDefaults.$getCallSiteArray();
    }

    public WindowsDefaults(Binding context) {
        CallSite[] callSiteArray = WindowsDefaults.$getCallSiteArray();
        super(context);
    }

    public static void main(String ... args) {
        CallSite[] callSiteArray = WindowsDefaults.$getCallSiteArray();
        callSiteArray[0].call(InvokerHelper.class, WindowsDefaults.class, args);
    }

    @Override
    public Object run() {
        CallSite[] callSiteArray = WindowsDefaults.$getCallSiteArray();
        callSiteArray[1].callCurrent((GroovyObject)this, Defaults.class);
        Object prefs = callSiteArray[2].call(Preferences.class, Console.class);
        Object fontFamily = callSiteArray[3].call(prefs, "fontName", "Consolas");
        if (DefaultTypeTransformation.booleanUnbox(ScriptBytecodeAdapter.findRegex(callSiteArray[4].call(callSiteArray[5].callGetProperty(System.class), "os.version"), "6\\."))) {
            Object object = fontFamily;
            callSiteArray[6].call(callSiteArray[7].callGetProperty(callSiteArray[8].callGroovyObjectGetProperty(this)), callSiteArray[9].callGetProperty(StyleConstants.class), object);
            Object object2 = fontFamily;
            callSiteArray[10].call(callSiteArray[11].call(callSiteArray[12].callGroovyObjectGetProperty(this), callSiteArray[13].callGetProperty(StyleContext.class)), callSiteArray[14].callGetProperty(StyleConstants.class), object2);
            return object2;
        }
        return null;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != WindowsDefaults.class) {
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
        stringArray[2] = "userNodeForPackage";
        stringArray[3] = "get";
        stringArray[4] = "getAt";
        stringArray[5] = "properties";
        stringArray[6] = "putAt";
        stringArray[7] = "regular";
        stringArray[8] = "styles";
        stringArray[9] = "FontFamily";
        stringArray[10] = "putAt";
        stringArray[11] = "getAt";
        stringArray[12] = "styles";
        stringArray[13] = "DEFAULT_STYLE";
        stringArray[14] = "FontFamily";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[15];
        WindowsDefaults.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(WindowsDefaults.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = WindowsDefaults.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

