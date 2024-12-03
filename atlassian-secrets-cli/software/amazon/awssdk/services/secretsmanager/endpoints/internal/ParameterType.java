/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import java.util.Locale;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;

@SdkInternalApi
public enum ParameterType {
    STRING("String"),
    BOOLEAN("Boolean");

    private final String name;

    private ParameterType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public static ParameterType fromNode(JsonNode node) {
        return ParameterType.fromValue(node.asString());
    }

    public static ParameterType fromValue(String value) {
        switch (value.toLowerCase(Locale.ENGLISH)) {
            case "string": {
                return STRING;
            }
            case "boolean": {
                return BOOLEAN;
            }
        }
        throw SdkClientException.create("Unknown parameter type: " + value);
    }
}

