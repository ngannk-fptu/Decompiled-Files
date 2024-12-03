/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.impl.audit;

import com.atlassian.confluence.impl.audit.AuditRecordEntity;
import com.atlassian.confluence.impl.audit.AuditSearchTokenizer;
import com.google.common.collect.ImmutableSet;

@Deprecated
public class AuditSearchUtils {
    public static String computeSearchString(AuditRecordEntity entity) {
        AuditSearchTokenizer tokenizer = new AuditSearchTokenizer();
        tokenizer.put(entity.getAuthorName());
        tokenizer.put(entity.getAuthorKey().getStringValue());
        tokenizer.put(entity.getAuthorFullName());
        tokenizer.put(entity.getRemoteAddress());
        tokenizer.put(entity.getSummary());
        tokenizer.put(entity.getCategory());
        tokenizer.put(entity.getObjectName());
        tokenizer.put(entity.getObjectType());
        if (entity.getAssociatedObjects() != null) {
            entity.getAssociatedObjects().stream().forEach(associatedObject -> {
                tokenizer.put(associatedObject.getName());
                tokenizer.put(associatedObject.getType());
            });
        }
        if (entity.getChangedValues() != null) {
            entity.getChangedValues().stream().forEach(changedValue -> {
                tokenizer.put(changedValue.getName());
                tokenizer.put(changedValue.getNewValue());
                tokenizer.put(changedValue.getOldValue());
            });
        }
        return tokenizer.getTokenizedString();
    }

    public static ImmutableSet<String> tokenize(String stringToTokenize) {
        return new AuditSearchTokenizer().put(stringToTokenize).getTokens();
    }
}

