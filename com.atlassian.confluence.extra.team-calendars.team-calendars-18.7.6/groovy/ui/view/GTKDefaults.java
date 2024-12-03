/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui.view;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Script;
import groovy.ui.Console;
import groovy.ui.view.Defaults;
import java.awt.print.PrinterJob;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.prefs.Preferences;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class GTKDefaults
extends Script {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public GTKDefaults() {
        CallSite[] callSiteArray = GTKDefaults.$getCallSiteArray();
    }

    public GTKDefaults(Binding context) {
        CallSite[] callSiteArray = GTKDefaults.$getCallSiteArray();
        super(context);
    }

    public static void main(String ... args) {
        CallSite[] callSiteArray = GTKDefaults.$getCallSiteArray();
        callSiteArray[0].call(InvokerHelper.class, GTKDefaults.class, args);
    }

    @Override
    public Object run() {
        CallSite[] callSiteArray = GTKDefaults.$getCallSiteArray();
        callSiteArray[1].callCurrent((GroovyObject)this, Defaults.class);
        Object prefs = callSiteArray[2].call(Preferences.class, Console.class);
        Object fontFamily = callSiteArray[3].call(prefs, "fontName", "DejaVu Sans Mono");
        Object object = fontFamily;
        callSiteArray[4].call(callSiteArray[5].callGetProperty(callSiteArray[6].callGroovyObjectGetProperty(this)), callSiteArray[7].callGetProperty(StyleConstants.class), object);
        Object object2 = fontFamily;
        callSiteArray[8].call(callSiteArray[9].call(callSiteArray[10].callGroovyObjectGetProperty(this), callSiteArray[11].callGetProperty(StyleContext.class)), callSiteArray[12].callGetProperty(StyleConstants.class), object2);
        Object object3 = callSiteArray[13].call(PrinterJob.class);
        ScriptBytecodeAdapter.setGroovyObjectProperty(object3, GTKDefaults.class, this, "pj");
        Object object4 = callSiteArray[14].call(callSiteArray[15].callGroovyObjectGetProperty(this));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object4, GTKDefaults.class, this, "ps");
        callSiteArray[16].call(callSiteArray[17].callGroovyObjectGetProperty(this));
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
                return ScriptBytecodeAdapter.compareEqual(callSiteArray[0].callGetProperty(it), "application/vnd.cups-postscript");
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

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[1];
                stringArray[0] = "mimeType";
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
        Object object5 = callSiteArray[18].call((Object)((List)ScriptBytecodeAdapter.asType(callSiteArray[19].call(callSiteArray[20].callGroovyObjectGetProperty(this)), List.class)), new _run_closure1(this, this));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object5, GTKDefaults.class, this, "docFlav");
        Object object6 = callSiteArray[21].call(callSiteArray[22].callGroovyObjectGetProperty(this));
        ScriptBytecodeAdapter.setGroovyObjectProperty(object6, GTKDefaults.class, this, "attrset");
        Object object7 = callSiteArray[23].call(callSiteArray[24].callGroovyObjectGetProperty(this), OrientationRequested.class);
        Object object8 = DefaultTypeTransformation.booleanUnbox(object7) ? object7 : callSiteArray[25].call(callSiteArray[26].callGroovyObjectGetProperty(this), OrientationRequested.class);
        ScriptBytecodeAdapter.setGroovyObjectProperty(object8, GTKDefaults.class, this, "orient");
        Object object9 = callSiteArray[27].call(callSiteArray[28].callGroovyObjectGetProperty(this), callSiteArray[29].callGroovyObjectGetProperty(this), callSiteArray[30].callGroovyObjectGetProperty(this), callSiteArray[31].callGroovyObjectGetProperty(this));
        try {
            return object9;
        }
        catch (NullPointerException npe) {
            Object object10 = callSiteArray[32].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createMap(new Object[]{"name", "Print...", "closure", ScriptBytecodeAdapter.getMethodPointer(callSiteArray[33].callGroovyObjectGetProperty(this), "print"), "mnemonic", "P", "accelerator", callSiteArray[34].callCurrent((GroovyObject)this, "P"), "shortDescription", "Printing does not work in Java with this version of CUPS", "enabled", false}));
            ScriptBytecodeAdapter.setGroovyObjectProperty(object10, GTKDefaults.class, this, "printAction");
            Object object11 = object10;
            return object11;
        }
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != GTKDefaults.class) {
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
        stringArray[4] = "putAt";
        stringArray[5] = "regular";
        stringArray[6] = "styles";
        stringArray[7] = "FontFamily";
        stringArray[8] = "putAt";
        stringArray[9] = "getAt";
        stringArray[10] = "styles";
        stringArray[11] = "DEFAULT_STYLE";
        stringArray[12] = "FontFamily";
        stringArray[13] = "getPrinterJob";
        stringArray[14] = "getPrintService";
        stringArray[15] = "pj";
        stringArray[16] = "getAttributes";
        stringArray[17] = "ps";
        stringArray[18] = "find";
        stringArray[19] = "getSupportedDocFlavors";
        stringArray[20] = "ps";
        stringArray[21] = "getAttributes";
        stringArray[22] = "ps";
        stringArray[23] = "get";
        stringArray[24] = "attrset";
        stringArray[25] = "getDefaultAttributeValue";
        stringArray[26] = "ps";
        stringArray[27] = "isAttributeValueSupported";
        stringArray[28] = "ps";
        stringArray[29] = "orient";
        stringArray[30] = "docFlav";
        stringArray[31] = "attrset";
        stringArray[32] = "action";
        stringArray[33] = "controller";
        stringArray[34] = "shortcut";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[35];
        GTKDefaults.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(GTKDefaults.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = GTKDefaults.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

