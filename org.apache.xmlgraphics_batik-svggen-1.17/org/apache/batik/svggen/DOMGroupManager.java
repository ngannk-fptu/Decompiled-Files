/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.g2d.GraphicContext
 *  org.apache.batik.ext.awt.g2d.TransformStackElement
 */
package org.apache.batik.svggen;

import java.util.HashMap;
import java.util.Map;
import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.apache.batik.ext.awt.g2d.TransformStackElement;
import org.apache.batik.svggen.DOMTreeManager;
import org.apache.batik.svggen.ErrorConstants;
import org.apache.batik.svggen.SVGAttribute;
import org.apache.batik.svggen.SVGAttributeMap;
import org.apache.batik.svggen.SVGGraphicContext;
import org.apache.batik.svggen.SVGGraphics2DRuntimeException;
import org.apache.batik.svggen.SVGSyntax;
import org.w3c.dom.Element;

public class DOMGroupManager
implements SVGSyntax,
ErrorConstants {
    public static final short DRAW = 1;
    public static final short FILL = 16;
    protected GraphicContext gc;
    protected DOMTreeManager domTreeManager;
    protected SVGGraphicContext groupGC;
    protected Element currentGroup;

    public DOMGroupManager(GraphicContext gc, DOMTreeManager domTreeManager) {
        if (gc == null) {
            throw new SVGGraphics2DRuntimeException("gc should not be null");
        }
        if (domTreeManager == null) {
            throw new SVGGraphics2DRuntimeException("domTreeManager should not be null");
        }
        this.gc = gc;
        this.domTreeManager = domTreeManager;
        this.recycleCurrentGroup();
        this.groupGC = domTreeManager.gcConverter.toSVG(gc);
    }

    void recycleCurrentGroup() {
        this.currentGroup = this.domTreeManager.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "g");
    }

    public void addElement(Element element) {
        this.addElement(element, (short)17);
    }

    public void addElement(Element element, short method) {
        if (!this.currentGroup.hasChildNodes()) {
            this.currentGroup.appendChild(element);
            this.groupGC = this.domTreeManager.gcConverter.toSVG(this.gc);
            SVGGraphicContext deltaGC = DOMGroupManager.processDeltaGC(this.groupGC, this.domTreeManager.defaultGC);
            this.domTreeManager.getStyleHandler().setStyle(this.currentGroup, deltaGC.getGroupContext(), this.domTreeManager.getGeneratorContext());
            if ((method & 1) == 0) {
                deltaGC.getGraphicElementContext().put("stroke", "none");
            }
            if ((method & 0x10) == 0) {
                deltaGC.getGraphicElementContext().put("fill", "none");
            }
            this.domTreeManager.getStyleHandler().setStyle(element, deltaGC.getGraphicElementContext(), this.domTreeManager.getGeneratorContext());
            this.setTransform(this.currentGroup, deltaGC.getTransformStack());
            this.domTreeManager.appendGroup(this.currentGroup, this);
        } else if (this.gc.isTransformStackValid()) {
            SVGGraphicContext elementGC = this.domTreeManager.gcConverter.toSVG(this.gc);
            SVGGraphicContext deltaGC = DOMGroupManager.processDeltaGC(elementGC, this.groupGC);
            this.trimContextForElement(deltaGC, element);
            if (this.countOverrides(deltaGC) <= this.domTreeManager.maxGCOverrides) {
                this.currentGroup.appendChild(element);
                if ((method & 1) == 0) {
                    deltaGC.getContext().put("stroke", "none");
                }
                if ((method & 0x10) == 0) {
                    deltaGC.getContext().put("fill", "none");
                }
                this.domTreeManager.getStyleHandler().setStyle(element, deltaGC.getContext(), this.domTreeManager.getGeneratorContext());
                this.setTransform(element, deltaGC.getTransformStack());
            } else {
                this.currentGroup = this.domTreeManager.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "g");
                this.addElement(element, method);
            }
        } else {
            this.currentGroup = this.domTreeManager.getDOMFactory().createElementNS("http://www.w3.org/2000/svg", "g");
            this.gc.validateTransformStack();
            this.addElement(element, method);
        }
    }

    protected int countOverrides(SVGGraphicContext deltaGC) {
        return deltaGC.getGroupContext().size();
    }

    protected void trimContextForElement(SVGGraphicContext svgGC, Element element) {
        String tag = element.getTagName();
        Map groupAttrMap = svgGC.getGroupContext();
        if (tag != null) {
            for (Object o : groupAttrMap.keySet()) {
                String attrName = (String)o;
                SVGAttribute attr = SVGAttributeMap.get(attrName);
                if (attr == null || attr.appliesTo(tag)) continue;
                groupAttrMap.remove(attrName);
            }
        }
    }

    protected void setTransform(Element element, TransformStackElement[] transformStack) {
        String transform = this.domTreeManager.gcConverter.toSVG(transformStack).trim();
        if (transform.length() > 0) {
            element.setAttributeNS(null, "transform", transform);
        }
    }

    static SVGGraphicContext processDeltaGC(SVGGraphicContext gc, SVGGraphicContext referenceGc) {
        Map groupDelta = DOMGroupManager.processDeltaMap(gc.getGroupContext(), referenceGc.getGroupContext());
        Map graphicElementDelta = gc.getGraphicElementContext();
        TransformStackElement[] gcTransformStack = gc.getTransformStack();
        TransformStackElement[] referenceStack = referenceGc.getTransformStack();
        int deltaStackLength = gcTransformStack.length - referenceStack.length;
        TransformStackElement[] deltaTransformStack = new TransformStackElement[deltaStackLength];
        System.arraycopy(gcTransformStack, referenceStack.length, deltaTransformStack, 0, deltaStackLength);
        SVGGraphicContext deltaGC = new SVGGraphicContext(groupDelta, graphicElementDelta, deltaTransformStack);
        return deltaGC;
    }

    static Map processDeltaMap(Map map, Map referenceMap) {
        HashMap<String, String> mapDelta = new HashMap<String, String>();
        for (Object o : map.keySet()) {
            String refValue;
            String key = (String)o;
            String value = (String)map.get(key);
            if (value.equals(refValue = (String)referenceMap.get(key))) continue;
            mapDelta.put(key, value);
        }
        return mapDelta;
    }
}

