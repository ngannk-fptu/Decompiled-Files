/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.ActivationDataFlavor
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import javax.activation.ActivationDataFlavor;

public class ActivationDataFlavorConverter
implements Converter {
    static /* synthetic */ Class class$java$lang$Class;

    public boolean canConvert(Class type) {
        return type == ActivationDataFlavor.class;
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Class representationClass;
        String name;
        ActivationDataFlavor dataFlavor = (ActivationDataFlavor)source;
        String mimeType = dataFlavor.getMimeType();
        if (mimeType != null) {
            writer.startNode("mimeType");
            writer.setValue(mimeType);
            writer.endNode();
        }
        if ((name = dataFlavor.getHumanPresentableName()) != null) {
            writer.startNode("humanRepresentableName");
            writer.setValue(name);
            writer.endNode();
        }
        if ((representationClass = dataFlavor.getRepresentationClass()) != null) {
            writer.startNode("representationClass");
            context.convertAnother(representationClass);
            writer.endNode();
        }
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        String mimeType = null;
        String name = null;
        Class type = null;
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            String elementName = reader.getNodeName();
            if (elementName.equals("mimeType")) {
                mimeType = reader.getValue();
            } else if (elementName.equals("humanRepresentableName")) {
                name = reader.getValue();
            } else if (elementName.equals("representationClass")) {
                type = (Class)context.convertAnother(null, class$java$lang$Class == null ? ActivationDataFlavorConverter.class$("java.lang.Class") : class$java$lang$Class);
            } else {
                ConversionException exception = new ConversionException("Unknown child element");
                exception.add("element", reader.getNodeName());
                throw exception;
            }
            reader.moveUp();
        }
        ActivationDataFlavor dataFlavor = null;
        try {
            dataFlavor = type == null ? new ActivationDataFlavor(mimeType, name) : (mimeType == null ? new ActivationDataFlavor(type, name) : new ActivationDataFlavor(type, mimeType, name));
        }
        catch (IllegalArgumentException ex) {
            throw new ConversionException(ex);
        }
        catch (NullPointerException ex) {
            throw new ConversionException(ex);
        }
        return dataFlavor;
    }
}

