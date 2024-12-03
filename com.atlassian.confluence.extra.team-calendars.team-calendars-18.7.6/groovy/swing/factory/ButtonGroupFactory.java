/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.MetaClass;
import groovy.swing.factory.BeanFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class ButtonGroupFactory
extends BeanFactory {
    public static final String DELEGATE_PROPERTY_BUTTON_GROUP = "_delegateProperty:buttonGroup";
    public static final String DEFAULT_DELEGATE_PROPERTY_BUTTON_GROUP = "buttonGroup";
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ButtonGroupFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = ButtonGroupFactory.$getCallSiteArray();
        super(ButtonGroup.class, true);
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) {
        CallSite[] callSiteArray = ButtonGroupFactory.$getCallSiteArray();
        Object object = callSiteArray[0].call((Object)attributes, "buttonGroupProperty");
        Object object2 = DefaultTypeTransformation.booleanUnbox(object) ? object : DEFAULT_DELEGATE_PROPERTY_BUTTON_GROUP;
        callSiteArray[1].call(callSiteArray[2].callGroovyObjectGetProperty(builder), DELEGATE_PROPERTY_BUTTON_GROUP, object2);
        return ScriptBytecodeAdapter.invokeMethodOnSuperN(BeanFactory.class, this, "newInstance", new Object[]{builder, name, value, attributes});
    }

    public static Object buttonGroupAttributeDelegate(Object builder, Object node, Object attributes) {
        CallSite[] callSiteArray = ButtonGroupFactory.$getCallSiteArray();
        Object object = callSiteArray[3].callSafe(callSiteArray[4].callGetPropertySafe(builder), DELEGATE_PROPERTY_BUTTON_GROUP);
        Object buttonGroupAttr = DefaultTypeTransformation.booleanUnbox(object) ? object : DEFAULT_DELEGATE_PROPERTY_BUTTON_GROUP;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[5].call(attributes, buttonGroupAttr))) {
            Object o = callSiteArray[6].call(attributes, buttonGroupAttr);
            if (o instanceof ButtonGroup && node instanceof AbstractButton) {
                Object object2 = o;
                ScriptBytecodeAdapter.setProperty(object2, null, callSiteArray[7].callGetProperty(node), "group");
                return callSiteArray[8].call(attributes, buttonGroupAttr);
            }
            return null;
        }
        return null;
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ButtonGroupFactory.class) {
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

    public /* synthetic */ MetaClass super$3$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "remove";
        stringArray[1] = "putAt";
        stringArray[2] = "context";
        stringArray[3] = "getAt";
        stringArray[4] = "context";
        stringArray[5] = "containsKey";
        stringArray[6] = "get";
        stringArray[7] = "model";
        stringArray[8] = "remove";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[9];
        ButtonGroupFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ButtonGroupFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ButtonGroupFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

