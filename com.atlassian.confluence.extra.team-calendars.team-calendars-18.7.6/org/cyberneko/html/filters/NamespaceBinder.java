/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.xni.Augmentations
 *  org.apache.xerces.xni.NamespaceContext
 *  org.apache.xerces.xni.QName
 *  org.apache.xerces.xni.XMLAttributes
 *  org.apache.xerces.xni.XMLLocator
 *  org.apache.xerces.xni.XNIException
 *  org.apache.xerces.xni.parser.XMLComponentManager
 *  org.apache.xerces.xni.parser.XMLConfigurationException
 */
package org.cyberneko.html.filters;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.cyberneko.html.HTMLElements;
import org.cyberneko.html.filters.DefaultFilter;
import org.cyberneko.html.xercesbridge.XercesBridge;

public class NamespaceBinder
extends DefaultFilter {
    public static final String XHTML_1_0_URI = "http://www.w3.org/1999/xhtml";
    public static final String XML_URI = "http://www.w3.org/XML/1998/namespace";
    public static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String OVERRIDE_NAMESPACES = "http://cyberneko.org/html/features/override-namespaces";
    protected static final String INSERT_NAMESPACES = "http://cyberneko.org/html/features/insert-namespaces";
    private static final String[] RECOGNIZED_FEATURES = new String[]{"http://xml.org/sax/features/namespaces", "http://cyberneko.org/html/features/override-namespaces", "http://cyberneko.org/html/features/insert-namespaces"};
    private static final Boolean[] FEATURE_DEFAULTS = new Boolean[]{null, Boolean.FALSE, Boolean.FALSE};
    protected static final String NAMES_ELEMS = "http://cyberneko.org/html/properties/names/elems";
    protected static final String NAMES_ATTRS = "http://cyberneko.org/html/properties/names/attrs";
    protected static final String NAMESPACES_URI = "http://cyberneko.org/html/properties/namespaces-uri";
    private static final String[] RECOGNIZED_PROPERTIES = new String[]{"http://cyberneko.org/html/properties/names/elems", "http://cyberneko.org/html/properties/names/attrs", "http://cyberneko.org/html/properties/namespaces-uri"};
    private static final Object[] PROPERTY_DEFAULTS = new Object[]{null, null, "http://www.w3.org/1999/xhtml"};
    protected static final short NAMES_NO_CHANGE = 0;
    protected static final short NAMES_UPPERCASE = 1;
    protected static final short NAMES_LOWERCASE = 2;
    protected boolean fNamespaces;
    protected boolean fNamespacePrefixes;
    protected boolean fOverrideNamespaces;
    protected boolean fInsertNamespaces;
    protected short fNamesElems;
    protected short fNamesAttrs;
    protected String fNamespacesURI;
    protected final NamespaceSupport fNamespaceContext = new NamespaceSupport();
    private final QName fQName = new QName();

    @Override
    public String[] getRecognizedFeatures() {
        return NamespaceBinder.merge(super.getRecognizedFeatures(), RECOGNIZED_FEATURES);
    }

    @Override
    public Boolean getFeatureDefault(String featureId) {
        for (int i = 0; i < RECOGNIZED_FEATURES.length; ++i) {
            if (!RECOGNIZED_FEATURES[i].equals(featureId)) continue;
            return FEATURE_DEFAULTS[i];
        }
        return super.getFeatureDefault(featureId);
    }

    @Override
    public String[] getRecognizedProperties() {
        return NamespaceBinder.merge(super.getRecognizedProperties(), RECOGNIZED_PROPERTIES);
    }

    @Override
    public Object getPropertyDefault(String propertyId) {
        for (int i = 0; i < RECOGNIZED_PROPERTIES.length; ++i) {
            if (!RECOGNIZED_PROPERTIES[i].equals(propertyId)) continue;
            return PROPERTY_DEFAULTS[i];
        }
        return super.getPropertyDefault(propertyId);
    }

    @Override
    public void reset(XMLComponentManager manager) throws XMLConfigurationException {
        super.reset(manager);
        this.fNamespaces = manager.getFeature(NAMESPACES);
        this.fOverrideNamespaces = manager.getFeature(OVERRIDE_NAMESPACES);
        this.fInsertNamespaces = manager.getFeature(INSERT_NAMESPACES);
        this.fNamesElems = NamespaceBinder.getNamesValue(String.valueOf(manager.getProperty(NAMES_ELEMS)));
        this.fNamesAttrs = NamespaceBinder.getNamesValue(String.valueOf(manager.getProperty(NAMES_ATTRS)));
        this.fNamespacesURI = String.valueOf(manager.getProperty(NAMESPACES_URI));
        this.fNamespaceContext.reset();
    }

    @Override
    public void startDocument(XMLLocator locator, String encoding, NamespaceContext nscontext, Augmentations augs) throws XNIException {
        super.startDocument(locator, encoding, this.fNamespaceContext, augs);
    }

    @Override
    public void startElement(QName element, XMLAttributes attrs, Augmentations augs) throws XNIException {
        if (this.fNamespaces) {
            this.fNamespaceContext.pushContext();
            this.bindNamespaces(element, attrs);
            int dcount = this.fNamespaceContext.getDeclaredPrefixCount();
            if (this.fDocumentHandler != null && dcount > 0) {
                for (int i = 0; i < dcount; ++i) {
                    String prefix = this.fNamespaceContext.getDeclaredPrefixAt(i);
                    String uri = this.fNamespaceContext.getURI(prefix);
                    XercesBridge.getInstance().XMLDocumentHandler_startPrefixMapping(this.fDocumentHandler, prefix, uri, augs);
                }
            }
        }
        super.startElement(element, attrs, augs);
    }

    @Override
    public void emptyElement(QName element, XMLAttributes attrs, Augmentations augs) throws XNIException {
        String prefix;
        int i;
        int dcount;
        if (this.fNamespaces) {
            this.fNamespaceContext.pushContext();
            this.bindNamespaces(element, attrs);
            dcount = this.fNamespaceContext.getDeclaredPrefixCount();
            if (this.fDocumentHandler != null && dcount > 0) {
                for (i = 0; i < dcount; ++i) {
                    prefix = this.fNamespaceContext.getDeclaredPrefixAt(i);
                    String uri = this.fNamespaceContext.getURI(prefix);
                    XercesBridge.getInstance().XMLDocumentHandler_startPrefixMapping(this.fDocumentHandler, prefix, uri, augs);
                }
            }
        }
        super.emptyElement(element, attrs, augs);
        if (this.fNamespaces) {
            dcount = this.fNamespaceContext.getDeclaredPrefixCount();
            if (this.fDocumentHandler != null && dcount > 0) {
                for (i = dcount - 1; i >= 0; --i) {
                    prefix = this.fNamespaceContext.getDeclaredPrefixAt(i);
                    XercesBridge.getInstance().XMLDocumentHandler_endPrefixMapping(this.fDocumentHandler, prefix, augs);
                }
            }
            this.fNamespaceContext.popContext();
        }
    }

    @Override
    public void endElement(QName element, Augmentations augs) throws XNIException {
        if (this.fNamespaces) {
            this.bindNamespaces(element, null);
        }
        super.endElement(element, augs);
        if (this.fNamespaces) {
            int dcount = this.fNamespaceContext.getDeclaredPrefixCount();
            if (this.fDocumentHandler != null && dcount > 0) {
                for (int i = dcount - 1; i >= 0; --i) {
                    String prefix = this.fNamespaceContext.getDeclaredPrefixAt(i);
                    XercesBridge.getInstance().XMLDocumentHandler_endPrefixMapping(this.fDocumentHandler, prefix, augs);
                }
            }
            this.fNamespaceContext.popContext();
        }
    }

    protected static void splitQName(QName qname) {
        int index = qname.rawname.indexOf(58);
        if (index != -1) {
            qname.prefix = qname.rawname.substring(0, index);
            qname.localpart = qname.rawname.substring(index + 1);
        }
    }

    protected static final short getNamesValue(String value) {
        if (value.equals("lower")) {
            return 2;
        }
        if (value.equals("upper")) {
            return 1;
        }
        return 0;
    }

    protected static final String modifyName(String name, short mode) {
        switch (mode) {
            case 1: {
                return name.toUpperCase(Locale.ENGLISH);
            }
            case 2: {
                return name.toLowerCase(Locale.ENGLISH);
            }
        }
        return name;
    }

    protected void bindNamespaces(QName element, XMLAttributes attrs) {
        NamespaceBinder.splitQName(element);
        int attrCount = attrs != null ? attrs.getLength() : 0;
        for (int i = attrCount - 1; i >= 0; --i) {
            String uri;
            attrs.getName(i, this.fQName);
            String aname = this.fQName.rawname;
            String ANAME = aname.toUpperCase(Locale.ENGLISH);
            if (!ANAME.startsWith("XMLNS:") && !ANAME.equals("XMLNS")) continue;
            int anamelen = aname.length();
            String aprefix = anamelen > 5 ? aname.substring(0, 5) : null;
            String alocal = anamelen > 5 ? aname.substring(6) : aname;
            String avalue = attrs.getValue(i);
            if (anamelen > 5) {
                aprefix = NamespaceBinder.modifyName(aprefix, (short)2);
                alocal = NamespaceBinder.modifyName(alocal, this.fNamesElems);
                aname = aprefix + ':' + alocal;
            } else {
                aname = alocal = NamespaceBinder.modifyName(alocal, (short)2);
            }
            this.fQName.setValues(aprefix, alocal, aname, null);
            attrs.setName(i, this.fQName);
            String prefix = alocal != aname ? alocal : "";
            String string = uri = avalue.length() > 0 ? avalue : null;
            if (this.fOverrideNamespaces && prefix.equals(element.prefix) && HTMLElements.getElement(element.localpart, null) != null) {
                uri = this.fNamespacesURI;
            }
            this.fNamespaceContext.declarePrefix(prefix, uri);
        }
        String prefix = element.prefix != null ? element.prefix : "";
        element.uri = this.fNamespaceContext.getURI(prefix);
        if (element.uri != null && element.prefix == null) {
            element.prefix = "";
        }
        if (this.fInsertNamespaces && attrs != null && HTMLElements.getElement(element.localpart, null) != null && (element.prefix == null || this.fNamespaceContext.getURI(element.prefix) == null)) {
            String xmlns = "xmlns" + (element.prefix != null ? ":" + element.prefix : "");
            this.fQName.setValues(null, xmlns, xmlns, null);
            attrs.addAttribute(this.fQName, "CDATA", this.fNamespacesURI);
            this.bindNamespaces(element, attrs);
            return;
        }
        attrCount = attrs != null ? attrs.getLength() : 0;
        for (int i = 0; i < attrCount; ++i) {
            attrs.getName(i, this.fQName);
            NamespaceBinder.splitQName(this.fQName);
            String string = !this.fQName.rawname.equals("xmlns") ? (this.fQName.prefix != null ? this.fQName.prefix : "") : (prefix = "xmlns");
            if (!prefix.equals("")) {
                String string2 = this.fQName.uri = prefix.equals("xml") ? XML_URI : this.fNamespaceContext.getURI(prefix);
            }
            if (prefix.equals("xmlns") && this.fQName.uri == null) {
                this.fQName.uri = XMLNS_URI;
            }
            attrs.setName(i, this.fQName);
        }
    }

    public static class NamespaceSupport
    implements NamespaceContext {
        protected int fTop = 0;
        protected int[] fLevels = new int[10];
        protected Entry[] fEntries = new Entry[10];

        public NamespaceSupport() {
            this.pushContext();
            this.declarePrefix("xml", NamespaceContext.XML_URI);
            this.declarePrefix("xmlns", NamespaceContext.XMLNS_URI);
        }

        public String getURI(String prefix) {
            for (int i = this.fLevels[this.fTop] - 1; i >= 0; --i) {
                Entry entry = this.fEntries[i];
                if (!entry.prefix.equals(prefix)) continue;
                return entry.uri;
            }
            return null;
        }

        public int getDeclaredPrefixCount() {
            return this.fLevels[this.fTop] - this.fLevels[this.fTop - 1];
        }

        public String getDeclaredPrefixAt(int index) {
            return this.fEntries[this.fLevels[this.fTop - 1] + index].prefix;
        }

        public NamespaceContext getParentContext() {
            return this;
        }

        public void reset() {
            this.fTop = 1;
            this.fLevels[1] = this.fLevels[this.fTop - 1];
        }

        public void pushContext() {
            if (++this.fTop == this.fLevels.length) {
                int[] iarray = new int[this.fLevels.length + 10];
                System.arraycopy(this.fLevels, 0, iarray, 0, this.fLevels.length);
                this.fLevels = iarray;
            }
            this.fLevels[this.fTop] = this.fLevels[this.fTop - 1];
        }

        public void popContext() {
            if (this.fTop > 1) {
                --this.fTop;
            }
        }

        public boolean declarePrefix(String prefix, String uri) {
            int count = this.getDeclaredPrefixCount();
            for (int i = 0; i < count; ++i) {
                String dprefix = this.getDeclaredPrefixAt(i);
                if (!dprefix.equals(prefix)) continue;
                return false;
            }
            Entry entry = new Entry(prefix, uri);
            if (this.fLevels[this.fTop] == this.fEntries.length) {
                Entry[] earray = new Entry[this.fEntries.length + 10];
                System.arraycopy(this.fEntries, 0, earray, 0, this.fEntries.length);
                this.fEntries = earray;
            }
            int n = this.fTop;
            int n2 = this.fLevels[n];
            this.fLevels[n] = n2 + 1;
            this.fEntries[n2] = entry;
            return true;
        }

        public String getPrefix(String uri) {
            for (int i = this.fLevels[this.fTop] - 1; i >= 0; --i) {
                Entry entry = this.fEntries[i];
                if (!entry.uri.equals(uri)) continue;
                return entry.prefix;
            }
            return null;
        }

        public Enumeration getAllPrefixes() {
            Vector<String> prefixes = new Vector<String>();
            for (int i = this.fLevels[1]; i < this.fLevels[this.fTop]; ++i) {
                String prefix = this.fEntries[i].prefix;
                if (prefixes.contains(prefix)) continue;
                prefixes.addElement(prefix);
            }
            return prefixes.elements();
        }

        static class Entry {
            public String prefix;
            public String uri;

            public Entry(String prefix, String uri) {
                this.prefix = prefix;
                this.uri = uri;
            }
        }
    }
}

