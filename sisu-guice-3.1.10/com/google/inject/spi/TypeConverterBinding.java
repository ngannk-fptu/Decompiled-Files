/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.google.inject.spi;

import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Errors;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.Element;
import com.google.inject.spi.ElementVisitor;
import com.google.inject.spi.TypeConverter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class TypeConverterBinding
implements Element {
    private final Object source;
    private final Matcher<? super TypeLiteral<?>> typeMatcher;
    private final TypeConverter typeConverter;

    public TypeConverterBinding(Object source, Matcher<? super TypeLiteral<?>> typeMatcher, TypeConverter typeConverter) {
        this.source = Preconditions.checkNotNull((Object)source, (Object)"source");
        this.typeMatcher = (Matcher)Preconditions.checkNotNull(typeMatcher, (Object)"typeMatcher");
        this.typeConverter = (TypeConverter)Preconditions.checkNotNull((Object)typeConverter, (Object)"typeConverter");
    }

    @Override
    public Object getSource() {
        return this.source;
    }

    public Matcher<? super TypeLiteral<?>> getTypeMatcher() {
        return this.typeMatcher;
    }

    public TypeConverter getTypeConverter() {
        return this.typeConverter;
    }

    @Override
    public <T> T acceptVisitor(ElementVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public void applyTo(Binder binder) {
        binder.withSource(this.getSource()).convertToTypes(this.typeMatcher, this.typeConverter);
    }

    public String toString() {
        return this.typeConverter + " which matches " + this.typeMatcher + " (bound at " + Errors.convert(this.source) + ")";
    }
}

