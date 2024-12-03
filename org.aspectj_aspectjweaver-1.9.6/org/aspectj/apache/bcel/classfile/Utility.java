/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.aspectj.apache.bcel.Constants;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.ClassFormatException;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.LocalVariable;
import org.aspectj.apache.bcel.classfile.LocalVariableTable;
import org.aspectj.apache.bcel.classfile.annotation.AnnotationGen;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeAnnos;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeInvisAnnos;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeInvisParamAnnos;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeParamAnnos;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeVisAnnos;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeVisParamAnnos;
import org.aspectj.apache.bcel.generic.Type;
import org.aspectj.apache.bcel.util.ByteSequence;

public abstract class Utility {
    private static boolean wide = false;

    public static final String accessToString(int access_flags) {
        return Utility.accessToString(access_flags, false);
    }

    public static final String accessToString(int access_flags, boolean for_class) {
        StringBuffer buf = new StringBuffer();
        int p = 0;
        int i = 0;
        while (p < 2048) {
            p = Utility.pow2(i);
            if ((access_flags & p) != 0 && (!for_class || p != 32 && p != 512)) {
                buf.append(Constants.ACCESS_NAMES[i]).append(" ");
            }
            ++i;
        }
        return buf.toString().trim();
    }

    public static final String classOrInterface(int access_flags) {
        return (access_flags & 0x200) != 0 ? "interface" : "class";
    }

    public static final String codeToString(byte[] code, ConstantPool constant_pool, int index, int length, boolean verbose) {
        StringBuffer buf = new StringBuffer(code.length * 20);
        ByteSequence stream = new ByteSequence(code);
        try {
            int i;
            for (i = 0; i < index; ++i) {
                Utility.codeToString(stream, constant_pool, verbose);
            }
            i = 0;
            while (stream.available() > 0) {
                if (length < 0 || i < length) {
                    String indices = Utility.fillup(stream.getIndex() + ":", 6, true, ' ');
                    buf.append(indices + Utility.codeToString(stream, constant_pool, verbose) + '\n');
                }
                ++i;
            }
        }
        catch (IOException e) {
            System.out.println(buf.toString());
            e.printStackTrace();
            throw new ClassFormatException("Byte code error: " + e);
        }
        return buf.toString();
    }

    public static final String codeToString(byte[] code, ConstantPool constant_pool, int index, int length) {
        return Utility.codeToString(code, constant_pool, index, length, true);
    }

    public static final String codeToString(ByteSequence bytes, ConstantPool constant_pool) throws IOException {
        return Utility.codeToString(bytes, constant_pool, true);
    }

    public static final String compactClassName(String str) {
        return Utility.compactClassName(str, true);
    }

    public static final String compactClassName(String str, String prefix, boolean chopit) {
        str = str.replace('/', '.');
        if (chopit) {
            String result;
            int len = prefix.length();
            if (str.startsWith(prefix) && (result = str.substring(len)).indexOf(46) == -1) {
                str = result;
            }
        }
        return str;
    }

    public static final String compactClassName(String str, boolean chopit) {
        return Utility.compactClassName(str, "java.lang.", chopit);
    }

    public static final String methodSignatureToString(String signature, String name, String access) {
        return Utility.methodSignatureToString(signature, name, access, true);
    }

    public static final String methodSignatureToString(String signature, String name, String access, boolean chopit) {
        return Utility.methodSignatureToString(signature, name, access, chopit, null);
    }

    public static final String methodSignatureToString(String signature, String name, String access, boolean chopit, LocalVariableTable vars) throws ClassFormatException {
        String type;
        StringBuffer buf = new StringBuffer("(");
        int var_index = access.indexOf("static") >= 0 ? 0 : 1;
        try {
            if (signature.charAt(0) != '(') {
                throw new ClassFormatException("Invalid method signature: " + signature);
            }
            int index = 1;
            while (signature.charAt(index) != ')') {
                ResultHolder rh = Utility.signatureToStringInternal(signature.substring(index), chopit);
                String param_type = rh.getResult();
                buf.append(param_type);
                if (vars != null) {
                    LocalVariable l = vars.getLocalVariable(var_index);
                    if (l != null) {
                        buf.append(" " + l.getName());
                    }
                } else {
                    buf.append(" arg" + var_index);
                }
                var_index = "double".equals(param_type) || "long".equals(param_type) ? (var_index += 2) : ++var_index;
                buf.append(", ");
                index += rh.getConsumedChars();
            }
            type = Utility.signatureToString(signature.substring(++index), chopit);
        }
        catch (StringIndexOutOfBoundsException e) {
            throw new ClassFormatException("Invalid method signature: " + signature);
        }
        if (buf.length() > 1) {
            buf.setLength(buf.length() - 2);
        }
        buf.append(")");
        return access + (access.length() > 0 ? " " : "") + type + " " + name + buf.toString();
    }

