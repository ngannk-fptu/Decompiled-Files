/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.plugins.rest.common.version;

import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ApiVersion
implements Comparable {
    public static final String NONE_STRING = "none";
    public static final ApiVersion NONE = new ApiVersion("none");
    private static final String DOT = ".";
    private static final Pattern VERSION_PATTERN = Pattern.compile("(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?(?:\\.([\\w-]*))?");
    private final Integer major;
    private final Integer minor;
    private final Integer micro;
    private final String classifier;

    private static RuntimeException defaultVersionExceptionMapping(String version) {
        Objects.requireNonNull(version);
        throw new IllegalArgumentException(version);
    }

    public ApiVersion(String version) {
        this(version, new Function<String, RuntimeException>(){

            @Override
            @Nullable
            public RuntimeException apply(String input) {
                return ApiVersion.defaultVersionExceptionMapping(input);
            }
        });
    }

    protected ApiVersion(String version, Function<String, RuntimeException> exceptionMapper) {
        if (version == null) {
            throw exceptionMapper.apply(version);
        }
        if (NONE_STRING.equals(version)) {
            this.micro = null;
            this.minor = null;
            this.major = null;
            this.classifier = null;
        } else {
            Matcher matcher = VERSION_PATTERN.matcher(version);
            if (!matcher.matches()) {
                throw exceptionMapper.apply(version);
            }
            this.major = Integer.valueOf(matcher.group(1));
            this.minor = matcher.group(2) != null ? Integer.valueOf(matcher.group(2)) : null;
            this.micro = matcher.group(3) != null ? Integer.valueOf(matcher.group(3)) : null;
            this.classifier = matcher.group(4);
        }
    }

    public boolean isNone() {
        return this.equals(NONE);
    }

    public static boolean isNone(String version) {
        return NONE_STRING.equals(version);
    }

    public Integer getMajor() {
        return this.major;
    }

    public Integer getMinor() {
        return this.minor;
    }

    public Integer getMicro() {
        return this.micro;
    }

    public String getClassifier() {
        return this.classifier;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof ApiVersion)) {
            return false;
        }
        ApiVersion version = (ApiVersion)o;
        return new EqualsBuilder().append((Object)this.major, (Object)version.major).append((Object)this.minor, (Object)version.minor).append((Object)this.micro, (Object)version.micro).append((Object)this.classifier, (Object)version.classifier).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(3, 41).append((Object)this.major).append((Object)this.minor).append((Object)this.micro).append((Object)this.classifier).toHashCode();
    }

    public int compareTo(Object o) {
        if (o == null) {
            return 1;
        }
        if (o == this) {
            return 0;
        }
        if (!(o instanceof ApiVersion)) {
            return 1;
        }
        ApiVersion that = (ApiVersion)o;
        int majorDifference = ApiVersion.compare(this.major, that.major);
        if (majorDifference != 0) {
            return majorDifference;
        }
        int minorDifference = ApiVersion.compare(this.minor, that.minor);
        if (minorDifference != 0) {
            return minorDifference;
        }
        int microDifference = ApiVersion.compare(this.micro, that.micro);
        if (microDifference != 0) {
            return microDifference;
        }
        return ApiVersion.compare(this.classifier, that.classifier);
    }

    private static <T extends Comparable<T>> int compare(T n, T m) {
        if (n == null && m == null) {
            return 0;
        }
        if (n == null) {
            return -1;
        }
        if (m == null) {
            return 1;
        }
        return n.compareTo(m);
    }

    public String toString() {
        return this.isNone() ? NONE_STRING : this.major + (this.minor != null ? DOT + this.minor : "") + (this.micro != null ? DOT + this.micro : "") + (StringUtils.isNotBlank((CharSequence)this.classifier) ? DOT + this.classifier : "");
    }
}

