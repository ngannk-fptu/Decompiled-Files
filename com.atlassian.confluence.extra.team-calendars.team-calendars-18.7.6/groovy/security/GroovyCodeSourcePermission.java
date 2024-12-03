/*
 * Decompiled with CFR 0.152.
 */
package groovy.security;

import java.security.BasicPermission;

public final class GroovyCodeSourcePermission
extends BasicPermission {
    public GroovyCodeSourcePermission(String name) {
        super(name);
    }

    public GroovyCodeSourcePermission(String name, String actions) {
        super(name, actions);
    }
}

