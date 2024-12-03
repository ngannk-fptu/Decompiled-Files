/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.thoughtworks.qdox.JavaDocBuilder
 *  com.thoughtworks.qdox.model.JavaClass
 *  com.thoughtworks.qdox.model.JavaMethod
 *  com.thoughtworks.qdox.model.JavaParameter
 *  com.thoughtworks.qdox.model.Type
 */
package org.codehaus.groovy.tools;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.Type;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.lang.MissingPropertyException;
import groovy.lang.Reference;
import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import groovy.text.TemplateEngine;
import groovy.util.CliBuilder;
import java.io.File;
import java.lang.ref.SoftReference;
import java.text.BreakIterator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.tools.shell.util.Logger;
import org.codehaus.groovy.tools.shell.util.MessageSource;

public class DocGenerator
implements GroovyObject {
    private static final MessageSource messages;
    private static final Logger log;
    private static final Comparator SORT_KEY_COMPARATOR;
    private static final Map<String, Object> CONFIG;
    private List<File> sourceFiles;
    private File outputDir;
    private DocSource docSource;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public DocGenerator(List<File> sourceFiles, File outputFolder) {
        MetaClass metaClass;
        CallSite[] callSiteArray = DocGenerator.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        List<File> list = sourceFiles;
        this.sourceFiles = (List)ScriptBytecodeAdapter.castToType(list, List.class);
        File file = outputFolder;
        this.outputDir = (File)ScriptBytecodeAdapter.castToType(file, File.class);
        Object object = callSiteArray[0].callStatic(DocGenerator.class, sourceFiles);
        this.docSource = (DocSource)ScriptBytecodeAdapter.castToType(object, DocSource.class);
    }

    private static DocSource parseSource(List<File> sourceFiles) {
        CallSite[] callSiteArray = DocGenerator.$getCallSiteArray();
        Reference<JavaDocBuilder> builder = new Reference<JavaDocBuilder>((JavaDocBuilder)ScriptBytecodeAdapter.castToType(callSiteArray[1].callConstructor(JavaDocBuilder.class), JavaDocBuilder.class));
        public class _parseSource_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference builder;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _parseSource_closure1(Object _outerInstance, Object _thisObject, Reference builder) {
                Reference reference;
                CallSite[] callSiteArray = _parseSource_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.builder = reference = builder;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _parseSource_closure1.$getCallSiteArray();
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(it))) {
                    callSiteArray[1].call(this.builder.get(), callSiteArray[2].call(it));
                    return callSiteArray[3].call(callSiteArray[4].callGetProperty(DocGenerator.class), new GStringImpl(new Object[]{it}, new String[]{"adding reader for ", ""}));
                }
                return callSiteArray[5].call(callSiteArray[6].callGetProperty(DocGenerator.class), new GStringImpl(new Object[]{callSiteArray[7].callGetProperty(it)}, new String[]{"not found, skipping: ", ""}));
            }

            public JavaDocBuilder getBuilder() {
                CallSite[] callSiteArray = _parseSource_closure1.$getCallSiteArray();
                return (JavaDocBuilder)ScriptBytecodeAdapter.castToType(this.builder.get(), JavaDocBuilder.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _parseSource_closure1.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _parseSource_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "exists";
                stringArray[1] = "addSource";
                stringArray[2] = "newReader";
                stringArray[3] = "debug";
                stringArray[4] = "log";
                stringArray[5] = "debug";
                stringArray[6] = "log";
                stringArray[7] = "path";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[8];
                _parseSource_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_parseSource_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _parseSource_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[2].call(sourceFiles, new _parseSource_closure1(DocGenerator.class, DocGenerator.class, builder));
        public class _parseSource_closure2
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _parseSource_closure2(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _parseSource_closure2.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object source) {
                CallSite[] callSiteArray = _parseSource_closure2.$getCallSiteArray();
                public class _closure19
                extends Closure
                implements GeneratedClosure {
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure19(Object _outerInstance, Object _thisObject) {
                        CallSite[] callSiteArray = _closure19.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                    }

                    public Object doCall(Object aClass) {
                        CallSite[] callSiteArray = _closure19.$getCallSiteArray();
                        public class _closure20
                        extends Closure
                        implements GeneratedClosure {
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure20(Object _outerInstance, Object _thisObject) {
                                CallSite[] callSiteArray = _closure20.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                            }

                            public Object doCall(Object it) {
                                CallSite[] callSiteArray = _closure20.$getCallSiteArray();
                                public class _closure21
                                extends Closure
                                implements GeneratedClosure {
                                    private static /* synthetic */ ClassInfo $staticClassInfo;
                                    public static transient /* synthetic */ boolean __$stMC;
                                    private static /* synthetic */ SoftReference $callSiteArray;

                                    public _closure21(Object _outerInstance, Object _thisObject) {
                                        CallSite[] callSiteArray = _closure21.$getCallSiteArray();
                                        super(_outerInstance, _thisObject);
                                    }

                                    public Object doCall(Object it) {
                                        CallSite[] callSiteArray = _closure21.$getCallSiteArray();
                                        return ScriptBytecodeAdapter.compareEqual(callSiteArray[0].callGetProperty(callSiteArray[1].callGetProperty(it)), "java.lang.Deprecated");
                                    }

                                    public Object doCall() {
                                        CallSite[] callSiteArray = _closure21.$getCallSiteArray();
                                        return this.doCall(null);
                                    }

                                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                        if (this.getClass() != _closure21.class) {
                                            return ScriptBytecodeAdapter.initMetaClass(this);
                                        }
                                        ClassInfo classInfo = $staticClassInfo;
                                        if (classInfo == null) {
                                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                        }
                                        return classInfo.getMetaClass();
                                    }

                                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                        stringArray[0] = "fullyQualifiedName";
                                        stringArray[1] = "type";
                                    }

                                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                        String[] stringArray = new String[2];
                                        _closure21.$createCallSiteArray_1(stringArray);
                                        return new CallSiteArray(_closure21.class, stringArray);
                                    }

                                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                        CallSiteArray callSiteArray;
                                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                            callSiteArray = _closure21.$createCallSiteArray();
                                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                        }
                                        return callSiteArray.array;
                                    }
                                }
                                return !DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(callSiteArray[1].callGetProperty(it), new _closure21(this, this.getThisObject())));
                            }

                            public Object doCall() {
                                CallSite[] callSiteArray = _closure20.$getCallSiteArray();
                                return this.doCall(null);
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _closure20.class) {
                                    return ScriptBytecodeAdapter.initMetaClass(this);
                                }
                                ClassInfo classInfo = $staticClassInfo;
                                if (classInfo == null) {
                                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                }
                                return classInfo.getMetaClass();
                            }

                            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                stringArray[0] = "any";
                                stringArray[1] = "annotations";
                            }

                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                String[] stringArray = new String[2];
                                _closure20.$createCallSiteArray_1(stringArray);
                                return new CallSiteArray(_closure20.class, stringArray);
                            }

                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                CallSiteArray callSiteArray;
                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                    callSiteArray = _closure20.$createCallSiteArray();
                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                }
                                return callSiteArray.array;
                            }
                        }
                        return callSiteArray[0].call(callSiteArray[1].callGetProperty(aClass), new _closure20(this, this.getThisObject()));
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure19.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "findAll";
                        stringArray[1] = "methods";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[2];
                        _closure19.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_closure19.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure19.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                return callSiteArray[0].call(callSiteArray[1].callGetProperty(source), new _closure19(this, this.getThisObject()));
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _parseSource_closure2.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "collectMany";
                stringArray[1] = "classes";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _parseSource_closure2.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_parseSource_closure2.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _parseSource_closure2.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        Object methods = callSiteArray[3].call(callSiteArray[4].callGetProperty(builder.get()), new _parseSource_closure2(DocGenerator.class, DocGenerator.class));
        Reference<Object> docSource = new Reference<Object>(callSiteArray[5].callConstructor(DocSource.class));
        public class _parseSource_closure3
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference docSource;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _parseSource_closure3(Object _outerInstance, Object _thisObject, Reference docSource) {
                Reference reference;
                CallSite[] callSiteArray = _parseSource_closure3.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.docSource = reference = docSource;
            }

            public Object doCall(JavaMethod method) {
                CallSite[] callSiteArray = _parseSource_closure3.$getCallSiteArray();
                if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass() ? !DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(method)) || !DefaultTypeTransformation.booleanUnbox(callSiteArray[1].call(method)) : !DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call(method)) || !DefaultTypeTransformation.booleanUnbox(callSiteArray[3].call(method))) {
                    return null;
                }
                Object firstParam = callSiteArray[4].call(callSiteArray[5].callGetProperty(method), 0);
                Object firstParamType = DefaultTypeTransformation.booleanUnbox(callSiteArray[6].call(callSiteArray[7].callGetProperty(firstParam))) ? callSiteArray[8].callGetProperty(firstParam) : callSiteArray[9].callConstructor(Type.class, callSiteArray[10].callGetProperty(firstParam), 0, callSiteArray[11].callGetProperty(firstParam));
                return callSiteArray[12].call(this.docSource.get(), firstParamType, method);
            }

            public Object call(JavaMethod method) {
                CallSite[] callSiteArray = _parseSource_closure3.$getCallSiteArray();
                return callSiteArray[13].callCurrent((GroovyObject)this, method);
            }

            public Object getDocSource() {
                CallSite[] callSiteArray = _parseSource_closure3.$getCallSiteArray();
                return this.docSource.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _parseSource_closure3.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "isPublic";
                stringArray[1] = "isStatic";
                stringArray[2] = "isPublic";
                stringArray[3] = "isStatic";
                stringArray[4] = "getAt";
                stringArray[5] = "parameters";
                stringArray[6] = "isEmpty";
                stringArray[7] = "resolvedValue";
                stringArray[8] = "type";
                stringArray[9] = "<$constructor$>";
                stringArray[10] = "resolvedValue";
                stringArray[11] = "parentClass";
                stringArray[12] = "add";
                stringArray[13] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[14];
                _parseSource_closure3.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_parseSource_closure3.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _parseSource_closure3.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[6].call(methods, new _parseSource_closure3(DocGenerator.class, DocGenerator.class, docSource));
        callSiteArray[7].call(docSource.get());
        return (DocSource)ScriptBytecodeAdapter.castToType(docSource.get(), DocSource.class);
    }

    public void generateAll() {
        CallSite[] callSiteArray = DocGenerator.$getCallSiteArray();
        Object engine = callSiteArray[8].callConstructor(SimpleTemplateEngine.class);
        Reference<Object> indexTemplate = new Reference<Object>(callSiteArray[9].callCurrent(this, engine, "index.html"));
        public class _generateAll_closure4
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference indexTemplate;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _generateAll_closure4(Object _outerInstance, Object _thisObject, Reference indexTemplate) {
                Reference reference;
                CallSite[] callSiteArray = _generateAll_closure4.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.indexTemplate = reference = indexTemplate;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _generateAll_closure4.$getCallSiteArray();
                return callSiteArray[0].call(it, callSiteArray[1].call(this.indexTemplate.get(), ScriptBytecodeAdapter.createMap(new Object[]{"title", callSiteArray[2].callGetProperty(callSiteArray[3].callGetProperty(DocGenerator.class))})));
            }

            public Object getIndexTemplate() {
                CallSite[] callSiteArray = _generateAll_closure4.$getCallSiteArray();
                return this.indexTemplate.get();
            }

            public Object doCall() {
                CallSite[] callSiteArray = _generateAll_closure4.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _generateAll_closure4.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "leftShift";
                stringArray[1] = "make";
                stringArray[2] = "title";
                stringArray[3] = "CONFIG";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[4];
                _generateAll_closure4.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_generateAll_closure4.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _generateAll_closure4.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[10].call(callSiteArray[11].callConstructor(File.class, this.outputDir, "index.html"), new _generateAll_closure4(this, this, indexTemplate));
        Reference<Object> overviewTemplate = new Reference<Object>(callSiteArray[12].callCurrent(this, engine, "overview-summary.html"));
        public class _generateAll_closure5
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference overviewTemplate;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _generateAll_closure5(Object _outerInstance, Object _thisObject, Reference overviewTemplate) {
                Reference reference;
                CallSite[] callSiteArray = _generateAll_closure5.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.overviewTemplate = reference = overviewTemplate;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _generateAll_closure5.$getCallSiteArray();
                return callSiteArray[0].call(it, callSiteArray[1].call(this.overviewTemplate.get(), ScriptBytecodeAdapter.createMap(new Object[]{"title", callSiteArray[2].callGetProperty(callSiteArray[3].callGetProperty(DocGenerator.class))})));
            }

            public Object getOverviewTemplate() {
                CallSite[] callSiteArray = _generateAll_closure5.$getCallSiteArray();
                return this.overviewTemplate.get();
            }

            public Object doCall() {
                CallSite[] callSiteArray = _generateAll_closure5.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _generateAll_closure5.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "leftShift";
                stringArray[1] = "make";
                stringArray[2] = "title";
                stringArray[3] = "CONFIG";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[4];
                _generateAll_closure5.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_generateAll_closure5.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _generateAll_closure5.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[13].call(callSiteArray[14].callConstructor(File.class, this.outputDir, "overview-summary.html"), new _generateAll_closure5(this, this, overviewTemplate));
        Reference<Object> overviewFrameTemplate = new Reference<Object>(callSiteArray[15].callCurrent(this, engine, "template.overview-frame.html"));
        public class _generateAll_closure6
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference overviewFrameTemplate;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _generateAll_closure6(Object _outerInstance, Object _thisObject, Reference overviewFrameTemplate) {
                Reference reference;
                CallSite[] callSiteArray = _generateAll_closure6.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.overviewFrameTemplate = reference = overviewFrameTemplate;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _generateAll_closure6.$getCallSiteArray();
                public class _closure22
                extends Closure
                implements GeneratedClosure {
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure22(Object _outerInstance, Object _thisObject) {
                        CallSite[] callSiteArray = _closure22.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure22.$getCallSiteArray();
                        return !DefaultTypeTransformation.booleanUnbox(callSiteArray[0].callGetProperty(it));
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure22.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure22.class) {
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
                        stringArray[0] = "primitive";
                        return new CallSiteArray(_closure22.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure22.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                Object docPackagesExceptPrimitiveType = callSiteArray[0].call(callSiteArray[1].callGetProperty(callSiteArray[2].callGroovyObjectGetProperty(this)), new _closure22(this, this.getThisObject()));
                return callSiteArray[3].call(it, callSiteArray[4].call(this.overviewFrameTemplate.get(), ScriptBytecodeAdapter.createMap(new Object[]{"packages", docPackagesExceptPrimitiveType, "title", callSiteArray[5].callGetProperty(callSiteArray[6].callGetProperty(DocGenerator.class))})));
            }

            public Object getOverviewFrameTemplate() {
                CallSite[] callSiteArray = _generateAll_closure6.$getCallSiteArray();
                return this.overviewFrameTemplate.get();
            }

            public Object doCall() {
                CallSite[] callSiteArray = _generateAll_closure6.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _generateAll_closure6.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "findAll";
                stringArray[1] = "packages";
                stringArray[2] = "docSource";
                stringArray[3] = "leftShift";
                stringArray[4] = "make";
                stringArray[5] = "title";
                stringArray[6] = "CONFIG";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[7];
                _generateAll_closure6.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_generateAll_closure6.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _generateAll_closure6.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[16].call(callSiteArray[17].callConstructor(File.class, this.outputDir, "overview-frame.html"), new _generateAll_closure6(this, this, overviewFrameTemplate));
        public class _generateAll_closure7
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _generateAll_closure7(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _generateAll_closure7.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object writer) {
                Reference<Object> writer2 = new Reference<Object>(writer);
                CallSite[] callSiteArray = _generateAll_closure7.$getCallSiteArray();
                public class _closure23
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference writer;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure23(Object _outerInstance, Object _thisObject, Reference writer) {
                        Reference reference;
                        CallSite[] callSiteArray = _closure23.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.writer = reference = writer;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure23.$getCallSiteArray();
                        return callSiteArray[0].call(this.writer.get(), it);
                    }

                    public Object getWriter() {
                        CallSite[] callSiteArray = _closure23.$getCallSiteArray();
                        return this.writer.get();
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure23.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure23.class) {
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
                        stringArray[0] = "println";
                        return new CallSiteArray(_closure23.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure23.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                return callSiteArray[0].call(ScriptBytecodeAdapter.getPropertySpreadSafe(_generateAll_closure7.class, callSiteArray[1].callGetProperty(callSiteArray[2].callGroovyObjectGetProperty(this)), "name"), new _closure23(this, this.getThisObject(), writer2));
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _generateAll_closure7.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "each";
                stringArray[1] = "packages";
                stringArray[2] = "docSource";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[3];
                _generateAll_closure7.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_generateAll_closure7.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _generateAll_closure7.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[18].call(callSiteArray[19].callConstructor(File.class, this.outputDir, "package-list"), new _generateAll_closure7(this, this));
        Reference<Object> allClassesTemplate = new Reference<Object>(callSiteArray[20].callCurrent(this, engine, "template.allclasses-frame.html"));
        public class _generateAll_closure8
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference allClassesTemplate;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _generateAll_closure8(Object _outerInstance, Object _thisObject, Reference allClassesTemplate) {
                Reference reference;
                CallSite[] callSiteArray = _generateAll_closure8.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.allClassesTemplate = reference = allClassesTemplate;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _generateAll_closure8.$getCallSiteArray();
                return callSiteArray[0].call(it, callSiteArray[1].call(this.allClassesTemplate.get(), ScriptBytecodeAdapter.createMap(new Object[]{"docTypes", callSiteArray[2].callGetProperty(callSiteArray[3].callGroovyObjectGetProperty(this)), "title", callSiteArray[4].callGetProperty(callSiteArray[5].callGetProperty(DocGenerator.class))})));
            }

            public Object getAllClassesTemplate() {
                CallSite[] callSiteArray = _generateAll_closure8.$getCallSiteArray();
                return this.allClassesTemplate.get();
            }

            public Object doCall() {
                CallSite[] callSiteArray = _generateAll_closure8.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _generateAll_closure8.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "leftShift";
                stringArray[1] = "make";
                stringArray[2] = "allDocTypes";
                stringArray[3] = "docSource";
                stringArray[4] = "title";
                stringArray[5] = "CONFIG";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[6];
                _generateAll_closure8.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_generateAll_closure8.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _generateAll_closure8.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[21].call(callSiteArray[22].callConstructor(File.class, this.outputDir, "allclasses-frame.html"), new _generateAll_closure8(this, this, allClassesTemplate));
        Reference<Object> packageFrameTemplate = new Reference<Object>(callSiteArray[23].callCurrent(this, engine, "template.package-frame.html"));
        Reference<Object> packageSummaryTemplate = new Reference<Object>(callSiteArray[24].callCurrent(this, engine, "template.package-summary.html"));
        public class _generateAll_closure9
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference packageFrameTemplate;
            private /* synthetic */ Reference packageSummaryTemplate;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _generateAll_closure9(Object _outerInstance, Object _thisObject, Reference packageFrameTemplate, Reference packageSummaryTemplate) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _generateAll_closure9.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.packageFrameTemplate = reference2 = packageFrameTemplate;
                this.packageSummaryTemplate = reference = packageSummaryTemplate;
            }

            public Object doCall(DocPackage docPackage) {
                Reference<DocPackage> docPackage2 = new Reference<DocPackage>(docPackage);
                CallSite[] callSiteArray = _generateAll_closure9.$getCallSiteArray();
                Object dir = callSiteArray[0].call(DocUtil.class, callSiteArray[1].callGroovyObjectGetProperty(this), callSiteArray[2].callGetProperty(docPackage2.get()));
                public class _closure24
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference packageFrameTemplate;
                    private /* synthetic */ Reference docPackage;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure24(Object _outerInstance, Object _thisObject, Reference packageFrameTemplate, Reference docPackage) {
                        Reference reference;
                        Reference reference2;
                        CallSite[] callSiteArray = _closure24.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.packageFrameTemplate = reference2 = packageFrameTemplate;
                        this.docPackage = reference = docPackage;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure24.$getCallSiteArray();
                        return callSiteArray[0].call(it, callSiteArray[1].call(this.packageFrameTemplate.get(), ScriptBytecodeAdapter.createMap(new Object[]{"docPackage", this.docPackage.get(), "title", callSiteArray[2].callGetProperty(callSiteArray[3].callGroovyObjectGetProperty(this))})));
                    }

                    public Object getPackageFrameTemplate() {
                        CallSite[] callSiteArray = _closure24.$getCallSiteArray();
                        return this.packageFrameTemplate.get();
                    }

                    public DocPackage getDocPackage() {
                        CallSite[] callSiteArray = _closure24.$getCallSiteArray();
                        return (DocPackage)ScriptBytecodeAdapter.castToType(this.docPackage.get(), DocPackage.class);
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure24.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure24.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "leftShift";
                        stringArray[1] = "make";
                        stringArray[2] = "title";
                        stringArray[3] = "CONFIG";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[4];
                        _closure24.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_closure24.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure24.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                callSiteArray[3].call(callSiteArray[4].callConstructor(File.class, dir, "package-frame.html"), new _closure24(this, this.getThisObject(), this.packageFrameTemplate, docPackage2));
                public class _closure25
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference packageSummaryTemplate;
                    private /* synthetic */ Reference docPackage;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure25(Object _outerInstance, Object _thisObject, Reference packageSummaryTemplate, Reference docPackage) {
                        Reference reference;
                        Reference reference2;
                        CallSite[] callSiteArray = _closure25.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.packageSummaryTemplate = reference2 = packageSummaryTemplate;
                        this.docPackage = reference = docPackage;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure25.$getCallSiteArray();
                        return callSiteArray[0].call(it, callSiteArray[1].call(this.packageSummaryTemplate.get(), ScriptBytecodeAdapter.createMap(new Object[]{"docPackage", this.docPackage.get(), "title", callSiteArray[2].callGetProperty(callSiteArray[3].callGroovyObjectGetProperty(this))})));
                    }

                    public Object getPackageSummaryTemplate() {
                        CallSite[] callSiteArray = _closure25.$getCallSiteArray();
                        return this.packageSummaryTemplate.get();
                    }

                    public DocPackage getDocPackage() {
                        CallSite[] callSiteArray = _closure25.$getCallSiteArray();
                        return (DocPackage)ScriptBytecodeAdapter.castToType(this.docPackage.get(), DocPackage.class);
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure25.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure25.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "leftShift";
                        stringArray[1] = "make";
                        stringArray[2] = "title";
                        stringArray[3] = "CONFIG";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[4];
                        _closure25.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_closure25.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure25.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                return callSiteArray[5].call(callSiteArray[6].callConstructor(File.class, dir, "package-summary.html"), new _closure25(this, this.getThisObject(), this.packageSummaryTemplate, docPackage2));
            }

            public Object call(DocPackage docPackage) {
                Reference<DocPackage> docPackage2 = new Reference<DocPackage>(docPackage);
                CallSite[] callSiteArray = _generateAll_closure9.$getCallSiteArray();
                return callSiteArray[7].callCurrent((GroovyObject)this, docPackage2.get());
            }

            public Object getPackageFrameTemplate() {
                CallSite[] callSiteArray = _generateAll_closure9.$getCallSiteArray();
                return this.packageFrameTemplate.get();
            }

            public Object getPackageSummaryTemplate() {
                CallSite[] callSiteArray = _generateAll_closure9.$getCallSiteArray();
                return this.packageSummaryTemplate.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _generateAll_closure9.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "createPackageDirectory";
                stringArray[1] = "outputDir";
                stringArray[2] = "name";
                stringArray[3] = "withWriter";
                stringArray[4] = "<$constructor$>";
                stringArray[5] = "withWriter";
                stringArray[6] = "<$constructor$>";
                stringArray[7] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[8];
                _generateAll_closure9.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_generateAll_closure9.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _generateAll_closure9.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[25].call(callSiteArray[26].callGetProperty(this.docSource), new _generateAll_closure9(this, this, packageFrameTemplate, packageSummaryTemplate));
        Reference<Object> classTemplate = new Reference<Object>(callSiteArray[27].callCurrent(this, engine, "template.class.html"));
        public class _generateAll_closure10
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference classTemplate;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _generateAll_closure10(Object _outerInstance, Object _thisObject, Reference classTemplate) {
                Reference reference;
                CallSite[] callSiteArray = _generateAll_closure10.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.classTemplate = reference = classTemplate;
            }

            public Object doCall(DocType docType) {
                Reference<DocType> docType2 = new Reference<DocType>(docType);
                CallSite[] callSiteArray = _generateAll_closure10.$getCallSiteArray();
                Object dir = callSiteArray[0].call(DocUtil.class, callSiteArray[1].callGroovyObjectGetProperty(this), callSiteArray[2].callGetProperty(docType2.get()));
                public class _closure26
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference classTemplate;
                    private /* synthetic */ Reference docType;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure26(Object _outerInstance, Object _thisObject, Reference classTemplate, Reference docType) {
                        Reference reference;
                        Reference reference2;
                        CallSite[] callSiteArray = _closure26.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.classTemplate = reference2 = classTemplate;
                        this.docType = reference = docType;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure26.$getCallSiteArray();
                        return callSiteArray[0].call(it, callSiteArray[1].call(this.classTemplate.get(), ScriptBytecodeAdapter.createMap(new Object[]{"docType", this.docType.get(), "title", callSiteArray[2].callGetProperty(callSiteArray[3].callGroovyObjectGetProperty(this))})));
                    }

                    public Object getClassTemplate() {
                        CallSite[] callSiteArray = _closure26.$getCallSiteArray();
                        return this.classTemplate.get();
                    }

                    public DocType getDocType() {
                        CallSite[] callSiteArray = _closure26.$getCallSiteArray();
                        return (DocType)ScriptBytecodeAdapter.castToType(this.docType.get(), DocType.class);
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure26.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure26.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "leftShift";
                        stringArray[1] = "make";
                        stringArray[2] = "title";
                        stringArray[3] = "CONFIG";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[4];
                        _closure26.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_closure26.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure26.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                return callSiteArray[3].call(callSiteArray[4].callConstructor(File.class, dir, callSiteArray[5].call(callSiteArray[6].callGetProperty(docType2.get()), ".html")), new _closure26(this, this.getThisObject(), this.classTemplate, docType2));
            }

            public Object call(DocType docType) {
                Reference<DocType> docType2 = new Reference<DocType>(docType);
                CallSite[] callSiteArray = _generateAll_closure10.$getCallSiteArray();
                return callSiteArray[7].callCurrent((GroovyObject)this, docType2.get());
            }

            public Object getClassTemplate() {
                CallSite[] callSiteArray = _generateAll_closure10.$getCallSiteArray();
                return this.classTemplate.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _generateAll_closure10.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "createPackageDirectory";
                stringArray[1] = "outputDir";
                stringArray[2] = "packageName";
                stringArray[3] = "withWriter";
                stringArray[4] = "<$constructor$>";
                stringArray[5] = "plus";
                stringArray[6] = "simpleClassName";
                stringArray[7] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[8];
                _generateAll_closure10.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_generateAll_closure10.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _generateAll_closure10.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[28].call(callSiteArray[29].callGetProperty(this.docSource), new _generateAll_closure10(this, this, classTemplate));
        Reference<Object> indexAllTemplate = new Reference<Object>(callSiteArray[30].callCurrent(this, engine, "template.index-all.html"));
        public class _generateAll_closure11
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference indexAllTemplate;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _generateAll_closure11(Object _outerInstance, Object _thisObject, Reference indexAllTemplate) {
                Reference reference;
                CallSite[] callSiteArray = _generateAll_closure11.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.indexAllTemplate = reference = indexAllTemplate;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _generateAll_closure11.$getCallSiteArray();
                return callSiteArray[0].call(it, callSiteArray[1].call(this.indexAllTemplate.get(), ScriptBytecodeAdapter.createMap(new Object[]{"indexMap", callSiteArray[2].callCurrent(this), "title", callSiteArray[3].callGetProperty(callSiteArray[4].callGetProperty(DocGenerator.class))})));
            }

            public Object getIndexAllTemplate() {
                CallSite[] callSiteArray = _generateAll_closure11.$getCallSiteArray();
                return this.indexAllTemplate.get();
            }

            public Object doCall() {
                CallSite[] callSiteArray = _generateAll_closure11.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _generateAll_closure11.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "leftShift";
                stringArray[1] = "make";
                stringArray[2] = "generateIndexMap";
                stringArray[3] = "title";
                stringArray[4] = "CONFIG";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[5];
                _generateAll_closure11.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_generateAll_closure11.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _generateAll_closure11.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[31].call(callSiteArray[32].callConstructor(File.class, this.outputDir, "index-all.html"), new _generateAll_closure11(this, this, indexAllTemplate));
        public class _generateAll_closure12
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _generateAll_closure12(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _generateAll_closure12.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(String resource) {
                CallSite[] callSiteArray = _generateAll_closure12.$getCallSiteArray();
                return callSiteArray[0].call(callSiteArray[1].callConstructor(File.class, callSiteArray[2].callGroovyObjectGetProperty(this), resource), callSiteArray[3].callGetProperty(callSiteArray[4].call(callSiteArray[5].callCurrent(this), resource)));
            }

            public Object call(String resource) {
                CallSite[] callSiteArray = _generateAll_closure12.$getCallSiteArray();
                if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    return callSiteArray[6].callCurrent((GroovyObject)this, resource);
                }
                return this.doCall(resource);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _generateAll_closure12.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "leftShift";
                stringArray[1] = "<$constructor$>";
                stringArray[2] = "outputDir";
                stringArray[3] = "bytes";
                stringArray[4] = "getResource";
                stringArray[5] = "getClass";
                stringArray[6] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[7];
                _generateAll_closure12.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_generateAll_closure12.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _generateAll_closure12.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[33].call((Object)ScriptBytecodeAdapter.createList(new Object[]{"groovy.ico", "stylesheet.css"}), new _generateAll_closure12(this, this));
    }

    private Template createTemplate(TemplateEngine templateEngine, String resourceFile) {
        CallSite[] callSiteArray = DocGenerator.$getCallSiteArray();
        Object resourceUrl = callSiteArray[34].call(callSiteArray[35].callCurrent(this), resourceFile);
        return (Template)ScriptBytecodeAdapter.castToType(callSiteArray[36].call((Object)templateEngine, callSiteArray[37].callGetProperty(resourceUrl)), Template.class);
    }

    private Map generateIndexMap() {
        CallSite[] callSiteArray = DocGenerator.$getCallSiteArray();
        Reference<List> indexItems = new Reference<List>(ScriptBytecodeAdapter.createList(new Object[0]));
        public class _generateIndexMap_closure13
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference indexItems;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _generateIndexMap_closure13(Object _outerInstance, Object _thisObject, Reference indexItems) {
                Reference reference;
                CallSite[] callSiteArray = _generateIndexMap_closure13.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.indexItems = reference = indexItems;
            }

            public Object doCall(DocType docType) {
                Reference<DocType> docType2 = new Reference<DocType>(docType);
                CallSite[] callSiteArray = _generateIndexMap_closure13.$getCallSiteArray();
                callSiteArray[0].call(this.indexItems.get(), ScriptBytecodeAdapter.createMap(new Object[]{"index", callSiteArray[1].call(callSiteArray[2].call(callSiteArray[3].callGetProperty(docType2.get())), 0), "docType", docType2.get(), "sortKey", callSiteArray[4].callGetProperty(docType2.get())}));
                public class _closure27
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference indexItems;
                    private /* synthetic */ Reference docType;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure27(Object _outerInstance, Object _thisObject, Reference indexItems, Reference docType) {
                        Reference reference;
                        Reference reference2;
                        CallSite[] callSiteArray = _closure27.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.indexItems = reference2 = indexItems;
                        this.docType = reference = docType;
                    }

                    public Object doCall(DocMethod docMethod) {
                        CallSite[] callSiteArray = _closure27.$getCallSiteArray();
                        return callSiteArray[0].call(this.indexItems.get(), ScriptBytecodeAdapter.createMap(new Object[]{"index", callSiteArray[1].call(callSiteArray[2].call(callSiteArray[3].callGetProperty(callSiteArray[4].callGetProperty(docMethod))), 0), "docType", this.docType.get(), "docMethod", docMethod, "sortKey", callSiteArray[5].callGetProperty(docMethod)}));
                    }

                    public Object call(DocMethod docMethod) {
                        CallSite[] callSiteArray = _closure27.$getCallSiteArray();
                        return callSiteArray[6].callCurrent((GroovyObject)this, docMethod);
                    }

                    public Object getIndexItems() {
                        CallSite[] callSiteArray = _closure27.$getCallSiteArray();
                        return this.indexItems.get();
                    }

                    public DocType getDocType() {
                        CallSite[] callSiteArray = _closure27.$getCallSiteArray();
                        return (DocType)ScriptBytecodeAdapter.castToType(this.docType.get(), DocType.class);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure27.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "leftShift";
                        stringArray[1] = "getAt";
                        stringArray[2] = "capitalize";
                        stringArray[3] = "name";
                        stringArray[4] = "javaMethod";
                        stringArray[5] = "sortKey";
                        stringArray[6] = "doCall";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[7];
                        _closure27.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_closure27.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure27.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                return callSiteArray[5].call(callSiteArray[6].callGetProperty(docType2.get()), new _closure27(this, this.getThisObject(), this.indexItems, docType2));
            }

            public Object call(DocType docType) {
                Reference<DocType> docType2 = new Reference<DocType>(docType);
                CallSite[] callSiteArray = _generateIndexMap_closure13.$getCallSiteArray();
                return callSiteArray[7].callCurrent((GroovyObject)this, docType2.get());
            }

            public Object getIndexItems() {
                CallSite[] callSiteArray = _generateIndexMap_closure13.$getCallSiteArray();
                return this.indexItems.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _generateIndexMap_closure13.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "leftShift";
                stringArray[1] = "getAt";
                stringArray[2] = "capitalize";
                stringArray[3] = "simpleClassName";
                stringArray[4] = "sortKey";
                stringArray[5] = "each";
                stringArray[6] = "docMethods";
                stringArray[7] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[8];
                _generateIndexMap_closure13.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_generateIndexMap_closure13.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _generateIndexMap_closure13.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[38].call(callSiteArray[39].callGetProperty(this.docSource), new _generateIndexMap_closure13(this, this, indexItems));
        public class _generateIndexMap_closure14
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _generateIndexMap_closure14(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _generateIndexMap_closure14.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _generateIndexMap_closure14.$getCallSiteArray();
                return callSiteArray[0].callConstructor(TreeSet.class, callSiteArray[1].callGetProperty(DocGenerator.class));
            }

            public Object doCall() {
                CallSite[] callSiteArray = _generateIndexMap_closure14.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _generateIndexMap_closure14.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "<$constructor$>";
                stringArray[1] = "SORT_KEY_COMPARATOR";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _generateIndexMap_closure14.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_generateIndexMap_closure14.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _generateIndexMap_closure14.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        Object indexMap = callSiteArray[40].call(callSiteArray[41].callConstructor(TreeMap.class), new _generateIndexMap_closure14(this, this));
        Object indexItem = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[42].call(indexItems.get()), Iterator.class);
        while (iterator.hasNext()) {
            indexItem = iterator.next();
            callSiteArray[43].call(callSiteArray[44].call(indexMap, callSiteArray[45].call(indexItem, "index")), (Object)indexItem);
        }
        return (Map)ScriptBytecodeAdapter.castToType(indexMap, Map.class);
    }

    public static void main(String ... args) {
        CallSite[] callSiteArray = DocGenerator.$getCallSiteArray();
        Object cli = callSiteArray[46].callConstructor(CliBuilder.class, ScriptBytecodeAdapter.createMap(new Object[]{"usage", "DocGenerator [options] [sourcefiles]", "posix", false}));
        callSiteArray[47].call(cli, ScriptBytecodeAdapter.createMap(new Object[]{"longOpt", "help"}), callSiteArray[48].call((Object)messages, "cli.option.help.description"));
        callSiteArray[49].call(cli, ScriptBytecodeAdapter.createMap(new Object[]{"longOpt", "version"}), callSiteArray[50].call((Object)messages, "cli.option.version.description"));
        callSiteArray[51].call(cli, ScriptBytecodeAdapter.createMap(new Object[]{"longOpt", "outputDir", "args", 1, "argName", "path"}), callSiteArray[52].call((Object)messages, "cli.option.output.dir.description"));
        callSiteArray[53].call(cli, ScriptBytecodeAdapter.createMap(new Object[]{"longOpt", "title", "args", 1, "argName", "text"}), callSiteArray[54].call((Object)messages, "cli.option.title.description"));
        callSiteArray[55].call(cli, ScriptBytecodeAdapter.createMap(new Object[]{"args", 2, "valueSeparator", "=", "argName", "comma-separated-package-prefixes=url"}), callSiteArray[56].call((Object)messages, "cli.option.link.patterns.description"));
        Object options = callSiteArray[57].call(cli, (Object)args);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[58].callGetProperty(options))) {
            callSiteArray[59].call(cli);
            return;
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[60].callGetProperty(options)) && ScriptBytecodeAdapter.compareEqual(callSiteArray[61].call(callSiteArray[62].call(callSiteArray[63].callGetProperty(options)), 2), 1)) {
            throw (Throwable)callSiteArray[64].callConstructor(IllegalArgumentException.class, "Links should be specified in pattern=url pairs");
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[65].callGetProperty(options))) {
            callSiteArray[66].callStatic(DocGenerator.class, callSiteArray[67].call(messages, "cli.info.version", callSiteArray[68].callGetProperty(GroovySystem.class)));
            return;
        }
        Object start = callSiteArray[69].call(System.class);
        Object object = callSiteArray[71].callGetProperty(options);
        Object outputDir = callSiteArray[70].callConstructor(File.class, DefaultTypeTransformation.booleanUnbox(object) ? object : "target/html/groovy-jdk");
        callSiteArray[72].call(outputDir);
        Object object2 = callSiteArray[73].callGetProperty(options);
        Object object3 = DefaultTypeTransformation.booleanUnbox(object2) ? object2 : "Groovy JDK";
        ScriptBytecodeAdapter.setProperty(object3, null, CONFIG, "title");
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[74].callGetProperty(options))) {
            public class _main_closure15
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _main_closure15(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _main_closure15.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(Object prefixes, Object url) {
                    Reference<Object> url2 = new Reference<Object>(url);
                    CallSite[] callSiteArray = _main_closure15.$getCallSiteArray();
                    public class _closure28
                    extends Closure
                    implements GeneratedClosure {
                        private /* synthetic */ Reference url;
                        private static /* synthetic */ ClassInfo $staticClassInfo;
                        public static transient /* synthetic */ boolean __$stMC;
                        private static /* synthetic */ SoftReference $callSiteArray;

                        public _closure28(Object _outerInstance, Object _thisObject, Reference url) {
                            Reference reference;
                            CallSite[] callSiteArray = _closure28.$getCallSiteArray();
                            super(_outerInstance, _thisObject);
                            this.url = reference = url;
                        }

                        public Object doCall(Object it) {
                            CallSite[] callSiteArray = _closure28.$getCallSiteArray();
                            return ScriptBytecodeAdapter.createList(new Object[]{it, this.url.get()});
                        }

                        public Object getUrl() {
                            CallSite[] callSiteArray = _closure28.$getCallSiteArray();
                            return this.url.get();
                        }

                        public Object doCall() {
                            CallSite[] callSiteArray = _closure28.$getCallSiteArray();
                            return this.doCall(null);
                        }

                        protected /* synthetic */ MetaClass $getStaticMetaClass() {
                            if (this.getClass() != _closure28.class) {
                                return ScriptBytecodeAdapter.initMetaClass(this);
                            }
                            ClassInfo classInfo = $staticClassInfo;
                            if (classInfo == null) {
                                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                            }
                            return classInfo.getMetaClass();
                        }

                        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                            String[] stringArray = new String[]{};
                            return new CallSiteArray(_closure28.class, stringArray);
                        }

                        private static /* synthetic */ CallSite[] $getCallSiteArray() {
                            CallSiteArray callSiteArray;
                            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                callSiteArray = _closure28.$createCallSiteArray();
                                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                            }
                            return callSiteArray.array;
                        }
                    }
                    return callSiteArray[0].call(callSiteArray[1].call(prefixes, ","), new _closure28(this, this.getThisObject(), url2));
                }

                public Object call(Object prefixes, Object url) {
                    Reference<Object> url2 = new Reference<Object>(url);
                    CallSite[] callSiteArray = _main_closure15.$getCallSiteArray();
                    return callSiteArray[2].callCurrent(this, prefixes, url2.get());
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _main_closure15.class) {
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
                    stringArray[1] = "tokenize";
                    stringArray[2] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[3];
                    _main_closure15.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_main_closure15.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _main_closure15.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            Object object4 = callSiteArray[75].call(callSiteArray[76].call(callSiteArray[77].call(callSiteArray[78].callGetProperty(options), 2), new _main_closure15(DocGenerator.class, DocGenerator.class)));
            ScriptBytecodeAdapter.setProperty(object4, null, CONFIG, "links");
        }
        Object object5 = callSiteArray[79].callGetProperty(Locale.class);
        ScriptBytecodeAdapter.setProperty(object5, null, CONFIG, "locale");
        public class _main_closure16
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _main_closure16(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _main_closure16.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _main_closure16.$getCallSiteArray();
                return callSiteArray[0].call(DocUtil.class, it);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _main_closure16.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _main_closure16.class) {
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
                stringArray[0] = "sourceFileOf";
                return new CallSiteArray(_main_closure16.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _main_closure16.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        Reference<Object> srcFiles = new Reference<Object>(callSiteArray[80].call(callSiteArray[81].call(options), new _main_closure16(DocGenerator.class, DocGenerator.class)));
        try {
            public class _main_closure17
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference srcFiles;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _main_closure17(Object _outerInstance, Object _thisObject, Reference srcFiles) {
                    Reference reference;
                    CallSite[] callSiteArray = _main_closure17.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.srcFiles = reference = srcFiles;
                }

                public Object doCall(Object aClass) {
                    CallSite[] callSiteArray = _main_closure17.$getCallSiteArray();
                    Object className = callSiteArray[0].call(callSiteArray[1].callGetProperty(aClass), "\\$.*", "");
                    Reference<Object> additionalFile = new Reference<Object>(callSiteArray[2].call(DocUtil.class, className));
                    public class _closure29
                    extends Closure
                    implements GeneratedClosure {
                        private /* synthetic */ Reference additionalFile;
                        private static /* synthetic */ ClassInfo $staticClassInfo;
                        public static transient /* synthetic */ boolean __$stMC;
                        private static /* synthetic */ SoftReference $callSiteArray;

                        public _closure29(Object _outerInstance, Object _thisObject, Reference additionalFile) {
                            Reference reference;
                            CallSite[] callSiteArray = _closure29.$getCallSiteArray();
                            super(_outerInstance, _thisObject);
                            this.additionalFile = reference = additionalFile;
                        }

                        public Object doCall(Object it) {
                            CallSite[] callSiteArray = _closure29.$getCallSiteArray();
                            return ScriptBytecodeAdapter.compareNotEqual(callSiteArray[0].callGetProperty(it), callSiteArray[1].callGetProperty(this.additionalFile.get()));
                        }

                        public Object getAdditionalFile() {
                            CallSite[] callSiteArray = _closure29.$getCallSiteArray();
                            return this.additionalFile.get();
                        }

                        public Object doCall() {
                            CallSite[] callSiteArray = _closure29.$getCallSiteArray();
                            return this.doCall(null);
                        }

                        protected /* synthetic */ MetaClass $getStaticMetaClass() {
                            if (this.getClass() != _closure29.class) {
                                return ScriptBytecodeAdapter.initMetaClass(this);
                            }
                            ClassInfo classInfo = $staticClassInfo;
                            if (classInfo == null) {
                                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                            }
                            return classInfo.getMetaClass();
                        }

                        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                            stringArray[0] = "canonicalPath";
                            stringArray[1] = "canonicalPath";
                        }

                        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                            String[] stringArray = new String[2];
                            _closure29.$createCallSiteArray_1(stringArray);
                            return new CallSiteArray(_closure29.class, stringArray);
                        }

                        private static /* synthetic */ CallSite[] $getCallSiteArray() {
                            CallSiteArray callSiteArray;
                            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                callSiteArray = _closure29.$createCallSiteArray();
                                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                            }
                            return callSiteArray.array;
                        }
                    }
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[3].call(this.srcFiles.get(), new _closure29(this, this.getThisObject(), additionalFile)))) {
                        return callSiteArray[4].call(this.srcFiles.get(), additionalFile.get());
                    }
                    return null;
                }

                public Object getSrcFiles() {
                    CallSite[] callSiteArray = _main_closure17.$getCallSiteArray();
                    return this.srcFiles.get();
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _main_closure17.class) {
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
                    stringArray[1] = "name";
                    stringArray[2] = "sourceFileOf";
                    stringArray[3] = "every";
                    stringArray[4] = "leftShift";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[5];
                    _main_closure17.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_main_closure17.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _main_closure17.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[82].call(callSiteArray[83].callGetProperty(DefaultGroovyMethods.class), new _main_closure17(DocGenerator.class, DocGenerator.class, srcFiles));
        }
        catch (MissingPropertyException e) {
            callSiteArray[84].call(log, callSiteArray[85].callGetProperty(e), e);
        }
        Object docGen = callSiteArray[86].callConstructor(DocGenerator.class, srcFiles.get(), outputDir);
        callSiteArray[87].call(docGen);
        Object end = callSiteArray[88].call(System.class);
        callSiteArray[89].call((Object)log, new GStringImpl(new Object[]{callSiteArray[90].call(end, start)}, new String[]{"Done. Took ", " milliseconds."}));
    }

    public /* synthetic */ Object this$dist$invoke$1(String name, Object args) {
        CallSite[] callSiteArray = DocGenerator.$getCallSiteArray();
        return ScriptBytecodeAdapter.invokeMethodOnCurrentN(DocGenerator.class, this, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
    }

    public /* synthetic */ void this$dist$set$1(String name, Object value) {
        CallSite[] callSiteArray = DocGenerator.$getCallSiteArray();
        Object object = value;
        ScriptBytecodeAdapter.setGroovyObjectProperty(object, DocGenerator.class, this, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
    }

    public /* synthetic */ Object this$dist$get$1(String name) {
        CallSite[] callSiteArray = DocGenerator.$getCallSiteArray();
        return ScriptBytecodeAdapter.getGroovyObjectProperty(DocGenerator.class, this, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != DocGenerator.class) {
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
        Comparator comparator;
        Object object = DocGenerator.$getCallSiteArray()[91].callConstructor(MessageSource.class, DocGenerator.class);
        messages = (MessageSource)ScriptBytecodeAdapter.castToType(object, MessageSource.class);
        Object object2 = DocGenerator.$getCallSiteArray()[92].call(Logger.class, DocGenerator.class);
        log = (Logger)ScriptBytecodeAdapter.castToType(object2, Logger.class);
        public class __clinit__closure18
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public __clinit__closure18(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = __clinit__closure18.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object a, Object b) {
                CallSite[] callSiteArray = __clinit__closure18.$getCallSiteArray();
                return callSiteArray[0].call(callSiteArray[1].callGetProperty(a), callSiteArray[2].callGetProperty(b));
            }

            public Object call(Object a, Object b) {
                CallSite[] callSiteArray = __clinit__closure18.$getCallSiteArray();
                return callSiteArray[3].callCurrent(this, a, b);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != __clinit__closure18.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "compareTo";
                stringArray[1] = "sortKey";
                stringArray[2] = "sortKey";
                stringArray[3] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[4];
                __clinit__closure18.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(__clinit__closure18.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = __clinit__closure18.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        SORT_KEY_COMPARATOR = comparator = (Comparator)ScriptBytecodeAdapter.asType(ScriptBytecodeAdapter.createMap(new Object[]{"compare", new __clinit__closure18(DocGenerator.class, DocGenerator.class)}), Comparator.class);
        Object object3 = DocGenerator.$getCallSiteArray()[93].callConstructor(ConcurrentHashMap.class);
        CONFIG = (Map)ScriptBytecodeAdapter.castToType(object3, Map.class);
    }

    public List<File> getSourceFiles() {
        return this.sourceFiles;
    }

    public void setSourceFiles(List<File> list) {
        this.sourceFiles = list;
    }

    public File getOutputDir() {
        return this.outputDir;
    }

    public void setOutputDir(File file) {
        this.outputDir = file;
    }

    public DocSource getDocSource() {
        return this.docSource;
    }

    public void setDocSource(DocSource docSource) {
        this.docSource = docSource;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "parseSource";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "each";
        stringArray[3] = "collectMany";
        stringArray[4] = "sources";
        stringArray[5] = "<$constructor$>";
        stringArray[6] = "each";
        stringArray[7] = "populateInheritedMethods";
        stringArray[8] = "<$constructor$>";
        stringArray[9] = "createTemplate";
        stringArray[10] = "withWriter";
        stringArray[11] = "<$constructor$>";
        stringArray[12] = "createTemplate";
        stringArray[13] = "withWriter";
        stringArray[14] = "<$constructor$>";
        stringArray[15] = "createTemplate";
        stringArray[16] = "withWriter";
        stringArray[17] = "<$constructor$>";
        stringArray[18] = "withWriter";
        stringArray[19] = "<$constructor$>";
        stringArray[20] = "createTemplate";
        stringArray[21] = "withWriter";
        stringArray[22] = "<$constructor$>";
        stringArray[23] = "createTemplate";
        stringArray[24] = "createTemplate";
        stringArray[25] = "each";
        stringArray[26] = "packages";
        stringArray[27] = "createTemplate";
        stringArray[28] = "each";
        stringArray[29] = "allDocTypes";
        stringArray[30] = "createTemplate";
        stringArray[31] = "withWriter";
        stringArray[32] = "<$constructor$>";
        stringArray[33] = "each";
        stringArray[34] = "getResource";
        stringArray[35] = "getClass";
        stringArray[36] = "createTemplate";
        stringArray[37] = "text";
        stringArray[38] = "each";
        stringArray[39] = "allDocTypes";
        stringArray[40] = "withDefault";
        stringArray[41] = "<$constructor$>";
        stringArray[42] = "iterator";
        stringArray[43] = "leftShift";
        stringArray[44] = "getAt";
        stringArray[45] = "getAt";
        stringArray[46] = "<$constructor$>";
        stringArray[47] = "help";
        stringArray[48] = "getAt";
        stringArray[49] = "_";
        stringArray[50] = "getAt";
        stringArray[51] = "o";
        stringArray[52] = "getAt";
        stringArray[53] = "title";
        stringArray[54] = "getAt";
        stringArray[55] = "link";
        stringArray[56] = "getAt";
        stringArray[57] = "parse";
        stringArray[58] = "help";
        stringArray[59] = "usage";
        stringArray[60] = "links";
        stringArray[61] = "mod";
        stringArray[62] = "size";
        stringArray[63] = "links";
        stringArray[64] = "<$constructor$>";
        stringArray[65] = "version";
        stringArray[66] = "println";
        stringArray[67] = "format";
        stringArray[68] = "version";
        stringArray[69] = "currentTimeMillis";
        stringArray[70] = "<$constructor$>";
        stringArray[71] = "outputDir";
        stringArray[72] = "mkdirs";
        stringArray[73] = "title";
        stringArray[74] = "links";
        stringArray[75] = "collectEntries";
        stringArray[76] = "collectMany";
        stringArray[77] = "collate";
        stringArray[78] = "links";
        stringArray[79] = "default";
        stringArray[80] = "collect";
        stringArray[81] = "arguments";
        stringArray[82] = "each";
        stringArray[83] = "ADDITIONAL_CLASSES";
        stringArray[84] = "error";
        stringArray[85] = "message";
        stringArray[86] = "<$constructor$>";
        stringArray[87] = "generateAll";
        stringArray[88] = "currentTimeMillis";
        stringArray[89] = "debug";
        stringArray[90] = "minus";
        stringArray[91] = "<$constructor$>";
        stringArray[92] = "create";
        stringArray[93] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[94];
        DocGenerator.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(DocGenerator.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = DocGenerator.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }

    static class DocSource
    implements GroovyObject {
        private SortedSet<DocPackage> packages;
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private transient /* synthetic */ MetaClass metaClass;
        private static /* synthetic */ SoftReference $callSiteArray;

        public DocSource() {
            MetaClass metaClass;
            CallSite[] callSiteArray = DocSource.$getCallSiteArray();
            Object object = callSiteArray[0].callConstructor(TreeSet.class, callSiteArray[1].callGetProperty(DocGenerator.class));
            this.packages = (SortedSet)ScriptBytecodeAdapter.castToType(object, SortedSet.class);
            this.metaClass = metaClass = this.$getStaticMetaClass();
        }

        public void add(Type type, JavaMethod javaMethod) {
            CallSite[] callSiteArray = DocSource.$getCallSiteArray();
            Reference<DocType> tempDocType = new Reference<DocType>((DocType)ScriptBytecodeAdapter.castToType(callSiteArray[2].callConstructor(DocType.class, ScriptBytecodeAdapter.createMap(new Object[]{"type", type})), DocType.class));
            public class _add_closure1
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference tempDocType;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _add_closure1(Object _outerInstance, Object _thisObject, Reference tempDocType) {
                    Reference reference;
                    CallSite[] callSiteArray = _add_closure1.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.tempDocType = reference = tempDocType;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _add_closure1.$getCallSiteArray();
                    return ScriptBytecodeAdapter.compareEqual(callSiteArray[0].callGetProperty(it), callSiteArray[1].callGetProperty(this.tempDocType.get()));
                }

                public DocType getTempDocType() {
                    CallSite[] callSiteArray = _add_closure1.$getCallSiteArray();
                    return (DocType)ScriptBytecodeAdapter.castToType(this.tempDocType.get(), DocType.class);
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _add_closure1.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _add_closure1.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "name";
                    stringArray[1] = "packageName";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[2];
                    _add_closure1.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_add_closure1.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _add_closure1.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            DocPackage aPackage = (DocPackage)ScriptBytecodeAdapter.castToType(callSiteArray[3].call(this.packages, new _add_closure1(this, this, tempDocType)), DocPackage.class);
            if (!DefaultTypeTransformation.booleanUnbox(aPackage)) {
                Object object = callSiteArray[4].callConstructor(DocPackage.class, ScriptBytecodeAdapter.createMap(new Object[]{"name", callSiteArray[5].callGetProperty(tempDocType.get())}));
                aPackage = (DocPackage)ScriptBytecodeAdapter.castToType(object, DocPackage.class);
                callSiteArray[6].call(this.packages, aPackage);
            }
            public class _add_closure2
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference tempDocType;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _add_closure2(Object _outerInstance, Object _thisObject, Reference tempDocType) {
                    Reference reference;
                    CallSite[] callSiteArray = _add_closure2.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.tempDocType = reference = tempDocType;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _add_closure2.$getCallSiteArray();
                    return ScriptBytecodeAdapter.compareEqual(callSiteArray[0].callGetProperty(it), callSiteArray[1].callGetProperty(this.tempDocType.get()));
                }

                public DocType getTempDocType() {
                    CallSite[] callSiteArray = _add_closure2.$getCallSiteArray();
                    return (DocType)ScriptBytecodeAdapter.castToType(this.tempDocType.get(), DocType.class);
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _add_closure2.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _add_closure2.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "fullyQualifiedClassName";
                    stringArray[1] = "fullyQualifiedClassName";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[2];
                    _add_closure2.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_add_closure2.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _add_closure2.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            DocType docType = (DocType)ScriptBytecodeAdapter.castToType(callSiteArray[7].call(callSiteArray[8].callGetProperty(aPackage), new _add_closure2(this, this, tempDocType)), DocType.class);
            if (!DefaultTypeTransformation.booleanUnbox(docType)) {
                DocType docType2;
                docType = docType2 = tempDocType.get();
                callSiteArray[9].call(callSiteArray[10].callGetProperty(aPackage), docType);
            }
            Object docMethod = callSiteArray[11].callConstructor(DocMethod.class, ScriptBytecodeAdapter.createMap(new Object[]{"declaringDocType", docType, "javaMethod", javaMethod}));
            callSiteArray[12].call(callSiteArray[13].callGetProperty(docType), docMethod);
        }

        public void populateInheritedMethods() {
            CallSite[] callSiteArray = DocSource.$getCallSiteArray();
            public class _populateInheritedMethods_closure3
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _populateInheritedMethods_closure3(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _populateInheritedMethods_closure3.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _populateInheritedMethods_closure3.$getCallSiteArray();
                    return ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[0].callGetProperty(it), it});
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _populateInheritedMethods_closure3.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _populateInheritedMethods_closure3.class) {
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
                    stringArray[0] = "fullyQualifiedClassName";
                    return new CallSiteArray(_populateInheritedMethods_closure3.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _populateInheritedMethods_closure3.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            Reference<Object> allTypes = new Reference<Object>(callSiteArray[14].call(callSiteArray[15].callGroovyObjectGetProperty(this), new _populateInheritedMethods_closure3(this, this)));
            public class _populateInheritedMethods_closure4
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference allTypes;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _populateInheritedMethods_closure4(Object _outerInstance, Object _thisObject, Reference allTypes) {
                    Reference reference;
                    CallSite[] callSiteArray = _populateInheritedMethods_closure4.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.allTypes = reference = allTypes;
                }

                public Object doCall(Object name, Object docType) {
                    CallSite[] callSiteArray = _populateInheritedMethods_closure4.$getCallSiteArray();
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(name, "[]")) || DefaultTypeTransformation.booleanUnbox(callSiteArray[1].call(name, "primitive-types"))) {
                        return null;
                    }
                    Type next = (Type)ScriptBytecodeAdapter.castToType(callSiteArray[2].callGetProperty(callSiteArray[3].callGetProperty(docType)), Type.class);
                    while (ScriptBytecodeAdapter.compareNotEqual(next, null)) {
                        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[4].call(callSiteArray[5].call(this.allTypes.get()), callSiteArray[6].callGetProperty(next)))) {
                            Object object = callSiteArray[7].callGetProperty(callSiteArray[8].call(this.allTypes.get(), callSiteArray[9].callGetProperty(next)));
                            callSiteArray[10].call(callSiteArray[11].callGetProperty(docType), callSiteArray[12].call(this.allTypes.get(), callSiteArray[13].callGetProperty(next)), object);
                        }
                        Object object = callSiteArray[14].callGetProperty(callSiteArray[15].callGetProperty(next));
                        next = (Type)ScriptBytecodeAdapter.castToType(object, Type.class);
                    }
                    Object remaining = callSiteArray[16].call(callSiteArray[17].callGetProperty(callSiteArray[18].callGetProperty(docType)));
                    while (!DefaultTypeTransformation.booleanUnbox(callSiteArray[19].call(remaining))) {
                        Object nextInt = callSiteArray[20].call(remaining, 0);
                        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[21].call(callSiteArray[22].call(this.allTypes.get()), callSiteArray[23].callGetProperty(nextInt)))) {
                            Object object = callSiteArray[24].callGetProperty(callSiteArray[25].call(this.allTypes.get(), callSiteArray[26].callGetProperty(nextInt)));
                            callSiteArray[27].call(callSiteArray[28].callGetProperty(docType), callSiteArray[29].call(this.allTypes.get(), callSiteArray[30].callGetProperty(nextInt)), object);
                        }
                        callSiteArray[31].call(remaining, callSiteArray[32].call(callSiteArray[33].callGetProperty(nextInt)));
                    }
                    return null;
                }

                public Object call(Object name, Object docType) {
                    CallSite[] callSiteArray = _populateInheritedMethods_closure4.$getCallSiteArray();
                    return callSiteArray[34].callCurrent(this, name, docType);
                }

                public Object getAllTypes() {
                    CallSite[] callSiteArray = _populateInheritedMethods_closure4.$getCallSiteArray();
                    return this.allTypes.get();
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _populateInheritedMethods_closure4.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "endsWith";
                    stringArray[1] = "startsWith";
                    stringArray[2] = "superClass";
                    stringArray[3] = "javaClass";
                    stringArray[4] = "contains";
                    stringArray[5] = "keySet";
                    stringArray[6] = "value";
                    stringArray[7] = "docMethods";
                    stringArray[8] = "getAt";
                    stringArray[9] = "value";
                    stringArray[10] = "putAt";
                    stringArray[11] = "inheritedMethods";
                    stringArray[12] = "getAt";
                    stringArray[13] = "value";
                    stringArray[14] = "superClass";
                    stringArray[15] = "javaClass";
                    stringArray[16] = "toList";
                    stringArray[17] = "implementedInterfaces";
                    stringArray[18] = "javaClass";
                    stringArray[19] = "isEmpty";
                    stringArray[20] = "remove";
                    stringArray[21] = "contains";
                    stringArray[22] = "keySet";
                    stringArray[23] = "fullyQualifiedName";
                    stringArray[24] = "docMethods";
                    stringArray[25] = "getAt";
                    stringArray[26] = "fullyQualifiedName";
                    stringArray[27] = "putAt";
                    stringArray[28] = "inheritedMethods";
                    stringArray[29] = "getAt";
                    stringArray[30] = "fullyQualifiedName";
                    stringArray[31] = "addAll";
                    stringArray[32] = "toList";
                    stringArray[33] = "implementedInterfaces";
                    stringArray[34] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[35];
                    _populateInheritedMethods_closure4.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_populateInheritedMethods_closure4.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _populateInheritedMethods_closure4.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[16].call(allTypes.get(), new _populateInheritedMethods_closure4(this, this, allTypes));
        }

        public SortedSet<DocType> getAllDocTypes() {
            CallSite[] callSiteArray = DocSource.$getCallSiteArray();
            Object allSet = callSiteArray[17].callConstructor(TreeSet.class, callSiteArray[18].callGetProperty(DocGenerator.class));
            public class _getAllDocTypes_closure5
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getAllDocTypes_closure5(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _getAllDocTypes_closure5.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _getAllDocTypes_closure5.$getCallSiteArray();
                    return callSiteArray[0].callGetProperty(it);
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _getAllDocTypes_closure5.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getAllDocTypes_closure5.class) {
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
                    stringArray[0] = "docTypes";
                    return new CallSiteArray(_getAllDocTypes_closure5.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getAllDocTypes_closure5.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[19].call(allSet, callSiteArray[20].call(this.packages, new _getAllDocTypes_closure5(this, this)));
            return (SortedSet)ScriptBytecodeAdapter.castToType(allSet, SortedSet.class);
        }

        public /* synthetic */ Object methodMissing(String name, Object args) {
            CallSite[] callSiteArray = DocSource.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(DocSource.class, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
            CallSite[] callSiteArray = DocSource.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(DocSource.class, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public /* synthetic */ void propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = DocSource.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = DocSource.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public /* synthetic */ Object propertyMissing(String name) {
            CallSite[] callSiteArray = DocSource.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(DocSource.class, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ Object $static_propertyMissing(String name) {
            CallSite[] callSiteArray = DocSource.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(DocSource.class, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != DocSource.class) {
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

        public SortedSet<DocPackage> getPackages() {
            return this.packages;
        }

        public void setPackages(SortedSet<DocPackage> sortedSet) {
            this.packages = sortedSet;
        }

        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
            stringArray[0] = "<$constructor$>";
            stringArray[1] = "SORT_KEY_COMPARATOR";
            stringArray[2] = "<$constructor$>";
            stringArray[3] = "find";
            stringArray[4] = "<$constructor$>";
            stringArray[5] = "packageName";
            stringArray[6] = "leftShift";
            stringArray[7] = "find";
            stringArray[8] = "docTypes";
            stringArray[9] = "leftShift";
            stringArray[10] = "docTypes";
            stringArray[11] = "<$constructor$>";
            stringArray[12] = "leftShift";
            stringArray[13] = "docMethods";
            stringArray[14] = "collectEntries";
            stringArray[15] = "allDocTypes";
            stringArray[16] = "each";
            stringArray[17] = "<$constructor$>";
            stringArray[18] = "SORT_KEY_COMPARATOR";
            stringArray[19] = "addAll";
            stringArray[20] = "collectMany";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[21];
            DocSource.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(DocSource.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = DocSource.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }

    static class DocPackage
    implements GroovyObject {
        private static final String PRIMITIVE_TYPE_PSEUDO_PACKAGE = "primitive-types";
        private String name;
        private SortedSet<DocType> docTypes;
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private transient /* synthetic */ MetaClass metaClass;
        private static /* synthetic */ SoftReference $callSiteArray;

        public DocPackage() {
            MetaClass metaClass;
            CallSite[] callSiteArray = DocPackage.$getCallSiteArray();
            Object object = callSiteArray[0].callConstructor(TreeSet.class, callSiteArray[1].callGetProperty(DocGenerator.class));
            this.docTypes = (SortedSet)ScriptBytecodeAdapter.castToType(object, SortedSet.class);
            this.metaClass = metaClass = this.$getStaticMetaClass();
        }

        public boolean isPrimitive() {
            CallSite[] callSiteArray = DocPackage.$getCallSiteArray();
            return ScriptBytecodeAdapter.compareEqual(this.name, PRIMITIVE_TYPE_PSEUDO_PACKAGE);
        }

        public String getSortKey() {
            CallSite[] callSiteArray = DocPackage.$getCallSiteArray();
            return this.name;
        }

        public /* synthetic */ Object methodMissing(String name, Object args) {
            CallSite[] callSiteArray = DocPackage.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(DocPackage.class, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
            CallSite[] callSiteArray = DocPackage.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(DocPackage.class, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public /* synthetic */ void propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = DocPackage.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = DocPackage.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public /* synthetic */ Object propertyMissing(String name) {
            CallSite[] callSiteArray = DocPackage.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(DocPackage.class, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ Object $static_propertyMissing(String name) {
            CallSite[] callSiteArray = DocPackage.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(DocPackage.class, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != DocPackage.class) {
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

        public static String getPRIMITIVE_TYPE_PSEUDO_PACKAGE() {
            return PRIMITIVE_TYPE_PSEUDO_PACKAGE;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String string) {
            this.name = string;
        }

        public SortedSet<DocType> getDocTypes() {
            return this.docTypes;
        }

        public void setDocTypes(SortedSet<DocType> sortedSet) {
            this.docTypes = sortedSet;
        }

        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
            stringArray[0] = "<$constructor$>";
            stringArray[1] = "SORT_KEY_COMPARATOR";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[2];
            DocPackage.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(DocPackage.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = DocPackage.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }

    static class DocType
    implements GroovyObject {
        private Type type;
        private final String shortComment;
        private SortedSet<DocMethod> docMethods;
        private Map<String, List<DocMethod>> inheritedMethods;
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private transient /* synthetic */ MetaClass metaClass;
        private static /* synthetic */ SoftReference $callSiteArray;

        public DocType() {
            MetaClass metaClass;
            String string;
            CallSite[] callSiteArray = DocType.$getCallSiteArray();
            this.shortComment = string = "";
            Object object = callSiteArray[0].callConstructor(TreeSet.class, callSiteArray[1].callGetProperty(DocGenerator.class));
            this.docMethods = (SortedSet)ScriptBytecodeAdapter.castToType(object, SortedSet.class);
            Object object2 = callSiteArray[2].callConstructor(LinkedHashMap.class);
            this.inheritedMethods = (Map)ScriptBytecodeAdapter.castToType(object2, Map.class);
            this.metaClass = metaClass = this.$getStaticMetaClass();
        }

        public JavaClass getJavaClass() {
            CallSite[] callSiteArray = DocType.$getCallSiteArray();
            return (JavaClass)ScriptBytecodeAdapter.castToType(callSiteArray[3].callGetProperty(this.type), JavaClass.class);
        }

        public String getPackageName() {
            CallSite[] callSiteArray = DocType.$getCallSiteArray();
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[4].callGetProperty(this.type))) {
                return ShortTypeHandling.castToString(callSiteArray[5].callGetProperty(DocPackage.class));
            }
            Object fqcn = callSiteArray[6].callGroovyObjectGetProperty(this);
            if (ScriptBytecodeAdapter.compareLessThan(callSiteArray[7].call(fqcn, "."), 0)) {
                return "";
            }
            return ShortTypeHandling.castToString(callSiteArray[8].call(fqcn, "\\.[^.]*$", ""));
        }

        public String getSimpleClassName() {
            CallSite[] callSiteArray = DocType.$getCallSiteArray();
            return ShortTypeHandling.castToString(callSiteArray[9].call(callSiteArray[10].callGroovyObjectGetProperty(this), "^.*\\.", ""));
        }

        public String getFullyQualifiedClassName() {
            CallSite[] callSiteArray = DocType.$getCallSiteArray();
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[11].callGetProperty(this.type))) {
                return ShortTypeHandling.castToString(callSiteArray[12].call(callSiteArray[13].call(callSiteArray[14].callGetProperty(DocPackage.class), "."), callSiteArray[15].call(this.type)));
            }
            return ShortTypeHandling.castToString(callSiteArray[16].call(DocUtil.class, callSiteArray[17].call(this.type)));
        }

        public boolean isInterface() {
            CallSite[] callSiteArray = DocType.$getCallSiteArray();
            return DefaultTypeTransformation.booleanUnbox(callSiteArray[18].call(callSiteArray[19].callGetProperty(this.type)));
        }

        public String getSortKey() {
            CallSite[] callSiteArray = DocType.$getCallSiteArray();
            return ShortTypeHandling.castToString(callSiteArray[20].call(callSiteArray[21].call(callSiteArray[22].callGroovyObjectGetProperty(this), " "), callSiteArray[23].callGroovyObjectGetProperty(this)));
        }

        public String linkAnchor(DocType otherDocType) {
            CallSite[] callSiteArray = DocType.$getCallSiteArray();
            return ShortTypeHandling.castToString(callSiteArray[24].call(DocUtil.class, callSiteArray[25].callGroovyObjectGetProperty(otherDocType), callSiteArray[26].callGroovyObjectGetProperty(this)));
        }

        public /* synthetic */ Object methodMissing(String name, Object args) {
            CallSite[] callSiteArray = DocType.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(DocType.class, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
            CallSite[] callSiteArray = DocType.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(DocType.class, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public /* synthetic */ void propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = DocType.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = DocType.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public /* synthetic */ Object propertyMissing(String name) {
            CallSite[] callSiteArray = DocType.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(DocType.class, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ Object $static_propertyMissing(String name) {
            CallSite[] callSiteArray = DocType.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(DocType.class, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != DocType.class) {
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

        public final String getShortComment() {
            return this.shortComment;
        }

        public SortedSet<DocMethod> getDocMethods() {
            return this.docMethods;
        }

        public void setDocMethods(SortedSet<DocMethod> sortedSet) {
            this.docMethods = sortedSet;
        }

        public Map<String, List<DocMethod>> getInheritedMethods() {
            return this.inheritedMethods;
        }

        public void setInheritedMethods(Map<String, List<DocMethod>> map) {
            this.inheritedMethods = map;
        }

        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
            stringArray[0] = "<$constructor$>";
            stringArray[1] = "SORT_KEY_COMPARATOR";
            stringArray[2] = "<$constructor$>";
            stringArray[3] = "javaClass";
            stringArray[4] = "primitive";
            stringArray[5] = "PRIMITIVE_TYPE_PSEUDO_PACKAGE";
            stringArray[6] = "fullyQualifiedClassName";
            stringArray[7] = "indexOf";
            stringArray[8] = "replaceAll";
            stringArray[9] = "replaceAll";
            stringArray[10] = "fullyQualifiedClassName";
            stringArray[11] = "primitive";
            stringArray[12] = "plus";
            stringArray[13] = "plus";
            stringArray[14] = "PRIMITIVE_TYPE_PSEUDO_PACKAGE";
            stringArray[15] = "toString";
            stringArray[16] = "resolveJdkClassName";
            stringArray[17] = "toString";
            stringArray[18] = "isInterface";
            stringArray[19] = "javaClass";
            stringArray[20] = "plus";
            stringArray[21] = "plus";
            stringArray[22] = "simpleClassName";
            stringArray[23] = "fullyQualifiedClassName";
            stringArray[24] = "getLinkAnchor";
            stringArray[25] = "fullyQualifiedClassName";
            stringArray[26] = "packageName";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[27];
            DocType.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(DocType.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = DocType.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }

    static class DocMethod
    implements GroovyObject {
        private DocType declaringDocType;
        private JavaMethod javaMethod;
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private transient /* synthetic */ MetaClass metaClass;
        private static /* synthetic */ SoftReference $callSiteArray;

        public DocMethod() {
            MetaClass metaClass;
            CallSite[] callSiteArray = DocMethod.$getCallSiteArray();
            this.metaClass = metaClass = this.$getStaticMetaClass();
        }

        public String getName() {
            CallSite[] callSiteArray = DocMethod.$getCallSiteArray();
            return ShortTypeHandling.castToString(callSiteArray[0].callGetProperty(this.javaMethod));
        }

        public List<JavaParameter> getParameters() {
            CallSite[] callSiteArray = DocMethod.$getCallSiteArray();
            if (ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[1].call(callSiteArray[2].call(this.javaMethod)), 1)) {
                return (List)ScriptBytecodeAdapter.castToType(callSiteArray[3].call(callSiteArray[4].call(callSiteArray[5].call(this.javaMethod)), ScriptBytecodeAdapter.createRange(1, -1, true)), List.class);
            }
            return ScriptBytecodeAdapter.createList(new Object[0]);
        }

        public String getParametersSignature() {
            CallSite[] callSiteArray = DocMethod.$getCallSiteArray();
            public class _getParametersSignature_closure1
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getParametersSignature_closure1(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _getParametersSignature_closure1.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _getParametersSignature_closure1.$getCallSiteArray();
                    return callSiteArray[0].call(DocUtil.class, callSiteArray[1].call(callSiteArray[2].callGetProperty(it)));
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _getParametersSignature_closure1.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getParametersSignature_closure1.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "resolveJdkClassName";
                    stringArray[1] = "toString";
                    stringArray[2] = "type";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[3];
                    _getParametersSignature_closure1.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_getParametersSignature_closure1.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getParametersSignature_closure1.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            return ShortTypeHandling.castToString(callSiteArray[6].call(callSiteArray[7].call(callSiteArray[8].callGroovyObjectGetProperty(this), new _getParametersSignature_closure1(this, this)), ", "));
        }

        public String getParametersDocUrl() {
            CallSite[] callSiteArray = DocMethod.$getCallSiteArray();
            public class _getParametersDocUrl_closure2
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getParametersDocUrl_closure2(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _getParametersDocUrl_closure2.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _getParametersDocUrl_closure2.$getCallSiteArray();
                    return new GStringImpl(new Object[]{callSiteArray[0].call(DocUtil.class, callSiteArray[1].call(callSiteArray[2].callGetProperty(it)), callSiteArray[3].callGroovyObjectGetProperty(callSiteArray[4].callGroovyObjectGetProperty(this))), callSiteArray[5].callGetProperty(it)}, new String[]{"", " ", ""});
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _getParametersDocUrl_closure2.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getParametersDocUrl_closure2.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "getLinkAnchor";
                    stringArray[1] = "toString";
                    stringArray[2] = "type";
                    stringArray[3] = "packageName";
                    stringArray[4] = "declaringDocType";
                    stringArray[5] = "name";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[6];
                    _getParametersDocUrl_closure2.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_getParametersDocUrl_closure2.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getParametersDocUrl_closure2.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            return ShortTypeHandling.castToString(callSiteArray[9].call(callSiteArray[10].call(callSiteArray[11].callGroovyObjectGetProperty(this), new _getParametersDocUrl_closure2(this, this)), ", "));
        }

        public String getReturnTypeDocUrl() {
            CallSite[] callSiteArray = DocMethod.$getCallSiteArray();
            Object returnType = callSiteArray[12].callGetProperty(this.javaMethod);
            String resolvedReturnType = DefaultTypeTransformation.booleanUnbox(returnType) ? callSiteArray[13].call(DocUtil.class, callSiteArray[14].call(returnType)) : "";
            return ShortTypeHandling.castToString(callSiteArray[15].call(DocUtil.class, resolvedReturnType, callSiteArray[16].callGroovyObjectGetProperty(this.declaringDocType)));
        }

        public String getComment() {
            CallSite[] callSiteArray = DocMethod.$getCallSiteArray();
            Object object = callSiteArray[18].callGetProperty(this.javaMethod);
            return ShortTypeHandling.castToString(callSiteArray[17].call(DocUtil.class, DefaultTypeTransformation.booleanUnbox(object) ? object : "", callSiteArray[19].callGroovyObjectGetProperty(this.declaringDocType)));
        }

        public String getShortComment() {
            CallSite[] callSiteArray = DocMethod.$getCallSiteArray();
            Object object = callSiteArray[22].callGetProperty(this.javaMethod);
            return ShortTypeHandling.castToString(callSiteArray[20].call(DocUtil.class, callSiteArray[21].call(DocUtil.class, DefaultTypeTransformation.booleanUnbox(object) ? object : ""), callSiteArray[23].callGroovyObjectGetProperty(this.declaringDocType)));
        }

        public String getReturnComment() {
            CallSite[] callSiteArray = DocMethod.$getCallSiteArray();
            Object object = callSiteArray[25].callGetPropertySafe(callSiteArray[26].call((Object)this.javaMethod, "return"));
            return ShortTypeHandling.castToString(callSiteArray[24].call(DocUtil.class, DefaultTypeTransformation.booleanUnbox(object) ? object : "", callSiteArray[27].callGroovyObjectGetProperty(this.declaringDocType)));
        }

        public Map getParameterComments() {
            CallSite[] callSiteArray = DocMethod.$getCallSiteArray();
            public class _getParameterComments_closure3
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getParameterComments_closure3(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _getParameterComments_closure3.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _getParameterComments_closure3.$getCallSiteArray();
                    Object name = callSiteArray[0].call(callSiteArray[1].callGetProperty(it), " .*", "");
                    Object comment = callSiteArray[2].call(DocUtil.class, callSiteArray[3].call(callSiteArray[4].callGetProperty(it), "^\\w*", ""), callSiteArray[5].callGroovyObjectGetProperty(callSiteArray[6].callGroovyObjectGetProperty(this)));
                    return ScriptBytecodeAdapter.createList(new Object[]{name, comment});
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _getParameterComments_closure3.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getParameterComments_closure3.class) {
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
                    stringArray[1] = "value";
                    stringArray[2] = "formatJavadocText";
                    stringArray[3] = "replaceAll";
                    stringArray[4] = "value";
                    stringArray[5] = "packageName";
                    stringArray[6] = "declaringDocType";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[7];
                    _getParameterComments_closure3.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_getParameterComments_closure3.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getParameterComments_closure3.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            return (Map)ScriptBytecodeAdapter.castToType(callSiteArray[28].call(callSiteArray[29].call(callSiteArray[30].call((Object)this.javaMethod, "param"), 1), new _getParameterComments_closure3(this, this)), Map.class);
        }

        public List<String> getSeeComments() {
            CallSite[] callSiteArray = DocMethod.$getCallSiteArray();
            public class _getSeeComments_closure4
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getSeeComments_closure4(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _getSeeComments_closure4.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _getSeeComments_closure4.$getCallSiteArray();
                    return callSiteArray[0].call(DocUtil.class, callSiteArray[1].callGetProperty(it), callSiteArray[2].callGroovyObjectGetProperty(callSiteArray[3].callGroovyObjectGetProperty(this)));
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _getSeeComments_closure4.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getSeeComments_closure4.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "getLinkAnchor";
                    stringArray[1] = "value";
                    stringArray[2] = "packageName";
                    stringArray[3] = "declaringDocType";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[4];
                    _getSeeComments_closure4.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_getSeeComments_closure4.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getSeeComments_closure4.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            return (List)ScriptBytecodeAdapter.castToType(callSiteArray[31].call(callSiteArray[32].call((Object)this.javaMethod, "see"), new _getSeeComments_closure4(this, this)), List.class);
        }

        public String getSinceComment() {
            CallSite[] callSiteArray = DocMethod.$getCallSiteArray();
            return ShortTypeHandling.castToString(callSiteArray[33].callGetPropertySafe(callSiteArray[34].call((Object)this.javaMethod, "since")));
        }

        public boolean isStatic() {
            CallSite[] callSiteArray = DocMethod.$getCallSiteArray();
            return ScriptBytecodeAdapter.compareEqual(callSiteArray[35].callGetProperty(callSiteArray[36].callGetProperty(this.javaMethod)), "DefaultGroovyStaticMethods");
        }

        public String getSortKey() {
            CallSite[] callSiteArray = DocMethod.$getCallSiteArray();
            return ShortTypeHandling.castToString(callSiteArray[37].call(callSiteArray[38].call(callSiteArray[39].call(callSiteArray[40].call(callSiteArray[41].callGroovyObjectGetProperty(this), " "), callSiteArray[42].callGroovyObjectGetProperty(this)), " "), callSiteArray[43].callGroovyObjectGetProperty(this.declaringDocType)));
        }

        public /* synthetic */ Object methodMissing(String name, Object args) {
            CallSite[] callSiteArray = DocMethod.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(DocMethod.class, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
            CallSite[] callSiteArray = DocMethod.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(DocMethod.class, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public /* synthetic */ void propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = DocMethod.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = DocMethod.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public /* synthetic */ Object propertyMissing(String name) {
            CallSite[] callSiteArray = DocMethod.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(DocMethod.class, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ Object $static_propertyMissing(String name) {
            CallSite[] callSiteArray = DocMethod.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(DocMethod.class, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != DocMethod.class) {
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

        public DocType getDeclaringDocType() {
            return this.declaringDocType;
        }

        public void setDeclaringDocType(DocType docType) {
            this.declaringDocType = docType;
        }

        public JavaMethod getJavaMethod() {
            return this.javaMethod;
        }

        public void setJavaMethod(JavaMethod javaMethod) {
            this.javaMethod = javaMethod;
        }

        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
            stringArray[0] = "name";
            stringArray[1] = "size";
            stringArray[2] = "getParameters";
            stringArray[3] = "getAt";
            stringArray[4] = "toList";
            stringArray[5] = "getParameters";
            stringArray[6] = "join";
            stringArray[7] = "collect";
            stringArray[8] = "parameters";
            stringArray[9] = "join";
            stringArray[10] = "collect";
            stringArray[11] = "parameters";
            stringArray[12] = "returns";
            stringArray[13] = "resolveJdkClassName";
            stringArray[14] = "toString";
            stringArray[15] = "getLinkAnchor";
            stringArray[16] = "packageName";
            stringArray[17] = "formatJavadocText";
            stringArray[18] = "comment";
            stringArray[19] = "packageName";
            stringArray[20] = "formatJavadocText";
            stringArray[21] = "getFirstSentence";
            stringArray[22] = "comment";
            stringArray[23] = "packageName";
            stringArray[24] = "formatJavadocText";
            stringArray[25] = "value";
            stringArray[26] = "getTagByName";
            stringArray[27] = "packageName";
            stringArray[28] = "collectEntries";
            stringArray[29] = "drop";
            stringArray[30] = "getTagsByName";
            stringArray[31] = "collect";
            stringArray[32] = "getTagsByName";
            stringArray[33] = "value";
            stringArray[34] = "getTagByName";
            stringArray[35] = "name";
            stringArray[36] = "parentClass";
            stringArray[37] = "plus";
            stringArray[38] = "plus";
            stringArray[39] = "plus";
            stringArray[40] = "plus";
            stringArray[41] = "name";
            stringArray[42] = "parametersSignature";
            stringArray[43] = "fullyQualifiedClassName";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[44];
            DocMethod.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(DocMethod.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = DocMethod.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }

    static class DocUtil
    implements GroovyObject {
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private transient /* synthetic */ MetaClass metaClass;
        private static /* synthetic */ SoftReference $callSiteArray;

        public DocUtil() {
            MetaClass metaClass;
            CallSite[] callSiteArray = DocUtil.$getCallSiteArray();
            this.metaClass = metaClass = this.$getStaticMetaClass();
        }

        public static String resolveJdkClassName(String className) {
            CallSite[] callSiteArray = DocUtil.$getCallSiteArray();
            if (ScriptBytecodeAdapter.isCase(className, ScriptBytecodeAdapter.createList(new Object[]{"A", "B", "E", "G", "K", "S", "T", "U", "V", "W"}))) {
                return "java.lang.Object";
            }
            if (ScriptBytecodeAdapter.isCase(className, ScriptBytecodeAdapter.createList(new Object[]{"T[]", "E[]", "K[]"}))) {
                return "java.lang.Object[]";
            }
            return className;
        }

        public static String formatJavadocText(String text, String packageName) {
            CallSite[] callSiteArray = DocUtil.$getCallSiteArray();
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                return ShortTypeHandling.castToString(callSiteArray[0].callStatic(DocUtil.class, callSiteArray[1].callStatic(DocUtil.class, text), packageName));
            }
            return DocUtil.linkify(DocUtil.codify(text), packageName);
        }

        private static String linkify(String text, String packageName) {
            Reference<String> packageName2 = new Reference<String>(packageName);
            CallSite[] callSiteArray = DocUtil.$getCallSiteArray();
            public class _linkify_closure1
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference packageName;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _linkify_closure1(Object _outerInstance, Object _thisObject, Reference packageName) {
                    Reference reference;
                    CallSite[] callSiteArray = _linkify_closure1.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.packageName = reference = packageName;
                }

                public Object doCall(String all, String destination) {
                    CallSite[] callSiteArray = _linkify_closure1.$getCallSiteArray();
                    return callSiteArray[0].call(DocUtil.class, destination, this.packageName.get());
                }

                public Object call(String all, String destination) {
                    CallSite[] callSiteArray = _linkify_closure1.$getCallSiteArray();
                    if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                        return callSiteArray[1].callCurrent(this, all, destination);
                    }
                    return this.doCall(all, destination);
                }

                public String getPackageName() {
                    CallSite[] callSiteArray = _linkify_closure1.$getCallSiteArray();
                    return ShortTypeHandling.castToString(this.packageName.get());
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _linkify_closure1.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "getLinkAnchor";
                    stringArray[1] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[2];
                    _linkify_closure1.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_linkify_closure1.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _linkify_closure1.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            return ShortTypeHandling.castToString(callSiteArray[2].call(text, "\\{@link\\s+([^}]*)\\s*\\}", new _linkify_closure1(DocUtil.class, DocGenerator.class, packageName2)));
        }

        private static String codify(String text) {
            CallSite[] callSiteArray = DocUtil.$getCallSiteArray();
            public class _codify_closure2
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _codify_closure2(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _codify_closure2.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(String all, String code) {
                    CallSite[] callSiteArray = _codify_closure2.$getCallSiteArray();
                    return new GStringImpl(new Object[]{code}, new String[]{"<code>", "</code>"});
                }

                public Object call(String all, String code) {
                    CallSite[] callSiteArray = _codify_closure2.$getCallSiteArray();
                    if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                        return callSiteArray[0].callCurrent(this, all, code);
                    }
                    return this.doCall(all, code);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _codify_closure2.class) {
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
                    return new CallSiteArray(_codify_closure2.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _codify_closure2.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            return ShortTypeHandling.castToString(callSiteArray[3].call(text, "\\{@code\\s+([^}]*)\\s*\\}", new _codify_closure2(DocUtil.class, DocGenerator.class)));
        }

        public static String getFirstSentence(String text) {
            CallSite[] callSiteArray = DocUtil.$getCallSiteArray();
            Object boundary = callSiteArray[4].call(BreakIterator.class, callSiteArray[5].callGetProperty(callSiteArray[6].callGetProperty(DocGenerator.class)));
            callSiteArray[7].call(boundary, text);
            int start = DefaultTypeTransformation.intUnbox(callSiteArray[8].call(boundary));
            int end = DefaultTypeTransformation.intUnbox(callSiteArray[9].call(boundary));
            if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (ScriptBytecodeAdapter.compareGreaterThan(start, -1) && ScriptBytecodeAdapter.compareGreaterThan(end, -1)) {
                    return ShortTypeHandling.castToString(callSiteArray[10].call(text, start, end));
                }
            } else if (ScriptBytecodeAdapter.compareGreaterThan(start, -1) && ScriptBytecodeAdapter.compareGreaterThan(end, -1)) {
                return ShortTypeHandling.castToString(callSiteArray[11].call(text, start, end));
            }
            return text;
        }

        public static String getLinkAnchor(String destination, String originPackageName) {
            Reference<String> destination2 = new Reference<String>(destination);
            CallSite[] callSiteArray = DocUtil.$getCallSiteArray();
            Object inGdk = callSiteArray[12].call((Object)destination2.get(), "#");
            if (DefaultTypeTransformation.booleanUnbox(inGdk)) {
                public class _getLinkAnchor_closure3
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference destination;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _getLinkAnchor_closure3(Object _outerInstance, Object _thisObject, Reference destination) {
                        Reference reference;
                        CallSite[] callSiteArray = _getLinkAnchor_closure3.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.destination = reference = destination;
                    }

                    public Object doCall(String all, String name, String argsText) {
                        CallSite[] callSiteArray = _getLinkAnchor_closure3.$getCallSiteArray();
                        Object args = callSiteArray[0].call(callSiteArray[1].call((Object)argsText, ",\\s?"));
                        Object first = callSiteArray[2].call(args, 0);
                        Object object = callSiteArray[3].call(new GStringImpl(new Object[]{first, name, callSiteArray[4].call(args, ", ")}, new String[]{"", "#", "(", ")"}));
                        this.destination.set(ShortTypeHandling.castToString(object));
                        return object;
                    }

                    public Object call(String all, String name, String argsText) {
                        CallSite[] callSiteArray = _getLinkAnchor_closure3.$getCallSiteArray();
                        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                            return callSiteArray[5].callCurrent(this, all, name, argsText);
                        }
                        return this.doCall(all, name, argsText);
                    }

                    public String getDestination() {
                        CallSite[] callSiteArray = _getLinkAnchor_closure3.$getCallSiteArray();
                        return ShortTypeHandling.castToString(this.destination.get());
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _getLinkAnchor_closure3.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "toList";
                        stringArray[1] = "split";
                        stringArray[2] = "remove";
                        stringArray[3] = "toString";
                        stringArray[4] = "join";
                        stringArray[5] = "doCall";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[6];
                        _getLinkAnchor_closure3.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_getLinkAnchor_closure3.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _getLinkAnchor_closure3.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                callSiteArray[13].call((Object)ScriptBytecodeAdapter.findRegex(destination2.get(), "#([^(]*)\\(([^)]+)\\)"), new _getLinkAnchor_closure3(DocUtil.class, DocGenerator.class, destination2));
            }
            Object fullyQualifiedClassName = callSiteArray[14].callStatic(DocUtil.class, callSiteArray[15].call(destination2.get(), "#.*$", ""));
            Object methodSignatureHash = callSiteArray[16].call(destination2.get(), "^[^#]*", "");
            Object simpleClassName = callSiteArray[17].call(fullyQualifiedClassName, ".*\\.", "");
            Reference<Object> packageName = new Reference<Object>(callSiteArray[18].call(fullyQualifiedClassName, ".?[^.]+$", ""));
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[19].callGetProperty(packageName.get()))) {
                return destination2.get();
            }
            Object apiBaseUrl = null;
            GStringImpl title = null;
            if (DefaultTypeTransformation.booleanUnbox(inGdk)) {
                GStringImpl gStringImpl;
                Object object;
                apiBaseUrl = object = callSiteArray[20].call((Object)"../", callSiteArray[21].call(callSiteArray[22].call((Object)originPackageName, "."), 1));
                title = gStringImpl = new GStringImpl(new Object[]{fullyQualifiedClassName}, new String[]{"GDK enhancement for ", ""});
            } else {
                GStringImpl gStringImpl;
                title = gStringImpl = new GStringImpl(new Object[]{packageName.get()}, new String[]{"Class in ", ""});
                String string = "./";
                apiBaseUrl = string;
                public class _getLinkAnchor_closure4
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference packageName;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _getLinkAnchor_closure4(Object _outerInstance, Object _thisObject, Reference packageName) {
                        Reference reference;
                        CallSite[] callSiteArray = _getLinkAnchor_closure4.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.packageName = reference = packageName;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _getLinkAnchor_closure4.$getCallSiteArray();
                        return callSiteArray[0].call(this.packageName.get(), it);
                    }

                    public Object getPackageName() {
                        CallSite[] callSiteArray = _getLinkAnchor_closure4.$getCallSiteArray();
                        return this.packageName.get();
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _getLinkAnchor_closure4.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _getLinkAnchor_closure4.class) {
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
                        stringArray[0] = "startsWith";
                        return new CallSiteArray(_getLinkAnchor_closure4.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _getLinkAnchor_closure4.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                String key = ShortTypeHandling.castToString(callSiteArray[23].call(callSiteArray[24].call(callSiteArray[25].callGetProperty(callSiteArray[26].callGetProperty(DocGenerator.class))), new _getLinkAnchor_closure4(DocUtil.class, DocGenerator.class, packageName)));
                if (DefaultTypeTransformation.booleanUnbox(key)) {
                    Object object;
                    apiBaseUrl = object = callSiteArray[27].call(callSiteArray[28].callGetProperty(callSiteArray[29].callGetProperty(DocGenerator.class)), key);
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[30].call(apiBaseUrl, ".."))) {
                        Object object2;
                        apiBaseUrl = object2 = callSiteArray[31].call(callSiteArray[32].call((Object)"../", callSiteArray[33].call(callSiteArray[34].call((Object)originPackageName, "."), 1)), apiBaseUrl);
                    }
                }
            }
            GStringImpl url = new GStringImpl(new Object[]{apiBaseUrl, callSiteArray[35].call(packageName.get(), ".", "/"), simpleClassName, methodSignatureHash}, new String[]{"", "", "/", ".html", ""});
            return ShortTypeHandling.castToString(new GStringImpl(new Object[]{url, title, simpleClassName, methodSignatureHash}, new String[]{"<a href=\"", "\" title=\"", "\">", "", "</a>"}));
        }

        public static File createPackageDirectory(File outputDir, String packageName) {
            CallSite[] callSiteArray = DocUtil.$getCallSiteArray();
            Object packagePath = null;
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                Object object;
                packagePath = object = callSiteArray[36].callStatic(DocUtil.class, packageName);
            } else {
                String string = DocUtil.filePathOf(packageName);
                packagePath = string;
            }
            Object dir = callSiteArray[37].callConstructor(File.class, outputDir, packagePath);
            callSiteArray[38].call(dir);
            return (File)ScriptBytecodeAdapter.castToType(dir, File.class);
        }

        private static String filePathOf(String packageName) {
            CallSite[] callSiteArray = DocUtil.$getCallSiteArray();
            Object fileSep = callSiteArray[39].callGetProperty(File.class);
            if (ScriptBytecodeAdapter.compareEqual(fileSep, "\\")) {
                fileSep = callSiteArray[40].call(fileSep, 2);
            }
            return ShortTypeHandling.castToString(callSiteArray[41].call(packageName, "\\.", fileSep));
        }

        public static File sourceFileOf(String pathOrClassName) {
            CallSite[] callSiteArray = DocUtil.$getCallSiteArray();
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[42].call((Object)pathOrClassName, "/"))) {
                return (File)ScriptBytecodeAdapter.castToType(callSiteArray[43].callConstructor(File.class, pathOrClassName), File.class);
            }
            return (File)ScriptBytecodeAdapter.castToType(callSiteArray[44].callConstructor(File.class, callSiteArray[45].call(callSiteArray[46].call((Object)"src/main/", callSiteArray[47].call(pathOrClassName, ".", "/")), ".java")), File.class);
        }

        public /* synthetic */ Object methodMissing(String name, Object args) {
            CallSite[] callSiteArray = DocUtil.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(DocUtil.class, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
            CallSite[] callSiteArray = DocUtil.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(DocUtil.class, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public /* synthetic */ void propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = DocUtil.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = DocUtil.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public /* synthetic */ Object propertyMissing(String name) {
            CallSite[] callSiteArray = DocUtil.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(DocUtil.class, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ Object $static_propertyMissing(String name) {
            CallSite[] callSiteArray = DocUtil.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(DocUtil.class, DocGenerator.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != DocUtil.class) {
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
            stringArray[0] = "linkify";
            stringArray[1] = "codify";
            stringArray[2] = "replaceAll";
            stringArray[3] = "replaceAll";
            stringArray[4] = "getSentenceInstance";
            stringArray[5] = "locale";
            stringArray[6] = "CONFIG";
            stringArray[7] = "setText";
            stringArray[8] = "first";
            stringArray[9] = "next";
            stringArray[10] = "substring";
            stringArray[11] = "substring";
            stringArray[12] = "startsWith";
            stringArray[13] = "each";
            stringArray[14] = "resolveJdkClassName";
            stringArray[15] = "replaceFirst";
            stringArray[16] = "replaceFirst";
            stringArray[17] = "replaceFirst";
            stringArray[18] = "replaceFirst";
            stringArray[19] = "empty";
            stringArray[20] = "multiply";
            stringArray[21] = "plus";
            stringArray[22] = "count";
            stringArray[23] = "find";
            stringArray[24] = "keySet";
            stringArray[25] = "links";
            stringArray[26] = "CONFIG";
            stringArray[27] = "getAt";
            stringArray[28] = "links";
            stringArray[29] = "CONFIG";
            stringArray[30] = "startsWith";
            stringArray[31] = "plus";
            stringArray[32] = "multiply";
            stringArray[33] = "plus";
            stringArray[34] = "count";
            stringArray[35] = "replace";
            stringArray[36] = "filePathOf";
            stringArray[37] = "<$constructor$>";
            stringArray[38] = "mkdirs";
            stringArray[39] = "separator";
            stringArray[40] = "multiply";
            stringArray[41] = "replaceAll";
            stringArray[42] = "contains";
            stringArray[43] = "<$constructor$>";
            stringArray[44] = "<$constructor$>";
            stringArray[45] = "plus";
            stringArray[46] = "plus";
            stringArray[47] = "replace";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[48];
            DocUtil.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(DocUtil.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = DocUtil.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }
}

