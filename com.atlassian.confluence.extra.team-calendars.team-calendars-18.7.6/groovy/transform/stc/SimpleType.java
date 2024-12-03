/*
 * Decompiled with CFR 0.152.
 */
package groovy.transform.stc;

import groovy.transform.stc.SingleSignatureClosureHint;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;

public class SimpleType
extends SingleSignatureClosureHint {
    @Override
    public ClassNode[] getParameterTypes(MethodNode node, String[] options, SourceUnit sourceUnit, CompilationUnit unit, ASTNode usage) {
        ClassNode[] result = new ClassNode[options.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = this.findClassNode(sourceUnit, unit, options[i]);
        }
        return result;
    }
}

