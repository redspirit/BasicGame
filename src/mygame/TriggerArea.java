/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;

/**
 *
 * @author aleksej
 */
public class TriggerArea {
    
    private Geometry shape;
    private boolean isContains = false;
    private Vector3f center, rotation, sizes;
    
    public String debugText = "";
    
    public TriggerArea(Geometry shape){
        
        this.shape = shape;
   
        this.makeCenter();
        this.makeSizes();
        this.makeAngles();
        
        System.out.println("center " + this.center);
        System.out.println("rotation " + this.rotation);
        System.out.println("sizes " + this.sizes);
        
    }
    
    private Vector3f rotatePoint(Vector3f dot, float angle){

        float a = (float) Math.atan2(dot.x, dot.z) - angle;
        float radius = dot.distance(Vector3f.ZERO);
        
        float nx = radius * (float) Math.sin(a) * (float) Math.cos(0);
	float ny = radius * (float) Math.sin(a) * (float) Math.sin(0);
        float nz = radius * (float) Math.cos(a);
	
        return new Vector3f(nx, ny, nz);
    }
    
    
    
    private boolean contains(Vector3f pos){
    
        float ax = pos.x - this.center.x;
        float ay = pos.y - this.center.y;
        float az = pos.z - this.center.z;
        
        Vector3f rotated = this.rotatePoint(new Vector3f(ax, ay, az), this.rotation.y);

        return (rotated.x >= -this.sizes.x && rotated.x <= this.sizes.x && 
                rotated.z >= -this.sizes.z && rotated.z <= this.sizes.z);

    }

    public boolean isIntersects(Camera cam){
        return isIntersects(cam.getLocation());
    }    
    
    public boolean isIntersects(Vector3f pos){
    
        Vector3f pos2 = new Vector3f(pos.x, pos.y - 4.9f, pos.z);
        
        boolean cont = this.contains(pos);
        
        if(cont != this.isContains) {
            
            if(cont) {
                //System.out.println("Hover ON");
            } else {
               //System.out.println("Hover OFF"); 
            }
            
        }
        
        this.isContains = cont;
        return cont;
    
    }
    
    public Geometry getGeometry(){
        return this.shape;
    }
    private void makeCenter(){
        this.center = this.shape.getLocalTranslation();
    }
    private void makeAngles(){
        float[] angles = new float[3];
        this.shape.getLocalRotation().toAngles(angles);
        this.rotation = new Vector3f(angles[0], angles[1], angles[2]);
    }
    private void makeSizes(){
        BoundingBox bound = (BoundingBox) this.shape.getModelBound();
        Vector3f scale = this.shape.getLocalScale();
        this.sizes = new Vector3f(
                bound.getXExtent() * scale.x,
                bound.getYExtent() * scale.y,
                bound.getZExtent() * scale.z
            );  
    }

    
}
