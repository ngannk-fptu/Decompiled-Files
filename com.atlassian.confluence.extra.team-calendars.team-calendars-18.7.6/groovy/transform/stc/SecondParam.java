/*
 * Decompiled with CFR 0.152.
 */
package groovy.transform.stc;

import groovy.transform.stc.PickAnyArgumentHint;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;

public class SecondParam
extends PickAnyArgumentHint {
    public SecondParam() {
        super(1, -1);
    }

    public static class Component
    extends SecondParam {
        @Override
        public ClassNode[] getParameterTypes(MethodNode node, String[] options, SourceUnit sourceUnit, CompilationUnit unit, ASTNode usage) {
            ClassNode[] parameterTypes = super.getParameterTypes(node, options, sourceUnit, unit, usage);
            parameterTypes[0] = parameterTypes[0].getComponentType();
            return parameterTypes;
        }
    }

    public static class ThirdGenericType
    extends PickAnyArgumentHint {
        public ThirdGenericType() {
            super(1, 2);
        }
    }

    public static class SecondGenericType
    extends PickAnyArgumentHint {
        public SecondGenericType() {
            super(1, 1);
        }
    }

    public static class FirstGenericType
    extends PickAnyArgumentHint {
        public FirstGenericType() {
            super(1, 0);
        }
    }
}

