/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.schema;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import org.apache.xmlbeans.SchemaAttributeGroup;
import org.apache.xmlbeans.SchemaField;
import org.apache.xmlbeans.SchemaGlobalAttribute;
import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaModelGroup;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlFactoryHook;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlSaxHandler;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.xpath.XPathFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Node;

public abstract class SchemaTypeLoaderBase
implements SchemaTypeLoader {
    private static final String USER_AGENT = "XMLBeans/" + XmlBeans.getVersion() + " (" + XmlBeans.getTitle() + ")";

    private static String doCompilePath(String pathExpr, XmlOptions options) {
        return XPathFactory.compilePath(pathExpr, options);
    }

    private static String doCompileQuery(String queryExpr, XmlOptions options) {
        return XPathFactory.compileQuery(queryExpr, options);
    }

    @Override
    public SchemaType findType(QName name) {
        SchemaType.Ref ref = this.findTypeRef(name);
        if (ref == null) {
            return null;
        }
        SchemaType result = ref.get();
        assert (result != null);
        return result;
    }

    @Override
    public SchemaType findDocumentType(QName name) {
        SchemaType.Ref ref = this.findDocumentTypeRef(name);
        if (ref == null) {
            return null;
        }
        SchemaType result = ref.get();
        assert (result != null);
        return result;
    }

    @Override
    public SchemaType findAttributeType(QName name) {
        SchemaType.Ref ref = this.findAttributeTypeRef(name);
        if (ref == null) {
            return null;
        }
        SchemaType result = ref.get();
        assert (result != null);
        return result;
    }

    @Override
    public SchemaModelGroup findModelGroup(QName name) {
        SchemaModelGroup.Ref ref = this.findModelGroupRef(name);
        if (ref == null) {
            return null;
        }
        SchemaModelGroup result = ref.get();
        assert (result != null);
        return result;
    }

    @Override
    public SchemaAttributeGroup findAttributeGroup(QName name) {
        SchemaAttributeGroup.Ref ref = this.findAttributeGroupRef(name);
        if (ref == null) {
            return null;
        }
        SchemaAttributeGroup result = ref.get();
        assert (result != null);
        return result;
    }

    @Override
    public SchemaGlobalElement findElement(QName name) {
        SchemaGlobalElement.Ref ref = this.findElementRef(name);
        if (ref == null) {
            return null;
        }
        SchemaGlobalElement result = ref.get();
        assert (result != null);
        return result;
    }

    @Override
    public SchemaGlobalAttribute findAttribute(QName name) {
        SchemaGlobalAttribute.Ref ref = this.findAttributeRef(name);
        if (ref == null) {
            return null;
        }
        SchemaGlobalAttribute result = ref.get();
        assert (result != null);
        return result;
    }

    @Override
    public XmlObject newInstance(SchemaType type, XmlOptions options) {
        XmlFactoryHook hook = XmlFactoryHook.ThreadContext.getHook();
        if (hook != null) {
            return hook.newInstance(this, type, options);
        }
        return Locale.newInstance(this, type, options);
    }

    @Override
    public XmlObject parse(String xmlText, SchemaType type, XmlOptions options) throws XmlException {
        XmlFactoryHook hook = XmlFactoryHook.ThreadContext.getHook();
        if (hook != null) {
            return hook.parse((SchemaTypeLoader)this, xmlText, type, options);
        }
        return Locale.parseToXmlObject((SchemaTypeLoader)this, xmlText, type, options);
    }

    @Override
    public XmlObject parse(XMLStreamReader xsr, SchemaType type, XmlOptions options) throws XmlException {
        XmlFactoryHook hook = XmlFactoryHook.ThreadContext.getHook();
        if (hook != null) {
            return hook.parse((SchemaTypeLoader)this, xsr, type, options);
        }
        return Locale.parseToXmlObject((SchemaTypeLoader)this, xsr, type, options);
    }

    @Override
    public XmlObject parse(File file, SchemaType type, XmlOptions options) throws XmlException, IOException {
        String fileName = file.toURI().normalize().toString();
        if (options == null) {
            options = new XmlOptions();
            options.setDocumentSourceName(fileName);
        } else if (options.getDocumentSourceName() == null) {
            options = new XmlOptions(options);
            options.setDocumentSourceName(fileName);
        }
        try (FileInputStream fis = new FileInputStream(file);){
            XmlObject xmlObject = this.parse(fis, type, options);
            return xmlObject;
        }
    }

    @Override
    public XmlObject parse(URL url, SchemaType type, XmlOptions options) throws XmlException, IOException {
        URLConnection conn;
        if (options == null) {
            options = new XmlOptions();
            options.setDocumentSourceName(url.toString());
        } else if (options.getDocumentSourceName() == null) {
            options = new XmlOptions(options);
            options.setDocumentSourceName(url.toString());
        }
        boolean redirected = false;
        int count = 0;
        do {
            conn = url.openConnection();
            conn.addRequestProperty("User-Agent", USER_AGENT);
            conn.addRequestProperty("Accept", "application/xml, text/xml, */*");
            if (!(conn instanceof HttpURLConnection)) continue;
            HttpURLConnection httpcon = (HttpURLConnection)conn;
            int code = httpcon.getResponseCode();
            boolean bl = redirected = code == 301 || code == 302;
            if (redirected && count > 5) {
                redirected = false;
            }
            if (!redirected) continue;
            String newLocation = httpcon.getHeaderField("Location");
            if (newLocation == null) {
                redirected = false;
                continue;
            }
            url = new URL(newLocation);
            ++count;
        } while (redirected);
        try (InputStream stream = conn.getInputStream();){
            XmlObject xmlObject = this.parse(stream, type, options);
            return xmlObject;
        }
    }

    @Override
    public XmlObject parse(InputStream jiois, SchemaType type, XmlOptions options) throws XmlException, IOException {
        DigestInputStream digestStream;
        XmlFactoryHook hook;
        block5: {
            hook = XmlFactoryHook.ThreadContext.getHook();
            digestStream = null;
            if (options != null && options.isLoadMessageDigest()) {
                MessageDigest sha;
                try {
                    sha = MessageDigest.getInstance("SHA");
                }
                catch (NoSuchAlgorithmException e) {
                    break block5;
                }
                digestStream = new DigestInputStream(jiois, sha);
                jiois = digestStream;
            }
        }
        if (hook != null) {
            return hook.parse((SchemaTypeLoader)this, jiois, type, options);
        }
        XmlObject result = Locale.parseToXmlObject((SchemaTypeLoader)this, jiois, type, options);
        if (digestStream != null) {
            result.documentProperties().setMessageDigest(digestStream.getMessageDigest().digest());
        }
        return result;
    }

    @Override
    public XmlObject parse(Reader jior, SchemaType type, XmlOptions options) throws XmlException, IOException {
        XmlFactoryHook hook = XmlFactoryHook.ThreadContext.getHook();
        if (hook != null) {
            return hook.parse((SchemaTypeLoader)this, jior, type, options);
        }
        return Locale.parseToXmlObject((SchemaTypeLoader)this, jior, type, options);
    }

    @Override
    public XmlObject parse(Node node, SchemaType type, XmlOptions options) throws XmlException {
        XmlFactoryHook hook = XmlFactoryHook.ThreadContext.getHook();
        if (hook != null) {
            return hook.parse((SchemaTypeLoader)this, node, type, options);
        }
        return Locale.parseToXmlObject((SchemaTypeLoader)this, node, type, options);
    }

    @Override
    public XmlSaxHandler newXmlSaxHandler(SchemaType type, XmlOptions options) {
        XmlFactoryHook hook = XmlFactoryHook.ThreadContext.getHook();
        if (hook != null) {
            return hook.newXmlSaxHandler(this, type, options);
        }
        return Locale.newSaxHandler(this, type, options);
    }

    @Override
    public DOMImplementation newDomImplementation(XmlOptions options) {
        return Locale.newDomImplementation(this, options);
    }

    public String compilePath(String pathExpr) {
        return this.compilePath(pathExpr, null);
    }

    @Override
    public String compilePath(String pathExpr, XmlOptions options) {
        return SchemaTypeLoaderBase.doCompilePath(pathExpr, options);
    }

    public String compileQuery(String queryExpr) {
        return this.compileQuery(queryExpr, null);
    }

    @Override
    public String compileQuery(String queryExpr, XmlOptions options) {
        return SchemaTypeLoaderBase.doCompileQuery(queryExpr, options);
    }

    @Override
    public SchemaType typeForSignature(String signature) {
        String uri;
        int end = signature.indexOf(64);
        if (end < 0) {
            uri = "";
            end = signature.length();
        } else {
            uri = signature.substring(end + 1);
        }
        ArrayList<String> parts = new ArrayList<String>();
        int index = 0;
        while (index < end) {
            int next;
            int nextc = signature.indexOf(58, index);
            int nextd = signature.indexOf(124, index);
            int n = nextc < 0 ? nextd : (next = nextd < 0 ? nextc : Math.min(nextc, nextd));
            if (next < 0 || next > end) {
                next = end;
            }
            String part = signature.substring(index, next);
            parts.add(part);
            index = next + 1;
        }
        SchemaType curType = null;
        block13: for (int i = parts.size() - 1; i >= 0; --i) {
            String part = (String)parts.get(i);
            if (part.length() < 1) {
                throw new IllegalArgumentException();
            }
            int offset = part.length() >= 2 && part.charAt(1) == '=' ? 2 : 1;
            switch (part.charAt(0)) {
                case 'T': {
                    if (curType != null) {
                        throw new IllegalArgumentException();
                    }
                    curType = this.findType(QNameHelper.forLNS(part.substring(offset), uri));
                    if (curType != null) continue block13;
                    return null;
                }
                case 'D': {
                    if (curType != null) {
                        throw new IllegalArgumentException();
                    }
                    curType = this.findDocumentType(QNameHelper.forLNS(part.substring(offset), uri));
                    if (curType != null) continue block13;
                    return null;
                }
                case 'C': 
                case 'R': {
                    if (curType != null) {
                        throw new IllegalArgumentException();
                    }
                    curType = this.findAttributeType(QNameHelper.forLNS(part.substring(offset), uri));
                    if (curType != null) continue block13;
                    return null;
                }
                case 'E': 
                case 'U': {
                    SchemaField field;
                    String localName;
                    if (curType != null) {
                        if (curType.getContentType() < 3) {
                            return null;
                        }
                        SchemaType[] subTypes = curType.getAnonymousTypes();
                        localName = part.substring(offset);
                        for (SchemaType subType : subTypes) {
                            field = subType.getContainerField();
                            if (field == null || field.isAttribute() || !field.getName().getLocalPart().equals(localName)) continue;
                            curType = subType;
                            continue block13;
                        }
                        return null;
                    }
                    SchemaGlobalElement elt = this.findElement(QNameHelper.forLNS(part.substring(offset), uri));
                    if (elt == null) {
                        return null;
                    }
                    curType = elt.getType();
                    continue block13;
                }
                case 'A': 
                case 'Q': {
                    SchemaField field;
                    String localName;
                    if (curType != null) {
                        if (curType.isSimpleType()) {
                            return null;
                        }
                        SchemaType[] subTypes = curType.getAnonymousTypes();
                        localName = part.substring(offset);
                        for (SchemaType subType : subTypes) {
                            field = subType.getContainerField();
                            if (field == null || !field.isAttribute() || !field.getName().getLocalPart().equals(localName)) continue;
                            curType = subType;
                            continue block13;
                        }
                        return null;
                    }
                    SchemaGlobalAttribute attr = this.findAttribute(QNameHelper.forLNS(part.substring(offset), uri));
                    if (attr == null) {
                        return null;
                    }
                    curType = attr.getType();
                    continue block13;
                }
                case 'B': {
                    if (curType == null) {
                        throw new IllegalArgumentException();
                    }
                    if (curType.getSimpleVariety() != 1) {
                        return null;
                    }
                    SchemaType[] subTypes = curType.getAnonymousTypes();
                    if (subTypes.length != 1) {
                        return null;
                    }
                    curType = subTypes[0];
                    continue block13;
                }
                case 'I': {
                    if (curType == null) {
                        throw new IllegalArgumentException();
                    }
                    if (curType.getSimpleVariety() != 3) {
                        return null;
                    }
                    SchemaType[] subTypes = curType.getAnonymousTypes();
                    if (subTypes.length != 1) {
                        return null;
                    }
                    curType = subTypes[0];
                    continue block13;
                }
                case 'M': {
                    int index2;
                    if (curType == null) {
                        throw new IllegalArgumentException();
                    }
                    try {
                        index2 = Integer.parseInt(part.substring(offset));
                    }
                    catch (Exception e) {
                        throw new IllegalArgumentException();
                    }
                    if (curType.getSimpleVariety() != 2) {
                        return null;
                    }
                    SchemaType[] subTypes = curType.getAnonymousTypes();
                    if (subTypes.length <= index2) {
                        return null;
                    }
                    curType = subTypes[index2];
                    continue block13;
                }
                default: {
                    throw new IllegalArgumentException();
                }
            }
        }
        return curType;
    }
}

