package com.gdx.game.Screens;

import static com.gdx.game.Setting.GameResources.BACKGROUND_IMG_PATH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.gdx.game.NineCircles;
import com.gdx.game.Scenes.ParallaxBackground;
import com.gdx.game.Scenes.ParallaxLayer;

public class SettingScreen implements Screen {
    private NineCircles game;
    private OrthographicCamera gamecam;
    private ExtendViewport gamePort;
    private Stage stage;
    private Skin skin;
    private BitmapFont white, black;
    private TextureAtlas atlas;
    private TextButton buttonExit;
   private TextButton buttonSound;
    private Table table;
    private Label heading;
    private ParallaxBackground rbg;

    public SettingScreen(NineCircles game){
        this.game = game;
    }
    public void handleInput(float delta){

    }
    public void update(float delta){
        handleInput(delta);
    }

    @Override
    public void show() {
        gamecam = new OrthographicCamera();
        gamePort = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), gamecam);

        stage = new Stage(gamePort, game.batch);
        Gdx.input.setInputProcessor(stage);

        //   largeWhiteFont = FontBuilder.generate(48, Color.WHITE, GameResources.FONT_PATH);
        //    commonWhiteFont = FontBuilder.generate(24, Color.WHITE, GameResources.FONT_PATH);
        //    commonBlackFont = FontBuilder.generate(24, Color.BLACK, GameResources.FONT_PATH);

        atlas = new TextureAtlas("font/atlas.pack");
        //skin = new Skin(atlas);
        skin = new Skin(Gdx.files.internal("font/menuSkin.json"), new TextureAtlas("font/atlas.pack"));
        table = new Table(skin);
        table.setBounds(0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Creating fonts
        white = new BitmapFont(Gdx.files.internal("font/white32.fnt"), false);
        black = new BitmapFont(Gdx.files.internal("font/black32.fnt"), false);

        // Creating buttons
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("button.up");
        textButtonStyle.down = skin.getDrawable("button.down");

        textButtonStyle.pressedOffsetX = 1;
        textButtonStyle.pressedOffsetY = -1;
        textButtonStyle.font = black;

        buttonExit = new TextButton("RETURN", textButtonStyle);
        buttonExit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.dispose();
                Gdx.input.setInputProcessor(stage);
                game.setScreen(new MenuScreen(game));
            }
        });
        buttonExit.pad(5);




      /*  buttonSound = new TextButton("Sound - "+translateStateToText(NineCircles.audioManager.isSoundOn), textButtonStyle);
        buttonSound.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.dispose();
                Gdx.input.setInputProcessor(stage);
                    NineCircles.audioManager.isSoundOn = !NineCircles.audioManager.isSoundOn;
                    buttonSound = new TextButton("Sound - "+translateStateToText(NineCircles.audioManager.isSoundOn), textButtonStyle);
                game.setScreen(new SettingScreen(game));
            }
        });
        buttonSound.pad(5);
*/


        heading = new Label("Setting", new Label.LabelStyle(new BitmapFont(Gdx.files.internal("font/white64.fnt"), false), Color.WHITE));

        // Assembling
        table.add(heading);
        table.getCell(heading).spaceBottom(10);
        table.row();

        table.add(buttonSound);
        // table.getCell(buttonSound).spaceBottom(15);
        table.row();


        table.add(buttonExit);
        table.row();
        table.debug();
        stage.addActor(table);


        rbg = new ParallaxBackground(new ParallaxLayer[]{
            new ParallaxLayer(new TextureRegion(new Texture(Gdx.files.internal(BACKGROUND_IMG_PATH))),new Vector2(0.5f, 0.5f),new Vector2(0, 300)),
            //new ParallaxLayer(atlas.findRegion("bg2"),new Vector2(1.0f,1.0f),new Vector2(0, 500)),
        }, 800, 480,new Vector2(150,0));

    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1); // Color then opacity
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        rbg.render(delta);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
        gamecam.update();

        stage.setViewport(gamePort);
        table.invalidateHierarchy();
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
    public void dispose() {

    }
    private String translateStateToText(boolean state) {
        return state ? "ON" : "OFF";
    }
}
