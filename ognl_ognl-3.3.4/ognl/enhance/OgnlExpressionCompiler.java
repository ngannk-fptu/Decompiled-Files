/*
 * Decompiled with CFR 0.152.
 */
package ognl.enhance;

import java.lang.reflect.Method;
import ognl.Node;
import ognl.OgnlContext;

public interface OgnlExpressionCompiler {
    public static final String ROOT_TYPE = "-ognl-root-type";

    public void compileExpression(OgnlContext var1, Node var2, Object var3) throws Exception;

    public String getClassName(Class var1);

    public Class getInterfaceClass(Class var1);

    public Class getSuperOrInterfaceClass(Method var1, Class var2);

    public Class getRootExpressionClass(Node var1, OgnlContext var2);

    public String castExpression(OgnlContext var1, Node var2, String var3);

    public String createLocalReference(OgnlContext var1, String var2, Class var3);
}

