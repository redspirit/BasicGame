/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.animation.AnimControl;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.debug.SkeletonDebugger;
import com.jme3.scene.shape.Line;
import static mygame.SpatialUtils.makeGeometry;

/**
 *
 * @author aleksej
 */
public class Debug {
    
    public static void showNodeAxes(AssetManager am, Node n, float axisLen) {
        Vector3f v = new Vector3f(axisLen, 0, 0);
        Arrow a = new Arrow(v);
        Material mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        Geometry geom = new Geometry(n.getName() + "XAxis", a);
        geom.setMaterial(mat);
        n.attachChild(geom);
 
 
        //
        v = new Vector3f(0, axisLen, 0);
        a = new Arrow(v);
        mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Green);
        geom = new Geometry(n.getName() + "YAxis", a);
        geom.setMaterial(mat);
        n.attachChild(geom);
 
 
        //
        v = new Vector3f(0, 0, axisLen);
        a = new Arrow(v);
        mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom = new Geometry(n.getName() + "ZAxis", a);
        geom.setMaterial(mat);
        n.attachChild(geom);
    }
 
    public static void showVector3fArrow(AssetManager am, Node n, Vector3f v, ColorRGBA color, String name) {
        Arrow a = new Arrow(v);
        a.setLineWidth(4);
        Material mat = MaterialUtils.makeMaterial(am, "Common/MatDefs/Misc/Unshaded.j3md", color);
        Geometry geom = makeGeometry(a, mat, name);
        n.attachChild(geom);
    }
 
    public static void showVector3fLine(AssetManager am, Node n, Vector3f v, ColorRGBA color, String name) {
        Line l = new Line(v.subtract(v), v);
        Material mat = MaterialUtils.makeMaterial(am, "Common/MatDefs/Misc/Unshaded.j3md", color);
        Geometry geom = makeGeometry(l, mat, name);
        n.attachChild(geom);
    }
 
    public static void attachSkeleton(AssetManager am, Node player, AnimControl control) {
        SkeletonDebugger skeletonDebug = new SkeletonDebugger("skeleton", control.getSkeleton());
        Material mat2 = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Yellow);
        mat2.getAdditionalRenderState().setDepthTest(false);
        skeletonDebug.setMaterial(mat2);
        player.attachChild(skeletonDebug);
    }
 

    public static void attachWireFrameDebugGrid(AssetManager assetManager, Node n, Vector3f pos, Integer size, ColorRGBA color)
    {
        Geometry g = new Geometry("wireFrameDebugGrid", new Grid(size, size, 1.0f));//1WU
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        g.center().move(pos);
        n.attachChild(g);
    }
    
    
    
    public static void attachCoordinateAxes(Vector3f pos, AssetManager am, Node n){
        Arrow arrow = new Arrow(Vector3f.UNIT_X);
        arrow.setLineWidth(3);
        putShape(arrow, ColorRGBA.Red, am ,n).setLocalTranslation(pos);

        arrow = new Arrow(Vector3f.UNIT_Y);
        arrow.setLineWidth(3);
        putShape(arrow, ColorRGBA.Green, am ,n).setLocalTranslation(pos);

        arrow = new Arrow(Vector3f.UNIT_Z);
        arrow.setLineWidth(3);
        putShape(arrow, ColorRGBA.Blue, am ,n).setLocalTranslation(pos);
   }
    
    public static void simpleArray(Vector3f pos, Vector3f normal, AssetManager am, Node n){
        Arrow arrow = new Arrow(normal);
        arrow.setLineWidth(3);
        putShape(arrow, ColorRGBA.Red, am ,n).setLocalTranslation(pos);
        //geom.setLocalScale(new Vector3f(2, 2, 2));
   }    

   public static Geometry putShape(Mesh shape, ColorRGBA color, AssetManager assetManager, Node n){
        Geometry g = new Geometry("coordinate axis", shape);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        g.setMaterial(mat);
        n.attachChild(g);
        return g;
   }      
    
    
}
