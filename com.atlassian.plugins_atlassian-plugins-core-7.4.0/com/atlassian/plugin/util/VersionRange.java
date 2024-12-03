/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.plugin.util;

import com.atlassian.plugin.util.VersionStringComparator;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class VersionRange {
    private static final Pattern RANGE_PATTERN = Pattern.compile("(\\(|\\[)?([\\d\\w]+(?:[\\.-][\\d\\w]+)*)?(?:,([\\d\\w]+(?:[\\.-][\\d\\w]+)*)?)?(\\)|\\])?");

    private VersionRange() {
    }

    abstract boolean isInRange(String var1);

    public VersionRange or(VersionRange other) {
        return new OrVersionRange(this, other);
    }

    public static VersionRange empty() {
        return new EmptyVersionRange();
    }

    public static VersionRange all() {
        return new AllVersionRange();
    }

    public static VersionRange parse(String range) {
        ActualVersionRangeBuilder builder;
        Matcher matcher = RANGE_PATTERN.matcher(range);
        Preconditions.checkState((boolean)matcher.matches(), (Object)("Range '" + range + "' doesn't match pattern " + RANGE_PATTERN.pattern()));
        String leftParenthesis = matcher.group(1);
        String leftVersion = matcher.group(2);
        String rightVersion = matcher.group(3);
        String rightParenthesis = matcher.group(4);
        Preconditions.checkState((leftVersion != null || rightVersion != null ? 1 : 0) != 0, (Object)"No version configured for range!");
        if (leftParenthesis == null) {
            Preconditions.checkState((leftVersion != null ? 1 : 0) != 0);
            Preconditions.checkState((rightParenthesis == null ? 1 : 0) != 0);
            Preconditions.checkState((rightVersion == null ? 1 : 0) != 0);
            return VersionRange.include(leftVersion).unbounded();
        }
        if (leftParenthesis.equals("[") && rightParenthesis.equals("]") && rightVersion == null) {
            return VersionRange.single(leftVersion);
        }
        if (leftParenthesis.equals("[")) {
            Preconditions.checkState((leftVersion != null ? 1 : 0) != 0);
            builder = VersionRange.include(leftVersion);
        } else if (leftParenthesis.equals("(")) {
            builder = leftVersion != null ? VersionRange.exclude(leftVersion) : VersionRange.unbounded();
        } else {
            throw new IllegalStateException("Incorrect start of range! " + leftParenthesis);
        }
        if (rightParenthesis.equals("]")) {
            Preconditions.checkState((rightVersion != null ? 1 : 0) != 0);
            return builder.include(rightVersion);
        }
        if (rightParenthesis.equals(")")) {
            if (rightVersion != null) {
                return builder.exclude(rightVersion);
            }
            return builder.unbounded();
        }
        throw new IllegalStateException("Incorrect ent of range! " + rightParenthesis);
    }

    public static VersionRange single(String version) {
        return new SingleVersionRange(version);
    }

    public static ActualVersionRangeBuilder include(String version) {
        return new ActualVersionRangeBuilder(true, version);
    }

    public static ActualVersionRangeBuilder exclude(String version) {
        return new ActualVersionRangeBuilder(false, version);
    }

    public static ActualVersionRangeBuilder unbounded() {
        return new ActualVersionRangeBuilder(true, null);
    }

    private static boolean isLowerThan(String version, String rightVersion, boolean rightIncluded) {
        int rightCompare = VersionRange.newVersionComparator().compare(rightVersion, version);
        return rightCompare > 0 || rightIncluded && rightCompare == 0;
    }

    private static boolean isGreaterThan(boolean leftIncluded, String leftVersion, String version) {
        int leftCompare = VersionRange.newVersionComparator().compare(version, leftVersion);
        return leftCompare > 0 || leftIncluded && leftCompare == 0;
    }

    private static Comparator<String> newVersionComparator() {
        return new VersionStringComparator();
    }

    private static final class OrVersionRange
    extends VersionRange {
        private final VersionRange or1;
        private final VersionRange or2;

        private OrVersionRange(VersionRange or1, VersionRange or2) {
            this.or1 = (VersionRange)Preconditions.checkNotNull((Object)or1);
            this.or2 = (VersionRange)Preconditions.checkNotNull((Object)or2);
        }

        @Override
        boolean isInRange(String v) {
            return this.or1.isInRange(v) || this.or2.isInRange(v);
        }

        public int hashCode() {
            return Objects.hash(this.or1, this.or2);
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            OrVersionRange that = (OrVersionRange)obj;
            return Objects.equals(this.or1, that.or1) && Objects.equals(this.or2, that.or2);
        }

        public String toString() {
            return this.or1 + "," + this.or2;
        }
    }

    private static final class EmptyVersionRange
    extends VersionRange {
        private EmptyVersionRange() {
        }

        @Override
        boolean isInRange(String version) {
            return false;
        }

        public int hashCode() {
            return 2;
        }

        public boolean equals(Object obj) {
            return obj != null && this.getClass() == obj.getClass();
        }

        public String toString() {
            return "()";
        }
    }

    private static final class AllVersionRange
    extends VersionRange {
        private AllVersionRange() {
        }

        @Override
        boolean isInRange(String version) {
            return true;
        }

        public int hashCode() {
            return 1;
        }

        public boolean equals(Object obj) {
            return obj != null && this.getClass() == obj.getClass();
        }

        public String toString() {
            return "(,)";
        }
    }

    private static final class RightUnboundedVersionRange
    extends VersionRange {
        private final boolean leftIncluded;
        private final String leftVersion;

        private RightUnboundedVersionRange(boolean leftIncluded, String leftVersion) {
            this.leftIncluded = leftIncluded;
            this.leftVersion = (String)Preconditions.checkNotNull((Object)leftVersion);
        }

        @Override
        boolean isInRange(String v) {
            return VersionRange.isGreaterThan(this.leftIncluded, this.leftVersion, v);
        }

        public int hashCode() {
            return Objects.hash(this.leftIncluded, this.leftVersion);
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            RightUnboundedVersionRange that = (RightUnboundedVersionRange)obj;
            return Objects.equals(this.leftIncluded, that.leftIncluded) && Objects.equals(this.leftVersion, that.leftVersion);
        }

        public String toString() {
            return (this.leftIncluded ? "[" : "(") + this.leftVersion + ",)";
        }
    }

    private static final class LeftUnboundedVersionRange
    extends VersionRange {
        private final boolean rightIncluded;
        private final String rightVersion;

        private LeftUnboundedVersionRange(boolean rightIncluded, String rightVersion) {
            this.rightIncluded = rightIncluded;
            this.rightVersion = (String)Preconditions.checkNotNull((Object)rightVersion);
        }

        @Override
        boolean isInRange(String v) {
            return VersionRange.isLowerThan(v, this.rightVersion, this.rightIncluded);
        }

        public int hashCode() {
            return Objects.hash(this.rightIncluded, this.rightVersion);
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            LeftUnboundedVersionRange that = (LeftUnboundedVersionRange)obj;
            return Objects.equals(this.rightIncluded, that.rightIncluded) && Objects.equals(this.rightVersion, that.rightVersion);
        }

        public String toString() {
            return "(," + this.rightVersion + (this.rightIncluded ? "]" : ")");
        }
    }

    private static final class ActualVersionRange
    extends VersionRange {
        private final boolean leftIncluded;
        private final String leftVersion;
        private final boolean rightIncluded;
        private final String rightVersion;

        private ActualVersionRange(boolean leftIncluded, String leftVersion, boolean rightIncluded, String rightVersion) {
            this.leftIncluded = leftIncluded;
            this.leftVersion = (String)Preconditions.checkNotNull((Object)leftVersion);
            this.rightIncluded = rightIncluded;
            this.rightVersion = (String)Preconditions.checkNotNull((Object)rightVersion);
        }

        @Override
        boolean isInRange(String v) {
            return VersionRange.isGreaterThan(this.leftIncluded, this.leftVersion, v) && VersionRange.isLowerThan(v, this.rightVersion, this.rightIncluded);
        }

        public int hashCode() {
            return Objects.hash(this.leftIncluded, this.leftVersion, this.rightIncluded, this.rightVersion);
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            ActualVersionRange that = (ActualVersionRange)obj;
            return Objects.equals(this.leftIncluded, that.leftIncluded) && Objects.equals(this.leftVersion, that.leftVersion) && Objects.equals(this.rightIncluded, that.rightIncluded) && Objects.equals(this.rightVersion, that.rightVersion);
        }

        public String toString() {
            return (this.leftIncluded ? "[" : "(") + this.leftVersion + "," + this.rightVersion + (this.rightIncluded ? "]" : ")");
        }
    }

    public static final class ActualVersionRangeBuilder {
        private final boolean leftIncluded;
        private final String leftVersion;

        public ActualVersionRangeBuilder(boolean leftIncluded, String leftVersion) {
            this.leftIncluded = leftIncluded;
            this.leftVersion = leftVersion;
        }

        public VersionRange include(String version) {
            return this.newRange(version, true);
        }

        public VersionRange exclude(String version) {
            return this.newRange(version, false);
        }

        private VersionRange newRange(String version, boolean rightIncluded) {
            if (this.leftVersion != null) {
                return this.newActualRange(version, rightIncluded);
            }
            return this.newLeftUnboundedRange(version, rightIncluded);
        }

        private LeftUnboundedVersionRange newLeftUnboundedRange(String version, boolean rightIncluded) {
            return new LeftUnboundedVersionRange(rightIncluded, version);
        }

        private ActualVersionRange newActualRange(String version, boolean rightIncluded) {
            return new ActualVersionRange(this.leftIncluded, this.leftVersion, rightIncluded, version);
        }

        public VersionRange unbounded() {
            if (this.leftVersion == null) {
                throw new IllegalStateException();
            }
            return new RightUnboundedVersionRange(this.leftIncluded, this.leftVersion);
        }
    }

    private static class SingleVersionRange
    extends VersionRange {
        private final String version;

        private SingleVersionRange(String version) {
            this.version = (String)Preconditions.checkNotNull((Object)version);
        }

        @Override
        boolean isInRange(String v) {
            return VersionRange.newVersionComparator().compare(this.version, v) == 0;
        }

        public int hashCode() {
            return Objects.hash(this.version);
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            SingleVersionRange that = (SingleVersionRange)obj;
            return Objects.equals(this.version, that.version);
        }

        public String toString() {
            return "[" + this.version + "]";
        }
    }
}

