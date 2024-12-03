/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.io.GroovyPrintWriter;
import groovy.lang.Closure;
import groovy.lang.StringWriterIOException;
import groovy.lang.Writable;
import groovy.transform.stc.ClosureParams;
import groovy.transform.stc.FirstParam;
import groovy.transform.stc.FromString;
import groovy.transform.stc.SimpleType;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport;
import org.codehaus.groovy.runtime.FlushingStreamWriter;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.IteratorClosureAdapter;
import org.codehaus.groovy.runtime.callsite.BooleanClosureWrapper;

public class IOGroovyMethods
extends DefaultGroovyMethodsSupport {
    private static final Logger LOG = Logger.getLogger(IOGroovyMethods.class.getName());
    private static int charBufferSize = 4096;
    private static int expectedLineLength = 160;
    private static int EOF = -1;

    public static Writer leftShift(Writer self, Object value) throws IOException {
        InvokerHelper.write(self, value);
        return self;
    }

    public static Appendable leftShift(Appendable self, Object value) throws IOException {
        InvokerHelper.append(self, value);
        return self;
    }

    public static Appendable withFormatter(Appendable self, @ClosureParams(value=SimpleType.class, options={"java.util.Formatter"}) Closure closure) {
        Formatter formatter = new Formatter(self);
        IOGroovyMethods.callWithFormatter(closure, formatter);
        return self;
    }

    public static Appendable withFormatter(Appendable self, Locale locale, @ClosureParams(value=SimpleType.class, options={"java.util.Formatter"}) Closure closure) {
        Formatter formatter = new Formatter(self, locale);
        IOGroovyMethods.callWithFormatter(closure, formatter);
        return self;
    }

    private static void callWithFormatter(Closure closure, Formatter formatter) {
        try {
            closure.call((Object)formatter);
        }
        finally {
            formatter.flush();
            formatter.close();
        }
    }

    public static void write(Writer self, Writable writable) throws IOException {
        writable.writeTo(self);
    }

    public static Writer leftShift(OutputStream self, Object value) throws IOException {
        FlushingStreamWriter writer = new FlushingStreamWriter(self);
        IOGroovyMethods.leftShift(writer, value);
        return writer;
    }

    public static void leftShift(ObjectOutputStream self, Object value) throws IOException {
        self.writeObject(value);
    }

    public static OutputStream leftShift(OutputStream self, InputStream in) throws IOException {
        int count;
        byte[] buf = new byte[1024];
        while ((count = in.read(buf, 0, buf.length)) != -1) {
            if (count == 0) {
                Thread.yield();
                continue;
            }
            self.write(buf, 0, count);
        }
        self.flush();
        return self;
    }

    public static OutputStream leftShift(OutputStream self, byte[] value) throws IOException {
        self.write(value);
        self.flush();
        return self;
    }

    public static ObjectOutputStream newObjectOutputStream(OutputStream outputStream) throws IOException {
        return new ObjectOutputStream(outputStream);
    }

    public static <T> T withObjectOutputStream(OutputStream outputStream, @ClosureParams(value=SimpleType.class, options={"java.io.ObjectOutputStream"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withStream(IOGroovyMethods.newObjectOutputStream(outputStream), closure);
    }

    public static ObjectInputStream newObjectInputStream(InputStream inputStream) throws IOException {
        return new ObjectInputStream(inputStream);
    }

    public static ObjectInputStream newObjectInputStream(InputStream inputStream, final ClassLoader classLoader) throws IOException {
        return new ObjectInputStream(inputStream){

            @Override
            protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                return Class.forName(desc.getName(), true, classLoader);
            }
        };
    }

    public static void eachObject(ObjectInputStream ois, Closure closure) throws IOException, ClassNotFoundException {
        try {
            try {
                while (true) {
                    Object obj = ois.readObject();
                    closure.call(obj);
                }
            }
            catch (EOFException e) {
                ObjectInputStream temp = ois;
                ois = null;
                ((InputStream)temp).close();
                IOGroovyMethods.closeWithWarning(ois);
            }
        }
        catch (Throwable throwable) {
            IOGroovyMethods.closeWithWarning(ois);
            throw throwable;
        }
    }

    public static <T> T withObjectInputStream(InputStream inputStream, @ClosureParams(value=SimpleType.class, options={"java.io.ObjectInputStream"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withStream(IOGroovyMethods.newObjectInputStream(inputStream), closure);
    }

    public static <T> T withObjectInputStream(InputStream inputStream, ClassLoader classLoader, @ClosureParams(value=SimpleType.class, options={"java.io.ObjectInputStream"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withStream(IOGroovyMethods.newObjectInputStream(inputStream, classLoader), closure);
    }

    public static <T> T eachLine(InputStream stream, String charset, @ClosureParams(value=FromString.class, options={"String", "String,Integer"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.eachLine(stream, charset, 1, closure);
    }

    public static <T> T eachLine(InputStream stream, String charset, int firstLine, @ClosureParams(value=FromString.class, options={"String", "String,Integer"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.eachLine(new InputStreamReader(stream, charset), firstLine, closure);
    }

    public static <T> T eachLine(InputStream stream, @ClosureParams(value=FromString.class, options={"String", "String,Integer"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.eachLine(stream, 1, closure);
    }

    public static <T> T eachLine(InputStream stream, int firstLine, @ClosureParams(value=FromString.class, options={"String", "String,Integer"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.eachLine(new InputStreamReader(stream), firstLine, closure);
    }

    public static <T> T eachLine(Reader self, @ClosureParams(value=FromString.class, options={"String", "String,Integer"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.eachLine(self, 1, closure);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> T eachLine(Reader self, int firstLine, @ClosureParams(value=FromString.class, options={"String", "String,Integer"}) Closure<T> closure) throws IOException {
        int count = firstLine;
        T result = null;
        BufferedReader br = self instanceof BufferedReader ? (BufferedReader)self : new BufferedReader(self);
        try {
            String line;
            while ((line = br.readLine()) != null) {
                result = DefaultGroovyMethods.callClosureForLine(closure, line, count);
                ++count;
            }
            Reader temp = self;
            self = null;
            temp.close();
            T t = result;
            return t;
        }
        finally {
            IOGroovyMethods.closeWithWarning(self);
            IOGroovyMethods.closeWithWarning(br);
        }
    }

    public static <T> T splitEachLine(Reader self, String regex, @ClosureParams(value=FromString.class, options={"List<String>"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.splitEachLine(self, Pattern.compile(regex), closure);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> T splitEachLine(Reader self, Pattern pattern, @ClosureParams(value=FromString.class, options={"List<String>"}) Closure<T> closure) throws IOException {
        T result = null;
        BufferedReader br = self instanceof BufferedReader ? (BufferedReader)self : new BufferedReader(self);
        try {
            String line;
            while ((line = br.readLine()) != null) {
                List<String> vals = Arrays.asList(pattern.split(line));
                result = closure.call((Object)vals);
            }
            Reader temp = self;
            self = null;
            temp.close();
            T t = result;
            return t;
        }
        finally {
            IOGroovyMethods.closeWithWarning(self);
            IOGroovyMethods.closeWithWarning(br);
        }
    }

    public static <T> T splitEachLine(InputStream stream, String regex, String charset, @ClosureParams(value=FromString.class, options={"List<String>"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.splitEachLine((Reader)new BufferedReader(new InputStreamReader(stream, charset)), regex, closure);
    }

    public static <T> T splitEachLine(InputStream stream, Pattern pattern, String charset, @ClosureParams(value=FromString.class, options={"List<String>"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.splitEachLine((Reader)new BufferedReader(new InputStreamReader(stream, charset)), pattern, closure);
    }

    public static <T> T splitEachLine(InputStream stream, String regex, @ClosureParams(value=FromString.class, options={"List<String>"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.splitEachLine((Reader)new BufferedReader(new InputStreamReader(stream)), regex, closure);
    }

    public static <T> T splitEachLine(InputStream stream, Pattern pattern, @ClosureParams(value=FromString.class, options={"List<String>"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.splitEachLine((Reader)new BufferedReader(new InputStreamReader(stream)), pattern, closure);
    }

    public static String readLine(Reader self) throws IOException {
        if (self instanceof BufferedReader) {
            BufferedReader br = (BufferedReader)self;
            return br.readLine();
        }
        if (self.markSupported()) {
            return IOGroovyMethods.readLineFromReaderWithMark(self);
        }
        return IOGroovyMethods.readLineFromReaderWithoutMark(self);
    }

    private static String readLineFromReaderWithMark(Reader input) throws IOException {
        char[] cbuf = new char[charBufferSize];
        try {
            input.mark(charBufferSize);
        }
        catch (IOException e) {
            LOG.warning("Caught exception setting mark on supporting reader: " + e);
            return IOGroovyMethods.readLineFromReaderWithoutMark(input);
        }
        int count = input.read(cbuf);
        if (count == EOF) {
            return null;
        }
        StringBuilder line = new StringBuilder(expectedLineLength);
        int ls = IOGroovyMethods.lineSeparatorIndex(cbuf, count);
        while (ls == -1) {
            line.append(cbuf, 0, count);
            count = input.read(cbuf);
            if (count == EOF) {
                return line.toString();
            }
            ls = IOGroovyMethods.lineSeparatorIndex(cbuf, count);
        }
        line.append(cbuf, 0, ls);
        int skipLS = 1;
        if (ls + 1 < count) {
            if (cbuf[ls] == '\r' && cbuf[ls + 1] == '\n') {
                ++skipLS;
            }
        } else if (cbuf[ls] == '\r' && input.read() == 10) {
            ++skipLS;
        }
        input.reset();
        input.skip(line.length() + skipLS);
        return line.toString();
    }

    private static String readLineFromReaderWithoutMark(Reader input) throws IOException {
        int c = input.read();
        if (c == -1) {
            return null;
        }
        StringBuilder line = new StringBuilder(expectedLineLength);
        while (c != EOF && c != 10 && c != 13) {
            char ch = (char)c;
            line.append(ch);
            c = input.read();
        }
        return line.toString();
    }

    private static int lineSeparatorIndex(char[] array, int length) {
        for (int k = 0; k < length; ++k) {
            if (!IOGroovyMethods.isLineSeparator(array[k])) continue;
            return k;
        }
        return -1;
    }

    private static boolean isLineSeparator(char c) {
        return c == '\n' || c == '\r';
    }

    public static List<String> readLines(InputStream stream) throws IOException {
        return IOGroovyMethods.readLines(IOGroovyMethods.newReader(stream));
    }

    public static List<String> readLines(InputStream stream, String charset) throws IOException {
        return IOGroovyMethods.readLines(IOGroovyMethods.newReader(stream, charset));
    }

    public static List<String> readLines(Reader reader) throws IOException {
        IteratorClosureAdapter closure = new IteratorClosureAdapter(reader);
        IOGroovyMethods.eachLine(reader, closure);
        return closure.asList();
    }

    public static String getText(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        return IOGroovyMethods.getText(reader);
    }

    public static String getText(InputStream is, String charset) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
        return IOGroovyMethods.getText(reader);
    }

    public static String getText(Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        return IOGroovyMethods.getText(bufferedReader);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getText(BufferedReader reader) throws IOException {
        StringBuilder answer = new StringBuilder();
        char[] charBuffer = new char[8192];
        try {
            int nbCharRead;
            while ((nbCharRead = reader.read(charBuffer)) != -1) {
                answer.append(charBuffer, 0, nbCharRead);
            }
            BufferedReader temp = reader;
            reader = null;
            ((Reader)temp).close();
        }
        finally {
            IOGroovyMethods.closeWithWarning(reader);
        }
        return answer.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream answer = new ByteArrayOutputStream();
        byte[] byteBuffer = new byte[8192];
        try {
            int nbByteRead;
            while ((nbByteRead = is.read(byteBuffer)) != -1) {
                answer.write(byteBuffer, 0, nbByteRead);
            }
        }
        finally {
            IOGroovyMethods.closeWithWarning(is);
        }
        return answer.toByteArray();
    }

    public static void setBytes(OutputStream os, byte[] bytes) throws IOException {
        try {
            os.write(bytes);
        }
        finally {
            IOGroovyMethods.closeWithWarning(os);
        }
    }

    public static void writeLine(BufferedWriter writer, String line) throws IOException {
        writer.write(line);
        writer.newLine();
    }

    public static Iterator<String> iterator(Reader self) {
        final BufferedReader bufferedReader = self instanceof BufferedReader ? (BufferedReader)self : new BufferedReader(self);
        return new Iterator<String>(){
            String nextVal;
            boolean nextMustRead = true;
            boolean hasNext = true;

            @Override
            public boolean hasNext() {
                if (this.nextMustRead && this.hasNext) {
                    try {
                        this.nextVal = this.readNext();
                        this.nextMustRead = false;
                    }
                    catch (IOException e) {
                        this.hasNext = false;
                    }
                }
                return this.hasNext;
            }

            @Override
            public String next() {
                String retval = null;
                if (this.nextMustRead) {
                    try {
                        retval = this.readNext();
                    }
                    catch (IOException e) {
                        this.hasNext = false;
                    }
                } else {
                    retval = this.nextVal;
                }
                this.nextMustRead = true;
                return retval;
            }

            private String readNext() throws IOException {
                String nv = bufferedReader.readLine();
                if (nv == null) {
                    this.hasNext = false;
                }
                return nv;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Cannot remove() from a Reader Iterator");
            }
        };
    }

    public static Iterator<Byte> iterator(InputStream self) {
        return IOGroovyMethods.iterator(new DataInputStream(self));
    }

    public static Iterator<Byte> iterator(final DataInputStream self) {
        return new Iterator<Byte>(){
            Byte nextVal;
            boolean nextMustRead = true;
            boolean hasNext = true;

            @Override
            public boolean hasNext() {
                if (this.nextMustRead && this.hasNext) {
                    try {
                        this.nextVal = self.readByte();
                        this.nextMustRead = false;
                    }
                    catch (IOException e) {
                        this.hasNext = false;
                    }
                }
                return this.hasNext;
            }

            @Override
            public Byte next() {
                Byte retval = null;
                if (this.nextMustRead) {
                    try {
                        retval = self.readByte();
                    }
                    catch (IOException e) {
                        this.hasNext = false;
                    }
                } else {
                    retval = this.nextVal;
                }
                this.nextMustRead = true;
                return retval;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Cannot remove() from a DataInputStream Iterator");
            }
        };
    }

    public static BufferedReader newReader(InputStream self) {
        return new BufferedReader(new InputStreamReader(self));
    }

    public static BufferedReader newReader(InputStream self, String charset) throws UnsupportedEncodingException {
        return new BufferedReader(new InputStreamReader(self, charset));
    }

    public static PrintWriter newPrintWriter(Writer writer) {
        return new GroovyPrintWriter(writer);
    }

    public static PrintWriter newPrintWriter(OutputStream stream) {
        return new GroovyPrintWriter(stream);
    }

    public static <T> T withPrintWriter(Writer writer, @ClosureParams(value=SimpleType.class, options={"java.io.PrintWriter"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withWriter(IOGroovyMethods.newPrintWriter(writer), closure);
    }

    public static <T> T withPrintWriter(OutputStream stream, @ClosureParams(value=SimpleType.class, options={"java.io.PrintWriter"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withWriter(IOGroovyMethods.newPrintWriter(stream), closure);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> T withWriter(Writer writer, @ClosureParams(value=SimpleType.class, options={"java.io.Writer"}) Closure<T> closure) throws IOException {
        try {
            T result = closure.call((Object)writer);
            try {
                writer.flush();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            Writer temp = writer;
            writer = null;
            temp.close();
            T t = result;
            return t;
        }
        finally {
            IOGroovyMethods.closeWithWarning(writer);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T> T withReader(Reader reader, @ClosureParams(value=SimpleType.class, options={"java.io.Reader"}) Closure<T> closure) throws IOException {
        try {
            T result = closure.call((Object)reader);
            Reader temp = reader;
            reader = null;
            temp.close();
            T t = result;
            return t;
        }
        finally {
            IOGroovyMethods.closeWithWarning(reader);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T, U extends InputStream> T withStream(U stream, @ClosureParams(value=FirstParam.class) Closure<T> closure) throws IOException {
        try {
            T result = closure.call((Object)stream);
            U temp = stream;
            stream = null;
            temp.close();
            T t = result;
            return t;
        }
        finally {
            IOGroovyMethods.closeWithWarning(stream);
        }
    }

    public static <T> T withReader(InputStream in, @ClosureParams(value=SimpleType.class, options={"java.io.Reader"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withReader(new InputStreamReader(in), closure);
    }

    public static <T> T withReader(InputStream in, String charset, @ClosureParams(value=SimpleType.class, options={"java.io.Reader"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withReader(new InputStreamReader(in, charset), closure);
    }

    public static <T> T withWriter(OutputStream stream, @ClosureParams(value=SimpleType.class, options={"java.io.Writer"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withWriter(new OutputStreamWriter(stream), closure);
    }

    public static Writer newWriter(OutputStream stream) {
        return new OutputStreamWriter(stream);
    }

    public static <T> T withWriter(OutputStream stream, String charset, @ClosureParams(value=SimpleType.class, options={"java.io.Writer"}) Closure<T> closure) throws IOException {
        return IOGroovyMethods.withWriter(new OutputStreamWriter(stream, charset), closure);
    }

    public static Writer newWriter(OutputStream stream, String charset) throws UnsupportedEncodingException {
        return new OutputStreamWriter(stream, charset);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T, U extends OutputStream> T withStream(U os, @ClosureParams(value=FirstParam.class) Closure<T> closure) throws IOException {
        try {
            T result = closure.call((Object)os);
            os.flush();
            U temp = os;
            os = null;
            temp.close();
            T t = result;
            return t;
        }
        finally {
            IOGroovyMethods.closeWithWarning(os);
        }
    }

    public static void eachByte(InputStream is, @ClosureParams(value=SimpleType.class, options={"byte"}) Closure closure) throws IOException {
        try {
            int b;
            while ((b = is.read()) != -1) {
                closure.call((Object)((byte)b));
            }
            InputStream temp = is;
            is = null;
            temp.close();
        }
        finally {
            IOGroovyMethods.closeWithWarning(is);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void eachByte(InputStream is, int bufferLen, @ClosureParams(value=FromString.class, options={"byte[],Integer"}) Closure closure) throws IOException {
        byte[] buffer = new byte[bufferLen];
        try {
            int bytesRead;
            while ((bytesRead = is.read(buffer, 0, bufferLen)) > 0) {
                closure.call(buffer, bytesRead);
            }
            InputStream temp = is;
            is = null;
            temp.close();
        }
        finally {
            IOGroovyMethods.closeWithWarning(is);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void transformChar(Reader self, Writer writer, @ClosureParams(value=SimpleType.class, options={"java.lang.String"}) Closure closure) throws IOException {
        try {
            int c;
            char[] chars = new char[1];
            while ((c = self.read()) != -1) {
                chars[0] = (char)c;
                writer.write((String)closure.call((Object)new String(chars)));
            }
            writer.flush();
            Writer temp2 = writer;
            writer = null;
            temp2.close();
            Reader temp1 = self;
            self = null;
            temp1.close();
        }
        finally {
            IOGroovyMethods.closeWithWarning(self);
            IOGroovyMethods.closeWithWarning(writer);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void transformLine(Reader reader, Writer writer, @ClosureParams(value=SimpleType.class, options={"java.lang.String"}) Closure closure) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        BufferedWriter bw = new BufferedWriter(writer);
        try {
            String line;
            while ((line = br.readLine()) != null) {
                Object o = closure.call((Object)line);
                if (o == null) continue;
                bw.write(o.toString());
                bw.newLine();
            }
            bw.flush();
            Writer temp2 = writer;
            writer = null;
            temp2.close();
            Reader temp1 = reader;
            reader = null;
            temp1.close();
        }
        finally {
            IOGroovyMethods.closeWithWarning(br);
            IOGroovyMethods.closeWithWarning(reader);
            IOGroovyMethods.closeWithWarning(bw);
            IOGroovyMethods.closeWithWarning(writer);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void filterLine(Reader reader, Writer writer, @ClosureParams(value=SimpleType.class, options={"java.lang.String"}) Closure closure) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        BufferedWriter bw = new BufferedWriter(writer);
        try {
            String line;
            BooleanClosureWrapper bcw = new BooleanClosureWrapper(closure);
            while ((line = br.readLine()) != null) {
                if (!bcw.call(line)) continue;
                bw.write(line);
                bw.newLine();
            }
            bw.flush();
            Writer temp2 = writer;
            writer = null;
            temp2.close();
            Reader temp1 = reader;
            reader = null;
            temp1.close();
        }
        finally {
            IOGroovyMethods.closeWithWarning(br);
            IOGroovyMethods.closeWithWarning(reader);
            IOGroovyMethods.closeWithWarning(bw);
            IOGroovyMethods.closeWithWarning(writer);
        }
    }

    public static Writable filterLine(Reader reader, final @ClosureParams(value=SimpleType.class, options={"java.lang.String"}) Closure closure) {
        final BufferedReader br = new BufferedReader(reader);
        return new Writable(){

            @Override
            public Writer writeTo(Writer out) throws IOException {
                String line;
                BufferedWriter bw = new BufferedWriter(out);
                BooleanClosureWrapper bcw = new BooleanClosureWrapper(closure);
                while ((line = br.readLine()) != null) {
                    if (!bcw.call(line)) continue;
                    bw.write(line);
                    bw.newLine();
                }
                bw.flush();
                return out;
            }

            public String toString() {
                StringWriter buffer = new StringWriter();
                try {
                    this.writeTo(buffer);
                }
                catch (IOException e) {
                    throw new StringWriterIOException(e);
                }
                return buffer.toString();
            }
        };
    }

    public static Writable filterLine(InputStream self, @ClosureParams(value=SimpleType.class, options={"java.lang.String"}) Closure predicate) {
        return IOGroovyMethods.filterLine(IOGroovyMethods.newReader(self), predicate);
    }

    public static Writable filterLine(InputStream self, String charset, @ClosureParams(value=SimpleType.class, options={"java.lang.String"}) Closure predicate) throws UnsupportedEncodingException {
        return IOGroovyMethods.filterLine(IOGroovyMethods.newReader(self, charset), predicate);
    }

    public static void filterLine(InputStream self, Writer writer, @ClosureParams(value=SimpleType.class, options={"java.lang.String"}) Closure predicate) throws IOException {
        IOGroovyMethods.filterLine(IOGroovyMethods.newReader(self), writer, predicate);
    }

    public static void filterLine(InputStream self, Writer writer, String charset, @ClosureParams(value=SimpleType.class, options={"java.lang.String"}) Closure predicate) throws IOException {
        IOGroovyMethods.filterLine(IOGroovyMethods.newReader(self, charset), writer, predicate);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static <T, U extends Closeable> T withCloseable(U self, @ClosureParams(value=FirstParam.class) Closure<T> action) throws IOException {
        try {
            T result = action.call((Object)self);
            U temp = self;
            self = null;
            temp.close();
            T t = result;
            return t;
        }
        finally {
            DefaultGroovyMethodsSupport.closeWithWarning(self);
        }
    }
}