    public static final String replace(String str, String old, String new_) {
        StringBuffer buf = new StringBuffer();
        try {
            int index = str.indexOf(old);
            if (index != -1) {
                int old_index = 0;
                while ((index = str.indexOf(old, old_index)) != -1) {
                    buf.append(str.substring(old_index, index));
                    buf.append(new_);
                    old_index = index + old.length();
                }
                buf.append(str.substring(old_index));
                str = buf.toString();
            }
        }
        catch (StringIndexOutOfBoundsException e) {
            System.err.println(e);
        }
        return str;
    }

    public static final String signatureToString(String signature) {
        return Utility.signatureToString(signature, true);
    }

    public static final String signatureToString(String signature, boolean chopit) {
        ResultHolder rh = Utility.signatureToStringInternal(signature, chopit);
        return rh.getResult();
    }

    public static final ResultHolder signatureToStringInternal(String signature, boolean chopit) {
        int processedChars = 1;
        try {
            switch (signature.charAt(0)) {
                case 'B': {
                    return ResultHolder.BYTE;
                }
                case 'C': {
                    return ResultHolder.CHAR;
                }
                case 'D': {
                    return ResultHolder.DOUBLE;
                }
                case 'F': {
                    return ResultHolder.FLOAT;
                }
                case 'I': {
                    return ResultHolder.INT;
                }
                case 'J': {
                    return ResultHolder.LONG;
                }
                case 'L': {
                    int genericStart;
                    int index = signature.indexOf(59);
                    if (index < 0) {
                        throw new ClassFormatException("Invalid signature: " + signature);
                    }
                    if (signature.length() > index + 1 && signature.charAt(index + 1) == '>') {
                        index += 2;
                    }
                    if ((genericStart = signature.indexOf(60)) != -1) {
                        int genericEnd = signature.indexOf(62);
                        ResultHolder rh = Utility.signatureToStringInternal(signature.substring(genericStart + 1, genericEnd), chopit);
                        StringBuffer sb = new StringBuffer();
                        sb.append(signature.substring(1, genericStart));
                        sb.append("<").append(rh.getResult()).append(">");
                        ResultHolder retval = new ResultHolder(Utility.compactClassName(sb.toString(), chopit), genericEnd + 1);
                        return retval;
                    }
                    processedChars = index + 1;
                    ResultHolder retval = new ResultHolder(Utility.compactClassName(signature.substring(1, index), chopit), processedChars);
                    return retval;
                }
                case 'S': {
                    return ResultHolder.SHORT;
                }
                case 'Z': {
                    return ResultHolder.BOOLEAN;
                }
                case '[': {
                    StringBuffer brackets = new StringBuffer();
                    int n = 0;
                    while (signature.charAt(n) == '[') {
                        brackets.append("[]");
                        ++n;
                    }
                    int consumedChars = n;
                    ResultHolder restOfIt = Utility.signatureToStringInternal(signature.substring(n), chopit);
                    brackets.insert(0, restOfIt.getResult());
                    return new ResultHolder(brackets.toString(), consumedChars += restOfIt.getConsumedChars());
                }
                case 'V': {
                    return ResultHolder.VOID;
                }
            }
            throw new ClassFormatException("Invalid signature: `" + signature + "'");
        }
        catch (StringIndexOutOfBoundsException e) {
            throw new ClassFormatException("Invalid signature: " + e + ":" + signature);
        }
    }

