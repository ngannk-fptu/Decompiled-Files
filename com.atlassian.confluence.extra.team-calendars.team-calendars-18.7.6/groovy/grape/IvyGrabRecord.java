/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.ivy.core.module.id.ModuleRevisionId
 */
package groovy.grape;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.List;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class IvyGrabRecord
implements GroovyObject {
    private ModuleRevisionId mrid;
    private List<String> conf;
    private boolean changing;
    private boolean transitive;
    private boolean force;
    private String classifier;
    private String ext;
    private String type;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static final /* synthetic */ long $const$0;
    private static final /* synthetic */ long $const$1;
    private static final /* synthetic */ long $const$2;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public IvyGrabRecord() {
        MetaClass metaClass;
        CallSite[] callSiteArray = IvyGrabRecord.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public int hashCode() {
        CallSite[] callSiteArray = IvyGrabRecord.$getCallSiteArray();
        return DefaultTypeTransformation.intUnbox(callSiteArray[0].call(callSiteArray[1].call(callSiteArray[2].call(callSiteArray[3].call(callSiteArray[4].call(callSiteArray[5].call(callSiteArray[6].call(callSiteArray[7].call(this.mrid), callSiteArray[8].call(this.conf)), this.changing ? $const$0 : (long)0x55555555), this.transitive ? $const$1 : (long)0x66666666), this.force ? $const$2 : (long)0x77777777), DefaultTypeTransformation.booleanUnbox(this.classifier) ? callSiteArray[9].call(this.classifier) : Integer.valueOf(0)), DefaultTypeTransformation.booleanUnbox(this.ext) ? callSiteArray[10].call(this.ext) : Integer.valueOf(0)), DefaultTypeTransformation.booleanUnbox(this.type) ? callSiteArray[11].call(this.type) : Integer.valueOf(0)));
    }

    public boolean equals(Object o) {
        CallSite[] callSiteArray = IvyGrabRecord.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return ScriptBytecodeAdapter.compareEqual(callSiteArray[12].callGetProperty(o), IvyGrabRecord.class) && ScriptBytecodeAdapter.compareEqual(this.changing, callSiteArray[13].callGetProperty(o)) && ScriptBytecodeAdapter.compareEqual(this.transitive, callSiteArray[14].callGetProperty(o)) && ScriptBytecodeAdapter.compareEqual(this.force, callSiteArray[15].callGetProperty(o)) && ScriptBytecodeAdapter.compareEqual(this.mrid, callSiteArray[16].callGetProperty(o)) && ScriptBytecodeAdapter.compareEqual(this.conf, callSiteArray[17].callGetProperty(o)) && ScriptBytecodeAdapter.compareEqual(this.classifier, callSiteArray[18].callGetProperty(o)) && ScriptBytecodeAdapter.compareEqual(this.ext, callSiteArray[19].callGetProperty(o)) && ScriptBytecodeAdapter.compareEqual(this.type, callSiteArray[20].callGetProperty(o));
        }
        return ScriptBytecodeAdapter.compareEqual(callSiteArray[21].callGetProperty(o), IvyGrabRecord.class) && ScriptBytecodeAdapter.compareEqual(this.changing, callSiteArray[22].callGetProperty(o)) && ScriptBytecodeAdapter.compareEqual(this.transitive, callSiteArray[23].callGetProperty(o)) && ScriptBytecodeAdapter.compareEqual(this.force, callSiteArray[24].callGetProperty(o)) && ScriptBytecodeAdapter.compareEqual(this.mrid, callSiteArray[25].callGetProperty(o)) && ScriptBytecodeAdapter.compareEqual(this.conf, callSiteArray[26].callGetProperty(o)) && ScriptBytecodeAdapter.compareEqual(this.classifier, callSiteArray[27].callGetProperty(o)) && ScriptBytecodeAdapter.compareEqual(this.ext, callSiteArray[28].callGetProperty(o)) && ScriptBytecodeAdapter.compareEqual(this.type, callSiteArray[29].callGetProperty(o));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != IvyGrabRecord.class) {
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

    public static /* synthetic */ void __$swapInit() {
        long l;
        long l2;
        long l3;
        CallSite[] callSiteArray = IvyGrabRecord.$getCallSiteArray();
        $callSiteArray = null;
        $const$0 = l3 = 0xAAAAAAAAL;
        $const$1 = l2 = 0xBBBBBBBBL;
        $const$2 = l = 0xCCCCCCCCL;
    }

    static {
        IvyGrabRecord.__$swapInit();
    }

    public ModuleRevisionId getMrid() {
        return this.mrid;
    }

    public void setMrid(ModuleRevisionId moduleRevisionId) {
        this.mrid = moduleRevisionId;
    }

    public List<String> getConf() {
        return this.conf;
    }

    public void setConf(List<String> list) {
        this.conf = list;
    }

    public boolean getChanging() {
        return this.changing;
    }

    public boolean isChanging() {
        return this.changing;
    }

    public void setChanging(boolean bl) {
        this.changing = bl;
    }

    public boolean getTransitive() {
        return this.transitive;
    }

    public boolean isTransitive() {
        return this.transitive;
    }

    public void setTransitive(boolean bl) {
        this.transitive = bl;
    }

    public boolean getForce() {
        return this.force;
    }

    public boolean isForce() {
        return this.force;
    }

    public void setForce(boolean bl) {
        this.force = bl;
    }

    public String getClassifier() {
        return this.classifier;
    }

    public void setClassifier(String string) {
        this.classifier = string;
    }

    public String getExt() {
        return this.ext;
    }

    public void setExt(String string) {
        this.ext = string;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String string) {
        this.type = string;
    }

    public /* synthetic */ boolean super$1$equals(Object object) {
        return super.equals(object);
    }

    public /* synthetic */ int super$1$hashCode() {
        return super.hashCode();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "xor";
        stringArray[1] = "xor";
        stringArray[2] = "xor";
        stringArray[3] = "xor";
        stringArray[4] = "xor";
        stringArray[5] = "xor";
        stringArray[6] = "xor";
        stringArray[7] = "hashCode";
        stringArray[8] = "hashCode";
        stringArray[9] = "hashCode";
        stringArray[10] = "hashCode";
        stringArray[11] = "hashCode";
        stringArray[12] = "class";
        stringArray[13] = "changing";
        stringArray[14] = "transitive";
        stringArray[15] = "force";
        stringArray[16] = "mrid";
        stringArray[17] = "conf";
        stringArray[18] = "classifier";
        stringArray[19] = "ext";
        stringArray[20] = "type";
        stringArray[21] = "class";
        stringArray[22] = "changing";
        stringArray[23] = "transitive";
        stringArray[24] = "force";
        stringArray[25] = "mrid";
        stringArray[26] = "conf";
        stringArray[27] = "classifier";
        stringArray[28] = "ext";
        stringArray[29] = "type";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[30];
        IvyGrabRecord.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(IvyGrabRecord.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = IvyGrabRecord.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

