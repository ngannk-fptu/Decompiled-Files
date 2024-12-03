/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.factory;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.awt.Component;
import java.awt.Window;
import java.lang.ref.SoftReference;
import java.util.Map;
import javax.swing.JSplitPane;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class SplitPaneFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public SplitPaneFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = SplitPaneFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = SplitPaneFactory.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, JSplitPane.class))) {
            return value;
        }
        JSplitPane answer = (JSplitPane)ScriptBytecodeAdapter.castToType(callSiteArray[1].callConstructor(JSplitPane.class), JSplitPane.class);
        callSiteArray[2].call((Object)answer, (Object)null);
        callSiteArray[3].call((Object)answer, (Object)null);
        callSiteArray[4].call((Object)answer, (Object)null);
        callSiteArray[5].call((Object)answer, (Object)null);
        return answer;
    }

    @Override
    public void setChild(FactoryBuilderSupport factory, Object parent, Object child) {
        CallSite[] callSiteArray = SplitPaneFactory.$getCallSiteArray();
        if (!(child instanceof Component) || child instanceof Window) {
            return;
        }
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[6].call(parent), callSiteArray[7].callGetProperty(JSplitPane.class))) {
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[8].call(parent), null)) {
                callSiteArray[9].call(parent, child);
            } else {
                callSiteArray[10].call(parent, child);
            }
        } else if (ScriptBytecodeAdapter.compareEqual(callSiteArray[11].call(parent), null)) {
            callSiteArray[12].call(parent, child);
        } else {
            callSiteArray[13].call(parent, child);
        }
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != SplitPaneFactory.class) {
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

    public /* synthetic */ void super$2$setChild(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2) {
        super.setChild(factoryBuilderSupport, object, object2);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "checkValueIsType";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "setLeftComponent";
        stringArray[3] = "setRightComponent";
        stringArray[4] = "setTopComponent";
        stringArray[5] = "setBottomComponent";
        stringArray[6] = "getOrientation";
        stringArray[7] = "HORIZONTAL_SPLIT";
        stringArray[8] = "getTopComponent";
        stringArray[9] = "setTopComponent";
        stringArray[10] = "setBottomComponent";
        stringArray[11] = "getLeftComponent";
        stringArray[12] = "setLeftComponent";
        stringArray[13] = "setRightComponent";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[14];
        SplitPaneFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(SplitPaneFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = SplitPaneFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

