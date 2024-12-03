/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.spi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.batik.ext.awt.image.spi.ErrorConstants;
import org.apache.batik.ext.awt.image.spi.RegistryEntry;

public abstract class AbstractRegistryEntry
implements RegistryEntry,
ErrorConstants {
    String name;
    float priority;
    List exts;
    List mimeTypes;

    public AbstractRegistryEntry(String name, float priority, String[] exts, String[] mimeTypes) {
        this.name = name;
        this.priority = priority;
        this.exts = new ArrayList(exts.length);
        for (String ext : exts) {
            this.exts.add(ext);
        }
        this.exts = Collections.unmodifiableList(this.exts);
        this.mimeTypes = new ArrayList(mimeTypes.length);
        for (String mimeType : mimeTypes) {
            this.mimeTypes.add(mimeType);
        }
        this.mimeTypes = Collections.unmodifiableList(this.mimeTypes);
    }

    public AbstractRegistryEntry(String name, float priority, String ext, String mimeType) {
        this.name = name;
        this.priority = priority;
        this.exts = new ArrayList(1);
        this.exts.add(ext);
        this.exts = Collections.unmodifiableList(this.exts);
        this.mimeTypes = new ArrayList(1);
        this.mimeTypes.add(mimeType);
        this.mimeTypes = Collections.unmodifiableList(this.mimeTypes);
    }

    @Override
    public String getFormatName() {
        return this.name;
    }

    @Override
    public List getStandardExtensions() {
        return this.exts;
    }

    @Override
    public List getMimeTypes() {
        return this.mimeTypes;
    }

    @Override
    public float getPriority() {
        return this.priority;
    }
}

