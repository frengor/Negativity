package com.elikill58.negativity.spigot.protocols;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.LocationUtils;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class FastLadderProtocol extends Cheat implements Listener {

	public FastLadderProtocol() {
		super(CheatKeys.FAST_LADDER, false, Material.LADDER, CheatCategory.MOVEMENT, true, "ladder", "ladders");
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (!p.getGameMode().equals(GameMode.SURVIVAL) && !p.getGameMode().equals(GameMode.ADVENTURE))
			return;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.hasDetectionActive(this) || np.hasElytra())
			return;
		Location loc = p.getLocation().clone();
		if (!loc.getBlock().getType().equals(Material.LADDER)){
			np.isOnLadders = false;
			return;
		}
		if (!np.isOnLadders) {
			np.isOnLadders = true;
			return;
		}
		if(p.isFlying() || p.hasPotionEffect(PotionEffectType.JUMP))
			return;
		for (PotionEffect pe : p.getActivePotionEffects())
			if (pe.getType().equals(PotionEffectType.SPEED) && pe.getAmplifier() > 2)
				return;
		if(LocationUtils.hasMaterialsAround(loc, "WATER"))
			return;
		Location from = e.getFrom(), to = e.getTo();
		Location fl = from.clone().subtract(to);
		double distance = to.toVector().distance(from.toVector());
		int nbLadder = 0;
		Location tempLoc = loc.clone();
		while(tempLoc.getBlock().getType() == Material.LADDER) {
			nbLadder++;
			tempLoc.add(0, -1, 0);
		}
		if (distance > 0.23 && distance < 3.8 && nbLadder > 2 && loc.add(0, 1, 0).getBlock().getType().name().contains("LADDER")) {
			int ping = Utils.getPing(p);
			boolean mayCancel = SpigotNegativity.alertMod(ReportType.WARNING, p, this, UniversalUtils.parseInPorcent(distance * 350),
					"On ladders. Distance from/to : " + distance + ". Ping: " + ping + "ms. Number Ladder: " + nbLadder, hoverMsg("main", "%nb%", nbLadder));
			if (isSetBack() && mayCancel)
				e.setTo(e.getFrom().clone().add(new Location(fl.getWorld(), fl.getX() / 2, fl.getY() / 2, fl.getZ()))
						.add(0, 0.5, 0));
		}
	}
}
