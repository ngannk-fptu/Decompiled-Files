/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.version;

import aQute.bnd.version.MavenVersion;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MavenVersionRange {
    static final Pattern RESTRICTION_P = Pattern.compile("\\s*(((?<li>\\[|\\()\\s*(?<low>[^,\\s\\]\\[()]*)\\s*,\\s*(?<high>[^,\\s\\[\\]()]*)\\s*(?<hi>\\]|\\)))|(?<single>[^,\\s\\]\\[()]+))\\s*(?<comma>,)?\\s*", 4);
    final boolean li;
    final boolean hi;
    final MavenVersion low;
    final MavenVersion high;
    MavenVersionRange nextOr;

    public MavenVersionRange(String range) {
        this(RESTRICTION_P.matcher(range == null ? "0" : range));
    }

    private MavenVersionRange(Matcher m) {
        if (!m.lookingAt()) {
            throw new IllegalArgumentException("Invalid version range " + m);
        }
        String single = m.group("single");
        if (single != null) {
            this.li = true;
            this.low = new MavenVersion(single);
            this.high = MavenVersion.HIGHEST;
            this.hi = true;
        } else {
            this.li = m.group("li").equals("[");
            this.hi = m.group("hi").equals("]");
            this.low = MavenVersion.parseMavenString(m.group("low"));
            this.high = MavenVersion.parseMavenString(m.group("high"));
        }
        if (m.group("comma") != null) {
            m.region(m.end(), m.regionEnd());
            this.nextOr = new MavenVersionRange(m);
        } else {
            this.nextOr = null;
        }
    }

    public boolean includes(MavenVersion mvr) {
        boolean highOk;
        int l = mvr.compareTo(this.low);
        int h = mvr.compareTo(this.high);
        boolean lowOk = l > 0 || this.li && l == 0;
        boolean bl = highOk = h < 0 || this.hi && h == 0;
        if (lowOk && highOk) {
            return true;
        }
        if (this.nextOr != null) {
            return this.nextOr.includes(mvr);
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.toString(sb);
        return sb.toString();
    }

    private void toString(StringBuilder sb) {
        if (this.li) {
            sb.append("[");
        } else {
            sb.append("(");
        }
        sb.append(this.low);
        sb.append(",");
        sb.append(this.high);
        if (this.hi) {
            sb.append("]");
        } else {
            sb.append(")");
        }
        if (this.nextOr != null) {
            sb.append(",");
            this.nextOr.toString(sb);
        }
    }

    public static MavenVersionRange parseRange(String version) {
        try {
            return new MavenVersionRange(version);
        }
        catch (Exception exception) {
            return null;
        }
    }

    public boolean wasSingle() {
        return this.li && !this.hi && this.high == MavenVersion.HIGHEST && this.nextOr == null;
    }

    public static boolean isRange(String version) {
        if (version == null) {
            return false;
        }
        return (version = version.trim()).startsWith("[") || version.startsWith("(");
    }
}

