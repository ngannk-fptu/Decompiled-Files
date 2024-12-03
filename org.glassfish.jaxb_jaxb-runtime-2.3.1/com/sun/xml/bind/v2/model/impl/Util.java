/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.MimeType
 *  javax.activation.MimeTypeParseException
 *  javax.xml.bind.annotation.XmlMimeType
 *  javax.xml.bind.annotation.XmlSchemaType
 *  javax.xml.bind.annotation.XmlSchemaTypes
 */
package com.sun.xml.bind.v2.model.impl;

import com.sun.xml.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.bind.v2.model.annotation.AnnotationSource;
import com.sun.xml.bind.v2.model.annotation.Locatable;
import com.sun.xml.bind.v2.model.impl.Messages;
import com.sun.xml.bind.v2.model.impl.ModelBuilder;
import com.sun.xml.bind.v2.runtime.IllegalAnnotationException;
import java.lang.annotation.Annotation;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSchemaTypes;
import javax.xml.namespace.QName;

final class Util {
    Util() {
    }

    static <T, C, F, M> QName calcSchemaType(AnnotationReader<T, C, F, M> reader, AnnotationSource primarySource, C enclosingClass, T individualType, Locatable src) {
        XmlSchemaType xst = primarySource.readAnnotation(XmlSchemaType.class);
        if (xst != null) {
            return new QName(xst.namespace(), xst.name());
        }
        XmlSchemaTypes xsts = reader.getPackageAnnotation(XmlSchemaTypes.class, enclosingClass, src);
        XmlSchemaType[] values = null;
        if (xsts != null) {
            values = xsts.value();
        } else {
            xst = reader.getPackageAnnotation(XmlSchemaType.class, enclosingClass, src);
            if (xst != null) {
                values = new XmlSchemaType[]{xst};
            }
        }
        if (values != null) {
            for (XmlSchemaType item : values) {
                if (!reader.getClassValue((Annotation)item, "type").equals(individualType)) continue;
                return new QName(item.namespace(), item.name());
            }
        }
        return null;
    }

    static MimeType calcExpectedMediaType(AnnotationSource primarySource, ModelBuilder builder) {
        XmlMimeType xmt = primarySource.readAnnotation(XmlMimeType.class);
        if (xmt == null) {
            return null;
        }
        try {
            return new MimeType(xmt.value());
        }
        catch (MimeTypeParseException e) {
            builder.reportError(new IllegalAnnotationException(Messages.ILLEGAL_MIME_TYPE.format(xmt.value(), e.getMessage()), (Annotation)xmt));
            return null;
        }
    }
}

