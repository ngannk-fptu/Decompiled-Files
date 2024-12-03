/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import java.io.Serializable;
import java.util.Properties;

public class TypeDef
implements Serializable {
    private String typeClass;
    private Properties parameters;

    public TypeDef(String typeClass, Properties parameters) {
        this.typeClass = typeClass;
        this.parameters = parameters;
    }

    public Properties getParameters() {
        return this.parameters;
    }

    public String getTypeClass() {
        return this.typeClass;
    }
}

