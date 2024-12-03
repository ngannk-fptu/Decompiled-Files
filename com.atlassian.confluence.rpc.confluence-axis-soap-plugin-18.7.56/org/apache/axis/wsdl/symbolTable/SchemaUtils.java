/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.symbolTable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.xml.namespace.QName;
import javax.xml.rpc.holders.BooleanHolder;
import javax.xml.rpc.holders.IntHolder;
import javax.xml.rpc.holders.QNameHolder;
import org.apache.axis.Constants;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.wsdl.symbolTable.ContainedAttribute;
import org.apache.axis.wsdl.symbolTable.Element;
import org.apache.axis.wsdl.symbolTable.ElementDecl;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.Type;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.apache.axis.wsdl.symbolTable.Utils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SchemaUtils {
    static final QName VALUE_QNAME = Utils.findQName("", "_value");
    private static String[] schemaTypes = new String[]{"string", "normalizedString", "token", "byte", "unsignedByte", "base64Binary", "hexBinary", "integer", "positiveInteger", "negativeInteger", "nonNegativeInteger", "nonPositiveInteger", "int", "unsignedInt", "long", "unsignedLong", "short", "unsignedShort", "decimal", "float", "double", "boolean", "time", "dateTime", "duration", "date", "gMonth", "gYear", "gYearMonth", "gDay", "gMonthDay", "Name", "QName", "NCName", "anyURI", "language", "ID", "IDREF", "IDREFS", "ENTITY", "ENTITIES", "NOTATION", "NMTOKEN", "NMTOKENS", "anySimpleType"};
    private static final Set schemaTypeSet = new HashSet<String>(Arrays.asList(schemaTypes));

    public static boolean isMixed(Node node) {
        if (SchemaUtils.isXSDNode(node, "complexType")) {
            String mixed = ((org.w3c.dom.Element)node).getAttribute("mixed");
            if (mixed != null && mixed.length() > 0) {
                return "true".equalsIgnoreCase(mixed) || "1".equals(mixed);
            }
            NodeList children = node.getChildNodes();
            for (int j = 0; j < children.getLength(); ++j) {
                Node kid = children.item(j);
                if (!SchemaUtils.isXSDNode(kid, "complexContent")) continue;
                mixed = ((org.w3c.dom.Element)kid).getAttribute("mixed");
                return "true".equalsIgnoreCase(mixed) || "1".equals(mixed);
            }
        }
        return false;
    }

    public static Node getUnionNode(Node node) {
        if (SchemaUtils.isXSDNode(node, "simpleType")) {
            NodeList children = node.getChildNodes();
            for (int j = 0; j < children.getLength(); ++j) {
                Node kid = children.item(j);
                if (!SchemaUtils.isXSDNode(kid, "union")) continue;
                return kid;
            }
        }
        return null;
    }

    public static Node getListNode(Node node) {
        if (SchemaUtils.isXSDNode(node, "simpleType")) {
            NodeList children = node.getChildNodes();
            for (int j = 0; j < children.getLength(); ++j) {
                Node kid = children.item(j);
                if (!SchemaUtils.isXSDNode(kid, "list")) continue;
                return kid;
            }
        }
        return null;
    }

    public static boolean isSimpleTypeWithUnion(Node node) {
        return SchemaUtils.getUnionNode(node) != null;
    }

    public static boolean isWrappedType(Node node) {
        Node kid;
        int j;
        NodeList children;
        if (node == null) {
            return false;
        }
        if (SchemaUtils.isXSDNode(node, "element")) {
            children = node.getChildNodes();
            boolean hasComplexType = false;
            for (j = 0; j < children.getLength(); ++j) {
                kid = children.item(j);
                if (!SchemaUtils.isXSDNode(kid, "complexType")) continue;
                node = kid;
                hasComplexType = true;
                break;
            }
            if (!hasComplexType) {
                return false;
            }
        }
        if (SchemaUtils.isXSDNode(node, "complexType")) {
            children = node.getChildNodes();
            for (int j2 = 0; j2 < children.getLength(); ++j2) {
                Node kid2 = children.item(j2);
                if (SchemaUtils.isXSDNode(kid2, "complexContent")) {
                    return false;
                }
                if (!SchemaUtils.isXSDNode(kid2, "simpleContent")) continue;
                return false;
            }
            children = node.getChildNodes();
            int len = children.getLength();
            for (j = 0; j < len; ++j) {
                kid = children.item(j);
                String localName = kid.getLocalName();
                if (localName == null || !Constants.isSchemaXSD(kid.getNamespaceURI())) continue;
                if (localName.equals("sequence")) {
                    Node sequenceNode = kid;
                    NodeList sequenceChildren = sequenceNode.getChildNodes();
                    int sequenceLen = sequenceChildren.getLength();
                    for (int k = 0; k < sequenceLen; ++k) {
                        Node sequenceKid = sequenceChildren.item(k);
                        String sequenceLocalName = sequenceKid.getLocalName();
                        if (sequenceLocalName == null || !Constants.isSchemaXSD(sequenceKid.getNamespaceURI())) continue;
                        if (sequenceLocalName.equals("choice")) {
                            Node choiceNode = sequenceKid;
                            NodeList choiceChildren = choiceNode.getChildNodes();
                            int choiceLen = choiceChildren.getLength();
                            for (int l = 0; l < choiceLen; ++l) {
                                Node choiceKid = choiceChildren.item(l);
                                String choiceLocalName = choiceKid.getLocalName();
                                if (choiceLocalName == null || !Constants.isSchemaXSD(choiceKid.getNamespaceURI()) || choiceLocalName.equals("element")) continue;
                                return false;
                            }
                            continue;
                        }
                        if (sequenceLocalName.equals("element")) continue;
                        return false;
                    }
                    return true;
                }
                return false;
            }
        }
        return true;
    }

    public static Vector getContainedElementDeclarations(Node node, SymbolTable symbolTable) {
        NodeList children;
        if (node == null) {
            return null;
        }
        if (SchemaUtils.isXSDNode(node, "element")) {
            children = node.getChildNodes();
            for (int j = 0; j < children.getLength(); ++j) {
                Node kid = children.item(j);
                if (!SchemaUtils.isXSDNode(kid, "complexType")) continue;
                node = kid;
                break;
            }
        }
        if (SchemaUtils.isXSDNode(node, "complexType")) {
            Node kid;
            int j;
            children = node.getChildNodes();
            Node complexContent = null;
            Node simpleContent = null;
            Node extension = null;
            for (j = 0; j < children.getLength(); ++j) {
                kid = children.item(j);
                if (SchemaUtils.isXSDNode(kid, "complexContent")) {
                    complexContent = kid;
                    break;
                }
                if (!SchemaUtils.isXSDNode(kid, "simpleContent")) continue;
                simpleContent = kid;
            }
            if (complexContent != null) {
                children = complexContent.getChildNodes();
                for (j = 0; j < children.getLength() && extension == null; ++j) {
                    kid = children.item(j);
                    if (!SchemaUtils.isXSDNode(kid, "extension") && !SchemaUtils.isXSDNode(kid, "restriction")) continue;
                    extension = kid;
                }
            }
            if (simpleContent != null) {
                children = simpleContent.getChildNodes();
                int len = children.getLength();
                for (int j2 = 0; j2 < len && extension == null; ++j2) {
                    Node kid2 = children.item(j2);
                    String localName = kid2.getLocalName();
                    if (localName == null || !localName.equals("extension") && !localName.equals("restriction") || !Constants.isSchemaXSD(kid2.getNamespaceURI())) continue;
                    QName extendsOrRestrictsType = Utils.getTypeQName(children.item(j2), new BooleanHolder(), false);
                    Vector<ElementDecl> v = new Vector<ElementDecl>();
                    ElementDecl elem = new ElementDecl(symbolTable.getTypeEntry(extendsOrRestrictsType, false), VALUE_QNAME);
                    v.add(elem);
                    return v;
                }
            }
            if (extension != null) {
                node = extension;
            }
            children = node.getChildNodes();
            Vector v = new Vector();
            int len = children.getLength();
            for (int j3 = 0; j3 < len; ++j3) {
                Node kid3 = children.item(j3);
                String localName = kid3.getLocalName();
                if (localName == null || !Constants.isSchemaXSD(kid3.getNamespaceURI())) continue;
                if (localName.equals("sequence")) {
                    v.addAll(SchemaUtils.processSequenceNode(kid3, symbolTable));
                    continue;
                }
                if (localName.equals("all")) {
                    v.addAll(SchemaUtils.processAllNode(kid3, symbolTable));
                    continue;
                }
                if (localName.equals("choice")) {
                    v.addAll(SchemaUtils.processChoiceNode(kid3, symbolTable));
                    continue;
                }
                if (!localName.equals("group")) continue;
                v.addAll(SchemaUtils.processGroupNode(kid3, symbolTable));
            }
            return v;
        }
        if (SchemaUtils.isXSDNode(node, "group")) {
            return null;
        }
        QName[] simpleQName = SchemaUtils.getContainedSimpleTypes(node);
        if (simpleQName != null) {
            Vector<ElementDecl> v = null;
            for (int i = 0; i < simpleQName.length; ++i) {
                Type simpleType = symbolTable.getType(simpleQName[i]);
                if (simpleType == null) continue;
                if (v == null) {
                    v = new Vector<ElementDecl>();
                }
                QName qname = null;
                qname = simpleQName.length > 1 ? new QName("", simpleQName[i].getLocalPart() + "Value") : new QName("", "value");
                v.add(new ElementDecl(simpleType, qname));
            }
            return v;
        }
        return null;
    }

    private static Vector processChoiceNode(Node choiceNode, SymbolTable symbolTable) {
        Vector<ElementDecl> v = new Vector<ElementDecl>();
        NodeList children = choiceNode.getChildNodes();
        int len = children.getLength();
        for (int j = 0; j < len; ++j) {
            Node kid = children.item(j);
            String localName = kid.getLocalName();
            if (localName == null || !Constants.isSchemaXSD(kid.getNamespaceURI())) continue;
            if (localName.equals("choice")) {
                v.addAll(SchemaUtils.processChoiceNode(kid, symbolTable));
                continue;
            }
            if (localName.equals("sequence")) {
                v.addAll(SchemaUtils.processSequenceNode(kid, symbolTable));
                continue;
            }
            if (localName.equals("group")) {
                v.addAll(SchemaUtils.processGroupNode(kid, symbolTable));
                continue;
            }
            if (localName.equals("element")) {
                ElementDecl elem = SchemaUtils.processChildElementNode(kid, symbolTable);
                if (elem == null) continue;
                elem.setMinOccursIs0(true);
                v.add(elem);
                continue;
            }
            if (!localName.equals("any")) continue;
            Type type = symbolTable.getType(Constants.XSD_ANY);
            ElementDecl elem = new ElementDecl(type, Utils.findQName("", "any"));
            elem.setAnyElement(true);
            v.add(elem);
        }
        return v;
    }

    private static Node getChildByName(Node parentNode, String name) throws DOMException {
        if (parentNode == null) {
            return null;
        }
        NodeList children = parentNode.getChildNodes();
        if (children != null) {
            for (int i = 0; i < children.getLength(); ++i) {
                Node child = children.item(i);
                if (child == null || child.getNodeName() == null || !name.equals(child.getNodeName())) continue;
                return child;
            }
        }
        return null;
    }

    public static String getTextByPath(Node root, String path) throws DOMException {
        StringTokenizer st = new StringTokenizer(path, "/");
        Node node = root;
        while (st.hasMoreTokens()) {
            String elementName = st.nextToken();
            Node child = SchemaUtils.getChildByName(node, elementName);
            if (child == null) {
                throw new DOMException(8, "could not find " + elementName);
            }
            node = child;
        }
        String text = "";
        NodeList children = node.getChildNodes();
        if (children != null) {
            for (int i = 0; i < children.getLength(); ++i) {
                Node child = children.item(i);
                if (child == null || child.getNodeName() == null || !child.getNodeName().equals("#text") && !child.getNodeName().equals("#cdata-section")) continue;
                text = text + child.getNodeValue();
            }
        }
        return text;
    }

    public static String getAnnotationDocumentation(Node typeNode) {
        NodeList children;
        Node documentationNode;
        Node annotationNode;
        for (annotationNode = typeNode.getFirstChild(); annotationNode != null && !SchemaUtils.isXSDNode(annotationNode, "annotation"); annotationNode = annotationNode.getNextSibling()) {
        }
        if (annotationNode != null) {
            for (documentationNode = annotationNode.getFirstChild(); documentationNode != null && !SchemaUtils.isXSDNode(documentationNode, "documentation"); documentationNode = documentationNode.getNextSibling()) {
            }
        } else {
            documentationNode = null;
        }
        String text = "";
        if (documentationNode != null && (children = documentationNode.getChildNodes()) != null) {
            for (int i = 0; i < children.getLength(); ++i) {
                Node child = children.item(i);
                if (child == null || child.getNodeName() == null || !child.getNodeName().equals("#text") && !child.getNodeName().equals("#cdata-section")) continue;
                text = text + child.getNodeValue();
            }
        }
        return text;
    }

    private static Vector processSequenceNode(Node sequenceNode, SymbolTable symbolTable) {
        Vector<ElementDecl> v = new Vector<ElementDecl>();
        NodeList children = sequenceNode.getChildNodes();
        int len = children.getLength();
        for (int j = 0; j < len; ++j) {
            ElementDecl elem;
            Node kid = children.item(j);
            String localName = kid.getLocalName();
            if (localName == null || !Constants.isSchemaXSD(kid.getNamespaceURI())) continue;
            if (localName.equals("choice")) {
                v.addAll(SchemaUtils.processChoiceNode(kid, symbolTable));
                continue;
            }
            if (localName.equals("sequence")) {
                v.addAll(SchemaUtils.processSequenceNode(kid, symbolTable));
                continue;
            }
            if (localName.equals("group")) {
                v.addAll(SchemaUtils.processGroupNode(kid, symbolTable));
                continue;
            }
            if (localName.equals("any")) {
                Type type = symbolTable.getType(Constants.XSD_ANY);
                ElementDecl elem2 = new ElementDecl(type, Utils.findQName("", "any"));
                elem2.setAnyElement(true);
                v.add(elem2);
                continue;
            }
            if (!localName.equals("element") || (elem = SchemaUtils.processChildElementNode(kid, symbolTable)) == null) continue;
            v.add(elem);
        }
        return v;
    }

    private static Vector processGroupNode(Node groupNode, SymbolTable symbolTable) {
        Vector v;
        block7: {
            block6: {
                v = new Vector();
                if (groupNode.getAttributes().getNamedItem("ref") != null) break block6;
                NodeList children = groupNode.getChildNodes();
                int len = children.getLength();
                for (int j = 0; j < len; ++j) {
                    Node kid = children.item(j);
                    String localName = kid.getLocalName();
                    if (localName == null || !Constants.isSchemaXSD(kid.getNamespaceURI())) continue;
                    if (localName.equals("choice")) {
                        v.addAll(SchemaUtils.processChoiceNode(kid, symbolTable));
                        continue;
                    }
                    if (localName.equals("sequence")) {
                        v.addAll(SchemaUtils.processSequenceNode(kid, symbolTable));
                        continue;
                    }
                    if (!localName.equals("all")) continue;
                    v.addAll(SchemaUtils.processAllNode(kid, symbolTable));
                }
                break block7;
            }
            QName nodeName = Utils.getNodeNameQName(groupNode);
            QName nodeType = Utils.getTypeQName(groupNode, new BooleanHolder(), false);
            Type type = (Type)symbolTable.getTypeEntry(nodeType, false);
            if (type == null || type.getNode() == null) break block7;
            Node node = type.getNode();
            NodeList children = node.getChildNodes();
            for (int j = 0; j < children.getLength(); ++j) {
                QName subNodeKind = Utils.getNodeQName(children.item(j));
                if (subNodeKind == null || !Constants.isSchemaXSD(subNodeKind.getNamespaceURI())) continue;
                if (subNodeKind.getLocalPart().equals("sequence")) {
                    v.addAll(SchemaUtils.processSequenceNode(children.item(j), symbolTable));
                    continue;
                }
                if (subNodeKind.getLocalPart().equals("all")) {
                    v.addAll(SchemaUtils.processAllNode(children.item(j), symbolTable));
                    continue;
                }
                if (!subNodeKind.getLocalPart().equals("choice")) continue;
                v.addAll(SchemaUtils.processChoiceNode(children.item(j), symbolTable));
            }
        }
        return v;
    }

    private static Vector processAllNode(Node allNode, SymbolTable symbolTable) {
        Vector<ElementDecl> v = new Vector<ElementDecl>();
        NodeList children = allNode.getChildNodes();
        for (int j = 0; j < children.getLength(); ++j) {
            ElementDecl elem;
            Node kid = children.item(j);
            if (!SchemaUtils.isXSDNode(kid, "element") || (elem = SchemaUtils.processChildElementNode(kid, symbolTable)) == null) continue;
            v.add(elem);
        }
        return v;
    }

    private static ElementDecl processChildElementNode(Node elementNode, SymbolTable symbolTable) {
        QName nodeName = Utils.getNodeNameQName(elementNode);
        BooleanHolder forElement = new BooleanHolder();
        String comments = null;
        comments = SchemaUtils.getAnnotationDocumentation(elementNode);
        QName nodeType = Utils.getTypeQName(elementNode, forElement, false);
        TypeEntry type = symbolTable.getTypeEntry(nodeType, forElement.value);
        if (!forElement.value) {
            String def;
            String form = Utils.getAttribute(elementNode, "form");
            if (form != null && form.equals("unqualified")) {
                nodeName = Utils.findQName("", nodeName.getLocalPart());
            } else if (form == null && ((def = Utils.getScopedAttribute(elementNode, "elementFormDefault")) == null || def.equals("unqualified"))) {
                nodeName = Utils.findQName("", nodeName.getLocalPart());
            }
        }
        if (type != null) {
            String maxOccurs;
            ElementDecl elem = new ElementDecl(type, nodeName);
            elem.setDocumentation(comments);
            String minOccurs = Utils.getAttribute(elementNode, "minOccurs");
            if (minOccurs != null && minOccurs.equals("0")) {
                elem.setMinOccursIs0(true);
            }
            if ((maxOccurs = Utils.getAttribute(elementNode, "maxOccurs")) != null) {
                if (maxOccurs.equals("unbounded")) {
                    elem.setMaxOccursIsUnbounded(true);
                } else if (maxOccurs.equals("1")) {
                    elem.setMaxOccursIsExactlyOne(true);
                }
            } else {
                elem.setMaxOccursIsExactlyOne(true);
            }
            elem.setNillable(JavaUtils.isTrueExplicitly(Utils.getAttribute(elementNode, "nillable")));
            String useValue = Utils.getAttribute(elementNode, "use");
            if (useValue != null) {
                elem.setOptional(useValue.equalsIgnoreCase("optional"));
            }
            return elem;
        }
        return null;
    }

    public static QName getElementAnonQName(Node node) {
        if (SchemaUtils.isXSDNode(node, "element")) {
            NodeList children = node.getChildNodes();
            for (int j = 0; j < children.getLength(); ++j) {
                Node kid = children.item(j);
                if (!SchemaUtils.isXSDNode(kid, "complexType") && !SchemaUtils.isXSDNode(kid, "simpleType")) continue;
                return Utils.getNodeNameQName(kid);
            }
        }
        return null;
    }

    public static QName getAttributeAnonQName(Node node) {
        if (SchemaUtils.isXSDNode(node, "attribute")) {
            NodeList children = node.getChildNodes();
            for (int j = 0; j < children.getLength(); ++j) {
                Node kid = children.item(j);
                if (!SchemaUtils.isXSDNode(kid, "complexType") && !SchemaUtils.isXSDNode(kid, "simpleType")) continue;
                return Utils.getNodeNameQName(kid);
            }
        }
        return null;
    }

    public static boolean isSimpleTypeOrSimpleContent(Node node) {
        NodeList children;
        if (node == null) {
            return false;
        }
        if (SchemaUtils.isXSDNode(node, "element")) {
            children = node.getChildNodes();
            for (int j = 0; j < children.getLength(); ++j) {
                Node kid = children.item(j);
                if (SchemaUtils.isXSDNode(kid, "complexType")) {
                    node = kid;
                    break;
                }
                if (!SchemaUtils.isXSDNode(kid, "simpleType")) continue;
                return true;
            }
        }
        if (SchemaUtils.isXSDNode(node, "simpleType")) {
            return true;
        }
        if (SchemaUtils.isXSDNode(node, "complexType")) {
            children = node.getChildNodes();
            Node complexContent = null;
            Node simpleContent = null;
            for (int j = 0; j < children.getLength(); ++j) {
                Node kid = children.item(j);
                if (SchemaUtils.isXSDNode(kid, "complexContent")) {
                    complexContent = kid;
                    break;
                }
                if (!SchemaUtils.isXSDNode(kid, "simpleContent")) continue;
                simpleContent = kid;
            }
            if (complexContent != null) {
                return false;
            }
            if (simpleContent != null) {
                return true;
            }
        }
        return false;
    }

    private static boolean isXSDNode(Node node, String schemaLocalName) {
        if (node == null) {
            return false;
        }
        String localName = node.getLocalName();
        if (localName == null) {
            return false;
        }
        return localName.equals(schemaLocalName) && Constants.isSchemaXSD(node.getNamespaceURI());
    }

    public static TypeEntry getComplexElementRestrictionBase(Node node, SymbolTable symbolTable) {
        NodeList children;
        if (node == null) {
            return null;
        }
        if (SchemaUtils.isXSDNode(node, "element")) {
            children = node.getChildNodes();
            Node complexNode = null;
            for (int j = 0; j < children.getLength() && complexNode == null; ++j) {
                if (!SchemaUtils.isXSDNode(children.item(j), "complexType")) continue;
                node = complexNode = children.item(j);
            }
        }
        if (SchemaUtils.isXSDNode(node, "complexType")) {
            Node kid;
            int j;
            children = node.getChildNodes();
            Node content = null;
            Node restriction = null;
            for (j = 0; j < children.getLength() && content == null; ++j) {
                kid = children.item(j);
                if (!SchemaUtils.isXSDNode(kid, "complexContent")) continue;
                content = kid;
            }
            if (content != null) {
                children = content.getChildNodes();
                for (j = 0; j < children.getLength() && restriction == null; ++j) {
                    kid = children.item(j);
                    if (!SchemaUtils.isXSDNode(kid, "restriction")) continue;
                    restriction = kid;
                }
            }
            if (restriction == null) {
                return null;
            }
            QName restrictionType = Utils.getTypeQName(restriction, new BooleanHolder(), false);
            if (restrictionType == null) {
                return null;
            }
            return symbolTable.getType(restrictionType);
        }
        return null;
    }

    public static TypeEntry getComplexElementExtensionBase(Node node, SymbolTable symbolTable) {
        NodeList children;
        if (node == null) {
            return null;
        }
        Object cached = (TypeEntry)symbolTable.node2ExtensionBase.get(node);
        if (cached != null) {
            return cached;
        }
        if (SchemaUtils.isXSDNode(node, "element")) {
            children = node.getChildNodes();
            Node complexNode = null;
            for (int j = 0; j < children.getLength() && complexNode == null; ++j) {
                if (!SchemaUtils.isXSDNode(children.item(j), "complexType")) continue;
                node = complexNode = children.item(j);
            }
        }
        if (SchemaUtils.isXSDNode(node, "complexType")) {
            QName extendsType;
            Node kid;
            int j;
            children = node.getChildNodes();
            Node content = null;
            Node extension = null;
            for (j = 0; j < children.getLength() && content == null; ++j) {
                kid = children.item(j);
                if (!SchemaUtils.isXSDNode(kid, "complexContent") && !SchemaUtils.isXSDNode(kid, "simpleContent")) continue;
                content = kid;
            }
            if (content != null) {
                children = content.getChildNodes();
                for (j = 0; j < children.getLength() && extension == null; ++j) {
                    kid = children.item(j);
                    if (!SchemaUtils.isXSDNode(kid, "extension")) continue;
                    extension = kid;
                }
            }
            cached = extension == null ? null : ((extendsType = Utils.getTypeQName(extension, new BooleanHolder(), false)) == null ? null : symbolTable.getType(extendsType));
        }
        symbolTable.node2ExtensionBase.put(node, cached);
        return cached;
    }

    public static QName getSimpleTypeBase(Node node) {
        QName[] qname = SchemaUtils.getContainedSimpleTypes(node);
        if (qname != null && qname.length > 0) {
            return qname[0];
        }
        return null;
    }

    public static QName[] getContainedSimpleTypes(Node node) {
        NodeList children;
        QName[] baseQNames = null;
        if (node == null) {
            return null;
        }
        if (SchemaUtils.isXSDNode(node, "element")) {
            children = node.getChildNodes();
            for (int j = 0; j < children.getLength(); ++j) {
                if (!SchemaUtils.isXSDNode(children.item(j), "simpleType")) continue;
                node = children.item(j);
                break;
            }
        }
        if (SchemaUtils.isXSDNode(node, "simpleType")) {
            children = node.getChildNodes();
            Node restrictionNode = null;
            Node unionNode = null;
            for (int j = 0; j < children.getLength() && restrictionNode == null; ++j) {
                if (SchemaUtils.isXSDNode(children.item(j), "restriction")) {
                    restrictionNode = children.item(j);
                    continue;
                }
                if (!SchemaUtils.isXSDNode(children.item(j), "union")) continue;
                unionNode = children.item(j);
            }
            if (restrictionNode != null) {
                baseQNames = new QName[]{Utils.getTypeQName(restrictionNode, new BooleanHolder(), false)};
            }
            if (unionNode != null) {
                baseQNames = Utils.getMemberTypeQNames(unionNode);
            }
            if (baseQNames != null && restrictionNode != null && unionNode != null) {
                NodeList enums = restrictionNode.getChildNodes();
                for (int i = 0; i < enums.getLength(); ++i) {
                    if (!SchemaUtils.isXSDNode(enums.item(i), "enumeration")) continue;
                    return null;
                }
            }
        }
        return baseQNames;
    }

    public static Node getRestrictionOrExtensionNode(Node node) {
        NodeList children;
        Node re = null;
        if (node == null) {
            return re;
        }
        if (SchemaUtils.isXSDNode(node, "element")) {
            children = node.getChildNodes();
            for (int j = 0; j < children.getLength(); ++j) {
                Node n = children.item(j);
                if (!SchemaUtils.isXSDNode(n, "simpleType") && !SchemaUtils.isXSDNode(n, "complexType") && !SchemaUtils.isXSDNode(n, "simpleContent")) continue;
                node = n;
                break;
            }
        }
        if (SchemaUtils.isXSDNode(node, "simpleType") || SchemaUtils.isXSDNode(node, "complexType")) {
            Node kid;
            children = node.getChildNodes();
            Node complexContent = null;
            if (node.getLocalName().equals("complexType")) {
                for (int j = 0; j < children.getLength() && complexContent == null; ++j) {
                    kid = children.item(j);
                    if (!SchemaUtils.isXSDNode(kid, "complexContent") && !SchemaUtils.isXSDNode(kid, "simpleContent")) continue;
                    complexContent = kid;
                }
                node = complexContent;
            }
            if (node != null) {
                children = node.getChildNodes();
                for (int j = 0; j < children.getLength() && re == null; ++j) {
                    kid = children.item(j);
                    if (!SchemaUtils.isXSDNode(kid, "extension") && !SchemaUtils.isXSDNode(kid, "restriction")) continue;
                    re = kid;
                }
            }
        }
        return re;
    }

    public static QName getArrayComponentQName(Node node, IntHolder dims, BooleanHolder underlTypeNillable, QNameHolder itemQName, SymbolTable symbolTable) {
        dims.value = 1;
        underlTypeNillable.value = false;
        QName qName = SchemaUtils.getCollectionComponentQName(node, itemQName);
        if (qName == null) {
            qName = SchemaUtils.getArrayComponentQName_JAXRPC(node, dims, underlTypeNillable, symbolTable);
        }
        return qName;
    }

    public static QName getCollectionComponentQName(Node node, QNameHolder itemQName) {
        QName fullQName;
        BooleanHolder forElement;
        QName componentTypeQName;
        boolean storeComponentQName = false;
        if (node == null) {
            return null;
        }
        if (itemQName != null && SchemaUtils.isXSDNode(node, "complexType")) {
            Node sequence = SchemaUtils.getChildByName(node, "sequence");
            if (sequence == null) {
                return null;
            }
            NodeList children = sequence.getChildNodes();
            Node element = null;
            for (int i = 0; i < children.getLength(); ++i) {
                if (children.item(i).getNodeType() != 1) continue;
                if (element == null) {
                    element = children.item(i);
                    continue;
                }
                return null;
            }
            if (element == null) {
                return null;
            }
            node = element;
            storeComponentQName = true;
        }
        if (SchemaUtils.isXSDNode(node, "element") && (componentTypeQName = Utils.getTypeQName(node, forElement = new BooleanHolder(), true)) != null && !componentTypeQName.equals(fullQName = Utils.getTypeQName(node, forElement, false))) {
            String name;
            if (storeComponentQName && (name = Utils.getAttribute(node, "name")) != null) {
                String def = Utils.getScopedAttribute(node, "elementFormDefault");
                String namespace = "";
                if (def != null && def.equals("qualified")) {
                    namespace = Utils.getScopedAttribute(node, "targetNamespace");
                }
                itemQName.value = new QName(namespace, name);
            }
            return componentTypeQName;
        }
        return null;
    }

    private static QName getArrayComponentQName_JAXRPC(Node node, IntHolder dims, BooleanHolder underlTypeNillable, SymbolTable symbolTable) {
        NodeList children;
        dims.value = 0;
        underlTypeNillable.value = false;
        if (node == null) {
            return null;
        }
        if (SchemaUtils.isXSDNode(node, "element")) {
            children = node.getChildNodes();
            for (int j = 0; j < children.getLength(); ++j) {
                Node kid = children.item(j);
                if (!SchemaUtils.isXSDNode(kid, "complexType")) continue;
                node = kid;
                break;
            }
        }
        if (SchemaUtils.isXSDNode(node, "complexType")) {
            children = node.getChildNodes();
            Node complexContentNode = null;
            for (int j = 0; j < children.getLength(); ++j) {
                Node kid = children.item(j);
                if (!SchemaUtils.isXSDNode(kid, "complexContent") && !SchemaUtils.isXSDNode(kid, "simpleContent")) continue;
                complexContentNode = kid;
                break;
            }
            Node restrictionNode = null;
            if (complexContentNode != null) {
                children = complexContentNode.getChildNodes();
                for (int j = 0; j < children.getLength(); ++j) {
                    Node kid = children.item(j);
                    if (!SchemaUtils.isXSDNode(kid, "restriction")) continue;
                    restrictionNode = kid;
                    break;
                }
            }
            QName baseType = null;
            if (!(restrictionNode == null || (baseType = Utils.getTypeQName(restrictionNode, new BooleanHolder(), false)) == null || baseType.getLocalPart().equals("Array") && Constants.isSOAP_ENC(baseType.getNamespaceURI()) || symbolTable.arrayTypeQNames.contains(baseType))) {
                baseType = null;
            }
            Node groupNode = null;
            Node attributeNode = null;
            if (baseType != null) {
                children = restrictionNode.getChildNodes();
                for (int j = 0; j < children.getLength() && groupNode == null && attributeNode == null; ++j) {
                    BooleanHolder isRef;
                    QName refQName;
                    Node kid = children.item(j);
                    if ((SchemaUtils.isXSDNode(kid, "sequence") || SchemaUtils.isXSDNode(kid, "all")) && (groupNode = kid).getChildNodes().getLength() == 0) {
                        groupNode = null;
                    }
                    if (!SchemaUtils.isXSDNode(kid, "attribute") || (refQName = Utils.getTypeQName(kid, isRef = new BooleanHolder(), false)) == null || !isRef.value || !refQName.getLocalPart().equals("arrayType") || !Constants.isSOAP_ENC(refQName.getNamespaceURI())) continue;
                    attributeNode = kid;
                }
            }
            if (attributeNode != null) {
                int i;
                String wsdlArrayTypeValue = null;
                Vector attrs = Utils.getAttributesWithLocalName(attributeNode, "arrayType");
                for (i = 0; i < attrs.size() && wsdlArrayTypeValue == null; ++i) {
                    Node attrNode = (Node)attrs.elementAt(i);
                    String attrName = attrNode.getNodeName();
                    QName attrQName = Utils.getQNameFromPrefixedName(attributeNode, attrName);
                    if (!Constants.isWSDL(attrQName.getNamespaceURI())) continue;
                    wsdlArrayTypeValue = attrNode.getNodeValue();
                }
                if (wsdlArrayTypeValue != null && (i = wsdlArrayTypeValue.indexOf(91)) > 0) {
                    String prefixedName = wsdlArrayTypeValue.substring(0, i);
                    String mangledString = wsdlArrayTypeValue.replace(',', '[');
                    dims.value = 0;
                    int index = mangledString.indexOf(91);
                    while (index > 0) {
                        ++dims.value;
                        index = mangledString.indexOf(91, index + 1);
                    }
                    return Utils.getQNameFromPrefixedName(restrictionNode, prefixedName);
                }
            } else if (groupNode != null) {
                NodeList elements = groupNode.getChildNodes();
                Node elementNode = null;
                for (int i = 0; i < elements.getLength() && elementNode == null; ++i) {
                    Node kid = elements.item(i);
                    if (!SchemaUtils.isXSDNode(kid, "element")) continue;
                    elementNode = elements.item(i);
                    break;
                }
                if (elementNode != null) {
                    String maxOccursValue;
                    String underlTypeNillableValue = Utils.getAttribute(elementNode, "nillable");
                    if (underlTypeNillableValue != null && underlTypeNillableValue.equals("true")) {
                        underlTypeNillable.value = true;
                    }
                    if ((maxOccursValue = Utils.getAttribute(elementNode, "maxOccurs")) != null && maxOccursValue.equalsIgnoreCase("unbounded")) {
                        dims.value = 1;
                        return Utils.getTypeQName(elementNode, new BooleanHolder(), true);
                    }
                }
            }
        }
        return null;
    }

    private static void addAttributeToVector(Vector v, Node child, SymbolTable symbolTable) {
        QName attributeName = Utils.getNodeNameQName(child);
        BooleanHolder forElement = new BooleanHolder();
        QName attributeType = Utils.getTypeQName(child, forElement, false);
        if (!forElement.value) {
            String def;
            String form = Utils.getAttribute(child, "form");
            if (form != null && form.equals("unqualified")) {
                attributeName = Utils.findQName("", attributeName.getLocalPart());
            } else if (form == null && ((def = Utils.getScopedAttribute(child, "attributeFormDefault")) == null || def.equals("unqualified"))) {
                attributeName = Utils.findQName("", attributeName.getLocalPart());
            }
        } else {
            attributeName = attributeType;
        }
        TypeEntry type = symbolTable.getTypeEntry(attributeType, forElement.value);
        if (type instanceof Element) {
            type = ((Element)type).getRefType();
        }
        if (type != null && attributeName != null) {
            ContainedAttribute attr = new ContainedAttribute(type, attributeName);
            String useValue = Utils.getAttribute(child, "use");
            if (useValue != null) {
                attr.setOptional(useValue.equalsIgnoreCase("optional"));
            }
            v.add(attr);
        }
    }

    private static void addAttributeToVector(Vector v, SymbolTable symbolTable, QName type, QName name) {
        TypeEntry typeEnt = symbolTable.getTypeEntry(type, false);
        if (typeEnt != null) {
            v.add(new ContainedAttribute(typeEnt, name));
        }
    }

    private static void addAttributeGroupToVector(Vector v, Node attrGrpnode, SymbolTable symbolTable) {
        QName attributeGroupType = Utils.getTypeQName(attrGrpnode, new BooleanHolder(), false);
        TypeEntry type = symbolTable.getTypeEntry(attributeGroupType, false);
        if (type != null) {
            if (type.getNode() != null) {
                NodeList children = type.getNode().getChildNodes();
                for (int j = 0; j < children.getLength(); ++j) {
                    Node kid = children.item(j);
                    if (SchemaUtils.isXSDNode(kid, "attribute")) {
                        SchemaUtils.addAttributeToVector(v, kid, symbolTable);
                        continue;
                    }
                    if (!SchemaUtils.isXSDNode(kid, "attributeGroup")) continue;
                    SchemaUtils.addAttributeGroupToVector(v, kid, symbolTable);
                }
            } else if (type.isBaseType()) {
                if (type.getQName().equals(Constants.SOAP_COMMON_ATTRS11)) {
                    SchemaUtils.addAttributeToVector(v, symbolTable, Constants.XSD_ID, new QName("http://schemas.xmlsoap.org/soap/encoding/", "id"));
                    SchemaUtils.addAttributeToVector(v, symbolTable, Constants.XSD_ANYURI, new QName("http://schemas.xmlsoap.org/soap/encoding/", "href"));
                } else if (type.getQName().equals(Constants.SOAP_COMMON_ATTRS12)) {
                    SchemaUtils.addAttributeToVector(v, symbolTable, Constants.XSD_ID, new QName("http://www.w3.org/2003/05/soap-encoding", "id"));
                } else if (type.getQName().equals(Constants.SOAP_ARRAY_ATTRS11)) {
                    SchemaUtils.addAttributeToVector(v, symbolTable, Constants.XSD_STRING, new QName("http://www.w3.org/2003/05/soap-encoding", "arrayType"));
                    SchemaUtils.addAttributeToVector(v, symbolTable, Constants.XSD_STRING, new QName("http://www.w3.org/2003/05/soap-encoding", "offset"));
                } else if (type.getQName().equals(Constants.SOAP_ARRAY_ATTRS12)) {
                    SchemaUtils.addAttributeToVector(v, symbolTable, Constants.XSD_STRING, new QName("http://www.w3.org/2003/05/soap-encoding", "arraySize"));
                    SchemaUtils.addAttributeToVector(v, symbolTable, Constants.XSD_QNAME, new QName("http://www.w3.org/2003/05/soap-encoding", "itemType"));
                }
            }
        }
    }

    public static Vector getContainedAttributeTypes(Node node, SymbolTable symbolTable) {
        NodeList children;
        Vector v = null;
        if (node == null) {
            return null;
        }
        if (SchemaUtils.isXSDNode(node, "element")) {
            children = node.getChildNodes();
            int len = children.getLength();
            for (int j = 0; j < len; ++j) {
                Node kid = children.item(j);
                if (!SchemaUtils.isXSDNode(kid, "complexType")) continue;
                node = kid;
                break;
            }
        }
        if (SchemaUtils.isXSDNode(node, "complexType")) {
            Node kid;
            int j;
            children = node.getChildNodes();
            Node content = null;
            int len = children.getLength();
            for (j = 0; j < len; ++j) {
                kid = children.item(j);
                if (!SchemaUtils.isXSDNode(kid, "complexContent") && !SchemaUtils.isXSDNode(kid, "simpleContent")) continue;
                content = kid;
                break;
            }
            if (content != null) {
                children = content.getChildNodes();
                len = children.getLength();
                for (j = 0; j < len; ++j) {
                    kid = children.item(j);
                    if (!SchemaUtils.isXSDNode(kid, "extension") && !SchemaUtils.isXSDNode(kid, "restriction")) continue;
                    node = kid;
                    break;
                }
            }
            children = node.getChildNodes();
            len = children.getLength();
            for (int i = 0; i < len; ++i) {
                Node child = children.item(i);
                if (SchemaUtils.isXSDNode(child, "attributeGroup")) {
                    if (v == null) {
                        v = new Vector();
                    }
                    SchemaUtils.addAttributeGroupToVector(v, child, symbolTable);
                    continue;
                }
                if (SchemaUtils.isXSDNode(child, "anyAttribute")) {
                    if (v != null) continue;
                    v = new Vector();
                    continue;
                }
                if (!SchemaUtils.isXSDNode(child, "attribute")) continue;
                if (v == null) {
                    v = new Vector();
                }
                SchemaUtils.addAttributeToVector(v, child, symbolTable);
            }
        }
        return v;
    }

    private static boolean isSimpleSchemaType(String s) {
        if (s == null) {
            return false;
        }
        return schemaTypeSet.contains(s);
    }

    public static boolean isSimpleSchemaType(QName qname) {
        if (qname == null || !Constants.isSchemaXSD(qname.getNamespaceURI())) {
            return false;
        }
        return SchemaUtils.isSimpleSchemaType(qname.getLocalPart());
    }

    public static TypeEntry getBaseType(TypeEntry type, SymbolTable symbolTable) {
        QName baseQName;
        Node node = type.getNode();
        TypeEntry base = SchemaUtils.getComplexElementExtensionBase(node, symbolTable);
        if (base == null) {
            base = SchemaUtils.getComplexElementRestrictionBase(node, symbolTable);
        }
        if (base == null && (baseQName = SchemaUtils.getSimpleTypeBase(node)) != null) {
            base = symbolTable.getType(baseQName);
        }
        return base;
    }

    public static boolean isListWithItemType(Node node) {
        return SchemaUtils.getListItemType(node) != null;
    }

    public static QName getListItemType(Node node) {
        int j;
        NodeList children;
        if (node == null) {
            return null;
        }
        if (SchemaUtils.isXSDNode(node, "element")) {
            children = node.getChildNodes();
            for (j = 0; j < children.getLength(); ++j) {
                if (!SchemaUtils.isXSDNode(children.item(j), "simpleType")) continue;
                node = children.item(j);
                break;
            }
        }
        if (SchemaUtils.isXSDNode(node, "simpleType")) {
            children = node.getChildNodes();
            for (j = 0; j < children.getLength(); ++j) {
                if (!SchemaUtils.isXSDNode(children.item(j), "list")) continue;
                Node listNode = children.item(j);
                org.w3c.dom.Element listElement = (org.w3c.dom.Element)listNode;
                String type = listElement.getAttribute("itemType");
                if (type.equals("")) {
                    Node localType = null;
                    children = listNode.getChildNodes();
                    for (j = 0; j < children.getLength() && localType == null; ++j) {
                        if (!SchemaUtils.isXSDNode(children.item(j), "simpleType")) continue;
                        localType = children.item(j);
                    }
                    if (localType != null) {
                        return SchemaUtils.getSimpleTypeBase(localType);
                    }
                    return null;
                }
                return Utils.getQNameFromPrefixedName(node, type);
            }
        }
        return null;
    }
}

