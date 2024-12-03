/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.poi.hwpf.model.FileInformationBlock;
import org.apache.poi.hwpf.model.GenericPropertyNode;
import org.apache.poi.hwpf.model.NoteType;
import org.apache.poi.hwpf.model.PlexOfCps;
import org.apache.poi.hwpf.model.types.FRDAbstractType;
import org.apache.poi.util.Internal;

@Internal
public class NotesTables {
    private PlexOfCps descriptors = new PlexOfCps(FRDAbstractType.getSize());
    private final NoteType noteType;
    private PlexOfCps textPositions = new PlexOfCps(0);

    public NotesTables(NoteType noteType) {
        this.noteType = noteType;
        this.textPositions.addProperty(new GenericPropertyNode(0, 1, new byte[0]));
    }

    public NotesTables(NoteType noteType, byte[] tableStream, FileInformationBlock fib) {
        this.noteType = noteType;
        this.read(tableStream, fib);
    }

    public GenericPropertyNode getDescriptor(int index) {
        return this.descriptors.getProperty(index);
    }

    public int getDescriptorsCount() {
        return this.descriptors.length();
    }

    public GenericPropertyNode getTextPosition(int index) {
        return this.textPositions.getProperty(index);
    }

    private void read(byte[] tableStream, FileInformationBlock fib) {
        int referencesStart = fib.getNotesDescriptorsOffset(this.noteType);
        int referencesLength = fib.getNotesDescriptorsSize(this.noteType);
        if (referencesStart != 0 && referencesLength != 0) {
            this.descriptors = new PlexOfCps(tableStream, referencesStart, referencesLength, FRDAbstractType.getSize());
        }
        int textPositionsStart = fib.getNotesTextPositionsOffset(this.noteType);
        int textPositionsLength = fib.getNotesTextPositionsSize(this.noteType);
        if (textPositionsStart != 0 && textPositionsLength != 0) {
            this.textPositions = new PlexOfCps(tableStream, textPositionsStart, textPositionsLength, 0);
        }
    }

    public void writeRef(FileInformationBlock fib, ByteArrayOutputStream tableStream) throws IOException {
        if (this.descriptors == null || this.descriptors.length() == 0) {
            fib.setNotesDescriptorsOffset(this.noteType, tableStream.size());
            fib.setNotesDescriptorsSize(this.noteType, 0);
            return;
        }
        int start = tableStream.size();
        tableStream.write(this.descriptors.toByteArray());
        int end = tableStream.size();
        fib.setNotesDescriptorsOffset(this.noteType, start);
        fib.setNotesDescriptorsSize(this.noteType, end - start);
    }

    public void writeTxt(FileInformationBlock fib, ByteArrayOutputStream tableStream) throws IOException {
        if (this.textPositions == null || this.textPositions.length() == 0) {
            fib.setNotesTextPositionsOffset(this.noteType, tableStream.size());
            fib.setNotesTextPositionsSize(this.noteType, 0);
            return;
        }
        int start = tableStream.size();
        tableStream.write(this.textPositions.toByteArray());
        int end = tableStream.size();
        fib.setNotesTextPositionsOffset(this.noteType, start);
        fib.setNotesTextPositionsSize(this.noteType, end - start);
    }
}

