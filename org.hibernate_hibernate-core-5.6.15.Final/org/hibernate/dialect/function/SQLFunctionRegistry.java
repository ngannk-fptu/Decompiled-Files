/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.function;

import java.util.Map;
import java.util.TreeMap;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.SQLFunction;

public class SQLFunctionRegistry {
    private final Map<String, SQLFunction> functionMap = new TreeMap<String, SQLFunction>(String.CASE_INSENSITIVE_ORDER);

    public SQLFunctionRegistry(Dialect dialect, Map<String, SQLFunction> userFunctionMap) {
        this.functionMap.putAll(dialect.getFunctions());
        if (userFunctionMap != null) {
            this.functionMap.putAll(userFunctionMap);
        }
    }

    public SQLFunction findSQLFunction(String functionName) {
        return this.functionMap.get(functionName);
    }

    public boolean hasFunction(String functionName) {
        return this.functionMap.containsKey(functionName);
    }
}

