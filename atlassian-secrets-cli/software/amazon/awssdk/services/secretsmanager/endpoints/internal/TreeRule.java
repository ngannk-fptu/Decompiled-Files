/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import java.util.List;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Rule;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.RuleValueVisitor;

@SdkInternalApi
public class TreeRule
extends Rule {
    private final List<Rule> rules;

    protected TreeRule(Rule.Builder builder, List<Rule> rules) {
        super(builder);
        this.rules = rules;
    }

    @Override
    public <T> T accept(RuleValueVisitor<T> v) {
        return v.visitTreeRule(this.rules);
    }

    public String toString() {
        return "TreeRule{conditions=" + this.conditions + ", documentation='" + this.documentation + '\'' + ", rules=" + this.rules + '}';
    }
}

