package com.gdx.game.Screens;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.gdx.game.NineCircles;
import com.gdx.game.Scenes.Hud;
import com.gdx.game.Sprites.Enemy;
import com.gdx.game.Sprites.Hero;
import com.gdx.game.Tools.B2WorldCreator;
import com.gdx.game.Tools.WorldContactListener;
import java.util.ArrayList;
public class PlayScreen implements Screen {
    private NineCircles game;
    private OrthographicCamera gamecam;
    private ExtendViewport gamePort;
    private Hud hud;
    private TextureAtlas atlas;
    private TextureAtlas atlasTwo;
    private Hero hero;
    private ArrayList<Enemy> enemyList;
    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    private World world;
    private Box2DDebugRenderer b2dr;

    public PlayScreen(NineCircles game){
        atlas = new TextureAtlas("packers.pack");
        atlasTwo = new TextureAtlas("man.pack");
        this.game = game;
        gamecam = new OrthographicCamera();
        gamePort = new ExtendViewport(NineCircles.V_WIDTH / NineCircles.PPM, NineCircles.V_HEIGHT / NineCircles.PPM, gamecam);
        hud = new Hud(game.batch);
        maploader = new TmxMapLoader();
        map = maploader.load("desert2.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / NineCircles.PPM);
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        world = new World(new Vector2(0, 0), true);
        b2dr = new Box2DDebugRenderer();
        new B2WorldCreator(this);
        hero = new Hero(world, this);
        enemyList = new ArrayList<Enemy>();
        for(int i = 0; i < 8; ++i){
            enemyList.add(new Enemy(world, this, (float) (Math.random()) * 1250 / NineCircles.PPM, (float) (Math.random()) * 1250 / NineCircles.PPM));
        }
        world.setContactListener(new WorldContactListener());
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        renderer.render();
        b2dr.render(world, gamecam.combined);
        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();

        if(!enemyList.isEmpty()){
            for(int i = 0; i < enemyList.size(); ++i){
                if (!enemyList.get(i).isDestroyed()){
                    enemyList.get(i).draw(game.batch);
                }
            }
        }

        if (hero.getBulletList() != null){
            for(int i = 0; i < hero.getBulletList().size(); ++i){
                hero.getBulletList().get(i).draw(game.batch);
            }
        }
        hero.draw(game.batch);
        game.batch.end();
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    public void update(float delta) {
        if (hud.getTime() <= 0){
            MenuScreen.MaxScoreGame=hud.getScore();
            game.setScreen(new PlayScreen(game));
        }
        handleInput(delta);
        hero.update(delta);
        if (!enemyList.isEmpty()) {
            for (int i = 0; i < enemyList.size(); ++i){
                if(!enemyList.get(i).isDestroyed()){
                    if(enemyList.get(i).getHealth() <= 0){
                        enemyList.get(i).deleteBody();
                        enemyList.get(i).setDestroyed(true);
                        hud.addScore(100);
                        enemyList.add(new Enemy(world, this, (float)(Math.random())*1250/NineCircles.PPM, (float)(Math.random())*1250/NineCircles.PPM));
                    }
                    else{
                        enemyList.get(i).update(delta, hero.getHeroBody().getPosition().x, hero.getHeroBody().getPosition().y);
                    }
                }
            }
        }

        if (hero.getBulletList() != null){
            for(int i = 0; i < hero.getBulletList().size(); ++i){
                hero.getBulletList().get(i).update(delta);
                if (System.nanoTime() - hero.getBulletList().get(i).getCreationTime() > 1 * 1000000000.0f || hero.getBulletList().get(i).getDestroyed()) {
                    hero.getBulletList().get(i).deleteBody();
                    hero.getBulletList().remove(i);
                }
            }
        }

        hud.update(delta);
        world.step(1 / 60f, 6, 2);
        gamecam.position.x = hero.getHeroBody().getPosition().x;
        gamecam.position.y = hero.getHeroBody().getPosition().y;
        gamecam.update();
        renderer.setView(gamecam);
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }

    public TextureAtlas getAtlasTwo() { return atlasTwo;}

    public void handleInput(float delta){
        float localSpeed = 0;


        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
            localSpeed = hero.getRunningSpeed();
            if (hero.getHeroBody().getLinearVelocity().x != 0 || hero.getHeroBody().getLinearVelocity().y != 0){
                hero.walking.pause();
                hero.running.play();
            }
        }
        else{
            localSpeed = hero.getWalkingSpeed();
            hero.running.pause();
            if (hero.getHeroBody().getLinearVelocity().x != 0 || hero.getHeroBody().getLinearVelocity().y != 0){
                hero.walking.play();
            }
            else{
                hero.walking.pause();
            }
        }

        if(Gdx.input.isKeyPressed(Input.Keys.W) && hero.getHeroBody().getLinearVelocity().y <= 5){
            hero.getHeroBody().setLinearVelocity(hero.getHeroBody().getLinearVelocity().x, localSpeed);

        }
        if(Gdx.input.isKeyPressed(Input.Keys.S) && hero.getHeroBody().getLinearVelocity().y >= -5){
            hero.getHeroBody().setLinearVelocity(hero.getHeroBody().getLinearVelocity().x, -1 * localSpeed);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D) && hero.getHeroBody().getLinearVelocity().x <= 5){
            hero.getHeroBody().setLinearVelocity(localSpeed, hero.getHeroBody().getLinearVelocity().y);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.A) && hero.getHeroBody().getLinearVelocity().x >= -5){
            hero.getHeroBody().setLinearVelocity(-1 * localSpeed, hero.getHeroBody().getLinearVelocity().y);
        }


        if(!Gdx.input.isKeyPressed(Input.Keys.W) && !Gdx.input.isKeyPressed(Input.Keys.S)){
            hero.getHeroBody().setLinearVelocity(hero.getHeroBody().getLinearVelocity().x, 0);
        }
        if(!Gdx.input.isKeyPressed(Input.Keys.A) && !Gdx.input.isKeyPressed(Input.Keys.D)){
            hero.getHeroBody().setLinearVelocity(0, hero.getHeroBody().getLinearVelocity().y);
        }


        if (Gdx.input.justTouched() && hero.getHeroBody().getLinearVelocity().x == 0 && hero.getHeroBody().getLinearVelocity().y == 0) {
            hero.heroBullet(world, this, hero.getHeroBody().getPosition().x, hero.getHeroBody().getPosition().y, hero.getRotation(), hero.getHeroRadius() / NineCircles.PPM);
            float a,x,y = 0;
            a=hero.getRotation();
            x= (float) Math.sin(a)*100;
            y= (float) Math.cos(a)*100;
            System.out.println(a+ " " +x+ " " +y);
            hero.getHeroBody().setLinearVelocity(x, y);

        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            game.setScreen(new MenuScreen(game));
        }
    }



    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    public TiledMap getMap() {
        return map;
    }

    public World getWorld(){
        return world;
    }

    @Override
    public void dispose() {
        if (hero.getBulletList() != null){
            for(int i = 0; i < hero.getBulletList().size(); ++i){
                hero.getBulletList().get(i).disposeSound();
            }
        }
        hud.dispose();
        map.dispose();
        renderer.dispose();
        b2dr.dispose();
        world.dispose();

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {

    }
}

