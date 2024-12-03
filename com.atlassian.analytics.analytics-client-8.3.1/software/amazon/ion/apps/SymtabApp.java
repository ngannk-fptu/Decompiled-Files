/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.apps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import software.amazon.ion.IonCatalog;
import software.amazon.ion.IonException;
import software.amazon.ion.IonReader;
import software.amazon.ion.IonType;
import software.amazon.ion.IonWriter;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.apps.BaseApp;

public class SymtabApp
extends BaseApp {
    private ArrayList<SymbolTable> myImports = new ArrayList();
    private ArrayList<String> mySymbols = new ArrayList();
    private String mySymtabName;
    private int mySymtabVersion;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Need one file to build symtab");
            return;
        }
        SymtabApp app = new SymtabApp();
        app.doMain(args);
    }

    protected int processOptions(String[] args) {
        for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            if ("--catalog".equals(arg)) {
                String symtabPath = args[++i];
                this.loadCatalog(symtabPath);
                continue;
            }
            if ("--import".equals(arg)) {
                String name = args[++i];
                IonCatalog catalog = this.mySystem.getCatalog();
                SymbolTable table = catalog.getTable(name);
                if (table == null) {
                    String message = "There's no symbol table in the catalog named " + name;
                    throw new RuntimeException(message);
                }
                this.myImports.add(table);
                this.logDebug("Imported symbol table " + name + "@" + table.getVersion());
                continue;
            }
            if ("--name".equals(arg)) {
                if (this.mySymtabName != null) {
                    throw new RuntimeException("Multiple names");
                }
                this.mySymtabName = args[++i];
                if (this.mySymtabName.length() != 0) continue;
                throw new RuntimeException("Name must not be empty");
            }
            if ("--version".equals(arg)) {
                if (this.mySymtabVersion != 0) {
                    throw new RuntimeException("Multiple versions");
                }
                int version = Integer.parseInt(arg);
                if (version < 1) {
                    throw new RuntimeException("Version must be at least 1");
                }
                if (version != 1) {
                    String message = "Symtab extension not implemented";
                    throw new UnsupportedOperationException(message);
                }
                this.mySymtabVersion = version;
                continue;
            }
            return i;
        }
        return args.length;
    }

    protected boolean optionsAreValid(String[] filePaths) {
        if (this.mySymtabName == null) {
            throw new RuntimeException("Must provide --name");
        }
        if (this.mySymtabVersion == 0) {
            this.mySymtabVersion = 1;
        }
        if (filePaths.length == 0) {
            System.err.println("Must provide list of files to provide symbols");
            return false;
        }
        return true;
    }

    public void processFiles(String[] filePaths) {
        super.processFiles(filePaths);
        SymbolTable[] importArray = new SymbolTable[this.myImports.size()];
        this.myImports.toArray(importArray);
        SymbolTable mySymtab = this.mySystem.newSharedSymbolTable(this.mySymtabName, this.mySymtabVersion, this.mySymbols.iterator(), importArray);
        IonWriter w = this.mySystem.newTextWriter(System.out);
        try {
            mySymtab.writeTo(w);
            System.out.println();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void process(IonReader reader) throws IonException {
        IonType type;
        while ((type = reader.next()) != null) {
            String fieldName = reader.getFieldName();
            this.intern(fieldName);
            this.internAnnotations(reader);
            switch (type) {
                case SYMBOL: {
                    String text = reader.stringValue();
                    this.intern(text);
                    break;
                }
                case LIST: 
                case SEXP: 
                case STRUCT: {
                    reader.stepIn();
                    break;
                }
            }
            while (reader.next() != null && reader.getDepth() > 0) {
                reader.stepOut();
            }
        }
    }

    private void internAnnotations(IonReader reader) {
        Iterator<String> i = reader.iterateTypeAnnotations();
        assert (i != null);
        while (i.hasNext()) {
            String ann = i.next();
            this.intern(ann);
        }
    }

    private void intern(String text) {
        if (text != null) {
            if (text.equals("$ion") || text.startsWith("$ion_")) {
                return;
            }
            this.mySymbols.add(text);
        }
    }
}

