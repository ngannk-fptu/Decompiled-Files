/*
 * Decompiled with CFR 0.152.
 */
package groovy.inspect.swingui;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import java.lang.ref.SoftReference;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class TreeNodeWithProperties
extends DefaultMutableTreeNode
implements GroovyObject {
    private List<List<String>> properties;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public TreeNodeWithProperties(Object userObject, List<List<String>> properties) {
        MetaClass metaClass;
        CallSite[] callSiteArray = TreeNodeWithProperties.$getCallSiteArray();
        super(userObject);
        this.metaClass = metaClass = this.$getStaticMetaClass();
        List<List<String>> list = properties;
        this.properties = (List)ScriptBytecodeAdapter.castToType(list, List.class);
    }

    public String getPropertyValue(String name) {
        Reference<String> name2 = new Reference<String>(name);
        CallSite[] callSiteArray = TreeNodeWithProperties.$getCallSiteArray();
        public class _getPropertyValue_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference name;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _getPropertyValue_closure1(Object _outerInstance, Object _thisObject, Reference name) {
                Reference reference;
                CallSite[] callSiteArray = _getPropertyValue_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.name = reference = name;
            }

            public Object doCall(Object n, Object v, Object t) {
                CallSite[] callSiteArray = _getPropertyValue_closure1.$getCallSiteArray();
                return ScriptBytecodeAdapter.compareEqual(this.name.get(), n);
            }

            public Object call(Object n, Object v, Object t) {
                CallSite[] callSiteArray = _getPropertyValue_closure1.$getCallSiteArray();
                return callSiteArray[0].callCurrent(this, n, v, t);
            }

            public String getName() {
                CallSite[] callSiteArray = _getPropertyValue_closure1.$getCallSiteArray();
                return ShortTypeHandling.castToString(this.name.get());
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _getPropertyValue_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[1];
                stringArray[0] = "doCall";
                return new CallSiteArray(_getPropertyValue_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _getPropertyValue_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        Object match = callSiteArray[0].call(this.properties, new _getPropertyValue_closure1(this, this, name2));
        return ShortTypeHandling.castToString(ScriptBytecodeAdapter.compareNotEqual(match, null) ? callSiteArray[1].call(match, 1) : null);
    }

    public boolean isClassNode() {
        CallSite[] callSiteArray = TreeNodeWithProperties.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return ScriptBytecodeAdapter.isCase(callSiteArray[2].callCurrent((GroovyObject)this, "class"), ScriptBytecodeAdapter.createList(new Object[]{"class org.codehaus.groovy.ast.ClassNode", "class org.codehaus.groovy.ast.InnerClassNode"}));
        }
        return ScriptBytecodeAdapter.isCase(this.getPropertyValue("class"), ScriptBytecodeAdapter.createList(new Object[]{"class org.codehaus.groovy.ast.ClassNode", "class org.codehaus.groovy.ast.InnerClassNode"}));
    }

    public boolean isMethodNode() {
        CallSite[] callSiteArray = TreeNodeWithProperties.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return ScriptBytecodeAdapter.isCase(callSiteArray[3].callCurrent((GroovyObject)this, "class"), ScriptBytecodeAdapter.createList(new Object[]{"class org.codehaus.groovy.ast.MethodNode", "class org.codehaus.groovy.ast.ConstructorNode"}));
        }
        return ScriptBytecodeAdapter.isCase(this.getPropertyValue("class"), ScriptBytecodeAdapter.createList(new Object[]{"class org.codehaus.groovy.ast.MethodNode", "class org.codehaus.groovy.ast.ConstructorNode"}));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != TreeNodeWithProperties.class) {
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

    public List<List<String>> getProperties() {
        return this.properties;
    }

    public void setProperties(List<List<String>> list) {
        this.properties = list;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "find";
        stringArray[1] = "getAt";
        stringArray[2] = "getPropertyValue";
        stringArray[3] = "getPropertyValue";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[4];
        TreeNodeWithProperties.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(TreeNodeWithProperties.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = TreeNodeWithProperties.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

