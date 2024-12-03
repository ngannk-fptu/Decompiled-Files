/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.zip.Adler32;
import java.util.zip.Checksum;
import org.apache.batik.svggen.DOMTreeManager;
import org.apache.batik.svggen.ErrorConstants;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.svggen.SVGSyntax;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class ImageCacher
implements SVGSyntax,
ErrorConstants {
    DOMTreeManager domTreeManager = null;
    Map imageCache = new HashMap();
    Checksum checkSum = new Adler32();

    public ImageCacher() {
    }

    public ImageCacher(DOMTreeManager domTreeManager) {
        this();
        this.setDOMTreeManager(domTreeManager);
    }

    public void setDOMTreeManager(DOMTreeManager domTreeManager) {
        if (domTreeManager == null) {
            throw new IllegalArgumentException();
        }
        this.domTreeManager = domTreeManager;
    }

    public DOMTreeManager getDOMTreeManager() {
        return this.domTreeManager;
    }

    public String lookup(ByteArrayOutputStream os, int width, int height, SVGGeneratorContext ctx) throws SVGGraphics2DIOException {
        int checksum = this.getChecksum(os.toByteArray());
        Integer key = checksum;
        String href = null;
        Object data = this.getCacheableData(os);
        LinkedList<ImageCacheEntry> list = (LinkedList<ImageCacheEntry>)this.imageCache.get(key);
        if (list == null) {
            list = new LinkedList<ImageCacheEntry>();
            this.imageCache.put(key, list);
        } else {
            ListIterator i = list.listIterator(0);
            while (i.hasNext()) {
                ImageCacheEntry entry = (ImageCacheEntry)i.next();
                if (entry.checksum != checksum || !this.imagesMatch(entry.src, data)) continue;
                href = entry.href;
                break;
            }
        }
        if (href == null) {
            ImageCacheEntry newEntry = this.createEntry(checksum, data, width, height, ctx);
            list.add(newEntry);
            href = newEntry.href;
        }
        return href;
    }

    abstract Object getCacheableData(ByteArrayOutputStream var1);

    abstract boolean imagesMatch(Object var1, Object var2) throws SVGGraphics2DIOException;

    abstract ImageCacheEntry createEntry(int var1, Object var2, int var3, int var4, SVGGeneratorContext var5) throws SVGGraphics2DIOException;

    int getChecksum(byte[] data) {
        this.checkSum.reset();
        this.checkSum.update(data, 0, data.length);
        return (int)this.checkSum.getValue();
    }

    public static class External
    extends ImageCacher {
        private String imageDir;
        private String prefix;
        private String suffix;

        public External(String imageDir, String prefix, String suffix) {
            this.imageDir = imageDir;
            this.prefix = prefix;
            this.suffix = suffix;
        }

        @Override
        Object getCacheableData(ByteArrayOutputStream os) {
            return os;
        }

        @Override
        boolean imagesMatch(Object o1, Object o2) throws SVGGraphics2DIOException {
            boolean match = false;
            FileInputStream imageStream = null;
            try {
                imageStream = new FileInputStream((File)o1);
                int imageLen = imageStream.available();
                byte[] imageBytes = new byte[imageLen];
                byte[] candidateBytes = ((ByteArrayOutputStream)o2).toByteArray();
                for (int bytesRead = 0; bytesRead != imageLen; bytesRead += imageStream.read(imageBytes, bytesRead, imageLen - bytesRead)) {
                }
                match = Arrays.equals(imageBytes, candidateBytes);
            }
            catch (IOException e) {
                throw new SVGGraphics2DIOException("could not read image File " + ((File)o1).getName());
            }
            finally {
                try {
                    if (imageStream != null) {
                        imageStream.close();
                    }
                }
                catch (IOException iOException) {}
            }
            return match;
        }

        @Override
        ImageCacheEntry createEntry(int checksum, Object data, int width, int height, SVGGeneratorContext ctx) throws SVGGraphics2DIOException {
            File imageFile = null;
            try {
                while (imageFile == null) {
                    String fileId = ctx.idGenerator.generateID(this.prefix);
                    imageFile = new File(this.imageDir, fileId + this.suffix);
                    if (!imageFile.exists()) continue;
                    imageFile = null;
                }
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                ((ByteArrayOutputStream)data).writeTo(outputStream);
                ((ByteArrayOutputStream)data).close();
            }
            catch (IOException e) {
                throw new SVGGraphics2DIOException("could not write image File " + imageFile.getName());
            }
            return new ImageCacheEntry(checksum, imageFile, imageFile.getName());
        }
    }

    public static class Embedded
    extends ImageCacher {
        @Override
        public void setDOMTreeManager(DOMTreeManager domTreeManager) {
            if (this.domTreeManager != domTreeManager) {
                this.domTreeManager = domTreeManager;
                this.imageCache = new HashMap();
            }
        }

        @Override
        Object getCacheableData(ByteArrayOutputStream os) {
            return "data:image/png;base64," + os.toString();
        }

        @Override
        boolean imagesMatch(Object o1, Object o2) {
            return o1.equals(o2);
        }

        @Override
        ImageCacheEntry createEntry(int checksum, Object data, int width, int height, SVGGeneratorContext ctx) {
            String id = ctx.idGenerator.generateID("image");
            this.addToTree(id, (String)data, width, height, ctx);
            return new ImageCacheEntry(checksum, data, "#" + id);
        }

        private void addToTree(String id, String href, int width, int height, SVGGeneratorContext ctx) {
            Document domFactory = this.domTreeManager.getDOMFactory();
            Element imageElement = domFactory.createElementNS("http://www.w3.org/2000/svg", "image");
            imageElement.setAttributeNS(null, "id", id);
            imageElement.setAttributeNS(null, "width", Integer.toString(width));
            imageElement.setAttributeNS(null, "height", Integer.toString(height));
            imageElement.setAttributeNS("http://www.w3.org/1999/xlink", "xlink:href", href);
            this.domTreeManager.addOtherDef(imageElement);
        }
    }

    private static class ImageCacheEntry {
        public int checksum;
        public Object src;
        public String href;

        ImageCacheEntry(int checksum, Object src, String href) {
            this.checksum = checksum;
            this.src = src;
            this.href = href;
        }
    }
}

