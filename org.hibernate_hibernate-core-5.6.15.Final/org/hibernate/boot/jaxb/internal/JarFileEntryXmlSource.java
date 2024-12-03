/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import org.hibernate.boot.MappingException;
import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.internal.InputStreamXmlSource;
import org.hibernate.boot.jaxb.spi.Binder;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.boot.jaxb.spi.XmlSource;

public class JarFileEntryXmlSource
extends XmlSource {
    private final JarFile jarFile;
    private final ZipEntry jarFileEntry;

    public JarFileEntryXmlSource(Origin origin, JarFile jarFile, ZipEntry jarFileEntry) {
        super(origin);
        this.jarFile = jarFile;
        this.jarFileEntry = jarFileEntry;
    }

    @Override
    public Binding doBind(Binder binder) {
        InputStream stream;
        try {
            stream = this.jarFile.getInputStream(this.jarFileEntry);
        }
        catch (IOException e) {
            throw new MappingException(String.format("Unable to open InputStream for jar file entry [%s : %s]", this.jarFile.getName(), this.jarFileEntry.getName()), e, this.getOrigin());
        }
        return InputStreamXmlSource.doBind(binder, stream, this.getOrigin(), true);
    }
}

