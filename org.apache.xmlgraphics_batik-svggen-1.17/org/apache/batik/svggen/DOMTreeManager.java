/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.g2d.GraphicContext
 */
package org.apache.batik.svggen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.apache.batik.svggen.DOMGroupManager;
import org.apache.batik.svggen.ErrorConstants;
import org.apache.batik.svggen.ExtensionHandler;
import org.apache.batik.svggen.SVGBufferedImageOp;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphicContext;
import org.apache.batik.svggen.SVGGraphicContextConverter;
import org.apache.batik.svggen.SVGGraphics2DRuntimeException;
import org.apache.batik.svggen.SVGSyntax;
import org.apache.batik.svggen.StyleHandler;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DOMTreeManager
implements SVGSyntax,
ErrorConstants {
    int maxGCOverrides;
    protected final List groupManagers = Collections.synchronizedList(new ArrayList());
    protected List genericDefSet = new LinkedList();
    SVGGraphicContext defaultGC;
    protected Element topLevelGroup;
    SVGGraphicContextConverter gcConverter;
    protected SVGGeneratorContext generatorContext;
    protected SVGBufferedImageOp filterConverter;
    protected List otherDefs;

    public DOMTreeManager(GraphicContext gc, SVGGeneratorContext generatorContext, int maxGCOverrides) {
        if (gc == null) {
            throw new SVGGraphics2DRuntimeException("gc should not be null");
        }
        if (maxGCOverrides <= 0) {
            throw new SVGGraphics2DRuntimeException("maxGcOverrides should be greater than zero");
        }
        if (generatorContext == null) {
            throw new SVGGraphics2DRuntimeException("generatorContext should not be null");
        }
        this.generatorContext = generatorContext;
        this.maxGCOverrides = maxGCOverrides;
        this.recycleTopLevelGroup();
        this.defaultGC = this.gcConverter.toSVG(gc);
    }

    public void addGroupManager(DOMGroupManager groupManager) {
        if (groupManager != null) {
            this.groupManagers.add(groupManager);
        }
    }

    public void removeGroupManager(DOMGroupManager groupManager) {
        if (groupManager != null) {
            this.groupManagers.remove(groupManager);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void appendGroup(Element group, DOMGroupManager groupManager) {
        this.topLevelGroup.appendChild(group);
        List list = this.groupManagers;
        synchronized (list) {
            int nManagers = this.groupManagers.size();
            for (Object groupManager1 : this.groupManagers) {
                DOMGroupManager gm = (DOMGroupManager)groupManager1;
                if (gm == groupManager) continue;
                gm.recycleCurrentGroup();
            }
        }
    }

    protected void recycleTopLevelGroup() {
        this.recycleTopLevelGroup(true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void recycleTopLevelGroup(boolean recycleConverters) {
        List list = this.groupManagers;
        synchronized (list) {
            int nManagers = this.groupManagers.size();
            for (Object groupManager : this.groupManagers) {
                DOMGroupManager gm = (DOMGroupManager)groupManager;
                gm.recycleCurrentGroup();
            }
        }
        this.topLevelGroup = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "g");
        if (recycleConverters) {
            this.filterConverter = new SVGBufferedImageOp(this.generatorContext);
            this.gcConverter = new SVGGraphicContextConverter(this.generatorContext);
        }
    }

    public void setTopLevelGroup(Element topLevelGroup) {
        if (topLevelGroup == null) {
            throw new SVGGraphics2DRuntimeException("topLevelGroup should not be null");
        }
        if (!"g".equalsIgnoreCase(topLevelGroup.getTagName())) {
            throw new SVGGraphics2DRuntimeException("topLevelGroup should be a group <g>");
        }
        this.recycleTopLevelGroup(false);
        this.topLevelGroup = topLevelGroup;
    }

    public Element getRoot() {
        return this.getRoot(null);
    }

    public Element getRoot(Element svgElement) {
        Element svg = svgElement;
        if (svg == null) {
            svg = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "svg");
        }
        if (this.gcConverter.getCompositeConverter().getAlphaCompositeConverter().requiresBackgroundAccess()) {
            svg.setAttributeNS(null, "enable-background", "new");
        }
        if (this.generatorContext.generatorComment != null) {
            Comment generatorComment = this.generatorContext.domFactory.createComment(this.generatorContext.generatorComment);
            svg.appendChild(generatorComment);
        }
        this.applyDefaultRenderingStyle(svg);
        svg.appendChild(this.getGenericDefinitions());
        svg.appendChild(this.getTopLevelGroup());
        return svg;
    }

    public void applyDefaultRenderingStyle(Element element) {
        Map groupDefaults = this.defaultGC.getGroupContext();
        this.generatorContext.styleHandler.setStyle(element, groupDefaults, this.generatorContext);
    }

    public Element getGenericDefinitions() {
        Element genericDefs = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "defs");
        for (Object aGenericDefSet : this.genericDefSet) {
            genericDefs.appendChild((Element)aGenericDefSet);
        }
        genericDefs.setAttributeNS(null, "id", "genericDefs");
        return genericDefs;
    }

    public ExtensionHandler getExtensionHandler() {
        return this.generatorContext.getExtensionHandler();
    }

    void setExtensionHandler(ExtensionHandler extensionHandler) {
        this.generatorContext.setExtensionHandler(extensionHandler);
    }

    public List getDefinitionSet() {
        List defSet = this.gcConverter.getDefinitionSet();
        defSet.removeAll(this.genericDefSet);
        defSet.addAll(this.filterConverter.getDefinitionSet());
        if (this.otherDefs != null) {
            defSet.addAll(this.otherDefs);
            this.otherDefs = null;
        }
        this.filterConverter = new SVGBufferedImageOp(this.generatorContext);
        this.gcConverter = new SVGGraphicContextConverter(this.generatorContext);
        return defSet;
    }

    public void addOtherDef(Element definition) {
        if (this.otherDefs == null) {
            this.otherDefs = new LinkedList();
        }
        this.otherDefs.add(definition);
    }

    public Element getTopLevelGroup() {
        boolean includeDefinitionSet = true;
        return this.getTopLevelGroup(includeDefinitionSet);
    }

    public Element getTopLevelGroup(boolean includeDefinitionSet) {
        List defSet;
        Element topLevelGroup = this.topLevelGroup;
        if (includeDefinitionSet && (defSet = this.getDefinitionSet()).size() > 0) {
            Element defElement = null;
            NodeList defsElements = topLevelGroup.getElementsByTagName("defs");
            if (defsElements.getLength() > 0) {
                defElement = (Element)defsElements.item(0);
            }
            if (defElement == null) {
                defElement = this.generatorContext.domFactory.createElementNS("http://www.w3.org/2000/svg", "defs");
                defElement.setAttributeNS(null, "id", this.generatorContext.idGenerator.generateID("defs"));
                topLevelGroup.insertBefore(defElement, topLevelGroup.getFirstChild());
            }
            for (Object aDefSet : defSet) {
                defElement.appendChild((Element)aDefSet);
            }
        }
        this.recycleTopLevelGroup(false);
        return topLevelGroup;
    }

    public SVGBufferedImageOp getFilterConverter() {
        return this.filterConverter;
    }

    public SVGGraphicContextConverter getGraphicContextConverter() {
        return this.gcConverter;
    }

    SVGGeneratorContext getGeneratorContext() {
        return this.generatorContext;
    }

    Document getDOMFactory() {
        return this.generatorContext.domFactory;
    }

    StyleHandler getStyleHandler() {
        return this.generatorContext.styleHandler;
    }
}

