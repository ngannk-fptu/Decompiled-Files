/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.bridge;

import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.SVGDescriptiveElementBridge;

public class SVGTitleElementBridge
extends SVGDescriptiveElementBridge {
    @Override
    public String getLocalName() {
        return "title";
    }

    @Override
    public Bridge getInstance() {
        return new SVGTitleElementBridge();
    }
}

