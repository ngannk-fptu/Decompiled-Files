/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import com.amazonaws.util.ComparableUtils;
import com.amazonaws.util.NumberUtils;
import com.amazonaws.util.StringUtils;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaVersionParser {
    public static final String JAVA_VERSION_PROPERTY = "java.version";
    private static String MAJOR_VERSION_FAMILY_PATTERN = "\\d+";
    private static String MAJOR_VERSION_PATTERN = "\\d+";
    private static String MAINTENANCE_NUMBER_PATTERN = "\\d+";
    private static String UPDATE_NUMBER_PATTERN = "\\d+";
    private static Pattern VERSION_REGEX = Pattern.compile(String.format("(%s)\\.(%s)\\.(%s)(?:_(%s))?.*", MAJOR_VERSION_FAMILY_PATTERN, MAJOR_VERSION_PATTERN, MAINTENANCE_NUMBER_PATTERN, UPDATE_NUMBER_PATTERN));
    private static final JavaVersion currentJavaVersion = JavaVersionParser.parseJavaVersion(System.getProperty("java.version"));

    private JavaVersionParser() {
    }

    public static JavaVersion getCurrentJavaVersion() {
        return currentJavaVersion;
    }

    public static JavaVersion parseJavaVersion(String fullVersionString) {
        Matcher matcher;
        if (!StringUtils.isNullOrEmpty(fullVersionString) && (matcher = VERSION_REGEX.matcher(fullVersionString)).matches()) {
            Integer majorVersionFamily = NumberUtils.tryParseInt(matcher.group(1));
            Integer majorVersion = NumberUtils.tryParseInt(matcher.group(2));
            Integer maintenanceNumber = NumberUtils.tryParseInt(matcher.group(3));
            Integer updateNumber = NumberUtils.tryParseInt(matcher.group(4));
            return new JavaVersion(majorVersionFamily, majorVersion, maintenanceNumber, updateNumber);
        }
        return JavaVersion.UNKNOWN;
    }

    public static enum KnownJavaVersions {
        JAVA_6(1, 6),
        JAVA_7(1, 7),
        JAVA_8(1, 8),
        JAVA_9(1, 9),
        UNKNOWN(0, -1);

        private Integer knownMajorVersionFamily;
        private Integer knownMajorVersion;

        private KnownJavaVersions(int majorVersionFamily, int majorVersion) {
            this.knownMajorVersionFamily = majorVersionFamily;
            this.knownMajorVersion = majorVersion;
        }

        public static KnownJavaVersions fromMajorVersion(Integer majorVersionFamily, Integer majorVersion) {
            for (KnownJavaVersions version : KnownJavaVersions.values()) {
                if (!version.isMajorVersion(majorVersionFamily, majorVersion)) continue;
                return version;
            }
            return UNKNOWN;
        }

        private boolean isMajorVersion(Integer majorVersionFamily, Integer majorVersion) {
            return this.knownMajorVersionFamily.equals(majorVersionFamily) && this.knownMajorVersion.equals(majorVersion);
        }
    }

    public static final class JavaVersion
    implements Comparable<JavaVersion> {
        public static final JavaVersion UNKNOWN = new JavaVersion(null, null, null, null);
        private final Integer[] tokenizedVersion;
        private final Integer majorVersionFamily;
        private final Integer majorVersion;
        private final Integer maintenanceNumber;
        private final Integer updateNumber;
        private final KnownJavaVersions knownVersion;

        public JavaVersion(Integer majorVersionFamily, Integer majorVersion, Integer maintenanceNumber, Integer updateNumber) {
            this.majorVersionFamily = majorVersionFamily;
            this.majorVersion = majorVersion;
            this.maintenanceNumber = maintenanceNumber;
            this.updateNumber = updateNumber;
            this.knownVersion = KnownJavaVersions.fromMajorVersion(majorVersionFamily, majorVersion);
            this.tokenizedVersion = this.getTokenizedVersion();
        }

        private Integer[] getTokenizedVersion() {
            return new Integer[]{this.majorVersionFamily, this.majorVersion, this.maintenanceNumber, this.updateNumber};
        }

        public Integer getMajorVersionFamily() {
            return this.majorVersionFamily;
        }

        public Integer getMajorVersion() {
            return this.majorVersion;
        }

        public String getMajorVersionString() {
            return String.format("%d.%d", this.majorVersionFamily, this.majorVersion);
        }

        public Integer getMaintenanceNumber() {
            return this.maintenanceNumber;
        }

        public Integer getUpdateNumber() {
            return this.updateNumber;
        }

        public KnownJavaVersions getKnownVersion() {
            return this.knownVersion;
        }

        @Override
        public int compareTo(JavaVersion other) {
            for (int i = 0; i < this.tokenizedVersion.length; ++i) {
                int tokenComparison = ComparableUtils.safeCompare(this.tokenizedVersion[i], other.tokenizedVersion[i]);
                if (tokenComparison == 0) continue;
                return tokenComparison;
            }
            return 0;
        }

        public int hashCode() {
            int prime = 31;
            int result = 1;
            result = 31 * result + (this.knownVersion == null ? 0 : this.knownVersion.hashCode());
            result = 31 * result + (this.maintenanceNumber == null ? 0 : this.maintenanceNumber.hashCode());
            result = 31 * result + (this.majorVersion == null ? 0 : this.majorVersion.hashCode());
            result = 31 * result + (this.majorVersionFamily == null ? 0 : this.majorVersionFamily.hashCode());
            result = 31 * result + Arrays.hashCode((Object[])this.tokenizedVersion);
            result = 31 * result + (this.updateNumber == null ? 0 : this.updateNumber.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            JavaVersion other = (JavaVersion)obj;
            if (this.knownVersion != other.knownVersion) {
                return false;
            }
            if (this.maintenanceNumber == null ? other.maintenanceNumber != null : !this.maintenanceNumber.equals(other.maintenanceNumber)) {
                return false;
            }
            if (this.majorVersion == null ? other.majorVersion != null : !this.majorVersion.equals(other.majorVersion)) {
                return false;
            }
            if (this.majorVersionFamily == null ? other.majorVersionFamily != null : !this.majorVersionFamily.equals(other.majorVersionFamily)) {
                return false;
            }
            if (!Arrays.equals((Object[])this.tokenizedVersion, (Object[])other.tokenizedVersion)) {
                return false;
            }
            return !(this.updateNumber == null ? other.updateNumber != null : !this.updateNumber.equals(other.updateNumber));
        }
    }
}

