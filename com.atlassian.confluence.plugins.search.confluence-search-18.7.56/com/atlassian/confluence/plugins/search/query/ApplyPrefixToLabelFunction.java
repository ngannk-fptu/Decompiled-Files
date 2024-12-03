/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelParser
 *  com.atlassian.confluence.labels.Namespace
 *  com.atlassian.confluence.labels.ParsedLabelName
 *  com.atlassian.fugue.Option
 *  com.google.common.base.Function
 *  com.google.common.base.Strings
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.plugins.search.query;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelParser;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.labels.ParsedLabelName;
import com.atlassian.fugue.Option;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import javax.annotation.Nullable;

public class ApplyPrefixToLabelFunction
implements Function<String, Option<String>> {
    private static final ApplyPrefixToLabelFunction INSTANCE = new ApplyPrefixToLabelFunction();

    public static ApplyPrefixToLabelFunction getInstance() {
        return INSTANCE;
    }

    private ApplyPrefixToLabelFunction() {
    }

    public Option<String> apply(@Nullable String rawLabel) {
        if (Strings.isNullOrEmpty((String)rawLabel)) {
            return Option.none();
        }
        ParsedLabelName parsedLabelName = LabelParser.parse((String)rawLabel);
        if (parsedLabelName == null) {
            return Option.none();
        }
        if (Namespace.isGlobal((Label)parsedLabelName.toLabel())) {
            parsedLabelName.setPrefix(Namespace.GLOBAL.getPrefix());
        }
        Label label = parsedLabelName.toLabel();
        return Option.some((Object)label.toStringWithNamespace());
    }
}

