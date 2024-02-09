package com.hellobike.finance.flow.engine.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 徐磊080
 */
public class AntPathMatcher {

    private static final int CACHE_TURNOFF_THRESHOLD = 65536;
    private static final char[] WILDCARD_CHARS = { '*', '?' };
    private static final String PATH_SEPARATOR = "/";

    /**
     * 缓存匹配模式
     */
    private volatile Boolean cachePatterns = true;
    private final Map<String, String[]> tokenizedPatternCache = new ConcurrentHashMap<>(256);
    private final Map<String, AntPathStringMatcher> stringMatcherCache = new ConcurrentHashMap<>(256);

    /**
     * 判断给定的{@code path}是否是可以进行模式匹配的路径
     *
     * @param path 匹配路径
     * @return {@code true} if the given {@code path} represents a pattern
     */
    public boolean isPattern(String path) {
        return (path.indexOf('*') != -1 || path.indexOf('?') != -1);
    }

    /**
     * 将给定的{@code path}与给定的{@code pattern}进行匹配
     *
     * @param pattern 匹配模式
     * @param path 匹配路径
     * @return 匹配返回true
     */
    public boolean match(String pattern, String path) {
        return doMatch(pattern, path, true);
    }

    /**
     * 将给定的{@code path}与给定的{@code pattern}进行前缀匹配
     *
     * @param pattern 匹配模式
     * @param path 匹配路径
     * @return 匹配返回true
     */
    public boolean matchStart(String pattern, String path) {
        return doMatch(pattern, path, false);
    }

    private boolean doMatch(String pattern, String path, boolean fullMatch) {
        if (path.startsWith(PATH_SEPARATOR) != pattern.startsWith(PATH_SEPARATOR)) {
            return false;
        }
        String[] pattDirs = tokenizePattern(pattern);
        if (fullMatch && !isPotentialMatch(path, pattDirs)) {
            return false;
        }
        String[] pathDirs = StringUtils.tokenizeToStringArray(path, PATH_SEPARATOR, false, true);
        int pattIdxStart = 0;
        int pattIdxEnd = pattDirs.length - 1;
        int pathIdxStart = 0;
        int pathIdxEnd = pathDirs.length - 1;

        // Match all elements up to the first **
        while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            String pattDir = pattDirs[pattIdxStart];
            if ("**".equals(pattDir)) {
                break;
            }
            if (!matchStrings(pattDir, pathDirs[pathIdxStart])) {
                return false;
            }
            pattIdxStart++;
            pathIdxStart++;
        }

