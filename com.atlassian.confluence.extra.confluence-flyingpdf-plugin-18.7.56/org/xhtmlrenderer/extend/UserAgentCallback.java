/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.extend;

import org.xhtmlrenderer.resource.CSSResource;
import org.xhtmlrenderer.resource.ImageResource;
import org.xhtmlrenderer.resource.XMLResource;

public interface UserAgentCallback {
    public CSSResource getCSSResource(String var1);

    public ImageResource getImageResource(String var1);

    public XMLResource getXMLResource(String var1);

    public byte[] getBinaryResource(String var1);

    public boolean isVisited(String var1);

    public void setBaseURL(String var1);

    public String getBaseURL();

    public String resolveURI(String var1);
}

