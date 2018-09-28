package cs445final;
/**
 *File: Basic3D.java
 *class:CS445-Computer Graphics 
 *Assignment:Final Project
 *Date last modified:Nov28
 *Authors:Daniel Lopez,Roberto Ruiz,Monica Say, Bryce Metcalf,and David Escobedo
 *Purpose: This class is used to create the window where our world will be displayed, intialize gl, 
 * and where the program is started. 
 */
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import javax.swing.ImageIcon;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

public class Basic3D {
    private int type;
    private int displayHeight;
    private int displayWidth;
private FPCameraController fp;
private DisplayMode displayMode;
private ByteBuffer[] icons = new ByteBuffer[3];
private FloatBuffer lightPosition;
private FloatBuffer whiteLight;

public void start() {
try {
createWindow();
initGL();
fp.init();
fp.gameLoop();//render();
} catch (Exception e) {
e.printStackTrace();
}
}
private void initLightArrays() {
lightPosition = BufferUtils.createFloatBuffer(4);
lightPosition.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
whiteLight = BufferUtils.createFloatBuffer(4);
whiteLight.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();
}
private void createWindow() throws Exception{
Display.setFullscreen(false);

DisplayMode d[] =
Display.getAvailableDisplayModes();
for (int i = 0; i < d.length; i++) {
if (d[i].getWidth() == displayWidth
&& d[i].getHeight() == displayHeight
&& d[i].getBitsPerPixel() == 32) {
displayMode = d[i];
break;
}

}
Display.setDisplayMode(displayMode);
Display.setTitle("Minecraft");
Display.create();
}
private void initGL() {
    glEnableClientState(GL_VERTEX_ARRAY);
glEnableClientState(GL_COLOR_ARRAY);
glEnable(GL_DEPTH_TEST);     
glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

glMatrixMode(GL_PROJECTION);
glLoadIdentity();
     glEnable(GL_TEXTURE_2D);
        glEnableClientState (GL_TEXTURE_COORD_ARRAY);
        
GLU.gluPerspective(100.0f, (float)displayMode.getWidth()/(float)
displayMode.getHeight(), 0.1f, 300.0f);
glMatrixMode(GL_MODELVIEW);
glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

initLightArrays();
glLight(GL_LIGHT0, GL_POSITION, lightPosition); //sets our light’s position
glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);//sets our specular light
glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);//sets our diffuse light
glLight(GL_LIGHT0, GL_AMBIENT, whiteLight);//sets our ambient light
glEnable(GL_LIGHTING);//enables our lighting
glEnable(GL_LIGHT0);//enables light0
}
/*
public static void main(String[] args) {
Basic3D basic = new Basic3D();
basic.start();
}
*/
public Basic3D(){
    displayHeight = 480;
    displayWidth = 640; 
    type = 0;
    fp = new FPCameraController(0f,0f,0f,0);
}
public Basic3D(int w, int h, int t){
    type = t;
    displayHeight = h;
    displayWidth = w; 
    fp = new FPCameraController(0f,0f,0f,type);
    
}
}
