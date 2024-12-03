/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.StringUtils
 */
package org.springframework.transaction.interceptor;

import java.beans.PropertyEditorSupport;
import org.springframework.transaction.interceptor.NoRollbackRuleAttribute;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.util.StringUtils;

public class TransactionAttributeEditor
extends PropertyEditorSupport {
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.hasLength((String)text)) {
            String[] tokens = StringUtils.commaDelimitedListToStringArray((String)text);
            RuleBasedTransactionAttribute attr = new RuleBasedTransactionAttribute();
            for (String token : tokens) {
                String trimmedToken = StringUtils.trimWhitespace((String)token.trim());
                if (StringUtils.containsWhitespace((String)trimmedToken)) {
                    throw new IllegalArgumentException("Transaction attribute token contains illegal whitespace: [" + trimmedToken + "]");
                }
                if (trimmedToken.startsWith("PROPAGATION_")) {
                    attr.setPropagationBehaviorName(trimmedToken);
                    continue;
                }
                if (trimmedToken.startsWith("ISOLATION_")) {
                    attr.setIsolationLevelName(trimmedToken);
                    continue;
                }
                if (trimmedToken.startsWith("timeout_")) {
                    String value = trimmedToken.substring("timeout_".length());
                    attr.setTimeoutString(value);
                    continue;
                }
                if (trimmedToken.equals("readOnly")) {
                    attr.setReadOnly(true);
                    continue;
                }
                if (trimmedToken.startsWith("+")) {
                    attr.getRollbackRules().add(new NoRollbackRuleAttribute(trimmedToken.substring(1)));
                    continue;
                }
                if (trimmedToken.startsWith("-")) {
                    attr.getRollbackRules().add(new RollbackRuleAttribute(trimmedToken.substring(1)));
                    continue;
                }
                throw new IllegalArgumentException("Invalid transaction attribute token: [" + trimmedToken + "]");
            }
            attr.resolveAttributeStrings(null);
            this.setValue(attr);
        } else {
            this.setValue(null);
        }
    }
}

