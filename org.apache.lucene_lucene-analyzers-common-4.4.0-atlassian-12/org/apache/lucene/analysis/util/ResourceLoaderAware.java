/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.analysis.util;

import java.io.IOException;
import org.apache.lucene.analysis.util.ResourceLoader;

public interface ResourceLoaderAware {
    public void inform(ResourceLoader var1) throws IOException;
}

