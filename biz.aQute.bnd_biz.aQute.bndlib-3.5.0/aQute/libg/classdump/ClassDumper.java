/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.classdump;

import aQute.lib.io.IO;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.nio.file.Paths;

public class ClassDumper {
    static final int ACC_PUBLIC = 1;
    static final int ACC_FINAL = 16;
    static final int ACC_SUPER = 32;
    static final int ACC_INTERFACE = 512;
    static final int ACC_ABSTRACT = 1024;
    final String path;
    static final String NUM_COLUMN = "%-30s %d%n";
    static final String HEX_COLUMN = "%-30s %x%n";
    static final String STR_COLUMN = "%-30s %s%n";
    PrintStream ps = System.err;
    Object[] pool;
    InputStream in;

    public ClassDumper(String path) throws Exception {
        this(path, IO.stream(Paths.get(path, new String[0])));
    }

    public ClassDumper(String path, InputStream in) throws IOException {
        this.path = path;
        this.in = in;
    }

    public void dump(PrintStream ps) throws Exception {
        if (ps != null) {
            this.ps = ps;
        }
        DataInputStream din = new DataInputStream(this.in);
        this.parseClassFile(din);
        din.close();
    }

    void parseClassFile(DataInputStream in) throws IOException {
        int magic = in.readInt();
        if (magic != -889275714) {
            throw new IOException("Not a valid class file (no CAFEBABE header)");
        }
        this.ps.printf(HEX_COLUMN, "magic", magic);
        int minor = in.readUnsignedShort();
        int major = in.readUnsignedShort();
        this.ps.printf(STR_COLUMN, "version", "" + major + "." + minor);
        int pool_size = in.readUnsignedShort();
        this.ps.printf(NUM_COLUMN, "pool size", pool_size);
        this.pool = new Object[pool_size];
        block15: for (int poolIndex = 1; poolIndex < pool_size; ++poolIndex) {
            byte tag = in.readByte();
            switch (tag) {
                case 0: {
                    this.ps.printf("%30d tag (0)%n", poolIndex);
                    break block15;
                }
                case 1: {
                    String name = in.readUTF();
                    this.pool[poolIndex] = name;
                    this.ps.printf("%30d tag(1) utf8 '%s'%n", poolIndex, name);
                    continue block15;
                }
                case 2: {
                    throw new IOException("Invalid tag " + tag);
                }
                case 3: {
                    int i = in.readInt();
                    this.pool[poolIndex] = i;
                    this.ps.printf("%30d tag(3) int %s%n", poolIndex, i);
                    continue block15;
                }
                case 4: {
                    float f = in.readFloat();
                    this.pool[poolIndex] = Float.valueOf(f);
                    this.ps.printf("%30d tag(4) float %s%n", poolIndex, Float.valueOf(f));
                    continue block15;
                }
                case 5: {
                    long l = in.readLong();
                    this.pool[poolIndex] = l;
                    this.ps.printf("%30d tag(5) long %s%n", poolIndex, l);
                    ++poolIndex;
                    continue block15;
                }
                case 6: {
                    double d = in.readDouble();
                    this.pool[poolIndex] = d;
                    this.ps.printf("%30d tag(6) double %s%n", poolIndex, d);
                    ++poolIndex;
                    continue block15;
                }
                case 7: {
                    int class_index = in.readUnsignedShort();
                    this.pool[poolIndex] = class_index;
                    this.ps.printf("%30d tag(7) constant classs %d%n", poolIndex, class_index);
                    continue block15;
                }
                case 8: {
                    int string_index = in.readUnsignedShort();
                    this.pool[poolIndex] = string_index;
                    this.ps.printf("%30d tag(8) constant string %d%n", poolIndex, string_index);
                    continue block15;
                }
                case 9: {
                    int class_index = in.readUnsignedShort();
                    int name_and_type_index = in.readUnsignedShort();
                    this.pool[poolIndex] = new Assoc(9, class_index, name_and_type_index);
                    this.ps.printf("%30d tag(9) field ref %d/%d%n", poolIndex, class_index, name_and_type_index);
                    continue block15;
                }
                case 10: {
                    int class_index = in.readUnsignedShort();
                    int name_and_type_index = in.readUnsignedShort();
                    this.pool[poolIndex] = new Assoc(10, class_index, name_and_type_index);
                    this.ps.printf("%30d tag(10) method ref %d/%d%n", poolIndex, class_index, name_and_type_index);
                    continue block15;
                }
                case 11: {
                    int class_index = in.readUnsignedShort();
                    int name_and_type_index = in.readUnsignedShort();
                    this.pool[poolIndex] = new Assoc(11, class_index, name_and_type_index);
                    this.ps.printf("%30d tag(11) interface and method ref %d/%d%n", poolIndex, class_index, name_and_type_index);
                    continue block15;
                }
                case 12: {
                    int name_index = in.readUnsignedShort();
                    int descriptor_index = in.readUnsignedShort();
                    this.pool[poolIndex] = new Assoc(tag, name_index, descriptor_index);
                    this.ps.printf("%30d tag(12) name and type %d/%d%n", poolIndex, name_index, descriptor_index);
                    continue block15;
                }
                default: {
                    throw new IllegalArgumentException("Unknown tag: " + tag);
                }
            }
        }
        int access = in.readUnsignedShort();
        this.printAccess(access);
        int this_class = in.readUnsignedShort();
        int super_class = in.readUnsignedShort();
        this.ps.printf("%-30s %x %s(#%d)%n", "this_class", access, this.pool[this_class], this_class);
        this.ps.printf("%-30s %s(#%d)%n", "super_class", this.pool[super_class], super_class);
        int interfaces_count = in.readUnsignedShort();
        this.ps.printf(NUM_COLUMN, "interface count", interfaces_count);
        for (int i = 0; i < interfaces_count; ++i) {
            int interface_index = in.readUnsignedShort();
            this.ps.printf("%-30s interface %s(#%d)", "interface count", this.pool[interface_index], interfaces_count);
        }
        int field_count = in.readUnsignedShort();
        this.ps.printf(NUM_COLUMN, "field count", field_count);
        for (int i = 0; i < field_count; ++i) {
            access = in.readUnsignedShort();
            this.printAccess(access);
            int name_index = in.readUnsignedShort();
            int descriptor_index = in.readUnsignedShort();
            this.ps.printf("%-30s %x %s(#%d) %s(#%d)%n", "field def", access, this.pool[name_index], name_index, this.pool[descriptor_index], descriptor_index);
            this.doAttributes(in, "  ");
        }
        int method_count = in.readUnsignedShort();
        this.ps.printf(NUM_COLUMN, "method count", method_count);
        for (int i = 0; i < method_count; ++i) {
            int access_flags = in.readUnsignedShort();
            this.printAccess(access_flags);
            int name_index = in.readUnsignedShort();
            int descriptor_index = in.readUnsignedShort();
            this.ps.printf("%-30s %x %s(#%d) %s(#%d)%n", "method def", access_flags, this.pool[name_index], name_index, this.pool[descriptor_index], descriptor_index);
            this.doAttributes(in, "  ");
        }
        this.doAttributes(in, "");
        if (in.read() >= 0) {
            this.ps.printf("Extra bytes follow ...", new Object[0]);
        }
    }

