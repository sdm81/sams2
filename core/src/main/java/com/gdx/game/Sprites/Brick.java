package com.gdx.game.Sprites;

import com.badlogic.gdx.math.Rectangle;
import com.gdx.game.NineCircles;
import com.gdx.game.Screens.PlayScreen;


public class Brick extends InteractiveTileObject {
    public Brick(PlayScreen screen, Rectangle bounds) {
        super(screen.getWorld(), screen.getMap(), bounds);
        fixture.setUserData(this);
        setCategoryFilter(NineCircles.BULLET_BIT);
    }

}
