package de.othr.jit.entity;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import de.othr.jit.utility.SecureHashUtil;

/**
 * This class represents a directory in the file system
 * @author codemonkey500
 *
 */
public class Directory implements JitObject, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String hash;
    private String name;
    private List<JitObject> children;
    private Directory parent;

    public Directory(String name) {
        this.hash = "";
        this.children = new LinkedList<JitObject>();
        this.name = name;
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


    public List<JitObject> getChildren() {
        return children;
    }


    public void addChildren(List<JitObject> child) {
        this.children.addAll(child);
    }
    
    public void addChild(JitObject child) {
        this.children.add(child);
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
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public void resetHash() {
        this.hash = "";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Directory other = (Directory) obj;
        return Objects.equals(name, other.name);
    }


    @Override
    public String toString() {
        return "Directory " + hash + " " + name;
    }
   
}
