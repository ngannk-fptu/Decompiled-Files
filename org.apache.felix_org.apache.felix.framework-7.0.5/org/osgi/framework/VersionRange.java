/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.framework;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import org.osgi.framework.Version;

public class VersionRange {
    public static final char LEFT_OPEN = '(';
    public static final char LEFT_CLOSED = '[';
    public static final char RIGHT_OPEN = ')';
    public static final char RIGHT_CLOSED = ']';
    private final boolean leftClosed;
    private final Version left;
    private final Version right;
    private final boolean rightClosed;
    private final boolean empty;
    private transient String versionRangeString;
    private transient int hash;
    private static final String LEFT_OPEN_DELIMITER = "(";
    private static final String LEFT_CLOSED_DELIMITER = "[";
    private static final String LEFT_DELIMITERS = "[(";
    private static final String RIGHT_OPEN_DELIMITER = ")";
    private static final String RIGHT_CLOSED_DELIMITER = "]";
    private static final String RIGHT_DELIMITERS = ")]";
    private static final String ENDPOINT_DELIMITER = ",";

    public VersionRange(char leftType, Version leftEndpoint, Version rightEndpoint, char rightType) {
        if (leftType != '[' && leftType != '(') {
            throw new IllegalArgumentException("invalid leftType \"" + leftType + "\"");
        }
        if (rightType != ')' && rightType != ']') {
            throw new IllegalArgumentException("invalid rightType \"" + rightType + "\"");
        }
        if (leftEndpoint == null) {
            throw new IllegalArgumentException("null leftEndpoint argument");
        }
        this.leftClosed = leftType == '[';
        this.rightClosed = rightType == ']';
        this.left = leftEndpoint;
        this.right = rightEndpoint;
        this.empty = this.isEmpty0();
    }

    public VersionRange(String range) {
        Version endpointRight;
        boolean closedRight;
        Version endpointLeft;
        boolean closedLeft;
        try {
            StringTokenizer st = new StringTokenizer(range, LEFT_DELIMITERS, true);
            String token = st.nextToken().trim();
            if (token.length() == 0) {
                token = st.nextToken();
            }
            if (!(closedLeft = LEFT_CLOSED_DELIMITER.equals(token)) && !LEFT_OPEN_DELIMITER.equals(token)) {
                if (st.hasMoreTokens()) {
                    throw new IllegalArgumentException("invalid range \"" + range + "\": invalid format");
                }
                this.leftClosed = true;
                this.rightClosed = false;
                this.left = VersionRange.parseVersion(token, range);
                this.right = null;
                this.empty = false;
                return;
            }
            String version = st.nextToken(ENDPOINT_DELIMITER);
            endpointLeft = VersionRange.parseVersion(version, range);
            token = st.nextToken();
            version = st.nextToken(RIGHT_DELIMITERS);
            token = st.nextToken();
            closedRight = RIGHT_CLOSED_DELIMITER.equals(token);
            if (!closedRight && !RIGHT_OPEN_DELIMITER.equals(token)) {
                throw new IllegalArgumentException("invalid range \"" + range + "\": invalid format");
            }
            endpointRight = VersionRange.parseVersion(version, range);
            if (st.hasMoreTokens() && (token = st.nextToken("").trim()).length() != 0) {
                throw new IllegalArgumentException("invalid range \"" + range + "\": invalid format");
            }
        }
        catch (NoSuchElementException e) {
            throw new IllegalArgumentException("invalid range \"" + range + "\": invalid format", e);
        }
        this.leftClosed = closedLeft;
        this.rightClosed = closedRight;
        this.left = endpointLeft;
        this.right = endpointRight;
        this.empty = this.isEmpty0();
    }

    private static Version parseVersion(String version, String range) {
        try {
            return Version.valueOf(version);
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("invalid range \"" + range + "\": " + e.getMessage(), e);
        }
    }

    public Version getLeft() {
        return this.left;
    }

    public Version getRight() {
        return this.right;
    }

    public char getLeftType() {
        return this.leftClosed ? (char)'[' : '(';
    }

    public char getRightType() {
        return this.rightClosed ? (char)']' : ')';
    }

    public boolean includes(Version version) {
        if (this.empty) {
            return false;
        }
        if (this.left.compareTo(version) >= (this.leftClosed ? 1 : 0)) {
            return false;
        }
        if (this.right == null) {
            return true;
        }
        return this.right.compareTo(version) >= (this.rightClosed ? 0 : 1);
    }

    public VersionRange intersection(VersionRange ... ranges) {
        if (ranges == null || ranges.length == 0) {
            return this;
        }
        boolean closedLeft = this.leftClosed;
        boolean closedRight = this.rightClosed;
        Version endpointLeft = this.left;
        Version endpointRight = this.right;
        for (VersionRange range : ranges) {
            int comparison = endpointLeft.compareTo(range.left);
            if (comparison == 0) {
                closedLeft = closedLeft && range.leftClosed;
            } else if (comparison < 0) {
                endpointLeft = range.left;
                closedLeft = range.leftClosed;
            }
            if (range.right == null) continue;
            if (endpointRight == null) {
                endpointRight = range.right;
                closedRight = range.rightClosed;
                continue;
            }
            comparison = endpointRight.compareTo(range.right);
            if (comparison == 0) {
                closedRight = closedRight && range.rightClosed;
                continue;
            }
            if (comparison <= 0) continue;
            endpointRight = range.right;
            closedRight = range.rightClosed;
        }
        return new VersionRange(closedLeft ? (char)'[' : '(', endpointLeft, endpointRight, closedRight ? (char)']' : ')');
    }

