/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.RuleSet;
import org.apache.commons.digester.annotations.AnnotationRuleProvider;
import org.apache.commons.digester.annotations.DigesterLoader;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class FromAnnotationsRuleSet
implements RuleSet {
    private final Map<String, List<AnnotationRuleProvider<Annotation, AnnotatedElement, Rule>>> rules = new LinkedHashMap<String, List<AnnotationRuleProvider<Annotation, AnnotatedElement, Rule>>>();
    private final Set<Class<?>> mappedClasses = new HashSet();
    private final DigesterLoader digesterLoader;
    private volatile String namespaceURI;

    protected FromAnnotationsRuleSet(DigesterLoader digesterLoader) {
        this.digesterLoader = digesterLoader;
    }

    @Override
    public void addRuleInstances(Digester digester) {
        for (Map.Entry<String, List<AnnotationRuleProvider<Annotation, AnnotatedElement, Rule>>> entry : this.rules.entrySet()) {
            String pattern = entry.getKey();
            for (AnnotationRuleProvider<Annotation, AnnotatedElement, Rule> provider : entry.getValue()) {
                Rule rule = provider.get();
                if (this.namespaceURI != null) {
                    rule.setNamespaceURI(this.namespaceURI);
                }
                digester.addRule(pattern, rule);
            }
        }
    }

    public void addRules(Class<?> target) {
        this.digesterLoader.addRulesTo(target, this);
    }

    public <A extends Annotation, E extends AnnotatedElement, R extends Rule, T extends AnnotationRuleProvider<A, E, R>> void addRuleProvider(String pattern, Class<T> klass, A annotation, E element) {
        T annotationRuleProvider = this.digesterLoader.getAnnotationRuleProviderFactory().newInstance(klass);
        annotationRuleProvider.init(annotation, element);
        this.addRuleProvider(pattern, (AnnotationRuleProvider<? extends Annotation, ? extends AnnotatedElement, ? extends Rule>)annotationRuleProvider);
    }

    public void addRuleProvider(String pattern, AnnotationRuleProvider<? extends Annotation, ? extends AnnotatedElement, ? extends Rule> ruleProvider) {
        List<Object> rules;
        if (this.rules.containsKey(pattern)) {
            rules = this.rules.get(pattern);
        } else {
            rules = new ArrayList();
            this.rules.put(pattern, rules);
        }
        rules.add(ruleProvider);
    }

    public <T extends AnnotationRuleProvider<? extends Annotation, ? extends AnnotatedElement, ? extends Rule>> T getProvider(String pattern, Class<T> providerClass) {
        if (!this.rules.containsKey(pattern)) {
            return null;
        }
        for (AnnotationRuleProvider<Annotation, AnnotatedElement, Rule> rule : this.rules.get(pattern)) {
            if (!providerClass.isInstance(rule)) continue;
            return (T)((AnnotationRuleProvider)providerClass.cast(rule));
        }
        return null;
    }

    public void addRulesProviderFrom(FromAnnotationsRuleSet ruleSet) {
        this.rules.putAll(ruleSet.getRules());
    }

    protected boolean mapsClass(Class<?> clazz) {
        return this.mappedClasses.contains(clazz);
    }

    protected void addMappedClass(Class<?> clazz) {
        this.mappedClasses.add(clazz);
    }

    private Map<String, List<AnnotationRuleProvider<Annotation, AnnotatedElement, Rule>>> getRules() {
        return this.rules;
    }

    @Override
    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    public String toString() {
        return "{ mappedClasses=" + this.mappedClasses + ", rules=" + this.rules.toString() + ", namespaceURI=" + this.namespaceURI + " }";
    }
}

