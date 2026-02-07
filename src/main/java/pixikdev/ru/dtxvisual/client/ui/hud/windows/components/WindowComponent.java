package pixikdev.ru.dtxvisual.client.ui.hud.windows.components;

import pixikdev.ru.dtxvisual.client.ui.clickgui.components.Component;
import pixikdev.ru.dtxvisual.client.util.animations.Animation;
import lombok.*;

@Getter @Setter
public abstract class WindowComponent extends Component {
	protected Animation animation;

	public WindowComponent(String name) {
		super(name);
	}
}
