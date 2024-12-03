/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata;

import org.apache.tika.metadata.Property;

public interface Rendering {
    public static final String RENDERING_PREFIX = "rendering:";
    public static final Property RENDERED_BY = Property.externalTextBag("rendering:Rendered-By");
    public static final Property RENDERED_MS = Property.externalReal("rendering:rendering-time-ms");
}

