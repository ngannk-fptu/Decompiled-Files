/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm;

import groovy.lang.GroovyRuntimeException;
import groovyjarjarasm.asm.ClassVisitor;
import groovyjarjarasm.asm.MethodVisitor;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.InterfaceHelperClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.classgen.AsmClassGenerator;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.classgen.asm.AssertionWriter;
import org.codehaus.groovy.classgen.asm.BinaryExpressionHelper;
import org.codehaus.groovy.classgen.asm.BinaryExpressionMultiTypeDispatcher;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.classgen.asm.CallSiteWriter;
import org.codehaus.groovy.classgen.asm.ClosureWriter;
import org.codehaus.groovy.classgen.asm.CompileStack;
import org.codehaus.groovy.classgen.asm.InvocationWriter;
import org.codehaus.groovy.classgen.asm.OperandStack;
import org.codehaus.groovy.classgen.asm.OptimizingStatementWriter;
import org.codehaus.groovy.classgen.asm.StatementMetaTypeChooser;
import org.codehaus.groovy.classgen.asm.StatementWriter;
import org.codehaus.groovy.classgen.asm.TypeChooser;
import org.codehaus.groovy.classgen.asm.UnaryExpressionHelper;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.SourceUnit;

public class WriterController {
    private static Constructor indyWriter;
    private static Constructor indyCallSiteWriter;
    private static Constructor indyBinHelper;
    private AsmClassGenerator acg;
    private MethodVisitor methodVisitor;
    private CompileStack compileStack;
    private OperandStack operandStack;
    private ClassNode classNode;
    private CallSiteWriter callSiteWriter;
    private ClassVisitor cv;
    private ClosureWriter closureWriter;
    private String internalClassName;
    private InvocationWriter invocationWriter;
    private BinaryExpressionHelper binaryExpHelper;
    private BinaryExpressionHelper fastPathBinaryExpHelper;
    private UnaryExpressionHelper unaryExpressionHelper;
    private UnaryExpressionHelper fastPathUnaryExpressionHelper;
    private AssertionWriter assertionWriter;
    private String internalBaseClassName;
    private ClassNode outermostClass;
    private MethodNode methodNode;
    private SourceUnit sourceUnit;
    private ConstructorNode constructorNode;
    private GeneratorContext context;
    private InterfaceHelperClassNode interfaceClassLoadingClass;
    public boolean optimizeForInt = true;
    private StatementWriter statementWriter;
    private boolean fastPath = false;
    private TypeChooser typeChooser;
    private int bytecodeVersion = 49;
    private int lineNumber = -1;
    private int helperMethodIndex = 0;
    private List<String> superMethodNames = new ArrayList<String>();

    public void init(AsmClassGenerator asmClassGenerator, GeneratorContext gcon, ClassVisitor cv, ClassNode cn) {
        CompilerConfiguration config = cn.getCompileUnit().getConfig();
        Map<String, Boolean> optOptions = config.getOptimizationOptions();
        boolean invokedynamic = false;
        if (!optOptions.isEmpty()) {
            if (Boolean.FALSE.equals(optOptions.get("all"))) {
                this.optimizeForInt = false;
            } else {
                if (Boolean.TRUE.equals(optOptions.get("indy"))) {
                    invokedynamic = true;
                }
                if (Boolean.FALSE.equals(optOptions.get("int"))) {
                    this.optimizeForInt = false;
                }
                if (invokedynamic) {
                    this.optimizeForInt = false;
                }
            }
        }
        this.classNode = cn;
        this.outermostClass = null;
        this.internalClassName = BytecodeHelper.getClassInternalName(this.classNode);
        this.bytecodeVersion = WriterController.chooseBytecodeVersion(invokedynamic, config.getTargetBytecode());
        if (invokedynamic) {
            try {
                this.invocationWriter = (InvocationWriter)indyWriter.newInstance(this);
                this.callSiteWriter = (CallSiteWriter)indyCallSiteWriter.newInstance(this);
                this.binaryExpHelper = (BinaryExpressionHelper)indyBinHelper.newInstance(this);
            }
            catch (Exception e) {
                throw new GroovyRuntimeException("Cannot use invokedynamic, indy module was excluded from this build.");
            }
        } else {
            this.callSiteWriter = new CallSiteWriter(this);
            this.invocationWriter = new InvocationWriter(this);
            this.binaryExpHelper = new BinaryExpressionHelper(this);
        }
        this.unaryExpressionHelper = new UnaryExpressionHelper(this);
        if (this.optimizeForInt) {
            this.fastPathBinaryExpHelper = new BinaryExpressionMultiTypeDispatcher(this);
            this.fastPathUnaryExpressionHelper = new UnaryExpressionHelper(this);
        } else {
            this.fastPathBinaryExpHelper = this.binaryExpHelper;
            this.fastPathUnaryExpressionHelper = new UnaryExpressionHelper(this);
        }
        this.operandStack = new OperandStack(this);
        this.assertionWriter = new AssertionWriter(this);
        this.closureWriter = new ClosureWriter(this);
        this.internalBaseClassName = BytecodeHelper.getClassInternalName(this.classNode.getSuperClass());
        this.acg = asmClassGenerator;
        this.sourceUnit = this.acg.getSourceUnit();
        this.context = gcon;
        this.compileStack = new CompileStack(this);
        this.cv = cv;
        this.statementWriter = this.optimizeForInt ? new OptimizingStatementWriter(this) : new StatementWriter(this);
        this.typeChooser = new StatementMetaTypeChooser();
    }

