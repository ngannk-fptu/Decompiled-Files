/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.hslf.dev;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherTextboxRecord;
import org.apache.poi.hslf.record.EscherTextboxWrapper;
import org.apache.poi.hslf.record.HSLFEscherRecordFactory;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.StyleTextPropAtom;
import org.apache.poi.hslf.record.TextBytesAtom;
import org.apache.poi.hslf.record.TextCharsAtom;
import org.apache.poi.hslf.usermodel.HSLFSlideShowImpl;
import org.apache.poi.util.HexDump;

public final class SlideShowRecordDumper {
    static final String tabs = "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t";
    private final boolean optVerbose;
    private final boolean optEscher;
    private final HSLFSlideShowImpl doc;
    private final PrintStream ps;

    public static void main(String[] args) throws IOException {
        int ndx;
        boolean verbose = false;
        boolean escher = false;
        for (ndx = 0; ndx < args.length && args[ndx].charAt(0) == '-'; ++ndx) {
            if (args[ndx].equals("-escher")) {
                escher = true;
                continue;
            }
            if (args[ndx].equals("-verbose")) {
                verbose = true;
                continue;
            }
            SlideShowRecordDumper.printUsage();
            return;
        }
        if (ndx != args.length - 1) {
            SlideShowRecordDumper.printUsage();
            return;
        }
        String filename = args[ndx];
        SlideShowRecordDumper foo = new SlideShowRecordDumper(System.out, filename, verbose, escher);
        foo.printDump();
        foo.doc.close();
    }

    public static void printUsage() {
        System.err.println("Usage: SlideShowRecordDumper [-escher] [-verbose] <filename>");
        System.err.println("Valid Options:");
        System.err.println("-escher\t\t: dump contents of escher records");
        System.err.println("-verbose\t: dump binary contents of each record");
    }

    public SlideShowRecordDumper(PrintStream ps, String fileName, boolean verbose, boolean escher) throws IOException {
        this.ps = ps;
        this.optVerbose = verbose;
        this.optEscher = escher;
        this.doc = new HSLFSlideShowImpl(fileName);
    }

    public void printDump() throws IOException {
        this.walkTree(0, 0, this.doc.getRecords(), 0);
    }

    public String makeHex(int number, int padding) {
        StringBuilder hex = new StringBuilder(Integer.toHexString(number).toUpperCase(Locale.ROOT));
        while (hex.length() < padding) {
            hex.insert(0, "0");
        }
        return hex.toString();
    }

    public String reverseHex(String s) {
        StringBuilder ret = new StringBuilder();
        int pos = 0;
        if ((s.length() & 1) == 1) {
            ret.append(0);
            ++pos;
        }
        for (char c : s.toCharArray()) {
            if (pos > 0 && (pos & 1) == 0) {
                ret.append(' ');
            }
            ret.append(c);
            ++pos;
        }
        return ret.toString();
    }

    public int getDiskLen(Record r) throws IOException {
        int diskLen = 0;
        if (r != null) {
            UnsynchronizedByteArrayOutputStream baos = new UnsynchronizedByteArrayOutputStream();
            r.writeOut((OutputStream)baos);
            diskLen = baos.size();
        }
        return diskLen;
    }

    public String getPrintableRecordContents(Record r) throws IOException {
        if (r == null) {
            return "<<null>>";
        }
        UnsynchronizedByteArrayOutputStream baos = new UnsynchronizedByteArrayOutputStream();
        r.writeOut((OutputStream)baos);
        byte[] b = baos.toByteArray();
        return HexDump.dump(b, 0L, 0);
    }

    public void printEscherRecord(EscherRecord er, int indent) {
        if (er instanceof EscherContainerRecord) {
            this.printEscherContainerRecord((EscherContainerRecord)er, indent);
        } else if (er instanceof EscherTextboxRecord) {
            this.printEscherTextBox((EscherTextboxRecord)er, indent);
        } else {
            this.ps.print(tabs.substring(0, indent));
            this.ps.println(er);
        }
    }

