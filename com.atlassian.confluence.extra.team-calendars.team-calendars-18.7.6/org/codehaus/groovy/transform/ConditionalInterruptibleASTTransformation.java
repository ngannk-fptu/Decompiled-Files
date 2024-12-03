/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.transform.ConditionalInterrupt;
import java.lang.ref.SoftReference;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.tools.ClosureUtils;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.transform.AbstractInterruptibleASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class ConditionalInterruptibleASTTransformation
extends AbstractInterruptibleASTTransformation
implements GroovyObject {
    private static final ClassNode MY_TYPE;
    private ClosureExpression conditionNode;
    private String conditionMethod;
    private MethodCallExpression conditionCallExpression;
    private ClassNode currentClass;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ConditionalInterruptibleASTTransformation() {
        MetaClass metaClass;
        CallSite[] callSiteArray = ConditionalInterruptibleASTTransformation.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    protected ClassNode type() {
        CallSite[] callSiteArray = ConditionalInterruptibleASTTransformation.$getCallSiteArray();
        return MY_TYPE;
    }

    @Override
    protected void setupTransform(AnnotationNode node) {
        CallSite[] callSiteArray = ConditionalInterruptibleASTTransformation.$getCallSiteArray();
        ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractInterruptibleASTTransformation.class, this, "setupTransform", new Object[]{node});
        Object member = callSiteArray[0].call((Object)node, "value");
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (!DefaultTypeTransformation.booleanUnbox(member) || !(member instanceof ClosureExpression)) {
                callSiteArray[1].callStatic(ConditionalInterruptibleASTTransformation.class, new GStringImpl(new Object[]{member}, new String[]{"Expected closure value for annotation parameter 'value'. Found ", ""}));
            }
        } else if (!DefaultTypeTransformation.booleanUnbox(member) || !(member instanceof ClosureExpression)) {
            callSiteArray[2].callStatic(ConditionalInterruptibleASTTransformation.class, new GStringImpl(new Object[]{member}, new String[]{"Expected closure value for annotation parameter 'value'. Found ", ""}));
        }
        Object object = member;
        this.conditionNode = (ClosureExpression)ScriptBytecodeAdapter.castToType(object, ClosureExpression.class);
        Object object2 = callSiteArray[3].call(callSiteArray[4].call((Object)"conditionalTransform", callSiteArray[5].call(node)), "$condition");
        this.conditionMethod = ShortTypeHandling.castToString(object2);
        Object object3 = callSiteArray[6].callConstructor(MethodCallExpression.class, callSiteArray[7].callConstructor(VariableExpression.class, "this"), this.conditionMethod, callSiteArray[8].callConstructor(ArgumentListExpression.class));
        this.conditionCallExpression = (MethodCallExpression)ScriptBytecodeAdapter.castToType(object3, MethodCallExpression.class);
    }

    @Override
    protected String getErrorMessage() {
        CallSite[] callSiteArray = ConditionalInterruptibleASTTransformation.$getCallSiteArray();
        return ShortTypeHandling.castToString(callSiteArray[9].call((Object)"Execution interrupted. The following condition failed: ", callSiteArray[10].callCurrent((GroovyObject)this, this.conditionNode)));
    }

    @Override
    public void visitClass(ClassNode type) {
        ClassNode classNode;
        CallSite[] callSiteArray = ConditionalInterruptibleASTTransformation.$getCallSiteArray();
        this.currentClass = classNode = type;
        Object method = callSiteArray[11].call((Object)type, ArrayUtil.createArray(this.conditionMethod, callSiteArray[12].call(callSiteArray[13].callGroovyObjectGetProperty(this), callSiteArray[14].callGroovyObjectGetProperty(this)), callSiteArray[15].callGetProperty(ClassHelper.class), callSiteArray[16].callGetProperty(Parameter.class), callSiteArray[17].callGetProperty(ClassNode.class), callSiteArray[18].callGetProperty(this.conditionNode)));
        boolean bl = true;
        ScriptBytecodeAdapter.setProperty(bl, null, method, "synthetic");
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[19].callGroovyObjectGetProperty(this))) {
            ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractInterruptibleASTTransformation.class, this, "visitClass", new Object[]{type});
        }
    }

    @Override
    protected Expression createCondition() {
        CallSite[] callSiteArray = ConditionalInterruptibleASTTransformation.$getCallSiteArray();
        return this.conditionCallExpression;
    }

    @Override
    public void visitAnnotations(AnnotatedNode node) {
        CallSite[] callSiteArray = ConditionalInterruptibleASTTransformation.$getCallSiteArray();
    }

    @Override
    public void visitField(FieldNode node) {
        CallSite[] callSiteArray = ConditionalInterruptibleASTTransformation.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[20].call(node)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[21].call(node))) {
                ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractInterruptibleASTTransformation.class, this, "visitField", new Object[]{node});
            }
        } else if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[22].call(node)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[23].call(node))) {
            ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractInterruptibleASTTransformation.class, this, "visitField", new Object[]{node});
        }
    }

    @Override
    public void visitProperty(PropertyNode node) {
        CallSite[] callSiteArray = ConditionalInterruptibleASTTransformation.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[24].call(node)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[25].call(node))) {
                ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractInterruptibleASTTransformation.class, this, "visitProperty", new Object[]{node});
            }
        } else if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[26].call(node)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[27].call(node))) {
            ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractInterruptibleASTTransformation.class, this, "visitProperty", new Object[]{node});
        }
    }

    @Override
    public void visitClosureExpression(ClosureExpression closureExpr) {
        CallSite[] callSiteArray = ConditionalInterruptibleASTTransformation.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareEqual(closureExpr, this.conditionNode)) {
            return;
        }
        Object code = callSiteArray[28].callGetProperty(closureExpr);
        Object object = callSiteArray[29].callCurrent((GroovyObject)this, code);
        ScriptBytecodeAdapter.setProperty(object, null, closureExpr, "code");
        ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractInterruptibleASTTransformation.class, this, "visitClosureExpression", new Object[]{closureExpr});
    }

    @Override
    public void visitMethod(MethodNode node) {
        CallSite[] callSiteArray = ConditionalInterruptibleASTTransformation.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[30].callGetProperty(node), this.conditionMethod) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[31].call(node))) {
                return;
            }
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[32].callGetProperty(node), "run") && DefaultTypeTransformation.booleanUnbox(callSiteArray[33].call(this.currentClass)) && ScriptBytecodeAdapter.compareEqual(callSiteArray[34].callGetProperty(callSiteArray[35].callGetProperty(node)), 0)) {
                ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractInterruptibleASTTransformation.class, this, "visitMethod", new Object[]{node});
            } else {
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[36].callGroovyObjectGetProperty(this)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[37].call(node)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[38].call(node)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[39].call(node))) {
                    Object code = callSiteArray[40].callGetProperty(node);
                    Object object = callSiteArray[41].callCurrent((GroovyObject)this, code);
                    ScriptBytecodeAdapter.setProperty(object, null, node, "code");
                }
                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[42].call(node)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[43].call(node))) {
                    ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractInterruptibleASTTransformation.class, this, "visitMethod", new Object[]{node});
                }
            }
        } else {
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[44].callGetProperty(node), this.conditionMethod) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[45].call(node))) {
                return;
            }
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[46].callGetProperty(node), "run") && DefaultTypeTransformation.booleanUnbox(callSiteArray[47].call(this.currentClass)) && ScriptBytecodeAdapter.compareEqual(callSiteArray[48].callGetProperty(callSiteArray[49].callGetProperty(node)), 0)) {
                ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractInterruptibleASTTransformation.class, this, "visitMethod", new Object[]{node});
            } else {
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[50].callGroovyObjectGetProperty(this)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[51].call(node)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[52].call(node)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[53].call(node))) {
                    Object code = callSiteArray[54].callGetProperty(node);
                    Object object = callSiteArray[55].callCurrent((GroovyObject)this, code);
                    ScriptBytecodeAdapter.setProperty(object, null, node, "code");
                }
                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[56].call(node)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[57].call(node))) {
                    ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractInterruptibleASTTransformation.class, this, "visitMethod", new Object[]{node});
                }
            }
        }
    }

    private String convertClosureToSource(ClosureExpression expression) {
        CallSite[] callSiteArray = ConditionalInterruptibleASTTransformation.$getCallSiteArray();
        String string = ShortTypeHandling.castToString(callSiteArray[58].call(ClosureUtils.class, callSiteArray[59].callGetProperty(callSiteArray[60].callGroovyObjectGetProperty(this)), expression));
        try {
            return string;
        }
        catch (Exception e) {
            String string2 = ShortTypeHandling.castToString(callSiteArray[61].callGetProperty(e));
            return string2;
        }
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ConditionalInterruptibleASTTransformation.class) {
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
        Object object = ConditionalInterruptibleASTTransformation.$getCallSiteArray()[62].call(ClassHelper.class, ConditionalInterrupt.class);
        MY_TYPE = (ClassNode)ScriptBytecodeAdapter.castToType(object, ClassNode.class);
    }

    public /* synthetic */ void super$2$visitClosureExpression(ClosureExpression closureExpression) {
        super.visitClosureExpression(closureExpression);
    }

    public /* synthetic */ void super$3$visitField(FieldNode fieldNode) {
        super.visitField(fieldNode);
    }

    public /* synthetic */ void super$3$visitMethod(MethodNode methodNode) {
        super.visitMethod(methodNode);
    }

    public /* synthetic */ void super$3$visitClass(ClassNode classNode) {
        super.visitClass(classNode);
    }

    public /* synthetic */ void super$3$visitProperty(PropertyNode propertyNode) {
        super.visitProperty(propertyNode);
    }

    public /* synthetic */ void super$3$visitAnnotations(AnnotatedNode annotatedNode) {
        super.visitAnnotations(annotatedNode);
    }

    public /* synthetic */ void super$4$setupTransform(AnnotationNode annotationNode) {
        super.setupTransform(annotationNode);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "getMember";
        stringArray[1] = "internalError";
        stringArray[2] = "internalError";
        stringArray[3] = "plus";
        stringArray[4] = "plus";
        stringArray[5] = "hashCode";
        stringArray[6] = "<$constructor$>";
        stringArray[7] = "<$constructor$>";
        stringArray[8] = "<$constructor$>";
        stringArray[9] = "plus";
        stringArray[10] = "convertClosureToSource";
        stringArray[11] = "addMethod";
        stringArray[12] = "or";
        stringArray[13] = "ACC_PRIVATE";
        stringArray[14] = "ACC_SYNTHETIC";
        stringArray[15] = "OBJECT_TYPE";
        stringArray[16] = "EMPTY_ARRAY";
        stringArray[17] = "EMPTY_ARRAY";
        stringArray[18] = "code";
        stringArray[19] = "applyToAllMembers";
        stringArray[20] = "isStatic";
        stringArray[21] = "isSynthetic";
        stringArray[22] = "isStatic";
        stringArray[23] = "isSynthetic";
        stringArray[24] = "isStatic";
        stringArray[25] = "isSynthetic";
        stringArray[26] = "isStatic";
        stringArray[27] = "isSynthetic";
        stringArray[28] = "code";
        stringArray[29] = "wrapBlock";
        stringArray[30] = "name";
        stringArray[31] = "isSynthetic";
        stringArray[32] = "name";
        stringArray[33] = "isScript";
        stringArray[34] = "length";
        stringArray[35] = "parameters";
        stringArray[36] = "checkOnMethodStart";
        stringArray[37] = "isSynthetic";
        stringArray[38] = "isStatic";
        stringArray[39] = "isAbstract";
        stringArray[40] = "code";
        stringArray[41] = "wrapBlock";
        stringArray[42] = "isSynthetic";
        stringArray[43] = "isStatic";
        stringArray[44] = "name";
        stringArray[45] = "isSynthetic";
        stringArray[46] = "name";
        stringArray[47] = "isScript";
        stringArray[48] = "length";
        stringArray[49] = "parameters";
        stringArray[50] = "checkOnMethodStart";
        stringArray[51] = "isSynthetic";
        stringArray[52] = "isStatic";
        stringArray[53] = "isAbstract";
        stringArray[54] = "code";
        stringArray[55] = "wrapBlock";
        stringArray[56] = "isSynthetic";
        stringArray[57] = "isStatic";
        stringArray[58] = "convertClosureToSource";
        stringArray[59] = "source";
        stringArray[60] = "source";
        stringArray[61] = "message";
        stringArray[62] = "make";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[63];
        ConditionalInterruptibleASTTransformation.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ConditionalInterruptibleASTTransformation.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ConditionalInterruptibleASTTransformation.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}

