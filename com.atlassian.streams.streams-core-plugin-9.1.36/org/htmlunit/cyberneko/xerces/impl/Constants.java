/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.impl;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public final class Constants {
    public static final String SAX_FEATURE_PREFIX = "http://xml.org/sax/features/";
    public static final String NAMESPACES_FEATURE = "namespaces";
    public static final String NAMESPACE_PREFIXES_FEATURE = "namespace-prefixes";
    public static final String LEXICAL_HANDLER_PARAMETER_ENTITIES_FEATURE = "lexical-handler/parameter-entities";
    public static final String UNICODE_NORMALIZATION_CHECKING_FEATURE = "unicode-normalization-checking";
    public static final String SAX_PROPERTY_PREFIX = "http://xml.org/sax/properties/";
    public static final String LEXICAL_HANDLER_PROPERTY = "lexical-handler";
    public static final String DOCUMENT_XML_VERSION_PROPERTY = "document-xml-version";
    public static final String INCLUDE_COMMENTS_FEATURE = "include-comments";
    public static final String CREATE_CDATA_NODES_FEATURE = "create-cdata-nodes";
    public static final String XERCES_FEATURE_PREFIX = "http://apache.org/xml/features/";
    public static final String CONTINUE_AFTER_FATAL_ERROR_FEATURE = "continue-after-fatal-error";
    public static final String CREATE_ENTITY_REF_NODES_FEATURE = "dom/create-entity-ref-nodes";
    public static final String INCLUDE_IGNORABLE_WHITESPACE = "dom/include-ignorable-whitespace";
    public static final String NOTIFY_BUILTIN_REFS_FEATURE = "scanner/notify-builtin-refs";
    public static final String STANDARD_URI_CONFORMANT_FEATURE = "standard-uri-conformant";
    public static final String XERCES_PROPERTY_PREFIX = "http://apache.org/xml/properties/";
    public static final String ERROR_HANDLER_PROPERTY = "internal/error-handler";
    private static final String[] fgSAXProperties = new String[]{"lexical-handler"};
    private static final String[] fgXercesFeatures = new String[]{"continue-after-fatal-error", "dom/create-entity-ref-nodes", "dom/include-ignorable-whitespace", "scanner/notify-builtin-refs", "standard-uri-conformant"};
    private static final String[] fgXercesProperties = new String[]{"internal/error-handler"};
    private static final Enumeration<Object> fgEmptyEnumeration = new ArrayEnumeration(new Object[0]);

    private Constants() {
    }

    public static Enumeration<Object> getSAXProperties() {
        return fgSAXProperties.length > 0 ? new ArrayEnumeration(fgSAXProperties) : fgEmptyEnumeration;
    }

    public static Enumeration<Object> getXercesFeatures() {
        return fgXercesFeatures.length > 0 ? new ArrayEnumeration(fgXercesFeatures) : fgEmptyEnumeration;
    }

    public static Enumeration<Object> getXercesProperties() {
        return fgXercesProperties.length > 0 ? new ArrayEnumeration(fgXercesProperties) : fgEmptyEnumeration;
    }

    public static void main(String[] argv) {
        Constants.print("SAX properties:", SAX_PROPERTY_PREFIX, fgSAXProperties);
        Constants.print("Xerces features:", XERCES_FEATURE_PREFIX, fgXercesFeatures);
        Constants.print("Xerces properties:", XERCES_PROPERTY_PREFIX, fgXercesProperties);
    }

    private static void print(String header, String prefix, Object[] array) {
        System.out.print(header);
        if (array.length > 0) {
            System.out.println();
            for (Object o : array) {
                System.out.print("  ");
                System.out.print(prefix);
                System.out.println(o);
            }
        } else {
            System.out.println(" none.");
        }
    }

    static class ArrayEnumeration
    implements Enumeration<Object> {
        private final Object[] array_;
        private int index_;

        ArrayEnumeration(Object[] array) {
            this.array_ = array;
        }

        @Override
        public boolean hasMoreElements() {
            return this.index_ < this.array_.length;
        }

        @Override
        public Object nextElement() {
            if (this.index_ < this.array_.length) {
                return this.array_[this.index_++];
            }
            throw new NoSuchElementException();
        }
    }
}

