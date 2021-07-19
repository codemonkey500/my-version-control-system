package de.othr.jit.entity;

import java.io.Serializable;

/**
 * This interface describes a JitObject. 
 * @author codemonkey500
 *
 */
public interface JitObject extends Serializable {

    /**
     * Use this method to generate a SHA-1 secure hash code.
     * This method will set the objects hash value and even return
     * this value.
     * This is useful especially when it comes to hashing directories.
     * @param target - represents the byte-Array to be hashed
     * @return the secure SHA-1 hash code of the param
     */
    public String setHash(byte[] target);
    
    
    /**
     * Use this method to get the objects hash code.
     * @return the secure SHA-1 hash code of the param
     */
    public String getHash();
    
    /**
     * Use this method to set the name of the JitObject
     * @param name of the JitObject
     */
    public void setName(String name);
    
    
    /**
     * Use this method to get the name of the JitObject
     */
    public String getName();
    
    
    /**
     * Use this method to reset a hash.
     * The corresponding variable will
     * receive an empty string
     */
    public void resetHash();
    
    /**
     * Use this method to set an objects parent
     * @param - dir will be set as parent for this object
     */
    public void setParent(Directory dir);
    
    /**
     * Use this method to get an objects parent
     * @return - this objects parent
     */
    public Directory getParent();

}
