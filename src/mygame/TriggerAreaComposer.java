/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import java.util.ArrayList;

/**
 *
 * @author Алексей
 */
public class TriggerAreaComposer {
    
    private TriggerAreaComposer.EventHandler listener;
    private ArrayList<TriggerArea> triggers = new ArrayList<TriggerArea>();
    
    public String debugText = "";
    
    public TriggerAreaComposer(){
    
    
    }
    
    public void addTrigger(TriggerArea trigger){
        this.triggers.add(trigger);
    }

    public void addGeometry(Geometry geo, String name){
        addTrigger(new TriggerArea(geo, name));
    }
    
    public void check(Camera cam){
        this.check(cam.getLocation());
    }     
    
    public void check(Vector3f pos){
    
        boolean cont;
        TriggerArea ta;
        
        for (int i = 0; i < this.triggers.size(); i++) {
            
            ta = this.triggers.get(i);
            cont = ta.isIntersects(pos);
            
            if(cont != ta.isContains && this.listener != null) {

                if(cont) {
                   this.listener.hoverIn(ta);
                } else {
                   this.listener.hoverOut(ta);
                }

            }

            ta.isContains = cont;            
            
        }
        
        
    }    
    
    public void setListener (TriggerAreaComposer.EventHandler listener){
        this.listener = listener;
    }
    
    public static interface EventHandler {
        public void hoverIn(TriggerArea object);
        public void hoverOut(TriggerArea object);
    }        
    
}
