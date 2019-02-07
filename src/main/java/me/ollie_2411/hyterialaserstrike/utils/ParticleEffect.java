package me.ollie_2411.hyterialaserstrike.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Credit to someone
 */

public enum ParticleEffect
{
    EXPLOSION_NORMAL("EXPLOSION_NORMAL", 0, "explode", 0, -1),
    EXPLOSION_LARGE("EXPLOSION_LARGE", 1, "largeexplode", 1, -1),
    EXPLOSION_HUGE("EXPLOSION_HUGE", 2, "hugeexplosion", 2, -1),
    FIREWORKS_SPARK("FIREWORKS_SPARK", 3, "fireworksSpark", 3, -1),
    WATER_BUBBLE("WATER_BUBBLE", 4, "bubble", 4, -1, false, true),
    WATER_SPLASH("WATER_SPLASH", 5, "splash", 5, -1),
    WATER_WAKE("WATER_WAKE", 6, "wake", 6, 7),
    SUSPENDED("SUSPENDED", 7, "suspended", 7, -1, false, true),
    SUSPENDED_DEPTH("SUSPENDED_DEPTH", 8, "depthSuspend", 8, -1),
    CRIT("CRIT", 9, "crit", 9, -1),
    CRIT_MAGIC("CRIT_MAGIC", 10, "magicCrit", 10, -1),
    SMOKE_NORMAL("SMOKE_NORMAL", 11, "smoke", 11, -1),
    SMOKE_LARGE("SMOKE_LARGE", 12, "largesmoke", 12, -1),
    SPELL("SPELL", 13, "spell", 13, -1),
    SPELL_INSTANT("SPELL_INSTANT", 14, "instantSpell", 14, -1),
    SPELL_MOB("SPELL_MOB", 15, "mobSpell", 15, -1),
    SPELL_MOB_AMBIENT("SPELL_MOB_AMBIENT", 16, "mobSpellAmbient", 16, -1),
    SPELL_WITCH("SPELL_WITCH", 17, "witchMagic", 17, -1),
    DRIP_WATER("DRIP_WATER", 18, "dripWater", 18, -1),
    DRIP_LAVA("DRIP_LAVA", 19, "dripLava", 19, -1),
    VILLAGER_ANGRY("VILLAGER_ANGRY", 20, "angryVillager", 20, -1),
    VILLAGER_HAPPY("VILLAGER_HAPPY", 21, "happyVillager", 21, -1),
    TOWN_AURA("TOWN_AURA", 22, "townaura", 22, -1),
    NOTE("NOTE", 23, "note", 23, -1),
    PORTAL("PORTAL", 24, "portal", 24, -1),
    ENCHANTMENT_TABLE("ENCHANTMENT_TABLE", 25, "enchantmenttable", 25, -1),
    FLAME("FLAME", 26, "flame", 26, -1),
    LAVA("LAVA", 27, "lava", 27, -1),
    FOOTSTEP("FOOTSTEP", 28, "footstep", 28, -1),
    CLOUD("CLOUD", 29, "cloud", 29, -1),
    REDSTONE("REDSTONE", 30, "reddust", 30, -1),
    SNOWBALL("SNOWBALL", 31, "snowballpoof", 31, -1),
    SNOW_SHOVEL("SNOW_SHOVEL", 32, "snowshovel", 32, -1),
    SLIME("SLIME", 33, "slime", 33, -1),
    HEART("HEART", 34, "heart", 34, -1),
    BARRIER("BARRIER", 35, "barrier", 35, 8),
    ITEM_CRACK("ITEM_CRACK", 36, "iconcrack", 36, -1, true),
    BLOCK_CRACK("BLOCK_CRACK", 37, "blockcrack", 37, -1, true),
    BLOCK_DUST("BLOCK_DUST", 38, "blockdust", 38, 7, true),
    WATER_DROP("WATER_DROP", 39, "droplet", 39, 8),
    ITEM_TAKE("ITEM_TAKE", 40, "take", 40, 8),
    MOB_APPEARANCE("MOB_APPEARANCE", 41, "mobappearance", 41, 8);

    private static final Map<String, ParticleEffect> NAME_MAP;
    private static final Map<Integer, ParticleEffect> ID_MAP;
    private final String name;
    private final int id;
    private final int requiredVersion;
    private final boolean requiresData;
    private final boolean requiresWater;

    static {
        NAME_MAP = new HashMap<String, ParticleEffect>();
        ID_MAP = new HashMap<Integer, ParticleEffect>();
        ParticleEffect[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final ParticleEffect effect = values[i];
            ParticleEffect.NAME_MAP.put(effect.name, effect);
            ParticleEffect.ID_MAP.put(effect.id, effect);
        }
    }

