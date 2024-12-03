/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.document.Field
 *  org.apache.lucene.document.FieldType
 *  org.apache.lucene.document.TextField
 *  org.apache.lucene.index.FieldInfo
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.StoredFieldVisitor
 *  org.apache.lucene.index.StoredFieldVisitor$Status
 */
package org.apache.lucene.search.vectorhighlight;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StoredFieldVisitor;
import org.apache.lucene.search.highlight.DefaultEncoder;
import org.apache.lucene.search.highlight.Encoder;
import org.apache.lucene.search.vectorhighlight.BoundaryScanner;
import org.apache.lucene.search.vectorhighlight.FieldFragList;
import org.apache.lucene.search.vectorhighlight.FieldPhraseList;
import org.apache.lucene.search.vectorhighlight.FragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.SimpleBoundaryScanner;

public abstract class BaseFragmentsBuilder
implements FragmentsBuilder {
    protected String[] preTags;
    protected String[] postTags;
    public static final String[] COLORED_PRE_TAGS = new String[]{"<b style=\"background:yellow\">", "<b style=\"background:lawngreen\">", "<b style=\"background:aquamarine\">", "<b style=\"background:magenta\">", "<b style=\"background:palegreen\">", "<b style=\"background:coral\">", "<b style=\"background:wheat\">", "<b style=\"background:khaki\">", "<b style=\"background:lime\">", "<b style=\"background:deepskyblue\">", "<b style=\"background:deeppink\">", "<b style=\"background:salmon\">", "<b style=\"background:peachpuff\">", "<b style=\"background:violet\">", "<b style=\"background:mediumpurple\">", "<b style=\"background:palegoldenrod\">", "<b style=\"background:darkkhaki\">", "<b style=\"background:springgreen\">", "<b style=\"background:turquoise\">", "<b style=\"background:powderblue\">"};
    public static final String[] COLORED_POST_TAGS = new String[]{"</b>"};
    private char multiValuedSeparator = (char)32;
    private final BoundaryScanner boundaryScanner;
    private boolean discreteMultiValueHighlighting = false;
    private static final Encoder NULL_ENCODER = new DefaultEncoder();

    protected BaseFragmentsBuilder() {
        this(new String[]{"<b>"}, new String[]{"</b>"});
    }

    protected BaseFragmentsBuilder(String[] preTags, String[] postTags) {
        this(preTags, postTags, new SimpleBoundaryScanner());
    }

    protected BaseFragmentsBuilder(BoundaryScanner boundaryScanner) {
        this(new String[]{"<b>"}, new String[]{"</b>"}, boundaryScanner);
    }

    protected BaseFragmentsBuilder(String[] preTags, String[] postTags, BoundaryScanner boundaryScanner) {
        this.preTags = preTags;
        this.postTags = postTags;
        this.boundaryScanner = boundaryScanner;
    }

    static Object checkTagsArgument(Object tags) {
        if (tags instanceof String) {
            return tags;
        }
        if (tags instanceof String[]) {
            return tags;
        }
        throw new IllegalArgumentException("type of preTags/postTags must be a String or String[]");
    }

    public abstract List<FieldFragList.WeightedFragInfo> getWeightedFragInfoList(List<FieldFragList.WeightedFragInfo> var1);

    @Override
    public String createFragment(IndexReader reader, int docId, String fieldName, FieldFragList fieldFragList) throws IOException {
        return this.createFragment(reader, docId, fieldName, fieldFragList, this.preTags, this.postTags, NULL_ENCODER);
    }

    @Override
    public String[] createFragments(IndexReader reader, int docId, String fieldName, FieldFragList fieldFragList, int maxNumFragments) throws IOException {
        return this.createFragments(reader, docId, fieldName, fieldFragList, maxNumFragments, this.preTags, this.postTags, NULL_ENCODER);
    }

    @Override
    public String createFragment(IndexReader reader, int docId, String fieldName, FieldFragList fieldFragList, String[] preTags, String[] postTags, Encoder encoder) throws IOException {
        String[] fragments = this.createFragments(reader, docId, fieldName, fieldFragList, 1, preTags, postTags, encoder);
        if (fragments == null || fragments.length == 0) {
            return null;
        }
        return fragments[0];
    }

    @Override
    public String[] createFragments(IndexReader reader, int docId, String fieldName, FieldFragList fieldFragList, int maxNumFragments, String[] preTags, String[] postTags, Encoder encoder) throws IOException {
        if (maxNumFragments < 0) {
            throw new IllegalArgumentException("maxNumFragments(" + maxNumFragments + ") must be positive number.");
        }
        List<FieldFragList.WeightedFragInfo> fragInfos = fieldFragList.getFragInfos();
        Field[] values = this.getFields(reader, docId, fieldName);
        if (values.length == 0) {
            return null;
        }
        if (this.discreteMultiValueHighlighting && values.length > 1) {
            fragInfos = this.discreteMultiValueHighlighting(fragInfos, values);
        }
        int limitFragments = maxNumFragments < (fragInfos = this.getWeightedFragInfoList(fragInfos)).size() ? maxNumFragments : fragInfos.size();
        ArrayList<String> fragments = new ArrayList<String>(limitFragments);
        StringBuilder buffer = new StringBuilder();
        int[] nextValueIndex = new int[]{0};
        for (int n = 0; n < limitFragments; ++n) {
            FieldFragList.WeightedFragInfo fragInfo = fragInfos.get(n);
            fragments.add(this.makeFragment(buffer, nextValueIndex, values, fragInfo, preTags, postTags, encoder));
        }
        return fragments.toArray(new String[fragments.size()]);
    }

    protected Field[] getFields(IndexReader reader, int docId, final String fieldName) throws IOException {
        final ArrayList fields = new ArrayList();
        reader.document(docId, new StoredFieldVisitor(){

            public void stringField(FieldInfo fieldInfo, String value) {
                FieldType ft = new FieldType(TextField.TYPE_STORED);
                ft.setStoreTermVectors(fieldInfo.hasVectors());
                fields.add(new Field(fieldInfo.name, value, ft));
            }

            public StoredFieldVisitor.Status needsField(FieldInfo fieldInfo) {
                return fieldInfo.name.equals(fieldName) ? StoredFieldVisitor.Status.YES : StoredFieldVisitor.Status.NO;
            }
        });
        return fields.toArray(new Field[fields.size()]);
    }

    protected String makeFragment(StringBuilder buffer, int[] index, Field[] values, FieldFragList.WeightedFragInfo fragInfo, String[] preTags, String[] postTags, Encoder encoder) {
        StringBuilder fragment = new StringBuilder();
        int s = fragInfo.getStartOffset();
        int[] modifiedStartOffset = new int[]{s};
        String src = this.getFragmentSourceMSO(buffer, index, values, s, fragInfo.getEndOffset(), modifiedStartOffset);
        int srcIndex = 0;
        for (FieldFragList.WeightedFragInfo.SubInfo subInfo : fragInfo.getSubInfos()) {
            for (FieldPhraseList.WeightedPhraseInfo.Toffs to : subInfo.getTermsOffsets()) {
                fragment.append(encoder.encodeText(src.substring(srcIndex, to.getStartOffset() - modifiedStartOffset[0]))).append(this.getPreTag(preTags, subInfo.getSeqnum())).append(encoder.encodeText(src.substring(to.getStartOffset() - modifiedStartOffset[0], to.getEndOffset() - modifiedStartOffset[0]))).append(this.getPostTag(postTags, subInfo.getSeqnum()));
                srcIndex = to.getEndOffset() - modifiedStartOffset[0];
            }
        }
        fragment.append(encoder.encodeText(src.substring(srcIndex)));
        return fragment.toString();
    }

    protected String getFragmentSourceMSO(StringBuilder buffer, int[] index, Field[] values, int startOffset, int endOffset, int[] modifiedStartOffset) {
        while (buffer.length() < endOffset && index[0] < values.length) {
            int n = index[0];
            index[0] = n + 1;
            buffer.append(values[n].stringValue());
            buffer.append(this.getMultiValuedSeparator());
        }
        int bufferLength = buffer.length();
        if (values[index[0] - 1].fieldType().tokenized()) {
            --bufferLength;
        }
        int eo = bufferLength < endOffset ? bufferLength : this.boundaryScanner.findEndOffset(buffer, endOffset);
        modifiedStartOffset[0] = this.boundaryScanner.findStartOffset(buffer, startOffset);
        return buffer.substring(modifiedStartOffset[0], eo);
    }

    protected String getFragmentSource(StringBuilder buffer, int[] index, Field[] values, int startOffset, int endOffset) {
        while (buffer.length() < endOffset && index[0] < values.length) {
            buffer.append(values[index[0]].stringValue());
            buffer.append(this.multiValuedSeparator);
            index[0] = index[0] + 1;
        }
        int eo = buffer.length() < endOffset ? buffer.length() : endOffset;
        return buffer.substring(startOffset, eo);
    }

    protected List<FieldFragList.WeightedFragInfo> discreteMultiValueHighlighting(List<FieldFragList.WeightedFragInfo> fragInfos, Field[] fields) {
        HashMap fieldNameToFragInfos = new HashMap();
        for (Field field : fields) {
            fieldNameToFragInfos.put(field.name(), new ArrayList());
        }
        block1: for (FieldFragList.WeightedFragInfo fragInfo : fragInfos) {
            int fieldEnd = 0;
            for (Field field : fields) {
                if (field.stringValue().isEmpty()) {
                    ++fieldEnd;
                    continue;
                }
                int fieldStart = fieldEnd;
                if (fragInfo.getStartOffset() >= fieldStart && fragInfo.getEndOffset() >= fieldStart && fragInfo.getStartOffset() <= (fieldEnd += field.stringValue().length() + 1) && fragInfo.getEndOffset() <= fieldEnd) {
                    ((List)fieldNameToFragInfos.get(field.name())).add(fragInfo);
                    continue block1;
                }
                if (fragInfo.getSubInfos().isEmpty()) continue block1;
                FieldPhraseList.WeightedPhraseInfo.Toffs firstToffs = fragInfo.getSubInfos().get(0).getTermsOffsets().get(0);
                if (fragInfo.getStartOffset() >= fieldEnd || firstToffs.getStartOffset() >= fieldEnd) continue;
                int fragStart = fieldStart;
                if (fragInfo.getStartOffset() > fieldStart && fragInfo.getStartOffset() < fieldEnd) {
                    fragStart = fragInfo.getStartOffset();
                }
                int fragEnd = fieldEnd;
                if (fragInfo.getEndOffset() > fieldStart && fragInfo.getEndOffset() < fieldEnd) {
                    fragEnd = fragInfo.getEndOffset();
                }
                ArrayList<FieldFragList.WeightedFragInfo.SubInfo> subInfos = new ArrayList<FieldFragList.WeightedFragInfo.SubInfo>();
                FieldFragList.WeightedFragInfo weightedFragInfo = new FieldFragList.WeightedFragInfo(fragStart, fragEnd, subInfos, fragInfo.getTotalBoost());
                Iterator<FieldFragList.WeightedFragInfo.SubInfo> subInfoIterator = fragInfo.getSubInfos().iterator();
                while (subInfoIterator.hasNext()) {
                    FieldFragList.WeightedFragInfo.SubInfo subInfo = subInfoIterator.next();
                    ArrayList<FieldPhraseList.WeightedPhraseInfo.Toffs> toffsList = new ArrayList<FieldPhraseList.WeightedPhraseInfo.Toffs>();
                    Iterator<FieldPhraseList.WeightedPhraseInfo.Toffs> toffsIterator = subInfo.getTermsOffsets().iterator();
                    while (toffsIterator.hasNext()) {
                        FieldPhraseList.WeightedPhraseInfo.Toffs toffs = toffsIterator.next();
                        if (toffs.getStartOffset() < fieldStart || toffs.getEndOffset() > fieldEnd) continue;
                        toffsList.add(toffs);
                        toffsIterator.remove();
                    }
                    if (!toffsList.isEmpty()) {
                        subInfos.add(new FieldFragList.WeightedFragInfo.SubInfo(subInfo.getText(), toffsList, subInfo.getSeqnum()));
                    }
                    if (!subInfo.getTermsOffsets().isEmpty()) continue;
                    subInfoIterator.remove();
                }
                ((List)fieldNameToFragInfos.get(field.name())).add(weightedFragInfo);
            }
        }
        ArrayList<FieldFragList.WeightedFragInfo> result = new ArrayList<FieldFragList.WeightedFragInfo>();
        for (List weightedFragInfos : fieldNameToFragInfos.values()) {
            result.addAll(weightedFragInfos);
        }
        Collections.sort(result, new Comparator<FieldFragList.WeightedFragInfo>(){

            @Override
            public int compare(FieldFragList.WeightedFragInfo info1, FieldFragList.WeightedFragInfo info2) {
                return info1.getStartOffset() - info2.getStartOffset();
            }
        });
        return result;
    }

    public void setMultiValuedSeparator(char separator) {
        this.multiValuedSeparator = separator;
    }

    public char getMultiValuedSeparator() {
        return this.multiValuedSeparator;
    }

    public boolean isDiscreteMultiValueHighlighting() {
        return this.discreteMultiValueHighlighting;
    }

    public void setDiscreteMultiValueHighlighting(boolean discreteMultiValueHighlighting) {
        this.discreteMultiValueHighlighting = discreteMultiValueHighlighting;
    }

    protected String getPreTag(int num) {
        return this.getPreTag(this.preTags, num);
    }

    protected String getPostTag(int num) {
        return this.getPostTag(this.postTags, num);
    }

    protected String getPreTag(String[] preTags, int num) {
        int n = num % preTags.length;
        return preTags[n];
    }

    protected String getPostTag(String[] postTags, int num) {
        int n = num % postTags.length;
        return postTags[n];
    }
}

