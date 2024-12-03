/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.Immutable
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.http.auth.aws.signer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import software.amazon.awssdk.annotations.Immutable;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
@Immutable
public final class RegionSet {
    public static final RegionSet GLOBAL = RegionSet.create(Collections.singleton("*"));
    private final Set<String> regionSet;
    private final String regionSetString;

    private RegionSet(Collection<String> regions) {
        this.regionSet = Collections.unmodifiableSet(new HashSet<String>(regions));
        this.regionSetString = String.join((CharSequence)",", this.regionSet);
    }

    public String asString() {
        return this.regionSetString;
    }

    public Set<String> asSet() {
        return this.regionSet;
    }

    public static RegionSet create(String value) {
        Validate.notBlank((CharSequence)value, (String)"value must not be blank!", (Object[])new Object[0]);
        return RegionSet.create(Arrays.asList(value.trim().split(",")));
    }

    public static RegionSet create(Collection<String> regions) {
        Validate.notEmpty(regions, (String)"regions must not be empty!", (Object[])new Object[0]);
        return new RegionSet(regions.stream().map(s -> ((String)Validate.notBlank((CharSequence)s, (String)"region must not be empty!", (Object[])new Object[0])).trim()).collect(Collectors.toList()));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RegionSet that = (RegionSet)o;
        return this.regionSet.equals(that.regionSet);
    }

    public int hashCode() {
        return Objects.hashCode(this.regionSet);
    }
}

