/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.dynamic.scaffold;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.matcher.FilterableList;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface MethodGraph {
    public Node locate(MethodDescription.SignatureToken var1);

    public NodeList listNodes();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Simple
    implements MethodGraph {
        private final LinkedHashMap<MethodDescription.SignatureToken, Node> nodes;

        public Simple(LinkedHashMap<MethodDescription.SignatureToken, Node> nodes) {
            this.nodes = nodes;
        }

        public static MethodGraph of(List<? extends MethodDescription> methodDescriptions) {
            LinkedHashMap<MethodDescription.SignatureToken, Node> nodes = new LinkedHashMap<MethodDescription.SignatureToken, Node>();
            for (MethodDescription methodDescription : methodDescriptions) {
                nodes.put(methodDescription.asSignatureToken(), new Node.Simple(methodDescription));
            }
            return new Simple(nodes);
        }

        @Override
        public Node locate(MethodDescription.SignatureToken token) {
            Node node = this.nodes.get(token);
            return node == null ? Node.Unresolved.INSTANCE : node;
        }

        @Override
        public NodeList listNodes() {
            return new NodeList(new ArrayList<Node>(this.nodes.values()));
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
            return this.nodes.equals(((Simple)object).nodes);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.nodes.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class NodeList
    extends FilterableList.AbstractBase<Node, NodeList> {
        private final List<? extends Node> nodes;

        public NodeList(List<? extends Node> nodes) {
            this.nodes = nodes;
        }

        @Override
        public Node get(int index) {
            return this.nodes.get(index);
        }

        @Override
        public int size() {
            return this.nodes.size();
        }

        @Override
        protected NodeList wrap(List<Node> values) {
            return new NodeList(values);
        }

        public MethodList<?> asMethodList() {
            ArrayList<MethodDescription> methodDescriptions = new ArrayList<MethodDescription>(this.size());
            for (Node node : this.nodes) {
                methodDescriptions.add(node.getRepresentative());
            }
            return new MethodList.Explicit(methodDescriptions);
        }
    }

    @SuppressFBWarnings(value={"IC_SUPERCLASS_USES_SUBCLASS_DURING_INITIALIZATION"}, justification="Safe initialization is implied.")
    public static interface Compiler {
        public static final Compiler DEFAULT = Default.forJavaHierarchy();

        public Linked compile(TypeDefinition var1);

        @Deprecated
        public Linked compile(TypeDescription var1);

        public Linked compile(TypeDefinition var1, TypeDescription var2);

        @Deprecated
        public Linked compile(TypeDescription var1, TypeDescription var2);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Default<T>
        extends AbstractBase {
            private final Harmonizer<T> harmonizer;
            private final Merger merger;
            private final TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor;
            private final ElementMatcher<? super MethodDescription> matcher;

            protected Default(Harmonizer<T> harmonizer, Merger merger, TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor) {
                this(harmonizer, merger, visitor, ElementMatchers.any());
            }

            public Default(Harmonizer<T> harmonizer, Merger merger, TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor, ElementMatcher<? super MethodDescription> matcher) {
                this.harmonizer = harmonizer;
                this.merger = merger;
                this.visitor = visitor;
                this.matcher = matcher;
            }

            public static <S> Compiler of(Harmonizer<S> harmonizer, Merger merger) {
                return new Default<S>(harmonizer, merger, TypeDescription.Generic.Visitor.Reifying.INITIATING);
            }

            public static <S> Compiler of(Harmonizer<S> harmonizer, Merger merger, ElementMatcher<? super MethodDescription> matcher) {
                return new Default<S>(harmonizer, merger, TypeDescription.Generic.Visitor.Reifying.INITIATING, matcher);
            }

            public static <S> Compiler of(Harmonizer<S> harmonizer, Merger merger, TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor) {
                return new Default<S>(harmonizer, merger, visitor);
            }

            public static Compiler forJavaHierarchy() {
                return Default.of(Harmonizer.ForJavaMethod.INSTANCE, Merger.Directional.LEFT);
            }

            public static Compiler forJVMHierarchy() {
                return Default.of(Harmonizer.ForJVMMethod.INSTANCE, Merger.Directional.LEFT);
            }

            @Override
            public Linked compile(TypeDefinition typeDefinition, TypeDescription viewPoint) {
                Key.Store store;
                HashMap<TypeDefinition, Key.Store<T>> snapshots = new HashMap<TypeDefinition, Key.Store<T>>();
                Key.Store<T> rootStore = this.doAnalyze(typeDefinition, snapshots, ElementMatchers.isVirtual().and(ElementMatchers.isVisibleTo(viewPoint)).and(this.matcher));
                TypeDescription.Generic superClass = typeDefinition.getSuperClass();
                TypeList.Generic interfaceTypes = typeDefinition.getInterfaces();
                HashMap<TypeDescription, MethodGraph> interfaceGraphs = new HashMap<TypeDescription, MethodGraph>();
                for (TypeDescription.Generic interfaceType : interfaceTypes) {
                    Key.Store store2 = (Key.Store)snapshots.get(interfaceType);
                    if (store2 == null) {
                        throw new IllegalStateException("Failed to resolve interface type " + interfaceType + " from " + snapshots.keySet());
                    }
                    interfaceGraphs.put(interfaceType.asErasure(), store2.asGraph(this.merger));
                }
                if (superClass == null) {
                    store = null;
                } else {
                    store = (Key.Store)snapshots.get(superClass);
                    if (store == null) {
                        throw new IllegalStateException("Failed to resolve super class " + superClass + " from " + snapshots.keySet());
                    }
                }
                return new Linked.Delegation(rootStore.asGraph(this.merger), store == null ? Empty.INSTANCE : store.asGraph(this.merger), interfaceGraphs);
            }

            protected Key.Store<T> analyze(TypeDefinition typeDefinition, TypeDefinition key, Map<TypeDefinition, Key.Store<T>> snapshots, ElementMatcher<? super MethodDescription> relevanceMatcher) {
                Key.Store<T> store = snapshots.get(key);
                if (store == null) {
                    store = this.doAnalyze(typeDefinition, snapshots, relevanceMatcher);
                    snapshots.put(key, store);
                }
                return store;
            }

            protected Key.Store<T> analyzeNullable(@MaybeNull TypeDescription.Generic typeDescription, Map<TypeDefinition, Key.Store<T>> snapshots, ElementMatcher<? super MethodDescription> relevanceMatcher) {
                return typeDescription == null ? new Key.Store() : this.analyze(typeDescription.accept(this.visitor), typeDescription, snapshots, relevanceMatcher);
            }

            protected Key.Store<T> doAnalyze(TypeDefinition typeDefinition, Map<TypeDefinition, Key.Store<T>> snapshots, ElementMatcher<? super MethodDescription> relevanceMatcher) {
                Key.Store<T> store = this.analyzeNullable(typeDefinition.getSuperClass(), snapshots, relevanceMatcher);
                Key.Store<T> interfaceStore = new Key.Store<T>();
                for (TypeDescription.Generic interfaceType : typeDefinition.getInterfaces()) {
                    interfaceStore = interfaceStore.combineWith(this.analyze(interfaceType.accept(this.visitor), interfaceType, snapshots, relevanceMatcher));
                }
                return store.inject(interfaceStore).registerTopLevel((List<MethodDescription>)typeDefinition.getDeclaredMethods().filter(relevanceMatcher), this.harmonizer);
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
                if (!this.harmonizer.equals(((Default)object).harmonizer)) {
                    return false;
                }
                if (!this.merger.equals(((Default)object).merger)) {
                    return false;
                }
                if (!this.visitor.equals(((Default)object).visitor)) {
                    return false;
                }
                return this.matcher.equals(((Default)object).matcher);
            }

            public int hashCode() {
                return (((this.getClass().hashCode() * 31 + this.harmonizer.hashCode()) * 31 + this.merger.hashCode()) * 31 + this.visitor.hashCode()) * 31 + this.matcher.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static abstract class Key<S> {
                protected final String internalName;
                protected final int parameterCount;

                protected Key(String internalName, int parameterCount) {
                    this.internalName = internalName;
                    this.parameterCount = parameterCount;
                }

                protected abstract Set<S> getIdentifiers();

                public int hashCode() {
                    return this.internalName.hashCode() + 31 * this.parameterCount;
                }

                public boolean equals(@MaybeNull Object other) {
                    if (this == other) {
                        return true;
                    }
                    if (!(other instanceof Key)) {
                        return false;
                    }
                    Key key = (Key)other;
                    return this.internalName.equals(key.internalName) && this.parameterCount == key.parameterCount && !Collections.disjoint(this.getIdentifiers(), key.getIdentifiers());
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                @HashCodeAndEqualsPlugin.Enhance
                protected static class Store<V> {
                    private final LinkedHashMap<Harmonized<V>, Entry<V>> entries;

                    protected Store() {
                        this(new LinkedHashMap<Harmonized<V>, Entry<V>>());
                    }

                    private Store(LinkedHashMap<Harmonized<V>, Entry<V>> entries) {
                        this.entries = entries;
                    }

                    private static <W> Entry<W> combine(Entry<W> left, Entry<W> right) {
                        Set<MethodDescription> leftMethods = left.getCandidates();
                        Set<MethodDescription> rightMethods = right.getCandidates();
                        LinkedHashSet<MethodDescription> combined = new LinkedHashSet<MethodDescription>();
                        combined.addAll(leftMethods);
                        combined.addAll(rightMethods);
                        block0: for (MethodDescription leftMethod : leftMethods) {
                            MethodDescription rightMethod;
                            TypeDescription rightType;
                            TypeDescription leftType = leftMethod.getDeclaringType().asErasure();
                            Iterator<MethodDescription> iterator = rightMethods.iterator();
                            while (iterator.hasNext() && !leftType.equals(rightType = (rightMethod = iterator.next()).getDeclaringType().asErasure())) {
                                if (leftType.isAssignableTo(rightType)) {
                                    combined.remove(rightMethod);
                                    continue block0;
                                }
                                if (!leftType.isAssignableFrom(rightType)) continue;
                                combined.remove(leftMethod);
                                continue block0;
                            }
                        }
                        Harmonized<W> key = left.getKey().combineWith(right.getKey());
                        Visibility visibility = left.getVisibility().expandTo(right.getVisibility());
                        return combined.size() == 1 ? new Entry.Resolved<W>(key, (MethodDescription)combined.iterator().next(), visibility, false) : new Entry.Ambiguous<W>(key, combined, visibility);
                    }

                    protected Store<V> registerTopLevel(List<? extends MethodDescription> methodDescriptions, Harmonizer<V> harmonizer) {
                        if (methodDescriptions.isEmpty()) {
                            return this;
                        }
                        LinkedHashMap<Harmonized<Entry<V>>, Entry<Entry<V>>> entries = new LinkedHashMap<Harmonized<Entry<V>>, Entry<Entry<V>>>(this.entries);
                        for (MethodDescription methodDescription : methodDescriptions) {
                            Harmonized<V> key = Harmonized.of(methodDescription, harmonizer);
                            Entry<Object> currentEntry = (Entry<Object>)entries.remove(key);
                            Entry<V> extendedEntry = (currentEntry == null ? new Entry.Initial<V>(key) : currentEntry).extendBy(methodDescription, harmonizer);
                            entries.put(extendedEntry.getKey(), extendedEntry);
                        }
                        return new Store<V>(entries);
                    }

                    protected Store<V> combineWith(Store<V> store) {
                        if (this.entries.isEmpty()) {
                            return store;
                        }
                        if (store.entries.isEmpty()) {
                            return this;
                        }
                        LinkedHashMap<Harmonized<Entry<V>>, Entry<Entry<V>>> entries = new LinkedHashMap<Harmonized<Entry<V>>, Entry<Entry<V>>>(this.entries);
                        for (Entry<V> entry : store.entries.values()) {
                            Entry previousEntry = (Entry)entries.remove(entry.getKey());
                            Entry<V> injectedEntry = previousEntry == null ? entry : Store.combine(previousEntry, entry);
                            entries.put(injectedEntry.getKey(), injectedEntry);
                        }
                        return new Store<V>(entries);
                    }

                    protected Store<V> inject(Store<V> store) {
                        if (this.entries.isEmpty()) {
                            return store;
                        }
                        if (store.entries.isEmpty()) {
                            return this;
                        }
                        LinkedHashMap<Harmonized<Entry<V>>, Entry<Entry<V>>> entries = new LinkedHashMap<Harmonized<Entry<V>>, Entry<Entry<V>>>(this.entries);
                        for (Entry<V> entry : store.entries.values()) {
                            Entry previous = (Entry)entries.remove(entry.getKey());
                            Entry<V> injectedEntry = previous == null ? entry : previous.inject(entry);
                            entries.put(injectedEntry.getKey(), injectedEntry);
                        }
                        return new Store<V>(entries);
                    }

                    protected MethodGraph asGraph(Merger merger) {
                        LinkedHashMap<Key<MethodDescription.TypeToken>, Node> entries = new LinkedHashMap<Key<MethodDescription.TypeToken>, Node>();
                        for (Entry<V> entry : this.entries.values()) {
                            Node node = entry.asNode(merger);
                            entries.put(entry.getKey().detach(node.getRepresentative().asTypeToken()), node);
                        }
                        return new Graph(entries);
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
                        return this.entries.equals(((Store)object).entries);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.entries.hashCode();
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    @HashCodeAndEqualsPlugin.Enhance
                    protected static class Graph
                    implements MethodGraph {
                        private final LinkedHashMap<Key<MethodDescription.TypeToken>, Node> entries;

                        protected Graph(LinkedHashMap<Key<MethodDescription.TypeToken>, Node> entries) {
                            this.entries = entries;
                        }

                        @Override
                        public Node locate(MethodDescription.SignatureToken token) {
                            Node node = this.entries.get(Detached.of(token));
                            return node == null ? Node.Unresolved.INSTANCE : node;
                        }

                        @Override
                        public NodeList listNodes() {
                            return new NodeList(new ArrayList<Node>(this.entries.values()));
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
                            return this.entries.equals(((Graph)object).entries);
                        }

                        public int hashCode() {
                            return this.getClass().hashCode() * 31 + this.entries.hashCode();
                        }
                    }

                    /*
                     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                     */
                    protected static interface Entry<W> {
                        public Harmonized<W> getKey();

                        public Set<MethodDescription> getCandidates();

                        public Visibility getVisibility();

                        public Entry<W> extendBy(MethodDescription var1, Harmonizer<W> var2);

                        public Entry<W> inject(Entry<W> var1);

                        public Node asNode(Merger var1);

                        /*
                         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                         */
                        @HashCodeAndEqualsPlugin.Enhance
                        public static class Ambiguous<U>
                        implements Entry<U> {
                            private final Harmonized<U> key;
                            private final LinkedHashSet<MethodDescription> methodDescriptions;
                            private final Visibility visibility;

                            protected Ambiguous(Harmonized<U> key, LinkedHashSet<MethodDescription> methodDescriptions, Visibility visibility) {
                                this.key = key;
                                this.methodDescriptions = methodDescriptions;
                                this.visibility = visibility;
                            }

                            protected static <Q> Entry<Q> of(Harmonized<Q> key, MethodDescription left, MethodDescription right, Visibility visibility) {
                                visibility = visibility.expandTo(left.getVisibility()).expandTo(right.getVisibility());
                                return left.isBridge() ^ right.isBridge() ? new Resolved<Q>(key, left.isBridge() ? right : left, visibility, false) : new Ambiguous<Q>(key, new LinkedHashSet<MethodDescription>(Arrays.asList(left, right)), visibility);
                            }

                            @Override
                            public Harmonized<U> getKey() {
                                return this.key;
                            }

                            @Override
                            public Set<MethodDescription> getCandidates() {
                                return this.methodDescriptions;
                            }

                            @Override
                            public Visibility getVisibility() {
                                return this.visibility;
                            }

                            @Override
                            public Entry<U> extendBy(MethodDescription methodDescription, Harmonizer<U> harmonizer) {
                                Harmonized<U> key = this.key.extend((MethodDescription.InDefinedShape)methodDescription.asDefined(), harmonizer);
                                LinkedHashSet<MethodDescription> methodDescriptions = new LinkedHashSet<MethodDescription>();
                                TypeDescription declaringType = methodDescription.getDeclaringType().asErasure();
                                boolean bridge = methodDescription.isBridge();
                                Visibility visibility = this.visibility;
                                for (MethodDescription extendedMethod : this.methodDescriptions) {
                                    if (extendedMethod.getDeclaringType().asErasure().equals(declaringType)) {
                                        if (extendedMethod.isBridge() ^ bridge) {
                                            methodDescriptions.add(bridge ? extendedMethod : methodDescription);
                                        } else {
                                            methodDescriptions.add(methodDescription);
                                            methodDescriptions.add(extendedMethod);
                                        }
                                    }
                                    visibility = visibility.expandTo(extendedMethod.getVisibility());
                                }
                                if (methodDescriptions.isEmpty()) {
                                    return new Resolved<U>(key, methodDescription, visibility, bridge);
                                }
                                if (methodDescriptions.size() == 1) {
                                    return new Resolved<U>(key, (MethodDescription)methodDescriptions.iterator().next(), visibility, false);
                                }
                                return new Ambiguous<U>(key, methodDescriptions, visibility);
                            }

                            @Override
                            public Entry<U> inject(Entry<U> entry) {
                                TypeDescription target;
                                LinkedHashSet<MethodDescription> methodDescriptions = new LinkedHashSet<MethodDescription>();
                                block0: for (MethodDescription methodDescription : this.methodDescriptions) {
                                    target = methodDescription.getDeclaringType().asErasure();
                                    for (MethodDescription candidate : entry.getCandidates()) {
                                        TypeDescription typeDescription = candidate.getDeclaringType().asErasure();
                                        if (typeDescription.equals(target) || !typeDescription.isAssignableTo(target)) continue;
                                        continue block0;
                                    }
                                    methodDescriptions.add(methodDescription);
                                }
                                block2: for (MethodDescription candidate : entry.getCandidates()) {
                                    target = candidate.getDeclaringType().asErasure();
                                    for (MethodDescription methodDescription : this.methodDescriptions) {
                                        if (!methodDescription.getDeclaringType().asErasure().isAssignableTo(target)) continue;
                                        continue block2;
                                    }
                                    methodDescriptions.add(candidate);
                                }
                                return methodDescriptions.size() == 1 ? new Resolved<U>(this.key.combineWith(entry.getKey()), (MethodDescription)methodDescriptions.iterator().next(), this.visibility.expandTo(entry.getVisibility())) : new Ambiguous<U>(this.key.combineWith(entry.getKey()), methodDescriptions, this.visibility.expandTo(entry.getVisibility()));
                            }

                            @Override
                            public net.bytebuddy.dynamic.scaffold.MethodGraph$Node asNode(Merger merger) {
                                Iterator iterator = this.methodDescriptions.iterator();
                                MethodDescription methodDescription = (MethodDescription)iterator.next();
                                while (iterator.hasNext()) {
                                    methodDescription = merger.merge(methodDescription, (MethodDescription)iterator.next());
                                }
                                return new Node(this.key.detach(methodDescription.asTypeToken()), methodDescription, this.visibility);
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
                                if (!this.visibility.equals(((Ambiguous)object).visibility)) {
                                    return false;
                                }
                                if (!this.key.equals(((Ambiguous)object).key)) {
                                    return false;
                                }
                                return this.methodDescriptions.equals(((Ambiguous)object).methodDescriptions);
                            }

                            public int hashCode() {
                                return ((this.getClass().hashCode() * 31 + this.key.hashCode()) * 31 + this.methodDescriptions.hashCode()) * 31 + this.visibility.hashCode();
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            @HashCodeAndEqualsPlugin.Enhance
                            protected static class Node
                            implements net.bytebuddy.dynamic.scaffold.MethodGraph$Node {
                                private final Detached key;
                                private final MethodDescription methodDescription;
                                private final Visibility visibility;

                                protected Node(Detached key, MethodDescription methodDescription, Visibility visibility) {
                                    this.key = key;
                                    this.methodDescription = methodDescription;
                                    this.visibility = visibility;
                                }

                                @Override
                                public Node.Sort getSort() {
                                    return Node.Sort.AMBIGUOUS;
                                }

                                @Override
                                public MethodDescription getRepresentative() {
                                    return this.methodDescription;
                                }

                                @Override
                                public Set<MethodDescription.TypeToken> getMethodTypes() {
                                    return this.key.getIdentifiers();
                                }

                                @Override
                                public Visibility getVisibility() {
                                    return this.visibility;
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
                                    if (!this.visibility.equals(((Node)object).visibility)) {
                                        return false;
                                    }
                                    if (!this.key.equals(((Node)object).key)) {
                                        return false;
                                    }
                                    return this.methodDescription.equals(((Node)object).methodDescription);
                                }

                                public int hashCode() {
                                    return ((this.getClass().hashCode() * 31 + this.key.hashCode()) * 31 + this.methodDescription.hashCode()) * 31 + this.visibility.hashCode();
                                }
                            }
                        }

                        /*
                         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                         */
                        @HashCodeAndEqualsPlugin.Enhance
                        public static class Resolved<U>
                        implements Entry<U> {
                            private static final int MADE_VISIBLE = 5;
                            private static final boolean NOT_MADE_VISIBLE = false;
                            private final Harmonized<U> key;
                            private final MethodDescription methodDescription;
                            private final Visibility visibility;
                            private final boolean madeVisible;

                            protected Resolved(Harmonized<U> key, MethodDescription methodDescription, Visibility visibility) {
                                this(key, methodDescription, visibility, false);
                            }

                            protected Resolved(Harmonized<U> key, MethodDescription methodDescription, Visibility visibility, boolean madeVisible) {
                                this.key = key;
                                this.methodDescription = methodDescription;
                                this.visibility = visibility;
                                this.madeVisible = madeVisible;
                            }

                            private static <V> Entry<V> of(Harmonized<V> key, MethodDescription override, MethodDescription original, Visibility visibility) {
                                visibility = visibility.expandTo(original.getVisibility()).expandTo(override.getVisibility());
                                return override.isBridge() ? new Resolved<V>(key, original, visibility, (original.getDeclaringType().getModifiers() & 5) == 0) : new Resolved<V>(key, override, visibility, false);
                            }

                            @Override
                            public Harmonized<U> getKey() {
                                return this.key;
                            }

                            @Override
                            public Set<MethodDescription> getCandidates() {
                                return Collections.singleton(this.methodDescription);
                            }

                            @Override
                            public Visibility getVisibility() {
                                return this.visibility;
                            }

                            @Override
                            public Entry<U> extendBy(MethodDescription methodDescription, Harmonizer<U> harmonizer) {
                                Harmonized<U> key = this.key.extend((MethodDescription.InDefinedShape)methodDescription.asDefined(), harmonizer);
                                Visibility visibility = this.visibility.expandTo(methodDescription.getVisibility());
                                return methodDescription.getDeclaringType().equals(this.methodDescription.getDeclaringType()) ? Ambiguous.of(key, methodDescription, this.methodDescription, visibility) : Resolved.of(key, methodDescription, this.methodDescription, visibility);
                            }

                            @Override
                            public Entry<U> inject(Entry<U> entry) {
                                if (this.methodDescription.getDeclaringType().isInterface()) {
                                    LinkedHashSet<MethodDescription> candidates = new LinkedHashSet<MethodDescription>();
                                    candidates.add(this.methodDescription);
                                    TypeDescription target = this.methodDescription.getDeclaringType().asErasure();
                                    for (MethodDescription methodDescription : entry.getCandidates()) {
                                        if (methodDescription.getDeclaringType().asErasure().isAssignableTo(target)) {
                                            candidates.remove(this.methodDescription);
                                            candidates.add(methodDescription);
                                            continue;
                                        }
                                        if (methodDescription.getDeclaringType().asErasure().isAssignableFrom(target)) continue;
                                        candidates.add(methodDescription);
                                    }
                                    return candidates.size() == 1 ? new Resolved<U>(this.key.combineWith(entry.getKey()), (MethodDescription)candidates.iterator().next(), this.visibility.expandTo(entry.getVisibility()), this.madeVisible) : new Ambiguous<U>(this.key.combineWith(entry.getKey()), candidates, this.visibility.expandTo(entry.getVisibility()));
                                }
                                return new Resolved<U>(this.key.combineWith(entry.getKey()), this.methodDescription, this.visibility.expandTo(entry.getVisibility()), this.madeVisible);
                            }

                            @Override
                            public net.bytebuddy.dynamic.scaffold.MethodGraph$Node asNode(Merger merger) {
                                return new Node(this.key.detach(this.methodDescription.asTypeToken()), this.methodDescription, this.visibility, this.madeVisible);
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
                                if (this.madeVisible != ((Resolved)object).madeVisible) {
                                    return false;
                                }
                                if (!this.visibility.equals(((Resolved)object).visibility)) {
                                    return false;
                                }
                                if (!this.key.equals(((Resolved)object).key)) {
                                    return false;
                                }
                                return this.methodDescription.equals(((Resolved)object).methodDescription);
                            }

                            public int hashCode() {
                                return (((this.getClass().hashCode() * 31 + this.key.hashCode()) * 31 + this.methodDescription.hashCode()) * 31 + this.visibility.hashCode()) * 31 + this.madeVisible;
                            }

                            /*
                             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                             */
                            @HashCodeAndEqualsPlugin.Enhance
                            protected static class Node
                            implements net.bytebuddy.dynamic.scaffold.MethodGraph$Node {
                                private final Detached key;
                                private final MethodDescription methodDescription;
                                private final Visibility visibility;
                                private final boolean visible;

                                protected Node(Detached key, MethodDescription methodDescription, Visibility visibility, boolean visible) {
                                    this.key = key;
                                    this.methodDescription = methodDescription;
                                    this.visibility = visibility;
                                    this.visible = visible;
                                }

                                @Override
                                public Node.Sort getSort() {
                                    return this.visible ? Node.Sort.VISIBLE : Node.Sort.RESOLVED;
                                }

                                @Override
                                public MethodDescription getRepresentative() {
                                    return this.methodDescription;
                                }

                                @Override
                                public Set<MethodDescription.TypeToken> getMethodTypes() {
                                    return this.key.getIdentifiers();
                                }

                                @Override
                                public Visibility getVisibility() {
                                    return this.visibility;
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
                                    if (this.visible != ((Node)object).visible) {
                                        return false;
                                    }
                                    if (!this.visibility.equals(((Node)object).visibility)) {
                                        return false;
                                    }
                                    if (!this.key.equals(((Node)object).key)) {
                                        return false;
                                    }
                                    return this.methodDescription.equals(((Node)object).methodDescription);
                                }

                                public int hashCode() {
                                    return (((this.getClass().hashCode() * 31 + this.key.hashCode()) * 31 + this.methodDescription.hashCode()) * 31 + this.visibility.hashCode()) * 31 + this.visible;
                                }
                            }
                        }

                        /*
                         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                         */
                        public static class Initial<U>
                        implements Entry<U> {
                            private final Harmonized<U> key;

                            protected Initial(Harmonized<U> key) {
                                this.key = key;
                            }

                            @Override
                            public Harmonized<U> getKey() {
                                throw new IllegalStateException("Cannot extract key from initial entry:" + this);
                            }

                            @Override
                            public Set<MethodDescription> getCandidates() {
                                throw new IllegalStateException("Cannot extract method from initial entry:" + this);
                            }

                            @Override
                            public Visibility getVisibility() {
                                throw new IllegalStateException("Cannot extract visibility from initial entry:" + this);
                            }

                            @Override
                            public Entry<U> extendBy(MethodDescription methodDescription, Harmonizer<U> harmonizer) {
                                return new Resolved<U>(this.key.extend((MethodDescription.InDefinedShape)methodDescription.asDefined(), harmonizer), methodDescription, methodDescription.getVisibility(), false);
                            }

                            @Override
                            public Entry<U> inject(Entry<U> entry) {
                                throw new IllegalStateException("Cannot inject into initial entry without a registered method: " + this);
                            }

                            @Override
                            public Node asNode(Merger merger) {
                                throw new IllegalStateException("Cannot transform initial entry without a registered method: " + this);
                            }

                            public int hashCode() {
                                return this.key.hashCode();
                            }

                            public boolean equals(@MaybeNull Object other) {
                                if (this == other) {
                                    return true;
                                }
                                if (other == null || this.getClass() != other.getClass()) {
                                    return false;
                                }
                                Initial initial = (Initial)other;
                                return this.key.equals(initial.key);
                            }
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static class Detached
                extends Key<MethodDescription.TypeToken> {
                    private final Set<MethodDescription.TypeToken> identifiers;

                    protected Detached(String internalName, int parameterCount, Set<MethodDescription.TypeToken> identifiers) {
                        super(internalName, parameterCount);
                        this.identifiers = identifiers;
                    }

                    protected static Detached of(MethodDescription.SignatureToken token) {
                        return new Detached(token.getName(), token.getParameterTypes().size(), Collections.singleton(token.asTypeToken()));
                    }

                    @Override
                    protected Set<MethodDescription.TypeToken> getIdentifiers() {
                        return this.identifiers;
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static class Harmonized<V>
                extends Key<V> {
                    private final Map<V, Set<MethodDescription.TypeToken>> identifiers;

                    protected Harmonized(String internalName, int parameterCount, Map<V, Set<MethodDescription.TypeToken>> identifiers) {
                        super(internalName, parameterCount);
                        this.identifiers = identifiers;
                    }

                    protected static <Q> Harmonized<Q> of(MethodDescription methodDescription, Harmonizer<Q> harmonizer) {
                        MethodDescription.TypeToken typeToken = methodDescription.asTypeToken();
                        return new Harmonized<Q>(methodDescription.getInternalName(), methodDescription.getParameters().size(), Collections.singletonMap(harmonizer.harmonize(typeToken), Collections.emptySet()));
                    }

                    protected Detached detach(MethodDescription.TypeToken typeToken) {
                        HashSet<MethodDescription.TypeToken> identifiers = new HashSet<MethodDescription.TypeToken>();
                        for (Set<MethodDescription.TypeToken> typeTokens : this.identifiers.values()) {
                            identifiers.addAll(typeTokens);
                        }
                        identifiers.add(typeToken);
                        return new Detached(this.internalName, this.parameterCount, identifiers);
                    }

                    protected Harmonized<V> combineWith(Harmonized<V> key) {
                        HashMap<Set<MethodDescription.TypeToken>, Set<MethodDescription.TypeToken>> identifiers = new HashMap<Set<MethodDescription.TypeToken>, Set<MethodDescription.TypeToken>>(this.identifiers);
                        for (Map.Entry<V, Set<MethodDescription.TypeToken>> entry : key.identifiers.entrySet()) {
                            HashSet typeTokens = (HashSet)identifiers.get(entry.getKey());
                            if (typeTokens == null) {
                                identifiers.put((Set<MethodDescription.TypeToken>)entry.getKey(), entry.getValue());
                                continue;
                            }
                            typeTokens = new HashSet(typeTokens);
                            typeTokens.addAll(entry.getValue());
                            identifiers.put((Set<MethodDescription.TypeToken>)entry.getKey(), typeTokens);
                        }
                        return new Harmonized<V>(this.internalName, this.parameterCount, identifiers);
                    }

                    protected Harmonized<V> extend(MethodDescription.InDefinedShape methodDescription, Harmonizer<V> harmonizer) {
                        HashMap<Set<MethodDescription.TypeToken>, Set<MethodDescription.TypeToken>> identifiers = new HashMap<Set<MethodDescription.TypeToken>, Set<MethodDescription.TypeToken>>(this.identifiers);
                        MethodDescription.TypeToken typeToken = methodDescription.asTypeToken();
                        V identifier = harmonizer.harmonize(typeToken);
                        HashSet<MethodDescription.TypeToken> typeTokens = (HashSet<MethodDescription.TypeToken>)identifiers.get(identifier);
                        if (typeTokens == null) {
                            identifiers.put((Set<MethodDescription.TypeToken>)identifier, Collections.singleton(typeToken));
                        } else {
                            typeTokens = new HashSet<MethodDescription.TypeToken>(typeTokens);
                            typeTokens.add(typeToken);
                            identifiers.put((Set<MethodDescription.TypeToken>)identifier, (Set<MethodDescription.TypeToken>)typeTokens);
                        }
                        return new Harmonized<V>(this.internalName, this.parameterCount, identifiers);
                    }

                    @Override
                    protected Set<V> getIdentifiers() {
                        return this.identifiers.keySet();
                    }
                }
            }

            public static interface Merger {
                public MethodDescription merge(MethodDescription var1, MethodDescription var2);

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum Directional implements Merger
                {
                    LEFT(true),
                    RIGHT(false);

                    private final boolean left;

                    private Directional(boolean left) {
                        this.left = left;
                    }

                    @Override
                    public MethodDescription merge(MethodDescription left, MethodDescription right) {
                        return this.left ? left : right;
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static interface Harmonizer<S> {
                public S harmonize(MethodDescription.TypeToken var1);

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum ForJVMMethod implements Harmonizer<Token>
                {
                    INSTANCE;


                    @Override
                    public Token harmonize(MethodDescription.TypeToken typeToken) {
                        return new Token(typeToken);
                    }

                    protected static class Token {
                        private final MethodDescription.TypeToken typeToken;
                        private final int hashCode;

                        public Token(MethodDescription.TypeToken typeToken) {
                            this.typeToken = typeToken;
                            this.hashCode = typeToken.getReturnType().hashCode() + 31 * typeToken.getParameterTypes().hashCode();
                        }

                        public int hashCode() {
                            return this.hashCode;
                        }

                        public boolean equals(@MaybeNull Object other) {
                            if (this == other) {
                                return true;
                            }
                            if (!(other instanceof Token)) {
                                return false;
                            }
                            Token token = (Token)other;
                            return this.typeToken.getReturnType().equals(token.typeToken.getReturnType()) && this.typeToken.getParameterTypes().equals(token.typeToken.getParameterTypes());
                        }

                        public String toString() {
                            return this.typeToken.toString();
                        }
                    }
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                public static enum ForJavaMethod implements Harmonizer<Token>
                {
                    INSTANCE;


                    @Override
                    public Token harmonize(MethodDescription.TypeToken typeToken) {
                        return new Token(typeToken);
                    }

                    protected static class Token {
                        private final MethodDescription.TypeToken typeToken;
                        private final int hashCode;

                        protected Token(MethodDescription.TypeToken typeToken) {
                            this.typeToken = typeToken;
                            this.hashCode = typeToken.getParameterTypes().hashCode();
                        }

                        public int hashCode() {
                            return this.hashCode;
                        }

                        public boolean equals(@MaybeNull Object other) {
                            return this == other || other instanceof Token && this.typeToken.getParameterTypes().equals(((Token)other).typeToken.getParameterTypes());
                        }

                        public String toString() {
                            return this.typeToken.getParameterTypes().toString();
                        }
                    }
                }
            }
        }

        public static abstract class AbstractBase
        implements Compiler {
            public Linked compile(TypeDefinition typeDefinition) {
                return this.compile(typeDefinition, typeDefinition.asErasure());
            }

            @Deprecated
            public Linked compile(TypeDescription typeDescription) {
                return this.compile((TypeDefinition)typeDescription, typeDescription);
            }

            @Deprecated
            public Linked compile(TypeDescription typeDefinition, TypeDescription viewPoint) {
                return this.compile((TypeDefinition)typeDefinition, viewPoint);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum ForDeclaredMethods implements Compiler
        {
            INSTANCE;


            @Override
            public Linked compile(TypeDefinition typeDefinition) {
                return this.compile(typeDefinition, typeDefinition.asErasure());
            }

            @Override
            @Deprecated
            public Linked compile(TypeDescription typeDescription) {
                return this.compile((TypeDefinition)typeDescription, typeDescription);
            }

            @Override
            public Linked compile(TypeDefinition typeDefinition, TypeDescription viewPoint) {
                LinkedHashMap<MethodDescription.SignatureToken, Node> nodes = new LinkedHashMap<MethodDescription.SignatureToken, Node>();
                for (MethodDescription methodDescription : (MethodList)typeDefinition.getDeclaredMethods().filter(ElementMatchers.isVirtual().and(ElementMatchers.not(ElementMatchers.isBridge())).and(ElementMatchers.isVisibleTo(viewPoint)))) {
                    nodes.put(methodDescription.asSignatureToken(), new Node.Simple(methodDescription));
                }
                return new Linked.Delegation(new Simple(nodes), Empty.INSTANCE, Collections.<TypeDescription, MethodGraph>emptyMap());
            }

            @Override
            @Deprecated
            public Linked compile(TypeDescription typeDefinition, TypeDescription viewPoint) {
                return this.compile((TypeDefinition)typeDefinition, viewPoint);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface Node {
        public Sort getSort();

        public MethodDescription getRepresentative();

        public Set<MethodDescription.TypeToken> getMethodTypes();

        public Visibility getVisibility();

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Simple
        implements Node {
            private final MethodDescription methodDescription;

            public Simple(MethodDescription methodDescription) {
                this.methodDescription = methodDescription;
            }

            @Override
            public Sort getSort() {
                return Sort.RESOLVED;
            }

            @Override
            public MethodDescription getRepresentative() {
                return this.methodDescription;
            }

            @Override
            public Set<MethodDescription.TypeToken> getMethodTypes() {
                return Collections.emptySet();
            }

            @Override
            public Visibility getVisibility() {
                return this.methodDescription.getVisibility();
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
                return this.methodDescription.equals(((Simple)object).methodDescription);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.methodDescription.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Unresolved implements Node
        {
            INSTANCE;


            @Override
            public Sort getSort() {
                return Sort.UNRESOLVED;
            }

            @Override
            public MethodDescription getRepresentative() {
                throw new IllegalStateException("Cannot resolve the method of an illegal node");
            }

            @Override
            public Set<MethodDescription.TypeToken> getMethodTypes() {
                throw new IllegalStateException("Cannot resolve bridge method of an illegal node");
            }

            @Override
            public Visibility getVisibility() {
                throw new IllegalStateException("Cannot resolve visibility of an illegal node");
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Sort {
            VISIBLE(true, true, true),
            RESOLVED(true, true, false),
            AMBIGUOUS(true, false, false),
            UNRESOLVED(false, false, false);

            private final boolean resolved;
            private final boolean unique;
            private final boolean madeVisible;

            private Sort(boolean resolved, boolean unique, boolean madeVisible) {
                this.resolved = resolved;
                this.unique = unique;
                this.madeVisible = madeVisible;
            }

            public boolean isResolved() {
                return this.resolved;
            }

            public boolean isUnique() {
                return this.unique;
            }

            public boolean isMadeVisible() {
                return this.madeVisible;
            }
        }
    }

    public static interface Linked
    extends MethodGraph {
        public MethodGraph getSuperClassGraph();

        public MethodGraph getInterfaceGraph(TypeDescription var1);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Delegation
        implements Linked {
            private final MethodGraph methodGraph;
            private final MethodGraph superClassGraph;
            private final Map<TypeDescription, MethodGraph> interfaceGraphs;

            public Delegation(MethodGraph methodGraph, MethodGraph superClassGraph, Map<TypeDescription, MethodGraph> interfaceGraphs) {
                this.methodGraph = methodGraph;
                this.superClassGraph = superClassGraph;
                this.interfaceGraphs = interfaceGraphs;
            }

            @Override
            public MethodGraph getSuperClassGraph() {
                return this.superClassGraph;
            }

            @Override
            public MethodGraph getInterfaceGraph(TypeDescription typeDescription) {
                MethodGraph interfaceGraph = this.interfaceGraphs.get(typeDescription);
                return interfaceGraph == null ? Empty.INSTANCE : interfaceGraph;
            }

            @Override
            public Node locate(MethodDescription.SignatureToken token) {
                return this.methodGraph.locate(token);
            }

            @Override
            public NodeList listNodes() {
                return this.methodGraph.listNodes();
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
                if (!this.methodGraph.equals(((Delegation)object).methodGraph)) {
                    return false;
                }
                if (!this.superClassGraph.equals(((Delegation)object).superClassGraph)) {
                    return false;
                }
                return ((Object)this.interfaceGraphs).equals(((Delegation)object).interfaceGraphs);
            }

            public int hashCode() {
                return ((this.getClass().hashCode() * 31 + this.methodGraph.hashCode()) * 31 + this.superClassGraph.hashCode()) * 31 + ((Object)this.interfaceGraphs).hashCode();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Empty implements Linked,
    Compiler
    {
        INSTANCE;


        @Override
        public Node locate(MethodDescription.SignatureToken token) {
            return Node.Unresolved.INSTANCE;
        }

        @Override
        public NodeList listNodes() {
            return new NodeList(Collections.emptyList());
        }

        @Override
        public MethodGraph getSuperClassGraph() {
            return this;
        }

        @Override
        public MethodGraph getInterfaceGraph(TypeDescription typeDescription) {
            return this;
        }

        @Override
        public Linked compile(TypeDefinition typeDefinition) {
            return this;
        }

        @Override
        @Deprecated
        public Linked compile(TypeDescription typeDescription) {
            return this;
        }

        @Override
        public Linked compile(TypeDefinition typeDefinition, TypeDescription viewPoint) {
            return this;
        }

        @Override
        @Deprecated
        public Linked compile(TypeDescription typeDefinition, TypeDescription viewPoint) {
            return this;
        }
    }
}

