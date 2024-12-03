/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.metadata.TikaPagedText;
import org.apache.tika.renderer.RenderResult;
import org.apache.tika.renderer.RenderResults;

public class PageBasedRenderResults
extends RenderResults {
    Map<Integer, List<RenderResult>> results = new HashMap<Integer, List<RenderResult>>();

    public PageBasedRenderResults(TemporaryResources tmp) {
        super(tmp);
    }

    @Override
    public void add(RenderResult result) {
        Integer page = result.getMetadata().getInt(TikaPagedText.PAGE_NUMBER);
        if (page != null) {
            List<RenderResult> pageResults = this.results.get(page);
            if (pageResults == null) {
                pageResults = new ArrayList<RenderResult>();
                this.results.put(page, pageResults);
            }
            pageResults.add(result);
        }
        super.add(result);
    }

    public List<RenderResult> getPage(int pageNumber) {
        return this.results.get(pageNumber);
    }
}

