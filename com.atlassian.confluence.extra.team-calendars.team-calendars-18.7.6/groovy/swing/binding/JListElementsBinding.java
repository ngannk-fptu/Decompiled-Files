/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.binding;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.swing.binding.AbstractSyntheticBinding;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.SoftReference;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.codehaus.groovy.binding.PropertyBinding;
import org.codehaus.groovy.binding.TargetBinding;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class JListElementsBinding
extends AbstractSyntheticBinding
implements ListDataListener,
PropertyChangeListener,
GroovyObject {
    private JList boundList;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public JListElementsBinding(PropertyBinding propertyBinding, TargetBinding target) {
        MetaClass metaClass;
        CallSite[] callSiteArray = JListElementsBinding.$getCallSiteArray();
        super(propertyBinding, target, JList.class, "elements");
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    protected void syntheticBind() {
        JList jList;
        CallSite[] callSiteArray = JListElementsBinding.$getCallSiteArray();
        this.boundList = jList = (JList)ScriptBytecodeAdapter.castToType(callSiteArray[0].call((PropertyBinding)ScriptBytecodeAdapter.castToType(callSiteArray[1].callGroovyObjectGetProperty(this), PropertyBinding.class)), JList.class);
        callSiteArray[2].call(this.boundList, "model", this);
        callSiteArray[3].call(callSiteArray[4].call(this.boundList), this);
    }

    @Override
    protected void syntheticUnbind() {
        CallSite[] callSiteArray = JListElementsBinding.$getCallSiteArray();
        callSiteArray[5].call(this.boundList, "model", this);
        callSiteArray[6].call(callSiteArray[7].call(this.boundList), this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        CallSite[] callSiteArray = JListElementsBinding.$getCallSiteArray();
        callSiteArray[8].callCurrent(this);
        callSiteArray[9].call((Object)((ListModel)ScriptBytecodeAdapter.castToType(callSiteArray[10].call(event), ListModel.class)), this);
        callSiteArray[11].call((Object)((ListModel)ScriptBytecodeAdapter.castToType(callSiteArray[12].call(event), ListModel.class)), this);
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
        CallSite[] callSiteArray = JListElementsBinding.$getCallSiteArray();
        callSiteArray[13].callCurrent(this);
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        CallSite[] callSiteArray = JListElementsBinding.$getCallSiteArray();
        callSiteArray[14].callCurrent(this);
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        CallSite[] callSiteArray = JListElementsBinding.$getCallSiteArray();
        callSiteArray[15].callCurrent(this);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != JListElementsBinding.class) {
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

    public JList getBoundList() {
        return this.boundList;
    }

    public void setBoundList(JList jList) {
        this.boundList = jList;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "getBean";
        stringArray[1] = "sourceBinding";
        stringArray[2] = "addPropertyChangeListener";
        stringArray[3] = "addListDataListener";
        stringArray[4] = "getModel";
        stringArray[5] = "removePropertyChangeListener";
        stringArray[6] = "removeListDataListener";
        stringArray[7] = "getModel";
        stringArray[8] = "update";
        stringArray[9] = "removeListDataListener";
        stringArray[10] = "getOldValue";
        stringArray[11] = "addListDataListener";
        stringArray[12] = "getNewValue";
        stringArray[13] = "update";
        stringArray[14] = "update";
        stringArray[15] = "update";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[16];
        JListElementsBinding.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(JListElementsBinding.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = JListElementsBinding.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

