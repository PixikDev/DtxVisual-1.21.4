package pixikdev.ru.dtxvisual.modules.impl.utility;

import pixikdev.ru.dtxvisual.client.events.impl.EventAttackEntity;
import pixikdev.ru.dtxvisual.client.managers.FriendsManager;
import pixikdev.ru.dtxvisual.client.ChatUtils;
import pixikdev.ru.dtxvisual.modules.api.Category;
import pixikdev.ru.dtxvisual.modules.api.Module;
import pixikdev.ru.dtxvisual.client.events.impl.EventMouse;
import pixikdev.ru.dtxvisual.modules.settings.api.Bind;
import pixikdev.ru.dtxvisual.modules.settings.impl.BindSetting;
import pixikdev.ru.dtxvisual.modules.settings.impl.BooleanSetting;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.client.resource.language.I18n;

public class FriendHelper extends Module {

    private final BindSetting friendKey = new BindSetting("setting.friendKey", new Bind(2, true));
    private final BooleanSetting noFriendDamage = new BooleanSetting("setting.noFriendDamage", true);

    public FriendHelper() {
        super("FriendHelper", Category.Utility, I18n.translate("module.friendhelper.description"));
        getSettings().add(friendKey);
        getSettings().add(noFriendDamage);
    }

    // Добавлено: публичный геттер, чтобы другие части кода (миксины) могли корректно узнать состояние
    public BooleanSetting getNoFriendDamage() {
        return noFriendDamage;
    }

    @EventHandler
    public void onAttack(EventAttackEntity e) {
        if (!isToggled() || !noFriendDamage.getValue()) return;

        Entity target = e.getTarget();
        if (target instanceof PlayerEntity player) {
            String namePrimary = player.getGameProfile().getName();
            String nameAlt = player.getName() != null ? player.getName().getString() : namePrimary;
            if (FriendsManager.checkFriend(namePrimary) || FriendsManager.checkFriend(nameAlt)) {
                e.cancel();
            }
        }
    }

    @EventHandler
    public void onMouse(EventMouse event) {
        if (!isToggled()) return;
        if (event.getAction() != 1) return; // press
        if (friendKey.getValue().isMouse() && friendKey.getValue().getKey() == event.getButton()) {
            handleFriendAction();
        }
    }

    private void handleFriendAction() {
        if (mc.crosshairTarget == null || mc.crosshairTarget.getType() != HitResult.Type.ENTITY) return;
        EntityHitResult ehr = (EntityHitResult) mc.crosshairTarget;
        if (!(ehr.getEntity() instanceof PlayerEntity player)) return;
        String playerName = player.getName().getString();
        if (FriendsManager.checkFriend(playerName)) {
            FriendsManager.removeFriend(playerName);
            ChatUtils.sendMessage(String.format(I18n.translate("friend.removed"), playerName));
        } else {
            FriendsManager.addFriend(playerName);
            ChatUtils.sendMessage(String.format(I18n.translate("friend.added"), playerName));
        }
    }
}
