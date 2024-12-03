/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public interface CSSProperty {
    public static final String INHERIT_KEYWORD = "INHERIT";
    public static final String INITIAL_KEYWORD = "INITIAL";
    public static final String UNSET_KEYWORD = "UNSET";
    public static final String FONT_SERIF = "Serif";
    public static final String FONT_SANS_SERIF = "SansSerif";
    public static final String FONT_MONOSPACED = "Monospaced";
    public static final String FONT_CURSIVE = "Zapf-Chancery";
    public static final String FONT_FANTASY = "Western";

    public boolean inherited();

    public boolean equalsInherit();

    public boolean equalsInitial();

    public boolean equalsUnset();

    public String toString();

    public static class GenericCSSPropertyProxy
    implements CSSProperty {
        private String text;

        private GenericCSSPropertyProxy(String thePropertyValue) {
            this.text = thePropertyValue;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return false;
        }

        @Override
        public boolean equalsInitial() {
            return false;
        }

        @Override
        public boolean equalsUnset() {
            return false;
        }

        @Override
        public String toString() {
            return this.text;
        }

        public static GenericCSSPropertyProxy valueOf(String value) {
            return new GenericCSSPropertyProxy(value == null ? "" : value.toLowerCase());
        }
    }

    public static enum TransitionTimingFunction implements CSSProperty
    {
        timing_function(""),
        list_values(""),
        LINEAR("linear"),
        EASE("ease"),
        EASE_IN("ease-in"),
        EASE_OUT("ease-out"),
        EASE_IN_OUT("ease-in-out"),
        STEP_START("step-start"),
        STEP_END("step-end"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private TransitionTimingFunction(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum TransitionProperty implements CSSProperty
    {
        custom_ident(""),
        list_values(""),
        ALL("all"),
        NONE("none"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private TransitionProperty(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum TransitionDuration implements CSSProperty
    {
        time(""),
        list_values(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private TransitionDuration(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum TransitionDelay implements CSSProperty
    {
        time(""),
        list_values(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private TransitionDelay(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Transition implements CSSProperty
    {
        component_values(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Transition(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum AnimationTimingFunction implements CSSProperty
    {
        timing_function(""),
        list_values(""),
        LINEAR("linear"),
        EASE("ease"),
        EASE_IN("ease-in"),
        EASE_OUT("ease-out"),
        EASE_IN_OUT("ease-in-out"),
        STEP_START("step-start"),
        STEP_END("step-end"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private AnimationTimingFunction(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum AnimationPlayState implements CSSProperty
    {
        list_values(""),
        RUNNING("running"),
        PAUSED("paused"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private AnimationPlayState(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum AnimationName implements CSSProperty
    {
        custom_ident(""),
        list_values(""),
        NONE("none"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private AnimationName(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum AnimationIterationCount implements CSSProperty
    {
        number(""),
        list_values(""),
        INFINITE("infinite"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private AnimationIterationCount(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum AnimationFillMode implements CSSProperty
    {
        list_values(""),
        NONE("none"),
        FORWARDS("forwards"),
        BACKWARDS("backwards"),
        BOTH("both"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private AnimationFillMode(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum AnimationDuration implements CSSProperty
    {
        time(""),
        list_values(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private AnimationDuration(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum AnimationDirection implements CSSProperty
    {
        list_values(""),
        NORMAL("normal"),
        REVERSE("reverse"),
        ALTERNATE("alternate"),
        ALTERNATE_REVERSE("alternate-reverse"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private AnimationDirection(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum AnimationDelay implements CSSProperty
    {
        time(""),
        list_values(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private AnimationDelay(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Animation implements CSSProperty
    {
        component_values(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Animation(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum GridAutoRowsColumns implements CSSProperty
    {
        length(""),
        list_values(""),
        AUTO("auto"),
        MIN_CONTENT("min-content"),
        MAX_CONTENT("max-content"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private GridAutoRowsColumns(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum GridAutoFlow implements CSSProperty
    {
        component_values(""),
        ROW("row"),
        COLUMN("column"),
        DENSE("dense"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private GridAutoFlow(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum GridTemplateRowsColumns implements CSSProperty
    {
        list_values(""),
        AUTO("auto"),
        MAX_CONTENT("max-content"),
        MIN_CONTENT("min-content"),
        NONE("none"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private GridTemplateRowsColumns(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum GridTemplateAreas implements CSSProperty
    {
        list_values(""),
        NONE("none"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private GridTemplateAreas(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum GridGap implements CSSProperty
    {
        component_values(""),
        length(""),
        NORMAL("normal"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private GridGap(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum GridStartEnd implements CSSProperty
    {
        component_values(""),
        number(""),
        identificator(""),
        AUTO("auto"),
        SPAN("span"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private GridStartEnd(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Grid implements CSSProperty
    {
        component_values(""),
        AUTO_FLOW("auto-flow"),
        NONE("none"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Grid(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum BackdropFilter implements CSSProperty
    {
        list_values(""),
        NONE("none"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private BackdropFilter(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Filter implements CSSProperty
    {
        list_values(""),
        NONE("none"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Filter(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Order implements CSSProperty
    {
        integer(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Order(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum JustifyContent implements CSSProperty
    {
        FLEX_START("flex-start"),
        FLEX_END("flex-end"),
        CENTER("center"),
        SPACE_BETWEEN("space-between"),
        SPACE_AROUND("space-around"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private JustifyContent(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum FlexWrap implements CSSProperty
    {
        NOWRAP("nowrap"),
        WRAP("wrap"),
        WRAP_REVERSE("wrap-reverse"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private FlexWrap(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum FlexShrink implements CSSProperty
    {
        number(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private FlexShrink(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum FlexGrow implements CSSProperty
    {
        number(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private FlexGrow(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum FlexDirection implements CSSProperty
    {
        ROW("row"),
        ROW_REVERSE("row-reverse"),
        COLUMN("column"),
        COLUMN_REVERSE("column-reverse"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private FlexDirection(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum FlexBasis implements CSSProperty
    {
        CONTENT("content"),
        length(""),
        percentage(""),
        AUTO("auto"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private FlexBasis(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum FlexFlow implements CSSProperty
    {
        component_values(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private FlexFlow(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Flex implements CSSProperty
    {
        component_values(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Flex(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum AlignSelf implements CSSProperty
    {
        AUTO("auto"),
        FLEX_START("flex-start"),
        FLEX_END("flex-end"),
        CENTER("center"),
        BASELINE("baseline"),
        STRETCH("stretch"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private AlignSelf(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInherit() {
            return false;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum AlignItems implements CSSProperty
    {
        FLEX_START("flex-start"),
        FLEX_END("flex-end"),
        CENTER("center"),
        BASELINE("baseline"),
        STRETCH("stretch"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private AlignItems(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum AlignContent implements CSSProperty
    {
        FLEX_START("flex-start"),
        FLEX_END("flex-end"),
        CENTER("center"),
        SPACE_BETWEEN("space-between"),
        SPACE_AROUND("space-around"),
        STRETCH("stretch"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private AlignContent(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum ZIndex implements CSSProperty
    {
        integer(""),
        AUTO("auto"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private ZIndex(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum LetterSpacing implements CSSProperty
    {
        length(""),
        NORMAL("normal"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private LetterSpacing(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum WordSpacing implements CSSProperty
    {
        length(""),
        NORMAL("normal"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private WordSpacing(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Widows implements CSSProperty
    {
        integer(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Widows(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum WhiteSpace implements CSSProperty
    {
        NORMAL("normal"),
        PRE("pre"),
        NOWRAP("nowrap"),
        PRE_WRAP("pre-wrap"),
        PRE_LINE("pre-line"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private WhiteSpace(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Volume implements CSSProperty
    {
        number(""),
        percentage(""),
        SILENT("silent"),
        X_SOFT("x-soft"),
        SOFT("soft"),
        MEDIUM("medium"),
        LOUD("loud"),
        X_LOUD("x-loud"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Volume(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum VoiceFamily implements CSSProperty
    {
        list_values(""),
        MALE("male"),
        FEMALE("female"),
        CHILD("child"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private VoiceFamily(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Visibility implements CSSProperty
    {
        VISIBLE("visible"),
        HIDDEN("hidden"),
        COLLAPSE("collapse"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Visibility(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum VerticalAlign implements CSSProperty
    {
        length(""),
        percentage(""),
        BASELINE("baseline"),
        SUB("sub"),
        SUPER("super"),
        TOP("top"),
        TEXT_TOP("text-top"),
        MIDDLE("middle"),
        BOTTOM("bottom"),
        TEXT_BOTTOM("text-bottom"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private VerticalAlign(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum UnicodeRange implements CSSProperty
    {
        list_values;


        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return false;
        }

        @Override
        public boolean equalsInitial() {
            return false;
        }

        @Override
        public boolean equalsUnset() {
            return false;
        }

        @Override
        public String toString() {
            return "";
        }
    }

    public static enum UnicodeBidi implements CSSProperty
    {
        NORMAL("normal"),
        EMDEB("embed"),
        BIDI_OVERRIDE("bidi-override"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private UnicodeBidi(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum TransformOrigin implements CSSProperty
    {
        list_values(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private TransformOrigin(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Transform implements CSSProperty
    {
        list_values(""),
        NONE("none"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Transform(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum TextTransform implements CSSProperty
    {
        CAPITALIZE("capitalize"),
        UPPERCASE("uppercase"),
        LOWERCASE("lowercase"),
        NONE("none"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private TextTransform(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum TextIndent implements CSSProperty
    {
        length(""),
        percentage(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private TextIndent(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum TextDecoration implements CSSProperty
    {
        list_values(""),
        UNDERLINE("underline"),
        OVERLINE("overline"),
        BLINK("blink"),
        LINE_THROUGH("line-through"),
        NONE("none"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private TextDecoration(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum TextAlign implements CSSProperty
    {
        BY_DIRECTION("by-direction"),
        LEFT("left"),
        RIGHT("right"),
        CENTER("center"),
        JUSTIFY("justify"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private TextAlign(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum TableLayout implements CSSProperty
    {
        AUTO("auto"),
        FIXED("fixed"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private TableLayout(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Quotes implements CSSProperty
    {
        list_values(""),
        NONE("none"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Quotes(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Left implements CSSProperty
    {
        length(""),
        percentage(""),
        AUTO("auto"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Left(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Bottom implements CSSProperty
    {
        length(""),
        percentage(""),
        AUTO("auto"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Bottom(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Right implements CSSProperty
    {
        length(""),
        percentage(""),
        AUTO("auto"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Right(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Top implements CSSProperty
    {
        length(""),
        percentage(""),
        AUTO("auto"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Top(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Stress implements CSSProperty
    {
        number(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Stress(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum SpeechRate implements CSSProperty
    {
        number(""),
        X_SLOW("x-slow"),
        SLOW("slow"),
        MEDIUM("medium"),
        FAST("fast"),
        X_FAST("x-fast"),
        FASTER("faster"),
        SLOWER("slower"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private SpeechRate(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Speak implements CSSProperty
    {
        NORMAL("normal"),
        NONE("none"),
        SPELL_OUT("spell-out"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Speak(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum SpeakPunctuation implements CSSProperty
    {
        CODE("code"),
        NONE("none"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private SpeakPunctuation(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum SpeakNumeral implements CSSProperty
    {
        DIGITS("digits"),
        CONTINUOUS("continuous"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private SpeakNumeral(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum SpeakHeader implements CSSProperty
    {
        ONCE("once"),
        ALWAYS("always"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private SpeakHeader(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Richness implements CSSProperty
    {
        number("number"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Richness(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Position implements CSSProperty
    {
        STATIC("static"),
        RELATIVE("relative"),
        ABSOLUTE("absolute"),
        FIXED("fixed"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Position(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum PlayDuring implements CSSProperty
    {
        uri(""),
        uri_mix(""),
        uri_repeat(""),
        AUTO("auto"),
        NONE("none"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private PlayDuring(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Pitch implements CSSProperty
    {
        frequency(""),
        X_LOW("x-low"),
        LOW("low"),
        MEDIUM("medium"),
        HIGH("high"),
        X_HIGH("x-high"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Pitch(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum PitchRange implements CSSProperty
    {
        number(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private PitchRange(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Pause implements CSSProperty
    {
        component_values(""),
        time(""),
        percentage(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Pause(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum PageBreakInside implements CSSProperty
    {
        AUTO("auto"),
        AVOID("avoid"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private PageBreakInside(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum PageBreak implements CSSProperty
    {
        AUTO("auto"),
        ALWAYS("always"),
        AVOID("avoid"),
        LEFT("left"),
        RIGHT("right"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private PageBreak(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Padding implements CSSProperty
    {
        length(""),
        percentage(""),
        component_values(""),
        AUTO("auto"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Padding(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Overflow implements CSSProperty
    {
        component_values(""),
        VISIBLE("visible"),
        HIDDEN("hidden"),
        CLIP("clip"),
        SCROLL("scroll"),
        AUTO("auto"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Overflow(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum OutlineColor implements CSSProperty
    {
        color(""),
        INVERT("invert"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private OutlineColor(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum OutlineStyle implements CSSProperty
    {
        NONE("none"),
        DOTTED("dotted"),
        DASHED("dashed"),
        SOLID("solid"),
        DOUBLE("double"),
        GROOVE("groove"),
        RIDGE("ridge"),
        INSET("inset"),
        OUTSET("outset"),
        HIDDEN("hidden"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private OutlineStyle(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum OutlineWidth implements CSSProperty
    {
        length(""),
        THIN("thin"),
        MEDIUM("medium"),
        THICK("thick"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private OutlineWidth(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Outline implements CSSProperty
    {
        component_values(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Outline(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Orphans implements CSSProperty
    {
        integer(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Orphans(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Opacity implements CSSProperty
    {
        number(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Opacity(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Margin implements CSSProperty
    {
        length(""),
        percentage(""),
        component_values(""),
        AUTO("auto"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Margin(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum ListStyleType implements CSSProperty
    {
        DISC("disc"),
        CIRCLE("circle"),
        SQUARE("square"),
        DECIMAL("decimal"),
        DECIMAL_LEADING_ZERO("decimal-leading-zero"),
        LOWER_ROMAN("lower-roman"),
        UPPER_ROMAN("upper-roman"),
        LOWER_GREEK("lower-greek"),
        LOWER_LATIN("lower-latin"),
        UPPER_LATN("upper-latin"),
        ARMENIAN("armenian"),
        GEORGIAN("georgian"),
        LOWER_ALPHA("lower-alpha"),
        UPPER_ALPHA("upper-alpha"),
        NONE("none"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private ListStyleType(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum ListStylePosition implements CSSProperty
    {
        INSIDE("inside"),
        OUTSIDE("outside"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private ListStylePosition(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum ListStyleImage implements CSSProperty
    {
        uri(""),
        NONE("none"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private ListStyleImage(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum ListStyle implements CSSProperty
    {
        component_values(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private ListStyle(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Float implements CSSProperty
    {
        NONE("none"),
        LEFT("left"),
        RIGHT("right"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Float(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum EmptyCells implements CSSProperty
    {
        SHOW("show"),
        HIDE("hide"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private EmptyCells(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum MaxHeight implements CSSProperty
    {
        length(""),
        percentage(""),
        NONE("none"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private MaxHeight(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum MinHeight implements CSSProperty
    {
        length(""),
        percentage(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private MinHeight(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Height implements CSSProperty
    {
        length(""),
        percentage(""),
        AUTO("auto"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Height(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum MaxWidth implements CSSProperty
    {
        length(""),
        percentage(""),
        NONE("none"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private MaxWidth(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum MinWidth implements CSSProperty
    {
        length(""),
        percentage(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private MinWidth(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Width implements CSSProperty
    {
        length(""),
        percentage(""),
        AUTO("auto"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Width(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Display implements CSSProperty
    {
        INLINE("inline"),
        BLOCK("block"),
        LIST_ITEM("list-item"),
        RUN_IN("run-in"),
        INLINE_BLOCK("inline-block"),
        TABLE("table"),
        INLINE_TABLE("inline-table"),
        TABLE_ROW_GROUP("table-row-group"),
        TABLE_HEADER_GROUP("table-header-group"),
        TABLE_FOOTER_GROUP("table-footer-group"),
        TABLE_ROW("table-row"),
        TABLE_COLUMN_GROUP("table-column-group"),
        TABLE_COLUMN("table-column"),
        TABLE_CELL("table-cell"),
        TABLE_CAPTION("table-caption"),
        FLEX("flex"),
        INLINE_FLEX("inline-flex"),
        GRID("grid"),
        INLINE_GRID("inline-grid"),
        NONE("none"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Display(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Direction implements CSSProperty
    {
        LTR("ltr"),
        RTL("rtl"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Direction(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Cursor implements CSSProperty
    {
        AUTO("auto"),
        CROSSHAIR("crosshair"),
        DEFAULT("default"),
        POINTER("pointer"),
        MOVE("move"),
        E_RESIZE("e-resize"),
        NE_RESIZE("ne-resize"),
        NW_RESIZE("nw-resize"),
        N_RESIZE("n-resize"),
        SE_RESIZE("se-resize"),
        SW_RESIZE("sw-resize"),
        S_RESIZE("s-resize"),
        W_RESIZE("w-resize"),
        TEXT("text"),
        WAIT("wait"),
        PROGRESS("progress"),
        HELP("help"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Cursor(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Clip implements CSSProperty
    {
        shape(""),
        AUTO("auto"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Clip(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Clear implements CSSProperty
    {
        NONE("none"),
        LEFT("left"),
        RIGHT("right"),
        BOTH("both"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Clear(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum CounterReset implements CSSProperty
    {
        list_values(""),
        NONE("none"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private CounterReset(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum CounterIncrement implements CSSProperty
    {
        list_values(""),
        NONE("none"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private CounterIncrement(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Content implements CSSProperty
    {
        list_values(""),
        NORMAL("normal"),
        NONE("none"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Content(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum CaptionSide implements CSSProperty
    {
        TOP("top"),
        BOTTOM("bottom"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private CaptionSide(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum TabSize implements CSSProperty
    {
        integer(""),
        length(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private TabSize(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum LineHeight implements CSSProperty
    {
        number(""),
        length(""),
        percentage(""),
        NORMAL("normal"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private LineHeight(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum FontWeight implements CSSProperty
    {
        numeric_100("100"),
        numeric_200("200"),
        numeric_300("300"),
        numeric_400("400"),
        numeric_500("500"),
        numeric_600("600"),
        numeric_700("700"),
        numeric_800("800"),
        numeric_900("900"),
        NORMAL("normal"),
        BOLD("bold"),
        BOLDER("bolder"),
        LIGHTER("lighter"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private FontWeight(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum FontVariant implements CSSProperty
    {
        SMALL_CAPS("small-caps"),
        NORMAL("normal"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private FontVariant(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum FontStyle implements CSSProperty
    {
        NORMAL("normal"),
        ITALIC("italic"),
        OBLIQUE("oblique"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private FontStyle(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum FontSize implements CSSProperty
    {
        percentage(""),
        length(""),
        XX_SMALL("xx-small"),
        X_SMALL("x-small"),
        SMALL("small"),
        MEDIUM("medium"),
        LARGE("large"),
        X_LARGE("x-large"),
        XX_LARGE("xx-large"),
        LARGER("larger"),
        SMALLER("smaller"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private FontSize(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum FontFamily implements CSSProperty
    {
        list_values("", ""),
        SERIF("serif", "Serif"),
        SANS_SERIF("sans-serif", "SansSerif"),
        CURSIVE("cursive", "Zapf-Chancery"),
        FANTASY("fantasy", "Western"),
        MONOSPACE("monospace", "Monospaced"),
        INHERIT("inherit", ""),
        INITIAL("initial", ""),
        UNSET("unset", "");

        private String text;
        private String awtval;

        private FontFamily(String text, String awtval) {
            this.text = text;
            this.awtval = awtval;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }

        public String getAWTValue() {
            return this.awtval;
        }
    }

    public static enum Font implements CSSProperty
    {
        component_values(""),
        CAPTION("caption"),
        ICON("icon"),
        MENU("menu"),
        MESSAGE_BOX("message-box"),
        SMALL_CAPTION("small-caption"),
        STATUS_BAR("status-bar"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Font(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Elevation implements CSSProperty
    {
        angle(""),
        BELOW("below"),
        LEVEL("level"),
        ABOVE("above"),
        HIGHER("higher"),
        LOWER("lower"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Elevation(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum BoxSizing implements CSSProperty
    {
        CONTENT_BOX("content-box"),
        BORDER_BOX("border-box"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private BoxSizing(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum BoxShadow implements CSSProperty
    {
        component_values(""),
        NONE("none"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private BoxShadow(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum BorderWidth implements CSSProperty
    {
        component_values(""),
        length(""),
        THIN("thin"),
        MEDIUM("medium"),
        THICK("thick"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private BorderWidth(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum BorderStyle implements CSSProperty
    {
        component_values(""),
        NONE("none"),
        HIDDEN("hidden"),
        DOTTED("dotted"),
        DASHED("dashed"),
        SOLID("solid"),
        DOUBLE("double"),
        GROOVE("groove"),
        RIDGE("ridge"),
        INSET("inset"),
        OUTSET("outset"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private BorderStyle(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum BorderSpacing implements CSSProperty
    {
        list_values(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private BorderSpacing(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum BorderRadius implements CSSProperty
    {
        component_values(""),
        list_values(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private BorderRadius(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum BorderColor implements CSSProperty
    {
        color(""),
        component_values(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private BorderColor(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum BorderCollapse implements CSSProperty
    {
        COLLAPSE("collapse"),
        SEPARATE("separate"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private BorderCollapse(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Border implements CSSProperty
    {
        component_values(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Border(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum BackgroundSize implements CSSProperty
    {
        list_values(""),
        CONTAIN("contain"),
        COVER("cover"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private BackgroundSize(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum BackgroundRepeat implements CSSProperty
    {
        REPEAT("repeat"),
        REPEAT_X("repeat-x"),
        REPEAT_Y("repeat-y"),
        NO_REPEAT("no-repeat"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private BackgroundRepeat(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum BackgroundPosition implements CSSProperty
    {
        list_values(""),
        LEFT("left"),
        CENTER("center"),
        RIGHT("right"),
        TOP("top"),
        BOTTOM("bottom"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private BackgroundPosition(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum BackgroundImage implements CSSProperty
    {
        uri(""),
        gradient(""),
        NONE("none"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private BackgroundImage(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum BackgroundColor implements CSSProperty
    {
        color(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private BackgroundColor(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum BackgroundAttachment implements CSSProperty
    {
        SCROLL("scroll"),
        FIXED("fixed"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private BackgroundAttachment(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Background implements CSSProperty
    {
        component_values(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Background(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Cue implements CSSProperty
    {
        component_values(""),
        uri(""),
        NONE("none"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Cue(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return false;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Color implements CSSProperty
    {
        color(""),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Color(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static enum Azimuth implements CSSProperty
    {
        angle(""),
        LEFT_SIDE("left-side"),
        FAR_LEFT("far-left"),
        LEFT("left"),
        CENTER_LEFT("center-left"),
        CENTER("center"),
        CENTER_RIGHT("center-right"),
        RIGHT("right"),
        FAR_RIGHT("far-right"),
        RIGHT_SIDE("right-side"),
        BEHIND("behind"),
        LEFTWARDS("leftwards"),
        RIGHTWARDS("rightwards"),
        INHERIT("inherit"),
        INITIAL("initial"),
        UNSET("unset");

        private String text;

        private Azimuth(String text) {
            this.text = text;
        }

        @Override
        public boolean inherited() {
            return true;
        }

        @Override
        public boolean equalsInherit() {
            return this == INHERIT;
        }

        @Override
        public boolean equalsInitial() {
            return this == INITIAL;
        }

        @Override
        public boolean equalsUnset() {
            return this == UNSET;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }

    public static class Translator {
        private static Map<Class<? extends CSSProperty>, Method> translators = new HashMap<Class<? extends CSSProperty>, Method>();

        public static final <T extends CSSProperty> T valueOf(Class<T> type, String value) {
            try {
                Method m = translators.get(type);
                if (m == null) {
                    m = type.getMethod("valueOf", String.class);
                }
                return (T)((CSSProperty)m.invoke(null, value));
            }
            catch (Exception e) {
                return null;
            }
        }

        public static final <T extends CSSProperty> T createInherit(Class<T> type) {
            return Translator.valueOf(type, CSSProperty.INHERIT_KEYWORD);
        }
    }
}

