/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema;

public enum Action {
    NONE("none"),
    CREATE_ONLY("create", "create-only"),
    DROP("drop"),
    CREATE("drop-and-create", "create"),
    CREATE_DROP(null, "create-drop"),
    VALIDATE(null, "validate"),
    UPDATE(null, "update");

    private final String externalJpaName;
    private final String externalHbm2ddlName;

    private Action(String externalJpaName) {
        this(externalJpaName, externalJpaName);
    }

    private Action(String externalJpaName, String externalHbm2ddlName) {
        this.externalJpaName = externalJpaName;
        this.externalHbm2ddlName = externalHbm2ddlName;
    }

    public String getExternalJpaName() {
        return this.externalJpaName;
    }

    public String getExternalHbm2ddlName() {
        return this.externalHbm2ddlName;
    }

    public String toString() {
        return ((Object)((Object)this)).getClass().getSimpleName() + "(externalJpaName=" + this.externalJpaName + ", externalHbm2ddlName=" + this.externalHbm2ddlName + ")";
    }

    public static Action interpretJpaSetting(Object value) {
        if (value == null) {
            return NONE;
        }
        if (Action.class.isInstance(value)) {
            return (Action)((Object)value);
        }
        String name = value.toString().trim();
        if (name.isEmpty() || Action.NONE.externalJpaName.equals(name)) {
            return NONE;
        }
        for (Action action : Action.values()) {
            if (action.externalJpaName == null || !action.externalJpaName.equals(name)) continue;
            return action;
        }
        for (Action action : Action.values()) {
            if (action.externalHbm2ddlName == null || !action.externalHbm2ddlName.equals(name)) continue;
            return action;
        }
        throw new IllegalArgumentException("Unrecognized JPA schema generation action value : " + value);
    }

    public static Action interpretHbm2ddlSetting(Object value) {
        if (value == null) {
            return NONE;
        }
        if (Action.class.isInstance(value)) {
            return Action.hbm2ddlSetting((Action)((Object)value));
        }
        String name = value.toString().trim();
        if (name.isEmpty() || Action.NONE.externalJpaName.equals(name)) {
            return NONE;
        }
        for (Action action : Action.values()) {
            if (action.externalHbm2ddlName == null || !action.externalHbm2ddlName.equals(name)) continue;
            return Action.hbm2ddlSetting(action);
        }
        for (Action action : Action.values()) {
            if (action.externalJpaName == null || !action.externalJpaName.equals(name)) continue;
            return Action.hbm2ddlSetting(action);
        }
        throw new IllegalArgumentException("Unrecognized legacy `hibernate.hbm2ddl.auto` value : `" + value + "`");
    }

    private static Action hbm2ddlSetting(Action action) {
        return action;
    }
}

