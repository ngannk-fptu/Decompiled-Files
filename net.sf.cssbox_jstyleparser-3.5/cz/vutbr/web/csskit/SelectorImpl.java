/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.unbescape.css.CssEscape
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.CSSFactory;
import cz.vutbr.web.css.CombinedSelector;
import cz.vutbr.web.css.ElementMatcher;
import cz.vutbr.web.css.MatchCondition;
import cz.vutbr.web.css.Rule;
import cz.vutbr.web.css.Selector;
import cz.vutbr.web.csskit.AbstractRule;
import cz.vutbr.web.csskit.OutputUtil;
import java.util.List;
import java.util.Objects;
import org.unbescape.css.CssEscape;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SelectorImpl
extends AbstractRule<Selector.SelectorPart>
implements Selector {
    protected Selector.Combinator combinator;
    protected Selector.PseudoElementType pseudoElementType;

    @Override
    public Rule<Selector.SelectorPart> replaceAll(List<Selector.SelectorPart> replacement) {
        for (Selector.SelectorPart item : replacement) {
            this.checkPseudoElement(item);
        }
        return super.replaceAll(replacement);
    }

    @Override
    public Selector.SelectorPart set(int index, Selector.SelectorPart element) {
        this.checkPseudoElement(element);
        return super.set(index, element);
    }

    @Override
    public void add(int index, Selector.SelectorPart element) {
        this.checkPseudoElement(element);
        super.add(index, element);
    }

    @Override
    public boolean add(Selector.SelectorPart o) {
        this.checkPseudoElement(o);
        return super.add(o);
    }

    @Override
    public Selector.Combinator getCombinator() {
        return this.combinator;
    }

    private void checkPseudoElement(Selector.SelectorPart item) {
        if (item instanceof Selector.PseudoElement) {
            this.pseudoElementType = ((Selector.PseudoElement)item).getType();
        }
    }

    @Override
    public Selector setCombinator(Selector.Combinator combinator) {
        this.combinator = combinator;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.combinator != null) {
            sb.append(this.combinator.value());
        }
        sb = OutputUtil.appendList(sb, this.list, "");
        return sb.toString();
    }

    @Override
    public String getClassName() {
        String className = null;
        for (Selector.SelectorPart item : this.list) {
            if (!(item instanceof Selector.ElementClass)) continue;
            className = ((Selector.ElementClass)item).getClassName();
        }
        return className;
    }

    @Override
    public String getIDName() {
        String idName = null;
        for (Selector.SelectorPart item : this.list) {
            if (!(item instanceof Selector.ElementID)) continue;
            idName = ((Selector.ElementID)item).getID();
        }
        return idName;
    }

    @Override
    public String getElementName() {
        String elementName = null;
        for (Selector.SelectorPart item : this.list) {
            if (!(item instanceof Selector.ElementName)) continue;
            elementName = ((Selector.ElementName)item).getName();
        }
        return elementName;
    }

    @Override
    public Selector.PseudoElementType getPseudoElementType() {
        return this.pseudoElementType;
    }

    @Override
    public boolean hasPseudoClass(Selector.PseudoClassType pct) {
        for (Selector.SelectorPart item : this.list) {
            if (!(item instanceof Selector.PseudoClass) || ((Selector.PseudoClass)item).getType() != pct) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean matches(Element e) {
        for (Selector.SelectorPart item : this.list) {
            if (item != null && item.matches(e, CSSFactory.getElementMatcher(), CSSFactory.getDefaultMatchCondition())) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean matches(Element e, ElementMatcher matcher, MatchCondition cond) {
        for (Selector.SelectorPart item : this.list) {
            if (item != null && item.matches(e, matcher, cond)) continue;
            return false;
        }
        return true;
    }

    @Override
    public void computeSpecificity(CombinedSelector.Specificity spec) {
        for (Selector.SelectorPart item : this.list) {
            item.computeSpecificity(spec);
        }
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.combinator == null ? 0 : this.combinator.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof SelectorImpl)) {
            return false;
        }
        SelectorImpl other = (SelectorImpl)obj;
        return !(this.combinator == null ? other.combinator != null : !this.combinator.equals((Object)other.combinator));
    }

    public static class ElementDOMImpl
    implements Selector.ElementDOM {
        private Element elem;
        private boolean inlinePriority;

        protected ElementDOMImpl(Element e, boolean inlinePriority) {
            this.elem = e;
            this.inlinePriority = inlinePriority;
        }

        @Override
        public Element getElement() {
            return this.elem;
        }

        @Override
        public Selector.ElementDOM setElement(Element e) {
            this.elem = e;
            return this;
        }

        @Override
        public void computeSpecificity(CombinedSelector.Specificity spec) {
            if (this.inlinePriority) {
                spec.add(CombinedSelector.Specificity.Level.A);
            }
        }

        @Override
        public boolean matches(Element e, ElementMatcher matcher, MatchCondition cond) {
            return this.elem.equals(e);
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.elem == null ? 0 : this.elem.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof ElementDOMImpl)) {
                return false;
            }
            ElementDOMImpl other = (ElementDOMImpl)obj;
            return !(this.elem == null ? other.elem != null : !this.elem.equals(other.elem));
        }
    }

    public static class ElementAttributeImpl
    implements Selector.ElementAttribute {
        private Selector.Operator operator;
        private String attribute;
        private String value;
        private boolean isStringValue;

        protected ElementAttributeImpl(String value, boolean isStringValue, Selector.Operator operator, String attribute) {
            this.isStringValue = isStringValue;
            this.operator = operator;
            this.attribute = attribute;
            this.setValue(value);
        }

        @Override
        public Selector.Operator getOperator() {
            return this.operator;
        }

        @Override
        public void setOperator(Selector.Operator operator) {
            this.operator = operator;
        }

        @Override
        public String getAttribute() {
            return this.attribute;
        }

        @Override
        public Selector.ElementAttribute setAttribute(String name) {
            this.attribute = name;
            return this;
        }

        @Override
        public void computeSpecificity(CombinedSelector.Specificity spec) {
            spec.add(CombinedSelector.Specificity.Level.C);
        }

        @Override
        public boolean matches(Element e, ElementMatcher matcher, MatchCondition cond) {
            return matcher.matchesAttribute(e, this.attribute, this.value, this.operator);
        }

        @Override
        public String getValue() {
            return this.value;
        }

        @Override
        public Selector.ElementAttribute setValue(String value) {
            this.value = value;
            return this;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("[").append(this.attribute);
            sb.append(this.operator.value());
            if (this.value != null) {
                if (this.isStringValue) {
                    sb.append("'");
                    sb.append(CssEscape.escapeCssString((String)this.value));
                    sb.append("'");
                } else {
                    sb.append(CssEscape.escapeCssIdentifier((String)this.value));
                }
            }
            sb.append("]");
            return sb.toString();
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.attribute == null ? 0 : this.attribute.hashCode());
            result = 31 * result + (this.isStringValue ? 1231 : 1237);
            result = 31 * result + (this.operator == null ? 0 : this.operator.hashCode());
            result = 31 * result + (this.value == null ? 0 : this.value.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof ElementAttributeImpl)) {
                return false;
            }
            ElementAttributeImpl other = (ElementAttributeImpl)obj;
            if (this.attribute == null ? other.attribute != null : !this.attribute.equals(other.attribute)) {
                return false;
            }
            if (this.isStringValue != other.isStringValue) {
                return false;
            }
            if (this.operator == null ? other.operator != null : !this.operator.equals((Object)other.operator)) {
                return false;
            }
            return !(this.value == null ? other.value != null : !this.value.equals(other.value));
        }
    }

    public static class ElementIDImpl
    implements Selector.ElementID {
        private String id;

        protected ElementIDImpl(String value) {
            this.setID(value);
        }

        @Override
        public void computeSpecificity(CombinedSelector.Specificity spec) {
            spec.add(CombinedSelector.Specificity.Level.B);
        }

        @Override
        public boolean matches(Element e, ElementMatcher matcher, MatchCondition cond) {
            return matcher.matchesID(e, this.id);
        }

        @Override
        public Selector.ElementID setID(String id) {
            if (id == null) {
                throw new IllegalArgumentException("Invalid element ID (null)");
            }
            this.id = id;
            return this;
        }

        @Override
        public String getID() {
            return this.id;
        }

        public String toString() {
            return "#" + CssEscape.escapeCssIdentifier((String)this.id);
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.id == null ? 0 : this.id.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof ElementIDImpl)) {
                return false;
            }
            ElementIDImpl other = (ElementIDImpl)obj;
            return !(this.id == null ? other.id != null : !this.id.equals(other.id));
        }
    }

    public static class PseudoElementImpl
    implements Selector.PseudoElement {
        private final String name;
        private final String functionValue;
        private final Selector.PseudoElementType type;
        private Selector nestedSelector;

        private PseudoElementImpl(String name, String functionValue, Selector nestedSelector) {
            this.name = name;
            this.type = Selector.PseudoElementType.forName(name);
            this.functionValue = functionValue;
            this.nestedSelector = nestedSelector;
            if (this.type != null) {
                switch (this.type) {
                    case CUE: {
                        if (nestedSelector != null || functionValue == null) break;
                        nestedSelector = new SelectorImpl();
                        nestedSelector.add(new ElementNameImpl(functionValue));
                        break;
                    }
                }
            }
        }

        protected PseudoElementImpl(String name) {
            this(name, null, null);
        }

        protected PseudoElementImpl(String name, String functionValue) {
            this(name, functionValue, null);
        }

        protected PseudoElementImpl(String name, Selector nestedSelector) {
            this(name, null, nestedSelector);
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getFunctionValue() {
            return this.functionValue;
        }

        @Override
        public Selector.PseudoElementType getType() {
            return this.type;
        }

        @Override
        public Selector getNestedSelector() {
            return this.nestedSelector;
        }

        @Override
        public void computeSpecificity(CombinedSelector.Specificity spec) {
            spec.add(CombinedSelector.Specificity.Level.D);
        }

        @Override
        public boolean matches(Element e, ElementMatcher matcher, MatchCondition cond) {
            return this.type != null;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(":").append(":");
            if (this.name != null) {
                sb.append(this.name);
            }
            if (this.nestedSelector != null) {
                sb.append("(").append(this.nestedSelector.toString()).append(")");
            } else if (this.functionValue != null) {
                sb.append("(").append(CssEscape.escapeCssIdentifier((String)this.functionValue)).append(")");
            }
            return sb.toString();
        }

        public int hashCode() {
            int hash = 3;
            hash = 53 * hash + Objects.hashCode(this.name);
            hash = 53 * hash + Objects.hashCode(this.functionValue);
            hash = 53 * hash + Objects.hashCode((Object)this.type);
            hash = 53 * hash + Objects.hashCode(this.nestedSelector);
            return hash;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            PseudoElementImpl other = (PseudoElementImpl)obj;
            if (this.type != other.type) {
                return false;
            }
            if (!Objects.equals(this.name, other.name)) {
                return false;
            }
            if (!Objects.equals(this.functionValue, other.functionValue)) {
                return false;
            }
            return Objects.equals(this.nestedSelector, other.nestedSelector);
        }
    }

    public static class PseudoClassImpl
    implements Selector.PseudoClass {
        private final String name;
        private final String functionValue;
        private final Selector.PseudoClassType type;
        private Selector nestedSelector;
        private int[] elementIndex;

        private PseudoClassImpl(String name, String functionValue, Selector nestedSelector) {
            this.name = name;
            this.type = Selector.PseudoClassType.forName(name);
            this.functionValue = functionValue;
            this.nestedSelector = nestedSelector;
            if (this.type != null) {
                switch (this.type) {
                    case NOT: {
                        if (nestedSelector != null || functionValue == null) break;
                        nestedSelector = new SelectorImpl();
                        nestedSelector.unlock();
                        nestedSelector.add(new ElementNameImpl(functionValue));
                        break;
                    }
                    case NTH_CHILD: 
                    case NTH_LAST_CHILD: 
                    case NTH_OF_TYPE: 
                    case NTH_LAST_OF_TYPE: {
                        this.elementIndex = this.decodeIndex(functionValue);
                        break;
                    }
                }
            }
        }

        protected PseudoClassImpl(String name) {
            this(name, null, null);
        }

        protected PseudoClassImpl(String name, String functionValue) {
            this(name, functionValue, null);
        }

        protected PseudoClassImpl(String name, Selector nestedSelector) {
            this(name, null, nestedSelector);
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getFunctionValue() {
            return this.functionValue;
        }

        @Override
        public Selector.PseudoClassType getType() {
            return this.type;
        }

        @Override
        public Selector getNestedSelector() {
            return this.nestedSelector;
        }

        @Override
        public void computeSpecificity(CombinedSelector.Specificity spec) {
            spec.add(CombinedSelector.Specificity.Level.C);
        }

        @Override
        public boolean matches(Element e, ElementMatcher matcher, MatchCondition cond) {
            if (this.type == null) {
                return false;
            }
            switch (this.type) {
                case FIRST_CHILD: 
                case LAST_CHILD: 
                case ONLY_CHILD: {
                    if (e.getParentNode().getNodeType() == 1) {
                        boolean first = false;
                        boolean last = false;
                        if (this.type != Selector.PseudoClassType.LAST_CHILD) {
                            Node prev = e;
                            do {
                                if ((prev = prev.getPreviousSibling()) != null) continue;
                                first = true;
                                break;
                            } while (prev.getNodeType() != 1);
                        }
                        if (this.type != Selector.PseudoClassType.FIRST_CHILD) {
                            Node next = e;
                            do {
                                if ((next = next.getNextSibling()) != null) continue;
                                last = true;
                                break;
                            } while (next.getNodeType() != 1);
                        }
                        switch (this.type) {
                            case FIRST_CHILD: {
                                return first;
                            }
                            case LAST_CHILD: {
                                return last;
                            }
                        }
                        return first && last;
                    }
                    return false;
                }
                case FIRST_OF_TYPE: 
                case LAST_OF_TYPE: 
                case ONLY_OF_TYPE: {
                    if (e.getParentNode().getNodeType() == 1) {
                        boolean firstt = false;
                        boolean lastt = false;
                        if (this.type != Selector.PseudoClassType.LAST_OF_TYPE) {
                            Node prev = e;
                            firstt = true;
                            do {
                                if ((prev = prev.getPreviousSibling()) == null || prev.getNodeType() != 1 || !this.isSameElementType(e, (Element)prev)) continue;
                                firstt = false;
                            } while (prev != null && firstt);
                        }
                        if (this.type != Selector.PseudoClassType.FIRST_OF_TYPE) {
                            Node next = e;
                            lastt = true;
                            do {
                                if ((next = next.getNextSibling()) == null || next.getNodeType() != 1 || !this.isSameElementType(e, (Element)next)) continue;
                                lastt = false;
                            } while (next != null && lastt);
                        }
                        switch (this.type) {
                            case FIRST_OF_TYPE: {
                                return firstt;
                            }
                            case LAST_OF_TYPE: {
                                return lastt;
                            }
                        }
                        return firstt && lastt;
                    }
                    return false;
                }
                case NTH_CHILD: {
                    return this.positionMatches(this.countSiblingsBefore(e, false) + 1, this.elementIndex);
                }
                case NTH_LAST_CHILD: {
                    return this.positionMatches(this.countSiblingsAfter(e, false) + 1, this.elementIndex);
                }
                case NTH_OF_TYPE: {
                    return this.positionMatches(this.countSiblingsBefore(e, true) + 1, this.elementIndex);
                }
                case NTH_LAST_OF_TYPE: {
                    return this.positionMatches(this.countSiblingsAfter(e, true) + 1, this.elementIndex);
                }
                case ROOT: {
                    return e.getParentNode().getNodeType() == 9;
                }
                case EMPTY: {
                    NodeList elist = e.getChildNodes();
                    for (int i = 0; i < elist.getLength(); ++i) {
                        short t = elist.item(i).getNodeType();
                        if (t != 1 && t != 3 && t != 4 && t != 5) continue;
                        return false;
                    }
                    return true;
                }
                case NOT: {
                    return this.nestedSelector != null && !this.nestedSelector.matches(e, matcher, cond);
                }
            }
            return cond.isSatisfied(e, this);
        }

        protected boolean isSameElementType(Element e1, Element e2) {
            return e1.getNodeName().equalsIgnoreCase(e2.getNodeName());
        }

        protected boolean positionMatches(int pos, int[] n) {
            if (n != null) {
                try {
                    int an = pos - n[1];
                    if (n[0] == 0) {
                        return an == 0;
                    }
                    return an * n[0] >= 0 && an % n[0] == 0;
                }
                catch (NumberFormatException ex) {
                    return false;
                }
            }
            return false;
        }

        protected int countSiblingsBefore(Element e, boolean sameType) {
            int cnt = 0;
            Node prev = e;
            do {
                if ((prev = prev.getPreviousSibling()) == null || prev.getNodeType() != 1 || sameType && !this.isSameElementType(e, (Element)prev)) continue;
                ++cnt;
            } while (prev != null);
            return cnt;
        }

        protected int countSiblingsAfter(Element e, boolean sameType) {
            int cnt = 0;
            Node next = e;
            do {
                if ((next = next.getNextSibling()) == null || next.getNodeType() != 1 || sameType && !this.isSameElementType(e, (Element)next)) continue;
                ++cnt;
            } while (next != null);
            return cnt;
        }

        protected int[] decodeIndex(String index) {
            if (index == null) {
                return null;
            }
            String s = index.toLowerCase().trim();
            if (s.equals("odd")) {
                return new int[]{2, 1};
            }
            if (s.equals("even")) {
                return new int[]{2, 0};
            }
            try {
                int[] ret = new int[]{0, 0};
                int n = s.indexOf(110);
                if (n != -1) {
                    String sa = s.substring(0, n).trim();
                    ret[0] = sa.length() == 0 ? 1 : (sa.equals("-") ? -1 : Integer.parseInt(sa));
                    ++n;
                    StringBuilder sb = new StringBuilder();
                    while (n < s.length()) {
                        char ch = s.charAt(n);
                        if (ch != '+' && !Character.isWhitespace(ch)) {
                            sb.append(ch);
                        }
                        ++n;
                    }
                    if (sb.length() > 0) {
                        ret[1] = Integer.parseInt(sb.toString());
                    }
                } else {
                    ret[1] = Integer.parseInt(s);
                }
                return ret;
            }
            catch (NumberFormatException nfe) {
                return null;
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(":");
            if (this.name != null) {
                sb.append(this.name);
            }
            if (this.nestedSelector != null) {
                sb.append("(").append(this.nestedSelector.toString()).append(")");
            } else if (this.functionValue != null) {
                sb.append("(").append(CssEscape.escapeCssIdentifier((String)this.functionValue)).append(")");
            }
            return sb.toString();
        }

        public int hashCode() {
            int hash = 17;
            hash = 43 * hash + Objects.hashCode(this.name);
            hash = 43 * hash + Objects.hashCode(this.functionValue);
            hash = 43 * hash + Objects.hashCode((Object)this.type);
            hash = 43 * hash + Objects.hashCode(this.nestedSelector);
            return hash;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            PseudoClassImpl other = (PseudoClassImpl)obj;
            if (this.type != other.type) {
                return false;
            }
            if (!Objects.equals(this.name, other.name)) {
                return false;
            }
            if (!Objects.equals(this.functionValue, other.functionValue)) {
                return false;
            }
            return Objects.equals(this.nestedSelector, other.nestedSelector);
        }
    }

    public static class PseudoPageImpl
    implements Selector.PseudoPage {
        private final String name;
        private final Selector.PseudoPageType type;

        protected PseudoPageImpl(String name) {
            this.name = name;
            this.type = Selector.PseudoPageType.forName(name);
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public Selector.PseudoPageType getType() {
            return this.type;
        }

        @Override
        public void computeSpecificity(CombinedSelector.Specificity spec) {
            spec.add(CombinedSelector.Specificity.Level.C);
        }

        @Override
        public boolean matches(Element e, ElementMatcher matcher, MatchCondition cond) {
            if (this.type == null) {
                return false;
            }
            return cond.isSatisfied(e, this);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(":");
            if (this.name != null) {
                sb.append(this.name);
            }
            return sb.toString();
        }

        public int hashCode() {
            int hash = 7;
            hash = 23 * hash + Objects.hashCode(this.name);
            hash = 23 * hash + Objects.hashCode((Object)this.type);
            return hash;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            PseudoPageImpl other = (PseudoPageImpl)obj;
            if (!Objects.equals(this.name, other.name)) {
                return false;
            }
            return this.type == other.type;
        }
    }

    public static class ElementClassImpl
    implements Selector.ElementClass {
        private String className;

        protected ElementClassImpl(String className) {
            this.setClassName(className);
        }

        @Override
        public void computeSpecificity(CombinedSelector.Specificity spec) {
            spec.add(CombinedSelector.Specificity.Level.C);
        }

        @Override
        public boolean matches(Element e, ElementMatcher matcher, MatchCondition cond) {
            return matcher.matchesClass(e, this.className);
        }

        @Override
        public String getClassName() {
            return this.className;
        }

        @Override
        public Selector.ElementClass setClassName(String className) {
            if (className == null) {
                throw new IllegalArgumentException("Invalid element class (null)");
            }
            this.className = className;
            return this;
        }

        public String toString() {
            return "." + CssEscape.escapeCssIdentifier((String)this.className);
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.className == null ? 0 : this.className.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof ElementClassImpl)) {
                return false;
            }
            ElementClassImpl other = (ElementClassImpl)obj;
            return !(this.className == null ? other.className != null : !this.className.equals(other.className));
        }
    }

    public static class ElementNameImpl
    implements Selector.ElementName {
        private String name;

        protected ElementNameImpl(String name) {
            this.setName(name);
        }

        @Override
        public void computeSpecificity(CombinedSelector.Specificity spec) {
            if (!"*".equals(this.name)) {
                spec.add(CombinedSelector.Specificity.Level.D);
            }
        }

        @Override
        public boolean matches(Element e, ElementMatcher matcher, MatchCondition cond) {
            if (this.name != null && "*".equals(this.name)) {
                return true;
            }
            return matcher.matchesName(e, this.name);
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public Selector.ElementName setName(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Invalid element name (null)");
            }
            this.name = name;
            return this;
        }

        public String toString() {
            return CssEscape.escapeCssIdentifier((String)this.name);
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof ElementNameImpl)) {
                return false;
            }
            ElementNameImpl other = (ElementNameImpl)obj;
            return !(this.name == null ? other.name != null : !this.name.equals(other.name));
        }
    }
}

