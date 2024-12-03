/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.description;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.bytebuddy.description.DeclaredByType;
import net.bytebuddy.description.ModifierReviewable;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.annotation.AnnotationSource;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.FilterableList;

public interface ByteCodeElement
extends NamedElement.WithRuntimeName,
NamedElement.WithDescriptor,
ModifierReviewable.OfByteCodeElement,
DeclaredByType,
AnnotationSource {
    public boolean isVisibleTo(TypeDescription var1);

    public boolean isAccessibleTo(TypeDescription var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface Token<T extends Token<T>> {
        public T accept(TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> var1);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static class TokenList<S extends Token<S>>
        extends FilterableList.AbstractBase<S, TokenList<S>> {
            private final List<? extends S> tokens;

            public TokenList(S ... token) {
                this(Arrays.asList(token));
            }

            public TokenList(List<? extends S> tokens) {
                this.tokens = tokens;
            }

            public TokenList<S> accept(TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor) {
                ArrayList tokens = new ArrayList(this.tokens.size());
                for (Token token : this.tokens) {
                    tokens.add(token.accept(visitor));
                }
                return new TokenList(tokens);
            }

            @Override
            protected TokenList<S> wrap(List<S> values) {
                return new TokenList<S>(values);
            }

            @Override
            public S get(int index) {
                return (S)((Token)this.tokens.get(index));
            }

            @Override
            public int size() {
                return this.tokens.size();
            }
        }
    }

    public static interface Member
    extends DeclaredByType.WithMandatoryDeclaration,
    ByteCodeElement,
    NamedElement.WithGenericName {
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface TypeDependant<T extends TypeDependant<?, S>, S extends Token<S>> {
        public T asDefined();

        public S asToken(ElementMatcher<? super TypeDescription> var1);
    }
}

