/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Binding
 *  javax.wsdl.Definition
 *  javax.wsdl.Fault
 *  javax.wsdl.Message
 *  javax.wsdl.Operation
 *  javax.wsdl.OperationType
 *  javax.wsdl.Port
 *  javax.wsdl.PortType
 *  javax.wsdl.Service
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.wsdl.toJava;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Fault;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.xml.namespace.QName;
import javax.xml.rpc.holders.BooleanHolder;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.gen.Generator;
import org.apache.axis.wsdl.gen.GeneratorFactory;
import org.apache.axis.wsdl.gen.NoopGenerator;
import org.apache.axis.wsdl.symbolTable.BaseTypeMapping;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.ContainedAttribute;
import org.apache.axis.wsdl.symbolTable.Element;
import org.apache.axis.wsdl.symbolTable.ElementDecl;
import org.apache.axis.wsdl.symbolTable.FaultInfo;
import org.apache.axis.wsdl.symbolTable.MessageEntry;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.PortTypeEntry;
import org.apache.axis.wsdl.symbolTable.SchemaUtils;
import org.apache.axis.wsdl.symbolTable.ServiceEntry;
import org.apache.axis.wsdl.symbolTable.SymTabEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.Type;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaBindingWriter;
import org.apache.axis.wsdl.toJava.JavaServiceWriter;
import org.apache.axis.wsdl.toJava.JavaTypeWriter;
import org.apache.axis.wsdl.toJava.NamespaceSelector;
import org.apache.axis.wsdl.toJava.Utils;
import org.apache.commons.logging.Log;

