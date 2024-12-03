/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 */
package org.apache.bcel.classfile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.FilterReader;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.util.ByteSequence;
import org.apache.commons.lang3.ArrayUtils;

public abstract class Utility {
    private static final ThreadLocal<Integer> CONSUMER_CHARS;
    private static boolean wide;
    private static final int FREE_CHARS = 48;
    private static final int[] CHAR_MAP;
    private static final int[] MAP_CHAR;
    private static final char ESCAPE_CHAR = '$';

    public static String accessToString(int accessFlags) {
        return Utility.accessToString(accessFlags, false);
    }

    public static String accessToString(int accessFlags, boolean forClass) {
        StringBuilder buf = new StringBuilder();
        int p = 0;
        int i = 0;
        while (p < 32768) {
            p = Utility.pow2(i);
            if ((accessFlags & p) != 0 && (!forClass || p != 32 && p != 512)) {
                buf.append(Const.getAccessName(i)).append(" ");
            }
            ++i;
        }
        return buf.toString().trim();
    }

    private static short byteToShort(byte b) {
        return b < 0 ? (short)(256 + b) : (short)b;
    }

    public static String classOrInterface(int accessFlags) {
        return (accessFlags & 0x200) != 0 ? "interface" : "class";
    }

    public static int clearBit(int flag, int i) {
        int bit = Utility.pow2(i);
        return (flag & bit) == 0 ? flag : flag ^ bit;
    }

    public static String codeToString(byte[] code, ConstantPool constantPool, int index, int length) {
        return Utility.codeToString(code, constantPool, index, length, true);
    }

    public static String codeToString(byte[] code, ConstantPool constantPool, int index, int length, boolean verbose) {
        StringBuilder buf = new StringBuilder(code.length * 20);
        try (ByteSequence stream = new ByteSequence(code);){
            int i;
            for (i = 0; i < index; ++i) {
                Utility.codeToString(stream, constantPool, verbose);
            }
            i = 0;
            while (stream.available() > 0) {
                if (length < 0 || i < length) {
                    String indices = Utility.fillup(stream.getIndex() + ":", 6, true, ' ');
                    buf.append(indices).append(Utility.codeToString(stream, constantPool, verbose)).append('\n');
                }
                ++i;
            }
        }
        catch (IOException e) {
            throw new ClassFormatException("Byte code error: " + buf.toString(), e);
        }
        return buf.toString();
    }

    public static String codeToString(ByteSequence bytes, ConstantPool constantPool) throws IOException {
        return Utility.codeToString(bytes, constantPool, true);
    }

