/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.jaxb.internal;

import java.io.IOException;
import java.io.InputStream;
import org.hibernate.boot.InvalidMappingException;
import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.spi.Binder;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.boot.jaxb.spi.XmlSource;
import org.hibernate.internal.CoreLogging;
import org.jboss.logging.Logger;

public class InputStreamXmlSource
extends XmlSource {
    private static final Logger log = CoreLogging.logger(InputStreamXmlSource.class);
    private final InputStream inputStream;
    private final boolean autoClose;

    public InputStreamXmlSource(Origin origin, InputStream inputStream, boolean autoClose) {
        super(origin);
        this.inputStream = inputStream;
        this.autoClose = autoClose;
    }

    @Override
    public Binding doBind(Binder binder) {
        return InputStreamXmlSource.doBind(binder, this.inputStream, this.getOrigin(), this.autoClose);
    }

    public static Binding doBind(Binder binder, InputStream inputStream, Origin origin, boolean autoClose) {
        try {
            Binding binding = binder.bind(inputStream, origin);
            return binding;
        }
        catch (Exception e) {
            throw new InvalidMappingException(origin, (Throwable)e);
        }
        finally {
            if (autoClose) {
                try {
                    inputStream.close();
                }
                catch (IOException ignore) {
                    log.trace((Object)"Was unable to close input stream");
                }
            }
        }
    }
}

