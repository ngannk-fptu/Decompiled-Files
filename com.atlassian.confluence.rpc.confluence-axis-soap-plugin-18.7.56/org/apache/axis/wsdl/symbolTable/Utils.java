/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.symbolTable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.xml.namespace.QName;
import javax.xml.rpc.holders.BooleanHolder;
import org.apache.axis.Constants;
import org.apache.axis.utils.XMLUtils;
import org.apache.axis.wsdl.symbolTable.ContainedAttribute;
import org.apache.axis.wsdl.symbolTable.DefinedType;
import org.apache.axis.wsdl.symbolTable.ElementDecl;
import org.apache.axis.wsdl.symbolTable.SchemaUtils;
import org.apache.axis.wsdl.symbolTable.SymTabEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.Type;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class Utils {
    static final Map nsmap = new HashMap();

    static QName findQName(String namespace, String localName) {
        QName qname = null;
        HashMap<String, QName> ln2qn = (HashMap<String, QName>)nsmap.get(namespace);
        if (null == ln2qn) {
            ln2qn = new HashMap<String, QName>();
            nsmap.put(namespace, ln2qn);
            qname = new QName(namespace, localName);
            ln2qn.put(localName, qname);
        } else {
            qname = (QName)ln2qn.get(localName);
            if (null == qname) {
                qname = new QName(namespace, localName);
                ln2qn.put(localName, qname);
            }
        }
        return qname;
    }

    public static String getScopedAttribute(Node node, String attr) {
        if (node == null) {
            return null;
        }
        if (node.getAttributes() == null) {
            return Utils.getScopedAttribute(node.getParentNode(), attr);
        }
        Node attrNode = node.getAttributes().getNamedItem(attr);
        if (attrNode != null) {
            return attrNode.getNodeValue();
        }
        return Utils.getScopedAttribute(node.getParentNode(), attr);
    }

    public static String getAttribute(Node node, String attr) {
        if (node == null || node.getAttributes() == null) {
            return null;
        }
        Node attrNode = node.getAttributes().getNamedItem(attr);
        if (attrNode != null) {
            return attrNode.getNodeValue();
        }
        return null;
    }

    public static Vector getAttributesWithLocalName(Node node, String localName) {
        Vector<Node> v = new Vector<Node>();
        if (node == null) {
            return v;
        }
        NamedNodeMap map = node.getAttributes();
        if (map != null) {
            for (int i = 0; i < map.getLength(); ++i) {
                Node attrNode = map.item(i);
                if (attrNode == null || !attrNode.getLocalName().equals(localName)) continue;
                v.add(attrNode);
            }
        }
        return v;
    }

    public static QName getNodeQName(Node node) {
        if (node == null) {
            return null;
        }
        String localName = node.getLocalName();
        if (localName == null) {
            return null;
        }
        String namespace = node.getNamespaceURI();
        return Utils.findQName(namespace, localName);
    }

    public static QName getNodeNameQName(Node node) {
        QName ref;
        if (node == null) {
            return null;
        }
        String localName = null;
        String namespace = null;
        localName = Utils.getAttribute(node, "name");
        if (localName == null && (ref = Utils.getTypeQNameFromAttr(node, "ref")) != null) {
            localName = ref.getLocalPart();
            namespace = ref.getNamespaceURI();
        }
        Node search = node.getParentNode();
        while (search != null) {
            String ln = search.getLocalName();
            if (ln.equals("schema")) {
                search = null;
                continue;
            }
            if (ln.equals("element") || ln.equals("attribute")) {
                localName = ">" + Utils.getNodeNameQName(search).getLocalPart();
                search = null;
                continue;
            }
            if (ln.equals("complexType") || ln.equals("simpleType")) {
                localName = Utils.getNodeNameQName(search).getLocalPart() + ">" + localName;
                search = null;
                continue;
            }
            search = search.getParentNode();
        }
        if (localName == null) {
            return null;
        }
        if (namespace == null) {
            namespace = Utils.getScopedAttribute(node, "targetNamespace");
        }
        return Utils.findQName(namespace, localName);
    }

    public static QName getTypeQName(Node node, BooleanHolder forElement, boolean ignoreMaxOccurs) {
        if (node == null) {
            return null;
        }
        forElement.value = false;
        QName qName = Utils.getTypeQNameFromAttr(node, "type");
        if (qName == null) {
            String localName = node.getLocalName();
            if (!(localName == null || localName.equals("attributeGroup") || localName.equals("group") || localName.equals("list"))) {
                forElement.value = true;
            }
            qName = Utils.getTypeQNameFromAttr(node, "ref");
        }
        if (qName == null) {
            qName = Utils.getTypeQNameFromAttr(node, "itemType");
        }
        if (!ignoreMaxOccurs && qName != null) {
            String maxOccursValue = Utils.getAttribute(node, "maxOccurs");
            String minOccursValue = Utils.getAttribute(node, "minOccurs");
            if (maxOccursValue == null) {
                maxOccursValue = "1";
            }
            if (minOccursValue == null) {
                minOccursValue = "1";
            }
            if (!(minOccursValue.equals("0") && maxOccursValue.equals("1") || maxOccursValue.equals("1") && minOccursValue.equals("1"))) {
                String localPart = qName.getLocalPart();
                String range = "[";
                if (!minOccursValue.equals("1")) {
                    range = range + minOccursValue;
                }
                range = range + ",";
                if (!maxOccursValue.equals("1")) {
                    range = range + maxOccursValue;
                }
                range = range + "]";
                localPart = localPart + range;
                qName = Utils.findQName(qName.getNamespaceURI(), localPart);
            }
        }
        if (qName == null) {
            forElement.value = true;
            qName = Utils.getTypeQNameFromAttr(node, "element");
        }
        if (qName == null) {
            forElement.value = false;
            qName = Utils.getTypeQNameFromAttr(node, "base");
        }
        return qName;
    }

    public static QName[] getMemberTypeQNames(Node node) {
        String attribute = Utils.getAttribute(node, "memberTypes");
        if (attribute == null) {
            return null;
        }
        StringTokenizer tokenizer = new StringTokenizer(attribute, " ");
        QName[] memberTypes = new QName[tokenizer.countTokens()];
        int i = 0;
        while (tokenizer.hasMoreElements()) {
            String element = (String)tokenizer.nextElement();
            memberTypes[i] = XMLUtils.getFullQNameFromString(element, node);
            ++i;
        }
        return memberTypes;
    }

    private static QName getTypeQNameFromAttr(Node node, String typeAttrName) {
        if (node == null) {
            return null;
        }
        String prefixedName = Utils.getAttribute(node, typeAttrName);
        if (prefixedName == null && typeAttrName.equals("type") && Utils.getAttribute(node, "ref") == null && Utils.getAttribute(node, "base") == null && Utils.getAttribute(node, "element") == null) {
            QName anonQName = SchemaUtils.getElementAnonQName(node);
            if (anonQName == null) {
                anonQName = SchemaUtils.getAttributeAnonQName(node);
            }
            if (anonQName != null) {
                return anonQName;
            }
            String localName = node.getLocalName();
            if (localName != null && Constants.isSchemaXSD(node.getNamespaceURI()) && (localName.equals("element") || localName.equals("attribute"))) {
                return Constants.XSD_ANYTYPE;
            }
        }
        if (prefixedName == null) {
            return null;
        }
        QName qName = Utils.getQNameFromPrefixedName(node, prefixedName);
        return qName;
    }

    public static QName getQNameFromPrefixedName(Node node, String prefixedName) {
        String localName = prefixedName.substring(prefixedName.lastIndexOf(":") + 1);
        String namespace = null;
        namespace = prefixedName.length() == localName.length() ? Utils.getScopedAttribute(node, "xmlns") : Utils.getScopedAttribute(node, "xmlns:" + prefixedName.substring(0, prefixedName.lastIndexOf(":")));
        return Utils.findQName(namespace, localName);
    }

    public static HashSet getDerivedTypes(TypeEntry type, SymbolTable symbolTable) {
        HashSet<SymTabEntry> types = (HashSet<SymTabEntry>)symbolTable.derivedTypes.get(type);
        if (types != null) {
            return types;
        }
        types = new HashSet<SymTabEntry>();
        symbolTable.derivedTypes.put(type, types);
        if (type != null && type.getNode() != null) {
            Utils.getDerivedTypes(type, types, symbolTable);
        } else if (type != null && Constants.isSchemaXSD(type.getQName().getNamespaceURI()) && (type.getQName().getLocalPart().equals("anyType") || type.getQName().getLocalPart().equals("any"))) {
            Collection typeValues = symbolTable.getTypeIndex().values();
            Iterator it = typeValues.iterator();
            while (it.hasNext()) {
                SymTabEntry e = (SymTabEntry)it.next();
                if (e.getQName().getLocalPart().startsWith(">")) continue;
                types.add(e);
            }
        }
        return types;
    }

    private static void getDerivedTypes(TypeEntry type, HashSet types, SymbolTable symbolTable) {
        if (types.size() == symbolTable.getTypeEntryCount()) {
            return;
        }
        Iterator it = symbolTable.getTypeIndex().values().iterator();
        while (it.hasNext()) {
            Type t = (Type)it.next();
            if (!(t instanceof DefinedType) || t.getNode() == null || types.contains(t) || ((DefinedType)t).getComplexTypeExtensionBase(symbolTable) != type) continue;
            types.add(t);
            Utils.getDerivedTypes(t, types, symbolTable);
        }
    }

    protected static HashSet getNestedTypes(TypeEntry type, SymbolTable symbolTable, boolean derivedFlag) {
        HashSet types = new HashSet();
        Utils.getNestedTypes(type, types, symbolTable, derivedFlag);
        return types;
    }

    private static void getNestedTypes(TypeEntry type, HashSet types, SymbolTable symbolTable, boolean derivedFlag) {
        TypeEntry extendType;
        if (type == null) {
            return;
        }
        if (types.size() == symbolTable.getTypeEntryCount()) {
            return;
        }
        if (derivedFlag) {
            HashSet derivedTypes = Utils.getDerivedTypes(type, symbolTable);
            Iterator it = derivedTypes.iterator();
            while (it.hasNext()) {
                TypeEntry derivedType = (TypeEntry)it.next();
                if (types.contains(derivedType)) continue;
                types.add(derivedType);
                Utils.getNestedTypes(derivedType, types, symbolTable, derivedFlag);
            }
        }
        if (type.getNode() == null) {
            return;
        }
        Node node = type.getNode();
        Vector v = SchemaUtils.getContainedElementDeclarations(node, symbolTable);
        if (v != null) {
            for (int i = 0; i < v.size(); ++i) {
                ElementDecl elem = (ElementDecl)v.get(i);
                if (types.contains(elem.getType())) continue;
                types.add(elem.getType());
                Utils.getNestedTypes(elem.getType(), types, symbolTable, derivedFlag);
            }
        }
        if ((v = SchemaUtils.getContainedAttributeTypes(node, symbolTable)) != null) {
            for (int i = 0; i < v.size(); ++i) {
                ContainedAttribute attr = (ContainedAttribute)v.get(i);
                TypeEntry te = attr.getType();
                if (types.contains(te)) continue;
                types.add(te);
                Utils.getNestedTypes(te, types, symbolTable, derivedFlag);
            }
        }
        if (type.getRefType() != null && !types.contains(type.getRefType())) {
            types.add(type.getRefType());
            Utils.getNestedTypes(type.getRefType(), types, symbolTable, derivedFlag);
        }
        if ((extendType = SchemaUtils.getComplexElementExtensionBase(node, symbolTable)) != null && !types.contains(extendType)) {
            types.add(extendType);
            Utils.getNestedTypes(extendType, types, symbolTable, derivedFlag);
        }
    }

    public static String genQNameAttributeString(QName qname, String prefix) {
        if (qname.getNamespaceURI() == null || qname.getNamespaceURI().equals("")) {
            return qname.getLocalPart();
        }
        return prefix + ":" + qname.getLocalPart() + "\" xmlns:" + prefix + "=\"" + qname.getNamespaceURI();
    }

    public static String genQNameAttributeStringWithLastLocalPart(QName qname, String prefix) {
        String lastLocalPart = Utils.getLastLocalPart(qname.getLocalPart());
        if (qname.getNamespaceURI() == null || qname.getNamespaceURI().equals("")) {
            return lastLocalPart;
        }
        return prefix + ":" + lastLocalPart + "\" xmlns:" + prefix + "=\"" + qname.getNamespaceURI();
    }

    public static String getLastLocalPart(String localPart) {
        int anonymousDelimitorIndex = localPart.lastIndexOf(62);
        if (anonymousDelimitorIndex > -1 && anonymousDelimitorIndex < localPart.length() - 1) {
            localPart = localPart.substring(anonymousDelimitorIndex + 1);
        }
        return localPart;
    }
}

