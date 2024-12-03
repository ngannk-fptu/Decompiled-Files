/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.Name
 */
package com.sun.xml.messaging.saaj.soap.name;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import org.w3c.dom.Element;

public class NameImpl
implements Name {
    public static final String XML_NAMESPACE_PREFIX = "xml";
    public static final String XML_SCHEMA_NAMESPACE_PREFIX = "xs";
    public static final String SOAP_ENVELOPE_PREFIX = "SOAP-ENV";
    public static final String XML_NAMESPACE = "http://www.w3.org/XML/1998/namespace";
    public static final String SOAP11_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/";
    public static final String SOAP12_NAMESPACE = "http://www.w3.org/2003/05/soap-envelope";
    public static final String XML_SCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
    protected String uri = "";
    protected String localName = "";
    protected String prefix = "";
    private String qualifiedName = null;
    protected static final Logger log = Logger.getLogger("com.sun.xml.messaging.saaj.soap.name", "com.sun.xml.messaging.saaj.soap.name.LocalStrings");
    public static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/".intern();

    protected NameImpl(String name) {
        this.localName = name == null ? "" : name;
    }

    protected NameImpl(String name, String prefix, String uri) {
        this.uri = uri == null ? "" : uri;
        this.localName = name == null ? "" : name;
        String string = this.prefix = prefix == null ? "" : prefix;
        if (this.prefix.equals("xmlns") && this.uri.equals("")) {
            this.uri = XMLNS_URI;
        }
        if (this.uri.equals(XMLNS_URI) && this.prefix.equals("")) {
            this.prefix = "xmlns";
        }
    }

    public static Name convertToName(QName qname) {
        return new NameImpl(qname.getLocalPart(), qname.getPrefix(), qname.getNamespaceURI());
    }

    public static QName convertToQName(Name name) {
        return new QName(name.getURI(), name.getLocalName(), name.getPrefix());
    }

    public static NameImpl createFromUnqualifiedName(String name) {
        return new NameImpl(name);
    }

    public static Name createFromTagName(String tagName) {
        return NameImpl.createFromTagAndUri(tagName, "");
    }

    public static Name createFromQualifiedName(String qualifiedName, String uri) {
        return NameImpl.createFromTagAndUri(qualifiedName, uri);
    }

    protected static Name createFromTagAndUri(String tagName, String uri) {
        if (tagName == null) {
            log.severe("SAAJ0201.name.not.created.from.null.tag");
            throw new IllegalArgumentException("Cannot create a name from a null tag.");
        }
        int index = tagName.indexOf(58);
        if (index < 0) {
            return new NameImpl(tagName, "", uri);
        }
        return new NameImpl(tagName.substring(index + 1), tagName.substring(0, index), uri);
    }

    protected static int getPrefixSeparatorIndex(String qualifiedName) {
        int index = qualifiedName.indexOf(58);
        if (index < 0) {
            log.log(Level.SEVERE, "SAAJ0202.name.invalid.arg.format", new String[]{qualifiedName});
            throw new IllegalArgumentException("Argument \"" + qualifiedName + "\" must be of the form: \"prefix:localName\"");
        }
        return index;
    }

    public static String getPrefixFromQualifiedName(String qualifiedName) {
        return qualifiedName.substring(0, NameImpl.getPrefixSeparatorIndex(qualifiedName));
    }

    public static String getLocalNameFromQualifiedName(String qualifiedName) {
        return qualifiedName.substring(NameImpl.getPrefixSeparatorIndex(qualifiedName) + 1);
    }

    public static String getPrefixFromTagName(String tagName) {
        if (NameImpl.isQualified(tagName)) {
            return NameImpl.getPrefixFromQualifiedName(tagName);
        }
        return "";
    }

    public static String getLocalNameFromTagName(String tagName) {
        if (NameImpl.isQualified(tagName)) {
            return NameImpl.getLocalNameFromQualifiedName(tagName);
        }
        return tagName;
    }

    public static boolean isQualified(String tagName) {
        return tagName.indexOf(58) >= 0;
    }

    public static NameImpl create(String name, String prefix, String uri) {
        if (prefix == null) {
            prefix = "";
        }
        if (uri == null) {
            uri = "";
        }
        if (name == null) {
            name = "";
        }
        if (!uri.equals("") && !name.equals("")) {
            if (uri.equals(SOAP11_NAMESPACE)) {
                if (name.equalsIgnoreCase("Envelope")) {
                    return NameImpl.createEnvelope1_1Name(prefix);
                }
                if (name.equalsIgnoreCase("Header")) {
                    return NameImpl.createHeader1_1Name(prefix);
                }
                if (name.equalsIgnoreCase("Body")) {
                    return NameImpl.createBody1_1Name(prefix);
                }
                if (name.equalsIgnoreCase("Fault")) {
                    return NameImpl.createFault1_1Name(prefix);
                }
                return new SOAP1_1Name(name, prefix);
            }
            if (uri.equals(SOAP12_NAMESPACE)) {
                if (name.equalsIgnoreCase("Envelope")) {
                    return NameImpl.createEnvelope1_2Name(prefix);
                }
                if (name.equalsIgnoreCase("Header")) {
                    return NameImpl.createHeader1_2Name(prefix);
                }
                if (name.equalsIgnoreCase("Body")) {
                    return NameImpl.createBody1_2Name(prefix);
                }
                if (name.equals("Fault") || name.equals("Reason") || name.equals("Detail")) {
                    return NameImpl.createFault1_2Name(name, prefix);
                }
                if (name.equals("Code") || name.equals("Subcode")) {
                    return NameImpl.createCodeSubcode1_2Name(prefix, name);
                }
                return new SOAP1_2Name(name, prefix);
            }
        }
        return new NameImpl(name, prefix, uri);
    }

    public static String createQName(String prefix, String localName) {
        if (prefix == null || prefix.equals("")) {
            return localName;
        }
        return prefix + ":" + localName;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Name)) {
            return false;
        }
        Name otherName = (Name)obj;
        if (!this.uri.equals(otherName.getURI())) {
            return false;
        }
        return this.localName.equals(otherName.getLocalName());
    }

    public int hashCode() {
        return this.localName.hashCode();
    }

    public String getLocalName() {
        return this.localName;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getURI() {
        return this.uri;
    }

    public String getQualifiedName() {
        if (this.qualifiedName == null) {
            this.qualifiedName = this.prefix != null && this.prefix.length() > 0 ? this.prefix + ":" + this.localName : this.localName;
        }
        return this.qualifiedName;
    }

    public static NameImpl createEnvelope1_1Name(String prefix) {
        return new Envelope1_1Name(prefix);
    }

    public static NameImpl createEnvelope1_2Name(String prefix) {
        return new Envelope1_2Name(prefix);
    }

    public static NameImpl createHeader1_1Name(String prefix) {
        return new Header1_1Name(prefix);
    }

    public static NameImpl createHeader1_2Name(String prefix) {
        return new Header1_2Name(prefix);
    }

    public static NameImpl createBody1_1Name(String prefix) {
        return new Body1_1Name(prefix);
    }

    public static NameImpl createBody1_2Name(String prefix) {
        return new Body1_2Name(prefix);
    }

    public static NameImpl createFault1_1Name(String prefix) {
        return new Fault1_1Name(prefix);
    }

    public static NameImpl createNotUnderstood1_2Name(String prefix) {
        return new NotUnderstood1_2Name(prefix);
    }

    public static NameImpl createUpgrade1_2Name(String prefix) {
        return new Upgrade1_2Name(prefix);
    }

    public static NameImpl createSupportedEnvelope1_2Name(String prefix) {
        return new SupportedEnvelope1_2Name(prefix);
    }

    public static NameImpl createFault1_2Name(String localName, String prefix) {
        return new Fault1_2Name(localName, prefix);
    }

    public static NameImpl createCodeSubcode1_2Name(String prefix, String localName) {
        return new CodeSubcode1_2Name(localName, prefix);
    }

    public static NameImpl createDetail1_1Name() {
        return new Detail1_1Name();
    }

    public static NameImpl createDetail1_1Name(String prefix) {
        return new Detail1_1Name(prefix);
    }

    public static NameImpl createFaultElement1_1Name(String localName) {
        return new FaultElement1_1Name(localName);
    }

    public static NameImpl createFaultElement1_1Name(String localName, String prefix) {
        return new FaultElement1_1Name(localName, prefix);
    }

    public static NameImpl createSOAP11Name(String string) {
        return new SOAP1_1Name(string, null);
    }

    public static NameImpl createSOAP12Name(String string) {
        return new SOAP1_2Name(string, null);
    }

    public static NameImpl createSOAP12Name(String localName, String prefix) {
        return new SOAP1_2Name(localName, prefix);
    }

    public static NameImpl createXmlName(String localName) {
        return new NameImpl(localName, XML_NAMESPACE_PREFIX, XML_NAMESPACE);
    }

    public static Name copyElementName(Element element) {
        String localName = element.getLocalName();
        String prefix = element.getPrefix();
        String uri = element.getNamespaceURI();
        return NameImpl.create(localName, prefix, uri);
    }

    static class CodeSubcode1_2Name
    extends SOAP1_2Name {
        CodeSubcode1_2Name(String prefix, String localName) {
            super(prefix, localName);
        }
    }

    static class SupportedEnvelope1_2Name
    extends NameImpl {
        SupportedEnvelope1_2Name(String prefix) {
            super("SupportedEnvelope", prefix == null || prefix.equals("") ? "env" : prefix, NameImpl.SOAP12_NAMESPACE);
        }
    }

    static class Upgrade1_2Name
    extends NameImpl {
        Upgrade1_2Name(String prefix) {
            super("Upgrade", prefix == null || prefix.equals("") ? "env" : prefix, NameImpl.SOAP12_NAMESPACE);
        }
    }

    static class NotUnderstood1_2Name
    extends NameImpl {
        NotUnderstood1_2Name(String prefix) {
            super("NotUnderstood", prefix == null || prefix.equals("") ? "env" : prefix, NameImpl.SOAP12_NAMESPACE);
        }
    }

    static class Fault1_2Name
    extends NameImpl {
        Fault1_2Name(String name, String prefix) {
            super(name == null || name.equals("") ? "Fault" : name, prefix == null || prefix.equals("") ? "env" : prefix, NameImpl.SOAP12_NAMESPACE);
        }
    }

    static class Body1_2Name
    extends SOAP1_2Name {
        Body1_2Name(String prefix) {
            super("Body", prefix);
        }
    }

    static class Header1_2Name
    extends SOAP1_2Name {
        Header1_2Name(String prefix) {
            super("Header", prefix);
        }
    }

    static class Envelope1_2Name
    extends SOAP1_2Name {
        Envelope1_2Name(String prefix) {
            super("Envelope", prefix);
        }
    }

    static class SOAP1_2Name
    extends NameImpl {
        SOAP1_2Name(String name, String prefix) {
            super(name, prefix == null || prefix.equals("") ? "env" : prefix, NameImpl.SOAP12_NAMESPACE);
        }
    }

    static class FaultElement1_1Name
    extends NameImpl {
        FaultElement1_1Name(String localName) {
            super(localName);
        }

        FaultElement1_1Name(String localName, String prefix) {
            super(localName, prefix, "");
        }
    }

    static class Detail1_1Name
    extends NameImpl {
        Detail1_1Name() {
            super("detail");
        }

        Detail1_1Name(String prefix) {
            super("detail", prefix, "");
        }
    }

    static class Fault1_1Name
    extends NameImpl {
        Fault1_1Name(String prefix) {
            super("Fault", prefix == null || prefix.equals("") ? NameImpl.SOAP_ENVELOPE_PREFIX : prefix, NameImpl.SOAP11_NAMESPACE);
        }
    }

    static class Body1_1Name
    extends SOAP1_1Name {
        Body1_1Name(String prefix) {
            super("Body", prefix);
        }
    }

    static class Header1_1Name
    extends SOAP1_1Name {
        Header1_1Name(String prefix) {
            super("Header", prefix);
        }
    }

    static class Envelope1_1Name
    extends SOAP1_1Name {
        Envelope1_1Name(String prefix) {
            super("Envelope", prefix);
        }
    }

    static class SOAP1_1Name
    extends NameImpl {
        SOAP1_1Name(String name, String prefix) {
            super(name, prefix == null || prefix.equals("") ? NameImpl.SOAP_ENVELOPE_PREFIX : prefix, NameImpl.SOAP11_NAMESPACE);
        }
    }
}

