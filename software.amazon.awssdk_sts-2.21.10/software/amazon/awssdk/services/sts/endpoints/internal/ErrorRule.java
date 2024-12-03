/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.sts.endpoints.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.sts.endpoints.internal.Expr;
import software.amazon.awssdk.services.sts.endpoints.internal.Rule;
import software.amazon.awssdk.services.sts.endpoints.internal.RuleValueVisitor;

@SdkInternalApi
public class ErrorRule
extends Rule {
    private final Expr error;

    public ErrorRule(Rule.Builder builder, Expr error) {
        super(builder);
        this.error = error;
    }

    @Override
    public <T> T accept(RuleValueVisitor<T> v) {
        return v.visitErrorRule(this.error);
    }

    public String toString() {
        return "ErrorRule{error=" + this.error + ", conditions=" + this.conditions + ", documentation='" + this.documentation + '\'' + '}';
    }
}

