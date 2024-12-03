/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.annotation.DomHandler
 */
package com.sun.xml.bind.v2.runtime.property;

import com.sun.xml.bind.api.AccessorException;
import com.sun.xml.bind.v2.ClassFactory;
import com.sun.xml.bind.v2.model.core.PropertyKind;
import com.sun.xml.bind.v2.model.core.WildcardMode;
import com.sun.xml.bind.v2.model.runtime.RuntimeElement;
import com.sun.xml.bind.v2.model.runtime.RuntimeReferencePropertyInfo;
import com.sun.xml.bind.v2.runtime.ElementBeanInfoImpl;
import com.sun.xml.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.bind.v2.runtime.JaxBeanInfo;
import com.sun.xml.bind.v2.runtime.XMLSerializer;
import com.sun.xml.bind.v2.runtime.property.PropertyImpl;
import com.sun.xml.bind.v2.runtime.property.UnmarshallerChain;
import com.sun.xml.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.bind.v2.runtime.unmarshaller.ChildLoader;
import com.sun.xml.bind.v2.runtime.unmarshaller.WildcardLoader;
import com.sun.xml.bind.v2.util.QNameMap;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.DomHandler;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class SingleReferenceNodeProperty<BeanT, ValueT>
extends PropertyImpl<BeanT> {
    private final Accessor<BeanT, ValueT> acc;
    private final QNameMap<JaxBeanInfo> expectedElements = new QNameMap();
    private final DomHandler domHandler;
    private final WildcardMode wcMode;

    public SingleReferenceNodeProperty(JAXBContextImpl context, RuntimeReferencePropertyInfo prop) {
        super(context, prop);
        this.acc = prop.getAccessor().optimize(context);
        for (RuntimeElement runtimeElement : prop.getElements()) {
            this.expectedElements.put(runtimeElement.getElementName(), context.getOrCreate(runtimeElement));
        }
        if (prop.getWildcard() != null) {
            this.domHandler = (DomHandler)ClassFactory.create((Class)prop.getDOMHandler());
            this.wcMode = prop.getWildcard();
        } else {
            this.domHandler = null;
            this.wcMode = null;
        }
    }

    @Override
    public void reset(BeanT bean) throws AccessorException {
        this.acc.set(bean, null);
    }

    @Override
    public String getIdValue(BeanT beanT) {
        return null;
    }

    @Override
    public void serializeBody(BeanT o, XMLSerializer w, Object outerPeer) throws SAXException, AccessorException, IOException, XMLStreamException {
        ValueT v = this.acc.get(o);
        if (v != null) {
            try {
                JaxBeanInfo bi = w.grammar.getBeanInfo(v, true);
                if (bi.jaxbType == Object.class && this.domHandler != null) {
                    w.writeDom(v, this.domHandler, o, this.fieldName);
                } else {
                    bi.serializeRoot(v, w);
                }
            }
            catch (JAXBException e) {
                w.reportError(this.fieldName, e);
            }
        }
    }

    @Override
    public void buildChildElementUnmarshallers(UnmarshallerChain chain, QNameMap<ChildLoader> handlers) {
        for (QNameMap.Entry<JaxBeanInfo> n : this.expectedElements.entrySet()) {
            handlers.put(n.nsUri, n.localName, new ChildLoader(n.getValue().getLoader(chain.context, true), this.acc));
        }
        if (this.domHandler != null) {
            handlers.put(CATCH_ALL, new ChildLoader(new WildcardLoader(this.domHandler, this.wcMode), this.acc));
        }
    }

    @Override
    public PropertyKind getKind() {
        return PropertyKind.REFERENCE;
    }

    @Override
    public Accessor getElementPropertyAccessor(String nsUri, String localName) {
        JaxBeanInfo bi = this.expectedElements.get(nsUri, localName);
        if (bi != null) {
            if (bi instanceof ElementBeanInfoImpl) {
                final ElementBeanInfoImpl ebi = (ElementBeanInfoImpl)bi;
                return new Accessor<BeanT, Object>(ebi.expectedType){

                    @Override
                    public Object get(BeanT bean) throws AccessorException {
                        Object r = SingleReferenceNodeProperty.this.acc.get(bean);
                        if (r instanceof JAXBElement) {
                            return ((JAXBElement)r).getValue();
                        }
                        return r;
                    }

                    @Override
                    public void set(BeanT bean, Object value) throws AccessorException {
                        if (value != null) {
                            try {
                                value = ebi.createInstanceFromValue(value);
                            }
                            catch (IllegalAccessException e) {
                                throw new AccessorException(e);
                            }
                            catch (InvocationTargetException e) {
                                throw new AccessorException(e);
                            }
                            catch (InstantiationException e) {
                                throw new AccessorException(e);
                            }
                        }
                        SingleReferenceNodeProperty.this.acc.set(bean, value);
                    }
                };
            }
            return this.acc;
        }
        return null;
    }
}

