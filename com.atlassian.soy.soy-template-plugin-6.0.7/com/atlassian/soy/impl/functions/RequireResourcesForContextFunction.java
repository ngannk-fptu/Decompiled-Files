/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.google.inject.Singleton
 *  com.google.template.soy.base.SoySyntaxException
 *  com.google.template.soy.data.SoyData
 *  com.google.template.soy.data.SoyValue
 *  com.google.template.soy.data.restricted.StringData
 *  com.google.template.soy.shared.restricted.SoyJavaFunction
 *  javax.inject.Inject
 */
package com.atlassian.soy.impl.functions;

import com.atlassian.plugin.webresource.WebResourceManager;
import com.google.inject.Singleton;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

@Singleton
public class RequireResourcesForContextFunction
implements SoyJavaFunction {
    private static final Set<Integer> ARGS_SIZE = Collections.singleton(1);
    private final WebResourceManager webResourceManager;

    @Inject
    public RequireResourcesForContextFunction(WebResourceManager webResourceManager) {
        this.webResourceManager = webResourceManager;
    }

    public SoyData computeForJava(List<SoyValue> args) {
        SoyValue data = args.get(0);
        if (!(data instanceof StringData)) {
            throw SoySyntaxException.createWithoutMetaInfo((String)("Argument to " + this.getName() + "() is not a literal string"));
        }
        String context = data.stringValue();
        this.webResourceManager.requireResourcesForContext(context);
        return StringData.EMPTY_STRING;
    }

    public String getName() {
        return "webResourceManager_requireResourcesForContext";
    }

    public Set<Integer> getValidArgsSizes() {
        return ARGS_SIZE;
    }
}

