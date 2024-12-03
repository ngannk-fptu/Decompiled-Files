/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.Singleton
 *  com.google.template.soy.data.SanitizedContent$ContentKind
 *  com.google.template.soy.data.SoyData
 *  com.google.template.soy.data.SoyValue
 *  com.google.template.soy.data.UnsafeSanitizedContentOrdainer
 *  com.google.template.soy.data.restricted.NumberData
 *  com.google.template.soy.data.restricted.StringData
 *  com.google.template.soy.jssrc.restricted.JsExpr
 *  com.google.template.soy.jssrc.restricted.SoyJsSrcFunction
 *  com.google.template.soy.shared.restricted.Sanitizers
 *  com.google.template.soy.shared.restricted.SoyJavaFunction
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.soy.impl.functions;

import com.atlassian.soy.impl.functions.GetTextFunction;
import com.atlassian.soy.spi.i18n.I18nResolver;
import com.atlassian.soy.spi.i18n.JsLocaleResolver;
import com.google.inject.Singleton;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.UnsafeSanitizedContentOrdainer;
import com.google.template.soy.data.restricted.NumberData;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.jssrc.restricted.JsExpr;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;
import com.google.template.soy.shared.restricted.Sanitizers;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;

@Singleton
public class GetTextAsHtmlFunction
implements SoyJsSrcFunction,
SoyJavaFunction {
    public static final String FUNCTION_NAME = "getTextAsHtml";
    private final GetTextFunction getTextFunction;

    @Inject
    public GetTextAsHtmlFunction(JsLocaleResolver jsLocaleResolver, I18nResolver i18nResolver, @Named(value="atlassian.soy.functions.text.use.ajs") boolean useAjsI18n) {
        this.getTextFunction = new GetTextFunction(jsLocaleResolver, i18nResolver, useAjsI18n);
    }

    public String getName() {
        return FUNCTION_NAME;
    }

    public Set<Integer> getValidArgsSizes() {
        return this.getTextFunction.getValidArgsSizes();
    }

    public JsExpr computeForJsSrc(List<JsExpr> args) {
        ArrayList<JsExpr> escapedArgs = new ArrayList<JsExpr>(args.size());
        escapedArgs.add(args.get(0));
        for (JsExpr arg : args.subList(1, args.size())) {
            escapedArgs.add(new JsExpr("typeof (" + arg.getText() + ") === 'number' ? " + arg.getText() + " : soy.$$escapeHtml(" + arg.getText() + ")", Integer.MAX_VALUE));
        }
        return new JsExpr("soydata.VERY_UNSAFE.ordainSanitizedHtml(" + this.getTextFunction.computeForJsSrc(escapedArgs).getText() + ")", Integer.MAX_VALUE);
    }

    public SoyData computeForJava(List<SoyValue> args) {
        ArrayList<SoyValue> escapedArgs = new ArrayList<SoyValue>();
        SoyValue i18nKey = args.get(0);
        escapedArgs.add(i18nKey);
        for (SoyValue arg : args.subList(1, args.size())) {
            SoyValue escapedArg = arg instanceof NumberData ? arg : StringData.forValue((String)Sanitizers.escapeHtml((SoyValue)arg));
            escapedArgs.add(escapedArg);
        }
        String html = this.getTextFunction.computeForJava(escapedArgs).stringValue();
        if (html.equals(i18nKey.stringValue())) {
            html = Sanitizers.escapeHtml((String)html);
        }
        return UnsafeSanitizedContentOrdainer.ordainAsSafe((String)html, (SanitizedContent.ContentKind)SanitizedContent.ContentKind.HTML);
    }
}

