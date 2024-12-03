/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.thoughtworks.xstream.XStream
 *  com.thoughtworks.xstream.io.HierarchicalStreamDriver
 *  com.thoughtworks.xstream.io.xml.StaxDriver
 */
package org.codehaus.groovy.control;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.net.URI;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

public abstract class XStreamUtils {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void serialize(String name, Object ast) {
        File astFile;
        FileWriter astFileWriter;
        XStream xstream;
        block6: {
            if (name == null || name.length() == 0) {
                return;
            }
            xstream = new XStream((HierarchicalStreamDriver)new StaxDriver());
            astFileWriter = null;
            astFile = XStreamUtils.astFile(name);
            if (astFile != null) break block6;
            System.out.println("File-name for writing " + name + " AST could not be determined!");
            DefaultGroovyMethods.closeQuietly(astFileWriter);
            return;
        }
        try {
            astFileWriter = new FileWriter(astFile, false);
            xstream.toXML(ast, (Writer)astFileWriter);
            System.out.println("Written AST to " + name + ".xml");
        }
        catch (Exception e) {
            try {
                System.out.println("Couldn't write to " + name + ".xml");
                e.printStackTrace();
            }
            catch (Throwable throwable) {
                DefaultGroovyMethods.closeQuietly(astFileWriter);
                throw throwable;
            }
            DefaultGroovyMethods.closeQuietly(astFileWriter);
        }
        DefaultGroovyMethods.closeQuietly(astFileWriter);
    }

    private static File astFile(String uriOrFileName) {
        try {
            String astFileName = uriOrFileName + ".xml";
            return uriOrFileName.startsWith("file:") ? new File(URI.create(astFileName)) : new File(astFileName);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }
}

