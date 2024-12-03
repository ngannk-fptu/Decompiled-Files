/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.hibernate.boot.MappingNotFoundException;
import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.internal.InputStreamXmlSource;
import org.hibernate.boot.jaxb.spi.Binder;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.boot.jaxb.spi.XmlSource;

public class FileXmlSource
extends XmlSource {
    private final File file;

    public FileXmlSource(Origin origin, File file) {
        super(origin);
        this.file = file;
    }

    @Override
    public Binding doBind(Binder binder) {
        return FileXmlSource.doBind(binder, this.file, this.getOrigin());
    }

    public static Binding doBind(Binder binder, File file, Origin origin) {
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
        }
        catch (FileNotFoundException e) {
            throw new MappingNotFoundException(e, origin);
        }
        return InputStreamXmlSource.doBind(binder, fis, origin, true);
    }
}

