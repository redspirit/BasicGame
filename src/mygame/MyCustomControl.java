/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author aleksej
 */
public class MyCustomControl extends RigidBodyControl implements PhysicsCollisionListener{
    
    private int n = 0;
    
    public MyCustomControl(BulletAppState bulletAppState) {
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
    }
    
    public void collision(PhysicsCollisionEvent event) {
        
        n++;
        
        System.out.println("EVENT: " + event.getNodeA().toString() + " > " + n);
        
        //Spatial n = event.getNodeA();
        
       
        //if ( event.getNodeA().getName().equals("player") ) {
    
            
        //}
        
    }    
    
    
}
