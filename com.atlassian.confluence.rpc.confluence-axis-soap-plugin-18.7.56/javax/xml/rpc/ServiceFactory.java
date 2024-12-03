/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.rpc;

import java.net.URL;
import java.util.Properties;
import javax.xml.namespace.QName;
import javax.xml.rpc.FactoryFinder;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceException;

public abstract class ServiceFactory {
    public static final String SERVICEFACTORY_PROPERTY = "javax.xml.rpc.ServiceFactory";

    protected ServiceFactory() {
    }

    public static ServiceFactory newInstance() throws ServiceException {
        try {
            return (ServiceFactory)FactoryFinder.find(SERVICEFACTORY_PROPERTY, "org.apache.axis.client.ServiceFactory");
        }
        catch (FactoryFinder.ConfigurationError e) {
            throw new ServiceException(e.getException());
        }
    }

    public abstract Service createService(URL var1, QName var2) throws ServiceException;

    public abstract Service createService(QName var1) throws ServiceException;

    public abstract Service loadService(Class var1) throws ServiceException;

    public abstract Service loadService(URL var1, Class var2, Properties var3) throws ServiceException;

    public abstract Service loadService(URL var1, QName var2, Properties var3) throws ServiceException;
}

