/*
 * Decompiled with CFR 0.152.
 */
package groovy.transform.stc;

import groovy.transform.stc.ClosureSignatureHint;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.tools.GenericsUtils;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;

public class FromString
extends ClosureSignatureHint {
    @Override
    public List<ClassNode[]> getClosureSignatures(MethodNode node, SourceUnit sourceUnit, CompilationUnit compilationUnit, String[] options, ASTNode usage) {
        ArrayList<ClassNode[]> list = new ArrayList<ClassNode[]>(options.length);
        for (String option : options) {
            list.add(FromString.parseOption(option, sourceUnit, compilationUnit, node, usage));
        }
        return list;
    }

    private static ClassNode[] parseOption(String option, SourceUnit sourceUnit, CompilationUnit compilationUnit, MethodNode mn, ASTNode usage) {
        return GenericsUtils.parseClassNodesFromString(option, sourceUnit, compilationUnit, mn, usage);
    }
}

