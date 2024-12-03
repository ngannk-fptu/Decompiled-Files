/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.MetaClass;
import groovy.swing.factory.SwingBorderFactory;
import groovy.util.FactoryBuilderSupport;
import java.awt.Color;
import java.awt.Font;
import java.lang.ref.SoftReference;
import java.util.Map;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class TitledBorderFactory
extends SwingBorderFactory {
    private static final Map positions;
    private static final Map justifications;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public TitledBorderFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = TitledBorderFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        Object object;
        Object object2;
        CallSite[] callSiteArray = TitledBorderFactory.$getCallSiteArray();
        Object object3 = callSiteArray[0].call((Object)attributes, "parent");
        ScriptBytecodeAdapter.setProperty(object3, null, callSiteArray[1].callGroovyObjectGetProperty(builder), "applyBorderToParent");
        String title = null;
        if (DefaultTypeTransformation.booleanUnbox(value)) {
            String string;
            title = string = (String)ScriptBytecodeAdapter.asType(value, String.class);
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call((Object)attributes, "title"))) {
                throw (Throwable)callSiteArray[3].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name}, new String[]{"", " cannot have both a value attribute and an attribute title:"}));
            }
        } else {
            String string;
            title = string = (String)ScriptBytecodeAdapter.asType(callSiteArray[4].call((Object)attributes, "title"), String.class);
        }
        TitledBorder border = (TitledBorder)ScriptBytecodeAdapter.castToType(callSiteArray[5].callConstructor(TitledBorder.class, title), TitledBorder.class);
        Object position = callSiteArray[6].call((Object)attributes, "position");
        Object object4 = callSiteArray[7].call((Object)positions, position);
        position = object2 = DefaultTypeTransformation.booleanUnbox(object4) ? object4 : position;
        if (position instanceof Integer) {
            callSiteArray[8].call((Object)border, position);
        }
        Object justification = callSiteArray[9].call((Object)attributes, "justification");
        Object object5 = callSiteArray[10].call((Object)justifications, justification);
        justification = object = DefaultTypeTransformation.booleanUnbox(object5) ? object5 : justification;
        if (justification instanceof Integer) {
            callSiteArray[11].call((Object)border, justification);
        }
        Border otherBorder = (Border)ScriptBytecodeAdapter.castToType(callSiteArray[12].call((Object)attributes, "border"), Border.class);
        if (ScriptBytecodeAdapter.compareNotEqual(otherBorder, null)) {
            callSiteArray[13].call((Object)border, otherBorder);
        }
        Color color = (Color)ScriptBytecodeAdapter.castToType(callSiteArray[14].call((Object)attributes, "color"), Color.class);
        if (DefaultTypeTransformation.booleanUnbox(color)) {
            callSiteArray[15].call((Object)border, color);
        }
        Font font = (Font)ScriptBytecodeAdapter.castToType(callSiteArray[16].call((Object)attributes, "font"), Font.class);
        if (DefaultTypeTransformation.booleanUnbox(font)) {
            callSiteArray[17].call((Object)border, font);
        }
        return border;
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != TitledBorderFactory.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    static {
        Map map;
        Map map2;
        positions = map2 = ScriptBytecodeAdapter.createMap(new Object[]{"default", TitledBorderFactory.$getCallSiteArray()[18].callGetProperty(TitledBorder.class), "aboveTop", TitledBorderFactory.$getCallSiteArray()[19].callGetProperty(TitledBorder.class), "top", TitledBorderFactory.$getCallSiteArray()[20].callGetProperty(TitledBorder.class), "belowTop", TitledBorderFactory.$getCallSiteArray()[21].callGetProperty(TitledBorder.class), "aboveBottom", TitledBorderFactory.$getCallSiteArray()[22].callGetProperty(TitledBorder.class), "bottom", TitledBorderFactory.$getCallSiteArray()[23].callGetProperty(TitledBorder.class), "belowBottom", TitledBorderFactory.$getCallSiteArray()[24].callGetProperty(TitledBorder.class)});
        justifications = map = ScriptBytecodeAdapter.createMap(new Object[]{"default", TitledBorderFactory.$getCallSiteArray()[25].callGetProperty(TitledBorder.class), "left", TitledBorderFactory.$getCallSiteArray()[26].callGetProperty(TitledBorder.class), "center", TitledBorderFactory.$getCallSiteArray()[27].callGetProperty(TitledBorder.class), "right", TitledBorderFactory.$getCallSiteArray()[28].callGetProperty(TitledBorder.class), "leading", TitledBorderFactory.$getCallSiteArray()[29].callGetProperty(TitledBorder.class), "trailing", TitledBorderFactory.$getCallSiteArray()[30].callGetProperty(TitledBorder.class)});
    }

    public static Map getPositions() {
        return positions;
    }

    public static Map getJustifications() {
        return justifications;
    }

    public /* synthetic */ MetaClass super$3$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "remove";
        stringArray[1] = "context";
        stringArray[2] = "containsKey";
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "remove";
        stringArray[5] = "<$constructor$>";
        stringArray[6] = "remove";
        stringArray[7] = "getAt";
        stringArray[8] = "setTitlePosition";
        stringArray[9] = "remove";
        stringArray[10] = "getAt";
        stringArray[11] = "setTitleJustification";
        stringArray[12] = "remove";
        stringArray[13] = "setBorder";
        stringArray[14] = "remove";
        stringArray[15] = "setTitleColor";
        stringArray[16] = "remove";
        stringArray[17] = "setTitleFont";
        stringArray[18] = "DEFAULT_POSITION";
        stringArray[19] = "ABOVE_TOP";
        stringArray[20] = "TOP";
        stringArray[21] = "BELOW_TOP";
        stringArray[22] = "ABOVE_BOTTOM";
        stringArray[23] = "BOTTOM";
        stringArray[24] = "BELOW_BOTTOM";
        stringArray[25] = "DEFAULT_JUSTIFICATION";
        stringArray[26] = "LEFT";
        stringArray[27] = "CENTER";
        stringArray[28] = "RIGHT";
        stringArray[29] = "LEADING";
        stringArray[30] = "TRAILING";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[31];
        TitledBorderFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(TitledBorderFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = TitledBorderFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

