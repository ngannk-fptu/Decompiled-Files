/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import software.amazon.ion.IonCatalog;
import software.amazon.ion.IonContainer;
import software.amazon.ion.IonSystem;
import software.amazon.ion.IonWriter;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.impl.IonWriterSystemTree;
import software.amazon.ion.impl.IonWriterUser;
import software.amazon.ion.system.IonWriterBuilder;

@Deprecated
public final class PrivateIonWriterFactory {
    public static IonWriter makeWriter(IonContainer container) {
        IonSystem sys = container.getSystem();
        IonCatalog cat = sys.getCatalog();
        IonWriter writer = PrivateIonWriterFactory.makeWriter(cat, container);
        return writer;
    }

    public static IonWriter makeWriter(IonCatalog catalog, IonContainer container) {
        IonSystem sys = container.getSystem();
        SymbolTable defaultSystemSymtab = sys.getSystemSymbolTable();
        IonWriterSystemTree system_writer = new IonWriterSystemTree(defaultSystemSymtab, catalog, container, IonWriterBuilder.InitialIvmHandling.SUPPRESS);
        return new IonWriterUser(catalog, sys, system_writer);
    }

    public static IonWriter makeSystemWriter(IonContainer container) {
        IonSystem sys = container.getSystem();
        IonCatalog cat = sys.getCatalog();
        SymbolTable defaultSystemSymtab = sys.getSystemSymbolTable();
        IonWriterSystemTree writer = new IonWriterSystemTree(defaultSystemSymtab, cat, container, null);
        return writer;
    }
}

