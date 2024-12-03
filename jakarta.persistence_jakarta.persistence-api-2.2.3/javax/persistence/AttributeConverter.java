/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence;

public interface AttributeConverter<X, Y> {
    public Y convertToDatabaseColumn(X var1);

    public X convertToEntityAttribute(Y var1);
}

