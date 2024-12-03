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
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class BevelBorderFactory
extends SwingBorderFactory {
    private final int type;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public BevelBorderFactory(int newType) {
        int n;
        MetaClass metaClass;
        CallSite[] callSiteArray = BevelBorderFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        this.type = n = newType;
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        CallSite[] callSiteArray = BevelBorderFactory.$getCallSiteArray();
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
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[7].call((Object)attributes, "highlightOuter"))) {
            Color highlightOuter = (Color)ScriptBytecodeAdapter.castToType(callSiteArray[8].call((Object)attributes, "highlightOuter"), Color.class);
            Color highlightInner = (Color)ScriptBytecodeAdapter.castToType(callSiteArray[9].call((Object)attributes, "highlightInner"), Color.class);
            Color shadowOuter = (Color)ScriptBytecodeAdapter.castToType(callSiteArray[10].call((Object)attributes, "shadowOuter"), Color.class);
            Color shadowInner = (Color)ScriptBytecodeAdapter.castToType(callSiteArray[11].call((Object)attributes, "shadowInner"), Color.class);
            if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (DefaultTypeTransformation.booleanUnbox(highlightOuter) && DefaultTypeTransformation.booleanUnbox(highlightInner) && DefaultTypeTransformation.booleanUnbox(shadowOuter) && DefaultTypeTransformation.booleanUnbox(shadowInner) && !DefaultTypeTransformation.booleanUnbox(attributes)) {
                    return callSiteArray[12].call((Object)BorderFactory.class, ArrayUtil.createArray(this.type, highlightOuter, highlightInner, shadowOuter, shadowInner));
                }
            } else if (DefaultTypeTransformation.booleanUnbox(highlightOuter) && DefaultTypeTransformation.booleanUnbox(highlightInner) && DefaultTypeTransformation.booleanUnbox(shadowOuter) && DefaultTypeTransformation.booleanUnbox(shadowInner) && !DefaultTypeTransformation.booleanUnbox(attributes)) {
                return callSiteArray[13].call((Object)BorderFactory.class, ArrayUtil.createArray(this.type, highlightOuter, highlightInner, shadowOuter, shadowInner));
            }
        }
        if (DefaultTypeTransformation.booleanUnbox(attributes)) {
            throw (Throwable)callSiteArray[14].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name}, new String[]{"", " only accepts no attributes, or highlight: and shadow: attributes, or highlightOuter: and highlightInner: and shadowOuter: and shadowInner: attributes"}));
        }
        return callSiteArray[15].call(BorderFactory.class, this.type);
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != BevelBorderFactory.class) {
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
        stringArray[5] = "createBevelBorder";
        stringArray[6] = "createBevelBorder";
        stringArray[7] = "containsKey";
        stringArray[8] = "remove";
        stringArray[9] = "remove";
        stringArray[10] = "remove";
        stringArray[11] = "remove";
        stringArray[12] = "createBevelBorder";
        stringArray[13] = "createBevelBorder";
        stringArray[14] = "<$constructor$>";
        stringArray[15] = "createBevelBorder";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[16];
        BevelBorderFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(BevelBorderFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = BevelBorderFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

