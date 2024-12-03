/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Binding
 *  javax.wsdl.Fault
 *  javax.wsdl.Operation
 *  javax.wsdl.OperationType
 *  javax.wsdl.Port
 *  javax.wsdl.PortType
 */
package org.apache.axis.wsdl.toJava;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import javax.wsdl.Binding;
import javax.wsdl.Fault;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.xml.rpc.holders.BooleanHolder;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.ServiceEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaClassWriter;
import org.apache.axis.wsdl.toJava.Utils;

public class JavaTestCaseWriter
extends JavaClassWriter {
    private ServiceEntry sEntry;
    private SymbolTable symbolTable;
    private int counter = 1;
    static /* synthetic */ Class class$javax$xml$rpc$ServiceException;

    protected JavaTestCaseWriter(Emitter emitter, ServiceEntry sEntry, SymbolTable symbolTable) {
        super(emitter, sEntry.getName() + "TestCase", "testCase");
        this.sEntry = sEntry;
        this.symbolTable = symbolTable;
    }

    protected String getExtendsText() {
        return "extends junit.framework.TestCase ";
    }

    protected void writeFileBody(PrintWriter pw) throws IOException {
        pw.print("    public ");
        pw.print(this.getClassName());
        pw.println("(java.lang.String name) {");
        pw.println("        super(name);");
        pw.println("    }");
        pw.println("");
        Map portMap = this.sEntry.getService().getPorts();
        Iterator portIterator = portMap.values().iterator();
        while (portIterator.hasNext()) {
            Port p = (Port)portIterator.next();
            Binding binding = p.getBinding();
            BindingEntry bEntry = this.symbolTable.getBindingEntry(binding.getQName());
            if (bEntry.getBindingType() != 0) continue;
            String portName = p.getName();
            if (!JavaUtils.isJavaId(portName)) {
                portName = Utils.xmlNameToJavaClass(portName);
            }
            pw.println("    public void test" + portName + "WSDL() throws Exception {");
            pw.println("        javax.xml.rpc.ServiceFactory serviceFactory = javax.xml.rpc.ServiceFactory.newInstance();");
            pw.println("        java.net.URL url = new java.net.URL(new " + this.sEntry.getName() + "Locator" + "().get" + portName + "Address() + \"?WSDL\");");
            pw.println("        javax.xml.rpc.Service service = serviceFactory.createService(url, new " + this.sEntry.getName() + "Locator().getServiceName());");
            pw.println("        assertTrue(service != null);");
            pw.println("    }");
            pw.println("");
            PortType portType = binding.getPortType();
            this.writeComment(pw, p.getDocumentationElement(), true);
            this.writeServiceTestCode(pw, portName, portType, bEntry);
        }
    }

    protected final void writeServiceTestCode(PrintWriter pw, String portName, PortType portType, BindingEntry bEntry) {
        Iterator ops = portType.getOperations().iterator();
        while (ops.hasNext()) {
            Parameter returnParam;
            Operation op = (Operation)ops.next();
            OperationType type = op.getStyle();
            Parameters params = bEntry.getParameters(op);
            BooleanHolder bThrow = new BooleanHolder(false);
            if (OperationType.NOTIFICATION.equals(type) || OperationType.SOLICIT_RESPONSE.equals(type)) {
                pw.println("    " + params.signature);
                continue;
            }
            String javaOpName = Utils.xmlNameToJavaClass(op.getName());
            String testMethodName = "test" + this.counter++ + portName + javaOpName;
            pw.println("    public void " + testMethodName + "() throws Exception {");
            String bindingType = bEntry.getName() + "Stub";
            this.writeBindingAssignment(pw, bindingType, portName);
            pw.println("        // Test operation");
            String indent = "";
            Map faultMap = op.getFaults();
            if (faultMap != null && faultMap.size() > 0) {
                pw.println("        try {");
                indent = "    ";
            }
            if ((returnParam = params.returnParam) != null) {
                TypeEntry returnType = returnParam.getType();
                pw.print("        " + indent);
                pw.print(Utils.getParameterTypeName(returnParam));
                pw.print(" value = ");
                if (returnParam.getMIMEInfo() == null && !returnParam.isOmittable() && Utils.isPrimitiveType(returnType)) {
                    if ("boolean".equals(returnType.getName())) {
                        pw.println("false;");
                    } else {
                        pw.println("-3;");
                    }
                } else {
                    pw.println("null;");
                }
            }
            pw.print("        " + indent);
            if (returnParam != null) {
                pw.print("value = ");
            }
            pw.print("binding.");
            pw.print(Utils.xmlNameToJava(op.getName()));
            pw.print("(");
            Iterator iparam = params.list.iterator();
            boolean isFirst = true;
            while (iparam.hasNext()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    pw.print(", ");
                }
                Parameter param = (Parameter)iparam.next();
                String suffix = "";
                if (param.getMode() != 1) {
                    pw.print("new " + Utils.holder(param, this.emitter) + "(");
                    suffix = ")";
                }
                if (param.getMode() != 2) {
                    String constructorString = Utils.getConstructorForParam(param, this.symbolTable, bThrow);
                    pw.print(constructorString);
                }
                pw.print(suffix);
            }
            pw.println(");");
            if (faultMap != null && faultMap.size() > 0) {
                pw.println("        }");
            }
            if (faultMap != null) {
                Iterator i = faultMap.values().iterator();
                int count = 0;
                while (i.hasNext()) {
                    Fault f = (Fault)i.next();
                    pw.print("        catch (");
                    pw.print(Utils.getFullExceptionName(f.getMessage(), this.symbolTable));
                    pw.println(" e" + ++count + ") {");
                    pw.print("            ");
                    pw.println("throw new junit.framework.AssertionFailedError(\"" + f.getName() + " Exception caught: \" + e" + count + ");");
                    pw.println("        }");
                }
            }
            pw.println("        " + indent + "// TBD - validate results");
            pw.println("    }");
            pw.println();
        }
    }

    public final void writeBindingAssignment(PrintWriter pw, String bindingType, String portName) {
        pw.println("        " + bindingType + " binding;");
        pw.println("        try {");
        pw.println("            binding = (" + bindingType + ")");
        pw.print("                          new " + this.sEntry.getName());
        pw.println("Locator().get" + portName + "();");
        pw.println("        }");
        pw.println("        catch (" + (class$javax$xml$rpc$ServiceException == null ? (class$javax$xml$rpc$ServiceException = JavaTestCaseWriter.class$("javax.xml.rpc.ServiceException")) : class$javax$xml$rpc$ServiceException).getName() + " jre) {");
        pw.println("            if(jre.getLinkedCause()!=null)");
        pw.println("                jre.getLinkedCause().printStackTrace();");
        pw.println("            throw new junit.framework.AssertionFailedError(\"JAX-RPC ServiceException caught: \" + jre);");
        pw.println("        }");
        pw.println("        assertNotNull(\"" + Messages.getMessage("null00", "binding") + "\", binding);");
        pw.println();
        pw.println("        // Time out after a minute");
        pw.println("        binding.setTimeout(60000);");
        pw.println();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

