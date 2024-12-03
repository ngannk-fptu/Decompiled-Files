/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Binding
 *  javax.wsdl.Definition
 *  javax.wsdl.Message
 *  javax.wsdl.PortType
 *  javax.wsdl.Service
 */
package org.apache.axis.wsdl.gen;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Message;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.xml.namespace.QName;
import org.apache.axis.encoding.DefaultSOAPEncodingTypeMappingImpl;
import org.apache.axis.encoding.TypeMapping;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.wsdl.gen.Generator;
import org.apache.axis.wsdl.gen.GeneratorFactory;
import org.apache.axis.wsdl.gen.NoopGenerator;
import org.apache.axis.wsdl.symbolTable.BaseTypeMapping;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

public class NoopFactory
implements GeneratorFactory {
    private BaseTypeMapping btm = null;

    public void generatorPass(Definition def, SymbolTable symbolTable) {
    }

    public Generator getGenerator(Message message, SymbolTable symbolTable) {
        return new NoopGenerator();
    }

    public Generator getGenerator(PortType portType, SymbolTable symbolTable) {
        return new NoopGenerator();
    }

    public Generator getGenerator(Binding binding, SymbolTable symbolTable) {
        return new NoopGenerator();
    }

    public Generator getGenerator(Service service, SymbolTable symbolTable) {
        return new NoopGenerator();
    }

    public Generator getGenerator(TypeEntry type, SymbolTable symbolTable) {
        return new NoopGenerator();
    }

    public Generator getGenerator(Definition definition, SymbolTable symbolTable) {
        return new NoopGenerator();
    }

    public void setBaseTypeMapping(BaseTypeMapping btm) {
        this.btm = btm;
    }

    public BaseTypeMapping getBaseTypeMapping() {
        if (this.btm == null) {
            this.btm = new BaseTypeMapping(){
                TypeMapping defaultTM = DefaultSOAPEncodingTypeMappingImpl.createWithDelegate();

                public String getBaseName(QName qNameIn) {
                    QName qName = new QName(qNameIn.getNamespaceURI(), qNameIn.getLocalPart());
                    Class cls = this.defaultTM.getClassForQName(qName);
                    if (cls == null) {
                        return null;
                    }
                    return JavaUtils.getTextClassName(cls.getName());
                }
            };
        }
        return this.btm;
    }
}

