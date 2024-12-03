/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.sts.endpoints.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.services.sts.endpoints.internal.EndpointResult;
import software.amazon.awssdk.services.sts.endpoints.internal.Rule;
import software.amazon.awssdk.services.sts.endpoints.internal.RuleValueVisitor;

@SdkInternalApi
public final class EndpointRule
extends Rule {
    private final EndpointResult endpoint;

    protected EndpointRule(Rule.Builder builder, EndpointResult endpoint) {
        super(builder);
        this.endpoint = endpoint;
    }

    public EndpointResult getEndpoint() {
        return this.endpoint;
    }

    @Override
    public <T> T accept(RuleValueVisitor<T> visitor) {
        return visitor.visitEndpointRule(this.getEndpoint());
    }

    public String toString() {
        return "EndpointRule{endpoint=" + this.endpoint + ", conditions=" + this.conditions + ", documentation='" + this.documentation + '\'' + '}';
    }
}

