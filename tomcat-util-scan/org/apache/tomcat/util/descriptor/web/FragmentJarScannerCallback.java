/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.Jar
 *  org.apache.tomcat.JarScannerCallback
 */
package org.apache.tomcat.util.descriptor.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.tomcat.Jar;
import org.apache.tomcat.JarScannerCallback;
import org.apache.tomcat.util.descriptor.web.WebXml;
import org.apache.tomcat.util.descriptor.web.WebXmlParser;
import org.xml.sax.InputSource;

public class FragmentJarScannerCallback
implements JarScannerCallback {
    private static final String FRAGMENT_LOCATION = "META-INF/web-fragment.xml";
    private final WebXmlParser webXmlParser;
    private final boolean delegate;
    private final boolean parseRequired;
    private final Map<String, WebXml> fragments = new HashMap<String, WebXml>();
    private boolean ok = true;

    public FragmentJarScannerCallback(WebXmlParser webXmlParser, boolean delegate, boolean parseRequired) {
        this.webXmlParser = webXmlParser;
        this.delegate = delegate;
        this.parseRequired = parseRequired;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void scan(Jar jar, String webappPath, boolean isWebapp) throws IOException {
        InputStream is = null;
        WebXml fragment = new WebXml();
        fragment.setWebappJar(isWebapp);
        fragment.setDelegate(this.delegate);
        try {
            if (isWebapp && this.parseRequired) {
                is = jar.getInputStream(FRAGMENT_LOCATION);
            }
            if (is == null) {
                fragment.setDistributable(true);
            } else {
                String fragmentUrl = jar.getURL(FRAGMENT_LOCATION);
                InputSource source = new InputSource(fragmentUrl);
                source.setByteStream(is);
                if (!this.webXmlParser.parseWebXml(source, fragment, true)) {
                    this.ok = false;
                }
            }
        }
        finally {
            this.addFragment(fragment, jar.getJarFileURL());
        }
    }

    private String extractJarFileName(URL input) {
        String url = input.toString();
        if (url.endsWith("!/")) {
            url = url.substring(0, url.length() - 2);
        }
        return url.substring(url.lastIndexOf(47) + 1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void scan(File file, String webappPath, boolean isWebapp) throws IOException {
        block10: {
            WebXml fragment = new WebXml();
            fragment.setWebappJar(isWebapp);
            fragment.setDelegate(this.delegate);
            File fragmentFile = new File(file, FRAGMENT_LOCATION);
            try {
                if (fragmentFile.isFile()) {
                    try (FileInputStream stream = new FileInputStream(fragmentFile);){
                        InputSource source = new InputSource(fragmentFile.toURI().toURL().toString());
                        source.setByteStream(stream);
                        if (!this.webXmlParser.parseWebXml(source, fragment, true)) {
                            this.ok = false;
                        }
                        break block10;
                    }
                }
                fragment.setDistributable(true);
            }
            finally {
                this.addFragment(fragment, file.toURI().toURL());
            }
        }
    }

    private void addFragment(WebXml fragment, URL url) {
        fragment.setURL(url);
        if (fragment.getName() == null) {
            fragment.setName(url.toString());
        }
        fragment.setJarName(this.extractJarFileName(url));
        if (this.fragments.containsKey(fragment.getName())) {
            String duplicateName = fragment.getName();
            this.fragments.get(duplicateName).addDuplicate(url.toString());
            fragment.setName(url.toString());
        }
        this.fragments.put(fragment.getName(), fragment);
    }

    public void scanWebInfClasses() {
    }

    public boolean isOk() {
        return this.ok;
    }

    public Map<String, WebXml> getFragments() {
        return this.fragments;
    }
}

