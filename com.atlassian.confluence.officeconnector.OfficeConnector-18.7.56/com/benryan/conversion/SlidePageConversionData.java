/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.conversion.convert.FileFormat
 *  com.atlassian.plugins.conversion.convert.bean.BeanFile
 */
package com.benryan.conversion;

import com.atlassian.plugins.conversion.convert.FileFormat;
import com.atlassian.plugins.conversion.convert.bean.BeanFile;
import com.benryan.conversion.SlideDocConversionData;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class SlidePageConversionData
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final SlideDocConversionData parent;
    public final UUID id;
    public final int pageNum;
    public final String name;
    public final FileFormat format;

    public SlidePageConversionData(SlideDocConversionData parent, UUID id, int pageNum, String name, FileFormat format) {
        this.parent = parent;
        this.id = id;
        this.pageNum = pageNum;
        this.name = name;
        this.format = format;
    }

    public SlidePageConversionData(SlideDocConversionData parent, BeanFile beanFile) {
        this(parent, beanFile.id, beanFile.pageNum, beanFile.name, beanFile.format);
    }

    public int getSlideNum() {
        return this.pageNum;
    }

    public SlideDocConversionData getParent() {
        return this.parent;
    }

    public int hashCode() {
        return Objects.hash(this.parent.getKey(), this.id, this.pageNum, this.name, this.format);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SlidePageConversionData)) {
            return false;
        }
        SlidePageConversionData that = (SlidePageConversionData)obj;
        return Objects.equals(this.parent.getKey(), that.parent.getKey()) && Objects.equals(this.id, that.id) && Objects.equals(this.pageNum, that.pageNum) && Objects.equals(this.name, that.name) && Objects.equals(this.format, that.format);
    }
}

