/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import java.io.IOException;
import java.util.Iterator;
import software.amazon.ion.IonWriter;
import software.amazon.ion.SymbolToken;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface SymbolTable {
    public static final int UNKNOWN_SYMBOL_ID = -1;

    public String getName();

    public int getVersion();

    public boolean isLocalTable();

    public boolean isSharedTable();

    public boolean isSubstitute();

    public boolean isSystemTable();

    public boolean isReadOnly();

    public void makeReadOnly();

    public SymbolTable getSystemSymbolTable();

    public String getIonVersionId();

    public SymbolTable[] getImportedTables();

    public int getImportedMaxId();

    public int getMaxId();

    public SymbolToken intern(String var1);

    public SymbolToken find(String var1);

    public int findSymbol(String var1);

    public String findKnownSymbol(int var1);

    public Iterator<String> iterateDeclaredSymbolNames();

    public void writeTo(IonWriter var1) throws IOException;
}

