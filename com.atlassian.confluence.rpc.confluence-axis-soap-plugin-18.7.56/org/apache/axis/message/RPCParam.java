/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import org.apache.axis.Constants;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.constants.Style;
import org.apache.axis.description.ParameterDesc;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.message.MessageElement;
import org.apache.axis.message.RPCElement;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;

public class RPCParam
extends MessageElement
implements Serializable {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$message$RPCParam == null ? (class$org$apache$axis$message$RPCParam = RPCParam.class$("org.apache.axis.message.RPCParam")) : class$org$apache$axis$message$RPCParam).getName());
    private Object value = null;
    private int countSetCalls = 0;
    private ParameterDesc paramDesc;
    private Boolean wantXSIType = null;
    private static Method valueSetMethod;
    static /* synthetic */ Class class$org$apache$axis$message$RPCParam;
    static /* synthetic */ Class class$java$lang$Object;

    public RPCParam(String name, Object value) {
        this(new QName("", name), value);
    }

    public RPCParam(QName qname, Object value) {
        super(qname);
        if (value instanceof String) {
            try {
                this.addTextNode((String)value);
            }
            catch (SOAPException e) {
                throw new RuntimeException(Messages.getMessage("cannotCreateTextNode00"));
            }
        } else {
            this.value = value;
        }
    }

    public RPCParam(String namespace, String name, Object value) {
        this(new QName(namespace, name), value);
    }

    public void setRPCCall(RPCElement call) {
        this.parent = call;
    }

    public Object getObjectValue() {
        return this.value;
    }

    public void setObjectValue(Object value) {
        this.value = value;
    }

    public void set(Object newValue) {
        ++this.countSetCalls;
        if (this.countSetCalls == 1) {
            this.value = newValue;
            return;
        }
        if (this.countSetCalls == 2) {
            ArrayList<Object> list = new ArrayList<Object>();
            list.add(this.value);
            this.value = list;
        }
        ((ArrayList)this.value).add(newValue);
    }

    public static Method getValueSetMethod() {
        return valueSetMethod;
    }

    public ParameterDesc getParamDesc() {
        return this.paramDesc;
    }

    public void setParamDesc(ParameterDesc paramDesc) {
        this.paramDesc = paramDesc;
    }

    public void setXSITypeGeneration(Boolean value) {
        this.wantXSIType = value;
    }

    public Boolean getXSITypeGeneration() {
        return this.wantXSIType;
    }

    public void serialize(SerializationContext context) throws IOException {
        Class javaType = this.value == null ? null : this.value.getClass();
        QName xmlType = null;
        if (this.paramDesc != null) {
            MessageContext mc;
            Class clazz;
            if (javaType == null) {
                javaType = this.paramDesc.getJavaType() != null ? this.paramDesc.getJavaType() : javaType;
            } else if (!(javaType.equals(this.paramDesc.getJavaType()) || (clazz = JavaUtils.getPrimitiveClass(javaType)) != null && clazz.equals(this.paramDesc.getJavaType()) || javaType.equals(JavaUtils.getHolderValueType(this.paramDesc.getJavaType())))) {
                this.wantXSIType = Boolean.TRUE;
            }
            xmlType = this.paramDesc.getTypeQName();
            QName itemQName = this.paramDesc.getItemQName();
            if (itemQName == null && (mc = context.getMessageContext()) != null && mc.getOperation() != null && mc.getOperation().getStyle() == Style.DOCUMENT) {
                itemQName = Constants.QNAME_LITERAL_ITEM;
            }
            context.setItemQName(itemQName);
            QName itemType = this.paramDesc.getItemType();
            context.setItemType(itemType);
        }
        context.serialize(this.getQName(), (Attributes)null, this.value, xmlType, Boolean.TRUE, this.wantXSIType);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if (this.getQName() == null) {
            out.writeBoolean(false);
        } else {
            out.writeBoolean(true);
            out.writeObject(this.getQName().getNamespaceURI());
            out.writeObject(this.getQName().getLocalPart());
        }
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if (in.readBoolean()) {
            this.setQName(new QName((String)in.readObject(), (String)in.readObject()));
        }
        in.defaultReadObject();
    }

    protected void outputImpl(SerializationContext context) throws Exception {
        this.serialize(context);
    }

    public String getValue() {
        return this.getValueDOM();
    }

    public SOAPElement addTextNode(String s) throws SOAPException {
        this.value = s;
        return super.addTextNode(s);
    }

    public void setValue(String value) {
        this.value = value;
        super.setValue(value);
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
        Class cls = class$org$apache$axis$message$RPCParam == null ? (class$org$apache$axis$message$RPCParam = RPCParam.class$("org.apache.axis.message.RPCParam")) : class$org$apache$axis$message$RPCParam;
        try {
            valueSetMethod = cls.getMethod("set", class$java$lang$Object == null ? (class$java$lang$Object = RPCParam.class$("java.lang.Object")) : class$java$lang$Object);
        }
        catch (NoSuchMethodException e) {
            log.error((Object)Messages.getMessage("noValue00", "" + e));
            throw new RuntimeException(e.getMessage());
        }
    }
}

