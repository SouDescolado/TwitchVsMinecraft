package com.icrazyblaze.twitchmod;

import com.icrazyblaze.twitchmod.gui.MessageboxGui;
import com.icrazyblaze.twitchmod.util.TickHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.List;


public class BotCommands {

    public static String username = null;

    public static boolean oresExplode = false;
    public static boolean placeBedrock = false;

    public static Block[] oresArray = {Blocks.DIAMOND_ORE, Blocks.REDSTONE_ORE, Blocks.LIT_REDSTONE_ORE, Blocks.IRON_ORE, Blocks.GOLD_ORE, Blocks.LAPIS_ORE, Blocks.EMERALD_ORE, Blocks.COAL_ORE};

    public static List<Block> oresList = Arrays.asList(oresArray);


    public static EntityPlayerMP player() {

        PlayerList playerList = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList();
        EntityPlayerMP player = playerList.getPlayerByUsername(username);

        if (player == null) {
            player = playerList.getPlayers().get(0);
        }

        return player;

    }


    public static void addSlowness() {
        player().addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 400, 5));
    }

    public static void addHunger() {
        player().addPotionEffect(new PotionEffect(MobEffects.HUNGER, 800, 255));
    }

    public static void addSpeed() {
        player().addPotionEffect(new PotionEffect(MobEffects.SPEED, 400, 10));
    }

    public static void addPoison() {
        player().addPotionEffect(new PotionEffect(MobEffects.POISON, 400, 0));
    }

    public static void addNausea() {
        player().addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 400, 0));
    }
    public static void addRegen() {
        player().addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 400, 0));
    }

    public static void addLevitation() {
        player().addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 200, 1));
    }

    public static void noFall() {
        player().addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 400, 255));
    }

    public static void setOnFire() {

        BlockPos bpos = player().getPosition();
        IBlockState bposState = player().world.getBlockState(bpos);

        if (bposState == Blocks.AIR.getDefaultState()) {
            player().world.setBlockState(bpos, Blocks.FIRE.getDefaultState());
        }
        player().setFire(10);
    }

    public static void killPlayer() {

        Main.logger.info(player());

        player().setDead();

    }

    public static void deathTimer() {
        TickHandler.timerSeconds = 60;
        TickHandler.timerTicks = 0;
        TickHandler.killTimer = true;
    }


    public static void floorIsLava() {

        double xpos = player().posX;
        double ypos = player().posY;
        double zpos = player().posZ;


        BlockPos bpos = new BlockPos(xpos, ypos - 1, zpos);

        player().world.setBlockState(bpos, Blocks.LAVA.getDefaultState());
    }

    public static void waterBucket() {

        BlockPos bpos = player().getPosition();

        player().world.setBlockState(bpos, Blocks.WATER.getDefaultState());
    }

    public static void spawnAnvil() {

        double xpos = player().posX;
        double ypos = player().posY;
        double zpos = player().posZ;

        BlockPos bpos = new BlockPos(xpos, ypos + 16, zpos);

        player().world.setBlockState(bpos, Blocks.ANVIL.getDefaultState());
    }

    public static void spawnCreeper() {

        Vec3d lookVector = player().getLookVec();

        double dx = player().posX - (lookVector.x * 4);
        double dz = player().posZ - (lookVector.z * 4);

        Entity ent = new EntityCreeper(player().world);
        ent.setPosition(dx, player().posY, dz);

        player().world.spawnEntity(ent);

    }

    public static void creeperScare() {
        player().world.playSound(null, player().posX, player().posY, player().posZ, SoundEvents.ENTITY_CREEPER_PRIMED, SoundCategory.HOSTILE, 1.0F, 1.0F);
    }

    public static void spawnFireball() {

        Vec3d lookVector = player().getLookVec();

        double dx = player().posX + (lookVector.x * 2);
        double dz = player().posZ + (lookVector.z * 2);

        Entity ent = new EntityLargeFireball(player().world);
        ent.setPosition(dx, player().posY + player().getEyeHeight(), dz);


        ent.addVelocity(lookVector.x * 2, lookVector.y * 2, lookVector.z * 2);


        player().world.spawnEntity(ent);
    }

    public static void spawnLightning() {

        double xpos = player().posX;
        double ypos = player().posY;
        double zpos = player().posZ;


        player().world.spawnEntity(new EntityLightningBolt(player().world, xpos, ypos, zpos, false));
    }


    public static void breakBlock() {

        int range = 50;

        Vec3d lookVector = player().getLookVec();
        Vec3d posVector = new Vec3d(player().posX, player().posY + player().getEyeHeight(), player().posZ);

        RayTraceResult rayTrace = player().world.rayTraceBlocks(posVector, lookVector.scale(range).add(posVector));
        BlockPos bpos = rayTrace.getBlockPos();

        player().world.destroyBlock(bpos, false);

    }

    public static void dismount() {
        if (player().isRiding()) {
            player().dismountRidingEntity();
        }
    }

    public static void showMessagebox(String message) {

        // Cut off the command
        message = message.substring(12);

        // Then trim the string to the proper length (324 chars max)
        message = message.substring(0, Math.min(message.length(), 324));


        if (!player().world.isRemote) {
            Minecraft.getMinecraft().displayGuiScreen(new MessageboxGui(message));
        }
    }

    public static void placeSign(String message) {

        // Cut off the command
        message = message.substring(6);

        // Split every 15 characters
        int maxlength = 15;
        String[] splitMessage = message.split("(?<=\\G.{" + maxlength + "})");


        BlockPos bpos = player().getPosition();
        IBlockState bposState = player().world.getBlockState(bpos);


        // Make sure we don't replace any signs that have already been placed
        if (bposState != Blocks.STANDING_SIGN.getDefaultState()) {

            // Rotate the sign to face the player
            int playerFace = MathHelper.floor((double) ((player().rotationYaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
            player().world.setBlockState(bpos, Blocks.STANDING_SIGN.getDefaultState().withProperty(BlockStandingSign.ROTATION, Integer.valueOf(playerFace)), 11);

            // Change the sign's text
            TileEntity tileEntity = player().world.getTileEntity(bpos);
            if (tileEntity instanceof TileEntitySign) {
                TileEntitySign sign = (TileEntitySign) tileEntity;
                sign.signText[0] = new TextComponentString(splitMessage[0]);
                sign.signText[1] = new TextComponentString(splitMessage[1]);
                sign.signText[2] = new TextComponentString(splitMessage[2]);
                sign.signText[3] = new TextComponentString(splitMessage[3]);
            }
        }

    }

    @SubscribeEvent
    public void explode(BreakEvent event) {

        Block thisBlock = event.getState().getBlock();

        if (!oresList.contains(thisBlock)) {
            return;
        } else if (oresExplode) {
            event.getWorld().createExplosion(null, event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), 5, true);

            oresExplode = false;
        }

    }

    @SubscribeEvent
    public void bedrockOnBreak(BreakEvent event) {

        BlockPos bpos = event.getPos();

        if (placeBedrock) {
            event.setCanceled(true);
            event.getWorld().setBlockState(bpos, Blocks.BEDROCK.getDefaultState());
            placeBedrock = false;
        }

    }

}
