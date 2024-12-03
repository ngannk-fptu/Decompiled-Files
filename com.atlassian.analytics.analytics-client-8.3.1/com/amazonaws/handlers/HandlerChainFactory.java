/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.handlers;

import com.amazonaws.AmazonClientException;
import com.amazonaws.handlers.RequestHandler;
import com.amazonaws.handlers.RequestHandler2;
import com.amazonaws.util.ClassLoaderHelper;
import com.amazonaws.util.StringUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class HandlerChainFactory {
    private static final String GLOBAL_HANDLER_PATH = "com/amazonaws/global/handlers/request.handler2s";

    public List<RequestHandler2> newRequestHandlerChain(String resource) {
        return this.createRequestHandlerChain(resource, RequestHandler.class);
    }

    public List<RequestHandler2> newRequestHandler2Chain(String resource) {
        return this.createRequestHandlerChain(resource, RequestHandler2.class);
    }

    public List<RequestHandler2> getGlobalHandlers() {
        ArrayList<RequestHandler2> handlers = new ArrayList<RequestHandler2>();
        BufferedReader fileReader = null;
        try {
            ArrayList<URL> globalHandlerListLocations = Collections.list(this.getGlobalHandlerResources());
            for (URL url : globalHandlerListLocations) {
                String requestHandlerClassName;
                fileReader = new BufferedReader(new InputStreamReader(url.openStream(), StringUtils.UTF8));
                while ((requestHandlerClassName = fileReader.readLine()) != null) {
                    RequestHandler2 requestHandler = this.createRequestHandler(requestHandlerClassName, RequestHandler2.class);
                    if (requestHandler == null) continue;
                    handlers.add(requestHandler);
                }
            }
        }
        catch (Exception e) {
            throw new AmazonClientException("Unable to instantiate request handler chain for client: " + e.getMessage(), e);
        }
        finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            }
            catch (IOException iOException) {}
        }
        return handlers;
    }

    private Enumeration<URL> getGlobalHandlerResources() throws IOException {
        if (HandlerChainFactory.class.getClassLoader() == null) {
            return ClassLoader.getSystemResources(GLOBAL_HANDLER_PATH);
        }
        return HandlerChainFactory.class.getClassLoader().getResources(GLOBAL_HANDLER_PATH);
    }

    private RequestHandler2 createRequestHandler(String handlerClassName, Class<?> handlerApiClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if ((handlerClassName = handlerClassName.trim()).equals("")) {
            return null;
        }
        Class<?> requestHandlerClass = ClassLoaderHelper.loadClass(handlerClassName, handlerApiClass, this.getClass());
        Object requestHandlerObject = requestHandlerClass.newInstance();
        if (handlerApiClass.isInstance(requestHandlerObject)) {
            if (handlerApiClass == RequestHandler2.class) {
                return (RequestHandler2)requestHandlerObject;
            }
            if (handlerApiClass == RequestHandler.class) {
                return RequestHandler2.adapt((RequestHandler)requestHandlerObject);
            }
            throw new IllegalStateException();
        }
        throw new AmazonClientException("Unable to instantiate request handler chain for client.  Listed request handler ('" + handlerClassName + "') does not implement the " + handlerApiClass + " API.");
    }

    private List<RequestHandler2> createRequestHandlerChain(String resource, Class<?> handlerApiClass) {
        ArrayList<RequestHandler2> handlers = new ArrayList<RequestHandler2>();
        BufferedReader reader = null;
        try {
            String requestHandlerClassName;
            InputStream input = this.getClass().getResourceAsStream(resource);
            if (input == null) {
                ArrayList<RequestHandler2> arrayList = handlers;
                return arrayList;
            }
            reader = new BufferedReader(new InputStreamReader(input, StringUtils.UTF8));
            while ((requestHandlerClassName = reader.readLine()) != null) {
                RequestHandler2 requestHandler = this.createRequestHandler(requestHandlerClassName, handlerApiClass);
                if (requestHandler == null) continue;
                handlers.add(requestHandler);
            }
        }
        catch (Exception e) {
            throw new AmazonClientException("Unable to instantiate request handler chain for client: " + e.getMessage(), e);
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (IOException iOException) {}
        }
        return handlers;
    }
}

