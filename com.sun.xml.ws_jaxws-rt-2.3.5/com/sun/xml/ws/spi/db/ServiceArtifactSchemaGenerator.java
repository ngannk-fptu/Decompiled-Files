/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.bind.v2.schemagen.xmlschema.ComplexType
 *  com.sun.xml.bind.v2.schemagen.xmlschema.Element
 *  com.sun.xml.bind.v2.schemagen.xmlschema.ExplicitGroup
 *  com.sun.xml.bind.v2.schemagen.xmlschema.LocalElement
 *  com.sun.xml.bind.v2.schemagen.xmlschema.Occurs
 *  com.sun.xml.txw2.TXW
 *  com.sun.xml.txw2.output.ResultFactory
 *  com.sun.xml.txw2.output.XmlSerializer
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.SchemaOutputResolver
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.spi.db;

import com.sun.xml.bind.v2.schemagen.xmlschema.ComplexType;
import com.sun.xml.bind.v2.schemagen.xmlschema.Element;
import com.sun.xml.bind.v2.schemagen.xmlschema.ExplicitGroup;
import com.sun.xml.bind.v2.schemagen.xmlschema.LocalElement;
import com.sun.xml.bind.v2.schemagen.xmlschema.Occurs;
import com.sun.xml.txw2.TXW;
import com.sun.xml.txw2.output.ResultFactory;
import com.sun.xml.txw2.output.XmlSerializer;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.model.AbstractSEIModelImpl;
import com.sun.xml.ws.model.JavaMethodImpl;
import com.sun.xml.ws.model.ParameterImpl;
import com.sun.xml.ws.model.WrapperParameter;
import com.sun.xml.ws.spi.db.TypeInfo;
import com.sun.xml.ws.spi.db.WrapperComposite;
import com.sun.xml.ws.wsdl.writer.document.xsd.Import;
import com.sun.xml.ws.wsdl.writer.document.xsd.Schema;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.ws.WebServiceException;

public class ServiceArtifactSchemaGenerator {
    protected AbstractSEIModelImpl model;
    protected SchemaOutputResolver xsdResolver;
    static final String FilePrefix = "jaxwsGen";
    protected int fileIndex = 0;

    public ServiceArtifactSchemaGenerator(SEIModel model) {
        this.model = (AbstractSEIModelImpl)model;
    }

