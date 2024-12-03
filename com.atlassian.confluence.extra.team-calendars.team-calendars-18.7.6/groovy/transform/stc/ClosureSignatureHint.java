/*
 * Decompiled with CFR 0.152.
 */
package groovy.transform.stc;

import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;

public abstract class ClosureSignatureHint {
    public static ClassNode pickGenericType(ClassNode type, int gtIndex) {
        GenericsType[] genericsTypes = type.getGenericsTypes();
        if (genericsTypes == null || genericsTypes.length < gtIndex) {
            return ClassHelper.OBJECT_TYPE;
        }
        return genericsTypes[gtIndex].getType();
    }

    public static ClassNode pickGenericType(MethodNode node, int parameterIndex, int gtIndex) {
        Parameter[] parameters = node.getParameters();
        ClassNode type = parameters[parameterIndex].getOriginType();
        return ClosureSignatureHint.pickGenericType(type, gtIndex);
    }

    public abstract List<ClassNode[]> getClosureSignatures(MethodNode var1, SourceUnit var2, CompilationUnit var3, String[] var4, ASTNode var5);

    protected ClassNode findClassNode(SourceUnit sourceUnit, CompilationUnit compilationUnit, String className) {
        if (className.endsWith("[]")) {
            return this.findClassNode(sourceUnit, compilationUnit, className.substring(0, className.length() - 2)).makeArray();
        }
        ClassNode cn = compilationUnit.getClassNode(className);
        if (cn == null) {
            try {
                cn = ClassHelper.make(Class.forName(className, false, sourceUnit.getClassLoader()));
            }
            catch (ClassNotFoundException e) {
                cn = ClassHelper.make(className);
            }
        }
        return cn;
    }
}

