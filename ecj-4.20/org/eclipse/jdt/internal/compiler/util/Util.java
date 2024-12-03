/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.batch.Main;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
import org.eclipse.jdt.internal.compiler.util.Messages;
import org.eclipse.jdt.internal.compiler.util.SuffixConstants;

public class Util
implements SuffixConstants {
    public static final char C_BOOLEAN = 'Z';
    public static final char C_BYTE = 'B';
    public static final char C_CHAR = 'C';
    public static final char C_DOUBLE = 'D';
    public static final char C_FLOAT = 'F';
    public static final char C_INT = 'I';
    public static final char C_SEMICOLON = ';';
    public static final char C_COLON = ':';
    public static final char C_LONG = 'J';
    public static final char C_SHORT = 'S';
    public static final char C_VOID = 'V';
    public static final char C_TYPE_VARIABLE = 'T';
    public static final char C_STAR = '*';
    public static final char C_EXCEPTION_START = '^';
    public static final char C_EXTENDS = '+';
    public static final char C_SUPER = '-';
    public static final char C_DOT = '.';
    public static final char C_DOLLAR = '$';
    public static final char C_ARRAY = '[';
    public static final char C_RESOLVED = 'L';
    public static final char C_UNRESOLVED = 'Q';
    public static final char C_NAME_END = ';';
    public static final char C_PARAM_START = '(';
    public static final char C_PARAM_END = ')';
    public static final char C_GENERIC_START = '<';
    public static final char C_GENERIC_END = '>';
    public static final char C_CAPTURE = '!';
    private static final int DEFAULT_READING_SIZE = 8192;
    private static final int DEFAULT_WRITING_SIZE = 1024;
    public static final String UTF_8 = "UTF-8";
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String EMPTY_STRING = new String(CharOperation.NO_CHAR);
    public static final String COMMA_SEPARATOR = new String(CharOperation.COMMA_SEPARATOR);
    public static final int[] EMPTY_INT_ARRAY = new int[0];
    public static final int ZIP_FILE = 0;
    public static final int JMOD_FILE = 1;

    public static String buildAllDirectoriesInto(String outputPath, String relativeFileName) throws IOException {
        String fileName;
        String outputDirPath;
        char fileSeparatorChar = File.separatorChar;
        String fileSeparator = File.separator;
        outputPath = outputPath.replace('/', fileSeparatorChar);
        int separatorIndex = (relativeFileName = relativeFileName.replace('/', fileSeparatorChar)).lastIndexOf(fileSeparatorChar);
        if (separatorIndex == -1) {
            if (outputPath.endsWith(fileSeparator)) {
                outputDirPath = outputPath.substring(0, outputPath.length() - 1);
                fileName = String.valueOf(outputPath) + relativeFileName;
            } else {
                outputDirPath = outputPath;
                fileName = String.valueOf(outputPath) + fileSeparator + relativeFileName;
            }
        } else if (outputPath.endsWith(fileSeparator)) {
            outputDirPath = String.valueOf(outputPath) + relativeFileName.substring(0, separatorIndex);
            fileName = String.valueOf(outputPath) + relativeFileName;
        } else {
            outputDirPath = String.valueOf(outputPath) + fileSeparator + relativeFileName.substring(0, separatorIndex);
            fileName = String.valueOf(outputPath) + fileSeparator + relativeFileName;
        }
        File f = new File(outputDirPath);
        f.mkdirs();
        if (f.isDirectory()) {
            return fileName;
        }
        if (outputPath.endsWith(fileSeparator)) {
            outputPath = outputPath.substring(0, outputPath.length() - 1);
        }
        f = new File(outputPath);
        boolean checkFileType = false;
        if (f.exists()) {
            checkFileType = true;
        } else if (!f.mkdirs()) {
            if (f.exists()) {
                checkFileType = true;
            } else {
                throw new IOException(Messages.bind(Messages.output_notValidAll, f.getAbsolutePath()));
            }
        }
        if (checkFileType && !f.isDirectory()) {
            throw new IOException(Messages.bind(Messages.output_isFile, f.getAbsolutePath()));
        }
        StringBuffer outDir = new StringBuffer(outputPath);
        outDir.append(fileSeparator);
        StringTokenizer tokenizer = new StringTokenizer(relativeFileName, fileSeparator);
        String token = tokenizer.nextToken();
        while (tokenizer.hasMoreTokens()) {
            f = new File(outDir.append(token).append(fileSeparator).toString());
            checkFileType = false;
            if (f.exists()) {
                checkFileType = true;
            } else if (!f.mkdir()) {
                if (f.exists()) {
                    checkFileType = true;
                } else {
                    throw new IOException(Messages.bind(Messages.output_notValid, outDir.substring(outputPath.length() + 1, outDir.length() - 1), outputPath));
                }
            }
            if (checkFileType && !f.isDirectory()) {
                throw new IOException(Messages.bind(Messages.output_isFile, f.getAbsolutePath()));
            }
            token = tokenizer.nextToken();
        }
        return outDir.append(token).toString();
    }

    public static char[] bytesToChar(byte[] bytes, String encoding) throws IOException {
        return Util.getInputStreamAsCharArray(new ByteArrayInputStream(bytes), bytes.length, encoding);
    }

    public static int computeOuterMostVisibility(TypeDeclaration typeDeclaration, int visibility) {
        while (typeDeclaration != null) {
            switch (typeDeclaration.modifiers & 7) {
                case 2: {
                    visibility = 2;
                    break;
                }
                case 0: {
                    if (visibility == 2) break;
                    visibility = 0;
                    break;
                }
                case 4: {
                    if (visibility != 1) break;
                    visibility = 4;
                }
            }
            typeDeclaration = typeDeclaration.enclosingType;
        }
        return visibility;
    }

    public static byte[] getFileByteContent(File file) throws IOException {
        byte[] byArray;
        block7: {
            InputStream stream = null;
            try {
                stream = new BufferedInputStream(new FileInputStream(file));
                byArray = Util.getInputStreamAsByteArray(stream, (int)file.length());
                if (stream == null) break block7;
            }
            catch (Throwable throwable) {
                if (stream != null) {
                    try {
                        stream.close();
                    }
                    catch (IOException iOException) {}
                }
                throw throwable;
            }
            try {
                stream.close();
            }
            catch (IOException iOException) {}
        }
        return byArray;
    }

    public static char[] getFileCharContent(File file, String encoding) throws IOException {
        char[] cArray;
        block7: {
            FileInputStream stream = null;
            try {
                stream = new FileInputStream(file);
                cArray = Util.getInputStreamAsCharArray(stream, (int)file.length(), encoding);
                if (stream == null) break block7;
            }
            catch (Throwable throwable) {
                if (stream != null) {
                    try {
                        ((InputStream)stream).close();
                    }
                    catch (IOException iOException) {}
                }
                throw throwable;
            }
            try {
                ((InputStream)stream).close();
            }
            catch (IOException iOException) {}
        }
        return cArray;
    }

    private static FileOutputStream getFileOutputStream(boolean generatePackagesStructure, String outputPath, String relativeFileName) throws IOException {
        if (generatePackagesStructure) {
            return new FileOutputStream(new File(Util.buildAllDirectoriesInto(outputPath, relativeFileName)));
        }
        String fileName = null;
        char fileSeparatorChar = File.separatorChar;
        String fileSeparator = File.separator;
        outputPath = outputPath.replace('/', fileSeparatorChar);
        int indexOfPackageSeparator = relativeFileName.lastIndexOf(fileSeparatorChar);
        if (indexOfPackageSeparator == -1) {
            fileName = outputPath.endsWith(fileSeparator) ? String.valueOf(outputPath) + relativeFileName : String.valueOf(outputPath) + fileSeparator + relativeFileName;
        } else {
            int length = relativeFileName.length();
            fileName = outputPath.endsWith(fileSeparator) ? String.valueOf(outputPath) + relativeFileName.substring(indexOfPackageSeparator + 1, length) : String.valueOf(outputPath) + fileSeparator + relativeFileName.substring(indexOfPackageSeparator + 1, length);
        }
        return new FileOutputStream(new File(fileName));
    }

    public static byte[] getInputStreamAsByteArray(InputStream stream, int length) throws IOException {
        byte[] contents;
        if (length == -1) {
            contents = new byte[]{};
            int contentsLength = 0;
            int amountRead = -1;
            do {
                int amountRequested;
                if (contentsLength + (amountRequested = Math.max(stream.available(), 8192)) > contents.length) {
                    byte[] byArray = contents;
                    contents = new byte[contentsLength + amountRequested];
                    System.arraycopy(byArray, 0, contents, 0, contentsLength);
                }
                if ((amountRead = stream.read(contents, contentsLength, amountRequested)) <= 0) continue;
                contentsLength += amountRead;
            } while (amountRead != -1);
            if (contentsLength < contents.length) {
                byte[] byArray = contents;
                contents = new byte[contentsLength];
                System.arraycopy(byArray, 0, contents, 0, contentsLength);
            }
        } else {
            contents = new byte[length];
            int len = 0;
            int readSize = 0;
            while (readSize != -1 && len != length) {
                readSize = stream.read(contents, len += readSize, length - len);
            }
        }
        return contents;
    }

    public static char[] getInputStreamAsCharArray(InputStream stream, int length, String encoding) throws IOException {
        BufferedReader reader = null;
        try {
            reader = encoding == null ? new BufferedReader(new InputStreamReader(stream)) : new BufferedReader(new InputStreamReader(stream, encoding));
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            reader = new BufferedReader(new InputStreamReader(stream));
        }
        int totalRead = 0;
        char[] contents = length == -1 ? CharOperation.NO_CHAR : new char[length];
        while (true) {
            int amountRequested;
            if (totalRead < length) {
                amountRequested = length - totalRead;
            } else {
                int current = reader.read();
                if (current < 0) break;
                amountRequested = Math.max(stream.available(), 8192);
                if (totalRead + 1 + amountRequested > contents.length) {
                    char[] cArray = contents;
                    contents = new char[totalRead + 1 + amountRequested];
                    System.arraycopy(cArray, 0, contents, 0, totalRead);
                }
                contents[totalRead++] = (char)current;
            }
            int amountRead = reader.read(contents, totalRead, amountRequested);
            if (amountRead < 0) break;
            totalRead += amountRead;
        }
        int start = 0;
        if (totalRead > 0 && UTF_8.equals(encoding) && contents[0] == '\ufeff') {
            --totalRead;
            start = 1;
        }
        if (totalRead < contents.length) {
            char[] cArray = contents;
            contents = new char[totalRead];
            System.arraycopy(cArray, start, contents, 0, totalRead);
        }
        return contents;
    }

    public static String getExceptionSummary(Throwable exception) {
        StringWriter stringWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stringWriter));
        StringBuffer buffer = stringWriter.getBuffer();
        StringBuffer exceptionBuffer = new StringBuffer(50);
        exceptionBuffer.append(exception.toString());
        int i = 0;
        int lineSep = 0;
        int max = buffer.length();
        int line2Start = 0;
        block4: while (i < max) {
            switch (buffer.charAt(i)) {
                case '\n': 
                case '\r': {
                    if (line2Start > 0) {
                        exceptionBuffer.append(' ').append(buffer.substring(line2Start, i));
                        break block4;
                    }
                    ++lineSep;
                    break;
                }
                case '\t': 
                case ' ': {
                    break;
                }
                default: {
                    if (lineSep <= 0) break;
                    line2Start = i;
                    lineSep = 0;
                }
            }
            ++i;
        }
        return exceptionBuffer.toString();
    }

    public static int getLineNumber(int position, int[] lineEnds, int g, int d) {
        if (lineEnds == null) {
            return 1;
        }
        if (d == -1) {
            return 1;
        }
        int m = g;
        while (g <= d) {
            m = g + (d - g) / 2;
            int start = lineEnds[m];
            if (position < start) {
                d = m - 1;
                continue;
            }
            if (position > start) {
                g = m + 1;
                continue;
            }
            return m + 1;
        }
        if (position < lineEnds[m]) {
            return m + 1;
        }
        return m + 2;
    }

    public static byte[] getZipEntryByteContent(ZipEntry ze, ZipFile zip) throws IOException {
        byte[] byArray;
        block8: {
            InputStream stream = null;
            try {
                InputStream inputStream = zip.getInputStream(ze);
                if (inputStream == null) {
                    throw new IOException("Invalid zip entry name : " + ze.getName());
                }
                stream = new BufferedInputStream(inputStream);
                byArray = Util.getInputStreamAsByteArray(stream, (int)ze.getSize());
                if (stream == null) break block8;
            }
            catch (Throwable throwable) {
                if (stream != null) {
                    try {
                        stream.close();
                    }
                    catch (IOException iOException) {}
                }
                throw throwable;
            }
            try {
                stream.close();
            }
            catch (IOException iOException) {}
        }
        return byArray;
    }

    public static int hashCode(Object[] array) {
        int prime = 31;
        if (array == null) {
            return 0;
        }
        int result = 1;
        int index = 0;
        while (index < array.length) {
            result = prime * result + (array[index] == null ? 0 : array[index].hashCode());
            ++index;
        }
        return result;
    }

    public static final boolean isPotentialZipArchive(String name) {
        int i;
        int lastDot = name.lastIndexOf(46);
        if (lastDot == -1) {
            return false;
        }
        if (name.lastIndexOf(File.separatorChar) > lastDot) {
            return false;
        }
        int length = name.length();
        int extensionLength = length - lastDot - 1;
        if (extensionLength == "java".length()) {
            i = extensionLength - 1;
            while (i >= 0) {
                if (Character.toLowerCase(name.charAt(length - extensionLength + i)) != "java".charAt(i)) break;
                if (i == 0) {
                    return false;
                }
                --i;
            }
        }
        if (extensionLength == "class".length()) {
            i = extensionLength - 1;
            while (i >= 0) {
                if (Character.toLowerCase(name.charAt(length - extensionLength + i)) != "class".charAt(i)) {
                    return true;
                }
                --i;
            }
            return false;
        }
        return true;
    }

    public static final int archiveFormat(String name) {
        int i;
        int lastDot = name.lastIndexOf(46);
        if (lastDot == -1) {
            return -1;
        }
        if (name.lastIndexOf(File.separatorChar) > lastDot) {
            return -1;
        }
        int length = name.length();
        int extensionLength = length - lastDot - 1;
        if (extensionLength == "java".length()) {
            i = extensionLength - 1;
            while (i >= 0) {
                if (Character.toLowerCase(name.charAt(length - extensionLength + i)) != "java".charAt(i)) break;
                if (i == 0) {
                    return -1;
                }
                --i;
            }
        }
        if (extensionLength == "class".length()) {
            i = extensionLength - 1;
            while (i >= 0) {
                if (Character.toLowerCase(name.charAt(length - extensionLength + i)) != "class".charAt(i)) {
                    return 0;
                }
                --i;
            }
            return -1;
        }
        if (extensionLength == "jmod".length()) {
            i = extensionLength - 1;
            while (i >= 0) {
                if (Character.toLowerCase(name.charAt(length - extensionLength + i)) != "jmod".charAt(i)) {
                    return 0;
                }
                --i;
            }
            return 1;
        }
        return 0;
    }

    public static final boolean isClassFileName(char[] name) {
        int suffixLength;
        int nameLength = name == null ? 0 : name.length;
        if (nameLength < (suffixLength = SUFFIX_CLASS.length)) {
            return false;
        }
        int i = 0;
        int offset = nameLength - suffixLength;
        while (i < suffixLength) {
            char c = name[offset + i];
            if (c != SUFFIX_class[i] && c != SUFFIX_CLASS[i]) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public static final boolean isClassFileName(String name) {
        int suffixLength;
        int nameLength = name == null ? 0 : name.length();
        if (nameLength < (suffixLength = SUFFIX_CLASS.length)) {
            return false;
        }
        int i = 0;
        while (i < suffixLength) {
            int suffixIndex;
            char c = name.charAt(nameLength - i - 1);
            if (c != SUFFIX_class[suffixIndex = suffixLength - i - 1] && c != SUFFIX_CLASS[suffixIndex]) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public static final boolean isExcluded(char[] path, char[][] inclusionPatterns, char[][] exclusionPatterns, boolean isFolderPath) {
        int length;
        int i;
        block9: {
            if (inclusionPatterns == null && exclusionPatterns == null) {
                return false;
            }
            if (inclusionPatterns != null) {
                i = 0;
                length = inclusionPatterns.length;
                while (i < length) {
                    int star;
                    int lastSlash;
                    char[] pattern;
                    char[] folderPattern = pattern = inclusionPatterns[i];
                    if (isFolderPath && (lastSlash = CharOperation.lastIndexOf('/', pattern)) != -1 && lastSlash != pattern.length - 1 && ((star = CharOperation.indexOf('*', pattern, lastSlash)) == -1 || star >= pattern.length - 1 || pattern[star + 1] != '*')) {
                        folderPattern = CharOperation.subarray(pattern, 0, lastSlash);
                    }
                    if (!CharOperation.pathMatch(folderPattern, path, true, '/')) {
                        ++i;
                        continue;
                    }
                    break block9;
                }
                return true;
            }
        }
        if (isFolderPath) {
            path = CharOperation.concat(path, new char[]{'*'}, '/');
        }
        if (exclusionPatterns != null) {
            i = 0;
            length = exclusionPatterns.length;
            while (i < length) {
                if (CharOperation.pathMatch(exclusionPatterns[i], path, true, '/')) {
                    return true;
                }
                ++i;
            }
        }
        return false;
    }

    public static final boolean isJavaFileName(char[] name) {
        int suffixLength;
        int nameLength = name == null ? 0 : name.length;
        if (nameLength < (suffixLength = SUFFIX_JAVA.length)) {
            return false;
        }
        int i = 0;
        int offset = nameLength - suffixLength;
        while (i < suffixLength) {
            char c = name[offset + i];
            if (c != SUFFIX_java[i] && c != SUFFIX_JAVA[i]) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public static final boolean isJavaFileName(String name) {
        int suffixLength;
        int nameLength = name == null ? 0 : name.length();
        if (nameLength < (suffixLength = SUFFIX_JAVA.length)) {
            return false;
        }
        int i = 0;
        while (i < suffixLength) {
            int suffixIndex;
            char c = name.charAt(nameLength - i - 1);
            if (c != SUFFIX_java[suffixIndex = suffixLength - i - 1] && c != SUFFIX_JAVA[suffixIndex]) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public static final boolean isJrt(String name) {
        return name.endsWith("jrt-fs.jar");
    }

    public static void reverseQuickSort(char[][] list, int left, int right) {
        int original_left = left;
        int original_right = right;
        char[] mid = list[left + (right - left) / 2];
        while (true) {
            if (CharOperation.compareTo(list[left], mid) > 0) {
                ++left;
                continue;
            }
            while (CharOperation.compareTo(mid, list[right]) > 0) {
                --right;
            }
            if (left <= right) {
                char[] tmp = list[left];
                list[left] = list[right];
                list[right] = tmp;
                ++left;
                --right;
            }
            if (left > right) break;
        }
        if (original_left < right) {
            Util.reverseQuickSort(list, original_left, right);
        }
        if (left < original_right) {
            Util.reverseQuickSort(list, left, original_right);
        }
    }

    public static void reverseQuickSort(char[][] list, int left, int right, int[] result) {
        int original_left = left;
        int original_right = right;
        char[] mid = list[left + (right - left) / 2];
        while (true) {
            if (CharOperation.compareTo(list[left], mid) > 0) {
                ++left;
                continue;
            }
            while (CharOperation.compareTo(mid, list[right]) > 0) {
                --right;
            }
            if (left <= right) {
                char[] tmp = list[left];
                list[left] = list[right];
                list[right] = tmp;
                int temp = result[left];
                result[left] = result[right];
                result[right] = temp;
                ++left;
                --right;
            }
            if (left > right) break;
        }
        if (original_left < right) {
            Util.reverseQuickSort(list, original_left, right, result);
        }
        if (left < original_right) {
            Util.reverseQuickSort(list, left, original_right, result);
        }
    }

    public static final int searchColumnNumber(int[] startLineIndexes, int lineNumber, int position) {
        switch (lineNumber) {
            case 1: {
                return position + 1;
            }
            case 2: {
                return position - startLineIndexes[0];
            }
        }
        int line = lineNumber - 2;
        int length = startLineIndexes.length;
        if (line >= length) {
            return position - startLineIndexes[length - 1];
        }
        return position - startLineIndexes[line];
    }

    public static Boolean toBoolean(boolean bool) {
        if (bool) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public static String toString(Object[] objects) {
        return Util.toString(objects, new Displayable(){

            @Override
            public String displayString(Object o) {
                if (o == null) {
                    return "null";
                }
                return o.toString();
            }
        });
    }

    public static String toString(Object[] objects, Displayable renderer) {
        if (objects == null) {
            return "";
        }
        StringBuffer buffer = new StringBuffer(10);
        int i = 0;
        while (i < objects.length) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append(renderer.displayString(objects[i]));
            ++i;
        }
        return buffer.toString();
    }

    public static void writeToDisk(boolean generatePackagesStructure, String outputPath, String relativeFileName, ClassFile classFile) throws IOException {
        FileOutputStream file = Util.getFileOutputStream(generatePackagesStructure, outputPath, relativeFileName);
        try (BufferedOutputStream output = new BufferedOutputStream(file, 1024);){
            output.write(classFile.header, 0, classFile.headerOffset);
            output.write(classFile.contents, 0, classFile.contentsOffset);
            output.flush();
        }
    }

    public static void recordNestedType(ClassFile classFile, TypeBinding typeBinding) {
        if (classFile.visitedTypes == null) {
            classFile.visitedTypes = new HashSet<TypeBinding>(3);
        } else if (classFile.visitedTypes.contains(typeBinding)) {
            return;
        }
        classFile.visitedTypes.add(typeBinding);
        if (typeBinding.isParameterizedType() && (typeBinding.tagBits & 0x800L) != 0L) {
            TypeBinding[] arguments;
            ParameterizedTypeBinding parameterizedTypeBinding = (ParameterizedTypeBinding)typeBinding;
            ReferenceBinding genericType = parameterizedTypeBinding.genericType();
            if ((genericType.tagBits & 0x800L) != 0L) {
                Util.recordNestedType(classFile, genericType);
            }
            if ((arguments = parameterizedTypeBinding.arguments) != null) {
                int j = 0;
                int max2 = arguments.length;
                while (j < max2) {
                    TypeBinding argument = arguments[j];
                    if (argument.isWildcard()) {
                        ReferenceBinding[] superInterfaces;
                        ReferenceBinding superclass;
                        WildcardBinding wildcardBinding = (WildcardBinding)argument;
                        TypeBinding bound = wildcardBinding.bound;
                        if (bound != null && (bound.tagBits & 0x800L) != 0L) {
                            Util.recordNestedType(classFile, bound);
                        }
                        if ((superclass = wildcardBinding.superclass()) != null && (superclass.tagBits & 0x800L) != 0L) {
                            Util.recordNestedType(classFile, superclass);
                        }
                        if ((superInterfaces = wildcardBinding.superInterfaces()) != null) {
                            int k = 0;
                            int max3 = superInterfaces.length;
                            while (k < max3) {
                                ReferenceBinding superInterface = superInterfaces[k];
                                if ((superInterface.tagBits & 0x800L) != 0L) {
                                    Util.recordNestedType(classFile, superInterface);
                                }
                                ++k;
                            }
                        }
                    } else if ((argument.tagBits & 0x800L) != 0L) {
                        Util.recordNestedType(classFile, argument);
                    }
                    ++j;
                }
            }
        } else if (typeBinding.isTypeVariable() && (typeBinding.tagBits & 0x800L) != 0L) {
            TypeBinding[] upperBounds;
            TypeVariableBinding typeVariableBinding = (TypeVariableBinding)typeBinding;
            TypeBinding upperBound = typeVariableBinding.upperBound();
            if (upperBound != null && (upperBound.tagBits & 0x800L) != 0L) {
                Util.recordNestedType(classFile, upperBound);
            }
            if ((upperBounds = typeVariableBinding.otherUpperBounds()) != null) {
                int k = 0;
                int max3 = upperBounds.length;
                while (k < max3) {
                    TypeBinding otherUpperBound = upperBounds[k];
                    if ((otherUpperBound.tagBits & 0x800L) != 0L) {
                        Util.recordNestedType(classFile, otherUpperBound);
                    }
                    ++k;
                }
            }
        } else if (typeBinding.isNestedType()) {
            TypeBinding enclosingType = typeBinding;
            while (enclosingType.canBeSeenBy(classFile.referenceBinding.scope) && (enclosingType = enclosingType.enclosingType()) != null) {
            }
            boolean onBottomForBug445231 = enclosingType != null;
            classFile.recordInnerClasses(typeBinding, onBottomForBug445231);
        }
    }

    public static File getJavaHome() {
        File javaHomeFile;
        String javaHome = System.getProperty("java.home");
        if (javaHome != null && (javaHomeFile = new File(javaHome)).exists()) {
            return javaHomeFile;
        }
        return null;
    }

    public static void collectVMBootclasspath(List<FileSystem.Classpath> bootclasspaths, File javaHome) {
        List<FileSystem.Classpath> classpaths = Util.collectPlatformLibraries(javaHome);
        bootclasspaths.addAll(classpaths);
    }

    public static void collectRunningVMBootclasspath(List<FileSystem.Classpath> bootclasspaths) {
        Util.collectVMBootclasspath(bootclasspaths, null);
    }

    public static long getJDKLevel(File javaHome) {
        String version = System.getProperty("java.version");
        return CompilerOptions.versionToJdkLevel(version);
    }

    public static List<FileSystem.Classpath> collectFilesNames() {
        return Util.collectPlatformLibraries(null);
    }

    public static List<FileSystem.Classpath> collectPlatformLibraries(File javaHome) {
        String bootclasspathProperty;
        String javaversion = null;
        javaversion = System.getProperty("java.version");
        if (javaversion != null && javaversion.equalsIgnoreCase("1.1.8")) {
            throw new IllegalStateException();
        }
        long jdkLevel = CompilerOptions.versionToJdkLevel(javaversion);
        if (jdkLevel >= 0x350000L) {
            ArrayList<FileSystem.Classpath> filePaths = new ArrayList<FileSystem.Classpath>();
            if (javaHome == null) {
                javaHome = Util.getJavaHome();
            }
            if (javaHome != null) {
                filePaths.add(FileSystem.getJrtClasspath(javaHome.getAbsolutePath(), null, null, null));
                return filePaths;
            }
        }
        if (!((bootclasspathProperty = System.getProperty("sun.boot.class.path")) != null && bootclasspathProperty.length() != 0 || (bootclasspathProperty = System.getProperty("vm.boot.class.path")) != null && bootclasspathProperty.length() != 0)) {
            bootclasspathProperty = System.getProperty("org.apache.harmony.boot.class.path");
        }
        HashSet<String> filePaths = new HashSet<String>();
        if (bootclasspathProperty != null && bootclasspathProperty.length() != 0) {
            StringTokenizer tokenizer = new StringTokenizer(bootclasspathProperty, File.pathSeparator);
            while (tokenizer.hasMoreTokens()) {
                filePaths.add(tokenizer.nextToken());
            }
        } else {
            if (javaHome == null) {
                javaHome = Util.getJavaHome();
            }
            if (javaHome != null) {
                File[] directoriesToCheck = null;
                directoriesToCheck = System.getProperty("os.name").startsWith("Mac") ? new File[]{new File(javaHome, "../Classes")} : new File[]{new File(javaHome, "lib")};
                File[][] systemLibrariesJars = Main.getLibrariesFiles(directoriesToCheck);
                if (systemLibrariesJars != null) {
                    int i = 0;
                    int max = systemLibrariesJars.length;
                    while (i < max) {
                        File[] current = systemLibrariesJars[i];
                        if (current != null) {
                            int j = 0;
                            int max2 = current.length;
                            while (j < max2) {
                                filePaths.add(current[j].getAbsolutePath());
                                ++j;
                            }
                        }
                        ++i;
                    }
                }
            }
        }
        ArrayList<FileSystem.Classpath> classpaths = new ArrayList<FileSystem.Classpath>();
        for (String filePath : filePaths) {
            FileSystem.Classpath currentClasspath = FileSystem.getClasspath(filePath, null, null, null, null);
            if (currentClasspath == null) continue;
            classpaths.add(currentClasspath);
        }
        return classpaths;
    }

    public static int getParameterCount(char[] methodSignature) {
        try {
            int count = 0;
            int i = CharOperation.indexOf('(', methodSignature);
            if (i < 0) {
                throw new IllegalArgumentException(String.valueOf(methodSignature));
            }
            ++i;
            while (true) {
                if (methodSignature[i] == ')') {
                    return count;
                }
                int e = Util.scanTypeSignature(methodSignature, i);
                if (e < 0) {
                    throw new IllegalArgumentException(String.valueOf(methodSignature));
                }
                i = e + 1;
                ++count;
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(String.valueOf(methodSignature), e);
        }
    }

    public static int scanTypeSignature(char[] string, int start) {
        if (start >= string.length) {
            throw Util.newIllegalArgumentException(string, start);
        }
        char c = string[start];
        switch (c) {
            case '[': {
                return Util.scanArrayTypeSignature(string, start);
            }
            case 'L': 
            case 'Q': {
                return Util.scanClassTypeSignature(string, start);
            }
            case 'T': {
                return Util.scanTypeVariableSignature(string, start);
            }
            case 'B': 
            case 'C': 
            case 'D': 
            case 'F': 
            case 'I': 
            case 'J': 
            case 'S': 
            case 'V': 
            case 'Z': {
                return Util.scanBaseTypeSignature(string, start);
            }
            case '!': {
                return Util.scanCaptureTypeSignature(string, start);
            }
            case '*': 
            case '+': 
            case '-': {
                return Util.scanTypeBoundSignature(string, start);
            }
        }
        throw Util.newIllegalArgumentException(string, start);
    }

    public static int scanBaseTypeSignature(char[] string, int start) {
        if (start >= string.length) {
            throw Util.newIllegalArgumentException(string, start);
        }
        char c = string[start];
        if ("BCDFIJSVZ".indexOf(c) >= 0) {
            return start;
        }
        throw Util.newIllegalArgumentException(string, start);
    }

    public static int scanArrayTypeSignature(char[] string, int start) {
        int length = string.length;
        if (start >= length - 1) {
            throw Util.newIllegalArgumentException(string, start);
        }
        char c = string[start];
        if (c != '[') {
            throw Util.newIllegalArgumentException(string, start);
        }
        c = string[++start];
        while (c == '[') {
            if (start >= length - 1) {
                throw Util.newIllegalArgumentException(string, start);
            }
            c = string[++start];
        }
        return Util.scanTypeSignature(string, start);
    }

    public static int scanCaptureTypeSignature(char[] string, int start) {
        if (start >= string.length - 1) {
            throw Util.newIllegalArgumentException(string, start);
        }
        char c = string[start];
        if (c != '!') {
            throw Util.newIllegalArgumentException(string, start);
        }
        return Util.scanTypeBoundSignature(string, start + 1);
    }

    public static int scanTypeVariableSignature(char[] string, int start) {
        if (start >= string.length - 2) {
            throw Util.newIllegalArgumentException(string, start);
        }
        char c = string[start];
        if (c != 'T') {
            throw Util.newIllegalArgumentException(string, start);
        }
        int id = Util.scanIdentifier(string, start + 1);
        c = string[id + 1];
        if (c == ';') {
            return id + 1;
        }
        throw Util.newIllegalArgumentException(string, start);
    }

    public static int scanIdentifier(char[] string, int start) {
        if (start >= string.length) {
            throw Util.newIllegalArgumentException(string, start);
        }
        int p = start;
        do {
            char c;
            if ((c = string[p]) != '<' && c != '>' && c != ':' && c != ';' && c != '.' && c != '/') continue;
            return p - 1;
        } while (++p != string.length);
        return p - 1;
    }

    public static int scanClassTypeSignature(char[] string, int start) {
        if (start >= string.length - 2) {
            throw Util.newIllegalArgumentException(string, start);
        }
        char c = string[start];
        if (c != 'L' && c != 'Q') {
            return -1;
        }
        int p = start + 1;
        while (true) {
            if (p >= string.length) {
                throw Util.newIllegalArgumentException(string, start);
            }
            c = string[p];
            if (c == ';') {
                return p;
            }
            if (c == '<') {
                int e;
                p = e = Util.scanTypeArgumentSignatures(string, p);
            } else if (c == '.' || c == '/') {
                int id;
                p = id = Util.scanIdentifier(string, p + 1);
            }
            ++p;
        }
    }

    public static int scanTypeBoundSignature(char[] string, int start) {
        if (start >= string.length) {
            throw Util.newIllegalArgumentException(string, start);
        }
        char c = string[start];
        switch (c) {
            case '*': {
                return start;
            }
            case '+': 
            case '-': {
                break;
            }
            default: {
                throw Util.newIllegalArgumentException(string, start);
            }
        }
        c = string[++start];
        if (c != '*' && start >= string.length - 1) {
            throw new IllegalArgumentException();
        }
        switch (c) {
            case '!': {
                return Util.scanCaptureTypeSignature(string, start);
            }
            case '+': 
            case '-': {
                return Util.scanTypeBoundSignature(string, start);
            }
            case 'L': 
            case 'Q': {
                return Util.scanClassTypeSignature(string, start);
            }
            case 'T': {
                return Util.scanTypeVariableSignature(string, start);
            }
            case '[': {
                return Util.scanArrayTypeSignature(string, start);
            }
            case '*': {
                return start;
            }
        }
        throw Util.newIllegalArgumentException(string, start);
    }

    public static int scanTypeArgumentSignatures(char[] string, int start) {
        if (start >= string.length - 1) {
            throw Util.newIllegalArgumentException(string, start);
        }
        char c = string[start];
        if (c != '<') {
            throw Util.newIllegalArgumentException(string, start);
        }
        int p = start + 1;
        while (true) {
            if (p >= string.length) {
                throw Util.newIllegalArgumentException(string, start);
            }
            c = string[p];
            if (c == '>') {
                return p;
            }
            int e = Util.scanTypeArgumentSignature(string, p);
            p = e + 1;
        }
    }

    public static int scanTypeArgumentSignature(char[] string, int start) {
        if (start >= string.length) {
            throw Util.newIllegalArgumentException(string, start);
        }
        char c = string[start];
        switch (c) {
            case '*': {
                return start;
            }
            case '+': 
            case '-': {
                return Util.scanTypeBoundSignature(string, start);
            }
        }
        return Util.scanTypeSignature(string, start);
    }

    public static boolean effectivelyEqual(Object[] one, Object[] two) {
        int twoLength;
        if (one == two) {
            return true;
        }
        int oneLength = one == null ? 0 : one.length;
        int n = twoLength = two == null ? 0 : two.length;
        if (oneLength != twoLength) {
            return false;
        }
        if (oneLength == 0) {
            return true;
        }
        int i = 0;
        while (i < one.length) {
            if (one[i] != two[i]) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public static void appendEscapedChar(StringBuffer buffer, char c, boolean stringLiteral) {
        switch (c) {
            case '\b': {
                buffer.append("\\b");
                break;
            }
            case '\t': {
                buffer.append("\\t");
                break;
            }
            case '\n': {
                buffer.append("\\n");
                break;
            }
            case '\f': {
                buffer.append("\\f");
                break;
            }
            case '\r': {
                buffer.append("\\r");
                break;
            }
            case '\"': {
                if (stringLiteral) {
                    buffer.append("\\\"");
                    break;
                }
                buffer.append(c);
                break;
            }
            case '\'': {
                if (stringLiteral) {
                    buffer.append(c);
                    break;
                }
                buffer.append("\\'");
                break;
            }
            case '\\': {
                buffer.append("\\\\");
                break;
            }
            default: {
                if (c >= ' ') {
                    buffer.append(c);
                    break;
                }
                if (c >= '\u0010') {
                    buffer.append("\\u00").append(Integer.toHexString(c));
                    break;
                }
                if (c >= '\u0000') {
                    buffer.append("\\u000").append(Integer.toHexString(c));
                    break;
                }
                buffer.append(c);
            }
        }
    }

    private static IllegalArgumentException newIllegalArgumentException(char[] string, int start) {
        return new IllegalArgumentException("\"" + String.valueOf(string) + "\" at " + start);
    }

    public static interface Displayable {
        public String displayString(Object var1);
    }
}

