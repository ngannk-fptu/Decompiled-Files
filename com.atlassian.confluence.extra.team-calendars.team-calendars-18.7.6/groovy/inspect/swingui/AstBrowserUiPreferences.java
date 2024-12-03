/*
 * Decompiled with CFR 0.152.
 */
package groovy.inspect.swingui;

import groovy.inspect.swingui.CompilePhaseAdapter;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.prefs.Preferences;
import org.codehaus.groovy.control.Phases;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class AstBrowserUiPreferences
implements GroovyObject {
    private final Object frameLocation;
    private final Object frameSize;
    private final Object verticalDividerLocation;
    private final Object horizontalDividerLocation;
    private final boolean showScriptFreeForm;
    private final boolean showTreeView;
    private final boolean showScriptClass;
    private final boolean showClosureClasses;
    private int decompiledSourceFontSize;
    private final CompilePhaseAdapter selectedPhase;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public AstBrowserUiPreferences() {
        Object object;
        Object object2;
        MetaClass metaClass;
        CallSite[] callSiteArray = AstBrowserUiPreferences.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        Preferences prefs = (Preferences)ScriptBytecodeAdapter.castToType(callSiteArray[0].call(Preferences.class, AstBrowserUiPreferences.class), Preferences.class);
        List list = ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[1].call(prefs, "frameX", 200), callSiteArray[2].call(prefs, "frameY", 200)});
        this.frameLocation = list;
        List list2 = ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[3].call(prefs, "frameWidth", 800), callSiteArray[4].call(prefs, "frameHeight", 600)});
        this.frameSize = list2;
        Object object3 = callSiteArray[5].call(prefs, "decompiledFontSize", 12);
        this.decompiledSourceFontSize = DefaultTypeTransformation.intUnbox(object3);
        this.verticalDividerLocation = object2 = callSiteArray[6].call(Math.class, callSiteArray[7].call(prefs, "verticalSplitterLocation", 100), 100);
        this.horizontalDividerLocation = object = callSiteArray[8].call(Math.class, callSiteArray[9].call(prefs, "horizontalSplitterLocation", 100), 100);
        Object object4 = callSiteArray[10].call(prefs, "showScriptFreeForm", false);
        this.showScriptFreeForm = DefaultTypeTransformation.booleanUnbox(object4);
        Object object5 = callSiteArray[11].call(prefs, "showScriptClass", true);
        this.showScriptClass = DefaultTypeTransformation.booleanUnbox(object5);
        Object object6 = callSiteArray[12].call(prefs, "showClosureClasses", false);
        this.showClosureClasses = DefaultTypeTransformation.booleanUnbox(object6);
        Object object7 = callSiteArray[13].call(prefs, "showTreeView", true);
        this.showTreeView = DefaultTypeTransformation.booleanUnbox(object7);
        Reference<Integer> phase = new Reference<Integer>((Integer)ScriptBytecodeAdapter.castToType(callSiteArray[14].call(prefs, "compilerPhase", callSiteArray[15].callGetProperty(Phases.class)), Integer.class));
        Object object8 = callSiteArray[16].call(callSiteArray[17].call(CompilePhaseAdapter.class), new _closure1(this, this, phase));
        this.selectedPhase = (CompilePhaseAdapter)ShortTypeHandling.castToEnum(object8, CompilePhaseAdapter.class);
    }

    public Object save(Object frame, Object vSplitter, Object hSplitter, Object scriptFreeFormPref, Object scriptClassPref, Object closureClassesPref, CompilePhaseAdapter phase, Object showTreeView) {
        CallSite[] callSiteArray = AstBrowserUiPreferences.$getCallSiteArray();
        Preferences prefs = (Preferences)ScriptBytecodeAdapter.castToType(callSiteArray[18].call(Preferences.class, AstBrowserUiPreferences.class), Preferences.class);
        callSiteArray[19].call(prefs, "decompiledFontSize", ScriptBytecodeAdapter.createPojoWrapper(this.decompiledSourceFontSize, Integer.TYPE));
        callSiteArray[20].call(prefs, "frameX", ScriptBytecodeAdapter.createPojoWrapper(DefaultTypeTransformation.intUnbox(ScriptBytecodeAdapter.asType(callSiteArray[21].callGetProperty(callSiteArray[22].callGetProperty(frame)), Integer.TYPE)), Integer.TYPE));
        callSiteArray[23].call(prefs, "frameY", ScriptBytecodeAdapter.createPojoWrapper(DefaultTypeTransformation.intUnbox(ScriptBytecodeAdapter.asType(callSiteArray[24].callGetProperty(callSiteArray[25].callGetProperty(frame)), Integer.TYPE)), Integer.TYPE));
        callSiteArray[26].call(prefs, "frameWidth", ScriptBytecodeAdapter.createPojoWrapper(DefaultTypeTransformation.intUnbox(ScriptBytecodeAdapter.asType(callSiteArray[27].callGetProperty(callSiteArray[28].callGetProperty(frame)), Integer.TYPE)), Integer.TYPE));
        callSiteArray[29].call(prefs, "frameHeight", ScriptBytecodeAdapter.createPojoWrapper(DefaultTypeTransformation.intUnbox(ScriptBytecodeAdapter.asType(callSiteArray[30].callGetProperty(callSiteArray[31].callGetProperty(frame)), Integer.TYPE)), Integer.TYPE));
        callSiteArray[32].call(prefs, "verticalSplitterLocation", callSiteArray[33].callGetProperty(vSplitter));
        callSiteArray[34].call(prefs, "horizontalSplitterLocation", callSiteArray[35].callGetProperty(hSplitter));
        callSiteArray[36].call(prefs, "showScriptFreeForm", scriptFreeFormPref);
        callSiteArray[37].call(prefs, "showScriptClass", scriptClassPref);
        callSiteArray[38].call(prefs, "showClosureClasses", closureClassesPref);
        callSiteArray[39].call(prefs, "showTreeView", showTreeView);
        return callSiteArray[40].call(prefs, "compilerPhase", callSiteArray[41].callGroovyObjectGetProperty(phase));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != AstBrowserUiPreferences.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
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

    public final Object getFrameLocation() {
        return this.frameLocation;
    }

    public final Object getFrameSize() {
        return this.frameSize;
    }

    public final Object getVerticalDividerLocation() {
        return this.verticalDividerLocation;
    }

    public final Object getHorizontalDividerLocation() {
        return this.horizontalDividerLocation;
    }

    public final boolean getShowScriptFreeForm() {
        return this.showScriptFreeForm;
    }

    public final boolean isShowScriptFreeForm() {
        return this.showScriptFreeForm;
    }

    public final boolean getShowTreeView() {
        return this.showTreeView;
    }

    public final boolean isShowTreeView() {
        return this.showTreeView;
    }

    public final boolean getShowScriptClass() {
        return this.showScriptClass;
    }

    public final boolean isShowScriptClass() {
        return this.showScriptClass;
    }

    public final boolean getShowClosureClasses() {
        return this.showClosureClasses;
    }

    public final boolean isShowClosureClasses() {
        return this.showClosureClasses;
    }

    public int getDecompiledSourceFontSize() {
        return this.decompiledSourceFontSize;
    }

    public void setDecompiledSourceFontSize(int n) {
        this.decompiledSourceFontSize = n;
    }

    public final CompilePhaseAdapter getSelectedPhase() {
        return this.selectedPhase;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "userNodeForPackage";
        stringArray[1] = "getInt";
        stringArray[2] = "getInt";
        stringArray[3] = "getInt";
        stringArray[4] = "getInt";
        stringArray[5] = "getInt";
        stringArray[6] = "max";
        stringArray[7] = "getInt";
        stringArray[8] = "max";
        stringArray[9] = "getInt";
        stringArray[10] = "getBoolean";
        stringArray[11] = "getBoolean";
        stringArray[12] = "getBoolean";
        stringArray[13] = "getBoolean";
        stringArray[14] = "getInt";
        stringArray[15] = "SEMANTIC_ANALYSIS";
        stringArray[16] = "find";
        stringArray[17] = "values";
        stringArray[18] = "userNodeForPackage";
        stringArray[19] = "putInt";
        stringArray[20] = "putInt";
        stringArray[21] = "x";
        stringArray[22] = "location";
        stringArray[23] = "putInt";
        stringArray[24] = "y";
        stringArray[25] = "location";
        stringArray[26] = "putInt";
        stringArray[27] = "width";
        stringArray[28] = "size";
        stringArray[29] = "putInt";
        stringArray[30] = "height";
        stringArray[31] = "size";
        stringArray[32] = "putInt";
        stringArray[33] = "dividerLocation";
        stringArray[34] = "putInt";
        stringArray[35] = "dividerLocation";
        stringArray[36] = "putBoolean";
        stringArray[37] = "putBoolean";
        stringArray[38] = "putBoolean";
        stringArray[39] = "putBoolean";
        stringArray[40] = "putInt";
        stringArray[41] = "phaseId";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[42];
        AstBrowserUiPreferences.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(AstBrowserUiPreferences.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = AstBrowserUiPreferences.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }

    public class _closure1
    extends Closure
    implements GeneratedClosure {
        private /* synthetic */ Reference phase;
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private static /* synthetic */ SoftReference $callSiteArray;

        public _closure1(Object _outerInstance, Object _thisObject, Reference phase) {
            Reference reference;
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            super(_outerInstance, _thisObject);
            this.phase = reference = phase;
        }

        public Object doCall(Object it) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            return ScriptBytecodeAdapter.compareEqual(callSiteArray[0].callGetProperty(it), this.phase.get());
        }

        public Integer getPhase() {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            return (Integer)ScriptBytecodeAdapter.castToType(this.phase.get(), Integer.class);
        }

        public Object doCall() {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            return this.doCall(null);
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

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[1];
            stringArray[0] = "phaseId";
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

