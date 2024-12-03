/*
 * Decompiled with CFR 0.152.
 */
package groovy.transform.stc;

import groovy.transform.stc.ClosureSignatureHint;
import java.util.Collections;
import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;

public abstract class SingleSignatureClosureHint
extends ClosureSignatureHint {
    public abstract ClassNode[] getParameterTypes(MethodNode var1, String[] var2, SourceUnit var3, CompilationUnit var4, ASTNode var5);

    @Override
    public List<ClassNode[]> getClosureSignatures(MethodNode node, SourceUnit sourceUnit, CompilationUnit compilationUnit, String[] options, ASTNode usage) {
        return Collections.singletonList(this.getParameterTypes(node, options, sourceUnit, compilationUnit, usage));
    }
}

