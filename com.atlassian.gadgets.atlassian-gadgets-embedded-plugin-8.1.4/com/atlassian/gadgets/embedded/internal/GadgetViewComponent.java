/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.spec.GadgetSpec
 *  com.atlassian.gadgets.view.ModuleId
 *  com.atlassian.gadgets.view.ViewComponent
 *  com.atlassian.gadgets.view.ViewType
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package com.atlassian.gadgets.embedded.internal;

import com.atlassian.gadgets.spec.GadgetSpec;
import com.atlassian.gadgets.view.ModuleId;
import com.atlassian.gadgets.view.ViewComponent;
import com.atlassian.gadgets.view.ViewType;
import java.io.IOException;
import java.io.Writer;
import org.apache.commons.lang3.StringEscapeUtils;

class GadgetViewComponent
implements ViewComponent {
    private final ModuleId id;
    private final ViewType viewType;
    private final GadgetSpec spec;
    private final String renderedUrl;

    GadgetViewComponent(ModuleId id, ViewType viewType, GadgetSpec spec, String renderedUrl) {
        this.id = id;
        this.viewType = viewType;
        this.spec = spec;
        this.renderedUrl = renderedUrl;
    }

    public void writeTo(Writer writer) throws IOException {
        long rpcToken = Math.round(2.147483647E9 * Math.random());
        String renderedUrlWithRpcToken = StringEscapeUtils.escapeHtml4((String)(this.renderedUrl + "#rpctoken=" + rpcToken));
        String iframeId = "gadget-" + StringEscapeUtils.escapeHtml4((String)this.id.toString());
        writer.write("<iframe id=\"");
        writer.write(iframeId);
        writer.write("\" name=\"");
        writer.write(iframeId);
        writer.write("\" class=\"gadget\" src=\"");
        writer.write(renderedUrlWithRpcToken);
        writer.write("\" frameborder=\"0\"");
        writer.write(" scrolling=\"");
        writer.write(this.spec.isScrolling() ? "auto" : "no");
        writer.write("\" ");
        if (this.spec.getHeight() > 0) {
            writer.write(" height=\"");
            writer.write(Integer.toString(this.spec.getHeight()));
            writer.write("\"");
        }
        if (this.viewType == ViewType.CANVAS) {
            writer.write(" width=\"100%\"");
        } else if (this.spec.getWidth() > 0) {
            writer.write(" width=\"");
            writer.write(Integer.toString(this.spec.getWidth()));
            writer.write("\"");
        }
        writer.write(">");
        writer.write("<a href=\"");
        writer.write(renderedUrlWithRpcToken);
        writer.write("\">");
        writer.write(StringEscapeUtils.escapeHtml4((String)this.spec.getTitle()));
        writer.write("</a></iframe>");
    }
}