        if (pathIdxStart > pathIdxEnd) {
            // Path is exhausted, only match if rest of pattern is * or **'s
            if (pattIdxStart > pattIdxEnd) {
                return (pattern.endsWith(PATH_SEPARATOR) == path.endsWith(PATH_SEPARATOR));
            }
            if (!fullMatch) {
                return true;
            }
            if (pattIdxStart == pattIdxEnd && pattDirs[pattIdxStart].equals("*") && path.endsWith(PATH_SEPARATOR)) {
                return true;
            }
            for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
                if (!pattDirs[i].equals("**")) {
                    return false;
                }
            }
            return true;
        } else if (pattIdxStart > pattIdxEnd) {
            // String not exhausted, but pattern is. Failure.
            return false;
        } else if (!fullMatch && "**".equals(pattDirs[pattIdxStart])) {
            // Path start definitely matches due to "**" part in pattern.
            return true;
        }

        // up to last '**'
        while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            String pattDir = pattDirs[pattIdxEnd];
            if (pattDir.equals("**")) {
                break;
            }
            if (!matchStrings(pattDir, pathDirs[pathIdxEnd])) {
                return false;
            }
            pattIdxEnd--;
            pathIdxEnd--;
        }
        if (pathIdxStart > pathIdxEnd) {
            // String is exhausted
            for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
                if (!pattDirs[i].equals("**")) {
                    return false;
                }
            }
            return true;
        }

        while (pattIdxStart != pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            int patIdxTmp = -1;
            for (int i = pattIdxStart + 1; i <= pattIdxEnd; i++) {
                if (pattDirs[i].equals("**")) {
                    patIdxTmp = i;
                    break;
                }
            }
            if (patIdxTmp == pattIdxStart + 1) {
                // '**/**' situation, so skip one
                pattIdxStart++;
                continue;
            }
            // Find the pattern between padIdxStart & padIdxTmp in str between
            // strIdxStart & strIdxEnd
            int patLength = (patIdxTmp - pattIdxStart - 1);
            int strLength = (pathIdxEnd - pathIdxStart + 1);
            int foundIdx = -1;

            strLoop:
            for (int i = 0; i <= strLength - patLength; i++) {
                for (int j = 0; j < patLength; j++) {
                    String subPat = pattDirs[pattIdxStart + j + 1];
                    String subStr = pathDirs[pathIdxStart + i + j];
                    if (!matchStrings(subPat, subStr)) {
                        continue strLoop;
                    }
                }
                foundIdx = pathIdxStart + i;
                break;
            }

            if (foundIdx == -1) {
                return false;
            }
            pattIdxStart = patIdxTmp;
            pathIdxStart = foundIdx + patLength;
        }

        for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
            if (!pattDirs[i].equals("**")) {
                return false;
            }
        }
        return true;
    }

    private boolean isPotentialMatch(String path, String[] pattDirs) {
        int pos = 0;
        for (String pattDir : pattDirs) {
            int skipped = skipSeparator(path, pos);
            pos += skipped;
            skipped = skipSegment(path, pos, pattDir);
            if (skipped < pattDir.length()) {
                return (skipped > 0 || (!pattDir.isEmpty() && isWildcardChar(pattDir.charAt(0))));
            }
            pos += skipped;
        }
        return true;
    }

    private int skipSeparator(String path, int pos) {
        int skipped = 0;
        while (path.startsWith(PATH_SEPARATOR, pos + skipped)) {
            skipped += PATH_SEPARATOR.length();
        }
        return skipped;
    }

    private int skipSegment(String path, int pos, String prefix) {
        int skipped = 0;
        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            if (isWildcardChar(c)) {
                return skipped;
            }
            int currPos = pos + skipped;
            if (currPos >= path.length()) {
                return 0;
            }
            if (c == path.charAt(currPos)) {
                skipped++;
            }
        }
        return skipped;
    }

    private boolean isWildcardChar(char c) {
        for (char candidate : WILDCARD_CHARS) {
            if (c == candidate) {
                return true;
            }
        }
        return false;
    }

    private String[] tokenizePattern(String pattern) {
        String[] tokenized = null;
        Boolean cachePatterns = this.cachePatterns;
        if (cachePatterns == null || cachePatterns) {
            tokenized = this.tokenizedPatternCache.get(pattern);
        }
        if (tokenized == null) {
            tokenized = StringUtils.tokenizeToStringArray(pattern, PATH_SEPARATOR, false, true);
            if (cachePatterns == null && this.tokenizedPatternCache.size() >= CACHE_TURNOFF_THRESHOLD) {
                // Try to adapt to the runtime situation that we're encountering:
                // There are obviously too many different patterns coming in here...
                // So let's turn off the cache since the patterns are unlikely to be reoccurring.
                deactivatePatternCache();
                return tokenized;
            }
            if (cachePatterns == null || cachePatterns) {
                this.tokenizedPatternCache.put(pattern, tokenized);
            }
        }
        return tokenized;
    }

    private boolean matchStrings(String pattern, String str) {
        return getStringMatcher(pattern).matchStrings(str);
    }

    private AntPathStringMatcher getStringMatcher(String pattern) {
        AntPathStringMatcher matcher = null;
        Boolean cachePatterns = this.cachePatterns;
        if (cachePatterns == null || cachePatterns) {
            matcher = this.stringMatcherCache.get(pattern);
        }
        if (matcher == null) {
            matcher = new AntPathStringMatcher(pattern);
            if (cachePatterns == null && this.stringMatcherCache.size() >= CACHE_TURNOFF_THRESHOLD) {
                // Try to adapt to the runtime situation that we're encountering:
                // There are obviously too many different patterns coming in here...
                // So let's turn off the cache since the patterns are unlikely to be reoccurring.
                deactivatePatternCache();
                return matcher;
            }
            if (cachePatterns == null || cachePatterns) {
                this.stringMatcherCache.put(pattern, matcher);
            }
        }
        return matcher;
    }

    private void deactivatePatternCache() {
        this.cachePatterns = false;
        this.tokenizedPatternCache.clear();
        this.stringMatcherCache.clear();
    }

    /**
     * Tests whether or not a string matches against a pattern via a {@link Pattern}.
     * <p>The pattern may contain special characters: '*' means zero or more characters; '?' means one and
     * only one character.
     */
    private static class AntPathStringMatcher {

        private static final Pattern GLOB_PATTERN = Pattern.compile("\\?|\\*|\\{((?:\\{[^/]+?\\}|[^/{}]|\\\\[{}])+?)\\}");
        private final Pattern pattern;

        public AntPathStringMatcher(String pattern) {
            StringBuilder patternBuilder = new StringBuilder();
            Matcher matcher = GLOB_PATTERN.matcher(pattern);
            int end = 0;
            while (matcher.find()) {
                patternBuilder.append(quote(pattern, end, matcher.start()));
                String match = matcher.group();
                if ("?".equals(match)) {
                    patternBuilder.append('.');
                } else if ("*".equals(match)) {
                    patternBuilder.append(".*");
                }
                end = matcher.end();
            }
            patternBuilder.append(quote(pattern, end, pattern.length()));
            this.pattern = Pattern.compile(patternBuilder.toString());
        }

        private String quote(String s, int start, int end) {
            return start == end ? "" : Pattern.quote(s.substring(start, end));
        }

        public boolean matchStrings(String str) {
            Matcher matcher = this.pattern.matcher(str);
            return matcher.matches();
        }
    }
}
