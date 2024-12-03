/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.ast;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.security.CodeSource;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.tools.ast.TestHarnessOperation;
import org.codehaus.groovy.transform.ASTTransformation;

class TestHarnessClassLoader
extends GroovyClassLoader
implements GroovyObject {
    private final ASTTransformation transform;
    private final CompilePhase phase;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public TestHarnessClassLoader(ASTTransformation transform, CompilePhase phase) {
        MetaClass metaClass;
        CallSite[] callSiteArray = TestHarnessClassLoader.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        ASTTransformation aSTTransformation = transform;
        this.transform = (ASTTransformation)ScriptBytecodeAdapter.castToType(aSTTransformation, ASTTransformation.class);
        CompilePhase compilePhase = phase;
        this.phase = (CompilePhase)ShortTypeHandling.castToEnum((Object)compilePhase, CompilePhase.class);
    }

    @Override
    protected CompilationUnit createCompilationUnit(CompilerConfiguration config, CodeSource codeSource) {
        CallSite[] callSiteArray = TestHarnessClassLoader.$getCallSiteArray();
        CompilationUnit cu = (CompilationUnit)ScriptBytecodeAdapter.castToType(ScriptBytecodeAdapter.invokeMethodOnSuperN(GroovyClassLoader.class, this, "createCompilationUnit", new Object[]{config, codeSource}), CompilationUnit.class);
        callSiteArray[0].call(cu, callSiteArray[1].callConstructor(TestHarnessOperation.class, this.transform), callSiteArray[2].call((Object)this.phase));
        return cu;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != TestHarnessClassLoader.class) {
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

    public /* synthetic */ CompilationUnit super$5$createCompilationUnit(CompilerConfiguration compilerConfiguration, CodeSource codeSource) {
        return super.createCompilationUnit(compilerConfiguration, codeSource);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "addPhaseOperation";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "getPhaseNumber";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[3];
        TestHarnessClassLoader.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(TestHarnessClassLoader.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = TestHarnessClassLoader.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

