/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata.filter;

import org.apache.tika.config.Field;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.filter.MetadataFilter;
import org.apache.tika.utils.StringUtils;

public class GeoPointMetadataFilter
extends MetadataFilter {
    String geoPointFieldName = "location";

    @Field
    public void setGeoPointFieldName(String geoPointFieldName) {
        this.geoPointFieldName = geoPointFieldName;
    }

    @Override
    public void filter(Metadata metadata) throws TikaException {
        String lat = metadata.get(TikaCoreProperties.LATITUDE);
        if (StringUtils.isEmpty(lat)) {
            return;
        }
        String lng = metadata.get(TikaCoreProperties.LONGITUDE);
        if (StringUtils.isEmpty(lng)) {
            return;
        }
        metadata.set(this.geoPointFieldName, lat + "," + lng);
    }
}

