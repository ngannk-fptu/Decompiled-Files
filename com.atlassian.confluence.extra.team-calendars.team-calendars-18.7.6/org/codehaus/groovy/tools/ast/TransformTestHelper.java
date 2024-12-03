/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.ast;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.io.File;
import java.lang.ref.SoftReference;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.tools.ast.TestHarnessClassLoader;
import org.codehaus.groovy.transform.ASTTransformation;

public class TransformTestHelper
implements GroovyObject {
    private final ASTTransformation transform;
    private final CompilePhase phase;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public TransformTestHelper(ASTTransformation transform, CompilePhase phase) {
        MetaClass metaClass;
        CallSite[] callSiteArray = TransformTestHelper.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        ASTTransformation aSTTransformation = transform;
        this.transform = (ASTTransformation)ScriptBytecodeAdapter.castToType(aSTTransformation, ASTTransformation.class);
        CompilePhase compilePhase = phase;
        this.phase = (CompilePhase)ShortTypeHandling.castToEnum((Object)compilePhase, CompilePhase.class);
    }

    public Class parse(File input) {
        CallSite[] callSiteArray = TransformTestHelper.$getCallSiteArray();
        TestHarnessClassLoader loader = (TestHarnessClassLoader)ScriptBytecodeAdapter.castToType(callSiteArray[0].callConstructor(TestHarnessClassLoader.class, this.transform, (Object)this.phase), TestHarnessClassLoader.class);
        return ShortTypeHandling.castToClass(callSiteArray[1].call((Object)loader, input));
    }

    public Class parse(String input) {
        CallSite[] callSiteArray = TransformTestHelper.$getCallSiteArray();
        TestHarnessClassLoader loader = (TestHarnessClassLoader)ScriptBytecodeAdapter.castToType(callSiteArray[2].callConstructor(TestHarnessClassLoader.class, this.transform, (Object)this.phase), TestHarnessClassLoader.class);
        return ShortTypeHandling.castToClass(callSiteArray[3].call((Object)loader, input));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != TransformTestHelper.class) {
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
        stringArray[1] = "parseClass";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "parseClass";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[4];
        TransformTestHelper.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(TransformTestHelper.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = TransformTestHelper.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

