package de.othr.jit.datastructure;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import de.othr.jit.entity.Directory;
import de.othr.jit.entity.FileContainer;
import de.othr.jit.entity.JitObject;
import de.othr.jit.utility.FileSystemUtil;
import de.othr.jit.utility.SecureHashUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;

import static de.othr.jit.constants.Constants.*;

/**
 * This class is used to represent the file system.
 * 
 * @author codemonkey500
 *
 */
public class MerkleTree implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger
            .getLogger(MerkleTree.class.getName());
    
    private List<Directory> directories;
    private Directory root;
    private List<FileContainer> allFiles;

    public MerkleTree() {
        this.directories = new LinkedList<Directory>();
        this.allFiles = new LinkedList<FileContainer>();
    }

    /**
     * This method adds a file to a non existing MerkleTree.
     * The MerkleTree will get hashed aswell.
     * 
     * @param path - pointing to the file
     */
    public void addInitialFile(String path) {

        this.directories = createDirectoryList(path);
        hashInitialTree();

    }

    /**
     * This methods should be called on the already existing tree.
     * The file will be added to the data structure.
     * The affected parts of the tree will get new hash values.
     * 
     * @param path - pointing on a file
     */
    public void addFileToExistingTree(String path) {

        //creating the mergeTree
        MerkleTree treeToMerge = new MerkleTree();
        treeToMerge.addInitialFile(path);
        treeToMerge.hashInitialTree();
        this.allFiles.addAll(treeToMerge.allFiles);
        
        Directory parent = treeToMerge.getAllFiles().get(0).getParent();
        
        //searching for the right merge directory
        while(!this.directories.contains(parent)) {

            parent = parent.getParent();
        }
        
        List<JitObject> tempDirList = new LinkedList<JitObject>();
        
        //once the right dir is found, the right part of the mergeTree
        //will be added
        for (Directory d : this.directories) {
            if(d.equals(parent)) {
                d.addChildren(parent.getChildren());
                tempDirList = parent.getChildren()
                        .stream()
                        .filter(dir -> dir instanceof Directory)
                        .collect(Collectors.toList());
                break;
            }
        } 
        
        //checking if the merge did add a directory aswell.
        //If this is the case, we have to store this dir in our list.
        if(tempDirList.size() > 0) {
            this.directories.add((Directory) tempDirList.get(0));
        }
        
        this.hashMergedTree();
    }

    /**
     * This method creates a list of directories. Those directories
     * are part of the path. The directories will be linked so that
     * every element in this list knows its children.
     * 
     * @param path - is pointing to the file
     */
    private List<Directory> createDirectoryList(String path) {

        List<Directory> directories = new LinkedList<Directory>();

        Directory root = new Directory(ROOT_NAME);
        
        directories.add(root);
        this.root = root;

        List<String> split = FileSystemUtil.splitPath(path);

        //the path is poining on our start directory
        Path dir = Paths.get(split.get(0)).toFile().getAbsoluteFile().toPath();

        File file = new File(path);

        try {
            Files.walkFileTree(dir, new FileVisitor<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir,
                        BasicFileAttributes attrs) throws IOException {

                    directories.add(new Directory(dir.toFile().getName()));

                    return FileVisitResult.CONTINUE;

                }

                @Override
                public FileVisitResult visitFile(Path dir,
                        BasicFileAttributes attrs) throws IOException {

                    //When the file is found, the search will terminate
                    //The last directory will store the link to the file 
                    //in its list of children.
                    //All files of the tree will be stored
                    //in a list for later merge
                    if (dir.toFile().getName().equals(file.getName())) {
                        FileContainer file = new FileContainer(
                                dir.toFile().getName(), dir.toString());
                        file.setParent(directories.get(directories.size() - 1));
                        directories.get(directories.size() - 1).getChildren()
                                .add(file);
                        allFiles.add(file);

                        return FileVisitResult.TERMINATE;
                    }

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path dir,
                        IOException exc)
                        throws IOException {

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir,
                        IOException exc)
                        throws IOException {

                    directories.remove(new Directory(dir.toFile().getName()));
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, ExceptionUtils.getStackTrace(e));
        }

        //Link directories
        for (int i = directories.size() - 2; i >= 0; i--) {
            directories.get(i).addChild(directories.get(i + 1));
            directories.get(i + 1).setParent(directories.get(i));
        }

        return directories;
    }

    /**
     * Use this method to hash a inital tree.
     * The method will follow merkle tree hash principles.
     * @see <a href="https://en.wikipedia.org/wiki/Merkle_tree">MerkleTree</a>
     */
    private void hashInitialTree() {

        for (int i = directories.size() - 1; i >= 0; i--) {

            StringBuilder sb = new StringBuilder();

            for (JitObject child : directories.get(i).getChildren()) {
                sb.append(child.getHash());
            }

            directories.get(i).setHash(
                    SecureHashUtil
                            .computeHash(sb.toString().getBytes()).getBytes());
        }
    }
    
    /**
     * This method can be used to hash a MerkleTree.
     * This method will follow the merkle tree hash principles. 
     * @see <a href="https://en.wikipedia.org/wiki/Merkle_tree">MerkleTree</a>
     */
    private void hashMergedTree() {
        
        this.directories.stream().forEach(dir -> dir.resetHash());
        
        List<Directory> dirList = new LinkedList<Directory>();
        allFiles.stream().map(p -> p.getParent()).distinct()
                .forEach(p -> dirList.add(p));
        
        for (Directory dir : dirList) {
            Directory walker = dir;
            while(!walker.getName().equals(ROOT_NAME)) {
                StringBuilder sb = new StringBuilder();
                walker.getChildren().stream().map(c -> c.getHash())
                        .forEach(c -> sb.append(c));
                sb.append(walker.getHash());
                walker.setHash(sb.toString().getBytes());
                walker = walker.getParent();
            }
        }
        
        StringBuilder sb = new StringBuilder();
        this.root.getChildren().stream()
                .forEach(dir -> sb.append(dir.getHash()));
        this.root.setHash(sb.toString().getBytes());
        
    }

    public List<Directory> getDirectories() {
        return directories;
    }

    public Directory getRoot() {
        return root;
    }

    public void setRoot(Directory root) {
        this.root = root;
    }

    public List<FileContainer> getAllFiles() {
        return allFiles;
    }

    public void setAllFiles(List<FileContainer> allFiles) {
        this.allFiles = allFiles;
    }
}
