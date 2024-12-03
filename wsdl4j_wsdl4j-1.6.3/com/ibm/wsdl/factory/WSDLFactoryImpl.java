/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.wsdl.factory;

import com.ibm.wsdl.DefinitionImpl;
import com.ibm.wsdl.extensions.PopulatedExtensionRegistry;
import com.ibm.wsdl.xml.WSDLReaderImpl;
import com.ibm.wsdl.xml.WSDLWriterImpl;
import javax.wsdl.Definition;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;

public class WSDLFactoryImpl
extends WSDLFactory {
    public Definition newDefinition() {
        DefinitionImpl def = new DefinitionImpl();
        ExtensionRegistry extReg = this.newPopulatedExtensionRegistry();
        def.setExtensionRegistry(extReg);
        return def;
    }

    public WSDLReader newWSDLReader() {
        return new WSDLReaderImpl();
    }

    public WSDLWriter newWSDLWriter() {
        return new WSDLWriterImpl();
    }

    public ExtensionRegistry newPopulatedExtensionRegistry() {
        return new PopulatedExtensionRegistry();
    }
}

