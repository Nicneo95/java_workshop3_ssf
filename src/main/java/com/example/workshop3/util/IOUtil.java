package com.example.workshop3.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// CREATE A INPUT/OUTPUT UTILITY TO READ AND WRITE FILES
public class IOUtil {
    private static final Logger logger = LoggerFactory.getLogger(IOUtil.class);
    // this method is use to create a file directory
    public static void createDir(String path) {
        File dir = new File(path);
        boolean isDirCreated = dir.mkdirs();
        logger.info("dir created > " + isDirCreated);
        if (isDirCreated) {
            String osName = System.getProperty("os.name");
            if (!osName.contains("Windows")) {
                try {
                    // read write execute permission
                    String perm = "rwxrwx---";
                    Set<PosixFilePermission> permissions = PosixFilePermissions.fromString(perm);
                    Files.setPosixFilePermissions(dir.toPath(), permissions);
                } catch (IOException e) {
                    logger.error("Error ", e);
                }
            }
        }
    }
}