    public boolean isEmpty() {
        return this.empty;
    }

    private boolean isEmpty0() {
        if (this.right == null) {
            return false;
        }
        int comparison = this.left.compareTo(this.right);
        if (comparison == 0) {
            return !this.leftClosed || !this.rightClosed;
        }
        return comparison > 0;
    }

    public boolean isExact() {
        if (this.empty || this.right == null) {
            return false;
        }
        if (this.leftClosed) {
            if (this.rightClosed) {
                return this.left.equals(this.right);
            }
            Version adjacent1 = new Version(this.left.getMajor(), this.left.getMinor(), this.left.getMicro(), this.left.getQualifier() + "-");
            return adjacent1.compareTo(this.right) >= 0;
        }
        if (this.rightClosed) {
            Version adjacent1 = new Version(this.left.getMajor(), this.left.getMinor(), this.left.getMicro(), this.left.getQualifier() + "-");
            return adjacent1.equals(this.right);
        }
        Version adjacent2 = new Version(this.left.getMajor(), this.left.getMinor(), this.left.getMicro(), this.left.getQualifier() + "--");
        return adjacent2.compareTo(this.right) >= 0;
    }

    public String toString() {
        String s = this.versionRangeString;
        if (s != null) {
            return s;
        }
        String leftVersion = this.left.toString();
        if (this.right == null) {
            StringBuilder result = new StringBuilder(leftVersion.length() + 1);
            result.append(this.left.toString0());
            this.versionRangeString = result.toString();
            return this.versionRangeString;
        }
        String rightVerion = this.right.toString();
        StringBuilder result = new StringBuilder(leftVersion.length() + rightVerion.length() + 5);
        result.append(this.leftClosed ? (char)'[' : '(');
        result.append(this.left.toString0());
        result.append(ENDPOINT_DELIMITER);
        result.append(this.right.toString0());
        result.append(this.rightClosed ? (char)']' : ')');
        this.versionRangeString = result.toString();
        return this.versionRangeString;
    }

    public int hashCode() {
        int h = this.hash;
        if (h != 0) {
            return h;
        }
        if (this.empty) {
            this.hash = 31;
            return 31;
        }
        h = 31 + (this.leftClosed ? 7 : 5);
        h = 31 * h + this.left.hashCode();
        if (this.right != null) {
            h = 31 * h + this.right.hashCode();
            h = 31 * h + (this.rightClosed ? 7 : 5);
        }
        this.hash = h;
        return this.hash;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof VersionRange)) {
            return false;
        }
        VersionRange other = (VersionRange)object;
        if (this.empty && other.empty) {
            return true;
        }
        if (this.right == null) {
            return this.leftClosed == other.leftClosed && other.right == null && this.left.equals(other.left);
        }
        return this.leftClosed == other.leftClosed && this.rightClosed == other.rightClosed && this.left.equals(other.left) && this.right.equals(other.right);
    }

    public String toFilterString(String attributeName) {
        boolean multipleTerms;
        if (attributeName.length() == 0) {
            throw new IllegalArgumentException("invalid attributeName \"" + attributeName + "\"");
        }
        for (char ch : attributeName.toCharArray()) {
            if (ch != '=' && ch != '>' && ch != '<' && ch != '~' && ch != '(' && ch != ')') continue;
            throw new IllegalArgumentException("invalid attributeName \"" + attributeName + "\"");
        }
        StringBuilder result = new StringBuilder(128);
        boolean needPresence = !this.leftClosed && (this.right == null || !this.rightClosed);
        boolean bl = multipleTerms = needPresence || this.right != null;
        if (multipleTerms) {
            result.append("(&");
        }
        if (needPresence) {
            result.append('(');
            result.append(attributeName);
            result.append("=*)");
        }
        if (this.leftClosed) {
            result.append('(');
            result.append(attributeName);
            result.append(">=");
            result.append(this.left.toString0());
            result.append(')');
        } else {
            result.append("(!(");
            result.append(attributeName);
            result.append("<=");
            result.append(this.left.toString0());
            result.append("))");
        }
        if (this.right != null) {
            if (this.rightClosed) {
                result.append('(');
                result.append(attributeName);
                result.append("<=");
                result.append(this.right.toString0());
                result.append(')');
            } else {
                result.append("(!(");
                result.append(attributeName);
                result.append(">=");
                result.append(this.right.toString0());
                result.append("))");
            }
        }
        if (multipleTerms) {
            result.append(')');
        }
        return result.toString();
    }

    public static VersionRange valueOf(String range) {
        return new VersionRange(range);
    }
}

