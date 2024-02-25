package com.hellobike.finance.flow.engine.spi.storage;

import com.hellobike.finance.flow.engine.exception.FlowExecuteException;
import com.hellobike.finance.flow.engine.execute.FlowContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 本地存储
 *
 * @author 徐磊080
 */
public class LocalFlowContextStorage implements FlowContextStorage {

    private static final String FILE_PATH = System.getProperty("user.home")
            + File.separator + ".flow" + File.separator;

    @Override
    public void saveContext(String flowName, String suspendId, FlowContext context) {
        try {
            Path rootPath = Paths.get(FILE_PATH);
            if (!Files.exists(rootPath)) {
                Files.createDirectory(rootPath);
            }
            Path filePath = Paths.get(FILE_PATH + buildFileName(flowName, suspendId));
            Files.deleteIfExists(filePath);
            Files.createFile(filePath);
            // 序列化
            byte[] contextBytes;
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
                objectOutputStream.writeObject(context);
                contextBytes = byteArrayOutputStream.toByteArray();
            }
            // 写文件
            Files.write(filePath, contextBytes);
        } catch (Exception e) {
            throw new FlowExecuteException(e);
        }
    }

    @Override
    public FlowContext restoreContext(String flowName, String suspendId) {
        try {
            Path filePath = Paths.get(FILE_PATH + buildFileName(flowName, suspendId));
            if (!Files.exists(filePath)) {
                throw new FileNotFoundException("file [" + filePath + "] not exist!");
            }
            byte[] bytes = Files.readAllBytes(filePath);
            Object object;
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                 ObjectInputStream objectOutputStream = new ObjectInputStream(byteArrayInputStream)) {
                object = objectOutputStream.readObject();
            }
            if (!(object instanceof FlowContext)) {
                throw new FlowExecuteException("file context [" + object.getClass().getName() + "] is not instanceof FlowContext!");
            }
            return (FlowContext) object;
        } catch (Exception e) {
            throw new FlowExecuteException(e);
        }
    }

    @Override
    public void removeContext(String flowName, String suspendId) {
        try {
            Path path = Paths.get(FILE_PATH + buildFileName(flowName, suspendId));
            Files.deleteIfExists(path);
        } catch (Exception e) {
            throw new FlowExecuteException(e);
        }
    }

    private String buildFileName(String flowName, String suspendId) {
        return flowName + "_" + suspendId;
    }
}
