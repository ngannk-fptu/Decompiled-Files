/*
 * Decompiled with CFR 0.152.
 */
package groovy.model;

public interface ValueModel {
    public Object getValue();

    public void setValue(Object var1);

    public Class getType();

    public boolean isEditable();
}

