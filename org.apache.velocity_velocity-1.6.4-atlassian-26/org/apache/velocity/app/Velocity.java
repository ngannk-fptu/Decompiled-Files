/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.ExtendedProperties
 */
package org.apache.velocity.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Properties;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.log.Log;

public class Velocity
implements RuntimeConstants {
    public static void init() throws Exception {
        RuntimeSingleton.init();
    }

    public static void init(String propsFilename) throws Exception {
        RuntimeSingleton.init(propsFilename);
    }

    public static void init(Properties p) throws Exception {
        RuntimeSingleton.init(p);
    }

    public static void setProperty(String key, Object value) {
        RuntimeSingleton.setProperty(key, value);
    }

    public static void addProperty(String key, Object value) {
        RuntimeSingleton.addProperty(key, value);
    }

    public static void clearProperty(String key) {
        RuntimeSingleton.clearProperty(key);
    }

    public static void setExtendedProperties(ExtendedProperties configuration) {
        RuntimeSingleton.setConfiguration(configuration);
    }

    public static Object getProperty(String key) {
        return RuntimeSingleton.getProperty(key);
    }

    public static boolean evaluate(Context context, Writer out, String logTag, String instring) throws ParseErrorException, MethodInvocationException, ResourceNotFoundException, IOException {
        return RuntimeSingleton.getRuntimeServices().evaluate(context, out, logTag, instring);
    }

    public static boolean evaluate(Context context, Writer writer, String logTag, InputStream instream) throws ParseErrorException, MethodInvocationException, ResourceNotFoundException, IOException {
        BufferedReader br = null;
        String encoding = null;
        try {
            encoding = RuntimeSingleton.getString("input.encoding", "ISO-8859-1");
            br = new BufferedReader(new InputStreamReader(instream, encoding));
        }
        catch (UnsupportedEncodingException uce) {
            String msg = "Unsupported input encoding : " + encoding + " for template " + logTag;
            throw new ParseErrorException(msg);
        }
        return Velocity.evaluate(context, writer, logTag, br);
    }

    public static boolean evaluate(Context context, Writer writer, String logTag, Reader reader) throws ParseErrorException, MethodInvocationException, ResourceNotFoundException, IOException {
        return RuntimeSingleton.getRuntimeServices().evaluate(context, writer, logTag, reader);
    }

    public static boolean invokeVelocimacro(String vmName, String logTag, String[] params, Context context, Writer writer) {
        try {
            return RuntimeSingleton.getRuntimeServices().invokeVelocimacro(vmName, logTag, params, context, writer);
        }
        catch (IOException ioe) {
            String msg = "Velocity.invokeVelocimacro(" + vmName + ") failed";
            Velocity.getLog().error(msg, ioe);
            throw new VelocityException(msg, ioe);
        }
    }

    public static boolean mergeTemplate(String templateName, Context context, Writer writer) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception {
        return Velocity.mergeTemplate(templateName, RuntimeSingleton.getString("input.encoding", "ISO-8859-1"), context, writer);
    }

    public static boolean mergeTemplate(String templateName, String encoding, Context context, Writer writer) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception {
        Template template = RuntimeSingleton.getTemplate(templateName, encoding);
        if (template == null) {
            String msg = "Velocity.mergeTemplate() was unable to load template '" + templateName + "'";
            Velocity.getLog().error(msg);
            throw new ResourceNotFoundException(msg);
        }
        template.merge(context, writer);
        return true;
    }

    public static Template getTemplate(String name) throws ResourceNotFoundException, ParseErrorException, Exception {
        return RuntimeSingleton.getTemplate(name);
    }

    public static Template getTemplate(String name, String encoding) throws ResourceNotFoundException, ParseErrorException, Exception {
        return RuntimeSingleton.getTemplate(name, encoding);
    }

    public static boolean resourceExists(String resourceName) {
        return RuntimeSingleton.getLoaderNameForResource(resourceName) != null;
    }

    public static Log getLog() {
        return RuntimeSingleton.getLog();
    }

    public static void warn(Object message) {
        Velocity.getLog().warn(message);
    }

    public static void info(Object message) {
        Velocity.getLog().info(message);
    }

    public static void error(Object message) {
        Velocity.getLog().error(message);
    }

    public static void debug(Object message) {
        Velocity.getLog().debug(message);
    }

    public static void setApplicationAttribute(Object key, Object value) {
        RuntimeSingleton.getRuntimeInstance().setApplicationAttribute(key, value);
    }

    public static boolean templateExists(String resourceName) {
        return Velocity.resourceExists(resourceName);
    }
}

