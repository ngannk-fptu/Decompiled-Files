/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.resource.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.velocity.runtime.resource.util.StringResource;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

public class StringResourceRepositoryImpl
implements StringResourceRepository {
    protected Map resources = Collections.synchronizedMap(new HashMap());
    private String encoding = "UTF-8";

    public StringResource getStringResource(String name) {
        return (StringResource)this.resources.get(name);
    }

    public void putStringResource(String name, String body) {
        this.resources.put(name, new StringResource(body, this.getEncoding()));
    }

    public void putStringResource(String name, String body, String encoding) {
        this.resources.put(name, new StringResource(body, encoding));
    }

    public void removeStringResource(String name) {
        this.resources.remove(name);
    }

    public String getEncoding() {
        return this.encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}

