/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl;

import com.sun.jersey.core.header.AcceptableLanguageTag;
import com.sun.jersey.core.header.AcceptableMediaType;
import com.sun.jersey.core.header.AcceptableToken;
import com.sun.jersey.core.header.QualityFactor;
import com.sun.jersey.core.header.QualitySourceMediaType;
import com.sun.jersey.server.impl.model.HttpHelper;
import com.sun.jersey.spi.container.ContainerRequest;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Variant;

public final class VariantSelector {
    private static final DimensionChecker<AcceptableMediaType, MediaType> MEDIA_TYPE_DC = new DimensionChecker<AcceptableMediaType, MediaType>(){

        @Override
        public MediaType getDimension(VariantHolder v) {
            return v.v.getMediaType();
        }

        @Override
        public boolean isCompatible(AcceptableMediaType t, MediaType u) {
            return t.isCompatible(u);
        }

        @Override
        public int getQualitySource(VariantHolder v, MediaType u) {
            return v.mediaTypeQs;
        }

        @Override
        public String getVaryHeaderValue() {
            return "Accept";
        }
    };
    private static final DimensionChecker<AcceptableLanguageTag, Locale> LANGUAGE_TAG_DC = new DimensionChecker<AcceptableLanguageTag, Locale>(){

        @Override
        public Locale getDimension(VariantHolder v) {
            return v.v.getLanguage();
        }

        @Override
        public boolean isCompatible(AcceptableLanguageTag t, Locale u) {
            return t.isCompatible(u);
        }

        @Override
        public int getQualitySource(VariantHolder qsv, Locale u) {
            return 0;
        }

        @Override
        public String getVaryHeaderValue() {
            return "Accept-Language";
        }
    };
    private static final DimensionChecker<AcceptableToken, String> CHARSET_DC = new DimensionChecker<AcceptableToken, String>(){

        @Override
        public String getDimension(VariantHolder v) {
            MediaType m = v.v.getMediaType();
            return m != null ? m.getParameters().get("charset") : null;
        }

        @Override
        public boolean isCompatible(AcceptableToken t, String u) {
            return t.isCompatible(u);
        }

        @Override
        public int getQualitySource(VariantHolder qsv, String u) {
            return 0;
        }

        @Override
        public String getVaryHeaderValue() {
            return "Accept-Charset";
        }
    };
    private static final DimensionChecker<AcceptableToken, String> ENCODING_DC = new DimensionChecker<AcceptableToken, String>(){

        @Override
        public String getDimension(VariantHolder v) {
            return v.v.getEncoding();
        }

        @Override
        public boolean isCompatible(AcceptableToken t, String u) {
            return t.isCompatible(u);
        }

        @Override
        public int getQualitySource(VariantHolder qsv, String u) {
            return 0;
        }

        @Override
        public String getVaryHeaderValue() {
            return "Accept-Encoding";
        }
    };

    private VariantSelector() {
    }

    private static <T extends QualityFactor, U> LinkedList<VariantHolder> selectVariants(LinkedList<VariantHolder> vs, List<T> as, DimensionChecker<T, U> dc, Set<String> vary) {
        int cq = 0;
        int cqs = 0;
        LinkedList<VariantHolder> selected = new LinkedList<VariantHolder>();
        for (QualityFactor a : as) {
            int q = a.getQuality();
            Iterator iv = vs.iterator();
            while (iv.hasNext()) {
                VariantHolder v = (VariantHolder)iv.next();
                U d = dc.getDimension(v);
                if (d == null) continue;
                vary.add(dc.getVaryHeaderValue());
                int qs = dc.getQualitySource(v, d);
                if (qs < cqs || !dc.isCompatible(a, d)) continue;
                if (qs > cqs) {
                    cqs = qs;
                    cq = q;
                    selected.clear();
                    selected.add(v);
                } else if (q > cq) {
                    cq = q;
                    selected.addFirst(v);
                } else if (q == cq) {
                    selected.add(v);
                }
                iv.remove();
            }
        }
        for (VariantHolder v : vs) {
            if (dc.getDimension(v) != null) continue;
            selected.add(v);
        }
        return selected;
    }

    private static LinkedList<VariantHolder> getVariantHolderList(List<Variant> variants) {
        LinkedList<VariantHolder> l = new LinkedList<VariantHolder>();
        for (Variant v : variants) {
            MediaType mt = v.getMediaType();
            if (mt != null) {
                if (mt instanceof QualitySourceMediaType || mt.getParameters().containsKey("qs")) {
                    int qs = QualitySourceMediaType.getQualitySource(mt);
                    l.add(new VariantHolder(v, qs));
                    continue;
                }
                l.add(new VariantHolder(v));
                continue;
            }
            l.add(new VariantHolder(v));
        }
        return l;
    }

    public static Variant selectVariant(ContainerRequest r, List<Variant> variants) {
        LinkedList<VariantHolder> vhs = VariantSelector.getVariantHolderList(variants);
        HashSet<String> vary = new HashSet<String>();
        vhs = VariantSelector.selectVariants(vhs, HttpHelper.getAccept(r), MEDIA_TYPE_DC, vary);
        vhs = VariantSelector.selectVariants(vhs, HttpHelper.getAcceptLanguage(r), LANGUAGE_TAG_DC, vary);
        vhs = VariantSelector.selectVariants(vhs, HttpHelper.getAcceptCharset(r), CHARSET_DC, vary);
        if ((vhs = VariantSelector.selectVariants(vhs, HttpHelper.getAcceptEncoding(r), ENCODING_DC, vary)).isEmpty()) {
            return null;
        }
        StringBuilder varyHeader = new StringBuilder();
        for (String v : vary) {
            if (varyHeader.length() > 0) {
                varyHeader.append(',');
            }
            varyHeader.append(v);
        }
        r.getProperties().put("Vary", varyHeader.toString());
        return ((VariantHolder)vhs.iterator().next()).v;
    }

    private static class VariantHolder {
        private final Variant v;
        private final int mediaTypeQs;

        public VariantHolder(Variant v) {
            this(v, 1000);
        }

        public VariantHolder(Variant v, int mediaTypeQs) {
            this.v = v;
            this.mediaTypeQs = mediaTypeQs;
        }
    }

    private static interface DimensionChecker<T, U> {
        public U getDimension(VariantHolder var1);

        public int getQualitySource(VariantHolder var1, U var2);

        public boolean isCompatible(T var1, U var2);

        public String getVaryHeaderValue();
    }
}

