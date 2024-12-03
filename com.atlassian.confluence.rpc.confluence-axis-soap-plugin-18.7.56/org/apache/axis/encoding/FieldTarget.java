/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding;

import java.lang.reflect.Field;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.Target;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.xml.sax.SAXException;

public class FieldTarget
implements Target {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$FieldTarget == null ? (class$org$apache$axis$encoding$FieldTarget = FieldTarget.class$("org.apache.axis.encoding.FieldTarget")) : class$org$apache$axis$encoding$FieldTarget).getName());
    private Object targetObject;
    private Field targetField;
    static /* synthetic */ Class class$org$apache$axis$encoding$FieldTarget;

    public FieldTarget(Object targetObject, Field targetField) {
        this.targetObject = targetObject;
        this.targetField = targetField;
    }

    public FieldTarget(Object targetObject, String fieldName) throws NoSuchFieldException {
        Class<?> cls = targetObject.getClass();
        this.targetField = cls.getField(fieldName);
        this.targetObject = targetObject;
    }

    public void set(Object value) throws SAXException {
        try {
            this.targetField.set(this.targetObject, value);
        }
        catch (IllegalAccessException accEx) {
            log.error((Object)Messages.getMessage("illegalAccessException00"), (Throwable)accEx);
            throw new SAXException(accEx);
        }
        catch (IllegalArgumentException argEx) {
            log.error((Object)Messages.getMessage("illegalArgumentException00"), (Throwable)argEx);
            throw new SAXException(argEx);
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

