/*
 * Decompiled with CFR 0.152.
 */
package groovy.inspect.swingui;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.Map;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class BytecodeCollector
extends GroovyClassLoader.ClassCollector
implements GroovyObject {
    private Map<String, byte[]> bytecode;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public BytecodeCollector(GroovyClassLoader.ClassCollector delegate, Map<String, byte[]> bytecode) {
        MetaClass metaClass;
        CallSite[] callSiteArray = BytecodeCollector.$getCallSiteArray();
        super((GroovyClassLoader.InnerLoader)ScriptBytecodeAdapter.castToType(callSiteArray[0].callGetProperty(delegate), GroovyClassLoader.InnerLoader.class), (CompilationUnit)ScriptBytecodeAdapter.castToType(callSiteArray[1].callGetProperty(delegate), CompilationUnit.class), (SourceUnit)ScriptBytecodeAdapter.castToType(callSiteArray[2].callGetProperty(delegate), SourceUnit.class));
        this.metaClass = metaClass = this.$getStaticMetaClass();
        Map<String, byte[]> map = bytecode;
        this.bytecode = (Map)ScriptBytecodeAdapter.castToType(map, Map.class);
    }

    @Override
    protected Class createClass(byte[] code, ClassNode classNode) {
        CallSite[] callSiteArray = BytecodeCollector.$getCallSiteArray();
        byte[] byArray = code;
        callSiteArray[3].call(this.bytecode, callSiteArray[4].callGetProperty(classNode), byArray);
        return ShortTypeHandling.castToClass(ScriptBytecodeAdapter.invokeMethodOnSuperN(GroovyClassLoader.ClassCollector.class, this, "createClass", new Object[]{code, classNode}));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != BytecodeCollector.class) {
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

    public Map<String, byte[]> getBytecode() {
        return this.bytecode;
    }

    public void setBytecode(Map<String, byte[]> map) {
        this.bytecode = map;
    }

    public /* synthetic */ Class super$3$createClass(byte[] byArray, ClassNode classNode) {
        return super.createClass(byArray, classNode);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "cl";
        stringArray[1] = "unit";
        stringArray[2] = "su";
        stringArray[3] = "putAt";
        stringArray[4] = "name";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[5];
        BytecodeCollector.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(BytecodeCollector.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = BytecodeCollector.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

