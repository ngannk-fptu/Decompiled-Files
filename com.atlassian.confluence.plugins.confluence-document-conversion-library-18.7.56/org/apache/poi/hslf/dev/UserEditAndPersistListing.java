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
import org.apache.poi.hslf.record.CurrentUserAtom;
import org.apache.poi.hslf.record.PersistPtrHolder;
import org.apache.poi.hslf.record.PositionDependentRecord;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.UserEditAtom;
import org.apache.poi.hslf.usermodel.HSLFSlideShowImpl;
import org.apache.poi.util.LittleEndian;

public final class UserEditAndPersistListing {
    private static byte[] fileContents;

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Need to give a filename");
            System.exit(1);
        }
        try (HSLFSlideShowImpl ss = new HSLFSlideShowImpl(args[0]);){
            UnsynchronizedByteArrayOutputStream baos;
            fileContents = ss.getUnderlyingBytes();
            System.out.println();
            int pos = 0;
            for (Record r : ss.getRecords()) {
                if (r.getRecordType() == 6001L) {
                    System.out.println("Found PersistPtrFullBlock at " + pos + " (" + Integer.toHexString(pos) + ")");
                }
                if (r.getRecordType() == 6002L) {
                    System.out.println("Found PersistPtrIncrementalBlock at " + pos + " (" + Integer.toHexString(pos) + ")");
                    PersistPtrHolder pph = (PersistPtrHolder)r;
                    Map<Integer, Integer> sheetOffsets = pph.getSlideLocationsLookup();
                    for (int id : pph.getKnownSlideIDs()) {
                        Integer offset = sheetOffsets.get(id);
                        System.out.println("  Knows about sheet " + id);
                        System.out.println("    That sheet lives at " + offset);
                        Record atPos = UserEditAndPersistListing.findRecordAtPos(offset);
                        System.out.println("    The record at that pos is of type " + atPos.getRecordType());
                        System.out.println("    The record at that pos has class " + atPos.getClass().getName());
                        if (atPos instanceof PositionDependentRecord) continue;
                        System.out.println("    ** The record class isn't position aware! **");
                    }
                }
                baos = new UnsynchronizedByteArrayOutputStream();
                r.writeOut((OutputStream)baos);
                pos += baos.size();
            }
            System.out.println();
            pos = 0;
            for (Record r : ss.getRecords()) {
                if (r instanceof UserEditAtom) {
                    UserEditAtom uea = (UserEditAtom)r;
                    System.out.println("Found UserEditAtom at " + pos + " (" + Integer.toHexString(pos) + ")");
                    System.out.println("  lastUserEditAtomOffset = " + uea.getLastUserEditAtomOffset());
                    System.out.println("  persistPointersOffset  = " + uea.getPersistPointersOffset());
                    System.out.println("  docPersistRef          = " + uea.getDocPersistRef());
                    System.out.println("  maxPersistWritten      = " + uea.getMaxPersistWritten());
                }
                baos = new UnsynchronizedByteArrayOutputStream();
                r.writeOut((OutputStream)baos);
                pos += baos.size();
            }
            System.out.println();
            CurrentUserAtom cua = ss.getCurrentUserAtom();
            System.out.println("Checking Current User Atom");
            System.out.println("  Thinks the CurrentEditOffset is " + cua.getCurrentEditOffset());
            System.out.println();
        }
    }

    public static Record findRecordAtPos(int pos) {
        long type = LittleEndian.getUShort(fileContents, pos + 2);
        long rlen = LittleEndian.getUInt(fileContents, pos + 4);
        return Record.createRecordForType(type, fileContents, pos, (int)rlen + 8);
    }
}