    private void doAttributes(DataInputStream in, String indent) throws IOException {
        int attribute_count = in.readUnsignedShort();
        this.ps.printf(NUM_COLUMN, indent + "attribute count", attribute_count);
        for (int j = 0; j < attribute_count; ++j) {
            this.doAttribute(in, indent + j + ": ");
        }
    }

    private void doAttribute(DataInputStream in, String indent) throws IOException {
        int attribute_name_index = in.readUnsignedShort();
        long attribute_length = in.readInt();
        attribute_length &= 0xFFFFL;
        String attributeName = (String)this.pool[attribute_name_index];
        this.ps.printf("%-30s %s(#%d)%n", indent + "attribute", attributeName, attribute_name_index);
        if ("RuntimeVisibleAnnotations".equals(attributeName)) {
            this.doAnnotations(in, indent);
        } else if ("SourceFile".equals(attributeName)) {
            this.doSourceFile(in, indent);
        } else if ("Code".equals(attributeName)) {
            this.doCode(in, indent);
        } else if ("LineNumberTable".equals(attributeName)) {
            this.doLineNumberTable(in, indent);
        } else if ("LocalVariableTable".equals(attributeName)) {
            this.doLocalVariableTable(in, indent);
        } else if ("InnerClasses".equals(attributeName)) {
            this.doInnerClasses(in, indent);
        } else if ("Exceptions".equals(attributeName)) {
            this.doExceptions(in, indent);
        } else if ("EnclosingMethod".equals(attributeName)) {
            this.doEnclosingMethod(in, indent);
        } else if ("Signature".equals(attributeName)) {
            this.doSignature(in, indent);
        } else if (!"Synthetic".equals(attributeName) && !"Deprecated".equals(attributeName)) {
            this.ps.printf(NUM_COLUMN, indent + "Unknown attribute, skipping", attribute_length);
            if (attribute_length > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Attribute > 2Gb");
            }
            byte[] buffer = new byte[(int)attribute_length];
            in.readFully(buffer);
            this.printHex(buffer);
        }
    }

