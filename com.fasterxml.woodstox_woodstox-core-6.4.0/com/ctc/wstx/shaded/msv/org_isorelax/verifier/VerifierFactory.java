/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_isorelax.verifier;

import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Schema;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Verifier;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierConfigurationException;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.VerifierFactoryLoader;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public abstract class VerifierFactory {
    private EntityResolver resolver = null;
    private static HashMap providerMap = new HashMap();

    public Verifier newVerifier(String s) throws VerifierConfigurationException, SAXException, IOException {
        return this.compileSchema(s).newVerifier();
    }

    public Verifier newVerifier(File file) throws VerifierConfigurationException, SAXException, IOException {
        return this.compileSchema(file).newVerifier();
    }

    public Verifier newVerifier(InputStream inputstream) throws VerifierConfigurationException, SAXException, IOException {
        return this.compileSchema(inputstream, null).newVerifier();
    }

    public Verifier newVerifier(InputStream inputstream, String s) throws VerifierConfigurationException, SAXException, IOException {
        return this.compileSchema(inputstream, s).newVerifier();
    }

    public Verifier newVerifier(InputSource inputsource) throws VerifierConfigurationException, SAXException, IOException {
        return this.compileSchema(inputsource).newVerifier();
    }

    public abstract Schema compileSchema(InputSource var1) throws VerifierConfigurationException, SAXException, IOException;

    public Schema compileSchema(String s) throws VerifierConfigurationException, SAXException, IOException {
        return this.compileSchema(new InputSource(s));
    }

    public Schema compileSchema(InputStream inputstream) throws VerifierConfigurationException, SAXException, IOException {
        return this.compileSchema(inputstream, null);
    }

    public Schema compileSchema(InputStream inputstream, String s) throws VerifierConfigurationException, SAXException, IOException {
        InputSource inputsource = new InputSource(inputstream);
        inputsource.setSystemId(s);
        return this.compileSchema(inputsource);
    }

    public Schema compileSchema(File file) throws VerifierConfigurationException, SAXException, IOException {
        String s = "file:" + file.getAbsolutePath();
        if (File.separatorChar == '\\') {
            s = s.replace('\\', '/');
        }
        return this.compileSchema(new InputSource(s));
    }

    public boolean isFeature(String s) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://www.iso-relax.org/verifier/handler".equals(s) || "http://www.iso-relax.org/verifier/filter".equals(s)) {
            return true;
        }
        throw new SAXNotRecognizedException(s);
    }

    public void setFeature(String s, boolean flag) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException(s);
    }

    public Object getProperty(String s) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException(s);
    }

    public void setProperty(String s, Object obj) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException(s);
    }

    public void setEntityResolver(EntityResolver entityresolver) {
        this.resolver = entityresolver;
    }

    public EntityResolver getEntityResolver() {
        return this.resolver;
    }

    public static VerifierFactory newInstance() throws VerifierConfigurationException {
        return VerifierFactory.newInstance("http://www.xml.gr.jp/xmlns/relaxNamespace");
    }

    public static VerifierFactory newInstance(String s) throws VerifierConfigurationException {
        Iterator iterator = VerifierFactory.providers(VerifierFactoryLoader.class);
        while (iterator.hasNext()) {
            VerifierFactoryLoader verifierfactoryloader = (VerifierFactoryLoader)iterator.next();
            try {
                VerifierFactory verifierfactory = verifierfactoryloader.createFactory(s);
                if (verifierfactory == null) continue;
                return verifierfactory;
            }
            catch (Throwable throwable) {
            }
        }
        throw new VerifierConfigurationException("no validation engine available for: " + s);
    }

    private static synchronized Iterator providers(Class class1) {
        Enumeration<URL> enumeration;
        ClassLoader classloader = class1.getClassLoader();
        String s = "META-INF/services/" + class1.getName();
        Vector vector = (Vector)providerMap.get(s);
        if (vector != null) {
            return vector.iterator();
        }
        vector = new Vector();
        providerMap.put(s, vector);
        try {
            enumeration = classloader.getResources(s);
        }
        catch (IOException ioexception) {
            return vector.iterator();
        }
        while (enumeration.hasMoreElements()) {
            try {
                URL url = enumeration.nextElement();
                InputStream inputstream = url.openStream();
                InputStreamReader inputstreamreader = new InputStreamReader(inputstream, "UTF-8");
                BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
                String s1 = bufferedreader.readLine();
                while (s1 != null) {
                    try {
                        int i = s1.indexOf(35);
                        if (i != -1) {
                            s1 = s1.substring(0, i);
                        }
                        if ((s1 = s1.trim()).length() == 0) {
                            s1 = bufferedreader.readLine();
                            continue;
                        }
                        Object obj = classloader.loadClass(s1).newInstance();
                        vector.add(obj);
                    }
                    catch (Exception exception1) {
                        // empty catch block
                    }
                    s1 = bufferedreader.readLine();
                }
            }
            catch (Exception exception) {
            }
        }
        return vector.iterator();
    }
}

