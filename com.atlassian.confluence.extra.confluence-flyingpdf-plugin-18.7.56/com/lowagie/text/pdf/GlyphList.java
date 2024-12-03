/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.fonts.FontsResourceAnchor;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.StringTokenizer;

public class GlyphList {
    private static HashMap<Integer, String> unicode2names = new HashMap();
    private static HashMap<String, int[]> names2unicode = new HashMap();

    public static int[] nameToUnicode(String name) {
        return names2unicode.get(name);
    }

    public static String unicodeToName(int num) {
        return unicode2names.get(num);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static {
        InputStream is = null;
        try {
            int size;
            is = BaseFont.getResourceStream("com/lowagie/text/pdf/fonts/glyphlist.txt", FontsResourceAnchor.class.getClassLoader());
            if (is == null) {
                String msg = "glyphlist.txt not found as resource. (It must exist as resource in the package com.lowagie.text.pdf.fonts)";
                throw new Exception(msg);
            }
            byte[] buf = new byte[1024];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while ((size = is.read(buf)) >= 0) {
                out.write(buf, 0, size);
            }
            is.close();
            is = null;
            String s = PdfEncodings.convertToString(out.toByteArray(), null);
            StringTokenizer tk = new StringTokenizer(s, "\r\n");
            while (tk.hasMoreTokens()) {
                StringTokenizer t2;
                String line = tk.nextToken();
                if (line.startsWith("#") || !(t2 = new StringTokenizer(line, " ;\r\n\t\f")).hasMoreTokens()) continue;
                String name = t2.nextToken();
                if (!t2.hasMoreTokens()) continue;
                String hex = t2.nextToken();
                Integer num = Integer.valueOf(hex, 16);
                unicode2names.put(num, name);
                names2unicode.put(name, new int[]{num});
            }
        }
        catch (Exception e) {
            System.err.println("glyphlist.txt loading error: " + e.getMessage());
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (Exception exception) {}
            }
        }
    }
}

