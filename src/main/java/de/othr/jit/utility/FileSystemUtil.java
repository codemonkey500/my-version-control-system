package de.othr.jit.utility;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import de.othr.jit.datastructure.MerkleTree;
import org.apache.commons.io.FilenameUtils;
import org.springframework.util.FileSystemUtils;

import static de.othr.jit.constants.Constants.*;

/**
 * This class provides methods to work on
 * the file system
 * 
 * @author codemonkey500
 *
 */
public class FileSystemUtil {

    private static final Logger LOGGER = Logger
            .getLogger(FileSystemUtil.class.getName());
    
    private FileSystemUtil() {      
    }

    /**
     * This method can be used to create the init jit folders.
     * If the directory already exists, the user will get a message
     * on console.
     */
    public static void createInitDirectory() {

        if (new File(PATH_JIT_FOLDER).exists()) {
            LOGGER.log(Level.SEVERE, "Directory already exists!");
        } else {

            new File(PATH_OBJECTS_FOLDER).mkdirs();
            new File(PATH_STAGING_FOLDER).mkdirs();
            LOGGER.info("Jit init complete");
        }
    }

    /**
     * This method converts a file into a byte array.
     * 
     * @param path
     *            pointing to the file
     * @return byte[] representing the files content
     */
    public static byte[] convertFileToByte(Path path) {

        byte[] fileContent = "".getBytes();

        if (path.toFile().exists()) {
            try {
                fileContent = Files.readAllBytes(path);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Converting file failed!");
            }
        } else {
            LOGGER.log(Level.SEVERE, "File does not exist!");
        }

        return fileContent;
    }

    /**
     * This method is used to split a String path into pieces
     * 
     * @param path
     *            - e.g. "src/main/C.java"
     * @return a List<String> containing: [src, main, C.java]
     */
    public static List<String> splitPath(String path) {

        List<String> pathList = new ArrayList<String>();
        pathList.addAll(Arrays.asList(path.split("/")));

        return pathList;
    }

    /**
     * This method is used to serialize the MerkleTree.
     * The data structure will be stored in the folder .jit/staging
     * as a file called "staging.ser".
     * 
     * @param tree
     *            - to be serialized
     */
    public static void serializeStagingFile(MerkleTree tree) {

        try(ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(new File(PATH_STAGING_FILE)))) {
            out.writeObject(tree);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "staging.ser does not exist!");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Serialization of staging.ser failed!", e);
        }

    }

    /**
     * This method is used to deserialize the stagingFile.
     * In case an error occurs, the user will get a message on console.
     * 
     * @return - a MerkleTree object representing the current data structure
     */
    public static MerkleTree deserializeStagingFile() {

        MerkleTree tree = new MerkleTree();

        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(
                        new FileInputStream(PATH_STAGING_FILE)))) {

            tree = (MerkleTree) in.readObject();

        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "staging.ser does not exist!", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,
                    "Deserialization of staging.ser failed!", e);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE,
                    "Could not find the class for serialized object", e);
        }

        return tree;
    }

    /**
     * This method can be used to delete the staging file.
     */
    public static void deleteStagingFile() {

        if(!new File(PATH_STAGING_FILE).delete()) {
            LOGGER.log(Level.SEVERE, "Could not delete StagingFile!");
        }
    }

    /**
     * This method can be used to read from a file.
     * 
     * @param path - pointing to the file
     * @return the files content stored as a String
     */
    public static String readFile(String path) {

        StringBuilder sb = new StringBuilder();
        File file = new File(path);

        try (BufferedReader input = new BufferedReader(new FileReader(file))) {
            String line = null;

            while ((line = input.readLine()) != null) {
                sb.append(line);
            }

        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE,
                    "File with the path : " + path + "does not exist");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not read file");
        }

        return sb.toString();

    }

    /**
     * This method can be used to create a file and fill it with data.
     * 
     * @param path
     *            - with the file name included
     * @param data
     *            - as String representation to write in the file
     */
    public static void writeToFile(String path, String data) {

        File file = new File(path);
        try {
            if (file.createNewFile()) {

                try (BufferedWriter writer = new BufferedWriter(
                        new FileWriter(file, false))) {
                    writer.write(data);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not write to file");
        }
    }

    /**
     * This method can be used to delete all content from
     * the working directory except the .jit directory.
     */
    public static void deleteAllContentFromWorkingDirectory() {

        File f = new File(USER_DIR);

        List<String> dirList = Arrays.asList(f.list()).stream()
                .filter(dir -> !dir.equals(JIT_FOLDER_NAME))
                .collect(Collectors.toCollection(LinkedList::new));

        for (String dir : dirList) {
            FileSystemUtils.deleteRecursively(
                    Paths.get(dir).toFile().getAbsoluteFile());
        }
    }

    /**
     * This method can be used to get all file/dir names
     * without extension in a given dir.
     * 
     * @param path - to the directory
     * @return a list with file/dir names without extention
     */
    public static List<String> getFileNamesInDir(String path) {

        File dir = new File(path);

        return Arrays.asList(dir.list())
                .stream().map(f -> FilenameUtils.getBaseName(f))
                .collect(Collectors.toCollection(LinkedList::new));

    }

}
