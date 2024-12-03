/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.SerializationHandler
 */
package org.apache.xalan.xsltc.cmdline;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.Vector;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.DOMEnhancedForDTM;
import org.apache.xalan.xsltc.StripFilter;
import org.apache.xalan.xsltc.TransletException;
import org.apache.xalan.xsltc.cmdline.ObjectFactory;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.dom.DOMWSFilter;
import org.apache.xalan.xsltc.dom.XSLTCDTMManager;
import org.apache.xalan.xsltc.runtime.AbstractTranslet;
import org.apache.xalan.xsltc.runtime.Parameter;
import org.apache.xalan.xsltc.runtime.output.TransletOutputHandlerFactory;
import org.apache.xml.serializer.SerializationHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public final class Transform {
    private SerializationHandler _handler;
    private String _fileName;
    private String _className;
    private String _jarFileSrc;
    private boolean _isJarFileSpecified = false;
    private Vector _params = null;
    private boolean _uri;
    private boolean _debug;
    private int _iterations;

    public Transform(String className, String fileName, boolean uri, boolean debug, int iterations) {
        this._fileName = fileName;
        this._className = className;
        this._uri = uri;
        this._debug = debug;
        this._iterations = iterations;
    }

    public String getFileName() {
        return this._fileName;
    }

    public String getClassName() {
        return this._className;
    }

    public void setParameters(Vector params) {
        this._params = params;
    }

    private void setJarFileInputSrc(boolean flag, String jarFile) {
        this._isJarFileSpecified = flag;
        this._jarFileSrc = jarFile;
    }

    private void doTransform() {
        try {
            Class clazz = ObjectFactory.findProviderClass(this._className, ObjectFactory.findClassLoader(), true);
            AbstractTranslet translet = (AbstractTranslet)clazz.newInstance();
            translet.postInitialization();
            SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
                factory.setFeature("http://xml.org/sax/features/namespaces", true);
            }
            catch (Exception e) {
                factory.setNamespaceAware(true);
            }
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            XSLTCDTMManager dtmManager = (XSLTCDTMManager)XSLTCDTMManager.getDTMManagerClass().newInstance();
            DOMWSFilter wsfilter = translet != null && translet instanceof StripFilter ? new DOMWSFilter(translet) : null;
            DOMEnhancedForDTM dom = (DOMEnhancedForDTM)((Object)dtmManager.getDTM(new SAXSource(reader, new InputSource(this._fileName)), false, wsfilter, true, false, translet.hasIdCall()));
            dom.setDocumentURI(this._fileName);
            translet.prepassDocument(dom);
            int n = this._params.size();
            for (int i = 0; i < n; ++i) {
                Parameter param = (Parameter)this._params.elementAt(i);
                translet.addParameter(param._name, param._value);
            }
            TransletOutputHandlerFactory tohFactory = TransletOutputHandlerFactory.newInstance();
            tohFactory.setOutputType(0);
            tohFactory.setEncoding(translet._encoding);
            tohFactory.setOutputMethod(translet._method);
            if (this._iterations == -1) {
                translet.transform((DOM)dom, tohFactory.getSerializationHandler());
            } else if (this._iterations > 0) {
                long mm = System.currentTimeMillis();
                for (int i = 0; i < this._iterations; ++i) {
                    translet.transform((DOM)dom, tohFactory.getSerializationHandler());
                }
                mm = System.currentTimeMillis() - mm;
                System.err.println("\n<!--");
                System.err.println("  transform  = " + (double)mm / (double)this._iterations + " ms");
                System.err.println("  throughput = " + 1000.0 / ((double)mm / (double)this._iterations) + " tps");
                System.err.println("-->");
            }
        }
        catch (TransletException e) {
            if (this._debug) {
                e.printStackTrace();
            }
            System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + e.getMessage());
        }
        catch (RuntimeException e) {
            if (this._debug) {
                e.printStackTrace();
            }
            System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + e.getMessage());
        }
        catch (FileNotFoundException e) {
            if (this._debug) {
                e.printStackTrace();
            }
            ErrorMsg err = new ErrorMsg("FILE_NOT_FOUND_ERR", this._fileName);
            System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + err.toString());
        }
        catch (MalformedURLException e) {
            if (this._debug) {
                e.printStackTrace();
            }
            ErrorMsg err = new ErrorMsg("INVALID_URI_ERR", this._fileName);
            System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + err.toString());
        }
        catch (ClassNotFoundException e) {
            if (this._debug) {
                e.printStackTrace();
            }
            ErrorMsg err = new ErrorMsg("CLASS_NOT_FOUND_ERR", this._className);
            System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + err.toString());
        }
        catch (UnknownHostException e) {
            if (this._debug) {
                e.printStackTrace();
            }
            ErrorMsg err = new ErrorMsg("INVALID_URI_ERR", this._fileName);
            System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + err.toString());
        }
        catch (SAXException e) {
            Exception ex = e.getException();
            if (this._debug) {
                if (ex != null) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            }
            System.err.print(new ErrorMsg("RUNTIME_ERROR_KEY"));
            if (ex != null) {
                System.err.println(ex.getMessage());
            } else {
                System.err.println(e.getMessage());
            }
        }
        catch (Exception e) {
            if (this._debug) {
                e.printStackTrace();
            }
            System.err.println(new ErrorMsg("RUNTIME_ERROR_KEY") + e.getMessage());
        }
    }

    public static void printUsage() {
        System.err.println(new ErrorMsg("TRANSFORM_USAGE_STR"));
    }

    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                int i;
                int iterations = -1;
                boolean uri = false;
                boolean debug = false;
                boolean isJarFileSpecified = false;
                String jarFile = null;
                for (i = 0; i < args.length && args[i].charAt(0) == '-'; ++i) {
                    if (args[i].equals("-u")) {
                        uri = true;
                        continue;
                    }
                    if (args[i].equals("-x")) {
                        debug = true;
                        continue;
                    }
                    if (args[i].equals("-j")) {
                        isJarFileSpecified = true;
                        jarFile = args[++i];
                        continue;
                    }
                    if (args[i].equals("-n")) {
                        try {
                            iterations = Integer.parseInt(args[++i]);
                        }
                        catch (NumberFormatException numberFormatException) {}
                        continue;
                    }
                    Transform.printUsage();
                }
                if (args.length - i < 2) {
                    Transform.printUsage();
                }
                Transform handler = new Transform(args[i + 1], args[i], uri, debug, iterations);
                handler.setJarFileInputSrc(isJarFileSpecified, jarFile);
                Vector<Parameter> params = new Vector<Parameter>();
                i += 2;
                while (i < args.length) {
                    int equal = args[i].indexOf(61);
                    if (equal > 0) {
                        String name = args[i].substring(0, equal);
                        String value = args[i].substring(equal + 1);
                        params.addElement(new Parameter(name, value));
                    } else {
                        Transform.printUsage();
                    }
                    ++i;
                }
                if (i == args.length) {
                    handler.setParameters(params);
                    handler.doTransform();
                }
            } else {
                Transform.printUsage();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

