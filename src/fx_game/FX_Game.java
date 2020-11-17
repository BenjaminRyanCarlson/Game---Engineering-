/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fx_game;

import javafx.application.Application;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *
 * @author benja
 */

public class FX_Game extends Application {
    
    //HashMap to hold keyboard keys --- defaulted keyPressed == FALSE
    private final HashMap<KeyCode, Boolean> keys = new HashMap<>();
    
    // arraylist to hold the ojects created from the LevelMatrix.
    private final ArrayList<Node> wallList = new ArrayList<>(); 
    
    private BorderPane gamePanel = new BorderPane();
    
    //player
    private Node sprite;
    
    // level width and height.
    private int levelWidth;
    private int levelHeight;
    
    //objects and enemies
    private Node key;
    private Node weapon;
    private Node door;
    private Node enemy1; 
    private Node oxygen;
    private Node wall;
    
    // create the three levels
    levelMaker level1 = new levelMaker();
    levelMaker level2 = new levelMaker();
    levelMaker level3 = new levelMaker();
    
    // counter to know what level the player is on.
    int lvlCount = 0;
    
    // counter to know if the player has collected all the keys to progress to the next level.
    int keyCount = 0;
    
    //enemy velocity
    int v = 1;
    
    // badly named variable that represents the percentage the progress bar is at. 
    double seconds = 1.0;
    
    // used to pause and unpause the game. 
    public boolean pauseCount = true;
    
    // used to see if player is dead.
    public boolean dead = false;
    
        // O2 bar timer
    Timer pt = new Timer(true);
    
