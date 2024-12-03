/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.rest;

import java.io.Serializable;
import org.codehaus.jackson.annotate.JsonProperty;

public class AtlaskitEmoticonModel
implements Serializable {
    @JsonProperty
    private String[] ascii;
    @JsonProperty
    private String id;
    @JsonProperty
    private String name;
    @JsonProperty
    private String shortName;
    @JsonProperty
    private String fallback;
    @JsonProperty
    private String type;
    @JsonProperty
    private String category;
    @JsonProperty
    private Long order;
    @JsonProperty
    private ImageRepresentation representation;
    @JsonProperty
    private Boolean searchable;
    @JsonProperty
    private AtlaskitEmoticonModel[] skinVariations;

    public AtlaskitEmoticonModel(String id, String name, String shortName, String fallback, String type, String category, Long order, ImageRepresentation representation, Boolean searchable, AtlaskitEmoticonModel[] skinVariations, String[] ascii) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.fallback = fallback;
        this.type = type;
        this.category = category;
        this.order = order;
        this.representation = representation;
        this.searchable = searchable;
        this.skinVariations = skinVariations;
        this.ascii = ascii;
    }

    public String[] getAscii() {
        return this.ascii;
    }

    public String getId() {
        return this.id;
    }

    public String getShortName() {
        return this.shortName;
    }

    public String getName() {
        return this.name;
    }

    public String getFallback() {
        return this.fallback;
    }

    public String getType() {
        return this.type;
    }

    public String getCategory() {
        return this.category;
    }

    public Long getOrder() {
        return this.order;
    }

    public ImageRepresentation getRepresentation() {
        return this.representation;
    }

    public Boolean getSearchable() {
        return this.searchable;
    }

    public AtlaskitEmoticonModel[] getSkinVariations() {
        return this.skinVariations;
    }

    public static class Custom
    extends AtlaskitEmoticonModel {
        @JsonProperty
        private String creatorUserId;
        @JsonProperty
        private String createdDate;

        Custom(String id, String name, String shortName, String fallback, String type, String category, Long order, ImageRepresentation representation, Boolean searchable, String creatorUserId, String createdDate, AtlaskitEmoticonModel[] skinVariations, String[] ascii) {
            super(id, name, shortName, fallback, type, category, order, representation, searchable, skinVariations, ascii);
            this.creatorUserId = creatorUserId;
            this.createdDate = createdDate;
        }

        public String getCreatorUserId() {
            return this.creatorUserId;
        }

        public String getCreatedDate() {
            return this.createdDate;
        }
    }

    public static class ImageRepresentation {
        @JsonProperty
        private Integer height;
        @JsonProperty
        private Integer width;
        @JsonProperty
        private String imagePath;

        public ImageRepresentation(Integer height, Integer width, String imagePath) {
            this.height = height;
            this.width = width;
            this.imagePath = imagePath;
        }

        public Integer getHeight() {
            return this.height;
        }

        public Integer getWidth() {
            return this.width;
        }

        public String getImagePath() {
            return this.imagePath;
        }
    }
}

