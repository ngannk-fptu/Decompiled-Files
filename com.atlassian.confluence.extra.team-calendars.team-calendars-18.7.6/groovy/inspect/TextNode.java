/*
 * Decompiled with CFR 0.152.
 */
package groovy.inspect;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class TextNode
implements GroovyObject {
    private Object userObject;
    private List<List<String>> properties;
    private TextNode parent;
    private List children;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public TextNode(Object userObject) {
        Object object;
        MetaClass metaClass;
        CallSite[] callSiteArray = TextNode.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        this.userObject = object = userObject;
        Object object2 = callSiteArray[0].callConstructor(ArrayList.class);
        this.children = (List)ScriptBytecodeAdapter.castToType(object2, List.class);
    }

    public TextNode(Object userObject, List<List<String>> properties) {
        CallSite[] callSiteArray = TextNode.$getCallSiteArray();
        this(userObject);
        List<List<String>> list = properties;
        this.properties = (List)ScriptBytecodeAdapter.castToType(list, List.class);
    }

    public void add(TextNode child) {
        CallSite[] callSiteArray = TextNode.$getCallSiteArray();
        callSiteArray[1].call((Object)this.children, child);
    }

    public void setParent(TextNode newParent) {
        TextNode textNode;
        CallSite[] callSiteArray = TextNode.$getCallSiteArray();
        this.parent = textNode = newParent;
    }

    public String toString() {
        CallSite[] callSiteArray = TextNode.$getCallSiteArray();
        return ShortTypeHandling.castToString(DefaultTypeTransformation.booleanUnbox(this.userObject) ? callSiteArray[2].call(this.userObject) : "null");
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != TextNode.class) {
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

    public Object getUserObject() {
        return this.userObject;
    }

    public void setUserObject(Object object) {
        this.userObject = object;
    }

    public List<List<String>> getProperties() {
        return this.properties;
    }

    public void setProperties(List<List<String>> list) {
        this.properties = list;
    }

    public TextNode getParent() {
        return this.parent;
    }

    public List getChildren() {
        return this.children;
    }

    public void setChildren(List list) {
        this.children = list;
    }

    public /* synthetic */ String super$1$toString() {
        return super.toString();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "leftShift";
        stringArray[2] = "toString";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[3];
        TextNode.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(TextNode.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = TextNode.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

