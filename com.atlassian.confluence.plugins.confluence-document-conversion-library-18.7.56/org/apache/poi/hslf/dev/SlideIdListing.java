/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hslf.dev;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.hslf.record.Document;
import org.apache.poi.hslf.record.Notes;
import org.apache.poi.hslf.record.NotesAtom;
import org.apache.poi.hslf.record.PersistPtrHolder;
import org.apache.poi.hslf.record.PositionDependentRecord;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.Slide;
import org.apache.poi.hslf.record.SlideAtom;
import org.apache.poi.hslf.record.SlideListWithText;
import org.apache.poi.hslf.record.SlidePersistAtom;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFSlideShowImpl;
import org.apache.poi.util.LittleEndian;

public final class SlideIdListing {
    private static byte[] fileContents;

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Need to give a filename");
            System.exit(1);
        }
        try (HSLFSlideShowImpl hss = new HSLFSlideShowImpl(args[0]);
             HSLFSlideShow ss = new HSLFSlideShow(hss);){
            int i;
            SlideListWithText[] slwts;
            fileContents = hss.getUnderlyingBytes();
            Record[] records = hss.getRecords();
            Record[] latestRecords = ss.getMostRecentCoreRecords();
            Document document = null;
            for (Record latestRecord : latestRecords) {
                if (!(latestRecord instanceof Document)) continue;
                document = (Document)latestRecord;
            }
            System.out.println();
            for (SlideListWithText slwt : slwts = document.getSlideListWithTexts()) {
                Record[] cr;
                for (Record record : cr = slwt.getChildRecords()) {
                    if (!(record instanceof SlidePersistAtom)) continue;
                    SlidePersistAtom spa = (SlidePersistAtom)record;
                    System.out.println("SlidePersistAtom knows about slide:");
                    System.out.println("\t" + spa.getRefID());
                    System.out.println("\t" + spa.getSlideIdentifier());
                }
            }
            System.out.println();
            for (i = 0; i < latestRecords.length; ++i) {
                if (!(latestRecords[i] instanceof Slide)) continue;
                Slide s = (Slide)latestRecords[i];
                SlideAtom sa = s.getSlideAtom();
                System.out.println("Found the latest version of a slide record:");
                System.out.println("\tCore ID is " + s.getSheetId());
                System.out.println("\t(Core Records count is " + i + ")");
                System.out.println("\tDisk Position is " + s.getLastOnDiskOffset());
                System.out.println("\tMaster ID is " + sa.getMasterID());
                System.out.println("\tNotes ID is " + sa.getNotesID());
            }
            System.out.println();
            for (i = 0; i < latestRecords.length; ++i) {
                if (!(latestRecords[i] instanceof Notes)) continue;
                Notes n = (Notes)latestRecords[i];
                NotesAtom na = n.getNotesAtom();
                System.out.println("Found the latest version of a notes record:");
                System.out.println("\tCore ID is " + n.getSheetId());
                System.out.println("\t(Core Records count is " + i + ")");
                System.out.println("\tDisk Position is " + n.getLastOnDiskOffset());
                System.out.println("\tMatching slide is " + na.getSlideID());
            }
            System.out.println();
            int pos = 0;
            for (Record r : records) {
                if (r.getRecordType() == 6001L) {
                    System.out.println("Found PersistPtrFullBlock at " + pos + " (" + Integer.toHexString(pos) + ")");
                }
                if (r.getRecordType() == 6002L) {
                    System.out.println("Found PersistPtrIncrementalBlock at " + pos + " (" + Integer.toHexString(pos) + ")");
                    PersistPtrHolder pph = (PersistPtrHolder)r;
                    int[] sheetIDs = pph.getKnownSlideIDs();
                    Map<Integer, Integer> sheetOffsets = pph.getSlideLocationsLookup();
                    int[] nArray = sheetIDs;
                    int n = nArray.length;
                    for (int j = 0; j < n; ++j) {
                        Integer id = nArray[j];
                        Integer offset = sheetOffsets.get(id);
                        System.out.println("  Knows about sheet " + id);
                        System.out.println("    That sheet lives at " + offset);
                        Record atPos = SlideIdListing.findRecordAtPos(offset);
                        System.out.println("    The record at that pos is of type " + atPos.getRecordType());
                        System.out.println("    The record at that pos has class " + atPos.getClass().getName());
                        if (atPos instanceof PositionDependentRecord) continue;
                        System.out.println("    ** The record class isn't position aware! **");
                    }
                }
                UnsynchronizedByteArrayOutputStream baos = new UnsynchronizedByteArrayOutputStream();
                r.writeOut((OutputStream)baos);
                pos += baos.size();
            }
        }
        System.out.println();
    }

    public static Record findRecordAtPos(int pos) {
        long type = LittleEndian.getUShort(fileContents, pos + 2);
        long rlen = LittleEndian.getUInt(fileContents, pos + 4);
        return Record.createRecordForType(type, fileContents, pos, (int)rlen + 8);
    }
}

