/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalContextAdapterImpl;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.runtime.resource.Resource;

public class Template
extends Resource {
    private VelocityException errorCondition = null;

    public Template() {
        this.setType(1);
    }

    @Override
    public boolean process() throws ResourceNotFoundException, ParseErrorException, IOException {
        this.data = null;
        InputStream is = null;
        this.errorCondition = null;
        try {
            is = this.resourceLoader.getResourceStream(this.name);
        }
        catch (ResourceNotFoundException rnfe) {
            this.errorCondition = rnfe;
            throw rnfe;
        }
        if (is != null) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(is, this.encoding));
                this.data = this.rsvc.parse(br, this.name);
                this.initDocument();
                boolean bl = true;
                return bl;
            }
            catch (UnsupportedEncodingException uce) {
                String msg = "Template.process : Unsupported input encoding : " + this.encoding + " for template " + this.name;
                this.errorCondition = new ParseErrorException(msg);
                throw this.errorCondition;
            }
            catch (ParseException pex) {
                this.errorCondition = new ParseErrorException(pex);
                throw this.errorCondition;
            }
            catch (TemplateInitException pex) {
                this.errorCondition = new ParseErrorException(pex);
                throw this.errorCondition;
            }
            catch (RuntimeException e) {
                throw new RuntimeException("Exception thrown processing Template " + this.getName(), e);
            }
            finally {
                is.close();
            }
        }
        this.errorCondition = new ResourceNotFoundException("Unknown resource error for resource " + this.name);
        throw this.errorCondition;
    }

    public void initDocument() throws TemplateInitException {
        InternalContextAdapterImpl ica = new InternalContextAdapterImpl(new VelocityContext());
        try {
            ica.pushCurrentTemplateName(this.name);
            ica.setCurrentResource(this);
            ((SimpleNode)this.data).init(ica, this.rsvc);
        }
        finally {
            ica.popCurrentTemplateName();
            ica.setCurrentResource(null);
        }
    }

    public void merge(Context context, Writer writer) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, IOException {
        this.merge(context, writer, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void merge(Context context, Writer writer, List macroLibraries) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, IOException {
        if (this.errorCondition != null) {
            throw this.errorCondition;
        }
        if (this.data != null) {
            InternalContextAdapterImpl ica = new InternalContextAdapterImpl(context);
            ica.setMacroLibraries(macroLibraries);
            if (macroLibraries != null) {
                for (int i = 0; i < macroLibraries.size(); ++i) {
                    try {
                        this.rsvc.getTemplate((String)macroLibraries.get(i));
                        continue;
                    }
                    catch (ResourceNotFoundException re) {
                        this.rsvc.getLog().error("template.merge(): cannot find template " + (String)macroLibraries.get(i));
                        throw re;
                    }
                    catch (ParseErrorException pe) {
                        this.rsvc.getLog().error("template.merge(): syntax error in template " + (String)macroLibraries.get(i) + ".");
                        throw pe;
                    }
                    catch (Exception e) {
                        throw new RuntimeException("Template.merge(): parse failed in template  " + (String)macroLibraries.get(i) + ".", e);
                    }
                }
            }
            try {
                ica.pushCurrentTemplateName(this.name);
                ica.setCurrentResource(this);
                ((SimpleNode)this.data).render(ica, writer);
            }
            finally {
                ica.popCurrentTemplateName();
                ica.setCurrentResource(null);
            }
        } else {
            String msg = "Template.merge() failure. The document is null, most likely due to parsing error.";
            throw new RuntimeException(msg);
        }
    }
}

