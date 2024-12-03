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
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.log.Log;

public class VelocityEngine
implements RuntimeConstants {
    private RuntimeInstance ri = new RuntimeInstance();

    public VelocityEngine() {
    }

    public VelocityEngine(String propsFilename) throws Exception {
        this.ri.init(propsFilename);
    }

    public VelocityEngine(Properties p) throws Exception {
        this.ri.init(p);
    }

    public void init() throws Exception {
        this.ri.init();
    }

    public void init(String propsFilename) throws Exception {
        this.ri.init(propsFilename);
    }

    public void init(Properties p) throws Exception {
        this.ri.init(p);
    }

    public void setProperty(String key, Object value) {
        this.ri.setProperty(key, value);
    }

    public void addProperty(String key, Object value) {
        this.ri.addProperty(key, value);
    }

    public void clearProperty(String key) {
        this.ri.clearProperty(key);
    }

    public void setExtendedProperties(ExtendedProperties configuration) {
        this.ri.setConfiguration(configuration);
    }

    public Object getProperty(String key) {
        return this.ri.getProperty(key);
    }

    public boolean evaluate(Context context, Writer out, String logTag, String instring) throws ParseErrorException, MethodInvocationException, ResourceNotFoundException, IOException {
        return this.ri.evaluate(context, out, logTag, instring);
    }

    public boolean evaluate(Context context, Writer writer, String logTag, InputStream instream) throws ParseErrorException, MethodInvocationException, ResourceNotFoundException, IOException {
        BufferedReader br = null;
        String encoding = null;
        try {
            encoding = this.ri.getString("input.encoding", "ISO-8859-1");
            br = new BufferedReader(new InputStreamReader(instream, encoding));
        }
        catch (UnsupportedEncodingException uce) {
            String msg = "Unsupported input encoding : " + encoding + " for template " + logTag;
            throw new ParseErrorException(msg);
        }
        return this.evaluate(context, writer, logTag, br);
    }

    public boolean evaluate(Context context, Writer writer, String logTag, Reader reader) throws ParseErrorException, MethodInvocationException, ResourceNotFoundException, IOException {
        return this.ri.evaluate(context, writer, logTag, reader);
    }

    public boolean invokeVelocimacro(String vmName, String logTag, String[] params, Context context, Writer writer) throws Exception {
        return this.ri.invokeVelocimacro(vmName, logTag, params, context, writer);
    }

    public boolean mergeTemplate(String templateName, Context context, Writer writer) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception {
        return this.mergeTemplate(templateName, this.ri.getString("input.encoding", "ISO-8859-1"), context, writer);
    }

    public boolean mergeTemplate(String templateName, String encoding, Context context, Writer writer) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception {
        Template template = this.ri.getTemplate(templateName, encoding);
        if (template == null) {
            String msg = "VelocityEngine.mergeTemplate() was unable to load template '" + templateName + "'";
            this.getLog().error(msg);
            throw new ResourceNotFoundException(msg);
        }
        template.merge(context, writer);
        return true;
    }

    public Template getTemplate(String name) throws ResourceNotFoundException, ParseErrorException, Exception {
        return this.ri.getTemplate(name);
    }

    public Template getTemplate(String name, String encoding) throws ResourceNotFoundException, ParseErrorException, Exception {
        return this.ri.getTemplate(name, encoding);
    }

    public boolean resourceExists(String resourceName) {
        return this.ri.getLoaderNameForResource(resourceName) != null;
    }

    public boolean templateExists(String resourceName) {
        return this.resourceExists(resourceName);
    }

    public Log getLog() {
        return this.ri.getLog();
    }

    public void warn(Object message) {
        this.getLog().warn(message);
    }

    public void info(Object message) {
        this.getLog().info(message);
    }

    public void error(Object message) {
        this.getLog().error(message);
    }

    public void debug(Object message) {
        this.getLog().debug(message);
    }

    public void setApplicationAttribute(Object key, Object value) {
        this.ri.setApplicationAttribute(key, value);
    }

    public Object getApplicationAttribute(Object key) {
        return this.ri.getApplicationAttribute(key);
    }

    public VelocityEngine(RuntimeInstance runtimeInstance) {
        this.ri = runtimeInstance;
    }
}

