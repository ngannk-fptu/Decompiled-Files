/*
 * Decompiled with CFR 0.152.
 */
package groovy.inspect.swingui;

import groovy.inspect.swingui.ScriptToTreeNodeAdapter;
import groovy.inspect.swingui.TreeNodeBuildingVisitor;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class TreeNodeBuildingNodeOperation
extends CompilationUnit.PrimaryClassNodeOperation
implements GroovyObject {
    private final Object root;
    private final Object sourceCollected;
    private final ScriptToTreeNodeAdapter adapter;
    private final Object showScriptFreeForm;
    private final Object showScriptClass;
    private final Object showClosureClasses;
    private final Object nodeMaker;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public TreeNodeBuildingNodeOperation(ScriptToTreeNodeAdapter adapter, Object showScriptFreeForm, Object showScriptClass) {
        CallSite[] callSiteArray = TreeNodeBuildingNodeOperation.$getCallSiteArray();
        this(adapter, showScriptFreeForm, showScriptClass, false);
    }

    public TreeNodeBuildingNodeOperation(ScriptToTreeNodeAdapter adapter, Object showScriptFreeForm, Object showScriptClass, Object showClosureClasses) {
        Object object;
        Object object2;
        Object object3;
        Object object4;
        Object object5;
        MetaClass metaClass;
        Object object6;
        CallSite[] callSiteArray = TreeNodeBuildingNodeOperation.$getCallSiteArray();
        this.sourceCollected = object6 = callSiteArray[0].callConstructor(AtomicBoolean.class, false);
        this.metaClass = metaClass = this.$getStaticMetaClass();
        if (!DefaultTypeTransformation.booleanUnbox(adapter)) {
            throw (Throwable)callSiteArray[1].callConstructor(IllegalArgumentException.class, "Null: adapter");
        }
        ScriptToTreeNodeAdapter scriptToTreeNodeAdapter = adapter;
        this.adapter = (ScriptToTreeNodeAdapter)ScriptBytecodeAdapter.castToType(scriptToTreeNodeAdapter, ScriptToTreeNodeAdapter.class);
        this.showScriptFreeForm = object5 = showScriptFreeForm;
        this.showScriptClass = object4 = showScriptClass;
        this.showClosureClasses = object3 = showClosureClasses;
        this.nodeMaker = object2 = callSiteArray[2].callGroovyObjectGetProperty(adapter);
        this.root = object = callSiteArray[3].call(this.nodeMaker, "root");
    }

    @Override
    public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) {
        CallSite[] callSiteArray = TreeNodeBuildingNodeOperation.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[4].call(this.sourceCollected, true)) && DefaultTypeTransformation.booleanUnbox(this.showScriptFreeForm)) {
            ModuleNode ast = (ModuleNode)ScriptBytecodeAdapter.castToType(callSiteArray[5].call(source), ModuleNode.class);
            TreeNodeBuildingVisitor visitor = (TreeNodeBuildingVisitor)ScriptBytecodeAdapter.castToType(callSiteArray[6].callConstructor(TreeNodeBuildingVisitor.class, this.adapter), TreeNodeBuildingVisitor.class);
            callSiteArray[7].call(callSiteArray[8].call(ast), visitor);
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[9].callGroovyObjectGetProperty(visitor))) {
                callSiteArray[10].call(this.root, callSiteArray[11].callGroovyObjectGetProperty(visitor));
            }
            callSiteArray[12].callCurrent(this, "Methods", callSiteArray[13].call(ast));
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[14].call(classNode)) && !DefaultTypeTransformation.booleanUnbox(this.showScriptClass)) {
            return;
        }
        Object child = callSiteArray[15].call((Object)this.adapter, classNode);
        callSiteArray[16].call(this.root, child);
        callSiteArray[17].callCurrent(this, child, "Constructors", classNode);
        callSiteArray[18].callCurrent(this, child, "Methods", classNode);
        callSiteArray[19].callCurrent(this, child, "Fields", classNode);
        callSiteArray[20].callCurrent(this, child, "Properties", classNode);
        callSiteArray[21].callCurrent(this, child, "Annotations", classNode);
        if (DefaultTypeTransformation.booleanUnbox(this.showClosureClasses)) {
            callSiteArray[22].callCurrent((GroovyObject)this, classNode);
        }
    }

    protected void makeClosureClassTreeNodes(ClassNode classNode) {
        Reference<ClassNode> classNode2 = new Reference<ClassNode>(classNode);
        CallSite[] callSiteArray = TreeNodeBuildingNodeOperation.$getCallSiteArray();
        Object compileUnit = callSiteArray[23].callGetProperty(classNode2.get());
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[24].callGetProperty(compileUnit))) {
            return;
        }
        public class _makeClosureClassTreeNodes_closure1
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _makeClosureClassTreeNodes_closure1(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _makeClosureClassTreeNodes_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _makeClosureClassTreeNodes_closure1.$getCallSiteArray();
                return callSiteArray[0].callGetProperty(it);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _makeClosureClassTreeNodes_closure1.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _makeClosureClassTreeNodes_closure1.class) {
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
                stringArray[0] = "name";
                return new CallSiteArray(_makeClosureClassTreeNodes_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _makeClosureClassTreeNodes_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        Object innerClassNodes = callSiteArray[25].call(callSiteArray[26].call(callSiteArray[27].callGetProperty(compileUnit)), new _makeClosureClassTreeNodes_closure1(this, this));
        public class _makeClosureClassTreeNodes_closure2
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference classNode;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _makeClosureClassTreeNodes_closure2(Object _outerInstance, Object _thisObject, Reference classNode) {
                Reference reference;
                CallSite[] callSiteArray = _makeClosureClassTreeNodes_closure2.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.classNode = reference = classNode;
            }

            public Object doCall(InnerClassNode innerClassNode) {
                CallSite[] callSiteArray = _makeClosureClassTreeNodes_closure2.$getCallSiteArray();
                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call((Object)innerClassNode, callSiteArray[1].callGetProperty(ClassHelper.class)))) {
                    return null;
                }
                if (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[2].callGetProperty(innerClassNode), this.classNode.get())) {
                    return null;
                }
                Object child = callSiteArray[3].call(callSiteArray[4].callGroovyObjectGetProperty(this), innerClassNode);
                callSiteArray[5].call(callSiteArray[6].callGroovyObjectGetProperty(this), child);
                callSiteArray[7].callCurrent(this, child, "Constructors", innerClassNode);
                callSiteArray[8].callCurrent(this, child, "Methods", innerClassNode);
                callSiteArray[9].callCurrent(this, child, "Fields", innerClassNode);
                callSiteArray[10].callCurrent(this, child, "Properties", innerClassNode);
                return callSiteArray[11].callCurrent(this, child, "Annotations", innerClassNode);
            }

            public Object call(InnerClassNode innerClassNode) {
                CallSite[] callSiteArray = _makeClosureClassTreeNodes_closure2.$getCallSiteArray();
                return callSiteArray[12].callCurrent((GroovyObject)this, innerClassNode);
            }

            public ClassNode getClassNode() {
                CallSite[] callSiteArray = _makeClosureClassTreeNodes_closure2.$getCallSiteArray();
                return (ClassNode)ScriptBytecodeAdapter.castToType(this.classNode.get(), ClassNode.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _makeClosureClassTreeNodes_closure2.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "implementsInterface";
                stringArray[1] = "GENERATED_CLOSURE_Type";
                stringArray[2] = "outerMostClass";
                stringArray[3] = "make";
                stringArray[4] = "adapter";
                stringArray[5] = "add";
                stringArray[6] = "root";
                stringArray[7] = "collectConstructorData";
                stringArray[8] = "collectMethodData";
                stringArray[9] = "collectFieldData";
                stringArray[10] = "collectPropertyData";
                stringArray[11] = "collectAnnotationData";
                stringArray[12] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[13];
                _makeClosureClassTreeNodes_closure2.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_makeClosureClassTreeNodes_closure2.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _makeClosureClassTreeNodes_closure2.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[28].call(innerClassNodes, new _makeClosureClassTreeNodes_closure2(this, this, classNode2));
    }

    private List collectAnnotationData(Object parent, String name, ClassNode classNode) {
        CallSite[] callSiteArray = TreeNodeBuildingNodeOperation.$getCallSiteArray();
        Reference<Object> allAnnotations = new Reference<Object>(callSiteArray[29].call(this.nodeMaker, name));
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[30].callGetProperty(classNode))) {
            callSiteArray[31].call(parent, allAnnotations.get());
        }
        public class _collectAnnotationData_closure3
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference allAnnotations;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _collectAnnotationData_closure3(Object _outerInstance, Object _thisObject, Reference allAnnotations) {
                Reference reference;
                CallSite[] callSiteArray = _collectAnnotationData_closure3.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.allAnnotations = reference = allAnnotations;
            }

            public Object doCall(AnnotationNode annotationNode) {
                CallSite[] callSiteArray = _collectAnnotationData_closure3.$getCallSiteArray();
                Object ggrandchild = callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this), annotationNode);
                return callSiteArray[2].call(this.allAnnotations.get(), ggrandchild);
            }

            public Object call(AnnotationNode annotationNode) {
                CallSite[] callSiteArray = _collectAnnotationData_closure3.$getCallSiteArray();
                return callSiteArray[3].callCurrent((GroovyObject)this, annotationNode);
            }

            public Object getAllAnnotations() {
                CallSite[] callSiteArray = _collectAnnotationData_closure3.$getCallSiteArray();
                return this.allAnnotations.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _collectAnnotationData_closure3.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "make";
                stringArray[1] = "adapter";
                stringArray[2] = "add";
                stringArray[3] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[4];
                _collectAnnotationData_closure3.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_collectAnnotationData_closure3.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _collectAnnotationData_closure3.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return (List)ScriptBytecodeAdapter.castToType(callSiteArray[32].callSafe(callSiteArray[33].callGetProperty(classNode), new _collectAnnotationData_closure3(this, this, allAnnotations)), List.class);
    }

    private Object collectPropertyData(Object parent, String name, ClassNode classNode) {
        CallSite[] callSiteArray = TreeNodeBuildingNodeOperation.$getCallSiteArray();
        Reference<Object> allProperties = new Reference<Object>(callSiteArray[34].call(this.nodeMaker, name));
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[35].callGetProperty(classNode))) {
            callSiteArray[36].call(parent, allProperties.get());
        }
        public class _collectPropertyData_closure4
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference allProperties;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _collectPropertyData_closure4(Object _outerInstance, Object _thisObject, Reference allProperties) {
                Reference reference;
                CallSite[] callSiteArray = _collectPropertyData_closure4.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.allProperties = reference = allProperties;
            }

            public Object doCall(PropertyNode propertyNode) {
                CallSite[] callSiteArray = _collectPropertyData_closure4.$getCallSiteArray();
                Object ggrandchild = callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this), propertyNode);
                callSiteArray[2].call(this.allProperties.get(), ggrandchild);
                TreeNodeBuildingVisitor visitor = (TreeNodeBuildingVisitor)ScriptBytecodeAdapter.castToType(callSiteArray[3].callConstructor(TreeNodeBuildingVisitor.class, callSiteArray[4].callGroovyObjectGetProperty(this)), TreeNodeBuildingVisitor.class);
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[5].callGetPropertySafe(callSiteArray[6].callGetProperty(propertyNode)))) {
                    callSiteArray[7].call(callSiteArray[8].callGetProperty(callSiteArray[9].callGetProperty(propertyNode)), visitor);
                    return callSiteArray[10].call(ggrandchild, callSiteArray[11].callGroovyObjectGetProperty(visitor));
                }
                return null;
            }

            public Object call(PropertyNode propertyNode) {
                CallSite[] callSiteArray = _collectPropertyData_closure4.$getCallSiteArray();
                return callSiteArray[12].callCurrent((GroovyObject)this, propertyNode);
            }

            public Object getAllProperties() {
                CallSite[] callSiteArray = _collectPropertyData_closure4.$getCallSiteArray();
                return this.allProperties.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _collectPropertyData_closure4.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "make";
                stringArray[1] = "adapter";
                stringArray[2] = "add";
                stringArray[3] = "<$constructor$>";
                stringArray[4] = "adapter";
                stringArray[5] = "initialValueExpression";
                stringArray[6] = "field";
                stringArray[7] = "visit";
                stringArray[8] = "initialValueExpression";
                stringArray[9] = "field";
                stringArray[10] = "add";
                stringArray[11] = "currentNode";
                stringArray[12] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[13];
                _collectPropertyData_closure4.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_collectPropertyData_closure4.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _collectPropertyData_closure4.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return callSiteArray[37].callSafe(callSiteArray[38].callGetProperty(classNode), new _collectPropertyData_closure4(this, this, allProperties));
    }

    private Object collectFieldData(Object parent, String name, ClassNode classNode) {
        CallSite[] callSiteArray = TreeNodeBuildingNodeOperation.$getCallSiteArray();
        Reference<Object> allFields = new Reference<Object>(callSiteArray[39].call(this.nodeMaker, name));
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[40].callGetProperty(classNode))) {
            callSiteArray[41].call(parent, allFields.get());
        }
        public class _collectFieldData_closure5
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference allFields;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _collectFieldData_closure5(Object _outerInstance, Object _thisObject, Reference allFields) {
                Reference reference;
                CallSite[] callSiteArray = _collectFieldData_closure5.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.allFields = reference = allFields;
            }

            public Object doCall(FieldNode fieldNode) {
                CallSite[] callSiteArray = _collectFieldData_closure5.$getCallSiteArray();
                Object ggrandchild = callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this), fieldNode);
                callSiteArray[2].call(this.allFields.get(), ggrandchild);
                TreeNodeBuildingVisitor visitor = (TreeNodeBuildingVisitor)ScriptBytecodeAdapter.castToType(callSiteArray[3].callConstructor(TreeNodeBuildingVisitor.class, callSiteArray[4].callGroovyObjectGetProperty(this)), TreeNodeBuildingVisitor.class);
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[5].callGetProperty(fieldNode))) {
                    callSiteArray[6].call(callSiteArray[7].callGetProperty(fieldNode), visitor);
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[8].callGroovyObjectGetProperty(visitor))) {
                        return callSiteArray[9].call(ggrandchild, callSiteArray[10].callGroovyObjectGetProperty(visitor));
                    }
                    return null;
                }
                return null;
            }

            public Object call(FieldNode fieldNode) {
                CallSite[] callSiteArray = _collectFieldData_closure5.$getCallSiteArray();
                return callSiteArray[11].callCurrent((GroovyObject)this, fieldNode);
            }

            public Object getAllFields() {
                CallSite[] callSiteArray = _collectFieldData_closure5.$getCallSiteArray();
                return this.allFields.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _collectFieldData_closure5.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "make";
                stringArray[1] = "adapter";
                stringArray[2] = "add";
                stringArray[3] = "<$constructor$>";
                stringArray[4] = "adapter";
                stringArray[5] = "initialValueExpression";
                stringArray[6] = "visit";
                stringArray[7] = "initialValueExpression";
                stringArray[8] = "currentNode";
                stringArray[9] = "add";
                stringArray[10] = "currentNode";
                stringArray[11] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[12];
                _collectFieldData_closure5.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_collectFieldData_closure5.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _collectFieldData_closure5.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return callSiteArray[42].callSafe(callSiteArray[43].callGetProperty(classNode), new _collectFieldData_closure5(this, this, allFields));
    }

    private Object collectMethodData(Object parent, String name, ClassNode classNode) {
        CallSite[] callSiteArray = TreeNodeBuildingNodeOperation.$getCallSiteArray();
        Object allMethods = callSiteArray[44].call(this.nodeMaker, name);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[45].callGetProperty(classNode))) {
            callSiteArray[46].call(parent, allMethods);
        }
        return callSiteArray[47].callCurrent(this, allMethods, callSiteArray[48].callGetProperty(classNode));
    }

    private Object collectModuleNodeMethodData(String name, List methods) {
        CallSite[] callSiteArray = TreeNodeBuildingNodeOperation.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(methods)) {
            return null;
        }
        Object allMethods = callSiteArray[49].call(this.nodeMaker, name);
        callSiteArray[50].call(this.root, allMethods);
        return callSiteArray[51].callCurrent(this, allMethods, methods);
    }

    private Object doCollectMethodData(Object allMethods, List methods) {
        Reference<Object> allMethods2 = new Reference<Object>(allMethods);
        CallSite[] callSiteArray = TreeNodeBuildingNodeOperation.$getCallSiteArray();
        public class _doCollectMethodData_closure6
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference allMethods;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _doCollectMethodData_closure6(Object _outerInstance, Object _thisObject, Reference allMethods) {
                Reference reference;
                CallSite[] callSiteArray = _doCollectMethodData_closure6.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.allMethods = reference = allMethods;
            }

            public Object doCall(MethodNode methodNode) {
                CallSite[] callSiteArray = _doCollectMethodData_closure6.$getCallSiteArray();
                Reference<Object> ggrandchild = new Reference<Object>(callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this), methodNode));
                callSiteArray[2].call(this.allMethods.get(), ggrandchild.get());
                public class _closure8
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference ggrandchild;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure8(Object _outerInstance, Object _thisObject, Reference ggrandchild) {
                        Reference reference;
                        CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.ggrandchild = reference = ggrandchild;
                    }

                    public Object doCall(Parameter parameter) {
                        CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                        Object gggrandchild = callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this), parameter);
                        callSiteArray[2].call(this.ggrandchild.get(), gggrandchild);
                        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[3].callGetProperty(parameter))) {
                            TreeNodeBuildingVisitor visitor = (TreeNodeBuildingVisitor)ScriptBytecodeAdapter.castToType(callSiteArray[4].callConstructor(TreeNodeBuildingVisitor.class, callSiteArray[5].callGroovyObjectGetProperty(this)), TreeNodeBuildingVisitor.class);
                            callSiteArray[6].call(callSiteArray[7].callGetProperty(parameter), visitor);
                            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[8].callGroovyObjectGetProperty(visitor))) {
                                return callSiteArray[9].call(gggrandchild, callSiteArray[10].callGroovyObjectGetProperty(visitor));
                            }
                            return null;
                        }
                        return null;
                    }

                    public Object call(Parameter parameter) {
                        CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                        return callSiteArray[11].callCurrent((GroovyObject)this, parameter);
                    }

                    public Object getGgrandchild() {
                        CallSite[] callSiteArray = _closure8.$getCallSiteArray();
                        return this.ggrandchild.get();
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

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "make";
                        stringArray[1] = "adapter";
                        stringArray[2] = "add";
                        stringArray[3] = "initialExpression";
                        stringArray[4] = "<$constructor$>";
                        stringArray[5] = "adapter";
                        stringArray[6] = "visit";
                        stringArray[7] = "initialExpression";
                        stringArray[8] = "currentNode";
                        stringArray[9] = "add";
                        stringArray[10] = "currentNode";
                        stringArray[11] = "doCall";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[12];
                        _closure8.$createCallSiteArray_1(stringArray);
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
                callSiteArray[3].callSafe(callSiteArray[4].callGetProperty(methodNode), new _closure8(this, this.getThisObject(), ggrandchild));
                TreeNodeBuildingVisitor visitor = (TreeNodeBuildingVisitor)ScriptBytecodeAdapter.castToType(callSiteArray[5].callConstructor(TreeNodeBuildingVisitor.class, callSiteArray[6].callGroovyObjectGetProperty(this)), TreeNodeBuildingVisitor.class);
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[7].callGetProperty(methodNode))) {
                    callSiteArray[8].call(callSiteArray[9].callGetProperty(methodNode), visitor);
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[10].callGroovyObjectGetProperty(visitor))) {
                        return callSiteArray[11].call(ggrandchild.get(), callSiteArray[12].callGroovyObjectGetProperty(visitor));
                    }
                    return null;
                }
                return null;
            }

            public Object call(MethodNode methodNode) {
                CallSite[] callSiteArray = _doCollectMethodData_closure6.$getCallSiteArray();
                return callSiteArray[13].callCurrent((GroovyObject)this, methodNode);
            }

            public Object getAllMethods() {
                CallSite[] callSiteArray = _doCollectMethodData_closure6.$getCallSiteArray();
                return this.allMethods.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _doCollectMethodData_closure6.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "make";
                stringArray[1] = "adapter";
                stringArray[2] = "add";
                stringArray[3] = "each";
                stringArray[4] = "parameters";
                stringArray[5] = "<$constructor$>";
                stringArray[6] = "adapter";
                stringArray[7] = "code";
                stringArray[8] = "visit";
                stringArray[9] = "code";
                stringArray[10] = "currentNode";
                stringArray[11] = "add";
                stringArray[12] = "currentNode";
                stringArray[13] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[14];
                _doCollectMethodData_closure6.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_doCollectMethodData_closure6.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _doCollectMethodData_closure6.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return callSiteArray[52].callSafe((Object)methods, new _doCollectMethodData_closure6(this, this, allMethods2));
    }

    private Object collectConstructorData(Object parent, String name, ClassNode classNode) {
        CallSite[] callSiteArray = TreeNodeBuildingNodeOperation.$getCallSiteArray();
        Reference<Object> allCtors = new Reference<Object>(callSiteArray[53].call(this.nodeMaker, name));
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[54].callGetProperty(classNode))) {
            callSiteArray[55].call(parent, allCtors.get());
        }
        public class _collectConstructorData_closure7
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference allCtors;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _collectConstructorData_closure7(Object _outerInstance, Object _thisObject, Reference allCtors) {
                Reference reference;
                CallSite[] callSiteArray = _collectConstructorData_closure7.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.allCtors = reference = allCtors;
            }

            public Object doCall(ConstructorNode ctorNode) {
                CallSite[] callSiteArray = _collectConstructorData_closure7.$getCallSiteArray();
                Object ggrandchild = callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this), ctorNode);
                callSiteArray[2].call(this.allCtors.get(), ggrandchild);
                TreeNodeBuildingVisitor visitor = (TreeNodeBuildingVisitor)ScriptBytecodeAdapter.castToType(callSiteArray[3].callConstructor(TreeNodeBuildingVisitor.class, callSiteArray[4].callGroovyObjectGetProperty(this)), TreeNodeBuildingVisitor.class);
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[5].callGetProperty(ctorNode))) {
                    callSiteArray[6].call(callSiteArray[7].callGetProperty(ctorNode), visitor);
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[8].callGroovyObjectGetProperty(visitor))) {
                        return callSiteArray[9].call(ggrandchild, callSiteArray[10].callGroovyObjectGetProperty(visitor));
                    }
                    return null;
                }
                return null;
            }

            public Object call(ConstructorNode ctorNode) {
                CallSite[] callSiteArray = _collectConstructorData_closure7.$getCallSiteArray();
                return callSiteArray[11].callCurrent((GroovyObject)this, ctorNode);
            }

            public Object getAllCtors() {
                CallSite[] callSiteArray = _collectConstructorData_closure7.$getCallSiteArray();
                return this.allCtors.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _collectConstructorData_closure7.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "make";
                stringArray[1] = "adapter";
                stringArray[2] = "add";
                stringArray[3] = "<$constructor$>";
                stringArray[4] = "adapter";
                stringArray[5] = "code";
                stringArray[6] = "visit";
                stringArray[7] = "code";
                stringArray[8] = "currentNode";
                stringArray[9] = "add";
                stringArray[10] = "currentNode";
                stringArray[11] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[12];
                _collectConstructorData_closure7.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_collectConstructorData_closure7.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _collectConstructorData_closure7.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return callSiteArray[56].callSafe(callSiteArray[57].callGetProperty(classNode), new _collectConstructorData_closure7(this, this, allCtors));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != TreeNodeBuildingNodeOperation.class) {
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

    public final Object getRoot() {
        return this.root;
    }

    public final Object getSourceCollected() {
        return this.sourceCollected;
    }

    public final ScriptToTreeNodeAdapter getAdapter() {
        return this.adapter;
    }

    public final Object getShowScriptFreeForm() {
        return this.showScriptFreeForm;
    }

    public final Object getShowScriptClass() {
        return this.showScriptClass;
    }

    public final Object getShowClosureClasses() {
        return this.showClosureClasses;
    }

    public final Object getNodeMaker() {
        return this.nodeMaker;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "nodeMaker";
        stringArray[3] = "makeNode";
        stringArray[4] = "getAndSet";
        stringArray[5] = "getAST";
        stringArray[6] = "<$constructor$>";
        stringArray[7] = "visit";
        stringArray[8] = "getStatementBlock";
        stringArray[9] = "currentNode";
        stringArray[10] = "add";
        stringArray[11] = "currentNode";
        stringArray[12] = "collectModuleNodeMethodData";
        stringArray[13] = "getMethods";
        stringArray[14] = "isScript";
        stringArray[15] = "make";
        stringArray[16] = "add";
        stringArray[17] = "collectConstructorData";
        stringArray[18] = "collectMethodData";
        stringArray[19] = "collectFieldData";
        stringArray[20] = "collectPropertyData";
        stringArray[21] = "collectAnnotationData";
        stringArray[22] = "makeClosureClassTreeNodes";
        stringArray[23] = "compileUnit";
        stringArray[24] = "generatedInnerClasses";
        stringArray[25] = "sort";
        stringArray[26] = "values";
        stringArray[27] = "generatedInnerClasses";
        stringArray[28] = "each";
        stringArray[29] = "makeNode";
        stringArray[30] = "annotations";
        stringArray[31] = "add";
        stringArray[32] = "each";
        stringArray[33] = "annotations";
        stringArray[34] = "makeNode";
        stringArray[35] = "properties";
        stringArray[36] = "add";
        stringArray[37] = "each";
        stringArray[38] = "properties";
        stringArray[39] = "makeNode";
        stringArray[40] = "fields";
        stringArray[41] = "add";
        stringArray[42] = "each";
        stringArray[43] = "fields";
        stringArray[44] = "makeNode";
        stringArray[45] = "methods";
        stringArray[46] = "add";
        stringArray[47] = "doCollectMethodData";
        stringArray[48] = "methods";
        stringArray[49] = "makeNode";
        stringArray[50] = "add";
        stringArray[51] = "doCollectMethodData";
        stringArray[52] = "each";
        stringArray[53] = "makeNode";
        stringArray[54] = "declaredConstructors";
        stringArray[55] = "add";
        stringArray[56] = "each";
        stringArray[57] = "declaredConstructors";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[58];
        TreeNodeBuildingNodeOperation.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(TreeNodeBuildingNodeOperation.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = TreeNodeBuildingNodeOperation.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

