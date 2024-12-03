/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.tools;

import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.control.io.ReaderSource;

public class ClosureUtils {
    public static String convertClosureToSource(ReaderSource readerSource, ClosureExpression expression) throws Exception {
        if (expression == null) {
            throw new IllegalArgumentException("Null: expression");
        }
        StringBuilder result = new StringBuilder();
        for (int x = expression.getLineNumber(); x <= expression.getLastLineNumber(); ++x) {
            String line = readerSource.getLine(x, null);
            if (line == null) {
                throw new Exception("Error calculating source code for expression. Trying to read line " + x + " from " + readerSource.getClass());
            }
            if (x == expression.getLastLineNumber()) {
                line = line.substring(0, expression.getLastColumnNumber() - 1);
            }
            if (x == expression.getLineNumber()) {
                line = line.substring(expression.getColumnNumber() - 1);
            }
            result.append(line).append('\n');
        }
        String source = result.toString().trim();
        if (!source.startsWith("{")) {
            throw new Exception("Error converting ClosureExpression into source code. Closures must start with {. Found: " + source);
        }
        return source;
    }
}

