/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bounding.BoundingBox;
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
    
    /* функция работает не полноценно, не ищет точку вхождения в полгом 3Д
     * по высоте проверяет полько по bound не учитывая поворот
     * требуется доработать функцию для использования фигур с полным поворотом
     * упрощенная функция называется rotatePointSimple
     */
    
    private Vector3f rotatePoint(Vector3f dot, Vector3f angles){

        this.debugText = "N " + dot.toString();
        
        // добиться поворота точки в пространстве по трем угла
        float a = (float) Math.atan2(dot.x, dot.z) - angles.y;
        float b = (float) Math.atan2(dot.x, dot.y) - angles.z;
        float radius = dot.distance(Vector3f.ZERO);
        
        // определение точки в пространстве по трем углам
        float nx = radius * (float) Math.sin(a) * (float) Math.cos(b);
	float ny = radius * (float) Math.sin(a) * (float) Math.sin(b);
        float nz = radius * (float) Math.cos(a);
        
        return new Vector3f(nx, ny, nz);
    }
    
    private Vector3f rotatePointSimple(Vector3f dot, Vector3f angles){

        // если угл не задан то вообще ничего не вычисляем
        if(angles.y == 0) {
            return dot;
        }
        
        float a = (float) Math.atan2(dot.x, dot.z) - angles.y;
        float radius = (float) Math.sqrt((dot.x * dot.x) + (dot.z * dot.z));
        
        float nx = radius * (float) Math.sin(a);
        float nz = radius * (float) Math.cos(a);
        
        return new Vector3f(nx, dot.y, nz);
    }

    
    private boolean contains(Vector3f pos){
    
        float ax = pos.x - this.center.x;
        float ay = pos.y - this.center.y;
        float az = pos.z - this.center.z;
        
        Vector3f rotated = this.rotatePointSimple(new Vector3f(ax, ay, az), this.rotation);
        
        return (rotated.x >= -this.sizes.x && rotated.x <= this.sizes.x && 
                rotated.y >= -this.sizes.y && rotated.y <= this.sizes.y &&
                rotated.z >= -this.sizes.z && rotated.z <= this.sizes.z);

    }

    public boolean isIntersects(Camera cam){
        return isIntersects(cam.getLocation());
    }    
    
    public boolean isIntersects(Vector3f pos){
    
        Vector3f pos2 = new Vector3f(pos.x, pos.y - 4.9f, pos.z);
        
        boolean cont = this.contains(pos2);
        
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
