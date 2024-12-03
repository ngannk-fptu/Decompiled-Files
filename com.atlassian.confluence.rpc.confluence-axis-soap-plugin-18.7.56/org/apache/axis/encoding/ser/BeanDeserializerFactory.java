/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding.ser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.axis.description.TypeDesc;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.ser.BaseDeserializerFactory;
import org.apache.axis.encoding.ser.BeanDeserializer;
import org.apache.axis.utils.BeanPropertyDescriptor;
import org.apache.axis.utils.BeanUtils;
import org.apache.axis.utils.JavaUtils;

public class BeanDeserializerFactory
extends BaseDeserializerFactory {
    protected transient TypeDesc typeDesc = null;
    protected transient Map propertyMap = null;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$BeanDeserializer;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$EnumDeserializer;

    public BeanDeserializerFactory(Class javaType, QName xmlType) {
        super(class$org$apache$axis$encoding$ser$BeanDeserializer == null ? (class$org$apache$axis$encoding$ser$BeanDeserializer = BeanDeserializerFactory.class$("org.apache.axis.encoding.ser.BeanDeserializer")) : class$org$apache$axis$encoding$ser$BeanDeserializer, xmlType, javaType);
        if (JavaUtils.isEnumClass(javaType)) {
            this.deserClass = class$org$apache$axis$encoding$ser$EnumDeserializer == null ? (class$org$apache$axis$encoding$ser$EnumDeserializer = BeanDeserializerFactory.class$("org.apache.axis.encoding.ser.EnumDeserializer")) : class$org$apache$axis$encoding$ser$EnumDeserializer;
        }
        this.typeDesc = TypeDesc.getTypeDescForClass(javaType);
        this.propertyMap = BeanDeserializerFactory.getProperties(javaType, this.typeDesc);
    }

    public static Map getProperties(Class javaType, TypeDesc typeDesc) {
        HashMap<String, BeanPropertyDescriptor> propertyMap = null;
        if (typeDesc != null) {
            propertyMap = typeDesc.getPropertyDescriptorMap();
        } else {
            BeanPropertyDescriptor[] pd = BeanUtils.getPd(javaType, null);
            propertyMap = new HashMap<String, BeanPropertyDescriptor>();
            for (int i = 0; i < pd.length; ++i) {
                BeanPropertyDescriptor descriptor = pd[i];
                propertyMap.put(descriptor.getName(), descriptor);
            }
        }
        return propertyMap;
    }

    protected Deserializer getGeneralPurpose(String mechanismType) {
        if (this.javaType == null || this.xmlType == null) {
            return super.getGeneralPurpose(mechanismType);
        }
        if (this.deserClass == (class$org$apache$axis$encoding$ser$EnumDeserializer == null ? (class$org$apache$axis$encoding$ser$EnumDeserializer = BeanDeserializerFactory.class$("org.apache.axis.encoding.ser.EnumDeserializer")) : class$org$apache$axis$encoding$ser$EnumDeserializer)) {
            return super.getGeneralPurpose(mechanismType);
        }
        return new BeanDeserializer(this.javaType, this.xmlType, this.typeDesc, this.propertyMap);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.typeDesc = TypeDesc.getTypeDescForClass(this.javaType);
        this.propertyMap = BeanDeserializerFactory.getProperties(this.javaType, this.typeDesc);
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

