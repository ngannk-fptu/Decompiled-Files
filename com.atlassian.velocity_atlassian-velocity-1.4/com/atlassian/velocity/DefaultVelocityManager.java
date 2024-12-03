/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ClassLoaderUtils
 *  org.apache.commons.lang3.exception.ExceptionUtils
 *  org.apache.commons.text.StringEscapeUtils
 *  org.apache.log4j.Logger
 *  org.apache.velocity.VelocityContext
 *  org.apache.velocity.app.VelocityEngine
 *  org.apache.velocity.context.Context
 *  org.apache.velocity.exception.MethodInvocationException
 *  org.apache.velocity.exception.ResourceNotFoundException
 *  org.apache.velocity.exception.VelocityException
 */
package com.atlassian.velocity;

import com.atlassian.core.util.ClassLoaderUtils;
import com.atlassian.velocity.VelocityManager;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.VelocityException;

public class DefaultVelocityManager
implements VelocityManager {
    private static final Logger log = Logger.getLogger(DefaultVelocityManager.class);
    private VelocityEngine ve;

    @Override
    public String getBody(String templateDirectory, String template, Map<String, Object> contextParameters) throws VelocityException {
        return this.getEncodedBody(templateDirectory, template, null, null, contextParameters);
    }

    @Override
    public String getBody(String templateDirectory, String template, String baseurl, Map<String, Object> contextParameters) throws VelocityException {
        return this.getEncodedBody(templateDirectory, template, baseurl, null, contextParameters);
    }

    @Override
    public String getEncodedBody(String templateDirectory, String template, String encoding, Map<String, Object> contextParameters) throws VelocityException {
        return this.getEncodedBody(templateDirectory, template, null, encoding, contextParameters);
    }

    @Override
    public String getEncodedBody(String templateDirectory, String template, String baseurl, String encoding, Map<String, Object> contextParameters) throws VelocityException {
        return this.getEncodedBody(templateDirectory, template, baseurl, encoding, (Context)this.createVelocityContext(this.createContextParams(baseurl, contextParameters)));
    }

    @Override
    public String getEncodedBody(String templateDirectory, String template, String baseurl, String encoding, Context context) throws VelocityException {
        try {
            StringWriter sw = new StringWriter();
            this.writeEncodedBodyImpl(sw, templateDirectory, template, encoding, context);
            return sw.toString();
        }
        catch (IOException canThisEvenHappenedOnStringWriter) {
            return this.getErrorMessageForException(canThisEvenHappenedOnStringWriter);
        }
    }

    @Override
    public void writeEncodedBodyForContent(Writer writer, String contentFragment, Context context) throws IOException {
        if (contentFragment == null) {
            throw new VelocityException("Trying to render with no content.");
        }
        try {
            this.getVe().evaluate(context, writer, "getEncodedBodyFromContent", contentFragment);
        }
        catch (Exception e) {
            this.exceptionHandling(writer, e, "", "");
        }
    }

    @Override
    public void writeEncodedBody(Writer writer, String templateDirectory, String template, String encoding, Context context) throws VelocityException, IOException {
        this.writeEncodedBodyImpl(writer, templateDirectory, template, encoding, context);
    }

    private void writeEncodedBodyImpl(Writer writer, String templateDirectory, String template, String encoding, Context context) throws VelocityException, IOException {
        if (template == null) {
            throw new VelocityException("Trying to send mail with no template.");
        }
        try {
            if (encoding == null) {
                this.getVe().mergeTemplate(templateDirectory + template, context, writer);
            } else {
                this.getVe().mergeTemplate(templateDirectory + template, encoding, context, writer);
            }
        }
        catch (Exception e) {
            this.exceptionHandling(writer, e, templateDirectory, template);
        }
    }

    private void exceptionHandling(Writer writer, Exception e, String templateDirectory, String template) throws IOException {
        if (e instanceof ResourceNotFoundException) {
            log.error((Object)("ResourceNotFoundException occurred whilst loading resource " + template));
            URL templateUrl = ClassLoaderUtils.getResource((String)(templateDirectory + template), this.getClass());
            if (templateUrl == null) {
                throw new VelocityException("Could not find template '" + templateDirectory + template + "' ensure it is in the classpath.");
            }
            writer.write("Could not locate resource " + templateDirectory + template);
        } else if (e instanceof MethodInvocationException) {
            Throwable t = ((MethodInvocationException)e).getWrappedThrowable();
            log.error((Object)("MethodInvocationException occurred getting message body from Velocity: " + t), t);
            writer.write(this.getErrorMessageForException(e));
        } else {
            log.error((Object)("Exception getting message body from Velocity: " + e), (Throwable)e);
            writer.write(this.getErrorMessageForException(e));
        }
    }

    protected Map<String, Object> createContextParams(String baseurl, Map<String, Object> contextParameters) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("baseurl", baseurl);
        params.put("formatter", this.getDateFormat());
        params.putAll(contextParameters);
        return params;
    }

    protected VelocityContext createVelocityContext(Map<String, Object> params) {
        if (params != null) {
            params.put("ctx", params);
        }
        return new VelocityContext(params){

            public Object put(String key, Object value) {
                if (key == null) {
                    return null;
                }
                return this.internalPut(key, value);
            }
        };
    }

    @Override
    public String getEncodedBodyForContent(String content, String baseurl, Map<String, Object> contextParameters) throws VelocityException {
        if (content == null) {
            throw new VelocityException("Trying to send mail with no content.");
        }
        try {
            VelocityContext context = this.createVelocityContext(this.createContextParams(baseurl, contextParameters));
            StringWriter writer = new StringWriter();
            this.getVe().evaluate((Context)context, (Writer)writer, "getEncodedBodyFromContent", content);
            return writer.toString();
        }
        catch (Exception e) {
            log.error((Object)("Exception getting message body from Velocity: " + e), (Throwable)e);
            return this.getErrorMessageForException(e);
        }
    }

    protected String getErrorMessageForException(Exception e) {
        return "An error occurred whilst rendering this message.  Please contact the administrators, and inform them of this bug.\n\nDetails:\n-------\n" + StringEscapeUtils.escapeHtml4((String)ExceptionUtils.getStackTrace((Throwable)e));
    }

    @Override
    public DateFormat getDateFormat() {
        return new SimpleDateFormat("EEE, d MMM yyyy h:mm a");
    }

    protected synchronized VelocityEngine getVe() {
        if (this.ve == null) {
            this.ve = new VelocityEngine();
            this.initVe(this.ve);
        }
        return this.ve;
    }

    protected void initVe(VelocityEngine velocityEngine) {
        try {
            Properties props = new Properties();
            try {
                props.load(ClassLoaderUtils.getResourceAsStream((String)"velocity.properties", this.getClass()));
            }
            catch (Exception e) {
                props.put("resource.loader", "class");
                props.put("class.resource.loader.description", "Velocity Classpath Resource Loader");
                props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            }
            velocityEngine.init(props);
        }
        catch (Exception e) {
            log.error((Object)("Exception initialising Velocity: " + e), (Throwable)e);
        }
    }
}

