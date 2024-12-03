/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;

public class XWorkList
extends ArrayList {
    private static final Logger LOG = LogManager.getLogger(XWorkConverter.class);
    private Class clazz;

    public XWorkList(Class clazz) {
        this.clazz = clazz;
    }

    public XWorkList(Class clazz, int initialCapacity) {
        super(initialCapacity);
        this.clazz = clazz;
    }

    @Override
    public void add(int index, Object element) {
        if (index >= this.size()) {
            this.get(index);
        }
        element = this.convert(element);
        super.add(index, element);
    }

    @Override
    public boolean add(Object element) {
        element = this.convert(element);
        return super.add(element);
    }

    @Override
    public boolean addAll(Collection collection) {
        if (collection == null) {
            throw new NullPointerException("Collection to add is null");
        }
        for (Object nextElement : collection) {
            this.add(nextElement);
        }
        return true;
    }

    @Override
    public boolean addAll(int index, Collection collection) {
        if (collection == null) {
            throw new NullPointerException("Collection to add is null");
        }
        boolean trim = false;
        if (index >= this.size()) {
            trim = true;
        }
        Iterator it = collection.iterator();
        while (it.hasNext()) {
            this.add(index, it.next());
            ++index;
        }
        if (trim) {
            this.remove(this.size() - 1);
        }
        return true;
    }

    @Override
    public synchronized Object get(int index) {
        while (index >= this.size()) {
            try {
                this.add(this.getObjectFactory().buildBean(this.clazz, ActionContext.getContext().getContextMap()));
            }
            catch (Exception e) {
                throw new StrutsException(e);
            }
        }
        return super.get(index);
    }

    private ObjectFactory getObjectFactory() {
        return ActionContext.getContext().getInstance(ObjectFactory.class);
    }

    @Override
    public Object set(int index, Object element) {
        if (index >= this.size()) {
            this.get(index);
        }
        element = this.convert(element);
        return super.set(index, element);
    }

    private Object convert(Object element) {
        if (element != null && !this.clazz.isAssignableFrom(element.getClass())) {
            LOG.debug("Converting from {} to {}", (Object)element.getClass().getName(), (Object)this.clazz.getName());
            TypeConverter converter = this.getTypeConverter();
            Map<String, Object> context = ActionContext.getContext().getContextMap();
            element = converter.convertValue(context, null, null, null, element, this.clazz);
        }
        return element;
    }

    private TypeConverter getTypeConverter() {
        return ActionContext.getContext().getContainer().getInstance(XWorkConverter.class);
    }

    @Override
    public boolean contains(Object element) {
        element = this.convert(element);
        return super.contains(element);
    }
}

