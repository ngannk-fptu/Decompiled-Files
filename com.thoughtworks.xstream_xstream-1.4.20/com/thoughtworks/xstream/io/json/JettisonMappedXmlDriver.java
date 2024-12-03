/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jettison.mapped.Configuration
 *  org.codehaus.jettison.mapped.MappedNamespaceConvention
 *  org.codehaus.jettison.mapped.MappedXMLInputFactory
 *  org.codehaus.jettison.mapped.MappedXMLOutputFactory
 */
package com.thoughtworks.xstream.io.json;

import com.thoughtworks.xstream.io.AbstractDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.StreamException;
import com.thoughtworks.xstream.io.json.JettisonStaxWriter;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxReader;
import com.thoughtworks.xstream.io.xml.StaxWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import javax.xml.stream.XMLStreamException;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLInputFactory;
import org.codehaus.jettison.mapped.MappedXMLOutputFactory;

public class JettisonMappedXmlDriver
extends AbstractDriver {
    protected final MappedXMLOutputFactory mof;
    protected final MappedXMLInputFactory mif;
    protected final MappedNamespaceConvention convention;
    protected final boolean useSerializeAsArray;

    public JettisonMappedXmlDriver() {
        this((Configuration)null);
    }

    public JettisonMappedXmlDriver(Configuration config) {
        this(config, true);
    }

    public JettisonMappedXmlDriver(Configuration config, boolean useSerializeAsArray) {
        config = config == null ? new Configuration() : config;
        this.mof = new MappedXMLOutputFactory(config);
        this.mif = new MappedXMLInputFactory(config);
        this.convention = new MappedNamespaceConvention(config);
        this.useSerializeAsArray = useSerializeAsArray;
    }

    public HierarchicalStreamReader createReader(Reader reader) {
        try {
            return new StaxReader(new QNameMap(), this.mif.createXMLStreamReader(reader), this.getNameCoder());
        }
        catch (XMLStreamException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamReader createReader(InputStream input) {
        try {
            return new StaxReader(new QNameMap(), this.mif.createXMLStreamReader(input), this.getNameCoder());
        }
        catch (XMLStreamException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamReader createReader(URL in) {
        InputStream instream = null;
        try {
            instream = in.openStream();
            StaxReader staxReader = new StaxReader(new QNameMap(), this.mif.createXMLStreamReader(in.toExternalForm(), instream), this.getNameCoder());
            return staxReader;
        }
        catch (XMLStreamException e) {
            throw new StreamException(e);
        }
        catch (IOException e) {
            throw new StreamException(e);
        }
        finally {
            if (instream != null) {
                try {
                    instream.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    public HierarchicalStreamReader createReader(File in) {
        FileInputStream instream = null;
        try {
            instream = new FileInputStream(in);
            StaxReader staxReader = new StaxReader(new QNameMap(), this.mif.createXMLStreamReader(in.toURI().toASCIIString(), (InputStream)instream), this.getNameCoder());
            return staxReader;
        }
        catch (XMLStreamException e) {
            throw new StreamException(e);
        }
        catch (IOException e) {
            throw new StreamException(e);
        }
        finally {
            if (instream != null) {
                try {
                    ((InputStream)instream).close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    public HierarchicalStreamWriter createWriter(Writer writer) {
        try {
            if (this.useSerializeAsArray) {
                return new JettisonStaxWriter(new QNameMap(), this.mof.createXMLStreamWriter(writer), this.getNameCoder(), this.convention);
            }
            return new StaxWriter(new QNameMap(), this.mof.createXMLStreamWriter(writer), this.getNameCoder());
        }
        catch (XMLStreamException e) {
            throw new StreamException(e);
        }
    }

    public HierarchicalStreamWriter createWriter(OutputStream output) {
        try {
            if (this.useSerializeAsArray) {
                return new JettisonStaxWriter(new QNameMap(), this.mof.createXMLStreamWriter(output), this.getNameCoder(), this.convention);
            }
            return new StaxWriter(new QNameMap(), this.mof.createXMLStreamWriter(output), this.getNameCoder());
        }
        catch (XMLStreamException e) {
            throw new StreamException(e);
        }
    }
}

