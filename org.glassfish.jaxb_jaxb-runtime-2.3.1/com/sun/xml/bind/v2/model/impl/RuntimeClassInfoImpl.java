/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.bind.JAXBException
 */
package com.sun.xml.bind.v2.model.impl;

import com.sun.istack.NotNull;
import com.sun.xml.bind.AccessorFactory;
import com.sun.xml.bind.AccessorFactoryImpl;
import com.sun.xml.bind.InternalAccessorFactory;
import com.sun.xml.bind.XmlAccessorFactory;
import com.sun.xml.bind.annotation.XmlLocation;
import com.sun.xml.bind.api.AccessorException;
import com.sun.xml.bind.v2.ClassFactory;
import com.sun.xml.bind.v2.model.annotation.Locatable;
import com.sun.xml.bind.v2.model.core.PropertyKind;
import com.sun.xml.bind.v2.model.impl.AttributePropertyInfoImpl;
import com.sun.xml.bind.v2.model.impl.ClassInfoImpl;
import com.sun.xml.bind.v2.model.impl.ElementPropertyInfoImpl;
import com.sun.xml.bind.v2.model.impl.MapPropertyInfoImpl;
import com.sun.xml.bind.v2.model.impl.Messages;
import com.sun.xml.bind.v2.model.impl.PropertySeed;
import com.sun.xml.bind.v2.model.impl.ReferencePropertyInfoImpl;
import com.sun.xml.bind.v2.model.impl.RuntimeAttributePropertyInfoImpl;
import com.sun.xml.bind.v2.model.impl.RuntimeElementPropertyInfoImpl;
import com.sun.xml.bind.v2.model.impl.RuntimeMapPropertyInfoImpl;
import com.sun.xml.bind.v2.model.impl.RuntimeModelBuilder;
import com.sun.xml.bind.v2.model.impl.RuntimeReferencePropertyInfoImpl;
import com.sun.xml.bind.v2.model.impl.RuntimeValuePropertyInfoImpl;
import com.sun.xml.bind.v2.model.impl.ValuePropertyInfoImpl;
import com.sun.xml.bind.v2.model.runtime.RuntimeClassInfo;
import com.sun.xml.bind.v2.model.runtime.RuntimeElement;
import com.sun.xml.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.bind.v2.model.runtime.RuntimeValuePropertyInfo;
import com.sun.xml.bind.v2.runtime.IllegalAnnotationException;
import com.sun.xml.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.bind.v2.runtime.Location;
import com.sun.xml.bind.v2.runtime.Name;
import com.sun.xml.bind.v2.runtime.Transducer;
import com.sun.xml.bind.v2.runtime.XMLSerializer;
import com.sun.xml.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.bind.v2.runtime.reflect.TransducedAccessor;
import com.sun.xml.bind.v2.runtime.unmarshaller.UnmarshallingContext;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class RuntimeClassInfoImpl
extends ClassInfoImpl<Type, Class, Field, Method>
implements RuntimeClassInfo,
RuntimeElement {
    private Accessor<?, Locator> xmlLocationAccessor;
    private AccessorFactory accessorFactory;
    private boolean supressAccessorWarnings = false;
    private Accessor<?, Map<QName, String>> attributeWildcardAccessor;
    private boolean computedTransducer = false;
    private Transducer xducer = null;

    public RuntimeClassInfoImpl(RuntimeModelBuilder modelBuilder, Locatable upstream, Class clazz) {
        super(modelBuilder, upstream, clazz);
        this.accessorFactory = this.createAccessorFactory(clazz);
    }

    protected AccessorFactory createAccessorFactory(Class clazz) {
        AccessorFactory accFactory = null;
        JAXBContextImpl context = ((RuntimeModelBuilder)this.builder).context;
        if (context != null) {
            XmlAccessorFactory factoryAnn;
            this.supressAccessorWarnings = context.supressAccessorWarnings;
            if (context.xmlAccessorFactorySupport && (factoryAnn = this.findXmlAccessorFactoryAnnotation(clazz)) != null) {
                try {
                    accFactory = factoryAnn.value().newInstance();
                }
                catch (InstantiationException e) {
                    this.builder.reportError(new IllegalAnnotationException(Messages.ACCESSORFACTORY_INSTANTIATION_EXCEPTION.format(factoryAnn.getClass().getName(), this.nav().getClassName(clazz)), this));
                }
                catch (IllegalAccessException e) {
                    this.builder.reportError(new IllegalAnnotationException(Messages.ACCESSORFACTORY_ACCESS_EXCEPTION.format(factoryAnn.getClass().getName(), this.nav().getClassName(clazz)), this));
                }
            }
        }
        if (accFactory == null) {
            accFactory = AccessorFactoryImpl.getInstance();
        }
        return accFactory;
    }

    protected XmlAccessorFactory findXmlAccessorFactoryAnnotation(Class clazz) {
        XmlAccessorFactory factoryAnn = this.reader().getClassAnnotation(XmlAccessorFactory.class, clazz, this);
        if (factoryAnn == null) {
            factoryAnn = this.reader().getPackageAnnotation(XmlAccessorFactory.class, clazz, this);
        }
        return factoryAnn;
    }

    @Override
    public Method getFactoryMethod() {
        return super.getFactoryMethod();
    }

    @Override
    public final RuntimeClassInfoImpl getBaseClass() {
        return (RuntimeClassInfoImpl)super.getBaseClass();
    }

    @Override
    protected ReferencePropertyInfoImpl createReferenceProperty(PropertySeed<Type, Class, Field, Method> seed) {
        return new RuntimeReferencePropertyInfoImpl(this, seed);
    }

    @Override
    protected AttributePropertyInfoImpl createAttributeProperty(PropertySeed<Type, Class, Field, Method> seed) {
        return new RuntimeAttributePropertyInfoImpl(this, seed);
    }

    @Override
    protected ValuePropertyInfoImpl createValueProperty(PropertySeed<Type, Class, Field, Method> seed) {
        return new RuntimeValuePropertyInfoImpl(this, seed);
    }

    @Override
    protected ElementPropertyInfoImpl createElementProperty(PropertySeed<Type, Class, Field, Method> seed) {
        return new RuntimeElementPropertyInfoImpl(this, seed);
    }

    @Override
    protected MapPropertyInfoImpl createMapProperty(PropertySeed<Type, Class, Field, Method> seed) {
        return new RuntimeMapPropertyInfoImpl(this, seed);
    }

    @Override
    public List<? extends RuntimePropertyInfo> getProperties() {
        return super.getProperties();
    }

    @Override
    public RuntimePropertyInfo getProperty(String name) {
        return (RuntimePropertyInfo)super.getProperty(name);
    }

    @Override
    public void link() {
        this.getTransducer();
        super.link();
    }

    public <B> Accessor<B, Map<QName, String>> getAttributeWildcard() {
        for (RuntimeClassInfoImpl c = this; c != null; c = c.getBaseClass()) {
            if (c.attributeWildcard == null) continue;
            if (c.attributeWildcardAccessor == null) {
                c.attributeWildcardAccessor = c.createAttributeWildcardAccessor();
            }
            return c.attributeWildcardAccessor;
        }
        return null;
    }

    public Transducer getTransducer() {
        if (!this.computedTransducer) {
            this.computedTransducer = true;
            this.xducer = this.calcTransducer();
        }
        return this.xducer;
    }

    private Transducer calcTransducer() {
        RuntimeValuePropertyInfo valuep = null;
        if (this.hasAttributeWildcard()) {
            return null;
        }
        for (RuntimeClassInfoImpl ci = this; ci != null; ci = ci.getBaseClass()) {
            for (RuntimePropertyInfo runtimePropertyInfo : ci.getProperties()) {
                if (runtimePropertyInfo.kind() == PropertyKind.VALUE) {
                    valuep = (RuntimeValuePropertyInfo)runtimePropertyInfo;
                    continue;
                }
                return null;
            }
        }
        if (valuep == null) {
            return null;
        }
        if (!valuep.getTarget().isSimpleType()) {
            return null;
        }
        return new TransducerImpl((Class)this.getClazz(), TransducedAccessor.get(((RuntimeModelBuilder)this.builder).context, valuep));
    }

    private Accessor<?, Map<QName, String>> createAttributeWildcardAccessor() {
        assert (this.attributeWildcard != null);
        return ((RuntimePropertySeed)this.attributeWildcard).getAccessor();
    }

    protected RuntimePropertySeed createFieldSeed(Field field) {
        Accessor<Object, Object> acc;
        boolean readOnly = Modifier.isStatic(field.getModifiers());
        try {
            acc = this.supressAccessorWarnings ? ((InternalAccessorFactory)this.accessorFactory).createFieldAccessor((Class)this.clazz, field, readOnly, this.supressAccessorWarnings) : this.accessorFactory.createFieldAccessor((Class)this.clazz, field, readOnly);
        }
        catch (JAXBException e) {
            this.builder.reportError(new IllegalAnnotationException(Messages.CUSTOM_ACCESSORFACTORY_FIELD_ERROR.format(this.nav().getClassName(this.clazz), e.toString()), this));
            acc = Accessor.getErrorInstance();
        }
        return new RuntimePropertySeed(super.createFieldSeed(field), acc);
    }

    public RuntimePropertySeed createAccessorSeed(Method getter, Method setter) {
        Accessor acc;
        try {
            acc = this.accessorFactory.createPropertyAccessor((Class)this.clazz, getter, setter);
        }
        catch (JAXBException e) {
            this.builder.reportError(new IllegalAnnotationException(Messages.CUSTOM_ACCESSORFACTORY_PROPERTY_ERROR.format(this.nav().getClassName(this.clazz), e.toString()), this));
            acc = Accessor.getErrorInstance();
        }
        return new RuntimePropertySeed(super.createAccessorSeed(getter, setter), acc);
    }

    @Override
    protected void checkFieldXmlLocation(Field f) {
        if (this.reader().hasFieldAnnotation(XmlLocation.class, f)) {
            this.xmlLocationAccessor = new Accessor.FieldReflection(f);
        }
    }

    public Accessor<?, Locator> getLocatorField() {
        return this.xmlLocationAccessor;
    }

    private static final class TransducerImpl<BeanT>
    implements Transducer<BeanT> {
        private final TransducedAccessor<BeanT> xacc;
        private final Class<BeanT> ownerClass;

        public TransducerImpl(Class<BeanT> ownerClass, TransducedAccessor<BeanT> xacc) {
            this.xacc = xacc;
            this.ownerClass = ownerClass;
        }

        @Override
        public boolean useNamespace() {
            return this.xacc.useNamespace();
        }

        @Override
        public void declareNamespace(BeanT bean, XMLSerializer w) throws AccessorException {
            try {
                this.xacc.declareNamespace(bean, w);
            }
            catch (SAXException e) {
                throw new AccessorException(e);
            }
        }

        @Override
        @NotNull
        public CharSequence print(BeanT o) throws AccessorException {
            try {
                CharSequence value = this.xacc.print(o);
                if (value == null) {
                    throw new AccessorException(Messages.THERE_MUST_BE_VALUE_IN_XMLVALUE.format(o));
                }
                return value;
            }
            catch (SAXException e) {
                throw new AccessorException(e);
            }
        }

        @Override
        public BeanT parse(CharSequence lexical) throws AccessorException, SAXException {
            UnmarshallingContext ctxt = UnmarshallingContext.getInstance();
            Object inst = ctxt != null ? ctxt.createInstance(this.ownerClass) : ClassFactory.create(this.ownerClass);
            this.xacc.parse(inst, lexical);
            return inst;
        }

        @Override
        public void writeText(XMLSerializer w, BeanT o, String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
            if (!this.xacc.hasValue(o)) {
                throw new AccessorException(Messages.THERE_MUST_BE_VALUE_IN_XMLVALUE.format(o));
            }
            this.xacc.writeText(w, o, fieldName);
        }

        @Override
        public void writeLeafElement(XMLSerializer w, Name tagName, BeanT o, String fieldName) throws IOException, SAXException, XMLStreamException, AccessorException {
            if (!this.xacc.hasValue(o)) {
                throw new AccessorException(Messages.THERE_MUST_BE_VALUE_IN_XMLVALUE.format(o));
            }
            this.xacc.writeLeafElement(w, tagName, o, fieldName);
        }

        @Override
        public QName getTypeName(BeanT instance) {
            return null;
        }
    }

    static final class RuntimePropertySeed
    implements PropertySeed<Type, Class, Field, Method> {
        private final Accessor acc;
        private final PropertySeed<Type, Class, Field, Method> core;

        public RuntimePropertySeed(PropertySeed<Type, Class, Field, Method> core, Accessor acc) {
            this.core = core;
            this.acc = acc;
        }

        @Override
        public String getName() {
            return this.core.getName();
        }

        @Override
        public <A extends Annotation> A readAnnotation(Class<A> annotationType) {
            return this.core.readAnnotation(annotationType);
        }

        @Override
        public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
            return this.core.hasAnnotation(annotationType);
        }

        @Override
        public Type getRawType() {
            return this.core.getRawType();
        }

        @Override
        public Location getLocation() {
            return this.core.getLocation();
        }

        @Override
        public Locatable getUpstream() {
            return this.core.getUpstream();
        }

        public Accessor getAccessor() {
            return this.acc;
        }
    }
}

