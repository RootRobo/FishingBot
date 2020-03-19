/*
 * Created by David Luedtke (MrKinau)
 * 2019/10/18
 * Modified by RootRobo
 */

package systems.kinau.fishingbot.event.play;

import lombok.AllArgsConstructor;
import lombok.Getter;
import systems.kinau.fishingbot.event.Event;

@AllArgsConstructor
public class UpdateHealthEvent extends Event {

    @Getter private int health;
    @Getter private int food;
    @Getter private int saturation;
}
