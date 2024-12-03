/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.dynamic.scaffold;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.Transformer;
import net.bytebuddy.dynamic.VisibilityBridgeStrategy;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import net.bytebuddy.dynamic.scaffold.TypeInitializer;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.dynamic.scaffold.TypeWriter;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.LoadedTypeInitializer;
import net.bytebuddy.implementation.attribute.MethodAttributeAppender;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.matcher.LatentMatcher;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface MethodRegistry {
    public MethodRegistry prepend(LatentMatcher<? super MethodDescription> var1, Handler var2, MethodAttributeAppender.Factory var3, Transformer<MethodDescription> var4);

    public MethodRegistry append(LatentMatcher<? super MethodDescription> var1, Handler var2, MethodAttributeAppender.Factory var3, Transformer<MethodDescription> var4);

    public Prepared prepare(InstrumentedType var1, MethodGraph.Compiler var2, TypeValidation var3, VisibilityBridgeStrategy var4, LatentMatcher<? super MethodDescription> var5);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Default
    implements MethodRegistry {
        private final List<Entry> entries;

        public Default() {
            this.entries = Collections.emptyList();
        }

        private Default(List<Entry> entries) {
            this.entries = entries;
        }

        @Override
        public MethodRegistry prepend(LatentMatcher<? super MethodDescription> matcher, Handler handler, MethodAttributeAppender.Factory attributeAppenderFactory, Transformer<MethodDescription> transformer) {
            return new Default(CompoundList.of(new Entry(matcher, handler, attributeAppenderFactory, transformer), this.entries));
        }

        @Override
        public MethodRegistry append(LatentMatcher<? super MethodDescription> matcher, Handler handler, MethodAttributeAppender.Factory attributeAppenderFactory, Transformer<MethodDescription> transformer) {
            return new Default(CompoundList.of(this.entries, new Entry(matcher, handler, attributeAppenderFactory, transformer)));
        }

        @Override
        public net.bytebuddy.dynamic.scaffold.MethodRegistry$Prepared prepare(InstrumentedType instrumentedType, MethodGraph.Compiler methodGraphCompiler, TypeValidation typeValidation, VisibilityBridgeStrategy visibilityBridgeStrategy, LatentMatcher<? super MethodDescription> ignoredMethods) {
            LinkedHashMap<MethodDescription, Prepared.Entry> implementations = new LinkedHashMap<MethodDescription, Prepared.Entry>();
            HashSet<Handler> handlers = new HashSet<Handler>();
            HashSet<MethodDescription.InDefinedShape> declaredMethods = new HashSet<MethodDescription.InDefinedShape>(instrumentedType.getDeclaredMethods());
            for (Entry entry : this.entries) {
                InstrumentedType typeDescription;
                if (!handlers.add(entry.getHandler()) || instrumentedType == (typeDescription = entry.getHandler().prepare(instrumentedType))) continue;
                for (MethodDescription methodDescription : typeDescription.getDeclaredMethods()) {
                    if (declaredMethods.contains(methodDescription)) continue;
                    implementations.put(methodDescription, entry.asSupplementaryEntry(methodDescription));
                    declaredMethods.add((MethodDescription.InDefinedShape)methodDescription);
                }
                instrumentedType = typeDescription;
            }
            MethodGraph.Linked methodGraph = methodGraphCompiler.compile((TypeDefinition)instrumentedType);
            ElementMatcher.Junction<? super MethodDescription> relevanceMatcher = ElementMatchers.failSafe(ElementMatchers.not(ElementMatchers.anyOf(implementations.keySet())).and(ElementMatchers.returns(ElementMatchers.isVisibleTo(instrumentedType))).and(ElementMatchers.hasParameters(ElementMatchers.whereNone(ElementMatchers.hasType(ElementMatchers.not(ElementMatchers.isVisibleTo(instrumentedType))))))).and(ignoredMethods.resolve(instrumentedType));
            ArrayList<MethodDescription> methods = new ArrayList<MethodDescription>();
            for (MethodGraph.Node node : methodGraph.listNodes()) {
                boolean visibilityBridge;
                MethodDescription methodDescription = node.getRepresentative();
                boolean bl = visibilityBridge = instrumentedType.isPublic() && !instrumentedType.isInterface();
                if (relevanceMatcher.matches(methodDescription)) {
                    for (Entry entry : this.entries) {
                        if (!entry.resolve(instrumentedType).matches(methodDescription)) continue;
                        implementations.put(methodDescription, entry.asPreparedEntry(instrumentedType, methodDescription, node.getMethodTypes(), node.getVisibility()));
                        visibilityBridge = false;
                        break;
                    }
                }
                if (visibilityBridge && !node.getSort().isMadeVisible() && methodDescription.isPublic() && !methodDescription.isAbstract() && !methodDescription.isFinal() && methodDescription.getDeclaringType().isPackagePrivate() && visibilityBridgeStrategy.generateVisibilityBridge(methodDescription)) {
                    implementations.put(methodDescription, Prepared.Entry.forVisibilityBridge(methodDescription, node.getVisibility()));
                }
                methods.add(methodDescription);
            }
            for (MethodDescription methodDescription : CompoundList.of(instrumentedType.getDeclaredMethods().filter(ElementMatchers.not(ElementMatchers.isVirtual()).and(relevanceMatcher)), new MethodDescription.Latent.TypeInitializer(instrumentedType))) {
                for (Entry entry : this.entries) {
                    if (!entry.resolve(instrumentedType).matches(methodDescription)) continue;
                    implementations.put(methodDescription, entry.asPreparedEntry(instrumentedType, methodDescription, methodDescription.getVisibility()));
                    break;
                }
                methods.add(methodDescription);
            }
            return new Prepared(implementations, instrumentedType.getLoadedTypeInitializer(), instrumentedType.getTypeInitializer(), typeValidation.isEnabled() ? instrumentedType.validated() : instrumentedType, methodGraph, new MethodList.Explicit(methods));
        }

        public boolean equals(@MaybeNull Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }
            return ((Object)this.entries).equals(((Default)object).entries);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ((Object)this.entries).hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class Compiled
        implements net.bytebuddy.dynamic.scaffold.MethodRegistry$Compiled {
            private final TypeDescription instrumentedType;
            private final LoadedTypeInitializer loadedTypeInitializer;
            private final TypeInitializer typeInitializer;
            private final MethodList<?> methods;
            private final LinkedHashMap<MethodDescription, Entry> implementations;
            private final boolean supportsBridges;

            protected Compiled(TypeDescription instrumentedType, LoadedTypeInitializer loadedTypeInitializer, TypeInitializer typeInitializer, MethodList<?> methods, LinkedHashMap<MethodDescription, Entry> implementations, boolean supportsBridges) {
                this.instrumentedType = instrumentedType;
                this.loadedTypeInitializer = loadedTypeInitializer;
                this.typeInitializer = typeInitializer;
                this.methods = methods;
                this.implementations = implementations;
                this.supportsBridges = supportsBridges;
            }

            @Override
            public TypeDescription getInstrumentedType() {
                return this.instrumentedType;
            }

            @Override
            public LoadedTypeInitializer getLoadedTypeInitializer() {
                return this.loadedTypeInitializer;
            }

            @Override
            public TypeInitializer getTypeInitializer() {
                return this.typeInitializer;
            }

            @Override
            public MethodList<?> getMethods() {
                return this.methods;
            }

            @Override
            public MethodList<?> getInstrumentedMethods() {
                return (MethodList)new MethodList.Explicit<MethodDescription>((List<MethodDescription>)new ArrayList<MethodDescription>(this.implementations.keySet())).filter(ElementMatchers.not(ElementMatchers.isTypeInitializer()));
            }

            @Override
            public TypeWriter.MethodPool.Record target(MethodDescription methodDescription) {
                Entry entry = this.implementations.get(methodDescription);
                return entry == null ? new TypeWriter.MethodPool.Record.ForNonImplementedMethod(methodDescription) : entry.bind(this.instrumentedType, this.supportsBridges);
            }

            public boolean equals(@MaybeNull Object object) {
                if (this == object) {
                    return true;
                }
                if (object == null) {
                    return false;
                }
                if (this.getClass() != object.getClass()) {
                    return false;
                }
                if (this.supportsBridges != ((Compiled)object).supportsBridges) {
                    return false;
                }
                if (!this.instrumentedType.equals(((Compiled)object).instrumentedType)) {
                    return false;
                }
                if (!this.loadedTypeInitializer.equals(((Compiled)object).loadedTypeInitializer)) {
                    return false;
                }
                if (!this.typeInitializer.equals(((Compiled)object).typeInitializer)) {
                    return false;
                }
                if (!this.methods.equals(((Compiled)object).methods)) {
                    return false;
                }
                return this.implementations.equals(((Compiled)object).implementations);
            }

            public int hashCode() {
                return (((((this.getClass().hashCode() * 31 + this.instrumentedType.hashCode()) * 31 + this.loadedTypeInitializer.hashCode()) * 31 + this.typeInitializer.hashCode()) * 31 + this.methods.hashCode()) * 31 + this.implementations.hashCode()) * 31 + this.supportsBridges;
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class Entry {
                private final Handler.Compiled handler;
                private final MethodAttributeAppender attributeAppender;
                private final MethodDescription methodDescription;
                private final Set<MethodDescription.TypeToken> bridgeTypes;
                private final Visibility visibility;
                private final boolean bridgeMethod;

                protected Entry(Handler.Compiled handler, MethodAttributeAppender attributeAppender, MethodDescription methodDescription, Set<MethodDescription.TypeToken> bridgeTypes, Visibility visibility, boolean bridgeMethod) {
                    this.handler = handler;
                    this.attributeAppender = attributeAppender;
                    this.methodDescription = methodDescription;
                    this.bridgeTypes = bridgeTypes;
                    this.visibility = visibility;
                    this.bridgeMethod = bridgeMethod;
                }

                protected TypeWriter.MethodPool.Record bind(TypeDescription instrumentedType, boolean supportsBridges) {
                    if (this.bridgeMethod && !supportsBridges) {
                        return new TypeWriter.MethodPool.Record.ForNonImplementedMethod(this.methodDescription);
                    }
                    TypeWriter.MethodPool.Record record = this.handler.assemble(this.methodDescription, this.attributeAppender, this.visibility);
                    return supportsBridges ? TypeWriter.MethodPool.Record.AccessBridgeWrapper.of(record, instrumentedType, this.methodDescription, this.bridgeTypes, this.attributeAppender) : record;
                }

                public boolean equals(@MaybeNull Object object) {
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    if (this.bridgeMethod != ((Entry)object).bridgeMethod) {
                        return false;
                    }
                    if (!this.visibility.equals(((Entry)object).visibility)) {
                        return false;
                    }
                    if (!this.handler.equals(((Entry)object).handler)) {
                        return false;
                    }
                    if (!this.attributeAppender.equals(((Entry)object).attributeAppender)) {
                        return false;
                    }
                    if (!this.methodDescription.equals(((Entry)object).methodDescription)) {
                        return false;
                    }
                    return ((Object)this.bridgeTypes).equals(((Entry)object).bridgeTypes);
                }

                public int hashCode() {
                    return (((((this.getClass().hashCode() * 31 + this.handler.hashCode()) * 31 + this.attributeAppender.hashCode()) * 31 + this.methodDescription.hashCode()) * 31 + ((Object)this.bridgeTypes).hashCode()) * 31 + this.visibility.hashCode()) * 31 + this.bridgeMethod;
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class Prepared
        implements net.bytebuddy.dynamic.scaffold.MethodRegistry$Prepared {
            private final LinkedHashMap<MethodDescription, Entry> implementations;
            private final LoadedTypeInitializer loadedTypeInitializer;
            private final TypeInitializer typeInitializer;
            private final TypeDescription instrumentedType;
            private final MethodGraph.Linked methodGraph;
            private final MethodList<?> methods;

            protected Prepared(LinkedHashMap<MethodDescription, Entry> implementations, LoadedTypeInitializer loadedTypeInitializer, TypeInitializer typeInitializer, TypeDescription instrumentedType, MethodGraph.Linked methodGraph, MethodList<?> methods) {
                this.implementations = implementations;
                this.loadedTypeInitializer = loadedTypeInitializer;
                this.typeInitializer = typeInitializer;
                this.instrumentedType = instrumentedType;
                this.methodGraph = methodGraph;
                this.methods = methods;
            }

            @Override
            public TypeDescription getInstrumentedType() {
                return this.instrumentedType;
            }

            @Override
            public LoadedTypeInitializer getLoadedTypeInitializer() {
                return this.loadedTypeInitializer;
            }

            @Override
            public TypeInitializer getTypeInitializer() {
                return this.typeInitializer;
            }

            @Override
            public MethodList<?> getMethods() {
                return this.methods;
            }

            @Override
            public MethodList<?> getInstrumentedMethods() {
                return (MethodList)new MethodList.Explicit<MethodDescription>((List<MethodDescription>)new ArrayList<MethodDescription>(this.implementations.keySet())).filter(ElementMatchers.not(ElementMatchers.isTypeInitializer()));
            }

            @Override
            public net.bytebuddy.dynamic.scaffold.MethodRegistry$Compiled compile(Implementation.Target.Factory implementationTargetFactory, ClassFileVersion classFileVersion) {
                HashMap<Handler, Handler.Compiled> compilationCache = new HashMap<Handler, Handler.Compiled>();
                HashMap<MethodAttributeAppender.Factory, MethodAttributeAppender> attributeAppenderCache = new HashMap<MethodAttributeAppender.Factory, MethodAttributeAppender>();
                LinkedHashMap<MethodDescription, Compiled.Entry> entries = new LinkedHashMap<MethodDescription, Compiled.Entry>();
                Implementation.Target implementationTarget = implementationTargetFactory.make(this.instrumentedType, this.methodGraph, classFileVersion);
                for (Map.Entry<MethodDescription, Entry> entry : this.implementations.entrySet()) {
                    MethodAttributeAppender cachedAttributeAppender;
                    Handler.Compiled cachedHandler = (Handler.Compiled)compilationCache.get(entry.getValue().getHandler());
                    if (cachedHandler == null) {
                        cachedHandler = entry.getValue().getHandler().compile(implementationTarget);
                        compilationCache.put(entry.getValue().getHandler(), cachedHandler);
                    }
                    if ((cachedAttributeAppender = (MethodAttributeAppender)attributeAppenderCache.get(entry.getValue().getAppenderFactory())) == null) {
                        cachedAttributeAppender = entry.getValue().getAppenderFactory().make(this.instrumentedType);
                        attributeAppenderCache.put(entry.getValue().getAppenderFactory(), cachedAttributeAppender);
                    }
                    entries.put(entry.getKey(), new Compiled.Entry(cachedHandler, cachedAttributeAppender, entry.getValue().getMethodDescription(), entry.getValue().resolveBridgeTypes(), entry.getValue().getVisibility(), entry.getValue().isBridgeMethod()));
                }
                return new Compiled(this.instrumentedType, this.loadedTypeInitializer, this.typeInitializer, this.methods, entries, classFileVersion.isAtLeast(ClassFileVersion.JAVA_V5));
            }

            public boolean equals(@MaybeNull Object object) {
                if (this == object) {
                    return true;
                }
                if (object == null) {
                    return false;
                }
                if (this.getClass() != object.getClass()) {
                    return false;
                }
                if (!this.implementations.equals(((Prepared)object).implementations)) {
                    return false;
                }
                if (!this.loadedTypeInitializer.equals(((Prepared)object).loadedTypeInitializer)) {
                    return false;
                }
                if (!this.typeInitializer.equals(((Prepared)object).typeInitializer)) {
                    return false;
                }
                if (!this.instrumentedType.equals(((Prepared)object).instrumentedType)) {
                    return false;
                }
                if (!this.methodGraph.equals(((Prepared)object).methodGraph)) {
                    return false;
                }
                return this.methods.equals(((Prepared)object).methods);
            }

            public int hashCode() {
                return (((((this.getClass().hashCode() * 31 + this.implementations.hashCode()) * 31 + this.loadedTypeInitializer.hashCode()) * 31 + this.typeInitializer.hashCode()) * 31 + this.instrumentedType.hashCode()) * 31 + this.methodGraph.hashCode()) * 31 + this.methods.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class Entry {
                private final Handler handler;
                private final MethodAttributeAppender.Factory attributeAppenderFactory;
                private final MethodDescription methodDescription;
                private final Set<MethodDescription.TypeToken> typeTokens;
                private final Visibility visibility;
                private final boolean bridgeMethod;

                protected Entry(Handler handler, MethodAttributeAppender.Factory attributeAppenderFactory, MethodDescription methodDescription, Set<MethodDescription.TypeToken> typeTokens, Visibility visibility, boolean bridgeMethod) {
                    this.handler = handler;
                    this.attributeAppenderFactory = attributeAppenderFactory;
                    this.methodDescription = methodDescription;
                    this.typeTokens = typeTokens;
                    this.visibility = visibility;
                    this.bridgeMethod = bridgeMethod;
                }

                protected static Entry forVisibilityBridge(MethodDescription bridgeTarget, Visibility visibility) {
                    return new Entry(Handler.ForVisibilityBridge.INSTANCE, MethodAttributeAppender.Explicit.of(bridgeTarget), bridgeTarget, Collections.<MethodDescription.TypeToken>emptySet(), visibility, true);
                }

                protected Handler getHandler() {
                    return this.handler;
                }

                protected MethodAttributeAppender.Factory getAppenderFactory() {
                    return this.attributeAppenderFactory;
                }

                protected MethodDescription getMethodDescription() {
                    return this.methodDescription;
                }

                protected Set<MethodDescription.TypeToken> resolveBridgeTypes() {
                    HashSet<MethodDescription.TypeToken> typeTokens = new HashSet<MethodDescription.TypeToken>(this.typeTokens);
                    typeTokens.remove(this.methodDescription.asTypeToken());
                    return typeTokens;
                }

                protected Visibility getVisibility() {
                    return this.visibility;
                }

                protected boolean isBridgeMethod() {
                    return this.bridgeMethod;
                }

                public boolean equals(@MaybeNull Object object) {
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    if (this.bridgeMethod != ((Entry)object).bridgeMethod) {
                        return false;
                    }
                    if (!this.visibility.equals(((Entry)object).visibility)) {
                        return false;
                    }
                    if (!this.handler.equals(((Entry)object).handler)) {
                        return false;
                    }
                    if (!this.attributeAppenderFactory.equals(((Entry)object).attributeAppenderFactory)) {
                        return false;
                    }
                    if (!this.methodDescription.equals(((Entry)object).methodDescription)) {
                        return false;
                    }
                    return ((Object)this.typeTokens).equals(((Entry)object).typeTokens);
                }

                public int hashCode() {
                    return (((((this.getClass().hashCode() * 31 + this.handler.hashCode()) * 31 + this.attributeAppenderFactory.hashCode()) * 31 + this.methodDescription.hashCode()) * 31 + ((Object)this.typeTokens).hashCode()) * 31 + this.visibility.hashCode()) * 31 + this.bridgeMethod;
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class Entry
        implements LatentMatcher<MethodDescription> {
            private final LatentMatcher<? super MethodDescription> matcher;
            private final Handler handler;
            private final MethodAttributeAppender.Factory attributeAppenderFactory;
            private final Transformer<MethodDescription> transformer;

            protected Entry(LatentMatcher<? super MethodDescription> matcher, Handler handler, MethodAttributeAppender.Factory attributeAppenderFactory, Transformer<MethodDescription> transformer) {
                this.matcher = matcher;
                this.handler = handler;
                this.attributeAppenderFactory = attributeAppenderFactory;
                this.transformer = transformer;
            }

            protected Prepared.Entry asPreparedEntry(TypeDescription instrumentedType, MethodDescription methodDescription, Visibility visibility) {
                return this.asPreparedEntry(instrumentedType, methodDescription, Collections.<MethodDescription.TypeToken>emptySet(), visibility);
            }

            protected Prepared.Entry asPreparedEntry(TypeDescription instrumentedType, MethodDescription methodDescription, Set<MethodDescription.TypeToken> methodTypes, Visibility visibility) {
                return new Prepared.Entry(this.handler, this.attributeAppenderFactory, this.transformer.transform(instrumentedType, methodDescription), methodTypes, visibility, false);
            }

            protected Prepared.Entry asSupplementaryEntry(MethodDescription methodDescription) {
                return new Prepared.Entry(this.handler, MethodAttributeAppender.Explicit.of(methodDescription), methodDescription, Collections.<MethodDescription.TypeToken>emptySet(), methodDescription.getVisibility(), false);
            }

            protected Handler getHandler() {
                return this.handler;
            }

            @Override
            public ElementMatcher<? super MethodDescription> resolve(TypeDescription typeDescription) {
                return this.matcher.resolve(typeDescription);
            }

            public boolean equals(@MaybeNull Object object) {
                if (this == object) {
                    return true;
                }
                if (object == null) {
                    return false;
                }
                if (this.getClass() != object.getClass()) {
                    return false;
                }
                if (!this.matcher.equals(((Entry)object).matcher)) {
                    return false;
                }
                if (!this.handler.equals(((Entry)object).handler)) {
                    return false;
                }
                if (!this.attributeAppenderFactory.equals(((Entry)object).attributeAppenderFactory)) {
                    return false;
                }
                return this.transformer.equals(((Entry)object).transformer);
            }

            public int hashCode() {
                return (((this.getClass().hashCode() * 31 + this.matcher.hashCode()) * 31 + this.handler.hashCode()) * 31 + this.attributeAppenderFactory.hashCode()) * 31 + this.transformer.hashCode();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface Compiled
    extends TypeWriter.MethodPool {
        public TypeDescription getInstrumentedType();

        public MethodList<?> getMethods();

        public MethodList<?> getInstrumentedMethods();

        public LoadedTypeInitializer getLoadedTypeInitializer();

        public TypeInitializer getTypeInitializer();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface Prepared {
        public TypeDescription getInstrumentedType();

        public MethodList<?> getMethods();

        public MethodList<?> getInstrumentedMethods();

        public LoadedTypeInitializer getLoadedTypeInitializer();

        public TypeInitializer getTypeInitializer();

        public Compiled compile(Implementation.Target.Factory var1, ClassFileVersion var2);
    }

    public static interface Handler
    extends InstrumentedType.Prepareable {
        public Compiled compile(Implementation.Target var1);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForAnnotationValue
        implements Handler,
        Compiled {
            private final AnnotationValue<?, ?> annotationValue;

            public ForAnnotationValue(AnnotationValue<?, ?> annotationValue) {
                this.annotationValue = annotationValue;
            }

            @Override
            public InstrumentedType prepare(InstrumentedType instrumentedType) {
                return instrumentedType;
            }

            @Override
            public Compiled compile(Implementation.Target implementationTarget) {
                return this;
            }

            @Override
            public TypeWriter.MethodPool.Record assemble(MethodDescription methodDescription, MethodAttributeAppender attributeAppender, Visibility visibility) {
                return new TypeWriter.MethodPool.Record.ForDefinedMethod.WithAnnotationDefaultValue(methodDescription, this.annotationValue, attributeAppender);
            }

            public boolean equals(@MaybeNull Object object) {
                if (this == object) {
                    return true;
                }
                if (object == null) {
                    return false;
                }
                if (this.getClass() != object.getClass()) {
                    return false;
                }
                return this.annotationValue.equals(((ForAnnotationValue)object).annotationValue);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.annotationValue.hashCode();
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForImplementation
        implements Handler {
            private final Implementation implementation;

            public ForImplementation(Implementation implementation) {
                this.implementation = implementation;
            }

            public InstrumentedType prepare(InstrumentedType instrumentedType) {
                return this.implementation.prepare(instrumentedType);
            }

            public Compiled compile(Implementation.Target implementationTarget) {
                return new Compiled(this.implementation.appender(implementationTarget));
            }

            public boolean equals(@MaybeNull Object object) {
                if (this == object) {
                    return true;
                }
                if (object == null) {
                    return false;
                }
                if (this.getClass() != object.getClass()) {
                    return false;
                }
                return this.implementation.equals(((ForImplementation)object).implementation);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.implementation.hashCode();
            }

            @HashCodeAndEqualsPlugin.Enhance
            protected static class Compiled
            implements net.bytebuddy.dynamic.scaffold.MethodRegistry$Handler$Compiled {
                private final ByteCodeAppender byteCodeAppender;

                protected Compiled(ByteCodeAppender byteCodeAppender) {
                    this.byteCodeAppender = byteCodeAppender;
                }

                public TypeWriter.MethodPool.Record assemble(MethodDescription methodDescription, MethodAttributeAppender attributeAppender, Visibility visibility) {
                    return new TypeWriter.MethodPool.Record.ForDefinedMethod.WithBody(methodDescription, this.byteCodeAppender, attributeAppender, visibility);
                }

                public boolean equals(@MaybeNull Object object) {
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    return this.byteCodeAppender.equals(((Compiled)object).byteCodeAppender);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.byteCodeAppender.hashCode();
                }
            }
        }

        public static interface Compiled {
            public TypeWriter.MethodPool.Record assemble(MethodDescription var1, MethodAttributeAppender var2, Visibility var3);
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum ForVisibilityBridge implements Handler
        {
            INSTANCE;


            @Override
            public InstrumentedType prepare(InstrumentedType instrumentedType) {
                throw new IllegalStateException("A visibility bridge handler must not apply any preparations");
            }

            @Override
            public Compiled compile(Implementation.Target implementationTarget) {
                return new Compiled(implementationTarget.getInstrumentedType());
            }

            @HashCodeAndEqualsPlugin.Enhance
            protected static class Compiled
            implements net.bytebuddy.dynamic.scaffold.MethodRegistry$Handler$Compiled {
                private final TypeDescription instrumentedType;

                protected Compiled(TypeDescription instrumentedType) {
                    this.instrumentedType = instrumentedType;
                }

                public TypeWriter.MethodPool.Record assemble(MethodDescription methodDescription, MethodAttributeAppender attributeAppender, Visibility visibility) {
                    return TypeWriter.MethodPool.Record.ForDefinedMethod.OfVisibilityBridge.of(this.instrumentedType, methodDescription, attributeAppender);
                }

                public boolean equals(@MaybeNull Object object) {
                    if (this == object) {
                        return true;
                    }
                    if (object == null) {
                        return false;
                    }
                    if (this.getClass() != object.getClass()) {
                        return false;
                    }
                    return this.instrumentedType.equals(((Compiled)object).instrumentedType);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.instrumentedType.hashCode();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum ForAbstractMethod implements Handler,
        Compiled
        {
            INSTANCE;


            @Override
            public InstrumentedType prepare(InstrumentedType instrumentedType) {
                return instrumentedType;
            }

            @Override
            public Compiled compile(Implementation.Target implementationTarget) {
                return this;
            }

            @Override
            public TypeWriter.MethodPool.Record assemble(MethodDescription methodDescription, MethodAttributeAppender attributeAppender, Visibility visibility) {
                return new TypeWriter.MethodPool.Record.ForDefinedMethod.WithoutBody(methodDescription, attributeAppender, visibility);
            }
        }
    }
}

