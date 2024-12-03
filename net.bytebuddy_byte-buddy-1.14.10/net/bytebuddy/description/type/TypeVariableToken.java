/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.description.type;

import java.util.Collections;
import java.util.List;
import net.bytebuddy.build.CachedReturnPlugin;
import net.bytebuddy.description.ByteCodeElement;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TypeVariableToken
implements ByteCodeElement.Token<TypeVariableToken> {
    private final String symbol;
    private final List<? extends TypeDescription.Generic> bounds;
    private final List<? extends AnnotationDescription> annotations;
    private transient /* synthetic */ int hashCode;

    public TypeVariableToken(String symbol, List<? extends TypeDescription.Generic> bounds) {
        this(symbol, bounds, Collections.emptyList());
    }

    public TypeVariableToken(String symbol, List<? extends TypeDescription.Generic> bounds, List<? extends AnnotationDescription> annotations) {
        this.symbol = symbol;
        this.bounds = bounds;
        this.annotations = annotations;
    }

    public static TypeVariableToken of(TypeDescription.Generic typeVariable, ElementMatcher<? super TypeDescription> matcher) {
        return new TypeVariableToken(typeVariable.getSymbol(), typeVariable.getUpperBounds().accept(new TypeDescription.Generic.Visitor.Substitutor.ForDetachment(matcher)), typeVariable.getDeclaredAnnotations());
    }

    public String getSymbol() {
        return this.symbol;
    }

    public TypeList.Generic getBounds() {
        return new TypeList.Generic.Explicit(this.bounds);
    }

    public AnnotationList getAnnotations() {
        return new AnnotationList.Explicit(this.annotations);
    }

    @Override
    public TypeVariableToken accept(TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor) {
        return new TypeVariableToken(this.symbol, this.getBounds().accept(visitor), this.annotations);
    }

    @CachedReturnPlugin.Enhance(value="hashCode")
    public int hashCode() {
        int n;
        int n2;
        int n3 = this.hashCode;
        if (n3 != 0) {
            n2 = 0;
        } else {
            TypeVariableToken typeVariableToken = this;
            int result = typeVariableToken.symbol.hashCode();
            result = 31 * result + typeVariableToken.bounds.hashCode();
            n2 = n = (result = 31 * result + typeVariableToken.annotations.hashCode());
        }
        if (n == 0) {
            n = this.hashCode;
        } else {
            this.hashCode = n;
        }
        return n;
    }

    public boolean equals(@MaybeNull Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TypeVariableToken)) {
            return false;
        }
        TypeVariableToken typeVariableToken = (TypeVariableToken)other;
        return this.symbol.equals(typeVariableToken.symbol) && this.bounds.equals(typeVariableToken.bounds) && this.annotations.equals(typeVariableToken.annotations);
    }

    public String toString() {
        return this.symbol;
    }
}

