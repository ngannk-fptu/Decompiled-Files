/*
 * Decompiled with CFR 0.152.
 */
package groovy.inspect.swingui;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import org.codehaus.groovy.control.Phases;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public final class CompilePhaseAdapter
extends Enum<CompilePhaseAdapter>
implements GroovyObject {
    public static final /* enum */ CompilePhaseAdapter INITIALIZATION;
    public static final /* enum */ CompilePhaseAdapter PARSING;
    public static final /* enum */ CompilePhaseAdapter CONVERSION;
    public static final /* enum */ CompilePhaseAdapter SEMANTIC_ANALYSIS;
    public static final /* enum */ CompilePhaseAdapter CANONICALIZATION;
    public static final /* enum */ CompilePhaseAdapter INSTRUCTION_SELECTION;
    public static final /* enum */ CompilePhaseAdapter CLASS_GENERATION;
    public static final /* enum */ CompilePhaseAdapter OUTPUT;
    public static final /* enum */ CompilePhaseAdapter FINALIZATION;
    private final int phaseId;
    private final String string;
    public static final CompilePhaseAdapter MIN_VALUE;
    public static final CompilePhaseAdapter MAX_VALUE;
    private static final /* synthetic */ CompilePhaseAdapter[] $VALUES;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public CompilePhaseAdapter(Object phaseId, Object string) {
        MetaClass metaClass;
        CallSite[] callSiteArray = CompilePhaseAdapter.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        Object object = phaseId;
        this.phaseId = DefaultTypeTransformation.intUnbox(object);
        Object object2 = string;
        this.string = ShortTypeHandling.castToString(object2);
    }

    public String toString() {
        CallSite[] callSiteArray = CompilePhaseAdapter.$getCallSiteArray();
        return this.string;
    }

    public static final CompilePhaseAdapter[] values() {
        CallSite[] callSiteArray = CompilePhaseAdapter.$getCallSiteArray();
        return (CompilePhaseAdapter[])ScriptBytecodeAdapter.castToType($VALUES.clone(), CompilePhaseAdapter[].class);
    }

    public /* synthetic */ CompilePhaseAdapter next() {
        CallSite[] callSiteArray = CompilePhaseAdapter.$getCallSiteArray();
        Object ordinal = callSiteArray[0].call(callSiteArray[1].callCurrent(this));
        if (ScriptBytecodeAdapter.compareGreaterThanEqual(ordinal, callSiteArray[2].call($VALUES))) {
            Integer n = 0;
            ordinal = n;
        }
        return (CompilePhaseAdapter)ShortTypeHandling.castToEnum(callSiteArray[3].call((Object)$VALUES, ordinal), CompilePhaseAdapter.class);
    }

    public /* synthetic */ CompilePhaseAdapter previous() {
        CallSite[] callSiteArray = CompilePhaseAdapter.$getCallSiteArray();
        Object ordinal = callSiteArray[4].call(callSiteArray[5].callCurrent(this));
        if (ScriptBytecodeAdapter.compareLessThan(ordinal, 0)) {
            Object object;
            ordinal = object = callSiteArray[6].call(callSiteArray[7].call($VALUES), 1);
        }
        return (CompilePhaseAdapter)ShortTypeHandling.castToEnum(callSiteArray[8].call((Object)$VALUES, ordinal), CompilePhaseAdapter.class);
    }

    public static CompilePhaseAdapter valueOf(String name) {
        CallSite[] callSiteArray = CompilePhaseAdapter.$getCallSiteArray();
        return (CompilePhaseAdapter)ShortTypeHandling.castToEnum(callSiteArray[9].callStatic(CompilePhaseAdapter.class, CompilePhaseAdapter.class, name), CompilePhaseAdapter.class);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static final /* synthetic */ CompilePhaseAdapter $INIT(Object ... para) {
        CallSite[] callSiteArray = CompilePhaseAdapter.$getCallSiteArray();
        Object[] objectArray = ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{para}, new int[]{0});
        switch (ScriptBytecodeAdapter.selectConstructorAndTransformArguments(objectArray, -1, CompilePhaseAdapter.class)) {
            case -351174756: {
                return new CompilePhaseAdapter(objectArray[2], objectArray[3]);
            }
            default: {
                throw new IllegalArgumentException("This class has been compiled with a super class which is binary incompatible with the current super class found on classpath. You should recompile this class with the new version.");
            }
        }
    }

    static {
        CompilePhaseAdapter compilePhaseAdapter;
        CompilePhaseAdapter compilePhaseAdapter2;
        Object object = CompilePhaseAdapter.$getCallSiteArray()[10].callStatic(CompilePhaseAdapter.class, "INITIALIZATION", 0, CompilePhaseAdapter.$getCallSiteArray()[11].callGetProperty(Phases.class), "Initialization");
        INITIALIZATION = (CompilePhaseAdapter)ShortTypeHandling.castToEnum(object, CompilePhaseAdapter.class);
        Object object2 = CompilePhaseAdapter.$getCallSiteArray()[12].callStatic(CompilePhaseAdapter.class, "PARSING", 1, CompilePhaseAdapter.$getCallSiteArray()[13].callGetProperty(Phases.class), "Parsing");
        PARSING = (CompilePhaseAdapter)ShortTypeHandling.castToEnum(object2, CompilePhaseAdapter.class);
        Object object3 = CompilePhaseAdapter.$getCallSiteArray()[14].callStatic(CompilePhaseAdapter.class, "CONVERSION", 2, CompilePhaseAdapter.$getCallSiteArray()[15].callGetProperty(Phases.class), "Conversion");
        CONVERSION = (CompilePhaseAdapter)ShortTypeHandling.castToEnum(object3, CompilePhaseAdapter.class);
        Object object4 = CompilePhaseAdapter.$getCallSiteArray()[16].callStatic(CompilePhaseAdapter.class, "SEMANTIC_ANALYSIS", 3, CompilePhaseAdapter.$getCallSiteArray()[17].callGetProperty(Phases.class), "Semantic Analysis");
        SEMANTIC_ANALYSIS = (CompilePhaseAdapter)ShortTypeHandling.castToEnum(object4, CompilePhaseAdapter.class);
        Object object5 = CompilePhaseAdapter.$getCallSiteArray()[18].callStatic(CompilePhaseAdapter.class, "CANONICALIZATION", 4, CompilePhaseAdapter.$getCallSiteArray()[19].callGetProperty(Phases.class), "Canonicalization");
        CANONICALIZATION = (CompilePhaseAdapter)ShortTypeHandling.castToEnum(object5, CompilePhaseAdapter.class);
        Object object6 = CompilePhaseAdapter.$getCallSiteArray()[20].callStatic(CompilePhaseAdapter.class, "INSTRUCTION_SELECTION", 5, CompilePhaseAdapter.$getCallSiteArray()[21].callGetProperty(Phases.class), "Instruction Selection");
        INSTRUCTION_SELECTION = (CompilePhaseAdapter)ShortTypeHandling.castToEnum(object6, CompilePhaseAdapter.class);
        Object object7 = CompilePhaseAdapter.$getCallSiteArray()[22].callStatic(CompilePhaseAdapter.class, "CLASS_GENERATION", 6, CompilePhaseAdapter.$getCallSiteArray()[23].callGetProperty(Phases.class), "Class Generation");
        CLASS_GENERATION = (CompilePhaseAdapter)ShortTypeHandling.castToEnum(object7, CompilePhaseAdapter.class);
        Object object8 = CompilePhaseAdapter.$getCallSiteArray()[24].callStatic(CompilePhaseAdapter.class, "OUTPUT", 7, CompilePhaseAdapter.$getCallSiteArray()[25].callGetProperty(Phases.class), "Output");
        OUTPUT = (CompilePhaseAdapter)ShortTypeHandling.castToEnum(object8, CompilePhaseAdapter.class);
        Object object9 = CompilePhaseAdapter.$getCallSiteArray()[26].callStatic(CompilePhaseAdapter.class, "FINALIZATION", 8, CompilePhaseAdapter.$getCallSiteArray()[27].callGetProperty(Phases.class), "Finalization");
        FINALIZATION = (CompilePhaseAdapter)ShortTypeHandling.castToEnum(object9, CompilePhaseAdapter.class);
        MIN_VALUE = compilePhaseAdapter2 = INITIALIZATION;
        MAX_VALUE = compilePhaseAdapter = FINALIZATION;
        CompilePhaseAdapter[] compilePhaseAdapterArray = new CompilePhaseAdapter[]{INITIALIZATION, PARSING, CONVERSION, SEMANTIC_ANALYSIS, CANONICALIZATION, INSTRUCTION_SELECTION, CLASS_GENERATION, OUTPUT, FINALIZATION};
        $VALUES = compilePhaseAdapterArray;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != CompilePhaseAdapter.class) {
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

    public final int getPhaseId() {
        return this.phaseId;
    }

    public final String getString() {
        return this.string;
    }

    public /* synthetic */ String super$2$toString() {
        return super.toString();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "next";
        stringArray[1] = "ordinal";
        stringArray[2] = "size";
        stringArray[3] = "getAt";
        stringArray[4] = "previous";
        stringArray[5] = "ordinal";
        stringArray[6] = "minus";
        stringArray[7] = "size";
        stringArray[8] = "getAt";
        stringArray[9] = "valueOf";
        stringArray[10] = "$INIT";
        stringArray[11] = "INITIALIZATION";
        stringArray[12] = "$INIT";
        stringArray[13] = "PARSING";
        stringArray[14] = "$INIT";
        stringArray[15] = "CONVERSION";
        stringArray[16] = "$INIT";
        stringArray[17] = "SEMANTIC_ANALYSIS";
        stringArray[18] = "$INIT";
        stringArray[19] = "CANONICALIZATION";
        stringArray[20] = "$INIT";
        stringArray[21] = "INSTRUCTION_SELECTION";
        stringArray[22] = "$INIT";
        stringArray[23] = "CLASS_GENERATION";
        stringArray[24] = "$INIT";
        stringArray[25] = "OUTPUT";
        stringArray[26] = "$INIT";
        stringArray[27] = "FINALIZATION";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[28];
        CompilePhaseAdapter.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(CompilePhaseAdapter.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = CompilePhaseAdapter.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

