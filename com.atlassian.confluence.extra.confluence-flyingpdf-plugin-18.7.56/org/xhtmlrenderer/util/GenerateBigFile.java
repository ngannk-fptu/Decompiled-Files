/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.util;

import java.io.FileOutputStream;
import java.io.PrintWriter;

public class GenerateBigFile {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage:");
            System.out.println("GenerateBigFile output-file");
            System.exit(1);
        }
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileOutputStream(args[0]));
            out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>Big test file</title></head><body>");
            for (int i = 0; i < 10000; ++i) {
                String[] styles = new String[]{"10pt", "12pt", "14pt", "18pt", "24pt"};
                String[] fonts = new String[]{"Times", "Helvetica", "Courier"};
                String style = styles[(int)Math.floor(Math.random() * (double)styles.length)];
                String font = fonts[(int)Math.floor(Math.random() * (double)fonts.length)];
                String colour = Integer.toHexString((int)Math.floor(Math.random() * 256.0)) + Integer.toHexString((int)Math.floor(Math.random() * 256.0)) + Integer.toHexString((int)Math.floor(Math.random() * 256.0));
                out.println("<p style=\"font: " + style + " " + font + "; color: #" + colour + "\">Some Styled text to see how we can handle it</p>");
            }
            out.println("</body></html>");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            out.close();
        }
    }
}

