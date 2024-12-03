/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import javax.tools.FileObject;

public final class Util {
    public static String LINE_SEPARATOR = System.getProperty("line.separator");

    public static char[] getInputStreamAsCharArray(InputStream stream, int length, String encoding) throws IOException {
        Charset charset = null;
        try {
            charset = Charset.forName(encoding);
        }
        catch (IllegalCharsetNameException illegalCharsetNameException) {
            System.err.println("Illegal charset name : " + encoding);
            return null;
        }
        catch (UnsupportedCharsetException unsupportedCharsetException) {
            System.err.println("Unsupported charset : " + encoding);
            return null;
        }
        CharsetDecoder charsetDecoder = charset.newDecoder();
        charsetDecoder.onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
        byte[] contents = org.eclipse.jdt.internal.compiler.util.Util.getInputStreamAsByteArray(stream, length);
        ByteBuffer byteBuffer = ByteBuffer.allocate(contents.length);
        byteBuffer.put(contents);
        byteBuffer.flip();
        return charsetDecoder.decode(byteBuffer).array();
    }

    public static CharSequence getCharContents(FileObject fileObject, boolean ignoreEncodingErrors, byte[] contents, String encoding) throws IOException {
        if (contents == null) {
            return null;
        }
        Charset charset = null;
        try {
            charset = Charset.forName(encoding);
        }
        catch (IllegalCharsetNameException illegalCharsetNameException) {
            System.err.println("Illegal charset name : " + encoding);
            return null;
        }
        catch (UnsupportedCharsetException unsupportedCharsetException) {
            System.err.println("Unsupported charset : " + encoding);
            return null;
        }
        CharsetDecoder charsetDecoder = charset.newDecoder();
        ByteBuffer byteBuffer = ByteBuffer.allocate(contents.length);
        byteBuffer.put(contents);
        byteBuffer.flip();
        if (ignoreEncodingErrors) {
            charsetDecoder.onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
            return charsetDecoder.decode(byteBuffer);
        }
        charsetDecoder.onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
        CharBuffer out = CharBuffer.allocate(contents.length);
        CoderResult result = null;
        String replacement = charsetDecoder.replacement();
        int replacementLength = replacement.length();
        EncodingErrorCollector collector = null;
        while (true) {
            CharBuffer temp;
            if ((result = charsetDecoder.decode(byteBuffer, out, true)).isMalformed() || result.isUnmappable()) {
                if (collector == null) {
                    collector = new EncodingErrorCollector(fileObject, encoding);
                }
                Util.reportEncodingError(collector, out.position(), result.length());
                if (out.position() + replacementLength >= out.capacity()) {
                    temp = CharBuffer.allocate(out.capacity() * 2);
                    out.flip();
                    temp.put(out);
                    out = temp;
                }
                out.append(replacement);
                byteBuffer.position(byteBuffer.position() + result.length());
                continue;
            }
            if (!result.isOverflow()) break;
            temp = CharBuffer.allocate(out.capacity() * 2);
            out.flip();
            temp.put(out);
            out = temp;
        }
        out.flip();
        if (collector != null) {
            collector.reportAllEncodingErrors(out.toString());
        }
        return out;
    }

    private static void reportEncodingError(EncodingErrorCollector collector, int position, int length) {
        collector.collect(position, -length);
    }

    public static class EncodingError {
        int position;
        int length;

        public EncodingError(int position, int length) {
            this.position = position;
            this.length = length;
        }

        public String getSource(char[] unitSource) {
            char c;
            int startPosition = this.position;
            int endPosition = this.position + this.length - 1;
            if (startPosition > endPosition || startPosition < 0 && endPosition < 0 || unitSource.length == 0) {
                return "No source available";
            }
            StringBuffer errorBuffer = new StringBuffer();
            errorBuffer.append('\t');
            int sourceLength = unitSource.length;
            int begin = startPosition >= sourceLength ? sourceLength - 1 : startPosition;
            while (begin > 0) {
                c = unitSource[begin - 1];
                if (c == '\n' || c == '\r') break;
                --begin;
            }
            int end = endPosition >= sourceLength ? sourceLength - 1 : endPosition;
            while (end + 1 < sourceLength) {
                c = unitSource[end + 1];
                if (c == '\r' || c == '\n') break;
                ++end;
            }
            while ((c = unitSource[begin]) == ' ' || c == '\t') {
                ++begin;
            }
            errorBuffer.append(unitSource, begin, end - begin + 1);
            errorBuffer.append(LINE_SEPARATOR).append("\t");
            int i = begin;
            while (i < startPosition) {
                errorBuffer.append(unitSource[i] == '\t' ? (char)'\t' : ' ');
                ++i;
            }
            i = startPosition;
            while (i <= (endPosition >= sourceLength ? sourceLength - 1 : endPosition)) {
                errorBuffer.append('^');
                ++i;
            }
            return errorBuffer.toString();
        }
    }

    public static class EncodingErrorCollector {
        ArrayList<EncodingError> encodingErrors = new ArrayList();
        FileObject fileObject;
        String encoding;

        public EncodingErrorCollector(FileObject fileObject, String encoding) {
            this.fileObject = fileObject;
            this.encoding = encoding;
        }

        public void collect(int position, int length) {
            this.encodingErrors.add(new EncodingError(position, length));
        }

        public void reportAllEncodingErrors(String string) {
            char[] unitSource = string.toCharArray();
            for (EncodingError error : this.encodingErrors) {
                System.err.println(String.valueOf(this.fileObject.getName()) + " Unmappable character for encoding " + this.encoding);
                System.err.println(error.getSource(unitSource));
            }
        }
    }
}

