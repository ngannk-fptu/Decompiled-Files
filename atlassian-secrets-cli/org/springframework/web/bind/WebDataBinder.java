/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.bind;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.core.CollectionFactory;
import org.springframework.lang.Nullable;
import org.springframework.validation.DataBinder;
import org.springframework.web.multipart.MultipartFile;

public class WebDataBinder
extends DataBinder {
    public static final String DEFAULT_FIELD_MARKER_PREFIX = "_";
    public static final String DEFAULT_FIELD_DEFAULT_PREFIX = "!";
    @Nullable
    private String fieldMarkerPrefix = "_";
    @Nullable
    private String fieldDefaultPrefix = "!";
    private boolean bindEmptyMultipartFiles = true;

    public WebDataBinder(@Nullable Object target) {
        super(target);
    }

    public WebDataBinder(@Nullable Object target, String objectName) {
        super(target, objectName);
    }

    public void setFieldMarkerPrefix(@Nullable String fieldMarkerPrefix) {
        this.fieldMarkerPrefix = fieldMarkerPrefix;
    }

    @Nullable
    public String getFieldMarkerPrefix() {
        return this.fieldMarkerPrefix;
    }

    public void setFieldDefaultPrefix(@Nullable String fieldDefaultPrefix) {
        this.fieldDefaultPrefix = fieldDefaultPrefix;
    }

    @Nullable
    public String getFieldDefaultPrefix() {
        return this.fieldDefaultPrefix;
    }

    public void setBindEmptyMultipartFiles(boolean bindEmptyMultipartFiles) {
        this.bindEmptyMultipartFiles = bindEmptyMultipartFiles;
    }

    public boolean isBindEmptyMultipartFiles() {
        return this.bindEmptyMultipartFiles;
    }

    @Override
    protected void doBind(MutablePropertyValues mpvs) {
        this.checkFieldDefaults(mpvs);
        this.checkFieldMarkers(mpvs);
        super.doBind(mpvs);
    }

    protected void checkFieldDefaults(MutablePropertyValues mpvs) {
        String fieldDefaultPrefix = this.getFieldDefaultPrefix();
        if (fieldDefaultPrefix != null) {
            PropertyValue[] pvArray;
            for (PropertyValue pv : pvArray = mpvs.getPropertyValues()) {
                if (!pv.getName().startsWith(fieldDefaultPrefix)) continue;
                String field = pv.getName().substring(fieldDefaultPrefix.length());
                if (this.getPropertyAccessor().isWritableProperty(field) && !mpvs.contains(field)) {
                    mpvs.add(field, pv.getValue());
                }
                mpvs.removePropertyValue(pv);
            }
        }
    }

    protected void checkFieldMarkers(MutablePropertyValues mpvs) {
        String fieldMarkerPrefix = this.getFieldMarkerPrefix();
        if (fieldMarkerPrefix != null) {
            PropertyValue[] pvArray;
            for (PropertyValue pv : pvArray = mpvs.getPropertyValues()) {
                if (!pv.getName().startsWith(fieldMarkerPrefix)) continue;
                String field = pv.getName().substring(fieldMarkerPrefix.length());
                if (this.getPropertyAccessor().isWritableProperty(field) && !mpvs.contains(field)) {
                    Class<?> fieldType = this.getPropertyAccessor().getPropertyType(field);
                    mpvs.add(field, this.getEmptyValue(field, fieldType));
                }
                mpvs.removePropertyValue(pv);
            }
        }
    }

    @Nullable
    protected Object getEmptyValue(String field, @Nullable Class<?> fieldType) {
        return fieldType != null ? this.getEmptyValue(fieldType) : null;
    }

    @Nullable
    public Object getEmptyValue(Class<?> fieldType) {
        block6: {
            try {
                if (Boolean.TYPE == fieldType || Boolean.class == fieldType) {
                    return Boolean.FALSE;
                }
                if (fieldType.isArray()) {
                    return Array.newInstance(fieldType.getComponentType(), 0);
                }
                if (Collection.class.isAssignableFrom(fieldType)) {
                    return CollectionFactory.createCollection(fieldType, 0);
                }
                if (Map.class.isAssignableFrom(fieldType)) {
                    return CollectionFactory.createMap(fieldType, 0);
                }
            }
            catch (IllegalArgumentException ex) {
                if (!logger.isDebugEnabled()) break block6;
                logger.debug("Failed to create default value - falling back to null: " + ex.getMessage());
            }
        }
        return null;
    }

    protected void bindMultipart(Map<String, List<MultipartFile>> multipartFiles, MutablePropertyValues mpvs) {
        multipartFiles.forEach((key, values) -> {
            if (values.size() == 1) {
                MultipartFile value = (MultipartFile)values.get(0);
                if (this.isBindEmptyMultipartFiles() || !value.isEmpty()) {
                    mpvs.add((String)key, value);
                }
            } else {
                mpvs.add((String)key, values);
            }
        });
    }
}

