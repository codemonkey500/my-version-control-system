package de.othr.jit.core;

import java.io.File;
import java.nio.file.Paths;
import de.othr.jit.datastructure.MerkleTree;
import de.othr.jit.utility.FileSystemUtil;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.FileSystemUtils;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static de.othr.jit.constants.Constants.*;

/**
 * @author codemonkey500
 *
 */

public class JitTest {

    @BeforeClass
    public static void initJit() {
        Jit.initializeDirectory();
    }

    @Before
    public void deleteStaging() {

        FileSystemUtil.deleteStagingFile();
    }

    @AfterClass
    public static void deleteJitDir() {

        FileSystemUtils.deleteRecursively(
                Paths.get(".jit").toFile().getAbsoluteFile());
    }

    @Test
    public void addFileTest() {

        Jit.addFile(B_JAVA_PATH);
        Jit.addFile(A_JAVA_PATH);
        Jit.addFile(C_JAVA_PATH);

        MerkleTree tree = FileSystemUtil.deserializeStagingFile();

        //Checking size of both serialized lists
        assertEquals(3, tree.getAllFiles().size());
        assertEquals(7, tree.getDirectories().size());

    }

    @Test
    public void removeFileTest() {

        //Checking, wether the .ser file gets deleted, if all files are gone
        Jit.addFile(A_JAVA_PATH);
        Jit.removeFile(A_JAVA_PATH);

        assertEquals(false, new File(PATH_STAGING_FILE).exists());

        //Checken behavior for deleting 1 of 3 files       
        Jit.addFile(A_JAVA_PATH);
        Jit.addFile(B_JAVA_PATH);
        Jit.addFile(C_JAVA_PATH);
        Jit.removeFile(B_JAVA_PATH);

        MerkleTree tree = FileSystemUtil.deserializeStagingFile();

        assertEquals(true, new File(PATH_STAGING_FILE).exists());
        assertEquals(2, tree.getAllFiles().size());
        assertEquals(2,
                tree.getDirectories().stream()
                        .filter(dir -> dir.getName().equals("secondtestdir"))
                        .findFirst()
                        .map(c -> c.getChildren().size()).get());

        //Check, wether directory ajp still exists -> expected true
        assertEquals(true,
                tree.getDirectories().stream()
                        .filter(d -> d.getName().equals("secondtestdir"))
                        .findFirst()
                        .map(d -> d.getChildren().stream()
                                .anyMatch(dir -> dir.getName()
                                        .equals("thirdtestdir")))
                        .get());
    }
}
