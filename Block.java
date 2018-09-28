package cs445final;
/**
 *File: Block.java
 *class:CS445-Computer Graphics 
 *Assignment:Final Project
 *Date last modified:Nov28
 *Authors:Daniel Lopez,Roberto Ruiz,Monica Say, Bryce Metcalf,and David Escobedo
 *Purpose: The purpose of this class is to represent a Block.This class will be 
 *used in Chunk.java.
 */
public class Block {
    
private boolean IsActive
;
private BlockType Type;
private float x,y,z
;
public enum BlockType
{
//OverWorld
BlockType_Grass(0),
BlockType_Sand(1),
BlockType_Water(2),
BlockType_Dirt(3),
BlockType_Stone(4),
//default
BlockType_Bedrock(5),
BlockType_Default(6),
//nether
BlockType_NetherRack(7),
BlockType_SoulSand(8),
BlockType_Lava(9),
BlockType_Gravel(10),
BlockType_NetherBrick(11),
//End
BlockType_EndStone(12),
BlockType_Void(13),
BlockType_Obsidian(14),

;
private int BlockID;
BlockType
(int i) {
BlockID
=i;
}
public int GetID(){
return BlockID
;
}
//method: setID
//description: sets ID
public void SetID
(int i){
BlockID = i;
}
}
    //constructor: sets the block type. 
public Block(BlockType type){
Type= type;
}
//method: setCoords
//description: sets coordinates
public void setCoords(float x, float y, float z){
this.x = x;
this.y = y;
this.z = z;
}
//method: isActive
//description: returns if the block is active
public boolean IsActive() {
return IsActive;
}
//method: setActive
//description: sets active status
public void SetActive(boolean active){
IsActive=active;
}
//method: getID()
//description: returns ID
public int GetID(){
return Type.GetID();
}
}
