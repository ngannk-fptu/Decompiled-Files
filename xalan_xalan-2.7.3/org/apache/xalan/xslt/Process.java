/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xslt;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.ListResourceBundle;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.xalan.Version;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.trace.PrintTraceListener;
import org.apache.xalan.trace.TraceManager;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.ObjectFactory;
import org.apache.xalan.xslt.util.XslTransformErrorLocatorHelper;
import org.apache.xml.utils.DefaultErrorHandler;
import org.apache.xml.utils.SystemIDResolver;
import org.apache.xml.utils.WrappedRuntimeException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class Process {
    protected static void printArgOptions(ResourceBundle resbundle) {
        System.out.println(resbundle.getString("xslProc_option"));
        System.out.println("\n\t\t\t" + resbundle.getString("xslProc_common_options") + "\n");
        System.out.println(resbundle.getString("optionXSLTC"));
        System.out.println(resbundle.getString("optionIN"));
        System.out.println(resbundle.getString("optionXSL"));
        System.out.println(resbundle.getString("optionOUT"));
        System.out.println(resbundle.getString("optionV"));
        System.out.println(resbundle.getString("optionEDUMP"));
        System.out.println(resbundle.getString("optionXML"));
        System.out.println(resbundle.getString("optionTEXT"));
        System.out.println(resbundle.getString("optionHTML"));
        System.out.println(resbundle.getString("optionPARAM"));
        System.out.println(resbundle.getString("optionMEDIA"));
        System.out.println(resbundle.getString("optionFLAVOR"));
        System.out.println(resbundle.getString("optionDIAG"));
        System.out.println(resbundle.getString("optionURIRESOLVER"));
        System.out.println(resbundle.getString("optionENTITYRESOLVER"));
        Process.waitForReturnKey(resbundle);
        System.out.println(resbundle.getString("optionCONTENTHANDLER"));
        System.out.println(resbundle.getString("optionSECUREPROCESSING"));
        System.out.println("\n\t\t\t" + resbundle.getString("xslProc_xalan_options") + "\n");
        System.out.println(resbundle.getString("optionQC"));
        System.out.println(resbundle.getString("optionTT"));
        System.out.println(resbundle.getString("optionTG"));
        System.out.println(resbundle.getString("optionTS"));
        System.out.println(resbundle.getString("optionTTC"));
        System.out.println(resbundle.getString("optionTCLASS"));
        System.out.println(resbundle.getString("optionLINENUMBERS"));
        System.out.println(resbundle.getString("optionINCREMENTAL"));
        System.out.println(resbundle.getString("optionNOOPTIMIMIZE"));
        System.out.println(resbundle.getString("optionRL"));
        System.out.println("\n\t\t\t" + resbundle.getString("xslProc_xsltc_options") + "\n");
        System.out.println(resbundle.getString("optionXO"));
        Process.waitForReturnKey(resbundle);
        System.out.println(resbundle.getString("optionXD"));
        System.out.println(resbundle.getString("optionXJ"));
        System.out.println(resbundle.getString("optionXP"));
        System.out.println(resbundle.getString("optionXN"));
        System.out.println(resbundle.getString("optionXX"));
        System.out.println(resbundle.getString("optionXT"));
    }

    public static void main(String[] argv) {
        PrintWriter diagnosticsWriter;
        boolean doStackDumpOnError = false;
        boolean setQuietMode = false;
        boolean doDiag = false;
        String msg = null;
        boolean isSecureProcessing = false;
        PrintWriter dumpWriter = diagnosticsWriter = new PrintWriter(System.err, true);
        ListResourceBundle resbundle = XSLMessages.loadResourceBundle("org.apache.xalan.res.XSLTErrorResources");
        String flavor = "s2s";
        if (argv.length < 1) {
            Process.printArgOptions(resbundle);
        } else {
            TransformerFactory tfactory;
            boolean useXSLTC = false;
            for (int i = 0; i < argv.length; ++i) {
                if (!"-XSLTC".equalsIgnoreCase(argv[i])) continue;
                useXSLTC = true;
            }
            if (useXSLTC) {
                String key = "javax.xml.transform.TransformerFactory";
                String value = "org.apache.xalan.xsltc.trax.TransformerFactoryImpl";
                Properties props = System.getProperties();
                props.put(key, value);
                System.setProperties(props);
            }
            try {
                tfactory = TransformerFactory.newInstance();
                tfactory.setErrorListener(new DefaultErrorHandler(false));
            }
            catch (TransformerFactoryConfigurationError pfe) {
                pfe.printStackTrace(dumpWriter);
                msg = XSLMessages.createMessage("ER_NOT_SUCCESSFUL", null);
                diagnosticsWriter.println(msg);
                tfactory = null;
                Process.doExit(msg);
            }
            boolean formatOutput = false;
            boolean useSourceLocation = false;
            String inFileName = null;
            String outFileName = null;
            String dumpFileName = null;
            String xslFileName = null;
            Object treedumpFileName = null;
            PrintTraceListener tracer = null;
            String outputType = null;
            String media = null;
            Vector<String> params = new Vector<String>();
            boolean quietConflictWarnings = false;
            URIResolver uriResolver = null;
            EntityResolver entityResolver = null;
            ContentHandler contentHandler = null;
            int recursionLimit = -1;
            for (int i = 0; i < argv.length; ++i) {
                if ("-XSLTC".equalsIgnoreCase(argv[i])) continue;
                if ("-TT".equalsIgnoreCase(argv[i])) {
                    if (!useXSLTC) {
                        if (null == tracer) {
                            tracer = new PrintTraceListener(diagnosticsWriter);
                        }
                        tracer.m_traceTemplates = true;
                        continue;
                    }
                    Process.printInvalidXSLTCOption("-TT");
                    continue;
                }
                if ("-TG".equalsIgnoreCase(argv[i])) {
                    if (!useXSLTC) {
                        if (null == tracer) {
                            tracer = new PrintTraceListener(diagnosticsWriter);
                        }
                        tracer.m_traceGeneration = true;
                        continue;
                    }
                    Process.printInvalidXSLTCOption("-TG");
                    continue;
                }
                if ("-TS".equalsIgnoreCase(argv[i])) {
                    if (!useXSLTC) {
                        if (null == tracer) {
                            tracer = new PrintTraceListener(diagnosticsWriter);
                        }
                        tracer.m_traceSelection = true;
                        continue;
                    }
                    Process.printInvalidXSLTCOption("-TS");
                    continue;
                }
                if ("-TTC".equalsIgnoreCase(argv[i])) {
                    if (!useXSLTC) {
                        if (null == tracer) {
                            tracer = new PrintTraceListener(diagnosticsWriter);
                        }
                        tracer.m_traceElements = true;
                        continue;
                    }
                    Process.printInvalidXSLTCOption("-TTC");
                    continue;
                }
                if ("-INDENT".equalsIgnoreCase(argv[i])) {
                    int indentAmount;
                    if (i + 1 < argv.length && argv[i + 1].charAt(0) != '-') {
                        indentAmount = Integer.parseInt(argv[++i]);
                        continue;
                    }
                    indentAmount = 0;
                    continue;
                }
                if ("-IN".equalsIgnoreCase(argv[i])) {
                    if (i + 1 < argv.length && argv[i + 1].charAt(0) != '-') {
                        inFileName = argv[++i];
                        continue;
                    }
                    System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[]{"-IN"}));
                    continue;
                }
                if ("-MEDIA".equalsIgnoreCase(argv[i])) {
                    if (i + 1 < argv.length) {
                        media = argv[++i];
                        continue;
                    }
                    System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[]{"-MEDIA"}));
                    continue;
                }
                if ("-OUT".equalsIgnoreCase(argv[i])) {
                    if (i + 1 < argv.length && argv[i + 1].charAt(0) != '-') {
                        outFileName = argv[++i];
                        continue;
                    }
                    System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[]{"-OUT"}));
                    continue;
                }
                if ("-XSL".equalsIgnoreCase(argv[i])) {
                    if (i + 1 < argv.length && argv[i + 1].charAt(0) != '-') {
                        xslFileName = argv[++i];
                        continue;
                    }
                    System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[]{"-XSL"}));
                    continue;
                }
                if ("-FLAVOR".equalsIgnoreCase(argv[i])) {
                    if (i + 1 < argv.length) {
                        flavor = argv[++i];
                        continue;
                    }
                    System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[]{"-FLAVOR"}));
                    continue;
                }
                if ("-PARAM".equalsIgnoreCase(argv[i])) {
                    if (i + 2 < argv.length) {
                        String name = argv[++i];
                        params.addElement(name);
                        String expression = argv[++i];
                        params.addElement(expression);
                        continue;
                    }
                    System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[]{"-PARAM"}));
                    continue;
                }
                if ("-E".equalsIgnoreCase(argv[i])) continue;
                if ("-V".equalsIgnoreCase(argv[i])) {
                    diagnosticsWriter.println(resbundle.getString("version") + Version.getVersion() + ", " + resbundle.getString("version2"));
                    continue;
                }
                if ("-QC".equalsIgnoreCase(argv[i])) {
                    if (!useXSLTC) {
                        quietConflictWarnings = true;
                        continue;
                    }
                    Process.printInvalidXSLTCOption("-QC");
                    continue;
                }
                if ("-Q".equalsIgnoreCase(argv[i])) {
                    setQuietMode = true;
                    continue;
                }
                if ("-DIAG".equalsIgnoreCase(argv[i])) {
                    doDiag = true;
                    continue;
                }
                if ("-XML".equalsIgnoreCase(argv[i])) {
                    outputType = "xml";
                    continue;
                }
                if ("-TEXT".equalsIgnoreCase(argv[i])) {
                    outputType = "text";
                    continue;
                }
                if ("-HTML".equalsIgnoreCase(argv[i])) {
                    outputType = "html";
                    continue;
                }
                if ("-EDUMP".equalsIgnoreCase(argv[i])) {
                    doStackDumpOnError = true;
                    if (i + 1 >= argv.length || argv[i + 1].charAt(0) == '-') continue;
                    dumpFileName = argv[++i];
                    continue;
                }
                if ("-URIRESOLVER".equalsIgnoreCase(argv[i])) {
                    if (i + 1 < argv.length) {
                        try {
                            uriResolver = (URIResolver)ObjectFactory.newInstance(argv[++i], ObjectFactory.findClassLoader(), true);
                            tfactory.setURIResolver(uriResolver);
                        }
                        catch (ObjectFactory.ConfigurationError cnfe) {
                            msg = XSLMessages.createMessage("ER_CLASS_NOT_FOUND_FOR_OPTION", new Object[]{"-URIResolver"});
                            System.err.println(msg);
                            Process.doExit(msg);
                        }
                        continue;
                    }
                    msg = XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[]{"-URIResolver"});
                    System.err.println(msg);
                    Process.doExit(msg);
                    continue;
                }
                if ("-ENTITYRESOLVER".equalsIgnoreCase(argv[i])) {
                    if (i + 1 < argv.length) {
                        try {
                            entityResolver = (EntityResolver)ObjectFactory.newInstance(argv[++i], ObjectFactory.findClassLoader(), true);
                        }
                        catch (ObjectFactory.ConfigurationError cnfe) {
                            msg = XSLMessages.createMessage("ER_CLASS_NOT_FOUND_FOR_OPTION", new Object[]{"-EntityResolver"});
                            System.err.println(msg);
                            Process.doExit(msg);
                        }
                        continue;
                    }
                    msg = XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[]{"-EntityResolver"});
                    System.err.println(msg);
                    Process.doExit(msg);
                    continue;
                }
                if ("-CONTENTHANDLER".equalsIgnoreCase(argv[i])) {
                    if (i + 1 < argv.length) {
                        try {
                            contentHandler = (ContentHandler)ObjectFactory.newInstance(argv[++i], ObjectFactory.findClassLoader(), true);
                        }
                        catch (ObjectFactory.ConfigurationError cnfe) {
                            msg = XSLMessages.createMessage("ER_CLASS_NOT_FOUND_FOR_OPTION", new Object[]{"-ContentHandler"});
                            System.err.println(msg);
                            Process.doExit(msg);
                        }
                        continue;
                    }
                    msg = XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[]{"-ContentHandler"});
                    System.err.println(msg);
                    Process.doExit(msg);
                    continue;
                }
                if ("-L".equalsIgnoreCase(argv[i])) {
                    if (!useXSLTC) {
                        tfactory.setAttribute("http://xml.apache.org/xalan/properties/source-location", Boolean.TRUE);
                        continue;
                    }
                    Process.printInvalidXSLTCOption("-L");
                    continue;
                }
                if ("-INCREMENTAL".equalsIgnoreCase(argv[i])) {
                    if (!useXSLTC) {
                        tfactory.setAttribute("http://xml.apache.org/xalan/features/incremental", Boolean.TRUE);
                        continue;
                    }
                    Process.printInvalidXSLTCOption("-INCREMENTAL");
                    continue;
                }
                if ("-NOOPTIMIZE".equalsIgnoreCase(argv[i])) {
                    if (!useXSLTC) {
                        tfactory.setAttribute("http://xml.apache.org/xalan/features/optimize", Boolean.FALSE);
                        continue;
                    }
                    Process.printInvalidXSLTCOption("-NOOPTIMIZE");
                    continue;
                }
                if ("-RL".equalsIgnoreCase(argv[i])) {
                    if (!useXSLTC) {
                        if (i + 1 < argv.length) {
                            recursionLimit = Integer.parseInt(argv[++i]);
                            continue;
                        }
                        System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[]{"-rl"}));
                        continue;
                    }
                    if (i + 1 < argv.length && argv[i + 1].charAt(0) != '-') {
                        ++i;
                    }
                    Process.printInvalidXSLTCOption("-RL");
                    continue;
                }
                if ("-XO".equalsIgnoreCase(argv[i])) {
                    if (useXSLTC) {
                        if (i + 1 < argv.length && argv[i + 1].charAt(0) != '-') {
                            tfactory.setAttribute("generate-translet", "true");
                            tfactory.setAttribute("translet-name", argv[++i]);
                            continue;
                        }
                        tfactory.setAttribute("generate-translet", "true");
                        continue;
                    }
                    if (i + 1 < argv.length && argv[i + 1].charAt(0) != '-') {
                        ++i;
                    }
                    Process.printInvalidXalanOption("-XO");
                    continue;
                }
                if ("-XD".equalsIgnoreCase(argv[i])) {
                    if (useXSLTC) {
                        if (i + 1 < argv.length && argv[i + 1].charAt(0) != '-') {
                            tfactory.setAttribute("destination-directory", argv[++i]);
                            continue;
                        }
                        System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[]{"-XD"}));
                        continue;
                    }
                    if (i + 1 < argv.length && argv[i + 1].charAt(0) != '-') {
                        ++i;
                    }
                    Process.printInvalidXalanOption("-XD");
                    continue;
                }
                if ("-XJ".equalsIgnoreCase(argv[i])) {
                    if (useXSLTC) {
                        if (i + 1 < argv.length && argv[i + 1].charAt(0) != '-') {
                            tfactory.setAttribute("generate-translet", "true");
                            tfactory.setAttribute("jar-name", argv[++i]);
                            continue;
                        }
                        System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[]{"-XJ"}));
                        continue;
                    }
                    if (i + 1 < argv.length && argv[i + 1].charAt(0) != '-') {
                        ++i;
                    }
                    Process.printInvalidXalanOption("-XJ");
                    continue;
                }
                if ("-XP".equalsIgnoreCase(argv[i])) {
                    if (useXSLTC) {
                        if (i + 1 < argv.length && argv[i + 1].charAt(0) != '-') {
                            tfactory.setAttribute("package-name", argv[++i]);
                            continue;
                        }
                        System.err.println(XSLMessages.createMessage("ER_MISSING_ARG_FOR_OPTION", new Object[]{"-XP"}));
                        continue;
                    }
                    if (i + 1 < argv.length && argv[i + 1].charAt(0) != '-') {
                        ++i;
                    }
                    Process.printInvalidXalanOption("-XP");
                    continue;
                }
                if ("-XN".equalsIgnoreCase(argv[i])) {
                    if (useXSLTC) {
                        tfactory.setAttribute("enable-inlining", "true");
                        continue;
                    }
                    Process.printInvalidXalanOption("-XN");
                    continue;
                }
                if ("-XX".equalsIgnoreCase(argv[i])) {
                    if (useXSLTC) {
                        tfactory.setAttribute("debug", "true");
                        continue;
                    }
                    Process.printInvalidXalanOption("-XX");
                    continue;
                }
                if ("-XT".equalsIgnoreCase(argv[i])) {
                    if (useXSLTC) {
                        tfactory.setAttribute("auto-translet", "true");
                        continue;
                    }
                    Process.printInvalidXalanOption("-XT");
                    continue;
                }
                if ("-SECURE".equalsIgnoreCase(argv[i])) {
                    isSecureProcessing = true;
                    try {
                        tfactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
                    }
                    catch (TransformerConfigurationException transformerConfigurationException) {}
                    continue;
                }
                System.err.println(XSLMessages.createMessage("ER_INVALID_OPTION", new Object[]{argv[i]}));
            }
            if (inFileName == null && xslFileName == null) {
                msg = resbundle.getString("xslProc_no_input");
                System.err.println(msg);
                Process.doExit(msg);
            }
            try {
                StreamResult strResult;
                long start = System.currentTimeMillis();
                if (null != dumpFileName) {
                    dumpWriter = new PrintWriter(new FileWriter(dumpFileName));
                }
                Templates stylesheet = null;
                if (null != xslFileName) {
                    if (flavor.equals("d2d")) {
                        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
                        dfactory.setNamespaceAware(true);
                        if (isSecureProcessing) {
                            try {
                                dfactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
                            }
                            catch (ParserConfigurationException parserConfigurationException) {
                                // empty catch block
                            }
                        }
                        DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
                        Document xslDOM = docBuilder.parse(new InputSource(xslFileName));
                        stylesheet = tfactory.newTemplates(new DOMSource(xslDOM, xslFileName));
                    } else {
                        XslTransformErrorLocatorHelper.systemId = SystemIDResolver.getAbsoluteURI(xslFileName);
                        stylesheet = tfactory.newTemplates(new StreamSource(xslFileName));
                    }
                }
                if (null != outFileName) {
                    strResult = new StreamResult(new FileOutputStream(outFileName));
                    strResult.setSystemId(outFileName);
                } else {
                    strResult = new StreamResult(System.out);
                }
                SAXTransformerFactory stf = (SAXTransformerFactory)tfactory;
                if (!useXSLTC && useSourceLocation) {
                    stf.setAttribute("http://xml.apache.org/xalan/properties/source-location", Boolean.TRUE);
                }
                if (null == stylesheet) {
                    Source source = stf.getAssociatedStylesheet(new StreamSource(inFileName), media, null, null);
                    if (null != source) {
                        stylesheet = tfactory.newTemplates(source);
                    } else {
                        if (null != media) {
                            throw new TransformerException(XSLMessages.createMessage("ER_NO_STYLESHEET_IN_MEDIA", new Object[]{inFileName, media}));
                        }
                        throw new TransformerException(XSLMessages.createMessage("ER_NO_STYLESHEET_PI", new Object[]{inFileName}));
                    }
                }
                if (null != stylesheet) {
                    int i;
                    Transformer transformer = flavor.equals("th") ? null : stylesheet.newTransformer();
                    transformer.setErrorListener(new DefaultErrorHandler(false));
                    if (null != outputType) {
                        transformer.setOutputProperty("method", outputType);
                    }
                    if (transformer instanceof TransformerImpl) {
                        TransformerImpl impl = (TransformerImpl)transformer;
                        TraceManager tm = impl.getTraceManager();
                        if (null != tracer) {
                            tm.addTraceListener(tracer);
                        }
                        impl.setQuietConflictWarnings(quietConflictWarnings);
                        if (useSourceLocation) {
                            impl.setProperty("http://xml.apache.org/xalan/properties/source-location", Boolean.TRUE);
                        }
                        if (recursionLimit > 0) {
                            impl.setRecursionLimit(recursionLimit);
                        }
                    }
                    int nParams = params.size();
                    for (i = 0; i < nParams; i += 2) {
                        transformer.setParameter((String)params.elementAt(i), (String)params.elementAt(i + 1));
                    }
                    if (uriResolver != null) {
                        transformer.setURIResolver(uriResolver);
                    }
                    if (null != inFileName) {
                        if (flavor.equals("d2d")) {
                            DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
                            dfactory.setCoalescing(true);
                            dfactory.setNamespaceAware(true);
                            if (isSecureProcessing) {
                                try {
                                    dfactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
                                }
                                catch (ParserConfigurationException parserConfigurationException) {
                                    // empty catch block
                                }
                            }
                            DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
                            if (entityResolver != null) {
                                docBuilder.setEntityResolver(entityResolver);
                            }
                            Document xmlDoc = docBuilder.parse(new InputSource(inFileName));
                            Document doc = docBuilder.newDocument();
                            DocumentFragment outNode = doc.createDocumentFragment();
                            transformer.transform(new DOMSource(xmlDoc, inFileName), new DOMResult(outNode));
                            Transformer serializer = stf.newTransformer();
                            serializer.setErrorListener(new DefaultErrorHandler(false));
                            Properties serializationProps = stylesheet.getOutputProperties();
                            serializer.setOutputProperties(serializationProps);
                            if (contentHandler != null) {
                                SAXResult result = new SAXResult(contentHandler);
                                serializer.transform(new DOMSource(outNode), result);
                            } else {
                                serializer.transform(new DOMSource(outNode), strResult);
                            }
                        } else if (flavor.equals("th")) {
                            for (i = 0; i < 1; ++i) {
                                XMLReader reader = null;
                                try {
                                    SAXParserFactory factory = SAXParserFactory.newInstance();
                                    factory.setNamespaceAware(true);
                                    if (isSecureProcessing) {
                                        try {
                                            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
                                        }
                                        catch (SAXException doc) {
                                            // empty catch block
                                        }
                                    }
                                    SAXParser jaxpParser = factory.newSAXParser();
                                    reader = jaxpParser.getXMLReader();
                                }
                                catch (ParserConfigurationException ex) {
                                    throw new SAXException(ex);
                                }
                                catch (FactoryConfigurationError ex1) {
                                    throw new SAXException(ex1.toString());
                                }
                                catch (NoSuchMethodError ex1) {
                                }
                                catch (AbstractMethodError ex1) {
                                    // empty catch block
                                }
                                if (null == reader) {
                                    reader = XMLReaderFactory.createXMLReader();
                                }
                                if (!useXSLTC) {
                                    stf.setAttribute("http://xml.apache.org/xalan/features/incremental", Boolean.TRUE);
                                }
                                TransformerHandler th = stf.newTransformerHandler(stylesheet);
                                reader.setContentHandler(th);
                                reader.setDTDHandler(th);
                                if (th instanceof ErrorHandler) {
                                    reader.setErrorHandler((ErrorHandler)((Object)th));
                                }
                                try {
                                    reader.setProperty("http://xml.org/sax/properties/lexical-handler", th);
                                }
                                catch (SAXNotRecognizedException sAXNotRecognizedException) {
                                }
                                catch (SAXNotSupportedException sAXNotSupportedException) {
                                    // empty catch block
                                }
                                try {
                                    reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
                                }
                                catch (SAXException sAXException) {
                                    // empty catch block
                                }
                                th.setResult(strResult);
                                reader.parse(new InputSource(inFileName));
                            }
                        } else if (entityResolver != null) {
                            XMLReader reader = null;
                            try {
                                SAXParserFactory factory = SAXParserFactory.newInstance();
                                factory.setNamespaceAware(true);
                                if (isSecureProcessing) {
                                    try {
                                        factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
                                    }
                                    catch (SAXException th) {
                                        // empty catch block
                                    }
                                }
                                SAXParser jaxpParser = factory.newSAXParser();
                                reader = jaxpParser.getXMLReader();
                            }
                            catch (ParserConfigurationException ex) {
                                throw new SAXException(ex);
                            }
                            catch (FactoryConfigurationError ex1) {
                                throw new SAXException(ex1.toString());
                            }
                            catch (NoSuchMethodError ex1) {
                            }
                            catch (AbstractMethodError ex1) {
                                // empty catch block
                            }
                            if (null == reader) {
                                reader = XMLReaderFactory.createXMLReader();
                            }
                            reader.setEntityResolver(entityResolver);
                            if (contentHandler != null) {
                                SAXResult result = new SAXResult(contentHandler);
                                transformer.transform(new SAXSource(reader, new InputSource(inFileName)), result);
                            } else {
                                transformer.transform(new SAXSource(reader, new InputSource(inFileName)), strResult);
                            }
                        } else if (contentHandler != null) {
                            SAXResult result = new SAXResult(contentHandler);
                            transformer.transform(new StreamSource(inFileName), result);
                        } else {
                            transformer.transform(new StreamSource(inFileName), strResult);
                        }
                    } else {
                        StringReader reader = new StringReader("<?xml version=\"1.0\"?> <doc/>");
                        transformer.transform(new StreamSource(reader), strResult);
                    }
                } else {
                    msg = XSLMessages.createMessage("ER_NOT_SUCCESSFUL", null);
                    diagnosticsWriter.println(msg);
                    Process.doExit(msg);
                }
                if (null != outFileName && strResult != null) {
                    OutputStream out = strResult.getOutputStream();
                    Writer writer = strResult.getWriter();
                    try {
                        if (out != null) {
                            out.close();
                        }
                        if (writer != null) {
                            writer.close();
                        }
                    }
                    catch (IOException reader) {
                        // empty catch block
                    }
                }
                long stop = System.currentTimeMillis();
                long millisecondsDuration = stop - start;
                if (doDiag) {
                    Object[] msgArgs = new Object[]{inFileName, xslFileName, new Long(millisecondsDuration)};
                    msg = XSLMessages.createMessage("diagTiming", msgArgs);
                    diagnosticsWriter.println('\n');
                    diagnosticsWriter.println(msg);
                }
            }
            catch (Throwable throwable2) {
                Exception throwable2;
                while (throwable2 instanceof WrappedRuntimeException) {
                    throwable2 = ((WrappedRuntimeException)throwable2).getException();
                }
                if (throwable2 instanceof NullPointerException || throwable2 instanceof ClassCastException) {
                    doStackDumpOnError = true;
                }
                diagnosticsWriter.println();
                if (doStackDumpOnError) {
                    throwable2.printStackTrace(dumpWriter);
                } else {
                    DefaultErrorHandler.printLocation(diagnosticsWriter, throwable2);
                    diagnosticsWriter.println(XSLMessages.createMessage("ER_XSLT_ERROR", null) + " (" + throwable2.getClass().getName() + "): " + throwable2.getMessage());
                }
                if (null != dumpFileName) {
                    dumpWriter.close();
                }
                Process.doExit(throwable2.getMessage());
            }
            if (null != dumpFileName) {
                dumpWriter.close();
            }
            if (null != diagnosticsWriter) {
                // empty if block
            }
        }
    }

    static void doExit(String msg) {
        throw new RuntimeException(msg);
    }

    private static void waitForReturnKey(ResourceBundle resbundle) {
        System.out.println(resbundle.getString("xslProc_return_to_continue"));
        try {
            while (System.in.read() != 10) {
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private static void printInvalidXSLTCOption(String option) {
        System.err.println(XSLMessages.createMessage("xslProc_invalid_xsltc_option", new Object[]{option}));
    }

    private static void printInvalidXalanOption(String option) {
        System.err.println(XSLMessages.createMessage("xslProc_invalid_xalan_option", new Object[]{option}));
    }
}

