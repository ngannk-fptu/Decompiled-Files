/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tika.sax.StandardOrganizations;
import org.apache.tika.sax.StandardReference;

public class StandardsText {
    private static final String REGEX_HEADER = "(\\d{1,10}+\\.(\\d{1,10}+\\.?){0,10}+)\\p{Blank}+([A-Z]{1,64}+(\\s[A-Z]{1,64}+){0,256}+){5,10}+";
    private static final String REGEX_APPLICABLE_DOCUMENTS = "(?i:.*APPLICABLE\\sDOCUMENTS|REFERENCE|STANDARD|REQUIREMENT|GUIDELINE|COMPLIANCE.*)";
    private static final String REGEX_IDENTIFIER = "(?<identifier>([0-9]{3,64}+|([A-Z]{1,64}+(-|_|\\.)?[0-9]{2,64}+))((-|_|\\.)?[A-Z0-9]{1,64}+){0,64}+)";
    private static final String REGEX_ORGANIZATION = StandardOrganizations.getOrganzationsRegex();
    private static final String REGEX_STANDARD_TYPE = "(\\s(?i:Publication|Standard))";
    private static final String REGEX_FALLBACK = "\\(?(?<mainOrganization>[A-Z]\\w{1,64}+)\\)?((\\s?(?<separator>\\/)\\s?)(\\w{1,64}+\\s)*\\(?(?<secondOrganization>[A-Z]\\w{1,64}+)\\)?)?(\\s(?i:Publication|Standard))?(-|\\s)?(?<identifier>([0-9]{3,64}+|([A-Z]{1,64}+(-|_|\\.)?[0-9]{2,64}+))((-|_|\\.)?[A-Z0-9]{1,64}+){0,64}+)";
    private static final String REGEX_STANDARD = ".*" + REGEX_ORGANIZATION + ".+" + REGEX_ORGANIZATION + "?.*";

    public static ArrayList<StandardReference> extractStandardReferences(String text, double threshold) {
        Map<Integer, String> headers = StandardsText.findHeaders(text);
        return StandardsText.findStandards(text, headers, threshold);
    }

    private static Map<Integer, String> findHeaders(String text) {
        TreeMap<Integer, String> headers = new TreeMap<Integer, String>();
        Pattern pattern = Pattern.compile(REGEX_HEADER);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            headers.put(matcher.start(), matcher.group());
        }
        return headers;
    }

    private static ArrayList<StandardReference> findStandards(String text, Map<Integer, String> headers, double threshold) {
        ArrayList<StandardReference> standards = new ArrayList<StandardReference>();
        double score = 0.0;
        Pattern pattern = Pattern.compile(REGEX_FALLBACK);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            StandardReference.StandardReferenceBuilder builder = new StandardReference.StandardReferenceBuilder(matcher.group("mainOrganization"), matcher.group("identifier")).setSecondOrganization(matcher.group("separator"), matcher.group("secondOrganization"));
            score = 0.25;
            if (matcher.group().matches(REGEX_STANDARD)) {
                score += 0.25;
            }
            if (matcher.group().matches(".*(\\s(?i:Publication|Standard)).*")) {
                score += 0.25;
            }
            int startHeader = 0;
            int endHeader = 0;
            boolean headerFound = false;
            Iterator<Map.Entry<Integer, String>> iterator = headers.entrySet().iterator();
            while (iterator.hasNext() && !headerFound) {
                startHeader = endHeader;
                endHeader = iterator.next().getKey();
                if (endHeader <= matcher.start()) continue;
                headerFound = true;
            }
            String header = headers.get(startHeader);
            if (header != null && headers.get(startHeader).matches(REGEX_APPLICABLE_DOCUMENTS)) {
                score += 0.25;
            }
            builder.setScore(score);
            if (!(score >= threshold)) continue;
            standards.add(builder.build());
        }
        return standards;
    }
}

