/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.utils.internal.EnumUtils
 */
package software.amazon.awssdk.services.s3.model;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import software.amazon.awssdk.utils.internal.EnumUtils;

public enum BucketLocationConstraint {
    AF_SOUTH_1("af-south-1"),
    AP_EAST_1("ap-east-1"),
    AP_NORTHEAST_1("ap-northeast-1"),
    AP_NORTHEAST_2("ap-northeast-2"),
    AP_NORTHEAST_3("ap-northeast-3"),
    AP_SOUTH_1("ap-south-1"),
    AP_SOUTHEAST_1("ap-southeast-1"),
    AP_SOUTHEAST_2("ap-southeast-2"),
    AP_SOUTHEAST_3("ap-southeast-3"),
    CA_CENTRAL_1("ca-central-1"),
    CN_NORTH_1("cn-north-1"),
    CN_NORTHWEST_1("cn-northwest-1"),
    EU("EU"),
    EU_CENTRAL_1("eu-central-1"),
    EU_NORTH_1("eu-north-1"),
    EU_SOUTH_1("eu-south-1"),
    EU_WEST_1("eu-west-1"),
    EU_WEST_2("eu-west-2"),
    EU_WEST_3("eu-west-3"),
    ME_SOUTH_1("me-south-1"),
    SA_EAST_1("sa-east-1"),
    US_EAST_2("us-east-2"),
    US_GOV_EAST_1("us-gov-east-1"),
    US_GOV_WEST_1("us-gov-west-1"),
    US_WEST_1("us-west-1"),
    US_WEST_2("us-west-2"),
    AP_SOUTH_2("ap-south-2"),
    EU_SOUTH_2("eu-south-2"),
    UNKNOWN_TO_SDK_VERSION(null);

    private static final Map<String, BucketLocationConstraint> VALUE_MAP;
    private final String value;

    private BucketLocationConstraint(String value) {
        this.value = value;
    }

    public String toString() {
        return String.valueOf(this.value);
    }

    public static BucketLocationConstraint fromValue(String value) {
        if (value == null) {
            return null;
        }
        return VALUE_MAP.getOrDefault(value, UNKNOWN_TO_SDK_VERSION);
    }

    public static Set<BucketLocationConstraint> knownValues() {
        EnumSet<BucketLocationConstraint> knownValues = EnumSet.allOf(BucketLocationConstraint.class);
        knownValues.remove((Object)UNKNOWN_TO_SDK_VERSION);
        return knownValues;
    }

    static {
        VALUE_MAP = EnumUtils.uniqueIndex(BucketLocationConstraint.class, BucketLocationConstraint::toString);
    }
}

