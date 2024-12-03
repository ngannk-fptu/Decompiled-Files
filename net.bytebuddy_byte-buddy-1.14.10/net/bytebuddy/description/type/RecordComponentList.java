/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.description.type;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.bytebuddy.description.ByteCodeElement;
import net.bytebuddy.description.type.RecordComponentDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.FilterableList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface RecordComponentList<T extends RecordComponentDescription>
extends FilterableList<T, RecordComponentList<T>> {
    public ByteCodeElement.Token.TokenList<RecordComponentDescription.Token> asTokenList(ElementMatcher<? super TypeDescription> var1);

    public TypeList.Generic asTypeList();

    public RecordComponentList<RecordComponentDescription.InDefinedShape> asDefined();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Empty<S extends RecordComponentDescription>
    extends FilterableList.Empty<S, RecordComponentList<S>>
    implements RecordComponentList<S> {
        @Override
        public RecordComponentList<RecordComponentDescription.InDefinedShape> asDefined() {
            return new Empty<RecordComponentDescription.InDefinedShape>();
        }

        @Override
        public ByteCodeElement.Token.TokenList<RecordComponentDescription.Token> asTokenList(ElementMatcher<? super TypeDescription> matcher) {
            return new ByteCodeElement.Token.TokenList((ByteCodeElement.Token[])new RecordComponentDescription.Token[0]);
        }

        @Override
        public TypeList.Generic asTypeList() {
            return new TypeList.Generic.Empty();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class TypeSubstituting
    extends AbstractBase<RecordComponentDescription.InGenericShape> {
        private final TypeDescription.Generic declaringType;
        private final List<? extends RecordComponentDescription> recordComponentDescriptions;
        private final TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor;

        public TypeSubstituting(TypeDescription.Generic declaringType, List<? extends RecordComponentDescription> recordComponentDescriptions, TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor) {
            this.declaringType = declaringType;
            this.recordComponentDescriptions = recordComponentDescriptions;
            this.visitor = visitor;
        }

        @Override
        public RecordComponentDescription.InGenericShape get(int index) {
            return new RecordComponentDescription.TypeSubstituting(this.declaringType, this.recordComponentDescriptions.get(index), this.visitor);
        }

        @Override
        public int size() {
            return this.recordComponentDescriptions.size();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForTokens
    extends AbstractBase<RecordComponentDescription.InDefinedShape> {
        private final TypeDescription typeDescription;
        private final List<? extends RecordComponentDescription.Token> tokens;

        public ForTokens(TypeDescription typeDescription, RecordComponentDescription.Token ... token) {
            this(typeDescription, Arrays.asList(token));
        }

        public ForTokens(TypeDescription typeDescription, List<? extends RecordComponentDescription.Token> tokens) {
            this.typeDescription = typeDescription;
            this.tokens = tokens;
        }

        @Override
        public RecordComponentDescription.InDefinedShape get(int index) {
            return new RecordComponentDescription.Latent(this.typeDescription, this.tokens.get(index));
        }

        @Override
        public int size() {
            return this.tokens.size();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Explicit<S extends RecordComponentDescription>
    extends AbstractBase<S> {
        private final List<? extends S> recordComponents;

        public Explicit(S ... recordComponent) {
            this(Arrays.asList(recordComponent));
        }

        public Explicit(List<? extends S> recordComponents) {
            this.recordComponents = recordComponents;
        }

        @Override
        public S get(int index) {
            return (S)((RecordComponentDescription)this.recordComponents.get(index));
        }

        @Override
        public int size() {
            return this.recordComponents.size();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForLoadedRecordComponents
    extends AbstractBase<RecordComponentDescription.InDefinedShape> {
        private final List<?> recordComponents;

        protected ForLoadedRecordComponents(Object ... recordComponent) {
            this(Arrays.asList(recordComponent));
        }

        protected ForLoadedRecordComponents(List<?> recordComponents) {
            this.recordComponents = recordComponents;
        }

        @Override
        public RecordComponentDescription.InDefinedShape get(int index) {
            return new RecordComponentDescription.ForLoadedRecordComponent((AnnotatedElement)this.recordComponents.get(index));
        }

        @Override
        public int size() {
            return this.recordComponents.size();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class AbstractBase<S extends RecordComponentDescription>
    extends FilterableList.AbstractBase<S, RecordComponentList<S>>
    implements RecordComponentList<S> {
        @Override
        public ByteCodeElement.Token.TokenList<RecordComponentDescription.Token> asTokenList(ElementMatcher<? super TypeDescription> matcher) {
            ArrayList<RecordComponentDescription.Token> tokens = new ArrayList<RecordComponentDescription.Token>(this.size());
            for (RecordComponentDescription recordComponentDescription : this) {
                tokens.add(recordComponentDescription.asToken(matcher));
            }
            return new ByteCodeElement.Token.TokenList<RecordComponentDescription.Token>((List<RecordComponentDescription.Token>)tokens);
        }

        @Override
        public TypeList.Generic asTypeList() {
            ArrayList<TypeDescription.Generic> typeDescriptions = new ArrayList<TypeDescription.Generic>(this.size());
            for (RecordComponentDescription recordComponentDescription : this) {
                typeDescriptions.add(recordComponentDescription.getType());
            }
            return new TypeList.Generic.Explicit(typeDescriptions);
        }

        @Override
        public RecordComponentList<RecordComponentDescription.InDefinedShape> asDefined() {
            ArrayList recordComponents = new ArrayList(this.size());
            for (RecordComponentDescription recordComponentDescription : this) {
                recordComponents.add(recordComponentDescription.asDefined());
            }
            return new Explicit<RecordComponentDescription.InDefinedShape>(recordComponents);
        }

        @Override
        protected RecordComponentList<S> wrap(List<S> values) {
            return new Explicit<S>(values);
        }
    }
}

