/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MissingPropertyException;
import groovy.swing.factory.LayoutFactory;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.awt.Component;
import java.awt.Window;
import java.lang.ref.SoftReference;
import java.util.Map;
import javax.swing.JComponent;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class WidgetFactory
extends AbstractFactory
implements GroovyObject {
    private final Class restrictedType;
    protected final boolean leaf;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public WidgetFactory(Class restrictedType, boolean leaf) {
        MetaClass metaClass;
        CallSite[] callSiteArray = WidgetFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        Class clazz = restrictedType;
        this.restrictedType = ShortTypeHandling.castToClass(clazz);
        boolean bl = leaf;
        this.leaf = DefaultTypeTransformation.booleanUnbox(bl);
    }

    @Override
    public boolean isLeaf() {
        CallSite[] callSiteArray = WidgetFactory.$getCallSiteArray();
        return this.leaf;
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = WidgetFactory.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareEqual(value, null)) {
            Object object;
            value = object = callSiteArray[0].call((Object)attributes, name);
        }
        if (ScriptBytecodeAdapter.compareNotEqual(value, null) && DefaultTypeTransformation.booleanUnbox(callSiteArray[1].call(FactoryBuilderSupport.class, value, name, this.restrictedType))) {
            return value;
        }
        throw (Throwable)callSiteArray[2].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{name, name, callSiteArray[3].callGetProperty(this.restrictedType)}, new String[]{"", " must have either a value argument or an attribute named ", " that must be of type ", ""}));
    }

    @Override
    public void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        block7: {
            CallSite[] callSiteArray = WidgetFactory.$getCallSiteArray();
            if (!(child instanceof Component) || child instanceof Window) {
                return;
            }
            try {
                Object constraints = callSiteArray[4].callGetProperty(callSiteArray[5].callGroovyObjectGetProperty(builder));
                if (ScriptBytecodeAdapter.compareNotEqual(constraints, null)) {
                    callSiteArray[6].call(callSiteArray[7].call(LayoutFactory.class, parent), child, constraints);
                    if (child instanceof JComponent) {
                        callSiteArray[8].call(child, callSiteArray[9].callGetProperty(LayoutFactory.class), constraints);
                    }
                    callSiteArray[10].call(callSiteArray[11].callGroovyObjectGetProperty(builder), "constraints");
                    break block7;
                }
                callSiteArray[12].call(callSiteArray[13].call(LayoutFactory.class, parent), child);
            }
            catch (MissingPropertyException mpe) {
                callSiteArray[14].call(callSiteArray[15].call(LayoutFactory.class, parent), child);
            }
        }
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != WidgetFactory.class) {
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

    public final Class getRestrictedType() {
        return this.restrictedType;
    }

    public /* synthetic */ boolean super$2$isLeaf() {
        return super.isLeaf();
    }

    public /* synthetic */ void super$2$setChild(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2) {
        super.setChild(factoryBuilderSupport, object, object2);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "remove";
        stringArray[1] = "checkValueIsType";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "name";
        stringArray[4] = "constraints";
        stringArray[5] = "context";
        stringArray[6] = "add";
        stringArray[7] = "getLayoutTarget";
        stringArray[8] = "putClientProperty";
        stringArray[9] = "DEFAULT_DELEGATE_PROPERTY_CONSTRAINT";
        stringArray[10] = "remove";
        stringArray[11] = "context";
        stringArray[12] = "add";
        stringArray[13] = "getLayoutTarget";
        stringArray[14] = "add";
        stringArray[15] = "getLayoutTarget";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[16];
        WidgetFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(WidgetFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = WidgetFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

