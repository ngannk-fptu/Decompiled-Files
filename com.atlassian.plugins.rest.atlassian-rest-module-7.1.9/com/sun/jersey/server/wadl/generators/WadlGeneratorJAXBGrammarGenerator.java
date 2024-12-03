/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.bind.JAXBException
 *  javax.xml.bind.JAXBIntrospector
 *  javax.xml.bind.SchemaOutputResolver
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.sun.jersey.server.wadl.generators;

import com.sun.jersey.server.wadl.ApplicationDescription;
import com.sun.jersey.server.wadl.WadlGenerator;
import com.sun.jersey.server.wadl.WadlGeneratorImpl;
import com.sun.jersey.server.wadl.generators.AbstractWadlGeneratorGrammarGenerator;
import com.sun.research.ws.wadl.Param;
import com.sun.research.ws.wadl.Representation;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

public class WadlGeneratorJAXBGrammarGenerator
extends AbstractWadlGeneratorGrammarGenerator<QName> {
    private static final Logger LOGGER = Logger.getLogger(WadlGeneratorJAXBGrammarGenerator.class.getName());

    public WadlGeneratorJAXBGrammarGenerator() {
        super(new WadlGeneratorImpl(), QName.class);
    }

    @Override
    public boolean acceptMediaType(MediaType type) {
        if (type.equals(MediaType.APPLICATION_XML_TYPE) || type.equals(MediaType.TEXT_XML_TYPE) || type.getSubtype().endsWith("+xml")) {
            return true;
        }
        if (type.equals(MediaType.APPLICATION_JSON_TYPE) || type.getSubtype().endsWith("+json")) {
            return true;
        }
        return type.equals(MediaType.WILDCARD_TYPE);
    }

    @Override
    protected WadlGenerator.Resolver buildModelAndSchemas(Map<String, ApplicationDescription.ExternalGrammar> extraFiles) {
        HashSet<Class> classSet = new HashSet<Class>(this._seeAlso);
        for (AbstractWadlGeneratorGrammarGenerator.Pair pair : this._hasTypeWantsName) {
            Type parameterType;
            Type type;
            AbstractWadlGeneratorGrammarGenerator.HasType hasType = pair.hasType;
            Class clazz = hasType.getPrimaryClass();
            if (clazz.getAnnotation(XmlRootElement.class) != null) {
                classSet.add(clazz);
                continue;
            }
            if (!SPECIAL_GENERIC_TYPES.contains(clazz) || !((type = hasType.getType()) instanceof ParameterizedType) || !((parameterType = ((ParameterizedType)type).getActualTypeArguments()[0]) instanceof Class)) continue;
            classSet.add((Class)parameterType);
        }
        JAXBIntrospector introspector = null;
        try {
            JAXBContext context = JAXBContext.newInstance((Class[])classSet.toArray(new Class[classSet.size()]));
            final ArrayList results = new ArrayList();
            context.generateSchema(new SchemaOutputResolver(){
                int counter = 0;

                public Result createOutput(String namespaceUri, String suggestedFileName) {
                    StreamResult result = new StreamResult(new CharArrayWriter());
                    result.setSystemId("xsd" + this.counter++ + ".xsd");
                    results.add(result);
                    return result;
                }
            });
            for (StreamResult result : results) {
                CharArrayWriter writer = (CharArrayWriter)result.getWriter();
                byte[] contents = writer.toString().getBytes("UTF8");
                extraFiles.put(result.getSystemId(), new ApplicationDescription.ExternalGrammar(MediaType.APPLICATION_XML_TYPE, contents, true));
            }
            introspector = context.createJAXBIntrospector();
        }
        catch (JAXBException e) {
            LOGGER.log(Level.SEVERE, "Failed to generate the schema for the JAX-B elements", e);
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to generate the schema for the JAX-B elements due to an IO error", e);
        }
        if (introspector != null) {
            final JAXBIntrospector copy = introspector;
            return new WadlGenerator.Resolver(){

                @Override
                public <T> T resolve(Class type, MediaType mt, Class<T> resolvedType) {
                    if (!QName.class.equals(resolvedType)) {
                        return null;
                    }
                    if (!WadlGeneratorJAXBGrammarGenerator.this.acceptMediaType(mt)) {
                        return null;
                    }
                    Object parameterClassInstance = null;
                    try {
                        Constructor defaultConstructor = type.getDeclaredConstructor(new Class[0]);
                        defaultConstructor.setAccessible(true);
                        parameterClassInstance = defaultConstructor.newInstance(new Object[0]);
                    }
                    catch (InstantiationException ex) {
                        LOGGER.log(Level.FINE, null, ex);
                    }
                    catch (IllegalAccessException ex) {
                        LOGGER.log(Level.FINE, null, ex);
                    }
                    catch (IllegalArgumentException ex) {
                        LOGGER.log(Level.FINE, null, ex);
                    }
                    catch (InvocationTargetException ex) {
                        LOGGER.log(Level.FINE, null, ex);
                    }
                    catch (SecurityException ex) {
                        LOGGER.log(Level.FINE, null, ex);
                    }
                    catch (NoSuchMethodException ex) {
                        LOGGER.log(Level.FINE, null, ex);
                    }
                    if (parameterClassInstance == null) {
                        return null;
                    }
                    try {
                        return resolvedType.cast(copy.getElementName(parameterClassInstance));
                    }
                    catch (NullPointerException e) {
                        return null;
                    }
                }
            };
        }
        return null;
    }

    @Override
    protected AbstractWadlGeneratorGrammarGenerator.WantsName<QName> createParmWantsName(final Param param) {
        return new AbstractWadlGeneratorGrammarGenerator.WantsName<QName>(){

            @Override
            public boolean isElement() {
                return false;
            }

            @Override
            public void setName(QName name) {
                param.setType(name);
            }
        };
    }

    @Override
    protected AbstractWadlGeneratorGrammarGenerator.WantsName<QName> createRepresentationWantsName(final Representation rt) {
        return new AbstractWadlGeneratorGrammarGenerator.WantsName<QName>(){

            @Override
            public boolean isElement() {
                return true;
            }

            @Override
            public void setName(QName name) {
                rt.setElement(name);
            }
        };
    }
}

