/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.OperationType
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.description;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import javax.wsdl.OperationType;
import javax.xml.namespace.QName;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.description.FaultDesc;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.commons.logging.Log;

public class OperationDesc
implements Serializable {
    public static final int MSG_METHOD_BODYARRAY = 1;
    public static final int MSG_METHOD_SOAPENVELOPE = 2;
    public static final int MSG_METHOD_ELEMENTARRAY = 3;
    public static final int MSG_METHOD_DOCUMENT = 4;
    public static final int MSG_METHOD_NONCONFORMING = -4;
    public static Map mepStrings = new HashMap();
    protected static Log log;
    private ServiceDesc parent;
    private ArrayList parameters = new ArrayList();
    private String name;
    private QName elementQName;
    private transient Method method;
    private Style style = null;
    private Use use = null;
    private int numInParams = 0;
    private int numOutParams = 0;
    private String soapAction = null;
    private ArrayList faults = null;
    private ParameterDesc returnDesc = new ParameterDesc();
    private int messageOperationStyle = -1;
    private String documentation = null;
    private OperationType mep = OperationType.REQUEST_RESPONSE;
    static /* synthetic */ Class class$org$apache$axis$description$OperationDesc;

    public OperationDesc() {
        this.returnDesc.setMode((byte)2);
        this.returnDesc.setIsReturn(true);
    }

    public OperationDesc(String name, ParameterDesc[] parameters, QName returnQName) {
        this.name = name;
        this.returnDesc.setQName(returnQName);
        this.returnDesc.setMode((byte)2);
        this.returnDesc.setIsReturn(true);
        for (int i = 0; i < parameters.length; ++i) {
            this.addParameter(parameters[i]);
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocumentation() {
        return this.documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public QName getReturnQName() {
        return this.returnDesc.getQName();
    }

    public void setReturnQName(QName returnQName) {
        this.returnDesc.setQName(returnQName);
    }

    public QName getReturnType() {
        return this.returnDesc.getTypeQName();
    }

    public void setReturnType(QName returnType) {
        log.debug((Object)("@" + Integer.toHexString(this.hashCode()) + "setReturnType(" + returnType + ")"));
        this.returnDesc.setTypeQName(returnType);
    }

    public Class getReturnClass() {
        return this.returnDesc.getJavaType();
    }

    public void setReturnClass(Class returnClass) {
        this.returnDesc.setJavaType(returnClass);
    }

    public QName getElementQName() {
        return this.elementQName;
    }

    public void setElementQName(QName elementQName) {
        this.elementQName = elementQName;
    }

    public ServiceDesc getParent() {
        return this.parent;
    }

    public void setParent(ServiceDesc parent) {
        this.parent = parent;
    }

    public String getSoapAction() {
        return this.soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public Style getStyle() {
        if (this.style == null) {
            if (this.parent != null) {
                return this.parent.getStyle();
            }
            return Style.DEFAULT;
        }
        return this.style;
    }

    public void setUse(Use use) {
        this.use = use;
    }

    public Use getUse() {
        if (this.use == null) {
            if (this.parent != null) {
                return this.parent.getUse();
            }
            return Use.DEFAULT;
        }
        return this.use;
    }

    public void addParameter(ParameterDesc param) {
        param.setOrder(this.getNumParams());
        this.parameters.add(param);
        if (param.getMode() == 1 || param.getMode() == 3) {
            ++this.numInParams;
        }
        if (param.getMode() == 2 || param.getMode() == 3) {
            ++this.numOutParams;
        }
        log.debug((Object)("@" + Integer.toHexString(this.hashCode()) + " added parameter >" + param + "@" + Integer.toHexString(param.hashCode()) + "<total parameters:" + this.getNumParams()));
    }

    public void addParameter(QName paramName, QName xmlType, Class javaType, byte parameterMode, boolean inHeader, boolean outHeader) {
        ParameterDesc param = new ParameterDesc(paramName, parameterMode, xmlType, javaType, inHeader, outHeader);
        this.addParameter(param);
    }

    public ParameterDesc getParameter(int i) {
        if (this.parameters.size() <= i) {
            return null;
        }
        return (ParameterDesc)this.parameters.get(i);
    }

    public ArrayList getParameters() {
        return this.parameters;
    }

    public void setParameters(ArrayList newParameters) {
        this.parameters = new ArrayList();
        this.numInParams = 0;
        this.numOutParams = 0;
        ListIterator li = newParameters.listIterator();
        while (li.hasNext()) {
            this.addParameter((ParameterDesc)li.next());
        }
    }

    public int getNumInParams() {
        return this.numInParams;
    }

    public int getNumOutParams() {
        return this.numOutParams;
    }

    public int getNumParams() {
        return this.parameters.size();
    }

    public Method getMethod() {
        return this.method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public boolean isReturnHeader() {
        return this.returnDesc.isOutHeader();
    }

    public void setReturnHeader(boolean value) {
        this.returnDesc.setOutHeader(value);
    }

    public ParameterDesc getParamByQName(QName qname) {
        Iterator i = this.parameters.iterator();
        while (i.hasNext()) {
            ParameterDesc param = (ParameterDesc)i.next();
            if (!param.getQName().equals(qname)) continue;
            return param;
        }
        return null;
    }

    public ParameterDesc getInputParamByQName(QName qname) {
        ParameterDesc param = null;
        param = this.getParamByQName(qname);
        if (param == null || param.getMode() == 2) {
            param = null;
        }
        return param;
    }

    public ParameterDesc getOutputParamByQName(QName qname) {
        ParameterDesc param = null;
        Iterator i = this.parameters.iterator();
        while (i.hasNext()) {
            ParameterDesc pnext = (ParameterDesc)i.next();
            if (!pnext.getQName().equals(qname) || pnext.getMode() == 1) continue;
            param = pnext;
            break;
        }
        if (param == null) {
            if (null == this.returnDesc.getQName()) {
                param = new ParameterDesc(this.returnDesc);
                param.setQName(qname);
            } else if (qname.equals(this.returnDesc.getQName())) {
                param = this.returnDesc;
            }
        }
        return param;
    }

    public ArrayList getAllInParams() {
        ArrayList<ParameterDesc> result = new ArrayList<ParameterDesc>();
        Iterator i = this.parameters.iterator();
        while (i.hasNext()) {
            ParameterDesc desc = (ParameterDesc)i.next();
            if (desc.getMode() == 2) continue;
            result.add(desc);
        }
        return result;
    }

    public ArrayList getAllOutParams() {
        ArrayList<ParameterDesc> result = new ArrayList<ParameterDesc>();
        Iterator i = this.parameters.iterator();
        while (i.hasNext()) {
            ParameterDesc desc = (ParameterDesc)i.next();
            if (desc.getMode() == 1) continue;
            result.add(desc);
        }
        return result;
    }

    public ArrayList getOutParams() {
        ArrayList<ParameterDesc> result = new ArrayList<ParameterDesc>();
        Iterator i = this.parameters.iterator();
        while (i.hasNext()) {
            ParameterDesc desc = (ParameterDesc)i.next();
            if (desc.getMode() != 2) continue;
            result.add(desc);
        }
        return result;
    }

    public void addFault(FaultDesc fault) {
        if (this.faults == null) {
            this.faults = new ArrayList();
        }
        this.faults.add(fault);
    }

    public ArrayList getFaults() {
        return this.faults;
    }

    public FaultDesc getFaultByClass(Class cls) {
        if (this.faults == null || cls == null) {
            return null;
        }
        while (cls != null) {
            Iterator iterator = this.faults.iterator();
            while (iterator.hasNext()) {
                FaultDesc desc = (FaultDesc)iterator.next();
                if (!cls.getName().equals(desc.getClassName())) continue;
                return desc;
            }
            if ((cls = cls.getSuperclass()) == null || !cls.getName().startsWith("java.") && !cls.getName().startsWith("javax.")) continue;
            cls = null;
        }
        return null;
    }

    public FaultDesc getFaultByClass(Class cls, boolean checkParents) {
        if (checkParents) {
            return this.getFaultByClass(cls);
        }
        if (this.faults == null || cls == null) {
            return null;
        }
        Iterator iterator = this.faults.iterator();
        while (iterator.hasNext()) {
            FaultDesc desc = (FaultDesc)iterator.next();
            if (!cls.getName().equals(desc.getClassName())) continue;
            return desc;
        }
        return null;
    }

    public FaultDesc getFaultByQName(QName qname) {
        if (this.faults != null) {
            Iterator iterator = this.faults.iterator();
            while (iterator.hasNext()) {
                FaultDesc desc = (FaultDesc)iterator.next();
                if (!qname.equals(desc.getQName())) continue;
                return desc;
            }
        }
        return null;
    }

    public FaultDesc getFaultByXmlType(QName xmlType) {
        if (this.faults != null) {
            Iterator iterator = this.faults.iterator();
            while (iterator.hasNext()) {
                FaultDesc desc = (FaultDesc)iterator.next();
                if (!xmlType.equals(desc.getXmlType())) continue;
                return desc;
            }
        }
        return null;
    }

    public ParameterDesc getReturnParamDesc() {
        return this.returnDesc;
    }

    public String toString() {
        return this.toString("");
    }

    public String toString(String indent) {
        int i;
        String text = "";
        text = text + indent + "name:        " + this.getName() + "\n";
        text = text + indent + "returnQName: " + this.getReturnQName() + "\n";
        text = text + indent + "returnType:  " + this.getReturnType() + "\n";
        text = text + indent + "returnClass: " + this.getReturnClass() + "\n";
        text = text + indent + "elementQName:" + this.getElementQName() + "\n";
        text = text + indent + "soapAction:  " + this.getSoapAction() + "\n";
        text = text + indent + "style:       " + this.getStyle().getName() + "\n";
        text = text + indent + "use:         " + this.getUse().getName() + "\n";
        text = text + indent + "numInParams: " + this.getNumInParams() + "\n";
        text = text + indent + "method:" + this.getMethod() + "\n";
        for (i = 0; i < this.parameters.size(); ++i) {
            text = text + indent + " ParameterDesc[" + i + "]:\n";
            text = text + indent + ((ParameterDesc)this.parameters.get(i)).toString("  ") + "\n";
        }
        if (this.faults != null) {
            for (i = 0; i < this.faults.size(); ++i) {
                text = text + indent + " FaultDesc[" + i + "]:\n";
                text = text + indent + ((FaultDesc)this.faults.get(i)).toString("  ") + "\n";
            }
        }
        return text;
    }

    public int getMessageOperationStyle() {
        return this.messageOperationStyle;
    }

    public void setMessageOperationStyle(int messageOperationStyle) {
        this.messageOperationStyle = messageOperationStyle;
    }

    public OperationType getMep() {
        return this.mep;
    }

    public void setMep(OperationType mep) {
        this.mep = mep;
    }

    public void setMep(String mepString) {
        OperationType newMep = (OperationType)mepStrings.get(mepString);
        if (newMep != null) {
            this.mep = newMep;
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (this.method != null) {
            out.writeObject(this.method.getDeclaringClass());
            out.writeObject(this.method.getName());
            out.writeObject(this.method.getParameterTypes());
        } else {
            out.writeObject(null);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        Class clazz = (Class)in.readObject();
        if (clazz != null) {
            String methodName = (String)in.readObject();
            Class[] parameterTypes = (Class[])in.readObject();
            try {
                this.method = clazz.getMethod(methodName, parameterTypes);
            }
            catch (NoSuchMethodException e) {
                throw new IOException("Unable to deserialize the operation's method: " + methodName);
            }
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        mepStrings.put("request-response", OperationType.REQUEST_RESPONSE);
        mepStrings.put("oneway", OperationType.ONE_WAY);
        mepStrings.put("solicit-response", OperationType.SOLICIT_RESPONSE);
        mepStrings.put("notification", OperationType.NOTIFICATION);
        log = LogFactory.getLog((class$org$apache$axis$description$OperationDesc == null ? (class$org$apache$axis$description$OperationDesc = OperationDesc.class$("org.apache.axis.description.OperationDesc")) : class$org$apache$axis$description$OperationDesc).getName());
    }
}

