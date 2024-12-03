/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.util.DOMElementWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EchoProperties
extends Task {
    private static final String PROPERTIES = "properties";
    private static final String PROPERTY = "property";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "value";
    private File inFile = null;
    private File destfile = null;
    private boolean failonerror = true;
    private List<PropertySet> propertySets = new Vector<PropertySet>();
    private String format = "text";
    private String prefix;
    private String regex;

    public void setSrcfile(File file) {
        this.inFile = file;
    }

    public void setDestfile(File destfile) {
        this.destfile = destfile;
    }

    public void setFailOnError(boolean failonerror) {
        this.failonerror = failonerror;
    }

    public void setPrefix(String prefix) {
        if (prefix != null && !prefix.isEmpty()) {
            this.prefix = prefix;
            PropertySet ps = new PropertySet();
            ps.setProject(this.getProject());
            ps.appendPrefix(prefix);
            this.addPropertyset(ps);
        }
    }

    public void setRegex(String regex) {
        if (regex != null && !regex.isEmpty()) {
            this.regex = regex;
            PropertySet ps = new PropertySet();
            ps.setProject(this.getProject());
            ps.appendRegex(regex);
            this.addPropertyset(ps);
        }
    }

    public void addPropertyset(PropertySet ps) {
        this.propertySets.add(ps);
    }

    public void setFormat(FormatAttribute ea) {
        this.format = ea.getValue();
    }

    @Override
    public void execute() throws BuildException {
        if (this.prefix != null && this.regex != null) {
            throw new BuildException("Please specify either prefix or regex, but not both", this.getLocation());
        }
        Hashtable<Object, Object> allProps = new Hashtable<Object, Object>();
        if (this.inFile == null && this.propertySets.isEmpty()) {
            allProps.putAll(this.getProject().getProperties());
        } else if (this.inFile != null) {
            if (this.inFile.isDirectory()) {
                String message = "srcfile is a directory!";
                if (this.failonerror) {
                    throw new BuildException(message, this.getLocation());
                }
                this.log(message, 0);
                return;
            }
            if (this.inFile.exists() && !this.inFile.canRead()) {
                String message = "Can not read from the specified srcfile!";
                if (this.failonerror) {
                    throw new BuildException(message, this.getLocation());
                }
                this.log(message, 0);
                return;
            }
            try (InputStream in = Files.newInputStream(this.inFile.toPath(), new OpenOption[0]);){
                Properties props = new Properties();
                props.load(in);
                allProps.putAll(props);
            }
            catch (FileNotFoundException fnfe) {
                String message = "Could not find file " + this.inFile.getAbsolutePath();
                if (this.failonerror) {
                    throw new BuildException(message, fnfe, this.getLocation());
                }
                this.log(message, 1);
                return;
            }
            catch (IOException ioe) {
                String message = "Could not read file " + this.inFile.getAbsolutePath();
                if (this.failonerror) {
                    throw new BuildException(message, ioe, this.getLocation());
                }
                this.log(message, 1);
                return;
            }
        }
        this.propertySets.stream().map(PropertySet::getProperties).forEach(allProps::putAll);
        try (OutputStream os = this.createOutputStream();){
            if (os != null) {
                this.saveProperties(allProps, os);
            }
        }
        catch (IOException ioe) {
            if (this.failonerror) {
                throw new BuildException(ioe, this.getLocation());
            }
            this.log(ioe.getMessage(), 2);
        }
    }

    protected void saveProperties(Hashtable<Object, Object> allProps, OutputStream os) throws IOException, BuildException {
        final ArrayList<Object> keyList = new ArrayList<Object>(allProps.keySet());
        Properties props = new Properties(){
            private static final long serialVersionUID = 5090936442309201654L;

            @Override
            public Enumeration<Object> keys() {
                return keyList.stream().sorted(Comparator.comparing(Object::toString)).collect(Collectors.collectingAndThen(Collectors.toList(), Collections::enumeration));
            }

            @Override
            public Set<Map.Entry<Object, Object>> entrySet() {
                TreeSet<Map.Entry<Object, Object>> t = new TreeSet<Map.Entry<Object, Object>>(Comparator.comparing(((Function<Map.Entry, Object>)Map.Entry::getKey).andThen(Object::toString)));
                t.addAll(super.entrySet());
                return t;
            }
        };
        allProps.forEach((k, v) -> props.put(String.valueOf(k), String.valueOf(v)));
        if ("text".equals(this.format)) {
            this.jdkSaveProperties(props, os, "Ant properties");
        } else if ("xml".equals(this.format)) {
            this.xmlSaveProperties(props, os);
        }
    }

    private List<Tuple> sortProperties(Properties props) {
        return props.stringPropertyNames().stream().map(k -> new Tuple((String)k, props.getProperty((String)k))).sorted().collect(Collectors.toList());
    }

    protected void xmlSaveProperties(Properties props, OutputStream os) throws IOException {
        Document doc = EchoProperties.getDocumentBuilder().newDocument();
        Element rootElement = doc.createElement(PROPERTIES);
        List<Tuple> sorted = this.sortProperties(props);
        for (Tuple tuple : sorted) {
            Element propElement = doc.createElement(PROPERTY);
            propElement.setAttribute(ATTR_NAME, tuple.key);
            propElement.setAttribute(ATTR_VALUE, tuple.value);
            rootElement.appendChild(propElement);
        }
        try (OutputStreamWriter wri = new OutputStreamWriter(os, StandardCharsets.UTF_8);){
            wri.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            new DOMElementWriter().write(rootElement, wri, 0, "\t");
            ((Writer)wri).flush();
        }
        catch (IOException ioe) {
            throw new BuildException("Unable to write XML file", ioe);
        }
    }

    protected void jdkSaveProperties(Properties props, OutputStream os, String header) throws IOException {
        try {
            props.store(os, header);
        }
        catch (IOException ioe) {
            throw new BuildException(ioe, this.getLocation());
        }
        finally {
            if (os != null) {
                try {
                    os.close();
                }
                catch (IOException ioex) {
                    this.log("Failed to close output stream");
                }
            }
        }
    }

    private OutputStream createOutputStream() throws IOException {
        if (this.destfile == null) {
            return new LogOutputStream(this);
        }
        if (this.destfile.exists() && this.destfile.isDirectory()) {
            String message = "destfile is a directory!";
            if (this.failonerror) {
                throw new BuildException(message, this.getLocation());
            }
            this.log(message, 0);
            return null;
        }
        if (this.destfile.exists() && !this.destfile.canWrite()) {
            String message = "Can not write to the specified destfile!";
            if (this.failonerror) {
                throw new BuildException(message, this.getLocation());
            }
            this.log(message, 0);
            return null;
        }
        return Files.newOutputStream(this.destfile.toPath(), new OpenOption[0]);
    }

    private static DocumentBuilder getDocumentBuilder() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static class FormatAttribute
    extends EnumeratedAttribute {
        private String[] formats = new String[]{"xml", "text"};

        @Override
        public String[] getValues() {
            return this.formats;
        }
    }

    private static final class Tuple
    implements Comparable<Tuple> {
        private String key;
        private String value;

        private Tuple(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public int compareTo(Tuple o) {
            return Comparator.naturalOrder().compare(this.key, o.key);
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o == null || o.getClass() != this.getClass()) {
                return false;
            }
            Tuple that = (Tuple)o;
            return Objects.equals(this.key, that.key) && Objects.equals(this.value, that.value);
        }

        public int hashCode() {
            return Objects.hash(this.key);
        }
    }
}

