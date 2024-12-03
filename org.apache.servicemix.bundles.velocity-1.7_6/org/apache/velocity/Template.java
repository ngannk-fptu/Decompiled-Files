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
import org.apache.velocity.runtime.directive.Scope;
import org.apache.velocity.runtime.directive.StopCommand;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.runtime.resource.Resource;

public class Template
extends Resource {
    private String scopeName = "template";
    private boolean provideScope = false;
    private VelocityException errorCondition = null;

    public Template() {
        this.setType(1);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public boolean process() throws ResourceNotFoundException, ParseErrorException {
        boolean bl;
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
        if (is == null) {
            this.errorCondition = new ResourceNotFoundException("Unknown resource error for resource " + this.name);
            throw this.errorCondition;
        }
        try {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(is, this.encoding));
                this.data = this.rsvc.parse(br, this.name);
                this.initDocument();
                bl = true;
                Object var5_10 = null;
            }
            catch (UnsupportedEncodingException uce) {
                String msg = "Template.process : Unsupported input encoding : " + this.encoding + " for template " + this.name;
                this.errorCondition = new ParseErrorException(msg);
                throw this.errorCondition;
            }
            catch (ParseException pex) {
                this.errorCondition = new ParseErrorException(pex, this.name);
                throw this.errorCondition;
            }
            catch (TemplateInitException pex) {
                this.errorCondition = new ParseErrorException(pex, this.name);
                throw this.errorCondition;
            }
            catch (RuntimeException e) {
                this.errorCondition = new VelocityException("Exception thrown processing Template " + this.getName(), e);
                throw this.errorCondition;
            }
        }
        catch (Throwable throwable) {
            Object var5_11 = null;
            try {
                is.close();
                throw throwable;
            }
            catch (IOException e) {
                if (this.errorCondition != null) throw throwable;
                throw new VelocityException(e);
            }
        }
        try {}
        catch (IOException e) {
            if (this.errorCondition != null) return bl;
            throw new VelocityException(e);
        }
        is.close();
        return bl;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void initDocument() throws TemplateInitException {
        InternalContextAdapterImpl ica = new InternalContextAdapterImpl(new VelocityContext());
        try {
            ica.pushCurrentTemplateName(this.name);
            ica.setCurrentResource(this);
            ((SimpleNode)this.data).init(ica, this.rsvc);
            String property = this.scopeName + '.' + "provide.scope.control";
            this.provideScope = this.rsvc.getBoolean(property, this.provideScope);
        }
        finally {
            ica.popCurrentTemplateName();
            ica.setCurrentResource(null);
        }
    }

    public void merge(Context context, Writer writer) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        this.merge(context, writer, null);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void merge(Context context, Writer writer, List macroLibraries) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        if (this.errorCondition != null) {
            throw this.errorCondition;
        }
        if (this.data == null) {
            String msg = "Template.merge() failure. The document is null, most likely due to parsing error.";
            throw new RuntimeException(msg);
        }
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
        if (this.provideScope) {
            ica.put(this.scopeName, new Scope(this, ica.get(this.scopeName)));
        }
        try {
            try {
                ica.pushCurrentTemplateName(this.name);
                ica.setCurrentResource(this);
                ((SimpleNode)this.data).render(ica, writer);
            }
            catch (StopCommand stop) {
                if (!stop.isFor(this)) {
                    throw stop;
                }
                if (this.rsvc.getLog().isDebugEnabled()) {
                    this.rsvc.getLog().debug(stop.getMessage());
                }
                Object var8_13 = null;
                ica.popCurrentTemplateName();
                ica.setCurrentResource(null);
                if (!this.provideScope) return;
                Object obj2 = ica.get(this.scopeName);
                if (!(obj2 instanceof Scope)) return;
                Scope scope = (Scope)obj2;
                if (scope.getParent() != null) {
                    ica.put(this.scopeName, scope.getParent());
                    return;
                }
                if (scope.getReplaced() != null) {
                    ica.put(this.scopeName, scope.getReplaced());
                    return;
                }
                ica.remove(this.scopeName);
                return;
            }
            catch (IOException e) {
                throw new VelocityException("IO Error rendering template '" + this.name + "'", e);
            }
            Object var8_12 = null;
            ica.popCurrentTemplateName();
            ica.setCurrentResource(null);
            if (!this.provideScope) return;
        }
        catch (Throwable throwable) {
            Object var8_14 = null;
            ica.popCurrentTemplateName();
            ica.setCurrentResource(null);
            if (!this.provideScope) throw throwable;
            Object obj2 = ica.get(this.scopeName);
            if (!(obj2 instanceof Scope)) throw throwable;
            Scope scope = (Scope)obj2;
            if (scope.getParent() != null) {
                ica.put(this.scopeName, scope.getParent());
                throw throwable;
            }
            if (scope.getReplaced() != null) {
                ica.put(this.scopeName, scope.getReplaced());
                throw throwable;
            }
            ica.remove(this.scopeName);
            throw throwable;
        }
        Object obj2 = ica.get(this.scopeName);
        if (!(obj2 instanceof Scope)) return;
        Scope scope = (Scope)obj2;
        if (scope.getParent() != null) {
            ica.put(this.scopeName, scope.getParent());
            return;
        }
        if (scope.getReplaced() != null) {
            ica.put(this.scopeName, scope.getReplaced());
            return;
        }
        ica.remove(this.scopeName);
    }
}

