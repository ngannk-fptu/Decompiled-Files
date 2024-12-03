/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding.ser;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.Target;
import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.xml.sax.SAXException;

public class BeanPropertyTarget
implements Target {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$BeanPropertyTarget == null ? (class$org$apache$axis$encoding$ser$BeanPropertyTarget = BeanPropertyTarget.class$("org.apache.axis.encoding.ser.BeanPropertyTarget")) : class$org$apache$axis$encoding$ser$BeanPropertyTarget).getName());
    private Object object;
    private BeanPropertyDescriptor pd;
    private int index = -1;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$BeanPropertyTarget;
    static /* synthetic */ Class class$java$lang$Object;

    public BeanPropertyTarget(Object object, BeanPropertyDescriptor pd) {
        this.object = object;
        this.pd = pd;
        this.index = -1;
    }

    public BeanPropertyTarget(Object object, BeanPropertyDescriptor pd, int i) {
        this.object = object;
        this.pd = pd;
        this.index = i;
    }

    public void set(Object value) throws SAXException {
        try {
            if (this.index < 0) {
                this.pd.set(this.object, value);
            } else {
                this.pd.set(this.object, this.index, value);
            }
        }
        catch (Exception e) {
            try {
                Class<?> type = this.pd.getType();
                if (value.getClass().isArray() && value.getClass().getComponentType().isPrimitive() && type.isArray() && type.getComponentType().equals(class$java$lang$Object == null ? (class$java$lang$Object = BeanPropertyTarget.class$("java.lang.Object")) : class$java$lang$Object)) {
                    type = Array.newInstance(JavaUtils.getWrapperClass(value.getClass().getComponentType()), 0).getClass();
                }
                if (JavaUtils.isConvertable(value, type)) {
                    value = JavaUtils.convert(value, type);
                    if (this.index < 0) {
                        this.pd.set(this.object, value);
                    } else {
                        this.pd.set(this.object, this.index, value);
                    }
                }
                if (this.index == 0 && value.getClass().isArray() && !type.getClass().isArray()) {
                    for (int i = 0; i < Array.getLength(value); ++i) {
                        Object item = JavaUtils.convert(Array.get(value, i), type);
                        this.pd.set(this.object, i, item);
                    }
                }
                throw e;
            }
            catch (Exception ex) {
                Throwable t;
                String field = this.pd.getName();
                if (this.index >= 0) {
                    field = field + "[" + this.index + "]";
                }
                if (log.isErrorEnabled()) {
                    String valueType = "null";
                    if (value != null) {
                        valueType = value.getClass().getName();
                    }
                    log.error((Object)Messages.getMessage("cantConvert02", new String[]{valueType, field, this.index >= 0 ? this.pd.getType().getComponentType().getName() : this.pd.getType().getName()}));
                }
                if (ex instanceof InvocationTargetException && (t = ((InvocationTargetException)ex).getTargetException()) != null) {
                    String classname = this.object.getClass().getName();
                    throw new SAXException(Messages.getMessage("cantConvert04", new String[]{classname, field, value == null ? null : value.toString(), t.getMessage()}));
                }
                throw new SAXException(ex);
            }
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