    public static String codeToString(ByteSequence bytes, ConstantPool constantPool, boolean verbose) throws IOException {
        short opcode = (short)bytes.readUnsignedByte();
        int defaultOffset = 0;
        int noPadBytes = 0;
        StringBuilder buf = new StringBuilder(Const.getOpcodeName(opcode));
        if (opcode == 170 || opcode == 171) {
            int remainder = bytes.getIndex() % 4;
            noPadBytes = remainder == 0 ? 0 : 4 - remainder;
            for (int i = 0; i < noPadBytes; ++i) {
                byte b = bytes.readByte();
                if (b == 0) continue;
                System.err.println("Warning: Padding byte != 0 in " + Const.getOpcodeName(opcode) + ":" + b);
            }
            defaultOffset = bytes.readInt();
        }
        switch (opcode) {
            case 170: {
                int i;
                int low = bytes.readInt();
                int high = bytes.readInt();
                int offset = bytes.getIndex() - 12 - noPadBytes - 1;
                buf.append("\tdefault = ").append(defaultOffset += offset).append(", low = ").append(low).append(", high = ").append(high).append("(");
                int[] jumpTable = new int[high - low + 1];
                for (i = 0; i < jumpTable.length; ++i) {
                    jumpTable[i] = offset + bytes.readInt();
                    buf.append(jumpTable[i]);
                    if (i >= jumpTable.length - 1) continue;
                    buf.append(", ");
                }
                buf.append(")");
                break;
            }
            case 171: {
                int i;
                int npairs = bytes.readInt();
                int offset = bytes.getIndex() - 8 - noPadBytes - 1;
                int[] match = new int[npairs];
                int[] jumpTable = new int[npairs];
                buf.append("\tdefault = ").append(defaultOffset += offset).append(", npairs = ").append(npairs).append(" (");
                for (i = 0; i < npairs; ++i) {
                    match[i] = bytes.readInt();
                    jumpTable[i] = offset + bytes.readInt();
                    buf.append("(").append(match[i]).append(", ").append(jumpTable[i]).append(")");
                    if (i >= npairs - 1) continue;
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
                buf.append("\t\t#").append(bytes.getIndex() - 1 + bytes.readShort());
                break;
            }
            case 200: 
            case 201: {
                buf.append("\t\t#").append(bytes.getIndex() - 1 + bytes.readInt());
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
                buf.append("\t\t%").append(vindex);
                break;
            }
            case 196: {
                wide = true;
                buf.append("\t(wide)");
                break;
            }
            case 188: {
                buf.append("\t\t<").append(Const.getTypeName(bytes.readByte())).append(">");
                break;
            }
            case 178: 
            case 179: 
            case 180: 
            case 181: {
                int index = bytes.readUnsignedShort();
                buf.append("\t\t").append(constantPool.constantToString(index, (byte)9)).append(verbose ? " (" + index + ")" : "");
                break;
            }
            case 187: 
            case 192: {
                buf.append("\t");
            }
            case 193: {
                int index = bytes.readUnsignedShort();
                buf.append("\t<").append(constantPool.constantToString(index, (byte)7)).append(">").append(verbose ? " (" + index + ")" : "");
                break;
            }
            case 183: 
            case 184: {
                int index = bytes.readUnsignedShort();
                Object c = constantPool.getConstant(index);
                buf.append("\t").append(constantPool.constantToString(index, ((Constant)c).getTag())).append(verbose ? " (" + index + ")" : "");
                break;
            }
            case 182: {
                int index = bytes.readUnsignedShort();
                buf.append("\t").append(constantPool.constantToString(index, (byte)10)).append(verbose ? " (" + index + ")" : "");
                break;
            }
            case 185: {
                int index = bytes.readUnsignedShort();
                int nargs = bytes.readUnsignedByte();
                buf.append("\t").append(constantPool.constantToString(index, (byte)11)).append(verbose ? " (" + index + ")\t" : "").append(nargs).append("\t").append(bytes.readUnsignedByte());
                break;
            }
            case 186: {
                int index = bytes.readUnsignedShort();
                buf.append("\t").append(constantPool.constantToString(index, (byte)18)).append(verbose ? " (" + index + ")\t" : "").append(bytes.readUnsignedByte()).append(bytes.readUnsignedByte());
                break;
            }
            case 19: 
            case 20: {
                int index = bytes.readUnsignedShort();
                buf.append("\t\t").append(constantPool.constantToString(index, ((Constant)constantPool.getConstant(index)).getTag())).append(verbose ? " (" + index + ")" : "");
                break;
            }
            case 18: {
                int index = bytes.readUnsignedByte();
                buf.append("\t\t").append(constantPool.constantToString(index, ((Constant)constantPool.getConstant(index)).getTag())).append(verbose ? " (" + index + ")" : "");
                break;
            }
            case 189: {
                int index = bytes.readUnsignedShort();
                buf.append("\t\t<").append(Utility.compactClassName(constantPool.getConstantString(index, (byte)7), false)).append(">").append(verbose ? " (" + index + ")" : "");
                break;
            }
            case 197: {
                int index = bytes.readUnsignedShort();
                int dimensions = bytes.readUnsignedByte();
                buf.append("\t<").append(Utility.compactClassName(constantPool.getConstantString(index, (byte)7), false)).append(">\t").append(dimensions).append(verbose ? " (" + index + ")" : "");
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
                buf.append("\t\t%").append(vindex).append("\t").append(constant);
                break;
            }
            default: {
                if (Const.getNoOfOperands(opcode) <= 0) break;
                int i = 0;
                while ((long)i < Const.getOperandTypeCount(opcode)) {
                    buf.append("\t\t");
                    switch (Const.getOperandType(opcode, i)) {
                        case 8: {
                            buf.append(bytes.readByte());
                            break;
                        }
                        case 9: {
                            buf.append(bytes.readShort());
                            break;
                        }
                        case 10: {
                            buf.append(bytes.readInt());
                            break;
                        }
                        default: {
                            throw new IllegalStateException("Unreachable default case reached!");
                        }
                    }
                    ++i;
                }
                break block0;
            }
        }
        return buf.toString();
    }

    public static String compactClassName(String str) {
        return Utility.compactClassName(str, true);
    }

    public static String compactClassName(String str, boolean chopit) {
        return Utility.compactClassName(str, "java.lang.", chopit);
    }

    public static String compactClassName(String str, String prefix, boolean chopit) {
        int len = prefix.length();
        str = Utility.pathToPackage(str);
        if (chopit && str.startsWith(prefix) && str.substring(len).indexOf(46) == -1) {
            str = str.substring(len);
        }
        return str;
    }

    public static String convertString(String label) {
        char[] ch = label.toCharArray();
        StringBuilder buf = new StringBuilder();
        block7: for (char element : ch) {
            switch (element) {
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
                    buf.append(element);
                }
            }
        }
        return buf.toString();
    }

