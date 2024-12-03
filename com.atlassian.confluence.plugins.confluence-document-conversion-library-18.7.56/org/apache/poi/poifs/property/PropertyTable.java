/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.property;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import org.apache.poi.poifs.filesystem.BATManaged;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.poifs.filesystem.POIFSStream;
import org.apache.poi.poifs.property.DirectoryProperty;
import org.apache.poi.poifs.property.Property;
import org.apache.poi.poifs.property.PropertyFactory;
import org.apache.poi.poifs.property.RootProperty;
import org.apache.poi.poifs.storage.HeaderBlock;
import org.apache.poi.util.IOUtils;

public final class PropertyTable
implements BATManaged {
    private static final Logger LOG = LogManager.getLogger(PropertyTable.class);
    private final HeaderBlock _header_block;
    private final List<Property> _properties = new ArrayList<Property>();
    private final POIFSBigBlockSize _bigBigBlockSize;

    public PropertyTable(HeaderBlock headerBlock) {
        this._header_block = headerBlock;
        this._bigBigBlockSize = headerBlock.getBigBlockSize();
        this.addProperty(new RootProperty());
    }

    public PropertyTable(HeaderBlock headerBlock, POIFSFileSystem filesystem) throws IOException {
        this(headerBlock, new POIFSStream(filesystem, headerBlock.getPropertyStart()));
    }

    PropertyTable(HeaderBlock headerBlock, Iterable<ByteBuffer> dataSource) throws IOException {
        this._header_block = headerBlock;
        this._bigBigBlockSize = headerBlock.getBigBlockSize();
        for (ByteBuffer bb : dataSource) {
            byte[] data;
            if (bb.hasArray() && bb.arrayOffset() == 0 && bb.array().length == this._bigBigBlockSize.getBigBlockSize()) {
                data = bb.array();
            } else {
                data = IOUtils.safelyAllocate(this._bigBigBlockSize.getBigBlockSize(), POIFSFileSystem.getMaxRecordLength());
                int toRead = data.length;
                if (bb.remaining() < this._bigBigBlockSize.getBigBlockSize()) {
                    LOG.atWarn().log("Short Property Block, {} bytes instead of the expected {}", (Object)Unbox.box(bb.remaining()), (Object)Unbox.box(this._bigBigBlockSize.getBigBlockSize()));
                    toRead = bb.remaining();
                }
                bb.get(data, 0, toRead);
            }
            PropertyFactory.convertToProperties(data, this._properties);
        }
        if (this._properties.get(0) != null) {
            this.populatePropertyTree((DirectoryProperty)this._properties.get(0));
        }
    }

    public void addProperty(Property property) {
        this._properties.add(property);
    }

    public void removeProperty(Property property) {
        this._properties.remove(property);
    }

    public RootProperty getRoot() {
        return (RootProperty)this._properties.get(0);
    }

    public int getStartBlock() {
        return this._header_block.getPropertyStart();
    }

    @Override
    public void setStartBlock(int index) {
        this._header_block.setPropertyStart(index);
    }

    @Override
    public int countBlocks() {
        long rawSize = (long)this._properties.size() * 128L;
        int blkSize = this._bigBigBlockSize.getBigBlockSize();
        int numBlocks = (int)(rawSize / (long)blkSize);
        if (rawSize % (long)blkSize != 0L) {
            ++numBlocks;
        }
        return numBlocks;
    }

    public void preWrite() {
        ArrayList<Property> pList = new ArrayList<Property>();
        int i = 0;
        for (Property p : this._properties) {
            if (p == null) continue;
            p.setIndex(i++);
            pList.add(p);
        }
        for (Property p : pList) {
            p.preWrite();
        }
    }

    public void write(POIFSStream stream) throws IOException {
        OutputStream os = stream.getOutputStream();
        for (Property property : this._properties) {
            if (property == null) continue;
            property.writeData(os);
        }
        os.close();
        if (this.getStartBlock() != stream.getStartBlock()) {
            this.setStartBlock(stream.getStartBlock());
        }
    }

    private void populatePropertyTree(DirectoryProperty root) throws IOException {
        int index = root.getChildIndex();
        if (!Property.isValidIndex(index)) {
            return;
        }
        Stack<Property> children = new Stack<Property>();
        children.push(this._properties.get(index));
        while (!children.empty()) {
            Property property = (Property)children.pop();
            if (property == null) continue;
            root.addChild(property);
            if (property.isDirectory()) {
                this.populatePropertyTree((DirectoryProperty)property);
            }
            if (this.isValidIndex(index = property.getPreviousChildIndex())) {
                children.push(this._properties.get(index));
            }
            if (!this.isValidIndex(index = property.getNextChildIndex())) continue;
            children.push(this._properties.get(index));
        }
    }

    private boolean isValidIndex(int index) {
        if (!Property.isValidIndex(index)) {
            return false;
        }
        if (index < 0 || index >= this._properties.size()) {
            LOG.atWarn().log("Property index {} outside the valid range 0..{}", (Object)Unbox.box(index), (Object)Unbox.box(this._properties.size()));
            return false;
        }
        return true;
    }
}