    private ParticleEffect(final String s, final int n, final String name, final int id, final int requiredVersion, final boolean requiresData, final boolean requiresWater) {
        this.name = name;
        this.id = id;
        this.requiredVersion = requiredVersion;
        this.requiresData = requiresData;
        this.requiresWater = requiresWater;
    }

    private ParticleEffect(final String s, final int n, final String name, final int id, final int requiredVersion, final boolean requiresData) {
        this(s, n, name, id, requiredVersion, requiresData, false);
    }

    private ParticleEffect(final String s, final int n, final String name, final int id, final int requiredVersion) {
        this(s, n, name, id, requiredVersion, false);
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public int getRequiredVersion() {
        return this.requiredVersion;
    }

    public boolean getRequiresData() {
        return this.requiresData;
    }

    public boolean getRequiresWater() {
        return this.requiresWater;
    }

    public boolean isSupported() {
        return this.requiredVersion == -1 || ParticlePacket.getVersion() >= this.requiredVersion;
    }

    public static ParticleEffect fromName(final String name) {
        for (final Map.Entry<String, ParticleEffect> entry : ParticleEffect.NAME_MAP.entrySet()) {
            if (!entry.getKey().equalsIgnoreCase(name)) {
                continue;
            }
            return entry.getValue();
        }
        return null;
    }

    public static ParticleEffect fromId(final int id) {
        for (final Map.Entry<Integer, ParticleEffect> entry : ParticleEffect.ID_MAP.entrySet()) {
            if (entry.getKey() != id) {
                continue;
            }
            return entry.getValue();
        }
        return null;
    }

    private static boolean isWater(final Location location) {
        final Material material = location.getBlock().getType();
        return material == Material.WATER || material == Material.STATIONARY_WATER;
    }

    private static boolean isLongDistance(final Location location, final List<Player> players) {
        for (final Player player : players) {
            if (player.getLocation().distance(location) < 256.0) {
                continue;
            }
            return true;
        }
        return false;
    }

    public void display(final float offsetX, final float offsetY, final float offsetZ, final float speed, final int amount, final Location center, final double range) throws ParticleVersionException, ParticleDataException, IllegalArgumentException {
        if (!this.isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (this.requiresData) {
            throw new ParticleDataException("This particle effect requires additional data");
        }
        if (this.requiresWater && !isWater(center)) {
            throw new IllegalArgumentException("There is no water at the center location");
        }
        new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, range > 256.0, null).sendTo(center, range);
    }

    public void display(final float offsetX, final float offsetY, final float offsetZ, final float speed, final int amount, final Location center, final List<Player> players) throws ParticleVersionException, ParticleDataException, IllegalArgumentException {
        if (!this.isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (this.requiresData) {
            throw new ParticleDataException("This particle effect requires additional data");
        }
        if (this.requiresWater && !isWater(center)) {
            throw new IllegalArgumentException("There is no water at the center location");
        }
        new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, isLongDistance(center, players), null).sendTo(center, players);
    }

    public void display(final Vector direction, final float speed, final Location center, final double range) throws ParticleVersionException, ParticleDataException, IllegalArgumentException {
        if (!this.isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (this.requiresData) {
            throw new ParticleDataException("This particle effect requires additional data");
        }
        if (this.requiresWater && !isWater(center)) {
            throw new IllegalArgumentException("There is no water at the center location");
        }
        new ParticlePacket(this, direction, speed, range > 256.0, null).sendTo(center, range);
    }

    public void display(final Vector direction, final float speed, final Location center, final List<Player> players) throws ParticleVersionException, ParticleDataException, IllegalArgumentException {
        if (!this.isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (this.requiresData) {
            throw new ParticleDataException("This particle effect requires additional data");
        }
        if (this.requiresWater && !isWater(center)) {
            throw new IllegalArgumentException("There is no water at the center location");
        }
        new ParticlePacket(this, direction, speed, isLongDistance(center, players), null).sendTo(center, players);
    }

    public void display(final ParticleData data, final float offsetX, final float offsetY, final float offsetZ, final float speed, final int amount, final Location center, final double range) throws ParticleVersionException, ParticleDataException {
        if (!this.isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (!this.requiresData) {
            throw new ParticleDataException("This particle effect does not require additional data");
        }
        new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, range > 256.0, data).sendTo(center, range);
    }

    public void display(final ParticleData data, final float offsetX, final float offsetY, final float offsetZ, final float speed, final int amount, final Location center, final List<Player> players) throws ParticleVersionException, ParticleDataException {
        if (!this.isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (!this.requiresData) {
            throw new ParticleDataException("This particle effect does not require additional data");
        }
        new ParticlePacket(this, offsetX, offsetY, offsetZ, speed, amount, isLongDistance(center, players), data).sendTo(center, players);
    }

    public void display(final ParticleData data, final Vector direction, final float speed, final Location center, final double range) throws ParticleVersionException, ParticleDataException {
        if (!this.isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (!this.requiresData) {
            throw new ParticleDataException("This particle effect does not require additional data");
        }
        new ParticlePacket(this, direction, speed, range > 256.0, data).sendTo(center, range);
    }

    public void display(final ParticleData data, final Vector direction, final float speed, final Location center, final List<Player> players) throws ParticleVersionException, ParticleDataException {
        if (!this.isSupported()) {
            throw new ParticleVersionException("This particle effect is not supported by your server version");
        }
        if (!this.requiresData) {
            throw new ParticleDataException("This particle effect does not require additional data");
        }
        new ParticlePacket(this, direction, speed, isLongDistance(center, players), data).sendTo(center, players);
    }

    public abstract static class ParticleData
    {
        private final Material material;
        private final byte data;
        private final int[] packetData;

        public ParticleData(final Material material, final byte data) {
            super();
            this.material = material;
            this.data = data;
            this.packetData = new int[] { material.getId(), data };
        }

        public Material getMaterial() {
            return this.material;
        }

        public byte getData() {
            return this.data;
        }

        public int[] getPacketData() {
            return this.packetData;
        }

        public String getPacketDataString() {
            return "_" + this.packetData[0] + "_" + this.packetData[1];
        }
    }

    public static final class ItemData extends ParticleData
    {
        public ItemData(final Material material, final byte data) {
            super(material, data);
        }
    }

    public static final class BlockData extends ParticleData
    {
        public BlockData(final Material material, final byte data) throws IllegalArgumentException {
            super(material, data);
            if (!material.isBlock()) {
                throw new IllegalArgumentException("The material is not a block");
            }
        }
    }

    private static final class ParticleDataException extends RuntimeException
    {
        private static final long serialVersionUID = 3203085387160737484L;

        public ParticleDataException(final String message) {
            super(message);
        }
    }

    private static final class ParticleVersionException extends RuntimeException
    {
        private static final long serialVersionUID = 3203085387160737484L;

        public ParticleVersionException(final String message) {
            super(message);
        }
    }

    public static final class ParticlePacket
    {
        private static int version;
        private static Class<?> enumParticle;
        private static Constructor<?> packetConstructor;
        private static Method getHandle;
        private static Field playerConnection;
        private static Method sendPacket;
        private static boolean initialized;
        private final ParticleEffect effect;
        private final float offsetX;
        private final float offsetY;
        private final float offsetZ;
        private final float speed;
        private final int amount;
        private final boolean longDistance;
        private final ParticleData data;
        private Object packet;

        public ParticlePacket(final ParticleEffect effect, final float offsetX, final float offsetY, final float offsetZ, final float speed, final int amount, final boolean longDistance, final ParticleData data) throws IllegalArgumentException {
            super();
            initialize();
            if (speed < 0.0f) {
                throw new IllegalArgumentException("The speed is lower than 0");
            }
            if (amount < 1) {
                throw new IllegalArgumentException("The amount is lower than 1");
            }
            this.effect = effect;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.speed = speed;
            this.amount = amount;
            this.longDistance = longDistance;
            this.data = data;
        }

        public ParticlePacket(final ParticleEffect effect, final Vector direction, final float speed, final boolean longDistance, final ParticleData data) throws IllegalArgumentException {
            super();
            initialize();
            if (speed < 0.0f) {
                throw new IllegalArgumentException("The speed is lower than 0");
            }
            this.effect = effect;
            this.offsetX = (float)direction.getX();
            this.offsetY = (float)direction.getY();
            this.offsetZ = (float)direction.getZ();
            this.speed = speed;
            this.amount = 0;
            this.longDistance = longDistance;
            this.data = data;
        }

        public static void initialize() throws VersionIncompatibleException {
            if (ParticlePacket.initialized) {
                return;
            }
            try {
                ParticlePacket.version = Integer.parseInt(Character.toString(ReflectionUtils.PackageType.getServerVersion().charAt(3)));
                if (ParticlePacket.version > 7) {
                    ParticlePacket.enumParticle = ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("EnumParticle");
                }
                final Class<?> packetClass = ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass((ParticlePacket.version < 7) ? "Packet63WorldParticles" : "PacketPlayOutWorldParticles");
                ParticlePacket.packetConstructor = ReflectionUtils.getConstructor(packetClass, (Class<?>[])new Class[0]);
                ParticlePacket.getHandle = ReflectionUtils.getMethod("CraftPlayer", ReflectionUtils.PackageType.CRAFTBUKKIT_ENTITY, "getHandle", (Class<?>[])new Class[0]);
                ParticlePacket.playerConnection = ReflectionUtils.getField("EntityPlayer", ReflectionUtils.PackageType.MINECRAFT_SERVER, false, "playerConnection");
                ParticlePacket.sendPacket = ReflectionUtils.getMethod(ParticlePacket.playerConnection.getType(), "sendPacket", ReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("Packet"));
            }
            catch (Exception exception) {
                throw new VersionIncompatibleException("Your current bukkit version seems to be incompatible with this library", exception);
            }
            ParticlePacket.initialized = true;
        }

        public static int getVersion() {
            return ParticlePacket.version;
        }

        public static boolean isInitialized() {
            return ParticlePacket.initialized;
        }

        public void sendTo(final Location center, final Player player) throws PacketInstantiationException, PacketSendingException {
            if (this.packet == null) {
                try {
                    this.packet = ParticlePacket.packetConstructor.newInstance(new Object[0]);
                    Object id;
                    if (ParticlePacket.version < 8) {
                        id = this.effect.getName();
                        if (this.data != null) {
                            id = String.valueOf(id) + this.data.getPacketDataString();
                        }
                    }
                    else {
                        id = ParticlePacket.enumParticle.getEnumConstants()[this.effect.getId()];
                    }
                    ReflectionUtils.setValue(this.packet, true, "a", id);
                    ReflectionUtils.setValue(this.packet, true, "b", (float)center.getX());
                    ReflectionUtils.setValue(this.packet, true, "c", (float)center.getY());
                    ReflectionUtils.setValue(this.packet, true, "d", (float)center.getZ());
                    ReflectionUtils.setValue(this.packet, true, "e", this.offsetX);
                    ReflectionUtils.setValue(this.packet, true, "f", this.offsetY);
                    ReflectionUtils.setValue(this.packet, true, "g", this.offsetZ);
                    ReflectionUtils.setValue(this.packet, true, "h", this.speed);
                    ReflectionUtils.setValue(this.packet, true, "i", this.amount);
                    if (ParticlePacket.version > 7) {
                        ReflectionUtils.setValue(this.packet, true, "j", this.longDistance);
                        ReflectionUtils.setValue(this.packet, true, "k", (this.data == null) ? new int[0] : this.data.getPacketData());
                    }
                }
                catch (Exception exception) {
                    throw new PacketInstantiationException("Packet instantiation failed", exception);
                }
            }
            try {
                ParticlePacket.sendPacket.invoke(ParticlePacket.playerConnection.get(ParticlePacket.getHandle.invoke(player, new Object[0])), this.packet);
            }
            catch (Exception exception) {
                throw new PacketSendingException("Failed to send the packet to player '" + player.getName() + "'", exception);
            }
        }

        public void sendTo(final Location center, final List<Player> players) throws IllegalArgumentException {
            if (players.isEmpty()) {
                throw new IllegalArgumentException("The player list is empty");
            }
            for (final Player player : players) {
                this.sendTo(center, player);
            }
        }

        public void sendTo(final Location center, final double range) throws IllegalArgumentException {
            if (range < 1.0) {
                throw new IllegalArgumentException("The range is lower than 1");
            }
            final String worldName = center.getWorld().getName();
            final double squared = range * range;
            for (final Player player : Bukkit.getOnlinePlayers()) {
                if (player.getWorld().getName().equals(worldName)) {
                    if (player.getLocation().distanceSquared(center) > squared) {
                        continue;
                    }
                    this.sendTo(center, player);
                }
            }
        }

        private static final class VersionIncompatibleException extends RuntimeException
        {
            private static final long serialVersionUID = 3203085387160737484L;

            public VersionIncompatibleException(final String message, final Throwable cause) {
                super(message, cause);
            }
        }

        private static final class PacketInstantiationException extends RuntimeException
        {
            private static final long serialVersionUID = 3203085387160737484L;

            public PacketInstantiationException(final String message, final Throwable cause) {
                super(message, cause);
            }
        }

        private static final class PacketSendingException extends RuntimeException
        {
            private static final long serialVersionUID = 3203085387160737484L;

            public PacketSendingException(final String message, final Throwable cause) {
                super(message, cause);
            }
        }
    }
}

