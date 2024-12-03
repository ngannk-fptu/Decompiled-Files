/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.consumer.ConsumerCreationException
 *  com.atlassian.oauth.consumer.ConsumerService
 *  com.atlassian.oauth.util.RSAKeys
 *  com.atlassian.templaterenderer.RenderingException
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.oauth.consumer.internal.servlet;

import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.consumer.ConsumerCreationException;
import com.atlassian.oauth.consumer.ConsumerService;
import com.atlassian.oauth.util.RSAKeys;
import com.atlassian.templaterenderer.RenderingException;
import com.atlassian.templaterenderer.TemplateRenderer;
import java.io.IOException;
import java.io.Writer;
import java.security.Key;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ConsumerInfoServlet
extends HttpServlet {
    private final ConsumerService store;
    private final TemplateRenderer renderer;

    public ConsumerInfoServlet(ConsumerService store, TemplateRenderer renderer) {
        this.store = Objects.requireNonNull(store, "store");
        this.renderer = Objects.requireNonNull(renderer, "renderer");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("application/xml;charset=UTF-8");
        Consumer consumer = this.store.getConsumer();
        Map<String, Object> context = Stream.of(new AbstractMap.SimpleImmutableEntry<String, Consumer>("consumer", consumer), new AbstractMap.SimpleImmutableEntry<String, String>("encodedPublicKey", RSAKeys.toPemEncoding((Key)consumer.getPublicKey()))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        try {
            this.renderer.render("view.xml.vm", context, (Writer)response.getWriter());
        }
        catch (ConsumerCreationException | RenderingException e) {
            throw new ServletException(e);
        }
    }
}

