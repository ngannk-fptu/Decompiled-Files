/*
 * Decompiled with CFR 0.152.
 */
package groovy.transform.stc;

import groovy.transform.stc.ClosureSignatureHint;
import java.util.LinkedList;
import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.trait.Traits;

public class FromAbstractTypeMethods
extends ClosureSignatureHint {
    @Override
    public List<ClassNode[]> getClosureSignatures(MethodNode node, SourceUnit sourceUnit, CompilationUnit compilationUnit, String[] options, ASTNode usage) {
        String className = options[0];
        ClassNode cn = this.findClassNode(sourceUnit, compilationUnit, className);
        return FromAbstractTypeMethods.extractSignaturesFromMethods(cn);
    }

    private static List<ClassNode[]> extractSignaturesFromMethods(ClassNode cn) {
        List<MethodNode> methods = cn.getAllDeclaredMethods();
        LinkedList<ClassNode[]> signatures = new LinkedList<ClassNode[]>();
        for (MethodNode method : methods) {
            if (method.isSynthetic() || !method.isAbstract()) continue;
            FromAbstractTypeMethods.extractParametersFromMethod(signatures, method);
        }
        return signatures;
    }

    private static void extractParametersFromMethod(List<ClassNode[]> signatures, MethodNode method) {
        if (Traits.hasDefaultImplementation(method)) {
            return;
        }
        Parameter[] parameters = method.getParameters();
        ClassNode[] typeParams = new ClassNode[parameters.length];
        for (int i = 0; i < parameters.length; ++i) {
            typeParams[i] = parameters[i].getOriginType();
        }
        signatures.add(typeParams);
    }
}