    private static int chooseBytecodeVersion(boolean invokedynamic, String targetBytecode) {
        if (invokedynamic) {
            if ("1.8".equals(targetBytecode)) {
                return 52;
            }
            return 51;
        }
        if ("1.4".equals(targetBytecode)) {
            return 48;
        }
        if ("1.5".equals(targetBytecode)) {
            return 49;
        }
        if ("1.6".equals(targetBytecode)) {
            return 50;
        }
        if ("1.7".equals(targetBytecode)) {
            return 51;
        }
        if ("1.8".equals(targetBytecode)) {
            return 52;
        }
        throw new GroovyBugError("Bytecode version [" + targetBytecode + "] is not supported by the compiler");
    }

    public AsmClassGenerator getAcg() {
        return this.acg;
    }

    public void setMethodVisitor(MethodVisitor methodVisitor) {
        this.methodVisitor = methodVisitor;
    }

    public MethodVisitor getMethodVisitor() {
        return this.methodVisitor;
    }

    public CompileStack getCompileStack() {
        return this.compileStack;
    }

    public OperandStack getOperandStack() {
        return this.operandStack;
    }

    public ClassNode getClassNode() {
        return this.classNode;
    }

    public CallSiteWriter getCallSiteWriter() {
        return this.callSiteWriter;
    }

    public ClassVisitor getClassVisitor() {
        return this.cv;
    }

    public ClosureWriter getClosureWriter() {
        return this.closureWriter;
    }

    public ClassVisitor getCv() {
        return this.cv;
    }

    public String getInternalClassName() {
        return this.internalClassName;
    }

    public InvocationWriter getInvocationWriter() {
        return this.invocationWriter;
    }

    public BinaryExpressionHelper getBinaryExpressionHelper() {
        if (this.fastPath) {
            return this.fastPathBinaryExpHelper;
        }
        return this.binaryExpHelper;
    }

    public UnaryExpressionHelper getUnaryExpressionHelper() {
        if (this.fastPath) {
            return this.fastPathUnaryExpressionHelper;
        }
        return this.unaryExpressionHelper;
    }

    public AssertionWriter getAssertionWriter() {
        return this.assertionWriter;
    }

    public TypeChooser getTypeChooser() {
        return this.typeChooser;
    }

    public String getInternalBaseClassName() {
        return this.internalBaseClassName;
    }

    public MethodNode getMethodNode() {
        return this.methodNode;
    }

    public void setMethodNode(MethodNode mn) {
        this.methodNode = mn;
        this.constructorNode = null;
    }

    public ConstructorNode getConstructorNode() {
        return this.constructorNode;
    }

    public void setConstructorNode(ConstructorNode cn) {
        this.constructorNode = cn;
        this.methodNode = null;
    }

    public boolean isNotClinit() {
        return this.methodNode == null || !this.methodNode.getName().equals("<clinit>");
    }

    public SourceUnit getSourceUnit() {
        return this.sourceUnit;
    }