public class JavaGeneratorFactory
implements GeneratorFactory {
    private static final Log log_ = LogFactory.getLog((class$org$apache$axis$wsdl$toJava$JavaGeneratorFactory == null ? (class$org$apache$axis$wsdl$toJava$JavaGeneratorFactory = JavaGeneratorFactory.class$("org.apache.axis.wsdl.toJava.JavaGeneratorFactory")) : class$org$apache$axis$wsdl$toJava$JavaGeneratorFactory).getName());
    protected Emitter emitter;
    protected SymbolTable symbolTable;
    public static String COMPLEX_TYPE_FAULT = "ComplexTypeFault";
    public static String EXCEPTION_CLASS_NAME = "ExceptionClassName";
    public static String EXCEPTION_DATA_TYPE = "ExceptionDataType";
    private static final String SERVICE_SUFFIX = "_Service";
    private static final String PORT_TYPE_SUFFIX = "_PortType";
    private static final String TYPE_SUFFIX = "_Type";
    private static final String ELEMENT_SUFFIX = "_Element";
    private static final String EXCEPTION_SUFFIX = "_Exception";
    private static final String BINDING_SUFFIX = "_Binding";
    private Writers messageWriters = new Writers();
    private Writers portTypeWriters = new Writers();
    protected Writers bindingWriters = new Writers();
    protected Writers serviceWriters = new Writers();
    private Writers typeWriters = new Writers();
    private Writers defWriters = new Writers();
    BaseTypeMapping btm = null;
    static /* synthetic */ Class class$org$apache$axis$wsdl$toJava$JavaGeneratorFactory;
    static /* synthetic */ Class class$javax$wsdl$Definition;
    static /* synthetic */ Class class$org$apache$axis$wsdl$toJava$JavaDefinitionWriter;
    static /* synthetic */ Class class$org$apache$axis$wsdl$toJava$JavaDeployWriter;
    static /* synthetic */ Class class$org$apache$axis$wsdl$toJava$JavaUndeployWriter;
    static /* synthetic */ Class class$org$apache$axis$wsdl$toJava$JavaBuildFileWriter;
    static /* synthetic */ Class class$org$apache$axis$wsdl$toJava$Emitter;
    static /* synthetic */ Class class$org$apache$axis$wsdl$symbolTable$SymbolTable;
    static /* synthetic */ Class class$javax$wsdl$Message;
    static /* synthetic */ Class class$javax$wsdl$PortType;
    static /* synthetic */ Class class$javax$wsdl$Binding;
    static /* synthetic */ Class class$javax$wsdl$Service;
    static /* synthetic */ Class class$org$apache$axis$wsdl$symbolTable$TypeEntry;

    public JavaGeneratorFactory() {
        this.addGenerators();
    }

    public JavaGeneratorFactory(Emitter emitter) {
        this.emitter = emitter;
        this.addGenerators();
    }

    public void setEmitter(Emitter emitter) {
        this.emitter = emitter;
    }

    private void addGenerators() {
        this.addMessageGenerators();
        this.addPortTypeGenerators();
        this.addBindingGenerators();
        this.addServiceGenerators();
        this.addTypeGenerators();
        this.addDefinitionGenerators();
    }

    protected void addMessageGenerators() {
    }

    protected void addPortTypeGenerators() {
    }

    protected void addBindingGenerators() {
    }

    protected void addServiceGenerators() {
    }

    protected void addTypeGenerators() {
    }

    protected void addDefinitionGenerators() {
        this.addGenerator(class$javax$wsdl$Definition == null ? (class$javax$wsdl$Definition = JavaGeneratorFactory.class$("javax.wsdl.Definition")) : class$javax$wsdl$Definition, class$org$apache$axis$wsdl$toJava$JavaDefinitionWriter == null ? (class$org$apache$axis$wsdl$toJava$JavaDefinitionWriter = JavaGeneratorFactory.class$("org.apache.axis.wsdl.toJava.JavaDefinitionWriter")) : class$org$apache$axis$wsdl$toJava$JavaDefinitionWriter);
        this.addGenerator(class$javax$wsdl$Definition == null ? (class$javax$wsdl$Definition = JavaGeneratorFactory.class$("javax.wsdl.Definition")) : class$javax$wsdl$Definition, class$org$apache$axis$wsdl$toJava$JavaDeployWriter == null ? (class$org$apache$axis$wsdl$toJava$JavaDeployWriter = JavaGeneratorFactory.class$("org.apache.axis.wsdl.toJava.JavaDeployWriter")) : class$org$apache$axis$wsdl$toJava$JavaDeployWriter);
        this.addGenerator(class$javax$wsdl$Definition == null ? (class$javax$wsdl$Definition = JavaGeneratorFactory.class$("javax.wsdl.Definition")) : class$javax$wsdl$Definition, class$org$apache$axis$wsdl$toJava$JavaUndeployWriter == null ? (class$org$apache$axis$wsdl$toJava$JavaUndeployWriter = JavaGeneratorFactory.class$("org.apache.axis.wsdl.toJava.JavaUndeployWriter")) : class$org$apache$axis$wsdl$toJava$JavaUndeployWriter);
        this.addGenerator(class$javax$wsdl$Definition == null ? (class$javax$wsdl$Definition = JavaGeneratorFactory.class$("javax.wsdl.Definition")) : class$javax$wsdl$Definition, class$org$apache$axis$wsdl$toJava$JavaBuildFileWriter == null ? (class$org$apache$axis$wsdl$toJava$JavaBuildFileWriter = JavaGeneratorFactory.class$("org.apache.axis.wsdl.toJava.JavaBuildFileWriter")) : class$org$apache$axis$wsdl$toJava$JavaBuildFileWriter);
    }

    public void generatorPass(Definition def, SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.javifyNames(symbolTable);
        this.setFaultContext(symbolTable);
        this.resolveNameClashes(symbolTable);
        this.determineInterfaceNames(symbolTable);
        if (this.emitter.isAllWanted()) {
            this.setAllReferencesToTrue();
        } else {
            this.ignoreNonSOAPBindings(symbolTable);
        }
        this.constructSignatures(symbolTable);
        this.determineIfHoldersNeeded(symbolTable);
    }

    public Generator getGenerator(Message message, SymbolTable symbolTable) {
        if (this.include(message.getQName())) {
            MessageEntry mEntry = symbolTable.getMessageEntry(message.getQName());
            this.messageWriters.addStuff((Generator)new NoopGenerator(), mEntry, symbolTable);
            return this.messageWriters;
        }
        return new NoopGenerator();
    }

    public Generator getGenerator(PortType portType, SymbolTable symbolTable) {
        if (this.include(portType.getQName())) {
            PortTypeEntry ptEntry = symbolTable.getPortTypeEntry(portType.getQName());
            this.portTypeWriters.addStuff((Generator)new NoopGenerator(), ptEntry, symbolTable);
            return this.portTypeWriters;
        }
        return new NoopGenerator();
    }

    public Generator getGenerator(Binding binding, SymbolTable symbolTable) {
        if (this.include(binding.getQName())) {
            JavaBindingWriter writer = new JavaBindingWriter(this.emitter, binding, symbolTable);
            BindingEntry bEntry = symbolTable.getBindingEntry(binding.getQName());
            this.bindingWriters.addStuff((Generator)writer, bEntry, symbolTable);
            return this.bindingWriters;
        }
        return new NoopGenerator();
    }

    public Generator getGenerator(Service service, SymbolTable symbolTable) {
        if (this.include(service.getQName())) {
            JavaServiceWriter writer = new JavaServiceWriter(this.emitter, service, symbolTable);
            ServiceEntry sEntry = symbolTable.getServiceEntry(service.getQName());
            this.serviceWriters.addStuff((Generator)writer, sEntry, symbolTable);
            return this.serviceWriters;
        }
        return new NoopGenerator();
    }

    public Generator getGenerator(TypeEntry type, SymbolTable symbolTable) {
        if (this.include(type.getQName())) {
            JavaTypeWriter writer = new JavaTypeWriter(this.emitter, type, symbolTable);
            this.typeWriters.addStuff((Generator)writer, type, symbolTable);
            return this.typeWriters;
        }
        return new NoopGenerator();
    }

    public Generator getGenerator(Definition definition, SymbolTable symbolTable) {
        if (this.include(definition.getQName())) {
            this.defWriters.addStuff(null, definition, symbolTable);
            return this.defWriters;
        }
        return new NoopGenerator();
    }

    public void addGenerator(Class wsdlClass, Class generator) {
        if ((class$javax$wsdl$Message == null ? (class$javax$wsdl$Message = JavaGeneratorFactory.class$("javax.wsdl.Message")) : class$javax$wsdl$Message).isAssignableFrom(wsdlClass)) {
            this.messageWriters.addGenerator(generator);
        } else if ((class$javax$wsdl$PortType == null ? (class$javax$wsdl$PortType = JavaGeneratorFactory.class$("javax.wsdl.PortType")) : class$javax$wsdl$PortType).isAssignableFrom(wsdlClass)) {
            this.portTypeWriters.addGenerator(generator);
        } else if ((class$javax$wsdl$Binding == null ? (class$javax$wsdl$Binding = JavaGeneratorFactory.class$("javax.wsdl.Binding")) : class$javax$wsdl$Binding).isAssignableFrom(wsdlClass)) {
            this.bindingWriters.addGenerator(generator);
        } else if ((class$javax$wsdl$Service == null ? (class$javax$wsdl$Service = JavaGeneratorFactory.class$("javax.wsdl.Service")) : class$javax$wsdl$Service).isAssignableFrom(wsdlClass)) {
            this.serviceWriters.addGenerator(generator);
        } else if ((class$org$apache$axis$wsdl$symbolTable$TypeEntry == null ? (class$org$apache$axis$wsdl$symbolTable$TypeEntry = JavaGeneratorFactory.class$("org.apache.axis.wsdl.symbolTable.TypeEntry")) : class$org$apache$axis$wsdl$symbolTable$TypeEntry).isAssignableFrom(wsdlClass)) {
            this.typeWriters.addGenerator(generator);
        } else if ((class$javax$wsdl$Definition == null ? (class$javax$wsdl$Definition = JavaGeneratorFactory.class$("javax.wsdl.Definition")) : class$javax$wsdl$Definition).isAssignableFrom(wsdlClass)) {
            this.defWriters.addGenerator(generator);
        }
    }

    protected void javifyNames(SymbolTable symbolTable) {
        int uniqueNum = 0;
        HashMap anonQNames = new HashMap();
        Iterator it = symbolTable.getHashMap().values().iterator();
        while (it.hasNext()) {
            Vector v = (Vector)it.next();
            for (int i = 0; i < v.size(); ++i) {
                SymTabEntry entry = (SymTabEntry)v.elementAt(i);
                if (entry.getName() != null) continue;
                if (entry instanceof TypeEntry) {
                    uniqueNum = this.javifyTypeEntryName(symbolTable, (TypeEntry)entry, anonQNames, uniqueNum);
                    continue;
                }
                entry.setName(this.emitter.getJavaName(entry.getQName()));
            }
        }
    }

    protected int javifyTypeEntryName(SymbolTable symbolTable, TypeEntry entry, HashMap anonQNames, int uniqueNum) {
        TypeEntry base;
        TypeEntry tEntry = entry;
        String dims = tEntry.getDimensions();
        TypeEntry refType = tEntry.getRefType();
        while (refType != null) {
            tEntry = refType;
            dims = dims + tEntry.getDimensions();
            refType = tEntry.getRefType();
        }
        TypeEntry te = tEntry;
        while (te != null && (base = SchemaUtils.getBaseType(te, symbolTable)) != null) {
            uniqueNum = this.javifyTypeEntryName(symbolTable, base, anonQNames, uniqueNum);
            if (Utils.getEnumerationBaseAndValues(te.getNode(), symbolTable) == null && SchemaUtils.getComplexElementExtensionBase(te.getNode(), symbolTable) == null && te.getContainedAttributes() == null && !SchemaUtils.isSimpleTypeWithUnion(te.getNode())) {
                if (base.isSimpleType()) {
                    te.setSimpleType(true);
                    te.setName(base.getName());
                    te.setRefType(base);
                }
                if (base.isBaseType()) {
                    te.setBaseType(true);
                    te.setName(base.getName());
                    te.setRefType(base);
                }
            }
            if (!te.isSimpleType()) break;
            te = base;
        }
        if (tEntry.getName() == null) {
            Vector attributes;
            Vector elements;
            Class class1;
            boolean processed = false;
            QName typeQName = tEntry.getQName();
            QName itemType = SchemaUtils.getListItemType(tEntry.getNode());
            if (itemType != null) {
                TypeEntry itemEntry = symbolTable.getTypeEntry(itemType, false);
                this.javifyTypeEntryName(symbolTable, itemEntry, anonQNames, uniqueNum);
                TypeEntry refedEntry = itemEntry.getRefType();
                QName baseName = refedEntry == null ? itemEntry.getQName() : refedEntry.getQName();
                typeQName = new QName(baseName.getNamespaceURI(), baseName.getLocalPart() + "[]");
            }
            if (this.emitter.isDeploy() && (class1 = (Class)this.emitter.getQName2ClassMap().get(typeQName)) != null && !class1.isArray()) {
                tEntry.setName(JavaGeneratorFactory.getJavaClassName(class1));
                processed = true;
            }
            if (!processed) {
                if (typeQName.getLocalPart().indexOf(">") < 0) {
                    tEntry.setName(this.emitter.getJavaName(typeQName));
                } else {
                    int aidx;
                    String localName = typeQName.getLocalPart();
                    StringBuffer sb = new StringBuffer(localName);
                    while ((aidx = sb.toString().indexOf(">")) > -1) {
                        sb.replace(aidx, aidx + ">".length(), "");
                        char c = sb.charAt(aidx);
                        if (!Character.isLetter(c) || !Character.isLowerCase(c)) continue;
                        sb.setCharAt(aidx, Character.toUpperCase(c));
                    }
                    localName = sb.toString();
                    typeQName = new QName(typeQName.getNamespaceURI(), localName);
                    if (this.emitter.isTypeCollisionProtection() && !this.emitter.getNamespaceExcludes().contains(new NamespaceSelector(typeQName.getNamespaceURI()))) {
                        if (symbolTable.getType(typeQName) != null || anonQNames.get(typeQName) != null) {
                            localName = localName + "Type" + uniqueNum++;
                            typeQName = new QName(typeQName.getNamespaceURI(), localName);
                        }
                        anonQNames.put(typeQName, typeQName);
                    }
                    tEntry.setName(this.emitter.getJavaName(typeQName));
                }
            }
            if ((elements = tEntry.getContainedElements()) != null) {
                for (int i = 0; i < elements.size(); ++i) {
                    ElementDecl elem = (ElementDecl)elements.get(i);
                    String varName = this.emitter.getJavaVariableName(typeQName, elem.getQName(), true);
                    elem.setName(varName);
                }
            }
            if ((attributes = tEntry.getContainedAttributes()) != null) {
                for (int i = 0; i < attributes.size(); ++i) {
                    ContainedAttribute attr = (ContainedAttribute)attributes.get(i);
                    String varName = this.emitter.getJavaVariableName(typeQName, attr.getQName(), false);
                    attr.setName(varName);
                }
            }
        }
        entry.setName(tEntry.getName() + dims);
        return uniqueNum;
    }

    private static String getJavaClassName(Class clazz) {
        Class<?> class1 = clazz;
        while (class1.isArray()) {
            class1 = class1.getComponentType();
        }
        String name = class1.getName();
        name.replace('$', '.');
        return name;
    }

    private void setFaultContext(SymbolTable symbolTable) {
        Iterator it = symbolTable.getHashMap().values().iterator();
        while (it.hasNext()) {
            Vector v = (Vector)it.next();
            for (int i = 0; i < v.size(); ++i) {
                SymTabEntry entry = (SymTabEntry)v.elementAt(i);
                if (!(entry instanceof BindingEntry)) continue;
                BindingEntry bEntry = (BindingEntry)entry;
                HashMap allOpFaults = bEntry.getFaults();
                Iterator ops = allOpFaults.values().iterator();
                while (ops.hasNext()) {
                    ArrayList faults = (ArrayList)ops.next();
                    for (int j = 0; j < faults.size(); ++j) {
                        FaultInfo info = (FaultInfo)faults.get(j);
                        this.setFaultContext(info, symbolTable);
                    }
                }
            }
        }
    }

    private void setFaultContext(FaultInfo fault, SymbolTable symbolTable) {
        MessageEntry me;
        QName faultXmlType = null;
        Vector parts = new Vector();
        try {
            symbolTable.getParametersFromParts(parts, fault.getMessage().getOrderedParts(null), false, fault.getName(), null);
        }
        catch (IOException e) {
            // empty catch block
        }
        String exceptionClassName = null;
        for (int j = 0; j < parts.size(); ++j) {
            TypeEntry te = ((Parameter)parts.elementAt(j)).getType();
            TypeEntry elementTE = null;
            if (te instanceof Element) {
                elementTE = te;
                te = te.getRefType();
            }
            faultXmlType = te.getQName();
            if (te.getBaseType() != null || te.isSimpleType() || te.getDimensions().length() > 0 && te.getRefType().getBaseType() != null) continue;
            Boolean isComplexFault = (Boolean)te.getDynamicVar(COMPLEX_TYPE_FAULT);
            if (isComplexFault == null || !isComplexFault.booleanValue()) {
                te.setDynamicVar(COMPLEX_TYPE_FAULT, Boolean.TRUE);
                if (elementTE != null) {
                    te.setDynamicVar(COMPLEX_TYPE_FAULT, Boolean.TRUE);
                }
                HashSet derivedSet = org.apache.axis.wsdl.symbolTable.Utils.getDerivedTypes(te, symbolTable);
                Iterator derivedI = derivedSet.iterator();
                while (derivedI.hasNext()) {
                    TypeEntry derivedTE = (TypeEntry)derivedI.next();
                    derivedTE.setDynamicVar(COMPLEX_TYPE_FAULT, Boolean.TRUE);
                }
                TypeEntry base = SchemaUtils.getComplexElementExtensionBase(te.getNode(), symbolTable);
                while (base != null) {
                    base.setDynamicVar(COMPLEX_TYPE_FAULT, Boolean.TRUE);
                    base = SchemaUtils.getComplexElementExtensionBase(base.getNode(), symbolTable);
                }
            }
            exceptionClassName = te.getName();
        }
        String excName = this.getExceptionJavaNameHook(fault.getMessage().getQName());
        if (excName != null) {
            exceptionClassName = excName;
        }
        if ((me = symbolTable.getMessageEntry(fault.getMessage().getQName())) != null) {
            me.setDynamicVar(EXCEPTION_DATA_TYPE, faultXmlType);
            if (exceptionClassName != null) {
                me.setDynamicVar(COMPLEX_TYPE_FAULT, Boolean.TRUE);
                me.setDynamicVar(EXCEPTION_CLASS_NAME, exceptionClassName);
            } else {
                me.setDynamicVar(EXCEPTION_CLASS_NAME, this.emitter.getJavaName(me.getQName()));
            }
        }
    }

    protected String getExceptionJavaNameHook(QName qname) {
        return null;
    }

    protected void determineInterfaceNames(SymbolTable symbolTable) {
        Iterator it = symbolTable.getHashMap().values().iterator();
        while (it.hasNext()) {
            Vector v = (Vector)it.next();
            for (int i = 0; i < v.size(); ++i) {
                SymTabEntry entry = (SymTabEntry)v.elementAt(i);
                if (entry instanceof BindingEntry) {
                    BindingEntry bEntry = (BindingEntry)entry;
                    PortTypeEntry ptEntry = symbolTable.getPortTypeEntry(bEntry.getBinding().getPortType().getQName());
                    String seiName = this.getServiceEndpointInterfaceJavaNameHook(ptEntry, bEntry);
                    if (seiName == null) {
                        seiName = ptEntry.getName();
                    }
                    bEntry.setDynamicVar(JavaBindingWriter.INTERFACE_NAME, seiName);
                    continue;
                }
                if (!(entry instanceof ServiceEntry)) continue;
                ServiceEntry sEntry = (ServiceEntry)entry;
                String siName = this.getServiceInterfaceJavaNameHook(sEntry);
                if (siName != null) {
                    sEntry.setName(siName);
                }
                Service service = sEntry.getService();
                Map portMap = service.getPorts();
                Iterator portIterator = portMap.values().iterator();
                while (portIterator.hasNext()) {
                    String portName;
                    Port p = (Port)portIterator.next();
                    Binding binding = p.getBinding();
                    BindingEntry bEntry = symbolTable.getBindingEntry(binding.getQName());
                    if (bEntry.getBindingType() != 0 || (portName = this.getPortJavaNameHook(p.getName())) == null) continue;
                    bEntry.setDynamicVar("port name:" + p.getName(), portName);
                }
            }
        }
    }

    protected String getServiceEndpointInterfaceJavaNameHook(PortTypeEntry ptEntry, BindingEntry bEntry) {
        return null;
    }

    protected String getServiceInterfaceJavaNameHook(ServiceEntry sEntry) {
        return null;
    }

    protected String getPortJavaNameHook(String portName) {
        return null;
    }

    protected void resolveNameClashes(SymbolTable symbolTable) {
        HashSet<Type> anonTypes = new HashSet<Type>();
        ArrayList collisionCandidates = new ArrayList();
        ArrayList<String> localParts = new ArrayList<String>();
        Iterator i = symbolTable.getHashMap().keySet().iterator();
        while (i.hasNext()) {
            QName qName = (QName)i.next();
            String localPart = qName.getLocalPart();
            if (localParts.contains(localPart)) continue;
            localParts.add(localPart);
        }
        Map pkg2NamespacesMap = this.emitter.getNamespaces().getPkg2NamespacesMap();
        Iterator i2 = pkg2NamespacesMap.values().iterator();
        while (i2.hasNext()) {
            Vector namespaces = (Vector)i2.next();
            for (int j = 0; j < localParts.size(); ++j) {
                Vector v = new Vector();
                for (int k = 0; k < namespaces.size(); ++k) {
                    QName qName = new QName((String)namespaces.get(k), (String)localParts.get(j));
                    if (symbolTable.getHashMap().get(qName) == null) continue;
                    v.addAll((Vector)symbolTable.getHashMap().get(qName));
                }
                if (v.size() <= 0) continue;
                collisionCandidates.add(v);
            }
        }
        Iterator it = collisionCandidates.iterator();
        while (it.hasNext()) {
            SymTabEntry entry;
            Vector<TypeEntry> v = new Vector<TypeEntry>((Vector)it.next());
            int index = 0;
            while (index < v.size()) {
                if (v.elementAt(index) instanceof MessageEntry) {
                    MessageEntry msgEntry = (MessageEntry)v.elementAt(index);
                    if (msgEntry.getDynamicVar(EXCEPTION_CLASS_NAME) == null) {
                        v.removeElementAt(index);
                        continue;
                    }
                    ++index;
                    continue;
                }
                ++index;
            }
            if (v.size() <= 1) continue;
            boolean resolve = true;
            if (v.size() == 2 && (v.elementAt(0) instanceof Element && v.elementAt(1) instanceof Type || v.elementAt(1) instanceof Element && v.elementAt(0) instanceof Type)) {
                Element e = v.elementAt(0) instanceof Element ? (Element)v.elementAt(0) : (Element)v.elementAt(1);
                BooleanHolder forElement = new BooleanHolder();
                QName eType = Utils.getTypeQName(e.getNode(), forElement, false);
                if (eType != null && eType.equals(e.getQName()) && !forElement.value) {
                    resolve = false;
                }
            }
            if (resolve) {
                resolve = false;
                String name = null;
                for (int i3 = 0; i3 < v.size() && !resolve; ++i3) {
                    entry = (SymTabEntry)v.elementAt(i3);
                    if (entry instanceof MessageEntry || entry instanceof BindingEntry) {
                        String exceptionClassName = (String)entry.getDynamicVar(EXCEPTION_CLASS_NAME);
                        if (exceptionClassName == null) continue;
                        if (name == null) {
                            name = exceptionClassName;
                            continue;
                        }
                        if (!name.equals(exceptionClassName)) continue;
                        resolve = true;
                        continue;
                    }
                    if (name == null) {
                        name = entry.getName();
                        continue;
                    }
                    if (!name.equals(entry.getName())) continue;
                    resolve = true;
                }
            }
            if (!resolve) continue;
            boolean firstType = true;
            for (int i4 = 0; i4 < v.size(); ++i4) {
                BindingEntry bEntry;
                entry = (SymTabEntry)v.elementAt(i4);
                if (entry instanceof Element) {
                    entry.setName(this.mangleName(entry.getName(), ELEMENT_SUFFIX));
                    QName anonQName = new QName(entry.getQName().getNamespaceURI(), ">" + entry.getQName().getLocalPart());
                    Type anonType = symbolTable.getType(anonQName);
                    if (anonType == null) continue;
                    anonType.setName(entry.getName());
                    anonTypes.add(anonType);
                    continue;
                }
                if (entry instanceof TypeEntry) {
                    if (firstType) {
                        firstType = false;
                        Iterator types = symbolTable.getTypeIndex().values().iterator();
                        while (types.hasNext()) {
                            TypeEntry type = (TypeEntry)types.next();
                            if (type == entry || type.getBaseType() != null || !this.sameJavaClass(entry.getName(), type.getName())) continue;
                            v.add(type);
                        }
                    }
                    if (anonTypes.contains(entry)) continue;
                    boolean needResolve = false;
                    for (int j = 0; j < v.size(); ++j) {
                        SymTabEntry e = (SymTabEntry)v.elementAt(j);
                        if (!(e instanceof PortTypeEntry) && !(e instanceof ServiceEntry) && !(e instanceof BindingEntry)) continue;
                        needResolve = true;
                        break;
                    }
                    if (!needResolve) continue;
                    Boolean isComplexTypeFault = (Boolean)entry.getDynamicVar(COMPLEX_TYPE_FAULT);
                    if (isComplexTypeFault != null && isComplexTypeFault.booleanValue()) {
                        entry.setName(this.mangleName(entry.getName(), EXCEPTION_SUFFIX));
                    } else {
                        entry.setName(this.mangleName(entry.getName(), TYPE_SUFFIX));
                    }
                    Map elementIndex = symbolTable.getElementIndex();
                    ArrayList elements = new ArrayList(elementIndex.values());
                    for (int j = 0; j < elementIndex.size(); ++j) {
                        TypeEntry te = (TypeEntry)elements.get(j);
                        TypeEntry ref = te.getRefType();
                        if (ref == null || !entry.getQName().equals(ref.getQName())) continue;
                        te.setName(entry.getName());
                    }
                    if (isComplexTypeFault == null || !isComplexTypeFault.booleanValue()) continue;
                    List messageEntries = symbolTable.getMessageEntries();
                    for (int j = 0; j < messageEntries.size(); ++j) {
                        MessageEntry messageEntry = (MessageEntry)messageEntries.get(j);
                        Boolean isComplexTypeFaultMsg = (Boolean)messageEntry.getDynamicVar(COMPLEX_TYPE_FAULT);
                        if (isComplexTypeFaultMsg == null || !isComplexTypeFaultMsg.booleanValue()) continue;
                        QName exceptionDataType = (QName)messageEntry.getDynamicVar(EXCEPTION_DATA_TYPE);
                        if (!((TypeEntry)entry).getQName().equals(exceptionDataType)) continue;
                        String className = (String)messageEntry.getDynamicVar(EXCEPTION_CLASS_NAME);
                        messageEntry.setDynamicVar(EXCEPTION_CLASS_NAME, className + EXCEPTION_SUFFIX);
                    }
                    continue;
                }
                if (entry instanceof PortTypeEntry) {
                    entry.setName(this.mangleName(entry.getName(), PORT_TYPE_SUFFIX));
                    continue;
                }
                if (entry instanceof ServiceEntry) {
                    entry.setName(this.mangleName(entry.getName(), SERVICE_SUFFIX));
                    continue;
                }
                if (entry instanceof MessageEntry) {
                    Boolean complexTypeFault = (Boolean)entry.getDynamicVar(COMPLEX_TYPE_FAULT);
                    if (complexTypeFault != null && complexTypeFault.booleanValue()) continue;
                    String exceptionClassName = (String)entry.getDynamicVar(EXCEPTION_CLASS_NAME);
                    entry.setDynamicVar(EXCEPTION_CLASS_NAME, exceptionClassName + EXCEPTION_SUFFIX);
                    continue;
                }
                if (!(entry instanceof BindingEntry) || !(bEntry = (BindingEntry)entry).hasLiteral()) continue;
                entry.setName(this.mangleName(entry.getName(), BINDING_SUFFIX));
            }
        }
    }

    private String mangleName(String name, String mangle) {
        int index = name.indexOf("[");
        if (index >= 0) {
            String pre = name.substring(0, index);
            String post = name.substring(index);
            return pre + mangle + post;
        }
        return name + mangle;
    }

    private boolean sameJavaClass(String one, String two) {
        int index1 = one.indexOf("[");
        int index2 = two.indexOf("[");
        if (index1 > 0) {
            one = one.substring(0, index1);
        }
        if (index2 > 0) {
            two = two.substring(0, index2);
        }
        return one.equals(two);
    }

    protected void setAllReferencesToTrue() {
        Iterator it = this.symbolTable.getHashMap().values().iterator();
        while (it.hasNext()) {
            Vector v = (Vector)it.next();
            for (int i = 0; i < v.size(); ++i) {
                SymTabEntry entry = (SymTabEntry)v.elementAt(i);
                if (entry instanceof BindingEntry && ((BindingEntry)entry).getBindingType() != 0) {
                    entry.setIsReferenced(false);
                    continue;
                }
                entry.setIsReferenced(true);
            }
        }
    }

    protected void ignoreNonSOAPBindings(SymbolTable symbolTable) {
        Vector<PortTypeEntry> unusedPortTypes = new Vector<PortTypeEntry>();
        Vector<PortTypeEntry> usedPortTypes = new Vector<PortTypeEntry>();
        Iterator it = symbolTable.getHashMap().values().iterator();
        while (it.hasNext()) {
            Vector v = (Vector)it.next();
            for (int i = 0; i < v.size(); ++i) {
                SymTabEntry entry = (SymTabEntry)v.elementAt(i);
                if (!(entry instanceof BindingEntry)) continue;
                BindingEntry bEntry = (BindingEntry)entry;
                Binding binding = bEntry.getBinding();
                PortType portType = binding.getPortType();
                PortTypeEntry ptEntry = symbolTable.getPortTypeEntry(portType.getQName());
                if (bEntry.getBindingType() == 0) {
                    usedPortTypes.add(ptEntry);
                    if (!unusedPortTypes.contains(ptEntry)) continue;
                    unusedPortTypes.remove(ptEntry);
                    continue;
                }
                bEntry.setIsReferenced(false);
                if (usedPortTypes.contains(ptEntry)) continue;
                unusedPortTypes.add(ptEntry);
            }
        }
        for (int i = 0; i < unusedPortTypes.size(); ++i) {
            PortTypeEntry ptEntry = (PortTypeEntry)unusedPortTypes.get(i);
            ptEntry.setIsReferenced(false);
        }
    }

    protected void constructSignatures(SymbolTable symbolTable) {
        Iterator it = symbolTable.getHashMap().values().iterator();
        while (it.hasNext()) {
            Vector v = (Vector)it.next();
            for (int i = 0; i < v.size(); ++i) {
                SymTabEntry entry = (SymTabEntry)v.elementAt(i);
                if (!(entry instanceof BindingEntry)) continue;
                BindingEntry bEntry = (BindingEntry)entry;
                Binding binding = bEntry.getBinding();
                PortTypeEntry ptEntry = symbolTable.getPortTypeEntry(binding.getPortType().getQName());
                PortType portType = ptEntry.getPortType();
                Iterator operations = portType.getOperations().iterator();
                while (operations.hasNext()) {
                    Operation operation = (Operation)operations.next();
                    String wsdlOpName = operation.getName();
                    OperationType type = operation.getStyle();
                    String javaOpName = this.getOperationJavaNameHook(bEntry, wsdlOpName);
                    if (javaOpName == null) {
                        javaOpName = operation.getName();
                    }
                    Parameters parameters = bEntry.getParameters(operation);
                    if (OperationType.SOLICIT_RESPONSE.equals(type)) {
                        parameters.signature = "    // " + Messages.getMessage("invalidSolResp00", javaOpName);
                        System.err.println(Messages.getMessage("invalidSolResp00", javaOpName));
                        continue;
                    }
                    if (OperationType.NOTIFICATION.equals(type)) {
                        parameters.signature = "    // " + Messages.getMessage("invalidNotif00", javaOpName);
                        System.err.println(Messages.getMessage("invalidNotif00", javaOpName));
                        continue;
                    }
                    if (parameters == null) continue;
                    String returnType = this.getReturnTypeJavaNameHook(bEntry, wsdlOpName);
                    if (returnType != null && parameters.returnParam != null) {
                        parameters.returnParam.getType().setName(returnType);
                    }
                    for (int j = 0; j < parameters.list.size(); ++j) {
                        Parameter p = (Parameter)parameters.list.get(j);
                        String paramType = this.getParameterTypeJavaNameHook(bEntry, wsdlOpName, j);
                        if (paramType == null) continue;
                        p.getType().setName(paramType);
                    }
                    parameters.signature = this.constructSignature(parameters, javaOpName);
                }
            }
        }
    }

    protected String getOperationJavaNameHook(BindingEntry bEntry, String wsdlOpName) {
        return null;
    }

    protected String getReturnTypeJavaNameHook(BindingEntry bEntry, String wsdlOpName) {
        return null;
    }

    protected String getParameterTypeJavaNameHook(BindingEntry bEntry, String wsdlOpName, int pos) {
        return null;
    }

    private String constructSignature(Parameters parms, String opName) {
        String name = Utils.xmlNameToJava(opName);
        String ret = "void";
        if (parms != null && parms.returnParam != null) {
            ret = Utils.getParameterTypeName(parms.returnParam);
        }
        String signature = "    public " + ret + " " + name + "(";
        boolean needComma = false;
        for (int i = 0; parms != null && i < parms.list.size(); ++i) {
            Parameter p = (Parameter)parms.list.get(i);
            if (needComma) {
                signature = signature + ", ";
            } else {
                needComma = true;
            }
            String javifiedName = Utils.xmlNameToJava(p.getName());
            signature = p.getMode() == 1 ? signature + Utils.getParameterTypeName(p) + " " + javifiedName : signature + Utils.holder(p, this.emitter) + " " + javifiedName;
        }
        signature = signature + ") throws java.rmi.RemoteException";
        if (parms != null && parms.faults != null) {
            Iterator i = parms.faults.values().iterator();
            while (i.hasNext()) {
                Fault fault = (Fault)i.next();
                String exceptionName = Utils.getFullExceptionName(fault.getMessage(), this.symbolTable);
                if (exceptionName == null) continue;
                signature = signature + ", " + exceptionName;
            }
        }
        return signature;
    }

    protected void determineIfHoldersNeeded(SymbolTable symbolTable) {
        Iterator it = symbolTable.getHashMap().values().iterator();
        while (it.hasNext()) {
            Vector v = (Vector)it.next();
            for (int i = 0; i < v.size(); ++i) {
                if (!(v.get(i) instanceof BindingEntry)) continue;
                BindingEntry bEntry = (BindingEntry)v.get(i);
                Iterator operations = bEntry.getParameters().values().iterator();
                while (operations.hasNext()) {
                    Parameters parms = (Parameters)operations.next();
                    for (int j = 0; j < parms.list.size(); ++j) {
                        Type anonType;
                        QName anonQName;
                        Parameter p = (Parameter)parms.list.get(j);
                        if (p.getMode() == 1) continue;
                        TypeEntry typeEntry = p.getType();
                        typeEntry.setDynamicVar("Holder is needed", Boolean.TRUE);
                        if (!typeEntry.isSimpleType() && typeEntry.getRefType() != null) {
                            typeEntry.getRefType().setDynamicVar("Holder is needed", Boolean.TRUE);
                        }
                        if ((anonQName = SchemaUtils.getElementAnonQName(p.getType().getNode())) == null || (anonType = symbolTable.getType(anonQName)) == null) continue;
                        anonType.setDynamicVar("Holder is needed", Boolean.TRUE);
                    }
                }
            }
        }
    }

    public void setBaseTypeMapping(BaseTypeMapping btm) {
        this.btm = btm;
    }

    public BaseTypeMapping getBaseTypeMapping() {
        if (this.btm == null) {
            this.btm = new BaseTypeMapping(){

                public String getBaseName(QName qNameIn) {
                    QName qName = new QName(qNameIn.getNamespaceURI(), qNameIn.getLocalPart());
                    Class cls = JavaGeneratorFactory.this.emitter.getDefaultTypeMapping().getClassForQName(qName);
                    if (cls == null) {
                        return null;
                    }
                    return JavaUtils.getTextClassName(cls.getName());
                }
            };
        }
        return this.btm;
    }

    protected boolean include(QName qName) {
        String namespace = qName != null && qName.getNamespaceURI() != null ? qName.getNamespaceURI() : "";
        boolean doInclude = false;
        NamespaceSelector selector = new NamespaceSelector(namespace);
        if (qName == null || this.emitter == null || this.emitter.getNamespaceIncludes().contains(selector) || this.emitter.getNamespaceIncludes().size() == 0 && !this.emitter.getNamespaceExcludes().contains(selector)) {
            doInclude = true;
        } else {
            log_.info((Object)("excluding code generation for non-included QName:" + qName));
        }
        return doInclude;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    protected class Writers
    implements Generator {
        Vector writers = new Vector();
        SymbolTable symbolTable = null;
        Generator baseWriter = null;
        SymTabEntry entry = null;
        Definition def = null;

        protected Writers() {
        }

        public void addGenerator(Class writer) {
            this.writers.add(writer);
        }

        public void addStuff(Generator baseWriter, SymTabEntry entry, SymbolTable symbolTable) {
            this.baseWriter = baseWriter;
            this.entry = entry;
            this.symbolTable = symbolTable;
        }

        public void addStuff(Generator baseWriter, Definition def, SymbolTable symbolTable) {
            this.baseWriter = baseWriter;
            this.def = def;
            this.symbolTable = symbolTable;
        }

        public void generate() throws IOException {
            Object[] actualArgs;
            Class[] formalArgs;
            if (this.baseWriter != null) {
                this.baseWriter.generate();
            }
            if (this.entry != null) {
                formalArgs = new Class[]{class$org$apache$axis$wsdl$toJava$Emitter == null ? (class$org$apache$axis$wsdl$toJava$Emitter = JavaGeneratorFactory.class$("org.apache.axis.wsdl.toJava.Emitter")) : class$org$apache$axis$wsdl$toJava$Emitter, this.entry.getClass(), class$org$apache$axis$wsdl$symbolTable$SymbolTable == null ? (class$org$apache$axis$wsdl$symbolTable$SymbolTable = JavaGeneratorFactory.class$("org.apache.axis.wsdl.symbolTable.SymbolTable")) : class$org$apache$axis$wsdl$symbolTable$SymbolTable};
                actualArgs = new Object[]{JavaGeneratorFactory.this.emitter, this.entry, this.symbolTable};
            } else {
                formalArgs = new Class[]{class$org$apache$axis$wsdl$toJava$Emitter == null ? (class$org$apache$axis$wsdl$toJava$Emitter = JavaGeneratorFactory.class$("org.apache.axis.wsdl.toJava.Emitter")) : class$org$apache$axis$wsdl$toJava$Emitter, class$javax$wsdl$Definition == null ? (class$javax$wsdl$Definition = JavaGeneratorFactory.class$("javax.wsdl.Definition")) : class$javax$wsdl$Definition, class$org$apache$axis$wsdl$symbolTable$SymbolTable == null ? (class$org$apache$axis$wsdl$symbolTable$SymbolTable = JavaGeneratorFactory.class$("org.apache.axis.wsdl.symbolTable.SymbolTable")) : class$org$apache$axis$wsdl$symbolTable$SymbolTable};
                actualArgs = new Object[]{JavaGeneratorFactory.this.emitter, this.def, this.symbolTable};
            }
            for (int i = 0; i < this.writers.size(); ++i) {
                Generator gen;
                Class wClass = (Class)this.writers.get(i);
                try {
                    Constructor ctor = wClass.getConstructor(formalArgs);
                    gen = (Generator)ctor.newInstance(actualArgs);
                }
                catch (Throwable t) {
                    throw new IOException(Messages.getMessage("exception01", t.getMessage()));
                }
                gen.generate();
            }
        }
    }
}

