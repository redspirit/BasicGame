/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
 
/**
 * Example Vector Summ
 * @author Alex Cham aka Jcrypto
 */
public class SpatialUtils {

    public static Node makeNode(String name) {
        Node n = new Node(name);
        return n;
    }
 
    public static Geometry makeGeometry(Mesh mesh, Material mat, String name) {
        Geometry geom = new Geometry(name, mesh);
        geom.setMaterial(mat);
        geom.setLocalScale(new Vector3f(2, 2, 2));
        return geom;
    }
 
    public static Geometry makeGeometry(Vector3f loc, Vector3f scl, Mesh mesh, Material mat, String name) {
        Geometry geom = new Geometry(name, mesh);
        geom.setMaterial(mat);
        geom.setLocalTranslation(loc);
        geom.setLocalScale(scl);
        return geom;
    }
}