/*
 * Decompiled with CFR 0.152.
 */
package groovy.inspect.swingui;

import groovy.inspect.swingui.BytecodeCollector;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class GeneratedBytecodeAwareGroovyClassLoader
extends GroovyClassLoader
implements GroovyObject {
    private final Map<String, byte[]> bytecode;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public GeneratedBytecodeAwareGroovyClassLoader(GroovyClassLoader parent) {
        MetaClass metaClass;
        CallSite[] callSiteArray = GeneratedBytecodeAwareGroovyClassLoader.$getCallSiteArray();
        Object[] objectArray = new Object[]{parent};
        GeneratedBytecodeAwareGroovyClassLoader generatedBytecodeAwareGroovyClassLoader = this;
        switch (ScriptBytecodeAdapter.selectConstructorAndTransformArguments(objectArray, -1, GroovyClassLoader.class)) {
            case -2044833963: {
                Object[] objectArray2 = objectArray;
                super((ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[0], ClassLoader.class));
                break;
            }
            case -1374445560: {
                Object[] objectArray2 = objectArray;
                super((ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[0], ClassLoader.class), (CompilerConfiguration)ScriptBytecodeAdapter.castToType(objectArray[1], CompilerConfiguration.class));
                break;
            }
            case -991719697: {
                Object[] objectArray2 = objectArray;
                super((GroovyClassLoader)ScriptBytecodeAdapter.castToType(objectArray[0], GroovyClassLoader.class));
                break;
            }
            case 39797: {
                Object[] objectArray2 = objectArray;
                super();
                break;
            }
            case 341906380: {
                Object[] objectArray2 = objectArray;
                super((ClassLoader)ScriptBytecodeAdapter.castToType(objectArray[0], ClassLoader.class), (CompilerConfiguration)ScriptBytecodeAdapter.castToType(objectArray[1], CompilerConfiguration.class), DefaultTypeTransformation.booleanUnbox(objectArray[2]));
                break;
            }
            default: {
                throw new IllegalArgumentException("This class has been compiled with a super class which is binary incompatible with the current super class found on classpath. You should recompile this class with the new version.");
            }
        }
        Object object = callSiteArray[0].callConstructor(HashMap.class);
        this.bytecode = (Map)ScriptBytecodeAdapter.castToType(object, Map.class);
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    protected GroovyClassLoader.ClassCollector createCollector(CompilationUnit unit, SourceUnit su) {
        CallSite[] callSiteArray = GeneratedBytecodeAwareGroovyClassLoader.$getCallSiteArray();
        Object collector = ScriptBytecodeAdapter.invokeMethodOnSuperN(GroovyClassLoader.class, this, "createCollector", new Object[]{unit, su});
        return (GroovyClassLoader.ClassCollector)ScriptBytecodeAdapter.castToType(callSiteArray[1].callConstructor(BytecodeCollector.class, collector, this.bytecode), GroovyClassLoader.ClassCollector.class);
    }

    public void clearBytecodeTable() {
        CallSite[] callSiteArray = GeneratedBytecodeAwareGroovyClassLoader.$getCallSiteArray();
        callSiteArray[2].call(this.bytecode);
    }

    public byte[] getBytecode(String className) {
        CallSite[] callSiteArray = GeneratedBytecodeAwareGroovyClassLoader.$getCallSiteArray();
        return (byte[])ScriptBytecodeAdapter.castToType(callSiteArray[3].call(this.bytecode, className), byte[].class);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != GeneratedBytecodeAwareGroovyClassLoader.class) {
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

    public /* synthetic */ GroovyClassLoader.ClassCollector super$5$createCollector(CompilationUnit compilationUnit, SourceUnit sourceUnit) {
        return super.createCollector(compilationUnit, sourceUnit);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "clear";
        stringArray[3] = "getAt";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[4];
        GeneratedBytecodeAwareGroovyClassLoader.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(GeneratedBytecodeAwareGroovyClassLoader.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = GeneratedBytecodeAwareGroovyClassLoader.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