    void doSignature(DataInputStream in, String indent) throws IOException {
        int signature_index = in.readUnsignedShort();
        this.ps.printf("%-30s %s(#%d)%n", indent + "signature", this.pool[signature_index], signature_index);
    }

    void doEnclosingMethod(DataInputStream in, String indent) throws IOException {
        int class_index = in.readUnsignedShort();
        int method_index = in.readUnsignedShort();
        this.ps.printf("%-30s %s(#%d/c) %s%n", indent + "enclosing method", this.pool[(Integer)this.pool[class_index]], class_index, method_index == 0 ? "<>" : this.pool[method_index]);
    }

    private void doExceptions(DataInputStream in, String indent) throws IOException {
        int number_of_exceptions = in.readUnsignedShort();
        this.ps.printf(NUM_COLUMN, indent + "number of exceptions", number_of_exceptions);
        StringBuilder sb = new StringBuilder();
        String del = "";
        for (int i = 0; i < number_of_exceptions; ++i) {
            int exception_index_table = in.readUnsignedShort();
            sb.append(del);
            sb.append(this.pool[(Integer)this.pool[exception_index_table]]);
            sb.append("(#");
            sb.append(exception_index_table);
            sb.append("/c)");
            del = ", ";
        }
        this.ps.printf("%-30s %d: %s%n", indent + "exceptions", number_of_exceptions, sb);
    }

    private void doCode(DataInputStream in, String indent) throws IOException {
        int max_stack = in.readUnsignedShort();
        int max_locals = in.readUnsignedShort();
        int code_length = in.readInt();
        this.ps.printf(NUM_COLUMN, indent + "max_stack", max_stack);
        this.ps.printf(NUM_COLUMN, indent + "max_locals", max_locals);
        this.ps.printf(NUM_COLUMN, indent + "code_length", code_length);
        byte[] code = new byte[code_length];
        in.readFully(code);
        this.printHex(code);
        int exception_table_length = in.readUnsignedShort();
        this.ps.printf(NUM_COLUMN, indent + "exception_table_length", exception_table_length);
        for (int i = 0; i < exception_table_length; ++i) {
            int start_pc = in.readUnsignedShort();
            int end_pc = in.readUnsignedShort();
            int handler_pc = in.readUnsignedShort();
            int catch_type = in.readUnsignedShort();
            this.ps.printf("%-30s %d/%d/%d/%d%n", indent + "exception_table", start_pc, end_pc, handler_pc, catch_type);
        }
        this.doAttributes(in, indent + "  ");
    }

