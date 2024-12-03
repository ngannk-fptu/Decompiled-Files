/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ognl.ListPropertyAccessor
 *  ognl.OgnlException
 *  ognl.PropertyAccessor
 */
package com.opensymphony.xwork2.ognl.accessor;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.conversion.ObjectTypeDeterminer;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.OgnlUtil;
import com.opensymphony.xwork2.ognl.accessor.XWorkCollectionPropertyAccessor;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import ognl.ListPropertyAccessor;
import ognl.OgnlException;
import ognl.PropertyAccessor;
import org.apache.struts2.StrutsException;

public class XWorkListPropertyAccessor
extends ListPropertyAccessor {
    private XWorkCollectionPropertyAccessor _sAcc = new XWorkCollectionPropertyAccessor();
    private XWorkConverter xworkConverter;
    private ObjectFactory objectFactory;
    private ObjectTypeDeterminer objectTypeDeterminer;
    private OgnlUtil ognlUtil;
    private int autoGrowCollectionLimit = 255;

    @Inject(value="struts.ognl.autoGrowthCollectionLimit", required=false)
    public void setAutoGrowCollectionLimit(String value) {
        this.autoGrowCollectionLimit = Integer.parseInt(value);
    }

    @Inject(value="java.util.Collection")
    public void setXWorkCollectionPropertyAccessor(PropertyAccessor acc) {
        this._sAcc = (XWorkCollectionPropertyAccessor)acc;
    }

    @Inject
    public void setXWorkConverter(XWorkConverter conv) {
        this.xworkConverter = conv;
    }

    @Inject
    public void setObjectFactory(ObjectFactory fac) {
        this.objectFactory = fac;
    }

    @Inject
    public void setObjectTypeDeterminer(ObjectTypeDeterminer ot) {
        this.objectTypeDeterminer = ot;
    }

    @Inject
    public void setOgnlUtil(OgnlUtil util) {
        this.ognlUtil = util;
    }

    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        if (ReflectionContextState.isGettingByKeyProperty(context) || name.equals("makeNew")) {
            return this._sAcc.getProperty(context, target, name);
        }
        if (name instanceof String) {
            return super.getProperty(context, target, name);
        }
        ReflectionContextState.updateCurrentPropertyPath(context, name);
        Class lastClass = (Class)context.get("last.bean.accessed");
        String lastProperty = (String)context.get("last.property.accessed");
        if (name instanceof Number && ReflectionContextState.isCreatingNullObjects(context) && this.objectTypeDeterminer.shouldCreateIfNew(lastClass, lastProperty, target, null, true)) {
            List list = (List)target;
            int index = ((Number)name).intValue();
            int listSize = list.size();
            if (lastClass == null || lastProperty == null) {
                return super.getProperty(context, target, name);
            }
            Class beanClass = this.objectTypeDeterminer.getElementClass(lastClass, lastProperty, name);
            if (listSize <= index) {
                Object result;
                if (index > this.autoGrowCollectionLimit) {
                    throw new OgnlException("Error auto growing collection size to " + index + " which limited to " + this.autoGrowCollectionLimit);
                }
                for (int i = listSize; i < index; ++i) {
                    list.add(null);
                }
                try {
                    result = this.objectFactory.buildBean(beanClass, (Map<String, Object>)context);
                    list.add(index, result);
                }
                catch (Exception exc) {
                    throw new StrutsException(exc);
                }
                return result;
            }
            if (list.get(index) == null) {
                Object result;
                try {
                    result = this.objectFactory.buildBean(beanClass, (Map<String, Object>)context);
                    list.set(index, result);
                }
                catch (Exception exc) {
                    throw new StrutsException(exc);
                }
                return result;
            }
        }
        return super.getProperty(context, target, name);
    }

    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        Class lastClass = (Class)context.get("last.bean.accessed");
        String lastProperty = (String)context.get("last.property.accessed");
        Class convertToClass = this.objectTypeDeterminer.getElementClass(lastClass, lastProperty, name);
        if (name instanceof String && value.getClass().isArray()) {
            Object[] values;
            Collection c = (Collection)target;
            for (Object v : values = (Object[])value) {
                try {
                    Object o = this.objectFactory.buildBean(convertToClass, (Map<String, Object>)context);
                    this.ognlUtil.setValue((String)name, context, o, v);
                    c.add(o);
                }
                catch (Exception e) {
                    throw new OgnlException("Error converting given String values for Collection.", (Throwable)e);
                }
            }
            return;
        }
        Object realValue = this.getRealValue(context, value, convertToClass);
        if (target instanceof List && name instanceof Number) {
            List list = (List)target;
            int listSize = list.size();
            int count = ((Number)name).intValue();
            if (count > this.autoGrowCollectionLimit) {
                throw new OgnlException("Error auto growing collection size to " + count + " which limited to " + this.autoGrowCollectionLimit);
            }
            if (count >= listSize) {
                for (int i = listSize; i <= count; ++i) {
                    list.add(null);
                }
            }
        }
        super.setProperty(context, target, name, realValue);
    }

    private Object getRealValue(Map context, Object value, Class convertToClass) {
        if (value == null || convertToClass == null) {
            return value;
        }
        return this.xworkConverter.convertValue(context, value, convertToClass);
    }
}

