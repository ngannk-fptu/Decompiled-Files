/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.g2d.TransformStackElement
 *  org.apache.batik.util.SVGConstants
 */
package org.apache.batik.svggen;

import java.util.HashMap;
import java.util.Map;
import org.apache.batik.ext.awt.g2d.TransformStackElement;
import org.apache.batik.svggen.ErrorConstants;
import org.apache.batik.svggen.SVGGraphics2DRuntimeException;
import org.apache.batik.util.SVGConstants;

public class SVGGraphicContext
implements SVGConstants,
ErrorConstants {
    private static final String[] leafOnlyAttributes = new String[]{"opacity", "filter", "clip-path"};
    private static final String[] defaultValues = new String[]{"1", "none", "none"};
    private Map context;
    private Map groupContext;
    private Map graphicElementContext;
    private TransformStackElement[] transformStack;

    public SVGGraphicContext(Map context, TransformStackElement[] transformStack) {
        if (context == null) {
            throw new SVGGraphics2DRuntimeException("context map(s) should not be null");
        }
        if (transformStack == null) {
            throw new SVGGraphics2DRuntimeException("transformer stack should not be null");
        }
        this.context = context;
        this.transformStack = transformStack;
        this.computeGroupAndGraphicElementContext();
    }

    public SVGGraphicContext(Map groupContext, Map graphicElementContext, TransformStackElement[] transformStack) {
        if (groupContext == null || graphicElementContext == null) {
            throw new SVGGraphics2DRuntimeException("context map(s) should not be null");
        }
        if (transformStack == null) {
            throw new SVGGraphics2DRuntimeException("transformer stack should not be null");
        }
        this.groupContext = groupContext;
        this.graphicElementContext = graphicElementContext;
        this.transformStack = transformStack;
        this.computeContext();
    }

    public Map getContext() {
        return this.context;
    }

    public Map getGroupContext() {
        return this.groupContext;
    }

    public Map getGraphicElementContext() {
        return this.graphicElementContext;
    }

    public TransformStackElement[] getTransformStack() {
        return this.transformStack;
    }

    private void computeContext() {
        if (this.context != null) {
            return;
        }
        this.context = new HashMap(this.groupContext);
        this.context.putAll(this.graphicElementContext);
    }

    private void computeGroupAndGraphicElementContext() {
        if (this.groupContext != null) {
            return;
        }
        this.groupContext = new HashMap(this.context);
        this.graphicElementContext = new HashMap();
        for (int i = 0; i < leafOnlyAttributes.length; ++i) {
            Object attrValue = this.groupContext.get(leafOnlyAttributes[i]);
            if (attrValue == null) continue;
            if (!attrValue.equals(defaultValues[i])) {
                this.graphicElementContext.put(leafOnlyAttributes[i], attrValue);
            }
            this.groupContext.remove(leafOnlyAttributes[i]);
        }
    }
}

