/*
 * Decompiled with CFR 0.152.
 */
package groovy.inspect.swingui;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import java.io.Writer;
import java.lang.ref.SoftReference;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Stack;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.GroovyClassVisitor;
import org.codehaus.groovy.ast.GroovyCodeVisitor;
import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.PackageNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.AttributeExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ClosureListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.ElvisOperatorExpression;
import org.codehaus.groovy.ast.expr.EmptyExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.MethodPointerExpression;
import org.codehaus.groovy.ast.expr.NotExpression;
import org.codehaus.groovy.ast.expr.PostfixExpression;
import org.codehaus.groovy.ast.expr.PrefixExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.RangeExpression;
import org.codehaus.groovy.ast.expr.SpreadExpression;
import org.codehaus.groovy.ast.expr.SpreadMapExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.TernaryExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.UnaryMinusExpression;
import org.codehaus.groovy.ast.expr.UnaryPlusExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.AssertStatement;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.BreakStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.ContinueStatement;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.classgen.BytecodeExpression;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.classgen.Verifier;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class AstNodeToScriptVisitor
extends CompilationUnit.PrimaryClassNodeOperation
implements GroovyCodeVisitor,
GroovyClassVisitor,
GroovyObject {
    private final Writer _out;
    private Stack<String> classNameStack;
    private String _indent;
    private boolean readyToIndent;
    private boolean showScriptFreeForm;
    private boolean showScriptClass;
    private boolean scriptHasBeenVisited;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public AstNodeToScriptVisitor(Writer writer, boolean showScriptFreeForm, boolean showScriptClass) {
        MetaClass metaClass;
        boolean bl;
        String string;
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        Object object = callSiteArray[0].callConstructor(Stack.class);
        this.classNameStack = (Stack)ScriptBytecodeAdapter.castToType(object, Stack.class);
        this._indent = string = "";
        this.readyToIndent = bl = true;
        this.metaClass = metaClass = this.$getStaticMetaClass();
        Writer writer2 = writer;
        this._out = (Writer)ScriptBytecodeAdapter.castToType(writer2, Writer.class);
        boolean bl2 = showScriptFreeForm;
        this.showScriptFreeForm = DefaultTypeTransformation.booleanUnbox(bl2);
        boolean bl3 = showScriptClass;
        this.showScriptClass = DefaultTypeTransformation.booleanUnbox(bl3);
        boolean bl4 = false;
        this.scriptHasBeenVisited = DefaultTypeTransformation.booleanUnbox(bl4);
    }

    public AstNodeToScriptVisitor(Writer writer, boolean showScriptFreeForm) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        this(writer, showScriptFreeForm, true);
    }

    public AstNodeToScriptVisitor(Writer writer) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        this(writer, true, true);
    }

    @Override
    public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[1].callCurrent((GroovyObject)this, callSiteArray[2].callSafe(callSiteArray[3].callSafe(source)));
        callSiteArray[4].callCurrent((GroovyObject)this, source);
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (this.showScriptFreeForm && !this.scriptHasBeenVisited) {
                boolean bl;
                this.scriptHasBeenVisited = bl = true;
                callSiteArray[5].callSafe(callSiteArray[6].callSafe(callSiteArray[7].callSafe(source)), this);
            }
        } else if (this.showScriptFreeForm && !this.scriptHasBeenVisited) {
            boolean bl;
            this.scriptHasBeenVisited = bl = true;
            callSiteArray[8].callSafe(callSiteArray[9].callSafe(callSiteArray[10].callSafe(source)), this);
        }
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (this.showScriptClass || !DefaultTypeTransformation.booleanUnbox(callSiteArray[11].call(classNode))) {
                callSiteArray[12].callCurrent((GroovyObject)this, classNode);
            }
        } else if (this.showScriptClass || !DefaultTypeTransformation.booleanUnbox(callSiteArray[13].call(classNode))) {
            callSiteArray[14].callCurrent((GroovyObject)this, classNode);
        }
    }

    private Object visitAllImports(SourceUnit source) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        Reference<Boolean> staticImportsPresent = new Reference<Boolean>(false);
        Reference<Boolean> importsPresent = new Reference<Boolean>(false);
        public class _visitAllImports_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference staticImportsPresent;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitAllImports_closure1(Object _outerInstance, Object _thisObject, Reference staticImportsPresent) {
                Reference reference;
                CallSite[] callSiteArray = _visitAllImports_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.staticImportsPresent = reference = staticImportsPresent;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitAllImports_closure1.$getCallSiteArray();
                callSiteArray[0].callCurrent((GroovyObject)this, it);
                boolean bl = true;
                this.staticImportsPresent.set(bl);
                return bl;
            }

            public Boolean getStaticImportsPresent() {
                CallSite[] callSiteArray = _visitAllImports_closure1.$getCallSiteArray();
                return (Boolean)ScriptBytecodeAdapter.castToType(this.staticImportsPresent.get(), Boolean.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitAllImports_closure1.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitAllImports_closure1.class) {
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
                stringArray[0] = "visitImport";
                return new CallSiteArray(_visitAllImports_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitAllImports_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[15].callSafe(callSiteArray[16].callSafe(callSiteArray[17].callSafe(callSiteArray[18].callSafe(source))), new _visitAllImports_closure1(this, this, staticImportsPresent));
        public class _visitAllImports_closure2
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference staticImportsPresent;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitAllImports_closure2(Object _outerInstance, Object _thisObject, Reference staticImportsPresent) {
                Reference reference;
                CallSite[] callSiteArray = _visitAllImports_closure2.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.staticImportsPresent = reference = staticImportsPresent;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitAllImports_closure2.$getCallSiteArray();
                callSiteArray[0].callCurrent((GroovyObject)this, it);
                boolean bl = true;
                this.staticImportsPresent.set(bl);
                return bl;
            }

            public Boolean getStaticImportsPresent() {
                CallSite[] callSiteArray = _visitAllImports_closure2.$getCallSiteArray();
                return (Boolean)ScriptBytecodeAdapter.castToType(this.staticImportsPresent.get(), Boolean.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitAllImports_closure2.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitAllImports_closure2.class) {
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
                stringArray[0] = "visitImport";
                return new CallSiteArray(_visitAllImports_closure2.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitAllImports_closure2.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[19].callSafe(callSiteArray[20].callSafe(callSiteArray[21].callSafe(callSiteArray[22].callSafe(source))), new _visitAllImports_closure2(this, this, staticImportsPresent));
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (DefaultTypeTransformation.booleanUnbox(staticImportsPresent.get())) {
                callSiteArray[23].callCurrent(this);
            }
        } else if (DefaultTypeTransformation.booleanUnbox(staticImportsPresent.get())) {
            this.printDoubleBreak();
        }
        public class _visitAllImports_closure3
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference importsPresent;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitAllImports_closure3(Object _outerInstance, Object _thisObject, Reference importsPresent) {
                Reference reference;
                CallSite[] callSiteArray = _visitAllImports_closure3.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.importsPresent = reference = importsPresent;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitAllImports_closure3.$getCallSiteArray();
                callSiteArray[0].callCurrent((GroovyObject)this, it);
                boolean bl = true;
                this.importsPresent.set(bl);
                return bl;
            }

            public Boolean getImportsPresent() {
                CallSite[] callSiteArray = _visitAllImports_closure3.$getCallSiteArray();
                return (Boolean)ScriptBytecodeAdapter.castToType(this.importsPresent.get(), Boolean.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitAllImports_closure3.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitAllImports_closure3.class) {
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
                stringArray[0] = "visitImport";
                return new CallSiteArray(_visitAllImports_closure3.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitAllImports_closure3.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[24].callSafe(callSiteArray[25].callSafe(callSiteArray[26].callSafe(source)), new _visitAllImports_closure3(this, this, importsPresent));
        public class _visitAllImports_closure4
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference importsPresent;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitAllImports_closure4(Object _outerInstance, Object _thisObject, Reference importsPresent) {
                Reference reference;
                CallSite[] callSiteArray = _visitAllImports_closure4.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.importsPresent = reference = importsPresent;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitAllImports_closure4.$getCallSiteArray();
                callSiteArray[0].callCurrent((GroovyObject)this, it);
                boolean bl = true;
                this.importsPresent.set(bl);
                return bl;
            }

            public Boolean getImportsPresent() {
                CallSite[] callSiteArray = _visitAllImports_closure4.$getCallSiteArray();
                return (Boolean)ScriptBytecodeAdapter.castToType(this.importsPresent.get(), Boolean.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitAllImports_closure4.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitAllImports_closure4.class) {
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
                stringArray[0] = "visitImport";
                return new CallSiteArray(_visitAllImports_closure4.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitAllImports_closure4.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[27].callSafe(callSiteArray[28].callSafe(callSiteArray[29].callSafe(source)), new _visitAllImports_closure4(this, this, importsPresent));
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (DefaultTypeTransformation.booleanUnbox(importsPresent.get())) {
                return callSiteArray[30].callCurrent(this);
            }
            return null;
        }
        if (DefaultTypeTransformation.booleanUnbox(importsPresent.get())) {
            return this.printDoubleBreak();
        }
        return null;
    }

    public void print(Object parameter) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        Object output = callSiteArray[31].call(parameter);
        if (this.readyToIndent) {
            boolean bl;
            callSiteArray[32].call((Object)this._out, this._indent);
            this.readyToIndent = bl = false;
            while (DefaultTypeTransformation.booleanUnbox(callSiteArray[33].call(output, " "))) {
                Object object;
                output = object = callSiteArray[34].call(output, ScriptBytecodeAdapter.createRange(1, -1, true));
            }
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[35].call(callSiteArray[36].call(this._out), " ")) && DefaultTypeTransformation.booleanUnbox(callSiteArray[37].call(output, " "))) {
            Object object;
            output = object = callSiteArray[38].call(output, ScriptBytecodeAdapter.createRange(1, -1, true));
        }
        callSiteArray[39].call((Object)this._out, output);
    }

    public Object println(Object parameter) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        throw (Throwable)callSiteArray[40].callConstructor(UnsupportedOperationException.class, "Wrong API");
    }

    public Object indented(Closure block) {
        String string;
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        String startingIndent = this._indent;
        Object object = callSiteArray[41].call((Object)this._indent, "    ");
        this._indent = ShortTypeHandling.castToString(object);
        callSiteArray[42].call(block);
        this._indent = string = startingIndent;
        return string;
    }

    public Object printLineBreak() {
        boolean bl;
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[43].call(callSiteArray[44].call(this._out), "\n"))) {
            callSiteArray[45].call((Object)this._out, "\n");
        }
        this.readyToIndent = bl = true;
        return bl;
    }

    public Object printDoubleBreak() {
        boolean bl;
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[46].call(callSiteArray[47].call(this._out), "\n\n"))) {
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[48].call(callSiteArray[49].call(this._out), "\n"))) {
                callSiteArray[50].call((Object)this._out, "\n");
            } else {
                callSiteArray[51].call((Object)this._out, "\n");
                callSiteArray[52].call((Object)this._out, "\n");
            }
        }
        this.readyToIndent = bl = true;
        return bl;
    }

    public void visitPackage(PackageNode packageNode) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(packageNode)) {
            public class _visitPackage_closure5
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _visitPackage_closure5(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _visitPackage_closure5.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _visitPackage_closure5.$getCallSiteArray();
                    callSiteArray[0].callCurrent((GroovyObject)this, it);
                    return callSiteArray[1].callCurrent(this);
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _visitPackage_closure5.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _visitPackage_closure5.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "visitAnnotationNode";
                    stringArray[1] = "printLineBreak";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[2];
                    _visitPackage_closure5.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_visitPackage_closure5.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _visitPackage_closure5.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[53].callSafe(callSiteArray[54].callGetProperty(packageNode), new _visitPackage_closure5(this, this));
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[55].call(callSiteArray[56].callGetProperty(packageNode), "."))) {
                callSiteArray[57].callCurrent((GroovyObject)this, callSiteArray[58].call(callSiteArray[59].callGetProperty(packageNode), ScriptBytecodeAdapter.createRange(0, -2, true)));
            } else {
                callSiteArray[60].callCurrent((GroovyObject)this, callSiteArray[61].callGetProperty(packageNode));
            }
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                callSiteArray[62].callCurrent(this);
            } else {
                this.printDoubleBreak();
            }
        }
    }

    public void visitImport(ImportNode node) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(node)) {
            public class _visitImport_closure6
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _visitImport_closure6(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _visitImport_closure6.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _visitImport_closure6.$getCallSiteArray();
                    callSiteArray[0].callCurrent((GroovyObject)this, it);
                    return callSiteArray[1].callCurrent(this);
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _visitImport_closure6.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _visitImport_closure6.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "visitAnnotationNode";
                    stringArray[1] = "printLineBreak";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[2];
                    _visitImport_closure6.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_visitImport_closure6.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _visitImport_closure6.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[63].callSafe(callSiteArray[64].callGetProperty(node), new _visitImport_closure6(this, this));
            callSiteArray[65].callCurrent((GroovyObject)this, callSiteArray[66].callGetProperty(node));
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                callSiteArray[67].callCurrent(this);
            } else {
                this.printLineBreak();
            }
        }
    }

    @Override
    public void visitClass(ClassNode node) {
        Reference<ClassNode> node2 = new Reference<ClassNode>(node);
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[68].call(this.classNameStack, callSiteArray[69].callGetProperty(node2.get()));
        public class _visitClass_closure7
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitClass_closure7(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitClass_closure7.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitClass_closure7.$getCallSiteArray();
                callSiteArray[0].callCurrent((GroovyObject)this, it);
                return callSiteArray[1].callCurrent(this);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitClass_closure7.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitClass_closure7.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "visitAnnotationNode";
                stringArray[1] = "printLineBreak";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _visitClass_closure7.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitClass_closure7.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitClass_closure7.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[70].callSafe(callSiteArray[71].callGetPropertySafe(node2.get()), new _visitClass_closure7(this, this));
        callSiteArray[72].callCurrent((GroovyObject)this, callSiteArray[73].callGetProperty(node2.get()));
        callSiteArray[74].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{callSiteArray[75].callGetProperty(node2.get())}, new String[]{"class ", ""}));
        callSiteArray[76].callCurrent((GroovyObject)this, callSiteArray[77].callGetPropertySafe(node2.get()));
        Reference<Boolean> first = new Reference<Boolean>(true);
        public class _visitClass_closure8
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference first;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitClass_closure8(Object _outerInstance, Object _thisObject, Reference first) {
                Reference reference;
                CallSite[] callSiteArray = _visitClass_closure8.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.first = reference = first;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitClass_closure8.$getCallSiteArray();
                if (!DefaultTypeTransformation.booleanUnbox(this.first.get())) {
                    callSiteArray[0].callCurrent((GroovyObject)this, ", ");
                } else {
                    callSiteArray[1].callCurrent((GroovyObject)this, " implements ");
                }
                boolean bl = false;
                this.first.set(bl);
                return callSiteArray[2].callCurrent((GroovyObject)this, it);
            }

            public Boolean getFirst() {
                CallSite[] callSiteArray = _visitClass_closure8.$getCallSiteArray();
                return (Boolean)ScriptBytecodeAdapter.castToType(this.first.get(), Boolean.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitClass_closure8.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitClass_closure8.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "print";
                stringArray[1] = "print";
                stringArray[2] = "visitType";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[3];
                _visitClass_closure8.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitClass_closure8.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitClass_closure8.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[78].callSafe(callSiteArray[79].callGetProperty(node2.get()), new _visitClass_closure8(this, this, first));
        callSiteArray[80].callCurrent((GroovyObject)this, " extends ");
        callSiteArray[81].callCurrent((GroovyObject)this, callSiteArray[82].callGetProperty(node2.get()));
        callSiteArray[83].callCurrent((GroovyObject)this, " { ");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[84].callCurrent(this);
        } else {
            this.printDoubleBreak();
        }
        public class _visitClass_closure9
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference node;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitClass_closure9(Object _outerInstance, Object _thisObject, Reference node) {
                Reference reference;
                CallSite[] callSiteArray = _visitClass_closure9.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.node = reference = node;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitClass_closure9.$getCallSiteArray();
                public class _closure34
                extends Closure
                implements GeneratedClosure {
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure34(Object _outerInstance, Object _thisObject) {
                        CallSite[] callSiteArray = _closure34.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure34.$getCallSiteArray();
                        return callSiteArray[0].callCurrent((GroovyObject)this, it);
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure34.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure34.class) {
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
                        stringArray[0] = "visitProperty";
                        return new CallSiteArray(_closure34.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure34.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                callSiteArray[0].callSafe(callSiteArray[1].callGetPropertySafe(this.node.get()), new _closure34(this, this.getThisObject()));
                callSiteArray[2].callCurrent(this);
                public class _closure35
                extends Closure
                implements GeneratedClosure {
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure35(Object _outerInstance, Object _thisObject) {
                        CallSite[] callSiteArray = _closure35.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure35.$getCallSiteArray();
                        return callSiteArray[0].callCurrent((GroovyObject)this, it);
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure35.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure35.class) {
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
                        stringArray[0] = "visitField";
                        return new CallSiteArray(_closure35.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure35.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                callSiteArray[3].callSafe(callSiteArray[4].callGetPropertySafe(this.node.get()), new _closure35(this, this.getThisObject()));
                callSiteArray[5].callCurrent(this);
                public class _closure36
                extends Closure
                implements GeneratedClosure {
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure36(Object _outerInstance, Object _thisObject) {
                        CallSite[] callSiteArray = _closure36.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure36.$getCallSiteArray();
                        return callSiteArray[0].callCurrent((GroovyObject)this, it);
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure36.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure36.class) {
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
                        stringArray[0] = "visitConstructor";
                        return new CallSiteArray(_closure36.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure36.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                callSiteArray[6].callSafe(callSiteArray[7].callGetPropertySafe(this.node.get()), new _closure36(this, this.getThisObject()));
                callSiteArray[8].callCurrent(this);
                public class _closure37
                extends Closure
                implements GeneratedClosure {
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure37(Object _outerInstance, Object _thisObject) {
                        CallSite[] callSiteArray = _closure37.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure37.$getCallSiteArray();
                        return callSiteArray[0].callCurrent((GroovyObject)this, it);
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure37.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure37.class) {
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
                        stringArray[0] = "visitMethod";
                        return new CallSiteArray(_closure37.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure37.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                return callSiteArray[9].callSafe(callSiteArray[10].callGetPropertySafe(this.node.get()), new _closure37(this, this.getThisObject()));
            }

            public ClassNode getNode() {
                CallSite[] callSiteArray = _visitClass_closure9.$getCallSiteArray();
                return (ClassNode)ScriptBytecodeAdapter.castToType(this.node.get(), ClassNode.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitClass_closure9.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitClass_closure9.class) {
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
                stringArray[1] = "properties";
                stringArray[2] = "printLineBreak";
                stringArray[3] = "each";
                stringArray[4] = "fields";
                stringArray[5] = "printDoubleBreak";
                stringArray[6] = "each";
                stringArray[7] = "declaredConstructors";
                stringArray[8] = "printLineBreak";
                stringArray[9] = "each";
                stringArray[10] = "methods";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[11];
                _visitClass_closure9.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitClass_closure9.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitClass_closure9.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[85].callCurrent((GroovyObject)this, new _visitClass_closure9(this, this, node2));
        callSiteArray[86].callCurrent((GroovyObject)this, "}");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[87].callCurrent(this);
        } else {
            this.printLineBreak();
        }
        callSiteArray[88].call(this.classNameStack);
    }

    private void visitGenerics(GenericsType ... generics) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(generics)) {
            callSiteArray[89].callCurrent((GroovyObject)this, "<");
            Reference<Boolean> first = new Reference<Boolean>(true);
            public class _visitGenerics_closure10
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference first;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _visitGenerics_closure10(Object _outerInstance, Object _thisObject, Reference first) {
                    Reference reference;
                    CallSite[] callSiteArray = _visitGenerics_closure10.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.first = reference = first;
                }

                public Object doCall(GenericsType it) {
                    CallSite[] callSiteArray = _visitGenerics_closure10.$getCallSiteArray();
                    if (!DefaultTypeTransformation.booleanUnbox(this.first.get())) {
                        callSiteArray[0].callCurrent((GroovyObject)this, ", ");
                    }
                    boolean bl = false;
                    this.first.set(bl);
                    callSiteArray[1].callCurrent((GroovyObject)this, callSiteArray[2].callGetProperty(it));
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[3].callGetProperty(it))) {
                        callSiteArray[4].callCurrent((GroovyObject)this, " extends ");
                        Reference<Boolean> innerFirst = new Reference<Boolean>(true);
                        public class _closure38
                        extends Closure
                        implements GeneratedClosure {
                            private /* synthetic */ Reference innerFirst;
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure38(Object _outerInstance, Object _thisObject, Reference innerFirst) {
                                Reference reference;
                                CallSite[] callSiteArray = _closure38.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                                this.innerFirst = reference = innerFirst;
                            }

                            public Object doCall(ClassNode upperBound) {
                                CallSite[] callSiteArray = _closure38.$getCallSiteArray();
                                if (!DefaultTypeTransformation.booleanUnbox(this.innerFirst.get())) {
                                    callSiteArray[0].callCurrent((GroovyObject)this, " & ");
                                }
                                boolean bl = false;
                                this.innerFirst.set(bl);
                                return callSiteArray[1].callCurrent((GroovyObject)this, upperBound);
                            }

                            public Object call(ClassNode upperBound) {
                                CallSite[] callSiteArray = _closure38.$getCallSiteArray();
                                return callSiteArray[2].callCurrent((GroovyObject)this, upperBound);
                            }

                            public Boolean getInnerFirst() {
                                CallSite[] callSiteArray = _closure38.$getCallSiteArray();
                                return (Boolean)ScriptBytecodeAdapter.castToType(this.innerFirst.get(), Boolean.class);
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _closure38.class) {
                                    return ScriptBytecodeAdapter.initMetaClass(this);
                                }
                                ClassInfo classInfo = $staticClassInfo;
                                if (classInfo == null) {
                                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                }
                                return classInfo.getMetaClass();
                            }

                            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                stringArray[0] = "print";
                                stringArray[1] = "visitType";
                                stringArray[2] = "doCall";
                            }

                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                String[] stringArray = new String[3];
                                _closure38.$createCallSiteArray_1(stringArray);
                                return new CallSiteArray(_closure38.class, stringArray);
                            }

                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                CallSiteArray callSiteArray;
                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                    callSiteArray = _closure38.$createCallSiteArray();
                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                }
                                return callSiteArray.array;
                            }
                        }
                        callSiteArray[5].call(callSiteArray[6].callGetProperty(it), new _closure38(this, this.getThisObject(), innerFirst));
                    }
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[7].callGetProperty(it))) {
                        callSiteArray[8].callCurrent((GroovyObject)this, " super ");
                        return callSiteArray[9].callCurrent((GroovyObject)this, callSiteArray[10].callGetProperty(it));
                    }
                    return null;
                }

                public Object call(GenericsType it) {
                    CallSite[] callSiteArray = _visitGenerics_closure10.$getCallSiteArray();
                    return callSiteArray[11].callCurrent((GroovyObject)this, it);
                }

                public Boolean getFirst() {
                    CallSite[] callSiteArray = _visitGenerics_closure10.$getCallSiteArray();
                    return (Boolean)ScriptBytecodeAdapter.castToType(this.first.get(), Boolean.class);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _visitGenerics_closure10.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "print";
                    stringArray[1] = "print";
                    stringArray[2] = "name";
                    stringArray[3] = "upperBounds";
                    stringArray[4] = "print";
                    stringArray[5] = "each";
                    stringArray[6] = "upperBounds";
                    stringArray[7] = "lowerBound";
                    stringArray[8] = "print";
                    stringArray[9] = "visitType";
                    stringArray[10] = "lowerBound";
                    stringArray[11] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[12];
                    _visitGenerics_closure10.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_visitGenerics_closure10.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _visitGenerics_closure10.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[90].call((Object)generics, new _visitGenerics_closure10(this, this, first));
            callSiteArray[91].callCurrent((GroovyObject)this, ">");
        }
    }

    @Override
    public void visitConstructor(ConstructorNode node) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[92].callCurrent((GroovyObject)this, node);
    }

    private String visitParameters(Object parameters) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        Reference<Boolean> first = new Reference<Boolean>(true);
        public class _visitParameters_closure11
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference first;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitParameters_closure11(Object _outerInstance, Object _thisObject, Reference first) {
                Reference reference;
                CallSite[] callSiteArray = _visitParameters_closure11.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.first = reference = first;
            }

            public Object doCall(Parameter it) {
                CallSite[] callSiteArray = _visitParameters_closure11.$getCallSiteArray();
                if (!DefaultTypeTransformation.booleanUnbox(this.first.get())) {
                    callSiteArray[0].callCurrent((GroovyObject)this, ", ");
                }
                boolean bl = false;
                this.first.set(bl);
                public class _closure39
                extends Closure
                implements GeneratedClosure {
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure39(Object _outerInstance, Object _thisObject) {
                        CallSite[] callSiteArray = _closure39.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure39.$getCallSiteArray();
                        callSiteArray[0].callCurrent((GroovyObject)this, it);
                        return callSiteArray[1].callCurrent((GroovyObject)this, " ");
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure39.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure39.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "visitAnnotationNode";
                        stringArray[1] = "print";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[2];
                        _closure39.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_closure39.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure39.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                callSiteArray[1].callSafe(callSiteArray[2].callGetProperty(it), new _closure39(this, this.getThisObject()));
                callSiteArray[3].callCurrent((GroovyObject)this, callSiteArray[4].callGetProperty(it));
                callSiteArray[5].callCurrent((GroovyObject)this, callSiteArray[6].callGetProperty(it));
                callSiteArray[7].callCurrent((GroovyObject)this, callSiteArray[8].call((Object)" ", callSiteArray[9].callGetProperty(it)));
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[10].callGetProperty(it)) && !(callSiteArray[11].callGetProperty(it) instanceof EmptyExpression)) {
                    callSiteArray[12].callCurrent((GroovyObject)this, " = ");
                    return callSiteArray[13].call(callSiteArray[14].callGetProperty(it), this.getThisObject());
                }
                return null;
            }

            public Object call(Parameter it) {
                CallSite[] callSiteArray = _visitParameters_closure11.$getCallSiteArray();
                return callSiteArray[15].callCurrent((GroovyObject)this, it);
            }

            public Boolean getFirst() {
                CallSite[] callSiteArray = _visitParameters_closure11.$getCallSiteArray();
                return (Boolean)ScriptBytecodeAdapter.castToType(this.first.get(), Boolean.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitParameters_closure11.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "print";
                stringArray[1] = "each";
                stringArray[2] = "annotations";
                stringArray[3] = "visitModifiers";
                stringArray[4] = "modifiers";
                stringArray[5] = "visitType";
                stringArray[6] = "type";
                stringArray[7] = "print";
                stringArray[8] = "plus";
                stringArray[9] = "name";
                stringArray[10] = "initialExpression";
                stringArray[11] = "initialExpression";
                stringArray[12] = "print";
                stringArray[13] = "visit";
                stringArray[14] = "initialExpression";
                stringArray[15] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[16];
                _visitParameters_closure11.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitParameters_closure11.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitParameters_closure11.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return ShortTypeHandling.castToString(callSiteArray[93].call(parameters, new _visitParameters_closure11(this, this, first)));
    }

    @Override
    public void visitMethod(MethodNode node) {
        Reference<MethodNode> node2 = new Reference<MethodNode>(node);
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        public class _visitMethod_closure12
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitMethod_closure12(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitMethod_closure12.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitMethod_closure12.$getCallSiteArray();
                callSiteArray[0].callCurrent((GroovyObject)this, it);
                return callSiteArray[1].callCurrent(this);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitMethod_closure12.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitMethod_closure12.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "visitAnnotationNode";
                stringArray[1] = "printLineBreak";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _visitMethod_closure12.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitMethod_closure12.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitMethod_closure12.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[94].callSafe(callSiteArray[95].callGetPropertySafe(node2.get()), new _visitMethod_closure12(this, this));
        callSiteArray[96].callCurrent((GroovyObject)this, callSiteArray[97].callGetProperty(node2.get()));
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[98].callGetProperty(node2.get()), "<init>")) {
            callSiteArray[99].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{callSiteArray[100].call(this.classNameStack)}, new String[]{"", "("}));
            callSiteArray[101].callCurrent((GroovyObject)this, callSiteArray[102].callGetProperty(node2.get()));
            callSiteArray[103].callCurrent((GroovyObject)this, ") {");
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                callSiteArray[104].callCurrent(this);
            } else {
                this.printLineBreak();
            }
        } else if (ScriptBytecodeAdapter.compareEqual(callSiteArray[105].callGetProperty(node2.get()), "<clinit>")) {
            callSiteArray[106].callCurrent((GroovyObject)this, "{ ");
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                callSiteArray[107].callCurrent(this);
            } else {
                this.printLineBreak();
            }
        } else {
            callSiteArray[108].callCurrent((GroovyObject)this, callSiteArray[109].callGetProperty(node2.get()));
            callSiteArray[110].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{callSiteArray[111].callGetProperty(node2.get())}, new String[]{" ", "("}));
            callSiteArray[112].callCurrent((GroovyObject)this, callSiteArray[113].callGetProperty(node2.get()));
            callSiteArray[114].callCurrent((GroovyObject)this, ")");
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[115].callGetProperty(node2.get()))) {
                Reference<Boolean> first = new Reference<Boolean>(true);
                callSiteArray[116].callCurrent((GroovyObject)this, " throws ");
                public class _visitMethod_closure13
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference first;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _visitMethod_closure13(Object _outerInstance, Object _thisObject, Reference first) {
                        Reference reference;
                        CallSite[] callSiteArray = _visitMethod_closure13.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.first = reference = first;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _visitMethod_closure13.$getCallSiteArray();
                        if (!DefaultTypeTransformation.booleanUnbox(this.first.get())) {
                            callSiteArray[0].callCurrent((GroovyObject)this, ", ");
                        }
                        boolean bl = false;
                        this.first.set(bl);
                        return callSiteArray[1].callCurrent((GroovyObject)this, it);
                    }

                    public Boolean getFirst() {
                        CallSite[] callSiteArray = _visitMethod_closure13.$getCallSiteArray();
                        return (Boolean)ScriptBytecodeAdapter.castToType(this.first.get(), Boolean.class);
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _visitMethod_closure13.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _visitMethod_closure13.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "print";
                        stringArray[1] = "visitType";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[2];
                        _visitMethod_closure13.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_visitMethod_closure13.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _visitMethod_closure13.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                callSiteArray[117].call(callSiteArray[118].callGetProperty(node2.get()), new _visitMethod_closure13(this, this, first));
            }
            callSiteArray[119].callCurrent((GroovyObject)this, " {");
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                callSiteArray[120].callCurrent(this);
            } else {
                this.printLineBreak();
            }
        }
        public class _visitMethod_closure14
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference node;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitMethod_closure14(Object _outerInstance, Object _thisObject, Reference node) {
                Reference reference;
                CallSite[] callSiteArray = _visitMethod_closure14.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.node = reference = node;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitMethod_closure14.$getCallSiteArray();
                return callSiteArray[0].callSafe(callSiteArray[1].callGetPropertySafe(this.node.get()), this.getThisObject());
            }

            public MethodNode getNode() {
                CallSite[] callSiteArray = _visitMethod_closure14.$getCallSiteArray();
                return (MethodNode)ScriptBytecodeAdapter.castToType(this.node.get(), MethodNode.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitMethod_closure14.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitMethod_closure14.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "visit";
                stringArray[1] = "code";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _visitMethod_closure14.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitMethod_closure14.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitMethod_closure14.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[121].callCurrent((GroovyObject)this, new _visitMethod_closure14(this, this, node2));
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[122].callCurrent(this);
        } else {
            this.printLineBreak();
        }
        callSiteArray[123].callCurrent((GroovyObject)this, "}");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[124].callCurrent(this);
        } else {
            this.printDoubleBreak();
        }
    }

    private Object visitModifiers(int modifiers) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[125].call(Modifier.class, modifiers))) {
            callSiteArray[126].callCurrent((GroovyObject)this, "abstract ");
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[127].call(Modifier.class, modifiers))) {
            callSiteArray[128].callCurrent((GroovyObject)this, "final ");
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[129].call(Modifier.class, modifiers))) {
            callSiteArray[130].callCurrent((GroovyObject)this, "interface ");
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[131].call(Modifier.class, modifiers))) {
            callSiteArray[132].callCurrent((GroovyObject)this, "native ");
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[133].call(Modifier.class, modifiers))) {
            callSiteArray[134].callCurrent((GroovyObject)this, "private ");
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[135].call(Modifier.class, modifiers))) {
            callSiteArray[136].callCurrent((GroovyObject)this, "protected ");
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[137].call(Modifier.class, modifiers))) {
            callSiteArray[138].callCurrent((GroovyObject)this, "public ");
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[139].call(Modifier.class, modifiers))) {
            callSiteArray[140].callCurrent((GroovyObject)this, "static ");
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[141].call(Modifier.class, modifiers))) {
            callSiteArray[142].callCurrent((GroovyObject)this, "synchronized ");
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[143].call(Modifier.class, modifiers))) {
            callSiteArray[144].callCurrent((GroovyObject)this, "transient ");
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[145].call(Modifier.class, modifiers))) {
            return callSiteArray[146].callCurrent((GroovyObject)this, "volatile ");
        }
        return null;
    }

    @Override
    public void visitField(FieldNode node) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        public class _visitField_closure15
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitField_closure15(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitField_closure15.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitField_closure15.$getCallSiteArray();
                callSiteArray[0].callCurrent((GroovyObject)this, it);
                return callSiteArray[1].callCurrent(this);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitField_closure15.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitField_closure15.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "visitAnnotationNode";
                stringArray[1] = "printLineBreak";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _visitField_closure15.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitField_closure15.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitField_closure15.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[147].callSafe(callSiteArray[148].callGetPropertySafe(node), new _visitField_closure15(this, this));
        callSiteArray[149].callCurrent((GroovyObject)this, callSiteArray[150].callGetProperty(node));
        callSiteArray[151].callCurrent((GroovyObject)this, callSiteArray[152].callGetProperty(node));
        callSiteArray[153].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{callSiteArray[154].callGetProperty(node)}, new String[]{" ", " "}));
        Expression exp = (Expression)ScriptBytecodeAdapter.castToType(callSiteArray[155].callGetProperty(node), Expression.class);
        if (exp instanceof ConstantExpression) {
            Object object = callSiteArray[156].call(Verifier.class, exp);
            exp = (Expression)ScriptBytecodeAdapter.castToType(object, Expression.class);
        }
        ClassNode type = (ClassNode)ScriptBytecodeAdapter.castToType(callSiteArray[157].callGetPropertySafe(exp), ClassNode.class);
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[158].call(Modifier.class, callSiteArray[159].callGetProperty(node))) && DefaultTypeTransformation.booleanUnbox(callSiteArray[160].call(Modifier.class, callSiteArray[161].call(node))) && exp instanceof ConstantExpression && ScriptBytecodeAdapter.compareEqual(type, callSiteArray[162].callGetProperty(node)) && DefaultTypeTransformation.booleanUnbox(callSiteArray[163].call(ClassHelper.class, type))) {
                callSiteArray[164].callCurrent((GroovyObject)this, " = ");
                if (ScriptBytecodeAdapter.compareEqual(callSiteArray[165].callGetProperty(ClassHelper.class), type)) {
                    callSiteArray[166].callCurrent((GroovyObject)this, callSiteArray[167].call(callSiteArray[168].call((Object)"'", callSiteArray[169].call(callSiteArray[170].callGetProperty(callSiteArray[171].callGetProperty(node)), "'", "\\\\'")), "'"));
                } else if (ScriptBytecodeAdapter.compareEqual(callSiteArray[172].callGetProperty(ClassHelper.class), type)) {
                    callSiteArray[173].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{callSiteArray[174].callGetProperty(callSiteArray[175].callGetProperty(node))}, new String[]{"'", "'"}));
                } else {
                    callSiteArray[176].callCurrent((GroovyObject)this, callSiteArray[177].callGetProperty(callSiteArray[178].callGetProperty(node)));
                }
            }
        } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[179].call(Modifier.class, callSiteArray[180].callGetProperty(node))) && DefaultTypeTransformation.booleanUnbox(callSiteArray[181].call(Modifier.class, callSiteArray[182].call(node))) && exp instanceof ConstantExpression && ScriptBytecodeAdapter.compareEqual(type, callSiteArray[183].callGetProperty(node)) && DefaultTypeTransformation.booleanUnbox(callSiteArray[184].call(ClassHelper.class, type))) {
            callSiteArray[185].callCurrent((GroovyObject)this, " = ");
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[186].callGetProperty(ClassHelper.class), type)) {
                callSiteArray[187].callCurrent((GroovyObject)this, callSiteArray[188].call(callSiteArray[189].call((Object)"'", callSiteArray[190].call(callSiteArray[191].callGetProperty(callSiteArray[192].callGetProperty(node)), "'", "\\\\'")), "'"));
            } else if (ScriptBytecodeAdapter.compareEqual(callSiteArray[193].callGetProperty(ClassHelper.class), type)) {
                callSiteArray[194].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{callSiteArray[195].callGetProperty(callSiteArray[196].callGetProperty(node))}, new String[]{"'", "'"}));
            } else {
                callSiteArray[197].callCurrent((GroovyObject)this, callSiteArray[198].callGetProperty(callSiteArray[199].callGetProperty(node)));
            }
        }
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[200].callCurrent(this);
        } else {
            this.printLineBreak();
        }
    }

    public void visitAnnotationNode(AnnotationNode node) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[201].callCurrent((GroovyObject)this, callSiteArray[202].call((Object)"@", callSiteArray[203].callGetPropertySafe(callSiteArray[204].callGetPropertySafe(node))));
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[205].callGetPropertySafe(node))) {
            callSiteArray[206].callCurrent((GroovyObject)this, "(");
            Reference<Boolean> first = new Reference<Boolean>(true);
            public class _visitAnnotationNode_closure16
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference first;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _visitAnnotationNode_closure16(Object _outerInstance, Object _thisObject, Reference first) {
                    Reference reference;
                    CallSite[] callSiteArray = _visitAnnotationNode_closure16.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.first = reference = first;
                }

                public Object doCall(String name, Expression value) {
                    CallSite[] callSiteArray = _visitAnnotationNode_closure16.$getCallSiteArray();
                    if (DefaultTypeTransformation.booleanUnbox(this.first.get())) {
                        boolean bl = false;
                        this.first.set(bl);
                    } else {
                        callSiteArray[0].callCurrent((GroovyObject)this, ", ");
                    }
                    callSiteArray[1].callCurrent((GroovyObject)this, callSiteArray[2].call((Object)name, " = "));
                    return callSiteArray[3].call((Object)value, this.getThisObject());
                }

                public Object call(String name, Expression value) {
                    CallSite[] callSiteArray = _visitAnnotationNode_closure16.$getCallSiteArray();
                    return callSiteArray[4].callCurrent(this, name, value);
                }

                public Boolean getFirst() {
                    CallSite[] callSiteArray = _visitAnnotationNode_closure16.$getCallSiteArray();
                    return (Boolean)ScriptBytecodeAdapter.castToType(this.first.get(), Boolean.class);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _visitAnnotationNode_closure16.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "print";
                    stringArray[1] = "print";
                    stringArray[2] = "plus";
                    stringArray[3] = "visit";
                    stringArray[4] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[5];
                    _visitAnnotationNode_closure16.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_visitAnnotationNode_closure16.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _visitAnnotationNode_closure16.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[207].call(callSiteArray[208].callGetProperty(node), new _visitAnnotationNode_closure16(this, this, first));
            callSiteArray[209].callCurrent((GroovyObject)this, ")");
        }
    }

    @Override
    public void visitProperty(PropertyNode node) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
    }

    @Override
    public void visitBlockStatement(BlockStatement block) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        public class _visitBlockStatement_closure17
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitBlockStatement_closure17(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitBlockStatement_closure17.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitBlockStatement_closure17.$getCallSiteArray();
                callSiteArray[0].call(it, this.getThisObject());
                return callSiteArray[1].callCurrent(this);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitBlockStatement_closure17.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitBlockStatement_closure17.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "visit";
                stringArray[1] = "printLineBreak";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _visitBlockStatement_closure17.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitBlockStatement_closure17.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitBlockStatement_closure17.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[210].callSafe(callSiteArray[211].callGetPropertySafe(block), new _visitBlockStatement_closure17(this, this));
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[212].call(callSiteArray[213].call(this._out), "\n"))) {
                callSiteArray[214].callCurrent(this);
            }
        } else if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[215].call(callSiteArray[216].call(this._out), "\n"))) {
            this.printLineBreak();
        }
    }

    @Override
    public void visitForLoop(ForStatement statement) {
        Reference<ForStatement> statement2 = new Reference<ForStatement>(statement);
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[217].callCurrent((GroovyObject)this, "for (");
        if (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[218].callGetPropertySafe(statement2.get()), callSiteArray[219].callGetProperty(ForStatement.class))) {
            callSiteArray[220].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[221].callGetProperty(statement2.get())}));
            callSiteArray[222].callCurrent((GroovyObject)this, " : ");
        }
        if (callSiteArray[223].callGetPropertySafe(statement2.get()) instanceof ListExpression) {
            callSiteArray[224].callSafe(callSiteArray[225].callGetPropertySafe(statement2.get()), this);
        } else {
            callSiteArray[226].callSafe(callSiteArray[227].callGetPropertySafe(statement2.get()), this);
        }
        callSiteArray[228].callCurrent((GroovyObject)this, ") {");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[229].callCurrent(this);
        } else {
            this.printLineBreak();
        }
        public class _visitForLoop_closure18
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference statement;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitForLoop_closure18(Object _outerInstance, Object _thisObject, Reference statement) {
                Reference reference;
                CallSite[] callSiteArray = _visitForLoop_closure18.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.statement = reference = statement;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitForLoop_closure18.$getCallSiteArray();
                return callSiteArray[0].callSafe(callSiteArray[1].callGetPropertySafe(this.statement.get()), this.getThisObject());
            }

            public ForStatement getStatement() {
                CallSite[] callSiteArray = _visitForLoop_closure18.$getCallSiteArray();
                return (ForStatement)ScriptBytecodeAdapter.castToType(this.statement.get(), ForStatement.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitForLoop_closure18.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitForLoop_closure18.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "visit";
                stringArray[1] = "loopBlock";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _visitForLoop_closure18.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitForLoop_closure18.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitForLoop_closure18.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[230].callCurrent((GroovyObject)this, new _visitForLoop_closure18(this, this, statement2));
        callSiteArray[231].callCurrent((GroovyObject)this, "}");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[232].callCurrent(this);
        } else {
            this.printLineBreak();
        }
    }

    @Override
    public void visitIfElse(IfStatement ifElse) {
        Reference<IfStatement> ifElse2 = new Reference<IfStatement>(ifElse);
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[233].callCurrent((GroovyObject)this, "if (");
        callSiteArray[234].callSafe(callSiteArray[235].callGetPropertySafe(ifElse2.get()), this);
        callSiteArray[236].callCurrent((GroovyObject)this, ") {");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[237].callCurrent(this);
        } else {
            this.printLineBreak();
        }
        public class _visitIfElse_closure19
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference ifElse;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitIfElse_closure19(Object _outerInstance, Object _thisObject, Reference ifElse) {
                Reference reference;
                CallSite[] callSiteArray = _visitIfElse_closure19.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.ifElse = reference = ifElse;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitIfElse_closure19.$getCallSiteArray();
                return callSiteArray[0].callSafe(callSiteArray[1].callGetPropertySafe(this.ifElse.get()), this.getThisObject());
            }

            public IfStatement getIfElse() {
                CallSite[] callSiteArray = _visitIfElse_closure19.$getCallSiteArray();
                return (IfStatement)ScriptBytecodeAdapter.castToType(this.ifElse.get(), IfStatement.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitIfElse_closure19.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitIfElse_closure19.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "visit";
                stringArray[1] = "ifBlock";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _visitIfElse_closure19.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitIfElse_closure19.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitIfElse_closure19.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[238].callCurrent((GroovyObject)this, new _visitIfElse_closure19(this, this, ifElse2));
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[239].callCurrent(this);
        } else {
            this.printLineBreak();
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[240].callGetPropertySafe(ifElse2.get())) && !(callSiteArray[241].callGetProperty(ifElse2.get()) instanceof EmptyStatement)) {
            callSiteArray[242].callCurrent((GroovyObject)this, "} else {");
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                callSiteArray[243].callCurrent(this);
            } else {
                this.printLineBreak();
            }
            public class _visitIfElse_closure20
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference ifElse;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _visitIfElse_closure20(Object _outerInstance, Object _thisObject, Reference ifElse) {
                    Reference reference;
                    CallSite[] callSiteArray = _visitIfElse_closure20.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.ifElse = reference = ifElse;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _visitIfElse_closure20.$getCallSiteArray();
                    return callSiteArray[0].callSafe(callSiteArray[1].callGetPropertySafe(this.ifElse.get()), this.getThisObject());
                }

                public IfStatement getIfElse() {
                    CallSite[] callSiteArray = _visitIfElse_closure20.$getCallSiteArray();
                    return (IfStatement)ScriptBytecodeAdapter.castToType(this.ifElse.get(), IfStatement.class);
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _visitIfElse_closure20.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _visitIfElse_closure20.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "visit";
                    stringArray[1] = "elseBlock";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[2];
                    _visitIfElse_closure20.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_visitIfElse_closure20.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _visitIfElse_closure20.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[244].callCurrent((GroovyObject)this, new _visitIfElse_closure20(this, this, ifElse2));
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                callSiteArray[245].callCurrent(this);
            } else {
                this.printLineBreak();
            }
        }
        callSiteArray[246].callCurrent((GroovyObject)this, "}");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[247].callCurrent(this);
        } else {
            this.printLineBreak();
        }
    }

    @Override
    public void visitExpressionStatement(ExpressionStatement statement) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[248].call(callSiteArray[249].callGetProperty(statement), this);
    }

    @Override
    public void visitReturnStatement(ReturnStatement statement) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[250].callCurrent(this);
        } else {
            this.printLineBreak();
        }
        callSiteArray[251].callCurrent((GroovyObject)this, "return ");
        callSiteArray[252].call(callSiteArray[253].call(statement), this);
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[254].callCurrent(this);
        } else {
            this.printLineBreak();
        }
    }

    @Override
    public void visitSwitch(SwitchStatement statement) {
        Reference<SwitchStatement> statement2 = new Reference<SwitchStatement>(statement);
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[255].callCurrent((GroovyObject)this, "switch (");
        callSiteArray[256].callSafe(callSiteArray[257].callGetPropertySafe(statement2.get()), this);
        callSiteArray[258].callCurrent((GroovyObject)this, ") {");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[259].callCurrent(this);
        } else {
            this.printLineBreak();
        }
        public class _visitSwitch_closure21
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference statement;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitSwitch_closure21(Object _outerInstance, Object _thisObject, Reference statement) {
                Reference reference;
                CallSite[] callSiteArray = _visitSwitch_closure21.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.statement = reference = statement;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitSwitch_closure21.$getCallSiteArray();
                public class _closure40
                extends Closure
                implements GeneratedClosure {
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure40(Object _outerInstance, Object _thisObject) {
                        CallSite[] callSiteArray = _closure40.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure40.$getCallSiteArray();
                        return callSiteArray[0].callCurrent((GroovyObject)this, it);
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure40.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure40.class) {
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
                        stringArray[0] = "visitCaseStatement";
                        return new CallSiteArray(_closure40.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure40.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                callSiteArray[0].callSafe(callSiteArray[1].callGetPropertySafe(this.statement.get()), new _closure40(this, this.getThisObject()));
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[2].callGetPropertySafe(this.statement.get()))) {
                    callSiteArray[3].callCurrent((GroovyObject)this, "default: ");
                    callSiteArray[4].callCurrent(this);
                    return callSiteArray[5].callSafe(callSiteArray[6].callGetPropertySafe(this.statement.get()), this.getThisObject());
                }
                return null;
            }

            public SwitchStatement getStatement() {
                CallSite[] callSiteArray = _visitSwitch_closure21.$getCallSiteArray();
                return (SwitchStatement)ScriptBytecodeAdapter.castToType(this.statement.get(), SwitchStatement.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitSwitch_closure21.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitSwitch_closure21.class) {
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
                stringArray[1] = "caseStatements";
                stringArray[2] = "defaultStatement";
                stringArray[3] = "print";
                stringArray[4] = "printLineBreak";
                stringArray[5] = "visit";
                stringArray[6] = "defaultStatement";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[7];
                _visitSwitch_closure21.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitSwitch_closure21.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitSwitch_closure21.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[260].callCurrent((GroovyObject)this, new _visitSwitch_closure21(this, this, statement2));
        callSiteArray[261].callCurrent((GroovyObject)this, "}");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[262].callCurrent(this);
        } else {
            this.printLineBreak();
        }
    }

    @Override
    public void visitCaseStatement(CaseStatement statement) {
        Reference<CaseStatement> statement2 = new Reference<CaseStatement>(statement);
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[263].callCurrent((GroovyObject)this, "case ");
        callSiteArray[264].callSafe(callSiteArray[265].callGetPropertySafe(statement2.get()), this);
        callSiteArray[266].callCurrent((GroovyObject)this, ":");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[267].callCurrent(this);
        } else {
            this.printLineBreak();
        }
        public class _visitCaseStatement_closure22
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference statement;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitCaseStatement_closure22(Object _outerInstance, Object _thisObject, Reference statement) {
                Reference reference;
                CallSite[] callSiteArray = _visitCaseStatement_closure22.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.statement = reference = statement;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitCaseStatement_closure22.$getCallSiteArray();
                return callSiteArray[0].callSafe(callSiteArray[1].callGetPropertySafe(this.statement.get()), this.getThisObject());
            }

            public CaseStatement getStatement() {
                CallSite[] callSiteArray = _visitCaseStatement_closure22.$getCallSiteArray();
                return (CaseStatement)ScriptBytecodeAdapter.castToType(this.statement.get(), CaseStatement.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitCaseStatement_closure22.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitCaseStatement_closure22.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "visit";
                stringArray[1] = "code";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _visitCaseStatement_closure22.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitCaseStatement_closure22.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitCaseStatement_closure22.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[268].callCurrent((GroovyObject)this, new _visitCaseStatement_closure22(this, this, statement2));
    }

    @Override
    public void visitBreakStatement(BreakStatement statement) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[269].callCurrent((GroovyObject)this, "break");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[270].callCurrent(this);
        } else {
            this.printLineBreak();
        }
    }

    @Override
    public void visitContinueStatement(ContinueStatement statement) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[271].callCurrent((GroovyObject)this, "continue");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[272].callCurrent(this);
        } else {
            this.printLineBreak();
        }
    }

    @Override
    public void visitMethodCallExpression(MethodCallExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        Expression objectExp = (Expression)ScriptBytecodeAdapter.castToType(callSiteArray[273].call(expression), Expression.class);
        if (objectExp instanceof VariableExpression) {
            callSiteArray[274].callCurrent(this, objectExp, false);
        } else {
            callSiteArray[275].call((Object)objectExp, this);
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[276].callGetProperty(expression))) {
            callSiteArray[277].callCurrent((GroovyObject)this, "*");
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[278].callGetProperty(expression))) {
            callSiteArray[279].callCurrent((GroovyObject)this, "?");
        }
        callSiteArray[280].callCurrent((GroovyObject)this, ".");
        Expression method = (Expression)ScriptBytecodeAdapter.castToType(callSiteArray[281].call(expression), Expression.class);
        if (method instanceof ConstantExpression) {
            callSiteArray[282].callCurrent(this, method, true);
        } else {
            callSiteArray[283].call((Object)method, this);
        }
        callSiteArray[284].call(callSiteArray[285].call(expression), this);
    }

    @Override
    public void visitStaticMethodCallExpression(StaticMethodCallExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[286].callCurrent((GroovyObject)this, callSiteArray[287].call(callSiteArray[288].call(callSiteArray[289].callGetPropertySafe(callSiteArray[290].callGetPropertySafe(expression)), "."), callSiteArray[291].callGetPropertySafe(expression)));
        if (callSiteArray[292].callGetPropertySafe(expression) instanceof VariableExpression || callSiteArray[293].callGetPropertySafe(expression) instanceof MethodCallExpression) {
            callSiteArray[294].callCurrent((GroovyObject)this, "(");
            callSiteArray[295].callSafe(callSiteArray[296].callGetPropertySafe(expression), this);
            callSiteArray[297].callCurrent((GroovyObject)this, ")");
        } else {
            callSiteArray[298].callSafe(callSiteArray[299].callGetPropertySafe(expression), this);
        }
    }

    @Override
    public void visitConstructorCallExpression(ConstructorCallExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[300].callSafe(expression))) {
            callSiteArray[301].callCurrent((GroovyObject)this, "super");
        } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[302].callSafe(expression))) {
            callSiteArray[303].callCurrent((GroovyObject)this, "this ");
        } else {
            callSiteArray[304].callCurrent((GroovyObject)this, "new ");
            callSiteArray[305].callCurrent((GroovyObject)this, callSiteArray[306].callGetPropertySafe(expression));
        }
        callSiteArray[307].callSafe(callSiteArray[308].callGetPropertySafe(expression), this);
    }

    @Override
    public void visitBinaryExpression(BinaryExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[309].callSafe(callSiteArray[310].callGetPropertySafe(expression), this);
        callSiteArray[311].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{callSiteArray[312].callGetProperty(callSiteArray[313].callGetProperty(expression))}, new String[]{" ", " "}));
        callSiteArray[314].call(callSiteArray[315].callGetProperty(expression), this);
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[316].callGetPropertySafe(callSiteArray[317].callGetPropertySafe(expression)), "[")) {
            callSiteArray[318].callCurrent((GroovyObject)this, "]");
        }
    }

    @Override
    public void visitPostfixExpression(PostfixExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[319].callCurrent((GroovyObject)this, "(");
        callSiteArray[320].callSafe(callSiteArray[321].callGetPropertySafe(expression), this);
        callSiteArray[322].callCurrent((GroovyObject)this, ")");
        callSiteArray[323].callCurrent((GroovyObject)this, callSiteArray[324].callGetPropertySafe(callSiteArray[325].callGetPropertySafe(expression)));
    }

    @Override
    public void visitPrefixExpression(PrefixExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[326].callCurrent((GroovyObject)this, callSiteArray[327].callGetPropertySafe(callSiteArray[328].callGetPropertySafe(expression)));
        callSiteArray[329].callCurrent((GroovyObject)this, "(");
        callSiteArray[330].callSafe(callSiteArray[331].callGetPropertySafe(expression), this);
        callSiteArray[332].callCurrent((GroovyObject)this, ")");
    }

    @Override
    public void visitClosureExpression(ClosureExpression expression) {
        Reference<ClosureExpression> expression2 = new Reference<ClosureExpression>(expression);
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[333].callCurrent((GroovyObject)this, "{ ");
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[334].callGetPropertySafe(expression2.get()))) {
            callSiteArray[335].callCurrent((GroovyObject)this, callSiteArray[336].callGetPropertySafe(expression2.get()));
            callSiteArray[337].callCurrent((GroovyObject)this, " ->");
        }
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[338].callCurrent(this);
        } else {
            this.printLineBreak();
        }
        public class _visitClosureExpression_closure23
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference expression;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitClosureExpression_closure23(Object _outerInstance, Object _thisObject, Reference expression) {
                Reference reference;
                CallSite[] callSiteArray = _visitClosureExpression_closure23.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.expression = reference = expression;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitClosureExpression_closure23.$getCallSiteArray();
                return callSiteArray[0].callSafe(callSiteArray[1].callGetPropertySafe(this.expression.get()), this.getThisObject());
            }

            public ClosureExpression getExpression() {
                CallSite[] callSiteArray = _visitClosureExpression_closure23.$getCallSiteArray();
                return (ClosureExpression)ScriptBytecodeAdapter.castToType(this.expression.get(), ClosureExpression.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitClosureExpression_closure23.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitClosureExpression_closure23.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "visit";
                stringArray[1] = "code";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _visitClosureExpression_closure23.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitClosureExpression_closure23.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitClosureExpression_closure23.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[339].callCurrent((GroovyObject)this, new _visitClosureExpression_closure23(this, this, expression2));
        callSiteArray[340].callCurrent((GroovyObject)this, "}");
    }

    @Override
    public void visitTupleExpression(TupleExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[341].callCurrent((GroovyObject)this, "(");
        callSiteArray[342].callCurrent((GroovyObject)this, callSiteArray[343].callGetPropertySafe(expression));
        callSiteArray[344].callCurrent((GroovyObject)this, ")");
    }

    @Override
    public void visitRangeExpression(RangeExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[345].callCurrent((GroovyObject)this, "(");
        callSiteArray[346].callSafe(callSiteArray[347].callGetPropertySafe(expression), this);
        callSiteArray[348].callCurrent((GroovyObject)this, "..");
        callSiteArray[349].callSafe(callSiteArray[350].callGetPropertySafe(expression), this);
        callSiteArray[351].callCurrent((GroovyObject)this, ")");
    }

    @Override
    public void visitPropertyExpression(PropertyExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[352].callSafe(callSiteArray[353].callGetPropertySafe(expression), this);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[354].callGetPropertySafe(expression))) {
            callSiteArray[355].callCurrent((GroovyObject)this, "*");
        } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[356].callSafe(expression))) {
            callSiteArray[357].callCurrent((GroovyObject)this, "?");
        }
        callSiteArray[358].callCurrent((GroovyObject)this, ".");
        if (callSiteArray[359].callGetPropertySafe(expression) instanceof ConstantExpression) {
            callSiteArray[360].callCurrent(this, callSiteArray[361].callGetPropertySafe(expression), true);
        } else {
            callSiteArray[362].callSafe(callSiteArray[363].callGetPropertySafe(expression), this);
        }
    }

    @Override
    public void visitAttributeExpression(AttributeExpression attributeExpression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[364].callCurrent((GroovyObject)this, attributeExpression);
    }

    @Override
    public void visitFieldExpression(FieldExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[365].callCurrent((GroovyObject)this, callSiteArray[366].callGetPropertySafe(callSiteArray[367].callGetPropertySafe(expression)));
    }

    public void visitConstantExpression(ConstantExpression expression, boolean unwrapQuotes) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        if (callSiteArray[368].callGetProperty(expression) instanceof String && !unwrapQuotes) {
            Object escaped = callSiteArray[369].call(callSiteArray[370].call(ShortTypeHandling.castToString(callSiteArray[371].callGetProperty(expression)), "\n", "\\\\n"), "'", "\\\\'");
            callSiteArray[372].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{escaped}, new String[]{"'", "'"}));
        } else {
            callSiteArray[373].callCurrent((GroovyObject)this, callSiteArray[374].callGetProperty(expression));
        }
    }

    @Override
    public void visitClassExpression(ClassExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[375].callCurrent((GroovyObject)this, callSiteArray[376].callGetProperty(expression));
    }

    public void visitVariableExpression(VariableExpression expression, boolean spacePad) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        if (spacePad) {
            callSiteArray[377].callCurrent((GroovyObject)this, callSiteArray[378].call(callSiteArray[379].call((Object)" ", callSiteArray[380].callGetProperty(expression)), " "));
        } else {
            callSiteArray[381].callCurrent((GroovyObject)this, callSiteArray[382].callGetProperty(expression));
        }
    }

    @Override
    public void visitDeclarationExpression(DeclarationExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        if (callSiteArray[383].callGetPropertySafe(expression) instanceof ArgumentListExpression) {
            callSiteArray[384].callCurrent((GroovyObject)this, "def ");
            callSiteArray[385].callCurrent(this, callSiteArray[386].callGetPropertySafe(expression), true);
            callSiteArray[387].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{callSiteArray[388].callGetProperty(callSiteArray[389].callGetProperty(expression))}, new String[]{" ", " "}));
            callSiteArray[390].call(callSiteArray[391].callGetProperty(expression), this);
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[392].callGetPropertySafe(callSiteArray[393].callGetPropertySafe(expression)), "[")) {
                callSiteArray[394].callCurrent((GroovyObject)this, "]");
            }
        } else {
            callSiteArray[395].callCurrent((GroovyObject)this, callSiteArray[396].callGetPropertySafe(callSiteArray[397].callGetPropertySafe(expression)));
            callSiteArray[398].callCurrent((GroovyObject)this, expression);
        }
    }

    @Override
    public void visitGStringExpression(GStringExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[399].callCurrent((GroovyObject)this, callSiteArray[400].call(callSiteArray[401].call((Object)"\"", callSiteArray[402].callGetProperty(expression)), "\""));
    }

    @Override
    public void visitSpreadExpression(SpreadExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[403].callCurrent((GroovyObject)this, "*");
        callSiteArray[404].callSafe(callSiteArray[405].callGetPropertySafe(expression), this);
    }

    @Override
    public void visitNotExpression(NotExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[406].callCurrent((GroovyObject)this, "!(");
        callSiteArray[407].callSafe(callSiteArray[408].callGetPropertySafe(expression), this);
        callSiteArray[409].callCurrent((GroovyObject)this, ")");
    }

    @Override
    public void visitUnaryMinusExpression(UnaryMinusExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[410].callCurrent((GroovyObject)this, "-(");
        callSiteArray[411].callSafe(callSiteArray[412].callGetPropertySafe(expression), this);
        callSiteArray[413].callCurrent((GroovyObject)this, ")");
    }

    @Override
    public void visitUnaryPlusExpression(UnaryPlusExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[414].callCurrent((GroovyObject)this, "+(");
        callSiteArray[415].callSafe(callSiteArray[416].callGetPropertySafe(expression), this);
        callSiteArray[417].callCurrent((GroovyObject)this, ")");
    }

    @Override
    public void visitCastExpression(CastExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[418].callCurrent((GroovyObject)this, "((");
        callSiteArray[419].callSafe(callSiteArray[420].callGetPropertySafe(expression), this);
        callSiteArray[421].callCurrent((GroovyObject)this, ") as ");
        callSiteArray[422].callCurrent((GroovyObject)this, callSiteArray[423].callGetPropertySafe(expression));
        callSiteArray[424].callCurrent((GroovyObject)this, ")");
    }

    public void visitType(ClassNode classNode) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        Object name = callSiteArray[425].callGetProperty(classNode);
        if (DefaultTypeTransformation.booleanUnbox(ScriptBytecodeAdapter.findRegex(name, "^\\[+L")) && DefaultTypeTransformation.booleanUnbox(callSiteArray[426].call(name, ";"))) {
            int numDimensions = DefaultTypeTransformation.intUnbox(callSiteArray[427].call(name, "L"));
            if (!BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                callSiteArray[428].callCurrent((GroovyObject)this, callSiteArray[429].call((Object)new GStringImpl(new Object[]{callSiteArray[430].call(callSiteArray[431].callGetProperty(classNode), ScriptBytecodeAdapter.createRange(callSiteArray[432].call((Object)numDimensions, 1), -2, true))}, new String[]{"", ""}), callSiteArray[433].call((Object)"[]", numDimensions)));
            } else {
                callSiteArray[434].callCurrent((GroovyObject)this, callSiteArray[435].call((Object)new GStringImpl(new Object[]{callSiteArray[436].call(callSiteArray[437].callGetProperty(classNode), ScriptBytecodeAdapter.createRange(numDimensions + 1, -2, true))}, new String[]{"", ""}), callSiteArray[438].call((Object)"[]", numDimensions)));
            }
        } else {
            callSiteArray[439].callCurrent((GroovyObject)this, name);
        }
        callSiteArray[440].callCurrent((GroovyObject)this, callSiteArray[441].callGetPropertySafe(classNode));
    }

    public void visitArgumentlistExpression(ArgumentListExpression expression, boolean showTypes) {
        Reference<Boolean> showTypes2 = new Reference<Boolean>(showTypes);
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[442].callCurrent((GroovyObject)this, "(");
        Reference<Integer> count = new Reference<Integer>((Integer)ScriptBytecodeAdapter.castToType(callSiteArray[443].callSafe(callSiteArray[444].callGetPropertySafe(expression)), Integer.class));
        public class _visitArgumentlistExpression_closure24
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference showTypes;
            private /* synthetic */ Reference count;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitArgumentlistExpression_closure24(Object _outerInstance, Object _thisObject, Reference showTypes, Reference count) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _visitArgumentlistExpression_closure24.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.showTypes = reference2 = showTypes;
                this.count = reference = count;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitArgumentlistExpression_closure24.$getCallSiteArray();
                if (DefaultTypeTransformation.booleanUnbox(this.showTypes.get())) {
                    callSiteArray[0].callCurrent((GroovyObject)this, callSiteArray[1].callGetProperty(it));
                    callSiteArray[2].callCurrent((GroovyObject)this, " ");
                }
                if (it instanceof VariableExpression) {
                    callSiteArray[3].callCurrent(this, it, false);
                } else if (it instanceof ConstantExpression) {
                    callSiteArray[4].callCurrent(this, it, false);
                } else {
                    callSiteArray[5].call(it, this.getThisObject());
                }
                Object t = this.count.get();
                this.count.set((Integer)ScriptBytecodeAdapter.castToType(callSiteArray[6].call(t), Integer.class));
                if (DefaultTypeTransformation.booleanUnbox(this.count.get())) {
                    return callSiteArray[7].callCurrent((GroovyObject)this, ", ");
                }
                return null;
            }

            public boolean getShowTypes() {
                CallSite[] callSiteArray = _visitArgumentlistExpression_closure24.$getCallSiteArray();
                return DefaultTypeTransformation.booleanUnbox(this.showTypes.get());
            }

            public Integer getCount() {
                CallSite[] callSiteArray = _visitArgumentlistExpression_closure24.$getCallSiteArray();
                return (Integer)ScriptBytecodeAdapter.castToType(this.count.get(), Integer.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitArgumentlistExpression_closure24.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitArgumentlistExpression_closure24.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "visitType";
                stringArray[1] = "type";
                stringArray[2] = "print";
                stringArray[3] = "visitVariableExpression";
                stringArray[4] = "visitConstantExpression";
                stringArray[5] = "visit";
                stringArray[6] = "previous";
                stringArray[7] = "print";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[8];
                _visitArgumentlistExpression_closure24.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitArgumentlistExpression_closure24.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitArgumentlistExpression_closure24.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[445].call(callSiteArray[446].callGetProperty(expression), new _visitArgumentlistExpression_closure24(this, this, showTypes2, count));
        callSiteArray[447].callCurrent((GroovyObject)this, ")");
    }

    @Override
    public void visitBytecodeExpression(BytecodeExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[448].callCurrent((GroovyObject)this, "/*BytecodeExpression*/");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[449].callCurrent(this);
        } else {
            this.printLineBreak();
        }
    }

    @Override
    public void visitMapExpression(MapExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[450].callCurrent((GroovyObject)this, "[");
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[451].callSafe(callSiteArray[452].callGetPropertySafe(expression)), 0)) {
            callSiteArray[453].callCurrent((GroovyObject)this, ":");
        } else {
            callSiteArray[454].callCurrent((GroovyObject)this, callSiteArray[455].callGetPropertySafe(expression));
        }
        callSiteArray[456].callCurrent((GroovyObject)this, "]");
    }

    @Override
    public void visitMapEntryExpression(MapEntryExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        if (callSiteArray[457].callGetPropertySafe(expression) instanceof SpreadMapExpression) {
            callSiteArray[458].callCurrent((GroovyObject)this, "*");
        } else {
            callSiteArray[459].callSafe(callSiteArray[460].callGetPropertySafe(expression), this);
        }
        callSiteArray[461].callCurrent((GroovyObject)this, ": ");
        callSiteArray[462].callSafe(callSiteArray[463].callGetPropertySafe(expression), this);
    }

    @Override
    public void visitListExpression(ListExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[464].callCurrent((GroovyObject)this, "[");
        callSiteArray[465].callCurrent((GroovyObject)this, callSiteArray[466].callGetPropertySafe(expression));
        callSiteArray[467].callCurrent((GroovyObject)this, "]");
    }

    @Override
    public void visitTryCatchFinally(TryCatchStatement statement) {
        Reference<TryCatchStatement> statement2 = new Reference<TryCatchStatement>(statement);
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[468].callCurrent((GroovyObject)this, "try {");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[469].callCurrent(this);
        } else {
            this.printLineBreak();
        }
        public class _visitTryCatchFinally_closure25
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference statement;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitTryCatchFinally_closure25(Object _outerInstance, Object _thisObject, Reference statement) {
                Reference reference;
                CallSite[] callSiteArray = _visitTryCatchFinally_closure25.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.statement = reference = statement;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitTryCatchFinally_closure25.$getCallSiteArray();
                return callSiteArray[0].callSafe(callSiteArray[1].callGetPropertySafe(this.statement.get()), this.getThisObject());
            }

            public TryCatchStatement getStatement() {
                CallSite[] callSiteArray = _visitTryCatchFinally_closure25.$getCallSiteArray();
                return (TryCatchStatement)ScriptBytecodeAdapter.castToType(this.statement.get(), TryCatchStatement.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitTryCatchFinally_closure25.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitTryCatchFinally_closure25.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "visit";
                stringArray[1] = "tryStatement";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _visitTryCatchFinally_closure25.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitTryCatchFinally_closure25.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitTryCatchFinally_closure25.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[470].callCurrent((GroovyObject)this, new _visitTryCatchFinally_closure25(this, this, statement2));
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[471].callCurrent(this);
        } else {
            this.printLineBreak();
        }
        callSiteArray[472].callCurrent((GroovyObject)this, "} ");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[473].callCurrent(this);
        } else {
            this.printLineBreak();
        }
        public class _visitTryCatchFinally_closure26
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitTryCatchFinally_closure26(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitTryCatchFinally_closure26.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(CatchStatement catchStatement) {
                CallSite[] callSiteArray = _visitTryCatchFinally_closure26.$getCallSiteArray();
                return callSiteArray[0].callCurrent((GroovyObject)this, catchStatement);
            }

            public Object call(CatchStatement catchStatement) {
                CallSite[] callSiteArray = _visitTryCatchFinally_closure26.$getCallSiteArray();
                return callSiteArray[1].callCurrent((GroovyObject)this, catchStatement);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitTryCatchFinally_closure26.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "visitCatchStatement";
                stringArray[1] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _visitTryCatchFinally_closure26.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitTryCatchFinally_closure26.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitTryCatchFinally_closure26.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[474].callSafe(callSiteArray[475].callGetPropertySafe(statement2.get()), new _visitTryCatchFinally_closure26(this, this));
        callSiteArray[476].callCurrent((GroovyObject)this, "finally { ");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[477].callCurrent(this);
        } else {
            this.printLineBreak();
        }
        public class _visitTryCatchFinally_closure27
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference statement;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitTryCatchFinally_closure27(Object _outerInstance, Object _thisObject, Reference statement) {
                Reference reference;
                CallSite[] callSiteArray = _visitTryCatchFinally_closure27.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.statement = reference = statement;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitTryCatchFinally_closure27.$getCallSiteArray();
                return callSiteArray[0].callSafe(callSiteArray[1].callGetPropertySafe(this.statement.get()), this.getThisObject());
            }

            public TryCatchStatement getStatement() {
                CallSite[] callSiteArray = _visitTryCatchFinally_closure27.$getCallSiteArray();
                return (TryCatchStatement)ScriptBytecodeAdapter.castToType(this.statement.get(), TryCatchStatement.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitTryCatchFinally_closure27.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitTryCatchFinally_closure27.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "visit";
                stringArray[1] = "finallyStatement";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _visitTryCatchFinally_closure27.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitTryCatchFinally_closure27.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitTryCatchFinally_closure27.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[478].callCurrent((GroovyObject)this, new _visitTryCatchFinally_closure27(this, this, statement2));
        callSiteArray[479].callCurrent((GroovyObject)this, "} ");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[480].callCurrent(this);
        } else {
            this.printLineBreak();
        }
    }

    @Override
    public void visitThrowStatement(ThrowStatement statement) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[481].callCurrent((GroovyObject)this, "throw ");
        callSiteArray[482].callSafe(callSiteArray[483].callGetPropertySafe(statement), this);
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[484].callCurrent(this);
        } else {
            this.printLineBreak();
        }
    }

    @Override
    public void visitSynchronizedStatement(SynchronizedStatement statement) {
        Reference<SynchronizedStatement> statement2 = new Reference<SynchronizedStatement>(statement);
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[485].callCurrent((GroovyObject)this, "synchronized (");
        callSiteArray[486].callSafe(callSiteArray[487].callGetPropertySafe(statement2.get()), this);
        callSiteArray[488].callCurrent((GroovyObject)this, ") {");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[489].callCurrent(this);
        } else {
            this.printLineBreak();
        }
        public class _visitSynchronizedStatement_closure28
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference statement;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitSynchronizedStatement_closure28(Object _outerInstance, Object _thisObject, Reference statement) {
                Reference reference;
                CallSite[] callSiteArray = _visitSynchronizedStatement_closure28.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.statement = reference = statement;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitSynchronizedStatement_closure28.$getCallSiteArray();
                return callSiteArray[0].callSafe(callSiteArray[1].callGetPropertySafe(this.statement.get()), this.getThisObject());
            }

            public SynchronizedStatement getStatement() {
                CallSite[] callSiteArray = _visitSynchronizedStatement_closure28.$getCallSiteArray();
                return (SynchronizedStatement)ScriptBytecodeAdapter.castToType(this.statement.get(), SynchronizedStatement.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitSynchronizedStatement_closure28.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitSynchronizedStatement_closure28.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "visit";
                stringArray[1] = "code";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _visitSynchronizedStatement_closure28.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitSynchronizedStatement_closure28.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitSynchronizedStatement_closure28.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[490].callCurrent((GroovyObject)this, new _visitSynchronizedStatement_closure28(this, this, statement2));
        callSiteArray[491].callCurrent((GroovyObject)this, "}");
    }

    @Override
    public void visitTernaryExpression(TernaryExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[492].callSafe(callSiteArray[493].callGetPropertySafe(expression), this);
        callSiteArray[494].callCurrent((GroovyObject)this, " ? ");
        callSiteArray[495].callSafe(callSiteArray[496].callGetPropertySafe(expression), this);
        callSiteArray[497].callCurrent((GroovyObject)this, " : ");
        callSiteArray[498].callSafe(callSiteArray[499].callGetPropertySafe(expression), this);
    }

    @Override
    public void visitShortTernaryExpression(ElvisOperatorExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[500].callCurrent((GroovyObject)this, expression);
    }

    @Override
    public void visitBooleanExpression(BooleanExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[501].callSafe(callSiteArray[502].callGetPropertySafe(expression), this);
    }

    @Override
    public void visitWhileLoop(WhileStatement statement) {
        Reference<WhileStatement> statement2 = new Reference<WhileStatement>(statement);
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[503].callCurrent((GroovyObject)this, "while (");
        callSiteArray[504].callSafe(callSiteArray[505].callGetPropertySafe(statement2.get()), this);
        callSiteArray[506].callCurrent((GroovyObject)this, ") {");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[507].callCurrent(this);
        } else {
            this.printLineBreak();
        }
        public class _visitWhileLoop_closure29
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference statement;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitWhileLoop_closure29(Object _outerInstance, Object _thisObject, Reference statement) {
                Reference reference;
                CallSite[] callSiteArray = _visitWhileLoop_closure29.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.statement = reference = statement;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitWhileLoop_closure29.$getCallSiteArray();
                return callSiteArray[0].callSafe(callSiteArray[1].callGetPropertySafe(this.statement.get()), this.getThisObject());
            }

            public WhileStatement getStatement() {
                CallSite[] callSiteArray = _visitWhileLoop_closure29.$getCallSiteArray();
                return (WhileStatement)ScriptBytecodeAdapter.castToType(this.statement.get(), WhileStatement.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitWhileLoop_closure29.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitWhileLoop_closure29.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "visit";
                stringArray[1] = "loopBlock";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _visitWhileLoop_closure29.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitWhileLoop_closure29.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitWhileLoop_closure29.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[508].callCurrent((GroovyObject)this, new _visitWhileLoop_closure29(this, this, statement2));
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[509].callCurrent(this);
        } else {
            this.printLineBreak();
        }
        callSiteArray[510].callCurrent((GroovyObject)this, "}");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[511].callCurrent(this);
        } else {
            this.printLineBreak();
        }
    }

    @Override
    public void visitDoWhileLoop(DoWhileStatement statement) {
        Reference<DoWhileStatement> statement2 = new Reference<DoWhileStatement>(statement);
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[512].callCurrent((GroovyObject)this, "do {");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[513].callCurrent(this);
        } else {
            this.printLineBreak();
        }
        public class _visitDoWhileLoop_closure30
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference statement;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitDoWhileLoop_closure30(Object _outerInstance, Object _thisObject, Reference statement) {
                Reference reference;
                CallSite[] callSiteArray = _visitDoWhileLoop_closure30.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.statement = reference = statement;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitDoWhileLoop_closure30.$getCallSiteArray();
                return callSiteArray[0].callSafe(callSiteArray[1].callGetPropertySafe(this.statement.get()), this.getThisObject());
            }

            public DoWhileStatement getStatement() {
                CallSite[] callSiteArray = _visitDoWhileLoop_closure30.$getCallSiteArray();
                return (DoWhileStatement)ScriptBytecodeAdapter.castToType(this.statement.get(), DoWhileStatement.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitDoWhileLoop_closure30.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitDoWhileLoop_closure30.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "visit";
                stringArray[1] = "loopBlock";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _visitDoWhileLoop_closure30.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitDoWhileLoop_closure30.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitDoWhileLoop_closure30.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[514].callCurrent((GroovyObject)this, new _visitDoWhileLoop_closure30(this, this, statement2));
        callSiteArray[515].callCurrent((GroovyObject)this, "} while (");
        callSiteArray[516].callSafe(callSiteArray[517].callGetPropertySafe(statement2.get()), this);
        callSiteArray[518].callCurrent((GroovyObject)this, ")");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[519].callCurrent(this);
        } else {
            this.printLineBreak();
        }
    }

    @Override
    public void visitCatchStatement(CatchStatement statement) {
        Reference<CatchStatement> statement2 = new Reference<CatchStatement>(statement);
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[520].callCurrent((GroovyObject)this, "catch (");
        callSiteArray[521].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[522].callGetProperty(statement2.get())}));
        callSiteArray[523].callCurrent((GroovyObject)this, ") {");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[524].callCurrent(this);
        } else {
            this.printLineBreak();
        }
        public class _visitCatchStatement_closure31
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference statement;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitCatchStatement_closure31(Object _outerInstance, Object _thisObject, Reference statement) {
                Reference reference;
                CallSite[] callSiteArray = _visitCatchStatement_closure31.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.statement = reference = statement;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitCatchStatement_closure31.$getCallSiteArray();
                return callSiteArray[0].callSafe(callSiteArray[1].callGetProperty(this.statement.get()), this.getThisObject());
            }

            public CatchStatement getStatement() {
                CallSite[] callSiteArray = _visitCatchStatement_closure31.$getCallSiteArray();
                return (CatchStatement)ScriptBytecodeAdapter.castToType(this.statement.get(), CatchStatement.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitCatchStatement_closure31.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitCatchStatement_closure31.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "visit";
                stringArray[1] = "code";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _visitCatchStatement_closure31.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitCatchStatement_closure31.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitCatchStatement_closure31.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[525].callCurrent((GroovyObject)this, new _visitCatchStatement_closure31(this, this, statement2));
        callSiteArray[526].callCurrent((GroovyObject)this, "} ");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[527].callCurrent(this);
        } else {
            this.printLineBreak();
        }
    }

    @Override
    public void visitBitwiseNegationExpression(BitwiseNegationExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[528].callCurrent((GroovyObject)this, "~(");
        callSiteArray[529].callSafe(callSiteArray[530].callGetPropertySafe(expression), this);
        callSiteArray[531].callCurrent((GroovyObject)this, ") ");
    }

    @Override
    public void visitAssertStatement(AssertStatement statement) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[532].callCurrent((GroovyObject)this, "assert ");
        callSiteArray[533].callSafe(callSiteArray[534].callGetPropertySafe(statement), this);
        callSiteArray[535].callCurrent((GroovyObject)this, " : ");
        callSiteArray[536].callSafe(callSiteArray[537].callGetPropertySafe(statement), this);
    }

    @Override
    public void visitClosureListExpression(ClosureListExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        Reference<Boolean> first = new Reference<Boolean>(true);
        public class _visitClosureListExpression_closure32
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference first;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitClosureListExpression_closure32(Object _outerInstance, Object _thisObject, Reference first) {
                Reference reference;
                CallSite[] callSiteArray = _visitClosureListExpression_closure32.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.first = reference = first;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitClosureListExpression_closure32.$getCallSiteArray();
                if (!DefaultTypeTransformation.booleanUnbox(this.first.get())) {
                    callSiteArray[0].callCurrent((GroovyObject)this, ";");
                }
                boolean bl = false;
                this.first.set(bl);
                return callSiteArray[1].call(it, this.getThisObject());
            }

            public Boolean getFirst() {
                CallSite[] callSiteArray = _visitClosureListExpression_closure32.$getCallSiteArray();
                return (Boolean)ScriptBytecodeAdapter.castToType(this.first.get(), Boolean.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitClosureListExpression_closure32.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitClosureListExpression_closure32.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "print";
                stringArray[1] = "visit";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _visitClosureListExpression_closure32.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitClosureListExpression_closure32.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitClosureListExpression_closure32.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[538].callSafe(callSiteArray[539].callGetPropertySafe(expression), new _visitClosureListExpression_closure32(this, this, first));
    }

    @Override
    public void visitMethodPointerExpression(MethodPointerExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[540].callSafe(callSiteArray[541].callGetPropertySafe(expression), this);
        callSiteArray[542].callCurrent((GroovyObject)this, ".&");
        callSiteArray[543].callSafe(callSiteArray[544].callGetPropertySafe(expression), this);
    }

    @Override
    public void visitArrayExpression(ArrayExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[545].callCurrent((GroovyObject)this, "new ");
        callSiteArray[546].callCurrent((GroovyObject)this, callSiteArray[547].callGetPropertySafe(expression));
        callSiteArray[548].callCurrent((GroovyObject)this, "[");
        callSiteArray[549].callCurrent((GroovyObject)this, callSiteArray[550].callGetPropertySafe(expression));
        callSiteArray[551].callCurrent((GroovyObject)this, "]");
    }

    private void visitExpressionsAndCommaSeparate(List<? super Expression> expressions) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        Reference<Boolean> first = new Reference<Boolean>(true);
        public class _visitExpressionsAndCommaSeparate_closure33
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference first;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitExpressionsAndCommaSeparate_closure33(Object _outerInstance, Object _thisObject, Reference first) {
                Reference reference;
                CallSite[] callSiteArray = _visitExpressionsAndCommaSeparate_closure33.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.first = reference = first;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitExpressionsAndCommaSeparate_closure33.$getCallSiteArray();
                if (!DefaultTypeTransformation.booleanUnbox(this.first.get())) {
                    callSiteArray[0].callCurrent((GroovyObject)this, ", ");
                }
                boolean bl = false;
                this.first.set(bl);
                return callSiteArray[1].call(it, this.getThisObject());
            }

            public Boolean getFirst() {
                CallSite[] callSiteArray = _visitExpressionsAndCommaSeparate_closure33.$getCallSiteArray();
                return (Boolean)ScriptBytecodeAdapter.castToType(this.first.get(), Boolean.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitExpressionsAndCommaSeparate_closure33.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitExpressionsAndCommaSeparate_closure33.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "print";
                stringArray[1] = "visit";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _visitExpressionsAndCommaSeparate_closure33.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitExpressionsAndCommaSeparate_closure33.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitExpressionsAndCommaSeparate_closure33.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[552].callSafe(expressions, new _visitExpressionsAndCommaSeparate_closure33(this, this, first));
    }

    @Override
    public void visitSpreadMapExpression(SpreadMapExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        callSiteArray[553].callCurrent((GroovyObject)this, "*:");
        callSiteArray[554].callSafe(callSiteArray[555].callGetPropertySafe(expression), this);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != AstNodeToScriptVisitor.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    @Override
    public void visitConstantExpression(ConstantExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        this.visitConstantExpression(expression, false);
    }

    @Override
    public void visitVariableExpression(VariableExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        this.visitVariableExpression(expression, true);
    }

    @Override
    public void visitArgumentlistExpression(ArgumentListExpression expression) {
        CallSite[] callSiteArray = AstNodeToScriptVisitor.$getCallSiteArray();
        this.visitArgumentlistExpression(expression, false);
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

    public Stack<String> getClassNameStack() {
        return this.classNameStack;
    }

    public void setClassNameStack(Stack<String> stack) {
        this.classNameStack = stack;
    }

    public String get_indent() {
        return this._indent;
    }

    public void set_indent(String string) {
        this._indent = string;
    }

    public boolean getReadyToIndent() {
        return this.readyToIndent;
    }

    public boolean isReadyToIndent() {
        return this.readyToIndent;
    }

    public void setReadyToIndent(boolean bl) {
        this.readyToIndent = bl;
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

    public boolean getScriptHasBeenVisited() {
        return this.scriptHasBeenVisited;
    }

    public boolean isScriptHasBeenVisited() {
        return this.scriptHasBeenVisited;
    }

    public void setScriptHasBeenVisited(boolean bl) {
        this.scriptHasBeenVisited = bl;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "visitPackage";
        stringArray[2] = "getPackage";
        stringArray[3] = "getAST";
        stringArray[4] = "visitAllImports";
        stringArray[5] = "visit";
        stringArray[6] = "getStatementBlock";
        stringArray[7] = "getAST";
        stringArray[8] = "visit";
        stringArray[9] = "getStatementBlock";
        stringArray[10] = "getAST";
        stringArray[11] = "isScript";
        stringArray[12] = "visitClass";
        stringArray[13] = "isScript";
        stringArray[14] = "visitClass";
        stringArray[15] = "each";
        stringArray[16] = "values";
        stringArray[17] = "getStaticImports";
        stringArray[18] = "getAST";
        stringArray[19] = "each";
        stringArray[20] = "values";
        stringArray[21] = "getStaticStarImports";
        stringArray[22] = "getAST";
        stringArray[23] = "printDoubleBreak";
        stringArray[24] = "each";
        stringArray[25] = "getImports";
        stringArray[26] = "getAST";
        stringArray[27] = "each";
        stringArray[28] = "getStarImports";
        stringArray[29] = "getAST";
        stringArray[30] = "printDoubleBreak";
        stringArray[31] = "toString";
        stringArray[32] = "print";
        stringArray[33] = "startsWith";
        stringArray[34] = "getAt";
        stringArray[35] = "endsWith";
        stringArray[36] = "toString";
        stringArray[37] = "startsWith";
        stringArray[38] = "getAt";
        stringArray[39] = "print";
        stringArray[40] = "<$constructor$>";
        stringArray[41] = "plus";
        stringArray[42] = "call";
        stringArray[43] = "endsWith";
        stringArray[44] = "toString";
        stringArray[45] = "print";
        stringArray[46] = "endsWith";
        stringArray[47] = "toString";
        stringArray[48] = "endsWith";
        stringArray[49] = "toString";
        stringArray[50] = "print";
        stringArray[51] = "print";
        stringArray[52] = "print";
        stringArray[53] = "each";
        stringArray[54] = "annotations";
        stringArray[55] = "endsWith";
        stringArray[56] = "text";
        stringArray[57] = "print";
        stringArray[58] = "getAt";
        stringArray[59] = "text";
        stringArray[60] = "print";
        stringArray[61] = "text";
        stringArray[62] = "printDoubleBreak";
        stringArray[63] = "each";
        stringArray[64] = "annotations";
        stringArray[65] = "print";
        stringArray[66] = "text";
        stringArray[67] = "printLineBreak";
        stringArray[68] = "push";
        stringArray[69] = "name";
        stringArray[70] = "each";
        stringArray[71] = "annotations";
        stringArray[72] = "visitModifiers";
        stringArray[73] = "modifiers";
        stringArray[74] = "print";
        stringArray[75] = "name";
        stringArray[76] = "visitGenerics";
        stringArray[77] = "genericsTypes";
        stringArray[78] = "each";
        stringArray[79] = "unresolvedInterfaces";
        stringArray[80] = "print";
        stringArray[81] = "visitType";
        stringArray[82] = "unresolvedSuperClass";
        stringArray[83] = "print";
        stringArray[84] = "printDoubleBreak";
        stringArray[85] = "indented";
        stringArray[86] = "print";
        stringArray[87] = "printLineBreak";
        stringArray[88] = "pop";
        stringArray[89] = "print";
        stringArray[90] = "each";
        stringArray[91] = "print";
        stringArray[92] = "visitMethod";
        stringArray[93] = "each";
        stringArray[94] = "each";
        stringArray[95] = "annotations";
        stringArray[96] = "visitModifiers";
        stringArray[97] = "modifiers";
        stringArray[98] = "name";
        stringArray[99] = "print";
        stringArray[100] = "peek";
        stringArray[101] = "visitParameters";
        stringArray[102] = "parameters";
        stringArray[103] = "print";
        stringArray[104] = "printLineBreak";
        stringArray[105] = "name";
        stringArray[106] = "print";
        stringArray[107] = "printLineBreak";
        stringArray[108] = "visitType";
        stringArray[109] = "returnType";
        stringArray[110] = "print";
        stringArray[111] = "name";
        stringArray[112] = "visitParameters";
        stringArray[113] = "parameters";
        stringArray[114] = "print";
        stringArray[115] = "exceptions";
        stringArray[116] = "print";
        stringArray[117] = "each";
        stringArray[118] = "exceptions";
        stringArray[119] = "print";
        stringArray[120] = "printLineBreak";
        stringArray[121] = "indented";
        stringArray[122] = "printLineBreak";
        stringArray[123] = "print";
        stringArray[124] = "printDoubleBreak";
        stringArray[125] = "isAbstract";
        stringArray[126] = "print";
        stringArray[127] = "isFinal";
        stringArray[128] = "print";
        stringArray[129] = "isInterface";
        stringArray[130] = "print";
        stringArray[131] = "isNative";
        stringArray[132] = "print";
        stringArray[133] = "isPrivate";
        stringArray[134] = "print";
        stringArray[135] = "isProtected";
        stringArray[136] = "print";
        stringArray[137] = "isPublic";
        stringArray[138] = "print";
        stringArray[139] = "isStatic";
        stringArray[140] = "print";
        stringArray[141] = "isSynchronized";
        stringArray[142] = "print";
        stringArray[143] = "isTransient";
        stringArray[144] = "print";
        stringArray[145] = "isVolatile";
        stringArray[146] = "print";
        stringArray[147] = "each";
        stringArray[148] = "annotations";
        stringArray[149] = "visitModifiers";
        stringArray[150] = "modifiers";
        stringArray[151] = "visitType";
        stringArray[152] = "type";
        stringArray[153] = "print";
        stringArray[154] = "name";
        stringArray[155] = "initialValueExpression";
        stringArray[156] = "transformToPrimitiveConstantIfPossible";
        stringArray[157] = "type";
        stringArray[158] = "isStatic";
        stringArray[159] = "modifiers";
        stringArray[160] = "isFinal";
        stringArray[161] = "getModifiers";
        stringArray[162] = "type";
        stringArray[163] = "isStaticConstantInitializerType";
        stringArray[164] = "print";
        stringArray[165] = "STRING_TYPE";
        stringArray[166] = "print";
        stringArray[167] = "plus";
        stringArray[168] = "plus";
        stringArray[169] = "replaceAll";
        stringArray[170] = "text";
        stringArray[171] = "initialValueExpression";
        stringArray[172] = "char_TYPE";
        stringArray[173] = "print";
        stringArray[174] = "text";
        stringArray[175] = "initialValueExpression";
        stringArray[176] = "print";
        stringArray[177] = "text";
        stringArray[178] = "initialValueExpression";
        stringArray[179] = "isStatic";
        stringArray[180] = "modifiers";
        stringArray[181] = "isFinal";
        stringArray[182] = "getModifiers";
        stringArray[183] = "type";
        stringArray[184] = "isStaticConstantInitializerType";
        stringArray[185] = "print";
        stringArray[186] = "STRING_TYPE";
        stringArray[187] = "print";
        stringArray[188] = "plus";
        stringArray[189] = "plus";
        stringArray[190] = "replaceAll";
        stringArray[191] = "text";
        stringArray[192] = "initialValueExpression";
        stringArray[193] = "char_TYPE";
        stringArray[194] = "print";
        stringArray[195] = "text";
        stringArray[196] = "initialValueExpression";
        stringArray[197] = "print";
        stringArray[198] = "text";
        stringArray[199] = "initialValueExpression";
        stringArray[200] = "printLineBreak";
        stringArray[201] = "print";
        stringArray[202] = "plus";
        stringArray[203] = "name";
        stringArray[204] = "classNode";
        stringArray[205] = "members";
        stringArray[206] = "print";
        stringArray[207] = "each";
        stringArray[208] = "members";
        stringArray[209] = "print";
        stringArray[210] = "each";
        stringArray[211] = "statements";
        stringArray[212] = "endsWith";
        stringArray[213] = "toString";
        stringArray[214] = "printLineBreak";
        stringArray[215] = "endsWith";
        stringArray[216] = "toString";
        stringArray[217] = "print";
        stringArray[218] = "variable";
        stringArray[219] = "FOR_LOOP_DUMMY";
        stringArray[220] = "visitParameters";
        stringArray[221] = "variable";
        stringArray[222] = "print";
        stringArray[223] = "collectionExpression";
        stringArray[224] = "visit";
        stringArray[225] = "collectionExpression";
        stringArray[226] = "visit";
        stringArray[227] = "collectionExpression";
        stringArray[228] = "print";
        stringArray[229] = "printLineBreak";
        stringArray[230] = "indented";
        stringArray[231] = "print";
        stringArray[232] = "printLineBreak";
        stringArray[233] = "print";
        stringArray[234] = "visit";
        stringArray[235] = "booleanExpression";
        stringArray[236] = "print";
        stringArray[237] = "printLineBreak";
        stringArray[238] = "indented";
        stringArray[239] = "printLineBreak";
        stringArray[240] = "elseBlock";
        stringArray[241] = "elseBlock";
        stringArray[242] = "print";
        stringArray[243] = "printLineBreak";
        stringArray[244] = "indented";
        stringArray[245] = "printLineBreak";
        stringArray[246] = "print";
        stringArray[247] = "printLineBreak";
        stringArray[248] = "visit";
        stringArray[249] = "expression";
        stringArray[250] = "printLineBreak";
        stringArray[251] = "print";
        stringArray[252] = "visit";
        stringArray[253] = "getExpression";
        stringArray[254] = "printLineBreak";
        stringArray[255] = "print";
        stringArray[256] = "visit";
        stringArray[257] = "expression";
        stringArray[258] = "print";
        stringArray[259] = "printLineBreak";
        stringArray[260] = "indented";
        stringArray[261] = "print";
        stringArray[262] = "printLineBreak";
        stringArray[263] = "print";
        stringArray[264] = "visit";
        stringArray[265] = "expression";
        stringArray[266] = "print";
        stringArray[267] = "printLineBreak";
        stringArray[268] = "indented";
        stringArray[269] = "print";
        stringArray[270] = "printLineBreak";
        stringArray[271] = "print";
        stringArray[272] = "printLineBreak";
        stringArray[273] = "getObjectExpression";
        stringArray[274] = "visitVariableExpression";
        stringArray[275] = "visit";
        stringArray[276] = "spreadSafe";
        stringArray[277] = "print";
        stringArray[278] = "safe";
        stringArray[279] = "print";
        stringArray[280] = "print";
        stringArray[281] = "getMethod";
        stringArray[282] = "visitConstantExpression";
        stringArray[283] = "visit";
        stringArray[284] = "visit";
        stringArray[285] = "getArguments";
        stringArray[286] = "print";
        stringArray[287] = "plus";
        stringArray[288] = "plus";
        stringArray[289] = "name";
        stringArray[290] = "ownerType";
        stringArray[291] = "method";
        stringArray[292] = "arguments";
        stringArray[293] = "arguments";
        stringArray[294] = "print";
        stringArray[295] = "visit";
        stringArray[296] = "arguments";
        stringArray[297] = "print";
        stringArray[298] = "visit";
        stringArray[299] = "arguments";
        stringArray[300] = "isSuperCall";
        stringArray[301] = "print";
        stringArray[302] = "isThisCall";
        stringArray[303] = "print";
        stringArray[304] = "print";
        stringArray[305] = "visitType";
        stringArray[306] = "type";
        stringArray[307] = "visit";
        stringArray[308] = "arguments";
        stringArray[309] = "visit";
        stringArray[310] = "leftExpression";
        stringArray[311] = "print";
        stringArray[312] = "text";
        stringArray[313] = "operation";
        stringArray[314] = "visit";
        stringArray[315] = "rightExpression";
        stringArray[316] = "text";
        stringArray[317] = "operation";
        stringArray[318] = "print";
        stringArray[319] = "print";
        stringArray[320] = "visit";
        stringArray[321] = "expression";
        stringArray[322] = "print";
        stringArray[323] = "print";
        stringArray[324] = "text";
        stringArray[325] = "operation";
        stringArray[326] = "print";
        stringArray[327] = "text";
        stringArray[328] = "operation";
        stringArray[329] = "print";
        stringArray[330] = "visit";
        stringArray[331] = "expression";
        stringArray[332] = "print";
        stringArray[333] = "print";
        stringArray[334] = "parameters";
        stringArray[335] = "visitParameters";
        stringArray[336] = "parameters";
        stringArray[337] = "print";
        stringArray[338] = "printLineBreak";
        stringArray[339] = "indented";
        stringArray[340] = "print";
        stringArray[341] = "print";
        stringArray[342] = "visitExpressionsAndCommaSeparate";
        stringArray[343] = "expressions";
        stringArray[344] = "print";
        stringArray[345] = "print";
        stringArray[346] = "visit";
        stringArray[347] = "from";
        stringArray[348] = "print";
        stringArray[349] = "visit";
        stringArray[350] = "to";
        stringArray[351] = "print";
        stringArray[352] = "visit";
        stringArray[353] = "objectExpression";
        stringArray[354] = "spreadSafe";
        stringArray[355] = "print";
        stringArray[356] = "isSafe";
        stringArray[357] = "print";
        stringArray[358] = "print";
        stringArray[359] = "property";
        stringArray[360] = "visitConstantExpression";
        stringArray[361] = "property";
        stringArray[362] = "visit";
        stringArray[363] = "property";
        stringArray[364] = "visitPropertyExpression";
        stringArray[365] = "print";
        stringArray[366] = "name";
        stringArray[367] = "field";
        stringArray[368] = "value";
        stringArray[369] = "replaceAll";
        stringArray[370] = "replaceAll";
        stringArray[371] = "value";
        stringArray[372] = "print";
        stringArray[373] = "print";
        stringArray[374] = "value";
        stringArray[375] = "print";
        stringArray[376] = "text";
        stringArray[377] = "print";
        stringArray[378] = "plus";
        stringArray[379] = "plus";
        stringArray[380] = "name";
        stringArray[381] = "print";
        stringArray[382] = "name";
        stringArray[383] = "leftExpression";
        stringArray[384] = "print";
        stringArray[385] = "visitArgumentlistExpression";
        stringArray[386] = "leftExpression";
        stringArray[387] = "print";
        stringArray[388] = "text";
        stringArray[389] = "operation";
        stringArray[390] = "visit";
        stringArray[391] = "rightExpression";
        stringArray[392] = "text";
        stringArray[393] = "operation";
        stringArray[394] = "print";
        stringArray[395] = "visitType";
        stringArray[396] = "type";
        stringArray[397] = "leftExpression";
        stringArray[398] = "visitBinaryExpression";
        stringArray[399] = "print";
        stringArray[400] = "plus";
        stringArray[401] = "plus";
        stringArray[402] = "text";
        stringArray[403] = "print";
        stringArray[404] = "visit";
        stringArray[405] = "expression";
        stringArray[406] = "print";
        stringArray[407] = "visit";
        stringArray[408] = "expression";
        stringArray[409] = "print";
        stringArray[410] = "print";
        stringArray[411] = "visit";
        stringArray[412] = "expression";
        stringArray[413] = "print";
        stringArray[414] = "print";
        stringArray[415] = "visit";
        stringArray[416] = "expression";
        stringArray[417] = "print";
        stringArray[418] = "print";
        stringArray[419] = "visit";
        stringArray[420] = "expression";
        stringArray[421] = "print";
        stringArray[422] = "visitType";
        stringArray[423] = "type";
        stringArray[424] = "print";
        stringArray[425] = "name";
        stringArray[426] = "endsWith";
        stringArray[427] = "indexOf";
        stringArray[428] = "print";
        stringArray[429] = "plus";
        stringArray[430] = "getAt";
        stringArray[431] = "name";
        stringArray[432] = "plus";
        stringArray[433] = "multiply";
        stringArray[434] = "print";
        stringArray[435] = "plus";
        stringArray[436] = "getAt";
        stringArray[437] = "name";
        stringArray[438] = "multiply";
        stringArray[439] = "print";
        stringArray[440] = "visitGenerics";
        stringArray[441] = "genericsTypes";
        stringArray[442] = "print";
        stringArray[443] = "size";
        stringArray[444] = "expressions";
        stringArray[445] = "each";
        stringArray[446] = "expressions";
        stringArray[447] = "print";
        stringArray[448] = "print";
        stringArray[449] = "printLineBreak";
        stringArray[450] = "print";
        stringArray[451] = "size";
        stringArray[452] = "mapEntryExpressions";
        stringArray[453] = "print";
        stringArray[454] = "visitExpressionsAndCommaSeparate";
        stringArray[455] = "mapEntryExpressions";
        stringArray[456] = "print";
        stringArray[457] = "keyExpression";
        stringArray[458] = "print";
        stringArray[459] = "visit";
        stringArray[460] = "keyExpression";
        stringArray[461] = "print";
        stringArray[462] = "visit";
        stringArray[463] = "valueExpression";
        stringArray[464] = "print";
        stringArray[465] = "visitExpressionsAndCommaSeparate";
        stringArray[466] = "expressions";
        stringArray[467] = "print";
        stringArray[468] = "print";
        stringArray[469] = "printLineBreak";
        stringArray[470] = "indented";
        stringArray[471] = "printLineBreak";
        stringArray[472] = "print";
        stringArray[473] = "printLineBreak";
        stringArray[474] = "each";
        stringArray[475] = "catchStatements";
        stringArray[476] = "print";
        stringArray[477] = "printLineBreak";
        stringArray[478] = "indented";
        stringArray[479] = "print";
        stringArray[480] = "printLineBreak";
        stringArray[481] = "print";
        stringArray[482] = "visit";
        stringArray[483] = "expression";
        stringArray[484] = "printLineBreak";
        stringArray[485] = "print";
        stringArray[486] = "visit";
        stringArray[487] = "expression";
        stringArray[488] = "print";
        stringArray[489] = "printLineBreak";
        stringArray[490] = "indented";
        stringArray[491] = "print";
        stringArray[492] = "visit";
        stringArray[493] = "booleanExpression";
        stringArray[494] = "print";
        stringArray[495] = "visit";
        stringArray[496] = "trueExpression";
        stringArray[497] = "print";
        stringArray[498] = "visit";
        stringArray[499] = "falseExpression";
        stringArray[500] = "visitTernaryExpression";
        stringArray[501] = "visit";
        stringArray[502] = "expression";
        stringArray[503] = "print";
        stringArray[504] = "visit";
        stringArray[505] = "booleanExpression";
        stringArray[506] = "print";
        stringArray[507] = "printLineBreak";
        stringArray[508] = "indented";
        stringArray[509] = "printLineBreak";
        stringArray[510] = "print";
        stringArray[511] = "printLineBreak";
        stringArray[512] = "print";
        stringArray[513] = "printLineBreak";
        stringArray[514] = "indented";
        stringArray[515] = "print";
        stringArray[516] = "visit";
        stringArray[517] = "booleanExpression";
        stringArray[518] = "print";
        stringArray[519] = "printLineBreak";
        stringArray[520] = "print";
        stringArray[521] = "visitParameters";
        stringArray[522] = "variable";
        stringArray[523] = "print";
        stringArray[524] = "printLineBreak";
        stringArray[525] = "indented";
        stringArray[526] = "print";
        stringArray[527] = "printLineBreak";
        stringArray[528] = "print";
        stringArray[529] = "visit";
        stringArray[530] = "expression";
        stringArray[531] = "print";
        stringArray[532] = "print";
        stringArray[533] = "visit";
        stringArray[534] = "booleanExpression";
        stringArray[535] = "print";
        stringArray[536] = "visit";
        stringArray[537] = "messageExpression";
        stringArray[538] = "each";
        stringArray[539] = "expressions";
        stringArray[540] = "visit";
        stringArray[541] = "expression";
        stringArray[542] = "print";
        stringArray[543] = "visit";
        stringArray[544] = "methodName";
        stringArray[545] = "print";
        stringArray[546] = "visitType";
        stringArray[547] = "elementType";
        stringArray[548] = "print";
        stringArray[549] = "visitExpressionsAndCommaSeparate";
        stringArray[550] = "sizeExpression";
        stringArray[551] = "print";
        stringArray[552] = "each";
        stringArray[553] = "print";
        stringArray[554] = "visit";
        stringArray[555] = "expression";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[556];
        AstNodeToScriptVisitor.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(AstNodeToScriptVisitor.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = AstNodeToScriptVisitor.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

