/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.uri;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class URIUtil {
    private static final Pattern DRIVE_LETTER_PATTERN = Pattern.compile("([a-zA-Z]):\\\\(.*)");

    public static URI resolve(URI baseURI, String reference) throws URISyntaxException {
        URI resolved;
        boolean emptyRef = reference.isEmpty();
        if (emptyRef) {
            resolved = new URI(baseURI.getScheme(), baseURI.getSchemeSpecificPart(), null);
        } else {
            URI refURI;
            Matcher driveLetterMatcher = DRIVE_LETTER_PATTERN.matcher(reference);
            if (driveLetterMatcher.matches()) {
                refURI = new File(reference).toURI();
            } else {
                reference = reference.replace('\\', '/');
                try {
                    refURI = new URI(reference);
                }
                catch (URISyntaxException e) {
                    refURI = new URI(null, reference, null);
                }
            }
            resolved = baseURI.resolve(refURI);
        }
        return resolved;
    }
}

