/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot;

import org.hibernate.HibernateException;

public enum SchemaAutoTooling {
    CREATE("create"),
    CREATE_DROP("create-drop"),
    CREATE_ONLY("create-only"),
    DROP("drop"),
    UPDATE("update"),
    VALIDATE("validate"),
    NONE("none");

    private final String externalForm;

    private SchemaAutoTooling(String externalForm) {
        this.externalForm = externalForm;
    }

    public static SchemaAutoTooling interpret(String configurationValue) {
        if (configurationValue == null) {
            return null;
        }
        if ((configurationValue = configurationValue.trim()).isEmpty() || SchemaAutoTooling.NONE.externalForm.equals(configurationValue)) {
            return null;
        }
        if (SchemaAutoTooling.VALIDATE.externalForm.equals(configurationValue)) {
            return VALIDATE;
        }
        if (SchemaAutoTooling.UPDATE.externalForm.equals(configurationValue)) {
            return UPDATE;
        }
        if (SchemaAutoTooling.CREATE.externalForm.equals(configurationValue)) {
            return CREATE;
        }
        if (SchemaAutoTooling.CREATE_DROP.externalForm.equals(configurationValue)) {
            return CREATE_DROP;
        }
        if (SchemaAutoTooling.CREATE_ONLY.externalForm.equals(configurationValue)) {
            return CREATE_ONLY;
        }
        if (SchemaAutoTooling.DROP.externalForm.equals(configurationValue)) {
            return DROP;
        }
        throw new HibernateException("Unrecognized hibernate.hbm2ddl.auto value: '" + configurationValue + "'.  Supported values include 'create', 'create-drop', 'create-only', 'drop', 'update', 'none' and 'validate'.");
    }
}

