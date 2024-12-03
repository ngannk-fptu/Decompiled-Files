/*
 * Decompiled with CFR 0.152.
 */
package freemarker.cache;

import freemarker.template.MalformedTemplateNameException;
import freemarker.template.utility.StringUtil;

public abstract class TemplateNameFormat {
    public static final TemplateNameFormat DEFAULT_2_3_0 = new Default020300();
    public static final TemplateNameFormat DEFAULT_2_4_0 = new Default020400();

    private TemplateNameFormat() {
    }

    abstract String toRootBasedName(String var1, String var2) throws MalformedTemplateNameException;

    abstract String normalizeRootBasedName(String var1) throws MalformedTemplateNameException;

    abstract String rootBasedNameToAbsoluteName(String var1) throws MalformedTemplateNameException;

    private static void checkNameHasNoNullCharacter(String name) throws MalformedTemplateNameException {
        if (name.indexOf(0) != -1) {
            throw new MalformedTemplateNameException(name, "Null character (\\u0000) in the name; possible attack attempt");
        }
    }

    private static MalformedTemplateNameException newRootLeavingException(String name) {
        return new MalformedTemplateNameException(name, "Backing out from the root directory is not allowed");
    }

    private static final class Default020400
    extends TemplateNameFormat {
        private Default020400() {
        }

        @Override
        String toRootBasedName(String baseName, String targetName) {
            if (this.findSchemeSectionEnd(targetName) != 0) {
                return targetName;
            }
            if (targetName.startsWith("/")) {
                String targetNameAsRelative = targetName.substring(1);
                int schemeSectionEnd = this.findSchemeSectionEnd(baseName);
                if (schemeSectionEnd == 0) {
                    return targetNameAsRelative;
                }
                return baseName.substring(0, schemeSectionEnd) + targetNameAsRelative;
            }
            if (!baseName.endsWith("/")) {
                int baseEnd = baseName.lastIndexOf("/") + 1;
                if (baseEnd == 0) {
                    baseEnd = this.findSchemeSectionEnd(baseName);
                }
                baseName = baseName.substring(0, baseEnd);
            }
            return baseName + targetName;
        }

        @Override
        String normalizeRootBasedName(String name) throws MalformedTemplateNameException {
            String path;
            String scheme;
            TemplateNameFormat.checkNameHasNoNullCharacter(name);
            if (name.indexOf(92) != -1) {
                throw new MalformedTemplateNameException(name, "Backslash (\"\\\") is not allowed in template names. Use slash (\"/\") instead.");
            }
            int schemeSectionEnd = this.findSchemeSectionEnd(name);
            if (schemeSectionEnd == 0) {
                scheme = null;
                path = name;
            } else {
                scheme = name.substring(0, schemeSectionEnd);
                path = name.substring(schemeSectionEnd);
            }
            if (path.indexOf(58) != -1) {
                throw new MalformedTemplateNameException(name, "The ':' character can only be used after the scheme name (if there's any), not in the path part");
            }
            path = this.removeRedundantSlashes(path);
            path = this.removeDotSteps(path);
            path = this.resolveDotDotSteps(path, name);
            path = this.removeRedundantStarSteps(path);
            return scheme == null ? path : scheme + path;
        }

        private int findSchemeSectionEnd(String name) {
            int schemeColonIdx = name.indexOf(":");
            if (schemeColonIdx == -1 || name.lastIndexOf(47, schemeColonIdx - 1) != -1) {
                return 0;
            }
            if (schemeColonIdx + 2 < name.length() && name.charAt(schemeColonIdx + 1) == '/' && name.charAt(schemeColonIdx + 2) == '/') {
                return schemeColonIdx + 3;
            }
            return schemeColonIdx + 1;
        }

        private String removeRedundantSlashes(String path) {
            String prevName;
            while ((prevName = path) != (path = StringUtil.replace(path, "//", "/"))) {
            }
            return path.startsWith("/") ? path.substring(1) : path;
        }

        private String removeDotSteps(String path) {
            int nextFromIdx = path.length() - 1;
            int dotIdx;
            while ((dotIdx = path.lastIndexOf(46, nextFromIdx)) >= 0) {
                boolean slashRight;
                nextFromIdx = dotIdx - 1;
                if (dotIdx != 0 && path.charAt(dotIdx - 1) != '/') continue;
                if (dotIdx + 1 == path.length()) {
                    slashRight = false;
                } else {
                    if (path.charAt(dotIdx + 1) != '/') continue;
                    slashRight = true;
                }
                if (slashRight) {
                    path = path.substring(0, dotIdx) + path.substring(dotIdx + 2);
                    continue;
                }
                path = path.substring(0, path.length() - 1);
            }
            return path;
        }

        private String resolveDotDotSteps(String path, String name) throws MalformedTemplateNameException {
            int nextFromIdx = 0;
            int dotDotIdx;
            while ((dotDotIdx = path.indexOf("..", nextFromIdx)) >= 0) {
                int previousSlashIdx;
                boolean slashRight;
                if (dotDotIdx == 0) {
                    throw TemplateNameFormat.newRootLeavingException(name);
                }
                if (path.charAt(dotDotIdx - 1) != '/') {
                    nextFromIdx = dotDotIdx + 3;
                    continue;
                }
                if (dotDotIdx + 2 == path.length()) {
                    slashRight = false;
                } else if (path.charAt(dotDotIdx + 2) == '/') {
                    slashRight = true;
                } else {
                    nextFromIdx = dotDotIdx + 3;
                    continue;
                }
                boolean skippedStarStep = false;
                int searchSlashBacwardsFrom = dotDotIdx - 2;
                while (true) {
                    if (searchSlashBacwardsFrom == -1) {
                        throw TemplateNameFormat.newRootLeavingException(name);
                    }
                    previousSlashIdx = path.lastIndexOf(47, searchSlashBacwardsFrom);
                    if (previousSlashIdx == -1) {
                        if (searchSlashBacwardsFrom != 0 || path.charAt(0) != '*') break;
                        throw TemplateNameFormat.newRootLeavingException(name);
                    }
                    if (path.charAt(previousSlashIdx + 1) != '*' || path.charAt(previousSlashIdx + 2) != '/') break;
                    skippedStarStep = true;
                    searchSlashBacwardsFrom = previousSlashIdx - 1;
                }
                path = path.substring(0, previousSlashIdx + 1) + (skippedStarStep ? "*/" : "") + path.substring(dotDotIdx + (slashRight ? 3 : 2));
                nextFromIdx = previousSlashIdx + 1;
            }
            return path;
        }

        private String removeRedundantStarSteps(String path) {
            int supiciousIdx;
            while ((supiciousIdx = path.indexOf("*/*")) != -1) {
                String prevName = path;
                if (!(supiciousIdx != 0 && path.charAt(supiciousIdx - 1) != '/' || supiciousIdx + 3 != path.length() && path.charAt(supiciousIdx + 3) != '/')) {
                    path = path.substring(0, supiciousIdx) + path.substring(supiciousIdx + 2);
                }
                if (prevName != path) continue;
            }
            if (path.startsWith("*")) {
                if (path.length() == 1) {
                    path = "";
                } else if (path.charAt(1) == '/') {
                    path = path.substring(2);
                }
            }
            return path;
        }

        @Override
        String rootBasedNameToAbsoluteName(String name) throws MalformedTemplateNameException {
            if (this.findSchemeSectionEnd(name) != 0) {
                return name;
            }
            if (!name.startsWith("/")) {
                return "/" + name;
            }
            return name;
        }

        public String toString() {
            return "TemplateNameFormat.DEFAULT_2_4_0";
        }
    }

