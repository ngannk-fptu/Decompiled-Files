/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.url;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

class InnerJarURLConnection
extends JarURLConnection {
    private URL baseResource;
    private String[] segments;
    private InputStream in;

    public InnerJarURLConnection(URL url) throws IOException {
        String extraText;
        url = InnerJarURLConnection.normaliseURL(url);
        super(url);
        String baseText = url.getPath();
        int bangLoc = baseText.indexOf(33);
        String baseResourceText = baseText.substring(0, bangLoc);
        if (bangLoc <= baseText.length() - 2 && baseText.charAt(bangLoc + 1) == '/') {
            extraText = bangLoc + 2 == baseText.length() ? "" : baseText.substring(bangLoc + 1);
        } else {
            throw new MalformedURLException("No !/ in url: " + url.toExternalForm());
        }
        ArrayList<String> segments = new ArrayList<String>();
        StringTokenizer tokens = new StringTokenizer(extraText, "!");
        while (tokens.hasMoreTokens()) {
            segments.add(tokens.nextToken());
        }
        this.segments = segments.toArray(new String[0]);
        this.baseResource = new URL(baseResourceText);
    }

    protected static URL normaliseURL(URL url) throws MalformedURLException {
        String text = InnerJarURLConnection.normalizeUrlPath(url.toString());
        if (!text.startsWith("jar:")) {
            text = "jar:" + text;
        }
        if (text.indexOf(33) < 0) {
            text = text + "!/";
        }
        return new URL(text);
    }

    protected String[] getSegments() {
        return this.segments;
    }

    protected URL getBaseResource() {
        return this.baseResource;
    }

    @Override
    public void connect() throws IOException {
        if (this.segments.length == 0) {
            this.setupBaseResourceInputStream();
        } else {
            this.setupPathedInputStream();
        }
    }

    protected void setupBaseResourceInputStream() throws IOException {
        this.in = this.getBaseResource().openStream();
    }

    protected void setupPathedInputStream() throws IOException {
        InputStream curIn = this.getBaseResource().openStream();
        for (int i = 0; i < this.segments.length; ++i) {
            curIn = this.getSegmentInputStream(curIn, this.segments[i]);
        }
        this.in = curIn;
    }

    protected InputStream getSegmentInputStream(InputStream baseIn, String segment) throws IOException {
        JarEntry entry;
        JarInputStream jarIn = new JarInputStream(baseIn);
        while (jarIn.available() != 0 && (entry = jarIn.getNextJarEntry()) != null) {
            if (!("/" + entry.getName()).equals(segment)) continue;
            return jarIn;
        }
        throw new IOException("unable to locate segment: " + segment);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (this.in == null) {
            this.connect();
        }
        return this.in;
    }

    @Override
    public JarFile getJarFile() throws IOException {
        String url = this.baseResource.toExternalForm();
        if (url.startsWith("file:/")) {
            url = url.substring(6);
        }
        return new JarFile(URLDecoder.decode(url, "UTF-8"));
    }

    private static String normalizeUrlPath(String name) {
        int i;
        if (name.startsWith("/")) {
            name = name.substring(1);
        }
        if ((i = name.indexOf("/..")) > 0) {
            int j = name.lastIndexOf(47, i - 1);
            name = name.substring(0, j) + name.substring(i + 3);
        }
        return name;
    }
}

