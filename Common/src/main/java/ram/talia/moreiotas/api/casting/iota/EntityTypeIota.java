package ram.talia.moreiotas.api.casting.iota;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.utils.HexUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ram.talia.moreiotas.common.lib.hex.MoreIotasIotaTypes.ENTITY_TYPE;

public class EntityTypeIota extends Iota {
    public EntityTypeIota(@NotNull EntityType<?> entityType) {
        super(ENTITY_TYPE, entityType);
    }

    public EntityType<?> getEntityType() {
        return (EntityType<?>) this.payload;
    }

    @Override
    protected boolean toleratesOther(Iota that) {
        return typesMatch(this, that) &&
                that instanceof EntityTypeIota dent &&
                this.getEntityType().equals(dent.getEntityType());
    }

    @Override
    public boolean isTruthy() {
        return true;
    }

    @Override
    public @NotNull Tag serialize() {
        return StringTag.valueOf(BuiltInRegistries.ENTITY_TYPE.getKey(this.getEntityType()).toString());
    }

    public static IotaType<EntityTypeIota> TYPE = new IotaType<>() {
        @Nullable
        @Override
        public EntityTypeIota deserialize(Tag tag, ServerLevel world) throws IllegalArgumentException {
            var stag = HexUtils.downcast(tag, StringTag.TYPE);

            var typeLocation = ResourceLocation.read(stag.getAsString());
            var type = typeLocation.map(BuiltInRegistries.ENTITY_TYPE::get).get().left();

            return type.map(EntityTypeIota::new).orElse(null);
        }

        @Override
        public Component display(Tag tag) {
            if (!(tag instanceof StringTag stag)) {
                return Component.translatable("hexcasting.spelldata.unknown");
            }
            var typeLocation = ResourceLocation.read(stag.getAsString());
            var type = typeLocation.map(BuiltInRegistries.ENTITY_TYPE::get).get().left();

            return type.map(t -> t.getDescription().copy().append(" ").append(Component.translatable("moreiotas.spelldata.entity_type")).withStyle(ChatFormatting.DARK_AQUA))
                       .orElse(Component.translatable("hexcasting.spelldata.unknown"));
        }

        @Override
        public int color() {
            return 0xff_5555ff;
        }
    };
}
