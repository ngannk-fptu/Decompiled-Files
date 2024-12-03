/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.CombinedSelector;
import cz.vutbr.web.css.ElementMatcher;
import cz.vutbr.web.css.MatchCondition;
import cz.vutbr.web.css.Rule;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.w3c.dom.Element;

public interface Selector
extends Rule<SelectorPart> {
    public Combinator getCombinator();

    public Selector setCombinator(Combinator var1);

    public String getClassName();

    public String getIDName();

    public String getElementName();

    public PseudoElementType getPseudoElementType();

    public boolean hasPseudoClass(PseudoClassType var1);

    public void computeSpecificity(CombinedSelector.Specificity var1);

    public boolean matches(Element var1);

    public boolean matches(Element var1, ElementMatcher var2, MatchCondition var3);

    public static interface PseudoElement
    extends SelectorPart {
        public String getName();

        public String getFunctionValue();

        public PseudoElementType getType();

        public Selector getNestedSelector();
    }

    public static interface PseudoClass
    extends SelectorPart {
        public String getName();

        public String getFunctionValue();

        public PseudoClassType getType();

        public Selector getNestedSelector();
    }

    public static interface PseudoPage
    extends SelectorPart {
        public String getName();

        public PseudoPageType getType();
    }

    public static interface ElementDOM
    extends SelectorPart {
        public Element getElement();

        public ElementDOM setElement(Element var1);
    }

    public static interface ElementID
    extends SelectorPart {
        public String getID();

        public ElementID setID(String var1);
    }

    public static interface ElementClass
    extends SelectorPart {
        public String getClassName();

        public ElementClass setClassName(String var1);
    }

    public static interface ElementAttribute
    extends SelectorPart {
        public String getAttribute();

        public ElementAttribute setAttribute(String var1);

        public String getValue();

        public ElementAttribute setValue(String var1);

        public Operator getOperator();

        public void setOperator(Operator var1);
    }

    public static interface ElementName
    extends SelectorPart {
        public static final String WILDCARD = "*";

        public String getName();

        public ElementName setName(String var1);
    }

    public static interface SelectorPart {
        public boolean matches(Element var1, ElementMatcher var2, MatchCondition var3);

        public void computeSpecificity(CombinedSelector.Specificity var1);
    }

    public static enum PseudoElementType {
        FIRST_LINE("first-line"),
        FIRST_LETTER("first-letter"),
        BEFORE("before"),
        AFTER("after"),
        BACKDROP("backdrop"),
        CUE("cue"),
        GRAMMAR_ERROR("grammar-error"),
        PLACEHOLDER("placeholder"),
        SELECTION("selection"),
        SPELLING_ERROR("spelling-error"),
        vendor(null);

        private final String name;
        private static final Map<String, PseudoElementType> lookup;

        private PseudoElementType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static PseudoElementType forName(String name) {
            if (name == null) {
                return null;
            }
            if (name.startsWith("-") || name.startsWith("_")) {
                return vendor;
            }
            if (lookup.isEmpty()) {
                for (PseudoElementType type : PseudoElementType.values()) {
                    if (type.getName() == null) continue;
                    lookup.put(type.getName(), type);
                }
            }
            return lookup.get(name.toLowerCase());
        }

        static {
            lookup = new ConcurrentHashMap<String, PseudoElementType>();
        }
    }

    public static enum PseudoClassType {
        ACTIVE("active"),
        ANY("any"),
        ANY_LINK("any-link"),
        CHECKED("checked"),
        DEFAULT("default"),
        DEFINED("defined"),
        DIR("dir"),
        DISABLED("disabled"),
        EMPTY("empty"),
        ENABLED("enabled"),
        FIRST_CHILD("first-child"),
        FIRST_OF_TYPE("first-of-type"),
        FULLSCREEN("fullscreen"),
        FOCUS("focus"),
        FOCUS_WITHIN("focus-within"),
        HAS("has"),
        HOVER("hover"),
        INDETERMINATE("indeterminate"),
        IN_RANGE("in-range"),
        INVALID("invalid"),
        LANG("lang"),
        LAST_CHILD("last-child"),
        LAST_OF_TYPE("last-of-type"),
        LINK("link"),
        NOT("not"),
        NTH_CHILD("nth-child"),
        NTH_LAST_CHILD("nth-last-child"),
        NTH_LAST_OF_TYPE("nth-last-of-type"),
        NTH_OF_TYPE("nth-of-type"),
        ONLY_CHILD("only-child"),
        ONLY_OF_TYPE("only-of-type"),
        OPTIONAL("optional"),
        OUT_OF_RANGE("out-of-range"),
        PLACEHOLDER_SHOWN("placeholder-shown"),
        READ_ONLY("read-only"),
        READ_WRITE("read-write"),
        REQUIRED("required"),
        ROOT("root"),
        SCOPE("scope"),
        TARGET("target"),
        VALID("valid"),
        VISITED("visited"),
        vendor(null);

        private final String name;
        private static final Map<String, PseudoClassType> lookup;

        private PseudoClassType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static PseudoClassType forName(String name) {
            if (name == null) {
                return null;
            }
            if (name.startsWith("-") || name.startsWith("_")) {
                return vendor;
            }
            if (lookup.isEmpty()) {
                for (PseudoClassType type : PseudoClassType.values()) {
                    if (type.getName() == null) continue;
                    lookup.put(type.getName(), type);
                }
            }
            return lookup.get(name.toLowerCase());
        }

        static {
            lookup = new ConcurrentHashMap<String, PseudoClassType>();
        }
    }

    public static enum PseudoPageType {
        BLANK("blank"),
        FIRST("first"),
        LEFT("left"),
        RIGHT("right"),
        vendor(null);

        private final String name;
        private static final Map<String, PseudoPageType> lookup;

        private PseudoPageType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public static PseudoPageType forName(String name) {
            if (name == null) {
                return null;
            }
            if (name.startsWith("-") || name.startsWith("_")) {
                return vendor;
            }
            if (lookup.isEmpty()) {
                for (PseudoPageType type : PseudoPageType.values()) {
                    if (type.getName() == null) continue;
                    lookup.put(type.getName(), type);
                }
            }
            return lookup.get(name.toLowerCase());
        }

        static {
            lookup = new ConcurrentHashMap<String, PseudoPageType>();
        }
    }

    public static enum Operator {
        EQUALS("="),
        INCLUDES("~="),
        DASHMATCH("|="),
        CONTAINS("*="),
        STARTSWITH("^="),
        ENDSWITH("$="),
        NO_OPERATOR("");

        private String value;

        private Operator(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }
    }

    public static enum Combinator {
        DESCENDANT(" "),
        ADJACENT("+"),
        PRECEDING("~"),
        CHILD(">");

        private String value;

        private Combinator(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }
    }
}