    private static final class Default020300
    extends TemplateNameFormat {
        private Default020300() {
        }

        @Override
        String toRootBasedName(String baseName, String targetName) {
            if (targetName.indexOf("://") > 0) {
                return targetName;
            }
            if (targetName.startsWith("/")) {
                int schemeSepIdx = baseName.indexOf("://");
                if (schemeSepIdx > 0) {
                    return baseName.substring(0, schemeSepIdx + 2) + targetName;
                }
                return targetName.substring(1);
            }
            if (!baseName.endsWith("/")) {
                baseName = baseName.substring(0, baseName.lastIndexOf("/") + 1);
            }
            return baseName + targetName;
        }

        @Override
        String normalizeRootBasedName(String name) throws MalformedTemplateNameException {
            TemplateNameFormat.checkNameHasNoNullCharacter(name);
            String path = name;
            while (true) {
                int parentDirPathLoc;
                if ((parentDirPathLoc = path.indexOf("/../")) == 0) {
                    throw TemplateNameFormat.newRootLeavingException(name);
                }
                if (parentDirPathLoc == -1) {
                    if (!path.startsWith("../")) break;
                    throw TemplateNameFormat.newRootLeavingException(name);
                }
                int previousSlashLoc = path.lastIndexOf(47, parentDirPathLoc - 1);
                path = path.substring(0, previousSlashLoc + 1) + path.substring(parentDirPathLoc + "/../".length());
            }
            while (true) {
                int currentDirPathLoc;
                if ((currentDirPathLoc = path.indexOf("/./")) == -1) {
                    if (!path.startsWith("./")) break;
                    path = path.substring("./".length());
                    break;
                }
                path = path.substring(0, currentDirPathLoc) + path.substring(currentDirPathLoc + "/./".length() - 1);
            }
            if (path.length() > 1 && path.charAt(0) == '/') {
                path = path.substring(1);
            }
            return path;
        }

        @Override
        String rootBasedNameToAbsoluteName(String name) throws MalformedTemplateNameException {
            if (name.indexOf("://") > 0) {
                return name;
            }
            if (!name.startsWith("/")) {
                return "/" + name;
            }
            return name;
        }

        public String toString() {
            return "TemplateNameFormat.DEFAULT_2_3_0";
        }
    }
}

