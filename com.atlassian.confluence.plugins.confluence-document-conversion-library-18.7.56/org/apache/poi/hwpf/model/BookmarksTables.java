/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.hwpf.model.FileInformationBlock;
import org.apache.poi.hwpf.model.GenericPropertyNode;
import org.apache.poi.hwpf.model.PlexOfCps;
import org.apache.poi.hwpf.model.SttbUtils;
import org.apache.poi.hwpf.model.types.BKFAbstractType;
import org.apache.poi.util.Internal;

@Internal
public class BookmarksTables {
    private static final Logger LOG = LogManager.getLogger(BookmarksTables.class);
    private PlexOfCps descriptorsFirst = new PlexOfCps(4);
    private PlexOfCps descriptorsLim = new PlexOfCps(0);
    private List<String> names = new ArrayList<String>(0);

    public BookmarksTables(byte[] tableStream, FileInformationBlock fib) {
        this.read(tableStream, fib);
    }

    public void afterDelete(int startCp, int length) {
        this.descriptorsFirst.adjust(startCp, -length);
        this.descriptorsLim.adjust(startCp, -length);
        for (int i = 0; i < this.descriptorsFirst.length(); ++i) {
            GenericPropertyNode startNode = this.descriptorsFirst.getProperty(i);
            GenericPropertyNode endNode = this.descriptorsLim.getProperty(i);
            if (startNode.getStart() != endNode.getStart()) continue;
            LOG.atDebug().log("Removing bookmark #{}...", (Object)Unbox.box(i));
            this.remove(i);
            --i;
        }
    }

    public void afterInsert(int startCp, int length) {
        this.descriptorsFirst.adjust(startCp, length);
        this.descriptorsLim.adjust(startCp - 1, length);
    }

    public int getBookmarksCount() {
        return this.descriptorsFirst.length();
    }

    public GenericPropertyNode getDescriptorFirst(int index) throws IndexOutOfBoundsException {
        return this.descriptorsFirst.getProperty(index);
    }

    public int getDescriptorFirstIndex(GenericPropertyNode descriptorFirst) {
        return Arrays.asList(this.descriptorsFirst.toPropertiesArray()).indexOf(descriptorFirst);
    }

    public GenericPropertyNode getDescriptorLim(int index) throws IndexOutOfBoundsException {
        return this.descriptorsLim.getProperty(index);
    }

    public int getDescriptorsFirstCount() {
        return this.descriptorsFirst.length();
    }

    public int getDescriptorsLimCount() {
        return this.descriptorsLim.length();
    }

    public String getName(int index) {
        return this.names.get(index);
    }

    public int getNamesCount() {
        return this.names.size();
    }

    private void read(byte[] tableStream, FileInformationBlock fib) {
        int namesStart = fib.getFcSttbfbkmk();
        int namesLength = fib.getLcbSttbfbkmk();
        if (namesStart != 0 && namesLength != 0) {
            this.names = new ArrayList<String>(Arrays.asList(SttbUtils.readSttbfBkmk(tableStream, namesStart)));
        }
        int firstDescriptorsStart = fib.getFcPlcfbkf();
        int firstDescriptorsLength = fib.getLcbPlcfbkf();
        if (firstDescriptorsStart != 0 && firstDescriptorsLength != 0) {
            this.descriptorsFirst = new PlexOfCps(tableStream, firstDescriptorsStart, firstDescriptorsLength, BKFAbstractType.getSize());
        }
        int limDescriptorsStart = fib.getFcPlcfbkl();
        int limDescriptorsLength = fib.getLcbPlcfbkl();
        if (limDescriptorsStart != 0 && limDescriptorsLength != 0) {
            this.descriptorsLim = new PlexOfCps(tableStream, limDescriptorsStart, limDescriptorsLength, 0);
        }
    }

    public void remove(int index) {
        this.descriptorsFirst.remove(index);
        this.descriptorsLim.remove(index);
        this.names.remove(index);
    }

    public void setName(int index, String name) {
        this.names.set(index, name);
    }

    public void writePlcfBkmkf(FileInformationBlock fib, ByteArrayOutputStream tableStream) throws IOException {
        if (this.descriptorsFirst == null || this.descriptorsFirst.length() == 0) {
            fib.setFcPlcfbkf(0);
            fib.setLcbPlcfbkf(0);
            return;
        }
        int start = tableStream.size();
        tableStream.write(this.descriptorsFirst.toByteArray());
        int end = tableStream.size();
        fib.setFcPlcfbkf(start);
        fib.setLcbPlcfbkf(end - start);
    }

    public void writePlcfBkmkl(FileInformationBlock fib, ByteArrayOutputStream tableStream) throws IOException {
        if (this.descriptorsLim == null || this.descriptorsLim.length() == 0) {
            fib.setFcPlcfbkl(0);
            fib.setLcbPlcfbkl(0);
            return;
        }
        int start = tableStream.size();
        tableStream.write(this.descriptorsLim.toByteArray());
        int end = tableStream.size();
        fib.setFcPlcfbkl(start);
        fib.setLcbPlcfbkl(end - start);
    }

    public void writeSttbfBkmk(FileInformationBlock fib, ByteArrayOutputStream tableStream) throws IOException {
        if (this.names == null || this.names.isEmpty()) {
            fib.setFcSttbfbkmk(0);
            fib.setLcbSttbfbkmk(0);
            return;
        }
        int start = tableStream.size();
        SttbUtils.writeSttbfBkmk(this.names.toArray(new String[0]), tableStream);
        int end = tableStream.size();
        fib.setFcSttbfbkmk(start);
        fib.setLcbSttbfbkmk(end - start);
    }
}

