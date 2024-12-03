/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.dom4j.Document
 *  org.dom4j.io.OutputFormat
 *  org.dom4j.io.XMLWriter
 */
package com.atlassian.core.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class Dom4jUtil {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void saveDocumentTo(Document doc, String folder, String fileName) throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        File file = new File(folder, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        OutputStreamWriter writeOut = new OutputStreamWriter((OutputStream)new FileOutputStream(file), "UTF-8");
        try (XMLWriter writer = new XMLWriter((Writer)writeOut, format);){
            writer.write(doc);
        }
    }
}

