package com.hellobike.finance.flow.engine.utils;

/**
 * @author 徐磊080
 */
public class ClassLoaderUtils {

    /**
     * 获取类加载器
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader classLoader = null;
        try {
            classLoader = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // falling back
        }
        if (classLoader == null) {
            // No thread context class loader -> use class loader of this class.
            classLoader = ClassLoaderUtils.class.getClassLoader();
            if (classLoader == null) {
                try {
                    classLoader = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // return null
                }
            }
        }
        return classLoader;
    }
}
