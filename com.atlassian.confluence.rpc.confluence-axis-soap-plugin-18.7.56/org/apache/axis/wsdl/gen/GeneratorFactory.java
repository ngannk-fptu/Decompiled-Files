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
import org.apache.axis.wsdl.gen.Generator;
import org.apache.axis.wsdl.symbolTable.BaseTypeMapping;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.TypeEntry;

public interface GeneratorFactory {
    public void generatorPass(Definition var1, SymbolTable var2);

    public Generator getGenerator(Message var1, SymbolTable var2);

    public Generator getGenerator(PortType var1, SymbolTable var2);

    public Generator getGenerator(Binding var1, SymbolTable var2);

    public Generator getGenerator(Service var1, SymbolTable var2);

    public Generator getGenerator(TypeEntry var1, SymbolTable var2);

    public Generator getGenerator(Definition var1, SymbolTable var2);

    public void setBaseTypeMapping(BaseTypeMapping var1);

    public BaseTypeMapping getBaseTypeMapping();
}

