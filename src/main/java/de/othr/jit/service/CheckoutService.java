package de.othr.jit.service;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.othr.jit.utility.FileSystemUtil;
import static de.othr.jit.constants.Constants.*;

/**
 * This class is used for the checkout command.
 * @author codemonkey500
 *
 */
public class CheckoutService {
    
    private static final Logger LOGGER = Logger
            .getLogger(CheckoutService.class.getName());

    /**
     * This method is used to checkout a commit.
     * The current workspace will be deleted and the files from
     * checkout will be created.
     * @param hash - value of a commit
     */
    public void checkoutFiles(String hash) {
        

        //Checking if right file was selected
        if (FileSystemUtil.getFileNamesInDir(PATH_OBJECTS_FOLDER)
                .contains(hash)) {

            if(getHeader(hash).equals(COMMIT_HEADER_VALUE)) {
                
                //Delete working directory
                FileSystemUtil.deleteAllContentFromWorkingDirectory();
                
                List<String> body = getFileBody(hash);
                
                for (String line : body) {
                    List<String> bodyLine = splitBodyLine(line);
                    createWorkspace(bodyLine.get(0),
                            bodyLine.get(1), bodyLine.get(2), USER_DIR);
                }
                
                LOGGER.info("Checkout successful!");
            }
            else {
                LOGGER.log(Level.SEVERE,
                        "Wrong hash value selected! Pls select a commit!");
            }
            
        } else {
            LOGGER.log(Level.SEVERE, "Hash value does not exists.");
        }           
    }
    
    /**
     * This private method is used to
     * create the worspace recursively.
     * @param type - of the JitObject
     * @param hash - hash of the JitObject
     * @param name - name of the JitObject
     * @param path - path of the JitObject
     */
    private void createWorkspace(String type, String hash, String name,
            String path) {
        
        if(type.equals(DIRECTORY_HEADER_VALUE)) {
            
            path = path.concat("/").concat(name);
            File dir = new File(path);
            dir.mkdir();
            
            List<String> body = getFileBody(hash);
            
            for (String line : body) {
                List<String> split = splitBodyLine(line);
                createWorkspace(split.get(0), split.get(1), split.get(2), path);
            }
            
            
        } else if(type.equals(FILE_HEADER_VALUE)){
            
            String fileData = FileSystemUtil
                    .readFile(PATH_OBJECTS_FOLDER.concat("/")
                            .concat(hash).concat(".txt"));
            
            FileSystemUtil.writeToFile(path.concat("/").concat(name), fileData);
        }
    }

    
    /**
     * This private method is used to get the body of
     * file in the objects dir. 
     * @param hash - name of the file represented as hash
     * @return - the body without the header
     */
    private List<String> getFileBody(String hash) {
        
        String content = FileSystemUtil
                .readFile(PATH_OBJECTS_FOLDER + "/" + hash + ".txt");
        
        List<String> contentList = Arrays
                .asList(content.split(System.getProperty("line.separator")));
        
        ListIterator<String> iterator =  contentList.listIterator(1);
        List<String> body = new LinkedList<>();
        
        while (iterator.hasNext()) {
            body.add(iterator.next().toString());
        }
  
        return body;
    }
    
    /**
     * This private method is used to get the header of
     * a file in the objects dir.
     * @param hash - name of the file represented as hash.
     * @return - the header key word -> e.g Directory, Commit
     */
    private String getHeader(String hash) {
        
        
        String content = FileSystemUtil
                .readFile(PATH_OBJECTS_FOLDER + "/" + hash + ".txt");
        
        List<String> contentList = Arrays
                .asList(content.split(System.getProperty("line.separator")));
        
        return Arrays.asList(contentList.get(0).split(" ")).get(0);
    }
    
    private List<String> splitBodyLine(String bodyLine) {
        
        List<String> split = new LinkedList<>();
            
            split = Arrays.asList(bodyLine.split(" "));

        return split;
    }

}
