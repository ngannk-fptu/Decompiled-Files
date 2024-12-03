/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.MetaClass;
import groovy.swing.factory.SwingBorderFactory;
import groovy.util.FactoryBuilderSupport;
import java.awt.Color;
import java.lang.ref.SoftReference;
import java.util.Map;
import javax.swing.BorderFactory;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class EtchedBorderFactory
extends SwingBorderFactory {
    private final int type;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public EtchedBorderFactory(int newType) {
        int n;
        MetaClass metaClass;
        CallSite[] callSiteArray = EtchedBorderFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        this.type = n = newType;
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        CallSite[] callSiteArray = EtchedBorderFactory.$getCallSiteArray();
        Object object = callSiteArray[0].call((Object)attributes, "parent");
        ScriptBytecodeAdapter.setProperty(object, null, callSiteArray[1].callGroovyObjectGetProperty(builder), "applyBorderToParent");
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call((Object)attributes, "highlight"))) {
            Color highlight = (Color)ScriptBytecodeAdapter.castToType(callSiteArray[3].call((Object)attributes, "highlight"), Color.class);
            Color shadow = (Color)ScriptBytecodeAdapter.castToType(callSiteArray[4].call((Object)attributes, "shadow"), Color.class);
            if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (DefaultTypeTransformation.booleanUnbox(highlight) && DefaultTypeTransformation.booleanUnbox(shadow) && !DefaultTypeTransformation.booleanUnbox(attributes)) {
                    return callSiteArray[5].call(BorderFactory.class, this.type, highlight, shadow);
                }
            } else if (DefaultTypeTransformation.booleanUnbox(highlight) && DefaultTypeTransformation.booleanUnbox(shadow) && !DefaultTypeTransformation.booleanUnbox(attributes)) {
                return callSiteArray[6].call(BorderFactory.class, this.type, highlight, shadow);
            }
        }
        if (DefaultTypeTransformation.booleanUnbox(attributes)) {
            throw (Throwable)callSiteArray[7].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name}, new String[]{"", " only accepts no attributes, or highlight: and shadow: attributes"}));
        }
        return callSiteArray[8].call(BorderFactory.class, this.type);
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != EtchedBorderFactory.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public final int getType() {
        return this.type;
    }

    public /* synthetic */ MetaClass super$3$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "remove";
        stringArray[1] = "context";
        stringArray[2] = "containsKey";
        stringArray[3] = "remove";
        stringArray[4] = "remove";
        stringArray[5] = "createEtchedBorder";
        stringArray[6] = "createEtchedBorder";
        stringArray[7] = "<$constructor$>";
        stringArray[8] = "createEtchedBorder";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[9];
        EtchedBorderFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(EtchedBorderFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = EtchedBorderFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

