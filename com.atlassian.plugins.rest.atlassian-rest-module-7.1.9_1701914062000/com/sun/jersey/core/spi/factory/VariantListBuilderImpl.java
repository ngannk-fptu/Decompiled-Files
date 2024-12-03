/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Variant;

public class VariantListBuilderImpl
extends Variant.VariantListBuilder {
    private List<Variant> variants;
    private final List<MediaType> mediaTypes = new ArrayList<MediaType>();
    private final List<Locale> languages = new ArrayList<Locale>();
    private final List<String> charsets = new ArrayList<String>();
    private final List<String> encodings = new ArrayList<String>();

    @Override
    public List<Variant> build() {
        if (this.variants == null) {
            this.variants = new ArrayList<Variant>();
        }
        return this.variants;
    }

    @Override
    public Variant.VariantListBuilder add() {
        if (this.variants == null) {
            this.variants = new ArrayList<Variant>();
        }
        this.addMediaTypes();
        this.charsets.clear();
        this.languages.clear();
        this.encodings.clear();
        this.mediaTypes.clear();
        return this;
    }

    private void addMediaTypes() {
        if (this.mediaTypes.isEmpty()) {
            this.addLanguages(null);
        } else {
            for (MediaType mediaType : this.mediaTypes) {
                this.addLanguages(mediaType);
            }
        }
    }

    private void addLanguages(MediaType mediaType) {
        if (this.languages.isEmpty()) {
            this.addEncodings(mediaType, null);
        } else {
            for (Locale language : this.languages) {
                this.addEncodings(mediaType, language);
            }
        }
    }

    private void addEncodings(MediaType mediaType, Locale language) {
        if (this.encodings.isEmpty()) {
            this.addVariant(mediaType, language, null);
        } else {
            for (String encoding : this.encodings) {
                this.addVariant(mediaType, language, encoding);
            }
        }
    }

    private void addVariant(MediaType mediaType, Locale language, String encoding) {
        this.variants.add(new Variant(mediaType, language, encoding));
    }

    @Override
    public Variant.VariantListBuilder languages(Locale ... languages) {
        for (Locale language : languages) {
            this.languages.add(language);
        }
        return this;
    }

    @Override
    public Variant.VariantListBuilder encodings(String ... encodings) {
        for (String encoding : encodings) {
            this.encodings.add(encoding);
        }
        return this;
    }

    @Override
    public Variant.VariantListBuilder mediaTypes(MediaType ... mediaTypes) {
        for (MediaType mediaType : mediaTypes) {
            this.mediaTypes.add(mediaType);
        }
        return this;
    }
}

