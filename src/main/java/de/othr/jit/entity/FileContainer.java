package de.othr.jit.entity;

import java.io.Serializable;
import java.nio.file.Paths;
import java.util.Objects;
import de.othr.jit.utility.FileSystemUtil;
import de.othr.jit.utility.SecureHashUtil;

/**
 * This class represents a File in the file system
 * @author codemonkey500
 *
 */
public class FileContainer implements JitObject, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String path;
    private byte[] data;
    private String hash;
    private String name;
    private Directory parent;

    public FileContainer(String name, String path) {
        this.path = path;
        this.data = FileSystemUtil.convertFileToByte(Paths.get(path));
        this.name = name;
        this.hash = SecureHashUtil.computeHash(data);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String setHash(byte[] target) {
        
        this.hash = SecureHashUtil.computeHash(target);
        
        return hash;       
    }

    @Override
    public String getHash() {
        return hash;
    }

    @Override
    public void setName(String name) {
        this.name = name;    
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Directory getParent() {
        return parent;
    }
    @Override
    public void setParent(Directory parent) {
        this.parent = parent;
    }
    
    @Override
    public void resetHash() {
        this.hash = "";
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FileContainer other = (FileContainer) obj;
        return Objects.equals(name, other.name);
    }

    @Override
    public String toString() {
        return "File " + hash + " " + name;
    }
 
}