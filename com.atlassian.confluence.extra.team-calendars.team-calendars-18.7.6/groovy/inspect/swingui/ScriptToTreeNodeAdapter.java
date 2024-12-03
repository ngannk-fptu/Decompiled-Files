/*
 * Decompiled with CFR 0.152.
 */
package groovy.inspect.swingui;

import groovy.inspect.swingui.AstBrowserNodeMaker;
import groovy.inspect.swingui.TreeNodeBuildingNodeOperation;
import groovy.lang.Closure;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.lang.Writable;
import groovy.text.GStringTemplateEngine;
import groovy.text.Template;
import groovy.util.ConfigSlurper;
import java.io.File;
import java.io.StringWriter;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class ScriptToTreeNodeAdapter
implements GroovyObject {
    private static Properties classNameToStringForm;
    private boolean showScriptFreeForm;
    private boolean showScriptClass;
    private boolean showClosureClasses;
    private final GroovyClassLoader classLoader;
    private final AstBrowserNodeMaker nodeMaker;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ScriptToTreeNodeAdapter(Object classLoader, Object showScriptFreeForm, Object showScriptClass, Object showClosureClasses, Object nodeMaker) {
        MetaClass metaClass;
        CallSite[] callSiteArray = ScriptToTreeNodeAdapter.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        Object object = classLoader;
        Object object2 = DefaultTypeTransformation.booleanUnbox(object) ? object : callSiteArray[0].callConstructor(GroovyClassLoader.class, callSiteArray[1].callGetProperty(callSiteArray[2].callCurrent(this)));
        this.classLoader = (GroovyClassLoader)ScriptBytecodeAdapter.castToType(object2, GroovyClassLoader.class);
        Object object3 = showScriptFreeForm;
        this.showScriptFreeForm = DefaultTypeTransformation.booleanUnbox(object3);
        Object object4 = showScriptClass;
        this.showScriptClass = DefaultTypeTransformation.booleanUnbox(object4);
        Object object5 = showClosureClasses;
        this.showClosureClasses = DefaultTypeTransformation.booleanUnbox(object5);
        Object object6 = nodeMaker;
        this.nodeMaker = (AstBrowserNodeMaker)ScriptBytecodeAdapter.castToType(object6, AstBrowserNodeMaker.class);
    }

    static {
        try {
            URL url = (URL)ScriptBytecodeAdapter.castToType(ScriptToTreeNodeAdapter.$getCallSiteArray()[3].call(ClassLoader.class, "groovy/inspect/swingui/AstBrowserProperties.groovy"), URL.class);
            if (!DefaultTypeTransformation.booleanUnbox(url)) {
                Object object = ScriptToTreeNodeAdapter.$getCallSiteArray()[4].call(ScriptToTreeNodeAdapter.$getCallSiteArray()[5].callGetProperty(ScriptToTreeNodeAdapter.class), "groovy/inspect/swingui/AstBrowserProperties.groovy");
                url = (URL)ScriptBytecodeAdapter.castToType(object, URL.class);
            }
            Object config = ScriptToTreeNodeAdapter.$getCallSiteArray()[6].call(ScriptToTreeNodeAdapter.$getCallSiteArray()[7].callConstructor(ConfigSlurper.class), url);
            Object object = ScriptToTreeNodeAdapter.$getCallSiteArray()[8].call(config);
            classNameToStringForm = (Properties)ScriptBytecodeAdapter.castToType(object, Properties.class);
            String home = ShortTypeHandling.castToString(ScriptToTreeNodeAdapter.$getCallSiteArray()[9].call(System.class, "user.home"));
            if (DefaultTypeTransformation.booleanUnbox(home)) {
                File userFile = (File)ScriptBytecodeAdapter.castToType(ScriptToTreeNodeAdapter.$getCallSiteArray()[10].callConstructor(File.class, ScriptToTreeNodeAdapter.$getCallSiteArray()[11].call(ScriptToTreeNodeAdapter.$getCallSiteArray()[12].call((Object)home, ScriptToTreeNodeAdapter.$getCallSiteArray()[13].callGetProperty(File.class)), ".groovy/AstBrowserProperties.groovy")), File.class);
                if (DefaultTypeTransformation.booleanUnbox(ScriptToTreeNodeAdapter.$getCallSiteArray()[14].call(userFile))) {
                    Object customConfig = ScriptToTreeNodeAdapter.$getCallSiteArray()[15].call(ScriptToTreeNodeAdapter.$getCallSiteArray()[16].callConstructor(ConfigSlurper.class), ScriptToTreeNodeAdapter.$getCallSiteArray()[17].call(userFile));
                    ScriptToTreeNodeAdapter.$getCallSiteArray()[18].call((Object)classNameToStringForm, ScriptToTreeNodeAdapter.$getCallSiteArray()[19].call(customConfig));
                }
            }
        }
        catch (Exception ex) {
            Object object = ScriptToTreeNodeAdapter.$getCallSiteArray()[20].callConstructor(Properties.class);
            classNameToStringForm = (Properties)ScriptBytecodeAdapter.castToType(object, Properties.class);
        }
    }

    public Object compile(String script, int compilePhase) {
        CallSite[] callSiteArray = ScriptToTreeNodeAdapter.$getCallSiteArray();
        Object scriptName = callSiteArray[21].call(callSiteArray[22].call((Object)"script", callSiteArray[23].call(System.class)), ".groovy");
        GroovyCodeSource codeSource = (GroovyCodeSource)ScriptBytecodeAdapter.castToType(callSiteArray[24].callConstructor(GroovyCodeSource.class, script, scriptName, "/groovy/script"), GroovyCodeSource.class);
        CompilationUnit cu = (CompilationUnit)ScriptBytecodeAdapter.castToType(callSiteArray[25].callConstructor(CompilationUnit.class, callSiteArray[26].callGetProperty(CompilerConfiguration.class), callSiteArray[27].callGetProperty(codeSource), this.classLoader), CompilationUnit.class);
        callSiteArray[28].call((Object)cu, callSiteArray[29].call(this.classLoader, cu, null));
        Reference<TreeNodeBuildingNodeOperation> operation = new Reference<TreeNodeBuildingNodeOperation>((TreeNodeBuildingNodeOperation)ScriptBytecodeAdapter.castToType(callSiteArray[30].callConstructor(TreeNodeBuildingNodeOperation.class, this, this.showScriptFreeForm, this.showScriptClass, this.showClosureClasses), TreeNodeBuildingNodeOperation.class));
        callSiteArray[31].call(cu, operation.get(), compilePhase);
        callSiteArray[32].call(cu, callSiteArray[33].call(codeSource), script);
        try {
            callSiteArray[34].call((Object)cu, compilePhase);
        }
        catch (CompilationFailedException cfe) {
            callSiteArray[35].call(callSiteArray[36].callGroovyObjectGetProperty(operation.get()), callSiteArray[37].call((Object)this.nodeMaker, "Unable to produce AST for this phase due to earlier compilation error:"));
            public class _compile_closure1
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference operation;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _compile_closure1(Object _outerInstance, Object _thisObject, Reference operation) {
                    Reference reference;
                    CallSite[] callSiteArray = _compile_closure1.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.operation = reference = operation;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _compile_closure1.$getCallSiteArray();
                    return callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this.operation.get()), callSiteArray[2].call(callSiteArray[3].callGroovyObjectGetProperty(this), it));
                }

                public TreeNodeBuildingNodeOperation getOperation() {
                    CallSite[] callSiteArray = _compile_closure1.$getCallSiteArray();
                    return (TreeNodeBuildingNodeOperation)ScriptBytecodeAdapter.castToType(this.operation.get(), TreeNodeBuildingNodeOperation.class);
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _compile_closure1.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _compile_closure1.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "add";
                    stringArray[1] = "root";
                    stringArray[2] = "makeNode";
                    stringArray[3] = "nodeMaker";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[4];
                    _compile_closure1.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_compile_closure1.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _compile_closure1.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[38].call(callSiteArray[39].callGetProperty(cfe), new _compile_closure1(this, this, operation));
            callSiteArray[40].call(callSiteArray[41].callGroovyObjectGetProperty(operation.get()), callSiteArray[42].call((Object)this.nodeMaker, "Fix the above error(s) and then press Refresh"));
        }
        catch (Throwable t) {
            callSiteArray[43].call(callSiteArray[44].callGroovyObjectGetProperty(operation.get()), callSiteArray[45].call((Object)this.nodeMaker, "Unable to produce AST for this phase due to an error:"));
            callSiteArray[46].call(callSiteArray[47].callGroovyObjectGetProperty(operation.get()), callSiteArray[48].call((Object)this.nodeMaker, t));
            callSiteArray[49].call(callSiteArray[50].callGroovyObjectGetProperty(operation.get()), callSiteArray[51].call((Object)this.nodeMaker, "Fix the above error(s) and then press Refresh"));
        }
        return callSiteArray[52].callGroovyObjectGetProperty(operation.get());
    }

    public Object make(Object node) {
        CallSite[] callSiteArray = ScriptToTreeNodeAdapter.$getCallSiteArray();
        return callSiteArray[53].call(this.nodeMaker, callSiteArray[54].callCurrent((GroovyObject)this, node), callSiteArray[55].callCurrent((GroovyObject)this, node));
    }

    public Object make(MethodNode node) {
        CallSite[] callSiteArray = ScriptToTreeNodeAdapter.$getCallSiteArray();
        Object table = callSiteArray[56].callCurrent((GroovyObject)this, node);
        callSiteArray[57].callCurrent(this, table, node);
        return callSiteArray[58].call(this.nodeMaker, callSiteArray[59].callCurrent((GroovyObject)this, node), table);
    }

    public void extendMethodNodePropertyTable(List<List<String>> table, MethodNode node) {
        CallSite[] callSiteArray = ScriptToTreeNodeAdapter.$getCallSiteArray();
        callSiteArray[60].call(table, ScriptBytecodeAdapter.createList(new Object[]{"descriptor", callSiteArray[61].call(BytecodeHelper.class, node), "String"}));
    }

    private List<List<String>> getPropertyTable(Object node) {
        Reference<Object> node2 = new Reference<Object>(node);
        CallSite[] callSiteArray = ScriptToTreeNodeAdapter.$getCallSiteArray();
        public class _getPropertyTable_closure2
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _getPropertyTable_closure2(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _getPropertyTable_closure2.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _getPropertyTable_closure2.$getCallSiteArray();
                return callSiteArray[0].callGetProperty(it);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _getPropertyTable_closure2.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _getPropertyTable_closure2.class) {
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
                stringArray[0] = "getter";
                return new CallSiteArray(_getPropertyTable_closure2.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _getPropertyTable_closure2.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        public class _getPropertyTable_closure3
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference node;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _getPropertyTable_closure3(Object _outerInstance, Object _thisObject, Reference node) {
                Reference reference;
                CallSite[] callSiteArray = _getPropertyTable_closure3.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.node = reference = node;
            }

            public Object doCall(Object it) {
                Object value;
                Object name;
                CallSite[] callSiteArray;
                block6: {
                    callSiteArray = _getPropertyTable_closure3.$getCallSiteArray();
                    name = callSiteArray[0].call(callSiteArray[1].callGetProperty(it));
                    value = null;
                    try {
                        Object object;
                        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                            Object object2;
                            Object object3;
                            value = this.node.get() instanceof DeclarationExpression && (ScriptBytecodeAdapter.compareEqual(name, "variableExpression") || ScriptBytecodeAdapter.compareEqual(name, "tupleExpression")) ? (object3 = callSiteArray[2].call(callSiteArray[3].callGetProperty(this.node.get()))) : (object2 = callSiteArray[4].call(callSiteArray[5].call(it, this.node.get())));
                            break block6;
                        }
                        if (this.node.get() instanceof DeclarationExpression && (ScriptBytecodeAdapter.compareEqual(name, "variableExpression") || ScriptBytecodeAdapter.compareEqual(name, "tupleExpression"))) {
                            Object object4;
                            value = object4 = callSiteArray[6].call(callSiteArray[7].callGetProperty(this.node.get()));
                            break block6;
                        }
                        value = object = callSiteArray[8].call(callSiteArray[9].call(it, this.node.get()));
                    }
                    catch (GroovyBugError reflectionArtefact) {
                        Object object;
                        value = object = null;
                    }
                }
                Object type = callSiteArray[10].call(callSiteArray[11].callGetProperty(callSiteArray[12].callGetProperty(it)));
                return ScriptBytecodeAdapter.createList(new Object[]{name, value, type});
            }

            public Object getNode() {
                CallSite[] callSiteArray = _getPropertyTable_closure3.$getCallSiteArray();
                return this.node.get();
            }

            public Object doCall() {
                CallSite[] callSiteArray = _getPropertyTable_closure3.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _getPropertyTable_closure3.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "toString";
                stringArray[1] = "name";
                stringArray[2] = "toString";
                stringArray[3] = "leftExpression";
                stringArray[4] = "toString";
                stringArray[5] = "getProperty";
                stringArray[6] = "toString";
                stringArray[7] = "leftExpression";
                stringArray[8] = "toString";
                stringArray[9] = "getProperty";
                stringArray[10] = "toString";
                stringArray[11] = "simpleName";
                stringArray[12] = "type";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[13];
                _getPropertyTable_closure3.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_getPropertyTable_closure3.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _getPropertyTable_closure3.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        public class _getPropertyTable_closure4
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _getPropertyTable_closure4(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _getPropertyTable_closure4.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _getPropertyTable_closure4.$getCallSiteArray();
                return callSiteArray[0].call(it, 0);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _getPropertyTable_closure4.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _getPropertyTable_closure4.class) {
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
                stringArray[0] = "getAt";
                return new CallSiteArray(_getPropertyTable_closure4.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _getPropertyTable_closure4.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return (List)ScriptBytecodeAdapter.castToType(callSiteArray[62].callSafe(callSiteArray[63].callSafe(callSiteArray[64].callSafe(callSiteArray[65].callGetProperty(callSiteArray[66].callGetProperty(node2.get())), new _getPropertyTable_closure2(this, this)), new _getPropertyTable_closure3(this, this, node2)), new _getPropertyTable_closure4(this, this)), List.class);
    }

    private String getStringForm(Object node) {
        CallSite[] callSiteArray = ScriptToTreeNodeAdapter.$getCallSiteArray();
        Object templateTextForNode = callSiteArray[67].call((Object)classNameToStringForm, callSiteArray[68].callGetProperty(callSiteArray[69].callGetProperty(node)));
        if (DefaultTypeTransformation.booleanUnbox(templateTextForNode)) {
            GStringTemplateEngine engine = (GStringTemplateEngine)ScriptBytecodeAdapter.castToType(callSiteArray[70].callConstructor(GStringTemplateEngine.class), GStringTemplateEngine.class);
            Template template = (Template)ScriptBytecodeAdapter.castToType(callSiteArray[71].call((Object)engine, templateTextForNode), Template.class);
            Writable writable = (Writable)ScriptBytecodeAdapter.castToType(callSiteArray[72].call((Object)template, ScriptBytecodeAdapter.createMap(new Object[]{"expression", node})), Writable.class);
            StringWriter result = (StringWriter)ScriptBytecodeAdapter.castToType(callSiteArray[73].callConstructor(StringWriter.class), StringWriter.class);
            callSiteArray[74].call((Object)writable, result);
            return ShortTypeHandling.castToString(callSiteArray[75].call(result));
        }
        return ShortTypeHandling.castToString(callSiteArray[76].callGetProperty(callSiteArray[77].callGetProperty(node)));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ScriptToTreeNodeAdapter.class) {
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

    public static Properties getClassNameToStringForm() {
        return classNameToStringForm;
    }

    public static void setClassNameToStringForm(Properties properties) {
        classNameToStringForm = properties;
    }

    public boolean getShowScriptFreeForm() {
        return this.showScriptFreeForm;
    }

    public boolean isShowScriptFreeForm() {
        return this.showScriptFreeForm;
    }

    public void setShowScriptFreeForm(boolean bl) {
        this.showScriptFreeForm = bl;
    }

    public boolean getShowScriptClass() {
        return this.showScriptClass;
    }

    public boolean isShowScriptClass() {
        return this.showScriptClass;
    }

    public void setShowScriptClass(boolean bl) {
        this.showScriptClass = bl;
    }

    public boolean getShowClosureClasses() {
        return this.showClosureClasses;
    }

    public boolean isShowClosureClasses() {
        return this.showClosureClasses;
    }

    public void setShowClosureClasses(boolean bl) {
        this.showClosureClasses = bl;
    }

    public final GroovyClassLoader getClassLoader() {
        return this.classLoader;
    }

    public final AstBrowserNodeMaker getNodeMaker() {
        return this.nodeMaker;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "classLoader";
        stringArray[2] = "getClass";
        stringArray[3] = "getSystemResource";
        stringArray[4] = "getResource";
        stringArray[5] = "classLoader";
        stringArray[6] = "parse";
        stringArray[7] = "<$constructor$>";
        stringArray[8] = "toProperties";
        stringArray[9] = "getProperty";
        stringArray[10] = "<$constructor$>";
        stringArray[11] = "plus";
        stringArray[12] = "plus";
        stringArray[13] = "separator";
        stringArray[14] = "exists";
        stringArray[15] = "parse";
        stringArray[16] = "<$constructor$>";
        stringArray[17] = "toURL";
        stringArray[18] = "putAll";
        stringArray[19] = "toProperties";
        stringArray[20] = "<$constructor$>";
        stringArray[21] = "plus";
        stringArray[22] = "plus";
        stringArray[23] = "currentTimeMillis";
        stringArray[24] = "<$constructor$>";
        stringArray[25] = "<$constructor$>";
        stringArray[26] = "DEFAULT";
        stringArray[27] = "codeSource";
        stringArray[28] = "setClassgenCallback";
        stringArray[29] = "createCollector";
        stringArray[30] = "<$constructor$>";
        stringArray[31] = "addPhaseOperation";
        stringArray[32] = "addSource";
        stringArray[33] = "getName";
        stringArray[34] = "compile";
        stringArray[35] = "add";
        stringArray[36] = "root";
        stringArray[37] = "makeNode";
        stringArray[38] = "eachLine";
        stringArray[39] = "message";
        stringArray[40] = "add";
        stringArray[41] = "root";
        stringArray[42] = "makeNode";
        stringArray[43] = "add";
        stringArray[44] = "root";
        stringArray[45] = "makeNode";
        stringArray[46] = "add";
        stringArray[47] = "root";
        stringArray[48] = "makeNode";
        stringArray[49] = "add";
        stringArray[50] = "root";
        stringArray[51] = "makeNode";
        stringArray[52] = "root";
        stringArray[53] = "makeNodeWithProperties";
        stringArray[54] = "getStringForm";
        stringArray[55] = "getPropertyTable";
        stringArray[56] = "getPropertyTable";
        stringArray[57] = "extendMethodNodePropertyTable";
        stringArray[58] = "makeNodeWithProperties";
        stringArray[59] = "getStringForm";
        stringArray[60] = "leftShift";
        stringArray[61] = "getMethodDescriptor";
        stringArray[62] = "sort";
        stringArray[63] = "collect";
        stringArray[64] = "findAll";
        stringArray[65] = "properties";
        stringArray[66] = "metaClass";
        stringArray[67] = "getAt";
        stringArray[68] = "name";
        stringArray[69] = "class";
        stringArray[70] = "<$constructor$>";
        stringArray[71] = "createTemplate";
        stringArray[72] = "make";
        stringArray[73] = "<$constructor$>";
        stringArray[74] = "writeTo";
        stringArray[75] = "toString";
        stringArray[76] = "simpleName";
        stringArray[77] = "class";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[78];
        ScriptToTreeNodeAdapter.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ScriptToTreeNodeAdapter.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ScriptToTreeNodeAdapter.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

