/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.rpc;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

public interface Call {
    public static final String USERNAME_PROPERTY = "javax.xml.rpc.security.auth.username";
    public static final String PASSWORD_PROPERTY = "javax.xml.rpc.security.auth.password";
    public static final String OPERATION_STYLE_PROPERTY = "javax.xml.rpc.soap.operation.style";
    public static final String SOAPACTION_USE_PROPERTY = "javax.xml.rpc.soap.http.soapaction.use";
    public static final String SOAPACTION_URI_PROPERTY = "javax.xml.rpc.soap.http.soapaction.uri";
    public static final String ENCODINGSTYLE_URI_PROPERTY = "javax.xml.rpc.encodingstyle.namespace.uri";
    public static final String SESSION_MAINTAIN_PROPERTY = "javax.xml.rpc.session.maintain";

    public boolean isParameterAndReturnSpecRequired(QName var1);

    public void addParameter(String var1, QName var2, ParameterMode var3);

    public void addParameter(String var1, QName var2, Class var3, ParameterMode var4);

    public QName getParameterTypeByName(String var1);

    public void setReturnType(QName var1);

    public void setReturnType(QName var1, Class var2);

    public QName getReturnType();

    public void removeAllParameters();

    public QName getOperationName();

    public void setOperationName(QName var1);

    public QName getPortTypeName();

    public void setPortTypeName(QName var1);

    public void setTargetEndpointAddress(String var1);

    public String getTargetEndpointAddress();

    public void setProperty(String var1, Object var2);

    public Object getProperty(String var1);

    public void removeProperty(String var1);

    public Iterator getPropertyNames();

    public Object invoke(Object[] var1) throws RemoteException;

    public Object invoke(QName var1, Object[] var2) throws RemoteException;

    public void invokeOneWay(Object[] var1);

    public Map getOutputParams();

    public List getOutputValues();
}

