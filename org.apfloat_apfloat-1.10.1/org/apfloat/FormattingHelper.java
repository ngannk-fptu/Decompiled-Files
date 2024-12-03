/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat;

import java.io.FilterWriter;
import java.io.Flushable;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DecimalFormatSymbols;
import java.util.Formatter;
import java.util.Locale;

class FormattingHelper {
    private FormattingHelper() {
    }

    public static Writer wrapAppendableWriter(Appendable out) {
        return out instanceof Writer ? (Writer)out : new AppendableWriter(out);
    }

    public static Writer wrapLocalizeWriter(Writer out, Formatter formatter, int radix, boolean isUpperCase) {
        return new LocalizeWriter(out, formatter.locale(), radix <= 10, isUpperCase);
    }

    public static Writer wrapPadWriter(Writer out, boolean isLeftJustify) {
        out = isLeftJustify ? new CountWriter(out) : new BufferWriter(out);
        return out;
    }

    public static void finishPad(Writer out, long width) throws IOException {
        if (out instanceof CountWriter) {
            CountWriter counter = (CountWriter)out;
            long count = width - counter.count();
            FormattingHelper.pad(out, count);
        } else {
            BufferWriter buffer = (BufferWriter)out;
            long count = width - (long)buffer.getBuffer().length();
            FormattingHelper.pad(buffer.out(), count);
            buffer.out().append(buffer.getBuffer());
        }
    }

    private static void pad(Appendable out, long count) throws IOException {
        for (long i = 0L; i < count; ++i) {
            out.append(' ');
        }
    }

    private static class AppendableWriter
    extends Writer {
        private Appendable out;

        public AppendableWriter(Appendable out) {
            this.out = out;
        }

        @Override
        public void write(int c) throws IOException {
            this.out.append((char)c);
        }

        @Override
        public void write(char[] buffer, int offset, int length) throws IOException {
            for (int i = 0; i < length; ++i) {
                this.out.append(buffer[i + offset]);
            }
        }

        @Override
        public void write(String text, int offset, int length) throws IOException {
            this.out.append(text, offset, length);
        }

        @Override
        public Writer append(CharSequence sequence) throws IOException {
            this.out.append(sequence);
            return this;
        }

        @Override
        public Writer append(CharSequence sequence, int start, int end) throws IOException {
            this.out.append(sequence, start, end);
            return this;
        }

        @Override
        public void flush() throws IOException {
            if (this.out instanceof Flushable) {
                ((Flushable)((Object)this.out)).flush();
            }
        }

        @Override
        public void close() throws IOException {
            if (this.out instanceof AutoCloseable) {
                try {
                    ((AutoCloseable)((Object)this.out)).close();
                }
                catch (IOException ioe) {
                    throw ioe;
                }
                catch (Exception e) {
                    throw new IOException(e);
                }
            }
        }
    }

    private static class LocalizeWriter
    extends FilterWriter {
        private Locale locale;
        private boolean localizeDigits;
        private boolean isUpperCase;
        private char zero;
        private char decimalSeparator;

        public LocalizeWriter(Writer out, Locale locale, boolean localizeDigits, boolean isUpperCase) {
            super(out);
            this.locale = locale;
            this.localizeDigits = localizeDigits;
            this.isUpperCase = isUpperCase;
            if (locale != null) {
                DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(locale);
                this.zero = decimalFormatSymbols.getZeroDigit();
                this.decimalSeparator = decimalFormatSymbols.getDecimalSeparator();
            } else {
                this.zero = (char)48;
                this.decimalSeparator = (char)46;
            }
        }

        @Override
        public void write(int c) throws IOException {
            if (c == 46) {
                c = this.decimalSeparator;
            } else if (this.localizeDigits && c >= 48 && c <= 57) {
                c += this.zero - 48;
            }
            if (this.isUpperCase) {
                String s = this.locale == null ? String.valueOf((char)c).toUpperCase() : String.valueOf((char)c).toUpperCase(this.locale);
                for (int i = 0; i < s.length(); ++i) {
                    super.write(s.charAt(i));
                }
            } else {
                super.write(c);
            }
        }

        @Override
        public void write(char[] buffer, int offset, int length) throws IOException {
            for (int i = 0; i < length; ++i) {
                this.write(buffer[i + offset]);
            }
        }

        @Override
        public void write(String text, int offset, int length) throws IOException {
            for (int i = 0; i < length; ++i) {
                this.write(text.charAt(i + offset));
            }
        }
    }

    private static class CountWriter
    extends FilterWriter {
        private long count;

        public CountWriter(Writer out) {
            super(out);
        }

        @Override
        public void write(int c) throws IOException {
            super.write(c);
            ++this.count;
        }

        @Override
        public void write(char[] buffer, int offset, int length) throws IOException {
            super.write(buffer, offset, length);
            this.count += (long)length;
        }

        @Override
        public void write(String text, int offset, int length) throws IOException {
            super.write(text, offset, length);
            this.count += (long)length;
        }

        public long count() {
            return this.count;
        }
    }

    private static class BufferWriter
    extends StringWriter {
        private Writer out;

        public BufferWriter(Writer out) {
            this.out = out;
        }

        public Writer out() {
            return this.out;
        }
    }
}

