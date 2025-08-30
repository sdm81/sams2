package com.gdx.game.Sprites;

import static com.gdx.game.Setting.GameResources.STEP;
import static com.gdx.game.Setting.GameResources.STEP2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.gdx.game.NineCircles;
import com.gdx.game.Screens.PlayScreen;

import java.util.ArrayList;


public class Hero extends Sprite {
    public enum State { RUNNING, STANDING, SHOOTING, WALKING};
    public State currentState;
    public State previousState;
    private World world;
    private Body b2body;
    private static final float radius = 15;
    private ArrayList<Bullet> bulletList;
    private float xDif;
    private float yDif;
  //  private ConeLight heroCone;
    private PointLight pointLight;
    private Animation playerRun;
    private Animation playerWalk;
    private TextureRegion playerStanding;
    private TextureRegion playerShooting;
    private float stateTimer;
    private float lastShotTime;
    private float walkingSpeed;
    private float runningSpeed;
    public Music running;
    public Music walking;

    public Hero(World world, PlayScreen screen){
        super(screen.getAtlasTwo().findRegion("player"));

        this.world = world;
        defineHero();

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i = 1; i < 5; ++i){
            frames.add(new TextureRegion(getTexture(), 1 + i * 79, 1, 79, 127));
        }
        playerRun = new Animation(0.1f, frames);
        frames.clear();
        for(int i = 1; i < 5; ++i){
            frames.add(new TextureRegion(getTexture(), 1 + i * 79, 1, 79, 127));
            frames.add(new TextureRegion(getTexture(), 1 + i * 79, 1, 79, 127));
        }
        playerWalk = new Animation(0.1f, frames);

        playerShooting = new TextureRegion(getTexture(), 1 + 6 * 79, 1, 79, 127);

        playerStanding = new TextureRegion(getTexture(), 1 + 0 * 79, 1, 79, 127);

        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;

        bulletList = new ArrayList<Bullet>();
        lastShotTime = 0;
        walkingSpeed = 2.0f;
        runningSpeed = 3.5f;
        running = Gdx.audio.newMusic(Gdx.files.internal(STEP));
        running.setVolume(2f);
        running.setLooping(true);
        walking = Gdx.audio.newMusic(Gdx.files.internal(STEP2));
        walking.setVolume(0.8f);
        walking.setLooping(true);


        setBounds(1, 1, 79 / NineCircles.PPM, (127 - 45) / NineCircles.PPM);
        setRegion(playerStanding);
        setOrigin(getWidth() / 2 ,getWidth() / 2);
    }

    public State getState(){
        if(b2body.getLinearVelocity().x == runningSpeed || b2body.getLinearVelocity().y == runningSpeed || b2body.getLinearVelocity().x == -1 * runningSpeed || b2body.getLinearVelocity().y == -1 * runningSpeed){
            return State.RUNNING;
        }
        else if (System.nanoTime() - lastShotTime < 8 * 100000000.0){
            return State.SHOOTING;
        }
        else if(b2body.getLinearVelocity().x == walkingSpeed || b2body.getLinearVelocity().y == walkingSpeed || b2body.getLinearVelocity().x == -1 * walkingSpeed || b2body.getLinearVelocity().y == -1 * walkingSpeed){
            return State.WALKING;
        }
        else {
            return State.STANDING;
        }
    }


    public TextureRegion getFrame(float delta){
        currentState = getState();
        TextureRegion region;
        switch(currentState){
            case RUNNING:
                region = (TextureRegion) playerRun.getKeyFrame(stateTimer, true);
                break;
            case SHOOTING:
                region = playerShooting;
                break;
            case WALKING:
                region = (TextureRegion) playerWalk.getKeyFrame(stateTimer, true);
                break;
            case STANDING:
                region = playerStanding;
                break;
            default:
                region = playerStanding;
        }

        stateTimer = currentState == previousState ? stateTimer + delta : 0;
        previousState = currentState;

        return region;
    }

    public void update(float delta){
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getWidth() / 2);
        xDif = Gdx.input.getX() - Gdx.graphics.getWidth() / 2 ;
        yDif = Gdx.input.getY() - Gdx.graphics.getHeight() / 2;
        setRotation((float) Math.toDegrees((Math.atan2(xDif * -1, yDif * -1))));
        setRegion(getFrame(delta));
    }

    public void heroBullet(World world, PlayScreen screen, float xPos, float yPos, float angle, float shooterRadius){
        bulletList.add(new Bullet(world, screen, xPos, yPos, angle, shooterRadius));
        lastShotTime = System.nanoTime();
    }

    public ArrayList<Bullet> getBulletList(){
        return bulletList;
    }

    public void defineHero(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(14, 6);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(radius / NineCircles.PPM);
        fdef.filter.categoryBits = NineCircles.HERO_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef);
        b2body.createFixture(fdef).setUserData(this);
    }



    public Body getHeroBody(){
        return b2body;
    }

    public float getHeroRadius(){
        return radius;
    }


    public PointLight getHeroPoint(){
        return pointLight;
    }

    public float getWalkingSpeed(){
        return walkingSpeed;
    }

    public float getRunningSpeed(){
        return runningSpeed;
    }
}

