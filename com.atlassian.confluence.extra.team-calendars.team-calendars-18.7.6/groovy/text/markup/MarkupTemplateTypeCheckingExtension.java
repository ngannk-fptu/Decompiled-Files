/*
 * Decompiled with CFR 0.152.
 */
package groovy.text.markup;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.text.markup.BaseTemplate;
import groovy.text.markup.MarkupTemplateEngine;
import java.io.StringReader;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyRecognizer;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.ResolveVisitor;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.syntax.ParserException;
import org.codehaus.groovy.syntax.Reduction;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.syntax.Types;
import org.codehaus.groovy.transform.stc.GroovyTypeCheckingExtensionSupport;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport;
import org.codehaus.groovy.transform.stc.TypeCheckingContext;

public class MarkupTemplateTypeCheckingExtension
extends GroovyTypeCheckingExtensionSupport.TypeCheckingDSL {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public MarkupTemplateTypeCheckingExtension() {
        CallSite[] callSiteArray = MarkupTemplateTypeCheckingExtension.$getCallSiteArray();
    }

    @Override
    public Object run() {
        CallSite[] callSiteArray = MarkupTemplateTypeCheckingExtension.$getCallSiteArray();
        Reference<Object> modelTypesClassNodes = new Reference<Object>(null);
        public class _run_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference modelTypesClassNodes;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;
            private static /* synthetic */ Class $class$groovy$text$markup$MarkupTemplateEngine$TemplateGroovyClassLoader;

            public _run_closure1(Object _outerInstance, Object _thisObject, Reference modelTypesClassNodes) {
                Reference reference;
                CallSite[] callSiteArray = _run_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.modelTypesClassNodes = reference = modelTypesClassNodes;
            }

            public Object doCall(Object classNode) {
                CallSite[] callSiteArray = _run_closure1.$getCallSiteArray();
                Object modelTypes = callSiteArray[0].call(callSiteArray[1].callGetProperty(_run_closure1.$get$$class$groovy$text$markup$MarkupTemplateEngine$TemplateGroovyClassLoader()));
                if (ScriptBytecodeAdapter.compareNotEqual(modelTypes, null)) {
                    Map map = ScriptBytecodeAdapter.createMap(new Object[0]);
                    this.modelTypesClassNodes.set(map);
                    public class _closure7
                    extends Closure
                    implements GeneratedClosure {
                        private /* synthetic */ Reference modelTypesClassNodes;
                        private static /* synthetic */ ClassInfo $staticClassInfo;
                        public static transient /* synthetic */ boolean __$stMC;
                        private static /* synthetic */ SoftReference $callSiteArray;

                        public _closure7(Object _outerInstance, Object _thisObject, Reference modelTypesClassNodes) {
                            Reference reference;
                            CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                            super(_outerInstance, _thisObject);
                            this.modelTypesClassNodes = reference = modelTypesClassNodes;
                        }

                        public Object doCall(Object k, Object v) {
                            CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                            Object object = callSiteArray[0].callCurrent(this, v, callSiteArray[1].callGroovyObjectGetProperty(this));
                            callSiteArray[2].call(this.modelTypesClassNodes.get(), k, object);
                            return object;
                        }

                        public Object call(Object k, Object v) {
                            CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                            return callSiteArray[3].callCurrent(this, k, v);
                        }

                        public Object getModelTypesClassNodes() {
                            CallSite[] callSiteArray = _closure7.$getCallSiteArray();
                            return this.modelTypesClassNodes.get();
                        }

                        protected /* synthetic */ MetaClass $getStaticMetaClass() {
                            if (this.getClass() != _closure7.class) {
                                return ScriptBytecodeAdapter.initMetaClass(this);
                            }
                            ClassInfo classInfo = $staticClassInfo;
                            if (classInfo == null) {
                                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                            }
                            return classInfo.getMetaClass();
                        }

                        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                            stringArray[0] = "buildNodeFromString";
                            stringArray[1] = "context";
                            stringArray[2] = "putAt";
                            stringArray[3] = "doCall";
                        }

                        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                            String[] stringArray = new String[4];
                            _closure7.$createCallSiteArray_1(stringArray);
                            return new CallSiteArray(_closure7.class, stringArray);
                        }

                        private static /* synthetic */ CallSite[] $getCallSiteArray() {
                            CallSiteArray callSiteArray;
                            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                callSiteArray = _closure7.$createCallSiteArray();
                                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                            }
                            return callSiteArray.array;
                        }
                    }
                    callSiteArray[2].call(modelTypes, new _closure7(this, this.getThisObject(), this.modelTypesClassNodes));
                }
                Object modelTypesFromTemplate = callSiteArray[3].call(classNode, callSiteArray[4].callGetProperty(MarkupTemplateEngine.class));
                if (DefaultTypeTransformation.booleanUnbox(modelTypesFromTemplate)) {
                    if (ScriptBytecodeAdapter.compareEqual(this.modelTypesClassNodes.get(), null)) {
                        Object object = modelTypesFromTemplate;
                        this.modelTypesClassNodes.set(object);
                    } else {
                        callSiteArray[5].call(this.modelTypesClassNodes.get(), modelTypesFromTemplate);
                    }
                }
                if (ScriptBytecodeAdapter.compareEqual(this.modelTypesClassNodes.get(), null)) {
                    return callSiteArray[6].call(callSiteArray[7].callGroovyObjectGetProperty(this));
                }
                return null;
            }

            public Object getModelTypesClassNodes() {
                CallSite[] callSiteArray = _run_closure1.$getCallSiteArray();
                return this.modelTypesClassNodes.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _run_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "get";
                stringArray[1] = "modelTypes";
                stringArray[2] = "each";
                stringArray[3] = "getNodeMetaData";
                stringArray[4] = "MODELTYPES_ASTKEY";
                stringArray[5] = "putAll";
                stringArray[6] = "pushErrorCollector";
                stringArray[7] = "context";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[8];
                _run_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_run_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _run_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }

            private static /* synthetic */ Class $get$$class$groovy$text$markup$MarkupTemplateEngine$TemplateGroovyClassLoader() {
                Class clazz = $class$groovy$text$markup$MarkupTemplateEngine$TemplateGroovyClassLoader;
                if (clazz == null) {
                    clazz = $class$groovy$text$markup$MarkupTemplateEngine$TemplateGroovyClassLoader = _run_closure1.class$("groovy.text.markup.MarkupTemplateEngine$TemplateGroovyClassLoader");
                }
                return clazz;
            }

            static /* synthetic */ Class class$(String string) {
                try {
                    return Class.forName(string);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    throw new NoClassDefFoundError(classNotFoundException.getMessage());
                }
            }
        }
        callSiteArray[0].callCurrent((GroovyObject)this, new _run_closure1(this, this, modelTypesClassNodes));
        public class _run_closure2
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _run_closure2(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _run_closure2.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _run_closure2.$getCallSiteArray();
                public class _closure8
                extends Closure
                implements GeneratedClosure {
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure8(Object _outerInstance, Object _thisObject) {
                        CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                        List list = ScriptBytecodeAdapter.createList(new Object[0]);
                        ScriptBytecodeAdapter.setGroovyObjectProperty(list, _closure8.class, this, "builderCalls");
                        Map map = ScriptBytecodeAdapter.createMap(new Object[0]);
                        ScriptBytecodeAdapter.setGroovyObjectProperty(map, _closure8.class, this, "binaryExpressions");
                        return map;
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure8.class) {
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
                        return new CallSiteArray(_closure8.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure8.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                return callSiteArray[0].callCurrent((GroovyObject)this, new _closure8(this, this.getThisObject()));
            }

            public Object doCall() {
                CallSite[] callSiteArray = _run_closure2.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _run_closure2.class) {
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
                stringArray[0] = "newScope";
                return new CallSiteArray(_run_closure2.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _run_closure2.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[1].callCurrent((GroovyObject)this, new _run_closure2(this, this));
        public class _run_closure3
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference modelTypesClassNodes;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _run_closure3(Object _outerInstance, Object _thisObject, Reference modelTypesClassNodes) {
                Reference reference;
                CallSite[] callSiteArray = _run_closure3.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.modelTypesClassNodes = reference = modelTypesClassNodes;
            }

            public Object doCall(Object receiver, Object name, Object argList, Object argTypes, Object call) {
                CallSite[] callSiteArray = _run_closure3.$getCallSiteArray();
                if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    if (ScriptBytecodeAdapter.compareEqual("getAt", name) && ScriptBytecodeAdapter.compareEqual(callSiteArray[0].callGetProperty(ClassHelper.class), receiver)) {
                        Object enclosingBinaryExpression = callSiteArray[1].callGetProperty(callSiteArray[2].callGroovyObjectGetProperty(this));
                        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[3].call(callSiteArray[4].callGetProperty(enclosingBinaryExpression), callSiteArray[5].callGetProperty(call)))) {
                            Object stack = callSiteArray[6].callGetProperty(callSiteArray[7].callGroovyObjectGetProperty(this));
                            if (ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[8].call(stack), 1)) {
                                Object superEnclosing = callSiteArray[9].call(stack, 1);
                                Object opType = callSiteArray[10].callGetProperty(callSiteArray[11].callGetProperty(superEnclosing));
                                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[12].call(callSiteArray[13].callGetProperty(superEnclosing), enclosingBinaryExpression)) && DefaultTypeTransformation.booleanUnbox(callSiteArray[14].callStatic(StaticTypeCheckingSupport.class, opType))) {
                                    if (ScriptBytecodeAdapter.compareEqual(opType, callSiteArray[15].callGetProperty(Types.class))) {
                                        Object mce = callSiteArray[16].callConstructor(MethodCallExpression.class, callSiteArray[17].callGetProperty(enclosingBinaryExpression), "putAt", callSiteArray[18].callConstructor(ArgumentListExpression.class, callSiteArray[19].callGetProperty(enclosingBinaryExpression), callSiteArray[20].callGetProperty(superEnclosing)));
                                        callSiteArray[21].callCurrent((GroovyObject)this, mce);
                                        callSiteArray[22].call(callSiteArray[23].callGetProperty(callSiteArray[24].callGroovyObjectGetProperty(this)), superEnclosing, mce);
                                        return null;
                                    }
                                    throw (Throwable)callSiteArray[25].callConstructor(UnsupportedOperationException.class, new GStringImpl(new Object[]{callSiteArray[26].callGetProperty(superEnclosing)}, new String[]{"Operation not supported in templates: ", ". Please declare an explicit type for the variable."}));
                                }
                            }
                            callSiteArray[27].call(callSiteArray[28].callGetProperty(callSiteArray[29].callGroovyObjectGetProperty(this)), enclosingBinaryExpression, call);
                            return callSiteArray[30].callCurrent((GroovyObject)this, call);
                        }
                    }
                } else if (ScriptBytecodeAdapter.compareEqual("getAt", name) && ScriptBytecodeAdapter.compareEqual(callSiteArray[31].callGetProperty(ClassHelper.class), receiver)) {
                    Object enclosingBinaryExpression = callSiteArray[32].callGetProperty(callSiteArray[33].callGroovyObjectGetProperty(this));
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[34].call(callSiteArray[35].callGetProperty(enclosingBinaryExpression), callSiteArray[36].callGetProperty(call)))) {
                        Object stack = callSiteArray[37].callGetProperty(callSiteArray[38].callGroovyObjectGetProperty(this));
                        if (ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[39].call(stack), 1)) {
                            Object superEnclosing = callSiteArray[40].call(stack, 1);
                            Object opType = callSiteArray[41].callGetProperty(callSiteArray[42].callGetProperty(superEnclosing));
                            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[43].call(callSiteArray[44].callGetProperty(superEnclosing), enclosingBinaryExpression)) && DefaultTypeTransformation.booleanUnbox(callSiteArray[45].callStatic(StaticTypeCheckingSupport.class, opType))) {
                                if (ScriptBytecodeAdapter.compareEqual(opType, callSiteArray[46].callGetProperty(Types.class))) {
                                    Object mce = callSiteArray[47].callConstructor(MethodCallExpression.class, callSiteArray[48].callGetProperty(enclosingBinaryExpression), "putAt", callSiteArray[49].callConstructor(ArgumentListExpression.class, callSiteArray[50].callGetProperty(enclosingBinaryExpression), callSiteArray[51].callGetProperty(superEnclosing)));
                                    callSiteArray[52].callCurrent((GroovyObject)this, mce);
                                    callSiteArray[53].call(callSiteArray[54].callGetProperty(callSiteArray[55].callGroovyObjectGetProperty(this)), superEnclosing, mce);
                                    return null;
                                }
                                throw (Throwable)callSiteArray[56].callConstructor(UnsupportedOperationException.class, new GStringImpl(new Object[]{callSiteArray[57].callGetProperty(superEnclosing)}, new String[]{"Operation not supported in templates: ", ". Please declare an explicit type for the variable."}));
                            }
                        }
                        callSiteArray[58].call(callSiteArray[59].callGetProperty(callSiteArray[60].callGroovyObjectGetProperty(this)), enclosingBinaryExpression, call);
                        return callSiteArray[61].callCurrent((GroovyObject)this, call);
                    }
                }
                if (ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[62].callGetProperty(call), 0)) {
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[63].callGetProperty(call))) {
                        callSiteArray[64].call(callSiteArray[65].callGetProperty(callSiteArray[66].callGroovyObjectGetProperty(this)), call);
                        return callSiteArray[67].callCurrent(this, call, callSiteArray[68].callGetProperty(ClassHelper.class));
                    }
                    if (ScriptBytecodeAdapter.compareEqual(this.modelTypesClassNodes.get(), null)) {
                        return callSiteArray[69].callCurrent(this, call, callSiteArray[70].callGetProperty(ClassHelper.class));
                    }
                    return null;
                }
                return null;
            }

            public Object call(Object receiver, Object name, Object argList, Object argTypes, Object call) {
                CallSite[] callSiteArray = _run_closure3.$getCallSiteArray();
                return callSiteArray[71].callCurrent((GroovyObject)this, ArrayUtil.createArray(receiver, name, argList, argTypes, call));
            }

            public Object getModelTypesClassNodes() {
                CallSite[] callSiteArray = _run_closure3.$getCallSiteArray();
                return this.modelTypesClassNodes.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _run_closure3.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "OBJECT_TYPE";
                stringArray[1] = "enclosingBinaryExpression";
                stringArray[2] = "context";
                stringArray[3] = "is";
                stringArray[4] = "leftExpression";
                stringArray[5] = "objectExpression";
                stringArray[6] = "enclosingBinaryExpressionStack";
                stringArray[7] = "context";
                stringArray[8] = "size";
                stringArray[9] = "get";
                stringArray[10] = "type";
                stringArray[11] = "operation";
                stringArray[12] = "is";
                stringArray[13] = "leftExpression";
                stringArray[14] = "isAssignment";
                stringArray[15] = "ASSIGN";
                stringArray[16] = "<$constructor$>";
                stringArray[17] = "leftExpression";
                stringArray[18] = "<$constructor$>";
                stringArray[19] = "rightExpression";
                stringArray[20] = "rightExpression";
                stringArray[21] = "makeDynamic";
                stringArray[22] = "put";
                stringArray[23] = "binaryExpressions";
                stringArray[24] = "currentScope";
                stringArray[25] = "<$constructor$>";
                stringArray[26] = "text";
                stringArray[27] = "put";
                stringArray[28] = "binaryExpressions";
                stringArray[29] = "currentScope";
                stringArray[30] = "makeDynamic";
                stringArray[31] = "OBJECT_TYPE";
                stringArray[32] = "enclosingBinaryExpression";
                stringArray[33] = "context";
                stringArray[34] = "is";
                stringArray[35] = "leftExpression";
                stringArray[36] = "objectExpression";
                stringArray[37] = "enclosingBinaryExpressionStack";
                stringArray[38] = "context";
                stringArray[39] = "size";
                stringArray[40] = "get";
                stringArray[41] = "type";
                stringArray[42] = "operation";
                stringArray[43] = "is";
                stringArray[44] = "leftExpression";
                stringArray[45] = "isAssignment";
                stringArray[46] = "ASSIGN";
                stringArray[47] = "<$constructor$>";
                stringArray[48] = "leftExpression";
                stringArray[49] = "<$constructor$>";
                stringArray[50] = "rightExpression";
                stringArray[51] = "rightExpression";
                stringArray[52] = "makeDynamic";
                stringArray[53] = "put";
                stringArray[54] = "binaryExpressions";
                stringArray[55] = "currentScope";
                stringArray[56] = "<$constructor$>";
                stringArray[57] = "text";
                stringArray[58] = "put";
                stringArray[59] = "binaryExpressions";
                stringArray[60] = "currentScope";
                stringArray[61] = "makeDynamic";
                stringArray[62] = "lineNumber";
                stringArray[63] = "implicitThis";
                stringArray[64] = "leftShift";
                stringArray[65] = "builderCalls";
                stringArray[66] = "currentScope";
                stringArray[67] = "makeDynamic";
                stringArray[68] = "OBJECT_TYPE";
                stringArray[69] = "makeDynamic";
                stringArray[70] = "OBJECT_TYPE";
                stringArray[71] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[72];
                _run_closure3.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_run_closure3.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _run_closure3.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[2].callCurrent((GroovyObject)this, new _run_closure3(this, this, modelTypesClassNodes));
        public class _run_closure4
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference modelTypesClassNodes;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;
            private static /* synthetic */ Class $class$groovy$text$markup$MarkupBuilderCodeTransformer;

            public _run_closure4(Object _outerInstance, Object _thisObject, Reference modelTypesClassNodes) {
                Reference reference;
                CallSite[] callSiteArray = _run_closure4.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.modelTypesClassNodes = reference = modelTypesClassNodes;
            }

            public Object doCall(Object call, Object node) {
                CallSite[] callSiteArray = _run_closure4.$getCallSiteArray();
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].callCurrent((GroovyObject)this, call)) && ScriptBytecodeAdapter.compareNotEqual(this.modelTypesClassNodes.get(), null)) {
                    Object args = callSiteArray[1].callGetProperty(callSiteArray[2].callCurrent((GroovyObject)this, call));
                    if (ScriptBytecodeAdapter.compareEqual(callSiteArray[3].call(args), 1)) {
                        String varName = ShortTypeHandling.castToString(DefaultTypeTransformation.booleanUnbox(callSiteArray[4].callCurrent((GroovyObject)this, callSiteArray[5].call(args, 0))) ? callSiteArray[6].callGetProperty(callSiteArray[7].call(args, 0)) : callSiteArray[8].call(call, callSiteArray[9].callGetProperty(_run_closure4.$get$$class$groovy$text$markup$MarkupBuilderCodeTransformer())));
                        Object type = callSiteArray[10].call(this.modelTypesClassNodes.get(), varName);
                        if (DefaultTypeTransformation.booleanUnbox(type)) {
                            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[11].callGetProperty(callSiteArray[12].callGetProperty(call)), "this.getModel()")) {
                                return callSiteArray[13].callCurrent(this, call, type);
                            }
                            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[14].callGetProperty(call), "tryEscape")) {
                                return callSiteArray[15].callCurrent(this, call, type);
                            }
                            return null;
                        }
                        return null;
                    }
                    return null;
                }
                return null;
            }

            public Object call(Object call, Object node) {
                CallSite[] callSiteArray = _run_closure4.$getCallSiteArray();
                return callSiteArray[16].callCurrent(this, call, node);
            }

            public Object getModelTypesClassNodes() {
                CallSite[] callSiteArray = _run_closure4.$getCallSiteArray();
                return this.modelTypesClassNodes.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _run_closure4.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "isMethodCallExpression";
                stringArray[1] = "expressions";
                stringArray[2] = "getArguments";
                stringArray[3] = "size";
                stringArray[4] = "isConstantExpression";
                stringArray[5] = "getAt";
                stringArray[6] = "text";
                stringArray[7] = "getAt";
                stringArray[8] = "getNodeMetaData";
                stringArray[9] = "TARGET_VARIABLE";
                stringArray[10] = "getAt";
                stringArray[11] = "text";
                stringArray[12] = "objectExpression";
                stringArray[13] = "storeType";
                stringArray[14] = "methodAsString";
                stringArray[15] = "storeType";
                stringArray[16] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[17];
                _run_closure4.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_run_closure4.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _run_closure4.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }

            private static /* synthetic */ Class $get$$class$groovy$text$markup$MarkupBuilderCodeTransformer() {
                Class clazz = $class$groovy$text$markup$MarkupBuilderCodeTransformer;
                if (clazz == null) {
                    clazz = $class$groovy$text$markup$MarkupBuilderCodeTransformer = _run_closure4.class$("groovy.text.markup.MarkupBuilderCodeTransformer");
                }
                return clazz;
            }

            static /* synthetic */ Class class$(String string) {
                try {
                    return Class.forName(string);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    throw new NoClassDefFoundError(classNotFoundException.getMessage());
                }
            }
        }
        callSiteArray[3].callCurrent((GroovyObject)this, new _run_closure4(this, this, modelTypesClassNodes));
        public class _run_closure5
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference modelTypesClassNodes;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _run_closure5(Object _outerInstance, Object _thisObject, Reference modelTypesClassNodes) {
                Reference reference;
                CallSite[] callSiteArray = _run_closure5.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.modelTypesClassNodes = reference = modelTypesClassNodes;
            }

            public Object doCall(Object pexp) {
                CallSite[] callSiteArray = _run_closure5.$getCallSiteArray();
                if (ScriptBytecodeAdapter.compareEqual(callSiteArray[0].callGetProperty(callSiteArray[1].callGetProperty(pexp)), "this.getModel()")) {
                    if (ScriptBytecodeAdapter.compareNotEqual(this.modelTypesClassNodes.get(), null)) {
                        Object type = callSiteArray[2].call(this.modelTypesClassNodes.get(), callSiteArray[3].callGetProperty(pexp));
                        if (DefaultTypeTransformation.booleanUnbox(type)) {
                            return callSiteArray[4].callCurrent(this, pexp, type);
                        }
                        return null;
                    }
                    return callSiteArray[5].callCurrent((GroovyObject)this, pexp);
                }
                if (ScriptBytecodeAdapter.compareEqual(this.modelTypesClassNodes.get(), null)) {
                    return callSiteArray[6].callCurrent((GroovyObject)this, pexp);
                }
                return null;
            }

            public Object getModelTypesClassNodes() {
                CallSite[] callSiteArray = _run_closure5.$getCallSiteArray();
                return this.modelTypesClassNodes.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _run_closure5.class) {
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
                stringArray[1] = "objectExpression";
                stringArray[2] = "getAt";
                stringArray[3] = "propertyAsString";
                stringArray[4] = "makeDynamic";
                stringArray[5] = "makeDynamic";
                stringArray[6] = "makeDynamic";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[7];
                _run_closure5.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_run_closure5.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _run_closure5.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[4].callCurrent((GroovyObject)this, new _run_closure5(this, this, modelTypesClassNodes));
        public class _run_closure6
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _run_closure6(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _run_closure6.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object mn) {
                Reference<Object> mn2 = new Reference<Object>(mn);
                CallSite[] callSiteArray = _run_closure6.$getCallSiteArray();
                public class _closure9
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference mn;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure9(Object _outerInstance, Object _thisObject, Reference mn) {
                        Reference reference;
                        CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.mn = reference = mn;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                        return callSiteArray[0].call(callSiteArray[1].callConstructor(BuilderMethodReplacer.class, callSiteArray[2].callGetProperty(callSiteArray[3].callGroovyObjectGetProperty(this)), callSiteArray[4].callGroovyObjectGetProperty(this), callSiteArray[5].callGroovyObjectGetProperty(this)), this.mn.get());
                    }

                    public Object getMn() {
                        CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                        return this.mn.get();
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure9.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "visitMethod";
                        stringArray[1] = "<$constructor$>";
                        stringArray[2] = "source";
                        stringArray[3] = "context";
                        stringArray[4] = "builderCalls";
                        stringArray[5] = "binaryExpressions";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[6];
                        _closure9.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_closure9.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure9.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                return callSiteArray[0].callCurrent((GroovyObject)this, new _closure9(this, this.getThisObject(), mn2));
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _run_closure6.class) {
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
                stringArray[0] = "scopeExit";
                return new CallSiteArray(_run_closure6.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _run_closure6.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return callSiteArray[5].callCurrent((GroovyObject)this, new _run_closure6(this, this));
    }

    private static ClassNode buildNodeFromString(String option, TypeCheckingContext ctx) {
        Reference<TypeCheckingContext> ctx2 = new Reference<TypeCheckingContext>(ctx);
        GroovyLexer lexer = new GroovyLexer(new StringReader(option));
        Reference<GroovyRecognizer> rn = new Reference<GroovyRecognizer>(GroovyRecognizer.make(lexer));
        rn.get().classOrInterfaceType(true);
        Reference ref = new Reference(new AtomicReference());
        GroovyObject plugin = new GroovyObject(MarkupTemplateTypeCheckingExtension.class, ref, rn){
            public /* synthetic */ Reference rn;
            public /* synthetic */ Reference ref;
            public /* synthetic */ Class this$0;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private transient /* synthetic */ MetaClass metaClass;
            private static /* synthetic */ SoftReference $callSiteArray;
            {
                MetaClass metaClass;
                Reference reference;
                Reference reference2;
                Class clazz;
                CallSite[] callSiteArray = 1.$getCallSiteArray();
                this.this$0 = clazz = p0;
                this.rn = reference2 = p2;
                this.ref = reference = p1;
                this.metaClass = metaClass = this.$getStaticMetaClass();
            }

            @Override
            public ModuleNode buildAST(SourceUnit sourceUnit, ClassLoader classLoader, Reduction cst) throws ParserException {
                CallSite[] callSiteArray = 1.$getCallSiteArray();
                callSiteArray[0].call(this.ref.get(), callSiteArray[1].callCurrent((GroovyObject)this, callSiteArray[2].call(this.rn.get())));
                return (ModuleNode)ScriptBytecodeAdapter.castToType(null, ModuleNode.class);
            }

            public /* synthetic */ Object methodMissing(String name, Object args) {
                CallSite[] callSiteArray = 1.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodN(1.class, MarkupTemplateTypeCheckingExtension.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
            }

            public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
                CallSite[] callSiteArray = 1.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodN(1.class, MarkupTemplateTypeCheckingExtension.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
            }

            public /* synthetic */ void propertyMissing(String name, Object val) {
                CallSite[] callSiteArray = 1.$getCallSiteArray();
                Object object = val;
                ScriptBytecodeAdapter.setProperty(object, null, MarkupTemplateTypeCheckingExtension.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
                CallSite[] callSiteArray = 1.$getCallSiteArray();
                Object object = val;
                ScriptBytecodeAdapter.setProperty(object, null, MarkupTemplateTypeCheckingExtension.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public /* synthetic */ Object propertyMissing(String name) {
                CallSite[] callSiteArray = 1.$getCallSiteArray();
                return ScriptBytecodeAdapter.getProperty(1.class, MarkupTemplateTypeCheckingExtension.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public static /* synthetic */ Object $static_propertyMissing(String name) {
                CallSite[] callSiteArray = 1.$getCallSiteArray();
                return ScriptBytecodeAdapter.getProperty(1.class, MarkupTemplateTypeCheckingExtension.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != 1.class) {
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

            public /* synthetic */ ModuleNode super$3$buildAST(SourceUnit sourceUnit, ClassLoader classLoader, Reduction reduction) {
                return super.buildAST(sourceUnit, classLoader, reduction);
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "set";
                stringArray[1] = "makeTypeWithArguments";
                stringArray[2] = "getAST";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[3];
                1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = 1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        };
        plugin.buildAST(null, null, null);
        ClassNode parsedNode = (ClassNode)ScriptBytecodeAdapter.castToType(ref.get().get(), ClassNode.class);
        ClassNode dummyClass = new ClassNode("dummy", 0, ClassHelper.OBJECT_TYPE);
        dummyClass.setModule(new ModuleNode(ctx2.get().getSource()));
        MethodNode dummyMN = new MethodNode("dummy", 0, parsedNode, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, EmptyStatement.INSTANCE);
        dummyClass.addMethod(dummyMN);
        GroovyObject visitor = new GroovyObject(MarkupTemplateTypeCheckingExtension.class, ctx2, ctx2.get().getCompilationUnit()){
            public /* synthetic */ Reference ctx;
            public /* synthetic */ Class this$0;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private transient /* synthetic */ MetaClass metaClass;
            private static /* synthetic */ SoftReference $callSiteArray;
            {
                MetaClass metaClass;
                Reference reference;
                Class clazz;
                CallSite[] callSiteArray = 2.$getCallSiteArray();
                this.this$0 = clazz = p0;
                super((CompilationUnit)ScriptBytecodeAdapter.castToType(p3, CompilationUnit.class));
                this.ctx = reference = p1;
                this.metaClass = metaClass = this.$getStaticMetaClass();
            }

            @Override
            protected void addError(String msg, ASTNode expr) {
                CallSite[] callSiteArray = 2.$getCallSiteArray();
                callSiteArray[0].call(callSiteArray[1].callGetProperty(this.ctx.get()), callSiteArray[2].callConstructor(SyntaxErrorMessage.class, callSiteArray[3].callConstructor((Object)SyntaxException.class, ArrayUtil.createArray(callSiteArray[4].call((Object)msg, "\n"), callSiteArray[5].call(expr), callSiteArray[6].call(expr), callSiteArray[7].call(expr), callSiteArray[8].call(expr))), callSiteArray[9].callGetProperty(this.ctx.get())));
            }

            public /* synthetic */ Object methodMissing(String name, Object args) {
                CallSite[] callSiteArray = 2.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodN(2.class, MarkupTemplateTypeCheckingExtension.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
            }

            public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
                CallSite[] callSiteArray = 2.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodN(2.class, MarkupTemplateTypeCheckingExtension.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
            }

            public /* synthetic */ void propertyMissing(String name, Object val) {
                CallSite[] callSiteArray = 2.$getCallSiteArray();
                Object object = val;
                ScriptBytecodeAdapter.setProperty(object, null, MarkupTemplateTypeCheckingExtension.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
                CallSite[] callSiteArray = 2.$getCallSiteArray();
                Object object = val;
                ScriptBytecodeAdapter.setProperty(object, null, MarkupTemplateTypeCheckingExtension.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public /* synthetic */ Object propertyMissing(String name) {
                CallSite[] callSiteArray = 2.$getCallSiteArray();
                return ScriptBytecodeAdapter.getProperty(2.class, MarkupTemplateTypeCheckingExtension.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public static /* synthetic */ Object $static_propertyMissing(String name) {
                CallSite[] callSiteArray = 2.$getCallSiteArray();
                return ScriptBytecodeAdapter.getProperty(2.class, MarkupTemplateTypeCheckingExtension.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != 2.class) {
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

            public /* synthetic */ void super$3$addError(String string, ASTNode aSTNode) {
                super.addError(string, aSTNode);
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "addErrorAndContinue";
                stringArray[1] = "errorCollector";
                stringArray[2] = "<$constructor$>";
                stringArray[3] = "<$constructor$>";
                stringArray[4] = "plus";
                stringArray[5] = "getLineNumber";
                stringArray[6] = "getColumnNumber";
                stringArray[7] = "getLastLineNumber";
                stringArray[8] = "getLastColumnNumber";
                stringArray[9] = "source";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[10];
                2.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(2.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = 2.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        };
        ((ResolveVisitor)((Object)visitor)).startResolving(dummyClass, ctx2.get().getSource());
        return dummyMN.getReturnType();
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != MarkupTemplateTypeCheckingExtension.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ Object this$dist$invoke$4(String name, Object args) {
        CallSite[] callSiteArray = MarkupTemplateTypeCheckingExtension.$getCallSiteArray();
        return ScriptBytecodeAdapter.invokeMethodOnCurrentN(MarkupTemplateTypeCheckingExtension.class, this, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
    }

    public /* synthetic */ void this$dist$set$4(String name, Object value) {
        CallSite[] callSiteArray = MarkupTemplateTypeCheckingExtension.$getCallSiteArray();
        Object object = value;
        ScriptBytecodeAdapter.setGroovyObjectProperty(object, MarkupTemplateTypeCheckingExtension.class, this, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
    }

    public /* synthetic */ Object this$dist$get$4(String name) {
        CallSite[] callSiteArray = MarkupTemplateTypeCheckingExtension.$getCallSiteArray();
        return ScriptBytecodeAdapter.getGroovyObjectProperty(MarkupTemplateTypeCheckingExtension.class, this, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "beforeVisitClass";
        stringArray[1] = "beforeVisitMethod";
        stringArray[2] = "methodNotFound";
        stringArray[3] = "onMethodSelection";
        stringArray[4] = "unresolvedProperty";
        stringArray[5] = "afterVisitMethod";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[6];
        MarkupTemplateTypeCheckingExtension.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(MarkupTemplateTypeCheckingExtension.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = MarkupTemplateTypeCheckingExtension.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }

    static class BuilderMethodReplacer
    extends ClassCodeExpressionTransformer
    implements GroovyObject {
        private static final MethodNode METHOD_MISSING;
        private final SourceUnit unit;
        private final Set<MethodCallExpression> callsToBeReplaced;
        private final Map<BinaryExpression, MethodCallExpression> binaryExpressionsToBeReplaced;
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private transient /* synthetic */ MetaClass metaClass;
        private static /* synthetic */ ClassInfo $staticClassInfo$;
        private static /* synthetic */ SoftReference $callSiteArray;

        public BuilderMethodReplacer(SourceUnit unit, Collection<MethodCallExpression> calls, Map<BinaryExpression, MethodCallExpression> binExpressionsWithReplacements) {
            MetaClass metaClass;
            CallSite[] callSiteArray = BuilderMethodReplacer.$getCallSiteArray();
            this.metaClass = metaClass = this.$getStaticMetaClass();
            SourceUnit sourceUnit = unit;
            this.unit = (SourceUnit)ScriptBytecodeAdapter.castToType(sourceUnit, SourceUnit.class);
            Set set = (Set)ScriptBytecodeAdapter.asType(calls, Set.class);
            this.callsToBeReplaced = (Set)ScriptBytecodeAdapter.castToType(set, Set.class);
            Map<BinaryExpression, MethodCallExpression> map = binExpressionsWithReplacements;
            this.binaryExpressionsToBeReplaced = (Map)ScriptBytecodeAdapter.castToType(map, Map.class);
        }

        @Override
        protected SourceUnit getSourceUnit() {
            CallSite[] callSiteArray = BuilderMethodReplacer.$getCallSiteArray();
            return this.unit;
        }

        @Override
        public void visitClosureExpression(ClosureExpression expression) {
            CallSite[] callSiteArray = BuilderMethodReplacer.$getCallSiteArray();
            ScriptBytecodeAdapter.invokeMethodOnSuperN(ClassCodeExpressionTransformer.class, this, "visitClosureExpression", new Object[]{expression});
        }

        @Override
        public Expression transform(Expression exp) {
            CallSite[] callSiteArray = BuilderMethodReplacer.$getCallSiteArray();
            if (exp instanceof BinaryExpression && DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(this.binaryExpressionsToBeReplaced, exp))) {
                return (Expression)ScriptBytecodeAdapter.castToType(callSiteArray[1].call(this.binaryExpressionsToBeReplaced, exp), Expression.class);
            }
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call(this.callsToBeReplaced, exp))) {
                Object args = callSiteArray[3].callGetProperty(exp) instanceof TupleExpression ? callSiteArray[4].callGetProperty(callSiteArray[5].callGetProperty(exp)) : ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[6].callGetProperty(exp)});
                ScriptBytecodeAdapter.invokeMethodNSpreadSafe(BuilderMethodReplacer.class, args, "visit", new Object[]{this});
                Object call = callSiteArray[7].callConstructor(MethodCallExpression.class, callSiteArray[8].callConstructor(VariableExpression.class, "this"), "methodMissing", callSiteArray[9].callConstructor(ArgumentListExpression.class, callSiteArray[10].callConstructor(ConstantExpression.class, callSiteArray[11].call(exp)), callSiteArray[12].callConstructor(ArrayExpression.class, callSiteArray[13].callGetProperty(ClassHelper.class), ScriptBytecodeAdapter.createList(ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0})))));
                boolean bl = true;
                ScriptBytecodeAdapter.setProperty(bl, null, call, "implicitThis");
                Object object = callSiteArray[14].callGetProperty(exp);
                ScriptBytecodeAdapter.setProperty(object, null, call, "safe");
                Object object2 = callSiteArray[15].callGetProperty(exp);
                ScriptBytecodeAdapter.setProperty(object2, null, call, "spreadSafe");
                MethodNode methodNode = METHOD_MISSING;
                ScriptBytecodeAdapter.setProperty(methodNode, null, call, "methodTarget");
                return (Expression)ScriptBytecodeAdapter.castToType(call, Expression.class);
            }
            if (exp instanceof ClosureExpression) {
                callSiteArray[16].call(callSiteArray[17].callGetProperty(exp), this);
                return (Expression)ScriptBytecodeAdapter.castToType(ScriptBytecodeAdapter.invokeMethodOnSuperN(ClassCodeExpressionTransformer.class, this, "transform", new Object[]{exp}), Expression.class);
            }
            return (Expression)ScriptBytecodeAdapter.castToType(ScriptBytecodeAdapter.invokeMethodOnSuperN(ClassCodeExpressionTransformer.class, this, "transform", new Object[]{exp}), Expression.class);
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != BuilderMethodReplacer.class) {
                return ScriptBytecodeAdapter.initMetaClass(this);
            }
            ClassInfo classInfo = $staticClassInfo;
            if (classInfo == null) {
                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
            }
            return classInfo.getMetaClass();
        }

        public /* synthetic */ Object methodMissing(String name, Object args) {
            CallSite[] callSiteArray = BuilderMethodReplacer.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(BuilderMethodReplacer.class, MarkupTemplateTypeCheckingExtension.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
            CallSite[] callSiteArray = BuilderMethodReplacer.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(BuilderMethodReplacer.class, MarkupTemplateTypeCheckingExtension.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public /* synthetic */ void propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = BuilderMethodReplacer.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, MarkupTemplateTypeCheckingExtension.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = BuilderMethodReplacer.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, MarkupTemplateTypeCheckingExtension.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public /* synthetic */ Object propertyMissing(String name) {
            CallSite[] callSiteArray = BuilderMethodReplacer.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(BuilderMethodReplacer.class, MarkupTemplateTypeCheckingExtension.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ Object $static_propertyMissing(String name) {
            CallSite[] callSiteArray = BuilderMethodReplacer.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(BuilderMethodReplacer.class, MarkupTemplateTypeCheckingExtension.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
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
            Object object = BuilderMethodReplacer.$getCallSiteArray()[18].call(BuilderMethodReplacer.$getCallSiteArray()[19].call(BuilderMethodReplacer.$getCallSiteArray()[20].call(ClassHelper.class, BaseTemplate.class), "methodMissing"), 0);
            METHOD_MISSING = (MethodNode)ScriptBytecodeAdapter.castToType(object, MethodNode.class);
        }

        public /* synthetic */ void super$2$visitClosureExpression(ClosureExpression closureExpression) {
            super.visitClosureExpression(closureExpression);
        }

        public /* synthetic */ Expression super$4$transform(Expression expression) {
            return super.transform(expression);
        }

        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
            stringArray[0] = "containsKey";
            stringArray[1] = "get";
            stringArray[2] = "contains";
            stringArray[3] = "arguments";
            stringArray[4] = "expressions";
            stringArray[5] = "arguments";
            stringArray[6] = "arguments";
            stringArray[7] = "<$constructor$>";
            stringArray[8] = "<$constructor$>";
            stringArray[9] = "<$constructor$>";
            stringArray[10] = "<$constructor$>";
            stringArray[11] = "getMethodAsString";
            stringArray[12] = "<$constructor$>";
            stringArray[13] = "OBJECT_TYPE";
            stringArray[14] = "safe";
            stringArray[15] = "spreadSafe";
            stringArray[16] = "visit";
            stringArray[17] = "code";
            stringArray[18] = "getAt";
            stringArray[19] = "getMethods";
            stringArray[20] = "make";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[21];
            BuilderMethodReplacer.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(BuilderMethodReplacer.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = BuilderMethodReplacer.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }
}

