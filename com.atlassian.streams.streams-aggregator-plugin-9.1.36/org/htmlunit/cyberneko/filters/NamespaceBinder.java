/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.filters;

import java.util.Locale;
import org.htmlunit.cyberneko.HTMLConfiguration;
import org.htmlunit.cyberneko.filters.DefaultFilter;
import org.htmlunit.cyberneko.xerces.xni.Augmentations;
import org.htmlunit.cyberneko.xerces.xni.NamespaceContext;
import org.htmlunit.cyberneko.xerces.xni.QName;
import org.htmlunit.cyberneko.xerces.xni.XMLAttributes;
import org.htmlunit.cyberneko.xerces.xni.XMLLocator;
import org.htmlunit.cyberneko.xerces.xni.XNIException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLComponentManager;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLConfigurationException;

public class NamespaceBinder
extends DefaultFilter {
    public static final String XHTML_1_0_URI = "http://www.w3.org/1999/xhtml";
    public static final String XML_URI = "http://www.w3.org/XML/1998/namespace";
    public static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
    private static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    private static final String OVERRIDE_NAMESPACES = "http://cyberneko.org/html/features/override-namespaces";
    private static final String INSERT_NAMESPACES = "http://cyberneko.org/html/features/insert-namespaces";
    private static final String[] RECOGNIZED_FEATURES = new String[]{"http://xml.org/sax/features/namespaces", "http://cyberneko.org/html/features/override-namespaces", "http://cyberneko.org/html/features/insert-namespaces"};
    private static final Boolean[] FEATURE_DEFAULTS = new Boolean[]{null, Boolean.FALSE, Boolean.FALSE};
    private static final String NAMES_ELEMS = "http://cyberneko.org/html/properties/names/elems";
    private static final String NAMESPACES_URI = "http://cyberneko.org/html/properties/namespaces-uri";
    private static final String[] RECOGNIZED_PROPERTIES = new String[]{"http://cyberneko.org/html/properties/names/elems", "http://cyberneko.org/html/properties/namespaces-uri"};
    private static final Object[] PROPERTY_DEFAULTS = new Object[]{null, null, "http://www.w3.org/1999/xhtml"};
    private static final short NAMES_NO_CHANGE = 0;
    private static final short NAMES_UPPERCASE = 1;
    private static final short NAMES_LOWERCASE = 2;
    private boolean fNamespaces_;
    private boolean fOverrideNamespaces_;
    private boolean fInsertNamespaces_;
    private short fNamesElems_;
    private String fNamespacesURI_;
    private final NamespaceSupport fNamespaceContext_ = new NamespaceSupport();
    private final QName fQName_ = new QName();
    private final HTMLConfiguration htmlConfiguration_;

    public NamespaceBinder(HTMLConfiguration htmlConfiguration) {
        this.htmlConfiguration_ = htmlConfiguration;
    }

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
        this.fNamespaces_ = manager.getFeature(NAMESPACES);
        this.fOverrideNamespaces_ = manager.getFeature(OVERRIDE_NAMESPACES);
        this.fInsertNamespaces_ = manager.getFeature(INSERT_NAMESPACES);
        this.fNamesElems_ = NamespaceBinder.getNamesValue(String.valueOf(manager.getProperty(NAMES_ELEMS)));
        this.fNamespacesURI_ = String.valueOf(manager.getProperty(NAMESPACES_URI));
        this.fNamespaceContext_.reset();
    }

    @Override
    public void startDocument(XMLLocator locator, String encoding, NamespaceContext nscontext, Augmentations augs) throws XNIException {
        super.startDocument(locator, encoding, this.fNamespaceContext_, augs);
    }

    @Override
    public void startElement(QName element, XMLAttributes attrs, Augmentations augs) throws XNIException {
        if (this.fNamespaces_) {
            this.fNamespaceContext_.pushContext();
            this.bindNamespaces(element, attrs);
        }
        super.startElement(element, attrs, augs);
    }

    @Override
    public void emptyElement(QName element, XMLAttributes attrs, Augmentations augs) throws XNIException {
        if (this.fNamespaces_) {
            this.fNamespaceContext_.pushContext();
            this.bindNamespaces(element, attrs);
        }
        super.emptyElement(element, attrs, augs);
        if (this.fNamespaces_) {
            this.fNamespaceContext_.popContext();
        }
    }

    @Override
    public void endElement(QName element, Augmentations augs) throws XNIException {
        if (this.fNamespaces_) {
            this.bindNamespaces(element, null);
        }
        super.endElement(element, augs);
        if (this.fNamespaces_) {
            this.fNamespaceContext_.popContext();
        }
    }

    protected static void splitQName(QName qname) {
        int index = qname.rawname.indexOf(58);
        if (index != -1) {
            qname.prefix = qname.rawname.substring(0, index);
            qname.localpart = qname.rawname.substring(index + 1);
        }
    }

    protected static short getNamesValue(String value) {
        if ("lower".equals(value)) {
            return 2;
        }
        if ("upper".equals(value)) {
            return 1;
        }
        return 0;
    }

    protected static String modifyName(String name, short mode) {
        switch (mode) {
            case 1: {
                return name.toUpperCase(Locale.ROOT);
            }
            case 2: {
                return name.toLowerCase(Locale.ROOT);
            }
        }
        return name;
    }

    protected void bindNamespaces(QName element, XMLAttributes attrs) {
        NamespaceBinder.splitQName(element);
        if (attrs != null) {
            int attrCount = attrs.getLength();
            for (int i = attrCount - 1; i >= 0; --i) {
                String uri;
                attrs.getName(i, this.fQName_);
                String rawname = this.fQName_.rawname;
                String rawnameUC = rawname.toUpperCase(Locale.ROOT);
                if (!rawnameUC.startsWith("XMLNS:") && !"XMLNS".equals(rawnameUC)) continue;
                int anamelen = rawname.length();
                String aprefix = anamelen > 5 ? rawname.substring(0, 5) : null;
                String alocal = anamelen > 5 ? rawname.substring(6) : rawname;
                String avalue = attrs.getValue(i);
                if (anamelen > 5) {
                    aprefix = NamespaceBinder.modifyName(aprefix, (short)2);
                    alocal = NamespaceBinder.modifyName(alocal, this.fNamesElems_);
                    rawname = aprefix + ':' + alocal;
                } else {
                    rawname = alocal = NamespaceBinder.modifyName(alocal, (short)2);
                }
                this.fQName_.setValues(aprefix, alocal, rawname, null);
                attrs.setName(i, this.fQName_);
                String prefix = alocal != rawname ? alocal : "";
                String string = uri = avalue.length() > 0 ? avalue : null;
                if (this.fOverrideNamespaces_ && prefix.equals(element.prefix) && this.htmlConfiguration_.getHtmlElements().getElement(element.localpart, null) != null) {
                    uri = this.fNamespacesURI_;
                }
                this.fNamespaceContext_.declarePrefix(prefix, uri);
            }
        }
        String prefix = element.prefix != null ? element.prefix : "";
        element.uri = this.fNamespaceContext_.getURI(prefix);
        if (element.uri != null && element.prefix == null) {
            element.prefix = "";
        }
        if (this.fInsertNamespaces_ && attrs != null && this.htmlConfiguration_.getHtmlElements().getElement(element.localpart, null) != null && (element.prefix == null || this.fNamespaceContext_.getURI(element.prefix) == null)) {
            String xmlns = "xmlns" + (element.prefix != null ? ":" + element.prefix : "");
            this.fQName_.setValues(null, xmlns, xmlns, null);
            attrs.addAttribute(this.fQName_, "CDATA", this.fNamespacesURI_);
            this.bindNamespaces(element, attrs);
            return;
        }
        if (attrs != null) {
            int attrCount = attrs.getLength();
            for (int i = 0; i < attrCount; ++i) {
                attrs.getName(i, this.fQName_);
                NamespaceBinder.splitQName(this.fQName_);
                String string = !"xmlns".equals(this.fQName_.rawname) ? (this.fQName_.prefix != null ? this.fQName_.prefix : "") : (prefix = "xmlns");
                if (!"".equals(prefix)) {
                    String string2 = this.fQName_.uri = "xml".equals(prefix) ? XML_URI : this.fNamespaceContext_.getURI(prefix);
                }
                if ("xmlns".equals(prefix) && this.fQName_.uri == null) {
                    this.fQName_.uri = XMLNS_URI;
                }
                attrs.setName(i, this.fQName_);
            }
        }
    }

    public static class NamespaceSupport
    implements NamespaceContext {
        protected int fTop = 0;
        protected int[] fLevels = new int[10];
        protected Entry[] fEntries = new Entry[10];

        public NamespaceSupport() {
            this.pushContext();
            this.declarePrefix("xml", NamespaceBinder.XML_URI);
            this.declarePrefix("xmlns", NamespaceBinder.XMLNS_URI);
        }

        @Override
        public String getURI(String prefix) {
            for (int i = this.fLevels[this.fTop] - 1; i >= 0; --i) {
                Entry entry = this.fEntries[i];
                if (!entry.prefix.equals(prefix)) continue;
                return entry.uri;
            }
            return null;
        }

        @Override
        public int getDeclaredPrefixCount() {
            return this.fLevels[this.fTop] - this.fLevels[this.fTop - 1];
        }

        @Override
        public String getDeclaredPrefixAt(int index) {
            return this.fEntries[this.fLevels[this.fTop - 1] + index].prefix;
        }

        public NamespaceContext getParentContext() {
            return this;
        }

        @Override
        public void reset() {
            this.fTop = 1;
            this.fLevels[1] = this.fLevels[this.fTop - 1];
        }

        @Override
        public void pushContext() {
            if (++this.fTop == this.fLevels.length) {
                int[] iarray = new int[this.fLevels.length + 10];
                System.arraycopy(this.fLevels, 0, iarray, 0, this.fLevels.length);
                this.fLevels = iarray;
            }
            this.fLevels[this.fTop] = this.fLevels[this.fTop - 1];
        }

        @Override
        public void popContext() {
            if (this.fTop > 1) {
                --this.fTop;
            }
        }

        @Override
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

        static final class Entry {
            public final String prefix;
            public final String uri;

            Entry(String prefix, String uri) {
                this.prefix = prefix;
                this.uri = uri;
            }
        }
    }
}

