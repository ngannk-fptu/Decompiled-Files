/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Scope {
    public static final String REQUEST = "request";
    public static final String SESSION = "session";
    public static final String APPLICATION = "application";
    private static final List<String> VALUES;
    private static final Scope INSTANCE;

    public static final Scope getInstance() {
        return INSTANCE;
    }

    public static final void add(String newScope) {
        if (VALUES.contains(newScope = newScope.toLowerCase())) {
            throw new IllegalArgumentException("Scope '" + newScope + "' has already been registered.");
        }
        VALUES.add(newScope);
    }

    public static final boolean exists(String scope) {
        scope = scope.toLowerCase();
        return VALUES.contains(scope);
    }

    public static final List<String> values() {
        return Collections.unmodifiableList(VALUES);
    }

    private Scope() {
    }

    static {
        ArrayList<String> defaults = new ArrayList<String>(3);
        defaults.add(REQUEST);
        defaults.add(SESSION);
        defaults.add(APPLICATION);
        VALUES = Collections.synchronizedList(defaults);
        INSTANCE = new Scope();
    }
}

