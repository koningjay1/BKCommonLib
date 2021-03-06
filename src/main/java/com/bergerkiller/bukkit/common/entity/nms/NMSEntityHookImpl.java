package com.bergerkiller.bukkit.common.entity.nms;

import com.bergerkiller.bukkit.common.controller.DefaultEntityController;
import com.bergerkiller.bukkit.common.controller.EntityController;
import com.bergerkiller.bukkit.common.entity.CommonEntity;
import com.bergerkiller.bukkit.common.internal.CommonNMS;
import com.bergerkiller.bukkit.common.reflection.classes.EntityTypesRef;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

public class NMSEntityHookImpl implements NMSEntityHook {

    private EntityController<?> controller;

    public NMSEntityHookImpl(CommonEntity<?> entity) {
        this.controller = new DefaultEntityController();
        this.controller.bind(entity);
    }

    @Override
    public EntityController<?> getController() {
        return this.controller;
    }

    @Override
    public void setController(EntityController<?> controller) {
        this.controller = controller;
    }

    @Override
    public boolean e(EntityHuman human) {
        return controller.onInteractBy(CommonNMS.getHuman(human));
    }

    @Override
    public void t_() {
        controller.onTick();
    }

    @Override
    public boolean damageEntity(DamageSource damageSource, float damage) {
        controller.onDamage(com.bergerkiller.bukkit.common.wrappers.DamageSource.getForHandle(damageSource), damage);
        return true;
    }

    @Override
    public void burn(float damage) {
        controller.onBurnDamage(damage);
    }

    @Override
    public void g(double dx, double dy, double dz) {
        controller.onPush(dx, dy, dz);
    }

    @Override
    public void move(double dx, double dy, double dz) {
        controller.onMove(dx, dy, dz);
    }

    @Override
    public void die() {
        controller.onDie();
    }

    @Override
    public String getName() {
        return controller.getLocalizedName();
    }

    private String getSavedName() {
        return EntityTypesRef.classToNames.get(controller.getEntity().getHandle().getClass().getSuperclass());
    }

    @Override
    public boolean c(NBTTagCompound nbttagcompound) {
        if (this.controller.getEntity().isDead()) {
            return false;
        } else {
            nbttagcompound.setString("id", getSavedName());
            this.controller.getEntity().getHandle(Entity.class).e(nbttagcompound);
            return true;
        }
    }

    @Override
    public boolean d(NBTTagCompound nbttagcompound) {
        if (this.controller.getEntity().isDead() || (this.controller.getEntity().hasPlayerPassenger() && controller.isPlayerTakable())) {
            return false;
        } else {
            nbttagcompound.setString("id", getSavedName());
            this.controller.getEntity().getHandle(Entity.class).e(nbttagcompound);
            return true;
        }
    }

    @Override
    public void teleportTo(Location exit, boolean portal) {
        // Instead of using the default internal logic, we are better than that!
        // Let's bring forth our very own teleport logic!

        // Adjust the exit if the portal travel agent is being used
        CommonEntity<?> entity = this.controller.getEntity();
        if (portal) {
            Vector velocity = entity.getVelocity();
            CommonNMS.getNative(exit.getWorld()).getTravelAgent().adjustExit(entity.getHandle(Entity.class), exit, velocity);
            if (entity.vel.getX() != velocity.getX() || entity.vel.getY() != velocity.getY() || entity.vel.getZ() != velocity.getZ()) {
                entity.setVelocity(velocity);
            }
        }
        // Finally, teleport it
        this.controller.getEntity().teleport(exit, portal ? TeleportCause.NETHER_PORTAL : TeleportCause.UNKNOWN);
    }

    /*
     * The super methods are unused
     */
    @Override
    public boolean super_e(EntityHuman human) {
        return controller.getEntity().getHandle(NMSEntityHook.class).super_e(human);
    }

    @Override
    public void super_t_() {
        controller.getEntity().getHandle(NMSEntityHook.class).super_t_();
    }

    @Override
    public boolean super_damageEntity(DamageSource damagesource, float damage) {
        return controller.getEntity().getHandle(NMSEntityHook.class).super_damageEntity(damagesource, damage);
    }

    @Override
    public void super_burn(float damage) {
        controller.getEntity().getHandle(NMSEntityHook.class).super_burn(damage);
    }

    @Override
    public void super_g(double dx, double dy, double dz) {
        controller.getEntity().getHandle(NMSEntityHook.class).super_g(dx, dy, dz);
    }

    @Override
    public void super_move(double dx, double dy, double dz) {
        controller.getEntity().getHandle(NMSEntityHook.class).super_move(dx, dy, dz);
    }

    @Override
    public void super_die() {
        controller.getEntity().getHandle(NMSEntityHook.class).super_die();
    }

    @Override
    public String super_getName() {
        return controller.getEntity().getHandle(NMSEntityHook.class).super_getName();
    }

    @Override
    public void super_teleportTo(Location exit, boolean portal) {
        controller.getEntity().getHandle(NMSEntityHook.class).super_teleportTo(exit, portal);
    }
}
