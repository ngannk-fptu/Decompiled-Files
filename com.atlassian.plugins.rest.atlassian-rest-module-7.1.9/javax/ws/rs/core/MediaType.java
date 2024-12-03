/*
 * Decompiled with CFR 0.152.
 */
package javax.ws.rs.core;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import javax.ws.rs.ext.RuntimeDelegate;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MediaType {
    private String type;
    private String subtype;
    private Map<String, String> parameters;
    private static final Map<String, String> emptyMap = Collections.emptyMap();
    private static final RuntimeDelegate.HeaderDelegate<MediaType> delegate = RuntimeDelegate.getInstance().createHeaderDelegate(MediaType.class);
    public static final String MEDIA_TYPE_WILDCARD = "*";
    public static final String WILDCARD = "*/*";
    public static final MediaType WILDCARD_TYPE = new MediaType();
    public static final String APPLICATION_XML = "application/xml";
    public static final MediaType APPLICATION_XML_TYPE = new MediaType("application", "xml");
    public static final String APPLICATION_ATOM_XML = "application/atom+xml";
    public static final MediaType APPLICATION_ATOM_XML_TYPE = new MediaType("application", "atom+xml");
    public static final String APPLICATION_XHTML_XML = "application/xhtml+xml";
    public static final MediaType APPLICATION_XHTML_XML_TYPE = new MediaType("application", "xhtml+xml");
    public static final String APPLICATION_SVG_XML = "application/svg+xml";
    public static final MediaType APPLICATION_SVG_XML_TYPE = new MediaType("application", "svg+xml");
    public static final String APPLICATION_JSON = "application/json";
    public static final MediaType APPLICATION_JSON_TYPE = new MediaType("application", "json");
    public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final MediaType APPLICATION_FORM_URLENCODED_TYPE = new MediaType("application", "x-www-form-urlencoded");
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final MediaType MULTIPART_FORM_DATA_TYPE = new MediaType("multipart", "form-data");
    public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final MediaType APPLICATION_OCTET_STREAM_TYPE = new MediaType("application", "octet-stream");
    public static final String TEXT_PLAIN = "text/plain";
    public static final MediaType TEXT_PLAIN_TYPE = new MediaType("text", "plain");
    public static final String TEXT_XML = "text/xml";
    public static final MediaType TEXT_XML_TYPE = new MediaType("text", "xml");
    public static final String TEXT_HTML = "text/html";
    public static final MediaType TEXT_HTML_TYPE = new MediaType("text", "html");

    public static MediaType valueOf(String type) throws IllegalArgumentException {
        return delegate.fromString(type);
    }

    public MediaType(String type, String subtype, Map<String, String> parameters) {
        this.type = type == null ? MEDIA_TYPE_WILDCARD : type;
        String string = this.subtype = subtype == null ? MEDIA_TYPE_WILDCARD : subtype;
        if (parameters == null) {
            this.parameters = emptyMap;
        } else {
            TreeMap<String, String> map = new TreeMap<String, String>(new Comparator<String>(){

                @Override
                public int compare(String o1, String o2) {
                    return o1.compareToIgnoreCase(o2);
                }
            });
            for (Map.Entry<String, String> e : parameters.entrySet()) {
                map.put(e.getKey().toLowerCase(), e.getValue());
            }
            this.parameters = Collections.unmodifiableMap(map);
        }
    }

    public MediaType(String type, String subtype) {
        this(type, subtype, emptyMap);
    }

    public MediaType() {
        this(MEDIA_TYPE_WILDCARD, MEDIA_TYPE_WILDCARD);
    }

    public String getType() {
        return this.type;
    }

    public boolean isWildcardType() {
        return this.getType().equals(MEDIA_TYPE_WILDCARD);
    }

    public String getSubtype() {
        return this.subtype;
    }

    public boolean isWildcardSubtype() {
        return this.getSubtype().equals(MEDIA_TYPE_WILDCARD);
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    public boolean isCompatible(MediaType other) {
        if (other == null) {
            return false;
        }
        if (this.type.equals(MEDIA_TYPE_WILDCARD) || other.type.equals(MEDIA_TYPE_WILDCARD)) {
            return true;
        }
        if (this.type.equalsIgnoreCase(other.type) && (this.subtype.equals(MEDIA_TYPE_WILDCARD) || other.subtype.equals(MEDIA_TYPE_WILDCARD))) {
            return true;
        }
        return this.type.equalsIgnoreCase(other.type) && this.subtype.equalsIgnoreCase(other.subtype);
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MediaType)) {
            return false;
        }
        MediaType other = (MediaType)obj;
        return this.type.equalsIgnoreCase(other.type) && this.subtype.equalsIgnoreCase(other.subtype) && ((Object)this.parameters).equals(other.parameters);
    }

    public int hashCode() {
        return (this.type.toLowerCase() + this.subtype.toLowerCase()).hashCode() + ((Object)this.parameters).hashCode();
    }

    public String toString() {
        return delegate.toString(this);
    }
}

