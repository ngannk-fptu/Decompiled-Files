/*
 * Decompiled with CFR 0.152.
 */
package groovy.transform.stc;

import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.syntax.SyntaxException;

public class IncorrectTypeHintException
extends SyntaxException {
    public IncorrectTypeHintException(MethodNode mn, Throwable e, int line, int column) {
        super("Incorrect type hint in @ClosureParams in class " + mn.getDeclaringClass().getName() + " method " + mn.getTypeDescriptor() + " : " + e.getMessage(), e, line, column);
    }

    public IncorrectTypeHintException(MethodNode mn, String msg, int line, int column) {
        super("Incorrect type hint in @ClosureParams in class " + mn.getDeclaringClass().getName() + " method " + mn.getTypeDescriptor() + " : " + msg, line, column);
    }
}

