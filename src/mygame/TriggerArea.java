/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

/**
 *
 * @author aleksej
 */
public class TriggerArea {
    
    private BoundingVolume bound;
    private Geometry shape;
    private boolean isContains = false;
    
    public TriggerArea(Geometry shape){
        
        this.bound = shape.getModelBound();
        this.shape = shape;
        this.bound.transform(shape.getLocalTransform());
        
        
        System.out.println("transform " + shape.getLocalTransform().toString());
        
    }
    
    public boolean isIntersects(CharacterControl player){
    
        Vector3f pos = player.getPhysicsLocation();
        Vector3f pos2 = new Vector3f(pos.x, pos.y - 4.9f, pos.z);
        boolean cont = this.bound.contains(pos) || this.bound.contains(pos2);
        
        if(cont != this.isContains) {
            
            if(cont) {
                System.out.println("Hover ON");
            } else {
               System.out.println("Hover OFF"); 
            }
            
        }
        
        this.isContains = cont;
        return cont;
    
    }
    
    public Geometry getGeometry(){
        return shape;
    }

}
