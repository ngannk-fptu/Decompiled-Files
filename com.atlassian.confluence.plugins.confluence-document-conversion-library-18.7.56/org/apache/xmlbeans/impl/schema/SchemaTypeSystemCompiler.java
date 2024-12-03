/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.schema;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.xmlbeans.BindingConfig;
import org.apache.xmlbeans.Filer;
import org.apache.xmlbeans.SchemaCodePrinter;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.common.XmlErrorWatcher;
import org.apache.xmlbeans.impl.repackage.Repackager;
import org.apache.xmlbeans.impl.schema.BuiltinSchemaTypeSystem;
import org.apache.xmlbeans.impl.schema.SchemaDependencies;
import org.apache.xmlbeans.impl.schema.SchemaTypeCodePrinter;
import org.apache.xmlbeans.impl.schema.SchemaTypeLoaderImpl;
import org.apache.xmlbeans.impl.schema.SchemaTypeSystemImpl;
import org.apache.xmlbeans.impl.schema.StscChecker;
import org.apache.xmlbeans.impl.schema.StscImporter;
import org.apache.xmlbeans.impl.schema.StscJavaizer;
import org.apache.xmlbeans.impl.schema.StscResolver;
import org.apache.xmlbeans.impl.schema.StscState;
import org.apache.xmlbeans.impl.schema.StscTranslator;
import org.apache.xmlbeans.impl.util.FilerImpl;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;

public class SchemaTypeSystemCompiler {
    public static SchemaTypeSystem compile(Parameters params) {
        return SchemaTypeSystemCompiler.compileImpl(params.getExistingTypeSystem(), params.getName(), params.getSchemas(), params.getConfig(), params.getLinkTo(), params.getOptions(), params.getErrorListener(), params.isJavaize(), params.getBaseURI(), params.getSourcesToCopyMap(), params.getSchemasDir());
    }

