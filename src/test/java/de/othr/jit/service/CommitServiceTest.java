package de.othr.jit.service;

import java.io.File;
import java.nio.file.Paths;
import de.othr.jit.core.Jit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.FileSystemUtils;
import static de.othr.jit.constants.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author codemonkey500
 *
 */

public class CommitServiceTest {

    @BeforeClass
    public static void createDataStructure() {

        Jit.initializeDirectory();

        Jit.addFile(A_JAVA_PATH);
        Jit.addFile(B_JAVA_PATH);
    }

    @AfterClass
    public static void cleanDataStructure() {

        FileSystemUtils.deleteRecursively(
                Paths.get(".jit").toFile().getAbsoluteFile());
    }

    @Test
    public void commitFilesTest() {

        CommitService commit = new CommitService("CommitMessage");

        commit.commitFiles();

        assertEquals(8,
                new File(PATH_OBJECTS_FOLDER).listFiles().length);

    }

}
