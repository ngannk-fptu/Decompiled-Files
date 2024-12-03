/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.templates;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.abdera.i18n.templates.Context;
import org.apache.abdera.i18n.templates.Operation;

final class Evaluator {
    protected static final Pattern PATTERN = Pattern.compile("(?:-([^\\|]+)\\|)?(?:([^\\|]+)\\|)?(.*)");

    Evaluator() {
    }

    String[] getVariables(String token) {
        Matcher matcher = PATTERN.matcher(token);
        if (matcher.find()) {
            String op = matcher.group(1);
            String var = matcher.group(3);
            return Operation.get(op).getVariables(var);
        }
        return new String[0];
    }

    void explain(String token, Appendable buf) throws IOException {
        Matcher matcher = PATTERN.matcher(token);
        if (matcher.find()) {
            String op = matcher.group(1);
            String arg = matcher.group(2);
            String var = matcher.group(3);
            Operation.get(op).explain(var, arg, buf);
        }
    }

    String evaluate(String token, String defaultValue, Context context) {
        String value = null;
        Matcher matcher = PATTERN.matcher(token);
        if (matcher.find()) {
            String op = matcher.group(1);
            String arg = matcher.group(2);
            String var = matcher.group(3);
            value = Operation.get(op).evaluate(var, arg, context);
        }
        if (value == null) {
            value = defaultValue;
        }
        return value != null ? value : "";
    }

    String evaluate(String token, Context context) {
        return this.evaluate(token, "", context);
    }
}

