/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
 
/**
 * Example Vector Summ
 * @author Alex Cham aka Jcrypto
 */
 
public class MaterialUtils {
 
    public MaterialUtils(){
        
    }
 
    //"Common/MatDefs/Misc/Unshaded.j3md"
    public static Material makeMaterial(AssetManager am, String name, ColorRGBA color) {
        Material mat = new Material(am, name);
        mat.setColor("Color", color);
        return mat;
    }
    
}