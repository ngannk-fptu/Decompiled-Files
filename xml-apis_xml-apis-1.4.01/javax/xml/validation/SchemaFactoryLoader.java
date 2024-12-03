/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.validation;

import javax.xml.validation.SchemaFactory;

public abstract class SchemaFactoryLoader {
    protected SchemaFactoryLoader() {
    }

    public abstract SchemaFactory newFactory(String var1);
}

