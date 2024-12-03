/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.util;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.PrivateBinder;
import com.google.inject.Scope;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.internal.util.$Iterables;
import com.google.inject.internal.util.$Lists;
import com.google.inject.internal.util.$Maps;
import com.google.inject.internal.util.$Sets;
import com.google.inject.spi.DefaultBindingScopingVisitor;
import com.google.inject.spi.DefaultElementVisitor;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;
import com.google.inject.spi.PrivateElements;
import com.google.inject.spi.ScopeBinding;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Modules {
    public static final Module EMPTY_MODULE = new Module(){

        public void configure(Binder binder) {
        }
    };

    private Modules() {
    }

    public static OverriddenModuleBuilder override(Module ... modules) {
        return new RealOverriddenModuleBuilder(Arrays.asList(modules));
    }

    public static OverriddenModuleBuilder override(Iterable<? extends Module> modules) {
        return new RealOverriddenModuleBuilder(modules);
    }

    public static Module combine(Module ... modules) {
        return Modules.combine($ImmutableSet.of(modules));
    }

    public static Module combine(Iterable<? extends Module> modules) {
        final $ImmutableSet<? extends Module> modulesSet = $ImmutableSet.copyOf(modules);
        return new Module(){

            public void configure(Binder binder) {
                binder = binder.skipSources(this.getClass());
                for (Module module : modulesSet) {
                    binder.install(module);
                }
            }
        };
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class ModuleWriter
    extends DefaultElementVisitor<Void> {
        protected final Binder binder;

        ModuleWriter(Binder binder) {
            this.binder = binder;
        }

        @Override
        protected Void visitOther(Element element) {
            element.applyTo(this.binder);
            return null;
        }

        void writeAll(Iterable<? extends Element> elements) {
            for (Element element : elements) {
                element.acceptVisitor(this);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class RealOverriddenModuleBuilder
    implements OverriddenModuleBuilder {
        private final $ImmutableSet<Module> baseModules;

        private RealOverriddenModuleBuilder(Iterable<? extends Module> baseModules) {
            this.baseModules = $ImmutableSet.copyOf(baseModules);
        }

        @Override
        public Module with(Module ... overrides) {
            return this.with(Arrays.asList(overrides));
        }

        @Override
        public Module with(final Iterable<? extends Module> overrides) {
            return new AbstractModule(){

                @Override
                public void configure() {
                    Element element;
                    Binder baseBinder = this.binder();
                    List<Element> baseElements = Elements.getElements(RealOverriddenModuleBuilder.this.baseModules);
                    if (baseElements.size() == 1 && (element = $Iterables.getOnlyElement(baseElements)) instanceof PrivateElements) {
                        PrivateElements privateElements = (PrivateElements)element;
                        PrivateBinder privateBinder = baseBinder.newPrivateBinder().withSource(privateElements.getSource());
                        for (Key<?> exposed : privateElements.getExposedKeys()) {
                            privateBinder.withSource(privateElements.getExposedSource(exposed)).expose(exposed);
                        }
                        baseBinder = privateBinder;
                        baseElements = privateElements.getElements();
                    }
                    Binder binder = baseBinder;
                    LinkedHashSet<Element> elements = new LinkedHashSet<Element>(baseElements);
                    List<Element> overrideElements = Elements.getElements(overrides);
                    final HashSet overriddenKeys = $Sets.newHashSet();
                    final HashSet overridesScopeAnnotations = $Sets.newHashSet();
                    new ModuleWriter(binder){

                        @Override
                        public <T> Void visit(Binding<T> binding) {
                            overriddenKeys.add(binding.getKey());
                            return (Void)super.visit(binding);
                        }

                        @Override
                        public Void visit(ScopeBinding scopeBinding) {
                            overridesScopeAnnotations.add(scopeBinding.getAnnotationType());
                            return (Void)super.visit(scopeBinding);
                        }

                        @Override
                        public Void visit(PrivateElements privateElements) {
                            overriddenKeys.addAll(privateElements.getExposedKeys());
                            return (Void)super.visit(privateElements);
                        }
                    }.writeAll(overrideElements);
                    final HashMap scopeInstancesInUse = $Maps.newHashMap();
                    final ArrayList scopeBindings = $Lists.newArrayList();
                    new ModuleWriter(binder){

                        @Override
                        public <T> Void visit(Binding<T> binding) {
                            if (!overriddenKeys.remove(binding.getKey())) {
                                super.visit(binding);
                                Scope scope = this.getScopeInstanceOrNull(binding);
                                if (scope != null) {
                                    scopeInstancesInUse.put(scope, binding.getSource());
                                }
                            }
                            return null;
                        }

                        void rewrite(Binder binder, PrivateElements privateElements, Set<Key<?>> keysToSkip) {
                            PrivateBinder privateBinder = binder.withSource(privateElements.getSource()).newPrivateBinder();
                            HashSet<Key<?>> skippedExposes = $Sets.newHashSet();
                            for (Key<?> key : privateElements.getExposedKeys()) {
                                if (keysToSkip.remove(key)) {
                                    skippedExposes.add(key);
                                    continue;
                                }
                                privateBinder.withSource(privateElements.getExposedSource(key)).expose(key);
                            }
                            for (Element element : privateElements.getElements()) {
                                if (element instanceof Binding && skippedExposes.remove(((Binding)element).getKey())) continue;
                                if (element instanceof PrivateElements) {
                                    this.rewrite(privateBinder, (PrivateElements)element, skippedExposes);
                                    continue;
                                }
                                element.applyTo(privateBinder);
                            }
                        }

                        @Override
                        public Void visit(PrivateElements privateElements) {
                            this.rewrite(this.binder, privateElements, overriddenKeys);
                            return null;
                        }

                        @Override
                        public Void visit(ScopeBinding scopeBinding) {
                            scopeBindings.add(scopeBinding);
                            return null;
                        }
                    }.writeAll(elements);
                    new ModuleWriter(binder){

                        public Void visit(ScopeBinding scopeBinding) {
                            if (!overridesScopeAnnotations.remove(scopeBinding.getAnnotationType())) {
                                super.visit(scopeBinding);
                            } else {
                                Object source = scopeInstancesInUse.get(scopeBinding.getScope());
                                if (source != null) {
                                    this.binder.withSource(source).addError("The scope for @%s is bound directly and cannot be overridden.", scopeBinding.getAnnotationType().getSimpleName());
                                }
                            }
                            return null;
                        }
                    }.writeAll(scopeBindings);
                }

                private Scope getScopeInstanceOrNull(Binding<?> binding) {
                    return binding.acceptScopingVisitor(new DefaultBindingScopingVisitor<Scope>(){

                        @Override
                        public Scope visitScope(Scope scope) {
                            return scope;
                        }
                    });
                }
            };
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface OverriddenModuleBuilder {
        public Module with(Module ... var1);

        public Module with(Iterable<? extends Module> var1);
    }
}

