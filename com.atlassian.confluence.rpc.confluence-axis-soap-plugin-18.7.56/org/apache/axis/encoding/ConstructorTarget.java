/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.Target;
import org.apache.axis.i18n.Messages;
import org.xml.sax.SAXException;

public class ConstructorTarget
implements Target {
    private Constructor constructor = null;
    private Deserializer deSerializer = null;
    private List values = null;

    public ConstructorTarget(Constructor constructor, Deserializer deSerializer) {
        this.deSerializer = deSerializer;
        this.constructor = constructor;
        this.values = new ArrayList();
    }

    public void set(Object value) throws SAXException {
        try {
            this.values.add(value);
            if (this.constructor.getParameterTypes().length == this.values.size()) {
                Class<?>[] classes = this.constructor.getParameterTypes();
                Object[] args = new Object[this.constructor.getParameterTypes().length];
                for (int c = 0; c < classes.length; ++c) {
                    boolean found = false;
                    for (int i = 0; !found && i < this.values.size(); ++i) {
                        if (this.values.get(i).getClass().getName().toLowerCase().indexOf(classes[c].getName().toLowerCase()) == -1) continue;
                        found = true;
                        args[c] = this.values.get(i);
                    }
                    if (found) continue;
                    throw new SAXException(Messages.getMessage("cannotFindObjectForClass00", classes[c].toString()));
                }
                Object o = this.constructor.newInstance(args);
                this.deSerializer.setValue(o);
            }
        }
        catch (Exception e) {
            throw new SAXException(e);
        }
    }
}

