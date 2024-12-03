/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.ext.awt.image.spi.DefaultBrokenLinkProvider
 *  org.apache.batik.gvt.CompositeGraphicsNode
 *  org.apache.batik.gvt.GraphicsNode
 *  org.apache.batik.gvt.filter.GraphicsNodeRable8Bit
 */
package org.apache.batik.bridge;

import java.util.HashMap;
import org.apache.batik.bridge.ErrorConstants;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.spi.DefaultBrokenLinkProvider;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.filter.GraphicsNodeRable8Bit;

public class SVGBrokenLinkProvider
extends DefaultBrokenLinkProvider
implements ErrorConstants {
    public Filter getBrokenLinkImage(Object base, String code, Object[] params) {
        String message = SVGBrokenLinkProvider.formatMessage((Object)base, (String)code, (Object[])params);
        HashMap<String, String> props = new HashMap<String, String>();
        props.put("org.apache.batik.BrokenLinkImage", message);
        CompositeGraphicsNode cgn = new CompositeGraphicsNode();
        return new GraphicsNodeRable8Bit((GraphicsNode)cgn, props);
    }
}

