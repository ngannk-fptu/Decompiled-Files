/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.google.template.soy.basicdirectives;

import com.google.common.collect.ImmutableSet;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcPrintDirective;
import com.google.template.soy.shared.restricted.Sanitizers;
import com.google.template.soy.shared.restricted.SoyJavaPrintDirective;
import com.google.template.soy.shared.restricted.SoyPurePrintDirective;
import java.util.List;
import java.util.Set;
import javax.inject.Singleton;

public abstract class BasicEscapeDirective
implements SoyJavaPrintDirective,
SoyJsSrcPrintDirective {
    private final String name;
    private static final Set<Integer> VALID_ARGS_SIZES = ImmutableSet.of((Object)0);

    public BasicEscapeDirective(String name) {
        this.name = name;
    }

    protected abstract String escape(SoyValue var1);

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final Set<Integer> getValidArgsSizes() {
        return VALID_ARGS_SIZES;
    }

    @Override
    public final boolean shouldCancelAutoescape() {
        return true;
    }

    @Override
    public SoyValue applyForJava(SoyValue value, List<SoyValue> args) {
        return StringData.forValue(this.escape(value));
    }

    @Override
    public JsExpr applyForJsSrc(JsExpr value, List<JsExpr> args) {
        return new JsExpr("soy.$$" + this.name.substring(1) + "(" + value.getText() + ")", Integer.MAX_VALUE);
    }

    @Singleton
    @SoyPurePrintDirective
    static final class EscapeUri
    extends BasicEscapeDirective {
        EscapeUri() {
            super("|escapeUri");
        }

        @Override
        protected String escape(SoyValue value) {
            return Sanitizers.escapeUri(value);
        }
    }

    @Singleton
    @SoyPurePrintDirective
    static final class NormalizeUri
    extends BasicEscapeDirective {
        NormalizeUri() {
            super("|normalizeUri");
        }

        @Override
        protected String escape(SoyValue value) {
            return Sanitizers.normalizeUri(value);
        }
    }

    @Singleton
    @SoyPurePrintDirective
    static final class FilterNormalizeUri
    extends BasicEscapeDirective {
        FilterNormalizeUri() {
            super("|filterNormalizeUri");
        }

        @Override
        protected String escape(SoyValue value) {
            return Sanitizers.filterNormalizeUri(value);
        }
    }

    @Singleton
    @SoyPurePrintDirective
    static final class EscapeJsValue
    extends BasicEscapeDirective {
        EscapeJsValue() {
            super("|escapeJsValue");
        }

        @Override
        protected String escape(SoyValue value) {
            return Sanitizers.escapeJsValue(value);
        }
    }

    @Singleton
    @SoyPurePrintDirective
    static final class EscapeJsString
    extends BasicEscapeDirective {
        EscapeJsString() {
            super("|escapeJsString");
        }

        @Override
        protected String escape(SoyValue value) {
            return Sanitizers.escapeJsString(value);
        }
    }

    @Singleton
    @SoyPurePrintDirective
    static final class EscapeJsRegex
    extends BasicEscapeDirective {
        EscapeJsRegex() {
            super("|escapeJsRegex");
        }

        @Override
        protected String escape(SoyValue value) {
            return Sanitizers.escapeJsRegex(value);
        }
    }

    @Singleton
    @SoyPurePrintDirective
    static final class FilterHtmlElementName
    extends BasicEscapeDirective {
        FilterHtmlElementName() {
            super("|filterHtmlElementName");
        }

        @Override
        protected String escape(SoyValue value) {
            return Sanitizers.filterHtmlElementName(value);
        }
    }

    @Singleton
    @SoyPurePrintDirective
    static final class FilterHtmlAttributes
    extends BasicEscapeDirective {
        FilterHtmlAttributes() {
            super("|filterHtmlAttributes");
        }

        @Override
        protected String escape(SoyValue value) {
            return Sanitizers.filterHtmlAttributes(value);
        }
    }

    @Singleton
    @SoyPurePrintDirective
    static final class EscapeHtmlAttributeNospace
    extends BasicEscapeDirective {
        EscapeHtmlAttributeNospace() {
            super("|escapeHtmlAttributeNospace");
        }

        @Override
        protected String escape(SoyValue value) {
            return Sanitizers.escapeHtmlAttributeNospace(value);
        }
    }

    @Singleton
    @SoyPurePrintDirective
    static final class EscapeHtmlAttribute
    extends BasicEscapeDirective {
        EscapeHtmlAttribute() {
            super("|escapeHtmlAttribute");
        }

        @Override
        protected String escape(SoyValue value) {
            return Sanitizers.escapeHtmlAttribute(value);
        }
    }

    @Singleton
    @SoyPurePrintDirective
    static final class EscapeHtmlRcdata
    extends BasicEscapeDirective {
        EscapeHtmlRcdata() {
            super("|escapeHtmlRcdata");
        }

        @Override
        protected String escape(SoyValue value) {
            return Sanitizers.escapeHtmlRcdata(value);
        }
    }

    @Singleton
    @SoyPurePrintDirective
    static final class FilterCssValue
    extends BasicEscapeDirective {
        FilterCssValue() {
            super("|filterCssValue");
        }

        @Override
        protected String escape(SoyValue value) {
            return Sanitizers.filterCssValue(value);
        }
    }

    @Singleton
    @SoyPurePrintDirective
    static final class EscapeCssString
    extends BasicEscapeDirective {
        EscapeCssString() {
            super("|escapeCssString");
        }

        @Override
        protected String escape(SoyValue value) {
            return Sanitizers.escapeCssString(value);
        }
    }
}

