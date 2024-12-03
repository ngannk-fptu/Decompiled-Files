/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.css.engine.CSSContext
 *  org.apache.batik.css.engine.CSSEngine
 *  org.apache.batik.css.engine.value.ShorthandManager
 *  org.apache.batik.css.engine.value.ValueManager
 *  org.apache.batik.css.parser.ExtendedParser
 *  org.apache.batik.css.parser.ExtendedParserWrapper
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.apache.batik.util.Service
 *  org.apache.batik.util.XMLResourceDescriptor
 *  org.apache.batik.xml.XMLUtilities
 *  org.w3c.css.sac.Parser
 */
package org.apache.batik.dom;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.apache.batik.css.engine.CSSContext;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.value.ShorthandManager;
import org.apache.batik.css.engine.value.ValueManager;
import org.apache.batik.css.parser.ExtendedParser;
import org.apache.batik.css.parser.ExtendedParserWrapper;
import org.apache.batik.dom.AbstractDOMImplementation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractStylableDocument;
import org.apache.batik.dom.DomExtension;
import org.apache.batik.dom.GenericDocumentType;
import org.apache.batik.dom.GenericElement;
import org.apache.batik.dom.GenericElementNS;
import org.apache.batik.dom.StyleSheetFactory;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.util.DoublyIndexedTable;
import org.apache.batik.util.Service;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.batik.xml.XMLUtilities;
import org.w3c.css.sac.Parser;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.css.DOMImplementationCSS;
import org.w3c.dom.css.ViewCSS;

public abstract class ExtensibleDOMImplementation
extends AbstractDOMImplementation
implements DOMImplementationCSS,
StyleSheetFactory {
    protected DoublyIndexedTable customFactories;
    protected List customValueManagers;
    protected List customShorthandManagers;
    protected static List extensions = null;

    public ExtensibleDOMImplementation() {
        for (Object o : ExtensibleDOMImplementation.getDomExtensions()) {
            DomExtension de = (DomExtension)o;
            de.registerTags(this);
        }
    }

    public void registerCustomElementFactory(String namespaceURI, String localName, ElementFactory factory) {
        if (this.customFactories == null) {
            this.customFactories = new DoublyIndexedTable();
        }
        this.customFactories.put((Object)namespaceURI, (Object)localName, (Object)factory);
    }

    public void registerCustomCSSValueManager(ValueManager vm) {
        if (this.customValueManagers == null) {
            this.customValueManagers = new LinkedList();
        }
        this.customValueManagers.add(vm);
    }

    public void registerCustomCSSShorthandManager(ShorthandManager sm) {
        if (this.customShorthandManagers == null) {
            this.customShorthandManagers = new LinkedList();
        }
        this.customShorthandManagers.add(sm);
    }

    public CSSEngine createCSSEngine(AbstractStylableDocument doc, CSSContext ctx) {
        ShorthandManager[] sms;
        ValueManager[] vms;
        Parser p;
        String pn = XMLResourceDescriptor.getCSSParserClassName();
        try {
            p = (Parser)Class.forName(pn).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (ClassNotFoundException e) {
            throw new DOMException(15, this.formatMessage("css.parser.class", new Object[]{pn}));
        }
        catch (InstantiationException e) {
            throw new DOMException(15, this.formatMessage("css.parser.creation", new Object[]{pn}));
        }
        catch (IllegalAccessException e) {
            throw new DOMException(15, this.formatMessage("css.parser.access", new Object[]{pn}));
        }
        catch (NoSuchMethodException e) {
            throw new DOMException(15, this.formatMessage("css.parser.access", new Object[]{pn}));
        }
        catch (InvocationTargetException e) {
            throw new DOMException(15, this.formatMessage("css.parser.access", new Object[]{pn}));
        }
        ExtendedParser ep = ExtendedParserWrapper.wrap((Parser)p);
        if (this.customValueManagers == null) {
            vms = new ValueManager[]{};
        } else {
            vms = new ValueManager[this.customValueManagers.size()];
            Iterator it = this.customValueManagers.iterator();
            int i = 0;
            while (it.hasNext()) {
                vms[i++] = (ValueManager)it.next();
            }
        }
        if (this.customShorthandManagers == null) {
            sms = new ShorthandManager[]{};
        } else {
            sms = new ShorthandManager[this.customShorthandManagers.size()];
            Iterator it = this.customShorthandManagers.iterator();
            int i = 0;
            while (it.hasNext()) {
                sms[i++] = (ShorthandManager)it.next();
            }
        }
        CSSEngine result = this.createCSSEngine(doc, ctx, ep, vms, sms);
        doc.setCSSEngine(result);
        return result;
    }

    public abstract CSSEngine createCSSEngine(AbstractStylableDocument var1, CSSContext var2, ExtendedParser var3, ValueManager[] var4, ShorthandManager[] var5);

    public abstract ViewCSS createViewCSS(AbstractStylableDocument var1);

    public Element createElementNS(AbstractDocument document, String namespaceURI, String qualifiedName) {
        String name;
        ElementFactory cef;
        if (namespaceURI != null && namespaceURI.length() == 0) {
            namespaceURI = null;
        }
        if (namespaceURI == null) {
            return new GenericElement(qualifiedName.intern(), document);
        }
        if (this.customFactories != null && (cef = (ElementFactory)this.customFactories.get((Object)namespaceURI, (Object)(name = DOMUtilities.getLocalName(qualifiedName)))) != null) {
            return cef.create(DOMUtilities.getPrefix(qualifiedName), document);
        }
        return new GenericElementNS(namespaceURI.intern(), qualifiedName.intern(), document);
    }

    @Override
    public DocumentType createDocumentType(String qualifiedName, String publicId, String systemId) {
        int test;
        if (qualifiedName == null) {
            qualifiedName = "";
        }
        if (((test = XMLUtilities.testXMLQName((String)qualifiedName)) & 1) == 0) {
            throw new DOMException(5, this.formatMessage("xml.name", new Object[]{qualifiedName}));
        }
        if ((test & 2) == 0) {
            throw new DOMException(5, this.formatMessage("invalid.qname", new Object[]{qualifiedName}));
        }
        return new GenericDocumentType(qualifiedName, publicId, systemId);
    }

    protected static synchronized List getDomExtensions() {
        if (extensions != null) {
            return extensions;
        }
        extensions = new LinkedList();
        Iterator iter = Service.providers(DomExtension.class);
        block0: while (iter.hasNext()) {
            DomExtension lde;
            DomExtension de = (DomExtension)iter.next();
            float priority = de.getPriority();
            ListIterator<DomExtension> li = extensions.listIterator();
            do {
                if (li.hasNext()) continue;
                li.add(de);
                continue block0;
            } while (!((lde = (DomExtension)li.next()).getPriority() > priority));
            li.previous();
            li.add(de);
        }
        return extensions;
    }

    public static interface ElementFactory {
        public Element create(String var1, Document var2);
    }
}

