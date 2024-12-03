/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.ContextResource
 *  org.springframework.core.io.UrlResource
 */
package org.eclipse.gemini.blueprint.io;

import java.net.MalformedURLException;
import java.net.URL;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.UrlResource;

class UrlContextResource
extends UrlResource
implements ContextResource {
    private final String pathWithinContext;

    public UrlContextResource(String path) throws MalformedURLException {
        super(path);
        this.pathWithinContext = this.checkPath(path);
    }

    private String checkPath(String path) {
        return path.startsWith("/") ? path : "/" + path;
    }

    public UrlContextResource(URL url, String path) {
        super(url);
        this.pathWithinContext = this.checkPath(path);
    }

    public String getPathWithinContext() {
        return this.pathWithinContext;
    }
}