    private static int countBrackets(String brackets) {
        char[] chars = brackets.toCharArray();
        int count = 0;
        boolean open = false;
        block4: for (char c : chars) {
            switch (c) {
                case '[': {
                    if (open) {
                        throw new IllegalArgumentException("Illegally nested brackets:" + brackets);
                    }
                    open = true;
                    continue block4;
                }
                case ']': {
                    if (!open) {
                        throw new IllegalArgumentException("Illegally nested brackets:" + brackets);
                    }
                    open = false;
                    ++count;
                    continue block4;
                }
            }
        }
        if (open) {
            throw new IllegalArgumentException("Illegally nested brackets:" + brackets);
        }
        return count;
    }

    public static byte[] decode(String s, boolean uncompress) throws IOException {
        byte[] bytes;
        try (JavaReader jr = new JavaReader(new CharArrayReader(s.toCharArray()));
             ByteArrayOutputStream bos = new ByteArrayOutputStream();){
            int ch;
            while ((ch = jr.read()) >= 0) {
                bos.write(ch);
            }
            bytes = bos.toByteArray();
        }
        if (uncompress) {
            int b;
            GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
            byte[] tmp = new byte[bytes.length * 3];
            int count = 0;
            while ((b = gis.read()) >= 0) {
                tmp[count++] = (byte)b;
            }
            bytes = Arrays.copyOf(tmp, count);
        }
        return bytes;
    }

