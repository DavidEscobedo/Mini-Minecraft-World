package cs445final;
/**
 *File: Chunk.java
 *class:CS445-Computer Graphics 
 *Assignment:Final Project
 *Date last modified:Nov28
 *Authors:Daniel Lopez,Roberto Ruiz,Monica Say, Bryce Metcalf,and David Escobedo
 *Purpose: This Chunk class is used to represents sets of Blocks used to form our
 *world.There were slight experiments with offsets and texture mapping to create
 *inner golden nodes.Uses the terrain.png to set the textures of the grass, water,stone sand,etc.
 */
import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunk {
    private int type;
    //64 CHUNK WORLD
    static final int CHUNK_SIZE = 32;
    static final int CUBE_LENGTH = 2;
    static final float maxPersistance = 0.04f;
    static final float minPersistance = 0.06f;
	//this holds a 3dimensional array of blocks.
    private Block[][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int VBOTextureHandle;
    private int StartX, StartY, StartZ;
    private Random r;
    private Texture texture;
    private int maxSandHeight = 35;
    private int maxWaterHeight = 35;
    
    private int maxRandomHeight = 25;
    private int minSeed = 15;
    private int levelSize = 3;
    //method:render
//description: does the rendering
    public void render() {
        glPushMatrix();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glColorPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBindTexture(GL_TEXTURE_2D, 1);
        glTexCoordPointer(2, GL_FLOAT, 0, 0L);

        glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
        glPopMatrix();
    }

//Method: RebuildMesh
//Description: called once to create the cubes which will continue to be rendered
    public void rebuildMesh(float startX, float startY, float startZ) {

        Random random = new Random();
        float persistance = maxPersistance * random.nextFloat() + minPersistance;
        int seed = minSeed * random.nextInt();
        //bind sand and water blocks which will appear in the uppermost levels
        int[][] boundaries = new int[2][4];
        //sand:
        //x-min
        boundaries[0][0] = r.nextInt(maxRandomHeight);
        //x-max
        boundaries[0][1] = r.nextInt(maxRandomHeight)+maxSandHeight;
        //z-min
        boundaries[0][2] = r.nextInt(maxRandomHeight);
        //z-max
        boundaries[0][3] = r.nextInt(maxRandomHeight)+maxSandHeight;
        
        
        //water:
         //x-min
        boundaries[1][0] = r.nextInt(maxRandomHeight);
        //x-max
        boundaries[1][1] = r.nextInt(maxRandomHeight)+maxWaterHeight;
        //z-min
        boundaries[1][2] = r.nextInt(maxRandomHeight);
        //z-max
        boundaries[1][3] = r.nextInt(maxRandomHeight)+maxWaterHeight;
        
        
        
        SimplexNoise noise = new SimplexNoise(CHUNK_SIZE, persistance, seed);

        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer(
                (CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer(
                (CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer(
                (CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                
                //y correlates to height, x and z plane will work similarily to x-y plane in 2D 
                //ass we build upwards with y.
            for (int y = 0; y < CHUNK_SIZE; y++) {
                
                
                    if(y >= levelSize){

                    //used to get a random height from the noise class in order to
                    //make natural looking mountains
                    int height = ((int)startY + (int)Math.abs((CHUNK_SIZE * noise.getNoise(x,z)))*CUBE_LENGTH)-1;
                    //no need to go further,if we proceed this would create a flat surface
                    if (y > height) break;
                    
                  
                    
                    
                    //upper layer will always be grass
                    if(y == height ){
                        if(type == 0){
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                        }
                        else if(type == 1){
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_NetherRack);
                        }
                        else{
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_EndStone);
                        }
                    }
                    if(height > levelSize && y < height) 
                        
                    {
                        if(type ==0)Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                        else if(type == 1){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_SoulSand);
                        
                        }
                        else {
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_EndStone);
                        }
                    
                    }
                   //checks for boundaries of sand/water
                   //checks for boundaries of sand/water
                    if( checkBoundaries(0, x,y,z,boundaries)){
                        if(type == 0){
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Water);
                        }
                        else if(type == 1){
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Lava);
                        }
                        else{
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Void);
                        }
                    }
                     //checks for boundaries of sand/water
                    else if( checkBoundaries(1, x,y,z,boundaries)){
                        if(type == 0){
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Sand);
                        }
                        else{
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Gravel);
                        }
                    }
                    
                    /* TODO Dirt and Stone for Lower Layerss of Map
                    
                    */
                    
                    
                    
                    }
                    
                    VertexPositionData.put(createCube(
                            (float) (startX + x * CUBE_LENGTH-50),
                            //needed to change y component so that user starts above ground
                            (float) (y * CUBE_LENGTH + (CHUNK_SIZE * .8)-100),
                            (float) (startZ + z * CUBE_LENGTH-50)));

                    VertexColorData.put(createCubeVertexCol(getCubeColor(
                            Blocks[x][y][z])));

                    VertexTextureData.put(createTexCube( 0f,
                             0f, Blocks[(x)][y][z]));
                }
            }
        }
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
//method: checkBoundaries
//because sand and water must fight for land they need boundaries to show the program
//which spots are reserved for which block, this method tells the rebuildmesh if 
 //certain coordinates entail a certain block type. 0 is sand 1 is water.
    private boolean checkBoundaries(int type, int x, int y, int z, int[][] boundaries )
    {
       
        if( x >= boundaries[type][0] && x<= boundaries[type][1] && z >= boundaries[type][2] && z <= boundaries[type][3] && y == levelSize)
        {
            return true;
        }
        return false;
    }
    private float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i% CubeColorArray.length];
        }
        return cubeColors;
    }

  //method: createCube
    //Description: Creates Cubes of the types we defined
    public static float[] createCube(float x, float y, float z) {
        int offset = CUBE_LENGTH / 2;
        return new float[]{
            // TOP QUAD
            x + offset, y + offset, z,
            x - offset, y + offset, z,
            x - offset, y + offset, z - CUBE_LENGTH,
            x + offset, y + offset, z - CUBE_LENGTH,
            // BOTTOM QUAD
            x + offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z,
            x + offset, y - offset, z,
            // FRONT QUAD
            x + offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            // BACK QUAD
            x + offset, y - offset, z,
            x - offset, y - offset, z,
            x - offset, y + offset, z,
            x + offset, y + offset, z,
            // LEFT QUAD
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z,
            x - offset, y - offset, z,
            x - offset, y - offset, z - CUBE_LENGTH,
            // RIGHT QUAD
            x + offset, y + offset, z,
            x + offset, y + offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z};
    }

    //Method: getCubeColor
    //Purpose: Default cube color
    private float[] getCubeColor(Block block) {
        return new float[]{1, 1, 1};
    }

    //method: Chunk
    //Purpose: Constructor which starts the chunk making process
    public Chunk(int startX, int startY, int startZ, int t) {
        
        levelSize = startY-1;
        
        type = t;
        try {
            texture = TextureLoader.getTexture("PNG",
                    ResourceLoader.getResourceAsStream("terrain.png"));
        } catch (Exception e) {
            System.out.print("No Texture File");
        }
        r = new Random();
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];

        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    float rand = r.nextFloat();
                   
                    if (y == 0) {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Bedrock);
                    } else if (rand > 0.5f && (y > 0 && y <levelSize)) {
                        if(type == 0){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
                        }
                        else if(type == 1){
                           Blocks[x][y][z] = new Block(Block.BlockType.BlockType_NetherBrick); 
                        }
                        else{
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Obsidian);
                        }
                        
                    } else if (rand <= 0.5f && (y > 0 && y <levelSize)) {
                        if(type == 0){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                        }
                        else if(type == 1){
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_SoulSand);
                        }
                        else{
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_EndStone);
                        }
                    } else if (rand > 0.4f) {
                        if(type == 0){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                        }
                        else if(type == 1){
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_NetherRack);
                        }
                        else{
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_EndStone);
                        }
                    } else {
                        if(type == 0){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Sand);
                        }
                        else {
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Gravel);
                        }
                    } 
                }
            }
        }

        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        
        
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        //rebuildmesh called once
        rebuildMesh(startX, startY, startZ);
    }
    //method: createTexCube
    //Description: Returns Textured Cube, somewhat tested to check offset variability.

    public static float[] createTexCube(float x, float y, Block block) {

        float offset = (1024f / 16) / 1024f;
        switch (block.GetID()) {
            case 0: //grass (Most Used)
                return new float[]{
                    //bottom
                    x + offset * 3, y + offset * 10,
                    x + offset * 2, y + offset * 10,
                    x + offset * 2, y + offset * 9,
                    x + offset * 3, y + offset * 9,
                    // top
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // front
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    // back
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    // left
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    // right
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1};
            case 1: //sand 
                return new float[]{
                   
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    // top
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    // front
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    // back
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    // left
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    // right
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2};
            case 2: //water (slightly experimented)
                return new float[]{
                    // bottom
                    x + offset * 14, y + offset * 12,
                    x + offset * 15, y + offset * 12,
                    x + offset * 14, y + offset * 13,
                    x + offset * 15, y + offset * 13,
                    // top
                    x + offset * 15, y + offset * 12,
                    x + offset * 16, y + offset * 12,
                    x + offset * 15, y + offset * 13,
                    x + offset * 16, y + offset * 13,
                    // front
                    x + offset * 15, y + offset * 12,
                    x + offset * 16, y + offset * 12,
                    x + offset * 16, y + offset * 13,
                    x + offset * 15, y + offset * 13,
                    // back
                    x + offset * 15, y + offset * 12,
                    x + offset * 16, y + offset * 12,
                    x + offset * 16, y + offset * 13,
                    x + offset * 15, y + offset * 13,
                    // left  
                    x + offset * 15, y + offset * 12,
                    x + offset * 16, y + offset * 12,
                    x + offset * 16, y + offset * 13,
                    x + offset * 15, y + offset * 13,
                    // right
                    x + offset * 15, y + offset * 12,
                    x + offset * 16, y + offset * 12,
                    x + offset * 16, y + offset * 13,
                    x + offset * 15, y + offset * 13};
            case 3: //dirt 
                return new float[]{
                    // bottom
                    x + offset * 3, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    //top
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // front
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // back
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // left
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // right
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0};
            case 4: //stone
                System.out.println("Stone");
                return new float[]{
                    // bottom
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    // top
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    // front
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    // back
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    // left
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    // right
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1};
            case 7:
                 System.out.println("NetherRack");
                return new float[]{
                    // bottom
                    x + offset * 7, y + offset * 6,
                    x + offset * 8, y + offset * 6,
                    x + offset * 8, y + offset * 7,
                    x + offset * 7, y + offset * 7,
                    // top
                    x + offset * 7, y + offset * 6,
                    x + offset * 8, y + offset * 6,
                    x + offset * 8, y + offset * 7,
                    x + offset * 7, y + offset * 7,
                    // front
                    x + offset * 7, y + offset * 6,
                    x + offset * 8, y + offset * 6,
                    x + offset * 8, y + offset * 7,
                    x + offset * 7, y + offset * 7,
                    // back
                    x + offset * 7, y + offset * 6,
                    x + offset * 8, y + offset * 6,
                    x + offset * 8, y + offset * 7,
                    x + offset * 7, y + offset * 7,
                    // left
                    x + offset * 7, y + offset * 6,
                    x + offset * 8, y + offset * 6,
                    x + offset * 8, y + offset * 7,
                    x + offset * 7, y + offset * 7,
                    // right
                    x + offset * 7, y + offset * 6,
                    x + offset * 8, y + offset * 6,
                    x + offset * 8, y + offset * 7,
                    x + offset * 7, y + offset * 7};
            case 8:
                 System.out.println("Soul Sand");
                return new float[]{
                    // bottom
                    x + offset * 8, y + offset * 6,
                    x + offset * 9, y + offset * 6,
                    x + offset * 9, y + offset * 7,
                    x + offset * 8, y + offset * 7,
                    // top
                    x + offset * 8, y + offset * 6,
                    x + offset * 9, y + offset * 6,
                    x + offset * 9, y + offset * 7,
                    x + offset * 8, y + offset * 7,
                    // front
                    x + offset * 8, y + offset * 6,
                    x + offset * 9, y + offset * 6,
                    x + offset * 9, y + offset * 7,
                    x + offset * 8, y + offset * 7,
                    // back
                    x + offset * 8, y + offset * 6,
                    x + offset * 9, y + offset * 6,
                    x + offset * 9, y + offset * 7,
                    x + offset * 8, y + offset * 7,
                    // left
                    x + offset * 8, y + offset * 6,
                    x + offset * 9, y + offset * 6,
                    x + offset * 9, y + offset * 7,
                    x + offset * 8, y + offset * 7,
                    // right
                    x + offset * 8, y + offset * 6,
                    x + offset * 9, y + offset * 6,
                    x + offset * 9, y + offset * 7,
                    x + offset * 8, y + offset * 7};
            case 9: 
                 System.out.println("Lava");
                return new float[]{
                    // bottom
                    x + offset * 13, y + offset * 14,
                    x + offset * 14, y + offset * 14,
                    x + offset * 14, y + offset * 15,
                    x + offset * 13, y + offset * 15,
                    // top
                    x + offset * 13, y + offset * 14,
                    x + offset * 14, y + offset * 14,
                    x + offset * 14, y + offset * 15,
                    x + offset * 13, y + offset * 15,
                    // front
                    x + offset * 13, y + offset * 14,
                    x + offset * 14, y + offset * 14,
                    x + offset * 14, y + offset * 15,
                    x + offset * 13, y + offset * 15,
                    // back
                    x + offset * 13, y + offset * 14,
                    x + offset * 14, y + offset * 14,
                    x + offset * 14, y + offset * 15,
                    x + offset * 13, y + offset * 15,
                    // left
                    x + offset * 13, y + offset * 14,
                    x + offset * 14, y + offset * 14,
                    x + offset * 14, y + offset * 15,
                    x + offset * 13, y + offset * 15,
                    // right
                    x + offset * 13, y + offset * 14,
                    x + offset * 14, y + offset * 14,
                    x + offset * 14, y + offset * 15,
                    x + offset * 13, y + offset * 15};
            case 10:
                 System.out.println("Gravel");
                return new float[]{
                    // bottom
                    x + offset * 3, y + offset * 1,
                    x + offset * 4, y + offset * 1,
                    x + offset * 4, y + offset * 2,
                    x + offset * 3, y + offset * 2,
                    // top
                    x + offset * 3, y + offset * 1,
                    x + offset * 4, y + offset * 1,
                    x + offset * 4, y + offset * 2,
                    x + offset * 3, y + offset * 2,
                    // front
                    x + offset * 3, y + offset * 1,
                    x + offset * 4, y + offset * 1,
                    x + offset * 4, y + offset * 2,
                    x + offset * 3, y + offset * 2,
                    // back
                    x + offset * 3, y + offset * 1,
                    x + offset * 4, y + offset * 1,
                    x + offset * 4, y + offset * 2,
                    x + offset * 3, y + offset * 2,
                    // left
                    x + offset * 3, y + offset * 1,
                    x + offset * 4, y + offset * 1,
                    x + offset * 4, y + offset * 2,
                    x + offset * 3, y + offset * 2,
                    // right
                    x + offset * 3, y + offset * 1,
                    x + offset * 4, y + offset * 1,
                    x + offset * 4, y + offset * 2,
                    x + offset * 3, y + offset * 2};
            case 11:
                 System.out.println("Nether Brick");
                return new float[]{
                    // bottom
                    x + offset * 0, y + offset * 14,
                    x + offset * 1, y + offset * 14,
                    x + offset * 1, y + offset * 15,
                    x + offset * 0, y + offset * 15,
                    // top
                    x + offset * 0, y + offset * 14,
                    x + offset * 1, y + offset * 14,
                    x + offset * 1, y + offset * 15,
                    x + offset * 0, y + offset * 15,
                    // front
                    x + offset * 0, y + offset * 14,
                    x + offset * 1, y + offset * 14,
                    x + offset * 1, y + offset * 15,
                    x + offset * 0, y + offset * 15,
                    // back
                    x + offset * 0, y + offset * 14,
                    x + offset * 1, y + offset * 14,
                    x + offset * 1, y + offset * 15,
                    x + offset * 0, y + offset * 15,
                    // left
                    x + offset * 0, y + offset * 14,
                    x + offset * 1, y + offset * 14,
                    x + offset * 1, y + offset * 15,
                    x + offset * 0, y + offset * 15,
                    // right
                    x + offset * 0, y + offset * 14,
                    x + offset * 1, y + offset * 14,
                    x + offset * 1, y + offset * 15,
                    x + offset * 0, y + offset * 15};
            case 12:
                 System.out.println("End Stone");
                return new float[]{
                    // bottom
                    x + offset * 15, y + offset * 10,
                    x + offset * 16, y + offset * 10,
                    x + offset * 16, y + offset * 11,
                    x + offset * 15, y + offset * 11,
                    // top
                    x + offset * 15, y + offset * 10,
                    x + offset * 16, y + offset * 10,
                    x + offset * 16, y + offset * 11,
                    x + offset * 15, y + offset * 11,
                    // front
                    x + offset * 15, y + offset * 10,
                    x + offset * 16, y + offset * 10,
                    x + offset * 16, y + offset * 11,
                    x + offset * 15, y + offset * 11,
                    // back
                    x + offset * 15, y + offset * 10,
                    x + offset * 16, y + offset * 10,
                    x + offset * 16, y + offset * 11,
                    x + offset * 15, y + offset * 11,
                    // left
                    x + offset * 15, y + offset * 10,
                    x + offset * 16, y + offset * 10,
                    x + offset * 16, y + offset * 11,
                    x + offset * 15, y + offset * 11,
                    // right
                    x + offset * 15, y + offset * 10,
                    x + offset * 16, y + offset * 10,
                    x + offset * 16, y + offset * 11,
                    x + offset * 15, y + offset * 11};
            case 13:
                 System.out.println("Void");
                return new float[]{
                    // bottom
                    x + offset * 7, y + offset * 10,
                    x + offset * 8, y + offset * 10,
                    x + offset * 8, y + offset * 11,
                    x + offset * 7, y + offset * 11,
                    // top
                    x + offset * 7, y + offset * 10,
                    x + offset * 8, y + offset * 10,
                    x + offset * 8, y + offset * 11,
                    x + offset * 7, y + offset * 11,
                    // front
                    x + offset * 7, y + offset * 10,
                    x + offset * 8, y + offset * 10,
                    x + offset * 8, y + offset * 11,
                    x + offset * 7, y + offset * 11,
                    // back
                    x + offset * 7, y + offset * 10,
                    x + offset * 8, y + offset * 10,
                    x + offset * 8, y + offset * 11,
                    x + offset * 7, y + offset * 11,
                    // left
                    x + offset * 7, y + offset * 10,
                    x + offset * 8, y + offset * 10,
                    x + offset * 8, y + offset * 11,
                    x + offset * 7, y + offset * 11,
                    // right
                    x + offset * 7, y + offset * 10,
                    x + offset * 8, y + offset * 10,
                    x + offset * 8, y + offset * 11,
                    x + offset * 7, y + offset * 11};
            case 14:
                System.out.println("Obsidian");
                return new float[]{
                    // bottom
                    x + offset * 7, y + offset * 11,
                    x + offset * 8, y + offset * 11,
                    x + offset * 8, y + offset * 12,
                    x + offset * 7, y + offset * 12,
                    // top
                    x + offset * 7, y + offset * 11,
                    x + offset * 8, y + offset * 11,
                    x + offset * 8, y + offset * 12,
                    x + offset * 7, y + offset * 12,
                    // front
                    x + offset * 7, y + offset * 11,
                    x + offset * 8, y + offset * 11,
                    x + offset * 8, y + offset * 12,
                    x + offset * 7, y + offset * 12,
                    // back
                    x + offset * 7, y + offset * 11,
                    x + offset * 8, y + offset * 11,
                    x + offset * 8, y + offset * 12,
                    x + offset * 7, y + offset * 12,
                    // left
                    x + offset * 7, y + offset * 11,
                    x + offset * 8, y + offset * 11,
                    x + offset * 8, y + offset * 12,
                    x + offset * 7, y + offset * 12,
                    // right
                    x + offset * 7, y + offset * 11,
                    x + offset * 8, y + offset * 11,
                    x + offset * 8, y + offset * 12,
                    x + offset * 7, y + offset * 12};
                
            case 5:
            default: //bedrock 
                    System.out.println("bottom");
                    
                return new float[]{
                    // bottom
                    
                 
                
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // top
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // front
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // back
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // left
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // right
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2};
        }
    }
    

}