    //pause loop --- needed because once the main loop stopped the game no longer registered key presses because the update method is run in the main loop. 
    AnimationTimer pLoop = new AnimationTimer(){
        @Override
        public void handle(long now) {
           pause(); 
        }
    };
    //Main Loop
    AnimationTimer loop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                
              
            }
        };
    
    // press P to pause and O to unpause. Unexpected glitches in gameplay when upause was on the same keypress and pause. method is in the pLoop.
    private void pause(){
        if (keyPressed(KeyCode.P)){
            if (pauseCount == true){
                loop.stop();
                pauseCount = false;      
            }
        }
        if (keyPressed(KeyCode.O)){
            if(pauseCount == false){
                loop.start();
                pauseCount = true;
        }      
        }
    };
    //death alert when player runs out of air. 
    public void death(){
      if(seconds <= 0.05){
          Alert dead = new Alert(AlertType.INFORMATION);
                         dead.setTitle("You're dead.");
                         dead.setHeaderText("You had a good run...");
                         dead.setContentText("But in the end, you suffocated in the dark void of space.");  
                         dead.show();
                         // kill the timer
                         pt.cancel();

                         
      }     
}
    //death alert when player is killed by an enemy.
    public void killed(){
    Alert dead = new Alert(AlertType.INFORMATION);
                         
                         dead.setTitle("You're dead.");
                         dead.setHeaderText("You had a good run...");
                         dead.setContentText("But in the end, you became slime on the deck of your spaceship due to contact with a deadly alien.");  
                         dead.show();
                         // kill the timer
                         pt.cancel();
}
    // Class that holds the methods to create three distinct levels.
    private class levelMaker{
        
        //To create level 1.
        public void level1Setup() {
            //set the levelWidth equal to the length of the Level1 string array * 60, and the level height to 480. 
            // this is the size and with of the Canvas. 
        levelWidth = LevelMatrix.Level1[0].length() * 60;
        levelHeight = 480;
        
        // create all level objects
        key = createObject(240, 80, 20, 20, Color.BLACK, 0, 0);
        weapon = createObject(760, 40, 20, 20, Color.CORAL, 0, 0);
        door = createObject(1135, 360, 50, 80, Color.BISQUE, 0, 0);
        oxygen = createObject(900, 400, 20, 20, Color.BLUE, 100, 100);
        
        // iterate Level1 String array in the LevelMatrix
        for (int y = 0; y < LevelMatrix.Level1.length; y++){
            String line = LevelMatrix.Level1[y];
            // itereate through the characters in each String array and create a particular object(NODE) depending on the character recorded in that place in the array. 
            for (int x = 0; x < line.length(); x++){
                switch (line.charAt(x)){ //at each character in the line check for a 0 or 1. create wall object at each 1.
                    case '0':
                        break;
                    case '1':
                        wall = createObject(x*40, y*40, 40, 40, Color.SLATEGREY, 0, 0); 
                        wallList.add(wall);
                        break;
                  
                }                 
            }    
        }
        // create progressBar set at 100%
ProgressBar pb = new ProgressBar(seconds);

// Create tasks for Timer
TimerTask task = new TimerTask(){
    
    public void run(){
        double deathCount = 1.0;
        if(pauseCount == true){
            deathCount -= 0.03; // reduce the deathCount by 30% every second.
            pb.setProgress(seconds -= 0.03);  // reduce the ProgressBar by 30% every second.

        }
        if(deathCount <= 1){
    // run the death alert on JavaFX Thread. phew!!!
    Platform.runLater(() -> {
        death();
        
         
    });
                         
}
    }
    //pause the progressBar when game is paused.
    public void freeze(){
        if(pauseCount == false){
            pb.setProgress(seconds += 0);
            
        }       
    }
};

// schedule Timer to run the tasks every second.
pt.scheduleAtFixedRate(task, 1000, 1000);

// set progressBar height and width and put it in an HBox that is displayed in the gamePanel. 
pb.setMinWidth(160.0);
pb.setMinHeight(40.0);
HBox statusBox = new HBox(pb);
		statusBox.setPadding(new Insets(0, 0, 0, 40));
		gamePanel.getChildren().add(statusBox);      
                  
    }
 
        // Level 2 setup --- REFER TO LEVEL1 SETUP COMMENTS.
        public void level2Setup() {

        levelWidth = LevelMatrix.Level2[0].length() * 60;
        levelHeight = 480;
        key = createObject(240, 80, 20, 20, Color.BLACK, 0, 0);
        weapon = createObject(760, 100, 20, 20, Color.CORAL, 0, 0);
        door = createObject(1135, 360, 50, 80, Color.RED, 0, 0);
        oxygen = createObject(245, 245, 20, 20, Color.BLUE, 100, 100);
        // clear the wallList to be reloaded with level2 walls.
        wallList.clear();
        
        
        for (int y = 0; y < LevelMatrix.Level2.length; y++){
            String line = LevelMatrix.Level2[y];
            for (int x = 0; x < line.length(); x++){
                switch (line.charAt(x)){
                    case '0':
                        break;
                    case '1':
                        Node wall = createObject(x*40, y*40, 40, 40, Color.SLATEGREY, 0, 0);
                        wallList.add(wall);
                        break;
                  
                }
            }    
        }
        
        ProgressBar pb = new ProgressBar(seconds);
pb.setProgress(seconds);
TimerTask task = new TimerTask(){
    
    public void run(){
        double deathCount = 1.0;
        if(pauseCount == true){
            deathCount -= 0.03;
           pb.setProgress(seconds -= 0.03);  

        }
        if(deathCount <= 1){
    // run the death alert on JavaFX Thread. phew!!!
    Platform.runLater(() -> {
        death();
         
    });
                         
}
    }
    //pause the bar
    public void freeze(){
        if(pauseCount == false){
            pb.setProgress(seconds += 0);
            
        }       
    }
};


pt.scheduleAtFixedRate(task, 1000, 1000);

pb.setMinWidth(160.0);
pb.setMinHeight(40.0);
HBox statusBox = new HBox(pb);
		statusBox.setPadding(new Insets(0, 0, 0, 40));
		gamePanel.getChildren().add(statusBox);

    }
        // Level 3 setup --- REFER TO LEVEL1 SETUP COMMENTS.
        public void level3Setup() {

        levelWidth = LevelMatrix.Level3[0].length() * 60;
        levelHeight = 480;
        key = createObject(240, 80, 20, 20, Color.BLACK, 0, 0);
        weapon = createObject(760, 40, 20, 20, Color.CORAL, 0, 0);
        door = createObject(1135, 360, 50, 80, Color.BISQUE, 0, 0);
        oxygen = createObject(1200, 45, 20, 20, Color.BLUE, 100, 100);
        // clear the wallList to be reloaded with level3 walls.
        wallList.clear();
      
        for (int y = 0; y < LevelMatrix.Level3.length; y++){
            String line = LevelMatrix.Level3[y];
            for (int x = 0; x < line.length(); x++){
                switch (line.charAt(x)){
                    case '0':
                        break;
                    case '1':
                        Node wall = createObject(x*40, y*40, 40, 40, Color.SLATEGREY, 0, 0);
                        wallList.add(wall);
                        break;
                  
                }
            }    
        }
        ProgressBar pb = new ProgressBar(seconds);
pb.setProgress(seconds);
TimerTask task = new TimerTask(){
    
    public void run(){
        double deathCount = 1.0;
        if(pauseCount == true){
            deathCount -= 0.04;
           pb.setProgress(seconds -= 0.04);  

        }
        if(deathCount <= 1){
    // run the death alert on JavaFX Thread. phew!!!
    Platform.runLater(() -> {
        death();
         
    });
                         
}
    }
    //pause the bar
    public void freeze(){
        if(pauseCount == false){
            pb.setProgress(seconds += 0);
            
        }       
    }
};


pt.scheduleAtFixedRate(task, 1000, 1000);

pb.setMinWidth(160.0);
pb.setMinHeight(40.0);
HBox statusBox = new HBox(pb);
		statusBox.setPadding(new Insets(0, 0, 0, 40));
		gamePanel.getChildren().add(statusBox);
    }

    }

    // method in the main loop --- Checks for specific keys pressed vis keyPressed method, and if the sprite is within bounds of level then move the sprite via the moveYPos or moveXPos methods. 
    private void update(){
        if (keyPressed(KeyCode.UP) && sprite.getTranslateY() >= 5){
            moveYPos(-3);
        }
        if (keyPressed(KeyCode.DOWN) && sprite.getTranslateY() +20 <= levelHeight){
            moveYPos(3);
        }
        if (keyPressed(KeyCode.LEFT) && sprite.getTranslateX() >= 5){
        moveXPos(-3);
    }
        if (keyPressed(KeyCode.RIGHT) && sprite.getTranslateX() +20 <= levelWidth){
            moveXPos(3);
    }
        // assigns different movement behavior to the enemies depending on the level the player is on. 
if(lvlCount == 0){
    moveEnemyX(v);
}
if(lvlCount == 1){
    moveEnemyY(v);
}
if(lvlCount == 3){
    moveEnemyY(v);
    moveEnemyX(v);
}


       
    }
    
    //Collisions and Movement for the enemy on the X-Axis
    private void moveEnemyX(int velocity){
       // enemy collisions with the wall --- X-Axis
       // loop through the current wallList
            for(Node wall : wallList) {
                //if the enemy's Bounds interset the wall's bounds:
                if (enemy1.getBoundsInParent().intersects(wall.getBoundsInParent())){             
                        if (enemy1.getTranslateX() + 20 == wall.getTranslateX() && enemy1.getTranslateY() != wall.getTranslateY() + 40 && enemy1.getTranslateY() + 20 != wall.getTranslateY()){
                            v = -v; //reverse
                        } 
                    else{
                        if (enemy1.getTranslateX() == wall.getTranslateX() + 40 && enemy1.getTranslateY() != wall.getTranslateY() + 40 && enemy1.getTranslateY() + 20 != wall.getTranslateY()) {
                            v = -v; //reverse
                        }
                        if (enemy1.getTranslateY() + 20 == wall.getTranslateY() && enemy1.getTranslateX() != wall.getTranslateX() + 40 && enemy1.getTranslateX() + 20 != wall.getTranslateX()){
                            v = -v; //reverse
                        } 
                    else{
                        if (enemy1.getTranslateY() == wall.getTranslateY() + 40 && enemy1.getTranslateX() != wall.getTranslateX() + 40 && enemy1.getTranslateX() + 20 != wall.getTranslateX()) {
                            v = -v; //reverse
                        }
                    }
                    } 
                }
                // enemy Colisions with the sprite -- X-Axis -- had to be here because collisions only work with sprite if the sprite is moving. 
                if(enemy1.getBoundsInParent().intersects(sprite.getBoundsInParent())){
                    if(enemy1.getTranslateX() == sprite.getTranslateX() || enemy1.getTranslateX() == sprite.getTranslateX() + 30 || enemy1.getTranslateX() + 20 == sprite.getTranslateX() || enemy1.getTranslateX() + 20 == sprite.getTranslateX() + 30 || enemy1.getTranslateY() == sprite.getTranslateX() || enemy1.getTranslateY() == sprite.getTranslateX() + 30 || enemy1.getTranslateY() + 20 == sprite.getTranslateX() || enemy1.getTranslateY() + 20 == sprite.getTranslateX() + 30 ){
                         sprite.setTranslateX(-20);
                         sprite.setTranslateY(-20); // have to move the sprite or the collision happens over and over.
                         gamePanel.getChildren().remove(sprite);
                        killed(); //Game over
                    }
                }
                }
            // Move the enemy along the X-Axis.
            if(v > 0){
                enemy1.setTranslateX(enemy1.getTranslateX() - 3);
            }
            else{
                enemy1.setTranslateX(enemy1.getTranslateX() + 3);
            }   
          }
