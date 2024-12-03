/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.description.method;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.bytebuddy.description.ByteCodeElement;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.FilterableList;
import net.bytebuddy.utility.ConstructorComparator;
import net.bytebuddy.utility.GraalImageCode;
import net.bytebuddy.utility.MethodComparator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface MethodList<T extends MethodDescription>
extends FilterableList<T, MethodList<T>> {
    public ByteCodeElement.Token.TokenList<MethodDescription.Token> asTokenList(ElementMatcher<? super TypeDescription> var1);

    public List<MethodDescription.SignatureToken> asSignatureTokenList();

    public List<MethodDescription.SignatureToken> asSignatureTokenList(ElementMatcher<? super TypeDescription> var1, TypeDescription var2);

    public MethodList<MethodDescription.InDefinedShape> asDefined();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Empty<S extends MethodDescription>
    extends FilterableList.Empty<S, MethodList<S>>
    implements MethodList<S> {
        @Override
        public ByteCodeElement.Token.TokenList<MethodDescription.Token> asTokenList(ElementMatcher<? super TypeDescription> matcher) {
            return new ByteCodeElement.Token.TokenList((ByteCodeElement.Token[])new MethodDescription.Token[0]);
        }

        @Override
        public List<MethodDescription.SignatureToken> asSignatureTokenList() {
            return Collections.emptyList();
        }

        @Override
        public List<MethodDescription.SignatureToken> asSignatureTokenList(ElementMatcher<? super TypeDescription> matcher, TypeDescription typeDescription) {
            return Collections.emptyList();
        }

        @Override
        public MethodList<MethodDescription.InDefinedShape> asDefined() {
            return this;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class TypeSubstituting
    extends AbstractBase<MethodDescription.InGenericShape> {
        protected final TypeDescription.Generic declaringType;
        protected final List<? extends MethodDescription> methodDescriptions;
        protected final TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor;

        public TypeSubstituting(TypeDescription.Generic declaringType, List<? extends MethodDescription> methodDescriptions, TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor) {
            this.declaringType = declaringType;
            this.methodDescriptions = methodDescriptions;
            this.visitor = visitor;
        }

        @Override
        public MethodDescription.InGenericShape get(int index) {
            return new MethodDescription.TypeSubstituting(this.declaringType, this.methodDescriptions.get(index), this.visitor);
        }

        @Override
        public int size() {
            return this.methodDescriptions.size();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForTokens
    extends AbstractBase<MethodDescription.InDefinedShape> {
        private final TypeDescription declaringType;
        private final List<? extends MethodDescription.Token> tokens;

        public ForTokens(TypeDescription declaringType, MethodDescription.Token ... token) {
            this(declaringType, Arrays.asList(token));
        }

        public ForTokens(TypeDescription declaringType, List<? extends MethodDescription.Token> tokens) {
            this.declaringType = declaringType;
            this.tokens = tokens;
        }

        @Override
        public MethodDescription.InDefinedShape get(int index) {
            return new MethodDescription.Latent(this.declaringType, this.tokens.get(index));
        }

        @Override
        public int size() {
            return this.tokens.size();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Explicit<S extends MethodDescription>
    extends AbstractBase<S> {
        private final List<? extends S> methodDescriptions;

        public Explicit(S ... methodDescription) {
            this(Arrays.asList(methodDescription));
        }

        public Explicit(List<? extends S> methodDescriptions) {
            this.methodDescriptions = methodDescriptions;
        }

        @Override
        public S get(int index) {
            return (S)((MethodDescription)this.methodDescriptions.get(index));
        }

        @Override
        public int size() {
            return this.methodDescriptions.size();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForLoadedMethods
    extends AbstractBase<MethodDescription.InDefinedShape> {
        private final List<? extends Method> methods;
        private final List<? extends Constructor<?>> constructors;

        public ForLoadedMethods(Class<?> type) {
            this(GraalImageCode.getCurrent().sorted(type.getDeclaredConstructors(), ConstructorComparator.INSTANCE), GraalImageCode.getCurrent().sorted(type.getDeclaredMethods(), MethodComparator.INSTANCE));
        }

        public ForLoadedMethods(Constructor<?>[] constructor, Method[] method) {
            this(Arrays.asList(constructor), Arrays.asList(method));
        }

        public ForLoadedMethods(List<? extends Constructor<?>> constructors, List<? extends Method> methods) {
            this.constructors = constructors;
            this.methods = methods;
        }

        @Override
        public MethodDescription.InDefinedShape get(int index) {
            return index < this.constructors.size() ? new MethodDescription.ForLoadedConstructor(this.constructors.get(index)) : new MethodDescription.ForLoadedMethod(this.methods.get(index - this.constructors.size()));
        }

        @Override
        public int size() {
            return this.constructors.size() + this.methods.size();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class AbstractBase<S extends MethodDescription>
    extends FilterableList.AbstractBase<S, MethodList<S>>
    implements MethodList<S> {
        @Override
        protected MethodList<S> wrap(List<S> values) {
            return new Explicit<S>(values);
        }

        @Override
        public ByteCodeElement.Token.TokenList<MethodDescription.Token> asTokenList(ElementMatcher<? super TypeDescription> matcher) {
            ArrayList tokens = new ArrayList(this.size());
            for (MethodDescription methodDescription : this) {
                tokens.add(methodDescription.asToken(matcher));
            }
            return new ByteCodeElement.Token.TokenList<MethodDescription.Token>(tokens);
        }

        @Override
        public List<MethodDescription.SignatureToken> asSignatureTokenList() {
            ArrayList<MethodDescription.SignatureToken> tokens = new ArrayList<MethodDescription.SignatureToken>(this.size());
            for (MethodDescription methodDescription : this) {
                tokens.add(methodDescription.asSignatureToken());
            }
            return tokens;
        }

        @Override
        public List<MethodDescription.SignatureToken> asSignatureTokenList(ElementMatcher<? super TypeDescription> matcher, TypeDescription typeDescription) {
            ArrayList<MethodDescription.SignatureToken> tokens = new ArrayList<MethodDescription.SignatureToken>(this.size());
            for (MethodDescription methodDescription : this) {
                tokens.add(((MethodDescription.Token)methodDescription.asToken(matcher)).asSignatureToken(typeDescription));
            }
            return tokens;
        }

        @Override
        public MethodList<MethodDescription.InDefinedShape> asDefined() {
            ArrayList declaredForms = new ArrayList(this.size());
            for (MethodDescription methodDescription : this) {
                declaredForms.add(methodDescription.asDefined());
            }
            return new Explicit<MethodDescription.InDefinedShape>(declaredForms);
        }
    }
}

