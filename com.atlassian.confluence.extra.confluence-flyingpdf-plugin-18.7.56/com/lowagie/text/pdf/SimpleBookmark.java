/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.ByteBuffer;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNull;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.SimpleNamedDestination;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;
import java.util.StringTokenizer;

public final class SimpleBookmark
implements SimpleXMLDocHandler {
    private List<Map<String, Object>> topList;
    private Stack<Map<String, Object>> attr = new Stack();

    private SimpleBookmark() {
    }

    private static List<Map<String, Object>> bookmarkDepth(PdfReader reader, PdfDictionary outline, IntHashtable pages) {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        while (outline != null) {
            PdfNumber count;
            PdfNumber style;
            HashMap<String, Object> map = new HashMap<String, Object>();
            PdfString title = (PdfString)PdfReader.getPdfObjectRelease(outline.get(PdfName.TITLE));
            map.put("Title", title.toUnicodeString());
            PdfArray color = (PdfArray)PdfReader.getPdfObjectRelease(outline.get(PdfName.C));
            if (color != null && color.size() == 3) {
                ByteBuffer out = new ByteBuffer();
                out.append(color.getAsNumber(0).floatValue()).append(' ');
                out.append(color.getAsNumber(1).floatValue()).append(' ');
                out.append(color.getAsNumber(2).floatValue());
                map.put("Color", PdfEncodings.convertToString(out.toByteArray(), null));
            }
            if ((style = (PdfNumber)PdfReader.getPdfObjectRelease(outline.get(PdfName.F))) != null) {
                int f = style.intValue();
                String s = "";
                if ((f & 1) != 0) {
                    s = s + "italic ";
                }
                if ((f & 2) != 0) {
                    s = s + "bold ";
                }
                if ((s = s.trim()).length() != 0) {
                    map.put("Style", s);
                }
            }
            if ((count = (PdfNumber)PdfReader.getPdfObjectRelease(outline.get(PdfName.COUNT))) != null && count.intValue() < 0) {
                map.put("Open", "false");
            }
            try {
                PdfObject dest = PdfReader.getPdfObjectRelease(outline.get(PdfName.DEST));
                if (dest != null) {
                    SimpleBookmark.mapGotoBookmark(map, dest, pages);
                } else {
                    PdfDictionary action = (PdfDictionary)PdfReader.getPdfObjectRelease(outline.get(PdfName.A));
                    if (action != null) {
                        PdfObject file;
                        if (PdfName.GOTO.equals(PdfReader.getPdfObjectRelease(action.get(PdfName.S)))) {
                            dest = PdfReader.getPdfObjectRelease(action.get(PdfName.D));
                            if (dest != null) {
                                SimpleBookmark.mapGotoBookmark(map, dest, pages);
                            }
                        } else if (PdfName.URI.equals(PdfReader.getPdfObjectRelease(action.get(PdfName.S)))) {
                            map.put("Action", "URI");
                            map.put("URI", ((PdfString)PdfReader.getPdfObjectRelease(action.get(PdfName.URI))).toUnicodeString());
                        } else if (PdfName.GOTOR.equals(PdfReader.getPdfObjectRelease(action.get(PdfName.S)))) {
                            PdfObject newWindow;
                            dest = PdfReader.getPdfObjectRelease(action.get(PdfName.D));
                            if (dest != null) {
                                if (dest.isString()) {
                                    map.put("Named", dest.toString());
                                } else if (dest.isName()) {
                                    map.put("NamedN", PdfName.decodeName(dest.toString()));
                                } else if (dest.isArray()) {
                                    PdfArray arr = (PdfArray)dest;
                                    StringBuilder s = new StringBuilder();
                                    s.append(arr.getPdfObject(0).toString());
                                    s.append(' ').append(arr.getPdfObject(1).toString());
                                    for (int k = 2; k < arr.size(); ++k) {
                                        s.append(' ').append(arr.getPdfObject(k).toString());
                                    }
                                    map.put("Page", s.toString());
                                }
                            }
                            map.put("Action", "GoToR");
                            file = PdfReader.getPdfObjectRelease(action.get(PdfName.F));
                            if (file != null) {
                                if (file.isString()) {
                                    map.put("File", ((PdfString)file).toUnicodeString());
                                } else if (file.isDictionary() && (file = PdfReader.getPdfObject(((PdfDictionary)file).get(PdfName.F))).isString()) {
                                    map.put("File", ((PdfString)file).toUnicodeString());
                                }
                            }
                            if ((newWindow = PdfReader.getPdfObjectRelease(action.get(PdfName.NEWWINDOW))) != null) {
                                map.put("NewWindow", newWindow.toString());
                            }
                        } else if (PdfName.LAUNCH.equals(PdfReader.getPdfObjectRelease(action.get(PdfName.S)))) {
                            map.put("Action", "Launch");
                            file = PdfReader.getPdfObjectRelease(action.get(PdfName.F));
                            if (file == null) {
                                file = PdfReader.getPdfObjectRelease(action.get(PdfName.WIN));
                            }
                            if (file != null) {
                                if (file.isString()) {
                                    map.put("File", ((PdfString)file).toUnicodeString());
                                } else if (file.isDictionary() && (file = PdfReader.getPdfObjectRelease(((PdfDictionary)file).get(PdfName.F))).isString()) {
                                    map.put("File", ((PdfString)file).toUnicodeString());
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception dest) {
                // empty catch block
            }
            PdfDictionary first = (PdfDictionary)PdfReader.getPdfObjectRelease(outline.get(PdfName.FIRST));
            if (first != null) {
                map.put("Kids", SimpleBookmark.bookmarkDepth(reader, first, pages));
            }
            list.add(map);
            outline = (PdfDictionary)PdfReader.getPdfObjectRelease(outline.get(PdfName.NEXT));
        }
        return list;
    }

    private static void mapGotoBookmark(Map<String, Object> map, PdfObject dest, IntHashtable pages) {
        if (dest.isString()) {
            map.put("Named", dest.toString());
        } else if (dest.isName()) {
            map.put("Named", PdfName.decodeName(dest.toString()));
        } else if (dest.isArray()) {
            map.put("Page", SimpleBookmark.makeBookmarkParam((PdfArray)dest, pages));
        }
        map.put("Action", "GoTo");
    }

    private static String makeBookmarkParam(PdfArray dest, IntHashtable pages) {
        StringBuilder s = new StringBuilder();
        PdfObject obj = dest.getPdfObject(0);
        if (obj.isNumber()) {
            s.append(((PdfNumber)obj).intValue() + 1);
        } else {
            s.append(pages.get(SimpleBookmark.getNumber((PdfIndirectReference)obj)));
        }
        s.append(' ').append(dest.getPdfObject(1).toString().substring(1));
        for (int k = 2; k < dest.size(); ++k) {
            s.append(' ').append(dest.getPdfObject(k).toString());
        }
        return s.toString();
    }

    private static int getNumber(PdfIndirectReference indirect) {
        PdfDictionary pdfObj = (PdfDictionary)PdfReader.getPdfObjectRelease(indirect);
        if (pdfObj.contains(PdfName.TYPE) && pdfObj.get(PdfName.TYPE).equals(PdfName.PAGES) && pdfObj.contains(PdfName.KIDS)) {
            PdfArray kids = (PdfArray)pdfObj.get(PdfName.KIDS);
            indirect = (PdfIndirectReference)kids.getPdfObject(0);
        }
        return indirect.getNumber();
    }

    @Deprecated
    public static List getBookmark(PdfReader reader) {
        return SimpleBookmark.getBookmarkList(reader);
    }

    public static List<Map<String, Object>> getBookmarkList(PdfReader reader) {
        PdfDictionary catalog = reader.getCatalog();
        PdfObject obj = PdfReader.getPdfObjectRelease(catalog.get(PdfName.OUTLINES));
        if (obj == null || !obj.isDictionary()) {
            return null;
        }
        PdfDictionary outlines = (PdfDictionary)obj;
        IntHashtable pages = new IntHashtable();
        int numPages = reader.getNumberOfPages();
        for (int k = 1; k <= numPages; ++k) {
            pages.put(reader.getPageOrigRef(k).getNumber(), k);
            reader.releasePage(k);
        }
        return SimpleBookmark.bookmarkDepth(reader, (PdfDictionary)PdfReader.getPdfObjectRelease(outlines.get(PdfName.FIRST)), pages);
    }

    public static void eliminatePages(List list, int[] pageRange) {
        if (list == null) {
            return;
        }
        ListIterator it = list.listIterator();
        while (it.hasNext()) {
            List kids;
            String page;
            HashMap map = (HashMap)it.next();
            boolean hit = false;
            if ("GoTo".equals(map.get("Action")) && (page = (String)map.get("Page")) != null) {
                int idx = (page = page.trim()).indexOf(32);
                int pageNum = idx < 0 ? Integer.parseInt(page) : Integer.parseInt(page.substring(0, idx));
                int len = pageRange.length & 0xFFFFFFFE;
                for (int k = 0; k < len; k += 2) {
                    if (pageNum < pageRange[k] || pageNum > pageRange[k + 1]) continue;
                    hit = true;
                    break;
                }
            }
            if ((kids = (List)map.get("Kids")) != null) {
                SimpleBookmark.eliminatePages(kids, pageRange);
                if (kids.isEmpty()) {
                    map.remove("Kids");
                    kids = null;
                }
            }
            if (!hit) continue;
            if (kids == null) {
                it.remove();
                continue;
            }
            map.remove("Action");
            map.remove("Page");
            map.remove("Named");
        }
    }

    @Deprecated
    public static void shiftPageNumbers(List list, int pageShift, int[] pageRange) {
        SimpleBookmark.shiftPageNumbersInRange(list, pageShift, pageRange);
    }

    public static void shiftPageNumbersInRange(List<Map<String, Object>> list, int pageShift, int[] pageRange) {
        if (list == null) {
            return;
        }
        for (Map<String, Object> map : list) {
            List kids;
            String page;
            if ("GoTo".equals(map.get("Action")) && (page = (String)map.get("Page")) != null) {
                int idx = (page = page.trim()).indexOf(32);
                int pageNum = idx < 0 ? Integer.parseInt(page) : Integer.parseInt(page.substring(0, idx));
                boolean hit = false;
                if (pageRange == null) {
                    hit = true;
                } else {
                    int len = pageRange.length & 0xFFFFFFFE;
                    for (int k = 0; k < len; k += 2) {
                        if (pageNum < pageRange[k] || pageNum > pageRange[k + 1]) continue;
                        hit = true;
                        break;
                    }
                }
                if (hit) {
                    page = idx < 0 ? Integer.toString(pageNum + pageShift) : pageNum + pageShift + page.substring(idx);
                }
                map.put("Page", page);
            }
            if ((kids = (List)map.get("Kids")) == null) continue;
            SimpleBookmark.shiftPageNumbersInRange(kids, pageShift, pageRange);
        }
    }

    static void createOutlineAction(PdfDictionary outline, HashMap map, PdfWriter writer, boolean namedAsNames) {
        try {
            String file;
            String action = (String)map.get("Action");
            if ("GoTo".equals(action)) {
                String p = (String)map.get("Named");
                if (p != null) {
                    if (namedAsNames) {
                        outline.put(PdfName.DEST, new PdfName(p));
                    } else {
                        outline.put(PdfName.DEST, new PdfString(p, null));
                    }
                } else {
                    p = (String)map.get("Page");
                    if (p != null) {
                        PdfArray ar = new PdfArray();
                        StringTokenizer tk = new StringTokenizer(p);
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
                        outline.put(PdfName.DEST, ar);
                    }
                }
            } else if ("GoToR".equals(action)) {
                PdfDictionary dic = new PdfDictionary();
                String p = (String)map.get("Named");
                if (p != null) {
                    dic.put(PdfName.D, new PdfString(p, null));
                } else {
                    p = (String)map.get("NamedN");
                    if (p != null) {
                        dic.put(PdfName.D, new PdfName(p));
                    } else {
                        p = (String)map.get("Page");
                        if (p != null) {
                            PdfArray ar = new PdfArray();
                            StringTokenizer tk = new StringTokenizer(p);
                            ar.add(new PdfNumber(tk.nextToken()));
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
                            dic.put(PdfName.D, ar);
                        }
                    }
                }
                String file2 = (String)map.get("File");
                if (dic.size() > 0 && file2 != null) {
                    dic.put(PdfName.S, PdfName.GOTOR);
                    dic.put(PdfName.F, new PdfString(file2));
                    String nw = (String)map.get("NewWindow");
                    if (nw != null) {
                        if (nw.equals("true")) {
                            dic.put(PdfName.NEWWINDOW, PdfBoolean.PDFTRUE);
                        } else if (nw.equals("false")) {
                            dic.put(PdfName.NEWWINDOW, PdfBoolean.PDFFALSE);
                        }
                    }
                    outline.put(PdfName.A, dic);
                }
            } else if ("URI".equals(action)) {
                String uri = (String)map.get("URI");
                if (uri != null) {
                    PdfDictionary dic = new PdfDictionary();
                    dic.put(PdfName.S, PdfName.URI);
                    dic.put(PdfName.URI, new PdfString(uri));
                    outline.put(PdfName.A, dic);
                }
            } else if ("Launch".equals(action) && (file = (String)map.get("File")) != null) {
                PdfDictionary dic = new PdfDictionary();
                dic.put(PdfName.S, PdfName.LAUNCH);
                dic.put(PdfName.F, new PdfString(file));
                outline.put(PdfName.A, dic);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public static Object[] iterateOutlines(PdfWriter writer, PdfIndirectReference parent, List kids, boolean namedAsNames) throws IOException {
        PdfIndirectReference[] refs = new PdfIndirectReference[kids.size()];
        for (int k = 0; k < refs.length; ++k) {
            refs[k] = writer.getPdfIndirectReference();
        }
        int ptr = 0;
        int count = 0;
        ListIterator it = kids.listIterator();
        while (it.hasNext()) {
            String style;
            HashMap map = (HashMap)it.next();
            Object[] lower = null;
            List subKid = (List)map.get("Kids");
            if (subKid != null && !subKid.isEmpty()) {
                lower = SimpleBookmark.iterateOutlines(writer, refs[ptr], subKid, namedAsNames);
            }
            PdfDictionary outline = new PdfDictionary();
            ++count;
            if (lower != null) {
                outline.put(PdfName.FIRST, (PdfIndirectReference)lower[0]);
                outline.put(PdfName.LAST, (PdfIndirectReference)lower[1]);
                int n = (Integer)lower[2];
                if ("false".equals(map.get("Open"))) {
                    outline.put(PdfName.COUNT, new PdfNumber(-n));
                } else {
                    outline.put(PdfName.COUNT, new PdfNumber(n));
                    count += n;
                }
            }
            outline.put(PdfName.PARENT, parent);
            if (ptr > 0) {
                outline.put(PdfName.PREV, refs[ptr - 1]);
            }
            if (ptr < refs.length - 1) {
                outline.put(PdfName.NEXT, refs[ptr + 1]);
            }
            outline.put(PdfName.TITLE, new PdfString((String)map.get("Title"), "UnicodeBig"));
            String color = (String)map.get("Color");
            if (color != null) {
                try {
                    PdfArray arr = new PdfArray();
                    StringTokenizer tk = new StringTokenizer(color);
                    for (int k = 0; k < 3; ++k) {
                        float f = Float.parseFloat(tk.nextToken());
                        if (f < 0.0f) {
                            f = 0.0f;
                        }
                        if (f > 1.0f) {
                            f = 1.0f;
                        }
                        arr.add(new PdfNumber(f));
                    }
                    outline.put(PdfName.C, arr);
                }
                catch (Exception arr) {
                    // empty catch block
                }
            }
            if ((style = (String)map.get("Style")) != null) {
                style = style.toLowerCase();
                int bits = 0;
                if (style.contains("italic")) {
                    bits |= 1;
                }
                if (style.contains("bold")) {
                    bits |= 2;
                }
                if (bits != 0) {
                    outline.put(PdfName.F, new PdfNumber(bits));
                }
            }
            SimpleBookmark.createOutlineAction(outline, map, writer, namedAsNames);
            writer.addToBody((PdfObject)outline, refs[ptr]);
            ++ptr;
        }
        return new Object[]{refs[0], refs[refs.length - 1], count};
    }

    public static void exportToXMLNode(List list, Writer out, int indent, boolean onlyASCII) throws IOException {
        String dep = "";
        for (int k = 0; k < indent; ++k) {
            dep = dep + "  ";
        }
        for (Object o1 : list) {
            HashMap map = (HashMap)o1;
            String title = null;
            out.write(dep);
            out.write("<Title ");
            List kids = null;
            for (Map.Entry o : map.entrySet()) {
                Map.Entry entry = o;
                String key = (String)entry.getKey();
                if (key.equals("Title")) {
                    title = (String)entry.getValue();
                    continue;
                }
                if (key.equals("Kids")) {
                    kids = (List)entry.getValue();
                    continue;
                }
                out.write(key);
                out.write("=\"");
                String value = (String)entry.getValue();
                if (key.equals("Named") || key.equals("NamedN")) {
                    value = SimpleNamedDestination.escapeBinaryString(value);
                }
                out.write(XMLUtil.escapeXML(value, onlyASCII));
                out.write("\" ");
            }
            out.write(">");
            if (title == null) {
                title = "";
            }
            out.write(XMLUtil.escapeXML(title, onlyASCII));
            if (kids != null) {
                out.write("\n");
                SimpleBookmark.exportToXMLNode(kids, out, indent + 1, onlyASCII);
                out.write(dep);
            }
            out.write("</Title>\n");
        }
    }

    public static void exportToXML(List list, OutputStream out, String encoding, boolean onlyASCII) throws IOException {
        String jenc = IanaEncodings.getJavaEncoding(encoding);
        BufferedWriter wrt = new BufferedWriter(new OutputStreamWriter(out, jenc));
        SimpleBookmark.exportToXML(list, wrt, encoding, onlyASCII);
    }

    public static void exportToXML(List list, Writer wrt, String encoding, boolean onlyASCII) throws IOException {
        wrt.write("<?xml version=\"1.0\" encoding=\"");
        wrt.write(XMLUtil.escapeXML(encoding, onlyASCII));
        wrt.write("\"?>\n<Bookmark>\n");
        SimpleBookmark.exportToXMLNode(list, wrt, 1, onlyASCII);
        wrt.write("</Bookmark>\n");
        wrt.flush();
    }

    public static List<Map<String, Object>> importFromXML(InputStream in) throws IOException {
        SimpleBookmark book = new SimpleBookmark();
        SimpleXMLParser.parse((SimpleXMLDocHandler)book, in);
        return book.topList;
    }

    public static List<Map<String, Object>> importFromXML(Reader in) throws IOException {
        SimpleBookmark book = new SimpleBookmark();
        SimpleXMLParser.parse((SimpleXMLDocHandler)book, in);
        return book.topList;
    }

    @Override
    public void endDocument() {
    }

    @Override
    public void endElement(String tag) {
        if (tag.equals("Bookmark")) {
            if (this.attr.isEmpty()) {
                return;
            }
            throw new RuntimeException(MessageLocalization.getComposedMessage("bookmark.end.tag.out.of.place"));
        }
        if (!tag.equals("Title")) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.end.tag.1", tag));
        }
        Map<String, Object> attributes = this.attr.pop();
        String title = (String)attributes.get("Title");
        attributes.put("Title", title.trim());
        String named = (String)attributes.get("Named");
        if (named != null) {
            attributes.put("Named", SimpleNamedDestination.unEscapeBinaryString(named));
        }
        if ((named = (String)attributes.get("NamedN")) != null) {
            attributes.put("NamedN", SimpleNamedDestination.unEscapeBinaryString(named));
        }
        if (this.attr.isEmpty()) {
            this.topList.add(attributes);
        } else {
            Map<String, Object> parent = this.attr.peek();
            ArrayList<Map<String, Object>> kids = (ArrayList<Map<String, Object>>)parent.get("Kids");
            if (kids == null) {
                kids = new ArrayList<Map<String, Object>>();
                parent.put("Kids", kids);
            }
            kids.add(attributes);
        }
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
        if (this.topList == null) {
            if (tag.equals("Bookmark")) {
                this.topList = new ArrayList<Map<String, Object>>();
                return;
            }
            throw new RuntimeException(MessageLocalization.getComposedMessage("root.element.is.not.bookmark.1", tag));
        }
        if (!tag.equals("Title")) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("tag.1.not.allowed", tag));
        }
        HashMap<String, String> attributes = new HashMap<String, String>(h);
        attributes.put("Title", "");
        attributes.remove("Kids");
        this.attr.push(attributes);
    }

    @Override
    public void text(String str) {
        if (this.attr.isEmpty()) {
            return;
        }
        Map<String, Object> attributes = this.attr.peek();
        String title = (String)attributes.get("Title");
        title = title + str;
        attributes.put("Title", title);
    }
}

