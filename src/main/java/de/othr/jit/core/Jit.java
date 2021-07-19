package de.othr.jit.core;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import de.othr.jit.datastructure.MerkleTree;
import de.othr.jit.entity.Directory;
import de.othr.jit.entity.FileContainer;
import de.othr.jit.service.CheckoutService;
import de.othr.jit.service.CommitService;
import de.othr.jit.utility.FileSystemUtil;
import static de.othr.jit.constants.Constants.*;

/**
 * Represents the main class of the Jit-Tool.
 * 
 * @author codemonkey500
 *
 */
public class Jit {

    private static final Logger LOGGER = Logger.getLogger(Jit.class.getName());

    public static void main(String[] args) {

        if (args.length == 1 && args[0].equals(INIT)) {

            initializeDirectory();

        } else if(args.length == 2){
            switch (args[0]) {
            case ADD:
                addFile(args[1]);
                break;
            case REMOVE:
                removeFile(args[1]);
                break;
            case COMMIT:
                commit(args[1]);
                break;
            case CHECKOUT:
                checkoutFile(args[1]);
                break;
                
            default:
                LOGGER.log(Level.SEVERE, "Unknown command: " + args[0]);
            }
            
        } else {
            LOGGER.log(Level.SEVERE, "Missing argument");
        }
    }

    /**
     * This method is used to initialize three directories. It creates
     * the .jit directory and its sub directories.
     * If they already exist, this method will do nothing and
     * the user will get a message on console.
     */
    public static void initializeDirectory() {

        FileSystemUtil.createInitDirectory();

    }

    /**
     * This method is used to add a file to the data structure.
     * In case the .jit direcory does not exist, the user will get
     * a message on console.
     * 
     * @param path
     *            - pointing to the file
     */
    public static void addFile(String path) {

        if (!new File(PATH_JIT_FOLDER).exists()) {

            LOGGER.log(Level.SEVERE, "Initialize Jit first!");
            
        } else if(!new File(path).exists()) {
            
            LOGGER.log(Level.SEVERE, "Path not valid");
            
        } else if (new File(PATH_STAGING_FILE).exists()) {

            MerkleTree tree = FileSystemUtil
                    .deserializeStagingFile();

            //Check, if file already exists in the staging file
            String name = Paths.get(path).getFileName().toString();
            if (tree.getAllFiles().stream()
                    .map(f -> f.getName()).anyMatch(f -> f.equals(name))) {
                LOGGER.log(Level.SEVERE,
                        "File already exists in staging area!");
                return;
            }

            tree.addFileToExistingTree(path);
            FileSystemUtil.serializeStagingFile(tree);
            LOGGER.info(Paths.get(path).getFileName().toString()
                    + " was added to staging area!");
        } else {

            //Check for wether file exists or not
            if (new File(path).exists()) {
                LOGGER.info(Paths.get(path).getFileName().toString()
                        + " was added to staging area!");
                MerkleTree tree = new MerkleTree();
                tree.addInitialFile(path);
                FileSystemUtil.serializeStagingFile(tree);
            } else {
                LOGGER.log(Level.SEVERE, "File does not exits!");
            }

        }

    }

    /**
     * This method is used to delete a file from staging area.
     * In case the path is not valid, the user will get a message on console.
     * 
     * @param path
     *            - pointing to the file
     */
    public static void removeFile(String path) {

        if (!new File(path).exists()) {
            LOGGER.log(Level.SEVERE,
                    "File with path: " + path + " does not exist!");
        } else {
            MerkleTree tree = FileSystemUtil
                    .deserializeStagingFile();

            //Delete file in MerkleTree
            Optional<FileContainer> file = tree
                    .getAllFiles().stream()
                    .filter(f -> f.getPath().equals(
                            Paths.get(path).toAbsolutePath().toString()))
                    .findFirst();

            if (file.isPresent()) {
                String parentName = file.get().getParent().getName();
                Optional<Directory> parent = tree.getDirectories().stream()
                        .filter(dir -> dir.getName().equals(parentName))
                        .findFirst();
                parent.get().getChildren().remove(file.get());

                tree.getAllFiles().remove(file.get());

                //Delete empty directory
                if (parent.get().getChildren().isEmpty()) {
                    parent.get().getParent().getChildren().remove(parent.get());
                    tree.getDirectories().remove(parent.get());
                }
            } else {
                LOGGER.log(Level.SEVERE, "File not found in data structure!");
            }

            //No more files stored in staging area. -> Delete staging.ser
            if (tree.getAllFiles().size() == 0) {
                FileSystemUtil.deleteStagingFile();
                LOGGER.info("Staging area is empty now!");

            } else {
                FileSystemUtil.serializeStagingFile(tree);
                LOGGER.info("Removed "
                        + Paths.get(path).getFileName().toString()
                        + " from staging area!");
            }

        }
    }

    /**
     * This method is used to commit files from the stating area.
     * 
     * @param message - from the user
     */
    public static void commit(String message) {

        CommitService commit = new CommitService(message);
        commit.commitFiles();
        LOGGER.info("Commit successful");
    }

    /**
     * This method is used to checkout files from the object directory.
     * 
     * @param hash - representing the file name of the commit
     */
    public static void checkoutFile(String hash) {

        CheckoutService checkout = new CheckoutService();
        checkout.checkoutFiles(hash);

    }
}