    protected Schema create(String tns) {
        try {
            Result res = this.xsdResolver.createOutput(tns, FilePrefix + this.fileIndex++ + ".xsd");
            return (Schema)TXW.create(Schema.class, (XmlSerializer)ResultFactory.createSerializer((Result)res));
        }
        catch (IOException e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    public void generate(SchemaOutputResolver resolver) {
        this.xsdResolver = resolver;
        ArrayList<WrapperParameter> wrappers = new ArrayList<WrapperParameter>();
        for (JavaMethodImpl method : this.model.getJavaMethods()) {
            if (method.getBinding().isRpcLit()) continue;
            for (ParameterImpl p : method.getRequestParameters()) {
                if (!(p instanceof WrapperParameter) || !WrapperComposite.class.equals((Object)((WrapperParameter)p).getTypeInfo().type)) continue;
                wrappers.add((WrapperParameter)p);
            }
            for (ParameterImpl p : method.getResponseParameters()) {
                if (!(p instanceof WrapperParameter) || !WrapperComposite.class.equals((Object)((WrapperParameter)p).getTypeInfo().type)) continue;
                wrappers.add((WrapperParameter)p);
            }
        }
        if (wrappers.isEmpty()) {
            return;
        }
        HashMap<String, Schema> xsds = this.initWrappersSchemaWithImports(wrappers);
        this.postInit(xsds);
        for (WrapperParameter wp : wrappers) {
            String tns = wp.getName().getNamespaceURI();
            Schema xsd = xsds.get(tns);
            Element e = (Element)xsd._element(Element.class);
            e._attribute("name", (Object)wp.getName().getLocalPart());
            e.type(wp.getName());
            ComplexType ct = (ComplexType)xsd._element(ComplexType.class);
            ct._attribute("name", (Object)wp.getName().getLocalPart());
            ExplicitGroup sq = ct.sequence();
            for (ParameterImpl p : wp.getWrapperChildren()) {
                if (!p.getBinding().isBody()) continue;
                this.addChild(sq, p);
            }
        }
        for (Schema xsd : xsds.values()) {
            xsd.commit();
        }
    }

    protected void postInit(HashMap<String, Schema> xsds) {
    }

    protected void addChild(ExplicitGroup sq, ParameterImpl param) {
        TypeInfo typeInfo = param.getItemType();
        boolean repeatedElement = false;
        if (typeInfo == null) {
            typeInfo = param.getTypeInfo();
        } else if (typeInfo.getWrapperType() != null) {
            typeInfo = param.getTypeInfo();
        } else {
            repeatedElement = true;
        }
        Occurs child = this.addChild(sq, param.getName(), typeInfo);
        if (repeatedElement && child != null) {
            child.maxOccurs("unbounded");
        }
    }

    protected Occurs addChild(ExplicitGroup sq, QName name, TypeInfo typeInfo) {
        LocalElement le = null;
        QName type = this.model.getBindingContext().getTypeName(typeInfo);
        if (type != null) {
            le = sq.element();
            le._attribute("name", (Object)name.getLocalPart());
            le.type(type);
        } else if (typeInfo.type instanceof Class) {
            try {
                QName elemName = this.model.getBindingContext().getElementName((Class)typeInfo.type);
                if (elemName.getLocalPart().equals("any") && elemName.getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema")) {
                    return sq.any();
                }
                le = sq.element();
                le.ref(elemName);
            }
            catch (JAXBException je) {
                throw new WebServiceException(je.getMessage(), (Throwable)je);
            }
        }
        return le;
    }

    private HashMap<String, Schema> initWrappersSchemaWithImports(List<WrapperParameter> wrappers) {
        String tns;
        Object o = this.model.databindingInfo().properties().get("com.sun.xml.ws.api.model.DocWrappeeNamespapceQualified");
        boolean wrappeeQualified = o != null && o instanceof Boolean ? (Boolean)o : false;
        HashMap<String, Schema> xsds = new HashMap<String, Schema>();
        HashMap<String, HashSet<String>> imports = new HashMap<String, HashSet<String>>();
        for (WrapperParameter wrapperParameter : wrappers) {
            tns = wrapperParameter.getName().getNamespaceURI();
            Schema xsd = xsds.get(tns);
            if (xsd == null) {
                xsd = this.create(tns);
                xsd.targetNamespace(tns);
                if (wrappeeQualified) {
                    xsd._attribute("elementFormDefault", "qualified");
                }
                xsds.put(tns, xsd);
            }
            for (ParameterImpl p : wrapperParameter.getWrapperChildren()) {
                String nsToImport = p.getBinding().isBody() ? this.bodyParamNS(p) : null;
                if (nsToImport == null || nsToImport.equals(tns) || nsToImport.equals("http://www.w3.org/2001/XMLSchema")) continue;
                HashSet<String> importSet = (HashSet<String>)imports.get(tns);
                if (importSet == null) {
                    importSet = new HashSet<String>();
                    imports.put(tns, importSet);
                }
                importSet.add(nsToImport);
            }
        }
        for (Map.Entry entry : imports.entrySet()) {
            tns = (String)entry.getKey();
            Set importSet = (Set)entry.getValue();
            Schema xsd = xsds.get(tns);
            for (String nsToImport : importSet) {
                xsd._namespace(nsToImport, true);
            }
            for (String nsToImport : importSet) {
                Import imp = xsd._import();
                imp.namespace(nsToImport);
            }
        }
        return xsds;
    }

    protected String bodyParamNS(ParameterImpl p) {
        QName type;
        String nsToImport = null;
        TypeInfo typeInfo = p.getItemType();
        if (typeInfo == null) {
            typeInfo = p.getTypeInfo();
        }
        if ((type = this.model.getBindingContext().getTypeName(typeInfo)) != null) {
            nsToImport = type.getNamespaceURI();
        } else if (typeInfo.type instanceof Class) {
            try {
                QName elemRef = this.model.getBindingContext().getElementName((Class)typeInfo.type);
                if (elemRef != null) {
                    nsToImport = elemRef.getNamespaceURI();
                }
            }
            catch (JAXBException je) {
                throw new WebServiceException(je.getMessage(), (Throwable)je);
            }
        }
        return nsToImport;
    }
}

