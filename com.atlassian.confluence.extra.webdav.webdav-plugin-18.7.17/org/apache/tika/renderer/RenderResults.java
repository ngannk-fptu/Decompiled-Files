/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.renderer;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.renderer.RenderResult;

public class RenderResults
implements Closeable {
    private List<RenderResult> results = new ArrayList<RenderResult>();
    private final TemporaryResources tmp;

    public RenderResults(TemporaryResources tmp) {
        this.tmp = tmp;
    }

    public void add(RenderResult result) {
        this.tmp.addResource(result);
        this.results.add(result);
    }

    public List<RenderResult> getResults() {
        return this.results;
    }

    @Override
    public void close() throws IOException {
        this.tmp.close();
    }
}

