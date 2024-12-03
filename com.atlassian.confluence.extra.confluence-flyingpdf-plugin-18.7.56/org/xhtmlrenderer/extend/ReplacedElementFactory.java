/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.extend;

import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;

public interface ReplacedElementFactory {
    public ReplacedElement createReplacedElement(LayoutContext var1, BlockBox var2, UserAgentCallback var3, int var4, int var5);

    public void reset();

    public void remove(Element var1);

    public void setFormSubmissionListener(FormSubmissionListener var1);
}

