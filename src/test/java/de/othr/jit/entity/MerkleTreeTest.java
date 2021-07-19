package de.othr.jit.entity;

import java.nio.file.Paths;
import java.util.Optional;

import de.othr.jit.core.Jit;
import de.othr.jit.datastructure.MerkleTree;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.util.FileSystemUtils;

import static de.othr.jit.constants.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * @author codemonkey500
 *
 */

public class MerkleTreeTest {

    @BeforeClass
    public static void initJit() {
        Jit.initializeDirectory();
    }

    @AfterClass
    public static void deleteJitFolder() {

        FileSystemUtils.deleteRecursively(
                Paths.get(".jit").toFile().getAbsoluteFile());
    }

    @Test
    public void addInitialFileTest() {

        String s = B_JAVA_PATH;

        MerkleTree tree = new MerkleTree();

        tree.addInitialFile(s);

        //Checking if wether the file system is represented correctly
        assertEquals("root", tree.getDirectories().get(0).getName());
        assertEquals("src", tree.getDirectories().get(1).getName());
        assertEquals("test", tree.getDirectories().get(2).getName());
        assertEquals("java", tree.getDirectories().get(3).getName());
        assertEquals("firsttestdir", tree.getDirectories().get(4).getName());
        assertEquals("secondtestdir", tree.getDirectories().get(5).getName());

        //Check children links
        assertEquals("B.java",
                tree.getDirectories().get(5).getChildren().get(0).getName());
        assertEquals("src",
                tree.getDirectories().get(0).getChildren().get(0).getName());
        assertEquals("test",
                tree.getDirectories().get(1).getChildren().get(0).getName());
        assertEquals("java",
                tree.getDirectories().get(2).getChildren().get(0).getName());
        assertEquals("firsttestdir",
                tree.getDirectories().get(3).getChildren().get(0).getName());
        assertEquals("secondtestdir",
                tree.getDirectories().get(4).getChildren().get(0).getName());
    }

    @Test
    public void addFileToExistingTreeTest() {

        //############### Initiate tree ###############

        String s = A_JAVA_PATH;
        MerkleTree tree = new MerkleTree();
        tree.addInitialFile(s);

        assertEquals(true, tree.getAllFiles().stream()
                .anyMatch(file -> file.getName().equals("A.java")));

        //############### Adding second file and directory ###############

        //Store hash values before adding the new file
        Optional<Object> othHashBeforeSecondFile = tree.getDirectories()
                .stream()
                .filter(d -> d.getName().equals("secondtestdir"))
                .findFirst().map(othDir -> othDir.getHash());

        String s2 = C_JAVA_PATH;
        tree.addFileToExistingTree(s2);

        //Checking if all files are registered
        assertEquals(true, tree.getAllFiles().stream()
                .anyMatch(file -> file.getName().equals("C.java")));
        assertEquals(true, tree.getAllFiles().stream()
                .anyMatch(file -> file.getName().equals("A.java")));
        assertEquals(true, tree.getDirectories().stream()
                .anyMatch(d -> d.getName().equals("thirdtestdir")));

        //Check for a change in hash value for oth directory
        Optional<Directory> othHashAfterSecondFile = tree.getDirectories()
                .stream()
                .filter(d -> d.getName().equals("secondtestdir"))
                .findFirst();
        if (othHashAfterSecondFile.isPresent()
                && othHashBeforeSecondFile.isPresent()) {
            assertNotEquals(othHashBeforeSecondFile.get(),
                    othHashAfterSecondFile.get().getHash());
        }

        //############### Adding third file ###############

        //Store hash values before adding the new file
        Optional<Object> othHashBeforeThirdFile = tree.getDirectories().stream()
                .filter(d -> d.getName().equals("secondtestdir"))
                .findFirst().map(d -> d.getHash());

        String s3 = B_JAVA_PATH;
        tree.addFileToExistingTree(s3);

        //Checking if all files/dir are registered
        assertEquals(true, tree.getAllFiles().stream()
                .anyMatch(file -> file.getName().equals("B.java")));
        assertEquals(true, tree.getAllFiles().stream()
                .anyMatch(file -> file.getName().equals("C.java")));
        assertEquals(true, tree.getAllFiles().stream()
                .anyMatch(file -> file.getName().equals("A.java")));
        assertEquals(true, tree.getDirectories().stream()
                .anyMatch(d -> d.getName().equals("thirdtestdir")));

        //Check for a change in hash value for oth directory
        Optional<Directory> othHashAfterThirdFile = tree.getDirectories()
                .stream()
                .filter(d -> d.getName().equals("secondtestdir"))
                .findFirst();
        if (othHashAfterThirdFile.isPresent()
                && othHashBeforeThirdFile.isPresent()) {
            assertNotEquals(othHashBeforeThirdFile.get(),
                    othHashAfterThirdFile.get().getHash());
        }

        //############### Adding fourth file ###############

        //Store hash values before adding the new file
        Optional<Object> deHashBeforeFourthFile = tree.getDirectories().stream()
                .filter(d -> d.getName().equals("firsttestdir"))
                .findFirst().map(d -> d.getHash());

        Optional<Object> othHashBeforeFourthFile = tree.getDirectories()
                .stream()
                .filter(d -> d.getName().equals("secondtestdir"))
                .findFirst().map(d -> d.getHash());

        String s4 = D_JAVA_PATH;
        tree.addFileToExistingTree(s4);

        //Checking if all files/dir are registered
        assertEquals(true, tree.getAllFiles().stream()
                .anyMatch(file -> file.getName().equals("D.java")));
        assertEquals(true, tree.getAllFiles().stream()
                .anyMatch(file -> file.getName().equals("B.java")));
        assertEquals(true, tree.getAllFiles().stream()
                .anyMatch(file -> file.getName().equals("C.java")));
        assertEquals(true, tree.getAllFiles().stream()
                .anyMatch(file -> file.getName().equals("A.java")));
        assertEquals(true, tree.getDirectories().stream()
                .anyMatch(d -> d.getName().equals("thirdtestdir")));

        //Check for a change in hash value for de directory
        Optional<Directory> deHashAfterFourthFile = tree.getDirectories()
                .stream()
                .filter(d -> d.getName().equals("firsttestdir"))
                .findFirst();
        if (deHashAfterFourthFile.isPresent()
                && deHashBeforeFourthFile.isPresent()) {
            assertNotEquals(deHashBeforeFourthFile.get(),
                    deHashAfterFourthFile.get().getHash());
        }

        //Check oth directory. There should be no change in hash value
        Optional<Directory> othHashAfterFourthFile = tree.getDirectories()
                .stream()
                .filter(d -> d.getName().equals("secondtestdir"))
                .findFirst();

        if (othHashAfterFourthFile.isPresent()
                && othHashBeforeFourthFile.isPresent()) {
            assertEquals(othHashBeforeFourthFile.get(),
                    othHashAfterFourthFile.get().getHash());
        }

    }

}
