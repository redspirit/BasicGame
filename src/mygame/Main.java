package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
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
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

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
        
    
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
      
        initCrossHairs(); // a "+" in the middle of the screen to help aiming
        initAudio();

         /** Set up Physics */
         bulletAppState = new BulletAppState();
         stateManager.attach(bulletAppState);
         //bulletAppState.getPhysicsSpace().enableDebug(assetManager);

         // We re-use the flyby camera for rotation, while positioning is handled by physics
         viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
         flyCam.setMoveSpeed(80);
         setUpKeys();
         setUpLight();

         // We load the scene from the zip file and adjust its size.
         assetManager.registerLocator("town.zip", ZipLocator.class);
         sceneModel = assetManager.loadModel("main.scene");
         sceneModel.setLocalScale(2f);

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
    }

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
        audio_gun.setVolume(2);
        rootNode.attachChild(audio_gun);
    }      
    
    protected Geometry makeMark() {
        Sphere sphere = new Sphere(30, 30, 0.3f);
        Geometry mark = new Geometry("BOOM!", sphere);
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat.setColor("Color", ColorRGBA.Red);
        mark.setMaterial(mark_mat);
        return mark;
    }    
    
    protected void initCrossHairs() {
        setDisplayStatView(false);
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation(
          settings.getWidth() / 2 - ch.getLineWidth()/2, 
          settings.getHeight() / 2 + ch.getLineHeight()/2, 
          0);
        guiNode.attachChild(ch);
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
                // 4. Print the results
                System.out.println("----- Collisions? " + results.size() + "-----");
                for (int i = 0; i < results.size(); i++) {
                  // For each hit, we know distance, impact point, name of geometry.
                  float dist = results.getCollision(i).getDistance();
                  Vector3f pt = results.getCollision(i).getContactPoint();
                  String hit = results.getCollision(i).getGeometry().getName();
                }
                // 5. Use the results (we mark the hit object)
                if (results.size() > 0) {

                  Geometry mark = makeMark();

                  // The closest collision point is what was truly hit:
                  CollisionResult closest = results.getClosestCollision();
                  // Let's interact - we mark the hit with a red dot.
                  mark.setLocalTranslation(closest.getContactPoint());

                  rootNode.attachChild(mark);
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

        camDir.set(cam.getDirection()).multLocal(0.6f);
        camLeft.set(cam.getLeft()).multLocal(0.4f);
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


        float axisY = walkDirection.getY();
        if(axisY > 0.1f) axisY = 0.1f;
        if(axisY < -0.1f) axisY = -0.1f;
        walkDirection.setY(axisY);


        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation());        
        
    }    
    
}
