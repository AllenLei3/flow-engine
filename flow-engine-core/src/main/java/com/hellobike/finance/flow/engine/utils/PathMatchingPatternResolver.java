package com.hellobike.finance.flow.engine.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author xulei
 */
public class PathMatchingPatternResolver {

    private final ClassLoader classLoader = getDefaultClassLoader();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 根据classpath路径获取匹配的文件
     *
     * @param classpath 模式路径
     * @return 匹配的文件数组
     */
    public File[] getMatchFiles(String classpath) throws FileNotFoundException {
        if (classpath == null) {
            throw new IllegalArgumentException("classpath must not be null");
        }
        if (pathMatcher.isPattern(classpath)) {
            // 模式匹配
            return getPathMatchingFiles(classpath);
        } else {
            // 单个文件直接查找
            return new File[] {getFileByClasspath(classpath)};
        }
    }

    /**
     * 根据classpath路径获取模式匹配的文件列表
     *
     * @param classpath 模式匹配路径
     * @return 匹配的文件数组
     */
    public File[] getPathMatchingFiles(String classpath) throws FileNotFoundException {
        if (classpath == null) {
            throw new IllegalArgumentException("classpath must not be null");
        }
        Set<File> result = new LinkedHashSet<>(16);
        String rootDirPath = determineRootDir(classpath);
        String subPattern = classpath.substring(rootDirPath.length());
        URL rootDirUrl = classLoader.getResource(rootDirPath);
        // 读取匹配文件
        if (rootDirUrl != null && rootDirUrl.getProtocol().startsWith("file")) {
            File rootDirFile = getFileByClasspath(rootDirPath);
            result.addAll(doFindPathMatchingFiles(rootDirFile, subPattern));
        }
        return result.toArray(new File[0]);
    }

    private String determineRootDir(String location) {
        int prefixEnd = location.indexOf(':') + 1;
        int rootDirEnd = location.length();
        while (rootDirEnd > prefixEnd && pathMatcher.isPattern(location.substring(prefixEnd, rootDirEnd))) {
            rootDirEnd = location.lastIndexOf('/', rootDirEnd - 2) + 1;
        }
        if (rootDirEnd == 0) {
            rootDirEnd = prefixEnd;
        }
        return location.substring(0, rootDirEnd);
    }

    private File getFileByClasspath(String path) throws FileNotFoundException {
        URL resourceUrl = classLoader.getResource(path);
        if (resourceUrl == null) {
            throw new FileNotFoundException("path cannot be resolved to URL because it does not exist");
        }
        try {
            return new File(new URI(resourceUrl.toString()).getSchemeSpecificPart());
        } catch (URISyntaxException ex) {
            return new File(resourceUrl.getFile());
        }
    }

    private Set<File> doFindPathMatchingFiles(File rootDirResource, String subPattern) {
        File rootDir;
        try {
            rootDir = rootDirResource.getAbsoluteFile();
        } catch (Exception ex) {
            return Collections.emptySet();
        }
        return retrieveMatchingFiles(rootDir, subPattern);
    }

    private Set<File> retrieveMatchingFiles(File rootDir, String pattern) {
        if (!rootDir.exists() || !rootDir.isDirectory() || !rootDir.canRead()) {
            return Collections.emptySet();
        }
        String fullPattern = StringUtils.replace(rootDir.getAbsolutePath(), File.separator, "/");
        if (!pattern.startsWith("/")) {
            fullPattern += "/";
        }
        fullPattern = fullPattern + StringUtils.replace(pattern, File.separator, "/");
        Set<File> result = new LinkedHashSet<>(8);
        doRetrieveMatchingFiles(fullPattern, rootDir, result);
        return result;
    }

    private void doRetrieveMatchingFiles(String fullPattern, File dir, Set<File> result) {
        File[] dirContents = dir.listFiles();
        if (dirContents == null) {
            return;
        }
        Arrays.sort(dirContents);
        for (File content : dirContents) {
            String currPath = StringUtils.replace(content.getAbsolutePath(), File.separator, "/");
            if (content.isDirectory() && pathMatcher.matchStart(fullPattern, currPath + "/")) {
                if (content.canRead()) {
                    doRetrieveMatchingFiles(fullPattern, content, result);
                }
            }
            if (pathMatcher.match(fullPattern, currPath)) {
                result.add(content);
            }
        }
    }

    private static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = PathMatchingPatternResolver.class.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }
        return cl;
    }
}
