/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.description.field;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.bytebuddy.description.ByteCodeElement;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.FilterableList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface FieldList<T extends FieldDescription>
extends FilterableList<T, FieldList<T>> {
    public ByteCodeElement.Token.TokenList<FieldDescription.Token> asTokenList(ElementMatcher<? super TypeDescription> var1);

    public FieldList<FieldDescription.InDefinedShape> asDefined();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Empty<S extends FieldDescription>
    extends FilterableList.Empty<S, FieldList<S>>
    implements FieldList<S> {
        @Override
        public ByteCodeElement.Token.TokenList<FieldDescription.Token> asTokenList(ElementMatcher<? super TypeDescription> matcher) {
            return new ByteCodeElement.Token.TokenList((ByteCodeElement.Token[])new FieldDescription.Token[0]);
        }

        @Override
        public FieldList<FieldDescription.InDefinedShape> asDefined() {
            return this;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class TypeSubstituting
    extends AbstractBase<FieldDescription.InGenericShape> {
        private final TypeDescription.Generic declaringType;
        private final List<? extends FieldDescription> fieldDescriptions;
        private final TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor;

        public TypeSubstituting(TypeDescription.Generic declaringType, List<? extends FieldDescription> fieldDescriptions, TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor) {
            this.declaringType = declaringType;
            this.fieldDescriptions = fieldDescriptions;
            this.visitor = visitor;
        }

        @Override
        public FieldDescription.InGenericShape get(int index) {
            return new FieldDescription.TypeSubstituting(this.declaringType, this.fieldDescriptions.get(index), this.visitor);
        }

        @Override
        public int size() {
            return this.fieldDescriptions.size();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForTokens
    extends AbstractBase<FieldDescription.InDefinedShape> {
        private final TypeDescription declaringType;
        private final List<? extends FieldDescription.Token> tokens;

        public ForTokens(TypeDescription declaringType, FieldDescription.Token ... token) {
            this(declaringType, Arrays.asList(token));
        }

        public ForTokens(TypeDescription declaringType, List<? extends FieldDescription.Token> tokens) {
            this.declaringType = declaringType;
            this.tokens = tokens;
        }

        @Override
        public FieldDescription.InDefinedShape get(int index) {
            return new FieldDescription.Latent(this.declaringType, this.tokens.get(index));
        }

        @Override
        public int size() {
            return this.tokens.size();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Explicit<S extends FieldDescription>
    extends AbstractBase<S> {
        private final List<? extends S> fieldDescriptions;

        public Explicit(S ... fieldDescription) {
            this(Arrays.asList(fieldDescription));
        }

        public Explicit(List<? extends S> fieldDescriptions) {
            this.fieldDescriptions = fieldDescriptions;
        }

        @Override
        public S get(int index) {
            return (S)((FieldDescription)this.fieldDescriptions.get(index));
        }

        @Override
        public int size() {
            return this.fieldDescriptions.size();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForLoadedFields
    extends AbstractBase<FieldDescription.InDefinedShape> {
        private final List<? extends Field> fields;

        public ForLoadedFields(Field ... field) {
            this(Arrays.asList(field));
        }

        public ForLoadedFields(List<? extends Field> fields) {
            this.fields = fields;
        }

        @Override
        public FieldDescription.InDefinedShape get(int index) {
            return new FieldDescription.ForLoadedField(this.fields.get(index));
        }

        @Override
        public int size() {
            return this.fields.size();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class AbstractBase<S extends FieldDescription>
    extends FilterableList.AbstractBase<S, FieldList<S>>
    implements FieldList<S> {
        @Override
        public ByteCodeElement.Token.TokenList<FieldDescription.Token> asTokenList(ElementMatcher<? super TypeDescription> matcher) {
            ArrayList tokens = new ArrayList(this.size());
            for (FieldDescription fieldDescription : this) {
                tokens.add(fieldDescription.asToken(matcher));
            }
            return new ByteCodeElement.Token.TokenList<FieldDescription.Token>(tokens);
        }

        @Override
        public FieldList<FieldDescription.InDefinedShape> asDefined() {
            ArrayList declaredForms = new ArrayList(this.size());
            for (FieldDescription fieldDescription : this) {
                declaredForms.add(fieldDescription.asDefined());
            }
            return new Explicit<FieldDescription.InDefinedShape>(declaredForms);
        }

        @Override
        protected FieldList<S> wrap(List<S> values) {
            return new Explicit<S>(values);
        }
    }
}

