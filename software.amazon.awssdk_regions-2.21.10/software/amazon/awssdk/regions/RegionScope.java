/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.regions;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public final class RegionScope {
    public static final RegionScope GLOBAL;
    private static final Pattern REGION_SCOPE_PATTERN;
    private final String regionScope;

    private RegionScope(String regionScope) {
        this.regionScope = (String)Validate.paramNotBlank((CharSequence)regionScope, (String)"regionScope");
        this.validateFormat(regionScope);
    }

    public String id() {
        return this.regionScope;
    }

    public static RegionScope create(String value) {
        return new RegionScope(value);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RegionScope that = (RegionScope)o;
        return this.regionScope.equals(that.regionScope);
    }

    public int hashCode() {
        return 31 * (1 + (this.regionScope != null ? this.regionScope.hashCode() : 0));
    }

    private void validateFormat(String regionScope) {
        Matcher matcher = REGION_SCOPE_PATTERN.matcher(regionScope);
        if (!matcher.matches()) {
            if (regionScope.contains(",")) {
                throw new IllegalArgumentException("Incorrect region scope '" + regionScope + "'. Region scopes with more than one region defined are not supported.");
            }
            throw new IllegalArgumentException("Incorrect region scope '" + regionScope + "'. Region scope must be a string that either is a complete region string, such as 'us-east-1', or uses the wildcard '*' to represent any region that starts with the preceding parts. Wildcards must appear as a separate segment after a '-' dash, for example 'us-east-*'. A global scope of '*' is allowed.");
        }
        List<String> segments = Arrays.asList(regionScope.split("-"));
        String lastSegment = segments.get(segments.size() - 1);
        if (lastSegment.contains("*") && lastSegment.length() != 1) {
            throw new IllegalArgumentException("Incorrect region scope '" + regionScope + "'. A wildcard must only appear on its own at the end of the expression after a '-' dash. A global scope of '*' is allowed.");
        }
    }

    static {
        REGION_SCOPE_PATTERN = Pattern.compile("^([a-z0-9-])*([*]?)$");
        GLOBAL = RegionScope.create("*");
    }
}

