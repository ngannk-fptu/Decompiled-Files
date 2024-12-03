/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.parser.ParseException
 *  org.apache.batik.parser.UnitProcessor
 *  org.apache.batik.parser.UnitProcessor$Context
 */
package org.apache.batik.bridge;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.UnitProcessor;
import org.w3c.dom.Element;

public abstract class UnitProcessor
extends org.apache.batik.parser.UnitProcessor {
    public static UnitProcessor.Context createContext(BridgeContext ctx, Element e) {
        return new DefaultContext(ctx, e);
    }

    public static float svgHorizontalCoordinateToObjectBoundingBox(String s, String attr, UnitProcessor.Context ctx) {
        return UnitProcessor.svgToObjectBoundingBox(s, attr, (short)2, ctx);
    }

    public static float svgVerticalCoordinateToObjectBoundingBox(String s, String attr, UnitProcessor.Context ctx) {
        return UnitProcessor.svgToObjectBoundingBox(s, attr, (short)1, ctx);
    }

    public static float svgOtherCoordinateToObjectBoundingBox(String s, String attr, UnitProcessor.Context ctx) {
        return UnitProcessor.svgToObjectBoundingBox(s, attr, (short)0, ctx);
    }

    public static float svgHorizontalLengthToObjectBoundingBox(String s, String attr, UnitProcessor.Context ctx) {
        return UnitProcessor.svgLengthToObjectBoundingBox(s, attr, (short)2, ctx);
    }

    public static float svgVerticalLengthToObjectBoundingBox(String s, String attr, UnitProcessor.Context ctx) {
        return UnitProcessor.svgLengthToObjectBoundingBox(s, attr, (short)1, ctx);
    }

    public static float svgOtherLengthToObjectBoundingBox(String s, String attr, UnitProcessor.Context ctx) {
        return UnitProcessor.svgLengthToObjectBoundingBox(s, attr, (short)0, ctx);
    }

    public static float svgLengthToObjectBoundingBox(String s, String attr, short d, UnitProcessor.Context ctx) {
        float v = UnitProcessor.svgToObjectBoundingBox(s, attr, d, ctx);
        if (v < 0.0f) {
            throw new BridgeException(UnitProcessor.getBridgeContext(ctx), ctx.getElement(), "length.negative", new Object[]{attr, s});
        }
        return v;
    }

    public static float svgToObjectBoundingBox(String s, String attr, short d, UnitProcessor.Context ctx) {
        try {
            return org.apache.batik.parser.UnitProcessor.svgToObjectBoundingBox((String)s, (String)attr, (short)d, (UnitProcessor.Context)ctx);
        }
        catch (ParseException pEx) {
            throw new BridgeException(UnitProcessor.getBridgeContext(ctx), ctx.getElement(), (Exception)((Object)pEx), "attribute.malformed", new Object[]{attr, s, pEx});
        }
    }

    public static float svgHorizontalLengthToUserSpace(String s, String attr, UnitProcessor.Context ctx) {
        return UnitProcessor.svgLengthToUserSpace(s, attr, (short)2, ctx);
    }

    public static float svgVerticalLengthToUserSpace(String s, String attr, UnitProcessor.Context ctx) {
        return UnitProcessor.svgLengthToUserSpace(s, attr, (short)1, ctx);
    }

    public static float svgOtherLengthToUserSpace(String s, String attr, UnitProcessor.Context ctx) {
        return UnitProcessor.svgLengthToUserSpace(s, attr, (short)0, ctx);
    }

    public static float svgHorizontalCoordinateToUserSpace(String s, String attr, UnitProcessor.Context ctx) {
        return UnitProcessor.svgToUserSpace(s, attr, (short)2, ctx);
    }

    public static float svgVerticalCoordinateToUserSpace(String s, String attr, UnitProcessor.Context ctx) {
        return UnitProcessor.svgToUserSpace(s, attr, (short)1, ctx);
    }

    public static float svgOtherCoordinateToUserSpace(String s, String attr, UnitProcessor.Context ctx) {
        return UnitProcessor.svgToUserSpace(s, attr, (short)0, ctx);
    }

    public static float svgLengthToUserSpace(String s, String attr, short d, UnitProcessor.Context ctx) {
        float v = UnitProcessor.svgToUserSpace(s, attr, d, ctx);
        if (v < 0.0f) {
            throw new BridgeException(UnitProcessor.getBridgeContext(ctx), ctx.getElement(), "length.negative", new Object[]{attr, s});
        }
        return v;
    }

    public static float svgToUserSpace(String s, String attr, short d, UnitProcessor.Context ctx) {
        try {
            return org.apache.batik.parser.UnitProcessor.svgToUserSpace((String)s, (String)attr, (short)d, (UnitProcessor.Context)ctx);
        }
        catch (ParseException pEx) {
            throw new BridgeException(UnitProcessor.getBridgeContext(ctx), ctx.getElement(), (Exception)((Object)pEx), "attribute.malformed", new Object[]{attr, s, pEx});
        }
    }

    protected static BridgeContext getBridgeContext(UnitProcessor.Context ctx) {
        if (ctx instanceof DefaultContext) {
            return ((DefaultContext)ctx).ctx;
        }
        return null;
    }

    public static class DefaultContext
    implements UnitProcessor.Context {
        protected Element e;
        protected BridgeContext ctx;

        public DefaultContext(BridgeContext ctx, Element e) {
            this.ctx = ctx;
            this.e = e;
        }

        public Element getElement() {
            return this.e;
        }

        public float getPixelUnitToMillimeter() {
            return this.ctx.getUserAgent().getPixelUnitToMillimeter();
        }

        public float getPixelToMM() {
            return this.getPixelUnitToMillimeter();
        }

        public float getFontSize() {
            return CSSUtilities.getComputedStyle(this.e, 22).getFloatValue();
        }

        public float getXHeight() {
            return 0.5f;
        }

        public float getViewportWidth() {
            return this.ctx.getViewport(this.e).getWidth();
        }

        public float getViewportHeight() {
            return this.ctx.getViewport(this.e).getHeight();
        }
    }
}

