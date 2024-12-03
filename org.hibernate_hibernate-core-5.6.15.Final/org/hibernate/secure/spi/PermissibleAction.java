/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.secure.spi;

@Deprecated
public enum PermissibleAction {
    INSERT("insert"),
    UPDATE("update"),
    DELETE("delete"),
    READ("read"),
    ANY("*"){

        @Override
        public String[] getImpliedActions() {
            return new String[]{INSERT.externalName, UPDATE.externalName, DELETE.externalName, READ.externalName};
        }
    };

    private final String externalName;
    private final String[] impliedActions;

    private PermissibleAction(String externalName) {
        this.externalName = externalName;
        this.impliedActions = this.buildImpliedActions(externalName);
    }

    private String[] buildImpliedActions(String externalName) {
        return new String[]{externalName};
    }

    public String getExternalName() {
        return this.externalName;
    }

    public String[] getImpliedActions() {
        return this.impliedActions;
    }

    public static PermissibleAction interpret(String action) {
        if (PermissibleAction.INSERT.externalName.equals(action)) {
            return INSERT;
        }
        if (PermissibleAction.UPDATE.externalName.equals(action)) {
            return UPDATE;
        }
        if (PermissibleAction.DELETE.externalName.equals(action)) {
            return DELETE;
        }
        if (PermissibleAction.READ.externalName.equals(action)) {
            return READ;
        }
        if (PermissibleAction.ANY.externalName.equals(action)) {
            return ANY;
        }
        throw new IllegalArgumentException("Unrecognized action : " + action);
    }
}

