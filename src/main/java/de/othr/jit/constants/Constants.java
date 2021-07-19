package de.othr.jit.constants;

import java.nio.file.Paths;

/**
 * This class defines all the constants being used in this project
 * @author codemonkey500
 *
 */
public final class Constants {

    //#################### File System Constants ####################

    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String PATH_STAGING_FILE = USER_DIR
            + Paths.get("/.jit/staging/staging.ser");
    public static final String PATH_JIT_FOLDER = USER_DIR + Paths.get("/.jit");
    public static final String PATH_OBJECTS_FOLDER = USER_DIR
            + Paths.get("/.jit/objects");
    public static final String PATH_STAGING_FOLDER = USER_DIR
            + Paths.get("/.jit/staging");
    public static final String ROOT_NAME = "root";
    public static final String JIT_FOLDER_NAME = ".jit";

    //#################### Jit Commands  ####################

    public static final String INIT = "init";
    public static final String ADD = "add";
    public static final String REMOVE = "remove";
    public static final String COMMIT = "commit";
    public static final String CHECKOUT = "checkout";

    //#################### Header KeyWords  ####################

    public static final String COMMIT_HEADER_VALUE = "Commit:";
    public static final String DIRECTORY_HEADER_VALUE = "Directory";
    public static final String FILE_HEADER_VALUE = "File";

    //#################### TEST File Paths  ####################

    public static final String A_JAVA_PATH = "src/test/java"
            + "/firsttestdir/secondtestdir/A.java";
    public static final String B_JAVA_PATH = "src/test/java"
            + "/firsttestdir/secondtestdir/B.java";
    public static final String C_JAVA_PATH = "src/test/java"
            + "/firsttestdir/secondtestdir/thirdtestdir/C.java";
    public static final String D_JAVA_PATH = "src/test/java"
            + "/firsttestdir/D.java";

    private Constants() {
    }
}
