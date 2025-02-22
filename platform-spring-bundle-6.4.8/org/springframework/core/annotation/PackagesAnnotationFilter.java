/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.annotation;

import java.util.Arrays;
import org.springframework.core.annotation.AnnotationFilter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

final class PackagesAnnotationFilter
implements AnnotationFilter {
    private final String[] prefixes;
    private final int hashCode;

    PackagesAnnotationFilter(String ... packages) {
        Assert.notNull((Object)packages, "Packages array must not be null");
        this.prefixes = new String[packages.length];
        for (int i2 = 0; i2 < packages.length; ++i2) {
            String pkg = packages[i2];
            Assert.hasText(pkg, "Packages array must not have empty elements");
            this.prefixes[i2] = pkg + ".";
        }
        Arrays.sort(this.prefixes);
        this.hashCode = Arrays.hashCode(this.prefixes);
    }

    @Override
    public boolean matches(String annotationType) {
        for (String prefix : this.prefixes) {
            if (!annotationType.startsWith(prefix)) continue;
            return true;
        }
        return false;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        return Arrays.equals(this.prefixes, ((PackagesAnnotationFilter)other).prefixes);
    }

    public int hashCode() {
        return this.hashCode;
    }

    public String toString() {
        return "Packages annotation filter: " + StringUtils.arrayToCommaDelimitedString(this.prefixes);
    }
}

