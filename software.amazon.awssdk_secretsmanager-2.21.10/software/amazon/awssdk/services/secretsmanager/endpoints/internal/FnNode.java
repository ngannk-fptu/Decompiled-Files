/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.protocols.jsoncore.JsonNode
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.BooleanEqualsFn;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Expr;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Fn;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.GetAttr;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.IsSet;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.IsValidHostLabel;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.IsVirtualHostableS3Bucket;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Not;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.ParseArn;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.ParseUrl;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.PartitionFn;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.RuleError;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.SourceException;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.StringEqualsFn;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.Substring;
import software.amazon.awssdk.services.secretsmanager.endpoints.internal.UriEncodeFn;

@SdkInternalApi
public final class FnNode {
    private static final String ARGV = "argv";
    private static final String FN = "fn";
    private final String fn;
    private final List<Expr> argv;

    private FnNode(Builder builder) {
        this.fn = builder.fn;
        this.argv = builder.argv;
    }

    public static FnNode ofExprs(String fn, Expr ... expr) {
        return FnNode.builder().fn(fn).argv(Arrays.stream(expr).collect(Collectors.toList())).build();
    }

    public Fn validate() {
        switch (this.fn) {
            case "booleanEquals": {
                return new BooleanEqualsFn(this);
            }
            case "aws.partition": {
                return new PartitionFn(this);
            }
            case "stringEquals": {
                return new StringEqualsFn(this);
            }
            case "isSet": {
                return new IsSet(this);
            }
            case "isValidHostLabel": {
                return new IsValidHostLabel(this);
            }
            case "getAttr": {
                return new GetAttr(this);
            }
            case "aws.parseArn": {
                return new ParseArn(this);
            }
            case "not": {
                return new Not(this);
            }
            case "parseURL": {
                return new ParseUrl(this);
            }
            case "substring": {
                return new Substring(this);
            }
            case "uriEncode": {
                return new UriEncodeFn(this);
            }
            case "aws.isVirtualHostableS3Bucket": {
                return new IsVirtualHostableS3Bucket(this);
            }
        }
        throw RuleError.builder().cause((Throwable)((Object)SourceException.builder().message(String.format("`%s` is not a valid function", this.fn)).build())).build();
    }

    public String getId() {
        return this.fn;
    }

    public List<Expr> getArgv() {
        return this.argv;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FnNode fnNode = (FnNode)o;
        if (this.fn != null ? !this.fn.equals(fnNode.fn) : fnNode.fn != null) {
            return false;
        }
        return this.argv != null ? this.argv.equals(fnNode.argv) : fnNode.argv == null;
    }

    public int hashCode() {
        int result = this.fn != null ? this.fn.hashCode() : 0;
        result = 31 * result + (this.argv != null ? this.argv.hashCode() : 0);
        return result;
    }

    public static FnNode fromNode(JsonNode node) {
        Map objNode = node.asObject();
        return FnNode.builder().fn(((JsonNode)objNode.get(FN)).asString()).argv(((JsonNode)objNode.get(ARGV)).asArray().stream().map(Expr::fromNode).collect(Collectors.toList())).build();
    }

    public static class Builder {
        private String fn;
        private List<Expr> argv;

        public Builder argv(List<Expr> argv) {
            this.argv = argv;
            return this;
        }

        public Builder fn(String fn) {
            this.fn = fn;
            return this;
        }

        public FnNode build() {
            return new FnNode(this);
        }
    }
}

