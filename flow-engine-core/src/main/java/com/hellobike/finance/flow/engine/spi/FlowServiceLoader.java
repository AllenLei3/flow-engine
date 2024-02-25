package com.hellobike.finance.flow.engine.spi;

import com.hellobike.finance.flow.engine.spi.instance.InstanceFactory;
import com.hellobike.finance.flow.engine.utils.ClassLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.StreamSupport;

/**
 * 自定义扩展类加载器
 *
 * <br>
 *     1. 扩展类的实例通过{@link InstanceFactory}创建, 默认采用反射
 *     2. 懒加载
 *     3. 线程安全
 * </br>
 *
 * @see SPI
 * @author 徐磊080
 */
public class FlowServiceLoader<T> {

    private static final Logger LOG = LoggerFactory.getLogger(FlowServiceLoader.class);

    private static final ConcurrentMap<Class<?>, FlowServiceLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>(8);
    private static final ConcurrentMap<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>(64);
    private static final SpiLoadStrategy[] strategies = loadLoadingStrategies();
    private static final InstanceFactory INSTANCE_FACTORY = FlowServiceLoader.getLoader(InstanceFactory.class).getExtension();

    /**
     * 扩展类实例缓存
     */
    private final ConcurrentMap<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();

    /**
     * 扩展类Class缓存
     */
    private final Holder<Map<String, Class<T>>> cachedClasses = new Holder<>();

    /**
     * 扩展的默认实现名称
     */
    private final String cachedDefaultName;

    /**
     * 自定义扩展类型
     */
    private final Class<?> type;

    private FlowServiceLoader(Class<?> type) {
        this.type = type;
        this.cachedDefaultName = type.getAnnotation(SPI.class).value();
    }