    public static SchemaTypeSystemImpl compile(String name, SchemaTypeSystem existingSTS, XmlObject[] input, BindingConfig config, SchemaTypeLoader linkTo, Filer filer, XmlOptions options) throws XmlException {
        options = XmlOptions.maskNull(options);
        ArrayList<SchemaDocument.Schema> schemas = new ArrayList<SchemaDocument.Schema>();
        if (input != null) {
            for (int i = 0; i < input.length; ++i) {
                if (input[i] instanceof SchemaDocument.Schema) {
                    schemas.add((SchemaDocument.Schema)input[i]);
                    continue;
                }
                if (input[i] instanceof SchemaDocument && ((SchemaDocument)input[i]).getSchema() != null) {
                    schemas.add(((SchemaDocument)input[i]).getSchema());
                    continue;
                }
                throw new XmlException("Thread " + Thread.currentThread().getName() + ": The " + i + "th supplied input is not a schema document: its type is " + input[i].schemaType());
            }
        }
        Collection<XmlError> userErrors = options.getErrorListener();
        XmlErrorWatcher errorWatcher = new XmlErrorWatcher(userErrors);
        SchemaTypeSystemImpl stsi = SchemaTypeSystemCompiler.compileImpl(existingSTS, name, schemas.toArray(new SchemaDocument.Schema[0]), config, linkTo, options, errorWatcher, filer != null, options.getBaseURI(), null, null);
        if (errorWatcher.hasError() && stsi == null) {
            throw new XmlException(errorWatcher.firstError());
        }
        if (stsi != null && !stsi.isIncomplete() && filer != null) {
            stsi.save(filer);
            SchemaTypeSystemCompiler.generateTypes(stsi, filer, options);
        }
        return stsi;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static SchemaTypeSystemImpl compileImpl(SchemaTypeSystem system, String name, SchemaDocument.Schema[] schemas, BindingConfig config, SchemaTypeLoader linkTo, XmlOptions options, Collection<XmlError> outsideErrors, boolean javaize, URI baseURI, Map<String, String> sourcesToCopyMap, File schemasDir) {
        if (linkTo == null) {
            throw new IllegalArgumentException("Must supply linkTo");
        }
        XmlErrorWatcher errorWatcher = new XmlErrorWatcher(outsideErrors);
        boolean incremental = system != null;
        StscState state = StscState.start();
        boolean validate = options == null || !options.isCompileNoValidation();
        try {
            state.setErrorListener(errorWatcher);
            state.setBindingConfig(config);
            state.setOptions(options);
            state.setGivenTypeSystemName(name);
            state.setSchemasDir(schemasDir);
            if (baseURI != null) {
                state.setBaseUri(baseURI);
            }
            linkTo = SchemaTypeLoaderImpl.build(new SchemaTypeLoader[]{BuiltinSchemaTypeSystem.get(), linkTo}, null, null);
            state.setImportingTypeLoader(linkTo);
            ArrayList<SchemaDocument.Schema> validSchemas = new ArrayList<SchemaDocument.Schema>(schemas.length);
            if (validate) {
                XmlOptions validateOptions = new XmlOptions().setErrorListener(errorWatcher);
                if (options != null && options.isValidateTreatLaxAsSkip()) {
                    validateOptions.setValidateTreatLaxAsSkip();
                }
                for (SchemaDocument.Schema schema : schemas) {
                    if (!schema.validate(validateOptions)) continue;
                    validSchemas.add(schema);
                }
            } else {
                validSchemas.addAll(Arrays.asList(schemas));
            }
            SchemaDocument.Schema[] startWith = validSchemas.toArray(new SchemaDocument.Schema[0]);
            if (incremental) {
                HashSet<String> namespaces = new HashSet<String>();
                startWith = SchemaTypeSystemCompiler.getSchemasToRecompile((SchemaTypeSystemImpl)system, startWith, namespaces);
                state.initFromTypeSystem((SchemaTypeSystemImpl)system, namespaces);
            } else {
                state.setDependencies(new SchemaDependencies());
            }
            StscImporter.SchemaToProcess[] schemasAndChameleons = StscImporter.resolveImportsAndIncludes(startWith, incremental);
            StscTranslator.addAllDefinitions(schemasAndChameleons);
            StscResolver.resolveAll();
            StscChecker.checkAll();
            StscJavaizer.javaizeAllTypes(javaize);
            StscState.get().sts().loadFromStscState(state);
            if (sourcesToCopyMap != null) {
                sourcesToCopyMap.putAll(state.sourceCopyMap());
            }
            if (errorWatcher.hasError()) {
                if (state.allowPartial() && state.getRecovered() == errorWatcher.size()) {
                    StscState.get().sts().setIncomplete(true);
                } else {
                    SchemaTypeSystemImpl schemaTypeSystemImpl = null;
                    return schemaTypeSystemImpl;
                }
            }
            if (system != null) {
                ((SchemaTypeSystemImpl)system).setIncomplete(true);
            }
            SchemaTypeSystemImpl schemaTypeSystemImpl = StscState.get().sts();
            return schemaTypeSystemImpl;
        }
        finally {
            StscState.end();
        }
    }

    private static SchemaDocument.Schema[] getSchemasToRecompile(SchemaTypeSystemImpl system, SchemaDocument.Schema[] modified, Set<String> namespaces) {
        HashSet<String> modifiedFiles = new HashSet<String>();
        HashMap<String, SchemaDocument.Schema> haveFile = new HashMap<String, SchemaDocument.Schema>();
        ArrayList<SchemaDocument.Schema> result = new ArrayList<SchemaDocument.Schema>();
        for (SchemaDocument.Schema schema : modified) {
            String fileURL = schema.documentProperties().getSourceName();
            if (fileURL == null) {
                throw new IllegalArgumentException("One of the Schema files passed in doesn't have the source set, which prevents it to be incrementally compiled");
            }
            modifiedFiles.add(fileURL);
            haveFile.put(fileURL, schema);
            result.add(schema);
        }
        SchemaDependencies dep = system.getDependencies();
        List<String> nss = dep.getNamespacesTouched(modifiedFiles);
        namespaces.addAll(dep.computeTransitiveClosure(nss));
        List<String> needRecompilation = dep.getFilesTouched(namespaces);
        StscState.get().setDependencies(new SchemaDependencies(dep, namespaces));
        for (String url : needRecompilation) {
            SchemaDocument.Schema have = (SchemaDocument.Schema)haveFile.get(url);
            if (have != null) continue;
            try {
                XmlObject xdoc = StscImporter.DownloadTable.downloadDocument(StscState.get().getS4SLoader(), null, url);
                XmlOptions voptions = new XmlOptions();
                voptions.setErrorListener(StscState.get().getErrorListener());
                if (!(xdoc instanceof SchemaDocument) || !xdoc.validate(voptions)) {
                    StscState.get().error("Referenced document is not a valid schema, URL = " + url, 56, null);
                    continue;
                }
                SchemaDocument sDoc = (SchemaDocument)xdoc;
                result.add(sDoc.getSchema());
            }
            catch (MalformedURLException mfe) {
                StscState.get().error("exception.loading.url", new Object[]{"MalformedURLException", url, mfe.getMessage()}, null);
            }
            catch (IOException ioe) {
                StscState.get().error("exception.loading.url", new Object[]{"IOException", url, ioe.getMessage()}, null);
            }
            catch (XmlException xmle) {
                StscState.get().error("exception.loading.url", new Object[]{"XmlException", url, xmle.getMessage()}, null);
            }
        }
        return result.toArray(new SchemaDocument.Schema[0]);
    }

    public static boolean generateTypes(SchemaTypeSystem system, Filer filer, XmlOptions options) {
        SchemaCodePrinter printer;
        if (system instanceof SchemaTypeSystemImpl && ((SchemaTypeSystemImpl)system).isIncomplete()) {
            return false;
        }
        boolean success = true;
        ArrayList<SchemaType> types = new ArrayList<SchemaType>();
        types.addAll(Arrays.asList(system.globalTypes()));
        types.addAll(Arrays.asList(system.documentTypes()));
        types.addAll(Arrays.asList(system.attributeTypes()));
        SchemaCodePrinter schemaCodePrinter = printer = options == null ? null : options.getSchemaCodePrinter();
        if (printer == null) {
            printer = new SchemaTypeCodePrinter();
        }
        String indexClassName = SchemaTypeCodePrinter.indexClassForSystem(system);
        try (Writer out = filer.createSourceFile(indexClassName);){
            Repackager repackager = filer instanceof FilerImpl ? ((FilerImpl)filer).getRepackager() : null;
            printer.printHolder(out, system, options, repackager);
        }
        catch (IOException e) {
            System.err.println("IO Error " + e);
            success = false;
        }
        for (SchemaType type : types) {
            Throwable throwable;
            Writer writer;
            if (type.isBuiltinType() || type.getFullJavaName() == null) continue;
            String fjn = type.getFullJavaName();
            try {
                writer = filer.createSourceFile(fjn);
                throwable = null;
                try {
                    printer.printType(writer, type, options);
                }
                catch (Throwable throwable2) {
                    throwable = throwable2;
                    throw throwable2;
                }
                finally {
                    if (writer != null) {
                        if (throwable != null) {
                            try {
                                writer.close();
                            }
                            catch (Throwable throwable3) {
                                throwable.addSuppressed(throwable3);
                            }
                        } else {
                            writer.close();
                        }
                    }
                }
            }
            catch (IOException e) {
                System.err.println("IO Error " + e);
                success = false;
            }
            fjn = type.getFullJavaImplName();
            try {
                writer = filer.createSourceFile(fjn);
                throwable = null;
                try {
                    printer.printTypeImpl(writer, type, options);
                }
                catch (Throwable throwable4) {
                    throwable = throwable4;
                    throw throwable4;
                }
                finally {
                    if (writer == null) continue;
                    if (throwable != null) {
                        try {
                            writer.close();
                        }
                        catch (Throwable throwable5) {
                            throwable.addSuppressed(throwable5);
                        }
                        continue;
                    }
                    writer.close();
                }
            }
            catch (IOException e) {
                System.err.println("IO Error " + e);
                success = false;
            }
        }
        return success;
    }

    public static class Parameters {
        private SchemaTypeSystem existingSystem;
        private String name;
        private SchemaDocument.Schema[] schemas;
        private BindingConfig config;
        private SchemaTypeLoader linkTo;
        private XmlOptions options;
        private Collection<XmlError> errorListener;
        private boolean javaize;
        private URI baseURI;
        private Map<String, String> sourcesToCopyMap;
        private File schemasDir;

        public SchemaTypeSystem getExistingTypeSystem() {
            return this.existingSystem;
        }

        public void setExistingTypeSystem(SchemaTypeSystem system) {
            this.existingSystem = system;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public SchemaDocument.Schema[] getSchemas() {
            return this.schemas;
        }

        public void setSchemas(SchemaDocument.Schema[] schemas) {
            this.schemas = schemas == null ? null : (SchemaDocument.Schema[])schemas.clone();
        }

        public BindingConfig getConfig() {
            return this.config;
        }

        public void setConfig(BindingConfig config) {
            this.config = config;
        }

        public SchemaTypeLoader getLinkTo() {
            return this.linkTo;
        }

        public void setLinkTo(SchemaTypeLoader linkTo) {
            this.linkTo = linkTo;
        }

        public XmlOptions getOptions() {
            return this.options;
        }

        public void setOptions(XmlOptions options) {
            this.options = options;
        }

        public Collection<XmlError> getErrorListener() {
            return this.errorListener;
        }

        public void setErrorListener(Collection<XmlError> errorListener) {
            this.errorListener = errorListener;
        }

        public boolean isJavaize() {
            return this.javaize;
        }

        public void setJavaize(boolean javaize) {
            this.javaize = javaize;
        }

        public URI getBaseURI() {
            return this.baseURI;
        }

        public void setBaseURI(URI baseURI) {
            this.baseURI = baseURI;
        }

        public Map<String, String> getSourcesToCopyMap() {
            return this.sourcesToCopyMap;
        }

        public void setSourcesToCopyMap(Map<String, String> sourcesToCopyMap) {
            this.sourcesToCopyMap = sourcesToCopyMap;
        }

        public File getSchemasDir() {
            return this.schemasDir;
        }

        public void setSchemasDir(File schemasDir) {
            this.schemasDir = schemasDir;
        }
    }
}

