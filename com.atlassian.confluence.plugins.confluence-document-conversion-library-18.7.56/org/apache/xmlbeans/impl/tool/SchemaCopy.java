/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.tool;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.common.IOUtil;
import org.apache.xmlbeans.impl.tool.CodeGenUtil;
import org.apache.xmlbeans.impl.xb.substwsdl.DefinitionsDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.Annotated;
import org.apache.xmlbeans.impl.xb.xsdschema.ImportDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;

public class SchemaCopy {
    private static final XmlOptions loadOptions = new XmlOptions().setLoadSubstituteNamespaces(Collections.singletonMap("http://schemas.xmlsoap.org/wsdl/", "http://www.apache.org/internal/xmlbeans/wsdlsubst"));

    public static void printUsage() {
        System.out.println("Copies the XML schema at the specified URL to the specified file.");
        System.out.println("Usage: scopy sourceurl [targetfile]");
        System.out.println("    sourceurl - The URL at which the schema is located.");
        System.out.println("    targetfile - The file to which the schema should be copied.");
        System.out.println();
    }

    public static void main(String[] args) {
        URI target;
        if (args.length < 1 || args.length > 2) {
            SchemaCopy.printUsage();
            return;
        }
        URI source = null;
        try {
            if (args[0].compareToIgnoreCase("-usage") == 0) {
                SchemaCopy.printUsage();
                return;
            }
            source = new URI(args[0]);
            source.toURL();
        }
        catch (Exception e) {
            System.err.println("Badly formed URL " + source);
            return;
        }
        if (args.length < 2) {
            try {
                URI dir = new File(".").getCanonicalFile().toURI();
                String lastPart = source.getPath();
                lastPart = lastPart.substring(lastPart.lastIndexOf(47) + 1);
                target = CodeGenUtil.resolve(dir, URI.create(lastPart));
            }
            catch (Exception e) {
                System.err.println("Cannot canonicalize current directory");
                return;
            }
        }
        try {
            target = new URI(args[1]);
            if (!target.isAbsolute()) {
                target = null;
            } else if (!target.getScheme().equals("file")) {
                target = null;
            }
        }
        catch (Exception e) {
            target = null;
        }
        if (target == null) {
            try {
                target = Paths.get("", new String[0]).toAbsolutePath().toUri();
            }
            catch (Exception e) {
                System.err.println("Cannot canonicalize current directory");
                return;
            }
        }
        Map<URI, URI> thingsToCopy = SchemaCopy.findAllRelative(source, target);
        SchemaCopy.copyAll(thingsToCopy, true);
    }

    private static void copyAll(Map<URI, URI> uriMap, boolean stdout) {
        for (URI source : uriMap.keySet()) {
            URI target = uriMap.get(source);
            try {
                IOUtil.copyCompletely(source, target);
            }
            catch (Exception e) {
                if (!stdout) continue;
                System.out.println("Could not copy " + source + " -> " + target);
                continue;
            }
            if (!stdout) continue;
            System.out.println("Copied " + source + " -> " + target);
        }
    }

    public static Map<URI, URI> findAllRelative(URI source, URI target) {
        LinkedHashMap<URI, URI> result = new LinkedHashMap<URI, URI>();
        result.put(source, target);
        LinkedList<URI> process = new LinkedList<URI>();
        process.add(source);
        while (!process.isEmpty()) {
            URI nextSource = (URI)process.removeFirst();
            URI nextTarget = (URI)result.get(nextSource);
            Map<URI, URI> nextResults = SchemaCopy.findRelativeInOne(nextSource, nextTarget);
            for (URI newSource : nextResults.keySet()) {
                if (result.containsKey(newSource)) continue;
                result.put(newSource, nextResults.get(newSource));
                process.add(newSource);
            }
        }
        return result;
    }

    private static Map<URI, URI> findRelativeInOne(URI source, URI target) {
        try {
            URL sourceURL = source.toURL();
            XmlObject xobj = XmlObject.Factory.parse(sourceURL, loadOptions);
            LinkedHashMap<URI, URI> result = new LinkedHashMap<URI, URI>();
            if (xobj instanceof SchemaDocument) {
                SchemaCopy.putMappingsFromSchema(result, source, target, ((SchemaDocument)xobj).getSchema());
            } else if (xobj instanceof DefinitionsDocument) {
                SchemaCopy.putMappingsFromWsdl(result, source, target, ((DefinitionsDocument)xobj).getDefinitions());
            }
            return result;
        }
        catch (Exception exception) {
            return Collections.emptyMap();
        }
    }

    private static void putNewMapping(Map<URI, URI> result, URI origSource, URI origTarget, String literalURI) {
        try {
            if (literalURI == null) {
                return;
            }
            URI newRelative = new URI(literalURI);
            if (newRelative.isAbsolute()) {
                return;
            }
            URI newSource = CodeGenUtil.resolve(origSource, newRelative);
            URI newTarget = CodeGenUtil.resolve(origTarget, newRelative);
            result.put(newSource, newTarget);
        }
        catch (URISyntaxException uRISyntaxException) {
            // empty catch block
        }
    }

    private static void putMappingsFromSchema(Map<URI, URI> result, URI source, URI target, SchemaDocument.Schema schema) {
        for (ImportDocument.Import import_ : schema.getImportArray()) {
            SchemaCopy.putNewMapping(result, source, target, import_.getSchemaLocation());
        }
        for (Annotated annotated : schema.getIncludeArray()) {
            SchemaCopy.putNewMapping(result, source, target, annotated.getSchemaLocation());
        }
    }

    private static void putMappingsFromWsdl(Map<URI, URI> result, URI source, URI target, DefinitionsDocument.Definitions wdoc) {
        for (XmlObject xmlObject : wdoc.getTypesArray()) {
            SchemaDocument.Schema[] schemas;
            for (SchemaDocument.Schema schema : schemas = (SchemaDocument.Schema[])xmlObject.selectPath("declare namespace xs='http://www.w3.org/2001/XMLSchema' xs:schema")) {
                SchemaCopy.putMappingsFromSchema(result, source, target, schema);
            }
        }
        for (XmlObject xmlObject : wdoc.getImportArray()) {
            SchemaCopy.putNewMapping(result, source, target, xmlObject.getLocation());
        }
    }
}

