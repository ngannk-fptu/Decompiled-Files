/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.plugins.tiff;

import com.twelvemonkeys.lang.Validate;
import java.nio.ByteOrder;
import java.util.Arrays;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public final class TIFFStreamMetadata
extends IIOMetadata {
    public static final String SUN_NATIVE_STREAM_METADATA_FORMAT_NAME = "com_sun_media_imageio_plugins_tiff_stream_1.0";
    ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

    public TIFFStreamMetadata() {
        super(false, SUN_NATIVE_STREAM_METADATA_FORMAT_NAME, null, null, null);
    }

    TIFFStreamMetadata(ByteOrder byteOrder) {
        this();
        this.byteOrder = byteOrder;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public Node getAsTree(String string) {
        Validate.isTrue((boolean)this.nativeMetadataFormatName.equals(string), (Object)string, (String)"Unsupported metadata format: %s");
        IIOMetadataNode iIOMetadataNode = new IIOMetadataNode(this.nativeMetadataFormatName);
        IIOMetadataNode iIOMetadataNode2 = new IIOMetadataNode("ByteOrder");
        iIOMetadataNode.appendChild(iIOMetadataNode2);
        iIOMetadataNode2.setAttribute("value", this.byteOrder.toString());
        return iIOMetadataNode;
    }

    @Override
    public void mergeTree(String string, Node node) throws IIOInvalidTreeException {
        Validate.isTrue((boolean)this.nativeMetadataFormatName.equals(string), (Object)string, (String)"Unsupported metadata format: %s");
        Validate.notNull((Object)node, (String)"root");
        if (!this.nativeMetadataFormatName.equals(node.getNodeName())) {
            throw new IIOInvalidTreeException("Root must be " + this.nativeMetadataFormatName, node);
        }
        Node node2 = node.getFirstChild();
        if (node2 == null || !node2.getNodeName().equals("ByteOrder")) {
            throw new IIOInvalidTreeException("Missing \"ByteOrder\" node", node2);
        }
        NamedNodeMap namedNodeMap = node2.getAttributes();
        String string2 = namedNodeMap.getNamedItem("value").getNodeValue();
        if (string2 == null) {
            throw new IIOInvalidTreeException("Missing \"value\" attribute in \"ByteOrder\" node", node2);
        }
        ByteOrder byteOrder = this.getByteOrder(string2.toUpperCase());
        if (byteOrder == null) {
            throw new IIOInvalidTreeException("Unknown ByteOrder \"value\" attribute: " + string2, node2);
        }
        this.byteOrder = byteOrder;
    }

    private ByteOrder getByteOrder(String string) {
        switch (string) {
            case "BIG_ENDIAN": {
                return ByteOrder.BIG_ENDIAN;
            }
            case "LITTLE_ENDIAN": {
                return ByteOrder.LITTLE_ENDIAN;
            }
        }
        return null;
    }

    @Override
    public void reset() {
        this.byteOrder = ByteOrder.BIG_ENDIAN;
    }

    static void configureStreamByteOrder(IIOMetadata iIOMetadata, ImageOutputStream imageOutputStream) throws IIOInvalidTreeException {
        Validate.notNull((Object)imageOutputStream, (String)"imageOutput");
        if (iIOMetadata instanceof TIFFStreamMetadata) {
            imageOutputStream.setByteOrder(((TIFFStreamMetadata)iIOMetadata).byteOrder);
        } else if (iIOMetadata != null) {
            TIFFStreamMetadata tIFFStreamMetadata = new TIFFStreamMetadata();
            Validate.isTrue((boolean)Arrays.asList(iIOMetadata.getMetadataFormatNames()).contains(tIFFStreamMetadata.nativeMetadataFormatName), (String)String.format("Unsupported stream metadata format, expected %s: %s", tIFFStreamMetadata.nativeMetadataFormatName, Arrays.toString(iIOMetadata.getMetadataFormatNames())));
            tIFFStreamMetadata.mergeTree(tIFFStreamMetadata.nativeMetadataFormatName, iIOMetadata.getAsTree(tIFFStreamMetadata.nativeMetadataFormatName));
            imageOutputStream.setByteOrder(tIFFStreamMetadata.byteOrder);
        }
    }
}

