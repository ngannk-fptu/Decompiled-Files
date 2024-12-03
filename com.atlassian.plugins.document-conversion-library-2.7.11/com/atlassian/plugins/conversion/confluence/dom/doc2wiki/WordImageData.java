/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.aspose.words.Node
 */
package com.atlassian.plugins.conversion.confluence.dom.doc2wiki;

import com.aspose.words.Node;
import java.awt.image.BufferedImage;

public interface WordImageData {
    public boolean isLinkOnly() throws Exception;

    public int getImageType() throws Exception;

    public byte[] getImageBytes() throws Exception;

    public byte[] toByteArray() throws Exception;

    public BufferedImage toImage() throws Exception;

    public int getWidth();

    public int getHeight();

    public int getOriginalWidth();

    public int getOriginalHeight();

    public Node getNode();
}

