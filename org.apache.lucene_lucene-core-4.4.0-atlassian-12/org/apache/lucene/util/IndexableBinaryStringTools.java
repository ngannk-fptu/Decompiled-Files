/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

@Deprecated
public final class IndexableBinaryStringTools {
    private static final CodingCase[] CODING_CASES = new CodingCase[]{new CodingCase(7, 1), new CodingCase(14, 6, 2), new CodingCase(13, 5, 3), new CodingCase(12, 4, 4), new CodingCase(11, 3, 5), new CodingCase(10, 2, 6), new CodingCase(9, 1, 7), new CodingCase(8, 0)};

    private IndexableBinaryStringTools() {
    }

    public static int getEncodedLength(byte[] inputArray, int inputOffset, int inputLength) {
        return (int)((8L * (long)inputLength + 14L) / 15L) + 1;
    }

    public static int getDecodedLength(char[] encoded, int offset, int length) {
        int numChars = length - 1;
        if (numChars <= 0) {
            return 0;
        }
        long numFullBytesInFinalChar = encoded[offset + length - 1];
        long numEncodedChars = numChars - 1;
        return (int)((numEncodedChars * 15L + 7L) / 8L + numFullBytesInFinalChar);
    }

    public static void encode(byte[] inputArray, int inputOffset, int inputLength, char[] outputArray, int outputOffset, int outputLength) {
        assert (outputLength == IndexableBinaryStringTools.getEncodedLength(inputArray, inputOffset, inputLength));
        if (inputLength > 0) {
            CodingCase codingCase;
            int inputByteNum = inputOffset;
            int caseNum = 0;
            int outputCharNum = outputOffset;
            while (inputByteNum + IndexableBinaryStringTools.CODING_CASES[caseNum].numBytes <= inputLength) {
                codingCase = CODING_CASES[caseNum];
                outputArray[outputCharNum] = 2 == codingCase.numBytes ? (char)(((inputArray[inputByteNum] & 0xFF) << codingCase.initialShift) + ((inputArray[inputByteNum + 1] & 0xFF) >>> codingCase.finalShift & codingCase.finalMask) & Short.MAX_VALUE) : (char)(((inputArray[inputByteNum] & 0xFF) << codingCase.initialShift) + ((inputArray[inputByteNum + 1] & 0xFF) << codingCase.middleShift) + ((inputArray[inputByteNum + 2] & 0xFF) >>> codingCase.finalShift & codingCase.finalMask) & Short.MAX_VALUE);
                inputByteNum += codingCase.advanceBytes;
                if (++caseNum == CODING_CASES.length) {
                    caseNum = 0;
                }
                ++outputCharNum;
            }
            codingCase = CODING_CASES[caseNum];
            if (inputByteNum + 1 < inputLength) {
                outputArray[outputCharNum++] = (char)(((inputArray[inputByteNum] & 0xFF) << codingCase.initialShift) + ((inputArray[inputByteNum + 1] & 0xFF) << codingCase.middleShift) & Short.MAX_VALUE);
                outputArray[outputCharNum++] = '\u0001';
            } else if (inputByteNum < inputLength) {
                outputArray[outputCharNum++] = (char)((inputArray[inputByteNum] & 0xFF) << codingCase.initialShift & Short.MAX_VALUE);
                outputArray[outputCharNum++] = caseNum == 0 ? (char)'\u0001' : '\u0000';
            } else {
                outputArray[outputCharNum++] = '\u0001';
            }
        }
    }

    public static void decode(char[] inputArray, int inputOffset, int inputLength, byte[] outputArray, int outputOffset, int outputLength) {
        assert (outputLength == IndexableBinaryStringTools.getDecodedLength(inputArray, inputOffset, inputLength));
        int numInputChars = inputLength - 1;
        int numOutputBytes = outputLength;
        if (numOutputBytes > 0) {
            short inputChar;
            CodingCase codingCase;
            int inputCharNum;
            int caseNum = 0;
            int outputByteNum = outputOffset;
            for (inputCharNum = inputOffset; inputCharNum < numInputChars - 1; ++inputCharNum) {
                codingCase = CODING_CASES[caseNum];
                inputChar = (short)inputArray[inputCharNum];
                if (2 == codingCase.numBytes) {
                    if (0 == caseNum) {
                        outputArray[outputByteNum] = (byte)(inputChar >>> codingCase.initialShift);
                    } else {
                        int n = outputByteNum;
                        outputArray[n] = (byte)(outputArray[n] + (byte)(inputChar >>> codingCase.initialShift));
                    }
                    outputArray[outputByteNum + 1] = (byte)((inputChar & codingCase.finalMask) << codingCase.finalShift);
                } else {
                    int n = outputByteNum;
                    outputArray[n] = (byte)(outputArray[n] + (byte)(inputChar >>> codingCase.initialShift));
                    outputArray[outputByteNum + 1] = (byte)((inputChar & codingCase.middleMask) >>> codingCase.middleShift);
                    outputArray[outputByteNum + 2] = (byte)((inputChar & codingCase.finalMask) << codingCase.finalShift);
                }
                outputByteNum += codingCase.advanceBytes;
                if (++caseNum != CODING_CASES.length) continue;
                caseNum = 0;
            }
            inputChar = (short)inputArray[inputCharNum];
            codingCase = CODING_CASES[caseNum];
            if (0 == caseNum) {
                outputArray[outputByteNum] = 0;
            }
            int n = outputByteNum;
            outputArray[n] = (byte)(outputArray[n] + (byte)(inputChar >>> codingCase.initialShift));
            int bytesLeft = numOutputBytes - outputByteNum;
            if (bytesLeft > 1) {
                if (2 == codingCase.numBytes) {
                    outputArray[outputByteNum + 1] = (byte)((inputChar & codingCase.finalMask) >>> codingCase.finalShift);
                } else {
                    outputArray[outputByteNum + 1] = (byte)((inputChar & codingCase.middleMask) >>> codingCase.middleShift);
                    if (bytesLeft > 2) {
                        outputArray[outputByteNum + 2] = (byte)((inputChar & codingCase.finalMask) << codingCase.finalShift);
                    }
                }
            }
        }
    }

    static class CodingCase {
        int numBytes;
        int initialShift;
        int middleShift;
        int finalShift;
        int advanceBytes = 2;
        short middleMask;
        short finalMask;

        CodingCase(int initialShift, int middleShift, int finalShift) {
            this.numBytes = 3;
            this.initialShift = initialShift;
            this.middleShift = middleShift;
            this.finalShift = finalShift;
            this.finalMask = (short)(255 >>> finalShift);
            this.middleMask = (short)(255 << middleShift);
        }

        CodingCase(int initialShift, int finalShift) {
            this.numBytes = 2;
            this.initialShift = initialShift;
            this.finalShift = finalShift;
            this.finalMask = (short)(255 >>> finalShift);
            if (finalShift != 0) {
                this.advanceBytes = 1;
            }
        }
    }
}

