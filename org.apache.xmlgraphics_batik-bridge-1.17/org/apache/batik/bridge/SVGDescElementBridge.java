/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.bridge;

import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.SVGDescriptiveElementBridge;

public class SVGDescElementBridge
extends SVGDescriptiveElementBridge {
    @Override
    public String getLocalName() {
        return "desc";
    }

    @Override
    public Bridge getInstance() {
        return new SVGDescElementBridge();
    }
}

