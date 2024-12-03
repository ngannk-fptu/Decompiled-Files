/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.rpc;

import java.net.URL;
import java.rmi.Remote;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.encoding.TypeMappingRegistry;
import javax.xml.rpc.handler.HandlerRegistry;

public interface Service {
    public Remote getPort(QName var1, Class var2) throws ServiceException;

    public Remote getPort(Class var1) throws ServiceException;

    public Call[] getCalls(QName var1) throws ServiceException;

    public Call createCall(QName var1) throws ServiceException;

    public Call createCall(QName var1, QName var2) throws ServiceException;

    public Call createCall(QName var1, String var2) throws ServiceException;

    public Call createCall() throws ServiceException;

    public QName getServiceName();

    public Iterator getPorts() throws ServiceException;

    public URL getWSDLDocumentLocation();

    public TypeMappingRegistry getTypeMappingRegistry();

    public HandlerRegistry getHandlerRegistry();
}

