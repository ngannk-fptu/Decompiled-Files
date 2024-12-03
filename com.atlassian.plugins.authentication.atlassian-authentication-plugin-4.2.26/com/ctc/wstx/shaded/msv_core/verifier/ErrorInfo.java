/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier;

import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

public abstract class ErrorInfo {

    public static class IncompleteContentModel
    extends ElementErrorInfo {
        public IncompleteContentModel(String qn, String ns, String loc) {
            super(qn, ns, loc);
        }

        public IncompleteContentModel(StartTagInfo sti) {
            super(sti);
        }
    }

    public static class MissingAttribute
    extends ElementErrorInfo {
        public MissingAttribute(String qn, String ns, String loc) {
            super(qn, ns, loc);
        }

        public MissingAttribute(StartTagInfo sti) {
            super(sti);
        }
    }

    public static class BadAttribute
    extends ElementErrorInfo {
        public final String attQName;
        public final String attNamespaceURI;
        public final String attLocalName;
        public final String attValue;

        protected BadAttribute(StartTagInfo sti, String qn, String ns, String loc, String v) {
            super(sti);
            this.attQName = qn;
            this.attNamespaceURI = ns;
            this.attLocalName = loc;
            this.attValue = v;
        }
    }

    public static class BadTagName
    extends ElementErrorInfo {
        public BadTagName(String qn, String ns, String loc) {
            super(qn, ns, loc);
        }

        public BadTagName(StartTagInfo sti) {
            super(sti);
        }
    }

    public static class BadText
    extends ErrorInfo {
        public final String literal;

        public BadText(String _literal) {
            this.literal = _literal;
        }

        public BadText(StringBuffer _literal) {
            this.literal = _literal.toString();
        }
    }

    public static abstract class ElementErrorInfo
    extends ErrorInfo {
        public final String qName;
        public final String namespaceURI;
        public final String localName;

        public ElementErrorInfo(String qn, String ns, String loc) {
            this.qName = qn;
            this.namespaceURI = ns;
            this.localName = loc;
        }

        public ElementErrorInfo(StartTagInfo sti) {
            this(sti.qName, sti.namespaceURI, sti.localName);
        }
    }
}

