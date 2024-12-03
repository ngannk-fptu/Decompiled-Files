/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.anim.dom.SVGOMDocument
 *  org.apache.batik.util.Service
 */
package org.apache.batik.script;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.script.ImportInfo;
import org.apache.batik.script.Interpreter;
import org.apache.batik.script.InterpreterFactory;
import org.apache.batik.util.Service;
import org.w3c.dom.Document;

public class InterpreterPool {
    public static final String BIND_NAME_DOCUMENT = "document";
    protected static Map defaultFactories = new HashMap(7);
    protected Map factories = new HashMap(7);

    public InterpreterPool() {
        this.factories.putAll(defaultFactories);
    }

    public Interpreter createInterpreter(Document document, String language) {
        return this.createInterpreter(document, language, null);
    }

    public Interpreter createInterpreter(Document document, String language, ImportInfo imports) {
        InterpreterFactory factory = (InterpreterFactory)this.factories.get(language);
        if (factory == null) {
            return null;
        }
        if (imports == null) {
            imports = ImportInfo.getImports();
        }
        Interpreter interpreter = null;
        SVGOMDocument svgDoc = (SVGOMDocument)document;
        URL url = null;
        try {
            url = new URL(svgDoc.getDocumentURI());
        }
        catch (MalformedURLException malformedURLException) {
            // empty catch block
        }
        interpreter = factory.createInterpreter(url, svgDoc.isSVG12(), imports);
        if (interpreter == null) {
            return null;
        }
        if (document != null) {
            interpreter.bindObject(BIND_NAME_DOCUMENT, document);
        }
        return interpreter;
    }

    public void putInterpreterFactory(String language, InterpreterFactory factory) {
        this.factories.put(language, factory);
    }

    public void removeInterpreterFactory(String language) {
        this.factories.remove(language);
    }

    static {
        Iterator iter = Service.providers(InterpreterFactory.class);
        while (iter.hasNext()) {
            String[] mimeTypes;
            InterpreterFactory factory = null;
            factory = (InterpreterFactory)iter.next();
            for (String mimeType : mimeTypes = factory.getMimeTypes()) {
                defaultFactories.put(mimeType, factory);
            }
        }
    }
}

