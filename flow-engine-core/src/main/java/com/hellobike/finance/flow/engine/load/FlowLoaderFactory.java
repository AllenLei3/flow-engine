package com.hellobike.finance.flow.engine.load;

import com.hellobike.finance.flow.engine.utils.AssertUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author xulei
 */
public class FlowLoaderFactory {

    private static final Map<String, FlowLoader> LOADER_CACHE = new HashMap<>();

    static {
        ServiceLoader.load(FlowLoader.class)
                .forEach(loader -> LOADER_CACHE.put(loader.supportFileSuffix(), loader));
    }

    /**
     * 根据文件名称获取对应加载器
     *
     * @param fileName 流程定义文件名称
     * @return 流程加载器
     */
    public static FlowLoader getFlowLoader(String fileName) {
        AssertUtils.notNull(fileName == null, "fileName must not be null");
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            String suffix = fileName.substring(i + 1);
            FlowLoader loader = LOADER_CACHE.get(suffix.toLowerCase());
            if (loader != null) {
                return loader;
            }
        }
        throw new IllegalArgumentException("不支持的文件格式! fileName:" + fileName);
    }
}
