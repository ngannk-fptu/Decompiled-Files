/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.MetaClass;
import groovy.swing.factory.BeanFactory;
import groovy.util.FactoryBuilderSupport;
import java.awt.Component;
import java.awt.Window;
import java.lang.ref.SoftReference;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class ScrollPaneFactory
extends BeanFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ScrollPaneFactory() {
        CallSite[] callSiteArray = ScrollPaneFactory.$getCallSiteArray();
        this(JScrollPane.class);
    }

    public ScrollPaneFactory(Class klass) {
        MetaClass metaClass;
        CallSite[] callSiteArray = ScrollPaneFactory.$getCallSiteArray();
        super(klass, false);
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public void setChild(FactoryBuilderSupport factory, Object parent, Object child) {
        CallSite[] callSiteArray = ScrollPaneFactory.$getCallSiteArray();
        if (!(child instanceof Component) || child instanceof Window) {
            return;
        }
        if (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[0].callSafe(callSiteArray[1].call(parent)), null)) {
            throw (Throwable)callSiteArray[2].callConstructor(RuntimeException.class, "ScrollPane can only have one child component");
        }
        if (child instanceof JViewport) {
            callSiteArray[3].call(parent, child);
        } else {
            callSiteArray[4].call(parent, child);
        }
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ScrollPaneFactory.class) {
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
        stringArray[0] = "getView";
        stringArray[1] = "getViewport";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "setViewport";
        stringArray[4] = "setViewportView";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[5];
        ScrollPaneFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ScrollPaneFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ScrollPaneFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

