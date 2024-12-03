/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.hwpf.model.FieldsDocumentPart;
import org.apache.poi.hwpf.model.FileInformationBlock;
import org.apache.poi.hwpf.model.GenericPropertyNode;
import org.apache.poi.hwpf.model.PlexOfCps;
import org.apache.poi.hwpf.model.PlexOfField;
import org.apache.poi.util.Internal;

@Internal
public class FieldsTables {
    private static final int FLD_SIZE = 2;
    @Deprecated
    public static final int PLCFFLDATN = 0;
    @Deprecated
    public static final int PLCFFLDEDN = 1;
    @Deprecated
    public static final int PLCFFLDFTN = 2;
    @Deprecated
    public static final int PLCFFLDHDR = 3;
    @Deprecated
    public static final int PLCFFLDHDRTXBX = 4;
    @Deprecated
    public static final int PLCFFLDMOM = 5;
    @Deprecated
    public static final int PLCFFLDTXBX = 6;
    private Map<FieldsDocumentPart, PlexOfCps> _tables = new HashMap<FieldsDocumentPart, PlexOfCps>(FieldsDocumentPart.values().length);

    private static ArrayList<PlexOfField> toArrayList(PlexOfCps plexOfCps) {
        if (plexOfCps == null) {
            return new ArrayList<PlexOfField>();
        }
        ArrayList<PlexOfField> fields = new ArrayList<PlexOfField>(plexOfCps.length());
        for (int i = 0; i < plexOfCps.length(); ++i) {
            GenericPropertyNode propNode = plexOfCps.getProperty(i);
            PlexOfField plex = new PlexOfField(propNode);
            fields.add(plex);
        }
        return fields;
    }

    public FieldsTables(byte[] tableStream, FileInformationBlock fib) {
        for (FieldsDocumentPart part : FieldsDocumentPart.values()) {
            PlexOfCps plexOfCps = this.readPLCF(tableStream, fib, part);
            this._tables.put(part, plexOfCps);
        }
    }

    public ArrayList<PlexOfField> getFieldsPLCF(FieldsDocumentPart part) {
        return FieldsTables.toArrayList(this._tables.get((Object)part));
    }

    @Deprecated
    public ArrayList<PlexOfField> getFieldsPLCF(int partIndex) {
        return this.getFieldsPLCF(FieldsDocumentPart.values()[partIndex]);
    }

    private PlexOfCps readPLCF(byte[] tableStream, FileInformationBlock fib, FieldsDocumentPart documentPart) {
        int start = fib.getFieldsPlcfOffset(documentPart);
        int length = fib.getFieldsPlcfLength(documentPart);
        if (start <= 0 || length <= 0) {
            return null;
        }
        return new PlexOfCps(tableStream, start, length, 2);
    }

    private int savePlex(FileInformationBlock fib, FieldsDocumentPart part, PlexOfCps plexOfCps, ByteArrayOutputStream outputStream) throws IOException {
        if (plexOfCps == null || plexOfCps.length() == 0) {
            fib.setFieldsPlcfOffset(part, outputStream.size());
            fib.setFieldsPlcfLength(part, 0);
            return 0;
        }
        byte[] data = plexOfCps.toByteArray();
        int start = outputStream.size();
        int length = data.length;
        outputStream.write(data);
        fib.setFieldsPlcfOffset(part, start);
        fib.setFieldsPlcfLength(part, length);
        return length;
    }

    public void write(FileInformationBlock fib, ByteArrayOutputStream tableStream) throws IOException {
        for (FieldsDocumentPart part : FieldsDocumentPart.values()) {
            PlexOfCps plexOfCps = this._tables.get((Object)part);
            this.savePlex(fib, part, plexOfCps, tableStream);
        }
    }
}

