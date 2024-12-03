/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jersey.core.util.ReaderWriter
 *  javax.xml.bind.JAXBContext
 *  org.codehaus.jackson.JsonFactory
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jettison.badgerfish.BadgerFishXMLStreamReader
 *  org.codehaus.jettison.badgerfish.BadgerFishXMLStreamWriter
 *  org.codehaus.jettison.json.JSONObject
 *  org.codehaus.jettison.json.JSONTokener
 *  org.codehaus.jettison.mapped.Configuration
 *  org.codehaus.jettison.mapped.MappedNamespaceConvention
 *  org.codehaus.jettison.mapped.MappedXMLStreamReader
 *  org.codehaus.jettison.mapped.MappedXMLStreamWriter
 */
package com.sun.jersey.json.impl;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;
import com.sun.jersey.core.util.ReaderWriter;
import com.sun.jersey.json.impl.JSONHelper;
import com.sun.jersey.json.impl.reader.JsonXmlStreamReader;
import com.sun.jersey.json.impl.writer.JacksonArrayWrapperGenerator;
import com.sun.jersey.json.impl.writer.JacksonRootStrippingGenerator;
import com.sun.jersey.json.impl.writer.JsonXmlStreamWriter;
import com.sun.jersey.json.impl.writer.Stax2JacksonWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import javax.xml.bind.JAXBContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jettison.badgerfish.BadgerFishXMLStreamReader;
import org.codehaus.jettison.badgerfish.BadgerFishXMLStreamWriter;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONTokener;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamReader;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;

public class Stax2JsonFactory {
    private Stax2JsonFactory() {
    }

    public static XMLStreamWriter createWriter(Writer writer, JSONConfiguration config, Class<?> expectedType, JAXBContext jaxbContext) throws IOException {
        return Stax2JsonFactory.createWriter(writer, config, expectedType, jaxbContext, false);
    }

    public static XMLStreamWriter createWriter(Writer writer, JSONConfiguration config, Class<?> expectedType, JAXBContext jaxbContext, boolean writingList) throws IOException {
        if (jaxbContext instanceof JSONJAXBContext) {
            jaxbContext = ((JSONJAXBContext)jaxbContext).getOriginalJaxbContext();
        }
        switch (config.getNotation()) {
            case NATURAL: {
                JsonGenerator bodyGenerator;
                JsonGenerator rawGenerator = new JsonFactory().createJsonGenerator(writer);
                if (config.isHumanReadableFormatting()) {
                    rawGenerator.useDefaultPrettyPrinter();
                }
                JsonGenerator jsonGenerator = writingList ? JacksonArrayWrapperGenerator.createArrayWrapperGenerator(rawGenerator, config.isRootUnwrapping() ? 0 : 1) : (bodyGenerator = rawGenerator);
                if (config.isRootUnwrapping()) {
                    return new Stax2JacksonWriter(JacksonRootStrippingGenerator.createRootStrippingGenerator(bodyGenerator, writingList ? 2 : 1), config, expectedType, jaxbContext);
                }
                return new Stax2JacksonWriter(bodyGenerator, config, expectedType, jaxbContext);
            }
            case MAPPED: {
                return JsonXmlStreamWriter.createWriter(writer, config, JSONHelper.getRootElementName(expectedType));
            }
            case BADGERFISH: {
                return new BadgerFishXMLStreamWriter(writer);
            }
            case MAPPED_JETTISON: {
                Configuration jmConfig = null == config.getXml2JsonNs() ? new Configuration() : new Configuration(config.getXml2JsonNs());
                return new MappedXMLStreamWriter(new MappedNamespaceConvention(jmConfig), writer);
            }
        }
        return null;
    }

    public static XMLStreamReader createReader(Reader reader, JSONConfiguration config, String rootName, Class<?> expectedType, JAXBContext jaxbContext) throws XMLStreamException {
        return Stax2JsonFactory.createReader(reader, config, rootName, expectedType, jaxbContext, false);
    }

    public static XMLStreamReader createReader(Reader reader, JSONConfiguration config, String rootName, Class<?> expectedType, JAXBContext jaxbContext, boolean readingList) throws XMLStreamException {
        Reader nonEmptyReader = Stax2JsonFactory.ensureNonEmptyReader(reader);
        switch (config.getNotation()) {
            case NATURAL: 
            case MAPPED: {
                return JsonXmlStreamReader.create(nonEmptyReader, config, rootName, expectedType, jaxbContext, readingList);
            }
            case MAPPED_JETTISON: {
                try {
                    Configuration jmConfig = null == config.getXml2JsonNs() ? new Configuration() : new Configuration(config.getXml2JsonNs());
                    return new MappedXMLStreamReader(new JSONObject(new JSONTokener(ReaderWriter.readFromAsString((Reader)nonEmptyReader))), new MappedNamespaceConvention(jmConfig));
                }
                catch (Exception ex) {
                    throw new XMLStreamException(ex);
                }
            }
            case BADGERFISH: {
                try {
                    return new BadgerFishXMLStreamReader(new JSONObject(new JSONTokener(ReaderWriter.readFromAsString((Reader)nonEmptyReader))));
                }
                catch (Exception ex) {
                    throw new XMLStreamException(ex);
                }
            }
        }
        throw new IllegalArgumentException("Unknown JSON config");
    }

    private static Reader ensureNonEmptyReader(Reader reader) throws XMLStreamException {
        try {
            Reader mr = reader.markSupported() ? reader : new BufferedReader(reader);
            mr.mark(1);
            if (mr.read() == -1) {
                throw new XMLStreamException("JSON expression can not be empty!");
            }
            mr.reset();
            return mr;
        }
        catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
    }
}

