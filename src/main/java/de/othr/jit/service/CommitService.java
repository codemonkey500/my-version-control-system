package de.othr.jit.service;

import java.util.LinkedList;
import java.util.List;
import de.othr.jit.datastructure.MerkleTree;
import de.othr.jit.entity.Directory;
import de.othr.jit.entity.FileContainer;
import de.othr.jit.entity.JitObject;
import de.othr.jit.utility.FileSystemUtil;

import static de.othr.jit.constants.Constants.*;

/**
 * This class represents a commit.
 * @author codemonkey500
 *
 */
public class CommitService {
    
    private String message;
    private MerkleTree tree;
    
    
    public CommitService(String message) {
        this.message = COMMIT_HEADER_VALUE.concat(" ").concat(message);
        this.tree = FileSystemUtil.deserializeStagingFile();
    }
    
    
    /**
     * This method is used to commit all the files stored in
     * the staging area.
     */
    public void commitFiles() {
        
        List<Directory> directories = new LinkedList<Directory>();
        List<FileContainer> allFiles = new LinkedList<FileContainer>();
        allFiles.addAll(tree.getAllFiles());
        directories.addAll(tree.getDirectories());
        
        //Write all directory files
        for (Directory dir : directories) {

            if (dir.getName().equals(ROOT_NAME)) {
                StringBuilder sb = new StringBuilder();
                sb.append(this.getMessage());
                sb.append(System.getProperty("line.separator"));
                for (JitObject o : dir.getChildren()) {
                    sb.append(o.toString());
                    sb.append(System.getProperty("line.separator"));
                }

                FileSystemUtil
                        .writeToFile(buildFileName(dir.getHash()),
                                sb.toString());
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Directory");
                sb.append(System.getProperty("line.separator"));
                for (JitObject o : dir.getChildren()) {
                    sb.append(o.toString());
                    sb.append(System.getProperty("line.separator"));
                }

                FileSystemUtil
                        .writeToFile(buildFileName(dir.getHash()),
                                sb.toString());
            }
        }
        
        //Write files
        for (FileContainer file : allFiles) {
            
            String data = FileSystemUtil.readFile(file.getPath());
            
            FileSystemUtil.writeToFile(buildFileName(file.getHash()), data);
            

        }
        
    }
    
    /**
     * This method is used to connect the file name and 
     * the correct path to the objects folder.
     * @param hash - of the JitObject
     * @return - Path and file name as String to create the file
     */
    private String buildFileName(String hash) {
        
        StringBuilder sb = new StringBuilder();
        sb.append(PATH_OBJECTS_FOLDER);
        sb.append("/");
        sb.append(hash);
        sb.append(".txt");
        
        return sb.toString();
    }
    

    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    public MerkleTree getTree() {
        return tree;
    }


    public void setTree(MerkleTree tree) {
        this.tree = tree;
    }    

}
