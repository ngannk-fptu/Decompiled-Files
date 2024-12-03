/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.util;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.abdera.i18n.text.Localizer;
import org.apache.commons.codec.binary.Hex;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EntityTag
implements Cloneable,
Serializable,
Comparable<EntityTag> {
    private static final long serialVersionUID = 1559972888659121461L;
    private static final Pattern PATTERN = Pattern.compile("(\\*)|([wW]/)?\"([^\"]*)\"");
    private static final String INVALID_ENTITY_TAG = Localizer.get("INVALID.ENTITY.TAG");
    public static final EntityTag WILD = new EntityTag("*");
    private final String tag;
    private final boolean weak;
    private final boolean wild;

    public static EntityTag parse(String entity_tag) {
        if (entity_tag == null || entity_tag.length() == 0) {
            throw new IllegalArgumentException(INVALID_ENTITY_TAG);
        }
        Matcher m = PATTERN.matcher(entity_tag);
        if (m.find()) {
            boolean wild = m.group(1) != null;
            boolean weak = m.group(2) != null;
            String tag = wild ? "*" : m.group(3);
            return new EntityTag(tag, weak, wild);
        }
        throw new IllegalArgumentException(INVALID_ENTITY_TAG);
    }

    public static EntityTag[] parseTags(String entity_tags) {
        if (entity_tags == null || entity_tags.length() == 0) {
            return new EntityTag[0];
        }
        String[] tags = entity_tags.split("((?<=\")\\s*,\\s*(?=([wW]/)?\"|\\*))");
        ArrayList<EntityTag> etags = new ArrayList<EntityTag>();
        for (String tag : tags) {
            etags.add(EntityTag.parse(tag.trim()));
        }
        return etags.toArray(new EntityTag[etags.size()]);
    }

    public static boolean matchesAny(EntityTag tag1, String tags) {
        return EntityTag.matchesAny(tag1, EntityTag.parseTags(tags), false);
    }

    public static boolean matchesAny(EntityTag tag1, String tags, boolean weak) {
        return EntityTag.matchesAny(tag1, EntityTag.parseTags(tags), weak);
    }

    public static boolean matchesAny(String tag1, String tags) {
        return EntityTag.matchesAny(EntityTag.parse(tag1), EntityTag.parseTags(tags), false);
    }

    public static boolean matchesAny(String tag1, String tags, boolean weak) {
        return EntityTag.matchesAny(EntityTag.parse(tag1), EntityTag.parseTags(tags), weak);
    }

    public static boolean matchesAny(EntityTag tag1, EntityTag[] tags) {
        return EntityTag.matchesAny(tag1, tags, false);
    }

    public static boolean matchesAny(EntityTag tag1, EntityTag[] tags, boolean weak) {
        if (tags == null) {
            return tag1 == null;
        }
        if (tag1.isWild() && tags != null && tags.length > 0) {
            return true;
        }
        for (EntityTag tag : tags) {
            if (!tag1.equals(tag) && !tag.isWild()) continue;
            return true;
        }
        return false;
    }

    public static boolean matches(EntityTag tag1, EntityTag tag2) {
        return tag1.equals(tag2);
    }

    public static boolean matches(String tag1, String tag2) {
        EntityTag etag1 = EntityTag.parse(tag1);
        EntityTag etag2 = EntityTag.parse(tag2);
        return etag1.equals(etag2);
    }

    public static boolean matches(EntityTag tag1, String tag2) {
        return tag1.equals(EntityTag.parse(tag2));
    }

    public EntityTag(String tag) {
        this(tag, false);
    }

    public EntityTag(String tag, boolean weak) {
        EntityTag etag = this.attemptParse(tag);
        if (etag == null) {
            if (tag.indexOf(34) > -1) {
                throw new IllegalArgumentException(INVALID_ENTITY_TAG);
            }
            this.tag = tag;
            this.weak = weak;
            this.wild = tag.equals("*");
        } else {
            this.tag = etag.tag;
            this.weak = etag.weak;
            this.wild = etag.wild;
        }
    }

    private EntityTag(String tag, boolean weak, boolean wild) {
        this.tag = tag;
        this.weak = weak;
        this.wild = wild;
    }

    private EntityTag attemptParse(String tag) {
        try {
            return EntityTag.parse(tag);
        }
        catch (Exception e) {
            return null;
        }
    }

    public boolean isWild() {
        return this.wild;
    }

    public String getTag() {
        return this.tag;
    }

    public boolean isWeak() {
        return this.weak;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (this.wild) {
            buf.append("*");
        } else {
            if (this.weak) {
                buf.append("W/");
            }
            buf.append('\"');
            buf.append(this.tag);
            buf.append('\"');
        }
        return buf.toString();
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.tag == null ? 0 : this.tag.hashCode());
        result = 31 * result + (this.weak ? 1231 : 1237);
        result = 31 * result + (this.wild ? 1231 : 1237);
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
        EntityTag other = (EntityTag)obj;
        if (this.isWild() || other.isWild()) {
            return true;
        }
        if (this.tag == null ? other.tag != null : !this.tag.equals(other.tag)) {
            return false;
        }
        if (this.weak != other.weak) {
            return false;
        }
        return this.wild == other.wild;
    }

    protected EntityTag clone() {
        try {
            return (EntityTag)super.clone();
        }
        catch (CloneNotSupportedException e) {
            return new EntityTag(this.tag, this.weak, this.wild);
        }
    }

    public static EntityTag generate(String ... material) {
        String etag = null;
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            for (String s : material) {
                if (s == null) continue;
                md.update(s.getBytes("utf-8"));
            }
            byte[] digest = md.digest();
            etag = new String(Hex.encodeHex(digest));
        }
        catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(Localizer.get("HASHING.NOT.AVAILABLE"));
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(Localizer.get("UTF8.NOT.SUPPORTED"), e);
        }
        return new EntityTag(etag);
    }

    public static boolean matches(EntityTag etag, String ... material) {
        EntityTag etag2 = EntityTag.generate(material);
        return EntityTag.matches(etag, etag2);
    }

    public static String toString(EntityTag ... tags) {
        StringBuilder buf = new StringBuilder();
        for (EntityTag tag : tags) {
            if (buf.length() > 0) {
                buf.append(", ");
            }
            buf.append(tag.toString());
        }
        return buf.toString();
    }

    public static String toString(String ... tags) {
        StringBuilder buf = new StringBuilder();
        for (String tag : tags) {
            if (buf.length() > 0) {
                buf.append(", ");
            }
            EntityTag etag = new EntityTag(tag);
            buf.append(etag.toString());
        }
        return buf.toString();
    }

    @Override
    public int compareTo(EntityTag o) {
        if (o.isWild() && !this.isWild()) {
            return 1;
        }
        if (this.isWild() && !o.isWild()) {
            return -1;
        }
        if (o.isWeak() && !this.isWeak()) {
            return -1;
        }
        if (this.isWeak() && !o.isWeak()) {
            return 1;
        }
        return this.tag.compareTo(o.tag);
    }
}