    public boolean isStaticContext() {
        if (this.compileStack != null && this.compileStack.getScope() != null) {
            return this.compileStack.getScope().isInStaticContext();
        }
        if (!this.isInClosure()) {
            return false;
        }
        if (this.constructorNode != null) {
            return false;
        }
        return this.classNode.isStaticClass() || this.methodNode.isStatic();
    }

    public boolean isInClosure() {
        return this.classNode.getOuterClass() != null && this.classNode.getSuperClass() == ClassHelper.CLOSURE_TYPE;
    }

    public boolean isInClosureConstructor() {
        return this.constructorNode != null && this.classNode.getOuterClass() != null && this.classNode.getSuperClass() == ClassHelper.CLOSURE_TYPE;
    }

    public boolean isNotExplicitThisInClosure(boolean implicitThis) {
        return implicitThis || !this.isInClosure();
    }

    public boolean isStaticMethod() {
        return this.methodNode != null && this.methodNode.isStatic();
    }

    public ClassNode getReturnType() {
        if (this.methodNode != null) {
            return this.methodNode.getReturnType();
        }
        if (this.constructorNode != null) {
            return this.constructorNode.getReturnType();
        }
        throw new GroovyBugError("I spotted a return that is neither in a method nor in a constructor... I can not handle that");
    }

    public boolean isStaticConstructor() {
        return this.methodNode != null && this.methodNode.getName().equals("<clinit>");
    }

    public boolean isConstructor() {
        return this.constructorNode != null;
    }

    public boolean isInScriptBody() {
        if (this.classNode.isScriptBody()) {
            return true;
        }
        return this.classNode.isScript() && this.methodNode != null && this.methodNode.getName().equals("run");
    }

    public String getClassName() {
        String className = !this.classNode.isInterface() || this.interfaceClassLoadingClass == null ? this.internalClassName : BytecodeHelper.getClassInternalName(this.interfaceClassLoadingClass);
        return className;
    }

    public ClassNode getOutermostClass() {
        if (this.outermostClass == null) {
            this.outermostClass = this.classNode;
            while (this.outermostClass instanceof InnerClassNode) {
                this.outermostClass = this.outermostClass.getOuterClass();
            }
        }
        return this.outermostClass;
    }

    public GeneratorContext getContext() {
        return this.context;
    }

    public void setInterfaceClassLoadingClass(InterfaceHelperClassNode ihc) {
        this.interfaceClassLoadingClass = ihc;
    }

    public InterfaceHelperClassNode getInterfaceClassLoadingClass() {
        return this.interfaceClassLoadingClass;
    }

    public boolean shouldOptimizeForInt() {
        return this.optimizeForInt;
    }

    public StatementWriter getStatementWriter() {
        return this.statementWriter;
    }

    public void switchToFastPath() {
        this.fastPath = true;
        this.resetLineNumber();
    }

    public void switchToSlowPath() {
        this.fastPath = false;
        this.resetLineNumber();
    }

    public boolean isFastPath() {
        return this.fastPath;
    }

    public int getBytecodeVersion() {
        return this.bytecodeVersion;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public void setLineNumber(int n) {
        this.lineNumber = n;
    }

    public void resetLineNumber() {
        this.setLineNumber(-1);
    }

    public int getNextHelperMethodIndex() {
        return this.helperMethodIndex++;
    }

    public List<String> getSuperMethodNames() {
        return this.superMethodNames;
    }

    static {
        try {
            ClassLoader cl = WriterController.class.getClassLoader();
            Class<?> indyClass = cl.loadClass("org.codehaus.groovy.classgen.asm.indy.InvokeDynamicWriter");
            indyWriter = indyClass.getConstructor(WriterController.class);
            indyClass = cl.loadClass("org.codehaus.groovy.classgen.asm.indy.IndyCallSiteWriter");
            indyCallSiteWriter = indyClass.getConstructor(WriterController.class);
            indyClass = cl.loadClass("org.codehaus.groovy.classgen.asm.indy.IndyBinHelper");
            indyBinHelper = indyClass.getConstructor(WriterController.class);
        }
        catch (Exception e) {
            indyWriter = null;
            indyCallSiteWriter = null;
            indyBinHelper = null;
        }
    }
}

