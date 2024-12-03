/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.Nullable
 *  javax.activation.DataSource
 *  javax.xml.bind.JAXBContext
 *  javax.xml.soap.SOAPMessage
 *  javax.xml.ws.Dispatch
 *  javax.xml.ws.Service$Mode
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.api.pipe;

import com.sun.istack.Nullable;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.WSService;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.client.WSPortInfo;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.SEIModel;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.binding.BindingImpl;
import com.sun.xml.ws.client.WSServiceDelegate;
import com.sun.xml.ws.client.dispatch.DataSourceDispatch;
import com.sun.xml.ws.client.dispatch.DispatchImpl;
import com.sun.xml.ws.client.dispatch.JAXBDispatch;
import com.sun.xml.ws.client.dispatch.MessageDispatch;
import com.sun.xml.ws.client.dispatch.PacketDispatch;
import com.sun.xml.ws.client.dispatch.SOAPMessageDispatch;
import com.sun.xml.ws.client.sei.SEIStub;
import com.sun.xml.ws.developer.WSBindingProvider;
import com.sun.xml.ws.model.SOAPSEIModel;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import javax.activation.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceException;

public abstract class Stubs {
    private Stubs() {
    }

    @Deprecated
    public static Dispatch<SOAPMessage> createSAAJDispatch(QName portName, WSService owner, WSBinding binding, Service.Mode mode, Tube next, @Nullable WSEndpointReference epr) {
        DispatchImpl.checkValidSOAPMessageDispatch(binding, mode);
        return new SOAPMessageDispatch(portName, mode, (WSServiceDelegate)owner, next, (BindingImpl)binding, epr);
    }

    public static Dispatch<SOAPMessage> createSAAJDispatch(WSPortInfo portInfo, WSBinding binding, Service.Mode mode, @Nullable WSEndpointReference epr) {
        DispatchImpl.checkValidSOAPMessageDispatch(binding, mode);
        return new SOAPMessageDispatch(portInfo, mode, (BindingImpl)binding, epr);
    }

    @Deprecated
    public static Dispatch<DataSource> createDataSourceDispatch(QName portName, WSService owner, WSBinding binding, Service.Mode mode, Tube next, @Nullable WSEndpointReference epr) {
        DispatchImpl.checkValidDataSourceDispatch(binding, mode);
        return new DataSourceDispatch(portName, mode, (WSServiceDelegate)owner, next, (BindingImpl)binding, epr);
    }

    public static Dispatch<DataSource> createDataSourceDispatch(WSPortInfo portInfo, WSBinding binding, Service.Mode mode, @Nullable WSEndpointReference epr) {
        DispatchImpl.checkValidDataSourceDispatch(binding, mode);
        return new DataSourceDispatch(portInfo, mode, (BindingImpl)binding, epr);
    }

    @Deprecated
    public static Dispatch<Source> createSourceDispatch(QName portName, WSService owner, WSBinding binding, Service.Mode mode, Tube next, @Nullable WSEndpointReference epr) {
        return DispatchImpl.createSourceDispatch(portName, mode, (WSServiceDelegate)owner, next, (BindingImpl)binding, epr);
    }

    public static Dispatch<Source> createSourceDispatch(WSPortInfo portInfo, WSBinding binding, Service.Mode mode, @Nullable WSEndpointReference epr) {
        return DispatchImpl.createSourceDispatch(portInfo, mode, (BindingImpl)binding, epr);
    }

    public static <T> Dispatch<T> createDispatch(QName portName, WSService owner, WSBinding binding, Class<T> clazz, Service.Mode mode, Tube next, @Nullable WSEndpointReference epr) {
        if (clazz == SOAPMessage.class) {
            return Stubs.createSAAJDispatch(portName, owner, binding, mode, next, epr);
        }
        if (clazz == Source.class) {
            return Stubs.createSourceDispatch(portName, owner, binding, mode, next, epr);
        }
        if (clazz == DataSource.class) {
            return Stubs.createDataSourceDispatch(portName, owner, binding, mode, next, epr);
        }
        if (clazz == Message.class) {
            if (mode == Service.Mode.MESSAGE) {
                return Stubs.createMessageDispatch(portName, owner, binding, next, epr);
            }
            throw new WebServiceException(mode + " not supported with Dispatch<Message>");
        }
        if (clazz == Packet.class) {
            return Stubs.createPacketDispatch(portName, owner, binding, next, epr);
        }
        throw new WebServiceException("Unknown class type " + clazz.getName());
    }

