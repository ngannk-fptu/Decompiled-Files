/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.constants;

import java.io.ObjectStreamException;
import org.apache.axis.constants.Enum;

public class Scope
extends Enum {
    private static final Type type = new Type();
    public static final String REQUEST_STR = "Request";
    public static final String APPLICATION_STR = "Application";
    public static final String SESSION_STR = "Session";
    public static final String FACTORY_STR = "Factory";
    public static final Scope REQUEST = type.getScope("Request");
    public static final Scope APPLICATION = type.getScope("Application");
    public static final Scope SESSION = type.getScope("Session");
    public static final Scope FACTORY = type.getScope("Factory");
    public static final Scope DEFAULT = REQUEST;

    public static Scope getDefault() {
        return (Scope)type.getDefault();
    }

    public static final Scope getScope(int scope) {
        return type.getScope(scope);
    }

    public static final Scope getScope(String scope) {
        return type.getScope(scope);
    }

    public static final Scope getScope(String scope, Scope dephault) {
        return type.getScope(scope, dephault);
    }

    public static final boolean isValid(String scope) {
        return type.isValid(scope);
    }

    public static final int size() {
        return type.size();
    }

    public static final String[] getScopes() {
        return type.getEnumNames();
    }

    private Object readResolve() throws ObjectStreamException {
        return type.getScope(this.value);
    }

    private Scope(int value, String name) {
        super(type, value, name);
    }

    protected Scope() {
        super(type, DEFAULT.getValue(), DEFAULT.getName());
    }

    static {
        type.setDefault(DEFAULT);
    }

    public static class Type
    extends Enum.Type {
        private Type() {
            super("scope", new Enum[]{new Scope(0, Scope.REQUEST_STR), new Scope(1, Scope.APPLICATION_STR), new Scope(2, Scope.SESSION_STR), new Scope(3, Scope.FACTORY_STR)});
        }

        public final Scope getScope(int scope) {
            return (Scope)this.getEnum(scope);
        }

        public final Scope getScope(String scope) {
            return (Scope)this.getEnum(scope);
        }

        public final Scope getScope(String scope, Scope dephault) {
            return (Scope)this.getEnum(scope, dephault);
        }
    }
}

