/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Lists
 *  org.codehaus.jackson.JsonNode
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.contentproperty.index.extractor;

import com.atlassian.fugue.Option;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.Iterator;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JsonExpressionEvaluator {
    private static final Logger log = LoggerFactory.getLogger(JsonExpressionEvaluator.class);
    private static final Splitter JSON_EXPRESSION_SPLITTER = Splitter.on((String)".").omitEmptyStrings().trimResults();

    public Iterable<JsonNode> evaluate(JsonNode json, String jsonExpression) {
        Iterable jsonPaths = JSON_EXPRESSION_SPLITTER.split((CharSequence)jsonExpression);
        for (String path : jsonPaths) {
            json = json.path(path);
        }
        if (json.isMissingNode()) {
            log.debug("Expression {} doesn't evaluate to any node in JSON document.", (Object)jsonExpression);
            return Option.none();
        }
        if (json.isObject() || json.isArray()) {
            return Lists.newArrayList((Iterator)json.getElements());
        }
        return Option.some((Object)json);
    }
}

