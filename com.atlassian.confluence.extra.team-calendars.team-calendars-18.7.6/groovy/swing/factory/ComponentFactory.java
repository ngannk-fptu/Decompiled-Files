/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.MetaClass;
import groovy.lang.MissingPropertyException;
import groovy.swing.factory.BeanFactory;
import groovy.swing.factory.LayoutFactory;
import groovy.util.FactoryBuilderSupport;
import java.awt.Component;
import java.awt.Window;
import java.lang.ref.SoftReference;
import javax.swing.JComponent;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class ComponentFactory
extends BeanFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ComponentFactory(Class beanClass) {
        MetaClass metaClass;
        CallSite[] callSiteArray = ComponentFactory.$getCallSiteArray();
        super(beanClass);
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public ComponentFactory(Class beanClass, boolean leaf) {
        MetaClass metaClass;
        CallSite[] callSiteArray = ComponentFactory.$getCallSiteArray();
        super(beanClass, leaf);
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public void setChild(FactoryBuilderSupport builder, Object parent, Object child) {
        block7: {
            CallSite[] callSiteArray = ComponentFactory.$getCallSiteArray();
            if (!(child instanceof Component) || child instanceof Window) {
                return;
            }
            try {
                Object constraints = callSiteArray[0].callGetProperty(callSiteArray[1].callGroovyObjectGetProperty(builder));
                if (ScriptBytecodeAdapter.compareNotEqual(constraints, null)) {
                    callSiteArray[2].call(callSiteArray[3].call(LayoutFactory.class, parent), child, constraints);
                    if (child instanceof JComponent) {
                        callSiteArray[4].call(child, callSiteArray[5].callGetProperty(LayoutFactory.class), constraints);
                    }
                    callSiteArray[6].call(callSiteArray[7].callGroovyObjectGetProperty(builder), "constraints");
                    break block7;
                }
                callSiteArray[8].call(callSiteArray[9].call(LayoutFactory.class, parent), child);
            }
            catch (MissingPropertyException mpe) {
                callSiteArray[10].call(callSiteArray[11].call(LayoutFactory.class, parent), child);
            }
        }
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ComponentFactory.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ void super$2$setChild(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2) {
        super.setChild(factoryBuilderSupport, object, object2);
    }

    public /* synthetic */ MetaClass super$3$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "constraints";
        stringArray[1] = "context";
        stringArray[2] = "add";
        stringArray[3] = "getLayoutTarget";
        stringArray[4] = "putClientProperty";
        stringArray[5] = "DEFAULT_DELEGATE_PROPERTY_CONSTRAINT";
        stringArray[6] = "remove";
        stringArray[7] = "context";
        stringArray[8] = "add";
        stringArray[9] = "getLayoutTarget";
        stringArray[10] = "add";
        stringArray[11] = "getLayoutTarget";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[12];
        ComponentFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ComponentFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ComponentFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

