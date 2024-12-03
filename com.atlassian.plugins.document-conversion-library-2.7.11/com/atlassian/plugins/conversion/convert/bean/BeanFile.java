/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.convert.bean;

import com.atlassian.plugins.conversion.convert.FileFormat;
import java.util.UUID;

public class BeanFile {
    public UUID id;
    public int pageNum;
    public String name;
    public FileFormat format;

    private BeanFile() {
    }

    public BeanFile(UUID id, int pageNum, String name, FileFormat format) {
        this.id = id;
        this.pageNum = pageNum;
        this.name = name;
        this.format = format;
    }
}

