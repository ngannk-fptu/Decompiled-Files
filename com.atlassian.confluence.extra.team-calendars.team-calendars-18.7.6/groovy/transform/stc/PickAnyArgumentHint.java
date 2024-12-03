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

public class PickAnyArgumentHint
extends SingleSignatureClosureHint {
    private final int parameterIndex;
    private final int genericTypeIndex;

    public PickAnyArgumentHint() {
        this(0, -1);
    }

    public PickAnyArgumentHint(int parameterIndex, int genericTypeIndex) {
        this.parameterIndex = parameterIndex;
        this.genericTypeIndex = genericTypeIndex;
    }

    @Override
    public ClassNode[] getParameterTypes(MethodNode node, String[] options, SourceUnit sourceUnit, CompilationUnit unit, ASTNode usage) {
        ClassNode type = node.getParameters()[this.parameterIndex].getOriginType();
        if (this.genericTypeIndex >= 0) {
            type = PickAnyArgumentHint.pickGenericType(type, this.genericTypeIndex);
        }
        return new ClassNode[]{type};
    }
}