    public static String encode(byte[] bytes, boolean compress) throws IOException {
        if (compress) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();){
                GZIPOutputStream gos = new GZIPOutputStream(baos);
                Object object = null;
                try {
                    gos.write(bytes, 0, bytes.length);
                    gos.close();
                    bytes = baos.toByteArray();
                }
                catch (Throwable throwable) {
                    object = throwable;
                    throw throwable;
                }
                finally {
                    if (gos != null) {
                        if (object != null) {
                            try {
                                gos.close();
                            }
                            catch (Throwable throwable) {
                                ((Throwable)object).addSuppressed(throwable);
                            }
                        } else {
                            gos.close();
                        }
                    }
                }
            }
        }
        CharArrayWriter caw = new CharArrayWriter();
        try (JavaWriter jw = new JavaWriter(caw);){
            for (byte b : bytes) {
                int in = b & 0xFF;
                jw.write(in);
            }
        }
        return caw.toString();
    }

    public static String fillup(String str, int length, boolean leftJustify, char fill) {
        int len = length - str.length();
        char[] buf = new char[Math.max(len, 0)];
        Arrays.fill(buf, fill);
        if (leftJustify) {
            return str + new String(buf);
        }
        return new String(buf) + str;
    }

    public static String format(int i, int length, boolean leftJustify, char fill) {
        return Utility.fillup(Integer.toString(i), length, leftJustify, fill);
    }

    public static String getSignature(String type) {
        StringBuilder buf = new StringBuilder();
        char[] chars = type.toCharArray();
        boolean charFound = false;
        boolean delim = false;
        int index = -1;
        block4: for (int i = 0; i < chars.length; ++i) {
            switch (chars[i]) {
                case '\t': 
                case '\n': 
                case '\f': 
                case '\r': 
                case ' ': {
                    if (!charFound) continue block4;
                    delim = true;
                    continue block4;
                }
                case '[': {
                    if (!charFound) {
                        throw new IllegalArgumentException("Illegal type: " + type);
                    }
                    index = i;
                    break block4;
                }
                default: {
                    charFound = true;
                    if (delim) continue block4;
                    buf.append(chars[i]);
                }
            }
        }
        int brackets = 0;
        if (index > 0) {
            brackets = Utility.countBrackets(type.substring(index));
        }
        type = buf.toString();
        buf.setLength(0);
        for (int i = 0; i < brackets; ++i) {
            buf.append('[');
        }
        boolean found = false;
        for (int i = 4; i <= 12 && !found; ++i) {
            if (!Const.getTypeName(i).equals(type)) continue;
            found = true;
            buf.append(Const.getShortTypeName(i));
        }
        if (!found) {
            buf.append('L').append(Utility.packageToPath(type)).append(';');
        }
        return buf.toString();
    }

    public static boolean isJavaIdentifierPart(char ch) {
        return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0' && ch <= '9' || ch == '_';
    }

    public static boolean isSet(int flag, int i) {
        return (flag & Utility.pow2(i)) != 0;
    }

    public static String[] methodSignatureArgumentTypes(String signature) throws ClassFormatException {
        return Utility.methodSignatureArgumentTypes(signature, true);
    }

    public static String[] methodSignatureArgumentTypes(String signature, boolean chopit) throws ClassFormatException {
        ArrayList<String> vec = new ArrayList<String>();
        try {
            int index = signature.indexOf(40) + 1;
            if (index <= 0) {
                throw new ClassFormatException("Invalid method signature: " + signature);
            }
            while (signature.charAt(index) != ')') {
                vec.add(Utility.typeSignatureToString(signature.substring(index), chopit));
                index += Utility.unwrap(CONSUMER_CHARS);
            }
        }
        catch (StringIndexOutOfBoundsException e) {
            throw new ClassFormatException("Invalid method signature: " + signature, e);
        }
        return vec.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public static String methodSignatureReturnType(String signature) throws ClassFormatException {
        return Utility.methodSignatureReturnType(signature, true);
    }

    public static String methodSignatureReturnType(String signature, boolean chopit) throws ClassFormatException {
        String type;
        try {
            int index = signature.lastIndexOf(41) + 1;
            if (index <= 0) {
                throw new ClassFormatException("Invalid method signature: " + signature);
            }
            type = Utility.typeSignatureToString(signature.substring(index), chopit);
        }
        catch (StringIndexOutOfBoundsException e) {
            throw new ClassFormatException("Invalid method signature: " + signature, e);
        }
        return type;
    }

    public static String methodSignatureToString(String signature, String name, String access) {
        return Utility.methodSignatureToString(signature, name, access, true);
    }

    public static String methodSignatureToString(String signature, String name, String access, boolean chopit) {
        return Utility.methodSignatureToString(signature, name, access, chopit, null);
    }

    public static String methodSignatureToString(String signature, String name, String access, boolean chopit, LocalVariableTable vars) throws ClassFormatException {
        String type;
        StringBuilder buf = new StringBuilder("(");
        int varIndex = access.contains("static") ? 0 : 1;
        try {
            int index = signature.indexOf(40) + 1;
            if (index <= 0) {
                throw new ClassFormatException("Invalid method signature: " + signature);
            }
            while (signature.charAt(index) != ')') {
                String paramType = Utility.typeSignatureToString(signature.substring(index), chopit);
                buf.append(paramType);
                if (vars != null) {
                    LocalVariable l = vars.getLocalVariable(varIndex, 0);
                    if (l != null) {
                        buf.append(" ").append(l.getName());
                    }
                } else {
                    buf.append(" arg").append(varIndex);
                }
                varIndex = "double".equals(paramType) || "long".equals(paramType) ? (varIndex += 2) : ++varIndex;
                buf.append(", ");
                index += Utility.unwrap(CONSUMER_CHARS);
            }
            type = Utility.typeSignatureToString(signature.substring(++index), chopit);
        }
        catch (StringIndexOutOfBoundsException e) {
            throw new ClassFormatException("Invalid method signature: " + signature, e);
        }
        if (buf.length() > 1) {
            buf.setLength(buf.length() - 2);
        }
        buf.append(")");
        return access + (!access.isEmpty() ? " " : "") + type + " " + name + buf.toString();
    }

    public static String methodTypeToSignature(String ret, String[] argv) throws ClassFormatException {
        String str;
        StringBuilder buf = new StringBuilder("(");
        if (argv != null) {
            for (String element : argv) {
                str = Utility.getSignature(element);
                if (str.endsWith("V")) {
                    throw new ClassFormatException("Invalid type: " + element);
                }
                buf.append(str);
            }
        }
        str = Utility.getSignature(ret);
        buf.append(")").append(str);
        return buf.toString();
    }

    public static String packageToPath(String name) {
        return name.replace('.', '/');
    }

    public static String pathToPackage(String str) {
        return str.replace('/', '.');
    }

    private static int pow2(int n) {
        return 1 << n;
    }

    public static String printArray(Object[] obj) {
        return Utility.printArray(obj, true);
    }

    public static String printArray(Object[] obj, boolean braces) {
        return Utility.printArray(obj, braces, false);
    }

    public static String printArray(Object[] obj, boolean braces, boolean quote) {
        if (obj == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        if (braces) {
            buf.append('{');
        }
        for (int i = 0; i < obj.length; ++i) {
            if (obj[i] != null) {
                buf.append(quote ? "\"" : "").append(obj[i]).append(quote ? "\"" : "");
            } else {
                buf.append("null");
            }
            if (i >= obj.length - 1) continue;
            buf.append(", ");
        }
        if (braces) {
            buf.append('}');
        }
        return buf.toString();
    }

    public static void printArray(PrintStream out, Object[] obj) {
        out.println(Utility.printArray(obj, true));
    }

    public static void printArray(PrintWriter out, Object[] obj) {
        out.println(Utility.printArray(obj, true));
    }

    public static String replace(String str, String old, String new_) {
        try {
            if (str.contains(old)) {
                int index;
                StringBuilder buf = new StringBuilder();
                int oldIndex = 0;
                while ((index = str.indexOf(old, oldIndex)) != -1) {
                    buf.append(str, oldIndex, index);
                    buf.append(new_);
                    oldIndex = index + old.length();
                }
                buf.append(str.substring(oldIndex));
                str = buf.toString();
            }
        }
        catch (StringIndexOutOfBoundsException e) {
            System.err.println(e);
        }
        return str;
    }

    public static short searchOpcode(String name) {
        name = name.toLowerCase(Locale.ENGLISH);
        for (short i = 0; i < Const.OPCODE_NAMES_LENGTH; i = (short)(i + 1)) {
            if (!Const.getOpcodeName(i).equals(name)) continue;
            return i;
        }
        return -1;
    }

    public static int setBit(int flag, int i) {
        return flag | Utility.pow2(i);
    }

    public static String signatureToString(String signature) {
        return Utility.signatureToString(signature, true);
    }

    public static String signatureToString(String signature, boolean chopit) {
        String type = "";
        String typeParams = "";
        int index = 0;
        if (signature.charAt(0) == '<') {
            typeParams = Utility.typeParamTypesToString(signature, chopit);
            index += Utility.unwrap(CONSUMER_CHARS);
        }
        if (signature.charAt(index) == '(') {
            type = typeParams + Utility.typeSignaturesToString(signature.substring(index), chopit, ')');
            type = type + Utility.typeSignatureToString(signature.substring(index += Utility.unwrap(CONSUMER_CHARS)), chopit);
            index += Utility.unwrap(CONSUMER_CHARS);
            return type;
        }
        type = Utility.typeSignatureToString(signature.substring(index), chopit);
        if (typeParams.isEmpty() && (index += Utility.unwrap(CONSUMER_CHARS)) == signature.length()) {
            return type;
        }
        StringBuilder typeClass = new StringBuilder(typeParams);
        typeClass.append(" extends ");
        typeClass.append(type);
        if (index < signature.length()) {
            typeClass.append(" implements ");
            typeClass.append(Utility.typeSignatureToString(signature.substring(index), chopit));
            index += Utility.unwrap(CONSUMER_CHARS);
        }
        while (index < signature.length()) {
            typeClass.append(", ");
            typeClass.append(Utility.typeSignatureToString(signature.substring(index), chopit));
            index += Utility.unwrap(CONSUMER_CHARS);
        }
        return typeClass.toString();
    }

    public static String toHexString(byte[] bytes) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            short b = Utility.byteToShort(bytes[i]);
            String hex = Integer.toHexString(b);
            if (b < 16) {
                buf.append('0');
            }
            buf.append(hex);
            if (i >= bytes.length - 1) continue;
            buf.append(' ');
        }
        return buf.toString();
    }

    public static byte typeOfMethodSignature(String signature) throws ClassFormatException {
        try {
            if (signature.charAt(0) != '(') {
                throw new ClassFormatException("Invalid method signature: " + signature);
            }
            int index = signature.lastIndexOf(41) + 1;
            return Utility.typeOfSignature(signature.substring(index));
        }
        catch (StringIndexOutOfBoundsException e) {
            throw new ClassFormatException("Invalid method signature: " + signature, e);
        }
    }

    public static byte typeOfSignature(String signature) throws ClassFormatException {
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
                case 'L': 
                case 'T': {
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
                case '!': 
                case '*': 
                case '+': {
                    return Utility.typeOfSignature(signature.substring(1));
                }
            }
            throw new ClassFormatException("Invalid method signature: " + signature);
        }
        catch (StringIndexOutOfBoundsException e) {
            throw new ClassFormatException("Invalid method signature: " + signature, e);
        }
    }

    private static String typeParamTypesToString(String signature, boolean chopit) {
        StringBuilder typeParams = new StringBuilder("<");
        int index = 1;
        typeParams.append(Utility.typeParamTypeToString(signature.substring(index), chopit));
        index += Utility.unwrap(CONSUMER_CHARS);
        while (signature.charAt(index) != '>') {
            typeParams.append(", ");
            typeParams.append(Utility.typeParamTypeToString(signature.substring(index), chopit));
            index += Utility.unwrap(CONSUMER_CHARS);
        }
        Utility.wrap(CONSUMER_CHARS, index + 1);
        return typeParams.append(">").toString();
    }

    private static String typeParamTypeToString(String signature, boolean chopit) {
        int index = signature.indexOf(58);
        if (index <= 0) {
            throw new ClassFormatException("Invalid type parameter signature: " + signature);
        }
        StringBuilder typeParam = new StringBuilder(signature.substring(0, index));
        if (signature.charAt(++index) != ':') {
            typeParam.append(" extends ");
            typeParam.append(Utility.typeSignatureToString(signature.substring(index), chopit));
            index += Utility.unwrap(CONSUMER_CHARS);
        }
        while (signature.charAt(index) == ':') {
            typeParam.append(" & ");
            typeParam.append(Utility.typeSignatureToString(signature.substring(++index), chopit));
            index += Utility.unwrap(CONSUMER_CHARS);
        }
        Utility.wrap(CONSUMER_CHARS, index);
        return typeParam.toString();
    }

    private static String typeSignaturesToString(String signature, boolean chopit, char term) {
        StringBuilder typeList = new StringBuilder(signature.substring(0, 1));
        int index = 1;
        if (signature.charAt(index) != term) {
            typeList.append(Utility.typeSignatureToString(signature.substring(index), chopit));
            index += Utility.unwrap(CONSUMER_CHARS);
        }
        while (signature.charAt(index) != term) {
            typeList.append(", ");
            typeList.append(Utility.typeSignatureToString(signature.substring(index), chopit));
            index += Utility.unwrap(CONSUMER_CHARS);
        }
        Utility.wrap(CONSUMER_CHARS, index + 1);
        return typeList.append(term).toString();
    }

    public static String typeSignatureToString(String signature, boolean chopit) throws ClassFormatException {
        Utility.wrap(CONSUMER_CHARS, 1);
        try {
            switch (signature.charAt(0)) {
                case 'B': {
                    return "byte";
                }
                case 'C': {
                    return "char";
                }
                case 'D': {
                    return "double";
                }
                case 'F': {
                    return "float";
                }
                case 'I': {
                    return "int";
                }
                case 'J': {
                    return "long";
                }
                case 'T': {
                    int index = signature.indexOf(59);
                    if (index < 0) {
                        throw new ClassFormatException("Invalid type variable signature: " + signature);
                    }
                    Utility.wrap(CONSUMER_CHARS, index + 1);
                    return Utility.compactClassName(signature.substring(1, index), chopit);
                }
                case 'L': {
                    int fromIndex = signature.indexOf(60);
                    if (fromIndex < 0) {
                        fromIndex = 0;
                    } else if ((fromIndex = signature.indexOf(62, fromIndex)) < 0) {
                        throw new ClassFormatException("Invalid signature: " + signature);
                    }
                    int index = signature.indexOf(59, fromIndex);
                    if (index < 0) {
                        throw new ClassFormatException("Invalid signature: " + signature);
                    }
                    int bracketIndex = signature.substring(0, index).indexOf(60);
                    if (bracketIndex < 0) {
                        Utility.wrap(CONSUMER_CHARS, index + 1);
                        return Utility.compactClassName(signature.substring(1, index), chopit);
                    }
                    fromIndex = signature.indexOf(59);
                    if (fromIndex < 0) {
                        throw new ClassFormatException("Invalid signature: " + signature);
                    }
                    if (fromIndex < bracketIndex) {
                        Utility.wrap(CONSUMER_CHARS, fromIndex + 1);
                        return Utility.compactClassName(signature.substring(1, fromIndex), chopit);
                    }
                    StringBuilder type = new StringBuilder(Utility.compactClassName(signature.substring(1, bracketIndex), chopit)).append("<");
                    int consumedChars = bracketIndex + 1;
                    if (signature.charAt(consumedChars) == '+') {
                        type.append("? extends ");
                        ++consumedChars;
                    } else if (signature.charAt(consumedChars) == '-') {
                        type.append("? super ");
                        ++consumedChars;
                    }
                    if (signature.charAt(consumedChars) == '*') {
                        type.append("?");
                        ++consumedChars;
                    } else {
                        type.append(Utility.typeSignatureToString(signature.substring(consumedChars), chopit));
                        consumedChars = Utility.unwrap(CONSUMER_CHARS) + consumedChars;
                        Utility.wrap(CONSUMER_CHARS, consumedChars);
                    }
                    while (signature.charAt(consumedChars) != '>') {
                        type.append(", ");
                        if (signature.charAt(consumedChars) == '+') {
                            type.append("? extends ");
                            ++consumedChars;
                        } else if (signature.charAt(consumedChars) == '-') {
                            type.append("? super ");
                            ++consumedChars;
                        }
                        if (signature.charAt(consumedChars) == '*') {
                            type.append("?");
                            ++consumedChars;
                            continue;
                        }
                        type.append(Utility.typeSignatureToString(signature.substring(consumedChars), chopit));
                        consumedChars = Utility.unwrap(CONSUMER_CHARS) + consumedChars;
                        Utility.wrap(CONSUMER_CHARS, consumedChars);
                    }
                    type.append(">");
                    if (signature.charAt(++consumedChars) == '.') {
                        type.append(".");
                        type.append(Utility.typeSignatureToString("L" + signature.substring(consumedChars + 1), chopit));
                        consumedChars = Utility.unwrap(CONSUMER_CHARS) + consumedChars;
                        Utility.wrap(CONSUMER_CHARS, consumedChars);
                        return type.toString();
                    }
                    if (signature.charAt(consumedChars) != ';') {
                        throw new ClassFormatException("Invalid signature: " + signature);
                    }
                    Utility.wrap(CONSUMER_CHARS, consumedChars + 1);
                    return type.toString();
                }
                case 'S': {
                    return "short";
                }
                case 'Z': {
                    return "boolean";
                }
                case '[': {
                    StringBuilder brackets = new StringBuilder();
                    int n = 0;
                    while (signature.charAt(n) == '[') {
                        brackets.append("[]");
                        ++n;
                    }
                    int consumedChars = n;
                    String type = Utility.typeSignatureToString(signature.substring(n), chopit);
                    int temp = Utility.unwrap(CONSUMER_CHARS) + consumedChars;
                    Utility.wrap(CONSUMER_CHARS, temp);
                    return type + brackets.toString();
                }
                case 'V': {
                    return "void";
                }
            }
            throw new ClassFormatException("Invalid signature: '" + signature + "'");
        }
        catch (StringIndexOutOfBoundsException e) {
            throw new ClassFormatException("Invalid signature: " + signature, e);
        }
    }

    private static int unwrap(ThreadLocal<Integer> tl) {
        return tl.get();
    }

    private static void wrap(ThreadLocal<Integer> tl, int value) {
        tl.set(value);
    }

    static {
        int i;
        CONSUMER_CHARS = ThreadLocal.withInitial(() -> 0);
        CHAR_MAP = new int[48];
        MAP_CHAR = new int[256];
        int j = 0;
        for (i = 65; i <= 90; ++i) {
            Utility.CHAR_MAP[j] = i;
            Utility.MAP_CHAR[i] = j++;
        }
        for (i = 103; i <= 122; ++i) {
            Utility.CHAR_MAP[j] = i;
            Utility.MAP_CHAR[i] = j++;
        }
        Utility.CHAR_MAP[j] = 36;
        Utility.MAP_CHAR[36] = j++;
        Utility.CHAR_MAP[j] = 95;
        Utility.MAP_CHAR[95] = j;
    }

    private static class JavaWriter
    extends FilterWriter {
        public JavaWriter(Writer out) {
            super(out);
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            for (int i = 0; i < len; ++i) {
                this.write(cbuf[off + i]);
            }
        }

        @Override
        public void write(int b) throws IOException {
            if (Utility.isJavaIdentifierPart((char)b) && b != 36) {
                this.out.write(b);
            } else {
                this.out.write(36);
                if (b >= 0 && b < 48) {
                    this.out.write(CHAR_MAP[b]);
                } else {
                    char[] tmp = Integer.toHexString(b).toCharArray();
                    if (tmp.length == 1) {
                        this.out.write(48);
                        this.out.write(tmp[0]);
                    } else {
                        this.out.write(tmp[0]);
                        this.out.write(tmp[1]);
                    }
                }
            }
        }

        @Override
        public void write(String str, int off, int len) throws IOException {
            this.write(str.toCharArray(), off, len);
        }
    }

    private static class JavaReader
    extends FilterReader {
        public JavaReader(Reader in) {
            super(in);
        }

        @Override
        public int read() throws IOException {
            int b = this.in.read();
            if (b != 36) {
                return b;
            }
            int i = this.in.read();
            if (i < 0) {
                return -1;
            }
            if (i >= 48 && i <= 57 || i >= 97 && i <= 102) {
                int j = this.in.read();
                if (j < 0) {
                    return -1;
                }
                char[] tmp = new char[]{(char)i, (char)j};
                return Integer.parseInt(new String(tmp), 16);
            }
            return MAP_CHAR[i];
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            for (int i = 0; i < len; ++i) {
                cbuf[off + i] = (char)this.read();
            }
            return len;
        }
    }
}

