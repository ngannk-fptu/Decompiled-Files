/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.protocols.jsoncore.JsonNode
 */
package software.amazon.awssdk.services.sts.endpoints.internal;

import java.util.Map;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;

@SdkInternalApi
public class Outputs {
    private static final String DNS_SUFFIX = "dnsSuffix";
    private static final String DUAL_STACK_DNS_SUFFIX = "dualStackDnsSuffix";
    private static final String SUPPORTS_FIPS = "supportsFIPS";
    private static final String SUPPORTS_DUAL_STACK = "supportsDualStack";
    private final String dnsSuffix;
    private final String dualStackDnsSuffix;
    private final boolean supportsFips;
    private final boolean supportsDualStack;

    private Outputs(Builder builder) {
        this.dnsSuffix = builder.dnsSuffix;
        this.dualStackDnsSuffix = builder.dualStackDnsSuffix;
        this.supportsFips = builder.supportsFips;
        this.supportsDualStack = builder.supportsDualStack;
    }

    public String dnsSuffix() {
        return this.dnsSuffix;
    }

    public String dualStackDnsSuffix() {
        return this.dualStackDnsSuffix;
    }

    public boolean supportsFips() {
        return this.supportsFips;
    }

    public boolean supportsDualStack() {
        return this.supportsDualStack;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Outputs outputs = (Outputs)o;
        if (this.supportsFips != outputs.supportsFips) {
            return false;
        }
        if (this.supportsDualStack != outputs.supportsDualStack) {
            return false;
        }
        if (this.dnsSuffix != null ? !this.dnsSuffix.equals(outputs.dnsSuffix) : outputs.dnsSuffix != null) {
            return false;
        }
        return this.dualStackDnsSuffix != null ? this.dualStackDnsSuffix.equals(outputs.dualStackDnsSuffix) : outputs.dualStackDnsSuffix == null;
    }

    public int hashCode() {
        int result = this.dnsSuffix != null ? this.dnsSuffix.hashCode() : 0;
        result = 31 * result + (this.dualStackDnsSuffix != null ? this.dualStackDnsSuffix.hashCode() : 0);
        result = 31 * result + (this.supportsFips ? 1 : 0);
        result = 31 * result + (this.supportsDualStack ? 1 : 0);
        return result;
    }

    public String toString() {
        return "Outputs{dnsSuffix='" + this.dnsSuffix + '\'' + ", dualStackDnsSuffix='" + this.dualStackDnsSuffix + '\'' + ", supportsFips=" + this.supportsFips + ", supportsDualStack=" + this.supportsDualStack + '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Outputs fromNode(JsonNode node) {
        JsonNode supportsDualStack;
        JsonNode supportsFips;
        JsonNode dualStackDnsSuffix;
        Map objNode = node.asObject();
        Builder b = Outputs.builder();
        JsonNode dnsSuffix = (JsonNode)objNode.get(DNS_SUFFIX);
        if (dnsSuffix != null) {
            b.dnsSuffix(dnsSuffix.asString());
        }
        if ((dualStackDnsSuffix = (JsonNode)objNode.get(DUAL_STACK_DNS_SUFFIX)) != null) {
            b.dualStackDnsSuffix(dualStackDnsSuffix.asString());
        }
        if ((supportsFips = (JsonNode)objNode.get(SUPPORTS_FIPS)) != null) {
            b.supportsFips(supportsFips.asBoolean());
        }
        if ((supportsDualStack = (JsonNode)objNode.get(SUPPORTS_DUAL_STACK)) != null) {
            b.supportsDualStack(supportsDualStack.asBoolean());
        }
        return b.build();
    }

    public static class Builder {
        private String dnsSuffix;
        private String dualStackDnsSuffix;
        private boolean supportsFips;
        private boolean supportsDualStack;

        public Builder dnsSuffix(String dnsSuffix) {
            this.dnsSuffix = dnsSuffix;
            return this;
        }

        public Builder dualStackDnsSuffix(String dualStackDnsSuffix) {
            this.dualStackDnsSuffix = dualStackDnsSuffix;
            return this;
        }

        public Builder supportsFips(boolean supportsFips) {
            this.supportsFips = supportsFips;
            return this;
        }

        public Builder supportsDualStack(boolean supportsDualStack) {
            this.supportsDualStack = supportsDualStack;
            return this;
        }

        public Outputs build() {
            return new Outputs(this);
        }
    }
}

