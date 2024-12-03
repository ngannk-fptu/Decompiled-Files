/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.IntrospectionHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.Reference;

public class AntStructure
extends Task {
    private File output;
    private StructurePrinter printer = new DTDPrinter();

    public void setOutput(File output) {
        this.output = output;
    }

    public void add(StructurePrinter p) {
        this.printer = p;
    }

    @Override
    public void execute() throws BuildException {
        if (this.output == null) {
            throw new BuildException("output attribute is required", this.getLocation());
        }
        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(this.output.toPath(), new OpenOption[0]), StandardCharsets.UTF_8));){
            this.printer.printHead(out, this.getProject(), new Hashtable(this.getProject().getTaskDefinitions()), new Hashtable(this.getProject().getDataTypeDefinitions()));
            this.printer.printTargetDecl(out);
            for (String typeName : this.getProject().getCopyOfDataTypeDefinitions().keySet()) {
                this.printer.printElementDecl(out, this.getProject(), typeName, this.getProject().getDataTypeDefinitions().get(typeName));
            }
            for (String tName : this.getProject().getCopyOfTaskDefinitions().keySet()) {
                this.printer.printElementDecl(out, this.getProject(), tName, this.getProject().getTaskDefinitions().get(tName));
            }
            this.printer.printTail(out);
            if (out.checkError()) {
                throw new IOException("Encountered an error writing Ant structure");
            }
        }
        catch (IOException ioe) {
            throw new BuildException("Error writing " + this.output.getAbsolutePath(), ioe, this.getLocation());
        }
    }

    protected boolean isNmtoken(String s) {
        return DTDPrinter.isNmtoken(s);
    }

    protected boolean areNmtokens(String[] s) {
        return DTDPrinter.areNmtokens(s);
    }

    private static class DTDPrinter
    implements StructurePrinter {
        private static final String BOOLEAN = "%boolean;";
        private static final String TASKS = "%tasks;";
        private static final String TYPES = "%types;";
        private final Hashtable<String, String> visited = new Hashtable();

        private DTDPrinter() {
        }

        @Override
        public void printTail(PrintWriter out) {
            this.visited.clear();
        }

        @Override
        public void printHead(PrintWriter out, Project p, Hashtable<String, Class<?>> tasks, Hashtable<String, Class<?>> types) {
            this.printHead(out, tasks.keySet(), types.keySet());
        }

        private void printHead(PrintWriter out, Set<String> tasks, Set<String> types) {
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
            out.println("<!ENTITY % boolean \"(true|false|on|off|yes|no)\">");
            out.println(tasks.stream().collect(Collectors.joining(" | ", "<!ENTITY % tasks \"", "\">")));
            out.println(types.stream().collect(Collectors.joining(" | ", "<!ENTITY % types \"", "\">")));
            out.println();
            out.print("<!ELEMENT project (target | extension-point | ");
            out.print(TASKS);
            out.print(" | ");
            out.print(TYPES);
            out.println(")*>");
            out.println("<!ATTLIST project");
            out.println("          name    CDATA #IMPLIED");
            out.println("          default CDATA #IMPLIED");
            out.println("          basedir CDATA #IMPLIED>");
            out.println("");
        }

        @Override
        public void printTargetDecl(PrintWriter out) {
            out.print("<!ELEMENT target (");
            out.print(TASKS);
            out.print(" | ");
            out.print(TYPES);
            out.println(")*>");
            out.println("");
            this.printTargetAttrs(out, "target");
            out.println("<!ELEMENT extension-point EMPTY>");
            out.println("");
            this.printTargetAttrs(out, "extension-point");
        }

        private void printTargetAttrs(PrintWriter out, String tag) {
            out.print("<!ATTLIST ");
            out.println(tag);
            out.println("          id                      ID    #IMPLIED");
            out.println("          name                    CDATA #REQUIRED");
            out.println("          if                      CDATA #IMPLIED");
            out.println("          unless                  CDATA #IMPLIED");
            out.println("          depends                 CDATA #IMPLIED");
            out.println("          extensionOf             CDATA #IMPLIED");
            out.println("          onMissingExtensionPoint CDATA #IMPLIED");
            out.println("          description             CDATA #IMPLIED>");
            out.println("");
        }

        @Override
        public void printElementDecl(PrintWriter out, Project p, String name, Class<?> element) {
            IntrospectionHelper ih;
            if (this.visited.containsKey(name)) {
                return;
            }
            this.visited.put(name, "");
            try {
                ih = IntrospectionHelper.getHelper(p, element);
            }
            catch (Throwable t) {
                return;
            }
            StringBuilder sb = new StringBuilder("<!ELEMENT ").append(name).append(" ");
            if (Reference.class.equals(element)) {
                sb.append(String.format("EMPTY>%n<!ATTLIST %s%n          id ID #IMPLIED%n          refid IDREF #IMPLIED>%n", name));
                out.println(sb);
                return;
            }
            ArrayList<String> v = new ArrayList<String>();
            if (ih.supportsCharacters()) {
                v.add("#PCDATA");
            }
            if (TaskContainer.class.isAssignableFrom(element)) {
                v.add(TASKS);
            }
            v.addAll(Collections.list(ih.getNestedElements()));
            Collector<CharSequence, ?, String> joinAlts = Collectors.joining(" | ", "(", ")");
            if (v.isEmpty()) {
                sb.append("EMPTY");
            } else {
                sb.append(v.stream().collect(joinAlts));
                if (v.size() > 1 || !"#PCDATA".equals(v.get(0))) {
                    sb.append("*");
                }
            }
            sb.append(">");
            out.println(sb);
            sb = new StringBuilder();
            sb.append(String.format("<!ATTLIST %s%n          id ID #IMPLIED", name));
            for (String attrName : Collections.list(ih.getAttributes())) {
                block25: {
                    if ("id".equals(attrName)) continue;
                    sb.append(String.format("%n          %s ", attrName));
                    Class<?> type = ih.getAttributeType(attrName);
                    if (type.equals(Boolean.class) || type.equals(Boolean.TYPE)) {
                        sb.append(BOOLEAN).append(" ");
                    } else if (Reference.class.isAssignableFrom(type)) {
                        sb.append("IDREF ");
                    } else if (EnumeratedAttribute.class.isAssignableFrom(type)) {
                        try {
                            EnumeratedAttribute ea = type.asSubclass(EnumeratedAttribute.class).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                            String[] values = ea.getValues();
                            if (values == null || values.length == 0 || !DTDPrinter.areNmtokens(values)) {
                                sb.append("CDATA ");
                                break block25;
                            }
                            sb.append(Stream.of(values).collect(joinAlts)).append(" ");
                        }
                        catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException ie) {
                            sb.append("CDATA ");
                        }
                    } else if (Enum.class.isAssignableFrom(type)) {
                        try {
                            Enum[] values = (Enum[])type.getMethod("values", new Class[0]).invoke(null, new Object[0]);
                            if (values.length == 0) {
                                sb.append("CDATA ");
                                break block25;
                            }
                            sb.append(Stream.of(values).map(Enum::name).collect(joinAlts)).append(" ");
                        }
                        catch (Exception x) {
                            sb.append("CDATA ");
                        }
                    } else {
                        sb.append("CDATA ");
                    }
                }
                sb.append("#IMPLIED");
            }
            sb.append(String.format(">%n", new Object[0]));
            out.println(sb);
            for (String nestedName : v) {
                if ("#PCDATA".equals(nestedName) || TASKS.equals(nestedName) || TYPES.equals(nestedName)) continue;
                this.printElementDecl(out, p, nestedName, ih.getElementType(nestedName));
            }
        }

        public static final boolean isNmtoken(String s) {
            int length = s.length();
            for (int i = 0; i < length; ++i) {
                char c = s.charAt(i);
                if (Character.isLetterOrDigit(c) || c == '.' || c == '-' || c == '_' || c == ':') continue;
                return false;
            }
            return true;
        }

        public static final boolean areNmtokens(String[] s) {
            for (String value : s) {
                if (DTDPrinter.isNmtoken(value)) continue;
                return false;
            }
            return true;
        }
    }

    public static interface StructurePrinter {
        public void printHead(PrintWriter var1, Project var2, Hashtable<String, Class<?>> var3, Hashtable<String, Class<?>> var4);

        public void printTargetDecl(PrintWriter var1);

        public void printElementDecl(PrintWriter var1, Project var2, String var3, Class<?> var4);

        public void printTail(PrintWriter var1);
    }
}

