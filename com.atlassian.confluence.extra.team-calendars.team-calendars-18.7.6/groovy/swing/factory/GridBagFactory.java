/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.MetaClass;
import groovy.swing.factory.LayoutFactory;
import groovy.util.FactoryBuilderSupport;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.ref.SoftReference;
import java.util.Map;
import javax.swing.RootPaneContainer;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class GridBagFactory
extends LayoutFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public GridBagFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = GridBagFactory.$getCallSiteArray();
        super(GridBagLayout.class, true);
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public void addLayoutProperties(Object context) {
        CallSite[] callSiteArray = GridBagFactory.$getCallSiteArray();
        callSiteArray[0].callCurrent(this, context, GridBagConstraints.class);
    }

    public static void processGridBagConstraintsAttributes(FactoryBuilderSupport builder, Object node, Map attributes) {
        Object object;
        Object object2;
        Object object3;
        Object object4;
        Object object5;
        Object object6;
        Object object7;
        Object object8;
        Object object9;
        Object object10;
        Object object11;
        CallSite[] callSiteArray = GridBagFactory.$getCallSiteArray();
        if (!(node instanceof Component)) {
            return;
        }
        Object object12 = callSiteArray[1].callSafe(callSiteArray[2].callGroovyObjectGetPropertySafe(builder), callSiteArray[3].callGetProperty(LayoutFactory.class));
        Object constraintsAttr = DefaultTypeTransformation.booleanUnbox(object12) ? object12 : callSiteArray[4].callGetProperty(LayoutFactory.class);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[5].call((Object)attributes, constraintsAttr))) {
            return;
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[6].call(callSiteArray[7].call(builder), "constraints"))) {
            return;
        }
        Object parent = callSiteArray[8].call(builder);
        if (parent instanceof RootPaneContainer) {
            if (!(callSiteArray[9].call(callSiteArray[10].call((RootPaneContainer)ScriptBytecodeAdapter.castToType(parent, RootPaneContainer.class))) instanceof GridBagLayout)) {
                return;
            }
        } else if (parent instanceof Container) {
            if (!(callSiteArray[11].call((Container)ScriptBytecodeAdapter.castToType(parent, Container.class)) instanceof GridBagLayout)) {
                return;
            }
        } else {
            return;
        }
        boolean anyAttrs = false;
        GridBagConstraints gbc = (GridBagConstraints)ScriptBytecodeAdapter.castToType(callSiteArray[12].callConstructor(GridBagConstraints.class), GridBagConstraints.class);
        Object o = null;
        o = object11 = callSiteArray[13].callStatic(GridBagFactory.class, attributes, "gridx", Number.class);
        if (ScriptBytecodeAdapter.compareNotEqual(o, null)) {
            boolean bl;
            Object object13 = o;
            ScriptBytecodeAdapter.setProperty(object13, null, gbc, "gridx");
            anyAttrs = bl = true;
        }
        o = object10 = callSiteArray[14].callStatic(GridBagFactory.class, attributes, "gridy", Number.class);
        if (ScriptBytecodeAdapter.compareNotEqual(o, null)) {
            boolean bl;
            Object object14 = o;
            ScriptBytecodeAdapter.setProperty(object14, null, gbc, "gridy");
            anyAttrs = bl = true;
        }
        o = object9 = callSiteArray[15].callStatic(GridBagFactory.class, attributes, "gridwidth", Number.class);
        if (ScriptBytecodeAdapter.compareNotEqual(o, null)) {
            boolean bl;
            Object object15 = o;
            ScriptBytecodeAdapter.setProperty(object15, null, gbc, "gridwidth");
            anyAttrs = bl = true;
        }
        o = object8 = callSiteArray[16].callStatic(GridBagFactory.class, attributes, "gridheight", Number.class);
        if (ScriptBytecodeAdapter.compareNotEqual(o, null)) {
            boolean bl;
            Object object16 = o;
            ScriptBytecodeAdapter.setProperty(object16, null, gbc, "gridheight");
            anyAttrs = bl = true;
        }
        o = object7 = callSiteArray[17].callStatic(GridBagFactory.class, attributes, "weightx", Number.class);
        if (ScriptBytecodeAdapter.compareNotEqual(o, null)) {
            boolean bl;
            Object object17 = o;
            ScriptBytecodeAdapter.setProperty(object17, null, gbc, "weightx");
            anyAttrs = bl = true;
        }
        o = object6 = callSiteArray[18].callStatic(GridBagFactory.class, attributes, "weighty", Number.class);
        if (ScriptBytecodeAdapter.compareNotEqual(o, null)) {
            boolean bl;
            Object object18 = o;
            ScriptBytecodeAdapter.setProperty(object18, null, gbc, "weighty");
            anyAttrs = bl = true;
        }
        o = object5 = callSiteArray[19].callStatic(GridBagFactory.class, attributes, "anchor", Number.class);
        if (ScriptBytecodeAdapter.compareNotEqual(o, null)) {
            boolean bl;
            Object object19 = o;
            ScriptBytecodeAdapter.setProperty(object19, null, gbc, "anchor");
            anyAttrs = bl = true;
        }
        o = object4 = callSiteArray[20].callStatic(GridBagFactory.class, attributes, "fill", Number.class);
        if (ScriptBytecodeAdapter.compareNotEqual(o, null)) {
            boolean bl;
            Object object20 = o;
            ScriptBytecodeAdapter.setProperty(object20, null, gbc, "fill");
            anyAttrs = bl = true;
        }
        o = object3 = callSiteArray[21].callStatic(GridBagFactory.class, attributes, "insets", Object.class);
        if (ScriptBytecodeAdapter.compareNotEqual(o, null)) {
            boolean bl;
            Insets insets = (Insets)ScriptBytecodeAdapter.asType(o, Insets.class);
            ScriptBytecodeAdapter.setProperty(insets, null, gbc, "insets");
            anyAttrs = bl = true;
        }
        o = object2 = callSiteArray[22].callStatic(GridBagFactory.class, attributes, "ipadx", Number.class);
        if (ScriptBytecodeAdapter.compareNotEqual(o, null)) {
            boolean bl;
            Object object21 = o;
            ScriptBytecodeAdapter.setProperty(object21, null, gbc, "ipadx");
            anyAttrs = bl = true;
        }
        o = object = callSiteArray[23].callStatic(GridBagFactory.class, attributes, "ipady", Number.class);
        if (ScriptBytecodeAdapter.compareNotEqual(o, null)) {
            boolean bl;
            Object object22 = o;
            ScriptBytecodeAdapter.setProperty(object22, null, gbc, "ipady");
            anyAttrs = bl = true;
        }
        if (anyAttrs) {
            callSiteArray[24].call(callSiteArray[25].call(builder), "constraints", gbc);
        }
    }

    public static Object extractAttribute(Map attrs, String name, Class type) {
        CallSite[] callSiteArray = GridBagFactory.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[26].call((Object)attrs, name))) {
            Object o = callSiteArray[27].call((Object)attrs, name);
            if (ScriptBytecodeAdapter.compareNotEqual(o, null) && DefaultTypeTransformation.booleanUnbox(callSiteArray[28].call((Object)type, type))) {
                callSiteArray[29].call((Object)attrs, name);
                return o;
            }
            return null;
        }
        return null;
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != GridBagFactory.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ MetaClass super$4$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    public /* synthetic */ void super$4$addLayoutProperties(Object object) {
        super.addLayoutProperties(object);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "addLayoutProperties";
        stringArray[1] = "getAt";
        stringArray[2] = "context";
        stringArray[3] = "DELEGATE_PROPERTY_CONSTRAINT";
        stringArray[4] = "DEFAULT_DELEGATE_PROPERTY_CONSTRAINT";
        stringArray[5] = "containsKey";
        stringArray[6] = "containsKey";
        stringArray[7] = "getContext";
        stringArray[8] = "getCurrent";
        stringArray[9] = "getLayout";
        stringArray[10] = "getContentPane";
        stringArray[11] = "getLayout";
        stringArray[12] = "<$constructor$>";
        stringArray[13] = "extractAttribute";
        stringArray[14] = "extractAttribute";
        stringArray[15] = "extractAttribute";
        stringArray[16] = "extractAttribute";
        stringArray[17] = "extractAttribute";
        stringArray[18] = "extractAttribute";
        stringArray[19] = "extractAttribute";
        stringArray[20] = "extractAttribute";
        stringArray[21] = "extractAttribute";
        stringArray[22] = "extractAttribute";
        stringArray[23] = "extractAttribute";
        stringArray[24] = "put";
        stringArray[25] = "getContext";
        stringArray[26] = "containsKey";
        stringArray[27] = "get";
        stringArray[28] = "isAssignableFrom";
        stringArray[29] = "remove";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[30];
        GridBagFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(GridBagFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = GridBagFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

