package com.gdx.game.Tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.gdx.game.Sprites.Bullet;
import com.gdx.game.Sprites.Enemy;


public class WorldContactListener implements ContactListener {



    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if(fixA.getUserData() instanceof Bullet){
            ((Bullet) fixA.getUserData()).setDestroyed(true);

            if(fixB.getUserData() instanceof Enemy){
                int localHealth = ((Enemy) fixB.getUserData()).getHealth();
                ((Enemy) fixB.getUserData()).setHealth(localHealth - Bullet.damage);
                ((Enemy) fixB.getUserData()).setPointLight();
            }
        }
        if(fixB.getUserData() instanceof Bullet){
            ((Bullet) fixB.getUserData()).setDestroyed(true);

            if(fixA.getUserData() instanceof Enemy){
                int localHealth = ((Enemy) fixA.getUserData()).getHealth();
                ((Enemy) fixA.getUserData()).setHealth(localHealth - Bullet.damage);
                ((Enemy) fixA.getUserData()).setPointLight();
            }
        }

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
