package com.gdx.game.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.gdx.game.Screens.PlayScreen;
import com.gdx.game.Sprites.Brick;


public class B2WorldCreator {
    public B2WorldCreator(PlayScreen screen){

        for(MapObject object: screen.getMap().getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            new Brick(screen, rect);
        }
        for(MapObject object: screen.getMap().getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            new Brick(screen, rect);
        }
    }
}
