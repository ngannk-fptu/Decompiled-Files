/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.constants;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Hashtable;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public abstract class Enum
implements Serializable {
    private static final Hashtable types = new Hashtable(13);
    protected static Log log = LogFactory.getLog((class$org$apache$axis$constants$Enum == null ? (class$org$apache$axis$constants$Enum = Enum.class$("org.apache.axis.constants.Enum")) : class$org$apache$axis$constants$Enum).getName());
    private final Type type;
    public final int value;
    public final String name;
    static /* synthetic */ Class class$org$apache$axis$constants$Enum;

    protected Enum(Type type, int value, String name) {
        this.type = type;
        this.value = value;
        this.name = name.intern();
    }

    public final int getValue() {
        return this.value;
    }

    public final String getName() {
        return this.name;
    }

    public final Type getType() {
        return this.type;
    }

    public String toString() {
        return this.name;
    }

    public final boolean equals(Object obj) {
        return obj != null && obj instanceof Enum ? this._equals((Enum)obj) : false;
    }

    public int hashCode() {
        return this.value;
    }

    public final boolean equals(Enum obj) {
        return obj != null ? this._equals(obj) : false;
    }

    private final boolean _equals(Enum obj) {
        return obj.type == this.type && obj.value == this.value;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    public static abstract class Type
    implements Serializable {
        private final String name;
        private final Enum[] enums;
        private Enum dephault = null;

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected Type(String name, Enum[] enums) {
            this.name = name.intern();
            this.enums = enums;
            Hashtable hashtable = types;
            synchronized (hashtable) {
                types.put(name, this);
            }
        }

        public void setDefault(Enum dephault) {
            this.dephault = dephault;
        }

        public Enum getDefault() {
            return this.dephault;
        }

        public final String getName() {
            return this.name;
        }

        public final boolean isValid(String enumName) {
            for (int enumElt = 0; enumElt < this.enums.length; ++enumElt) {
                if (!this.enums[enumElt].getName().equalsIgnoreCase(enumName)) continue;
                return true;
            }
            return false;
        }

        public final int size() {
            return this.enums.length;
        }

        public final String[] getEnumNames() {
            String[] nms = new String[this.size()];
            for (int idx = 0; idx < this.enums.length; ++idx) {
                nms[idx] = this.enums[idx].getName();
            }
            return nms;
        }

        public final Enum getEnum(int enumElt) {
            return enumElt >= 0 && enumElt < this.enums.length ? this.enums[enumElt] : null;
        }

        public final Enum getEnum(String enumName) {
            Enum e = this.getEnum(enumName, null);
            if (e == null) {
                log.error((Object)Messages.getMessage("badEnum02", this.name, enumName));
            }
            return e;
        }

        public final Enum getEnum(String enumName, Enum dephault) {
            if (enumName != null && enumName.length() > 0) {
                for (int enumElt = 0; enumElt < this.enums.length; ++enumElt) {
                    Enum e = this.enums[enumElt];
                    if (!e.getName().equalsIgnoreCase(enumName)) continue;
                    return e;
                }
            }
            return dephault;
        }

        private Object readResolve() throws ObjectStreamException {
            Object type = types.get(this.name);
            if (type == null) {
                type = this;
                types.put(this.name, type);
            }
            return type;
        }
    }
}