// Same as moveEnemyX but for the Y-Axis.
    private void moveEnemyY(int velocity){
            for(Node wall : wallList) {
                if (enemy1.getBoundsInParent().intersects(wall.getBoundsInParent())){
                        if (enemy1.getTranslateY() + 30 == wall.getTranslateY() && enemy1.getTranslateX() != wall.getTranslateX() + 40 && enemy1.getTranslateX() + 20 != wall.getTranslateX()){
                            v = -v;
                        } 
                    else{
                        if (enemy1.getTranslateY() == wall.getTranslateY() + 40 && enemy1.getTranslateX() != wall.getTranslateX() + 40 && enemy1.getTranslateX() + 20 != wall.getTranslateX()) {
                            v = -v;
                        } 
                    }     
                }
                if(enemy1.getBoundsInParent().intersects(sprite.getBoundsInParent())){
                    if(enemy1.getTranslateY() == sprite.getTranslateY() || enemy1.getTranslateY() == sprite.getTranslateY()+ 30 || enemy1.getTranslateY() + 20 == sprite.getTranslateY() || enemy1.getTranslateY() + 20 == sprite.getTranslateY() + 30){
                        sprite.setTranslateX(-20);
                        sprite.setTranslateY(-20);
                        gamePanel.getChildren().remove(sprite);
                        killed();
                    }
                }
            }
            if(v > 0){
                enemy1.setTranslateY(enemy1.getTranslateY() + 3);
            }
            else{
                enemy1.setTranslateY(enemy1.getTranslateY() - 3);
            }   
        }

    // takes in a keycodes and defaults to FALSE. TRUE if pressed.
    private boolean keyPressed(KeyCode key){
        return keys.getOrDefault(key, false);
    }
    
    //Collisions and Movement for the Sprite along the X-Axis.
    private void moveXPos(int velocity){
// get the absolute value of velocity so to always have a positive number associated with velocity to use in for loop. 
int abVelocity;
if (velocity < 0){
    abVelocity = velocity * -1;
}else{
    abVelocity = velocity;
}
        //run if sprite is moving
        for (int i = 0; i < abVelocity; i++){ 
            //Collisions with walls
            for(Node wall : wallList) {
                if (sprite.getBoundsInParent().intersects(wall.getBoundsInParent())){
                    if(velocity > 0){
                        if (sprite.getTranslateX() + 20 == wall.getTranslateX() && sprite.getTranslateY() != wall.getTranslateY() + 40 && sprite.getTranslateY() + 20 != wall.getTranslateY()){
                            return;
                        } 
                    }
                    else{
                        if (sprite.getTranslateX() == wall.getTranslateX() + 40 && sprite.getTranslateY() != wall.getTranslateY() + 40 && sprite.getTranslateY() + 20 != wall.getTranslateY()) {
                            return;
                        }
                    } 
                }
                //Collisions with key object.
                if (sprite.getBoundsInParent().intersects(key.getBoundsInParent())){
                    if (sprite.getTranslateX() == key.getTranslateX() + 10){
                            // remove key on collision and increment the keyCount
                            gamePanel.getChildren().remove(key);
                            key.setTranslateX(-20);
                            key.setTranslateY(-20);
                            keyCount++;
                         
                        }
                }
                //Collisions with weapon ---- weapon currently acts as key in the game.
                if (sprite.getBoundsInParent().intersects(weapon.getBoundsInParent())){
                    if (sprite.getTranslateX() == weapon.getTranslateX() + 10){
                            //remove the weapon object and increment the keyCount.
                            gamePanel.getChildren().remove(weapon);
                            weapon.setTranslateX(-20);
                            weapon.setTranslateY(-20);
                            keyCount++;
                        }
                }
                // Collisions with oxygen
                if (sprite.getBoundsInParent().intersects(oxygen.getBoundsInParent())){
                    //remove the oxygen object and replenish the progressBar.
                    gamePanel.getChildren().remove(oxygen);
                    seconds = 1.0;
                }
                // collisions with doors -- Checks that the keycount is 2 --- player must collect both keys (weapon) to open the door. 
                if (sprite.getBoundsInParent().intersects(door.getBoundsInParent()) && keyCount == 2){
                    if (sprite.getTranslateX() == door.getTranslateX() + 10){
                            
                        // reset keyCount, remove all objects, sprite, and enemy
                            keyCount = 0;
                            gamePanel.getChildren().remove(weapon);
                            weapon.setTranslateX(-20);
                            weapon.setTranslateY(-20);
                            gamePanel.getChildren().remove(key);
                            key.setTranslateX(-20);
                            key.setTranslateY(-20);
                            gamePanel.getChildren().remove(door);
                            gamePanel.getChildren().removeAll(wallList);
                            gamePanel.getChildren().remove(enemy1);
                            gamePanel.getChildren().remove(oxygen);
                            
                            // create new enemy
                            enemy1 = createObject(365, 100, 30, 30, Color.RED, 0, 0);
                            // set up level2
                            level2.level2Setup();
                            //remove sprite, then create new sprite for level2
                            gamePanel.getChildren().remove(sprite);
                            sprite = createObject(0, 70, 20, 20, Color.PINK, 100, 100);
                            //inrement the lvlCount to know what level the player in on. 
                            lvlCount++;
                           
                        }
                    // check if the level count is 2 to setup level 3.
                     if(lvlCount == 2){
                            keyCount = 0;
                            gamePanel.getChildren().remove(weapon);
                            weapon.setTranslateX(-20);
                            weapon.setTranslateY(-20);
                            gamePanel.getChildren().remove(key);
                            key.setTranslateX(-20);
                            key.setTranslateY(-20);
                            gamePanel.getChildren().remove(door);
                            gamePanel.getChildren().remove(oxygen);
                            gamePanel.getChildren().removeAll(wallList);
                            gamePanel.getChildren().remove(enemy1);
                            enemy1 = createObject(800, 100, 30, 30, Color.RED, 100, 100);

                            level3.level3Setup();
                            
                            gamePanel.getChildren().remove(sprite);
                            sprite = createObject(0, 70, 20, 20, Color.PINK, 100, 100);
                            
                            lvlCount++;
                            }
                     //check if levelCount is 4 to see if player won the game. 
                     if(lvlCount == 4){
                         gamePanel.getChildren().remove(sprite);
                         // create Victory alert.
                         Alert winner = new Alert(AlertType.INFORMATION);
                         winner.setTitle("You Won!");
                         winner.setHeaderText("WOW! You Did It!");
                         winner.setContentText("You Saved The World!");
                         pt.cancel(); // cancel the Timer -- on another thread. 
                         //victory!
                         winner.show();
                     }
                }else{
                    
                }

            }
           // move the sprite.
            if(velocity > 0){
                sprite.setTranslateX(sprite.getTranslateX() + 1);
            }
            else{
                sprite.setTranslateX(sprite.getTranslateX() - 1);
            }   
          }
        }
    
    // Same as the moveXPos, but for the Y-Axis.
    private void moveYPos(int velocity){
// get the absolute value of velocity
int abVelocity;
if (velocity < 0){
    abVelocity = velocity * -1;
}else{
    abVelocity = velocity;
}
        //Collisions
        for (int i = 0; i < abVelocity; i++){
            for (Node wall : wallList){
                if (sprite.getBoundsInParent().intersects(wall.getBoundsInParent())){
                    if(velocity > 0){
                        if (sprite.getTranslateY() + 20 == wall.getTranslateY() && sprite.getTranslateX() != wall.getTranslateX() + 40 && sprite.getTranslateX() + 20 != wall.getTranslateX()){ 
                            return;
                        }
                    }
                    else{
                        if (sprite.getTranslateY() == wall.getTranslateY() + 40 && sprite.getTranslateX() != wall.getTranslateX() + 40 && sprite.getTranslateX() + 20 != wall.getTranslateX()){
                            return;
                        }
                    }
                }
                if (sprite.getBoundsInParent().intersects(key.getBoundsInParent())){
                    if (sprite.getTranslateY() == key.getTranslateY() + 10){
                            
                            gamePanel.getChildren().remove(key);
                            key.setTranslateX(-20);
                            key.setTranslateY(-20);
                            keyCount++;
                        }
                }
                if (sprite.getBoundsInParent().intersects(weapon.getBoundsInParent())){
                    if (sprite.getTranslateY() == weapon.getTranslateY() + 10){
                            
                            gamePanel.getChildren().remove(weapon);
                            weapon.setTranslateX(-20);
                            weapon.setTranslateY(-20);
                            keyCount++;
                        }
                }
                if (sprite.getBoundsInParent().intersects(oxygen.getBoundsInParent())){
                    gamePanel.getChildren().remove(oxygen);
                    seconds = 1.0;
                }
                
                if (sprite.getBoundsInParent().intersects(door.getBoundsInParent()) && keyCount == 2){
                    if (sprite.getTranslateY() == door.getTranslateY() + 10){
                        
                            keyCount = 0;
                            gamePanel.getChildren().remove(weapon);
                            gamePanel.getChildren().remove(key);
                            gamePanel.getChildren().remove(door);
                            gamePanel.getChildren().removeAll(wallList);
                            gamePanel.getChildren().remove(enemy1);
                            gamePanel.getChildren().remove(oxygen);
                            enemy1 = createObject(365, 100, 30, 30, Color.RED, 0, 0);
                            level2.level2Setup();
                            gamePanel.getChildren().remove(sprite);
                            sprite = createObject(0, 70, 20, 20, Color.PINK, 100, 100);
                            lvlCount++;
                        }
                     if (lvlCount == 2){
                            keyCount = 0;
                            gamePanel.getChildren().remove(weapon);
                            gamePanel.getChildren().remove(key);
                            gamePanel.getChildren().remove(door);
                            gamePanel.getChildren().remove(oxygen);
                            gamePanel.getChildren().removeAll(wallList);
                            gamePanel.getChildren().remove(enemy1);
                            enemy1 = createObject(800, 100, 30, 30, Color.RED, 100, 100);
                            level3.level3Setup();
                            gamePanel.getChildren().remove(sprite);
                            sprite = createObject(0, 70, 20, 20, Color.PINK, 100, 100);
                            lvlCount++;
                        }
                     if (lvlCount == 4){
                         gamePanel.getChildren().remove(sprite);
                         Alert winner = new Alert(AlertType.INFORMATION);
                         winner.setTitle("You Won!");
                         winner.setHeaderText("WOW! You Did It!");
                         winner.setContentText("You Saved The World!");
                         pt.cancel();
                         winner.show();
                     }
                }
            }
            if(velocity > 0){
                sprite.setTranslateY(sprite.getTranslateY() + 1);
            }
            else{
                sprite.setTranslateY(sprite.getTranslateY() - 1);
            }
        }
    }
    
    // method to create all the objects (Nodes) in the game and add them to the gamePanel.
    private Node createObject(int x, int y, int w, int h, Color color, int ah, int aw){
        Rectangle object = new Rectangle(w, h);
        object.setTranslateX(x);
        object.setTranslateY(y);
        object.setFill(color);
        object.setStroke(Color.BLACK);
        object.setArcHeight(ah);
        object.setArcWidth(aw);
        gamePanel.getChildren().add(object);
        return object;
    }
    
   
    @Override
    public void start(Stage primaryStage)throws Exception{ //start the game
        
        //start button
        Button button = new Button("Start");
        button.setMinWidth(100.0);
        button.setAlignment(Pos.CENTER);
        //title text
        Text title = new Text();
        title.setText("GET TO ENGINEERING!");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Impact", FontWeight.EXTRA_BOLD, 100));
        VBox vbox = new VBox(title, button);
        HBox hbox = new HBox(vbox);

        // when button is pressed, set up level1, create the sprite and the first enemy.
        button.setOnAction((ActionEvent event) -> {
        level1.level1Setup();
        sprite = createObject(0, 70, 20, 20, Color.PINK, 100, 100);
        enemy1 = createObject(900, 300, 30, 30, Color.RED, 0, 0);
       
        loop.start(); //main loop
        pLoop.start(); //pause loop

        hbox.getChildren().remove(vbox);
    });
        
       hbox.setSpacing(20.0);
       hbox.setPadding(new Insets(150, 100, 100, 50));
       vbox.setSpacing(50.0);
       Canvas canvas = new Canvas(1280, 480);

        gamePanel.setCenter(canvas);
        gamePanel.setBackground(new Background(new BackgroundFill(Color.DIMGREY, CornerRadii.EMPTY, Insets.EMPTY)));
        gamePanel.getChildren().add(hbox);
        
        Scene scene = new Scene(gamePanel);
        scene.setOnKeyPressed(event -> keys.put(event.getCode(), true));
        scene.setOnKeyReleased(event -> keys.put(event.getCode(), false));
        primaryStage.setTitle("Get To Engineering!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
  
    public static void main(String[] args){
        launch(args);
    }
}
