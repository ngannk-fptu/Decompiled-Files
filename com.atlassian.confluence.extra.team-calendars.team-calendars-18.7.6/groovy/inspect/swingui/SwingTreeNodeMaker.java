/*
 * Decompiled with CFR 0.152.
 */
package groovy.inspect.swingui;

import groovy.inspect.swingui.AstBrowserNodeMaker;
import groovy.inspect.swingui.TreeNodeWithProperties;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class SwingTreeNodeMaker
implements AstBrowserNodeMaker<DefaultMutableTreeNode>,
GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public SwingTreeNodeMaker() {
        MetaClass metaClass;
        CallSite[] callSiteArray = SwingTreeNodeMaker.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public DefaultMutableTreeNode makeNode(Object userObject) {
        CallSite[] callSiteArray = SwingTreeNodeMaker.$getCallSiteArray();
        return (DefaultMutableTreeNode)ScriptBytecodeAdapter.castToType(callSiteArray[0].callConstructor(DefaultMutableTreeNode.class, userObject), DefaultMutableTreeNode.class);
    }

    @Override
    public DefaultMutableTreeNode makeNodeWithProperties(Object userObject, List<List<String>> properties) {
        CallSite[] callSiteArray = SwingTreeNodeMaker.$getCallSiteArray();
        return (DefaultMutableTreeNode)ScriptBytecodeAdapter.castToType(callSiteArray[1].callConstructor(TreeNodeWithProperties.class, userObject, properties), DefaultMutableTreeNode.class);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != SwingTreeNodeMaker.class) {
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

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[2];
        SwingTreeNodeMaker.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(SwingTreeNodeMaker.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = SwingTreeNodeMaker.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

