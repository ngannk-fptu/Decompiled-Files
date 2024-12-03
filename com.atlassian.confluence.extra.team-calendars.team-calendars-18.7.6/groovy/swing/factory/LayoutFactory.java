/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.swing.factory.BeanFactory;
import groovy.util.FactoryBuilderSupport;
import java.awt.Container;
import java.lang.ref.SoftReference;
import java.util.Map;
import javax.swing.RootPaneContainer;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class LayoutFactory
extends BeanFactory {
    private Object contextProps;
    public static final String DELEGATE_PROPERTY_CONSTRAINT = "_delegateProperty:Constrinat";
    public static final String DEFAULT_DELEGATE_PROPERTY_CONSTRAINT = "constraints";
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public LayoutFactory(Class klass) {
        MetaClass metaClass;
        CallSite[] callSiteArray = LayoutFactory.$getCallSiteArray();
        super(klass);
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public LayoutFactory(Class klass, boolean leaf) {
        MetaClass metaClass;
        CallSite[] callSiteArray = LayoutFactory.$getCallSiteArray();
        super(klass, leaf);
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        CallSite[] callSiteArray = LayoutFactory.$getCallSiteArray();
        Object object = callSiteArray[0].call((Object)attributes, "constraintsProperty");
        Object object2 = DefaultTypeTransformation.booleanUnbox(object) ? object : DEFAULT_DELEGATE_PROPERTY_CONSTRAINT;
        callSiteArray[1].call(callSiteArray[2].callGroovyObjectGetProperty(builder), DELEGATE_PROPERTY_CONSTRAINT, object2);
        Object o = ScriptBytecodeAdapter.invokeMethodOnSuperN(BeanFactory.class, this, "newInstance", new Object[]{builder, name, value, attributes});
        callSiteArray[3].callCurrent((GroovyObject)this, callSiteArray[4].call(builder));
        return o;
    }

    public void addLayoutProperties(Object context, Class layoutClass) {
        Reference<Class> layoutClass2 = new Reference<Class>(layoutClass);
        CallSite[] callSiteArray = LayoutFactory.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareEqual(this.contextProps, null)) {
            Map map = ScriptBytecodeAdapter.createMap(new Object[0]);
            this.contextProps = map;
            public class _addLayoutProperties_closure1
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference layoutClass;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _addLayoutProperties_closure1(Object _outerInstance, Object _thisObject, Reference layoutClass) {
                    Reference reference;
                    CallSite[] callSiteArray = _addLayoutProperties_closure1.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.layoutClass = reference = layoutClass;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _addLayoutProperties_closure1.$getCallSiteArray();
                    Object name = callSiteArray[0].callGetProperty(it);
                    if (ScriptBytecodeAdapter.compareEqual(callSiteArray[1].call(name), name)) {
                        return callSiteArray[2].call(callSiteArray[3].callGroovyObjectGetProperty(this), name, ScriptBytecodeAdapter.getProperty(_addLayoutProperties_closure1.class, this.layoutClass.get(), ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""}))));
                    }
                    return null;
                }

                public Class getLayoutClass() {
                    CallSite[] callSiteArray = _addLayoutProperties_closure1.$getCallSiteArray();
                    return ShortTypeHandling.castToClass(this.layoutClass.get());
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _addLayoutProperties_closure1.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _addLayoutProperties_closure1.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "name";
                    stringArray[1] = "toUpperCase";
                    stringArray[2] = "put";
                    stringArray[3] = "contextProps";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[4];
                    _addLayoutProperties_closure1.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_addLayoutProperties_closure1.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _addLayoutProperties_closure1.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[5].call(callSiteArray[6].callGetProperty(layoutClass2.get()), new _addLayoutProperties_closure1(this, this, layoutClass2));
        }
        callSiteArray[7].call(context, this.contextProps);
    }

    public void addLayoutProperties(Object context) {
        CallSite[] callSiteArray = LayoutFactory.$getCallSiteArray();
        callSiteArray[8].callCurrent(this, context, callSiteArray[9].callGroovyObjectGetProperty(this));
    }

    @Override
    public void setParent(FactoryBuilderSupport builder, Object parent, Object child) {
        CallSite[] callSiteArray = LayoutFactory.$getCallSiteArray();
        if (parent instanceof Container) {
            Object object = child;
            ScriptBytecodeAdapter.setProperty(object, null, callSiteArray[10].callStatic(LayoutFactory.class, parent), "layout");
        }
    }

    public static Container getLayoutTarget(Container parent) {
        CallSite[] callSiteArray = LayoutFactory.$getCallSiteArray();
        if (parent instanceof RootPaneContainer) {
            RootPaneContainer rpc = (RootPaneContainer)ScriptBytecodeAdapter.castToType(parent, RootPaneContainer.class);
            Object object = callSiteArray[11].call(rpc);
            parent = (Container)ScriptBytecodeAdapter.castToType(object, Container.class);
        }
        return parent;
    }

    public static Object constraintsAttributeDelegate(Object builder, Object node, Object attributes) {
        CallSite[] callSiteArray = LayoutFactory.$getCallSiteArray();
        Object object = callSiteArray[12].callSafe(callSiteArray[13].callGetPropertySafe(builder), DELEGATE_PROPERTY_CONSTRAINT);
        Object constraintsAttr = DefaultTypeTransformation.booleanUnbox(object) ? object : DEFAULT_DELEGATE_PROPERTY_CONSTRAINT;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[14].call(attributes, constraintsAttr))) {
            Object object2 = callSiteArray[15].call(attributes, constraintsAttr);
            ScriptBytecodeAdapter.setProperty(object2, null, callSiteArray[16].callGetProperty(builder), DEFAULT_DELEGATE_PROPERTY_CONSTRAINT);
            return object2;
        }
        return null;
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != LayoutFactory.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public Object getContextProps() {
        return this.contextProps;
    }

    public void setContextProps(Object object) {
        this.contextProps = object;
    }

    public /* synthetic */ Object super$3$newInstance(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2, Map map) {
        return super.newInstance(factoryBuilderSupport, object, object2, map);
    }

    public /* synthetic */ void super$2$setParent(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2) {
        super.setParent(factoryBuilderSupport, object, object2);
    }

    public /* synthetic */ MetaClass super$3$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "remove";
        stringArray[1] = "putAt";
        stringArray[2] = "context";
        stringArray[3] = "addLayoutProperties";
        stringArray[4] = "getContext";
        stringArray[5] = "each";
        stringArray[6] = "fields";
        stringArray[7] = "putAll";
        stringArray[8] = "addLayoutProperties";
        stringArray[9] = "beanClass";
        stringArray[10] = "getLayoutTarget";
        stringArray[11] = "getContentPane";
        stringArray[12] = "getAt";
        stringArray[13] = "context";
        stringArray[14] = "containsKey";
        stringArray[15] = "remove";
        stringArray[16] = "context";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[17];
        LayoutFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(LayoutFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = LayoutFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

