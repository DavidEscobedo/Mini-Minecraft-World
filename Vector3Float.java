/**
 *File: Vector3Float.java
 *class:CS445-Computer Graphics 
 *Assignment:Final Project
 *Date last modified:Nov28
 *Authors:Daniel Lopez,Roberto Ruiz,Monica Say, Bryce Metcalf,and David Escobedo. 
 */
package cs445final;

public class Vector3Float {
    private float x, y, z;
    //this constructor takes in three arguments(x,y,z)
    //which are used to sent the X,Y,Z values
    public Vector3Float(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    //this method is used to retrieve the X value
    public float getX() {
        return x;
    }
    //this method is used to set the X value
    public void setX(float x) {
        this.x = x;
    }
    //this method is used to retrieve the Y value
    public float getY() {
        return y;
    }
    //this method is used to set the Y value
    public void setY(float y) {
        this.y = y;
    }
    //this method is used to retrieve the Z value
    public float getZ() {
        return z;
    }
    //this method is used to set the Z value
    public void setZ(float z) {
        this.z = z;
    }
    
}
