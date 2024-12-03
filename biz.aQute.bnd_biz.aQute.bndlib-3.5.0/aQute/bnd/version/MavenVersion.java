/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.version;

import aQute.bnd.version.Version;
import aQute.bnd.version.VersionRange;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MavenVersion
implements Comparable<MavenVersion> {
    static Pattern fuzzyVersion = Pattern.compile("(\\d+)(\\.(\\d+)(\\.(\\d+))?)?([^a-zA-Z0-9](.*))?", 32);
    static Pattern fuzzyVersionRange = Pattern.compile("(\\(|\\[)\\s*([-\\da-zA-Z.]+)\\s*,\\s*([-\\da-zA-Z.]+)\\s*(\\]|\\))", 32);
    static Pattern fuzzyModifier = Pattern.compile("(\\d+[.-])*(.*)", 32);
    public static final String VERSION_STRING = "(\\d{1,15})(\\.(\\d{1,9})(\\.(\\d{1,9}))?)?([-\\.]?([-_\\.\\da-zA-Z]+))?";
    static final SimpleDateFormat snapshotTimestamp = new SimpleDateFormat("yyyyMMdd.HHmmss", Locale.ROOT);
    public static final Pattern VERSIONRANGE = Pattern.compile("((\\(|\\[)(\\d{1,15})(\\.(\\d{1,9})(\\.(\\d{1,9}))?)?([-\\.]?([-_\\.\\da-zA-Z]+))?,(\\d{1,15})(\\.(\\d{1,9})(\\.(\\d{1,9}))?)?([-\\.]?([-_\\.\\da-zA-Z]+))?(\\]|\\)))|(\\d{1,15})(\\.(\\d{1,9})(\\.(\\d{1,9}))?)?([-\\.]?([-_\\.\\da-zA-Z]+))?");
    private static final Pattern VERSION;
    public static MavenVersion UNRESOLVED;
    static final String SNAPSHOT = "SNAPSHOT";
    public static final MavenVersion HIGHEST;
    public static final MavenVersion LOWEST;
    private final Version version;
    private final String literal;
    private final boolean snapshot;

    public MavenVersion(Version osgiVersion) {
        this.version = osgiVersion;
        String qual = "";
        if (this.version.qualifier != null) {
            qual = qual + "-" + this.version.qualifier;
        }
        this.literal = osgiVersion.getWithoutQualifier().toString() + qual;
        this.snapshot = osgiVersion.isSnapshot();
    }

    public MavenVersion(String maven) {
        this.version = new Version(MavenVersion.cleanupVersion(maven));
        this.literal = maven;
        this.snapshot = maven.endsWith("-SNAPSHOT");
    }

    public static final MavenVersion parseString(String versionStr) {
        if (versionStr == null) {
            versionStr = "0";
        } else if ((versionStr = versionStr.trim()).isEmpty()) {
            versionStr = "0";
        }
        Matcher m = VERSION.matcher(versionStr);
        if (!m.matches()) {
            throw new IllegalArgumentException("Invalid syntax for version: " + versionStr);
        }
        int major = Integer.parseInt(m.group(1));
        int minor = m.group(3) != null ? Integer.parseInt(m.group(3)) : 0;
        int micro = m.group(5) != null ? Integer.parseInt(m.group(5)) : 0;
        String qualifier = m.group(7);
        Version version = new Version(major, minor, micro, qualifier);
        return new MavenVersion(version);
    }

    public static final MavenVersion parseMavenString(String versionStr) {
        try {
            return new MavenVersion(versionStr);
        }
        catch (Exception e) {
            return null;
        }
    }

    public Version getOSGiVersion() {
        return this.version;
    }

    public boolean isSnapshot() {
        return this.snapshot;
    }

    @Override
    public int compareTo(MavenVersion other) {
        return this.version.compareTo(other.version);
    }

    public String toString() {
        return this.literal;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.literal == null ? 0 : this.literal.hashCode());
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
        MavenVersion other = (MavenVersion)obj;
        return this.literal.equals(other.literal);
    }

    public MavenVersion toSnapshot() {
        Version newv = new Version(this.version.getMajor(), this.version.getMinor(), this.version.getMicro(), SNAPSHOT);
        return new MavenVersion(newv);
    }

    public static String validate(String v) {
        if (v == null) {
            return "Version is null";
        }
        if (!VERSION.matcher(v).matches()) {
            return "Not a version";
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String toDateStamp(long epoch) {
        String datestamp;
        SimpleDateFormat simpleDateFormat = snapshotTimestamp;
        synchronized (simpleDateFormat) {
            datestamp = snapshotTimestamp.format(new Date(epoch));
        }
        return datestamp;
    }

    public static String toDateStamp(long epoch, String build) {
        String s = MavenVersion.toDateStamp(epoch);
        if (build != null) {
            s = s + "-" + build;
        }
        return s;
    }

    public MavenVersion toSnapshot(long epoch, String build) {
        return this.toSnapshot(MavenVersion.toDateStamp(epoch, build));
    }

    public MavenVersion toSnapshot(String timestamp, String build) {
        if (build != null) {
            timestamp = timestamp + "-" + build;
        }
        return this.toSnapshot(timestamp);
    }

    public MavenVersion toSnapshot(String dateStamp) {
        String clean = this.literal.substring(0, this.literal.length() - 9);
        String result = clean + "-" + dateStamp;
        return new MavenVersion(result);
    }

    public static String cleanupVersion(String version) {
        if (version == null || version.trim().isEmpty()) {
            return "0";
        }
        Matcher m = VERSIONRANGE.matcher(version);
        if (m.matches()) {
            try {
                VersionRange vr = new VersionRange(version);
                return version;
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        if ((m = fuzzyVersionRange.matcher(version)).matches()) {
            String prefix = m.group(1);
            String first = m.group(2);
            String last = m.group(3);
            String suffix = m.group(4);
            return prefix + MavenVersion.cleanupVersion(first) + "," + MavenVersion.cleanupVersion(last) + suffix;
        }
        m = fuzzyVersion.matcher(version);
        if (m.matches()) {
            StringBuilder result = new StringBuilder();
            String major = MavenVersion.removeLeadingZeroes(m.group(1));
            String minor = MavenVersion.removeLeadingZeroes(m.group(3));
            String micro = MavenVersion.removeLeadingZeroes(m.group(5));
            String qualifier = m.group(7);
            if (qualifier == null) {
                if (!MavenVersion.isInteger(minor)) {
                    qualifier = minor;
                    minor = "0";
                } else if (!MavenVersion.isInteger(micro)) {
                    qualifier = micro;
                    micro = "0";
                }
            }
            if (major != null) {
                result.append(major);
                if (minor != null) {
                    result.append(".");
                    result.append(minor);
                    if (micro != null) {
                        result.append(".");
                        result.append(micro);
                        if (qualifier != null) {
                            result.append(".");
                            MavenVersion.cleanupModifier(result, qualifier);
                        }
                    } else if (qualifier != null) {
                        result.append(".0.");
                        MavenVersion.cleanupModifier(result, qualifier);
                    }
                } else if (qualifier != null) {
                    result.append(".0.0.");
                    MavenVersion.cleanupModifier(result, qualifier);
                }
                return result.toString();
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("0.0.0.");
        MavenVersion.cleanupModifier(sb, version);
        return sb.toString();
    }

    private static boolean isInteger(String minor) {
        return minor.length() < 10 || minor.length() == 10 && minor.compareTo("2147483647") < 0;
    }

    private static String removeLeadingZeroes(String group) {
        int n;
        if (group == null) {
            return "0";
        }
        for (n = 0; n < group.length() - 1 && group.charAt(n) == '0'; ++n) {
        }
        if (n == 0) {
            return group;
        }
        return group.substring(n);
    }

    static void cleanupModifier(StringBuilder result, String modifier) {
        int l = result.length();
        Matcher m = fuzzyModifier.matcher(modifier);
        if (m.matches()) {
            modifier = m.group(2);
        }
        for (int i = 0; i < modifier.length(); ++i) {
            char c = modifier.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_') && c != '-') continue;
            result.append(c);
        }
        if (l == result.length()) {
            result.append("_");
        }
    }

    static {
        snapshotTimestamp.setTimeZone(TimeZone.getTimeZone("UTC"));
        VERSION = Pattern.compile(VERSION_STRING);
        UNRESOLVED = new MavenVersion("0-UNRESOLVED");
        HIGHEST = new MavenVersion(Version.HIGHEST);
        LOWEST = new MavenVersion("0");
    }
}

