/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.ContextUtils
 *  com.atlassian.confluence.velocity.context.ChainedVelocityContext
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timers
 *  com.atlassian.velocity.htmlsafe.HtmlFragment
 *  com.opensymphony.module.sitemesh.Decorator
 *  com.opensymphony.module.sitemesh.Factory
 *  com.opensymphony.module.sitemesh.HTMLPage
 *  com.opensymphony.module.sitemesh.Page
 *  com.opensymphony.module.sitemesh.PageParser
 *  com.opensymphony.module.sitemesh.SitemeshBufferWriter
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.velocity.VelocityContext
 *  org.apache.velocity.context.Context
 *  org.apache.velocity.context.InternalContextAdapter
 *  org.apache.velocity.exception.MethodInvocationException
 *  org.apache.velocity.exception.ParseErrorException
 *  org.apache.velocity.exception.ResourceNotFoundException
 *  org.apache.velocity.runtime.RuntimeServices
 *  org.apache.velocity.runtime.directive.Directive
 *  org.apache.velocity.runtime.parser.node.Node
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.velocity;

import com.atlassian.confluence.impl.profiling.DecoratorTimings;
import com.atlassian.confluence.setup.settings.DarkFeatures;
import com.atlassian.confluence.setup.sitemesh.SitemeshContextItemProvider;
import com.atlassian.confluence.setup.sitemesh.SitemeshPageBodyRenderable;
import com.atlassian.confluence.setup.sitemesh.SitemeshPageHeadRenderable;
import com.atlassian.confluence.util.profiling.VelocitySitemeshPage;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.confluence.velocity.ContextUtils;
import com.atlassian.confluence.velocity.context.ChainedVelocityContext;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timers;
import com.atlassian.velocity.htmlsafe.HtmlFragment;
import com.opensymphony.module.sitemesh.Decorator;
import com.opensymphony.module.sitemesh.Factory;
import com.opensymphony.module.sitemesh.HTMLPage;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.SitemeshBufferWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ApplyDecoratorDirective
extends Directive {
    private static final Logger log = LoggerFactory.getLogger(ApplyDecoratorDirective.class);
    private static final int BUFFER_INITIAL_SIZE = 1024;
    static final String STACK_KEY = "DirectiveStack";
    private final ThreadLocal<Map<String, Object>> params = ThreadLocal.withInitial(HashMap::new);

    public String getName() {
        return "applyDecorator";
    }

    public int getType() {
        return 1;
    }

    public void init(RuntimeServices services, InternalContextAdapter adapter, Node node) {
        super.init(services, adapter, node);
        int numArgs = node.jjtGetNumChildren();
        if (numArgs < 2) {
            services.getLog().error((Object)"#applyDecorator error: You need a decorator name in order to use this tag");
        } else if (numArgs > 3) {
            services.getLog().error((Object)"#applyDecorator error: Too many parameters");
        }
    }

    public boolean render(InternalContextAdapter adapter, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        DirectiveStack stack = (DirectiveStack)adapter.get(STACK_KEY);
        if (stack == null) {
            stack = new DirectiveStack();
            adapter.put(STACK_KEY, (Object)stack);
        }
        stack.push(this);
        try {
            boolean bl;
            block24: {
                Ticker ignored = Timers.start((String)"ApplyDecoratorDirective.render()");
                try {
                    this.params.get().clear();
                    HttpServletRequest request = (HttpServletRequest)adapter.get("request");
                    if (request == null) {
                        throw new IOException("No request object in context.");
                    }
                    HttpServletResponse response = (HttpServletResponse)adapter.get("response");
                    if (response == null) {
                        throw new IOException("No response object in context.");
                    }
                    String decoratorName = (String)node.jjtGetChild(0).value(adapter);
                    Node bodyNode = ApplyDecoratorDirective.getBodyNode(node);
                    Factory factory = (Factory)request.getServletContext().getAttribute("sitemesh.factory");
                    if (factory == null) {
                        throw new IllegalStateException("No SiteMesh Factory found in ServletContext");
                    }
                    Decorator decorator = factory.getDecoratorMapper().getNamedDecorator(request, decoratorName);
                    if (decorator != null) {
                        try (DecoratorTimings.DecoratorTimer timer = DecoratorTimings.newDecoratorTimer(decorator, request);){
                            HTMLPage page = ApplyDecoratorDirective.getRenderedTagBody(adapter, bodyNode, factory);
                            Context context = this.buildContext(adapter, node, request, page);
                            try {
                                String renderedDecorator = VelocityUtils.getRenderedTemplateWithoutSwallowingErrors(decorator.getPage(), context);
                                writer.write(renderedDecorator);
                            }
                            catch (Exception e) {
                                throw new RuntimeException("Error rendering template for decorator " + decoratorName, e);
                            }
                        }
                        this.params.get().clear();
                    } else {
                        bodyNode.render(adapter, writer);
                    }
                    bl = true;
                    if (ignored == null) break block24;
                }
                catch (Throwable throwable) {
                    if (ignored != null) {
                        try {
                            ignored.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                ignored.close();
            }
            return bl;
        }
        finally {
            stack.pop();
            this.params.remove();
        }
    }

    private static HTMLPage getRenderedTagBody(InternalContextAdapter adapter, Node bodyNode, Factory factory) throws IOException {
        SitemeshBufferWriter bodyContent = new SitemeshBufferWriter(1024);
        bodyNode.render(adapter, (Writer)bodyContent);
        PageParser parser = factory.getPageParser("text/html");
        return (HTMLPage)parser.parse(bodyContent.getSitemeshBuffer());
    }

    private static Node getBodyNode(Node node) {
        int bodyNode = 1;
        if (node.jjtGetNumChildren() == 3) {
            bodyNode = 2;
        }
        return node.jjtGetChild(bodyNode);
    }

    private Context buildContext(InternalContextAdapter adapter, Node node, HttpServletRequest request, HTMLPage tagBody) {
        ChainedVelocityContext context = new ChainedVelocityContext((Context)adapter);
        VelocityContext sitemeshContext = new VelocityContext(SitemeshContextItemProvider.getProvider(request).getContextMap());
        ContextUtils.putAll((Context)context, (Context)sitemeshContext);
        context.put("sitemeshPage", (Object)new VelocitySitemeshPage(tagBody));
        if (ContainerManager.isContainerSetup()) {
            context.put("comments", (Object)(DarkFeatures.isDarkFeatureEnabled("editor.slow.comment.disable") ? "quickComments" : "comments"));
        } else {
            context.put("comments", (Object)"comments");
        }
        if (node.jjtGetNumChildren() == 3) {
            context.put("title", (Object)new HtmlFragment(node.jjtGetChild(1).value(adapter)));
        } else {
            context.put("title", (Object)new HtmlFragment((Object)tagBody.getTitle()));
        }
        context.put("body", (Object)new SitemeshPageBodyRenderable((Page)tagBody));
        context.put("head", (Object)new SitemeshPageHeadRenderable(tagBody));
        context.put("params", this.params.get());
        return context;
    }

    public void addParameter(String paramName, Object paramValue) {
        if (paramValue instanceof Map && "params".equals(paramName)) {
            this.params.get().putAll((Map)paramValue);
        } else {
            this.params.get().put(paramName, paramValue);
        }
    }

    public static class DirectiveStack {
        private final Stack stack = new Stack();

        public ApplyDecoratorDirective pop() {
            try {
                return (ApplyDecoratorDirective)((Object)this.stack.pop());
            }
            catch (EmptyStackException e) {
                log.info("Someone's been popping out of order! " + e.getMessage(), (Throwable)e);
                return null;
            }
        }

        public void push(ApplyDecoratorDirective directive) {
            this.stack.push(directive);
        }

        public ApplyDecoratorDirective peek() {
            return this.stack.isEmpty() ? null : (ApplyDecoratorDirective)((Object)this.stack.peek());
        }
    }
}

