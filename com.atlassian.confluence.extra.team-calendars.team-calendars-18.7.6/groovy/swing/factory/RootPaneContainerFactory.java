/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MissingPropertyException;
import groovy.lang.Reference;
import groovy.swing.factory.LayoutFactory;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.awt.Component;
import java.awt.Window;
import java.lang.ref.SoftReference;
import java.util.ListIterator;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComponent;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public abstract class RootPaneContainerFactory
extends AbstractFactory
implements GroovyObject {
    public static final String DELEGATE_PROPERTY_DEFAULT_BUTTON = "_delegateProperty:defaultButton";
    public static final String DEFAULT_DELEGATE_PROPERTY_DEFAULT_BUTTON = "defaultButton";
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public RootPaneContainerFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = RootPaneContainerFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        block7: {
            CallSite[] callSiteArray = RootPaneContainerFactory.$getCallSiteArray();
            if (!(child instanceof Component) || child instanceof Window) {
                return;
            }
            try {
                Object constraints = callSiteArray[0].callGetProperty(callSiteArray[1].callGroovyObjectGetProperty(builder));
                if (ScriptBytecodeAdapter.compareNotEqual(constraints, null)) {
                    callSiteArray[2].call(callSiteArray[3].callGetProperty(parent), child, constraints);
                    if (child instanceof JComponent) {
                        callSiteArray[4].call(child, callSiteArray[5].callGetProperty(LayoutFactory.class), constraints);
                    }
                    callSiteArray[6].call(callSiteArray[7].callGroovyObjectGetProperty(builder), "constraints");
                    break block7;
                }
                callSiteArray[8].call(callSiteArray[9].callGetProperty(parent), child);
            }
            catch (MissingPropertyException mpe) {
                callSiteArray[10].call(callSiteArray[11].callGetProperty(parent), child);
            }
        }
    }

    /*
     * WARNING - void declaration
     */
    public void handleRootPaneTasks(FactoryBuilderSupport builder, Window container, Map attributes) {
        void var2_2;
        Reference<FactoryBuilderSupport> builder2 = new Reference<FactoryBuilderSupport>(builder);
        Reference<void> container2 = new Reference<void>(var2_2);
        CallSite[] callSiteArray = RootPaneContainerFactory.$getCallSiteArray();
        Object object = callSiteArray[12].call((Object)attributes, "defaultButtonProperty");
        Object object2 = DefaultTypeTransformation.booleanUnbox(object) ? object : DEFAULT_DELEGATE_PROPERTY_DEFAULT_BUTTON;
        callSiteArray[13].call(callSiteArray[14].callGroovyObjectGetProperty(builder2.get()), DELEGATE_PROPERTY_DEFAULT_BUTTON, object2);
        public class _handleRootPaneTasks_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference builder;
            private /* synthetic */ Reference container;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _handleRootPaneTasks_closure1(Object _outerInstance, Object _thisObject, Reference builder, Reference container) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _handleRootPaneTasks_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.builder = reference2 = builder;
                this.container = reference = container;
            }

            public Object doCall(Object myBuilder, Object node, Object myAttributes) {
                CallSite[] callSiteArray = _handleRootPaneTasks_closure1.$getCallSiteArray();
                if (node instanceof JButton && ScriptBytecodeAdapter.compareEqual(callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this.builder.get()), -1), this.container.get())) {
                    ListIterator li = (ListIterator)ScriptBytecodeAdapter.castToType(callSiteArray[2].call(callSiteArray[3].callGroovyObjectGetProperty(this.builder.get())), ListIterator.class);
                    Map context = null;
                    while (DefaultTypeTransformation.booleanUnbox(callSiteArray[4].call(li))) {
                        Object object = callSiteArray[5].call(li);
                        context = (Map)ScriptBytecodeAdapter.castToType(object, Map.class);
                    }
                    while (DefaultTypeTransformation.booleanUnbox(context) && ScriptBytecodeAdapter.compareNotEqual(callSiteArray[6].call((Object)context, callSiteArray[7].callGetProperty(FactoryBuilderSupport.class)), this.container.get())) {
                        Object object = callSiteArray[8].call(li);
                        context = (Map)ScriptBytecodeAdapter.castToType(object, Map.class);
                    }
                    Object object = callSiteArray[9].call((Object)context, callSiteArray[10].callGetProperty(RootPaneContainerFactory.class));
                    Object defaultButtonProperty = DefaultTypeTransformation.booleanUnbox(object) ? object : callSiteArray[11].callGetProperty(RootPaneContainerFactory.class);
                    Object defaultButton = callSiteArray[12].call(myAttributes, defaultButtonProperty);
                    if (DefaultTypeTransformation.booleanUnbox(defaultButton)) {
                        Object object2 = node;
                        ScriptBytecodeAdapter.setProperty(object2, null, callSiteArray[13].callGetProperty(this.container.get()), RootPaneContainerFactory.DEFAULT_DELEGATE_PROPERTY_DEFAULT_BUTTON);
                        return object2;
                    }
                    return null;
                }
                return null;
            }

            public Object call(Object myBuilder, Object node, Object myAttributes) {
                CallSite[] callSiteArray = _handleRootPaneTasks_closure1.$getCallSiteArray();
                return callSiteArray[14].callCurrent(this, myBuilder, node, myAttributes);
            }

            public FactoryBuilderSupport getBuilder() {
                CallSite[] callSiteArray = _handleRootPaneTasks_closure1.$getCallSiteArray();
                return (FactoryBuilderSupport)ScriptBytecodeAdapter.castToType(this.builder.get(), FactoryBuilderSupport.class);
            }

            public Window getContainer() {
                CallSite[] callSiteArray = _handleRootPaneTasks_closure1.$getCallSiteArray();
                return (Window)ScriptBytecodeAdapter.castToType(this.container.get(), Window.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _handleRootPaneTasks_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "getAt";
                stringArray[1] = "containingWindows";
                stringArray[2] = "listIterator";
                stringArray[3] = "contexts";
                stringArray[4] = "hasNext";
                stringArray[5] = "next";
                stringArray[6] = "getAt";
                stringArray[7] = "CURRENT_NODE";
                stringArray[8] = "previous";
                stringArray[9] = "getAt";
                stringArray[10] = "DELEGATE_PROPERTY_DEFAULT_BUTTON";
                stringArray[11] = "DEFAULT_DELEGATE_PROPERTY_DEFAULT_BUTTON";
                stringArray[12] = "remove";
                stringArray[13] = "rootPane";
                stringArray[14] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[15];
                _handleRootPaneTasks_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_handleRootPaneTasks_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _handleRootPaneTasks_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        Object object3 = callSiteArray[15].call((Object)builder2.get(), new _handleRootPaneTasks_closure1(this, this, builder2, container2));
        ScriptBytecodeAdapter.setProperty(object3, null, callSiteArray[16].callGroovyObjectGetProperty(builder2.get()), "defaultButtonDelegate");
        callSiteArray[17].call(callSiteArray[18].callGroovyObjectGetProperty(builder2.get()), (Window)container2.get());
        Object object4 = callSiteArray[19].call((Object)attributes, "pack");
        ScriptBytecodeAdapter.setProperty(object4, null, callSiteArray[20].callGroovyObjectGetProperty(builder2.get()), "pack");
        Object object5 = callSiteArray[21].call((Object)attributes, "show");
        ScriptBytecodeAdapter.setProperty(object5, null, callSiteArray[22].callGroovyObjectGetProperty(builder2.get()), "show");
        callSiteArray[23].call((Object)builder2.get(), ScriptBytecodeAdapter.getMethodPointer((Window)container2.get(), "dispose"));
    }

    @Override
    public void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        CallSite[] callSiteArray = RootPaneContainerFactory.$getCallSiteArray();
        if (node instanceof Window) {
            Object containingWindows = callSiteArray[24].callGroovyObjectGetProperty(builder);
            if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[25].callGetProperty(containingWindows)) && ScriptBytecodeAdapter.compareEqual(callSiteArray[26].callGetProperty(containingWindows), node)) {
                    callSiteArray[27].call(containingWindows);
                }
            } else if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[28].callGetProperty(containingWindows)) && ScriptBytecodeAdapter.compareEqual(callSiteArray[29].callGetProperty(containingWindows), node)) {
                callSiteArray[30].call(containingWindows);
            }
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[31].callGetProperty(callSiteArray[32].callGroovyObjectGetProperty(builder)))) {
            callSiteArray[33].call(node);
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[34].callGetProperty(callSiteArray[35].callGroovyObjectGetProperty(builder)))) {
            boolean bl = true;
            ScriptBytecodeAdapter.setProperty(bl, null, node, "visible");
        }
        callSiteArray[36].call((Object)builder, callSiteArray[37].callGetProperty(callSiteArray[38].callGroovyObjectGetProperty(builder)));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != RootPaneContainerFactory.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    @Override
    public MetaClass getMetaClass() {
        MetaClass metaClass = this.metaClass;
        if (metaClass != null) {
            return metaClass;
        }
        this.metaClass = this.$getStaticMetaClass();
        return this.metaClass;
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    public Object invokeMethod(String string, Object object) {
        return this.getMetaClass().invokeMethod((Object)this, string, object);
    }

    @Override
    public Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    public void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }

    public /* synthetic */ void super$2$onNodeCompleted(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2) {
        super.onNodeCompleted(factoryBuilderSupport, object, object2);
    }

    public /* synthetic */ void super$2$setChild(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2) {
        super.setChild(factoryBuilderSupport, object, object2);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "constraints";
        stringArray[1] = "context";
        stringArray[2] = "add";
        stringArray[3] = "contentPane";
        stringArray[4] = "putClientProperty";
        stringArray[5] = "DEFAULT_DELEGATE_PROPERTY_CONSTRAINT";
        stringArray[6] = "remove";
        stringArray[7] = "context";
        stringArray[8] = "add";
        stringArray[9] = "contentPane";
        stringArray[10] = "add";
        stringArray[11] = "contentPane";
        stringArray[12] = "remove";
        stringArray[13] = "putAt";
        stringArray[14] = "context";
        stringArray[15] = "addAttributeDelegate";
        stringArray[16] = "context";
        stringArray[17] = "add";
        stringArray[18] = "containingWindows";
        stringArray[19] = "remove";
        stringArray[20] = "context";
        stringArray[21] = "remove";
        stringArray[22] = "context";
        stringArray[23] = "addDisposalClosure";
        stringArray[24] = "containingWindows";
        stringArray[25] = "empty";
        stringArray[26] = "last";
        stringArray[27] = "removeLast";
        stringArray[28] = "empty";
        stringArray[29] = "last";
        stringArray[30] = "removeLast";
        stringArray[31] = "pack";
        stringArray[32] = "context";
        stringArray[33] = "pack";
        stringArray[34] = "show";
        stringArray[35] = "context";
        stringArray[36] = "removeAttributeDelegate";
        stringArray[37] = "defaultButtonDelegate";
        stringArray[38] = "context";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[39];
        RootPaneContainerFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(RootPaneContainerFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = RootPaneContainerFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

