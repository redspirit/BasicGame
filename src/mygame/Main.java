package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.audio.AudioNode;
import com.jme3.bounding.BoundingVolume;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix4f;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Cylinder;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.collision.Collidable;
import com.jme3.collision.UnsupportedCollisionException;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    private Spatial sceneModel;
    private BulletAppState bulletAppState;
    private RigidBodyControl landscape;
    private CharacterControl player;
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false;

    //Temporary vectors used on each frame.
    //They here to avoid instanciating new vectors on each frame
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();  
    private AudioNode audio_gun;
    private Geometry triggerArea;
    private TriggerAreaComposer TAC = new TriggerAreaComposer();
    private float PI = (float) Math.PI;

        
    public static void main(String[] args) {
        
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1024, 768);  
        settings.setFrequency(60);
        settings.setTitle("Spirit`s game");
        settings.setSettingsDialogImage("/Textures/logo.jpg");
        
        Main app = new Main();
        //app.setShowSettings(false);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
      
        initCrossHairs();
        initAudio();

         /** Set up Physics */
         bulletAppState = new BulletAppState();
         stateManager.attach(bulletAppState);
         //bulletAppState.getPhysicsSpace().enableDebug(assetManager);

         // We re-use the flyby camera for rotation, while positioning is handled by physics
         viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
         
         //flyCam.setRotationSpeed(0.5f);
         flyCam.setMoveSpeed(10);
         setUpKeys();
         setUpLight();
         
         cam.setLocation(new Vector3f(0, 50, 15));   
         
         
         // We load the scene from the zip file and adjust its size.
         assetManager.registerLocator("town.zip", ZipLocator.class);
         sceneModel = assetManager.loadModel("main.scene");
         sceneModel.setLocalScale(2f);
         sceneModel.setName("main scene");

         // We set up collision detection for the scene by creating a
         // compound collision shape and a static RigidBodyControl with mass zero.
         CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node) sceneModel);
         landscape = new RigidBodyControl(sceneShape, 0);
         sceneModel.addControl(landscape);

         // We set up collision detection for the player by creating
         // a capsule collision shape and a CharacterControl.
         // The CharacterControl offers extra settings for
         // size, stepheight, jumping, falling, and gravity.
         // We also put the player in its starting position.
         CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
         player = new CharacterControl(capsuleShape, 0.05f);
         player.setJumpSpeed(10);
         player.setFallSpeed(30);
         player.setGravity(30);
         player.setPhysicsLocation(new Vector3f(0, 10, 0));

         
         // We attach the scene and the player to the rootnode and the physics space,
         // to make them appear in the game world.
         rootNode.attachChild(sceneModel);
         bulletAppState.getPhysicsSpace().add(landscape);
         bulletAppState.getPhysicsSpace().add(player);
         
         makeTrigerArea();
    }
    
