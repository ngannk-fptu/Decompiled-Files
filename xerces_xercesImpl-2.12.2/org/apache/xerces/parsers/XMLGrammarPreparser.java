/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.parsers;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.parsers.ObjectFactory;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarLoader;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;

public class XMLGrammarPreparser {
    private static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
    protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    protected static final String GRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    private static final Hashtable KNOWN_LOADERS = new Hashtable();
    private static final String[] RECOGNIZED_PROPERTIES;
    protected final SymbolTable fSymbolTable;
    protected final XMLErrorReporter fErrorReporter;
    protected XMLEntityResolver fEntityResolver;
    protected XMLGrammarPool fGrammarPool;
    protected Locale fLocale;
    private final Hashtable fLoaders;
    private int fModCount = 1;

    public XMLGrammarPreparser() {
        this(new SymbolTable());
    }

    public XMLGrammarPreparser(SymbolTable symbolTable) {
        this.fSymbolTable = symbolTable;
        this.fLoaders = new Hashtable();
        this.fErrorReporter = new XMLErrorReporter();
        this.setLocale(Locale.getDefault());
        this.fEntityResolver = new XMLEntityManager();
    }

    public boolean registerPreparser(String string, XMLGrammarLoader xMLGrammarLoader) {
        if (xMLGrammarLoader == null) {
            if (KNOWN_LOADERS.containsKey(string)) {
                String string2 = (String)KNOWN_LOADERS.get(string);
                try {
                    ClassLoader classLoader = ObjectFactory.findClassLoader();
                    XMLGrammarLoader xMLGrammarLoader2 = (XMLGrammarLoader)ObjectFactory.newInstance(string2, classLoader, true);
                    this.fLoaders.put(string, new XMLGrammarLoaderContainer(xMLGrammarLoader2));
                }
                catch (Exception exception) {
                    return false;
                }
                return true;
            }
            return false;
        }
        this.fLoaders.put(string, new XMLGrammarLoaderContainer(xMLGrammarLoader));
        return true;
    }

    public Grammar preparseGrammar(String string, XMLInputSource xMLInputSource) throws XNIException, IOException {
        if (this.fLoaders.containsKey(string)) {
            XMLGrammarLoaderContainer xMLGrammarLoaderContainer = (XMLGrammarLoaderContainer)this.fLoaders.get(string);
            XMLGrammarLoader xMLGrammarLoader = xMLGrammarLoaderContainer.loader;
            if (xMLGrammarLoaderContainer.modCount != this.fModCount) {
                xMLGrammarLoader.setProperty(SYMBOL_TABLE, this.fSymbolTable);
                xMLGrammarLoader.setProperty(ENTITY_RESOLVER, this.fEntityResolver);
                xMLGrammarLoader.setProperty(ERROR_REPORTER, this.fErrorReporter);
                if (this.fGrammarPool != null) {
                    try {
                        xMLGrammarLoader.setProperty(GRAMMAR_POOL, this.fGrammarPool);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                xMLGrammarLoaderContainer.modCount = this.fModCount;
            }
            return xMLGrammarLoader.loadGrammar(xMLInputSource);
        }
        return null;
    }

    public void setLocale(Locale locale) {
        this.fLocale = locale;
        this.fErrorReporter.setLocale(locale);
    }

    public Locale getLocale() {
        return this.fLocale;
    }

    public void setErrorHandler(XMLErrorHandler xMLErrorHandler) {
        this.fErrorReporter.setProperty(ERROR_HANDLER, xMLErrorHandler);
    }

    public XMLErrorHandler getErrorHandler() {
        return this.fErrorReporter.getErrorHandler();
    }

    public void setEntityResolver(XMLEntityResolver xMLEntityResolver) {
        if (this.fEntityResolver != xMLEntityResolver) {
            if (++this.fModCount < 0) {
                this.clearModCounts();
            }
            this.fEntityResolver = xMLEntityResolver;
        }
    }

    public XMLEntityResolver getEntityResolver() {
        return this.fEntityResolver;
    }

    public void setGrammarPool(XMLGrammarPool xMLGrammarPool) {
        if (this.fGrammarPool != xMLGrammarPool) {
            if (++this.fModCount < 0) {
                this.clearModCounts();
            }
            this.fGrammarPool = xMLGrammarPool;
        }
    }

    public XMLGrammarPool getGrammarPool() {
        return this.fGrammarPool;
    }

    public XMLGrammarLoader getLoader(String string) {
        XMLGrammarLoaderContainer xMLGrammarLoaderContainer = (XMLGrammarLoaderContainer)this.fLoaders.get(string);
        return xMLGrammarLoaderContainer != null ? xMLGrammarLoaderContainer.loader : null;
    }

    public void setFeature(String string, boolean bl) {
        Enumeration enumeration = this.fLoaders.elements();
        while (enumeration.hasMoreElements()) {
            XMLGrammarLoader xMLGrammarLoader = ((XMLGrammarLoaderContainer)enumeration.nextElement()).loader;
            try {
                xMLGrammarLoader.setFeature(string, bl);
            }
            catch (Exception exception) {}
        }
        if (string.equals(CONTINUE_AFTER_FATAL_ERROR)) {
            this.fErrorReporter.setFeature(CONTINUE_AFTER_FATAL_ERROR, bl);
        }
    }

    public void setProperty(String string, Object object) {
        Enumeration enumeration = this.fLoaders.elements();
        while (enumeration.hasMoreElements()) {
            XMLGrammarLoader xMLGrammarLoader = ((XMLGrammarLoaderContainer)enumeration.nextElement()).loader;
            try {
                xMLGrammarLoader.setProperty(string, object);
            }
            catch (Exception exception) {}
        }
    }

    public boolean getFeature(String string, String string2) {
        XMLGrammarLoader xMLGrammarLoader = ((XMLGrammarLoaderContainer)this.fLoaders.get((Object)string)).loader;
        return xMLGrammarLoader.getFeature(string2);
    }

    public Object getProperty(String string, String string2) {
        XMLGrammarLoader xMLGrammarLoader = ((XMLGrammarLoaderContainer)this.fLoaders.get((Object)string)).loader;
        return xMLGrammarLoader.getProperty(string2);
    }

    private void clearModCounts() {
        Enumeration enumeration = this.fLoaders.elements();
        while (enumeration.hasMoreElements()) {
            XMLGrammarLoaderContainer xMLGrammarLoaderContainer = (XMLGrammarLoaderContainer)enumeration.nextElement();
            xMLGrammarLoaderContainer.modCount = 0;
        }
        this.fModCount = 1;
    }

    static {
        KNOWN_LOADERS.put("http://www.w3.org/2001/XMLSchema", "org.apache.xerces.impl.xs.XMLSchemaLoader");
        KNOWN_LOADERS.put("http://www.w3.org/TR/REC-xml", "org.apache.xerces.impl.dtd.XMLDTDLoader");
        RECOGNIZED_PROPERTIES = new String[]{SYMBOL_TABLE, ERROR_REPORTER, ERROR_HANDLER, ENTITY_RESOLVER, GRAMMAR_POOL};
    }

    static class XMLGrammarLoaderContainer {
        public final XMLGrammarLoader loader;
        public int modCount = 0;

        public XMLGrammarLoaderContainer(XMLGrammarLoader xMLGrammarLoader) {
            this.loader = xMLGrammarLoader;
        }
    }
}

