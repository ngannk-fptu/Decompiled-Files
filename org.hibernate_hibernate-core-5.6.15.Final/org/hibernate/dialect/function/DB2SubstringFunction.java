/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.function;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class DB2SubstringFunction
extends StandardSQLFunction {
    private static final Set<String> possibleStringUnits = new HashSet<String>(Arrays.asList("CODEUNITS16", "CODEUNITS32", "OCTETS"));

    public DB2SubstringFunction() {
        super("substring", StandardBasicTypes.STRING);
    }

    @Override
    protected String getRenderedName(List arguments) {
        String lastArgument = (String)arguments.get(arguments.size() - 1);
        if (lastArgument != null && possibleStringUnits.contains(lastArgument.toUpperCase())) {
            return this.getName();
        }
        return "substr";
    }
}

