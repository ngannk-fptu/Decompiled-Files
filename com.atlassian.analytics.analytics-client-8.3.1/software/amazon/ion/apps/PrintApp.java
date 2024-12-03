/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.apps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import software.amazon.ion.IonException;
import software.amazon.ion.IonReader;
import software.amazon.ion.IonSystem;
import software.amazon.ion.IonWriter;
import software.amazon.ion.apps.BaseApp;

public class PrintApp
extends BaseApp {
    private File myOutputDir;
    private String myOutputFile;

    public static void main(String[] args) {
        PrintApp app = new PrintApp();
        app.doMain(args);
    }

    protected int processOptions(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            String path;
            String arg = args[i];
            if ("--catalog".equals(arg)) {
                String symtabPath = args[++i];
                this.loadCatalog(symtabPath);
                continue;
            }
            if ("--output-dir".equals(arg)) {
                path = args[++i];
                this.myOutputDir = new File(path);
                if (this.myOutputDir.isDirectory() && this.myOutputDir.canWrite()) continue;
                throw new RuntimeException("Not a writeable directory: " + path);
            }
            if ("--output".equals(arg)) {
                this.myOutputFile = path = args[++i];
                this.myOutputDir = new File(path).getParentFile();
                if (this.myOutputDir.isDirectory() && this.myOutputDir.canWrite()) continue;
                throw new RuntimeException("Not a writeable directory: " + path);
            }
            return i;
        }
        return args.length;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void process(File inputFile, IonReader reader) throws IOException, IonException {
        if (this.myOutputDir == null) {
            this.process(reader, System.out);
        } else {
            String fileName = inputFile.getName();
            File outputFile = new File(this.myOutputDir, fileName);
            FileOutputStream out = new FileOutputStream(outputFile);
            try {
                this.process(reader, out);
            }
            finally {
                out.close();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void process(IonReader reader) throws IOException, IonException {
        if (this.myOutputDir == null) {
            this.process(reader, System.out);
        } else {
            File outputFile = new File(this.myOutputFile);
            FileOutputStream out = new FileOutputStream(outputFile);
            try {
                this.process(reader, out);
            }
            finally {
                out.close();
            }
        }
    }

    protected void process(IonReader reader, OutputStream out) throws IOException, IonException {
        IonSystem system = this.mySystem;
        IonWriter writer = system.newTextWriter(out);
        writer.writeValues(reader);
        out.write(10);
        out.flush();
    }
}

