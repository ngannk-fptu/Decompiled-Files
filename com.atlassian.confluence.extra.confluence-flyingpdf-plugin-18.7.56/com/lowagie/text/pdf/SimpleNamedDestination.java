/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNameTree;
import com.lowagie.text.pdf.PdfNull;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.xml.XMLUtil;
import com.lowagie.text.xml.simpleparser.IanaEncodings;
import com.lowagie.text.xml.simpleparser.SimpleXMLDocHandler;
import com.lowagie.text.xml.simpleparser.SimpleXMLParser;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

public final class SimpleNamedDestination
implements SimpleXMLDocHandler {
    private HashMap<String, String> xmlNames;
    private Map<String, String> xmlLast;

    private SimpleNamedDestination() {
    }

    public static HashMap<Object, Object> getNamedDestination(PdfReader reader, boolean fromNames) {
        IntHashtable pages = new IntHashtable();
        int numPages = reader.getNumberOfPages();
        for (int k = 1; k <= numPages; ++k) {
            pages.put(reader.getPageOrigRef(k).getNumber(), k);
        }
        HashMap names = fromNames ? reader.getNamedDestinationFromNames() : reader.getNamedDestinationFromStrings();
        Iterator it = names.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            PdfArray arr = (PdfArray)entry.getValue();
            StringBuilder s = new StringBuilder();
            try {
                s.append(pages.get(arr.getAsIndirectObject(0).getNumber()));
                s.append(' ').append(arr.getPdfObject(1).toString().substring(1));
                for (int k = 2; k < arr.size(); ++k) {
                    s.append(' ').append(arr.getPdfObject(k).toString());
                }
                entry.setValue(s.toString());
            }
            catch (Exception e) {
                it.remove();
            }
        }
        return names;
    }

    public static void exportToXML(HashMap names, OutputStream out, String encoding, boolean onlyASCII) throws IOException {
        String jenc = IanaEncodings.getJavaEncoding(encoding);
        BufferedWriter wrt = new BufferedWriter(new OutputStreamWriter(out, jenc));
        SimpleNamedDestination.exportToXML(names, wrt, encoding, onlyASCII);
    }

    public static void exportToXML(HashMap names, Writer wrt, String encoding, boolean onlyASCII) throws IOException {
        wrt.write("<?xml version=\"1.0\" encoding=\"");
        wrt.write(XMLUtil.escapeXML(encoding, onlyASCII));
        wrt.write("\"?>\n<Destination>\n");
        Iterator iterator = names.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry o;
            Map.Entry entry = o = iterator.next();
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            wrt.write("  <Name Page=\"");
            wrt.write(XMLUtil.escapeXML(value, onlyASCII));
            wrt.write("\">");
            wrt.write(XMLUtil.escapeXML(SimpleNamedDestination.escapeBinaryString(key), onlyASCII));
            wrt.write("</Name>\n");
        }
        wrt.write("</Destination>\n");
        wrt.flush();
    }

    public static HashMap<String, String> importFromXML(InputStream in) throws IOException {
        SimpleNamedDestination names = new SimpleNamedDestination();
        SimpleXMLParser.parse((SimpleXMLDocHandler)names, in);
        return names.xmlNames;
    }

    public static HashMap<String, String> importFromXML(Reader in) throws IOException {
        SimpleNamedDestination names = new SimpleNamedDestination();
        SimpleXMLParser.parse((SimpleXMLDocHandler)names, in);
        return names.xmlNames;
    }

    static PdfArray createDestinationArray(String value, PdfWriter writer) {
        PdfArray ar = new PdfArray();
        StringTokenizer tk = new StringTokenizer(value);
        int n = Integer.parseInt(tk.nextToken());
        ar.add(writer.getPageReference(n));
        if (!tk.hasMoreTokens()) {
            ar.add(PdfName.XYZ);
            ar.add(new float[]{0.0f, 10000.0f, 0.0f});
        } else {
            String fn = tk.nextToken();
            if (fn.startsWith("/")) {
                fn = fn.substring(1);
            }
            ar.add(new PdfName(fn));
            for (int k = 0; k < 4 && tk.hasMoreTokens(); ++k) {
                fn = tk.nextToken();
                if (fn.equals("null")) {
                    ar.add(PdfNull.PDFNULL);
                    continue;
                }
                ar.add(new PdfNumber(fn));
            }
        }
        return ar;
    }

    public static PdfDictionary outputNamedDestinationAsNames(HashMap names, PdfWriter writer) {
        PdfDictionary dic = new PdfDictionary();
        Iterator iterator = names.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry o;
            Map.Entry entry = o = iterator.next();
            try {
                String key = (String)entry.getKey();
                String value = (String)entry.getValue();
                PdfArray ar = SimpleNamedDestination.createDestinationArray(value, writer);
                PdfName kn = new PdfName(key);
                dic.put(kn, ar);
            }
            catch (Exception exception) {}
        }
        return dic;
    }

    public static PdfDictionary outputNamedDestinationAsStrings(Map<String, String> names, PdfWriter writer) throws IOException {
        HashMap<String, PdfIndirectReference> n2 = new HashMap<String, PdfIndirectReference>();
        Iterator<Map.Entry<String, String>> it = names.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            try {
                String value = entry.getValue();
                PdfArray ar = SimpleNamedDestination.createDestinationArray(value, writer);
                n2.put(entry.getKey(), writer.addToBody(ar).getIndirectReference());
            }
            catch (Exception e) {
                it.remove();
            }
        }
        return PdfNameTree.writeTree(n2, writer);
    }

    public static String escapeBinaryString(String s) {
        char[] cc;
        StringBuilder buf = new StringBuilder();
        for (char c : cc = s.toCharArray()) {
            if (c < ' ') {
                buf.append('\\');
                String octal = "00" + Integer.toOctalString(c);
                buf.append(octal.substring(octal.length() - 3));
                continue;
            }
            if (c == '\\') {
                buf.append("\\\\");
                continue;
            }
            buf.append(c);
        }
        return buf.toString();
    }

    public static String unEscapeBinaryString(String s) {
        StringBuilder buf = new StringBuilder();
        char[] cc = s.toCharArray();
        int len = cc.length;
        for (int k = 0; k < len; ++k) {
            char c = cc[k];
            if (c == '\\') {
                if (++k >= len) {
                    buf.append('\\');
                    break;
                }
                c = cc[k];
                if (c >= '0' && c <= '7') {
                    int n = c - 48;
                    ++k;
                    for (int j = 0; j < 2 && k < len && (c = cc[k]) >= '0' && c <= '7'; ++k, ++j) {
                        n = n * 8 + c - 48;
                    }
                    --k;
                    buf.append((char)n);
                    continue;
                }
                buf.append(c);
                continue;
            }
            buf.append(c);
        }
        return buf.toString();
    }

    @Override
    public void endDocument() {
    }

    @Override
    public void endElement(String tag) {
        if (tag.equals("Destination")) {
            if (this.xmlLast == null && this.xmlNames != null) {
                return;
            }
            throw new RuntimeException(MessageLocalization.getComposedMessage("destination.end.tag.out.of.place"));
        }
        if (!tag.equals("Name")) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.end.tag.1", tag));
        }
        if (this.xmlLast == null || this.xmlNames == null) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("name.end.tag.out.of.place"));
        }
        if (!this.xmlLast.containsKey("Page")) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("page.attribute.missing"));
        }
        this.xmlNames.put(SimpleNamedDestination.unEscapeBinaryString(this.xmlLast.get("Name")), this.xmlLast.get("Page"));
        this.xmlLast = null;
    }

    @Override
    public void startDocument() {
    }

    @Override
    @Deprecated
    public void startElement(String tag, HashMap h) {
        this.startElement(tag, (Map<String, String>)h);
    }

    @Override
    public void startElement(String tag, Map<String, String> h) {
        if (this.xmlNames == null) {
            if (tag.equals("Destination")) {
                this.xmlNames = new HashMap();
                return;
            }
            throw new RuntimeException(MessageLocalization.getComposedMessage("root.element.is.not.destination"));
        }
        if (!tag.equals("Name")) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("tag.1.not.allowed", tag));
        }
        if (this.xmlLast != null) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("nested.tags.are.not.allowed"));
        }
        this.xmlLast = new HashMap<String, String>(h);
        this.xmlLast.put("Name", "");
    }

    @Override
    public void text(String str) {
        if (this.xmlLast == null) {
            return;
        }
        String name = this.xmlLast.get("Name");
        name = name + str;
        this.xmlLast.put("Name", name);
    }
}

