/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.MetaClass;
import groovy.swing.factory.SwingBorderFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Map;
import javax.swing.border.LineBorder;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class LineBorderFactory
extends SwingBorderFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public LineBorderFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = LineBorderFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        CallSite[] callSiteArray = LineBorderFactory.$getCallSiteArray();
        Object object = callSiteArray[0].call((Object)attributes, "parent");
        ScriptBytecodeAdapter.setProperty(object, null, callSiteArray[1].callGroovyObjectGetProperty(builder), "applyBorderToParent");
        Object color = callSiteArray[2].call((Object)attributes, "color");
        if (ScriptBytecodeAdapter.compareEqual(color, null)) {
            throw (Throwable)callSiteArray[3].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name}, new String[]{"color: is a required attribute for ", ""}));
        }
        Object thickness = callSiteArray[4].call((Object)attributes, "thickness");
        if (ScriptBytecodeAdapter.compareEqual(thickness, null)) {
            int n = 1;
            thickness = n;
        }
        Object roundedCorners = callSiteArray[5].call((Object)attributes, "roundedCorners");
        if (ScriptBytecodeAdapter.compareEqual(roundedCorners, null)) {
            boolean bl = false;
            roundedCorners = bl;
        }
        if (DefaultTypeTransformation.booleanUnbox(attributes)) {
            throw (Throwable)callSiteArray[6].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name, callSiteArray[7].call(attributes)}, new String[]{"", " does not know how to handle the remaining attibutes: ", ""}));
        }
        return callSiteArray[8].callConstructor(LineBorder.class, color, thickness, roundedCorners);
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != LineBorderFactory.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ MetaClass super$3$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "remove";
        stringArray[1] = "context";
        stringArray[2] = "remove";
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "remove";
        stringArray[5] = "remove";
        stringArray[6] = "<$constructor$>";
        stringArray[7] = "keySet";
        stringArray[8] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[9];
        LineBorderFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(LineBorderFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = LineBorderFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