    public static final byte typeOfMethodSignature(String signature) throws ClassFormatException {
        try {
            if (signature.charAt(0) != '(') {
                throw new ClassFormatException("Invalid method signature: " + signature);
            }
            int index = signature.lastIndexOf(41) + 1;
            return Utility.typeOfSignature(signature.substring(index));
        }
        catch (StringIndexOutOfBoundsException e) {
            throw new ClassFormatException("Invalid method signature: " + signature);
        }
    }

    private static final short byteToShort(byte b) {
        return b < 0 ? (short)(256 + b) : (short)b;
    }

    public static final String toHexString(byte[] bytes) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < bytes.length; ++i) {
            short b = Utility.byteToShort(bytes[i]);
            String hex = Integer.toString(b, 16);
            if (b < 16) {
                buf.append('0');
            }
            buf.append(hex);
            if (i >= bytes.length - 1) continue;
            buf.append(' ');
        }
        return buf.toString();
    }

    public static final String format(int i, int length, boolean left_justify, char fill) {
        return Utility.fillup(Integer.toString(i), length, left_justify, fill);
    }

    public static final String fillup(String str, int length, boolean left_justify, char fill) {
        int len = length - str.length();
        char[] buf = new char[len < 0 ? 0 : len];
        for (int j = 0; j < buf.length; ++j) {
            buf[j] = fill;
        }
        if (left_justify) {
            return str + new String(buf);
        }
        return new String(buf) + str;
    }

    public static final String convertString(String label) {
        char[] ch = label.toCharArray();
        StringBuffer buf = new StringBuffer();
        block7: for (int i = 0; i < ch.length; ++i) {
            switch (ch[i]) {
                case '\n': {
                    buf.append("\\n");
                    continue block7;
                }
                case '\r': {
                    buf.append("\\r");
                    continue block7;
                }
                case '\"': {
                    buf.append("\\\"");
                    continue block7;
                }
                case '\'': {
                    buf.append("\\'");
                    continue block7;
                }
                case '\\': {
                    buf.append("\\\\");
                    continue block7;
                }
                default: {
                    buf.append(ch[i]);
                }
            }
        }
        return buf.toString();
    }

    public static Collection<RuntimeAnnos> getAnnotationAttributes(ConstantPool cp, List<AnnotationGen> annotations) {
        if (annotations.size() == 0) {
            return null;
        }
        try {
            int countVisible = 0;
            int countInvisible = 0;
            for (AnnotationGen a : annotations) {
                if (a.isRuntimeVisible()) {
                    ++countVisible;
                    continue;
                }
                ++countInvisible;
            }
            ByteArrayOutputStream rvaBytes = new ByteArrayOutputStream();
            ByteArrayOutputStream riaBytes = new ByteArrayOutputStream();
            DataOutputStream rvaDos = new DataOutputStream(rvaBytes);
            DataOutputStream riaDos = new DataOutputStream(riaBytes);
            rvaDos.writeShort(countVisible);
            riaDos.writeShort(countInvisible);
            for (AnnotationGen a : annotations) {
                if (a.isRuntimeVisible()) {
                    a.dump(rvaDos);
                    continue;
                }
                a.dump(riaDos);
            }
            rvaDos.close();
            riaDos.close();
            byte[] rvaData = rvaBytes.toByteArray();
            byte[] riaData = riaBytes.toByteArray();
            int rvaIndex = -1;
            int riaIndex = -1;
            if (rvaData.length > 2) {
                rvaIndex = cp.addUtf8("RuntimeVisibleAnnotations");
            }
            if (riaData.length > 2) {
                riaIndex = cp.addUtf8("RuntimeInvisibleAnnotations");
            }
            ArrayList<RuntimeAnnos> newAttributes = new ArrayList<RuntimeAnnos>();
            if (rvaData.length > 2) {
                newAttributes.add(new RuntimeVisAnnos(rvaIndex, rvaData.length, rvaData, cp));
            }
            if (riaData.length > 2) {
                newAttributes.add(new RuntimeInvisAnnos(riaIndex, riaData.length, riaData, cp));
            }
            return newAttributes;
        }
        catch (IOException e) {
            System.err.println("IOException whilst processing annotations");
            e.printStackTrace();
            return null;
        }
    }

    public static Attribute[] getParameterAnnotationAttributes(ConstantPool cp, List<AnnotationGen>[] vec) {
        int[] visCount = new int[vec.length];
        int totalVisCount = 0;
        int[] invisCount = new int[vec.length];
        int totalInvisCount = 0;
        try {
            for (int i = 0; i < vec.length; ++i) {
                List<AnnotationGen> l = vec[i];
                if (l == null) continue;
                for (AnnotationGen element : l) {
                    if (element.isRuntimeVisible()) {
                        int n = i;
                        visCount[n] = visCount[n] + 1;
                        ++totalVisCount;
                        continue;
                    }
                    int n = i;
                    invisCount[n] = invisCount[n] + 1;
                    ++totalInvisCount;
                }
            }
            ByteArrayOutputStream rvaBytes = new ByteArrayOutputStream();
            DataOutputStream rvaDos = new DataOutputStream(rvaBytes);
            rvaDos.writeByte(vec.length);
            for (int i = 0; i < vec.length; ++i) {
                rvaDos.writeShort(visCount[i]);
                if (visCount[i] <= 0) continue;
                List<AnnotationGen> l = vec[i];
                for (AnnotationGen element : l) {
                    if (!element.isRuntimeVisible()) continue;
                    element.dump(rvaDos);
                }
            }
            rvaDos.close();
            ByteArrayOutputStream riaBytes = new ByteArrayOutputStream();
            DataOutputStream riaDos = new DataOutputStream(riaBytes);
            riaDos.writeByte(vec.length);
            for (int i = 0; i < vec.length; ++i) {
                riaDos.writeShort(invisCount[i]);
                if (invisCount[i] <= 0) continue;
                List<AnnotationGen> l = vec[i];
                for (AnnotationGen element : l) {
                    if (element.isRuntimeVisible()) continue;
                    element.dump(riaDos);
                }
            }
            riaDos.close();
            byte[] rvaData = rvaBytes.toByteArray();
            byte[] riaData = riaBytes.toByteArray();
            int rvaIndex = -1;
            int riaIndex = -1;
            if (totalVisCount > 0) {
                rvaIndex = cp.addUtf8("RuntimeVisibleParameterAnnotations");
            }
            if (totalInvisCount > 0) {
                riaIndex = cp.addUtf8("RuntimeInvisibleParameterAnnotations");
            }
            ArrayList<RuntimeParamAnnos> newAttributes = new ArrayList<RuntimeParamAnnos>();
            if (totalVisCount > 0) {
                newAttributes.add(new RuntimeVisParamAnnos(rvaIndex, rvaData.length, rvaData, cp));
            }
            if (totalInvisCount > 0) {
                newAttributes.add(new RuntimeInvisParamAnnos(riaIndex, riaData.length, riaData, cp));
            }
            return newAttributes.toArray(new Attribute[0]);
        }
        catch (IOException e) {
            System.err.println("IOException whilst processing parameter annotations");
            e.printStackTrace();
            return null;
        }
    }

    public static final byte typeOfSignature(String signature) throws ClassFormatException {
        try {
            switch (signature.charAt(0)) {
                case 'B': {
                    return 8;
                }
                case 'C': {
                    return 5;
                }
                case 'D': {
                    return 7;
                }
                case 'F': {
                    return 6;
                }
                case 'I': {
                    return 10;
                }
                case 'J': {
                    return 11;
                }
                case 'L': {
                    return 14;
                }
                case '[': {
                    return 13;
                }
                case 'V': {
                    return 12;
                }
                case 'Z': {
                    return 4;
                }
                case 'S': {
                    return 9;
                }
            }
            throw new ClassFormatException("Invalid method signature: " + signature);
        }
        catch (StringIndexOutOfBoundsException e) {
            throw new ClassFormatException("Invalid method signature: " + signature);
        }
    }

    public static final byte typeOfSignature(char c) throws ClassFormatException {
        switch (c) {
            case 'B': {
                return 8;
            }
            case 'C': {
                return 5;
            }
            case 'D': {
                return 7;
            }
            case 'F': {
                return 6;
            }
            case 'I': {
                return 10;
            }
            case 'J': {
                return 11;
            }
            case 'L': {
                return 14;
            }
            case '[': {
                return 13;
            }
            case 'V': {
                return 12;
            }
            case 'Z': {
                return 4;
            }
            case 'S': {
                return 9;
            }
        }
        throw new ClassFormatException("Invalid type of signature: " + c);
    }

    public static final String codeToString(ByteSequence bytes, ConstantPool constant_pool, boolean verbose) throws IOException {
        int i;
        short opcode = (short)bytes.readUnsignedByte();
        int default_offset = 0;
        int no_pad_bytes = 0;
        StringBuffer buf = new StringBuffer(Constants.OPCODE_NAMES[opcode]);
        if (opcode == 170 || opcode == 171) {
            int remainder = bytes.getIndex() % 4;
            no_pad_bytes = remainder == 0 ? 0 : 4 - remainder;
            for (i = 0; i < no_pad_bytes; ++i) {
                byte b = bytes.readByte();
                if (b == 0) continue;
                System.err.println("Warning: Padding byte != 0 in " + Constants.OPCODE_NAMES[opcode] + ":" + b);
            }
            default_offset = bytes.readInt();
        }
        switch (opcode) {
            case 170: {
                int i2;
                int low = bytes.readInt();
                int high = bytes.readInt();
                int offset = bytes.getIndex() - 12 - no_pad_bytes - 1;
                buf.append("\tdefault = " + (default_offset += offset) + ", low = " + low + ", high = " + high + "(");
                int[] jump_table = new int[high - low + 1];
                for (i2 = 0; i2 < jump_table.length; ++i2) {
                    jump_table[i2] = offset + bytes.readInt();
                    buf.append(jump_table[i2]);
                    if (i2 >= jump_table.length - 1) continue;
                    buf.append(", ");
                }
                buf.append(")");
                break;
            }
            case 171: {
                int i2;
                int npairs = bytes.readInt();
                int offset = bytes.getIndex() - 8 - no_pad_bytes - 1;
                int[] match = new int[npairs];
                int[] jump_table = new int[npairs];
                buf.append("\tdefault = " + (default_offset += offset) + ", npairs = " + npairs + " (");
                for (i2 = 0; i2 < npairs; ++i2) {
                    match[i2] = bytes.readInt();
                    jump_table[i2] = offset + bytes.readInt();
                    buf.append("(" + match[i2] + ", " + jump_table[i2] + ")");
                    if (i2 >= npairs - 1) continue;
                    buf.append(", ");
                }
                buf.append(")");
                break;
            }
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 166: 
            case 167: 
            case 168: 
            case 198: 
            case 199: {
                buf.append("\t\t#" + (bytes.getIndex() - 1 + bytes.readShort()));
                break;
            }
            case 200: 
            case 201: {
                buf.append("\t\t#" + (bytes.getIndex() - 1 + bytes.readInt()));
                break;
            }
            case 21: 
            case 22: 
            case 23: 
            case 24: 
            case 25: 
            case 54: 
            case 55: 
            case 56: 
            case 57: 
            case 58: 
            case 169: {
                int vindex;
                if (wide) {
                    vindex = bytes.readUnsignedShort();
                    wide = false;
                } else {
                    vindex = bytes.readUnsignedByte();
                }
                buf.append("\t\t%" + vindex);
                break;
            }
            case 196: {
                wide = true;
                buf.append("\t(wide)");
                break;
            }
            case 188: {
                buf.append("\t\t<" + Constants.TYPE_NAMES[bytes.readByte()] + ">");
                break;
            }
            case 178: 
            case 179: 
            case 180: 
            case 181: {
                int index = bytes.readUnsignedShort();
                buf.append("\t\t" + constant_pool.constantToString(index, (byte)9) + (verbose ? " (" + index + ")" : ""));
                break;
            }
            case 187: 
            case 192: {
                buf.append("\t");
            }
            case 193: {
                int index = bytes.readUnsignedShort();
                buf.append("\t<" + constant_pool.constantToString(index) + ">" + (verbose ? " (" + index + ")" : ""));
                break;
            }
            case 182: 
            case 183: 
            case 184: {
                int index = bytes.readUnsignedShort();
                buf.append("\t" + constant_pool.constantToString(index) + (verbose ? " (" + index + ")" : ""));
                break;
            }
            case 185: {
                int index = bytes.readUnsignedShort();
                int nargs = bytes.readUnsignedByte();
                buf.append("\t" + constant_pool.constantToString(index) + (verbose ? " (" + index + ")\t" : "") + nargs + "\t" + bytes.readUnsignedByte());
                break;
            }
            case 186: {
                int index = bytes.readUnsignedShort();
                bytes.readUnsignedShort();
                buf.append("\t" + constant_pool.constantToString(index) + (verbose ? " (" + index + ")" : ""));
                break;
            }
            case 19: 
            case 20: {
                int index = bytes.readUnsignedShort();
                buf.append("\t\t" + constant_pool.constantToString(index) + (verbose ? " (" + index + ")" : ""));
                break;
            }
            case 18: {
                int index = bytes.readUnsignedByte();
                buf.append("\t\t" + constant_pool.constantToString(index) + (verbose ? " (" + index + ")" : ""));
                break;
            }
            case 189: {
                int index = bytes.readUnsignedShort();
                buf.append("\t\t<" + Utility.compactClassName(constant_pool.getConstantString(index, (byte)7), false) + ">" + (verbose ? " (" + index + ")" : ""));
                break;
            }
            case 197: {
                int index = bytes.readUnsignedShort();
                int dimensions = bytes.readUnsignedByte();
                buf.append("\t<" + Utility.compactClassName(constant_pool.getConstantString(index, (byte)7), false) + ">\t" + dimensions + (verbose ? " (" + index + ")" : ""));
                break;
            }
            case 132: {
                short constant;
                int vindex;
                if (wide) {
                    vindex = bytes.readUnsignedShort();
                    constant = bytes.readShort();
                    wide = false;
                } else {
                    vindex = bytes.readUnsignedByte();
                    constant = bytes.readByte();
                }
                buf.append("\t\t%" + vindex + "\t" + constant);
                break;
            }
            default: {
                if (Constants.iLen[opcode] - 1 <= 0) break;
                block28: for (i = 0; i < Constants.TYPE_OF_OPERANDS[opcode].length; ++i) {
                    buf.append("\t\t");
                    switch (Constants.TYPE_OF_OPERANDS[opcode][i]) {
                        case 8: {
                            buf.append(bytes.readByte());
                            continue block28;
                        }
                        case 9: {
                            buf.append(bytes.readShort());
                            continue block28;
                        }
                        case 10: {
                            buf.append(bytes.readInt());
                            continue block28;
                        }
                        default: {
                            System.err.println("Unreachable default case reached!");
                            System.exit(-1);
                        }
                    }
                }
            }
        }
        return buf.toString();
    }

    private static final int pow2(int n) {
        return 1 << n;
    }

    public static String toMethodSignature(Type returnType, Type[] argTypes) {
        StringBuffer buf = new StringBuffer("(");
        int length = argTypes == null ? 0 : argTypes.length;
        for (int i = 0; i < length; ++i) {
            buf.append(argTypes[i].getSignature());
        }
        buf.append(')');
        buf.append(returnType.getSignature());
        return buf.toString();
    }

    public static class ResultHolder {
        private String result;
        private int consumed;
        public static final ResultHolder BYTE = new ResultHolder("byte", 1);
        public static final ResultHolder CHAR = new ResultHolder("char", 1);
        public static final ResultHolder DOUBLE = new ResultHolder("double", 1);
        public static final ResultHolder FLOAT = new ResultHolder("float", 1);
        public static final ResultHolder INT = new ResultHolder("int", 1);
        public static final ResultHolder LONG = new ResultHolder("long", 1);
        public static final ResultHolder SHORT = new ResultHolder("short", 1);
        public static final ResultHolder BOOLEAN = new ResultHolder("boolean", 1);
        public static final ResultHolder VOID = new ResultHolder("void", 1);

        public ResultHolder(String s, int c) {
            this.result = s;
            this.consumed = c;
        }

        public String getResult() {
            return this.result;
        }

        public int getConsumedChars() {
            return this.consumed;
        }
    }
}

