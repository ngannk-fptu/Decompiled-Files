/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Binding
 *  javax.wsdl.Definition
 *  javax.wsdl.WSDLException
 */
package org.apache.axis.wsdl.gen;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.gen.Generator;
import org.apache.axis.wsdl.gen.GeneratorFactory;
import org.apache.axis.wsdl.gen.NoopFactory;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.CollectionElement;
import org.apache.axis.wsdl.symbolTable.MessageEntry;
import org.apache.axis.wsdl.symbolTable.PortTypeEntry;
import org.apache.axis.wsdl.symbolTable.ServiceEntry;
import org.apache.axis.wsdl.symbolTable.SymTabEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.Type;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.apache.axis.wsdl.toJava.Utils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class Parser {
    protected boolean debug = false;
    protected boolean quiet = false;
    protected boolean imports = true;
    protected boolean verbose = false;
    protected boolean nowrap = false;
    protected String username = null;
    protected String password = null;
    protected boolean wrapArrays = false;
    private long timeoutms = 45000L;
    private GeneratorFactory genFactory = null;
    private SymbolTable symbolTable = null;

    public boolean isDebug() {
        return this.debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isQuiet() {
        return this.quiet;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    public boolean isImports() {
        return this.imports;
    }

    public void setImports(boolean imports) {
        this.imports = imports;
    }

    public boolean isVerbose() {
        return this.verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isNowrap() {
        return this.nowrap;
    }

    public void setNowrap(boolean nowrap) {
        this.nowrap = nowrap;
    }

    public long getTimeout() {
        return this.timeoutms;
    }

    public void setTimeout(long timeout) {
        this.timeoutms = timeout;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public GeneratorFactory getFactory() {
        return this.genFactory;
    }

    public void setFactory(GeneratorFactory factory) {
        this.genFactory = factory;
    }

    public SymbolTable getSymbolTable() {
        return this.symbolTable;
    }

    public Definition getCurrentDefinition() {
        return this.symbolTable == null ? null : this.symbolTable.getDefinition();
    }

    public String getWSDLURI() {
        return this.symbolTable == null ? null : this.symbolTable.getWSDLURI();
    }

    public void run(String wsdlURI) throws Exception {
        if (this.getFactory() == null) {
            this.setFactory(new NoopFactory());
        }
        this.symbolTable = new SymbolTable(this.genFactory.getBaseTypeMapping(), this.imports, this.verbose, this.nowrap);
        this.symbolTable.setQuiet(this.quiet);
        this.symbolTable.setWrapArrays(this.wrapArrays);
        WSDLRunnable runnable = new WSDLRunnable(this.symbolTable, wsdlURI);
        Thread wsdlThread = new Thread(runnable);
        wsdlThread.start();
        try {
            if (this.timeoutms > 0L) {
                wsdlThread.join(this.timeoutms);
            } else {
                wsdlThread.join();
            }
        }
        catch (InterruptedException e) {
            // empty catch block
        }
        if (wsdlThread.isAlive()) {
            wsdlThread.interrupt();
            throw new IOException(Messages.getMessage("timedOut"));
        }
        if (runnable.getFailure() != null) {
            throw runnable.getFailure();
        }
    }

    public void run(String context, Document doc) throws IOException, SAXException, WSDLException, ParserConfigurationException {
        if (this.getFactory() == null) {
            this.setFactory(new NoopFactory());
        }
        this.symbolTable = new SymbolTable(this.genFactory.getBaseTypeMapping(), this.imports, this.verbose, this.nowrap);
        this.symbolTable.populate(context, doc);
        this.generate(this.symbolTable);
    }

    protected void sanityCheck(SymbolTable symbolTable) {
    }

    private void generate(SymbolTable symbolTable) throws IOException {
        this.sanityCheck(symbolTable);
        Definition def = symbolTable.getDefinition();
        this.genFactory.generatorPass(def, symbolTable);
        if (this.isDebug()) {
            symbolTable.dump(System.out);
        }
        this.generateTypes(symbolTable);
        Iterator it = symbolTable.getHashMap().values().iterator();
        while (it.hasNext()) {
            Vector v = (Vector)it.next();
            for (int i = 0; i < v.size(); ++i) {
                SymTabEntry entry = (SymTabEntry)v.elementAt(i);
                Generator gen = null;
                if (entry instanceof MessageEntry) {
                    gen = this.genFactory.getGenerator(((MessageEntry)entry).getMessage(), symbolTable);
                } else if (entry instanceof PortTypeEntry) {
                    PortTypeEntry pEntry = (PortTypeEntry)entry;
                    if (pEntry.getPortType().isUndefined()) continue;
                    gen = this.genFactory.getGenerator(pEntry.getPortType(), symbolTable);
                } else if (entry instanceof BindingEntry) {
                    BindingEntry bEntry = (BindingEntry)entry;
                    Binding binding = bEntry.getBinding();
                    if (binding.isUndefined() || !bEntry.isReferenced()) continue;
                    gen = this.genFactory.getGenerator(binding, symbolTable);
                } else if (entry instanceof ServiceEntry) {
                    gen = this.genFactory.getGenerator(((ServiceEntry)entry).getService(), symbolTable);
                }
                if (gen == null) continue;
                gen.generate();
            }
        }
        Generator gen = this.genFactory.getGenerator(def, symbolTable);
        gen.generate();
    }

    private void generateTypes(SymbolTable symbolTable) throws IOException {
        Map elements = symbolTable.getElementIndex();
        Collection elementCollection = elements.values();
        Iterator i = elementCollection.iterator();
        while (i.hasNext()) {
            boolean isType;
            TypeEntry type = (TypeEntry)i.next();
            boolean bl = isType = type instanceof Type || type instanceof CollectionElement;
            if (type.getNode() == null || Utils.isXsNode(type.getNode(), "attributeGroup") || Utils.isXsNode(type.getNode(), "group") || !type.isReferenced() || !isType || type.getBaseType() != null) continue;
            Generator gen = this.genFactory.getGenerator(type, symbolTable);
            gen.generate();
        }
        Map types = symbolTable.getTypeIndex();
        Collection typeCollection = types.values();
        Iterator i2 = typeCollection.iterator();
        while (i2.hasNext()) {
            boolean isType;
            TypeEntry type = (TypeEntry)i2.next();
            boolean bl = isType = type instanceof Type || type instanceof CollectionElement;
            if (type.getNode() == null || Utils.isXsNode(type.getNode(), "attributeGroup") || Utils.isXsNode(type.getNode(), "group") || !type.isReferenced() || !isType || type.getBaseType() != null) continue;
            Generator gen = this.genFactory.getGenerator(type, symbolTable);
            gen.generate();
        }
    }

    private class WSDLRunnable
    implements Runnable {
        private SymbolTable symbolTable;
        private String wsdlURI;
        private Exception failure = null;

        public WSDLRunnable(SymbolTable symbolTable, String wsdlURI) {
            this.symbolTable = symbolTable;
            this.wsdlURI = wsdlURI;
        }

        public void run() {
            try {
                this.symbolTable.populate(this.wsdlURI, Parser.this.username, Parser.this.password);
                Parser.this.generate(this.symbolTable);
            }
            catch (Exception e) {
                this.failure = e;
            }
        }

        public Exception getFailure() {
            return this.failure;
        }
    }
}

