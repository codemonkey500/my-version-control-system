package de.othr.jit.utility;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import de.othr.jit.constants.Constants;
import de.othr.jit.core.Jit;
import de.othr.jit.datastructure.MerkleTree;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.FileSystemUtils;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author codemonkey500
 *
 */


public class FileSystemUtilTest {

    @BeforeClass
    public static void initJit() {
        Jit.initializeDirectory();
    }

    @AfterClass
    public static void deleteJitFolder() {

        FileSystemUtils.deleteRecursively(
                Paths.get(".jit").toFile().getAbsoluteFile());
        FileSystemUtils.deleteRecursively(
                Paths.get("testDir").toFile().getAbsoluteFile());
    }

    @Test
    public void serializationTest() {

        MerkleTree treeBeforeSer = new MerkleTree();
        treeBeforeSer.addInitialFile(Constants.B_JAVA_PATH);

        FileSystemUtil.serializeStagingFile(treeBeforeSer);

        MerkleTree treeAfterSer = FileSystemUtil
                .deserializeStagingFile();

        //Checking size of both serialized lists
        assertEquals(treeBeforeSer.getAllFiles().size(),
                treeAfterSer.getAllFiles().size());
        assertEquals(treeBeforeSer.getDirectories().size(),
                treeAfterSer.getDirectories().size());

        //Checking file data
        assertEquals("B.java", treeAfterSer.getAllFiles().get(0).getName());

    }

    @Test
    public void splitPathTest() {

        String s = "src/test/java/A.java";

        List<String> list = FileSystemUtil.splitPath(s);

        assertEquals("src", list.get(0));
        assertEquals("test", list.get(1));
        assertEquals("java", list.get(2));
        assertEquals("A.java", list.get(3));
    }

    @Test
    public void readAndWriteTest() {

        final String FILE_NAME = "testfile.txt";
        final String DATA = "test";

        FileSystemUtil.writeToFile(FILE_NAME, DATA);

        assertEquals(true, new File(FILE_NAME).exists());
        assertEquals(DATA + System.getProperty("line.separator"),
                FileSystemUtil.readFile(FILE_NAME));

        //Delete testfile.txt after test
        new File(FILE_NAME).delete();
    }

    @Test
    public void getFileNamesInDir() {

        new File("testDir/dir").mkdirs();

        List<String> list = FileSystemUtil.getFileNamesInDir("testDir");

        assertEquals("dir", list.get(0));

    }
}
