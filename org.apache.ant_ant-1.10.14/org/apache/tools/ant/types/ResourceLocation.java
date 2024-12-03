/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.net.URL;

public class ResourceLocation {
    private String publicId = null;
    private String location = null;
    private URL base = null;

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setBase(URL base) {
        this.base = base;
    }

    public String getPublicId() {
        return this.publicId;
    }

    public String getLocation() {
        return this.location;
    }

    public URL getBase() {
        return this.base;
    }
}

