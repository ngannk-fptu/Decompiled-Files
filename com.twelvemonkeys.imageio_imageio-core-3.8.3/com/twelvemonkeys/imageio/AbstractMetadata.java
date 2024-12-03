/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio;

import java.util.Arrays;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import org.w3c.dom.Node;

public abstract class AbstractMetadata
extends IIOMetadata
implements Cloneable {
    protected AbstractMetadata(boolean bl, String string, String string2, String[] stringArray, String[] stringArray2) {
        super(bl, string, string2, stringArray, stringArray2);
    }

    protected AbstractMetadata() {
        super(true, null, null, null, null);
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public Node getAsTree(String string) {
        this.validateFormatName(string);
        if (string.equals(this.nativeMetadataFormatName)) {
            return this.getNativeTree();
        }
        if (string.equals("javax_imageio_1.0")) {
            return this.getStandardTree();
        }
        return null;
    }

    protected Node getNativeTree() {
        throw new UnsupportedOperationException("getNativeTree");
    }

    @Override
    public void mergeTree(String string, Node node) throws IIOInvalidTreeException {
        this.assertMutable();
        this.validateFormatName(string);
        if (!node.getNodeName().equals(string)) {
            throw new IIOInvalidTreeException("Root must be " + string, node);
        }
    }

    @Override
    public void reset() {
        this.assertMutable();
    }

    protected final void assertMutable() {
        if (this.isReadOnly()) {
            throw new IllegalStateException("Metadata is read-only");
        }
    }

    protected final void validateFormatName(String string) {
        Object[] objectArray = this.getMetadataFormatNames();
        if (objectArray != null) {
            for (String string2 : objectArray) {
                if (!string2.equals(string)) continue;
                return;
            }
        }
        throw new IllegalArgumentException(String.format("Unsupported format name: \"%s\". Expected one of %s", string, Arrays.toString(objectArray)));
    }

    protected static String toListString(short[] sArray) {
        String string = Arrays.toString(sArray);
        return string.substring(1, string.length() - 1);
    }
}

