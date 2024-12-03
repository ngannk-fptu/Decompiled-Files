/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sourcemap.ReadableSourceMap
 *  com.atlassian.sourcemap.ReadableSourceMapImpl
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  org.apache.commons.io.IOUtils
 *  org.dom4j.Document
 *  org.dom4j.DocumentException
 *  org.dom4j.DocumentHelper
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.webresource.impl.support;

import com.atlassian.plugin.webresource.impl.snapshot.Bundle;
import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;
import com.atlassian.plugin.webresource.impl.support.Content;
import com.atlassian.sourcemap.ReadableSourceMap;
import com.atlassian.sourcemap.ReadableSourceMapImpl;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Support {
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"webresource");

    public static Predicate<Bundle> efficientAndPredicate(List<Predicate<Bundle>> predicates) {
        if (predicates.size() == 0) {
            return Predicates.alwaysTrue();
        }
        if (predicates.size() == 1) {
            return predicates.get(0);
        }
        return Predicates.and(predicates);
    }

    public static String indent(String string, String indent) {
        return indent + string.replaceAll("\\n", "\n" + indent);
    }

    public static boolean equals(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }

    public static void copy(InputStream in, OutputStream out) {
        try {
            IOUtils.copy((InputStream)in, (OutputStream)out);
        }
        catch (IOException e) {
            Support.logIOException(e);
        }
        finally {
            IOUtils.closeQuietly((InputStream)in);
            try {
                out.flush();
            }
            catch (IOException e) {
                LOGGER.debug("Error flushing output stream", (Throwable)e);
            }
        }
    }

    public static void logIOException(IOException e) {
        if (e instanceof SocketException && "Broken pipe".equals(e.getMessage())) {
            LOGGER.trace("Client Abort error", (Throwable)e);
        } else {
            LOGGER.debug("IO Exception", (Throwable)e);
        }
    }

    public static Element parseXml(String xml) {
        Document document;
        try {
            document = DocumentHelper.parseText((String)xml);
        }
        catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        return document.getRootElement();
    }

    public static ReadableSourceMap getSourceMap(String resourcePath, Resource resource, String sourceUrl) {
        String pathForSourceMap = resourcePath + ".map";
        InputStream sourceMapAsStream = resource.getStreamFor(pathForSourceMap);
        if (sourceMapAsStream == null) {
            return null;
        }
        return ReadableSourceMapImpl.fromSource((InputStream)sourceMapAsStream);
    }

    public static String inspect(Content content) {
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        ReadableSourceMap sourceMap = content.writeTo(buff, true);
        try {
            buff.write("\n".getBytes());
            if (sourceMap != null) {
                buff.write(ReadableSourceMap.toWritableSourceMap((ReadableSourceMap)sourceMap).generate().getBytes());
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ((Object)buff).toString();
    }
}

