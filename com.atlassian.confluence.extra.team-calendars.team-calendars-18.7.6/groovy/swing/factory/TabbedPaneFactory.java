/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.lang.MetaClass;
import groovy.lang.MissingPropertyException;
import groovy.lang.Reference;
import groovy.swing.factory.BeanFactory;
import groovy.util.FactoryBuilderSupport;
import java.awt.Component;
import java.awt.Window;
import java.lang.ref.SoftReference;
import java.util.Map;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class TabbedPaneFactory
extends BeanFactory {
    public static final String DELEGATE_PROPERTY_TITLE = "_delegateProperty:title";
    public static final String DEFAULT_DELEGATE_PROPERTY_TITLE = "title";
    public static final String DELEGATE_PROPERTY_TAB_ICON = "_delegateProperty:tabIcon";
    public static final String DEFAULT_DELEGATE_PROPERTY_TAB_ICON = "tabIcon";
    public static final String DELEGATE_PROPERTY_TAB_DISABLED_ICON = "_delegateProperty:tabDisabledIcon";
    public static final String DEFAULT_DELEGATE_PROPERTY_TAB_DISABLED_ICON = "tabDisabledIcon";
    public static final String DELEGATE_PROPERTY_TAB_TOOL_TIP = "_delegateProperty:tabToolTip";
    public static final String DEFAULT_DELEGATE_PROPERTY_TAB_TOOL_TIP = "tabToolTip";
    public static final String DELEGATE_PROPERTY_TAB_FOREGROUND = "_delegateProperty:tabForeground";
    public static final String DEFAULT_DELEGATE_PROPERTY_TAB_FOREGROUND = "tabForeground";
    public static final String DELEGATE_PROPERTY_TAB_BACKGROUND = "_delegateProperty:tabBackground";
    public static final String DEFAULT_DELEGATE_PROPERTY_TAB_BACKGROUND = "tabBackground";
    public static final String DELEGATE_PROPERTY_TAB_ENABLED = "_delegateProperty:tabEnabled";
    public static final String DEFAULT_DELEGATE_PROPERTY_TAB_ENABLED = "tabEnabled";
    public static final String DELEGATE_PROPERTY_TAB_MNEMONIC = "_delegateProperty:tabMnemonic";
    public static final String DEFAULT_DELEGATE_PROPERTY_TAB_MNEMONIC = "tabMnemonic";
    public static final String DELEGATE_PROPERTY_TAB_DISPLAYED_MNEMONIC_INDEX = "_delegateProperty:tabDisplayedMnemonicIndex";
    public static final String DEFAULT_DELEGATE_PROPERTY_TAB_DISPLAYED_MNEMONIC_INDEX = "tabDisplayedMnemonicIndex";
    public static final String CONTEXT_DATA_KEY = "TabbdePaneFactoryData";
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public TabbedPaneFactory(Class beanClass) {
        MetaClass metaClass;
        CallSite[] callSiteArray = TabbedPaneFactory.$getCallSiteArray();
        super(beanClass, false);
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        Reference<FactoryBuilderSupport> builder2 = new Reference<FactoryBuilderSupport>(builder);
        CallSite[] callSiteArray = TabbedPaneFactory.$getCallSiteArray();
        Reference<Object> newChild = new Reference<Object>(ScriptBytecodeAdapter.invokeMethodOnSuperN(BeanFactory.class, this, "newInstance", new Object[]{builder2.get(), name, value, attributes}));
        public class _newInstance_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference builder;
            private /* synthetic */ Reference newChild;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _newInstance_closure1(Object _outerInstance, Object _thisObject, Reference builder, Reference newChild) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _newInstance_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.builder = reference2 = builder;
                this.newChild = reference = newChild;
            }

            public Object doCall(FactoryBuilderSupport cBuilder, Object cNode, Map cAttributes) {
                CallSite[] callSiteArray = _newInstance_closure1.$getCallSiteArray();
                if (ScriptBytecodeAdapter.compareEqual(callSiteArray[0].callGroovyObjectGetProperty(this.builder.get()), this.newChild.get())) {
                    return callSiteArray[1].callCurrent(this, cBuilder, cNode, cAttributes);
                }
                return null;
            }

            public Object call(FactoryBuilderSupport cBuilder, Object cNode, Map cAttributes) {
                CallSite[] callSiteArray = _newInstance_closure1.$getCallSiteArray();
                return callSiteArray[2].callCurrent(this, cBuilder, cNode, cAttributes);
            }

            public FactoryBuilderSupport getBuilder() {
                CallSite[] callSiteArray = _newInstance_closure1.$getCallSiteArray();
                return (FactoryBuilderSupport)ScriptBytecodeAdapter.castToType(this.builder.get(), FactoryBuilderSupport.class);
            }

            public Object getNewChild() {
                CallSite[] callSiteArray = _newInstance_closure1.$getCallSiteArray();
                return this.newChild.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _newInstance_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "current";
                stringArray[1] = "inspectChild";
                stringArray[2] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[3];
                _newInstance_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_newInstance_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _newInstance_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        _newInstance_closure1 _newInstance_closure12 = new _newInstance_closure1(this, this, builder2, newChild);
        ScriptBytecodeAdapter.setProperty(_newInstance_closure12, null, callSiteArray[0].callGroovyObjectGetProperty(builder2.get()), "tabbedPaneFactoryClosure");
        callSiteArray[1].call((Object)builder2.get(), callSiteArray[2].callGetProperty(callSiteArray[3].callGroovyObjectGetProperty(builder2.get())));
        Object object = callSiteArray[4].call((Object)attributes, "selectedIndex");
        ScriptBytecodeAdapter.setProperty(object, null, callSiteArray[5].callGroovyObjectGetProperty(builder2.get()), "selectedIndex");
        Object object2 = callSiteArray[6].call((Object)attributes, "selectedComponent");
        ScriptBytecodeAdapter.setProperty(object2, null, callSiteArray[7].callGroovyObjectGetProperty(builder2.get()), "selectedComponent");
        Object object3 = callSiteArray[8].call((Object)attributes, "titleProperty");
        Object object4 = DefaultTypeTransformation.booleanUnbox(object3) ? object3 : DEFAULT_DELEGATE_PROPERTY_TITLE;
        callSiteArray[9].call(callSiteArray[10].callGroovyObjectGetProperty(builder2.get()), DELEGATE_PROPERTY_TITLE, object4);
        Object object5 = callSiteArray[11].call((Object)attributes, "tabIconProperty");
        Object object6 = DefaultTypeTransformation.booleanUnbox(object5) ? object5 : DEFAULT_DELEGATE_PROPERTY_TAB_ICON;
        callSiteArray[12].call(callSiteArray[13].callGroovyObjectGetProperty(builder2.get()), DELEGATE_PROPERTY_TAB_ICON, object6);
        Object object7 = callSiteArray[14].call((Object)attributes, "tabDisabledIconProperty");
        Object object8 = DefaultTypeTransformation.booleanUnbox(object7) ? object7 : DEFAULT_DELEGATE_PROPERTY_TAB_DISABLED_ICON;
        callSiteArray[15].call(callSiteArray[16].callGroovyObjectGetProperty(builder2.get()), DELEGATE_PROPERTY_TAB_DISABLED_ICON, object8);
        Object object9 = callSiteArray[17].call((Object)attributes, "tabToolTipProperty");
        Object object10 = DefaultTypeTransformation.booleanUnbox(object9) ? object9 : DEFAULT_DELEGATE_PROPERTY_TAB_TOOL_TIP;
        callSiteArray[18].call(callSiteArray[19].callGroovyObjectGetProperty(builder2.get()), DELEGATE_PROPERTY_TAB_TOOL_TIP, object10);
        Object object11 = callSiteArray[20].call((Object)attributes, "tabBackgroundProperty");
        Object object12 = DefaultTypeTransformation.booleanUnbox(object11) ? object11 : DEFAULT_DELEGATE_PROPERTY_TAB_BACKGROUND;
        callSiteArray[21].call(callSiteArray[22].callGroovyObjectGetProperty(builder2.get()), DELEGATE_PROPERTY_TAB_BACKGROUND, object12);
        Object object13 = callSiteArray[23].call((Object)attributes, "tabForegroundProperty");
        Object object14 = DefaultTypeTransformation.booleanUnbox(object13) ? object13 : DEFAULT_DELEGATE_PROPERTY_TAB_FOREGROUND;
        callSiteArray[24].call(callSiteArray[25].callGroovyObjectGetProperty(builder2.get()), DELEGATE_PROPERTY_TAB_FOREGROUND, object14);
        Object object15 = callSiteArray[26].call((Object)attributes, "tabEnabledProperty");
        Object object16 = DefaultTypeTransformation.booleanUnbox(object15) ? object15 : DEFAULT_DELEGATE_PROPERTY_TAB_ENABLED;
        callSiteArray[27].call(callSiteArray[28].callGroovyObjectGetProperty(builder2.get()), DELEGATE_PROPERTY_TAB_ENABLED, object16);
        Object object17 = callSiteArray[29].call((Object)attributes, "tabMnemonicProperty");
        Object object18 = DefaultTypeTransformation.booleanUnbox(object17) ? object17 : DEFAULT_DELEGATE_PROPERTY_TAB_MNEMONIC;
        callSiteArray[30].call(callSiteArray[31].callGroovyObjectGetProperty(builder2.get()), DELEGATE_PROPERTY_TAB_MNEMONIC, object18);
        Object object19 = callSiteArray[32].call((Object)attributes, "tabDisplayedMnemonicIndexProperty");
        Object object20 = DefaultTypeTransformation.booleanUnbox(object19) ? object19 : DEFAULT_DELEGATE_PROPERTY_TAB_DISPLAYED_MNEMONIC_INDEX;
        callSiteArray[33].call(callSiteArray[34].callGroovyObjectGetProperty(builder2.get()), DELEGATE_PROPERTY_TAB_DISPLAYED_MNEMONIC_INDEX, object20);
        return newChild.get();
    }

    public static void inspectChild(FactoryBuilderSupport builder, Object node, Map attributes) {
        CallSite[] callSiteArray = TabbedPaneFactory.$getCallSiteArray();
        if (!(node instanceof Component) || node instanceof Window) {
            return;
        }
        Object object = callSiteArray[36].callSafe(callSiteArray[37].callGroovyObjectGetPropertySafe(builder), DELEGATE_PROPERTY_TITLE);
        Object name = callSiteArray[35].call((Object)attributes, DefaultTypeTransformation.booleanUnbox(object) ? object : DEFAULT_DELEGATE_PROPERTY_TITLE);
        Object object2 = callSiteArray[39].callSafe(callSiteArray[40].callGroovyObjectGetPropertySafe(builder), DELEGATE_PROPERTY_TAB_ICON);
        Object icon = callSiteArray[38].call((Object)attributes, DefaultTypeTransformation.booleanUnbox(object2) ? object2 : DEFAULT_DELEGATE_PROPERTY_TAB_ICON);
        Object object3 = callSiteArray[42].callSafe(callSiteArray[43].callGroovyObjectGetPropertySafe(builder), DELEGATE_PROPERTY_TAB_DISABLED_ICON);
        Object disabledIcon = callSiteArray[41].call((Object)attributes, DefaultTypeTransformation.booleanUnbox(object3) ? object3 : DEFAULT_DELEGATE_PROPERTY_TAB_DISABLED_ICON);
        Object object4 = callSiteArray[45].callSafe(callSiteArray[46].callGroovyObjectGetPropertySafe(builder), DELEGATE_PROPERTY_TAB_TOOL_TIP);
        Object toolTip = callSiteArray[44].call((Object)attributes, DefaultTypeTransformation.booleanUnbox(object4) ? object4 : DEFAULT_DELEGATE_PROPERTY_TAB_TOOL_TIP);
        Object object5 = callSiteArray[48].callSafe(callSiteArray[49].callGroovyObjectGetPropertySafe(builder), DELEGATE_PROPERTY_TAB_BACKGROUND);
        Object background = callSiteArray[47].call((Object)attributes, DefaultTypeTransformation.booleanUnbox(object5) ? object5 : DEFAULT_DELEGATE_PROPERTY_TAB_BACKGROUND);
        Object object6 = callSiteArray[51].callSafe(callSiteArray[52].callGroovyObjectGetPropertySafe(builder), DELEGATE_PROPERTY_TAB_FOREGROUND);
        Object foreground = callSiteArray[50].call((Object)attributes, DefaultTypeTransformation.booleanUnbox(object6) ? object6 : DEFAULT_DELEGATE_PROPERTY_TAB_FOREGROUND);
        Object object7 = callSiteArray[54].callSafe(callSiteArray[55].callGroovyObjectGetPropertySafe(builder), DELEGATE_PROPERTY_TAB_ENABLED);
        Object enabled = callSiteArray[53].call((Object)attributes, DefaultTypeTransformation.booleanUnbox(object7) ? object7 : DEFAULT_DELEGATE_PROPERTY_TAB_ENABLED);
        Object object8 = callSiteArray[57].callSafe(callSiteArray[58].callGroovyObjectGetPropertySafe(builder), DELEGATE_PROPERTY_TAB_MNEMONIC);
        Object mnemonic = callSiteArray[56].call((Object)attributes, DefaultTypeTransformation.booleanUnbox(object8) ? object8 : DEFAULT_DELEGATE_PROPERTY_TAB_MNEMONIC);
        Object object9 = callSiteArray[60].callSafe(callSiteArray[61].callGroovyObjectGetPropertySafe(builder), DELEGATE_PROPERTY_TAB_DISPLAYED_MNEMONIC_INDEX);
        Object displayedMnemonicIndex = callSiteArray[59].call((Object)attributes, DefaultTypeTransformation.booleanUnbox(object9) ? object9 : DEFAULT_DELEGATE_PROPERTY_TAB_DISPLAYED_MNEMONIC_INDEX);
        Object object10 = callSiteArray[62].call(callSiteArray[63].callGroovyObjectGetProperty(builder), CONTEXT_DATA_KEY);
        Object tabbedPaneContext = DefaultTypeTransformation.booleanUnbox(object10) ? object10 : ScriptBytecodeAdapter.createMap(new Object[0]);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[64].call(tabbedPaneContext))) {
            callSiteArray[65].call(callSiteArray[66].callGroovyObjectGetProperty(builder), CONTEXT_DATA_KEY, tabbedPaneContext);
        }
        callSiteArray[67].call(tabbedPaneContext, node, ScriptBytecodeAdapter.createList(new Object[]{name, icon, disabledIcon, toolTip, background, foreground, enabled, mnemonic, displayedMnemonicIndex}));
    }

    @Override
    public void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        CallSite[] callSiteArray = TabbedPaneFactory.$getCallSiteArray();
        if (!(child instanceof Component) || child instanceof Window) {
            return;
        }
        try {
            Object object = callSiteArray[68].callSafe(callSiteArray[69].call(callSiteArray[70].callGroovyObjectGetProperty(builder), CONTEXT_DATA_KEY), child);
            Object title = DefaultTypeTransformation.booleanUnbox(object) ? object : ScriptBytecodeAdapter.createList(new Object[]{null, null, null, null, null, null, null, null, null});
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[71].call(title, 0), null)) {
                Object object2 = callSiteArray[72].callGetProperty(child);
                callSiteArray[73].call(title, 0, object2);
            }
            callSiteArray[74].call(parent, callSiteArray[75].call(title, 0), callSiteArray[76].call(title, 1), child, callSiteArray[77].call(title, 3));
            int index = DefaultTypeTransformation.intUnbox(callSiteArray[78].call(parent, child));
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[79].call(title, 2))) {
                callSiteArray[80].call(parent, index, callSiteArray[81].call(title, 2));
            }
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[82].call(title, 4))) {
                callSiteArray[83].call(parent, index, callSiteArray[84].call(title, 4));
            }
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[85].call(title, 5))) {
                callSiteArray[86].call(parent, index, callSiteArray[87].call(title, 5));
            }
            if (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[88].call(title, 6), null)) {
                callSiteArray[89].call(parent, index, callSiteArray[90].call(title, 6));
            }
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[91].call(title, 7))) {
                Object mnemonic = callSiteArray[92].call(title, 7);
                if (mnemonic instanceof String || mnemonic instanceof GString) {
                    callSiteArray[93].call(parent, index, ScriptBytecodeAdapter.createPojoWrapper(DefaultTypeTransformation.intUnbox(ScriptBytecodeAdapter.asType(callSiteArray[94].call(mnemonic, 0), Integer.TYPE)), Integer.TYPE));
                } else {
                    callSiteArray[95].call(parent, index, ScriptBytecodeAdapter.createPojoWrapper(DefaultTypeTransformation.intUnbox(ScriptBytecodeAdapter.asType(mnemonic, Integer.TYPE)), Integer.TYPE));
                }
            }
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[96].call(title, 8))) {
                callSiteArray[97].call(parent, index, callSiteArray[98].call(title, 8));
            }
        }
        catch (MissingPropertyException mpe) {
            callSiteArray[99].call(parent, child);
        }
    }

    @Override
    public void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        CallSite[] callSiteArray = TabbedPaneFactory.$getCallSiteArray();
        ScriptBytecodeAdapter.invokeMethodOnSuperN(BeanFactory.class, this, "onNodeCompleted", new Object[]{builder, parent, node});
        callSiteArray[100].call((Object)builder, callSiteArray[101].callGetProperty(callSiteArray[102].callGroovyObjectGetProperty(builder)));
        if (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[103].callGetProperty(callSiteArray[104].callGroovyObjectGetProperty(builder)), null)) {
            Object object = callSiteArray[105].callGetProperty(callSiteArray[106].callGroovyObjectGetProperty(builder));
            ScriptBytecodeAdapter.setProperty(object, null, node, "selectedComponent");
        }
        if (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[107].callGetProperty(callSiteArray[108].callGroovyObjectGetProperty(builder)), null)) {
            Object object = callSiteArray[109].callGetProperty(callSiteArray[110].callGroovyObjectGetProperty(builder));
            ScriptBytecodeAdapter.setProperty(object, null, node, "selectedIndex");
        }
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != TabbedPaneFactory.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ Object super$3$newInstance(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2, Map map) {
        return super.newInstance(factoryBuilderSupport, object, object2, map);
    }

    public /* synthetic */ void super$2$onNodeCompleted(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2) {
        super.onNodeCompleted(factoryBuilderSupport, object, object2);
    }

    public /* synthetic */ void super$2$setChild(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2) {
        super.setChild(factoryBuilderSupport, object, object2);
    }

    public /* synthetic */ MetaClass super$3$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "context";
        stringArray[1] = "addAttributeDelegate";
        stringArray[2] = "tabbedPaneFactoryClosure";
        stringArray[3] = "context";
        stringArray[4] = "remove";
        stringArray[5] = "context";
        stringArray[6] = "remove";
        stringArray[7] = "context";
        stringArray[8] = "remove";
        stringArray[9] = "putAt";
        stringArray[10] = "context";
        stringArray[11] = "remove";
        stringArray[12] = "putAt";
        stringArray[13] = "context";
        stringArray[14] = "remove";
        stringArray[15] = "putAt";
        stringArray[16] = "context";
        stringArray[17] = "remove";
        stringArray[18] = "putAt";
        stringArray[19] = "context";
        stringArray[20] = "remove";
        stringArray[21] = "putAt";
        stringArray[22] = "context";
        stringArray[23] = "remove";
        stringArray[24] = "putAt";
        stringArray[25] = "context";
        stringArray[26] = "remove";
        stringArray[27] = "putAt";
        stringArray[28] = "context";
        stringArray[29] = "remove";
        stringArray[30] = "putAt";
        stringArray[31] = "context";
        stringArray[32] = "remove";
        stringArray[33] = "putAt";
        stringArray[34] = "context";
        stringArray[35] = "remove";
        stringArray[36] = "getAt";
        stringArray[37] = "parentContext";
        stringArray[38] = "remove";
        stringArray[39] = "getAt";
        stringArray[40] = "parentContext";
        stringArray[41] = "remove";
        stringArray[42] = "getAt";
        stringArray[43] = "parentContext";
        stringArray[44] = "remove";
        stringArray[45] = "getAt";
        stringArray[46] = "parentContext";
        stringArray[47] = "remove";
        stringArray[48] = "getAt";
        stringArray[49] = "parentContext";
        stringArray[50] = "remove";
        stringArray[51] = "getAt";
        stringArray[52] = "parentContext";
        stringArray[53] = "remove";
        stringArray[54] = "getAt";
        stringArray[55] = "parentContext";
        stringArray[56] = "remove";
        stringArray[57] = "getAt";
        stringArray[58] = "parentContext";
        stringArray[59] = "remove";
        stringArray[60] = "getAt";
        stringArray[61] = "parentContext";
        stringArray[62] = "get";
        stringArray[63] = "context";
        stringArray[64] = "isEmpty";
        stringArray[65] = "put";
        stringArray[66] = "context";
        stringArray[67] = "put";
        stringArray[68] = "get";
        stringArray[69] = "getAt";
        stringArray[70] = "context";
        stringArray[71] = "getAt";
        stringArray[72] = "name";
        stringArray[73] = "putAt";
        stringArray[74] = "addTab";
        stringArray[75] = "getAt";
        stringArray[76] = "getAt";
        stringArray[77] = "getAt";
        stringArray[78] = "indexOfComponent";
        stringArray[79] = "getAt";
        stringArray[80] = "setDisabledIconAt";
        stringArray[81] = "getAt";
        stringArray[82] = "getAt";
        stringArray[83] = "setBackgroundAt";
        stringArray[84] = "getAt";
        stringArray[85] = "getAt";
        stringArray[86] = "setForegroundAt";
        stringArray[87] = "getAt";
        stringArray[88] = "getAt";
        stringArray[89] = "setEnabledAt";
        stringArray[90] = "getAt";
        stringArray[91] = "getAt";
        stringArray[92] = "getAt";
        stringArray[93] = "setMnemonicAt";
        stringArray[94] = "charAt";
        stringArray[95] = "setMnemonicAt";
        stringArray[96] = "getAt";
        stringArray[97] = "setDisplayedMnemonicIndexAt";
        stringArray[98] = "getAt";
        stringArray[99] = "add";
        stringArray[100] = "removeAttributeDelegate";
        stringArray[101] = "tabbedPaneFactoryClosure";
        stringArray[102] = "context";
        stringArray[103] = "selectedComponent";
        stringArray[104] = "context";
        stringArray[105] = "selectedComponent";
        stringArray[106] = "context";
        stringArray[107] = "selectedIndex";
        stringArray[108] = "context";
        stringArray[109] = "selectedIndex";
        stringArray[110] = "context";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[111];
        TabbedPaneFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(TabbedPaneFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = TabbedPaneFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

