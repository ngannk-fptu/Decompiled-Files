/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.MatchCondition;
import cz.vutbr.web.css.Selector;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Element;

public class MatchConditionOnElements
implements MatchCondition {
    private Map<Element, Set<Selector.PseudoClassType>> elements;
    private Map<String, Set<Selector.PseudoClassType>> names;

    public MatchConditionOnElements() {
        this.elements = null;
        this.names = null;
    }

    public MatchConditionOnElements(Element e, Selector.PseudoClassType pseudoClass) {
        this.addMatch(e, pseudoClass);
    }

    public MatchConditionOnElements(String name, Selector.PseudoClassType pseudoClass) {
        this.addMatch(name, pseudoClass);
    }

    public void addMatch(Element e, Selector.PseudoClassType pseudoClass) {
        Set<Selector.PseudoClassType> classes;
        if (this.elements == null) {
            this.elements = new HashMap<Element, Set<Selector.PseudoClassType>>();
        }
        if ((classes = this.elements.get(e)) == null) {
            classes = new HashSet<Selector.PseudoClassType>(2);
            this.elements.put(e, classes);
        }
        classes.add(pseudoClass);
    }

    public void removeMatch(Element e, Selector.PseudoClassType pseudoClass) {
        Set<Selector.PseudoClassType> classes;
        if (this.elements != null && (classes = this.elements.get(e)) != null) {
            classes.remove((Object)pseudoClass);
        }
    }

    public void addMatch(String name, Selector.PseudoClassType pseudoClass) {
        Set<Selector.PseudoClassType> classes;
        if (this.names == null) {
            this.names = new HashMap<String, Set<Selector.PseudoClassType>>();
        }
        if ((classes = this.names.get(name)) == null) {
            classes = new HashSet<Selector.PseudoClassType>(2);
            this.names.put(name, classes);
        }
        classes.add(pseudoClass);
    }

    public void removeMatch(String name, Selector.PseudoClassType pseudoClass) {
        Set<Selector.PseudoClassType> classes;
        if (this.names != null && (classes = this.names.get(name)) != null) {
            classes.remove((Object)pseudoClass);
        }
    }

    @Override
    public boolean isSatisfied(Element e, Selector.SelectorPart selpart) {
        if (selpart instanceof Selector.PseudoClass) {
            Set<Selector.PseudoClassType> pseudos;
            Selector.PseudoClassType required = ((Selector.PseudoClass)selpart).getType();
            if (this.elements != null && (pseudos = this.elements.get(e)) != null) {
                return pseudos.contains((Object)required);
            }
            if (this.names != null && (pseudos = this.names.get(e.getTagName().toLowerCase())) != null) {
                return pseudos.contains((Object)required);
            }
            return false;
        }
        return false;
    }

    public Object clone() {
        HashSet clonedDeclarations;
        MatchConditionOnElements clone = new MatchConditionOnElements();
        if (this.elements != null) {
            clone.elements = new HashMap<Element, Set<Selector.PseudoClassType>>();
            for (Element e : this.elements.keySet()) {
                clonedDeclarations = new HashSet(this.elements.get(e));
                clone.elements.put(e, clonedDeclarations);
            }
        }
        if (this.names != null) {
            clone.names = new HashMap<String, Set<Selector.PseudoClassType>>();
            for (String n : this.names.keySet()) {
                clonedDeclarations = new HashSet(this.names.get(n));
                clone.names.put(n, clonedDeclarations);
            }
        }
        return clone;
    }
}

