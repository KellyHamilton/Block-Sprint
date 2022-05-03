/*
 * Made by Kelly Hamilton
 * Last Modifide on 08/03/2018
 * Game name "Block Sprint"
 * Version 1.0
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class BlockSprint extends Application {
    
    // Keyboard input
    private HashMap<KeyCode, Boolean> keys = new HashMap<KeyCode, Boolean>();
    
    // List of the platforms
    private ArrayList<Node> platforms = new ArrayList<Node>();
    // List of the enemies
    private ArrayList<Node> enemyList = new ArrayList<Node>();
    
    // Main root
    private Pane appRoot = new Pane();
    // Players,coins,ect
    private Pane gameRoot = new Pane();
    private Pane uiRoot = new Pane();
    
    private Node player;
    private Point2D playerVelocity = new Point2D(0, 0);
    private boolean canJump = true;
    
    private int levelWidth;
    
    private void initContent(){
        
        Rectangle bg = new Rectangle(1280, 720);
        bg.setFill(Color.BLACK);
        
        levelWidth = Levels.LEVEL1[0].length() * 60;
        
        buildL1();
        player = createEntity(0, 500, 40, 40, Color.GREEN);
        
        player.translateXProperty().addListener((obs, old, newValue) -> {
            int offset = newValue.intValue();
            
            if(offset > 640 && offset < levelWidth - 640){
                gameRoot.setLayoutX(-(offset - 640));
            }
        });
        
        appRoot.getChildren().addAll(bg, gameRoot, uiRoot);
        
    }
    
    private void update(){

        if(isPressed(KeyCode.SPACE) && player.getTranslateY() >= 5){
            jumpPlayer();
        }
        if(isPressed(KeyCode.ENTER)){
            reload();
        }
        if(player.getTranslateX() +40 <= levelWidth -5){
            movePlayerX(6);
        }
        if(playerVelocity.getY() < 10){
            playerVelocity = playerVelocity.add(0, 1);
        }
        movePlayerY((int)playerVelocity.getY());
        
    }
    
    private void enemyDamage(){
        
        for(Node enemy : enemyList){
            if(player.getBoundsInParent().intersects(enemy.getBoundsInParent())){
                reload();
                break;
            }
        }
    }
    
    private void deathUpdate(){
        if(player.getTranslateY() > 800){
            reload();
        }
    }
    
    private void reload(){
        
        player.setTranslateX(0);
        player.setTranslateY(500);
        gameRoot.setLayoutX(0);
        
        for(Node enemy : enemyList){
            enemy.getProperties().put("alive", false);
        }
        for(Iterator<Node> it = enemyList.iterator(); it.hasNext();){
            Node enemy = it.next();
            if(!(Boolean)enemy.getProperties().get("alive")){
                it.remove();
                gameRoot.getChildren().remove(enemy);
            }
        }
        
        
        for(Node platform : platforms){
            platform.getProperties().put("alive", false);
        }
        for(Iterator<Node> it = platforms.iterator(); it.hasNext();){
            Node platform = it.next();
            if(!(Boolean)platform.getProperties().get("alive")){
                it.remove();
                gameRoot.getChildren().remove(platform);
            }
        }
        
        for(Node enemy : enemyList){
            enemy.getProperties().put("alive", false);
        }
        for(Iterator<Node> it = enemyList.iterator(); it.hasNext();){
            Node enemy = it.next();
            if(!(Boolean)enemy.getProperties().get("alive")){
                it.remove();
                gameRoot.getChildren().remove(enemy);
            }
        }
        buildL1();
    }
    
    private void buildL1(){
        
        levelWidth = Levels.LEVEL1[0].length() * 60;
        
        for(int i=0; i<Levels.LEVEL1.length; i++){
            String line = Levels.LEVEL1[i];
            for(int j=0; j<line.length(); j++){
                switch(line.charAt(j)){
                    case '0':
                        break;
                        
                    case '1':
                        Node platform = createEntity(j*60, i*60, 60, 60, Color.GREY);
                        platforms.add(platform);
                        break;
                        
                    case '2':
                        Node enemy = createEntityT(j*60+25, i*60+60, 40, 40, Color.RED);
                        enemyList.add(enemy);
                        break;
                    
                }
            }
        }
    }
    
    private void movePlayerX(int value){
        
        boolean movingRight = value > 0;
        
        for(int i=0; i < Math.abs(value); i++){
            for(Node platform : platforms){
                if(player.getBoundsInParent().intersects(platform.getBoundsInParent())){
                    if(movingRight){
                        if(player.getTranslateX() +40 == platform.getTranslateX()){
                            return;
                        }
                    }
                    else{
                        if(player.getTranslateX() == platform.getTranslateX()+60){
                            return;
                        }
                    }
                }
            }
            player.setTranslateX(player.getTranslateX() + (movingRight ? 1 : -1));
        }
        
    }
    
    private void movePlayerY(int value){
        
        boolean movingDown = value > 0;
        
        for(int i=0; i < Math.abs(value); i++){
            for(Node platform : platforms){
                if(player.getBoundsInParent().intersects(platform.getBoundsInParent())){
                    if(movingDown){
                        if(player.getTranslateY() +40 == platform.getTranslateY()){
                            player.setTranslateY(player.getTranslateY()-1);
                            canJump = true;
                            return;
                        }
                    }
                    else{
                        if(player.getTranslateY() == platform.getTranslateY()+60){
                            return;
                        }
                    }
                }
            }
            player.setTranslateY(player.getTranslateY() + (movingDown ? 1 : -1));
        }
    }
    
    private void jumpPlayer(){
        
        if(canJump){
            playerVelocity = playerVelocity.add(0, -30);
            canJump = false;
        }
    }
    
    private Node createEntity(int x, int y, int w, int h, Color color){
        
        Rectangle entity = new Rectangle(w, h);
        entity.setTranslateX(x);
        entity.setTranslateY(y);
        entity.setFill(color);
        entity.getProperties().put("alive", true);
        
        gameRoot.getChildren().add(entity);
        
        return entity;
    }
    
    private Node createEntityT(int x, int y, int w, int h, Color color){
        
        Circle entity = new Circle(w/2);
        entity.setTranslateX(x);
        entity.setTranslateY(y);
        entity.setFill(color);
        entity.getProperties().put("alive", true);
        
        gameRoot.getChildren().add(entity);
        
        return entity;
    }
    
    private Boolean isPressed(KeyCode key){
        
        return keys.getOrDefault(key, false);
    }
    
    public void start(Stage primaryStage) throws Exception{
        initContent();
        
        Scene scene  = new Scene(appRoot);
        scene.setOnKeyPressed(event -> keys.put(event.getCode(), true));
        scene.setOnKeyReleased(event -> keys.put(event.getCode(), false));
        primaryStage.setTitle("Block Sprint");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        AnimationTimer timer = new AnimationTimer(){
            public void handle(long now){
                update();
                deathUpdate();
                enemyDamage();
            }
        };
        timer.start();
    }
    
    public static void main(String[] args){
        launch(args);
    }

  
}