    private void printEscherTextBox(EscherTextboxRecord tbRecord, int indent) {
        String ind = tabs.substring(0, indent);
        this.ps.println(ind + "EscherTextboxRecord:");
        EscherTextboxWrapper etw = new EscherTextboxWrapper(tbRecord);
        Record prevChild = null;
        for (Record child : etw.getChildRecords()) {
            if (child instanceof StyleTextPropAtom) {
                String text;
                if (prevChild instanceof TextCharsAtom) {
                    text = ((TextCharsAtom)prevChild).getText();
                } else if (prevChild instanceof TextBytesAtom) {
                    text = ((TextBytesAtom)prevChild).getText();
                } else {
                    this.ps.println(ind + "Error! Couldn't find preceding TextAtom for style");
                    continue;
                }
                StyleTextPropAtom tsp = (StyleTextPropAtom)child;
                tsp.setParentTextSize(text.length());
            }
            this.ps.println(ind + child);
            prevChild = child;
        }
    }

    private void printEscherContainerRecord(EscherContainerRecord ecr, int indent) {
        String ind = tabs.substring(0, indent);
        this.ps.println(ind + ecr.getClass().getName() + " (" + ecr.getRecordName() + "):");
        this.ps.println(ind + "  isContainer: " + ecr.isContainerRecord());
        this.ps.println(ind + "  options: 0x" + HexDump.toHex(ecr.getOptions()));
        this.ps.println(ind + "  recordId: 0x" + HexDump.toHex(ecr.getRecordId()));
        this.ps.println(ind + "  numchildren: " + ecr.getChildCount());
        this.ps.println(ind + "  children: ");
        int count = 0;
        for (EscherRecord record : ecr) {
            this.ps.println(ind + "   Child " + count + ":");
            this.printEscherRecord(record, indent + 1);
            ++count;
        }
    }

    public void walkTree(int depth, int pos, Record[] records, int indent) throws IOException {
        String ind = tabs.substring(0, indent);
        for (Record r : records) {
            if (r == null) {
                this.ps.println(ind + "At position " + pos + " (" + this.makeHex(pos, 6) + "):");
                this.ps.println(ind + "Warning! Null record found.");
                continue;
            }
            int len = this.getDiskLen(r);
            String hexType = this.makeHex((int)r.getRecordType(), 4);
            String rHexType = this.reverseHex(hexType);
            Class<?> c = r.getClass();
            String cname = c.toString();
            if (cname.startsWith("class ")) {
                cname = cname.substring(6);
            }
            if (cname.startsWith("org.apache.poi.hslf.record.")) {
                cname = cname.substring(27);
            }
            this.ps.println(ind + "At position " + pos + " (" + this.makeHex(pos, 6) + "):");
            this.ps.println(ind + " Record is of type " + cname);
            this.ps.println(ind + " Type is " + r.getRecordType() + " (" + hexType + " -> " + rHexType + " )");
            this.ps.println(ind + " Len is " + (len - 8) + " (" + this.makeHex(len - 8, 8) + "), on disk len is " + len);
            if (this.optEscher && cname.equals("PPDrawing")) {
                HSLFEscherRecordFactory factory = new HSLFEscherRecordFactory();
                UnsynchronizedByteArrayOutputStream baos = new UnsynchronizedByteArrayOutputStream();
                r.writeOut((OutputStream)baos);
                byte[] b = baos.toByteArray();
                EscherRecord er = factory.createRecord(b, 0);
                er.fillFields(b, 0, factory);
                this.printEscherRecord(er, indent + 1);
            } else if (this.optVerbose && r.getChildRecords() == null) {
                String recData = this.getPrintableRecordContents(r);
                this.ps.println(ind + recData);
            }
            this.ps.println();
            if (r.getChildRecords() != null) {
                this.walkTree(depth + 3, pos + 8, r.getChildRecords(), indent + 1);
            }
            pos += len;
        }
    }
}

