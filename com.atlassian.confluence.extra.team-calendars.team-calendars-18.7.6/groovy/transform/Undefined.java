/*
 * Decompiled with CFR 0.152.
 */
package groovy.transform;

import org.codehaus.groovy.ast.ClassNode;

public final class Undefined {
    public static final String STRING = "<DummyUndefinedMarkerString-DoNotUse>";

    private Undefined() {
    }

    public static boolean isUndefined(String other) {
        return STRING.equals(other);
    }

    public static boolean isUndefined(ClassNode other) {
        return CLASS.class.getName().equals(other.getName());
    }

    public static final class CLASS {
    }
}

