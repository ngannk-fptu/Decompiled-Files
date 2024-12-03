/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.MetaClass;
import groovy.swing.factory.RootPaneContainerFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class FrameFactory
extends RootPaneContainerFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public FrameFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = FrameFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = FrameFactory.$getCallSiteArray();
        JFrame frame = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, JFrame.class))) {
            Object object = value;
            frame = (JFrame)ScriptBytecodeAdapter.castToType(object, JFrame.class);
        } else {
            Object object = callSiteArray[1].callConstructor(JFrame.class);
            frame = (JFrame)ScriptBytecodeAdapter.castToType(object, JFrame.class);
        }
        callSiteArray[2].callCurrent(this, builder, frame, attributes);
        return frame;
    }

    @Override
    public void setChild(FactoryBuilderSupport build, Object parent, Object child) {
        CallSite[] callSiteArray = FrameFactory.$getCallSiteArray();
        if (child instanceof JMenuBar) {
            Object object = child;
            ScriptBytecodeAdapter.setProperty(object, null, parent, "JMenuBar");
        } else {
            ScriptBytecodeAdapter.invokeMethodOnSuperN(RootPaneContainerFactory.class, this, "setChild", new Object[]{build, parent, child});
        }
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != FrameFactory.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ void super$3$setChild(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2) {
        super.setChild(factoryBuilderSupport, object, object2);
    }

    public /* synthetic */ MetaClass super$3$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "checkValueIsType";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "handleRootPaneTasks";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[3];
        FrameFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(FrameFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = FrameFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