    public static <T> Dispatch<T> createDispatch(WSPortInfo portInfo, WSService owner, WSBinding binding, Class<T> clazz, Service.Mode mode, @Nullable WSEndpointReference epr) {
        if (clazz == SOAPMessage.class) {
            return Stubs.createSAAJDispatch(portInfo, binding, mode, epr);
        }
        if (clazz == Source.class) {
            return Stubs.createSourceDispatch(portInfo, binding, mode, epr);
        }
        if (clazz == DataSource.class) {
            return Stubs.createDataSourceDispatch(portInfo, binding, mode, epr);
        }
        if (clazz == Message.class) {
            if (mode == Service.Mode.MESSAGE) {
                return Stubs.createMessageDispatch(portInfo, binding, epr);
            }
            throw new WebServiceException(mode + " not supported with Dispatch<Message>");
        }
        if (clazz == Packet.class) {
            if (mode == Service.Mode.MESSAGE) {
                return Stubs.createPacketDispatch(portInfo, binding, epr);
            }
            throw new WebServiceException(mode + " not supported with Dispatch<Packet>");
        }
        throw new WebServiceException("Unknown class type " + clazz.getName());
    }

    @Deprecated
    public static Dispatch<Object> createJAXBDispatch(QName portName, WSService owner, WSBinding binding, JAXBContext jaxbContext, Service.Mode mode, Tube next, @Nullable WSEndpointReference epr) {
        return new JAXBDispatch(portName, jaxbContext, mode, (WSServiceDelegate)owner, next, (BindingImpl)binding, epr);
    }

    public static Dispatch<Object> createJAXBDispatch(WSPortInfo portInfo, WSBinding binding, JAXBContext jaxbContext, Service.Mode mode, @Nullable WSEndpointReference epr) {
        return new JAXBDispatch(portInfo, jaxbContext, mode, (BindingImpl)binding, epr);
    }

    @Deprecated
    public static Dispatch<Message> createMessageDispatch(QName portName, WSService owner, WSBinding binding, Tube next, @Nullable WSEndpointReference epr) {
        return new MessageDispatch(portName, (WSServiceDelegate)owner, next, (BindingImpl)binding, epr);
    }

    public static Dispatch<Message> createMessageDispatch(WSPortInfo portInfo, WSBinding binding, @Nullable WSEndpointReference epr) {
        return new MessageDispatch(portInfo, (BindingImpl)binding, epr);
    }

    public static Dispatch<Packet> createPacketDispatch(QName portName, WSService owner, WSBinding binding, Tube next, @Nullable WSEndpointReference epr) {
        return new PacketDispatch(portName, (WSServiceDelegate)owner, next, (BindingImpl)binding, epr);
    }

    public static Dispatch<Packet> createPacketDispatch(WSPortInfo portInfo, WSBinding binding, @Nullable WSEndpointReference epr) {
        return new PacketDispatch(portInfo, (BindingImpl)binding, epr);
    }

    public <T> T createPortProxy(WSService service, WSBinding binding, SEIModel model, Class<T> portInterface, Tube next, @Nullable WSEndpointReference epr) {
        SEIStub ps = new SEIStub((WSServiceDelegate)service, (BindingImpl)binding, (SOAPSEIModel)model, next, epr);
        return portInterface.cast(Proxy.newProxyInstance(portInterface.getClassLoader(), new Class[]{portInterface, WSBindingProvider.class}, (InvocationHandler)ps));
    }

    public <T> T createPortProxy(WSPortInfo portInfo, WSBinding binding, SEIModel model, Class<T> portInterface, @Nullable WSEndpointReference epr) {
        SEIStub ps = new SEIStub(portInfo, (BindingImpl)binding, (SOAPSEIModel)model, epr);
        return portInterface.cast(Proxy.newProxyInstance(portInterface.getClassLoader(), new Class[]{portInterface, WSBindingProvider.class}, (InvocationHandler)ps));
    }
}

