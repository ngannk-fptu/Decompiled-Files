/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.bcel.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.apache.bcel.Const;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.ConstantValue;
import org.apache.bcel.classfile.EmptyVisitor;
import org.apache.bcel.classfile.ExceptionTable;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.BCELFactory;
import org.apache.commons.lang3.StringUtils;

public class BCELifier
extends EmptyVisitor {
    private static final String BASE_PACKAGE = Const.class.getPackage().getName();
    private static final String CONSTANT_PREFIX = Const.class.getSimpleName() + ".";
    private final JavaClass clazz;
    private final PrintWriter printWriter;
    private final ConstantPoolGen constantPoolGen;

    static JavaClass getJavaClass(String name) throws ClassNotFoundException, IOException {
        JavaClass javaClass = Repository.lookupClass(name);
        if (javaClass == null) {
            javaClass = new ClassParser(name).parse();
        }
        return javaClass;
    }

    public static void main(String[] argv) throws Exception {
        if (argv.length != 1) {
            System.out.println("Usage: BCELifier className");
            System.out.println("\tThe class must exist on the classpath");
            return;
        }
        BCELifier bcelifier = new BCELifier(BCELifier.getJavaClass(argv[0]), System.out);
        bcelifier.start();
    }

    static String printArgumentTypes(Type[] argTypes) {
        if (argTypes.length == 0) {
            return "Type.NO_ARGS";
        }
        StringBuilder args = new StringBuilder();
        for (int i = 0; i < argTypes.length; ++i) {
            args.append(BCELifier.printType(argTypes[i]));
            if (i >= argTypes.length - 1) continue;
            args.append(", ");
        }
        return "new Type[] { " + args.toString() + " }";
    }

    static String printFlags(int flags) {
        return BCELifier.printFlags(flags, FLAGS.UNKNOWN);
    }

    public static String printFlags(int flags, FLAGS location) {
        if (flags == 0) {
            return "0";
        }
        StringBuilder buf = new StringBuilder();
        int i = 0;
        int pow = 1;
        while (pow <= 32768) {
            if ((flags & pow) != 0) {
                if (pow == 32 && location == FLAGS.CLASS) {
                    buf.append(CONSTANT_PREFIX).append("ACC_SUPER | ");
                } else if (pow == 64 && location == FLAGS.METHOD) {
                    buf.append(CONSTANT_PREFIX).append("ACC_BRIDGE | ");
                } else if (pow == 128 && location == FLAGS.METHOD) {
                    buf.append(CONSTANT_PREFIX).append("ACC_VARARGS | ");
                } else if (i < Const.ACCESS_NAMES_LENGTH) {
                    buf.append(CONSTANT_PREFIX).append("ACC_").append(Const.getAccessName(i).toUpperCase(Locale.ENGLISH)).append(" | ");
                } else {
                    buf.append(String.format(CONSTANT_PREFIX + "ACC_BIT %x | ", pow));
                }
            }
            pow <<= 1;
            ++i;
        }
        String str = buf.toString();
        return str.substring(0, str.length() - 3);
    }

    static String printType(String signature) {
        Type type = Type.getType(signature);
        byte t = type.getType();
        if (t <= 12) {
            return "Type." + Const.getTypeName(t).toUpperCase(Locale.ENGLISH);
        }
        if (type.toString().equals("java.lang.String")) {
            return "Type.STRING";
        }
        if (type.toString().equals("java.lang.Object")) {
            return "Type.OBJECT";
        }
        if (type.toString().equals("java.lang.StringBuffer")) {
            return "Type.STRINGBUFFER";
        }
        if (type instanceof ArrayType) {
            ArrayType at = (ArrayType)type;
            return "new ArrayType(" + BCELifier.printType(at.getBasicType()) + ", " + at.getDimensions() + ")";
        }
        return "new ObjectType(\"" + Utility.signatureToString(signature, false) + "\")";
    }

    static String printType(Type type) {
        return BCELifier.printType(type.getSignature());
    }

    public BCELifier(JavaClass clazz, OutputStream out) {
        this.clazz = clazz;
        this.printWriter = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), false);
        this.constantPoolGen = new ConstantPoolGen(this.clazz.getConstantPool());
    }

    private void printCreate() {
        this.printWriter.println("  public void create(OutputStream out) throws IOException {");
        Field[] fields = this.clazz.getFields();
        if (fields.length > 0) {
            this.printWriter.println("    createFields();");
        }
        Method[] methods = this.clazz.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            this.printWriter.println("    createMethod_" + i + "();");
        }
        this.printWriter.println("    _cg.getJavaClass().dump(out);");
        this.printWriter.println("  }");
        this.printWriter.println();
    }

    private void printMain() {
        String className = this.clazz.getClassName();
        this.printWriter.println("  public static void main(String[] args) throws Exception {");
        this.printWriter.println("    " + className + "Creator creator = new " + className + "Creator();");
        this.printWriter.println("    creator.create(new FileOutputStream(\"" + className + ".class\"));");
        this.printWriter.println("  }");
    }

    public void start() {
        this.visitJavaClass(this.clazz);
        this.printWriter.flush();
    }

    @Override
    public void visitField(Field field) {
        this.printWriter.println();
        this.printWriter.println("    field = new FieldGen(" + BCELifier.printFlags(field.getAccessFlags()) + ", " + BCELifier.printType(field.getSignature()) + ", \"" + field.getName() + "\", _cp);");
        ConstantValue cv = field.getConstantValue();
        if (cv != null) {
            this.printWriter.print("    field.setInitValue(");
            if (field.getType() == Type.CHAR) {
                this.printWriter.print("(char)");
            }
            if (field.getType() == Type.SHORT) {
                this.printWriter.print("(short)");
            }
            if (field.getType() == Type.BYTE) {
                this.printWriter.print("(byte)");
            }
            this.printWriter.print(cv);
            if (field.getType() == Type.LONG) {
                this.printWriter.print("L");
            }
            if (field.getType() == Type.FLOAT) {
                this.printWriter.print("F");
            }
            if (field.getType() == Type.DOUBLE) {
                this.printWriter.print("D");
            }
            this.printWriter.println(");");
        }
        this.printWriter.println("    _cg.addField(field.getField());");
    }

    @Override
    public void visitJavaClass(JavaClass clazz) {
        String className = clazz.getClassName();
        String superName = clazz.getSuperclassName();
        String packageName = clazz.getPackageName();
        String inter = Utility.printArray(clazz.getInterfaceNames(), false, true);
        if (StringUtils.isNotEmpty((CharSequence)packageName)) {
            className = className.substring(packageName.length() + 1);
            this.printWriter.println("package " + packageName + ";");
            this.printWriter.println();
        }
        this.printWriter.println("import " + BASE_PACKAGE + ".generic.*;");
        this.printWriter.println("import " + BASE_PACKAGE + ".classfile.*;");
        this.printWriter.println("import " + BASE_PACKAGE + ".*;");
        this.printWriter.println("import java.io.*;");
        this.printWriter.println();
        this.printWriter.println("public class " + className + "Creator {");
        this.printWriter.println("  private InstructionFactory _factory;");
        this.printWriter.println("  private ConstantPoolGen    _cp;");
        this.printWriter.println("  private ClassGen           _cg;");
        this.printWriter.println();
        this.printWriter.println("  public " + className + "Creator() {");
        this.printWriter.println("    _cg = new ClassGen(\"" + (packageName.isEmpty() ? className : packageName + "." + className) + "\", \"" + superName + "\", \"" + clazz.getSourceFileName() + "\", " + BCELifier.printFlags(clazz.getAccessFlags(), FLAGS.CLASS) + ", new String[] { " + inter + " });");
        this.printWriter.println("    _cg.setMajor(" + clazz.getMajor() + ");");
        this.printWriter.println("    _cg.setMinor(" + clazz.getMinor() + ");");
        this.printWriter.println();
        this.printWriter.println("    _cp = _cg.getConstantPool();");
        this.printWriter.println("    _factory = new InstructionFactory(_cg, _cp);");
        this.printWriter.println("  }");
        this.printWriter.println();
        this.printCreate();
        Field[] fields = clazz.getFields();
        if (fields.length > 0) {
            this.printWriter.println("  private void createFields() {");
            this.printWriter.println("    FieldGen field;");
            for (Field field : fields) {
                field.accept(this);
            }
            this.printWriter.println("  }");
            this.printWriter.println();
        }
        Method[] methods = clazz.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            this.printWriter.println("  private void createMethod_" + i + "() {");
            methods[i].accept(this);
            this.printWriter.println("  }");
            this.printWriter.println();
        }
        this.printMain();
        this.printWriter.println("}");
    }

    @Override
    public void visitMethod(Method method) {
        MethodGen mg = new MethodGen(method, this.clazz.getClassName(), this.constantPoolGen);
        this.printWriter.println("    InstructionList il = new InstructionList();");
        this.printWriter.println("    MethodGen method = new MethodGen(" + BCELifier.printFlags(method.getAccessFlags(), FLAGS.METHOD) + ", " + BCELifier.printType(mg.getReturnType()) + ", " + BCELifier.printArgumentTypes(mg.getArgumentTypes()) + ", new String[] { " + Utility.printArray(mg.getArgumentNames(), false, true) + " }, \"" + method.getName() + "\", \"" + this.clazz.getClassName() + "\", il, _cp);");
        ExceptionTable exceptionTable = method.getExceptionTable();
        if (exceptionTable != null) {
            String[] exceptionNames;
            for (String exceptionName : exceptionNames = exceptionTable.getExceptionNames()) {
                this.printWriter.print("    method.addException(\"");
                this.printWriter.print(exceptionName);
                this.printWriter.println("\");");
            }
        }
        this.printWriter.println();
        BCELFactory factory = new BCELFactory(mg, this.printWriter);
        factory.start();
        this.printWriter.println("    method.setMaxStack();");
        this.printWriter.println("    method.setMaxLocals();");
        this.printWriter.println("    _cg.addMethod(method.getMethod());");
        this.printWriter.println("    il.dispose();");
    }

    public static enum FLAGS {
        UNKNOWN,
        CLASS,
        METHOD;

    }
}

