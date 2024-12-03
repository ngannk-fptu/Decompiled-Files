/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.Test
 *  junit.framework.TestSuite
 *  junit.textui.TestRunner
 */
package groovy.util;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.util.IFileNameFinder;
import groovy.util.JavadocAssertionTestBuilder;
import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Collection;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class JavadocAssertionTestSuite
extends TestSuite
implements GroovyObject {
    public static final String SYSPROP_SRC_DIR = "javadocAssertion.src.dir";
    public static final String SYSPROP_SRC_PATTERN = "javadocAssertion.src.pattern";
    public static final String SYSPROP_SRC_EXCLUDES_PATTERN = "javadocAssertion.src.excludesPattern";
    private static final JavadocAssertionTestBuilder testBuilder;
    private static final IFileNameFinder finder;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public JavadocAssertionTestSuite() {
        MetaClass metaClass;
        CallSite[] callSiteArray = JavadocAssertionTestSuite.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public static Test suite() {
        CallSite[] callSiteArray = JavadocAssertionTestSuite.$getCallSiteArray();
        String basedir = ShortTypeHandling.castToString(callSiteArray[0].call(System.class, SYSPROP_SRC_DIR, "./src/"));
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return (Test)ScriptBytecodeAdapter.castToType(callSiteArray[1].callStatic(JavadocAssertionTestSuite.class, basedir), Test.class);
        }
        return JavadocAssertionTestSuite.suite(basedir);
    }

    public static Test suite(String basedir) {
        CallSite[] callSiteArray = JavadocAssertionTestSuite.$getCallSiteArray();
        String includePattern = ShortTypeHandling.castToString(callSiteArray[2].call(System.class, SYSPROP_SRC_PATTERN, "**/*.java,**/*.groovy"));
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return (Test)ScriptBytecodeAdapter.castToType(callSiteArray[3].callStatic(JavadocAssertionTestSuite.class, basedir, includePattern), Test.class);
        }
        return JavadocAssertionTestSuite.suite(basedir, includePattern);
    }

    public static Test suite(String basedir, String includePattern) {
        CallSite[] callSiteArray = JavadocAssertionTestSuite.$getCallSiteArray();
        String excludePattern = ShortTypeHandling.castToString(callSiteArray[4].call(System.class, SYSPROP_SRC_EXCLUDES_PATTERN, ""));
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return (Test)ScriptBytecodeAdapter.castToType(callSiteArray[5].callStatic(JavadocAssertionTestSuite.class, basedir, includePattern, excludePattern), Test.class);
        }
        return JavadocAssertionTestSuite.suite(basedir, includePattern, excludePattern);
    }

    public static Test suite(String basedir, String includePattern, String excludePattern) {
        CallSite[] callSiteArray = JavadocAssertionTestSuite.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            CallSite callSite = callSiteArray[6];
            CallSite callSite2 = callSiteArray[7];
            String string = basedir;
            valueRecorder.record(string, 17);
            Object object = callSite2.callConstructor(File.class, string);
            valueRecorder.record(object, 8);
            Object object2 = callSite.call(object);
            valueRecorder.record(object2, 26);
            if (DefaultTypeTransformation.booleanUnbox(object2)) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert new File(basedir).exists()", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        Reference<TestSuite> suite = new Reference<TestSuite>((TestSuite)ScriptBytecodeAdapter.castToType(callSiteArray[8].callConstructor(JavadocAssertionTestSuite.class), TestSuite.class));
        Collection filenames = (Collection)ScriptBytecodeAdapter.castToType(callSiteArray[9].call((Object)finder, ScriptBytecodeAdapter.createMap(new Object[]{"dir", basedir, "includes", includePattern, "excludes", excludePattern})), Collection.class);
        public class _suite_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference suite;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _suite_closure1(Object _outerInstance, Object _thisObject, Reference suite) {
                Reference reference;
                CallSite[] callSiteArray = _suite_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.suite = reference = suite;
            }

            public Object doCall(Object filename) {
                CallSite[] callSiteArray = _suite_closure1.$getCallSiteArray();
                String code = ShortTypeHandling.castToString(callSiteArray[0].callGetProperty(callSiteArray[1].callConstructor(File.class, filename)));
                Class test = ShortTypeHandling.castToClass(callSiteArray[2].call(callSiteArray[3].callGetProperty(JavadocAssertionTestSuite.class), filename, code));
                if (ScriptBytecodeAdapter.compareNotEqual(test, null)) {
                    return callSiteArray[4].call(this.suite.get(), test);
                }
                return null;
            }

            public TestSuite getSuite() {
                CallSite[] callSiteArray = _suite_closure1.$getCallSiteArray();
                return (TestSuite)ScriptBytecodeAdapter.castToType(this.suite.get(), TestSuite.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _suite_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "text";
                stringArray[1] = "<$constructor$>";
                stringArray[2] = "buildTest";
                stringArray[3] = "testBuilder";
                stringArray[4] = "addTestSuite";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[5];
                _suite_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_suite_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _suite_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[10].call((Object)filenames, new _suite_closure1(JavadocAssertionTestSuite.class, JavadocAssertionTestSuite.class, suite));
        return suite.get();
    }

    public static void main(String ... args) {
        CallSite[] callSiteArray = JavadocAssertionTestSuite.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object = callSiteArray[11].callGetProperty(args);
            if (ScriptBytecodeAdapter.isCase(object, 3)) {
                callSiteArray[12].call(TestRunner.class, callSiteArray[13].callStatic(JavadocAssertionTestSuite.class, callSiteArray[14].call((Object)args, 0), callSiteArray[15].call((Object)args, 1), callSiteArray[16].call((Object)args, 2)));
            } else if (ScriptBytecodeAdapter.isCase(object, 2)) {
                callSiteArray[17].call(TestRunner.class, callSiteArray[18].callStatic(JavadocAssertionTestSuite.class, callSiteArray[19].call((Object)args, 0), callSiteArray[20].call((Object)args, 1)));
            } else if (ScriptBytecodeAdapter.isCase(object, 1)) {
                callSiteArray[21].call(TestRunner.class, callSiteArray[22].callStatic(JavadocAssertionTestSuite.class, callSiteArray[23].call((Object)args, 0)));
            } else {
                callSiteArray[24].call(TestRunner.class, callSiteArray[25].callStatic(JavadocAssertionTestSuite.class));
            }
        } else {
            Object object = callSiteArray[26].callGetProperty(args);
            if (ScriptBytecodeAdapter.isCase(object, 3)) {
                callSiteArray[27].call(TestRunner.class, JavadocAssertionTestSuite.suite(ShortTypeHandling.castToString(BytecodeInterface8.objectArrayGet(args, 0)), ShortTypeHandling.castToString(BytecodeInterface8.objectArrayGet(args, 1)), ShortTypeHandling.castToString(BytecodeInterface8.objectArrayGet(args, 2))));
            } else if (ScriptBytecodeAdapter.isCase(object, 2)) {
                callSiteArray[28].call(TestRunner.class, JavadocAssertionTestSuite.suite(ShortTypeHandling.castToString(BytecodeInterface8.objectArrayGet(args, 0)), ShortTypeHandling.castToString(BytecodeInterface8.objectArrayGet(args, 1))));
            } else if (ScriptBytecodeAdapter.isCase(object, 1)) {
                callSiteArray[29].call(TestRunner.class, JavadocAssertionTestSuite.suite(ShortTypeHandling.castToString(BytecodeInterface8.objectArrayGet(args, 0))));
            } else {
                callSiteArray[30].call(TestRunner.class, JavadocAssertionTestSuite.suite());
            }
        }
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != JavadocAssertionTestSuite.class) {
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

    static {
        Object object = JavadocAssertionTestSuite.$getCallSiteArray()[31].callConstructor(JavadocAssertionTestBuilder.class);
        testBuilder = (JavadocAssertionTestBuilder)ScriptBytecodeAdapter.castToType(object, JavadocAssertionTestBuilder.class);
        Object object2 = JavadocAssertionTestSuite.$getCallSiteArray()[32].call(JavadocAssertionTestSuite.$getCallSiteArray()[33].call(Class.class, "groovy.util.FileNameFinder", true, JavadocAssertionTestSuite.$getCallSiteArray()[34].callGroovyObjectGetProperty(JavadocAssertionTestSuite.class)));
        finder = (IFileNameFinder)ScriptBytecodeAdapter.castToType(object2, IFileNameFinder.class);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "getProperty";
        stringArray[1] = "suite";
        stringArray[2] = "getProperty";
        stringArray[3] = "suite";
        stringArray[4] = "getProperty";
        stringArray[5] = "suite";
        stringArray[6] = "exists";
        stringArray[7] = "<$constructor$>";
        stringArray[8] = "<$constructor$>";
        stringArray[9] = "getFileNames";
        stringArray[10] = "each";
        stringArray[11] = "length";
        stringArray[12] = "run";
        stringArray[13] = "suite";
        stringArray[14] = "getAt";
        stringArray[15] = "getAt";
        stringArray[16] = "getAt";
        stringArray[17] = "run";
        stringArray[18] = "suite";
        stringArray[19] = "getAt";
        stringArray[20] = "getAt";
        stringArray[21] = "run";
        stringArray[22] = "suite";
        stringArray[23] = "getAt";
        stringArray[24] = "run";
        stringArray[25] = "suite";
        stringArray[26] = "length";
        stringArray[27] = "run";
        stringArray[28] = "run";
        stringArray[29] = "run";
        stringArray[30] = "run";
        stringArray[31] = "<$constructor$>";
        stringArray[32] = "newInstance";
        stringArray[33] = "forName";
        stringArray[34] = "classLoader";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[35];
        JavadocAssertionTestSuite.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(JavadocAssertionTestSuite.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = JavadocAssertionTestSuite.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

