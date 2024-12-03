/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.InvalidMimeTypeException
 *  org.springframework.util.MimeType
 *  org.springframework.util.MimeType$SpecificityComparator
 *  org.springframework.util.MimeTypeUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.http;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;

public class MediaType
extends MimeType
implements Serializable {
    private static final long serialVersionUID = 2069937152339670231L;
    public static final MediaType ALL = new MediaType("*", "*");
    public static final String ALL_VALUE = "*/*";
    public static final MediaType APPLICATION_ATOM_XML = new MediaType("application", "atom+xml");
    public static final String APPLICATION_ATOM_XML_VALUE = "application/atom+xml";
    public static final MediaType APPLICATION_CBOR = new MediaType("application", "cbor");
    public static final String APPLICATION_CBOR_VALUE = "application/cbor";
    public static final MediaType APPLICATION_FORM_URLENCODED = new MediaType("application", "x-www-form-urlencoded");
    public static final String APPLICATION_FORM_URLENCODED_VALUE = "application/x-www-form-urlencoded";
    public static final MediaType APPLICATION_GRAPHQL = new MediaType("application", "graphql+json");
    public static final String APPLICATION_GRAPHQL_VALUE = "application/graphql+json";
    public static final MediaType APPLICATION_JSON = new MediaType("application", "json");
    public static final String APPLICATION_JSON_VALUE = "application/json";
    @Deprecated
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType("application", "json", StandardCharsets.UTF_8);
    @Deprecated
    public static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";
    public static final MediaType APPLICATION_OCTET_STREAM;
    public static final String APPLICATION_OCTET_STREAM_VALUE = "application/octet-stream";
    public static final MediaType APPLICATION_PDF;
    public static final String APPLICATION_PDF_VALUE = "application/pdf";
    public static final MediaType APPLICATION_PROBLEM_JSON;
    public static final String APPLICATION_PROBLEM_JSON_VALUE = "application/problem+json";
    @Deprecated
    public static final MediaType APPLICATION_PROBLEM_JSON_UTF8;
    @Deprecated
    public static final String APPLICATION_PROBLEM_JSON_UTF8_VALUE = "application/problem+json;charset=UTF-8";
    public static final MediaType APPLICATION_PROBLEM_XML;
    public static final String APPLICATION_PROBLEM_XML_VALUE = "application/problem+xml";
    public static final MediaType APPLICATION_RSS_XML;
    public static final String APPLICATION_RSS_XML_VALUE = "application/rss+xml";
    public static final MediaType APPLICATION_NDJSON;
    public static final String APPLICATION_NDJSON_VALUE = "application/x-ndjson";
    @Deprecated
    public static final MediaType APPLICATION_STREAM_JSON;
    @Deprecated
    public static final String APPLICATION_STREAM_JSON_VALUE = "application/stream+json";
    public static final MediaType APPLICATION_XHTML_XML;
    public static final String APPLICATION_XHTML_XML_VALUE = "application/xhtml+xml";
    public static final MediaType APPLICATION_XML;
    public static final String APPLICATION_XML_VALUE = "application/xml";
    public static final MediaType IMAGE_GIF;
    public static final String IMAGE_GIF_VALUE = "image/gif";
    public static final MediaType IMAGE_JPEG;
    public static final String IMAGE_JPEG_VALUE = "image/jpeg";
    public static final MediaType IMAGE_PNG;
    public static final String IMAGE_PNG_VALUE = "image/png";
    public static final MediaType MULTIPART_FORM_DATA;
    public static final String MULTIPART_FORM_DATA_VALUE = "multipart/form-data";
    public static final MediaType MULTIPART_MIXED;
    public static final String MULTIPART_MIXED_VALUE = "multipart/mixed";
    public static final MediaType MULTIPART_RELATED;
    public static final String MULTIPART_RELATED_VALUE = "multipart/related";
    public static final MediaType TEXT_EVENT_STREAM;
    public static final String TEXT_EVENT_STREAM_VALUE = "text/event-stream";
    public static final MediaType TEXT_HTML;
    public static final String TEXT_HTML_VALUE = "text/html";
    public static final MediaType TEXT_MARKDOWN;
    public static final String TEXT_MARKDOWN_VALUE = "text/markdown";
    public static final MediaType TEXT_PLAIN;
    public static final String TEXT_PLAIN_VALUE = "text/plain";
    public static final MediaType TEXT_XML;
    public static final String TEXT_XML_VALUE = "text/xml";
    private static final String PARAM_QUALITY_FACTOR = "q";
    public static final Comparator<MediaType> QUALITY_VALUE_COMPARATOR;
    public static final Comparator<MediaType> SPECIFICITY_COMPARATOR;

    public MediaType(String type) {
        super(type);
    }

    public MediaType(String type, String subtype) {
        super(type, subtype, Collections.emptyMap());
    }

    public MediaType(String type, String subtype, Charset charset) {
        super(type, subtype, charset);
    }

    public MediaType(String type, String subtype, double qualityValue) {
        this(type, subtype, Collections.singletonMap(PARAM_QUALITY_FACTOR, Double.toString(qualityValue)));
    }

    public MediaType(MediaType other, Charset charset) {
        super((MimeType)other, charset);
    }

    public MediaType(MediaType other, @Nullable Map<String, String> parameters) {
        super(other.getType(), other.getSubtype(), parameters);
    }

    public MediaType(String type, String subtype, @Nullable Map<String, String> parameters) {
        super(type, subtype, parameters);
    }

    public MediaType(MimeType mimeType) {
        super(mimeType);
        this.getParameters().forEach(this::checkParameters);
    }

    protected void checkParameters(String parameter, String value) {
        super.checkParameters(parameter, value);
        if (PARAM_QUALITY_FACTOR.equals(parameter)) {
            String unquotedValue = this.unquote(value);
            double d = Double.parseDouble(unquotedValue);
            Assert.isTrue((d >= 0.0 && d <= 1.0 ? 1 : 0) != 0, () -> "Invalid quality value \"" + unquotedValue + "\": should be between 0.0 and 1.0");
        }
    }

    public double getQualityValue() {
        String qualityFactor = this.getParameter(PARAM_QUALITY_FACTOR);
        return qualityFactor != null ? Double.parseDouble(this.unquote(qualityFactor)) : 1.0;
    }

    public boolean includes(@Nullable MediaType other) {
        return super.includes((MimeType)other);
    }

    public boolean isCompatibleWith(@Nullable MediaType other) {
        return super.isCompatibleWith((MimeType)other);
    }

    public MediaType copyQualityValue(MediaType mediaType) {
        if (!mediaType.getParameters().containsKey(PARAM_QUALITY_FACTOR)) {
            return this;
        }
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>(this.getParameters());
        params.put(PARAM_QUALITY_FACTOR, (String)mediaType.getParameters().get(PARAM_QUALITY_FACTOR));
        return new MediaType(this, params);
    }

    public MediaType removeQualityValue() {
        if (!this.getParameters().containsKey(PARAM_QUALITY_FACTOR)) {
            return this;
        }
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>(this.getParameters());
        params.remove(PARAM_QUALITY_FACTOR);
        return new MediaType(this, params);
    }

    public static MediaType valueOf(String value) {
        return MediaType.parseMediaType(value);
    }

    public static MediaType parseMediaType(String mediaType) {
        MimeType type;
        try {
            type = MimeTypeUtils.parseMimeType((String)mediaType);
        }
        catch (InvalidMimeTypeException ex) {
            throw new InvalidMediaTypeException(ex);
        }
        try {
            return new MediaType(type);
        }
        catch (IllegalArgumentException ex) {
            throw new InvalidMediaTypeException(mediaType, ex.getMessage());
        }
    }

    public static List<MediaType> parseMediaTypes(@Nullable String mediaTypes) {
        if (!StringUtils.hasLength((String)mediaTypes)) {
            return Collections.emptyList();
        }
        List tokenizedTypes = MimeTypeUtils.tokenize((String)mediaTypes);
        ArrayList<MediaType> result = new ArrayList<MediaType>(tokenizedTypes.size());
        for (String type : tokenizedTypes) {
            if (!StringUtils.hasText((String)type)) continue;
            result.add(MediaType.parseMediaType(type));
        }
        return result;
    }

    public static List<MediaType> parseMediaTypes(@Nullable List<String> mediaTypes) {
        if (CollectionUtils.isEmpty(mediaTypes)) {
            return Collections.emptyList();
        }
        if (mediaTypes.size() == 1) {
            return MediaType.parseMediaTypes(mediaTypes.get(0));
        }
        ArrayList<MediaType> result = new ArrayList<MediaType>(8);
        for (String mediaType : mediaTypes) {
            result.addAll(MediaType.parseMediaTypes(mediaType));
        }
        return result;
    }

    public static List<MediaType> asMediaTypes(List<MimeType> mimeTypes) {
        ArrayList<MediaType> mediaTypes = new ArrayList<MediaType>(mimeTypes.size());
        for (MimeType mimeType : mimeTypes) {
            mediaTypes.add(MediaType.asMediaType(mimeType));
        }
        return mediaTypes;
    }

    public static MediaType asMediaType(MimeType mimeType) {
        if (mimeType instanceof MediaType) {
            return (MediaType)mimeType;
        }
        return new MediaType(mimeType.getType(), mimeType.getSubtype(), mimeType.getParameters());
    }

    public static String toString(Collection<MediaType> mediaTypes) {
        return MimeTypeUtils.toString(mediaTypes);
    }

    public static void sortBySpecificity(List<MediaType> mediaTypes) {
        Assert.notNull(mediaTypes, (String)"'mediaTypes' must not be null");
        if (mediaTypes.size() > 1) {
            mediaTypes.sort(SPECIFICITY_COMPARATOR);
        }
    }

    public static void sortByQualityValue(List<MediaType> mediaTypes) {
        Assert.notNull(mediaTypes, (String)"'mediaTypes' must not be null");
        if (mediaTypes.size() > 1) {
            mediaTypes.sort(QUALITY_VALUE_COMPARATOR);
        }
    }

    public static void sortBySpecificityAndQuality(List<MediaType> mediaTypes) {
        Assert.notNull(mediaTypes, (String)"'mediaTypes' must not be null");
        if (mediaTypes.size() > 1) {
            mediaTypes.sort(SPECIFICITY_COMPARATOR.thenComparing(QUALITY_VALUE_COMPARATOR));
        }
    }

    static {
        APPLICATION_NDJSON = new MediaType("application", "x-ndjson");
        APPLICATION_OCTET_STREAM = new MediaType("application", "octet-stream");
        APPLICATION_PDF = new MediaType("application", "pdf");
        APPLICATION_PROBLEM_JSON = new MediaType("application", "problem+json");
        APPLICATION_PROBLEM_JSON_UTF8 = new MediaType("application", "problem+json", StandardCharsets.UTF_8);
        APPLICATION_PROBLEM_XML = new MediaType("application", "problem+xml");
        APPLICATION_RSS_XML = new MediaType("application", "rss+xml");
        APPLICATION_STREAM_JSON = new MediaType("application", "stream+json");
        APPLICATION_XHTML_XML = new MediaType("application", "xhtml+xml");
        APPLICATION_XML = new MediaType("application", "xml");
        IMAGE_GIF = new MediaType("image", "gif");
        IMAGE_JPEG = new MediaType("image", "jpeg");
        IMAGE_PNG = new MediaType("image", "png");
        MULTIPART_FORM_DATA = new MediaType("multipart", "form-data");
        MULTIPART_MIXED = new MediaType("multipart", "mixed");
        MULTIPART_RELATED = new MediaType("multipart", "related");
        TEXT_EVENT_STREAM = new MediaType("text", "event-stream");
        TEXT_HTML = new MediaType("text", "html");
        TEXT_MARKDOWN = new MediaType("text", "markdown");
        TEXT_PLAIN = new MediaType("text", "plain");
        TEXT_XML = new MediaType("text", "xml");
        QUALITY_VALUE_COMPARATOR = (mediaType1, mediaType2) -> {
            double quality1 = mediaType1.getQualityValue();
            double quality2 = mediaType2.getQualityValue();
            int qualityComparison = Double.compare(quality2, quality1);
            if (qualityComparison != 0) {
                return qualityComparison;
            }
            if (mediaType1.isWildcardType() && !mediaType2.isWildcardType()) {
                return 1;
            }
            if (mediaType2.isWildcardType() && !mediaType1.isWildcardType()) {
                return -1;
            }
            if (!mediaType1.getType().equals(mediaType2.getType())) {
                return 0;
            }
            if (mediaType1.isWildcardSubtype() && !mediaType2.isWildcardSubtype()) {
                return 1;
            }
            if (mediaType2.isWildcardSubtype() && !mediaType1.isWildcardSubtype()) {
                return -1;
            }
            if (!mediaType1.getSubtype().equals(mediaType2.getSubtype())) {
                return 0;
            }
            int paramsSize1 = mediaType1.getParameters().size();
            int paramsSize2 = mediaType2.getParameters().size();
            return Integer.compare(paramsSize2, paramsSize1);
        };
        SPECIFICITY_COMPARATOR = new MimeType.SpecificityComparator<MediaType>(){

            protected int compareParameters(MediaType mediaType1, MediaType mediaType2) {
                double quality1 = mediaType1.getQualityValue();
                double quality2 = mediaType2.getQualityValue();
                int qualityComparison = Double.compare(quality2, quality1);
                if (qualityComparison != 0) {
                    return qualityComparison;
                }
                return super.compareParameters((MimeType)mediaType1, (MimeType)mediaType2);
            }
        };
    }
}

