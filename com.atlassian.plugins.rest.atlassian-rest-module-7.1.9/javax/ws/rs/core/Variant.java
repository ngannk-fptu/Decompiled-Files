/*
 * Decompiled with CFR 0.152.
 */
package javax.ws.rs.core;

import java.io.StringWriter;
import java.util.List;
import java.util.Locale;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.RuntimeDelegate;

public class Variant {
    private Locale language;
    private MediaType mediaType;
    private String encoding;

    public Variant(MediaType mediaType, Locale language, String encoding) {
        if (mediaType == null && language == null && encoding == null) {
            throw new IllegalArgumentException("mediaType, language, encoding all null");
        }
        this.encoding = encoding;
        this.language = language;
        this.mediaType = mediaType;
    }

    public Locale getLanguage() {
        return this.language;
    }

    public MediaType getMediaType() {
        return this.mediaType;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public static VariantListBuilder mediaTypes(MediaType ... mediaTypes) {
        VariantListBuilder b = VariantListBuilder.newInstance();
        b.mediaTypes(mediaTypes);
        return b;
    }

    public static VariantListBuilder languages(Locale ... languages) {
        VariantListBuilder b = VariantListBuilder.newInstance();
        b.languages(languages);
        return b;
    }

    public static VariantListBuilder encodings(String ... encodings) {
        VariantListBuilder b = VariantListBuilder.newInstance();
        b.encodings(encodings);
        return b;
    }

    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.language != null ? this.language.hashCode() : 0);
        hash = 29 * hash + (this.mediaType != null ? this.mediaType.hashCode() : 0);
        hash = 29 * hash + (this.encoding != null ? this.encoding.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Variant other = (Variant)obj;
        if (!(this.language == other.language || this.language != null && this.language.equals(other.language))) {
            return false;
        }
        if (!(this.mediaType == other.mediaType || this.mediaType != null && this.mediaType.equals(other.mediaType))) {
            return false;
        }
        return this.encoding == other.encoding || this.encoding != null && this.encoding.equals(other.encoding);
    }

    public String toString() {
        StringWriter w = new StringWriter();
        w.append("Variant[mediaType=");
        w.append(this.mediaType == null ? "null" : this.mediaType.toString());
        w.append(", language=");
        w.append(this.language == null ? "null" : this.language.toString());
        w.append(", encoding=");
        w.append(this.encoding == null ? "null" : this.encoding);
        w.append("]");
        return w.toString();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class VariantListBuilder {
        protected VariantListBuilder() {
        }

        public static VariantListBuilder newInstance() {
            VariantListBuilder b = RuntimeDelegate.getInstance().createVariantListBuilder();
            return b;
        }

        public abstract List<Variant> build();

        public abstract VariantListBuilder add();

        public abstract VariantListBuilder languages(Locale ... var1);

        public abstract VariantListBuilder encodings(String ... var1);

        public abstract VariantListBuilder mediaTypes(MediaType ... var1);
    }
}

