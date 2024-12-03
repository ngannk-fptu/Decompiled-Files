/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.stc;

import groovy.lang.Closure;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.DynamicVariable;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCall;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.classgen.asm.InvocationWriter;
import org.codehaus.groovy.transform.stc.DelegationMetadata;
import org.codehaus.groovy.transform.stc.ExtensionMethodNode;
import org.codehaus.groovy.transform.stc.GroovyTypeCheckingExtensionSupport;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingVisitor;
import org.codehaus.groovy.transform.stc.StaticTypesMarker;
import org.codehaus.groovy.transform.stc.TypeCheckingContext;
import org.codehaus.groovy.transform.stc.TypeCheckingExtension;

public class AbstractTypeCheckingExtension
extends TypeCheckingExtension {
    private static final Logger LOG = Logger.getLogger(GroovyTypeCheckingExtensionSupport.class.getName());
    protected final TypeCheckingContext context;
    private final Set<MethodNode> generatedMethods = new LinkedHashSet<MethodNode>();
    private final LinkedList<TypeCheckingScope> scopeData = new LinkedList();
    protected boolean handled = false;
    protected boolean debug = false;

    public AbstractTypeCheckingExtension(StaticTypeCheckingVisitor typeCheckingVisitor) {
        super(typeCheckingVisitor);
        this.context = typeCheckingVisitor.typeCheckingContext;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    public TypeCheckingScope newScope() {
        TypeCheckingScope scope = new TypeCheckingScope(this.scopeData.peek());
        this.scopeData.addFirst(scope);
        return scope;
    }

    public TypeCheckingScope newScope(Closure code) {
        TypeCheckingScope scope = this.newScope();
        Closure callback = code.rehydrate(scope, this, this);
        callback.call();
        return scope;
    }

    public TypeCheckingScope scopeExit() {
        return this.scopeData.removeFirst();
    }

    public TypeCheckingScope getCurrentScope() {
        return this.scopeData.peek();
    }

    public TypeCheckingScope scopeExit(Closure code) {
        TypeCheckingScope scope = this.scopeData.peek();
        Closure copy = code.rehydrate(scope, this, this);
        copy.call();
        return this.scopeExit();
    }

    public boolean isGenerated(MethodNode node) {
        return this.generatedMethods.contains(node);
    }

    public List<MethodNode> unique(MethodNode node) {
        return Collections.singletonList(node);
    }

    public MethodNode newMethod(String name, Class returnType) {
        return this.newMethod(name, ClassHelper.make(returnType));
    }

    public MethodNode newMethod(String name, ClassNode returnType) {
        MethodNode node = new MethodNode(name, 1, returnType, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, EmptyStatement.INSTANCE);
        this.generatedMethods.add(node);
        return node;
    }

    public MethodNode newMethod(String name, final Callable<ClassNode> returnType) {
        MethodNode node = new MethodNode(name, 1, ClassHelper.OBJECT_TYPE, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, EmptyStatement.INSTANCE){

            @Override
            public ClassNode getReturnType() {
                try {
                    return (ClassNode)returnType.call();
                }
                catch (Exception e) {
                    return super.getReturnType();
                }
            }
        };
        this.generatedMethods.add(node);
        return node;
    }

    public void delegatesTo(ClassNode type) {
        this.delegatesTo(type, 0);
    }

    public void delegatesTo(ClassNode type, int strategy) {
        this.delegatesTo(type, strategy, this.typeCheckingVisitor.typeCheckingContext.delegationMetadata);
    }

    public void delegatesTo(ClassNode type, int strategy, DelegationMetadata parent) {
        this.typeCheckingVisitor.typeCheckingContext.delegationMetadata = new DelegationMetadata(type, strategy, parent);
    }

    public boolean isAnnotatedBy(ASTNode node, Class annotation) {
        return this.isAnnotatedBy(node, ClassHelper.make(annotation));
    }

    public boolean isAnnotatedBy(ASTNode node, ClassNode annotation) {
        return node instanceof AnnotatedNode && !((AnnotatedNode)node).getAnnotations(annotation).isEmpty();
    }

    public boolean isDynamic(VariableExpression var) {
        return var.getAccessedVariable() instanceof DynamicVariable;
    }

    public boolean isExtensionMethod(MethodNode node) {
        return node instanceof ExtensionMethodNode;
    }

    public ArgumentListExpression getArguments(MethodCall call) {
        return InvocationWriter.makeArgumentList(call.getArguments());
    }

    protected Object safeCall(Closure closure, Object ... args) {
        try {
            return closure.call(args);
        }
        catch (Exception err) {
            this.typeCheckingVisitor.getSourceUnit().addException(err);
            return null;
        }
    }

    public boolean isMethodCall(Object o) {
        return o instanceof MethodCallExpression;
    }

    public boolean argTypesMatches(ClassNode[] argTypes, Class ... classes) {
        if (classes == null) {
            return argTypes == null || argTypes.length == 0;
        }
        if (argTypes.length != classes.length) {
            return false;
        }
        boolean match = true;
        for (int i = 0; i < argTypes.length && match; ++i) {
            match = AbstractTypeCheckingExtension.matchWithOrWithourBoxing(argTypes[i], classes[i]);
        }
        return match;
    }

    private static boolean matchWithOrWithourBoxing(ClassNode argType, Class aClass) {
        ClassNode type = ClassHelper.make(aClass);
        if (ClassHelper.isPrimitiveType(type) && !ClassHelper.isPrimitiveType(argType)) {
            type = ClassHelper.getWrapper(type);
        } else if (ClassHelper.isPrimitiveType(argType) && !ClassHelper.isPrimitiveType(type)) {
            type = ClassHelper.getUnwrapper(type);
        }
        boolean match = argType.equals(type);
        return match;
    }

    public boolean argTypesMatches(MethodCall call, Class ... classes) {
        ArgumentListExpression argumentListExpression = InvocationWriter.makeArgumentList(call.getArguments());
        ClassNode[] argumentTypes = this.typeCheckingVisitor.getArgumentTypes(argumentListExpression);
        return this.argTypesMatches(argumentTypes, classes);
    }

    public boolean firstArgTypesMatches(ClassNode[] argTypes, Class ... classes) {
        if (classes == null) {
            return argTypes == null || argTypes.length == 0;
        }
        if (argTypes.length < classes.length) {
            return false;
        }
        boolean match = true;
        for (int i = 0; i < classes.length && match; ++i) {
            match = AbstractTypeCheckingExtension.matchWithOrWithourBoxing(argTypes[i], classes[i]);
        }
        return match;
    }

    public boolean firstArgTypesMatches(MethodCall call, Class ... classes) {
        ArgumentListExpression argumentListExpression = InvocationWriter.makeArgumentList(call.getArguments());
        ClassNode[] argumentTypes = this.typeCheckingVisitor.getArgumentTypes(argumentListExpression);
        return this.firstArgTypesMatches(argumentTypes, classes);
    }

    public boolean argTypeMatches(ClassNode[] argTypes, int index, Class clazz) {
        if (index >= argTypes.length) {
            return false;
        }
        return AbstractTypeCheckingExtension.matchWithOrWithourBoxing(argTypes[index], clazz);
    }

    public boolean argTypeMatches(MethodCall call, int index, Class clazz) {
        ArgumentListExpression argumentListExpression = InvocationWriter.makeArgumentList(call.getArguments());
        ClassNode[] argumentTypes = this.typeCheckingVisitor.getArgumentTypes(argumentListExpression);
        return this.argTypeMatches(argumentTypes, index, clazz);
    }

    public <R> R withTypeChecker(Closure<R> code) {
        Closure clone = (Closure)code.clone();
        clone.setDelegate(this.typeCheckingVisitor);
        clone.setResolveStrategy(1);
        return (R)clone.call();
    }

    public MethodNode makeDynamic(MethodCall call) {
        return this.makeDynamic(call, ClassHelper.OBJECT_TYPE);
    }

    public MethodNode makeDynamic(MethodCall call, ClassNode returnType) {
        TypeCheckingContext.EnclosingClosure enclosingClosure = this.context.getEnclosingClosure();
        MethodNode enclosingMethod = this.context.getEnclosingMethod();
        ((ASTNode)((Object)call)).putNodeMetaData((Object)StaticTypesMarker.DYNAMIC_RESOLUTION, returnType);
        if (enclosingClosure != null) {
            enclosingClosure.getClosureExpression().putNodeMetaData((Object)StaticTypesMarker.DYNAMIC_RESOLUTION, Boolean.TRUE);
        } else {
            enclosingMethod.putNodeMetaData((Object)StaticTypesMarker.DYNAMIC_RESOLUTION, Boolean.TRUE);
        }
        this.setHandled(true);
        if (this.debug) {
            LOG.info("Turning " + call.getText() + " into a dynamic method call returning " + returnType.toString(false));
        }
        return new MethodNode(call.getMethodAsString(), 0, returnType, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, EmptyStatement.INSTANCE);
    }

    public void makeDynamic(PropertyExpression pexp) {
        this.makeDynamic(pexp, ClassHelper.OBJECT_TYPE);
    }

    public void makeDynamic(PropertyExpression pexp, ClassNode returnType) {
        this.context.getEnclosingMethod().putNodeMetaData((Object)StaticTypesMarker.DYNAMIC_RESOLUTION, Boolean.TRUE);
        pexp.putNodeMetaData((Object)StaticTypesMarker.DYNAMIC_RESOLUTION, returnType);
        this.storeType(pexp, returnType);
        this.setHandled(true);
        if (this.debug) {
            LOG.info("Turning '" + pexp.getText() + "' into a dynamic property access of type " + returnType.toString(false));
        }
    }

    public void makeDynamic(VariableExpression vexp) {
        this.makeDynamic(vexp, ClassHelper.OBJECT_TYPE);
    }

    public void makeDynamic(VariableExpression vexp, ClassNode returnType) {
        this.context.getEnclosingMethod().putNodeMetaData((Object)StaticTypesMarker.DYNAMIC_RESOLUTION, Boolean.TRUE);
        vexp.putNodeMetaData((Object)StaticTypesMarker.DYNAMIC_RESOLUTION, returnType);
        this.storeType(vexp, returnType);
        this.setHandled(true);
        if (this.debug) {
            LOG.info("Turning '" + vexp.getText() + "' into a dynamic variable access of type " + returnType.toString(false));
        }
    }

    public void log(String message) {
        LOG.info(message);
    }

    public BinaryExpression getEnclosingBinaryExpression() {
        return this.context.getEnclosingBinaryExpression();
    }

    public void pushEnclosingBinaryExpression(BinaryExpression binaryExpression) {
        this.context.pushEnclosingBinaryExpression(binaryExpression);
    }

    public void pushEnclosingClosureExpression(ClosureExpression closureExpression) {
        this.context.pushEnclosingClosureExpression(closureExpression);
    }

    public Expression getEnclosingMethodCall() {
        return this.context.getEnclosingMethodCall();
    }

    public Expression popEnclosingMethodCall() {
        return this.context.popEnclosingMethodCall();
    }

    public MethodNode popEnclosingMethod() {
        return this.context.popEnclosingMethod();
    }

    public ClassNode getEnclosingClassNode() {
        return this.context.getEnclosingClassNode();
    }

    public List<MethodNode> getEnclosingMethods() {
        return this.context.getEnclosingMethods();
    }

    public MethodNode getEnclosingMethod() {
        return this.context.getEnclosingMethod();
    }

    public void popTemporaryTypeInfo() {
        this.context.popTemporaryTypeInfo();
    }

    public void pushEnclosingClassNode(ClassNode classNode) {
        this.context.pushEnclosingClassNode(classNode);
    }

    public BinaryExpression popEnclosingBinaryExpression() {
        return this.context.popEnclosingBinaryExpression();
    }

    public List<ClassNode> getEnclosingClassNodes() {
        return this.context.getEnclosingClassNodes();
    }

    public List<TypeCheckingContext.EnclosingClosure> getEnclosingClosureStack() {
        return this.context.getEnclosingClosureStack();
    }

    public ClassNode popEnclosingClassNode() {
        return this.context.popEnclosingClassNode();
    }

    public void pushEnclosingMethod(MethodNode methodNode) {
        this.context.pushEnclosingMethod(methodNode);
    }

    public Set<MethodNode> getGeneratedMethods() {
        return this.generatedMethods;
    }

    public List<BinaryExpression> getEnclosingBinaryExpressionStack() {
        return this.context.getEnclosingBinaryExpressionStack();
    }

    public TypeCheckingContext.EnclosingClosure getEnclosingClosure() {
        return this.context.getEnclosingClosure();
    }

    public List<Expression> getEnclosingMethodCalls() {
        return this.context.getEnclosingMethodCalls();
    }

    public void pushEnclosingMethodCall(Expression call) {
        this.context.pushEnclosingMethodCall(call);
    }

    public TypeCheckingContext.EnclosingClosure popEnclosingClosure() {
        return this.context.popEnclosingClosure();
    }

    public void pushTemporaryTypeInfo() {
        this.context.pushTemporaryTypeInfo();
    }

    private static class TypeCheckingScope
    extends LinkedHashMap<String, Object> {
        private final TypeCheckingScope parent;

        private TypeCheckingScope(TypeCheckingScope parentScope) {
            this.parent = parentScope;
        }

        public TypeCheckingScope getParent() {
            return this.parent;
        }
    }
}