    /**
     * 提供静态构造方法, 用于获取指定{@code Class}的扩展类加载器
     *
     * @param type 扩展类
     */
    @SuppressWarnings("unchecked")
    public static <T> FlowServiceLoader<T> getLoader(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type == null");
        }
        if (!type.isAnnotationPresent(SPI.class)) {
            throw new IllegalArgumentException("Extension type (" + type + ") is not an extension, " +
                    "because it is NOT annotated with @SPI!");
        }
        FlowServiceLoader<T> flowServiceLoader = (FlowServiceLoader<T>) EXTENSION_LOADERS.get(type);
        if (flowServiceLoader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new FlowServiceLoader<T>(type));
            flowServiceLoader = (FlowServiceLoader<T>) EXTENSION_LOADERS.get(type);
        }
        return flowServiceLoader;
    }

    /**
     * 加载指定名称的扩展类实例. 当对应名称的实例加载不到时返回null
     *
     * @param name SPI name
     */
    @SuppressWarnings("unchecked")
    public T getExtension(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Service Extension name is NULL!");
        }
        final Holder<Object> holder = getOrCreateHolder(name);
        Object instance = holder.get();
        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }

    /**
     * 获取默认的扩展类实例
     */
    public T getExtension() {
        getExtensionClasses();
        if (cachedDefaultName == null || cachedDefaultName.isEmpty()) {
            return null;
        }
        return getExtension(cachedDefaultName);
    }

    /**
     * 获取默认的扩展类
     */
    public Class<T> getExtensionClz() {
        Map<String, Class<T>> extensionClz = getExtensionClasses();
        if (cachedDefaultName == null || cachedDefaultName.isEmpty()) {
            return null;
        }
        return extensionClz.get(cachedDefaultName);
    }

    /**
     * 获取该类型下所有的扩展类实例
     */
    public Map<String, T> getExtensionList() {
        Map<String, Class<T>> classMap = getExtensionClasses();
        Map<String, T> instance = new HashMap<>();
        classMap.forEach((key, clz) -> instance.put(key, getExtension(key)));
        return instance;
    }

    private Holder<Object> getOrCreateHolder(String name) {
        Holder<Object> holder = cachedInstances.get(name);
        if (holder == null) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }
        return holder;
    }

    /**
     * 根据自定义扩展名称实例化指定扩展实例
     *
     * @param name 自定义扩展名称
     * @return 自定义扩展实例
     */
    @SuppressWarnings("unchecked")
    private T createExtension(String name) {
        Class<T> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            throw new IllegalStateException("No such extension [" + type + "] by name " + name);
        }
        try {
            T instance = (T) EXTENSION_INSTANCES.get(clazz);
            if (instance == null) {
                if (INSTANCE_FACTORY == null) {
                    EXTENSION_INSTANCES.putIfAbsent(clazz, clazz.newInstance());
                } else {
                    EXTENSION_INSTANCES.putIfAbsent(clazz, INSTANCE_FACTORY.getInstance(clazz));
                }
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            }
            return instance;
        } catch (Throwable t) {
            throw new IllegalStateException("Extension instance (name: " + name + ", class: " +
                    type + ") couldn't be instantiated: " + t.getMessage(), t);
        }
    }

    /**
     * 通过JDK提供的SPI机制获取{@code LoadingStrategy}接口的所有实现类
     */
    private static SpiLoadStrategy[] loadLoadingStrategies() {
        return StreamSupport.stream(ServiceLoader.load(SpiLoadStrategy.class).spliterator(), false)
                .sorted()
                .toArray(SpiLoadStrategy[]::new);
    }

    /**
     * 一次性加载SPI目录下所有的自定义扩展类型
     */
    public Map<String, Class<T>> getExtensionClasses() {
        Map<String, Class<T>> classes = cachedClasses.get();
        if (classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if (classes == null) {
                    classes = loadExtensionClasses();
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    private Map<String, Class<T>> loadExtensionClasses() {
        Map<String, Class<T>> extensionClasses = new HashMap<>();
        for (SpiLoadStrategy strategy : strategies) {
            loadDirectory(extensionClasses, strategy.directory(), strategy.overridden(), strategy.excludedPackages());
        }
        return extensionClasses;
    }

    private void loadDirectory(Map<String, Class<T>> extensionClasses, String dir,
                               boolean overridden, String... excludedPackages) {
        String fileName = dir + type.getName();
        try {
            Enumeration<URL> urls;
            ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
            if (classLoader != null) {
                urls = classLoader.getResources(fileName);
            } else {
                urls = ClassLoader.getSystemResources(fileName);
            }
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL resourceURL = urls.nextElement();
                    loadResource(extensionClasses, classLoader, resourceURL, overridden, excludedPackages);
                }
            }
        } catch (Throwable t) {
            LOG.error("Exception occurred when loading extension " + type +
                    ", description file: " + fileName + "", t);
        }
    }

    private void loadResource(Map<String, Class<T>> extensionClasses, ClassLoader classLoader,
                              URL resourceURL, boolean overridden, String... excludedPackages) {
        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceURL.openStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    final int ci = line.indexOf('#');
                    if (ci >= 0) {
                        line = line.substring(0, ci);
                    }
                    line = line.trim();
                    if (!line.isEmpty()) {
                        try {
                            String name = null;
                            int i = line.indexOf('=');
                            if (i > 0) {
                                name = line.substring(0, i).trim();
                                line = line.substring(i + 1).trim();
                            }
                            if (!line.isEmpty() && !isExcluded(line, excludedPackages)) {
                                Class<T> clazz = (Class<T>) Class.forName(line, true, classLoader);
                                loadClass(extensionClasses, resourceURL, clazz, name, overridden);
                            }
                        } catch (Throwable t) {
                            throw new IllegalStateException("Failed to load extension class " + type + ", " +
                                    "class line: " + line + " in " + resourceURL + ", cause: " + t.getMessage(), t);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            LOG.error("Exception occurred when loading extension class " +
                    type + ", class file: " + resourceURL + " in " + resourceURL, t);
        }
    }

    private boolean isExcluded(String className, String... excludedPackages) {
        if (excludedPackages != null) {
            for (String excludePackage : excludedPackages) {
                if (className.startsWith(excludePackage + ".")) {
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("all")
    private void loadClass(Map<String, Class<T>> extensionClasses, URL resourceURL, Class<T> clazz, String name,
                           boolean overridden) throws NoSuchMethodException {
        if (!type.isAssignableFrom(clazz)) {
            throw new IllegalStateException("Error occurred when loading extension class " +
                    type + ", class line: " + clazz.getName() + ", class "
                    + clazz.getName() + " is not subtype of interface.");
        }
        clazz.getConstructor();
        if (name == null || name.isEmpty()) {
            throw new IllegalStateException("No such extension name for the " + clazz + " in the config " + resourceURL);
        }
        Class<T> c = extensionClasses.get(name);
        if (c == null || overridden) {
            // 如果可以覆盖,则后加载的覆盖先加载的
            extensionClasses.put(name, clazz);
        } else if (c != clazz) {
            String duplicateMsg = "Duplicate extension [" + type + "], name " + name + " on " + c.getName() + " and " + clazz.getName();
            LOG.error(duplicateMsg);
            throw new IllegalStateException(duplicateMsg);
        }
    }

    private static class Holder<T> {

        private volatile T value;

        public void set(T value) {
            this.value = value;
        }

        public T get() {
            return value;
        }
    }

    /**
     * 获取类加载器,优先从线程上下文中获取
     */
    private static ClassLoader getDefaultClassLoader() {
        ClassLoader classLoader = null;
        try {
            classLoader = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // falling back
        }
        if (classLoader == null) {
            // No thread context class loader -> use class loader of this class.
            classLoader = FlowServiceLoader.class.getClassLoader();
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
