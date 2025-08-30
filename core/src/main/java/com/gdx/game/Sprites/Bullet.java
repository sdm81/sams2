package com.gdx.game.Sprites;

import static com.gdx.game.Setting.GameResources.GUNSHOT;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.gdx.game.NineCircles;
import com.gdx.game.Screens.PlayScreen;


public class Bullet extends Sprite{
    private World world;
    private Body b2body;
    private TextureRegion blueBullet;
    private float xPos;
    private float yPos;
    private float shooterRadius;
    private float radius = 6;
    private float localAngle;
    private float creationTime;
    private boolean destroyed;
    private PointLight pointLight;
    public static final int damage = 10;
    private Music fire;

    public Bullet(World world, PlayScreen screen, float xPos, float yPos, float angle, float shooterRadius){
        super(screen.getAtlas().findRegion("BlueBall"));
        this.world = world;

        this.xPos = xPos;
        this.yPos = yPos;
        this.shooterRadius = shooterRadius;

        // This is to have the bullet time out
        creationTime = System.nanoTime();

        destroyed = false;

        // This is creating the bullet sound
        // This sound is made by Marvin and can be found here: http://soundbible.com/2004-Gun-Shot.html

        fire = Gdx.audio.newMusic(Gdx.files.internal(GUNSHOT));
        fire.setVolume(0.5f);
        fire.play();

        // This is to correct the angle for placement & physics calculations
        localAngle = angle;
        if(localAngle > 0){
            localAngle = localAngle - 180;
            localAngle = localAngle * -1;
            localAngle = localAngle + 180;
        }
        else{
            localAngle = localAngle * -1;
        }

        defineBullet();
        blueBullet = new TextureRegion(getTexture(), 1, 1, 11, 25);

        // Setting bounds of sprite
        setBounds(0, 0, 11 / NineCircles.PPM, 25 / NineCircles.PPM);
        setRegion(blueBullet);

        // This is so it will rotate around the center of the sprite
        setOrigin(getWidth() / 2,getWidth() / 2);
        rotate(angle); // Sets the sprite to the right angle
    }

    // This method is to connect the Box2D object with the sprite
    public void update(float delta){
        setPosition(b2body.getPosition().x - getWidth() / 2 - 12*(float)(Math.sin(Math.toRadians(localAngle)))/NineCircles.PPM, b2body.getPosition().y - getWidth() / 2 - 12*(float)(Math.cos(Math.toRadians(localAngle)))/NineCircles.PPM);
    }

    public void deleteBody(){
        world.destroyBody(b2body);
        b2body.setUserData(null);
        b2body = null;
    }
    public void disposeSound(){
        fire.dispose();
    }

    public void defineBullet(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(xPos + (2.9f*shooterRadius)*(float)(Math.sin(Math.toRadians(localAngle))), yPos + (2.9f*shooterRadius)*(float)(Math.cos(Math.toRadians(localAngle))));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        // This is the same math as before. It simply takes the corrected angle and uses it to scale and x and y vectors
        b2body.applyLinearImpulse(15*(float)(Math.sin(Math.toRadians(localAngle))),15*(float)(Math.cos(Math.toRadians(localAngle))),0,0,true);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(radius / NineCircles.PPM);
        fdef.filter.categoryBits = NineCircles.BULLET_BIT;
        fdef.filter.maskBits = NineCircles.DEFAULT_BIT | NineCircles.BRICK_BIT | NineCircles.ENEMY_BIT;
        fdef.shape = shape;
        fdef.density = .8f;
        b2body.createFixture(fdef);

        b2body.createFixture(fdef).setUserData(this);


    }
    //Getters and setters
    public float getCreationTime(){
        return creationTime;
    }
    public boolean getDestroyed(){
        return destroyed;
    }
    public void setDestroyed(Boolean input){
        destroyed = input;
    }
    public PointLight bulletLight(){
        return pointLight;
    }
}
