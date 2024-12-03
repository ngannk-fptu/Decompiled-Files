/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.aspose.words.ImageData
 *  com.aspose.words.Node
 */
package com.atlassian.plugins.conversion.confluence.dom.doc2wiki;

import com.aspose.words.ImageData;
import com.aspose.words.Node;
import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.WordImageData;
import java.awt.image.BufferedImage;

public class WordImageDataFactory {
    static WordImageData create(ImageData data, Node node, int width, int height, int originalWidth, int originalHeight) {
        return new DrawingMLImageDataWordImageData(data, node, width, height, originalWidth, originalHeight);
    }

    private static final class DrawingMLImageDataWordImageData
    extends AbstractWordImageData {
        private final ImageData imageData;

        private DrawingMLImageDataWordImageData(ImageData imageData, Node node, int width, int height, int originalWidth, int originalHeight) {
            super(node, width, height, originalWidth, originalHeight);
            this.imageData = imageData;
        }

        @Override
        public boolean isLinkOnly() throws Exception {
            return this.imageData.isLinkOnly();
        }

        @Override
        public int getImageType() throws Exception {
            return this.imageData.getImageType();
        }

        @Override
        public byte[] getImageBytes() throws Exception {
            return this.imageData.getImageBytes();
        }

        @Override
        public byte[] toByteArray() throws Exception {
            return this.imageData.toByteArray();
        }

        @Override
        public BufferedImage toImage() throws Exception {
            return this.imageData.toImage();
        }
    }

    private static abstract class AbstractWordImageData
    implements WordImageData {
        private final int width;
        private final int height;
        private final int originalWidth;
        private final int originalHeight;
        private final Node node;

        protected AbstractWordImageData(Node node, int width, int height, int originalWidth, int originalHeight) {
            this.node = node;
            this.width = width;
            this.height = height;
            this.originalWidth = originalWidth;
            this.originalHeight = originalHeight;
        }

        @Override
        public Node getNode() {
            return this.node;
        }

        @Override
        public int getWidth() {
            return this.width;
        }

        @Override
        public int getHeight() {
            return this.height;
        }

        @Override
        public int getOriginalWidth() {
            return this.originalWidth;
        }

        @Override
        public int getOriginalHeight() {
            return this.originalHeight;
        }
    }
}

