/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBException
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.client.sei;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.message.Messages;
import com.sun.xml.ws.client.sei.ValueGetter;
import com.sun.xml.ws.client.sei.ValueGetterFactory;
import com.sun.xml.ws.message.jaxb.JAXBMessage;
import com.sun.xml.ws.model.ParameterImpl;
import com.sun.xml.ws.model.WrapperParameter;
import com.sun.xml.ws.spi.db.BindingContext;
import com.sun.xml.ws.spi.db.DatabindingException;
import com.sun.xml.ws.spi.db.PropertyAccessor;
import com.sun.xml.ws.spi.db.WrapperComposite;
import com.sun.xml.ws.spi.db.XMLBridge;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

abstract class BodyBuilder {
    static final BodyBuilder EMPTY_SOAP11 = new Empty(SOAPVersion.SOAP_11);
    static final BodyBuilder EMPTY_SOAP12 = new Empty(SOAPVersion.SOAP_12);

    BodyBuilder() {
    }

    abstract Message createMessage(Object[] var1);

    static final class RpcLit
    extends Wrapped {
        RpcLit(WrapperParameter wp, SOAPVersion soapVersion, ValueGetterFactory getter) {
            super(wp, soapVersion, getter);
            assert (wp.getTypeInfo().type == WrapperComposite.class);
            this.parameterBridges = new XMLBridge[this.children.size()];
            for (int i = 0; i < this.parameterBridges.length; ++i) {
                this.parameterBridges[i] = ((ParameterImpl)this.children.get(i)).getXMLBridge();
            }
        }

        @Override
        Object build(Object[] methodArgs) {
            return this.buildWrapperComposite(methodArgs);
        }
    }

    static final class DocLit
    extends Wrapped {
        private final PropertyAccessor[] accessors;
        private final Class wrapper;
        private BindingContext bindingContext;
        private boolean dynamicWrapper;

        DocLit(WrapperParameter wp, SOAPVersion soapVersion, ValueGetterFactory getter) {
            super(wp, soapVersion, getter);
            this.bindingContext = wp.getOwner().getBindingContext();
            this.wrapper = (Class)wp.getXMLBridge().getTypeInfo().type;
            this.dynamicWrapper = WrapperComposite.class.equals((Object)this.wrapper);
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
        Object build(Object[] methodArgs) {
            if (this.dynamicWrapper) {
                return this.buildWrapperComposite(methodArgs);
            }
            try {
                Object bean = this.bindingContext.newWrapperInstace(this.wrapper);
                for (int i = this.indices.length - 1; i >= 0; --i) {
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

        protected Wrapped(WrapperParameter wp, SOAPVersion soapVersion, ValueGetterFactory getter) {
            super(wp.getXMLBridge(), soapVersion);
            this.children = wp.getWrapperChildren();
            this.indices = new int[this.children.size()];
            this.getters = new ValueGetter[this.children.size()];
            for (int i = 0; i < this.indices.length; ++i) {
                ParameterImpl p = this.children.get(i);
                this.indices[i] = p.getIndex();
                this.getters[i] = getter.get(p);
            }
        }

        protected WrapperComposite buildWrapperComposite(Object[] methodArgs) {
            WrapperComposite cs = new WrapperComposite();
            cs.bridges = this.parameterBridges;
            cs.values = new Object[this.parameterBridges.length];
            for (int i = this.indices.length - 1; i >= 0; --i) {
                Object arg = this.getters[i].get(methodArgs[this.indices[i]]);
                if (arg == null) {
                    throw new WebServiceException("Method Parameter: " + this.children.get(i).getName() + " cannot be null. This is BP 1.1 R2211 violation.");
                }
                cs.values[i] = arg;
            }
            return cs;
        }
    }

    static final class Bare
    extends JAXB {
        private final int methodPos;
        private final ValueGetter getter;

        Bare(ParameterImpl p, SOAPVersion soapVersion, ValueGetter getter) {
            super(p.getXMLBridge(), soapVersion);
            this.methodPos = p.getIndex();
            this.getter = getter;
        }

        @Override
        Object build(Object[] methodArgs) {
            return this.getter.get(methodArgs[this.methodPos]);
        }
    }

    private static abstract class JAXB
    extends BodyBuilder {
        private final XMLBridge bridge;
        private final SOAPVersion soapVersion;

        protected JAXB(XMLBridge bridge, SOAPVersion soapVersion) {
            assert (bridge != null);
            this.bridge = bridge;
            this.soapVersion = soapVersion;
        }

        @Override
        final Message createMessage(Object[] methodArgs) {
            return JAXBMessage.create(this.bridge, this.build(methodArgs), this.soapVersion);
        }

        abstract Object build(Object[] var1);
    }

    private static final class Empty
    extends BodyBuilder {
        private final SOAPVersion soapVersion;

        public Empty(SOAPVersion soapVersion) {
            this.soapVersion = soapVersion;
        }

        @Override
        Message createMessage(Object[] methodArgs) {
            return Messages.createEmpty(this.soapVersion);
        }
    }
}

