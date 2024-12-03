/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf;

import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.RenderingContext;

public interface ITextReplacedElement
extends ReplacedElement {
    public void paint(RenderingContext var1, ITextOutputDevice var2, BlockBox var3);
}

