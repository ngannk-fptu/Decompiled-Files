/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth.policy.conditions;

import com.amazonaws.auth.policy.Condition;
import java.util.Arrays;

public class IpAddressCondition
extends Condition {
    public IpAddressCondition(String ipAddressRange) {
        this(IpAddressComparisonType.IpAddress, ipAddressRange);
    }

    public IpAddressCondition(IpAddressComparisonType type, String ipAddressRange) {
        this.type = type.toString();
        this.conditionKey = "aws:SourceIp";
        this.values = Arrays.asList(ipAddressRange);
    }

    public static enum IpAddressComparisonType {
        IpAddress,
        NotIpAddress;

    }
}

