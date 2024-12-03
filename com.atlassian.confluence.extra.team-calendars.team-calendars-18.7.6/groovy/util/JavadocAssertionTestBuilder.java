/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.Closure;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class JavadocAssertionTestBuilder
implements GroovyObject {
    private static final Pattern javadocPattern;
    private static final Pattern assertionPattern;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public JavadocAssertionTestBuilder() {
        MetaClass metaClass;
        CallSite[] callSiteArray = JavadocAssertionTestBuilder.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public Class buildTest(String filename, String code) {
        CallSite[] callSiteArray = JavadocAssertionTestBuilder.$getCallSiteArray();
        Class test = null;
        List assertionTags = null;
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object = callSiteArray[0].callCurrent((GroovyObject)this, code);
            assertionTags = (List)ScriptBytecodeAdapter.castToType(object, List.class);
        } else {
            List list;
            assertionTags = list = this.getAssertionTags(code);
        }
        if (DefaultTypeTransformation.booleanUnbox(assertionTags)) {
            String testName = null;
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                Object object = callSiteArray[1].callCurrent((GroovyObject)this, filename);
                testName = ShortTypeHandling.castToString(object);
            } else {
                String string;
                testName = string = this.getTestName(filename);
            }
            Map lineNumberToAssertions = (Map)ScriptBytecodeAdapter.castToType(callSiteArray[2].callCurrent(this, code, assertionTags), Map.class);
            List testMethods = (List)ScriptBytecodeAdapter.castToType(callSiteArray[3].callCurrent(this, lineNumberToAssertions, filename), List.class);
            String testCode = ShortTypeHandling.castToString(callSiteArray[4].callCurrent(this, testName, testMethods));
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                Object object = callSiteArray[5].callCurrent((GroovyObject)this, testCode);
                test = ShortTypeHandling.castToClass(object);
            } else {
                Class clazz;
                test = clazz = this.createClass(testCode);
            }
        }
        return test;
    }

    private List getAssertionTags(String code) {
        CallSite[] callSiteArray = JavadocAssertionTestBuilder.$getCallSiteArray();
        Reference<List> assertions = new Reference<List>((List)ScriptBytecodeAdapter.castToType(callSiteArray[6].callConstructor(ArrayList.class), List.class));
        public class _getAssertionTags_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference assertions;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _getAssertionTags_closure1(Object _outerInstance, Object _thisObject, Reference assertions) {
                Reference reference;
                CallSite[] callSiteArray = _getAssertionTags_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.assertions = reference = assertions;
            }

            public Object doCall(Object javadoc) {
                CallSite[] callSiteArray = _getAssertionTags_closure1.$getCallSiteArray();
                return callSiteArray[0].call(this.assertions.get(), callSiteArray[1].call(javadoc, callSiteArray[2].callGetProperty(JavadocAssertionTestBuilder.class)));
            }

            public List getAssertions() {
                CallSite[] callSiteArray = _getAssertionTags_closure1.$getCallSiteArray();
                return (List)ScriptBytecodeAdapter.castToType(this.assertions.get(), List.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _getAssertionTags_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "addAll";
                stringArray[1] = "findAll";
                stringArray[2] = "assertionPattern";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[3];
                _getAssertionTags_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_getAssertionTags_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _getAssertionTags_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[7].call(code, javadocPattern, new _getAssertionTags_closure1(this, this, assertions));
        return assertions.get();
    }

    private String getTestName(String filename) {
        CallSite[] callSiteArray = JavadocAssertionTestBuilder.$getCallSiteArray();
        String filenameWithoutPath = ShortTypeHandling.castToString(callSiteArray[8].callGetProperty(callSiteArray[9].callConstructor(File.class, filename)));
        String testName = ShortTypeHandling.castToString(callSiteArray[10].call(callSiteArray[11].call(filenameWithoutPath, 0, callSiteArray[12].call((Object)filenameWithoutPath, ".")), "JavadocAssertionTest"));
        return testName;
    }

    private Map getLineNumberToAssertionsMap(String code, List assertionTags) {
        Reference<String> code2 = new Reference<String>(code);
        CallSite[] callSiteArray = JavadocAssertionTestBuilder.$getCallSiteArray();
        Reference<LinkedHashMap> lineNumberToAssertions = new Reference<LinkedHashMap>((LinkedHashMap)ScriptBytecodeAdapter.asType(ScriptBytecodeAdapter.createMap(new Object[0]), LinkedHashMap.class));
        Reference<Integer> codeIndex = new Reference<Integer>(0);
        public class _getLineNumberToAssertionsMap_closure2
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference codeIndex;
            private /* synthetic */ Reference code;
            private /* synthetic */ Reference lineNumberToAssertions;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _getLineNumberToAssertionsMap_closure2(Object _outerInstance, Object _thisObject, Reference codeIndex, Reference code, Reference lineNumberToAssertions) {
                Reference reference;
                Reference reference2;
                Reference reference3;
                CallSite[] callSiteArray = _getLineNumberToAssertionsMap_closure2.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.codeIndex = reference3 = codeIndex;
                this.code = reference2 = code;
                this.lineNumberToAssertions = reference = lineNumberToAssertions;
            }

            public Object doCall(Object tag) {
                CallSite[] callSiteArray = _getLineNumberToAssertionsMap_closure2.$getCallSiteArray();
                Object object = callSiteArray[0].call(this.code.get(), tag, this.codeIndex.get());
                this.codeIndex.set((Integer)ScriptBytecodeAdapter.castToType(object, Integer.class));
                int lineNumber = DefaultTypeTransformation.intUnbox(callSiteArray[1].call(callSiteArray[2].call(callSiteArray[3].call(this.code.get(), 0, this.codeIndex.get()), "(?m)^")));
                this.codeIndex.set((Integer)ScriptBytecodeAdapter.castToType(callSiteArray[4].call(this.codeIndex.get(), callSiteArray[5].call(tag)), Integer.class));
                String assertion = ShortTypeHandling.castToString(callSiteArray[6].callCurrent((GroovyObject)this, tag));
                return callSiteArray[7].call(callSiteArray[8].call(this.lineNumberToAssertions.get(), lineNumber, ScriptBytecodeAdapter.createList(new Object[0])), assertion);
            }

            public Integer getCodeIndex() {
                CallSite[] callSiteArray = _getLineNumberToAssertionsMap_closure2.$getCallSiteArray();
                return (Integer)ScriptBytecodeAdapter.castToType(this.codeIndex.get(), Integer.class);
            }

            public String getCode() {
                CallSite[] callSiteArray = _getLineNumberToAssertionsMap_closure2.$getCallSiteArray();
                return ShortTypeHandling.castToString(this.code.get());
            }

            public Map getLineNumberToAssertions() {
                CallSite[] callSiteArray = _getLineNumberToAssertionsMap_closure2.$getCallSiteArray();
                return (Map)ScriptBytecodeAdapter.castToType(this.lineNumberToAssertions.get(), Map.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _getLineNumberToAssertionsMap_closure2.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "indexOf";
                stringArray[1] = "size";
                stringArray[2] = "findAll";
                stringArray[3] = "substring";
                stringArray[4] = "plus";
                stringArray[5] = "size";
                stringArray[6] = "getAssertion";
                stringArray[7] = "leftShift";
                stringArray[8] = "get";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[9];
                _getLineNumberToAssertionsMap_closure2.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_getLineNumberToAssertionsMap_closure2.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _getLineNumberToAssertionsMap_closure2.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[13].call((Object)assertionTags, new _getLineNumberToAssertionsMap_closure2(this, this, codeIndex, code2, lineNumberToAssertions));
        return lineNumberToAssertions.get();
    }

    private String getAssertion(String tag) {
        CallSite[] callSiteArray = JavadocAssertionTestBuilder.$getCallSiteArray();
        String tagInner = ShortTypeHandling.castToString(callSiteArray[14].call(tag, callSiteArray[15].call(callSiteArray[16].call((Object)tag, ">"), 1), callSiteArray[17].call((Object)tag, "<")));
        String htmlAssertion = ShortTypeHandling.castToString(callSiteArray[18].call(tagInner, "(?m)^\\s*\\*", ""));
        Reference<String> assertion = new Reference<String>(htmlAssertion);
        public class _getAssertion_closure3
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference assertion;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _getAssertion_closure3(Object _outerInstance, Object _thisObject, Reference assertion) {
                Reference reference;
                CallSite[] callSiteArray = _getAssertion_closure3.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.assertion = reference = assertion;
            }

            public Object doCall(Object key, Object value) {
                CallSite[] callSiteArray = _getAssertion_closure3.$getCallSiteArray();
                Object object = callSiteArray[0].call(this.assertion.get(), new GStringImpl(new Object[]{key}, new String[]{"(?i)&", ";"}), value);
                this.assertion.set(ShortTypeHandling.castToString(object));
                return object;
            }

            public Object call(Object key, Object value) {
                CallSite[] callSiteArray = _getAssertion_closure3.$getCallSiteArray();
                return callSiteArray[1].callCurrent(this, key, value);
            }

            public String getAssertion() {
                CallSite[] callSiteArray = _getAssertion_closure3.$getCallSiteArray();
                return ShortTypeHandling.castToString(this.assertion.get());
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _getAssertion_closure3.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "replaceAll";
                stringArray[1] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _getAssertion_closure3.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_getAssertion_closure3.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _getAssertion_closure3.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[19].call((Object)ScriptBytecodeAdapter.createMap(new Object[]{"nbsp", " ", "gt", ">", "lt", "<", "quot", "\"", "apos", "'", "at", "@", "#64", "@", "ndash", "-", "amp", "&"}), new _getAssertion_closure3(this, this, assertion));
        Object object = callSiteArray[20].call(assertion.get(), "(?i)\\{@code ([^}]*)\\}", "$1");
        assertion.set(ShortTypeHandling.castToString(object));
        return assertion.get();
    }

    private List getTestMethods(Map lineNumberToAssertions, String filename) {
        Reference<String> filename2 = new Reference<String>(filename);
        CallSite[] callSiteArray = JavadocAssertionTestBuilder.$getCallSiteArray();
        public class _getTestMethods_closure4
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference filename;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _getTestMethods_closure4(Object _outerInstance, Object _thisObject, Reference filename) {
                Reference reference;
                CallSite[] callSiteArray = _getTestMethods_closure4.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.filename = reference = filename;
            }

            /*
             * WARNING - void declaration
             */
            public Object doCall(Object lineNumber, Object assertions) {
                void var2_2;
                Reference<Object> lineNumber2 = new Reference<Object>(lineNumber);
                Reference<void> assertions2 = new Reference<void>(var2_2);
                CallSite[] callSiteArray = _getTestMethods_closure4.$getCallSiteArray();
                Reference<Character> differentiator = new Reference<Character>((Character)ScriptBytecodeAdapter.castToType("a", Character.class));
                public class _closure5
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference assertions;
                    private /* synthetic */ Reference lineNumber;
                    private /* synthetic */ Reference differentiator;
                    private /* synthetic */ Reference filename;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure5(Object _outerInstance, Object _thisObject, Reference assertions, Reference lineNumber, Reference differentiator, Reference filename) {
                        Reference reference;
                        Reference reference2;
                        Reference reference3;
                        Reference reference4;
                        CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.assertions = reference4 = assertions;
                        this.lineNumber = reference3 = lineNumber;
                        this.differentiator = reference2 = differentiator;
                        this.filename = reference = filename;
                    }

                    public Object doCall(Object assertion) {
                        CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                        String suffix = ShortTypeHandling.castToString(ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[0].call(this.assertions.get()), 1) ? new GStringImpl(new Object[]{this.lineNumber.get(), this.differentiator.get()}, new String[]{"", "", ""}) : this.lineNumber.get());
                        Object t = this.differentiator.get();
                        this.differentiator.set((Character)ScriptBytecodeAdapter.castToType(callSiteArray[1].call(t), Character.class));
                        return callSiteArray[2].callCurrent(this, suffix, assertion, callSiteArray[3].callCurrent((GroovyObject)this, this.filename.get()));
                    }

                    public Object getAssertions() {
                        CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                        return this.assertions.get();
                    }

                    public Object getLineNumber() {
                        CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                        return this.lineNumber.get();
                    }

                    public Character getDifferentiator() {
                        CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                        return (Character)ScriptBytecodeAdapter.castToType(this.differentiator.get(), Character.class);
                    }

                    public String getFilename() {
                        CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                        return ShortTypeHandling.castToString(this.filename.get());
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure5.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "size";
                        stringArray[1] = "next";
                        stringArray[2] = "getTestMethodCodeForAssertion";
                        stringArray[3] = "basename";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[4];
                        _closure5.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_closure5.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure5.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                return callSiteArray[0].call((Object)assertions2.get(), new _closure5(this, this.getThisObject(), assertions2, lineNumber2, differentiator, this.filename));
            }

            /*
             * WARNING - void declaration
             */
            public Object call(Object lineNumber, Object assertions) {
                void var2_2;
                Reference<Object> lineNumber2 = new Reference<Object>(lineNumber);
                Reference<void> assertions2 = new Reference<void>(var2_2);
                CallSite[] callSiteArray = _getTestMethods_closure4.$getCallSiteArray();
                return callSiteArray[1].callCurrent(this, lineNumber2.get(), assertions2.get());
            }

            public String getFilename() {
                CallSite[] callSiteArray = _getTestMethods_closure4.$getCallSiteArray();
                return ShortTypeHandling.castToString(this.filename.get());
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _getTestMethods_closure4.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "collect";
                stringArray[1] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _getTestMethods_closure4.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_getTestMethods_closure4.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _getTestMethods_closure4.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        List testMethods = (List)ScriptBytecodeAdapter.castToType(callSiteArray[21].call(callSiteArray[22].call((Object)lineNumberToAssertions, new _getTestMethods_closure4(this, this, filename2))), List.class);
        return testMethods;
    }

    private String basename(String fullPath) {
        CallSite[] callSiteArray = JavadocAssertionTestBuilder.$getCallSiteArray();
        Object path = callSiteArray[23].callConstructor(File.class, fullPath);
        Object fullName = callSiteArray[24].callGetProperty(path);
        return ShortTypeHandling.castToString(callSiteArray[25].call(fullName, 0, callSiteArray[26].call(fullName, ".")));
    }

    private String getTestMethodCodeForAssertion(String suffix, String assertion, String basename) {
        CallSite[] callSiteArray = JavadocAssertionTestBuilder.$getCallSiteArray();
        return ShortTypeHandling.castToString(new GStringImpl(new Object[]{basename, suffix, callSiteArray[27].call((Object)((List)ScriptBytecodeAdapter.asType(callSiteArray[28].call((Object)assertion, "UTF-8"), List.class)), ", ")}, new String[]{"\n            public void testAssertionFrom", "Line", "() {\n                byte[] bytes = [ ", " ] as byte[]\n                Eval.me(new String(bytes, \"UTF-8\"))\n            }\n        "}));
    }

    private String getTestCode(String testName, List testMethods) {
        CallSite[] callSiteArray = JavadocAssertionTestBuilder.$getCallSiteArray();
        return ShortTypeHandling.castToString(callSiteArray[29].call(callSiteArray[30].call((Object)new GStringImpl(new Object[]{testName}, new String[]{"\n            class ", " extends junit.framework.TestCase {\n                "}), callSiteArray[31].call((Object)testMethods, "\r\n")), "\n            }\n        "));
    }

    private Class createClass(String testCode) {
        CallSite[] callSiteArray = JavadocAssertionTestBuilder.$getCallSiteArray();
        return ShortTypeHandling.castToClass(callSiteArray[32].call(callSiteArray[33].callConstructor(GroovyClassLoader.class), testCode));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != JavadocAssertionTestBuilder.class) {
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
        Object object = JavadocAssertionTestBuilder.$getCallSiteArray()[34].call(Pattern.class, "(?ims)/\\*\\*.*?\\*/");
        javadocPattern = (Pattern)ScriptBytecodeAdapter.castToType(object, Pattern.class);
        Object object2 = JavadocAssertionTestBuilder.$getCallSiteArray()[35].call(Pattern.class, "(?ims)<([a-z]+)\\s+class\\s*=\\s*['\"]groovyTestCase['\"]\\s*>.*?<\\s*/\\s*\\1>");
        assertionPattern = (Pattern)ScriptBytecodeAdapter.castToType(object2, Pattern.class);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "getAssertionTags";
        stringArray[1] = "getTestName";
        stringArray[2] = "getLineNumberToAssertionsMap";
        stringArray[3] = "getTestMethods";
        stringArray[4] = "getTestCode";
        stringArray[5] = "createClass";
        stringArray[6] = "<$constructor$>";
        stringArray[7] = "eachMatch";
        stringArray[8] = "name";
        stringArray[9] = "<$constructor$>";
        stringArray[10] = "plus";
        stringArray[11] = "substring";
        stringArray[12] = "lastIndexOf";
        stringArray[13] = "each";
        stringArray[14] = "substring";
        stringArray[15] = "plus";
        stringArray[16] = "indexOf";
        stringArray[17] = "lastIndexOf";
        stringArray[18] = "replaceAll";
        stringArray[19] = "each";
        stringArray[20] = "replaceAll";
        stringArray[21] = "flatten";
        stringArray[22] = "collect";
        stringArray[23] = "<$constructor$>";
        stringArray[24] = "name";
        stringArray[25] = "substring";
        stringArray[26] = "lastIndexOf";
        stringArray[27] = "join";
        stringArray[28] = "getBytes";
        stringArray[29] = "plus";
        stringArray[30] = "plus";
        stringArray[31] = "join";
        stringArray[32] = "parseClass";
        stringArray[33] = "<$constructor$>";
        stringArray[34] = "compile";
        stringArray[35] = "compile";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[36];
        JavadocAssertionTestBuilder.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(JavadocAssertionTestBuilder.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = JavadocAssertionTestBuilder.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

