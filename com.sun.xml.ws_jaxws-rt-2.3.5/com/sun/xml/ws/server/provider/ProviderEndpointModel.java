/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.activation.DataSource
 *  javax.xml.soap.SOAPMessage
 *  javax.xml.ws.Provider
 *  javax.xml.ws.Service$Mode
 *  javax.xml.ws.ServiceMode
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.soap.SOAPBinding
 */
package com.sun.xml.ws.server.provider;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.server.AsyncProvider;
import com.sun.xml.ws.resources.ServerMessages;
import com.sun.xml.ws.spi.db.BindingHelper;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.activation.DataSource;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPBinding;

final class ProviderEndpointModel<T> {
    final boolean isAsync;
    @NotNull
    final Service.Mode mode;
    @NotNull
    final Class datatype;
    @NotNull
    final Class implClass;

    ProviderEndpointModel(Class<T> implementorClass, WSBinding binding) {
        assert (implementorClass != null);
        assert (binding != null);
        this.implClass = implementorClass;
        this.mode = ProviderEndpointModel.getServiceMode(implementorClass);
        Class otherClass = binding instanceof SOAPBinding ? SOAPMessage.class : DataSource.class;
        this.isAsync = AsyncProvider.class.isAssignableFrom(implementorClass);
        Class baseType = this.isAsync ? AsyncProvider.class : Provider.class;
        Type baseParam = BindingHelper.getBaseType(implementorClass, baseType);
        if (baseParam == null) {
            throw new WebServiceException(ServerMessages.NOT_IMPLEMENT_PROVIDER(implementorClass.getName()));
        }
        if (!(baseParam instanceof ParameterizedType)) {
            throw new WebServiceException(ServerMessages.PROVIDER_NOT_PARAMETERIZED(implementorClass.getName()));
        }
        ParameterizedType pt = (ParameterizedType)baseParam;
        Type[] types = pt.getActualTypeArguments();
        if (!(types[0] instanceof Class)) {
            throw new WebServiceException(ServerMessages.PROVIDER_INVALID_PARAMETER_TYPE(implementorClass.getName(), types[0]));
        }
        this.datatype = (Class)types[0];
        if (this.mode == Service.Mode.PAYLOAD && this.datatype != Source.class) {
            throw new IllegalArgumentException("Illeagal combination - Mode.PAYLOAD and Provider<" + otherClass.getName() + ">");
        }
    }

    private static Service.Mode getServiceMode(Class<?> c) {
        ServiceMode mode = c.getAnnotation(ServiceMode.class);
        return mode == null ? Service.Mode.PAYLOAD : mode.value();
    }
}

