/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.header;

import com.sun.jersey.core.header.AcceptableMediaType;
import com.sun.jersey.core.header.QualitySourceMediaType;
import com.sun.jersey.core.header.reader.HttpHeaderReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class MediaTypes {
    public static final String WADL_STRING = "application/vnd.sun.wadl+xml";
    public static final MediaType WADL = MediaType.valueOf("application/vnd.sun.wadl+xml");
    public static final String WADL_JSON_STRING = "application/vnd.sun.wadl+json";
    public static final MediaType WADL_JSON = MediaType.valueOf("application/vnd.sun.wadl+json");
    public static final MediaType FAST_INFOSET = MediaType.valueOf("application/fastinfoset");
    public static final Comparator<MediaType> MEDIA_TYPE_COMPARATOR = new Comparator<MediaType>(){

        @Override
        public int compare(MediaType o1, MediaType o2) {
            if (o1.getType().equals("*") && !o2.getType().equals("*")) {
                return 1;
            }
            if (o2.getType().equals("*") && !o1.getType().equals("*")) {
                return -1;
            }
            if (o1.getSubtype().equals("*") && !o2.getSubtype().equals("*")) {
                return 1;
            }
            if (o2.getSubtype().equals("*") && !o1.getSubtype().equals("*")) {
                return -1;
            }
            return 0;
        }
    };
    public static final Comparator<List<? extends MediaType>> MEDIA_TYPE_LIST_COMPARATOR = new Comparator<List<? extends MediaType>>(){

        @Override
        public int compare(List<? extends MediaType> o1, List<? extends MediaType> o2) {
            return MEDIA_TYPE_COMPARATOR.compare(this.getLeastSpecific(o1), this.getLeastSpecific(o2));
        }

        public MediaType getLeastSpecific(List<? extends MediaType> l) {
            return l.get(l.size() - 1);
        }
    };
    public static final MediaType GENERAL_MEDIA_TYPE = new MediaType("*", "*");
    public static final List<MediaType> GENERAL_MEDIA_TYPE_LIST = MediaTypes.createMediaTypeList();
    public static final AcceptableMediaType GENERAL_ACCEPT_MEDIA_TYPE = new AcceptableMediaType("*", "*");
    public static final List<AcceptableMediaType> GENERAL_ACCEPT_MEDIA_TYPE_LIST = MediaTypes.createAcceptMediaTypeList();
    public static final Comparator<QualitySourceMediaType> QUALITY_SOURCE_MEDIA_TYPE_COMPARATOR = new Comparator<QualitySourceMediaType>(){

        @Override
        public int compare(QualitySourceMediaType o1, QualitySourceMediaType o2) {
            int i = o2.getQualitySource() - o1.getQualitySource();
            if (i != 0) {
                return i;
            }
            return MEDIA_TYPE_COMPARATOR.compare(o1, o2);
        }
    };
    public static final List<MediaType> GENERAL_QUALITY_SOURCE_MEDIA_TYPE_LIST = MediaTypes.createQualitySourceMediaTypeList();
    private static Map<String, MediaType> mediaTypeCache = new HashMap<String, MediaType>(){
        {
            this.put("application", new MediaType("application", "*"));
            this.put("multipart", new MediaType("multipart", "*"));
            this.put("text", new MediaType("text", "*"));
        }
    };

    private MediaTypes() {
    }

    public static final boolean typeEquals(MediaType m1, MediaType m2) {
        if (m1 == null || m2 == null) {
            return false;
        }
        return m1.getSubtype().equalsIgnoreCase(m2.getSubtype()) && m1.getType().equalsIgnoreCase(m2.getType());
    }

    public static final boolean intersects(List<? extends MediaType> ml1, List<? extends MediaType> ml2) {
        for (MediaType mediaType : ml1) {
            for (MediaType mediaType2 : ml2) {
                if (!MediaTypes.typeEquals(mediaType, mediaType2)) continue;
                return true;
            }
        }
        return false;
    }

    public static final MediaType mostSpecific(MediaType m1, MediaType m2) {
        if (m1.isWildcardSubtype() && !m2.isWildcardSubtype()) {
            return m2;
        }
        if (m1.isWildcardType() && !m2.isWildcardType()) {
            return m2;
        }
        return m1;
    }

    private static List<MediaType> createMediaTypeList() {
        return Collections.singletonList(GENERAL_MEDIA_TYPE);
    }

    private static List<AcceptableMediaType> createAcceptMediaTypeList() {
        return Collections.singletonList(GENERAL_ACCEPT_MEDIA_TYPE);
    }

    public static List<MediaType> createMediaTypes(Consumes mime) {
        if (mime == null) {
            return GENERAL_MEDIA_TYPE_LIST;
        }
        return MediaTypes.createMediaTypes(mime.value());
    }

    public static List<MediaType> createMediaTypes(Produces mime) {
        if (mime == null) {
            return GENERAL_MEDIA_TYPE_LIST;
        }
        return MediaTypes.createMediaTypes(mime.value());
    }

    public static List<MediaType> createMediaTypes(String[] mediaTypes) {
        ArrayList<MediaType> l = new ArrayList<MediaType>();
        try {
            for (String mediaType : mediaTypes) {
                HttpHeaderReader.readMediaTypes(l, mediaType);
            }
            Collections.sort(l, MEDIA_TYPE_COMPARATOR);
            return l;
        }
        catch (ParseException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    private static List<MediaType> createQualitySourceMediaTypeList() {
        return Collections.singletonList(new QualitySourceMediaType("*", "*"));
    }

    public static List<MediaType> createQualitySourceMediaTypes(Produces mime) {
        if (mime == null || mime.value().length == 0) {
            return GENERAL_QUALITY_SOURCE_MEDIA_TYPE_LIST;
        }
        return new ArrayList<MediaType>(MediaTypes.createQualitySourceMediaTypes(mime.value()));
    }

    public static List<QualitySourceMediaType> createQualitySourceMediaTypes(String[] mediaTypes) {
        try {
            return HttpHeaderReader.readQualitySourceMediaType(mediaTypes);
        }
        catch (ParseException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public static MediaType getTypeWildCart(MediaType mediaType) {
        MediaType mt = mediaTypeCache.get(mediaType.getType());
        if (mt == null) {
            mt = new MediaType(mediaType.getType(), "*");
        }
        return mt;
    }
}

