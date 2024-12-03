/*
 * Decompiled with CFR 0.152.
 */
package groovy.xml;

import groovy.xml.MarkupBuilder;
import java.util.HashMap;
import java.util.Map;

public class MarkupBuilderHelper {
    private MarkupBuilder builder;

    public MarkupBuilderHelper(MarkupBuilder builder) {
        this.builder = builder;
    }

    public void yield(Object value) {
        this.yield(value.toString());
    }

    public void yield(String value) {
        this.builder.yield(value, true);
    }

    public void yieldUnescaped(Object value) {
        this.yieldUnescaped(value.toString());
    }

    public void yieldUnescaped(String value) {
        this.builder.yield(value, false);
    }

    public void comment(String value) {
        this.yieldUnescaped("<!-- " + value + " -->");
    }

    public void xmlDeclaration(Map<String, Object> args) {
        HashMap<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
        map.put("xml", args);
        this.pi(map);
    }

    public void pi(Map<String, Map<String, Object>> args) {
        this.builder.pi(args);
    }
}

