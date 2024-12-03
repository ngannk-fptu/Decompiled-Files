/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.ComponentTransferFunction
 *  org.apache.batik.ext.awt.image.ConcreteComponentTransferFunction
 *  org.apache.batik.ext.awt.image.PadMode
 *  org.apache.batik.ext.awt.image.renderable.ComponentTransferRable8Bit
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.ext.awt.image.renderable.PadRable8Bit
 *  org.apache.batik.gvt.GraphicsNode
 */
package org.apache.batik.bridge;

import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.batik.bridge.AbstractSVGFilterPrimitiveElementBridge;
import org.apache.batik.bridge.AnimatableGenericSVGBridge;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.ext.awt.image.ComponentTransferFunction;
import org.apache.batik.ext.awt.image.ConcreteComponentTransferFunction;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.ComponentTransferRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.PadRable8Bit;
import org.apache.batik.gvt.GraphicsNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SVGFeComponentTransferElementBridge
extends AbstractSVGFilterPrimitiveElementBridge {
    @Override
    public String getLocalName() {
        return "feComponentTransfer";
    }

    @Override
    public Filter createFilter(BridgeContext ctx, Element filterElement, Element filteredElement, GraphicsNode filteredNode, Filter inputFilter, Rectangle2D filterRegion, Map filterMap) {
        Filter in = SVGFeComponentTransferElementBridge.getIn(filterElement, filteredElement, filteredNode, inputFilter, filterMap, ctx);
        if (in == null) {
            return null;
        }
        Rectangle2D defaultRegion = in.getBounds2D();
        Rectangle2D primitiveRegion = SVGUtilities.convertFilterPrimitiveRegion(filterElement, filteredElement, filteredNode, defaultRegion, filterRegion, ctx);
        ComponentTransferFunction funcR = null;
        ComponentTransferFunction funcG = null;
        ComponentTransferFunction funcB = null;
        ComponentTransferFunction funcA = null;
        for (Node n = filterElement.getFirstChild(); n != null; n = n.getNextSibling()) {
            Element e;
            Bridge bridge;
            if (n.getNodeType() != 1 || (bridge = ctx.getBridge(e = (Element)n)) == null || !(bridge instanceof SVGFeFuncElementBridge)) continue;
            SVGFeFuncElementBridge funcBridge = (SVGFeFuncElementBridge)bridge;
            ComponentTransferFunction func = funcBridge.createComponentTransferFunction(filterElement, e);
            if (funcBridge instanceof SVGFeFuncRElementBridge) {
                funcR = func;
                continue;
            }
            if (funcBridge instanceof SVGFeFuncGElementBridge) {
                funcG = func;
                continue;
            }
            if (funcBridge instanceof SVGFeFuncBElementBridge) {
                funcB = func;
                continue;
            }
            if (!(funcBridge instanceof SVGFeFuncAElementBridge)) continue;
            funcA = func;
        }
        ComponentTransferRable8Bit filter = new ComponentTransferRable8Bit(in, funcA, funcR, funcG, funcB);
        SVGFeComponentTransferElementBridge.handleColorInterpolationFilters((Filter)filter, filterElement);
        filter = new PadRable8Bit((Filter)filter, primitiveRegion, PadMode.ZERO_PAD);
        SVGFeComponentTransferElementBridge.updateFilterMap(filterElement, (Filter)filter, filterMap);
        return filter;
    }

    protected static abstract class SVGFeFuncElementBridge
    extends AnimatableGenericSVGBridge {
        protected SVGFeFuncElementBridge() {
        }

        public ComponentTransferFunction createComponentTransferFunction(Element filterElement, Element funcElement) {
            int type = SVGFeFuncElementBridge.convertType(funcElement, this.ctx);
            switch (type) {
                case 2: {
                    float[] v = SVGFeFuncElementBridge.convertTableValues(funcElement, this.ctx);
                    if (v == null) {
                        return ConcreteComponentTransferFunction.getIdentityTransfer();
                    }
                    return ConcreteComponentTransferFunction.getDiscreteTransfer((float[])v);
                }
                case 0: {
                    return ConcreteComponentTransferFunction.getIdentityTransfer();
                }
                case 4: {
                    float amplitude = AbstractSVGFilterPrimitiveElementBridge.convertNumber(funcElement, "amplitude", 1.0f, this.ctx);
                    float exponent = AbstractSVGFilterPrimitiveElementBridge.convertNumber(funcElement, "exponent", 1.0f, this.ctx);
                    float offset = AbstractSVGFilterPrimitiveElementBridge.convertNumber(funcElement, "offset", 0.0f, this.ctx);
                    return ConcreteComponentTransferFunction.getGammaTransfer((float)amplitude, (float)exponent, (float)offset);
                }
                case 3: {
                    float slope = AbstractSVGFilterPrimitiveElementBridge.convertNumber(funcElement, "slope", 1.0f, this.ctx);
                    float intercept = AbstractSVGFilterPrimitiveElementBridge.convertNumber(funcElement, "intercept", 0.0f, this.ctx);
                    return ConcreteComponentTransferFunction.getLinearTransfer((float)slope, (float)intercept);
                }
                case 1: {
                    float[] v = SVGFeFuncElementBridge.convertTableValues(funcElement, this.ctx);
                    if (v == null) {
                        return ConcreteComponentTransferFunction.getIdentityTransfer();
                    }
                    return ConcreteComponentTransferFunction.getTableTransfer((float[])v);
                }
            }
            throw new RuntimeException("invalid convertType:" + type);
        }

        protected static float[] convertTableValues(Element e, BridgeContext ctx) {
            String s = e.getAttributeNS(null, "tableValues");
            if (s.length() == 0) {
                return null;
            }
            StringTokenizer tokens = new StringTokenizer(s, " ,");
            float[] v = new float[tokens.countTokens()];
            try {
                int i = 0;
                while (tokens.hasMoreTokens()) {
                    v[i] = SVGUtilities.convertSVGNumber(tokens.nextToken());
                    ++i;
                }
            }
            catch (NumberFormatException nfEx) {
                throw new BridgeException(ctx, e, nfEx, "attribute.malformed", new Object[]{"tableValues", s});
            }
            return v;
        }

        protected static int convertType(Element e, BridgeContext ctx) {
            String s = e.getAttributeNS(null, "type");
            if (s.length() == 0) {
                throw new BridgeException(ctx, e, "attribute.missing", new Object[]{"type"});
            }
            if ("discrete".equals(s)) {
                return 2;
            }
            if ("identity".equals(s)) {
                return 0;
            }
            if ("gamma".equals(s)) {
                return 4;
            }
            if ("linear".equals(s)) {
                return 3;
            }
            if ("table".equals(s)) {
                return 1;
            }
            throw new BridgeException(ctx, e, "attribute.malformed", new Object[]{"type", s});
        }
    }

    public static class SVGFeFuncBElementBridge
    extends SVGFeFuncElementBridge {
        @Override
        public String getLocalName() {
            return "feFuncB";
        }
    }

    public static class SVGFeFuncGElementBridge
    extends SVGFeFuncElementBridge {
        @Override
        public String getLocalName() {
            return "feFuncG";
        }
    }

    public static class SVGFeFuncRElementBridge
    extends SVGFeFuncElementBridge {
        @Override
        public String getLocalName() {
            return "feFuncR";
        }
    }

    public static class SVGFeFuncAElementBridge
    extends SVGFeFuncElementBridge {
        @Override
        public String getLocalName() {
            return "feFuncA";
        }
    }
}

