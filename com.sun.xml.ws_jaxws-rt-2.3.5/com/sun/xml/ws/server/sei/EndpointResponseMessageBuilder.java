/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBException
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.server.sei;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.message.jaxb.JAXBMessage;
import com.sun.xml.ws.model.ParameterImpl;
import com.sun.xml.ws.model.WrapperParameter;
import com.sun.xml.ws.server.sei.ValueGetter;
import com.sun.xml.ws.spi.db.BindingContext;
import com.sun.xml.ws.spi.db.DatabindingException;
import com.sun.xml.ws.spi.db.PropertyAccessor;
import com.sun.xml.ws.spi.db.WrapperComposite;
import com.sun.xml.ws.spi.db.XMLBridge;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

public abstract class EndpointResponseMessageBuilder {
    public static final EndpointResponseMessageBuilder EMPTY_SOAP11 = new Empty(SOAPVersion.SOAP_11);
    public static final EndpointResponseMessageBuilder EMPTY_SOAP12 = new Empty(SOAPVersion.SOAP_12);

    public abstract Message createMessage(Object[] var1, Object var2);

    public static final class RpcLit
    extends Wrapped {
        public RpcLit(WrapperParameter wp, SOAPVersion soapVersion) {
            super(wp, soapVersion);
            assert (wp.getTypeInfo().type == WrapperComposite.class);
            this.parameterBridges = new XMLBridge[this.children.size()];
            for (int i = 0; i < this.parameterBridges.length; ++i) {
                this.parameterBridges[i] = ((ParameterImpl)this.children.get(i)).getXMLBridge();
            }
        }

        @Override
        Object build(Object[] methodArgs, Object returnValue) {
            return this.buildWrapperComposite(methodArgs, returnValue);
        }
    }

    public static final class DocLit
    extends Wrapped {
        private final PropertyAccessor[] accessors;
        private final Class wrapper;
        private boolean dynamicWrapper;
        private BindingContext bindingContext;

        public DocLit(WrapperParameter wp, SOAPVersion soapVersion) {
            super(wp, soapVersion);
            this.bindingContext = wp.getOwner().getBindingContext();
            this.wrapper = (Class)wp.getXMLBridge().getTypeInfo().type;
            this.dynamicWrapper = WrapperComposite.class.equals((Object)this.wrapper);
            this.children = wp.getWrapperChildren();
            this.parameterBridges = new XMLBridge[this.children.size()];
            this.accessors = new PropertyAccessor[this.children.size()];
            for (int i = 0; i < this.accessors.length; ++i) {
                ParameterImpl p = (ParameterImpl)this.children.get(i);
                QName name = p.getName();
                if (this.dynamicWrapper) {
                    this.parameterBridges[i] = ((ParameterImpl)this.children.get(i)).getInlinedRepeatedElementBridge();
                    if (this.parameterBridges[i] != null) continue;
                    this.parameterBridges[i] = ((ParameterImpl)this.children.get(i)).getXMLBridge();
                    continue;
                }
                try {
                    this.accessors[i] = p.getOwner().getBindingContext().getElementPropertyAccessor(this.wrapper, name.getNamespaceURI(), name.getLocalPart());
                    continue;
                }
                catch (JAXBException e) {
                    throw new WebServiceException(this.wrapper + " do not have a property of the name " + name, (Throwable)e);
                }
            }
        }

        @Override
        Object build(Object[] methodArgs, Object returnValue) {
            if (this.dynamicWrapper) {
                return this.buildWrapperComposite(methodArgs, returnValue);
            }
            try {
                Object bean = this.bindingContext.newWrapperInstace(this.wrapper);
                for (int i = this.indices.length - 1; i >= 0; --i) {
                    if (this.indices[i] == -1) {
                        this.accessors[i].set(bean, returnValue);
                        continue;
                    }
                    this.accessors[i].set(bean, this.getters[i].get(methodArgs[this.indices[i]]));
                }
                return bean;
            }
            catch (InstantiationException e) {
                InstantiationError x = new InstantiationError(e.getMessage());
                x.initCause(e);
                throw x;
            }
            catch (IllegalAccessException e) {
                IllegalAccessError x = new IllegalAccessError(e.getMessage());
                x.initCause(e);
                throw x;
            }
            catch (DatabindingException e) {
                throw new WebServiceException((Throwable)e);
            }
        }
    }

    static abstract class Wrapped
    extends JAXB {
        protected final int[] indices;
        protected final ValueGetter[] getters;
        protected XMLBridge[] parameterBridges;
        protected List<ParameterImpl> children;

        protected Wrapped(WrapperParameter wp, SOAPVersion soapVersion) {
            super(wp.getXMLBridge(), soapVersion);
            this.children = wp.getWrapperChildren();
            this.indices = new int[this.children.size()];
            this.getters = new ValueGetter[this.children.size()];
            for (int i = 0; i < this.indices.length; ++i) {
                ParameterImpl p = this.children.get(i);
                this.indices[i] = p.getIndex();
                this.getters[i] = ValueGetter.get(p);
            }
        }

        WrapperComposite buildWrapperComposite(Object[] methodArgs, Object returnValue) {
            WrapperComposite cs = new WrapperComposite();
            cs.bridges = this.parameterBridges;
            cs.values = new Object[this.parameterBridges.length];
            for (int i = this.indices.length - 1; i >= 0; --i) {
                Object v = this.indices[i] == -1 ? this.getters[i].get(returnValue) : this.getters[i].get(methodArgs[this.indices[i]]);
                if (v == null) {
                    throw new WebServiceException("Method Parameter: " + this.children.get(i).getName() + " cannot be null. This is BP 1.1 R2211 violation.");
                }
                cs.values[i] = v;
            }
            return cs;
        }
    }

    public static final class Bare
    extends JAXB {
        private final int methodPos;
        private final ValueGetter getter;

        public Bare(ParameterImpl p, SOAPVersion soapVersion) {
            super(p.getXMLBridge(), soapVersion);
            this.methodPos = p.getIndex();
            this.getter = ValueGetter.get(p);
        }

        @Override
        Object build(Object[] methodArgs, Object returnValue) {
            if (this.methodPos == -1) {
                return returnValue;
            }
            return this.getter.get(methodArgs[this.methodPos]);
        }
    }

    private static abstract class JAXB
    extends EndpointResponseMessageBuilder {
        private final XMLBridge bridge;
        private final SOAPVersion soapVersion;

        protected JAXB(XMLBridge bridge, SOAPVersion soapVersion) {
            assert (bridge != null);
            this.bridge = bridge;
            this.soapVersion = soapVersion;
        }

        @Override
        public final Message createMessage(Object[] methodArgs, Object returnValue) {
            return JAXBMessage.create(this.bridge, this.build(methodArgs, returnValue), this.soapVersion);
        }

        abstract Object build(Object[] var1, Object var2);
    }

    private static final class Empty
    extends EndpointResponseMessageBuilder {
        private final SOAPVersion soapVersion;

        public Empty(SOAPVersion soapVersion) {
            this.soapVersion = soapVersion;
        }

        @Override
        public Message createMessage(Object[] methodArgs, Object returnValue) {
            return Messages.createEmpty(this.soapVersion);
        }
    }
}