    protected void printHex(byte[] code) {
        int index = 0;
        while (index < code.length) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 16 && index < code.length; ++i) {
                String s;
                if ((s = Integer.toHexString(0xFF & code[index++]).toUpperCase()).length() == 1) {
                    sb.append("0");
                }
                sb.append(s);
                sb.append(" ");
            }
            this.ps.printf(STR_COLUMN, "", sb.toString());
        }
    }

    private void doSourceFile(DataInputStream in, String indent) throws IOException {
        int sourcefile_index = in.readUnsignedShort();
        this.ps.printf("%-30s %s(#%d)%n", indent + "Source file", this.pool[sourcefile_index], sourcefile_index);
    }

    private void doAnnotations(DataInputStream in, String indent) throws IOException {
        int num_annotations = in.readUnsignedShort();
        this.ps.printf(NUM_COLUMN, indent + "Number of annotations", num_annotations);
        for (int a = 0; a < num_annotations; ++a) {
            this.doAnnotation(in, indent);
        }
    }

    private void doAnnotation(DataInputStream in, String indent) throws IOException {
        int type_index = in.readUnsignedShort();
        this.ps.printf("%-30s %s(#%d)", indent + "type", this.pool[type_index], type_index);
        int num_element_value_pairs = in.readUnsignedShort();
        this.ps.printf(NUM_COLUMN, indent + "num_element_value_pairs", num_element_value_pairs);
        for (int v = 0; v < num_element_value_pairs; ++v) {
            int element_name_index = in.readUnsignedShort();
            this.ps.printf(NUM_COLUMN, indent + "element_name_index", element_name_index);
            this.doElementValue(in, indent);
        }
    }

    private void doElementValue(DataInputStream in, String indent) throws IOException {
        int tag = in.readUnsignedByte();
        switch (tag) {
            case 66: 
            case 67: 
            case 68: 
            case 70: 
            case 73: 
            case 74: 
            case 83: 
            case 90: 
            case 115: {
                int const_value_index = in.readUnsignedShort();
                this.ps.printf("%-30s %c %s(#%d)%n", indent + "element value", tag, this.pool[const_value_index], const_value_index);
                break;
            }
            case 101: {
                int type_name_index = in.readUnsignedShort();
                int const_name_index = in.readUnsignedShort();
                this.ps.printf("%-30s %c %s(#%d) %s(#%d)%n", indent + "type+const", tag, this.pool[type_name_index], type_name_index, this.pool[const_name_index], const_name_index);
                break;
            }
            case 99: {
                int class_info_index = in.readUnsignedShort();
                this.ps.printf("%-30s %c %s(#%d)%n", indent + "element value", tag, this.pool[class_info_index], class_info_index);
                break;
            }
            case 64: {
                this.ps.printf("%-30s %c%n", indent + "sub annotation", tag);
                this.doAnnotation(in, indent);
                break;
            }
            case 91: {
                int num_values = in.readUnsignedShort();
                this.ps.printf("%-30s %c num_values=%d%n", indent + "sub element value", tag, num_values);
                for (int i = 0; i < num_values; ++i) {
                    this.doElementValue(in, indent);
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid value for Annotation ElementValue tag " + tag);
            }
        }
    }

    void doLineNumberTable(DataInputStream in, String indent) throws IOException {
        int line_number_table_length = in.readUnsignedShort();
        this.ps.printf(NUM_COLUMN, indent + "line number table length", line_number_table_length);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line_number_table_length; ++i) {
            int start_pc = in.readUnsignedShort();
            int line_number = in.readUnsignedShort();
            sb.append(start_pc);
            sb.append("/");
            sb.append(line_number);
            sb.append(" ");
        }
        this.ps.printf("%-30s %d: %s%n", indent + "line number table", line_number_table_length, sb);
    }

    void doLocalVariableTable(DataInputStream in, String indent) throws IOException {
        int local_variable_table_length = in.readUnsignedShort();
        this.ps.printf(NUM_COLUMN, indent + "local variable table length", local_variable_table_length);
        for (int i = 0; i < local_variable_table_length; ++i) {
            int start_pc = in.readUnsignedShort();
            int length = in.readUnsignedShort();
            int name_index = in.readUnsignedShort();
            int descriptor_index = in.readUnsignedShort();
            int index = in.readUnsignedShort();
            this.ps.printf("%-30s %d: %d/%d %s(#%d) %s(#%d)%n", indent, index, start_pc, length, this.pool[name_index], name_index, this.pool[descriptor_index], descriptor_index);
        }
    }

    void doInnerClasses(DataInputStream in, String indent) throws IOException {
        int number_of_classes = in.readUnsignedShort();
        this.ps.printf(NUM_COLUMN, indent + "number of classes", number_of_classes);
        for (int i = 0; i < number_of_classes; ++i) {
            String iname;
            int inner_class_info_index = in.readUnsignedShort();
            int outer_class_info_index = in.readUnsignedShort();
            int inner_name_index = in.readUnsignedShort();
            int inner_class_access_flags = in.readUnsignedShort();
            this.printAccess(inner_class_access_flags);
            String oname = iname = "<>";
            if (inner_class_info_index != 0) {
                iname = (String)this.pool[(Integer)this.pool[inner_class_info_index]];
            }
            if (outer_class_info_index != 0) {
                oname = (String)this.pool[(Integer)this.pool[outer_class_info_index]];
            }
            this.ps.printf("%-30s %d: %x %s(#%d/c) %s(#%d/c) %s(#%d) %n", indent, i, inner_class_access_flags, iname, inner_class_info_index, oname, outer_class_info_index, this.pool[inner_name_index], inner_name_index);
        }
    }

    void printClassAccess(int mod) {
        this.ps.printf("%-30s", "Class Access");
        if ((1 & mod) != 0) {
            this.ps.print(" public");
        }
        if ((0x10 & mod) != 0) {
            this.ps.print(" final");
        }
        if ((0x20 & mod) != 0) {
            this.ps.print(" super");
        }
        if ((0x200 & mod) != 0) {
            this.ps.print(" interface");
        }
        if ((0x400 & mod) != 0) {
            this.ps.print(" abstract");
        }
        this.ps.println();
    }

    void printAccess(int mod) {
        this.ps.printf("%-30s", "Access");
        if (Modifier.isStatic(mod)) {
            this.ps.print(" static");
        }
        if (Modifier.isAbstract(mod)) {
            this.ps.print(" abstract");
        }
        if (Modifier.isPublic(mod)) {
            this.ps.print(" public");
        }
        if (Modifier.isFinal(mod)) {
            this.ps.print(" final");
        }
        if (Modifier.isInterface(mod)) {
            this.ps.print(" interface");
        }
        if (Modifier.isNative(mod)) {
            this.ps.print(" native");
        }
        if (Modifier.isPrivate(mod)) {
            this.ps.print(" private");
        }
        if (Modifier.isProtected(mod)) {
            this.ps.print(" protected");
        }
        if (Modifier.isStrict(mod)) {
            this.ps.print(" strict");
        }
        if (Modifier.isSynchronized(mod)) {
            this.ps.print(" synchronized");
        }
        if (Modifier.isTransient(mod)) {
            this.ps.print(" transient");
        }
        if (Modifier.isVolatile(mod)) {
            this.ps.print(" volatile");
        }
        this.ps.println();
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("clsd <class file>+");
        }
        for (int i = 0; i < args.length; ++i) {
            File f = new File(args[i]);
            if (!f.isFile()) {
                System.err.println("File does not exist or is directory " + f);
                continue;
            }
            ClassDumper cd = new ClassDumper(args[i]);
            cd.dump(null);
        }
    }

    static final class Assoc {
        byte tag;
        int a;
        int b;

        Assoc(byte tag, int a, int b) {
            this.tag = tag;
            this.a = a;
            this.b = b;
        }
    }
}

