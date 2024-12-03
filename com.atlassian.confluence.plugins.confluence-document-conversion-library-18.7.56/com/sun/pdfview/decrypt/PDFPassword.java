/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.decrypt;

import com.sun.pdfview.Identity8BitCharsetEncoder;
import com.sun.pdfview.PDFDocCharsetEncoder;
import com.sun.pdfview.PDFStringUtil;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PDFPassword {
    public static final PDFPassword EMPTY_PASSWORD = new PDFPassword(new byte[0]);
    private byte[] passwordBytes = null;
    private String passwordString = null;
    private static final PasswordByteGenerator[] PASSWORD_BYTE_GENERATORS = new PasswordByteGenerator[]{new PDFDocEncodingByteGenerator(null), new PDFDocEncodingByteGenerator((byte)0), new PDFDocEncodingByteGenerator((byte)63), new PasswordByteGenerator(){

        @Override
        public byte[] generateBytes(String password) {
            return PDFStringUtil.asBytes(password);
        }
    }, new IdentityEncodingByteGenerator(null), new IdentityEncodingByteGenerator((byte)0), new IdentityEncodingByteGenerator((byte)63)};

    public static PDFPassword nonNullPassword(PDFPassword password) {
        return password != null ? password : EMPTY_PASSWORD;
    }

    public PDFPassword(byte[] passwordBytes) {
        this.passwordBytes = passwordBytes != null ? passwordBytes : new byte[]{};
    }

    public PDFPassword(String passwordString) {
        this.passwordString = passwordString != null ? passwordString : "";
    }

    List<byte[]> getPasswordBytes(boolean unicodeConversion) {
        if (this.passwordBytes != null || this.passwordString == null) {
            return Collections.singletonList(this.passwordBytes);
        }
        if (this.isAlphaNum7BitString(this.passwordString)) {
            return Collections.singletonList(PDFStringUtil.asBytes(this.passwordString));
        }
        return PDFPassword.generatePossiblePasswordBytes(this.passwordString);
    }

    private static List<byte[]> generatePossiblePasswordBytes(String passwordString) {
        ArrayList<byte[]> possibilties = new ArrayList<byte[]>();
        for (PasswordByteGenerator generator : PASSWORD_BYTE_GENERATORS) {
            byte[] generated = generator.generateBytes(passwordString);
            boolean alreadyGenerated = false;
            for (int i = 0; !alreadyGenerated && i < possibilties.size(); ++i) {
                if (!Arrays.equals((byte[])possibilties.get(i), generated)) continue;
                alreadyGenerated = true;
            }
            if (alreadyGenerated) continue;
            possibilties.add(generated);
        }
        return possibilties;
    }

    private boolean isAlphaNum7BitString(String string) {
        for (int i = 0; i < string.length(); ++i) {
            char c = string.charAt(i);
            if (c < '\u007f' && Character.isLetterOrDigit(c)) continue;
            return false;
        }
        return true;
    }

    private static class IdentityEncodingByteGenerator
    extends CharsetEncoderGenerator {
        private IdentityEncodingByteGenerator(Byte replacementByte) {
            super(replacementByte);
        }

        @Override
        protected CharsetEncoder createCharsetEncoder() {
            return new Identity8BitCharsetEncoder();
        }
    }

    private static class PDFDocEncodingByteGenerator
    extends CharsetEncoderGenerator {
        private PDFDocEncodingByteGenerator(Byte replacementByte) {
            super(replacementByte);
        }

        @Override
        protected CharsetEncoder createCharsetEncoder() {
            return new PDFDocCharsetEncoder();
        }
    }

    private static abstract class CharsetEncoderGenerator
    implements PasswordByteGenerator {
        private Byte replacementByte;

        protected CharsetEncoderGenerator(Byte replacementByte) {
            this.replacementByte = replacementByte;
        }

        @Override
        public byte[] generateBytes(String password) {
            CharsetEncoder encoder = this.createCharsetEncoder();
            if (this.replacementByte != null) {
                encoder.replaceWith(new byte[]{this.replacementByte});
                encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
            } else {
                encoder.onUnmappableCharacter(CodingErrorAction.IGNORE);
            }
            try {
                ByteBuffer b = encoder.encode(CharBuffer.wrap(password));
                byte[] bytes = new byte[b.remaining()];
                b.get(bytes);
                return bytes;
            }
            catch (CharacterCodingException e) {
                return null;
            }
        }

        protected abstract CharsetEncoder createCharsetEncoder();
    }

    private static interface PasswordByteGenerator {
        public byte[] generateBytes(String var1);
    }
}