//    private void createMesh(){
//    
//        Vector3f dot;
//        
//        for(int x = 1; x <= 160; x++){
//            for(int y = 1; y <= 130; y++){
//        
//                dot = new Vector3f(x / 3 - 20, 1f, y / 3 - 35);
//                
//                if(ta.isIntersects(dot)) {
//                    //makeMark(ColorRGBA.Red, dot);
//                } else {
//                    //makeMark(ColorRGBA.Green, dot);
//                }                
//                
//            }
//        }
//        
//        
//    }
    
    private void setUpLight() {
        // We add light so we see the scene
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        rootNode.addLight(dl);
    }    
    
    private void setUpKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

        inputManager.addListener(actionListener, "Left");
        inputManager.addListener(actionListener, "Right");
        inputManager.addListener(actionListener, "Up");
        inputManager.addListener(actionListener, "Down");
        inputManager.addListener(actionListener, "Jump");
        inputManager.addListener(actionListener, "Shoot");
    }
    
    private void initAudio() {
        /* gun shot sound is to be triggered by a mouse click. */
        audio_gun = new AudioNode(assetManager, "Sounds/Effects/Gun.wav", false);
        audio_gun.setPositional(false);
        audio_gun.setLooping(false);
        audio_gun.setVolume(0.3f);
        rootNode.attachChild(audio_gun);
    }      
    
    protected void makeMark(ColorRGBA color, Vector3f center) {
        Sphere sphere = new Sphere(4, 4, 0.2f);
        Geometry mark = new Geometry("BOOM!", sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", color);
        mark.setMaterial(mark_mat);
        mark.setLocalTranslation(center);
        rootNode.attachChild(mark);
    }    
    
    
    protected void makeTrigerArea() {

        
        Geometry ta1 = new Geometry("trigger area", new Box(3f, 1f, 4f) );
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Blue);
        ta1.setMaterial(mat1);
        ta1.setLocalTranslation(new Vector3f(0f, 0.0f, -15f));
        ta1.rotate(0, 0f * PI, 0);
        TAC.addGeometry(ta1, "BLUE PLACE");
        rootNode.attachChild(ta1);


        Geometry ta2 = new Geometry("trigger area", new Box(3f, 1f, 4f) );
        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat2.setColor("Color", ColorRGBA.Green);
        ta2.setMaterial(mat2);
        ta2.setLocalTranslation(new Vector3f(-15f, 0.0f, -13f));
        ta2.rotate(0, 0.25f * PI, 0);
        TAC.addGeometry(ta2, "GREEN PLACE");
        rootNode.attachChild(ta2);

        
        Geometry ta3 = new Geometry("trigger area", new Box(3f, 1f, 4f) );
        Material mat3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat3.setColor("Color", ColorRGBA.Red);
        ta3.setMaterial(mat3);
        ta3.setLocalTranslation(new Vector3f(15f, 0.0f, -13f));
        ta3.rotate(0, -0.25f * PI, 0);
        TAC.addGeometry(ta3, "RED PLACE");
        rootNode.attachChild(ta3);
        
        
        TAC.setListener(new TriggerAreaComposer.EventHandler() {

            public void hoverIn(TriggerArea ta) {
                System.out.println("Hover IN " + ta.toString());
            }

            public void hoverOut(TriggerArea ta) {
                System.out.println("Hover OUT " + ta.toString()); 
            }
        });
        
    }       
    
    protected void initCrossHairs() {
        setDisplayStatView(false);
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+");
        ch.setLocalTranslation(
          settings.getWidth() / 2 - ch.getLineWidth()/2, 
          settings.getHeight() / 2 + ch.getLineHeight()/2, 
          0);
        guiNode.attachChild(ch);
    }         
     
    protected void makeBulletHole(Vector3f pos, Vector3f nor2) {
        // todo нужно создать отдельную модельку с текстурой в редакторе
        Geometry g = new Geometry("bullet hole", new Box(0.2f, 0.005f, 0.2f) );
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Textures/bullet_hole_2.png"));
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        //BlendMode.Color
        g.setQueueBucket(Bucket.Transparent);
        g.setMaterial(mat);
        g.setLocalTranslation(pos);
        g.rotateUpTo(nor2);
        rootNode.attachChild(g);
    }     
    
    
    private ActionListener actionListener = new ActionListener() {

        public void onAction(String binding, boolean isPressed, float tpf) {

            if (binding.equals("Shoot") && !isPressed) {
                // 1. Reset results list.
                CollisionResults results = new CollisionResults();
                // 2. Aim the ray from cam loc to cam direction.
                Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                // 3. Collect intersections between Ray and Shootables in results list.
                sceneModel.collideWith(ray, results);

                //float dist = results.getCollision(i).getDistance();
                //Vector3f pt = results.getCollision(i).getContactPoint();
                //String hit = results.getCollision(i).getGeometry().getName();

                // 5. Use the results (we mark the hit object)
                if (results.size() > 0) {

                  //Geometry mark = makeMark();
                    

                  CollisionResult closest = results.getClosestCollision();

                  Vector3f normal = closest.getContactNormal();
                  Vector3f point = closest.getContactPoint();
                  
                  
                  //Debug.simpleArray(point, normal, assetManager, rootNode);
                  
                  makeBulletHole(point, normal);
                  
                  
                  //System.out.println(closest.getContactNormal().toString());
                  //closest.
                  
                  //mark.setLocalTranslation(closest.getContactPoint());
                  //rootNode.attachChild(mark);
                  
                }

                audio_gun.playInstance();

              }      


            if (binding.equals("Left")) {
              left = isPressed;
            } else if (binding.equals("Right")) {
              right= isPressed;
            } else if (binding.equals("Up")) {
              up = isPressed;
            } else if (binding.equals("Down")) {
              down = isPressed;
            } else if (binding.equals("Jump")) {
              if (isPressed) { player.jump(); }
            }
        }    
        
   };    
    
    @Override
    public void simpleUpdate(float tpf) {

        float speed = 0.2f;
        
        camDir.set(cam.getDirection()).multLocal(speed);
        camLeft.set(cam.getLeft()).multLocal(speed);
        walkDirection.set(0, 0, 0);
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }


        //float axisY = walkDirection.getY();
        //if(axisY > 0.05f) axisY = 0.05f;
        //if(axisY < -0.05f) axisY = -0.05f;
        walkDirection.setY(0f);

        //player.getCollisionShape();

        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation());        
        
  
//        if(ta.isIntersects(cam)) {
//            ta.getGeometry().getMaterial().setColor("Color", ColorRGBA.Red);
//        } else {
//            ta.getGeometry().getMaterial().setColor("Color", ColorRGBA.Blue);
//        }
        
        //ta.getGeometry().rotate(0f,  tpf, 0f);
  
        TAC.check(cam);
        

        fpsText.setText("debug: " + TAC.debugText);
        
    }
    
}
