/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template.utility;

import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Deprecated
public class ToCanonical {
    static Configuration config = Configuration.getDefaultConfiguration();

    @Deprecated
    public static void main(String[] args) {
        config.setWhitespaceStripping(false);
        if (args.length == 0) {
            ToCanonical.usage();
        }
        for (int i = 0; i < args.length; ++i) {
            File f = new File(args[i]);
            if (!f.exists()) {
                System.err.println("File " + f + " doesn't exist.");
            }
            try {
                ToCanonical.convertFile(f);
                continue;
            }
            catch (Exception e) {
                System.err.println("Error converting file: " + f);
                e.printStackTrace();
            }
        }
    }

    static void convertFile(File f) throws IOException {
        File fullPath = f.getAbsoluteFile();
        File dir = fullPath.getParentFile();
        String filename = fullPath.getName();
        File convertedFile = new File(dir, filename + ".canonical");
        config.setDirectoryForTemplateLoading(dir);
        Template template = config.getTemplate(filename);
        try (FileWriter output = new FileWriter(convertedFile);){
            template.dump(output);
        }
    }

    static void usage() {
        System.err.println("Usage: java freemarker.template.utility.ToCanonical <filename(s)>");
    }
}

