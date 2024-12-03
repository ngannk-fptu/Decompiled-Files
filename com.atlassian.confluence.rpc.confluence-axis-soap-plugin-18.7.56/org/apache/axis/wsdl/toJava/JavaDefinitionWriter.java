/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Binding
 *  javax.wsdl.Definition
 *  javax.wsdl.Import
 *  javax.wsdl.Message
 */
package org.apache.axis.wsdl.toJava;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.Message;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.gen.Generator;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.FaultInfo;
import org.apache.axis.wsdl.symbolTable.MessageEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.toJava.DuplicateFileException;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaFaultWriter;
import org.apache.axis.wsdl.toJava.JavaGeneratorFactory;
import org.apache.axis.wsdl.toJava.Utils;

public class JavaDefinitionWriter
implements Generator {
    protected Emitter emitter;
    protected Definition definition;
    protected SymbolTable symbolTable;
    private HashSet importedFiles = new HashSet();

    public JavaDefinitionWriter(Emitter emitter, Definition definition, SymbolTable symbolTable) {
        this.emitter = emitter;
        this.definition = definition;
        this.symbolTable = symbolTable;
    }

    public void generate() throws IOException {
        this.writeFaults();
    }

    protected void writeFaults() throws IOException {
        ArrayList faults = new ArrayList();
        this.collectFaults(this.definition, faults);
        HashSet<String> generatedFaults = new HashSet<String>();
        Iterator fi = faults.iterator();
        while (fi.hasNext()) {
            Boolean complexTypeFault;
            FaultInfo faultInfo = (FaultInfo)fi.next();
            Message message = faultInfo.getMessage();
            String name = Utils.getFullExceptionName(message, this.symbolTable);
            if (generatedFaults.contains(name)) continue;
            generatedFaults.add(name);
            MessageEntry me = this.symbolTable.getMessageEntry(message.getQName());
            boolean emitSimpleFault = true;
            if (me != null && (complexTypeFault = (Boolean)me.getDynamicVar(JavaGeneratorFactory.COMPLEX_TYPE_FAULT)) != null && complexTypeFault.booleanValue()) {
                emitSimpleFault = false;
            }
            if (!emitSimpleFault) continue;
            try {
                JavaFaultWriter writer = new JavaFaultWriter(this.emitter, this.symbolTable, faultInfo);
                writer.generate();
            }
            catch (DuplicateFileException dfe) {
                System.err.println(Messages.getMessage("fileExistError00", dfe.getFileName()));
            }
        }
    }

    private void collectFaults(Definition def, ArrayList faults) throws IOException {
        Map imports = def.getImports();
        Object[] importValues = imports.values().toArray();
        for (int i = 0; i < importValues.length; ++i) {
            Vector v = (Vector)importValues[i];
            for (int j = 0; j < v.size(); ++j) {
                Import imp = (Import)v.get(j);
                if (this.importedFiles.contains(imp.getLocationURI())) continue;
                this.importedFiles.add(imp.getLocationURI());
                Definition importDef = imp.getDefinition();
                if (importDef == null) continue;
                this.collectFaults(importDef, faults);
            }
        }
        Map bindings = def.getBindings();
        Iterator bindi = bindings.values().iterator();
        while (bindi.hasNext()) {
            Binding binding = (Binding)bindi.next();
            BindingEntry entry = this.symbolTable.getBindingEntry(binding.getQName());
            if (!entry.isReferenced()) continue;
            HashMap faultMap = entry.getFaults();
            Iterator it = faultMap.values().iterator();
            while (it.hasNext()) {
                ArrayList list = (ArrayList)it.next();
                faults.addAll(list);
            }
        }
    }
}

