/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.google.inject.Singleton
 *  com.google.template.soy.data.SanitizedContent$ContentKind
 *  com.google.template.soy.data.SoyData
 *  com.google.template.soy.data.SoyValue
 *  com.google.template.soy.data.UnsafeSanitizedContentOrdainer
 *  com.google.template.soy.shared.restricted.SoyJavaFunction
 *  javax.inject.Inject
 */
package com.atlassian.soy.impl.functions;

import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.google.inject.Singleton;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.UnsafeSanitizedContentOrdainer;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

@Singleton
public class IncludeResourcesFunction
implements SoyJavaFunction {
    private static final Set<Integer> ARGS_SIZE = Collections.singleton(0);
    private final WebResourceManager webResourceManager;

    @Inject
    public IncludeResourcesFunction(WebResourceManager webResourceManager) {
        this.webResourceManager = webResourceManager;
    }

    public SoyData computeForJava(List<SoyValue> args) {
        StringWriter writer = new StringWriter();
        this.webResourceManager.includeResources((Writer)writer, UrlMode.AUTO);
        return UnsafeSanitizedContentOrdainer.ordainAsSafe((String)writer.toString(), (SanitizedContent.ContentKind)SanitizedContent.ContentKind.HTML);
    }

    public String getName() {
        return "webResourceManager_includeResources";
    }

    public Set<Integer> getValidArgsSizes() {
        return ARGS_SIZE;
    }
}

